package widgets;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import websocket.WidgetSessionHandler;

public class GIPODWidget extends Widget {

    //public int updateInterval = 30;
    private final String URLGIPODGENT;
    private static int PAGELIMIT = 50;

    /**
     * Construct a GIPODWidget.
     * This widget wil load road construction sites situated in Ghent
     */
    public GIPODWidget() {
        URLGIPODGENT = getSettings(getWidgetType())[0];
        updateInterval = 30;
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.GIPOD;
    }

    @Override
    public void parse() {

        JsonObjectBuilder jsonBuilder = initJsonBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        try {
            int currentPage = 0;
            int currentPageSize = PAGELIMIT;
            while (currentPageSize >= PAGELIMIT) {
                currentPageSize = 0;
                URL url = new URL(URLGIPODGENT + "&limit=" + PAGELIMIT + "&offset=" + currentPage);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try (JsonReader jsonReader = Json.createReader(conn.getInputStream())) {
                    JsonArray array = jsonReader.readArray();
                    currentPageSize = array.size();
                    for (int i = 0; i < currentPageSize; i++) {
                        arrayBuilder.add(array.getJsonObject(i));
                    }
                }
                currentPage+=PAGELIMIT;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(GIPODWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
            return;
        } catch (IOException ex) {
            Logger.getLogger(GIPODWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
            return;
        }
        jsonBuilder.add("GIPOD", arrayBuilder);
        jsonBuilder.add("lastUpdate",dateFormat.format(lastUpdate));
        json = jsonBuilder.build();
    }

    /**
     * Create a basic JsonBuilder object with this widgetType and action=data
     * @return the created JsonBuilder
     */
    private JsonObjectBuilder initJsonBuilder() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data")
                .add("widgetType", getWidgetType().name());
        return jsonBuilder;
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString());
        builder.add("title", "GIPOD URL");
        builder.add("schema", true);
        builder.add("schemaType", "json");
        builder.add("description", "Onderstaande JSON-databron wordt geparset in de GIPODWidget.");
        builder.add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }
    
}
