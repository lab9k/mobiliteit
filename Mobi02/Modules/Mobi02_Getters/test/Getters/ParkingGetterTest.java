/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Parking;
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
public class ParkingGetterTest {
    private EJBContainer container;
    public ParkingGetterTest() {
    }
    
    @Before
    public void setUp() {
        container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
    }
    
    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of getRawData method, of class ParkingGetter.
     */
    @Test
    public void testGetRawData() throws Exception {
        System.out.println("getRawData");
        ParkingGetter instance = (ParkingGetter)container.getContext().lookup("java:global/classes/ParkingGetter");
        String result = instance.getRawData();
        assertTrue(result.length() > 0);
    }

    /**
     * Test of getDataModel method, of class ParkingGetter.
     */
    @Test
    public void testGetDataModel() throws Exception {
        System.out.println("getDataModel");
        ParkingGetter instance = (ParkingGetter)container.getContext().lookup("java:global/classes/ParkingGetter");
        List<IApiModel> result = instance.getDataModel(true,true);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Parking || result.get(0) instanceof ApiRequestException);
    }
    
}
