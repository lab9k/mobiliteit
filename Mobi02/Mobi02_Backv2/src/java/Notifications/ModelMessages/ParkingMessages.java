/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

import Model.IApiModel;
import Model.Parking;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author Gebruiker
 */

@Stateless
public class ParkingMessages {

    /**
     *
     * @param models
     * @return
     */
    public List<String> getMessages(List<IApiModel> models) {
        List<String> hulp = new ArrayList<>();

        for (IApiModel m : models) {
            if (m instanceof Parking) {
                StringBuilder sb = new StringBuilder("In parking '");
                Parking p = (Parking) m;
                sb.append(p.getName());

                switch (p.getAvailableCapacity()) {
                    case 1:
                        sb.append("' is er nog ");
                        sb.append(p.getAvailableCapacity());
                        sb.append(" plaats vrij.");
                        break;
                    case 0:
                        sb.append(" zijn er geen vrije plaatsen meer.");
                        break;
                    default:
                        sb.append("' zijn nog ");
                        sb.append(p.getAvailableCapacity());
                        sb.append(" plaatsen vrij.");
                        break;
                }
                hulp.add(sb.toString());
            }
        }
        if(hulp.isEmpty()){
            hulp.add("De parkinggegevens zijn tijdelijk niet beschikbaar.");
        }
        return hulp;
    }
}
