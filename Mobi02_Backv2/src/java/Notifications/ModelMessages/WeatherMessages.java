/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

import Model.IApiModel;
import Model.Weather;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class WeatherMessages {

    /**
     *
     * @param models
     * @return
     */
    public List<String> getMessages(List<IApiModel> models) {
        List<String> help = new ArrayList<>();

        for (IApiModel m : models) {
            if (m instanceof Weather) {
                StringBuilder sb = new StringBuilder("");
                Weather w = (Weather) m;
                String rt = String.format("%.0f", w.getChanceRain());
                String tempMax = String.format("%.0f", w.getCelsiusMax());
                String tempMin = String.format("%.0f", w.getCelsiusMin());

                sb.append("Temperaturen tussen ");
                sb.append(tempMin);
                sb.append(" °C en ");
                sb.append(tempMax);
                sb.append(" °C.");

                sb.append("\nDe regenkans bedraagt ");
                sb.append(rt);
                sb.append("%.");

                help.add(sb.toString());
                
            }
        }
        if (help.isEmpty()) {
            help.add("Er is op dit moment geen weersinformatie beschikbaar.");
        }

        Logger.getLogger((this.getClass().getSimpleName())).log(Level.INFO, help.get(0));
        return help;
    }

}
