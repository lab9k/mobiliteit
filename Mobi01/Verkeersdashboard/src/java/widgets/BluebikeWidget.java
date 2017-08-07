package widgets;

import data.BikeCenter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import websocket.WidgetSessionHandler;

public final class BluebikeWidget extends Widget {

    private Map<String, BikeCenter> bikeCenters;
    private Map<String, String> bikeCenterUrls; //data source on url containts real time info about bike centers (capacity,currently in use, location,...)
    private DocumentBuilder builder;
    private int bikesAvailable = 0;
    private int totalBikes = 0;

    public BluebikeWidget() {
        bikeCenterUrls = getPairedSettings(getWidgetType());
        bikeCenters = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BluebikeWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.BLUEBIKE;
    }
    
    /**
     * Method to parse information about the bluebikes datasources. 
     */
    @Override
    public void parse() {
        if(bikeCenterUrls.isEmpty()){
            return;
        }
        if (builder == null) {
            totalBikes = 0;
            bikesAvailable = 0;
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                builder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(BluebikeWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }
        }
        for (String location : bikeCenterUrls.keySet()) {
            try {
                BikeCenter b = new BikeCenter();
                b.setLocation(location);
                //read the XML file containing information about bikecenters
                URL url = new URL(bikeCenterUrls.get(location));
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                Document doc = builder.parse(is);
                NodeList properties = doc.getElementsByTagName("element");
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                jsonBuilder.add("widgetType", "" + getWidgetType());
                //extract the available number of bikes from the XML file
                int available = Integer.parseInt(properties.item(4).getFirstChild().getNextSibling().getTextContent());
                b.setBikesAvailable(available);
                bikesAvailable += available;
                int total = Integer.parseInt(properties.item(2).getFirstChild().getNextSibling().getTextContent());
                b.setBikesTotal(total);
                totalBikes += total;
                bikeCenters.put(b.getLocation(), b);
            } catch (MalformedURLException ex) {
                Logger.getLogger(BluebikeWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            } catch (IOException | SAXException ex) {
                Logger.getLogger(BluebikeWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }

        }
        initJson();
    }

    /**
     * Builds the JSON for the bluebike centers.
     */
    private void initJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data")
                .add("widgetType", "" + getWidgetType());
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (String location : bikeCenters.keySet()) {
            addJsonBikeCenter(arrayBuilder, location);
        }
        jsonBuilder.add("bikeCenters", arrayBuilder.build())
                .add("percentAvailable", (int)(100*bikesAvailable/totalBikes))
                .add("lastUpdate", dateFormat.format(lastUpdate));
        json = jsonBuilder.build();

    }
/**
 * Adds the information for a certain bikecenter to the JSON
 * @param arrayBuilder
 * @param location 
 */
    private void addJsonBikeCenter(JsonArrayBuilder arrayBuilder, String location) {
        JsonObjectBuilder bikeCenterObjectBuilder = Json.createObjectBuilder();
        bikeCenterObjectBuilder.add("location", bikeCenters.get(location).getLocation())
                .add("available", bikeCenters.get(location).getBikesAvailable())
                .add("total", bikeCenters.get(location).getBikesTotal());
        arrayBuilder.add(bikeCenterObjectBuilder.build());
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("widgetType", getWidgetType().toString())
                .add("title", "Bluebike URL's")
                .add("schema", true)
                .add("schemaType", "xsd")
                .add("description", "Onderstaande waarden vormen paren van de vorm \"fietspuntnaam, fietspunturl\" en worden geparset in de BluebikeWidget")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return jsonBuilder.build();
    }
}
