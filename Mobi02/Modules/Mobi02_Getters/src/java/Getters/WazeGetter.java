/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Roadwork;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class WazeGetter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String PROP_NAME = "URLWaze";
    private String url;

    @PostConstruct
    public void init() {
        url = propertyBean.getProperty(PROP_NAME);
    }

    private List<IApiModel> createRoadworks() throws ApiRequestException {
        Locale locale = new Locale("nl");
        Locale.setDefault(locale);
        //getting resourcebundle for translating terms
        ResourceBundle labels = ResourceBundle.getBundle("Properties.Languages.LabelsBundle", Locale.getDefault());
        
        //Get Raw Json data from Waylay with source waze
        List<IApiModel> roadworks = new ArrayList<>();
        JsonNode data = new JsonNode(getRawData());
        JSONArray o = data.getObject().getJSONArray("alerts");
        for (int i = 0; i < o.length(); i++) {
            JSONObject j = (JSONObject) o.get(i);

            if (j.getString("type").equalsIgnoreCase("ROAD_CLOSED")) {
                Roadwork r = new Roadwork();
                if (j.has("city")) {
                    r.setCity(j.getString("city"));
                }
                if (j.has("type")) {
                    try {
                        String type = j.getString("type").replaceAll(" ", "_").toLowerCase();
                        r.setType(labels.getString(type));
                    } catch (MissingResourceException e) {
                        // System.out.println("type niet in local files");
                        r.setType(j.getString("type"));
                    }
                }
                if (j.has("subtype")) {
                    try {
                        String type = j.getString("subtype").replaceAll(" ", "_").toLowerCase();
                        r.setSubtype(labels.getString(type));
                    } catch (MissingResourceException e) {
                        // System.out.println("type niet in local files");
                        r.setSubtype(j.getString("subtype"));
                    }
                    
                }
                if (j.has("street")) {
                    r.setStreet(j.getString("street"));
                }
                if (j.has("location")) {
                    JSONObject loc = j.getJSONObject("location");
                    r.setLongitude(loc.getDouble("x"));
                    r.setLatitude(loc.getDouble("y"));
                }
                if (j.has("reportDescription")) {
                    r.setDescription(j.getString("reportDescription"));
                }
                r.setPubDate(j.get("pubMillis").toString());
                roadworks.add(r);
            }

        }
        return roadworks;

    }

    public String getRawData() throws ApiRequestException {
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (UnirestException ex) {
            Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("Waze", ex.getMessage());
        }
    }

   
    public List<IApiModel> getDataModel(){
        try {
            return createRoadworks();
        } catch (ApiRequestException ex) {
            //Logger.getLogger(WazeGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        }
    }

}
