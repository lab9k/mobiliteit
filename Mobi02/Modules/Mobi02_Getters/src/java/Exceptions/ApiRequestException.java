/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exceptions;

import Api.ApiType;
import Model.IApiModel;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.ApplicationException;

/**
 *
 * @author Ruben
 */
@ApplicationException(rollback=true)
public class ApiRequestException extends Exception implements IApiModel {
    private final String api;
    private boolean logToDb;
    
    /**
     * Creates a new instance of <code>ApiHttpRequestException</code> without
     * detail message.
     */
    public ApiRequestException() {
        api = "Unknown";
        logToDb = true;
    }

    /**
     * Constructs an instance of <code>ApiHttpRequestException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ApiRequestException(String msg) {
        super(msg);
        logToDb = true;
        
        api = "unknown";
    }

    public ApiRequestException(String api, String message) {
        super(message);
        this.api = api;
        logToDb = true;
    }

    public ApiRequestException(String api, boolean logToDb) {
        this.api = api;
        this.logToDb = logToDb;
    }

    public ApiRequestException(String api, boolean logToDb, String message) {
        super(message);
        this.api = api;
        this.logToDb = logToDb;
    }

    public ApiRequestException(String api, boolean logToDb, String message, Throwable cause) {
        super(message, cause);
        this.api = api;
        this.logToDb = logToDb;
    }

    public ApiRequestException(String api, boolean logToDb, Throwable cause) {
        super(cause);
        this.api = api;
        this.logToDb = logToDb;
    }

    public ApiRequestException(String api, boolean logToDb, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.api = api;
        this.logToDb = logToDb;
    }
    
    
    /*public LogEntity createLogEntity(){
        LogEntity entity = new LogEntity();
        entity.setMessage(this.getMessage());
        entity.setType(api);
        return entity;
    }*/

    public String getApi() {
        return api;
    }
    
    
    
    @Override
    public String getAsJSON() {
        return "{ \"ApiHttpRequestException\": { \"ApiType\": \"" +api + "\", \"message\": \"" + super.getMessage() + "\"}}"; 
    }

    @Override
    public ApiType getApiType() {
        return ApiType.EXCEPTION;
    }
    
    public List<IApiModel> getModelList(){
        ArrayList<IApiModel> list = new ArrayList<>();
        list.add(this);
        return list;
    }

    public boolean isLogToDb() {
        return logToDb;
    }

    public void setLogToDb(boolean logToDb) {
        this.logToDb = logToDb;
    }
    
    
}
