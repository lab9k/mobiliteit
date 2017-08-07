/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import Api.ApiType;
import Api.Caching.CachedData;
import Api.Loader.ApiLoaderBean;
import Database.Dao.StopsDao;
import Exceptions.ApiRequestException;
import Getters.TrainRouteGetter;
import Model.Delay;
import Model.IApiModel;
import Model.Parking;
import Properties.PropertyLoaderBean;
import com.google.gson.Gson;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author ruben
 */
@Path("apidata")
public class ApidataRest {

    @Context
    private UriInfo context;

    private ApiLoaderBean loader;
    private CachedData cache;
    private PropertyLoaderBean props;

    @EJB
    private StopsDao stopsDao;

    /**
     *
     * @param loader
     */
    @EJB(name = "apiloader")
    public void setLoader(ApiLoaderBean loader) {
        this.loader = loader;
    }

    /**
     *
     * @param cache
     */
    @EJB(name = "cacheddata")
    public void setCache(CachedData cache) {
        this.cache = cache;
    }

    /**
     *
     * @param props
     */
    @EJB(name = "properties")
    public void setProp(PropertyLoaderBean props) {
        this.props = props;
    }

    /**
     * Creates a new instance of ApidataRest
     */
    public ApidataRest() {
    }

    /**
     *
     *
     *
     * @param apitype
     * @return
     */
    @GET
    @Path("/{apitype}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@PathParam("apitype") String apitype) {
        try {

            ApiType api = props.getTypeFromString(apitype); //throws exception if apitype is not recognized
            String data = cache.getAsJson(api);
            if (data != null) {
                //Logger.getLogger(ApidataRest.class.getName()).log(Level.FINE, "Returning Cached data");
                return data;
            } else {
                //Logger.getLogger(ApidataRest.class.getName()).log(Level.WARNING, "Cache did not contain api data");
                return new ApiRequestException(apitype, "Geen data beschikbaar").getAsJSON(); //Give an error back rather than getting the data directly from an api.
            }

        } catch (ApiRequestException ex) {
            Logger.getLogger(ApidataRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

    /**
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/routes")
    public String getRoutes() {
        return loader.getRoutes();

    }

    /**
     *
     * @param lon
     * @param lat
     * @param rad
     * @return
     * @throws Exception
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{latitude}/{longitude}/{radius}")
    public String getDeLijn(@PathParam("longitude") double lon, @PathParam("latitude") double lat, @PathParam("radius") int rad) throws Exception {

        try {
            ApiType api = props.getTypeFromString("delijnpersonal");
            return loader.getModelPersonal(lon, lat, rad);

        } catch (ApiRequestException ex) {
            Logger.getLogger(ApidataRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

    /**
     *
     * @param name
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/parkings/{name}")
    public String getPersonalParking(@PathParam("name") String name) {
        try {

            ApiType api = props.getTypeFromString("parkings");
            List<IApiModel> data = cache.getDataFromApi(api);
            if (data != null) {
                String parkingName = props.getParking(name);
                if (parkingName != null) {
                    String namePark;
                    for (int i = 0; i < data.size(); i++) {
                        namePark = ((Parking) data.get(i)).getName();
                        if (namePark.equals(parkingName)) {
                            return data.get(i).getAsJSON();
                        }
                    }
                    return new ApiRequestException("parkings", "Geen data beschikbaar").getAsJSON();
                }
            } else {
                //Logger.getLogger(ApidataRest.class.getName()).log(Level.WARNING, "Cache did not contain api data, retrieved data directly");
                return new ApiRequestException("parkings", "Geen data beschikbaar").getAsJSON();
            }

        } catch (ApiRequestException ex) {
            Logger.getLogger(ApidataRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
        return null;
    }

    /**
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/train/stops")
    public String getAllTrainStops() {
        Gson gson = new Gson();
        return gson.toJson(stopsDao.getAllStopsNames());
    }
    @EJB
    TrainRouteGetter trainsRouteGetter;

    /**
     *
     * @param from
     * @param to
     * @param date
     * @param hour
     * @param timesel
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/train/{from}/{to}/{date}/{hour}/{timesel}")
    public String getTrainRouteFull(@PathParam("from") String from, @PathParam("to") String to, @PathParam("date") String date,
            @PathParam("hour") String hour, @PathParam("timesel") String timesel) {
        Gson gson = new Gson();
        return gson.toJson(trainsRouteGetter.getDataModel(from, to, date, hour, timesel));
    }

    /**
     *
     * @param from
     * @param to
     * @param date
     * @param hour
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/train/{from}/{to}/{date}/{hour}")
    public String getTrainRouteDate(@PathParam("from") String from, @PathParam("to") String to, @PathParam("date") String date,
            @PathParam("hour") String hour) {
        Gson gson = new Gson();
        return gson.toJson(trainsRouteGetter.getDataModel(from, to, date, hour, null));
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/train/{from}/{to}")
    public String getTrainRouteNow(@PathParam("from") String from, @PathParam("to") String to) {
        Gson gson = new Gson();
        return gson.toJson(trainsRouteGetter.getDataModel(from, to, null, null, null));
    }

    /**
     *
     * @param route
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/coyote/{route}")
    public String getRouteInformation(@PathParam("route") String route) {
        try {
            //Try to get the data from the cach, if it fails get the data directly from the server

            ApiType api = props.getTypeFromString("coyote");
            List<IApiModel> data = cache.getDataFromApi(api);
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    String r = ((Delay) data.get(i)).getRoute();
                    //System.out.println("route in rest: " + r);
                    if (r.equals(route)) {
                        return data.get(i).getAsJSON();
                    }
                }
                return new ApiRequestException("parkings", "Geen data beschikbaar").getAsJSON();
            } else {
                Logger.getLogger(ApidataRest.class.getName()).log(Level.WARNING, "Cache did not contain api data, retrieved data directly");
                return new ApiRequestException("Coyote", "Geen data beschikbaar").getAsJSON();
            }

        } catch (ApiRequestException ex) {
            Logger.getLogger(ApidataRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

}
