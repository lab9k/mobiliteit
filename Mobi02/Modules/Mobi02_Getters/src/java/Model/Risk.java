/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import com.google.gson.Gson;

/**
 *
 * @author Gebruiker
 */
public class Risk implements IApiModel{
    //Remark: not all the fields are obligatory
    private String city; 
    private String type;
    private String subtype;
    private String street;
    private double longitude;
    private double latitude;
    private String timestamp;
    private int speedLimit;
    private ApiType aType;

    public Risk() {
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }
    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Risk{" + "city=" + city + ", type=" + type + ", subtype=" + subtype + ", street=" + street + ", longitude=" + longitude + ", latitude=" + latitude + ", timestamp=" + timestamp + '}';
    }


    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        if(aType != null){
            return aType;
        }
        return ApiType.WAYLAY;
    }
}
