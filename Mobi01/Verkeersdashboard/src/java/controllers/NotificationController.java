package controllers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import data.Train;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;

public class NotificationController {

    private Properties props;
    private Session session;
    private String firebaseUrl = null;
    private String firebaseAppKey = null;

    public static NotificationController getInstance() {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }
    private ResourceBundle myResourceBundle = ResourceBundle.getBundle("data.ResourceKeys", Locale.getDefault());
    private static NotificationController instance = null;

    /**
     * Set up our mailproperties and session so we can use this for sending
     * emails.
     */
    protected NotificationController() {
        props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myResourceBundle.getString("gmail_acc"), myResourceBundle.getString("gmail_password"));
            }
        });
        ResourceBundle bundle = ResourceBundle.getBundle("data.ResourceKeys", Locale.getDefault());
        firebaseUrl = bundle.getString("FirebaseUrl");
        firebaseAppKey = bundle.getString("FirebaseApplicationKey");
    }

    /**
     * Send an email from our application's gmail to a list of recipients with a
     * corresponding subject and body
     *
     * @param emails
     * @param subject
     * @param body
     */
    public void sendMail(List<String> emails, String subject, String body) {
        if (!emails.isEmpty()) {
            Address[] aa = new Address[emails.size()];
            for (int i = 0; i < emails.size(); i++) {
                try {
                    aa[i] = new InternetAddress(emails.get(i));
                    System.out.println(emails.get(i));
                } catch (AddressException ex) {
                    Logger.getLogger(NotificationController.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("verkeersdashboardgent@gmail.com"));
                message.addRecipients(Message.RecipientType.TO,
                        aa);

                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                System.out.println("mail sent!!!!");

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     *
     * Send one email to one user
     *
     * @param email
     * @param subject
     * @param body
     */
    public void sendMail(String email, String subject, String body) {
        Address a = null;
        try {
            a = new InternetAddress(email);
        } catch (AddressException ex) {
            Logger.getLogger(NotificationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("verkeersdashboardgent@gmail.com"));
            message.addRecipient(Message.RecipientType.TO,
                    a);
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("mail sent!!!!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
/**
 * 
 * Send a chrome pushnotifcation to a certain user
 * @param FCMToken
 * @param title
 * @param body
 * @param clickAction
 * @param icon 
 */
    public void sendFirebaseNotification(String FCMToken, String title, String body, String clickAction, String icon) {
        try {
            URL url = new URL(firebaseUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + firebaseAppKey);

            JsonObjectBuilder jsonBody = Json.createObjectBuilder();
            jsonBody.add("to", FCMToken);
            JsonObjectBuilder jsonNotificationBody = Json.createObjectBuilder();
            jsonNotificationBody.add("title", title);
            jsonNotificationBody.add("body", body);
            if (clickAction != null) {
                jsonNotificationBody.add("click_action", clickAction);
            } else {
                jsonNotificationBody.add("click_action", "https://mobi-01.project.tiwi.be:8181");
            }
            if (icon != null) {
                jsonNotificationBody.add("icon", icon);
            }
            jsonBody.add("notification", jsonNotificationBody);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonBody.build().toString());
            writer.flush();
            writer.close();
            os.close();
            conn.connect();
            Object response = conn.getContent();
            System.out.println(response);
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(NotificationController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NotificationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
/**
 * 
 * Overload of sendFirebaseNotification with the fields we don't want to use already filled out with null's
 */
    public void sendFirebaseNotification(String FCMToken, String title, String body) {
        sendFirebaseNotification(FCMToken, title, body, null, null);
    }

    /**
     *
     * Send a notification about the weather to right recipients
     *
     * @param POP
     */
    public void sendWeatherNotification(int POP) {
        System.out.println("Sending weather notification...");
        //Email
        List<String> interestedUsers = DatabaseController.getInstance().getInterestedEmails("notify_rain");
        String message = "Er is vandaag " + POP + " % kans op regen, je neemt voor het zekerste toch je paraplu mee.";
        sendMail(interestedUsers, "Weer informatie", message);

        //Firebase
        List<String> interestedFirebaseUsers = DatabaseController.getInstance().getInterestedFirebaseTokens("notify_rain");
        System.out.println("Interested firebase tokens: " + interestedFirebaseUsers.size());
        for (String token : interestedFirebaseUsers) {
            sendFirebaseNotification(token, "Weer informatie", message);
        }
    }
/**
 * 
 * Try to send a notification to interested users who are subscribed to one or more delayed trains
 * 
 */
    public void sendNMBSNotification(List<Train> delayedTrains) {
        for (Train t : delayedTrains) {
            if (!t.isNotified()) {
                try {
                    ResultSet rs = DatabaseController.getInstance().getTrainSubscriptions(t.getTrainId(), t.getDayOfWeek(), t.getDepartureTime().toString());
                    String message = "";
                    if (t.isCancelled()) {
                        message = "Uw trein van " + t.getDepartureStation() + " naar " + t.getEndStation() + "\nis afgeschaft!";
                    } else {
                        message = "Uw trein van " + t.getDepartureStation() + " naar " + t.getEndStation() + "\nheeft een vertraging van: " + t.getDelay() / 60 + " minuten.";
                    }
                    ArrayList<String> emails = new ArrayList<>();
                    ArrayList<String> firebaseTokens = new ArrayList<>();
                    while (rs.next()) {
                        if (rs.getBoolean("notify_mail")) {
                            System.out.println("Interested user: " + rs.getString("user_email"));
                            emails.add(rs.getString("user_email"));
                        }
                        String token = rs.getString("firebase_token");

                        if (token != null) {
                            firebaseTokens.add(token);
                        }
                    }
                    sendMail(emails, "Trein Notificatie", message);
                    for (String token : firebaseTokens) {
                        sendFirebaseNotification(token, "Trein Notificatie", message);
                    }
                    t.setNotified(true);
                } catch (SQLException ex) {
                    Logger.getLogger(NotificationController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
