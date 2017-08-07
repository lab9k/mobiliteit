/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Risk;
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
public class WaylayGetterTest {
    private EJBContainer container;
    public WaylayGetterTest() {
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
     * Test of init method, of class WaylayGetter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        WaylayGetter instance = (WaylayGetter)container.getContext().lookup("java:global/classes/WaylayGetter");
        instance.init();
        
    }

    /**
     * Test of getRawData method, of class WaylayGetter.
     */
    @Test
    public void testGetRawData() throws Exception {
        System.out.println("getRawData");
        WaylayGetter instance = (WaylayGetter)container.getContext().lookup("java:global/classes/WaylayGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }

    

    /**
     * Test of getDataModel method, of class WaylayGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        WaylayGetter instance = (WaylayGetter)container.getContext().lookup("java:global/classes/WaylayGetter");
        List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Risk || result.get(0) instanceof ApiRequestException);
    }
    
}
