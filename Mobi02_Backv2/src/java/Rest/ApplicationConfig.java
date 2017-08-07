/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author ruben
 */
@javax.ws.rs.ApplicationPath("res")
public class ApplicationConfig extends Application {

    /**
     *
     * @return
     */
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(Rest.ApidataRest.class);
        resources.add(Rest.CorsFilter.class);
        resources.add(Rest.FacebookRest.class);
        resources.add(Rest.LoggedIn.UserProperties.class);
        resources.add(Rest.StatisticsRest.class);
        resources.add(Rest.UserRegister.class);
        resources.add(Rest.loginRest.class);
    }
    
}
