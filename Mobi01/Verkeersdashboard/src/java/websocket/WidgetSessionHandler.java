package websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.Session;
import websocket.WidgetSessionHandler.WidgetType;
import static websocket.WidgetSessionHandler.WidgetType.*;
import widgets.BluebikeWidget;
import widgets.CountPointWidget;
import widgets.CoyoteWidget;
import widgets.GIPODWidget;
import widgets.NMBSWidget;
import widgets.ParkAndRideWidget;
import widgets.ParkingFeeAreaWidget;
import widgets.ParkingWidget;
import widgets.PollutionWidget;
import widgets.VGSWidget;
import widgets.WeatherWidget;
import widgets.Widget;

@Singleton
@ApplicationScoped
public class WidgetSessionHandler {

    private static final Set<Session> sessions = new HashSet<>();
    private static final Set<Session> adminSessions = new HashSet<>();
    private static final Map<WidgetType, Widget> widgets = new HashMap<>();
    private static final Map<WidgetType, TimerTaskWidgetUpdater> tasks = new HashMap<>();
    private static TimerTaskAdminNotifier adminTask = new TimerTaskAdminNotifier();
    private static Timer minuteTimer;

    @Inject
    public WidgetSessionHandler() {
        minuteTimer = new Timer("minuteTimer");
        HTTPSDownload();
        initWidgets();
    }
    
    private  void HTTPSDownload() {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
    }

    /**
     * Try to start every known widget.
     */
    private static void initWidgets() {
        for (WidgetType type : WidgetType.values()) {
            addWidget(type);
        }
        Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.INFO, "initialised widgets: {0}", widgets.toString());
    }

    /**
     * Store new client sessions, to use later for sending updated data
     *
     * @param session The session associated with this client
     */
    public static void addSession(Session session) {
        synchronized (sessions) {
            sessions.add(session);
        }
        for (Widget widget : widgets.values()) {
            JsonObject addMessage = createDataMessage(widget);
            if (addMessage != null) {
                sendToSession(session, addMessage);
            }
        }
    }

    /**
     * Add a new session verified as admin
     *
     * @param session The session associated with this admin client
     */
    void addAdminSession(Session session) {
        synchronized (adminSessions) {
            adminSessions.add(session);
        }
        if (adminTask != null) {
            adminTask = new TimerTaskAdminNotifier();
            minuteTimer.scheduleAtFixedRate(adminTask, 1000, 60_000);
        }
    }

    /**
     * remove a session from our active sessions list
     *
     * @param session the session to be removed
     */
    public static void removeSession(Session session) {
        synchronized (sessions) {
            sessions.remove(session);
        }
    }

    /**
     * remove admin session from our list of active admin clients
     *
     * @param session the session to remove
     */
    public static void removeAdminSession(Session session) {
        synchronized (adminSessions) {
            adminSessions.remove(session);
            if (adminSessions.isEmpty()) {
                adminTask.cancel();
                adminTask = null;
            }
        }
    }

    /**
     * Get a list of all running widgets
     *
     * @return currently running widgets
     */
    public static List<Widget> getWidgets() {
        return new ArrayList<>(widgets.values());
    }

    /**
     * Get a list of the widgetTypes that are currently running
     *
     * @return WidgetTypes of the currently active widgets
     */
    public static List<WidgetType> getCurrentWidgetTypes() {
        return new ArrayList<>(widgets.keySet());
    }

    /**
     * Activate a widget
     *
     * @param widgetType The widgetType of the widget to activate
     */
    public static void addWidget(WidgetType widgetType) {
        try {
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.INFO, "Adding {0}Widget", widgetType);
            if (null != widgetType) {
                switch (widgetType) {
                    case WEATHER:
                        addWidget(new WeatherWidget());
                        break;
                    case BLUEBIKE:
                        addWidget(new BluebikeWidget());
                        break;
                    case PARKING:
                        addWidget(new ParkingWidget());
                        break;
                    case PARKANDRIDE:
                        addWidget(new ParkAndRideWidget());
                        break;
                    case COUNTPOINT:
                        addWidget(new CountPointWidget());
                        break;
                    case COYOTE:
                        addWidget(new CoyoteWidget());
                        break;
                    case NMBS:
                        addWidget(new NMBSWidget());
                        break;
                    case GIPOD:
                        addWidget(new GIPODWidget());
                        break;
                    case POLLUTION:
                        addWidget(new PollutionWidget());
                        break;
                    case VGS:
                        addWidget(new VGSWidget());
                        break;
                    case PARKINGFEEAREA:
                        addWidget(new ParkingFeeAreaWidget());
                        break;
                    default:
                        Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Exception: Requested widgetType not implemented. No widget added.\t{0}", widgetType);
                        break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Exception while adding " + widgetType + "Widget: ", ex);
        }
    }

    /**
     * Add a widget to the list of currently active widgets
     *
     * @param widget The widget to add
     */
    public static void addWidget(Widget widget) {
        widgets.put(widget.getWidgetType(), widget);
        JsonObject addMessage = createDataMessage(widget);
        sendToAllConnectedSessions(addMessage);

        TimerTaskWidgetUpdater task = new TimerTaskWidgetUpdater(widget);
        minuteTimer.scheduleAtFixedRate(task, 1000, widget.getUpdateInterval() * 60_000);
        tasks.put(widget.getWidgetType(), task);
    }

    /**
     * remove a widget from the currently active widgets list
     *
     * @param widget
     */
    public static void removeWidget(Widget widget) {
        removeWidget(widget.getWidgetType());
    }

    /**
     * Remove the widget with the provided WidgetType from the list of the
     * currently active widgets (and stop related update tasks)
     *
     * @param widgetType
     */
    public static void removeWidget(WidgetType widgetType) {
        Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.INFO, "Removing Widget: {0}", widgetType);//, ex);
        Widget widget = widgets.remove(widgetType);
        JsonObject deleteMessage = createDeleteMessage(widgetType);
        sendToAllConnectedSessions(deleteMessage);
        try {
            tasks.get(widgetType).cancel();
            tasks.remove(widgetType);
        } catch (NullPointerException ex) {
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Tried to cancel an updateTask that did not exist.");//, ex);
        }
        if (widget != null) {
            widget.cancelNotificationTasks();
        }
    }

    /**
     * get the currently active widgets that has this widgetType
     *
     * @param widgetType the type of the widget to get
     * @return the widget to get
     */
    public static Widget getWidgetByType(WidgetType widgetType) {
        return widgets.get(widgetType);
    }

    /**
     * Get the data message of the provided widget
     *
     * @param widget
     * @return
     */
    private static JsonObject createDataMessage(Widget widget) {
        return widget.getJson();
    }

    /**
     * Create a remove message for the provided widgetType
     *
     * @param widgetType The widgettype to remove
     * @return a JsonObject with instructions to remove this widgetType
     */
    private static JsonObject createDeleteMessage(WidgetType widgetType) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject removeMessage = provider.createObjectBuilder()
                .add("action", "remove")
                .add("widgetType", widgetType.name())
                .build();
        return removeMessage;
    }

    /**
     * Send a message to all active client sessions
     *
     * @param message the message to send
     */
    public static void sendToAllConnectedSessions(JsonObject message) {
        if (message != null) {
            Set<Session> sessionsCopy;
            synchronized (sessions) {
                sessionsCopy = new HashSet(sessions);
            }
            for (Session session : sessionsCopy) {
                sendToSession(session, message);
            }
        } else {
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Tried to send empty message: {0}", new String[]{(message == null ? "null" : message.toString())});
        }
    }

    /**
     * Send a message to a specific session
     *
     * @param session The session to send the message to
     * @param message The message to send
     */
    static void sendToSession(Session session, JsonObject message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message.toString());
            } else {
                sessions.remove(session);
                Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Tried to send message to closed session: {0}", (message == null ? "null" : message.toString()));
            }
        } catch (IOException | NullPointerException ex) {
            sessions.remove(session);
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Failed to send message to session: {0}\t\t{1}", new String[]{(message == null ? "null" : message.toString()), ex.getMessage()});
        }
    }

    /**
     * Send a message to all verified admin sessions
     *
     * @param message The message to send
     */
    public static void sendToAllConnectedAdminSessions(JsonObject message) {
        if (message != null) {
            Set<Session> sessionsCopy;
            synchronized (adminSessions) {
                sessionsCopy = new HashSet(adminSessions);
            }
            for (Session session : sessionsCopy) {
                sendToAdminSession(session, message);
            }
        }
    }

    /**
     * Send a message to a specific admin session
     *
     * @param session The admin session to send to
     * @param message The message to send
     */
    static void sendToAdminSession(Session session, JsonObject message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(message.toString());
            } else {
                adminSessions.remove(session);
            }
        } catch (IOException | NullPointerException ex) {
            adminSessions.remove(session);
            Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Failed to send message to adminSession: {0}\t\t{1}", new String[]{(message == null ? "null" : message.toString()), ex.getMessage()});
        }
    }

    /**
     * Enum containing all known WidgetTypes
     */
    public enum WidgetType {
        WEATHER, BLUEBIKE, PARKING, PARKANDRIDE, COUNTPOINT, COYOTE, NMBS, GIPOD, POLLUTION, VGS, PARKINGFEEAREA
    }
}
