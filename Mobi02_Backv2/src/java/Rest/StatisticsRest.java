/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import Api.Caching.Properties.CachingProperties;
import Database.Dao.R40Dao;
import Database.Exceptions.DatabaseException;
import Exceptions.ApiRequestException;
import com.google.gson.Gson;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ruben
 */
@Path("statistics")
public class StatisticsRest {

    private static final int HOUR_CORRECTION = -2;
    private HashMap<Integer,String> intToDay;

    @Context
    private UriInfo context;

    @EJB
    private R40Dao r40Dao;

    @EJB
    CachingProperties props;
    private Gson gson;

    /**
     * Creates a new instance of StatisticsRest
     */
    public StatisticsRest() {
        gson = new Gson();
        intToDay = new HashMap<>();
        intToDay.put(1, "MONDAY");
        intToDay.put(2, "TUESDAY");
        intToDay.put(3, "WEDNESDAY");
        intToDay.put(4, "THURSDAY");
        intToDay.put(5, "FRIDAY");
        intToDay.put(6, "SATURDAY");
        intToDay.put(0, "SUNDAY");
    }

    /**
     * Retrieves representation of an instance of Rest.StatisticsRest
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/r40/stations")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCountingStations() {
        //TODO return proper representation object
        return gson.toJson(r40Dao.getCountingStations());
    }

    /**
     *
     * @param station
     * @return
     */
    @GET
    @Path("/r40/avg/all/{station}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllTimeAverage(@PathParam("station") String station) {
        //TODO return proper representation object
        return gson.toJson(r40Dao.getAllTimeAverage(station));
    }

    /**
     *
     * @param station
     * @param day
     * @return
     */
    @GET
    @Path("/r40/avg/hist/{station}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHistoryPerDay(@PathParam("station") String station, @PathParam("day") String day) {
        try {
            int dayInt = Integer.parseInt(day);
            day = intToDay.get(dayInt);
            if (station.equals("total")) {
                return getHistoryPerDay(day);
            } else {
                //TODO return proper representation object

                //System.out.println(endDate);
                return gson.toJson(r40Dao.getAverageForEachWeekday(station, day));
            }
        } catch (Exception ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ex.getMessage()).getAsJSON();
        }
    }

    /**
     *
     * @param station
     * @return
     */
    @GET
    @Path("/r40/avg/hist/{station}/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHistoryPerDayToday(@PathParam("station") String station) {
        try {
            Date now = new Date();
            String day;
            Calendar cal = Calendar.getInstance();

            day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
            //System.out.println(endDate);

            if (station.equals("total")) {
                return getHistoryPerDay(day);
            } else {
                //TODO return proper representation object

                return gson.toJson(r40Dao.getAverageForEachWeekday(station, day));
            }
        } catch (Exception ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ex.getMessage()).getAsJSON();
        }
    }

    /**
     *
     * @param day
     * @return
     */
    @GET
    @Path("/traveltimes/hist/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTravelTimesHistory(@PathParam("day") String day) {
        int dayInt = Integer.parseInt(day);
            day = intToDay.get(dayInt);        
        
        try {
            return gson.toJson(r40Dao.getTravelTraveltimesHist(day));
        } catch (DatabaseException ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

    /**
     *
     * @return
     */
    @GET
    @Path("/traveltimes")
    @Produces(MediaType.APPLICATION_JSON)
    public String getTravelTimesToday() {

        try {

            return gson.toJson(r40Dao.getTraveltimesToday());
        } catch (DatabaseException ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getAsJSON();
        }
    }

    /**
     *
     * @param day
     * @return
     */
    public String getHistoryPerDay(String day) {
        try {

            return gson.toJson(r40Dao.getTotalAverageForEachWeekday(day));
        } catch (Exception ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ex.getMessage()).getAsJSON();
        }
    }

    /**
     *
     * @param station
     * @return
     */
    @GET
    @Path("/r40/avg/{station}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAverageToday(@PathParam("station") String station) {
        try {
            if (station.equals("total")) {
                return getSumAverageToday();
            }
            //System.out.println(endDate);
            return gson.toJson(r40Dao.getAverageToday(station));
        } catch (Exception ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ex.getMessage()).getAsJSON();
        }
    }

    /**
     *
     * @return
     */
    public String getSumAverageToday() {
        try {

            return gson.toJson(r40Dao.getTotalAverageToday());

        } catch (Exception ex) {
            Logger.getLogger(StatisticsRest.class.getName()).log(Level.SEVERE, null, ex);
            return new ApiRequestException(ex.getMessage()).getAsJSON();
        }
    }

}
