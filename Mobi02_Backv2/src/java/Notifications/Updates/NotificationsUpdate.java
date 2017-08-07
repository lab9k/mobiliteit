/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.Updates;

import Api.ApiType;
import Api.Caching.Properties.CachingProperties;
import Database.Dao.UserDao;
import Database.Entities.NotificationPreferences;
import Database.Entities.PersonalUser;
import Exceptions.ApiRequestException;
import Notifications.Mail.MailDao;
import Notifications.Messenger.MessengerDao;
import Properties.PropertyLoaderBean;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;

/**
 *
 * @author ruben
 */
@Stateless
public class NotificationsUpdate {

    private PropertyLoaderBean props;
    @EJB
    private CachingProperties cProps;

    @EJB
    private MessengerDao messengerDao;

    @EJB
    private UserDao userDao;

    @EJB
    private MailDao mailDao;

    /**
     *
     * @param props
     */
    @EJB(name = "properties")
    public void setProp(PropertyLoaderBean props) {
        this.props = props;
    }

    public void update() {

        try {
            HashMap<ApiType, List<String>> map = new HashMap<>();
            Date date = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(date);

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            //fix for timezone difference
            hour += cProps.getTimeout("timezone_correction");
            /*Server clock is GMT +0 --> Belgium : GMT +1*/

            int minutes = calendar.get(Calendar.MINUTE);

            Logger.getLogger((this.getClass().getSimpleName())).log(Level.INFO, "Checking if notifications need to be send");
            List<PersonalUser> users = userDao.getSubscribedUsers(hour);

            ApiType api = null;
            List<String> notifications = new ArrayList<>();
            boolean mails = false;

            for (PersonalUser p : users) {
                for (NotificationPreferences n : p.getNotification()) {
                    int sendMin = n.getSendMinutes();
                    api = props.getTypeFromString(n.getType());
                    int sendHour = n.getSendHour();
                    //correction: trains could be asked for each minute --> flooring to quart
                    if (api == ApiType.TRAINSGHENT) {
                        if (sendMin >= 0 && sendMin < 15) {
                            sendMin = 0;
                        } else if (sendMin >= 15 && sendMin < 30) {
                            sendMin = 15;
                        } else if (sendMin >= 30 && sendMin < 45) {
                            sendMin = 30;
                        } else if (sendMin >= 45) {
                            sendMin = 45;
                        }
                    }

                    if (sendHour == hour && sendMin == minutes && n.getPlatform().equals("messenger") && p.getFacebookInfo().getMessengerId() != null) {
                        
                        notifications = messengerDao.getNotificationMessage(api, n.getKeywords(), n.getSendHour(), n.getSendMinutes());
                        for (int i = 0; i < notifications.size(); i++) {
                            messengerDao.sendMessage(p.getFacebookInfo().getMessengerId(), notifications.get(i));
                        }

                    }

                    if (sendHour == hour && sendMin == minutes && n.getPlatform().equals("mail") && p.getEmail() != null) {
                        mails = true;
                        notifications = mailDao.getNotificationMessage(api, n.getKeywords(), n.getSendHour(), n.getSendMinutes());
                        map.put(api, notifications);
                    }

                }
                if (mails && !map.isEmpty()) {
                    String email = mailDao.formMail(map, p.getFirstName());
                    mailDao.sendMail(p.getEmail(), "Verkeersmeldingen", email);
                }
                mails = false;
                map = new HashMap<>();
            }
        } catch (ApiRequestException ex) {
            Logger.getLogger(NotificationsUpdate.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    /**
     *
     */
    /*
   @PostConstruct
    public void init() {
        messengerDao.sendMessage("1694737820568254", "Server is starting");
        messengerDao.sendMessage("1328792110567635", "Dag Jana,");
        //1328792110567635
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    //Testing messages
                    //eerst dao een algemene methode oproepen: getNotificationMessage(ApiType type)
                    //vervolgens gaat in dao op basis van het type de messengerdata.getWeather.. enzovoort opgeroepen worden
                    //die methode geeft dan de gepaste notificatie string terug, opgehaald uit data vanuit de cache
                    /*ApiType api = props.getTypeFromString("Coyote");
                    String keywords = "Brugsevaart (N9) Northbound - Gebroeders de Smetstraat - R4,Rooigemlaan (R40) Northbound - Drongensesteenweg - Palinghuizen";
                    List<String> notifications = messengerDao.getNotificationMessage(api, keywords);
                    for (int i = 0; i < notifications.size(); i++) {
                    messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }

                    //Weather messages
                    api = props.getTypeFromString("Weather");
                    notifications = messengerDao.getNotificationMessage(api, "");
                    for (int i = 0; i < notifications.size(); i++) {
                    messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }

                    //Bluebike messages
                    api = props.getTypeFromString("Bluebike");
                    notifications = messengerDao.getNotificationMessage(api, "Blue-bikes_Gent_Dampoort,Blue-bikes_Gent-Sint-Pieters");
                    for (int i = 0; i < notifications.size(); i++) {
                    messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }

                    //Parking messages
                    api = props.getTypeFromString("Parkings");
                    notifications = messengerDao.getNotificationMessage(api, "P02 Reep, P08 Ramen");
                    for (int i = 0; i < notifications.size(); i++) {
                    messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }

                    //Train messages
                    api = props.getTypeFromString("Trains");
                    notifications = messengerDao.getNotificationMessage(api, "Gent-Sint-Pieters,De Panne,Gent-Dampoort,Leuven");
                    for (int i = 0; i < notifications.size(); i++) {
                    messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }
     */
    //train messages
    /*
                    ApiType api = props.getTypeFromString("Trains");
                    List<String> notifications = messengerDao.getNotificationMessage(api, "Mechelen,Antwerpen", 11,45);
                    for (int i = 0; i < notifications.size(); i++) {
                        messengerDao.sendMessage("1328792110567635", notifications.get(i));
                    }

                    Date date = new Date();
                    Calendar calendar = GregorianCalendar.getInstance();
                    calendar.setTime(date);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY) ;
                    
                    //fix for timezone difference
                    hour += cProps.getTimeout("timezone_correction");
                    /*Server clock is GMT +0 --> Belgium : GMT +1*/
 /*
                    if (hour == 0) {
                    hour = 23;
                    } else {
                    hour--;
                    }
                    
                    //When summertime in Belgium, another 1 hour back
                    if (props.getSummertime()) {
                    if (hour == 0) {
                    hour = 23;
                    } else {
                    hour--;
                    }
                    }
                    int minutes = calendar.get(Calendar.MINUTE);

                    Logger.getLogger((this.getClass().getSimpleName())).log(Level.INFO,"Checking if Messenger need to be send");
                    List<PersonalUser> users = userDao.getSubscribedUsers(hour);
                    
                   // ApiType api;
                   // List<String> notifications;
                    
                    for (PersonalUser p : users) {
                        for (NotificationPreferences n : p.getNotification()) {
                            //checking on Platform choosen by user for getting notifications
                            //checking if user has a messengerid
                            int sendMin = n.getSendMinutes();
                            boolean minutesOk = false;
                            switch (sendMin) {
                                case 00:
                                    if (minutes >= 0 && minutes < 15) {
                                        minutesOk = true;
                                    }
                                    break;
                                case 15:
                                    if (minutes >= 15 && minutes < 30) {
                                        minutesOk = true;
                                    }
                                    break;
                                case 30:
                                    if (minutes >= 30 && minutes < 45) {
                                        minutesOk = true;
                                    }
                                    break;
                                case 45:
                                    if (minutes >= 45 && minutes < 60) {
                                        minutesOk = true;
                                    }
                                    break;
                            }

                            if (minutesOk && n.getPlatform().equals("messenger") && p.getFacebookInfo().getMessengerId() != null) {

                                ApiType api = props.getTypeFromString(n.getType());

                                List<String> notifications = messengerDao.getNotificationMessage(api, n.getKeywords(), n.getSendHour(), n.getSendMinutes());
                               
                                for (int i = 0; i < notifications.size(); i++) {
                                    messengerDao.sendMessage(p.getFacebookInfo().getMessengerId(), notifications.get(i));
                                }

                            }

                        }
                    }
                } catch (ApiRequestException ex) {
                    Logger.getLogger(MessengerTimer.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }, 1, 15, TimeUnit.MINUTES);

    }*/
    //  @Schedule(minute = "*/5", hour = "*")
    /*
    public void sendSubscribedInfo() {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        System.out.println("hour: " + Calendar.HOUR_OF_DAY);
        System.out.println("Checking if messages need to be send");
        
        List<PersonalUser> users = userDao.getSubscribedUsers(hour);

        for (PersonalUser p : users) {
            System.out.println("Sending to: " + p.getFirstName());
            for (NotificationPreferences n : p.getNotification()) {
                //checking on Platform choosen by user for getting notifications
                //checking if user has a messengerid
                if (n.getPlatform().equals("facebook") && p.getFacebookInfo().getMessengerId() != null) {
                    messengerDao.sendMessage(p.getFacebookInfo().getMessengerId(), "You subscribed for messages at " + hour + "h.");
                     
                }
                
            }
        }

    }*/
}
