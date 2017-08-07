/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Risk;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.HttpResponse;
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
 * @author ruben
 */
@Stateless
public class WaylayGetter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private String url, key, secret, prop;
    private static final String PROP_NAME = "URLWaylay";
    private static final String PW_NAME = "KeyWaylay";
    private static final String SECRET_NAME = "SecretWaylay";
    private static final String EXTRA_PROP = "PROPWaylay";

    @PostConstruct
    public void init() {
        url = propertyBean.getProperty(PROP_NAME);
        prop = propertyBean.getProperty(EXTRA_PROP);
        key = propertyBean.getPassword(PW_NAME);
        secret = propertyBean.getPassword(SECRET_NAME);
    }

    public String getRawData() throws ApiRequestException {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url)
                    .basicAuth(key, secret)
                    .header("Content-Type", "application/json")
                    .body(prop)
                    .asJson();
            return jsonResponse.getBody().toString();
        } catch (UnirestException ex) {
            //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public List<IApiModel> createRisks() throws ApiRequestException {
        try{
        Locale locale = new Locale("nl");
        Locale.setDefault(locale);
        ResourceBundle labels = ResourceBundle.getBundle("Properties.Languages.LabelsBundle", Locale.getDefault());
        List<IApiModel> risks = new ArrayList<>();
        //Get Raw Json data from Waylay with source waze
        JsonNode data = new JsonNode(getRawData());

        JSONArray o = data.getObject().getJSONObject("rawData").getJSONArray("result");
        for (int i = 0; i < o.length(); i++) {
            JSONObject j = (JSONObject) o.get(i);

            JSONObject pl = j.getJSONObject("payload");
            JSONObject spl = j.getJSONObject("sourcePayload");

            Risk r = new Risk();
            if (spl.has("city")) {
                r.setCity(spl.getString("city")); //sometimes there is no city in the message
            }
            if (spl.has("city")) {
                r.setCity(spl.getString("city")); //sometimes there is no city in the message
            }
            if (spl.has("type")) {
                try {
                    String type = spl.getString("type").replaceAll(" ", "_").toLowerCase();
                    r.setType(labels.getString(type));
                } catch (MissingResourceException e) {
                    System.out.println("type niet in local files");
                    r.setType(spl.getString("type"));
                }
            }
            if (pl.has("subtype")) {
                try {
                    String subtype = pl.getString("subtype").replaceAll(" ", "_").toLowerCase();
                    r.setSubtype(labels.getString(subtype));
                } catch (MissingResourceException e) {
                    // System.out.println("subtype niet in local files");
                    r.setSubtype(pl.getString("subtype"));
                }
            }
            if (pl.has("street")) {
                r.setStreet(pl.getString("street"));
            }
            if (pl.has("longitude")) {
                r.setLongitude(pl.getDouble("longitude"));
            }
            if (pl.has("latitude")) {
                r.setLatitude(pl.getDouble("latitude"));
            }
            r.setTimestamp(j.get("timestamp").toString());

            risks.add(r);
        }
        //check if there is data 
        if (risks.size() > 0) {
            return risks;
        } else {
            return new ApiRequestException(this.getClass().getSimpleName(),false, "There is no data to display.").getModelList();
        }
        }catch(Exception ex){
             //Logger.getLogger(WaylayGetter.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ApiType.WAYLAY.toString(),ex.getMessage()).getModelList();
        }

    }

    public List<IApiModel> getDataModel(){
        try {
            return createRisks();
        } catch (ApiRequestException ex) {
            //Logger.getLogger(WaylayGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        } catch (Exception ex){
            return new ApiRequestException(ApiType.WAYLAY.toString(),ex.getMessage()).getModelList();
        }
    }

}
