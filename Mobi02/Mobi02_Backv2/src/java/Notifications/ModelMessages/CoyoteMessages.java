/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications.ModelMessages;

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
public class CoyoteMessages{

    /**
     *
     * @param models
     * @return
     */
    public List<String> getMessages(List<IApiModel> models) {
        List<String> help = new ArrayList<>();
        
        for(IApiModel m: models){
            if(m instanceof Delay){
                StringBuilder sb = new StringBuilder("De huidige reistijd voor uw route bedraagt");
                Delay d = (Delay) m;
                String rt = String.format("%.0f" , d.getReal_time()) ;
                sb.append(rt);
                sb.append(" minuten. \nDit is ");
                sb.append(d.getMinutes());
                if(d.getMinutes()==1){
                    sb.append(" minuut trager dan normaal.");
                } else {
                    sb.append(" minuten trager dan normaal.");
                }
                help.add(sb.toString());
                
            }
        }
        if(help.isEmpty()){
            help.add("De informatie van uw routes is tijdelijk niet beschikbaar.");
        }
        return help;
    }
    
    
}
