/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Caching;

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
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

/**
 *
 * @author ruben
 */
@Singleton
@Startup
@DependsOn("cachingmanager")
public class CachingTimer {

    private int count = 0;

    @EJB
    private CachingManager manager;

    
    @Resource
    TimerService timerService;

    /**
     *
     */
    @PostConstruct
    public void init() {

        long duration = 120000;
        Timer timer
                = timerService.createIntervalTimer(0, duration, new TimerConfig("Caching Timer", false));

    }

    /**
     *
     */
    @Timeout
    public void updateManager() {
        Date now = new Date();
        System.out.println(now.getHours() + ":" + now.getMinutes() + ", Executing update!");
        manager.update();

    }
}
