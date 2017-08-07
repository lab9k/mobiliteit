/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Caching;

import Api.ApiType;
import Api.Loader.ApiLoaderBean;
import Api.Statistics.R40StatsFactory;
import Database.Dao.LogDao;
import Database.Dao.R40Dao;
import Database.Entities.R40Statistics;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Api.Caching.Properties.CachingProperties;
import Api.Statistics.StatisticsManager;
import Api.Statistics.TravelTimesStatsFactory;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author ruben
 */
@Singleton(name="cachingmanager")
@Startup
@DependsOn({"cacheddata","cachingproperties","StatisticsManager"})
public class CachingManager {

    

    @EJB
    private CachingProperties prop;
    
    @EJB
    private CachedData cache;
    
    @EJB
    private LogDao logDao;
    
    @EJB(name = "apiloader")
    private ApiLoaderBean apiLoader;
    
    @EJB
    private StatisticsManager manager;
    
   
    
    

    private HashMap<ApiType, Date> latestRefresh;

    /**
     *
     */
    @PostConstruct
    public void init() {
        latestRefresh = new HashMap<>();
        for (ApiType type : prop.getApiTypes()) {
            latestRefresh.put(type, null);
        }
    }
    
    /**
     *This method checks for each ApiType if the update timeout has been exceeded. If there is no key corresponding to the ApiType, it will be ignored and will not be cached.
     * If the API is an instance of R40 or Traveltimes, the data will also be placed inside the Statistics Containers for further calculations and to be saved in the database.
     * 
     */
    public void update() {
        try {
            Date now = new Date();
            for (ApiType type : prop.getApiTypes()) {
                //get the timeout from properties using the api name (case insensitive)
                Long timeout = prop.getTimeout(type.getNameCI());
                Date refreshedAt = latestRefresh.get(type);

                if (timeout != null && timeout > -1) {//do not cache data which has no timeout set
                    double timeBetween = -1;
                    if (refreshedAt != null) {
                        timeBetween = now.getTime() - refreshedAt.getTime();
                    }
                    //Logger.getLogger(CachingDao.class.getName()).log(Level.INFO, "{0}Checking if updates are needed, time between updates is: {1} max is: {2}", new Object[]{type.toString(), timeBetween, refreshRates.get(type)});
                    if (timeBetween < 0 || timeBetween > timeout) {
                        System.out.println(now.getHours() + ":" + now.getMinutes()+ " : " + type.toString() + " must be reloaded, timeout of " + timeout + "ms expired.");
                        
                        List<IApiModel> lijst = apiLoader.getModelAsObject(type.getNameCI());
                        
                        cache.UpdateModel(type, lijst);
                        
                        latestRefresh.put(type, new Date());
                        
                        //Add R40 to Statistics if needed
                        if(type == ApiType.R40TRAFFIC){
                            manager.addModelToContainer(lijst, type);
                        }
                        
                        //Add Coyote Traveltimes to statistics if needed
                        if(type == ApiType.TRAVELTIMES){
                            System.out.println("Traveltimes loggen!");
                            manager.addModelToContainer(lijst, type);
                        }
                        
                        //Log Exception
                        if(lijst != null && lijst.size() > 0 && lijst.get(0) instanceof ApiRequestException){
                            ApiRequestException ex = (ApiRequestException) lijst.get(0);
                            if(ex.isLogToDb())
                                persistLogToDb(ex);
                        }
                        
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
    
    /**
     *
     * @param ex
     */
    public void persistLogToDb(ApiRequestException ex){
        logDao.createLog(ex.getApi(), ex.getMessage());
    }
    
    
   
}
