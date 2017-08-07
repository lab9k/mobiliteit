/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.Delay;
import Model.IApiModel;
import Model.Risk;
import com.mashape.unirest.http.Unirest;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ruben
 */
public class CoyoteGetterTest {
    private EJBContainer container;
    
    public CoyoteGetterTest() {
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
     * Test of init method, of class CoyoteGetter.
     */
    @Test
    public void testInit() throws NamingException {
        System.out.println("init");
        CoyoteGetter instance = (CoyoteGetter)container.getContext().lookup("java:global/classes/CoyoteGetter");
        instance.init();
    }

    /**
     * Test of getDataModel method, of class CoyoteGetter.
     */
    @Test
    public void testGetDataModel() throws NamingException {
        System.out.println("getDataModel");
        CoyoteGetter instance = (CoyoteGetter)container.getContext().lookup("java:global/classes/CoyoteGetter");
        List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Delay || result.get(0) instanceof Risk || result.get(0) instanceof ApiRequestException);
   
    }

    /**
     * Test of getRawData method, of class CoyoteGetter.
     */
    @Test
    public void testGetRawData() throws Exception{
        System.out.println("getRawData");
        CoyoteGetter instance = (CoyoteGetter)container.getContext().lookup("java:global/classes/CoyoteGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }    
}
