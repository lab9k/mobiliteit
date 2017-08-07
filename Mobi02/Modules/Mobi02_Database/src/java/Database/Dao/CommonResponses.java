/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;



/**
 *
 * @author ruben
 * 
 */
public class CommonResponses {
    
    /**
     *
     * @param success
     * @return String
     */
    public static String succesJSON(boolean success){
        return "{\"success\": \"" + (success ? "true" : "false") + "\"}";
    }
    
    /**
     *
     * @param message
     * @return
     */
    public static String genericErrorJson(String message){
        
        return "{\"error\": {\"message\":\"" + message + "\"}";
    }
    
}
