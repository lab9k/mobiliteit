/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Train;
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
public class TrainGetterTest {
    private EJBContainer container;
    
    public TrainGetterTest() {
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
     * Test of init method, of class TrainGetter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        TrainGetter instance = (TrainGetter)container.getContext().lookup("java:global/classes/TrainGetter");
        instance.init();
        
    }
    /**
     * Test of getDataModel method, of class TrainGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        TrainGetter instance = (TrainGetter)container.getContext().lookup("java:global/classes/TrainGetter");
        List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Train || result.get(0) instanceof ApiRequestException);
    }

    /**
     * Test of getRawData method, of class TrainGetter.
     */
    @Test
    public void testGetRawData_String() throws Exception {
        System.out.println("getRawData");
        TrainGetter instance = (TrainGetter)container.getContext().lookup("java:global/classes/TrainGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }
    
}
