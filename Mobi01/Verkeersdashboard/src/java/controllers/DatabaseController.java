package controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue.ValueType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import websocket.WidgetWebsocket;
import widgets.CoyoteWidget;

public class DatabaseController {

    private ResourceBundle myResourceBundle = ResourceBundle.getBundle("data.ResourceKeys", Locale.getDefault());
    private DataSource ds;
    protected final ResourceBundle translation = ResourceBundle.getBundle("data.Translation", Locale.getDefault());
    private Context ctx;
    private static DatabaseController instance = null;

    private DatabaseController() {
        try {
            ctx = new InitialContext();
            this.ds = (DataSource) ctx.lookup("jdbc/dashboard");
            createTrainSubscriptionsTable();
        } catch (NamingException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static DatabaseController getInstance() {
        if (instance == null) {
            instance = new DatabaseController();
        }
        return instance;
    }

    /**
     *
     * Save the user's widgetconfiguration (position and size) to the database
     *
     * @param message
     */
    public void savePreferences(JsonObject message) {
        System.out.println(message);
        try (Connection dbConnection = ds.getConnection()) {
            PreparedStatement stmt = null;
            String instertstmt = "";
            if (message.containsKey("widgetpref")) {
                System.out.println("saving widgetprefs");
                instertstmt = "UPDATE users SET widgetpref=? Where user_id=?";
                stmt = dbConnection.prepareStatement(instertstmt);
                stmt.setString(1, message.getJsonArray("widgetpref").toString());
                stmt.setString(2, message.getString("userID"));
                stmt.executeUpdate();
            }
            if (message.containsKey("mobile_widgetpref")) {
                System.out.println("saving mobile widgetprefs");
                instertstmt = "UPDATE users SET mobile_widgetpref=? Where user_id=?";
                stmt = dbConnection.prepareStatement(instertstmt);
                stmt.setString(1, message.getJsonArray("mobile_widgetpref").toString());
                stmt.setString(2, message.getString("userID"));
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method used to decrypt the acces token we get from Google after we log in
     * with oAuth2, we need to verify if this token is actually authorised so
     * that the user ID is not spoofed
     *
     * @param token
     * @return
     */
    private String decryptAccesToken(String token) {
        String userID = "";
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(myResourceBundle.getString("googleAppID")))
                .build();
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
        } catch (GeneralSecurityException | IOException ex) {
            Logger.getLogger(WidgetWebsocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            // Print user identifier
            userID = payload.getSubject();
            //find the entry in the database or create a new entry if we have a new user
            return userID;
        } else {
            return "";
        }

    }

    /**
     * Called when a user logs in so that we can add this user to our database
     * and even send them their last configured widgetconfiguration for them to
     * load
     *
     * @param provider
     * @param accestoken
     * @param email
     * @return
     */
    public JsonObject userLogIn(String provider, String accestoken, String email) {
        String userID;
        if (provider.equals("Google")) {
            userID = decryptAccesToken(accestoken);
        } else {
            userID = accestoken;
        }

        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("action", "loginAuthenticated");
        builder.add("userID", userID);

        try (Connection dbConnection = ds.getConnection()) {
            DatabaseMetaData dbmd = dbConnection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "users", null);
            if (!rs.next()) {
                Statement stmt = dbConnection.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS users (user_id varchar(40) NOT NULL,widgetpref varchar(1200),mobile_widgetpref varchar(1200),user_email varchar(70),notify_rain BIT not null,notify_train BIT not null,firebase_token varchar(255),notify_mail BIT not null, CONSTRAINT users_pl PRIMARY KEY (user_id));");
            }
            PreparedStatement stmt;
            String instertstmt = "INSERT IGNORE INTO users VALUES(?,null,null,?,0,0,null,null)";
            stmt = dbConnection.prepareStatement(instertstmt);
            stmt.setString(1, userID);
            stmt.setString(2, email);
            stmt.executeUpdate();
            String selectstmt = "SELECT widgetpref,notify_rain,notify_train,firebase_token,notify_mail FROM users WHERE user_id =?";
            stmt = dbConnection.prepareStatement(selectstmt);
            stmt.setString(1, userID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("widgetpref") != null) {
                    builder.add("widgetPref", rs.getString("widgetpref"));
                }
                builder.add("notify_rain", rs.getString("notify_rain"));
                builder.add("notify_train", rs.getString("notify_train"));
                builder.add("notify_mail", rs.getString("notify_mail"));
                builder.add("notify_firebase", (rs.getString("firebase_token") != null));
            }
            selectstmt = "SELECT day_of_week,departure_station,end_station,departure_time,train_id FROM train_subs WHERE user_id =?";
            stmt = dbConnection.prepareStatement(selectstmt);
            stmt.setString(1, userID);
            rs = stmt.executeQuery();
            dbConnection.close();
            if (rs.isBeforeFirst()) {
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                while (rs.next()) {
                    arrayBuilder.add(Json.createObjectBuilder().add("dayOfWeek", rs.getString("day_of_week"))
                            .add("departureStation", rs.getString("departure_station"))
                            .add("endStation", rs.getString("end_station"))
                            .add("departureTime", rs.getString("departure_time"))
                            .add("trainId", rs.getString("train_id")));
                }
                builder.add("trainSubscriptions", arrayBuilder.build());
            }

        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }

        JsonObject json = builder.build();
        return json;
    }

    /**
     * Create the table needed for our coyote Widget if it didn't already exist.
     */
    public void createCoyoteTable() {
        try (Connection dbConnection = ds.getConnection()) {
            DatabaseMetaData dbmd = dbConnection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "avgTimes", null);
            if (!rs.next()) {
                Statement stmt = dbConnection.createStatement();
                stmt.execute("CREATE TABLE avgTimes (section varchar(100), day integer, minTime integer, avgTime integer, totalTime integer, totalEntries integer, startTimestamp varchar(5))");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create the table needed for the train subscriptions if it didn't already
     * exist.
     */
    public void createTrainSubscriptionsTable() {
        try (Connection dbConnection = ds.getConnection()) {
            DatabaseMetaData dbmd = dbConnection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "train_subs", null);
            if (!rs.next()) {
                Statement stmt = dbConnection.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS train_subs (user_id varchar(40), day_of_week varchar(50), departure_station varchar(50), end_station varchar(50), departure_time varchar(50), train_id varchar(80))");
                Statement stmt2 = dbConnection.createStatement();
                stmt2.execute("CREATE UNIQUE INDEX  rt_uq  ON train_subs (user_id,day_of_week,departure_station, end_station,departure_time,train_id)");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * Save the user's notification preferences (train, weather) in our database
     *
     * @param json
     */
    public void saveNotificationPreferences(JsonObject json) {
        try {
            try (Connection dbConnection = ds.getConnection()) {
                DatabaseMetaData dbmd = dbConnection.getMetaData();
                ResultSet rs = dbmd.getTables(null, null, "users", null);
                if (rs.next()) {
                    PreparedStatement stmt = null;
                    String instertstmt = "UPDATE users SET notify_rain=?,notify_train=?,notify_mail=? WHERE user_id=?";
                    stmt = dbConnection.prepareStatement(instertstmt);
                    stmt.setBoolean(1, json.getBoolean("notify_rain"));
                    stmt.setBoolean(2, json.getBoolean("notify_train"));
                    stmt.setBoolean(3, json.getBoolean("notify_mail"));
                    stmt.setString(4, json.getString("userID"));
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * 
     * Save user's preference about getting firebase notifications
     */
    public void saveNotificationFirebaseToken(JsonObject json) {
        //System.out.println(json.toString());
        try (Connection dbConnection = ds.getConnection()) {
            DatabaseMetaData dbmd = dbConnection.getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "users", null);
            if (rs.next()) {
                PreparedStatement stmt = null;
                String instertstmt = "UPDATE users SET firebase_token=? WHERE user_id=?";
                stmt = dbConnection.prepareStatement(instertstmt);
                if (json.get("firebaseToken").getValueType().equals(ValueType.STRING)) {
                    stmt.setString(1, json.getString("firebaseToken"));
                } else {
                    stmt.setString(1, null);
                }
                stmt.setString(2, json.getString("userID"));
                stmt.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * get a List of tokens that are interested in a certain notification
     * property e.g notifications about the weather
     *
     * @param property
     * @return
     */
    public List<String> getInterestedEmails(String property) {
        ArrayList<String> emails = new ArrayList<String>();
        try (Connection dbConnection = ds.getConnection()) {
            String selectstmt = "SELECT user_email FROM users WHERE " + property + "='1' and notify_mail=1;";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(selectstmt);
            dbConnection.close();
            while (rs.next()) {
                emails.add(rs.getString("user_email"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return emails;
    }

    /**
     * 
     * Get a list of users who have set a firebasetoken with which they want to receive notifications
     */
    public List<String> getInterestedFirebaseTokens(String property) {
        ArrayList<String> tokens = new ArrayList<String>();
        try (Connection dbConnection = ds.getConnection()) {
            String selectstmt = "SELECT firebase_token FROM users WHERE " + property + "='1' AND firebase_token IS NOT NULL;";
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(selectstmt);
            dbConnection.close();
            while (rs.next()) {
                tokens.add(rs.getString("firebase_token"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tokens;
    }

    /**
     *
     * Get a list of the tokens of the users that are interested in a certain
     * trainconnection we can use this to send notifications to the
     * corresponding tokens if there is something noteworthy happening e.g.
     * delay/cancellation
     *
     * @param trainId
     * @param dayOfWeek
     * @param departureTime
     * @return
     */
    public ResultSet getTrainSubscriptions(String trainId, String dayOfWeek, String departureTime) {
        System.out.println(trainId + dayOfWeek + departureTime + " ");
        ResultSet rs = null;
        try (Connection dbConnection = ds.getConnection()) {
            String selectstmt = "SELECT users.user_email, users.firebase_token,users.notify_mail FROM train_subs JOIN users ON train_subs.user_id = users.user_id WHERE train_id=? AND day_of_week=? AND departure_time=?";
            PreparedStatement stmt = dbConnection.prepareStatement(selectstmt);
            stmt.setString(1, trainId);
            stmt.setString(2, dayOfWeek);
            stmt.setString(3, departureTime);
            rs = stmt.executeQuery();
            dbConnection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

/**
 * 
 * Insert a particular users subscription to a certain train line in to the database so that it can be requested later when we want to send a notification about delayed trains
 */
    public boolean insertTrainSubscription(JsonObject json) {
        boolean succes = false;
        try (Connection dbConnection = ds.getConnection()) {
            PreparedStatement stmt;
            String instertstmt = "INSERT IGNORE INTO train_subs VALUES(?,?,?,?,?,?)";
            stmt = dbConnection.prepareStatement(instertstmt);
            stmt.setString(1, json.getString("userID"));
            stmt.setString(2, json.getString("dayOfWeek"));
            stmt.setString(3, json.getString("departureStation"));
            stmt.setString(4, json.getString("endStation"));
            stmt.setString(5, json.getString("departureTime"));
            stmt.setString(6, json.getString("trainId"));
            //stmt.setString(7, json.getString("email"));
            int a = stmt.executeUpdate();
            dbConnection.close();
            if (a == 0) {
                succes = false;
            } else {
                succes = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return succes;
    }
/**
 * 
 * 
 * Method to provide the users a way to unsubscribe to a certain train line
 */
    public boolean deleteTrainSubscription(JsonObject json) {
        boolean succes = false;
        try (Connection dbConnection = ds.getConnection()) {
            PreparedStatement stmt;
            String deletestmt = "DELETE from train_subs where day_of_week=? AND train_id=? AND user_id=? AND departure_time=? AND departure_station=?";
            stmt = dbConnection.prepareStatement(deletestmt);
            stmt.setString(1, json.getString("dayOfWeek"));
            stmt.setString(2, json.getString("trainId"));
            stmt.setString(3, json.getString("userID"));
            stmt.setString(4, json.getString("departureTime"));
            stmt.setString(5, json.getString("departureStation"));
            int a = stmt.executeUpdate();
            dbConnection.close();
            if (a == 0) {
                succes = false;
            } else {
                succes = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return succes;
    }
}
