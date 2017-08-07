/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

/**
 *
 * @author ruben
 */
public class ApiNotFoundException extends ApiRequestException {
    
    public ApiNotFoundException(){
        super("Api Type not recognized!");
    }
    
}
