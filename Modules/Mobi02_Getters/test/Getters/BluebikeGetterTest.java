/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.BlueBikeParking;
import Model.IApiModel;
import com.mashape.unirest.http.Unirest;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ruben
 */
public class BluebikeGetterTest {
    EJBContainer container;
    
    
    public BluebikeGetterTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    }
    
    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of init method, of class BluebikeGetter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        BluebikeGetter instance = (BluebikeGetter)container.getContext().lookup("java:global/classes/BluebikeGetter");
        instance.init();
    }

    

    /**
     * Test of getDataModel method, of class BluebikeGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        
        BluebikeGetter instance = (BluebikeGetter) container.getContext().lookup("java:global/classes/BluebikeGetter");
        
        List<IApiModel> result = instance.getDataModel();
        assertTrue(!result.isEmpty() && (result.get(0) instanceof BlueBikeParking || result.get(0) instanceof ApiRequestException));
    }

    /**
     * Test of getRawData method, of class BluebikeGetter.
     */
    @Test
    public void testGetRawData_0args() throws Exception {
        System.out.println("getRawData");
        BluebikeGetter instance = (BluebikeGetter)container.getContext().lookup("java:global/classes/BluebikeGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }
    
}
