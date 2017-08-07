/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;

import Database.Entities.R40Statistics;
import Database.Exceptions.DatabaseException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;

/**
 *
 * @author ruben
 * 
 * DAO for call to the db relating to statistics
 * Calls mostly stored procedures since the calls are not database independent (but the stored procedures could be rewritten for another database, without any change in code)
 */
@Stateless
public class R40Dao {

    @PersistenceContext(unitName = "Mobi02PU")
    private EntityManager em;

    /**
     *
     * @param object
     */
    public void persist(Object object) {
        em.persist(object);
    }

    /**
     *
     * @return
     */
    public List<String> getCountingStations() {
        Query q = em.createQuery("select distinct o.countId from R40Statistics o", R40Statistics.class);
        List<String> list = q.getResultList();
        return list;
    }

    /**
     *
     * @param station
     * @return
     */
    public double getAllTimeAverage(String station) {
        try {
            Query q = em.createQuery("select avg(o.average) from R40Statistics o WHERE o.countId = :station", R40Statistics.class);
            q.setParameter("station", station);
            return (double) q.getSingleResult();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return 0;
        }
    }

    /**
     *
     * @return
     * @throws DatabaseException
     */
    public List getAverageTravelTimeToday() throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_tt_today");
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("dayStr", date);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());

        }
    }

    /**
     *
     * @param day
     * @return
     * @throws DatabaseException
     */
    public List getAverageTravelTimeForEachWeekday(String day) throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_tt_hist");
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("dayStr", day);
            List o = q.getResultList();
            return o;
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());
        }
    }

    /**
     *
     * @param station
     * @return
     * @throws DatabaseException
     */
    public List getAverageToday(String station) throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_avg_today");
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);
            q.registerStoredProcedureParameter("station", String.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("station", station);
            q.setParameter("dayStr", date);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());

        }
    }

    /**
     *
     * @return
     * @throws DatabaseException
     */
    public List getTotalAverageToday() throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_avg_total");
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);
            q.registerStoredProcedureParameter("dateStr", String.class, ParameterMode.IN);
            q.setParameter("dateStr", date);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());

        }
    }

    /**
     *
     * @return
     * @throws DatabaseException
     */
    public List getTraveltimesToday() throws DatabaseException {
        return getTraveltimesToday(new Date());
    }

    /**
     *
     * @param d
     * @return
     * @throws DatabaseException
     */
    public List getTraveltimesToday(Date d) throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_tt_today");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(d);
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("dayStr", date);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());

        }
    }

    //call for dt days later or earlier

    /**
     *
     * @param dt
     * @return
     * @throws DatabaseException
     */
    public List getTraveltimesToday(int dt) throws DatabaseException {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, dt);
            Date d = cal.getTime();
            return getTraveltimesToday(d);
            
    }

    /**
     *
     * @param day
     * @return
     * @throws DatabaseException
     */
    public List getTravelTraveltimesHist(String day) throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_tt_hist");
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("dayStr", day);
            return q.getResultList();
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());

        }
    }

    /**
     *
     * @param station
     * @param day
     * @return
     * @throws DatabaseException
     */
    public List getAverageForEachWeekday(String station, String day) throws DatabaseException {
        try {
            StoredProcedureQuery q = em.createStoredProcedureQuery("r40_avg_hist");
            q.registerStoredProcedureParameter("station", String.class, ParameterMode.IN);
            q.registerStoredProcedureParameter("dayStr", String.class, ParameterMode.IN);
            q.setParameter("station", station);
            q.setParameter("dayStr", day);
            List o = q.getResultList();
            return o;
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());
        }
    }

    /**
     *
     * @param day
     * @return
     * @throws DatabaseException
     */
    public List getTotalAverageForEachWeekday(String day) throws DatabaseException {
        try {
            Query q = em.createNamedStoredProcedureQuery("r40_avg_hist_total");
            q.setParameter("daystr", day);
            List o = q.getResultList();
            return o;
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            throw new DatabaseException("R40Statistics", e.getMessage());
        }
    }

    private String getDateAsString(Date d) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return df.format(d);
    }

    /**
     *
     * @param object
     */
    public void persist1(Object object) {
        em.persist(object);
    }

}
