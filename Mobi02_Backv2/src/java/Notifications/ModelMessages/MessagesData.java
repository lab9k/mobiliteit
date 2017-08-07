/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

import Api.ApiType;
import Api.Caching.CachedData;
import Getters.TrainRouteGetter;
import Model.BlueBikeParking;
import Model.Delay;
import Model.IApiModel;
import Model.Parking;
import Model.TrainRoute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 *
 * @author Gebruiker
 */
@Singleton(name = "messengerdata")

public class MessagesData {

    @EJB
    private CachedData cache;

    @EJB
    private CoyoteMessages coyMess;

    @EJB
    private WeatherMessages weatherMess;

    @EJB
    private BlueBikeMessages blueMess;

    @EJB
    private ParkingMessages parkMess;
    
    @EJB
    private TrainMessages trainMess;
    
    @EJB
    private TrainRouteGetter trains;

    
    /**
     *
     * @param keywords
     * @return
     */
    public List<String> getBlueBikeNotification(String keywords) {
        //getting data from cache
        List<IApiModel> data = new ArrayList<>();
        data = cache.getDataFromApi(ApiType.BLUEBIKE);
        //keywords may consist of more then one route
        String[] parts = keywords.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        List<String> keys = Arrays.asList(parts);

        //adding selected bluebike locations
        List<IApiModel> selected = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof BlueBikeParking) {
                String r = ((BlueBikeParking) data.get(i)).getName();
                if (keys.contains(r)) {
                    selected.add(data.get(i));
                }
            }
        }
        return blueMess.getMessages(selected);
    }
    
    /**
     *
     * @param keywords
     * @return
     */
    public List<String> getWeatherNotification(String keywords) {
        //getting data from cache
        List<IApiModel> data = cache.getDataFromApi(ApiType.WEATHER);
        //keywords are not used in this api
        return weatherMess.getMessages(data);

    }

    /**
     *
     * @param keywords
     * @return
     */
    public List<String> getCoyoteNotification(String keywords) {
        //getting data from cache
        List<IApiModel> data = new ArrayList<>();
        data = cache.getDataFromApi(ApiType.COYOTE);

        //keywords may consist of more then one route
        String[] parts = keywords.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        List<String> keys = Arrays.asList(parts);

        //adding selected delays
        List<IApiModel> selected = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof Delay) {
                String r = ((Delay) data.get(i)).getRoute();
                if (keys.contains(r)) {
                    //route asked --> adding to list
                    selected.add(data.get(i));
                }
            }
        }
        //getting messages in a list of strings from list of ApiModel elements, how to form a messages depends on the type
        return coyMess.getMessages(selected);
    }

    

    /**
     *
     * @param keywords
     * @return
     */
    public List<String> getParkingNotification(String keywords) {
        //getting data from cache
        List<IApiModel> data = new ArrayList<>();
        data = cache.getDataFromApi(ApiType.PARKINGS);
        //keywords may consist of more then one route
        String[] parts = keywords.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        List<String> keys = Arrays.asList(parts);

        //adding selected parkings
        List<IApiModel> selected = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) instanceof Parking) {
                String r = ((Parking) data.get(i)).getName();
                if (keys.contains(r)) {
                    selected.add(data.get(i));
                }
            }
        }
        return parkMess.getMessages(selected);
    }

    /**
     *
     * @param keywords
     * @param hour
     * @param minutes
     * @return
     */
    public List<String> getTrainNotification(String keywords, int hour, int minutes) {
        //getting data from cache
       // List<IApiModel> data = cache.getDataFromApi(ApiType.TRAINSGHENT);
        
        Date date = new Date();
        SimpleDateFormat d = new SimpleDateFormat("ddMMYY");
        
        String dateString = d.format(date);
        
        StringBuilder sb = new StringBuilder();
        sb.append(hour);
        sb.append(minutes);
        
        //keywords may consist of more then one route
        String[] parts = keywords.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        //at even places --> departure stations
        //at odd places --> arrival stations
        List<String> depart = new ArrayList<>();
        List<String> arrival = new ArrayList<>();
        //map is no option because duplicates are possible

        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
            parts[i] = parts[i].replaceAll("\\s", "+");
            if ((i % 2) == 0) {
                depart.add(parts[i]);
            } else {
                arrival.add(parts[i]);
            }
        }
        
        List<IApiModel> selected = new ArrayList<>();
        for(int i = 0; i<depart.size(); i++){
            //api call to get trains from to at that moment
            List<IApiModel> l = trains.getDataModel(depart.get(i), arrival.get(i), null, null, "depart");
            //making sublist : maximum 2 notifications
            if(l.size()>2){
                for(int j=0; j<3; j++){
                    selected.add(l.get(j));
                }
            } else{
                selected = l;
            }
        }


        return trainMess.getMessages(selected);
    }
}
