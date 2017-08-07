package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import widgets.CoyoteWidget;

public class RoadSection {

    private String name;
    private int normalTime; //the normal travel time for this section
    private int realTime; //the current travel time for this section
    private int latestTimesSum; //the sum of the last realtimes (i.e. when it's 14:12, this is the sum of the realtimes at 14:10, 14:11 and 14:12)
    private int count; //number of times in latestTimesSum
    private int day; //sunday=1, saturday=7
    private Map<Integer, Integer> todayTimes; //the traveltimes measured today in pairs 'minutesOfDay, measuredTime'. minutesToday is the amount of minutes as returned by getMinutes()
    private JsonObject todayAvgJson; //containing array with the average times (for each 10 minutes) for this day  of the week AND an array with their timestamps
    private JsonObject todayTimesJson; //containing info about the realtimes which were measured today
    private DataSource ds;
    private final String timesQueryString //to query times in avgTimes
            = "SELECT avgTime, startTimestamp, totalTime, totalEntries, minTime "
                    + "FROM avgTimes "
                    + "WHERE section = ? AND day = ? AND minTime >= ? "
                    + "ORDER BY minTime";
    private String timesUpdateString //to update existing times in avgTimes
            = "UPDATE avgTimes SET totalTime = ?, avgTime = ? ,totalEntries = ? "
                    + " WHERE section = ? AND minTime = ? AND day = ?";
    private String timesInsertString //to insert new times in avgTimes
            ="INSERT INTO avgTimes "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    /**
     * Constructor with name of the section as parameter. Initializes all the
     * class variables en starts a queryAverageTimes()
     * @param name 
     */
    public RoadSection(String name) {
        this.name = name;
        count = 0;
        day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        todayTimes = new LinkedHashMap<>();
        try {
            Context ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("jdbc/dashboard");
            try {
                Connection conn = ds.getConnection();
                conn.setAutoCommit(true);
                queryAverageTimes(conn);
                todayTimesJson = Json.createObjectBuilder().build();
            } catch (SQLException ex) {
                Logger.getLogger(RoadSection.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (NamingException ex) {
            Logger.getLogger(RoadSection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setNormalTime(int time) {
        normalTime = time;
    }

    /**
     * Adds a new time and calculates the new average.
     * @param time 
     */
    public void addRealTime(int time) {
        int minutes = getMinutes();
        if (minutes % 10 == 0) {
            updateToday();
            realTime = latestTimesSum = time;
            count = 1;
        } else {
            latestTimesSum += time;
            count++;
            realTime = latestTimesSum / count; //calculate average of the last minutes
        }
    }

    /**
     * Returns the hour in minutes. E.g. at 2:17 a.m. it returns 137 (2*60+17)
     * @return the hour in minuts
     */
    private int getMinutes() {
        return 60 * Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * Converts the given minutes to a beautiful timestap in hh:mm format (24h)
     * @param minutes
     * @return timestamp in String
     */
    private String getTimeStamp(int minutes) {
        return minutes / 60 + ":" + String.format("%02d", minutes % 60);
    }

    /**
     * Returns a JsonObject with the basic information of the section. Name,
     * current realTime and the normalTime are added.
     * @return a JsonObject with basic information
     */
    public JsonObject getBasicJson() {
        JsonObjectBuilder routeBuilder = Json.createObjectBuilder();
        routeBuilder.add("name", name);
        routeBuilder.add("real_time", realTime);
        routeBuilder.add("normal_time", normalTime);
        return routeBuilder.build();
    }

    /**
     * Returns a JsonObject with more detailed information of the section.
     * Including all the realTimes of today and the average times
     * @return a JsonObject with more detailed information
     */
    public JsonObject getDetailedJson() {
        JsonObjectBuilder detailedJsonBuilder = Json.createObjectBuilder();
        detailedJsonBuilder.add("action", "data").add("widgetType", "HISTORIC").add("name", name);
        return detailedJsonBuilder.add("realTimes", todayTimesJson)
                .add("avgTimes", todayAvgJson).build();
    }

    /**
     * Called at midnight. Puts all the times of today in the database
     * (calculating new average times) and queries the averages for the new day.
     * Creates new connection for this. Invoked by CoyoteWidget
     */
    public void updateAvg() {
        try {
            Connection conn = ds.getConnection();
            PreparedStatement timesUpdate = conn.prepareStatement(timesUpdateString);
            PreparedStatement timesInsert = conn.prepareStatement(timesInsertString);
            conn.setAutoCommit(true);
            //update average times of yesterday
            ResultSet rs = getAvgResultSet(conn, name, day, 0);
            while (rs.next()) { //recalculating average for times that are already in the database
                int beginTime = rs.getInt("minTime");
                if (todayTimes.containsKey(beginTime)) {
                    int totalTime = rs.getInt("totalTime");
                    int totalEntries = rs.getInt("totalEntries");
                    if (totalTime > 2000000000) { //max int value is SQL Server is 2147483647
                        totalTime /= 10;
                        totalEntries /= 10;
                    }
                    totalEntries++;
                    totalTime += todayTimes.remove(beginTime);
                    int avgTime = (int) (totalTime / totalEntries);
                    timesUpdate.setInt(1, totalTime);
                    timesUpdate.setInt(2, avgTime);
                    timesUpdate.setInt(3, totalEntries);
                    timesUpdate.setString(4, name);
                    timesUpdate.setInt(5, beginTime);
                    timesUpdate.setInt(6, day);
                    timesUpdate.addBatch();
                }
            }
            timesUpdate.executeBatch();
            for (int beginTime : todayTimes.keySet()) { //inserting averages for times which aren't in the database yet
                int time = todayTimes.get(beginTime);
                timesInsert.setString(1, name);
                timesInsert.setInt(2, day);
                timesInsert.setInt(3, beginTime);
                timesInsert.setInt(4, time);
                timesInsert.setInt(5, time);
                timesInsert.setInt(6, 1);
                timesInsert.setString(7, getTimeStamp(beginTime));
                timesInsert.addBatch();
            }
            timesInsert.executeBatch();
            //new day -> query new averages
            todayTimes.clear();
            day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            queryAverageTimes(conn);
            updateToday();
        } catch (SQLException ex) {
            Logger.getLogger(CoyoteWidget.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Updates the todaytTimes json.
     */
    private void updateToday() {
        todayTimes.put(getMinutes(), realTime);
        JsonArrayBuilder timesBuilder = Json.createArrayBuilder();
        JsonArrayBuilder timestampBuilder = Json.createArrayBuilder();
        for(int i = 0; i < 60*24; i+=10){
            timestampBuilder.add(getTimeStamp(i));
            try {
                timesBuilder.add(todayTimes.get(i));
            }
            catch (NullPointerException e) {
                timesBuilder.add(JsonValue.NULL);
            }
        }
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("times", timesBuilder.build());
        builder.add("timestamps", timestampBuilder.build());
        todayTimesJson = builder.build();
    }

    /**
     * Queries the average times out of the database and builds todayAvgJson
     * with this information. Uses a given connection and closes it at
     * the end
     * @param conn 
     */
    private void queryAverageTimes(Connection conn) {
        try {
            JsonArrayBuilder timestampArrayBuilder = Json.createArrayBuilder();
            JsonArrayBuilder timesArrayBuilder = Json.createArrayBuilder();
            ResultSet rs = getAvgResultSet(conn, name, day, 0);
            while (rs.next()) {
                timestampArrayBuilder.add(rs.getString("startTimestamp"));
                timesArrayBuilder.add(rs.getInt("avgTime"));
            }
            JsonObjectBuilder avgBuilder = Json.createObjectBuilder();
            avgBuilder.add("timestamps", timestampArrayBuilder.build());
            avgBuilder.add("avgTimes", timesArrayBuilder.build());
            todayAvgJson = avgBuilder.build();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(RoadSection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns a ResultSet with the results of querying the average times of
     * the traject with the given name on a given day, starting from a
     * timestip described by minMinutes. Uses a given connection.
     * @param conn
     * @param name
     * @param day
     * @param minMinutes
     * @return
     * @throws SQLException 
     */
    private ResultSet getAvgResultSet(Connection conn, String name, int day, int minMinutes) throws SQLException {
        PreparedStatement timesQuery = conn.prepareStatement(timesQueryString);
        timesQuery.setString(1, name);
        timesQuery.setInt(2, day);
        timesQuery.setInt(3, minMinutes);
        return timesQuery.executeQuery();
    }
}