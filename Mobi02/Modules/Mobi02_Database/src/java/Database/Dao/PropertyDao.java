/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;

import Database.Entities.Property;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author ruben
 * 
 * Emulates a noSQL db -> when possible change to noSQL
 */
@Stateless
public class PropertyDao {

    @PersistenceContext(unitName = "Mobi02PU")
    private EntityManager em;

    /**
     *
     * @param type
     * @return
     */
    public List<Property> getProperties(String type) {
        Query q = em.createQuery("select prop from Property prop where prop.type = :type");
        q.setParameter("type", type);
        return q.getResultList();
    }

    /**
     *
     * @param type
     * @param key
     * @param value
     */
    public void addProperty(String type, String key, String value) {
        List<Property> props = getProperties(type);
        boolean isSet = false;
        for (Property p : props) {
            if (p.getPropertyKey().equals(key)) {
                p.setPropertyValue(value);
                isSet = true;
            }
        }
        if (!isSet) {
            Property entity = new Property();
            entity.setPropertyKey(key);
            entity.setPropertyValue(value);
            entity.setType(type);
            em.persist(entity);
        }
    }

}
