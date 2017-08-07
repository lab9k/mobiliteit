package websocket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import websocket.WidgetSessionHandler.WidgetType;

public class TimerTaskAdminNotifier extends TimerTask {
/**
 * Execute a task in which we send a list of disabled widget to the user's session to be used for the admin page.
 */
    @Override
    public void run() {
        try {
            List<WidgetType> disabledTypes = new ArrayList<>(Arrays.asList(WidgetType.values()));
            for(WidgetType type : WidgetSessionHandler.getCurrentWidgetTypes()){
                disabledTypes.remove(type);
            }
            JsonObjectBuilder builder = Json.createObjectBuilder();
            builder.add("action", "adminDisabledWidgets");
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (WidgetType disabledType : disabledTypes) {
                arrayBuilder.add(disabledType.name());
            }
            builder.add("disabledWidgets", arrayBuilder);
            JsonObject json = builder.build();
            WidgetSessionHandler.sendToAllConnectedAdminSessions(json);
        } catch(Exception ex){
            Logger.getLogger(TimerTaskAdminNotifier.class.getName()).log(Level.SEVERE, "Failed to send disabled widgets to admins: {0}", ex.getMessage());
        }
    }
    
}
