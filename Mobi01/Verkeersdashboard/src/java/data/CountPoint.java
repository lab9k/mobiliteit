package data;

import java.util.Objects;

public class CountPoint {
    String key;
    int speed;
    int count;
    double coordinate1;
    double coordinate2;
    int occupation;

    public CountPoint() {
    }

    public int getOccupation() {
        return occupation;
    }

    public void setOccupation(int occupation) {
        this.occupation = occupation;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCoordinate1(double coordinate1) {
        this.coordinate1 = coordinate1;
    }

    public void setCoordinate2(double coordinate2) {
        this.coordinate2 = coordinate2;
    }

    public int getSpeed() {
        return speed;
    }

    public int getCount() {
        return count;
    }

    public double getCoordinate1() {
        return coordinate1;
    }

    public double getCoordinate2() {
        return coordinate2;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.key);
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
        final CountPoint other = (CountPoint) obj;
        if ((this.coordinate1) != (other.coordinate1)) {
            return false;
        }
        if ((this.coordinate2) != (other.coordinate2)) {
            return false;
        }
        return true;
    }
}
