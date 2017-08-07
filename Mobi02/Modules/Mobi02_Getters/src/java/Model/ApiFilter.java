/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ruben
 */
public class ApiFilter {

    @SerializedName("weather")
    @Expose
    private Boolean weather;
    @SerializedName("parking")
    @Expose
    private Boolean parking;
    @SerializedName("parkingNMBS")
    @Expose
    private Boolean parkingNMBS;
    @SerializedName("bluebike")
    @Expose
    private Boolean bluebike;
    @SerializedName("r40")
    @Expose
    private Boolean r40;
    @SerializedName("trains")
    @Expose
    private Boolean trains;
    @SerializedName("coyote")
    @Expose
    private Boolean coyote;
    @SerializedName("waylay")
    @Expose
    private Boolean waylay;
    @SerializedName("parkings")
    @Expose
    private Boolean parkings;
    @SerializedName("waze")
    @Expose
    private Boolean waze;
    @SerializedName("delijn")
    @Expose
    private Boolean delijn;
    @SerializedName("gipod")
    @Expose
    private Boolean gipod;
    
    public void setAll(boolean defaultValue) {
        weather = defaultValue;
        parkings = defaultValue;
        bluebike = defaultValue;
        r40 = defaultValue;
        trains = defaultValue;
        coyote = defaultValue;
        waylay = defaultValue;
        waze = defaultValue;
        delijn = defaultValue;
        gipod = defaultValue;
    }

    public Boolean getGipod() {
        return gipod;
    }

    public void setGipod(Boolean gipod) {
        this.gipod = gipod;
    }

    
    
    public Boolean getWaze() {
        if(waze == null )
            waze = false;
        return waze;
    }

    public void setWaze(Boolean waze) {
        this.waze = waze;
    }

    
    
    public Boolean getWaylay() {
        if(waylay == null )
            waylay = false;
        return waylay;
    }

    public void setWaylay(Boolean waylay) {
        this.waylay = waylay;
    }
    
    

    public Boolean getCoyote() {
        if(coyote == null )
            coyote = false;
        return coyote;
    }

    public void setCoyote(Boolean coyote) {
        this.coyote = coyote;
    }
    
    public Boolean getWeather() {
        if(weather == null )
            weather = false;
        return weather;
    }

    public void setWeather(Boolean weather) {
        this.weather = weather;
    }

    public Boolean getParking() {
        if(parking == null )
            parking = false;
        return parking;
    }

    public void setParking(Boolean parking) {
        this.parking = parking;
    }

    public Boolean getParkingNMBS() {
        if(parkingNMBS == null )
            parkingNMBS = false;
        return parkingNMBS;
    }

    public void setParkingNMBS(Boolean parkingNMBS) {
        this.parkingNMBS = parkingNMBS;
    }

    public Boolean getBluebike() {
        if(bluebike == null )
            bluebike = false;
        return bluebike;
    }

    public void setBluebike(Boolean bluebike) {
        this.bluebike = bluebike;
    }

    public Boolean getR40() {
        if(r40 == null )
            r40 = false;
        return r40;
    }

    public void setR40(Boolean r40) {
        this.r40 = r40;
    }

    public Boolean getTrains() {
        if(trains == null )
            trains = false;
        return trains;
    }

    public void setTrains(Boolean trains) {
        this.trains = trains;
    }

    public Boolean getParkings() {
            if(parkings == null )
            parkings = false;
        return parkings;
    }

    public void setParkings(Boolean parkings) {
        this.parkings = parkings;
    }
    
    public Boolean getDelijn(){
        if(delijn == null){
            delijn = false;
        }
        return delijn;
    }
    
    public void setDelijn(Boolean delijn){
        this.delijn = delijn;
    }
    
    
    

    public List<ApiType> getList() {
        ArrayList<ApiType> list = new ArrayList<>();

        if (getWeather()) {
            list.add(ApiType.WEATHER);
        }
        if (getParking()) {
            list.add(ApiType.PARKINGHENT);
        }
        if (getParkingNMBS()) {
            list.add(ApiType.PARKINGNMBS);
        }
        if(getBluebike()){
            list.add(ApiType.BLUEBIKE);
        }
        if(getR40()){
            list.add(ApiType.R40TRAFFIC);
        }
        if(getTrains()){
            list.add(ApiType.TRAINSGHENT);
        }
        if(getCoyote()){
            list.add(ApiType.COYOTE);
        }
        if(getWaylay()){
            list.add(ApiType.WAYLAY);
        }
        if(getParkings()){
            list.add(ApiType.PARKINGS);
        }
        if(getWaze()){
            list.add(ApiType.WAZE);
        }
        if(getDelijn()){
            list.add(ApiType.DELIJN);
        }
        if(getGipod()){
            list.add(ApiType.GIPOD);
        }
        return list;
    }
}
