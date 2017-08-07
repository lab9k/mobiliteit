/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

import Model.IApiModel;
import Model.Train;
import Model.TrainRoute;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class TrainMessages {

    /**
     *
     * @param models
     * @return
     */
    public List<String> getMessages(List<IApiModel> models) {
        List<String> help = new ArrayList<>();
        for (IApiModel m : models) {
            if (m instanceof TrainRoute) {
                Train t = ((TrainRoute) m).get(0);
                StringBuilder sb = new StringBuilder("De trein van: ");
                sb.append(t.getStation());
                sb.append(" naar ");
                sb.append(t.getDest());
                sb.append(" op perron ");
                sb.append(t.getPlatform());
                sb.append(" van ");
                int time = t.getDeptime();

                StringBuilder h = new StringBuilder();
                h.append("");
                h.append(time);
                String strI = h.toString();
                Long epoch = Long.parseLong(strI);
                Date hourDep = new Date(epoch * 1000);
                SimpleDateFormat f = new SimpleDateFormat("HH:mm");
                sb.append(f.format(hourDep));

                switch (t.getDelay()) {
                    case 0:
                        sb.append(" heeft geen vertraging.");
                        break;
                    case 1:
                        sb.append(" heeft 1 minuut vertraging.");
                        break;
                    default:
                        sb.append(" heeft ");
                        sb.append(t.getDelay());
                        sb.append(" minuten vertraging.");
                        break;
                }
                help.add(sb.toString());

            }
        }
        if (help.isEmpty()) {
            help.add("De treininformatie is op dit moment niet beschikbaar.");
        }
        return help;
    }
}
