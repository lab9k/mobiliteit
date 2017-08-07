package widgets;

import data.PollutionStation;
import java.io.IOException;
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
import websocket.WidgetSessionHandler;

public final class PollutionWidget extends Widget {

    Map<Integer, PollutionStation> stations;
    String urlpoll;
    double aqiSum;
    
    public PollutionWidget() {
        urlpoll = "https://api.waqi.info/map/bounds/?latlng=51.005376,3.651683,51.096558,3.822670&token=" + getSettings(getWidgetType())[0];
        stations = new HashMap<>();
    }
    
    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.POLLUTION;
    }
    /**
     * Method to parse information about the Polltion datasource. 
     */
    @Override
    public void parse() {
        try {
            URL urlpos = new URL(urlpoll);
            URLConnection connpoll = urlpos.openConnection();
            JsonObjectBuilder posbuilder = Json.createObjectBuilder();
            aqiSum = 0;
            
            try (JsonReader jsonReader = Json.createReader(connpoll.getInputStream())) {
                JsonObject jsonObj = jsonReader.readObject();
                JsonArray data = jsonObj.getJsonArray("data");
                for (int i = 0; i < data.size(); i++) {
                    PollutionStation ps = new PollutionStation();
                    JsonObject station = data.getJsonObject(i);
                    double lat = station.getJsonNumber("lat").doubleValue();
                    ps.setLat(lat);
                    double lon = station.getJsonNumber("lon").doubleValue();
                    ps.setLon(lon);
                    double iddouble = station.getJsonNumber("uid").doubleValue();
                    int id = (int) iddouble;
                    ps.setId(id);

                    double aqi;
                    if(station.getString("aqi").equals("-")){
                        aqi = 0;
                    } else{
                    String aqidouble = station.getString("aqi");
                        aqi = Integer.parseInt(aqidouble);
                    }
                    ps.setAqi((int)aqi);
                    aqiSum+=aqi;
                    
                    stations.put(id, ps);
                }
                for (int id : stations.keySet()) {
                    String urlID = "https://api.waqi.info/feed/@" + id + "/?token=" + properties.getProperty("pollutionKey");
                    URL URLID = new URL(urlID);
                    URLConnection connID = URLID.openConnection();
                    JsonObjectBuilder statbuilder = Json.createObjectBuilder();
                    try (JsonReader jsonReader2 = Json.createReader(connID.getInputStream())) {
                        JsonObject station = jsonReader2.readObject();
                        JsonObject data2 = station.getJsonObject("data");
                        JsonObject city = data2.getJsonObject("city");
                        String name = city.getString("name");
                        PollutionStation ps1 = stations.get(id);
                        ps1.setName(name);

                        JsonObject iaqi = data2.getJsonObject("iaqi");
                        JsonObject hO = iaqi.getJsonObject("h");
                        int h = hO.getInt("v");
                        ps1.setH(h);

                        JsonObject no2O = iaqi.getJsonObject("no2");
                        double no2 = no2O.getJsonNumber("v").doubleValue();
                        ps1.setNo2(no2);
                        if (!name.equals("Gent (gustaaf Callierlaan)")) {
                            JsonObject o3O = iaqi.getJsonObject("o3");
                            double o3 = o3O.getJsonNumber("v").doubleValue();
                            ps1.setO3(o3);
                        }

                        JsonObject pO = iaqi.getJsonObject("p");
                        int p = pO.getInt("v");
                        ps1.setP(p);
                        
                        JsonObject pm10O = iaqi.getJsonObject("pm10");
                        int pm10 = no2O.getInt("v");
                        ps1.setPm10(pm10);

                        JsonObject pm25O = iaqi.getJsonObject("pm25");
                        int pm25 = pm25O.getInt("v");
                        ps1.setPm25(pm25);

                        JsonObject tO = iaqi.getJsonObject("t");
                        double t = tO.getJsonNumber("v").doubleValue();
                        ps1.setT(t);
                        stations.put(id, ps1);
                    }
                }
            }

        } catch (MalformedURLException | NullPointerException | ClassCastException ex) {
            Logger.getLogger(PollutionWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
            return;
        } catch (IOException ex) {
            Logger.getLogger(PollutionWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
            return;
        }
        initJson();
    }
    /**
     * Builds the JSON with all the information.
     */
    private void initJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data");
        jsonBuilder.add("widgetType", "" + getWidgetType());
        jsonBuilder.add("avgAqi", (int)(aqiSum/stations.size()));
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (PollutionStation p : stations.values()) {
            addJsonPollution(p, arrayBuilder);
        }
        jsonBuilder.add("stations", arrayBuilder.build());
        jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
        json = jsonBuilder.build();
    }
    /**
     * Adds information about Pollutionstation p to the JsonArrayBuilder arrayBuilder
     * @param p
     * @param arrayBuilder 
     */
    private void addJsonPollution(PollutionStation p, JsonArrayBuilder arrayBuilder) {
        arrayBuilder.add(Json.createObjectBuilder()
                .add("id", p.getId())
                .add("lat", p.getLat())
                .add("lon", p.getLon())
                .add("name", p.getName())
                .add("H", p.getH())
                .add("No2", p.getNo2())
                .add("O3", p.getO3())
                .add("P", p.getP())
                .add("Pm10", p.getPm10())
                .add("Pm25", p.getPm25())
                .add("aqi", p.getAqi()));
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "Pollution key")
                .add("schema", false)
                .add("description", "Dit is de key die gebruikt wordt voor het op halen van de gegevens van de WAQI")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

}
