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
 * @author Sven Delanoye
 */
public class Parking implements IApiModel{
    String name;
    String address;
    int totalCapacity;
    int availableCapacity;
    boolean open;
    String lastModifiedDate;
    private double longitude;
    private double latitude;

    public Parking(String name, String address, int totalCapacity, int availableCapacity, boolean open, String lastModifiedDate, double longitude, double latitude) {
        this.name = name;
        this.address = address;
        this.totalCapacity = totalCapacity;
        this.availableCapacity = availableCapacity;
        this.open = open;
        this.lastModifiedDate = lastModifiedDate;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    
    

    public Parking() {
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public void setAvailableCapacity(int availableCapacity) {
        this.availableCapacity = availableCapacity;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
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

    @Override
    public String toString() {
        return "Parking{" + "name=" + name + ", address=" + address + ", totalCapacity=" + totalCapacity + ", availableCapacity=" + availableCapacity + ", open=" + open + ", lastModifiedDate=" + lastModifiedDate + ", longitude=" + longitude + ", latitude=" + latitude + '}';
    }
    
    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.PARKINGS;
    }
}
