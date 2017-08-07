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
public class RoadworkGipod implements IApiModel{

    private String startDate;
    private String endDate;
    private String city;
    private String detail;
    private double latitude;
    private double longitude;
    private String description;
    private int gipodId;
    private String owner;

    public RoadworkGipod() {
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGipodId() {
        return gipodId;
    }

    public void setGipodId(int gipodId) {
        this.gipodId = gipodId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "RoadworkGipod{" + "startDate=" + startDate + ", endDate=" + endDate + ", city=" + city + ", detail=" + detail + ", latitude=" + latitude + ", longitude=" + longitude + ", description=" + description + ", gipodId=" + gipodId + ", owner=" + owner + '}';
    }
    
    
    
    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.GIPOD;
    }
    
    
    
}
