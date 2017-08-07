/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ruben
 */
@Entity
@Table(name="user_property")
public class UserProperty implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String keyString;
    
    @Lob
    private String valueString;
    
    /**
     *
     * @return
     */
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    public String getKeyString() {
        return keyString;
    }

    /**
     *
     * @param keyString
     */
    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }

    /**
     *
     * @return
     */
    public String getValueString() {
        return valueString;
    }

    /**
     *
     * @param valueString
     */
    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.keyString);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserProperty)) {
            return false;
        }
        UserProperty other = (UserProperty) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.UserProperty[ id=" + id + " ]";
    }
    
    
    
}
