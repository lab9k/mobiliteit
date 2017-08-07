/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.RoadworkGipod;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
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
public class GipodGetter {
 
    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String PROP_NAME = "URLGipod";
    private String url;

    @PostConstruct
    public void init() {
        url = propertyBean.getProperty(PROP_NAME);
    }

    
    public String getRawData() throws ApiRequestException {
        try {
            return Unirest.get(url).asJson().getBody().toString();
            
        } catch (UnirestException ex) {
            throw new ApiRequestException("gipod", ex.getMessage());
        }
    }

    public List<IApiModel> getDataModel(){
        try {
            return getRoadworksGipod();
        } catch (ApiRequestException ex) {
            return new ApiRequestException(ApiType.GIPOD.toString(),ex.getMessage()).getModelList();
        }
    }

    private List<IApiModel> getRoadworksGipod() throws ApiRequestException {
        JsonNode data = new JsonNode(getRawData());
        List<IApiModel> roadworks = new ArrayList<>();
       
        JSONArray o = data.getArray();
        for (int i = 0; i < o.length(); i++) {
            JSONObject j = (JSONObject) o.get(i);
            RoadworkGipod rw = new RoadworkGipod();
            rw.setCity("Gent");
            rw.setGipodId(j.getInt("gipodId"));
            rw.setOwner(j.getString("owner"));
            rw.setDescription(j.getString("description"));

            rw.setStartDate(getCorrectDate(j.getString("startDateTime")));
            rw.setEndDate(getCorrectDate(j.getString("endDateTime")));
            rw.setDetail(j.getString("detail"));
            JSONObject coor = j.getJSONObject("coordinate");
            JSONArray coorar = coor.getJSONArray("coordinates");

            rw.setLatitude(coorar.getDouble(1));
            rw.setLongitude(coorar.getDouble(0));
            roadworks.add(rw);
        }
        
        
        
        return roadworks;
    }

    private static String getCorrectDate(String d) {
        int index = d.indexOf('T');
        return d.substring(0, index);
    }
}
