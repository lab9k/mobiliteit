/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Roadwork;
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
public class WazeGetterTest {
    private EJBContainer container;
    public WazeGetterTest() {
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
     * Test of init method, of class WazeGetter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        WazeGetter instance = (WazeGetter)container.getContext().lookup("java:global/classes/WazeGetter");
        instance.init();
    }

    /**
     * Test of getRawData method, of class WazeGetter.
     */
    @Test
    public void testGetRawData() throws Exception {
        System.out.println("getRawData");
        WazeGetter instance = (WazeGetter)container.getContext().lookup("java:global/classes/WazeGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getDataModel method, of class WazeGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        WazeGetter instance = (WazeGetter)container.getContext().lookup("java:global/classes/WazeGetter");List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Roadwork || result.get(0) instanceof ApiRequestException);
    }
    
}
