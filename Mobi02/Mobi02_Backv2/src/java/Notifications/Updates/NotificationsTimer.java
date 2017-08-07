/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.Updates;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;

/**
 *
 * @author Gebruiker
 */
@Singleton
@Startup
public class NotificationsTimer {

    @EJB
    private NotificationsUpdate notifications;

    @Resource
    private ManagedScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void init() {

        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Date now = new Date();
                System.out.println(now.getHours() + ":" + now.getMinutes() + ", Executing notifications!");
                notifications.update();
            }
        }, 15 - GregorianCalendar.getInstance().get(Calendar.MINUTE) % 15, 15, TimeUnit.MINUTES);

    }

    public int getMinutesToNextQuarter() {
        Date date = new Date();
        Calendar c = GregorianCalendar.getInstance();
        int q = c.get(Calendar.MINUTE);
        return 15 - q % 15;
    }

}
