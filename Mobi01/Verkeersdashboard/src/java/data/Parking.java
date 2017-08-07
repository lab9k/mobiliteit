package data;

public class Parking {
    private int id;
    private int placesAvailable;
    private int capacity;
    private String name;
    private String code;
    private double lng;
    private double lat;
    private boolean open;

    public Parking(int id, int placesAvailable, int capacity, String name, String code, double lng, double lat, boolean open) {
        this.id = id;
        this.placesAvailable = open ? placesAvailable : 0;
        this.capacity = capacity;
        this.name = name;
        this.code = code;
        this.lng = lng;
        this.lat = lat;
        this.open = open;
    }

    public Parking(int id) {
        this.id = id;
        this.placesAvailable = 0;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlacesAvailable() {
        return placesAvailable;
    }

    public void setPlacesAvailable(int placesAvailable) {
        if(open)
            this.placesAvailable = placesAvailable;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parking)) {
            return false;
        }
        return this.id == ((Parking) o).getId();
    }
}
