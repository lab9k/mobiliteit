/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

import Model.BlueBikeParking;
import Model.Delay;
import Model.IApiModel;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class BlueBikeMessages {

    /**
     *
     * @param models
     * @return
     */
    public List<String> getMessages(List<IApiModel> models) {
        List<String> help = new ArrayList<>();

        for (IApiModel m : models) {
            if (m instanceof BlueBikeParking) {
                StringBuilder sb = new StringBuilder("In de BlueBike parking ");
                BlueBikeParking b = (BlueBikeParking) m;
                if(b.getName().contains("Dampoort")){
                    sb.append("'Gent Dampoort'");
                } else if(b.getName().contains("Sint-Pieters")){
                    sb.append("'Gent Sint-Pieters'");
                }
                switch (b.getAvailable()) {
                    case 1:
                        sb.append(" is er nog ");
                        sb.append(b.getAvailable());
                        sb.append(" fiets ter beschikking.");
                        break;
                    case 0:
                        sb.append(" zijn er geen fietsen meer beschikbaar.");
                        break;
                    default:
                        sb.append(" zijn er nog ");
                        sb.append(b.getAvailable());
                        sb.append(" fietsen ter beschikking.");
                        break;
                }
                help.add(sb.toString());
            }
        }
        if(help.isEmpty()){
            help.add("De informatie over BlueBike is op dit ogenblik niet beschikbaar.");
        }
        return help;
    }

}
