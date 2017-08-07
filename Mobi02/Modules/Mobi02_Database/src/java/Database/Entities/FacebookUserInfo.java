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
@Table(name="facebook_info")
public class FacebookUserInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pageId;
    private String messengerId;
    private boolean hasSendPermission;
    
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
    public String getPageId() {
        return pageId;
    }

    /**
     *
     * @param pageId
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     *
     * @return
     */
    public String getMessengerId() {
        return messengerId;
    }

    /**
     *
     * @param messengerId
     */
    public void setMessengerId(String messengerId) {
        this.messengerId = messengerId;
    }

    /**
     *
     * @return
     */
    public boolean isHasSendPermission() {
        return hasSendPermission;
    }

    /**
     *
     * @param hasSendPermission
     */
    public void setHasSendPermission(boolean hasSendPermission) {
        this.hasSendPermission = hasSendPermission;
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
        if (!(object instanceof FacebookUserInfo)) {
            return false;
        }
        FacebookUserInfo other = (FacebookUserInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.FacebookUserInfo[ id=" + id + " ]";
    }
    
}
