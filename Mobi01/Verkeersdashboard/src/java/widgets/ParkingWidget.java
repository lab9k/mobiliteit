package widgets;

import data.Parking;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import websocket.WidgetSessionHandler;

public class ParkingWidget extends Widget {

    private Map<Integer, Parking> parkings;
    private final String URLSTRING = "https://datatank.stad.gent/4/mobiliteit/bezettingparkingsrealtime.json"; //contains information about the car parks of the city of Ghent
    private final String NMBSURLSTRING = "https://datatank.stad.gent/4/mobiliteit/bezettingparkeergaragesnmbs.json"; //contains information about the car park at Gent Sint-Pieters station
    private boolean init = true;
    private int totalPlacesOccupied = 0;
    private int totalCapacity = 0;

    public ParkingWidget() {
        parkings = new HashMap<>();
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.PARKING;
    }

    @Override
    public void parse() {
        totalPlacesOccupied = 0;
        if (init) {
            init();
            init = false;
        }
        try {
            URL url = new URL(URLSTRING);
            URLConnection conn = url.openConnection();
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonArray mainArray = jsonReader.readArray();
            for (int i = 0; i < mainArray.size(); i++) {
                JsonObject parking = (JsonObject) mainArray.get(i);
                JsonObject parkingStatus = parking.getJsonObject("parkingStatus");
                parkings.get(parking.getInt("id")).setPlacesAvailable(parkingStatus.getInt("availableCapacity"));
                totalPlacesOccupied += parkingStatus.getInt("availableCapacity");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        parseNmbs();
        buildJson();
    }

    /**
     * Populates the parking Map by adding al the Parking object generated from
     * the datasource.
     */
    private void init() {
        try {
      
            URL url = new URL(URLSTRING);
            URLConnection conn = url.openConnection();
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonArray mainArray = jsonReader.readArray();
            for (int i = 0; i < mainArray.size(); i++) {
                JsonObject parking = (JsonObject) mainArray.get(i);
                JsonObject parkingStatus = parking.getJsonObject("parkingStatus");
                Parking newParking = new Parking(parking.getInt("id"), parkingStatus.getInt("availableCapacity"),
                        parking.getInt("totalCapacity"), parking.getString("name").substring(4), parking.getString("name").substring(0, 3),
                        parking.getJsonNumber("longitude").doubleValue(), parking.getJsonNumber("latitude").doubleValue(), parkingStatus.getBoolean("open"));
                parkings.put(parking.getInt("id"), newParking);
                totalCapacity += parking.getInt("totalCapacity");
            }
        } catch (IOException ex) {
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        buildJson();
    }

    /**
     * Builds the JSON with all the information.
     */
    private void buildJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data")
            .add("widgetType", "" + getWidgetType())
            .add("totalOccupied", (int)(100*totalPlacesOccupied/totalCapacity));
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Parking p : parkings.values()) {
            addJsonParking(p, arrayBuilder);
        }
        jsonBuilder.add("parkings", arrayBuilder.build())
            .add("lastUpdate", dateFormat.format(lastUpdate));
        json = jsonBuilder.build();
    }

    /**
     * Adds information about Parking p to the JsonArrayBuilder arrayBuilder
     * @param p
     * @param arrayBuilder 
     */
    private void addJsonParking(Parking p, JsonArrayBuilder arrayBuilder) {
        arrayBuilder.add(Json.createObjectBuilder()
                .add("id", p.getId())
                .add("code", p.getCode())
                .add("name", p.getName())
                .add("freePlaces", p.getPlacesAvailable())
                .add("capacity", p.getCapacity())
                .add("lng", p.getLng())
                .add("lat", p.getLat())
                .add("open", p.isOpen()));
    }

    /**
     * Method to parse information about the NMBS datasource. Different than
     * normal parsing because json-file of NMBS has no coördinates or code
     */
    private void parseNmbs() {
        try {
            URL url = new URL(NMBSURLSTRING);
            URLConnection conn = url.openConnection();
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            JsonArray mainArray = jsonReader.readArray();
            JsonObject parking = (JsonObject) mainArray.get(0);
            JsonObject parkingStatus = parking.getJsonObject("parkingStatus");
            Parking newParking = new Parking(parking.getInt("id"), parkingStatus.getInt("availableCapacity"),
                    parking.getInt("totalCapacity"), "NMBS Sint-Pietersstation", "NMBS",
                    3.7053538, 51.0375182, true); //hardcoded
            parkings.put(parking.getInt("id"), newParking);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JsonParsingException ex){
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex){
            Logger.getLogger(ParkingWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder settingsBuilder = Json.createObjectBuilder();
        settingsBuilder.add("widgetType", getWidgetType().toString())
                .add("title", "Parking URL's")
                .add("schema", true)
                .add("schemaType", "json")
                .add("description", "Onderstaande JSON-databronnen (gescheiden door komma) worden geparset in de ParkingWidget. "
                        + "Opmerking: voorlopig ligt databron van de NMBS parking plat en kan het dynamisch toevoegen niet getest worden. Daarom kan je nog niet dynamisch URL's instellen via dit venster.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return settingsBuilder.build();
    }
}
