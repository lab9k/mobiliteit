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
@Table(name="widgets")
public class Widgets implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private boolean bluebike,delijn,map,trains,parkings,coyote,waylay,weather;

    /**
     *
     * @return
     */
    public boolean isBluebike() {
        return bluebike;
    }

    /**
     *
     * @param bluebike
     */
    public void setBluebike(boolean bluebike) {
        this.bluebike = bluebike;
    }

    /**
     *
     * @return
     */
    public boolean isDelijn() {
        return delijn;
    }

    /**
     *
     * @param delijn
     */
    public void setDelijn(boolean delijn) {
        this.delijn = delijn;
    }

    /**
     *
     * @return
     */
    public boolean isWeather() {
        return weather;
    }

    /**
     *
     * @param weather
     */
    public void setWeather(boolean weather) {
        this.weather = weather;
    }
    
    /**
     *
     * @return
     */
    public boolean isMap() {
        return map;
    }

    /**
     *
     * @param map
     */
    public void setMap(boolean map) {
        this.map = map;
    }

    /**
     *
     * @return
     */
    public boolean isTrains() {
        return trains;
    }

    /**
     *
     * @param trains
     */
    public void setTrains(boolean trains) {
        this.trains = trains;
    }

    /**
     *
     * @return
     */
    public boolean isParkings() {
        return parkings;
    }

    /**
     *
     * @param parkings
     */
    public void setParkings(boolean parkings) {
        this.parkings = parkings;
    }

    /**
     *
     * @return
     */
    public boolean isCoyote() {
        return coyote;
    }

    /**
     *
     * @param coyote
     */
    public void setCoyote(boolean coyote) {
        this.coyote = coyote;
    }

    /**
     *
     * @return
     */
    public boolean isWaylay() {
        return waylay;
    }

    /**
     *
     * @param waylay
     */
    public void setWaylay(boolean waylay) {
        this.waylay = waylay;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Widgets)) {
            return false;
        }
        Widgets other = (Widgets) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.Widgets[ id=" + id + " ]";
    }
    
}
