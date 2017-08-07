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
public class Train implements IApiModel {

    String dest;
    int deptime;
    int delay;
    String vehicle;
    String platform;
    String station;
    String routeType;
    String totalTravelTime;

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public int getDeptime() {
        return deptime;
    }

    public void setDeptime(int deptime) {
        this.deptime = deptime;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(String totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }
    
    
    
    

    @Override
    public String toString() {
        return "Train{" + "dest=" + dest + ", deptime=" + deptime + ", delay=" + delay + ", vehicle=" + vehicle + ", platform=" + platform + ", station=" + station + '}';
    }

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.TRAINSGHENT;
    }

}
