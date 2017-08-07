/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.Haltes;
import Model.IApiModel;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class LijnstopsGetter {

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String PROP_NAME = "URLLijnhaltes";
    private final String LAT_DEF = "LatDefault";
    private final String LON_DEF = "LonDefault";
    private final String RAD_DEF = "RadDefault";
    private String url;
    private double lat, lon;
    private int rad;

    @PostConstruct
    public void init() {
        lat = Double.parseDouble(propertyBean.getProperty(LAT_DEF));
        lon = Double.parseDouble(propertyBean.getProperty(LON_DEF));
        rad = Integer.parseInt(propertyBean.getProperty(RAD_DEF));
        setUrl();
    }

    public void setUrl() {
        url = propertyBean.getProperty(PROP_NAME);
        int[] coor = propertyBean.getXandYCoordinates(lat, lon);
        url += coor[0];
        url += '/';
        url += coor[1];
        url += '/';
        url += rad;
    }

    public void setLocation(double lat, double lon, int rad) {
        this.lat = lat;
        this.lon = lon;
        setUrl();
    }

    public List<IApiModel> getDataModel() {
        try {
            return createHaltes();
        } catch (Exception ex) {
            //Logger.getLogger(LijnstopsGetter.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public List<IApiModel> createHaltes() throws ApiRequestException {
        //Get Raw Json data from UW API
        List<IApiModel> haltes = new ArrayList<>();
        try {
            JsonNode data = new JsonNode(getRawData());

            JSONArray o = data.getArray();

            for (int i = 0; i < o.length(); i++) {
                JSONObject j = (JSONObject) o.get(i);

                Haltes halte = new Haltes();
                /*possible to get more information about the different lines passing there*/
                JSONObject coordinaat = j.getJSONObject("coordinaat");
                halte.setX(coordinaat.getInt("x"));
                halte.setY(coordinaat.getInt("y"));
                halte.setLon(coordinaat.getDouble("ln"));
                halte.setLat(coordinaat.getDouble("lt"));
                halte.setAfstand(j.getInt("afstand"));
                halte.setHalteNaam(j.getString("omschrijvingKort"));
                halte.setHalteNummer(j.getInt("halteNummer"));

                haltes.add(halte);
            }
        } catch (ApiRequestException | JSONException e) {
            return null;
        }
        return haltes;
    }

    public String getRawData() throws ApiRequestException {
        try {
            //kan null returnen
            return Unirest.get(url).asString().getBody();
        } catch (Exception ex) {
            Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("Lijnhaltes", ex.getMessage());
        }

    }
}
