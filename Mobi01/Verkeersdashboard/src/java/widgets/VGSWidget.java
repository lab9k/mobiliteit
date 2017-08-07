package widgets;

import data.VGSSign;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import websocket.WidgetSessionHandler;

public final class VGSWidget extends Widget {

    private Set<VGSSign> signs = new HashSet<>();
    private Set<String> texts = new HashSet<>();
    private final static int SIGNSREFRESHINTERVALMINUTES = 43_829;  //Reload signs 1 time each month, text on the signs each [updateInterval]
    private int counter = 0;
    private final String[] urls;

    /**
     * Construct a VGS widget (VGS = "VerkeersGeleidingsSysteem" in Dutch).
     *
     * VGS is a network of digital information signs distributed acros Ghent.
     * This widget extracts the texts displayed on these signs and merges them
     * together. This way an overview of all the different texts shown on these
     * signs becomes available for displaying in a list.
     */
    public VGSWidget() {
        updateInterval = 5;
        urls = getSettings(getWidgetType());
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return websocket.WidgetSessionHandler.WidgetType.VGS;
    }

    @Override
    public void parse() {
        counter++;
        counter %= (SIGNSREFRESHINTERVALMINUTES / updateInterval);
        if (counter == 0 || signs.isEmpty()) {
            try {
                initSigns();
            } catch (MalformedURLException ex) {
                Logger.getLogger(VGSWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            } catch (IOException ex) {
                Logger.getLogger(VGSWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }
        }

        //Get text to match with the signs.
        try {
            for (VGSSign sign : signs) {
                URL url = new URL(String.format(urls[1], sign.getName()));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                StringBuilder sb = new StringBuilder();

                try (JsonReader jsonReader = Json.createReader(conn.getInputStream())) {
                    JsonObject jsonObjSignDetail = jsonReader.readObject();
                    if ("PUBLISHING".equals(jsonObjSignDetail.getString("publicationStatus"))) {
                        JsonArray jsonArrVmsDisplayContents = jsonObjSignDetail.getJsonArray("vmsDisplayContents");
                        for (Iterator<JsonValue> it = jsonArrVmsDisplayContents.iterator(); it.hasNext();) {
                            JsonObject jsonObjVmsDisplayContent = (JsonObject) it.next();
                            JsonArray jsonObjVmsDisplayContentAreas = jsonObjVmsDisplayContent.getJsonArray("areas");
                            for (Iterator<JsonValue> it2 = jsonObjVmsDisplayContentAreas.iterator(); it2.hasNext();) {
                                JsonObject jsonObjVmsDisplayContentArea = (JsonObject) it2.next();
                                if ("TEXT".equals(jsonObjVmsDisplayContentArea.getString("dataType"))) {
                                    sb.append(jsonObjVmsDisplayContentArea.getString("dataValue").trim()).append("\n");
                                }
                            }
                        }
                    }
                }
                String text = sb.toString();
                sign.setText(text);
                text = text.replaceAll("\\s{2,}", " ");
                if (text != null && !text.isEmpty()) {
                    texts.add(sb.toString());
                }
            }

            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            jsonBuilder.add("action", "data")
                    .add("widgetType", getWidgetType().name());
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (VGSSign sign : signs) {
                arrayBuilder.add(sign.toJsonObject());
            }
            jsonBuilder.add("signs", arrayBuilder);

            JsonArrayBuilder arrayBuilderText = Json.createArrayBuilder();
            for (String text : texts) {
                arrayBuilderText.add(text);
            }
            jsonBuilder.add("texts", arrayBuilderText);
            jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
            json = jsonBuilder.build();

        } catch (MalformedURLException ex) {
            Logger.getLogger(VGSWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(VGSWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
        }
    }

    /**
     * Fetches and stores the names and locations of the vgs-signs.
     *
     * This method is used to detect all deployed signs. This information is
     * necessary to request the text for each sign individualy.
     */
    private void initSigns() throws MalformedURLException, IOException {

        URL url = new URL(urls[0]);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        try (JsonReader jsonReader = Json.createReader(conn.getInputStream())) {
            JsonArray jsonArr = jsonReader.readArray();
            for (Iterator<JsonValue> it = jsonArr.iterator(); it.hasNext();) {
                JsonObject jsonObjSign = (JsonObject) it.next();
                String name = jsonObjSign.getString("name");
                if (name.startsWith("B")) {// || name.startsWith("O")) {
                    VGSSign sign = new VGSSign();
                    sign.setName(jsonObjSign.getString("name"));
                    JsonObject jsonObjLocation = jsonObjSign.getJsonObject("location");
                    sign.setLat(jsonObjLocation.getJsonNumber("y").doubleValue());
                    sign.setLng(jsonObjLocation.getJsonNumber("x").doubleValue());

                    signs.add(sign);
                }
            }
        }
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "VGS URL's")
                .add("schema", true)
                .add("schemaType", "json")
                .add("description", "Op de eerste URL wordt de data voor een algemeen overzicht van de borden opgehaald (hiervoor dient schema), op de tweede wordt de info per bord opgehaald.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

}
