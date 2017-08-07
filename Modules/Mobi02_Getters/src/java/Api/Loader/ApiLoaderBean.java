/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Loader;

import Api.ApiType;
import Exceptions.ApiNotFoundException;
import Exceptions.ApiRequestException;
import Getters.*;
import Model.IApiModel;
import Model.ModelConvertor;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 *
 * @author ruben
 */
@Singleton(name = "apiloader")
@DependsOn({"properties"})
public class ApiLoaderBean {

    @EJB(name="WeatherGetter")
    private WeatherGetter weather;

    //private HttpsClientBuilder httpBuilder;

    @EJB
    CoyoteGetter coyote;

    @EJB
    ModelConvertor modelConvertor;

    @EJB
    BluebikeGetter bluebike;

    @EJB(name="ParkingGetter")
    private ParkingGetter parking;

    @EJB
    R40Getter R40;

    @EJB
    TrainGetter trains;

    @EJB
    WaylayGetter waylay;

    @EJB
    WazeGetter waze;

    

    @EJB
    LijnApiGetter lijnapi;

    @EJB
    GipodGetter gipod;
    //add new Api's here

    @PostConstruct
    public void init() {
        //httpBuilder = new HttpsClientBuilder();
    }

    //Method uses the getter to fetch data from the api and returns it as a json string, exceptions also get parsed as json
    public String getModel(String typeString) {
        try {
            ApiType type = modelConvertor.getTypeFromString(typeString);
            switch (type) {
                case BLUEBIKE:
                    return modelConvertor.getModelAsJson(type, bluebike.getDataModel());
                case R40TRAFFIC:
                    return modelConvertor.getModelAsJson(type, R40.getDataModel());
                case PARKINGS:
                    return modelConvertor.getModelAsJson(type, parking.getDataModel(true,true));
                case TRAINSGHENT:
                    return modelConvertor.getModelAsJson(type, trains.getDataModel());
                case WAYLAY:
                    return modelConvertor.getModelAsJson(type, waylay.getDataModel());
                case WAZE:
                    return modelConvertor.getModelAsJson(type, waze.getDataModel());
                case WEATHER:
                    return modelConvertor.getModelAsJson(type, weather.getDataModel());
                case COYOTE:
                    return modelConvertor.getModelAsJson(type, coyote.getDataModel());
                case DELIJN:
                    return modelConvertor.getModelAsJson(type, lijnapi.getDataModel());
//add new Api's here
                case GIPOD:
                    return modelConvertor.getModelAsJson(type, gipod.getDataModel());
                default:
                    throw new ApiNotFoundException();
            }
        } catch (ApiRequestException ex) {
            return ex.getAsJSON();
        }

    }

    public List<IApiModel> getModelAsObject(String typeString) {
        try {
            ApiType type = modelConvertor.getTypeFromString(typeString);
            switch (type) {
                case BLUEBIKE:
                    return bluebike.getDataModel();
                case R40TRAFFIC:
                    return R40.getDataModel();
                case PARKINGS:
                    return parking.getDataModel(true,true);
                case TRAINSGHENT:
                    return trains.getDataModel();
                case WAYLAY:
                    return waylay.getDataModel();
                case WAZE:
                    return waze.getDataModel();
                case WEATHER:
                    return weather.getDataModel();
                case COYOTE:
                    return coyote.getDataModel();
                case DELIJN:
                    return lijnapi.getDataModel();
                case TRAVELTIMES:
                    return coyote.getTrajectValues();
                case GIPOD:
                    return gipod.getDataModel();
                default:
                    throw new ApiRequestException("Api Type not recognized!");
            }
        } catch (ApiRequestException ex) {
            List<IApiModel> list = new ArrayList<>();
            list.add(ex);
            return list;
        }

    }

    public String getModelPersonal(double lon, double lat, int rad) {
        try {
            lijnapi.setPersonalChoices(lat, lon, rad);
            ApiType type = modelConvertor.getTypeFromString("delijnpersonal");
            return modelConvertor.getModelAsJson(type, lijnapi.getDataModel());
        } catch (ApiRequestException ex) {
            Logger.getLogger(ApiLoaderBean.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

    /* public String getModelParkingByName(String name){
        try{
            ApiType type = modelConvertor.getTypeFromString("ParkingGhent", parking.getDataForParking(name));
        } catch (ApiRequestException ex){
            Logger.getLogger(ApiLoaderBean.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }*/
    public String getRoutes() {
        List<String> routes;
        routes = coyote.getRoutes();
       
        Gson gson = new Gson();
        return gson.toJson(routes);
    }

}
