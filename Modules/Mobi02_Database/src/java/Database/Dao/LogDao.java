/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;

import Database.Entities.ApiLog;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ruben
 */
@Stateless
public class LogDao {

    
    @PersistenceContext(unitName = "Mobi02PU")
    private EntityManager em;

    /**
     *
     * @return
     */
    public List<ApiLog> getAllLogs(){
        Query q = em.createQuery("select l from ApiLog l");
        return q.getResultList();
    }
    
    /**
     *
     * @param api
     * @param message
     */
    public void createLog(String api, String message) {
        ApiLog log = new ApiLog();
        log.setMessage(message);
        log.setType(api);
        em.persist(log);
    }
    
}
