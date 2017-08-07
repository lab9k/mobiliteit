/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import com.google.gson.Gson;
import java.util.Objects;

/**
 *
 * @author Gebruiker
 */
public class Busses implements IApiModel {

    private String backgroundColor; //for logo (with number): colors from De Lijn
    private String backgroundColorEdge; //logo
    private String foregroundColor; //logo
    private String foregroundColorEdge; //logo
    private String publicId; //number of line
    private String vehicle; //bus or tram
    private String description; //description from route
    private String plannedTime;
    private String sortTime; //time it will arrive at halte
    private String target; //direction target
    private String stopname;
    private String perron;
    
    public Busses() {
    }

    public String getStopname() {
        return stopname;
    }

    public void setStopname(String stopname) {
        this.stopname = stopname;
    }

    public String getPerron() {
        return perron;
    }

    public void setPerron(String perron) {
        this.perron = perron;
    }

    
    public String getPlannedTime() {
        return plannedTime;
    }

    public void setPlannedTime(String plannedTime) {
        this.plannedTime = plannedTime;
    }

    

    
    
    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundColorEdge() {
        return backgroundColorEdge;
    }

    public void setBackgroundColorEdge(String backgroundColorEdge) {
        this.backgroundColorEdge = backgroundColorEdge;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getForegroundColorEdge() {
        return foregroundColorEdge;
    }

    public void setForegroundColorEdge(String foregroundColorEdge) {
        this.foregroundColorEdge = foregroundColorEdge;
    }

  

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   

    public String getSortTime() {
        return sortTime;
    }

    public void setSortTime(String sortTime) {
        this.sortTime = sortTime;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        String jsonInString = gson.toJson(this);
        return jsonInString;
    }

    @Override
    public ApiType getApiType() {
        return ApiType.DELIJN;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.backgroundColor);
        hash = 19 * hash + Objects.hashCode(this.backgroundColorEdge);
        hash = 19 * hash + Objects.hashCode(this.foregroundColor);
        hash = 19 * hash + Objects.hashCode(this.foregroundColorEdge);
        hash = 19 * hash + Objects.hashCode(this.publicId);
        hash = 19 * hash + Objects.hashCode(this.vehicle);
        hash = 19 * hash + Objects.hashCode(this.description);
        hash = 19 * hash + Objects.hashCode(this.plannedTime);
        hash = 19 * hash + Objects.hashCode(this.sortTime);
        hash = 19 * hash + Objects.hashCode(this.target);
        hash = 19 * hash + Objects.hashCode(this.stopname);
        hash = 19 * hash + Objects.hashCode(this.perron);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Busses other = (Busses) obj;
        if (!Objects.equals(this.publicId, other.publicId)) {
            return false;
        }
        if (!Objects.equals(this.vehicle, other.vehicle)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.plannedTime, other.plannedTime)) {
            return false;
        }
        if (!Objects.equals(this.sortTime, other.sortTime)) {
            return false;
        }
        if (!Objects.equals(this.target, other.target)) {
            return false;
        }
        if (!Objects.equals(this.stopname, other.stopname)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Busses{" + "backgroundColor=" + backgroundColor + ", backgroundColorEdge=" + backgroundColorEdge + ", foregroundColor=" + foregroundColor + ", foregroundColorEdge=" + foregroundColorEdge + ", publicId=" + publicId + ", vehicle=" + vehicle + ", description=" + description + ", plannedTime=" + plannedTime + ", sortTime=" + sortTime + ", target=" + target + ", stopname=" + stopname + ", perron=" + perron + '}';
    }

    

   
    
    
    
}
