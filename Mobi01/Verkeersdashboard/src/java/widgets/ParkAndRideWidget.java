package widgets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
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
import websocket.WidgetSessionHandler.WidgetType;

public final class ParkAndRideWidget extends Widget {

    private final String urlString;

    public ParkAndRideWidget() {
        updateInterval = 1440;
        urlString = getSettings(getWidgetType())[0];
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetType.PARKANDRIDE;
    }

    @Override
    public void parse() {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList recordsList = doc.getElementsByTagName("Placemark");
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            jsonBuilder.add("action", "data");
            jsonBuilder.add("widgetType", getWidgetType().name());
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (int i = 0; i < recordsList.getLength(); i++) {
                Node parking = recordsList.item(i);
                NodeList info = parking.getFirstChild().getNextSibling().getFirstChild().getNextSibling().getChildNodes();
                if (info.item(1).getTextContent().equals("P+R")) {
                    String name = info.item(3).getTextContent();
                    Node point = parking.getLastChild().getPreviousSibling();
                    String coordinatesString = point.getFirstChild().getNextSibling().getTextContent();
                    String[] coordinates = coordinatesString.split(",");
                    arrayBuilder.add(Json.createObjectBuilder().add("name", name)
                            .add("x", Double.parseDouble(coordinates[0]))
                            .add("y", Double.parseDouble(coordinates[1])).build());
                }
            }
            jsonBuilder.add("parkings", arrayBuilder.build());
            jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
            json = jsonBuilder.build();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ParkAndRideWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
        }
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "ParkAndRide URL")
                .add("schema", true)
                .add("schemaType", "xsd")
                .add("description", "Onderstaande XML-databron wordt geparset in de ParkAndRideWidget.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

}
