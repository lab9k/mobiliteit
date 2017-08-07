/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Api.Caching.Properties.CachingProperties;
import Model.TravelTimes;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ruben
 * 
 * Same as R40 Countings Container, keeps a list of models and creates an entity on timer trigger
 */
public class TravelTimesStatsContainer {

    private CachingProperties cachingproperties;

    private List<TravelTimes> times;
    private long firstTimestamp;
    private long lastTimestamp;

    /**
     *
     */
    public TravelTimesStatsContainer() {
        cachingproperties = new CachingProperties();
        times = new ArrayList<>();
        firstTimestamp = System.currentTimeMillis();
        firstTimestamp += cachingproperties.getTimeout("timezone_correction") * 60 * 60 * 1000;
    }

    /**
     *
     * @param c
     */
    public void addTravelTime(TravelTimes c) {
        times.add(c);
        lastTimestamp = System.currentTimeMillis();
        lastTimestamp += cachingproperties.getTimeout("timezone_correction") * 60 * 60 * 1000;
    }

    /**
     *
     */
    public void reset() {
        times = new ArrayList<>();
        firstTimestamp = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public double getAverageRealtimeCW() {
        if (!times.isEmpty()) {
            double total = 0;
            for (TravelTimes c : times) {
                total += c.getRealTime_clockwise();
            }
            return total / times.size();
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    public double getAverageRealtimeACW() {
        if (!times.isEmpty()) {
            double total = 0;
            for (TravelTimes c : times) {
                total += c.getRealTime_antiClockwise();
            }
            return total / times.size();
        } else {
            return 0;
        }
    }

    /**
     *
     * @return
     */
    public Database.Entities.TravelTimes createEntity() {
        if (!times.isEmpty()) {
            System.out.println("Creating entity");
            Database.Entities.TravelTimes entity = new Database.Entities.TravelTimes();
            entity.setFirstTimestamp(new Timestamp(firstTimestamp));
            entity.setLastTimestamp(new Timestamp(lastTimestamp));
            entity.setRealTime_antiClockwise(getAverageRealtimeACW());
            entity.setRealTime_clockwise(getAverageRealtimeCW());
            return entity;
        }
        return null;
    }

}
