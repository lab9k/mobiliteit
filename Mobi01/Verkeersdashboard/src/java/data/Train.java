package data;

import java.time.LocalTime;

public class Train {
    private String departureStation;
    private String endStation;
    private int delay; //Train delay in seconds
    private Boolean cancelled;
    private Boolean left; //Has the train left the station
    private LocalTime departureTime;
    private String trainId;

    public Train() {
        this.notified = false;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public int getDelay() {
        return delay;
    }
    private Boolean notified;

    public Boolean isNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }
    


    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }



    @Override
    public String toString() {
        return dayOfWeek+"_"+trainId;
    }
      
    public void setDelay(int delay) {
        this.delay = delay;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public Boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public Boolean isLeft() {
        return left;
    }

    public void setLeft(Boolean left) {
        this.left = left;
    }

   
    private String dayOfWeek;

    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    } 
}
