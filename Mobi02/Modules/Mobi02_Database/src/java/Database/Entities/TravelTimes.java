/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Entities;

import java.io.Serializable;
import java.sql.Timestamp;
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
@Table(name="traveltimes")
public class TravelTimes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private double realTime_clockwise;
    private double realTime_antiClockwise;
    private java.sql.Timestamp firstTimestamp;
    private java.sql.Timestamp lastTimestamp;

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

    /**
     *
     * @return
     */
    public double getRealTime_clockwise() {
        return realTime_clockwise;
    }

    /**
     *
     * @param realTime_clockwise
     */
    public void setRealTime_clockwise(double realTime_clockwise) {
        this.realTime_clockwise = realTime_clockwise;
    }

    /**
     *
     * @return
     */
    public double getRealTime_antiClockwise() {
        return realTime_antiClockwise;
    }

    /**
     *
     * @param realTime_antiClockwise
     */
    public void setRealTime_antiClockwise(double realTime_antiClockwise) {
        this.realTime_antiClockwise = realTime_antiClockwise;
    }

    /**
     *
     * @return
     */
    public Timestamp getFirstTimestamp() {
        return firstTimestamp;
    }

    /**
     *
     * @param firstTimestamp
     */
    public void setFirstTimestamp(Timestamp firstTimestamp) {
        this.firstTimestamp = firstTimestamp;
    }

    /**
     *
     * @return
     */
    public Timestamp getLastTimestamp() {
        return lastTimestamp;
    }

    /**
     *
     * @param lastTimestamp
     */
    public void setLastTimestamp(Timestamp lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
    
    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TravelTimes)) {
            return false;
        }
        TravelTimes other = (TravelTimes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.TravelTimes[ id=" + id + " ]";
    }
    
}
