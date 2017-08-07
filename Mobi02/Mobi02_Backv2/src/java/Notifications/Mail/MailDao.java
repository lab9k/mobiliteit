/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.Mail;

import Api.ApiType;
import Exceptions.ApiNotFoundException;
import Notifications.ModelMessages.MessagesData;
import Properties.PropertyLoaderBean;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class MailDao {

    private PropertyLoaderBean props;

    @EJB
    private MessagesData messengerData;

    /**
     *
     * @param props
     */
    @EJB(name = "properties")
    public void setProp(PropertyLoaderBean props) {
        this.props = props;
    }

    /**
     *
     * @param recipient
     * @param subject
     * @param notificationMessage
     */
    public void sendMail(String recipient, String subject, String notificationMessage) {
        String username = props.getPassword("mail");
        String password = props.getPassword("passMail");

        
        //if you work on localhost --> change port to 587 (2 times)
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587"); //mailing local: 587 - server digital ocean: 465 but certificate not yet ok
        // Use the following if you need SSL
       // properties.put("mail.smtp.socketFactory.port", "587"); //mailing local: 587 - server digital ocean: 465 but certificate not yet ok
        //properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
       // properties.put("mail.smtp.socketFactory.fallback", "false");

        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); //only gmail addresses accepted
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(notificationMessage);

            Transport.send(message);

        } catch (MessagingException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING, "Exceptie",e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param map
     * @param name
     * @return
     */
    public String formMail(HashMap<ApiType, List<String>> map, String name) {
        StringBuilder sb = new StringBuilder("Beste ");
        sb.append(name).append(",\n");
        sb.append("Hieronder het overzicht van de gewenste verkeersmeldingen.\n\n");
        for (ApiType api : map.keySet()) {
            switch (api) {
                case WEATHER:
                    sb.append("Weerbericht: \n");
                    for (String n : map.get(api)) {
                        sb.append(n);
                        sb.append("\n");
                    }
                    sb.append("\n");
                    break;
                case COYOTE:
                    sb.append("Vertragingen voor uw routes: \n");
                    for (String n : map.get(api)) {
                        sb.append(n);
                        sb.append("\n");
                    }
                    sb.append("\n");
                    break;
                case BLUEBIKE:
                    sb.append("Informatie omtrent BlueBike: \n");
                    for (String n : map.get(api)) {
                        sb.append(n);
                        sb.append("\n");
                    }
                    sb.append("\n");
                    break;
                case PARKINGS:
                    sb.append("Informatie omtrent parkings: \n");
                    for (String n : map.get(api)) {
                        sb.append(n);
                        sb.append("\n");
                    }
                    sb.append("\n");
                    break;
                case TRAINSGHENT:
                    sb.append("Informatie omtrent gewenste treinen: \n");
                    for (String n : map.get(api)) {
                        sb.append(n);
                        sb.append("\n");
                    }
                    sb.append("\n");
                    break;
                default:
                    sb.append(" ");
            }
        }
        sb.append("\nMet vriendelijke groeten, \n");
        sb.append("Team Mobi02 i.o.v. Stad Gent");
        return sb.toString();
    }

    /**
     *
     * @param api
     * @param keywords
     * @param hour
     * @param minutes
     * @return
     * @throws ApiNotFoundException
     */
    public List<String> getNotificationMessage(ApiType api, String keywords, int hour, int minutes) throws ApiNotFoundException {
        switch (api) {
            case WEATHER:
                return messengerData.getWeatherNotification(keywords);

            case COYOTE:
                return messengerData.getCoyoteNotification(keywords);

            case BLUEBIKE:
                return messengerData.getBlueBikeNotification(keywords);

            case PARKINGS:
                return messengerData.getParkingNotification(keywords);

            case TRAINSGHENT:
                return messengerData.getTrainNotification(keywords, hour, minutes);

            default:
                throw new ApiNotFoundException();
        }
    }

}
