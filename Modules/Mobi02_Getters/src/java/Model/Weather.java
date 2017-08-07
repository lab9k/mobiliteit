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
 * @author ruben
 */
public class Weather implements IApiModel {

    private double celsiusMin;
    private double celsiusMax;
    private String day;
    private String iconUrl;
    private double avgWindKph;
    private String hint;
    private double avehumidity;
    private double chanceRain;

    public Weather(double celsiusMin, double celsiusMax, String day, String iconUrl, double avgWindKph, String hint, double avehumidity, double chanceRain) {
        this.celsiusMin = celsiusMin;
        this.celsiusMax = celsiusMax;
        this.day = day;
        this.iconUrl = iconUrl;
        this.avgWindKph = avgWindKph;
        this.hint = hint;
        this.avehumidity = avehumidity;
        this.chanceRain = chanceRain;
    }

    public Weather() {
    }

    public double getCelsiusMin() {
        return celsiusMin;
    }

    public void setCelsiusMin(double celsiusMin) {
        this.celsiusMin = celsiusMin;
    }

    public double getCelsiusMax() {
        return celsiusMax;
    }

    public void setCelsiusMax(double celsiusMax) {
        this.celsiusMax = celsiusMax;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public double getAvgWindKph() {
        return avgWindKph;
    }

    public void setAvgWindKph(double avgWindKph) {
        this.avgWindKph = avgWindKph;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public double getAvehumidity() {
        return avehumidity;
    }

    public void setAvehumidity(double avehumidity) {
        this.avehumidity = avehumidity;
    }

    public double getChanceRain() {
        return chanceRain;
    }

    public void setChanceRain(double chanceRain) {
        this.chanceRain = chanceRain;
    }
    
    
    
    
    
    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this, Weather.class);
    }

    @Override
    public ApiType getApiType() {
        return ApiType.WEATHER;
    }
    
}
