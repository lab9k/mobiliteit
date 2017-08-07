/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Api.Caching.Properties.CachingProperties;
import Database.Entities.R40Statistics;
import Model.Counting;
import static java.lang.Math.ceil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List; 
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author ruben
 * 
 * This class will contain a list of R40 countings, when the Statistics timer triggers it will create an instance of R40Statistics entity and will logged to the database
 * 
 */
public class R40StatsContainer {

    private CachingProperties cachingproperties;
    
    
    private List<Counting> countings;
    private long firstTimestamp;
    private long lastTimestamp;
    
    /**
     * Initializes the Container and sets the firstTimestamp
       */
   
    public R40StatsContainer() {
        countings = new ArrayList<>();
        firstTimestamp = System.currentTimeMillis();
        firstTimestamp += cachingproperties.getTimeout("timezone_correction") * 60 * 60 * 1000;
    }
    
    /**
     *
     * @param c
     * 
     * Add counting to the container and updates lastTimestamp
     */
    public void addCounting(Counting c){
        cachingproperties = new CachingProperties();
        countings.add(c);
        lastTimestamp = System.currentTimeMillis();
        lastTimestamp += cachingproperties.getTimeout("timezone_correction") * 60 * 60 * 1000;
    }
    
    /**
     *
     * @return
     */
    public String getCountId(){
        if(countings.isEmpty()){
            return null;
        }
        else{
            return countings.get(0).getContextEntity();
        }
    }
    
    /**
     *
     */
    public void reset(){
        countings = new ArrayList<>();
        firstTimestamp = System.currentTimeMillis();
    }
    
    /**
     *
     * @return
     */
    public double getAverage() {
        if (!countings.isEmpty()) {
            double total = 0;
            for (Counting c : countings) {
                total += c.getCount();
            }
            return total/countings.size();
        }
        else{
            return 0;
        }
    }
    
    /**
     *
     * @return
     */
    public int getMedian(){
        ArrayList<Counting> sorted = new ArrayList<>(countings);
        Collections.sort(sorted);
        if(!sorted.isEmpty()){
            return sorted.get((int)ceil(sorted.size()/2)).getCount();
        }
        else{
            return 0;
        }
    }
    
    /**
     *
     * @return
     */
    public int getMinCount(){
        if(!countings.isEmpty()){
            int min = countings.get(0).getCount();
            for(Counting c : countings){
                if(c.getCount() < min){
                    min = c.getCount();
                }
            }
            return min;
        }
        return 0;
    }
    
    /**
     *
     * @return
     */
    public int getMaxCount(){
        if(!countings.isEmpty()){
            int max = countings.get(0).getCount();
            for(Counting c : countings){
                if(c.getCount() < max){
                    max = c.getCount();
                }
            }
            return max;
        }
        return 0;
    }    
    
    /**
     *
     * @return
     */
    public R40Statistics createEntity(){
        if(!countings.isEmpty()){
        R40Statistics entity = new R40Statistics();
        entity.setAverage(getAverage());
        entity.setMaxCount(getMaxCount());
        entity.setMinCount(getMinCount());
        entity.setMedian(getMedian());
        entity.setCountId(getCountId());
        entity.setFirstTimestamp(new Timestamp(firstTimestamp));
        entity.setLastTimestamp(new Timestamp(lastTimestamp));
        entity.setLat(countings.get(0).getLatitude());
        entity.setLng(countings.get(0).getLongitude());
        return entity;
        }
        return null;
    }    
    
}
