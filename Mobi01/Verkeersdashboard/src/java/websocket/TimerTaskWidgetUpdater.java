package websocket;

import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import widgets.Widget;

public class TimerTaskWidgetUpdater extends TimerTask {

    Widget widget;
    private final long maxTardiness;

    public TimerTaskWidgetUpdater(Widget widget) {
        this.widget = widget;
        maxTardiness = widget.getUpdateInterval();
    }

    @Override
    public void run() {
        //Logger.getLogger(TimerTaskWidgetUpdater.class.getName()).log(Level.INFO, "Updating widget\t{0}\t\t{1}", new String[]{widget.getWidgetType().name(), (new Date()).toString()});
        if (System.currentTimeMillis() - scheduledExecutionTime() < maxTardiness*60_000) {
            try {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            widget.update();
                            WidgetSessionHandler.sendToAllConnectedSessions(widget.getJson());
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Exception while updating " + widget.getWidgetType().name() + "widget: ", ex);
                        }
                    }
                };
                (new Thread(run)).start();
            } catch (Exception ex) {
                Logger.getLogger(WidgetSessionHandler.class.getName()).log(Level.WARNING, "Exception while updating widgets: ", ex);
            }
        }
    }
}
