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
public class Counting implements IApiModel, Comparable<Counting>{
    double longitude;
    double latitude;
    int count;
    int speed;
    int OCC;
    long timestamp;
    String contextEntity;

    public String getContextEntity() {
        return contextEntity;
    }

    public void setContextEntity(String contextEntity) {
        this.contextEntity = contextEntity;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getOCC() {
        return OCC;
    }

    public void setOCC(int OCC) {
        this.OCC = OCC;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Counting{" + "longitude=" + longitude + ", latitude=" + latitude + ", count=" + count + ", speed=" + speed + ", OCC=" + OCC + ", timestamp=" + timestamp + '}';
    }

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.R40TRAFFIC;
    }
    
    @Override
    public int compareTo(Counting o) {
        return this.count - o.count;
    }
}
