/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Weather;
import com.mashape.unirest.http.Unirest;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ruben
 */
public class WeatherGetterTest {
    private EJBContainer container;
    public WeatherGetterTest() {
    }
    
    @Before
    public void setUp() throws Exception{
        container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    }
    
    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of init method, of class WeatherGetter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        WeatherGetter instance = (WeatherGetter)container.getContext().lookup("java:global/classes/WeatherGetter");
        instance.init();
    }

    /**
     * Test of getRawData method, of class WeatherGetter.
     */
    @Test
    public void testGetRawData() throws Exception {
        System.out.println("getRawData");
        WeatherGetter instance = (WeatherGetter)container.getContext().lookup("java:global/classes/WeatherGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getDataModel method, of class WeatherGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        WeatherGetter instance = (WeatherGetter)container.getContext().lookup("java:global/classes/WeatherGetter");
        List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Weather || result.get(0) instanceof ApiRequestException);
    }
    
}
