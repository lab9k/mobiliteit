/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Caching;

import Api.ApiType;
import Model.IApiModel;
import Model.ModelConvertor;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author ruben
 * 
 * This class will contain all cached APIData
 * 
 */
@Singleton(name = "cacheddata")
@Startup
public class CachedData {

    @EJB
    ModelConvertor conv;
    private HashMap<ApiType, List<IApiModel>> data;

    /**
     *
     * @param type The apitype
     * @param value The model
     * 
     * Adds the model corresponding to the type in the hashmap
     */
    public void UpdateModel(ApiType type, List<IApiModel> value) {
        try {
            if(data == null) data = new HashMap<>();
            data.put(type, value);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Exception while storing data in cache", e);
        }
    }
    
    /**
     *
     * @param type
     * @return
     * 
     * Returns a model in JSON-string based on the specified type, will return null if not found
     */
    public String getAsJson(ApiType type) {
        if(data == null) data = new HashMap<>();
        if(data.containsKey(type)){
            String models = conv.getModelAsJson(type, data.get(type));
            if (models != null) {
                return models;
            } 
        }
        return null;
    }

    /**
     *
     * @param type
     * @return
     * 
     * Returns a model based on the specified type, will return null if not found
     */
    public List<IApiModel> getDataFromApi(ApiType type) {
        if(data == null) data = new HashMap<>();
        if (data.containsKey(type)) {
            return data.get(type);
        } else {
            return null;
        }
    }

}
