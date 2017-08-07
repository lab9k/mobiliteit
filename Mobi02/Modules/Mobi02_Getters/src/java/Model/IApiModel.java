/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Api.ApiType;

/**
 *
 * @author ruben
 */
public interface IApiModel {
    
    String getAsJSON();
    ApiType getApiType();
}
