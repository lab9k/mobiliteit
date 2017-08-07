/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Api.ApiType;
import Database.Dao.R40Dao;
import Database.Entities.R40Statistics;
import Database.Entities.TravelTimes;
import Model.IApiModel;
import com.sun.istack.logging.Logger;
import java.util.List;
import java.util.logging.Level;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 *
 * @author ruben
 * 
 * Handles timer triggers and adds models to the corresponding factory
 */
@Singleton
public class StatisticsManager {

    @EJB
    private TravelTimesStatsFactory travelTimesStats;

    @EJB(name = "R40")
    private R40StatsFactory R40;

    @EJB
    R40Dao r40Dao;

    /**
     *
     * @param data
     * @param type
     */
    public void addModelToContainer(List<IApiModel> data, ApiType type){
        
        switch(type){
            case R40TRAFFIC:
                R40.addCountings(data);
                break;
            case TRAVELTIMES:
                travelTimesStats.addTraveltimes(data);
                
        }
        
    }

    /**
     *
     */
    public void persistStatsToDb() {
        List<R40Statistics> stats = R40.createEntities();
        //System.out.println("Persisting stats to DB");
        for (R40Statistics st : stats) {
            r40Dao.persist(st);
        }
    }

    /**
     *
     */
    public void persistTravelTimesToDb() {
        try {
            //System.out.println("Persisting traveltimes to DB");
            TravelTimes entity = travelTimesStats.createEntities();
            if(entity != null)
                r40Dao.persist(entity);
        } catch (Exception ex) {
            //zeker zijn dat timer niet wordt onderbroken
            Logger.getLogger(this.getClass()).log(Level.WARNING,"Exception occured", ex);
        }
    }
}
