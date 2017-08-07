/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import com.google.gson.Gson;
import java.util.ArrayList;

/**
 *
 * @author ruben
 */
public class TrainRoute extends ArrayList<Train> implements IApiModel{
    
        
    @Override
    public String getAsJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public ApiType getApiType() {
        return ApiType.TRAINROUTE;
    }
    
    public void setTotalTravelTime(String t){
        for(Train tr : this){
            tr.setTotalTravelTime(t);
        }
    }
    
}
