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
public class Haltes implements IApiModel{

    private int afstand;
    private int halteNummer;
    private String halteNaam;
    private int x;
    private int y;
    private double lon;
    private double lat;
    private String perron;
    

    public Haltes() {
    }

    public String getHalteNaam() {
        return halteNaam;
    }

    public void setHalteNaam(String halteNaam) {
        this.halteNaam = halteNaam;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    
    public int getAfstand() {
        return afstand;
    }

    public void setAfstand(int afstand) {
        this.afstand = afstand;
    }

    public int getHalteNummer() {
        return halteNummer;
    }

    public void setHalteNummer(int halteNummer) {
        this.halteNummer = halteNummer;
    }

     public String getPerron() {
        return perron;
    }

    public void setPerron(String perron) {
        this.perron = perron;
    }

    @Override
    public String toString() {
        return "Haltes{" + "afstand=" + afstand + ", halteNummer=" + halteNummer + ", halteNaam=" + halteNaam + ", x=" + x + ", y=" + y + ", lon=" + lon + ", lat=" + lat + ", perron=" + perron + '}';
    }

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.DELIJN;
    }
    
}
