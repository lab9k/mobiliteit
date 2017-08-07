package websocket;

import java.io.StringReader;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import controllers.DatabaseController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.sql.DataSource;
import websocket.WidgetSessionHandler.WidgetType;
import widgets.CoyoteWidget;
import widgets.Widget;

@ApplicationScoped
@ServerEndpoint("/actions")
public class WidgetWebsocket {

    private DataSource ds;
    private ResourceBundle myResourceBundle = ResourceBundle.getBundle("data.ResourceKeys", Locale.getDefault());

    @Inject
    private WidgetSessionHandler sessionHandler;// = new WidgetSessionHandler();

    /**
     * A new client connected to the websocket
     *
     * @param session the session of this new client
     */
    @OnOpen
    public void open(Session session) {
        WidgetSessionHandler.addSession(session);
    }

    /**
     * A client stopped using this websocket
     *
     * @param session the session of this client
     */
    @OnClose
    public void close(Session session) {
        WidgetSessionHandler.removeSession(session);
    }

    /**
     * An error occured in the websocket
     *
     * @param error the error that occured
     */
    @OnError
    public void onError(Throwable error) {
        Logger.getLogger(WidgetWebsocket.class.getName()).log(Level.SEVERE, null, error);
    }

    /**
     * Process a message from clients
     *
     * @param message the message from the client
     * @param session the session associated with this client
     */
    @OnMessage
    public void handleMessage(String message, Session session) {
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
            String action = jsonMessage.getString("action");
            String auth = "";
            if ("historicDataRequest".equals(action)) {
                WidgetSessionHandler.sendToSession(session, ((CoyoteWidget) WidgetSessionHandler.getWidgetByType(WidgetType.COYOTE)).getHistoricData(jsonMessage.getString("traject")));
            } else if ("loginAuthentication".equals(action)) {
                sessionHandler.sendToSession(session, DatabaseController.getInstance().userLogIn(jsonMessage.getString("provider"), jsonMessage.getString("token"), jsonMessage.getString("email")));
            } else if ("saveWidgetPreferences".equals(action)) {
                DatabaseController.getInstance().savePreferences(jsonMessage);
            } else if ("saveNotificationPreferences".equals(action)) {
                DatabaseController.getInstance().saveNotificationPreferences(jsonMessage);
            } else if ("saveNotificationFirebaseToken".equals(action)) {
                DatabaseController.getInstance().saveNotificationFirebaseToken(jsonMessage);
            } else if ("unsubscribeTrain".equals(action)) {
                DatabaseController.getInstance().deleteTrainSubscription(jsonMessage);
            } else if ("subscribeTrain".equals(action)) {
                if (DatabaseController.getInstance().insertTrainSubscription(jsonMessage)) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("action", "subscribedTrain");
                    builder.add("dayOfWeek", jsonMessage.getString("dayOfWeek"));
                    builder.add("departureTime", jsonMessage.getString("departureTime"));
                    builder.add("departureStation", jsonMessage.getString("departureStation"));
                    builder.add("endStation", jsonMessage.getString("endStation"));
                    builder.add("trainId", jsonMessage.getString("trainId"));
                    JsonObject json = builder.build();
                    sessionHandler.sendToSession(session, json);

                } else {
                    System.out.println("failed to add train"); //adding train failed
                }
            } else {
                try {
                    auth = jsonMessage.getString("auth");
                } catch (NullPointerException | ClassCastException ex) {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("action", "adminMessage");
                    builder.add("msg", "No password provided.");
                    JsonObject json = builder.build();
                    sessionHandler.sendToSession(session, json);
                    return;
                }
                if (auth.equals(myResourceBundle.getString("adminAuth"))) {
                    if ("enableWidget".equals(action)) {
                        WidgetType type = WidgetType.valueOf(jsonMessage.getString("widgetType"));
                        sessionHandler.removeWidget(type);
                        sessionHandler.addWidget(type);
                    } else if ("disableWidget".equals(action)) {
                        WidgetType type = WidgetType.valueOf(jsonMessage.getString("widgetType"));
                        sessionHandler.removeWidget(type);
                    } else if ("subscribeAsAdmin".equals(action)) {
                        sessionHandler.addAdminSession(session);
                    } else if ("authenticate".equals(action)) {
                        JsonObjectBuilder builder = Json.createObjectBuilder();
                        builder.add("action", "adminMessage");
                        builder.add("msg", "Authentication successfull.");
                        builder.add("authSuccess", true);
                        JsonObject json = builder.build();
                        sessionHandler.sendToSession(session, json);
                    } else if ("getConfiguration".equals(action)) {
                        WidgetType type = WidgetType.valueOf(jsonMessage.getString("widgetType"));
                        JsonObjectBuilder builder = Json.createObjectBuilder();
                        builder.add("action", "settings");
                        builder.add("content", WidgetSessionHandler.getWidgetByType(type).getSettings());
                        WidgetSessionHandler.sendToSession(session, builder.build());
                    } else if ("saveSettings".equals(action)) {
                        WidgetType type = WidgetType.valueOf(jsonMessage.getString("widgetType"));
                        updatePropertiesFile(jsonMessage.getString("widgetType"), jsonMessage.getString("settings"));
                        sessionHandler.removeWidget(type);
                        sessionHandler.addWidget(type);
                    }
                } else {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("action", "adminMessage");
                    builder.add("msg", "Wrong password provided.");
                    JsonObject json = builder.build();
                    sessionHandler.sendToSession(session, json);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WidgetWebsocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method called for updating the settings (URL's used, etc) of a certain
     * widget
     *
     * @param type
     * @param value
     */
    private void updatePropertiesFile(String type, String value) {
        Properties properties = new Properties();
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("data/ResourceKeys.properties");
            FileInputStream fileInputStream = new FileInputStream(new File(url.toURI().getPath()));
            properties.load(fileInputStream);
            System.out.println("----VOOR: " + properties);
            fileInputStream.close();
            properties.setProperty(type, value);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(url.toURI().getPath()));
            properties.store(fileOutputStream, "storing values to properties file");
            System.out.println("----NA: " + properties);
            fileOutputStream.close();
            Widget.loadProperties();
        } catch (IOException ex) {
            Logger.getLogger(WidgetWebsocket.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(WidgetWebsocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
