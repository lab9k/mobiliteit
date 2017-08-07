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
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

/**
 *
 * @author ruben
 */
@NamedStoredProcedureQuery(
	name = "r40_avg_hist_total", 
	procedureName = "r40_avg_hist_total", 
	parameters = { 
		@StoredProcedureParameter(mode = ParameterMode.IN, type = String.class, name = "daystr")
	}
)

@Entity
@Table(name = "r40")
public class R40Statistics implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type, countId;
    private java.sql.Timestamp firstTimestamp;
    private java.sql.Timestamp lastTimestamp;
    private double median;
    private double average;
    private double minCount;
    private double maxCount;
    private double lng, lat;

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
    public String getCountId() {
        return countId;
    }

    /**
     *
     * @param countId
     */
    public void setCountId(String countId) {
        this.countId = countId;
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

    /**
     *
     * @return
     */
    public double getMedian() {
        return median;
    }

    /**
     *
     * @param median
     */
    public void setMedian(double median) {
        this.median = median;
    }

    /**
     *
     * @return
     */
    public double getAverage() {
        return average;
    }

    /**
     *
     * @param average
     */
    public void setAverage(double average) {
        this.average = average;
    }

    /**
     *
     * @return
     */
    public double getMinCount() {
        return minCount;
    }

    /**
     *
     * @param minCount
     */
    public void setMinCount(double minCount) {
        this.minCount = minCount;
    }

    /**
     *
     * @return
     */
    public double getMaxCount() {
        return maxCount;
    }

    /**
     *
     * @param maxCount
     */
    public void setMaxCount(double maxCount) {
        this.maxCount = maxCount;
    }

    /**
     *
     * @return
     */
    public double getLng() {
        return lng;
    }

    /**
     *
     * @param lng
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     *
     * @return
     */
    public double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     */
    public void setLat(double lat) {
        this.lat = lat;
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
        if (!(object instanceof R40Statistics)) {
            return false;
        }
        R40Statistics other = (R40Statistics) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Database.Entities.R40Statistics[ id=" + id + " ]";
    }
    
}
