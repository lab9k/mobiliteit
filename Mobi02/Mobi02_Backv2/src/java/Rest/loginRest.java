/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import Database.Dao.UserDao;
import Notifications.Mail.MailDao;
import Sessions.SessionContainer;
import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ruben
 */
@Path("userlogin")
public class loginRest {

    @Context
    private UriInfo context;
    @EJB
    UserDao dao;
    @EJB
    SessionContainer sessions;

    @EJB
    MailDao mailDao;

    private Gson gson;

    /**
     * Creates a new instance of loginRest
     */
    public loginRest() {
        gson = new Gson();
    }

    /**
     * PUT method for updating or creating an instance of loginRest
     *
     * @param content representation for the resource
     * @param request
     * @return 
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response PostJson(String content, @Context HttpServletRequest request) {
        JSONObject obj = new JSONObject(content);
        String mail = obj.getString("username");
        String pass = obj.getString("userpass");
        long id = dao.findUser(mail, pass);
        if (id > 0) {
            sessions.login(request.getSession(), id);
            return Response.ok("{\"success\": \"True\"}").build();
        } else {
            return Response.ok("{\"success\": \"False\"}").build();
        }

    }

    /**
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response doGetJson(@Context HttpServletRequest request) {
        if (sessions.isLoggedIn(request.getSession()) > 0) {
            return Response.ok("{\"loggedIn\": \"True\"}").build();
        } else {
            return Response.ok("{\"loggedIn\": \"False\"}").build();
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @GET
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@Context HttpServletRequest request) {
        sessions.logout(request.getSession());
        return Response.ok("{\"success\": \"True\"}").build();
    }

    /**
     *
     * @param request
     * @param userJson
     * @return
     */
    @POST
    @Path("/facebook")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fbLogin(@Context HttpServletRequest request, String userJson) {
        try {
            JSONObject obj = new JSONObject(userJson);
            String pageId = obj.getString("page_id");
            String firstname = obj.getString("firstname");
            String lastname = obj.getString("lastname");
            Long userId = dao.loginWithFacebook(pageId);
            if (userId < 0) {
                dao.registerFacebook(pageId, firstname, lastname);
                userId = dao.loginWithFacebook(pageId);
                if (userId < 0) {
                    return Response.ok("{\"success\": \"False\"}").build();
                }
            }
            sessions.login(request.getSession(), userId);
            return Response.ok("{\"success\": \"True\"}").build();
        } catch (Exception ex) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",ex);
            return Response.status(Status.BAD_REQUEST).build();
        }
    }

    /**
     *
     * @param mailJson
     * @return
     */
    @POST
    @Path("/sendMail")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendMail(String mailJson) {
        JSONObject obj = new JSONObject(mailJson);
        String mail = obj.getString("mail");
        String subject = obj.getString("subject");
        String text = obj.getString("text");
        mailDao.sendMail(mail, subject, text);
        return null;
    }

}
