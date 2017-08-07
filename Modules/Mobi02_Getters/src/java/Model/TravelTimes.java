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
public class TravelTimes implements IApiModel {

    double normalTime_clockwise = 0;
    double realTime_clockwise = 0;
    double normalTime_antiClockwise = 0;
    double realTime_antiClockwise = 0;
    double length_clockwise = 0;
    double length_antiClockwise = 0;

    public TravelTimes() {
    }

    @Override
    public String toString() {
        return "TravelTimes{" + "normalTime_clockwise=" + normalTime_clockwise + ", realTime_clockwise=" + realTime_clockwise + ", normalTime_antiClockwise=" + normalTime_antiClockwise + ", realTime_antiClockwise=" + realTime_antiClockwise + ", length_clockwise=" + length_clockwise + ", length_antiClockwise=" + length_antiClockwise + '}';
    }

    
    public double getNormalTime_clockwise() {
        return normalTime_clockwise;
    }

    public void setNormalTime_clockwise(double normalTime_clockwise) {
        this.normalTime_clockwise = normalTime_clockwise;
    }

    public double getRealTime_clockwise() {
        return realTime_clockwise;
    }

    public void setRealTime_clockwise(double realTime_clockwise) {
        this.realTime_clockwise = realTime_clockwise;
    }

    public double getNormalTime_antiClockwise() {
        return normalTime_antiClockwise;
    }

    public void setNormalTime_antiClockwise(double normalTime_antiClockwise) {
        this.normalTime_antiClockwise = normalTime_antiClockwise;
    }

    public double getRealTime_antiClockwise() {
        return realTime_antiClockwise;
    }

    public void setRealTime_antiClockwise(double realTime_antiClockwise) {
        this.realTime_antiClockwise = realTime_antiClockwise;
    }

    public double getLength_clockwise() {
        return length_clockwise;
    }

    public void setLength_clockwise(double length_clockwise) {
        this.length_clockwise = length_clockwise;
    }

    public double getLength_antiClockwise() {
        return length_antiClockwise;
    }

    public void setLength_antiClockwise(double length_antiClockwise) {
        this.length_antiClockwise = length_antiClockwise;
    }
    
    

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.TRAVELTIMES;
    }

}
