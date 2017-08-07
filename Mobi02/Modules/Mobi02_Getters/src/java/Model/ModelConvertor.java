/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;
import Exceptions.ApiRequestException;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author ruben
 */
@Stateless
public class ModelConvertor {

    public String createJsonArray(List<IApiModel> model) {
        String s = "[ ";
        for (int i = 0; i < model.size() - 1; i++) {
            s += model.get(i).getAsJSON() + " , ";
        }
        s += model.get(model.size() - 1).getAsJSON() + " ]";
        return s;
    }

    public String getModelAsJson(ApiType naam, List<IApiModel> model) {
        if (model != null) {
            String json = "{ \"" + naam.toString() + "\": ";

            if (model.size() > 1) {
                json += createJsonArray(model);
            } else if (model.size() > 0) {
                json += model.get(0).getAsJSON();
            } else {
                json += "{ }";
            }

            return json + "}";
        } else{
            return null;
            //return new ApiRequestException("model is null").getAsJSON();
        }
    }

    public ApiType getTypeFromString(String type) throws ApiRequestException {
        type = type.toLowerCase();
        ApiType[] possibleValues = ApiType.BLUEBIKE.getDeclaringClass().getEnumConstants();
        for (ApiType t : possibleValues) {
            if (t.getNameCI().equals(type)) {
                return t;
            }
        }
        throw new ApiRequestException("Api Type not recognized!");
    }

}
