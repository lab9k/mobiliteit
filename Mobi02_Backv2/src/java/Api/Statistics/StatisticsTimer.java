/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Api.Caching.*;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

/**
 *
 * @author ruben
 * 
 * Timer Singleton, will execute methods from the StatisticsManager on triggers. 
 */
@Singleton
@Startup
@DependsOn("StatisticsManager")
public class StatisticsTimer {
    private int count = 0;

    @EJB
    private StatisticsManager manager;

    @Resource
    private ManagedScheduledExecutorService scheduledExecutorService;

    /**
     *
     */
    @PostConstruct
    public void init() {

       /* scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                persistStatsToDb();
            }
        },5*60000 , 5*60000, TimeUnit.MILLISECONDS);*/
        
        

    }

    private long millisToNextHour() {
        Calendar calendar = Calendar.getInstance();
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);
        int minutesToNextHour = 60 - minutes;
        int secondsToNextHour = 60 - seconds;
        int millisToNextHour = 1000 - millis;
        return minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }

    /**
     *
     */
    @Schedule(minute = "0", hour = "*")
    public void persistStatsToDb() {
        Date now = new Date();
        //System.out.println(now.getHours() + ":" + now.getMinutes() + ", Persisting to db!");
        manager.persistStatsToDb();
        manager.persistTravelTimesToDb();
    }
}
