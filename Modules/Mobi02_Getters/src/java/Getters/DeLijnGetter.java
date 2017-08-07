/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.Busses;
import Model.Haltes;
import Model.IApiModel;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class DeLijnGetter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private final String PROP_NAME = "URLDeLijn";

    private String url;
    private String genericUrl;
    private List<IApiModel> haltes;

    @PostConstruct
    public void init() {
        genericUrl = propertyBean.getProperty(PROP_NAME);
        haltes = new ArrayList<>();
    }

    /* public void setHaltes(List<IApiModel> haltes) {
        this.haltes = haltes;
    }*/
    private Set<IApiModel> createBusses() {
        
        Set<IApiModel> busSet = new HashSet<>();
        JsonNode data = new JsonNode(getRawData());
        //check als de data een array is -> anders is het een ApiRequestExceptie
        if (data.isArray()) {
            JSONArray o = data.getArray();

            for (int i = 0; i < o.length(); i++) {
                JSONObject j = (JSONObject) o.get(i);

                try {
                    JSONObject sortTime = j.getJSONObject("SortTime");
                    Busses bus = new Busses();

                    String sortTimeRaw = sortTime.getString("date");
                    /*format: yyyy-mm-dd hh:mm:ss.ssss - only needed: hh:mm*/
                    int index = sortTimeRaw.indexOf(':'); //index of first :
                    String sortTimeFilterd = sortTimeRaw.substring(index - 2, index + 3);
                    bus.setSortTime(sortTimeFilterd);

                    bus.setBackgroundColor(j.getString("BackgroundColor"));
                    bus.setBackgroundColorEdge(j.getString("BackgroundEdgeColor"));
                    bus.setDescription(j.getString("Description"));

                    String plannedTimeRaw = j.getString("PlannedTime");
                    /*format: yyyy-mm-dd hh:mm:ss.ssss - only needed: hh:mm*/
                    index = plannedTimeRaw.indexOf(':'); //index of first :
                    String plannedTimeFilterd = plannedTimeRaw.substring(index - 2, index + 3);
                    bus.setPlannedTime(plannedTimeFilterd);

                    bus.setForegroundColor(j.getString("ForegroundColor"));
                    bus.setForegroundColorEdge(j.getString("ForegroundEdgeColor"));
                    bus.setStopname(j.getString("StopName"));
                    bus.setVehicle(j.getString("Vehicle"));
                    bus.setTarget(j.getString("Target"));
                    String id = j.getString("PublicId");
                    if (id.equals("L") || id.equals("IC") || id.equals("P")) {
                        //it's not a bus but a train
                        throw new Exception("not a bus");
                    } else {
                        bus.setPublicId(id);
                    }
  
                    if (j.has("Platform")) {
                        bus.setPerron(j.getString("Platform"));
                    }

                    busSet.add(bus);
                } catch (Exception e) {
                    /* not a De Lijn halte*/
                    //System.out.println("nmbs!");
                }
            }
        } else {
            JSONObject j = data.getObject().getJSONObject("ApiHttpRequestException");
            ApiRequestException ex = new ApiRequestException((String) j.getString("ApiType"), (String) j.get("message"));

            busSet.add(ex);
        }
        return busSet;

    }

    public List<IApiModel> getDataModel(IApiModel halte) {
        url = genericUrl + ((Haltes) halte).getHalteNummer();
        List<IApiModel> busses = new ArrayList<>(createBusses());
        return busses;
    }

    public String getRawData() {
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (UnirestException ex) {
            
            return new ApiRequestException("Lijnen", "Er zijn geen bussen gevonden in de aangegeven buurt.").getAsJSON();
        }
    }
}
