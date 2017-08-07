package data;

import java.util.Objects;

public class BikeCenter {
    private String location;
    private int bikesAvailable;
    private int bikesTotal;
    
    public BikeCenter(){
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getBikesAvailable() {
        return bikesAvailable;
    }

    public void setBikesAvailable(int bikesAvailable) {
        this.bikesAvailable = bikesAvailable;
    }

    public int getBikesTotal() {
        return bikesTotal;
    }

    public void setBikesTotal(int bikesTotal) {
        this.bikesTotal = bikesTotal;
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
        final BikeCenter other = (BikeCenter) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.location);
        return hash;
    }
}
