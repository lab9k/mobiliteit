package widgets;

import data.CountPoint;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import websocket.WidgetSessionHandler;

public final class CountPointWidget extends Widget {

    private String[] urls;
    private Map<String, CountPoint> countpoints;
    private DocumentBuilder builder;

    public CountPointWidget() {
        urls = getSettings(getWidgetType());
        countpoints = new HashMap<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CountPointWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.COUNTPOINT;
    }
    
    /**
     * Method to parse information about the countpoint datasource.
     */
    @Override
    public void parse() {
        for (String urlString : urls) {
            try {

                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();

                Document doc = builder.parse(url.openStream());
                Node features = doc.getLastChild().getLastChild();
                NodeList featurelist = features.getChildNodes();

                for (int i = 0; i < featurelist.getLength(); i++) {
                    CountPoint c = new CountPoint();
                    Node element = featurelist.item(i);
                    NodeList elementParts = element.getChildNodes();
                    for (int j = 0; j < elementParts.getLength(); j++) {
                        Node part = elementParts.item(j);
                        if (part.getNodeName().equals("geometry")) {
                            double coordinate1 = Double.parseDouble(part.getLastChild().getFirstChild().getTextContent());
                            double coordinate2 = Double.parseDouble(part.getLastChild().getLastChild().getTextContent());
                            c.setCoordinate1(coordinate1);
                            c.setCoordinate2(coordinate2);
                            String key = part.getLastChild().getTextContent();
                            c.setKey(key);
                        } else if (part.getNodeName().equals("properties")) {
                            int speed = Integer.parseInt(part.getFirstChild().getFirstChild().getLastChild().getTextContent());
                            int occ = Integer.parseInt(part.getFirstChild().getFirstChild().getNextSibling().getLastChild().getTextContent());
                            int count = Integer.parseInt(part.getFirstChild().getFirstChild().getNextSibling().getNextSibling().getLastChild().getTextContent());
                            c.setCount(count);
                            c.setSpeed(speed);
                            c.setOccupation(occ);
                        }
                        countpoints.put(c.getKey(), c);
                    }

                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(CountPointWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            } catch (IOException | SAXException ex) {
                Logger.getLogger(CountPointWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }
        }
        initJson();
    }

    private void initJson() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data");
        jsonBuilder.add("widgetType", "" + getWidgetType());
        for (CountPoint p : countpoints.values()) {
            addJsonCountPoint(p, arrayBuilder);
        }

        jsonBuilder.add("countpoints", arrayBuilder.build());
        jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
        json = jsonBuilder.build();

    }
    /**
     * Adds information about Countpoint c to the JsonArrayBuilder arrayBuilder
     * @param p
     * @param arrayBuilder 
     */
    private void addJsonCountPoint(CountPoint p, JsonArrayBuilder arrayBuilder) {
        arrayBuilder.add(Json.createObjectBuilder()
                .add("x", p.getCoordinate1())
                .add("y", p.getCoordinate2())
                .add("speed", p.getSpeed())
                .add("count", p.getCount())
                .add("occupation", p.getOccupation()));

    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("widgetType", getWidgetType().toString())
                .add("title", "Countpoint URL's")
                .add("schema", true)
                .add("schemaType", "xsd")
                .add("description", "Onderstaande XML-databronnen (gescheiden door komma) worden geparset in de CountPointWidget.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return jsonBuilder.build();
    }
}
