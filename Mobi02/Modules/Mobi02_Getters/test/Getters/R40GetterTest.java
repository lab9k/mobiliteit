/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.Counting;
import Model.IApiModel;
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
public class R40GetterTest {
    private EJBContainer container;
    public R40GetterTest() {
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
     * Test of init method, of class R40Getter.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        R40Getter instance = (R40Getter)container.getContext().lookup("java:global/classes/R40Getter");
        instance.init();
    }

    /**
     * Test of getRawData method, of class R40Getter.
     */
    @Test
    public void testGetRawData() throws Exception {
        System.out.println("getRawData");
        R40Getter instance = (R40Getter)container.getContext().lookup("java:global/classes/R40Getter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getDataModel method, of class R40Getter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        R40Getter instance = (R40Getter)container.getContext().lookup("java:global/classes/R40Getter");
        List<IApiModel> result = instance.getDataModel();
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Counting || result.get(0) instanceof ApiRequestException);
    }
    
}
