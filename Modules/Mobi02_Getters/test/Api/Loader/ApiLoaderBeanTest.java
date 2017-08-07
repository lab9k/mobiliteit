/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Api.Loader;

import Api.ApiType;
import Exceptions.ApiNotFoundException;
import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.ModelConvertor;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author ruben
 */
public class ApiLoaderBeanTest {
    @Rule
    public final ExpectedException exception = ExpectedException.none();
     
    EJBContainer container;
    ApiLoaderBean instance;
    public ApiLoaderBeanTest() {
    }
    
    @Before
    public void setUp() throws NamingException {
        container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        
        instance = (ApiLoaderBean)container.getContext().lookup("java:global/classes/apiloader");
    }
    
    @After
    public void tearDown() {
        container.close();
    }

    /**
     * Test of getModel method, of class ApiLoaderBean.
     */
    @Test
    public void testGetModel(){
        System.out.println("getModel");
        
        for(ApiType type : ApiType.getAllValues()){
            assertFalse(instance.getModel(type.getNameCI()).isEmpty());
        }
    }
    @Test
    public void testGetModelWithErrors(){
        System.out.println("getModel");
        String res = instance.getModel("Wrongname");
        String expected = new ApiNotFoundException().getAsJSON();
        assertEquals(expected,res);
         
    }

    /**
     * Test of getModelAsObject method, of class ApiLoaderBean.
     */
    @Test
    public void testGetModelAsObject(){
        System.out.println("getModel");
        
        for(ApiType type : ApiType.getAllValues()){
            assertNotEquals(null,instance.getModelAsObject(type.getNameCI()));
        }
    }

    /**
     * Test of getModelPersonal method, of class ApiLoaderBean.
     */
    //Still fails
    @Test
    public void testGetModelPersonal(){
        System.out.println("getModelPersonal");
        double lon = 51.054342;
        double lat = 3.717424;
        int rad = 500;
        String result = instance.getModelPersonal(lon, lat, rad);
        assertFalse(result !=  null || result.isEmpty() );
    }
    
}
