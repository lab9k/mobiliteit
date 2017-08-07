    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ruben
 */
@Entity
@Table(name="notifications")
public class NotificationPreferences implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    //List of platforms seperated by semicolumn
    private String platform;
    //Reference to hour on which the notification should be sent 1-> 01:00, 13 -> 13:00
    private int sendHour;
    private int sendMinutes;
    private String type;
    //Keywords for the typeString => a CSV-string
    private String keywords;

    /**
     *
     * @return
     */
    public int getSendMinutes() {
        return sendMinutes;
    }

    /**
     *
     * @param sendMinutes
     */
    public void setSendMinutes(int sendMinutes) {
        this.sendMinutes = sendMinutes;
    }
    
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
    public String getPlatform() {
        return platform;
    }

    /**
     *
     * @param platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     *
     * @return
     */
    public int getSendHour() {
        return sendHour;
    }

    /**
     *
     * @param sendHour
     */
    public void setSendHour(int sendHour) {
        this.sendHour = sendHour;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     *
     * @param keywords
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotificationPreferences)) {
            return false;
        }
        NotificationPreferences other = (NotificationPreferences) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.NotificationPreferences[ id=" + id + " ]";
    }
    
}
