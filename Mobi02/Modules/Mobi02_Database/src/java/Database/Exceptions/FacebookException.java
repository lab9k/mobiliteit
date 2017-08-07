/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Exceptions;

/**
 *
 * @author ruben
 */
public class FacebookException extends Exception {

    /**
     * Creates a new instance of <code>FbIntegrationException</code> without
     * detail message.
     */
    public FacebookException() {
    }

    /**
     * Constructs an instance of <code>FbIntegrationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FacebookException(String msg) {
        super(msg);
    }
    
    /**
     *
     * @return
     */
    public String getAsJSON(){
        return "{ \"FacebookException\": {\"message\": \"" + super.getMessage() + "\"}}";
    }
}
