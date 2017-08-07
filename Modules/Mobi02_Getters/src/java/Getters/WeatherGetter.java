/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.ApiType;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Weather;
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
import javax.ejb.Local;
import javax.ejb.Stateless;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ruben
 */
@Stateless
public class WeatherGetter{

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String PROP_NAME = "URLWeatherUnderground";
    private String url;

    @PostConstruct
    public void init() {
        url = propertyBean.getProperty(PROP_NAME);
    }

    public Weather getForecast(int period) throws Exception {
        //Get Raw Json data from UW API
        JsonNode data = new JsonNode(getRawData());
        //Get the forecast of the next few days
        JSONArray forecast = data.getObject().getJSONObject("forecast").getJSONObject("simpleforecast").getJSONArray("forecastday");
        JSONArray forecastDay = data.getObject().getJSONObject("forecast").getJSONObject("txt_forecast").getJSONArray("forecastday");
        //Gets the first item of the array, this is todays forecast
        JSONObject obj = forecast.getJSONObject(period);
        return new Weather(
                obj.getJSONObject("low").getDouble("celsius"),
                obj.getJSONObject("high").getDouble("celsius"),
                obj.getJSONObject("date").getString("weekday"),
                obj.getString("icon_url").replace("http://", "https://"), //WU returns a unsafe http url, but https works aswell
                obj.getJSONObject("avewind").getDouble("kph"),
                obj.getString("conditions"),
                obj.getDouble("avehumidity"),
                forecastDay.getJSONObject(period).getDouble("pop")
        );

    }

    public String getRawData() throws ApiRequestException {
        try {
            return Unirest.get(url).asJson().getBody().toString();
        } catch (UnirestException ex) {
            Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException("Weather", ex.getMessage());
        }
    }

    public List<IApiModel> getDataModel(){
        try {
            ArrayList<IApiModel> model = new ArrayList<>();
            model.add(getForecast(0));
            return model;
        } catch (Exception ex) {
            Logger.getLogger(WeatherGetter.class.getName()).log(Level.SEVERE, null, ex);
            
            return new ApiRequestException(ApiType.WEATHER.toString(),ex.getMessage()).getModelList();
        }
    }

}
