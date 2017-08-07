/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Parking;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.json.JSONObject;

/**
 *
 * @author ruben
 */

public class ParkingNmbsGetter{

    
    private PropertyLoaderBean propertyBean;
    
    private static final String PROP_NAME = "URLParkingNmbs";
    private String url;
    
    
    
    public ParkingNmbsGetter(PropertyLoaderBean propertyBean) {
        this.propertyBean = propertyBean;
        url = propertyBean.getProperty(PROP_NAME);
       }
    
    
    public Parking getParking() throws ApiRequestException {
        try{
        JsonNode data = new JsonNode(getRawData());
        //The raw data is a JSON array
        JSONObject superObj = (JSONObject) data.getArray().get(0);
        JSONObject obj = superObj.getJSONObject("parkingStatus");

        return new Parking(
                "Parking Gent St. Pieters",
                "Sint-Denijslaan, 9000 Gent",
                obj.getInt("totalCapacity"),
                obj.getInt("availableCapacity"),
                (obj.getInt("open") == 0),
                obj.getString("lastModifiedDate"),
                3.705354,
                51.037522
        );
        }catch(Exception ex){
            throw new ApiRequestException(ApiType.PARKINGNMBS.toString(), ex.getMessage().replace('"', ' ') );
        }

    }

    public List<IApiModel> getDataModel(){
        try {
            ArrayList<IApiModel> model = new ArrayList<>();
            model.add(getParking());
            return model;
        } catch (ApiRequestException ex) {
            //Logger.getLogger(ParkingNmbsGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        }
    }
    
    public String getRawData() throws ApiRequestException{
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (Exception ex) {
            //Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("ParkingNmbs",ex.getMessage());
        }
    }
    
    
    
}
