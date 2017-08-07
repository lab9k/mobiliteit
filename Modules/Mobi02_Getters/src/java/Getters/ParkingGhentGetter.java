/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Parking;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ruben
 */

public class ParkingGhentGetter{

    
    private PropertyLoaderBean propertyBean;
    
    private static final String PROP_NAME = "URLParkingGhent";
    private String url;
    
    
    
    public ParkingGhentGetter(PropertyLoaderBean propertyBean){
        this.propertyBean = propertyBean;
        url = propertyBean.getProperty(PROP_NAME);
       }
    
    
    private List<IApiModel> createParkings(String jsonText) {
            JSONArray jsonArray = new JSONArray(jsonText);
        
        List<IApiModel> parkingstemp = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject j = (JSONObject) jsonArray.get(i);
            Parking p = new Parking();
            p.setAddress(j.getString("address"));
            p.setName(j.getString("name"));
            p.setTotalCapacity(j.getInt("totalCapacity"));
            p.setAvailableCapacity((int) ((JSONObject) j.get("parkingStatus")).get("availableCapacity"));
            p.setLastModifiedDate((String) ((JSONObject) j.get("parkingStatus")).get("lastModifiedDate"));
            p.setOpen((boolean) ((JSONObject) j.get("parkingStatus")).get("open"));
            p.setLatitude(j.getDouble("latitude"));
            p.setLongitude(j.getDouble("longitude"));
            //System.out.println(p.toString());
            parkingstemp.add(p);
        }
        return parkingstemp;
    }
    
    public String getRawData() throws ApiRequestException{
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (UnirestException ex) {
            //Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("ParkingGhent",ex.getMessage());
        }
    }
    
    public List<IApiModel> getDataModel(){
        try{
            return createParkings(getRawData());
        }catch(Exception e){
            return new ApiRequestException(this.getClass().getSimpleName(), e.getMessage()).getModelList();
        }
    }
    
}
