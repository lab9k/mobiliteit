package widgets;

import controllers.DatabaseController;
import data.RoadSection;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.net.ssl.HttpsURLConnection;
import javax.sql.DataSource;
import websocket.WidgetSessionHandler;

public final class CoyoteWidget extends Widget {

    private String sessionId;
    private final String[] urls;
    private final static int RETRY = 2;
    private int retry = 0;
    private Map<String, RoadSection> sections;
    private final List<String> R40Clockwise;
    private final List<String> R40CounterClockwise;

    public CoyoteWidget() {
        sections = new ConcurrentHashMap<>();
        urls = getSettings(getWidgetType());
        R40Clockwise = Arrays.asList(properties.getProperty("R40Clockwise").split(","));
        R40CounterClockwise = Arrays.asList(properties.getProperty("R40CounterClockwise").split(","));
        Timer timer = new Timer(); //timer to update the averages of all RoadSections at midnight
        Date now = new Date();
        Calendar midnight = Calendar.getInstance();
        midnight.setTime(now);
        midnight.add(Calendar.DATE, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for(RoadSection s : sections.values())
                    s.updateAvg();
            }
        }, midnight.getTime(), 24*60*60*1000); //24*60*60*1000 milliseconds = one day
        DatabaseController.getInstance().createCoyoteTable();
        
        
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.COYOTE;
    }

    @Override
    public void parse() {
        try {
            if (sessionId == null || sessionId.isEmpty()) {
                String id = getNewSessionId();
                if (id == null || id.isEmpty()) {
                    Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, "Exception: fetching new session ID failed.");
                    return;
                }
                sessionId = id;
            }
            try {
                URL url = new URL(urls[1]);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.addRequestProperty("Cookie", sessionId);
                JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
                jsonBuilder.add("action", "data").add("widgetType", getWidgetType().name());

                try (JsonReader jsonReader = Json.createReader(conn.getInputStream())) {
                    JsonObject jsonObj = jsonReader.readObject();
                    JsonObject gandObj = jsonObj.getJsonObject("Gand");
                    JsonArrayBuilder jsonArraySectionsBuilder = Json.createArrayBuilder();
                    JsonObjectBuilder jsonObjectAlertsBuilder = Json.createObjectBuilder();
                    JsonArrayBuilder jsonArrayTimeBuilder = Json.createArrayBuilder();
                    double totalNormalTime = 0;
                    double totalRealTime = 0;
                    double R40ClockwiseTime = 0;
                    double R40CounterClockwiseTime = 0;

                    for (String key : gandObj.keySet()) {
                        JsonObject streetObj = gandObj.getJsonObject(key);
                        //real time vs normal time for each section
                        try {
                            if (R40Clockwise.contains(key))
                                R40ClockwiseTime += (streetObj.getJsonNumber("real_time")).doubleValue();
                            else if (R40CounterClockwise.contains(key))
                                R40CounterClockwiseTime += (streetObj.getJsonNumber("real_time")).doubleValue();
                            JsonObjectBuilder route = Json.createObjectBuilder();
                            String[] parts = key.split(" - ");
                            String name = parts[0].substring(0, parts[0].lastIndexOf(" "));
                            String direction = parts[0].substring(parts[0].lastIndexOf(" ") + 1);
                            if (direction.contains("(")) {
                                direction = direction.substring(1, direction.length() - 1);
                            }
                            name += " [" + direction.substring(0, 1) + "]";
                            if (!sections.containsKey(name)) {
                                sections.put(name, new RoadSection(name));
                            }
                            RoadSection section = sections.get(name);
                            double normalTime = (streetObj.getJsonNumber("normal_time")).doubleValue();
                            double realTime = (streetObj.getJsonNumber("real_time")).doubleValue();
                            section.setNormalTime((int) normalTime);
                            section.addRealTime((int) realTime);
                            totalNormalTime += normalTime;
                            totalRealTime += realTime;
                            jsonArrayTimeBuilder.add(section.getBasicJson());
                        } catch (Exception ex) {
                            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.WARNING, "Exception while parsing time of sections.");
                        }

                        //wegen met vertraging
                        try {
                            JsonArray sectionArr = streetObj.getJsonArray("sections");
                            for (int i = 0; i < sectionArr.size(); i++) {
                                jsonArraySectionsBuilder.add(sectionArr.get(i));
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.WARNING, "Exception while parsing external sections.");
                        }

                        //wegen met alerts
                        try {
                            JsonObject alertsObj = streetObj.getJsonObject("alerts");
                            for (String alertKey : alertsObj.keySet()) {
                                jsonObjectAlertsBuilder.add(alertKey, processAlert(alertsObj.getJsonObject(alertKey)));
                            }
                        } catch (ClassCastException ex) {
                            //ignore: alerts == array => leeg
                        } catch (Exception ex) {
                            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.WARNING, "Exception while parsing external alerts. {0}", ex.getMessage());
                        }
                    }

                    //Algemene alerts
                    JsonObject alertsObj = jsonObj.getJsonObject("alerts");
                    for (String alertKey : alertsObj.keySet()) {
                        jsonObjectAlertsBuilder.add(alertKey, processAlert(alertsObj.getJsonObject(alertKey)));
                    }

                    double delayPercent = 100 * (totalRealTime / totalNormalTime - 1);
                    if (!sections.containsKey("Totaal")) {
                        sections.put("Totaal", new RoadSection("Totaal"));
                    }
                    sections.get("Totaal").setNormalTime((int) totalNormalTime);
                    sections.get("Totaal").addRealTime((int) totalRealTime);

                    jsonBuilder.add("R40Clockwise", (int)(R40ClockwiseTime/60))
                            .add("R40CounterClockwise", (int)(R40CounterClockwiseTime/60))
                            .add("sections", jsonArraySectionsBuilder)
                            .add("alerts", jsonObjectAlertsBuilder)
                            .add("timing", jsonArrayTimeBuilder)
                            .add("delayPercent", delayPercent);
                } finally {
                    conn.disconnect();
                }
                jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
                json = jsonBuilder.build();

                retry = 0;
            } catch (Exception ex) {

                if (retry < RETRY) {
                    Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, "Exception while fetching coyote data. retrying...\t\t{0}", ex.getMessage());
                    retry++;
                    sessionId = null;
                    parse();
                } else {
                    retry = 0;
                    Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, "Exception while fetching coyote data. aborting.", ex);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Request a new session ID from coyote to authenticate future requests
     * @return sessionID
     * @throws MalformedURLException
     * @throws IOException 
     */
    private String getNewSessionId() throws MalformedURLException, IOException {
        URL url = new URL(urls[0]);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        List<AbstractMap.SimpleEntry> params = new ArrayList<>();
        params.add(new SimpleEntry("login", properties.getProperty("coyoteLogin")));
        params.add(new SimpleEntry("password", properties.getProperty("coyotePassword")));

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();
        conn.connect();
        String id = conn.getHeaderField("Set-Cookie");
        conn.disconnect();
        return id;
    }

    
    /**
     * Put the given parameters in an url-compatible parameter string
     * @param params the parameters to add to a url
     * @return the parameters combined in one String
     * @throws UnsupportedEncodingException 
     */
    private String getQuery(List<SimpleEntry> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (SimpleEntry pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode((String) pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode((String) pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    /**
     * Returns the historic data about the RoadSection with name trajectName
     * @param trajectName
     * @return  JsonObject with historic information about trajectName
     */
    public JsonObject getHistoricData(String trajectName) {
        return sections.get(trajectName).getDetailedJson();
    }
    
    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "Coyote URL's")
                .add("schema", false)
                .add("description", "De eerste URL wordt gebruikt om een cookie op te halen. Deze cookie wordt gebruikt om via de tweede URL een JSON-file op te halen. Meer info <a href=\"https://drive.google.com/file/d/0B7oaoHqGAgCVSHdpNk1wdkM4MnM/view\">hier</a>.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

    /**
     * Translates the "type_lbl" from this alert (French to Dutch) and discards any information we don't need
     * @param jsonObject the alert object from the original Json, recieved from the data source
     * @return a new JsonObject with only the information we need and a translated alert description
     */
    private JsonValue processAlert(JsonObject jsonObject) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("lat", jsonObject.getJsonNumber("lat"));
        builder.add("lng", jsonObject.getJsonNumber("lng"));
        try {
            builder.add("type_lbl", coyoteTranslation.getString(jsonObject.getString("type_id")));
        } catch (Exception ex){
            builder.add("type_lbl", jsonObject.getString("type_lbl"));
        }
        
        return builder.build();
    }
}
