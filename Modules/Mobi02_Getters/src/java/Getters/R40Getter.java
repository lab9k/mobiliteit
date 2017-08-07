/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.Counting;
import Model.IApiModel;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class R40Getter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String PROP_NAME = "URLR40Traffic";
    private String url;
    
    @PostConstruct
    public void init() {
        url= propertyBean.getProperty(PROP_NAME);
    }
    
    
    private static List<IApiModel> createCountings(JSONArray jsonArray) {
        List<IApiModel> countingstemp = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject j = (JSONObject) jsonArray.get(i);
            Counting c = new Counting();
            String mydata = ((JSONObject) j.get("geometry")).get("coordinates").toString();
            Pattern pattern = Pattern.compile("\\[(.*),(.*)\\]");
            Matcher matcher = pattern.matcher(mydata);
            if (matcher.find()) {
                c.setLongitude(Double.parseDouble(matcher.group(1)));
                c.setLatitude(Double.parseDouble(matcher.group(2)));
            }
            JSONArray att = (JSONArray) ((JSONObject) j.get("properties")).get("attributes");
            for (int k = 0; k < att.length(); k++) {
                JSONObject attribute = (JSONObject) att.get(k);
                switch (attribute.getString("attributeName")) {
                    case "speed":
                        c.setSpeed(attribute.getInt("value"));
                        break;
                    case "OCC":
                        c.setOCC(attribute.getInt("value"));
                        break;
                    case "Count":
                        c.setCount(attribute.getInt("value"));
                        break;
                    case "Timestamp":
                        c.setTimestamp(attribute.getInt("value"));
                        break;
                }
            }
            c.setContextEntity(((JSONObject) j.get("properties")).getString("contextEntity"));
            //System.out.println(c.toString());
            countingstemp.add(c);
        }
        return countingstemp;
    }
    
    public String getRawData() throws ApiRequestException {
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (UnirestException ex) {
            //Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("Weather", ex.getMessage());
        }
    }

    public List<IApiModel> getDataModel() {
        try {
            JSONObject jsonObject = new JSONObject(getRawData());
            JSONArray jsonArray = (JSONArray) jsonObject.get("features");        
            return createCountings(jsonArray);
        } catch (ApiRequestException ex) {
            //Logger.getLogger(R40Getter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        } catch (Exception ex){
            return new ApiRequestException(ApiType.R40TRAFFIC.toString(),ex.getMessage()).getModelList();
        }
    }
}
