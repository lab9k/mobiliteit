/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;

import Database.Entities.NmbsStops;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ruben
 * 
 * Gives acces to table in the database related to all stops in that will be shown on the personal page
 */
@Stateless
public class StopsDao {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method") 
    
    @PersistenceContext(unitName = "Mobi02PU")
    private EntityManager em;
    
    /**
     *
     * @return
     */
    public List<NmbsStops> getAllStopsNames(){
        Query q = em.createQuery("select p.name from NmbsStops p");
        return q.getResultList();
    }
    
}
