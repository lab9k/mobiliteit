/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Properties.PropertyLoaderBean;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author ruben
 */
@Stateless
public class ParkingGetter{
    
    
    private ParkingGhentGetter parkingGhent;
    
    
    private ParkingNmbsGetter parkingNmbs;
    @EJB
    private PropertyLoaderBean propertyBean;
    
    @PostConstruct
    public void init(){
        parkingGhent = new ParkingGhentGetter(propertyBean);
        parkingNmbs = new ParkingNmbsGetter(propertyBean);
    }

   
    public String getRawData() throws ApiRequestException {
        return parkingGhent.getRawData() + "," +parkingNmbs.getRawData();
                
    }
    
    
   /* public List<IApiModel> getDataForParkings(List<String> names) throws ApiRequestException{
        List<IApiModel> parkings = getDataModel();
        for(int i=0; i<parkings.size(); i++){
            
        }
    }*/

    
    public List<IApiModel> getDataModel(boolean gent, boolean nmbs){
            List<IApiModel> list = new ArrayList<>();
            if(gent){
                list.addAll(parkingGhent.getDataModel());
            }
            if(nmbs){
                list.addAll(parkingNmbs.getDataModel());
            }
            return list;
        
    }
}
