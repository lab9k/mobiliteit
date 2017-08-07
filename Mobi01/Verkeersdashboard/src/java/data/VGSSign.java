package data;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class VGSSign {
    String name;
    double lat, lng;
    String text;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * Construct a JsonObject that represents this VGSSign
     * @return the JsonObject that represents this VGSSign
     */
    public JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("name", name)
                .add("lat", lat)
                .add("lng", lng)
                .add("text", text);
        return builder.build();
    }
}
