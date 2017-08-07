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
 * @author Sven
 */
public class BlueBikeParking implements IApiModel{
    double longitude;
    double latitude;
    String name;
    int totalCap;
    int inUse;
    int available;
    int inMaintenance;
    double price;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalCap() {
        return totalCap;
    }

    public void setTotalCap(int totalCap) {
        this.totalCap = totalCap;
    }

    public int getInUse() {
        return inUse;
    }

    public void setInUse(int inUse) {
        this.inUse = inUse;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getInMaintenance() {
        return inMaintenance;
    }

    public void setInMaintenance(int inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "BlueBikeParking{" + "longitude=" + longitude + ", latitude=" + latitude + ", name=" + name + ", totalCap=" + totalCap + ", inUse=" + inUse + ", available=" + available + ", inMaintenance=" + inMaintenance + ", price=" + price + '}';
    }
    
    
    
    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.BLUEBIKE;
    }
    
}
