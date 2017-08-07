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
public class DatabaseException extends Exception {
    private String entityName;

    /**
     * Creates a new instance of <code>DatabaseException</code> without detail
     * message.
     */
    public DatabaseException() {
    }

    /**
     * Constructs an instance of <code>DatabaseException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DatabaseException(String msg) {
        super(msg);
    }

    /**
     *
     * @param entityName
     * @param message
     */
    public DatabaseException(String entityName, String message) {
        super(message);
        this.entityName = entityName;
    }
    
    /**
     *
     * @return
     */
    public String getAsJSON(){
        if (entityName == null) entityName = "Unknown";
        return "{ \"DatabaseException\": { \"Entity\": \"" +entityName + "\", \"message\": \"" + super.getMessage() + "\"}}";
    }
}
