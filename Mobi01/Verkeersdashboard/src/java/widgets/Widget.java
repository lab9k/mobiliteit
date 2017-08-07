package widgets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import websocket.WidgetSessionHandler;
import websocket.WidgetSessionHandler.WidgetType;

public abstract class Widget {

    protected JsonObject json;
    protected Date lastUpdate = new Date();
    protected int updateInterval = 1;
    protected final ResourceBundle translation = ResourceBundle.getBundle("data.Translation", Locale.getDefault());
    protected final ResourceBundle coyoteTranslation = ResourceBundle.getBundle("data.CoyoteTranslation", Locale.getDefault());
    protected static Properties properties;
    private String status;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    protected Set<TimerTask> notificationtasks = new HashSet<>();

    /**
     * Default constructor. Initialises the widget, setting a default not
     * initialised json and loading the properties.
     */
    public Widget() {
        setFailJson("Not initialised.");
        if (properties == null) {
            properties = new Properties();
            loadProperties();
        }
    }

    /**
     * Widgettype classes inherit this by returning their WidgetType
     *
     * @return type of widget
     */
    public abstract WidgetSessionHandler.WidgetType getWidgetType();

    /**
     * Widgettype classes inherit this by parsing the data from their
     * datasource(s).
     */
    public abstract void parse();

    /**
     * Sets timestamp for lastUpdate and start parse procedure.
     */
    public void update() {
        lastUpdate = new Date();
        parse();
    }

    /**
     * Returns the current data-json (or error-json if no data present).
     *
     * @return most recent JsonObject representing this widget.
     */
    public JsonObject getJson() {
        return json;
    }

    /**
     * Returns a JsonObject with the content for the settings popup at the admin
     * page. Those are the settings in the ResourceKeys.properties file
     *
     * @return JsonObject with the content of the properties
     */
    public abstract JsonObject getSettings();

    /**
     * Initialises a secure SSLcontext.
     */
    public int getUpdateInterval() {
        return updateInterval;
    }

    public String getStatus() {
        return status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Reloads the ResourceKeys.properties file. Can be called when the settings
     * are updated via the admin page
     */
    public static void loadProperties() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("data/ResourceKeys.properties");
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(new File(url.toURI().getPath()));
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Widget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Widget.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Widget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets the status and updates the json with information about the error.
     * Can be called when an error occurs to inform the json-receivers
     *
     * @param status
     */
    protected final void setFailJson(String status) {
        this.status = status;
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("action", "error");
        jsonObjectBuilder.add("widgetType", getWidgetType().name());
        jsonObjectBuilder.add("status", (status == null ? "null" : status));
        jsonObjectBuilder.add("lastUpdate", dateFormat.format(lastUpdate));
        json = jsonObjectBuilder.build();
    }

    /**
     * When the value of the property of the WidgetType type exists of a String
     * with key-value pairs (also separated by comma's), this function returns
     * those pairs in a Map.
     *
     * @param type
     * @return Map with key-value pairs of the settings for type
     */
    protected Map<String, String> getPairedSettings(WidgetType type) {
        String[] settings = properties.getProperty(type.toString()).split(",");
        Map<String, String> res = new HashMap<>();
        try{
            for (int i = 0; i < settings.length; i += 2) {
                res.put(settings[i], settings[i + 1]);
            }
        } catch(Exception e){
            setFailJson("Laden configuratie mislukt: " + e.getMessage());
            res.clear();
        }
        return res;
    }

    /**
     * Returns a String array of which each element is a setting of the
     * WidgetType type. The value of the property of each WidgetType is a String
     * with multiple elements separated by comma (to simulate an array in a
     * properties file).
     *
     * @param type
     * @return settings for type
     */
    protected String[] getSettings(WidgetType type) {
        return properties.getProperty(type.toString()).split(",");
    }

    public final void cancelNotificationTasks() {
        for (TimerTask task : notificationtasks) {
            task.cancel();
        }
    }
}
