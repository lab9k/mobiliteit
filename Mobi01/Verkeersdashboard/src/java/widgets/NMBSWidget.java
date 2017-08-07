package widgets;

import controllers.NotificationController;
import data.Train;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import websocket.WidgetSessionHandler;

public final class NMBSWidget extends Widget {

    private DocumentBuilder builder;
    private Map<String, String> trainStations;
    private List<Train>  delayedTrains;
    private Timer timer;
    private Train tr1;
    private List<Train> trains;

    public NMBSWidget() {
        trains = new ArrayList<>();
        delayedTrains=new ArrayList<>();
        trainStations = getPairedSettings(getWidgetType());
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //DatabaseController.getInstance().buildTrainConnections();
        
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(NMBSWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
        TimerTask t=new TimerTask() {
            @Override
            public void run() {
                NotificationController.getInstance().sendNMBSNotification(delayedTrains); 
            }
        };
         TimerTask t2=new TimerTask() {
            @Override
            public void run() {delayedTrains.clear();}};
        notificationtasks.add(t);
        timer=new Timer();
        //send a notification to interested users about certain train
        timer.scheduleAtFixedRate(t, 1000*60, 1000*60);
        //clear the delayed trains after an hour
        timer.scheduleAtFixedRate(t2, 1000*60*60, 1000*60*60);
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.NMBS;
    }
/**
 * 
 * Parse the XML data about the trains coming through the stations and save this info into a list of Train objects
 */
    @Override
    public void parse() {
        if (builder == null) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                builder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(NMBSWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }
        }
        trains.clear();
        //trains.add(tr1);
        for (String station : trainStations.keySet()) {
            try {
                URL url = new URL(trainStations.get(station));
                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                Document doc = builder.parse(is);
                NodeList l = doc.getElementsByTagName("departure");
                for (int i = 0; i < l.getLength(); i++) {
                    Element e = (Element) l.item(i);
                    Train tr = new Train();
                    tr.setDepartureStation(station);
                    tr.setEndStation(e.getFirstChild().getTextContent());
                    Element e2 = (Element) e.getFirstChild().getNextSibling();
                    String datum = e2.getAttribute("formatted");
                    Element e3=(Element) e2.getNextSibling();
                    tr.setTrainId( e3.getTextContent());
                    tr.setDepartureTime(LocalTime.parse(datum, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                    tr.setDayOfWeek(translation.getString(LocalDateTime.now().getDayOfWeek().toString()));
                    Boolean left = Boolean.parseBoolean(e.getAttribute("left"));
                    tr.setLeft(left);
                    Boolean cancelled = Boolean.parseBoolean(e.getAttribute("canceled"));
                    int delay = Integer.parseInt(e.getAttribute("delay"));
                    tr.setDelay(delay);
                    tr.setCancelled(cancelled);
                    if(delay>300 | cancelled){
                        delayedTrains.add(tr);
                    }
                    trains.add(tr);
                }           
            } catch (MalformedURLException ex) {
                Logger.getLogger(NMBSWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            } catch (IOException | SAXException ex) {
                Logger.getLogger(NMBSWidget.class.getName()).log(Level.SEVERE, null, ex);
                setFailJson(ex.getMessage());
                return;
            }
        }
        initJson();
    }
/**
 * Builds the JSON with the necessary information for this widget
 */
    private void initJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data");
        jsonBuilder.add("widgetType", "" + getWidgetType());
        jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
        addTrains(jsonBuilder);
        json = jsonBuilder.build();
    }
    
    /**
     * Add all the trains we read from the XML to the JSON with their corresponding departure information
     * @param jsonBuilder 
     */
    private void addTrains(JsonObjectBuilder jsonBuilder) {
        if (!trains.isEmpty()) {
            Collections.sort(trains, new Comparator<Train>() {
                @Override
                public int compare(Train o1, Train o2) {
                    return o2.getDepartureTime().compareTo(o1.getDepartureTime());
                }
            });
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Train t : trains) {
                arrayBuilder.add(Json.createObjectBuilder().add("departureStation", t.getDepartureStation())
                        .add("endStation", t.getEndStation())
                        .add("departureTime", t.getDepartureTime().toString())
                        .add("delay", t.getDelay())
                        .add("left", t.isLeft())
                        .add("canceled", t.isCancelled()));
            }
            jsonBuilder.add("departures", arrayBuilder.build());
        }
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("widgetType", getWidgetType().toString())
                .add("title", "NMBS URL's")
                .add("schema", true)
                .add("schemaType", "xsd")
                .add("description", "Onderstaande waarden vormen paren van de vorm \"stationsnaam, stationurl\" en worden geparset in de NMBSWidget")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return jsonBuilder.build();
    }
}
