/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.Busses;
import Model.IApiModel;
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
public class LijnApiGetterTest {
     private EJBContainer container;
    public LijnApiGetterTest() {
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
     * Test of init method, of class DeLijnGetter.
     */
    

    /**
     * Test of getDataModel method, of class DeLijnGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        LijnApiGetter instance = (LijnApiGetter)container.getContext().lookup("java:global/classes/LijnApiGetter");
        List<IApiModel> result = instance.getDataModel();
        assertTrue(result.get(0) instanceof Busses || result.get(0) instanceof ApiRequestException);
   
    }
}
