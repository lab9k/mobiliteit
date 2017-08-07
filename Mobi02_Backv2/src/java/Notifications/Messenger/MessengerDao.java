/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.Messenger;

import Notifications.ModelMessages.MessagesData;
import Api.ApiType;
import Exceptions.ApiNotFoundException;
import Notifications.Updates.NotificationsUpdate;
import Properties.PropertyLoaderBean;
import Rest.FacebookRest;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author ruben
 */
@Stateless
public class MessengerDao {

    @EJB
    private MessagesData messengerData;

    @EJB
    private PropertyLoaderBean prop;

    /**
     *
     * @param userId
     * @return
     */
    public String sendWelcomeCheck(String userId) {
        String message = "Welcome to Mobi02, from now on we will send you periodic messages. You can change your messaging preferences at our site";
        return "";
    }

    /**
     *
     * @param userId
     * @param message
     * @return
     */
    public JsonNode sendMessage(String userId, String message) {
        try {
            //System.out.println("messageke: " + message);
            //System.out.println("id: " + userId);
            // 
            //HttpResponse<JsonNode> jsonResponse = Unirest.post("https://graph.facebook.com/v2.6/me/messages?access_token=EAALZA96ZBdjjwBAMm5fZAD3bIP6jspQhkBCZCZBdlcLHMixecV9ZBtzJHURUhx5EAN5w9i679jZBawE27nEZCvMrHDBiYCxcrGupDB92CjdFRcaC7CPE6yvNXUkwJT1omTqzQTR1g6e7MrOgAZApiX3y28vgDdGZA8Cru58HXTZC2PqZBQZDZD")
            HttpResponse<JsonNode> jsonResponse = Unirest.post(prop.getProperty("Messenger"))
                    .header("Content-Type", "application/json")
                    .body("{"
                            + "  \"recipient\": {"
                            + "    \"id\": \"" + userId + "\""
                            + "  },"
                            + "  \"message\": {"
                            + "    \"text\": \"" + message + "\""
                            + "  }"
                            + "}").asJson();
            //System.out.println("resp " + jsonResponse.getBody());
            return jsonResponse.getBody();
        } catch (UnirestException ex) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING, "Exceptie", ex);
            return null;
        }
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
