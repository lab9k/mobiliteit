/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Model.IApiModel;
import Model.TravelTimes;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author ruben
 * 
 * Provides easy access and extra checks before data gets added to the container
 */
@Singleton(name = "TravelTimesStats")
public class TravelTimesStatsFactory {

    private TravelTimesStatsContainer container;
    
    /**
     *
     */
    @PostConstruct
    public void init(){
        container = new TravelTimesStatsContainer();
        
    }
    
    /**
     *
     * @param list
     */
    public void addTraveltimes(List<IApiModel> list){
        if(list != null && list.get(0) instanceof TravelTimes){
            TravelTimes t = (TravelTimes) list.get(0);
            container.addTravelTime(t);
            System.out.println("Added to model");
        }
        
    }
    
    /**
     *
     * @return
     */
    public Database.Entities.TravelTimes createEntities(){
        Database.Entities.TravelTimes entity = container.createEntity();      
        container.reset();
        return entity;
    }
    
}
