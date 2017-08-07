/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author ruben
 */
@Entity
@Table(name="site_users")

public class PersonalUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(unique=true)
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    
    @OneToOne(cascade = CascadeType.ALL)
    private FacebookUserInfo facebookInfo;
    
    
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<NotificationPreferences> notification;
    
    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    private Set<UserProperty> properties;
    
    
    
    @OneToOne(cascade = CascadeType.ALL)
    private Widgets widgets;
    
    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @return
     */
    public Set<UserProperty> getProperties() {
        return properties;
    }

    /**
     *
     * @param properties
     */
    public void setProperties(Set<UserProperty> properties) {
        this.properties = properties;
    }

    /**
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
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
    public Widgets getWidgets() {
        return widgets;
    }

    /**
     *
     * @param widgets
     */
    public void setWidgets(Widgets widgets) {
        this.widgets = widgets;
    }

    /**
     *
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     */
    public FacebookUserInfo getFacebookInfo() {
        return facebookInfo;
    }

    /**
     *
     * @param facebookInfo
     */
    public void setFacebookInfo(FacebookUserInfo facebookInfo) {
        this.facebookInfo = facebookInfo;
    }

    /**
     *
     * @return
     */
    public Set<NotificationPreferences> getNotification() {
        return notification;
    }

    /**
     *
     * @param notification
     */
    public void setNotification(Set<NotificationPreferences> notification) {
        this.notification = notification;
    }
    
    /**
     *
     * @param prop
     */
    public void addProperty(UserProperty prop){
        if(properties == null) properties = new HashSet<>();
        properties.add(prop);
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
        if (!(object instanceof PersonalUser)) {
            return false;
        }
        PersonalUser other = (PersonalUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.PersonalUser[ id=" + id + " ]";
    }
    
}
