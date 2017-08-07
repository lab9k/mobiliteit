package widgets;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
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
import static widgets.Widget.properties;

public final class ParkingFeeAreaWidget extends Widget {

    String urlString;

    /**
     * This is the constructor for the ParkingFeeAreaWidget.
     *
     * This widget loads information about the different parking zones in the
     * city of Ghent. The represented data wil contain zones with an ID, a name,
     * a URL and a Polygon. This Polygon contains a list of Points representing
     * a polygon shape, and a list of a list of Points, representing holes in
     * this polygon. The Point elements contain latitude and longitude fields.
     *
     * The constructor sets the updateInterval to once a day and loads the used
     * URL from the properties file.
     */
    public ParkingFeeAreaWidget() {
        updateInterval = 1440;
        urlString = getSettings(getWidgetType())[0];
    }

    /**
     * Returns the type of this widget.
     *
     * @return PARKINGFEEAREA
     */
    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return websocket.WidgetSessionHandler.WidgetType.PARKINGFEEAREA;
    }

    /**
     * Starts the update procedure of this widget.
     *
     * The url, loaded from the properties file, is opened and the data is being
     * processed.
     *
     * The represented data wil contain zones with an ID, a name, a URL and a
     * Polygon. This Polygon contains a list of Points representing a polygon
     * shape, and a list of a list of Points, representing holes in this
     * polygon. The Point elements contain latitude and longitude fields.
     */
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
                String ID = null;
                String zoneName = null;
                String zoneURL = null;
                for (int j = 0; j < info.getLength(); j++) {
                    try {
                        switch (info.item(j).getAttributes().getNamedItem("name").getTextContent()) {
                            case "ID":
                                ID = info.item(j).getTextContent();
                                break;
                            case "Zone":
                                zoneName = info.item(j).getTextContent();
                                break;
                            case "URL":
                                zoneURL = info.item(j).getTextContent();
                        }
                    } catch (NullPointerException ex) {
                        //ignore
                    }
                }
                Zone zone = new Zone();
                if (ID != null) {
                    zone.setZoneID(ID);
                }
                if (zoneName != null) {
                    zone.setZoneName(zoneName);
                }
                if (zoneURL != null) {
                    zone.setZoneURL(zoneURL);
                }
                Node polygonCoordinates = parking.getLastChild().getPreviousSibling().getFirstChild().getNextSibling().getFirstChild().getNextSibling().getLastChild().getPreviousSibling();
                String coordinatesString = polygonCoordinates.getTextContent();
                String[] coordinates = coordinatesString.split(" ");
                Polygon polygon = new Polygon();
                for (String coord : coordinates) {
                    String[] latlng = coord.split(",");
                    if (latlng.length == 2) {
                        try {
                            polygon.addPoint(new Point(Double.valueOf(latlng[0]), Double.valueOf(latlng[1])));
                        } catch (NumberFormatException ex) {
                            Logger.getLogger(ParkingFeeAreaWidget.class.getName()).log(Level.WARNING, "Skiped polygon-coordinate: ", ex);
                        }
                    } else {
                        Logger.getLogger(ParkingFeeAreaWidget.class.getName()).log(Level.WARNING, "Skiped polygon-coordinate entry: latlng lenth != 2");
                    }
                }

                Node polygonHole = parking.getLastChild().getPreviousSibling().getLastChild().getPreviousSibling().getFirstChild().getNextSibling().getLastChild().getPreviousSibling();
                if (polygonHole != polygonCoordinates && !polygonHole.equals(polygonCoordinates) && polygonHole != null) {
                    polygon.addHole();
                    String coordinatesHoleString = polygonHole.getTextContent();
                    String[] coordinatesHole = coordinatesHoleString.split(" ");
                    for (String coord : coordinatesHole) {
                        String[] latlng = coord.split(",");
                        if (latlng.length == 2) {
                            try {
                                polygon.addPointToLastHole(new Point(Double.valueOf(latlng[0]), Double.valueOf(latlng[1])));
                            } catch (NumberFormatException ex) {
                                Logger.getLogger(ParkingFeeAreaWidget.class.getName()).log(Level.WARNING, "Skiped polygon-hole-coordinate: ", ex);
                            }
                        } else {
                            Logger.getLogger(ParkingFeeAreaWidget.class.getName()).log(Level.WARNING, "Skiped polygon-hole-coordinate entry: latlng lenth != 2");
                        }
                    }
                }
                zone.setPolygon(polygon);
                arrayBuilder.add(zone.toJsonObject());
            }
            jsonBuilder.add("zones", arrayBuilder.build());
            jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
            json = jsonBuilder.build();
        } catch (IOException | ParserConfigurationException | SAXException ex) {
            Logger.getLogger(ParkingFeeAreaWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
        }
    }

    /**
     * This method generates a JsonObject containing information about the
     * current configuration of this widget for use in the admin page.
     *
     * @return The generated JsonObject.
     */
    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "ParkingFeeArea widget URL")
                .add("schema", true)
                .add("schemaType", "xsd")
                .add("description", "Onderstaande XML-databron wordt geparset in de ParkingFeeAreaWidget.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

    /**
     * This class is used to represent a single parking zone.
     *
     * It contains fields for an ID, a name, a URL and a Polygon object.
     */
    class Zone {

        Polygon polygon;
        String zoneID;
        String zoneName;
        String zoneURL;

        public void setPolygon(Polygon polygon) {
            this.polygon = polygon;
        }

        public void setZoneID(String zoneID) {
            this.zoneID = zoneID;
        }

        public void setZoneName(String zoneName) {
            this.zoneName = zoneName;
        }

        public void setZoneURL(String zoneURL) {
            this.zoneURL = zoneURL;
        }

        /**
         * Copy the information of this zone into a JsonObject.
         *
         * @return JsonObject representing this zone.
         */
        public JsonObject toJsonObject() {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            if (zoneID != null) {
                builder.add("zoneID", zoneID);
            }
            if (zoneName != null) {
                builder.add("zoneName", zoneName);
            }
            if (zoneURL != null) {
                builder.add("zoneURL", zoneURL);
            }
            builder.add("polygon", polygon.toJsonArray());
            return builder.build();
        }

    }

    /**
     * This class is used to represent a Polygon to draw on a map. It can
     * conatain multiple Holes.
     */
    class Polygon {

        List<Point> points = new ArrayList<>();
        List<List<Point>> holesList = new ArrayList<>();

        /**
         * Add a hole to this Polygon.
         *
         * Holes are regions that are cut out of the polygon.
         */
        public void addHole() {
            holesList.add(new ArrayList<>());
        }

        /**
         * Add a point to the last added Polygon object.
         *
         * @param point The point to add.
         */
        public void addPointToLastHole(Point point) {
            holesList.get(holesList.size() - 1).add(point);
        }

        /**
         * Add a Point to the Polygon shape.
         *
         * @param point The Point to add.
         */
        public void addPoint(Point point) {
            points.add(point);
        }

        /**
         * Convert this Polygon to a JsonArray object.
         *
         * The JsonArray will contain The Polygon shape AND The Polygon holes.
         * The JsonArray is structured in a way that is compatible with the
         * client side Leaflet Map Polygon constructor.
         *
         * @return A JsonArray containes shape Points and hole Points.
         */
        public JsonArray toJsonArray() {
            //points.remove(points.size()-1);
            JsonArrayBuilder polygonBuilder = Json.createArrayBuilder();
            for (int i = 0; i <= holesList.size(); i++) {
                switch (i) {
                    case 0:
                        JsonArrayBuilder builder = Json.createArrayBuilder();
                        for (Point point : points) {
                            builder.add(point.toJsonArrayBuilder());
                        }
                        polygonBuilder.add(builder);
                        break;
                    default:
                        JsonArrayBuilder holeBuilder = Json.createArrayBuilder();
                        for (Point point : holesList.get(i - 1)) {
                            holeBuilder.add(point.toJsonArrayBuilder());
                        }
                        polygonBuilder.add(holeBuilder);
                        break;
                }
            }
            return polygonBuilder.build();
        }
    }

    /**
     * This class is used to represent a single point on the map using two
     * double values: longitude (lng) and latitude (lat).
     */
    class Point {

        double lat;
        double lng;

        public Point(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        /**
         * Convert this Point to a JsonObject representing this Point.
         *
         * @return a JsonObject that represents this Point.
         */
        public JsonObjectBuilder toJsonObjectBuilder() {
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("lat", lat)
                    .add("lng", lng);
            return builder;
        }

        /**
         * Convert the Point to a JsonArray representing this Point.
         *
         * @return a JsonArray that represents this Point.
         */
        public JsonArrayBuilder toJsonArrayBuilder() {
            JsonArrayBuilder builder = Json.createArrayBuilder();
            builder.add(lng)
                    .add(lat);
            return builder;
        }
    }

}
