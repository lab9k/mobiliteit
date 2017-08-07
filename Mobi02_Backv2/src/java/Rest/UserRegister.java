/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import Database.Dao.UserDao;
import Database.Entities.PersonalUser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author ruben
 */
@Stateless
@Path("register")
public class UserRegister {

    @Context
    private UriInfo context;
    @EJB
    private UserDao dao;

    /**
     * Creates a new instance of UserRegister
     */
    public UserRegister() {
    }

    /**
     * PUT method for updating or creating an instance of UserRegister
     * @param json
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postUser(String json) {
        Gson gson = new Gson();
        try{
        
        return dao.addUser(gson.fromJson(json,PersonalUser.class));
        }catch(Exception ex){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return "{\"Exception\": \"" + ex.getMessage() + "\"}";
        }
    }
}
