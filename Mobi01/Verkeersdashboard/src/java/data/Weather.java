package data;

import java.util.Objects;

public class Weather {
    private Double temperature;
    private Double QPF;
    private Double windSpeed;
    private String relativeHumidity;
    private String weatherDescription;
    private String day;

    public void setDay(String s){
        day=s;  
    }

    public String getDay() {
        return day;
    }
    
    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double Temperature) {
        this.temperature = Temperature;
    }

    private int POP;

    public int getPOP() {
        return POP;
    }
    /**
     * 
     * This is the probability of precipitation
     * @param POP 
     */
    public void setPOP(int POP) {
        this.POP = POP;
    }
/**
 * 
 * This a quantitive measure of how much precipitation there is gonna fall
 * @return 
 */
    public Double getQPF() {
        return QPF;
    }

    public void setQPF(Double QPF) {
        this.QPF = QPF;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double WindSpeed) {
        this.windSpeed = WindSpeed;
    }


    public String getRelativeHumidity() {
        return relativeHumidity;
    }

    public void setRelativeHumidity(String RelativeHumidity) {
        this.relativeHumidity = RelativeHumidity;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final Weather other = (Weather) obj;
        if (!Objects.equals(this.weatherDescription, other.weatherDescription)) {
            return false;
        }
        if (!Objects.equals(this.temperature, other.temperature)) {
            return false;
        }
        if (!Objects.equals(this.QPF, other.QPF)) {
            return false;
        }
        if (!Objects.equals(this.windSpeed, other.windSpeed)) {
            return false;
        }
        if (!Objects.equals(this.relativeHumidity, other.relativeHumidity)) {
            return false;
        }
        return true;
    }
}
