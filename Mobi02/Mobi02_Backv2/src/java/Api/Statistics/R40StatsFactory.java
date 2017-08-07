/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Statistics;

import Database.Entities.R40Statistics;
import Model.Counting;
import Model.IApiModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author ruben
 * 
 * Since the R40 countings api returns a whole set of data corresponding each to a single stations, multiple instances of the R40StatsContainer need to processed and persisted to the db.
 * This factory will make sure the proper data is added to the proper container
 */
@Singleton(name = "R40")
public class R40StatsFactory {

    
    private HashMap<String, R40StatsContainer> container;
    
    /**
     *
     */
    @PostConstruct
    public void init(){
        container = new HashMap<>();
    }
    
    /**
     *
     * @param list
     */
    public void addCountings(List<IApiModel> list){
        for(IApiModel model : list){
            if( model instanceof Counting){
             Counting c = (Counting) model;
            
            R40StatsContainer cont = container.get(c.getContextEntity());
            if(cont == null){
                cont = new R40StatsContainer();
            }
            cont.addCounting(c);
            container.put(c.getContextEntity(), cont);
            }
        }
    }
    
    /**
     *
     * @return
     */
    public List<R40Statistics> createEntities(){
        ArrayList<R40Statistics> list = new ArrayList<>();
        for(R40StatsContainer cont : container.values()){
            R40Statistics st = cont.createEntity();
            if(st != null){
                list.add(st);                
            }
            cont.reset();
        }
        return list;
    }
    
}
