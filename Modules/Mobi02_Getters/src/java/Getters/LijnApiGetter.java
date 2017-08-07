/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Model.IApiModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class LijnApiGetter {

    @EJB
    private DeLijnGetter delijn;

    @EJB
    private LijnstopsGetter lijnstops;

    public List<IApiModel> getDataModel() {

        /* werking:
            *default: haltes heeft default waarden
            *de lijn krijgt die haltes zijn get datamodel mee
            *dan de lijn zijn datamodel
            *Indien eerst personal choices gezet werden worden deze waarden gebruikt
         */
        List<IApiModel> haltes = lijnstops.getDataModel();
        try {
            if (haltes.size() > 3) {
                haltes = haltes.subList(0, 3);
            }

        } catch (Exception e) {
            //nothing
        }

        /*
            for(IApiModel halte : haltes){
                bussen.addAll(delijn.getDataModel(halte));
            }
         */
        return haltes;

    }

    public void setPersonalChoices(double lat, double lon, int rad) {
        lijnstops.setLocation(lat, lon, rad);
    }

}
