/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.BlueBikeParking;
import Model.IApiModel;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruben
 */
@Stateless
public class BluebikeGetter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String[] PROP_NAME = {"URL1Bluebike", "URL2Bluebike"};
    private ArrayList<String> urls;

    @PostConstruct
    public void init() {
        urls = new ArrayList<>();
        for (String s : PROP_NAME) {
            urls.add(propertyBean.getProperty(s));
        }
    }

    private List<IApiModel> getData() {
        try {
            ArrayList<IApiModel> blueBikeParkings = new ArrayList<>();

            for (String url : urls) {
                JSONObject jsonObject = new JSONObject(getRawData(url));
                blueBikeParkings.add(createBBParking(jsonObject));
            }
            return blueBikeParkings;
        } catch (ApiRequestException | JSONException e) {
            
            return new ApiRequestException(ApiType.BLUEBIKE.toString(), e.getMessage()).getModelList();
        }
    }


    /*
    * Get the Raw Json Answer from the WU API
    *
     */
    public String getRawData(String url) throws ApiRequestException {

        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url)
                    .asJson();
            if (jsonResponse.getStatus() != 200) {
                return null;
            } else {
                return jsonResponse.getBody().toString();
            }
        } catch (UnirestException ex) {
            //Logger.getLogger(BlueBike.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }

    }

    private IApiModel createBBParking(JSONObject j) throws ApiRequestException {

        BlueBikeParking b = new BlueBikeParking();
        String mydata = ((JSONObject) j.get("geometry")).get("coordinates").toString();
        Pattern pattern = Pattern.compile("\\[(.*),(.*)\\]");
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            b.setLongitude(Double.parseDouble(matcher.group(1)));
            b.setLatitude(Double.parseDouble(matcher.group(2)));
        }
        b.setName(((JSONObject) j.get("properties")).getString("contextEntity"));
        JSONArray att = (JSONArray) ((JSONObject) j.get("properties")).get("attributes");
        for (int k = 0; k < att.length(); k++) {
            JSONObject attribute = (JSONObject) att.get(k);
            switch (attribute.getString("attributeName")) {
                case "CapacityTotal":
                    b.setTotalCap(attribute.getInt("value"));
                    break;
                case "CapacityInUse":
                    b.setInUse(attribute.getInt("value"));
                    break;
                case "CapacityAvailable":
                    b.setAvailable(attribute.getInt("value"));
                    break;
                case "CapacityInMaintenance":
                    b.setInMaintenance(attribute.getInt("value"));
                    break;
                case "PriceEuro":
                    b.setPrice(attribute.getInt("value"));
                    break;
            }
        }
        //System.out.println(b.toString());
        return b;
    }

    public List<IApiModel> getDataModel() {
        return getData();
    }

    public String getRawData() throws ApiRequestException {
        String result = "";
        for (int i = 0; i < urls.size(); i++) {

            result += getRawData(urls.get(i)) + ((i == (urls.size() - 1)) ? "" : ",");
        }
        return result;
    }
}
