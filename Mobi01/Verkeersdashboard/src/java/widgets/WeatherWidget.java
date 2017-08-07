package widgets;

import controllers.NotificationController;
import data.Weather;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import websocket.WidgetSessionHandler;

public class WeatherWidget extends Widget {

    private ArrayList<Weather> forecast;
    private final String weatherUrl;
    private Timer timer;
/**
 * Constructor of WeatherWidget where we have an update interval of 15 mins because of a limit on the API calls
 */
    public WeatherWidget() {
        updateInterval = 15;
        weatherUrl = "http://api.wunderground.com/api/" + getSettings(getWidgetType())[0] + "/forecast/q/BE/gent.json";
        TimerTask t=new TimerTask() {
            @Override
            public void run() {
                if(!forecast.isEmpty())
                NotificationController.getInstance().sendWeatherNotification(forecast.get(0).getPOP());
            }
        };
        notificationtasks.add(t);
        timer=new Timer();
        Date now = new Date();
        Calendar midnight = Calendar.getInstance();
        midnight.setTime(now);
        midnight.add(Calendar.DATE, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 5);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        //schedule a notification for 5 AM in the morning
        timer.scheduleAtFixedRate(t, midnight.getTime(), 1000*60*60*24);
    }

    @Override
    public WidgetSessionHandler.WidgetType getWidgetType() {
        return WidgetSessionHandler.WidgetType.WEATHER;
    }

    @Override
    public void parse() {
        forecast = new ArrayList<>();
        try {
            URL url = new URL(weatherUrl);
            URLConnection conn = url.openConnection();
            JsonReader jsonReader = Json.createReader(conn.getInputStream());
            //insert a check if the json was correctly read
            JsonObject mainobj = jsonReader.readObject();
            JsonObject simpleforecast = mainobj.getJsonObject("forecast").getJsonObject("simpleforecast");
            JsonArray forecastArray = simpleforecast.getJsonArray("forecastday");
            JsonObject day1=(JsonObject) forecastArray.get(0);
            //NotificationController.getInstance().sendWeatherNotification(day1.getJsonNumber("pop").toString());
            for (int i = 0; i < forecastArray.size(); i++) {
                JsonObject day = (JsonObject) forecastArray.get(i);
                Weather w = new Weather();
                JsonObject date = day.getJsonObject("date");
                w.setDay(translation.getString(date.getString("weekday")));
                JsonObject temp = day.getJsonObject("high");
                w.setTemperature(Double.parseDouble(temp.getString("celsius")));
                w.setWeatherDescription(day.getString("icon"));
                w.setRelativeHumidity(day.getJsonNumber("avehumidity").toString());
                w.setQPF(Double.parseDouble(day.getJsonObject("qpf_allday").getJsonNumber("mm").toString()));
                w.setPOP(Integer.parseInt(day.getJsonNumber("pop").toString()));
                JsonObject wind = day.getJsonObject("avewind");
                w.setWindSpeed(Double.parseDouble(wind.getJsonNumber("kph").toString()));
                forecast.add(w);
            }
            initJson();

        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(WeatherWidget.class.getName()).log(Level.SEVERE, null, ex);
            setFailJson(ex.getMessage());
        }

    }
/**
 * Build the JSON with information about the weather
 */
    private void initJson() {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("action", "data");
        jsonBuilder.add("widgetType", "" + getWidgetType());
        jsonBuilder.add("lastUpdate", dateFormat.format(lastUpdate));

        if (!forecast.isEmpty()) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (Weather w : forecast) {
                arrayBuilder.add(Json.createObjectBuilder()
                        .add("day", w.getDay())
                        .add("weatherDescripton", w.getWeatherDescription())
                        .add("temperature", w.getTemperature())
                        .add("humidity", w.getRelativeHumidity())
                        .add("windSpeed", w.getWindSpeed())
                        .add("pop",w.getPOP()));
            }
            jsonBuilder.add("forecast", arrayBuilder.build());
        }
        json = jsonBuilder.build();
    }

    @Override
    public JsonObject getSettings() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("widgetType", getWidgetType().toString())
                .add("title", "ParkAndRide URL")
                .add("schema", false)
                .add("description", "Onderstaande key wordt gebruikt om data op te halen van WeatherUnderground.")
                .add("url", properties.getProperty(getWidgetType().toString()));
        return builder.build();
    }

}
