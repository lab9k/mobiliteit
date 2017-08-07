/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import com.google.gson.Gson;
import java.util.List;

/**
 *
 * @author Jana
 */
public class Delay implements IApiModel {

    String route;
    int minutes;
    
    double normal_time; //in minutes
    double real_time; //in minutes
    double length; //in km
    List<Risk> alerts_on_route;

    public List<Risk> getAlerts_on_route() {
        return alerts_on_route;
    }

    public void setAlerts_on_route(List<Risk> alerts_on_route) {
        this.alerts_on_route = alerts_on_route;
    }

    
    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }
    
    
    public double getNormal_time() {
        return normal_time;
    }

    public void setNormal_time(double normal_time) {
        this.normal_time = normal_time;
    }

    public double getReal_time() {
        return real_time;
    }

    public void setReal_time(double real_time) {
        this.real_time = real_time;
    }

    public Delay(String route, int delayMinutes,double normal_time, double real_time,double length) {
        this.route = route;
        this.minutes = delayMinutes;
        this.normal_time = normal_time;
        this.real_time = real_time;
        this.length = length;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "Delay{" + "route=" + route + ", minutes=" + minutes + ", normal_time=" + normal_time + ", real_time=" + real_time + ", length=" + length + ", alerts_on_route=" + alerts_on_route + '}';
    }

    
    

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.COYOTE;
    }
    
    
}
