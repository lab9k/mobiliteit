/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest.LoggedIn;

import Database.Dao.UserDao;
import Database.Entities.NotificationPreferences;
import Database.Entities.PersonalUser;
import Database.Entities.UserProperty;
import Database.Entities.Widgets;
import Database.Exceptions.DatabaseException;
import Sessions.SessionContainer;
import com.google.gson.Gson;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;

/**
 * REST Web Service
 * 
 * These calls will only work when an user is logged in. If not they will be intercepted by a filter.
 *
 * @author ruben
 */
@Stateless
@Path("user")
public class UserProperties {

    @Context
    private UriInfo context;
    @EJB
    UserDao dao;
    @EJB
    SessionContainer sessions;

    private Gson gson;

    /**
     * Creates a new instance of UserResource
     */
    public UserProperties() {
        gson = new Gson();
    }

    /**
     * Retrieves representation of an instance of Api.UserResource
     *
     * @param request
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@Context HttpServletRequest request) {
        long id = sessions.isLoggedIn(request.getSession());
        PersonalUser user = dao.getUser(id);

        if (user != null) {
            JSONObject obj = new JSONObject();
            obj.put("firstname", user.getFirstName());
            obj.put("lastname", user.getLastName());
            return obj.toString();
        } else {
            return "{ \"exception\": \" This user does not exist \" }";
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/widgets")
    public String getWidgets(@Context HttpServletRequest request) {
        long id = sessions.isLoggedIn(request.getSession());
        Widgets w = dao.getWidget(id);
        if (w != null) {
            return gson.toJson(w);
        } else {
            return new DatabaseException("Widgets", "User has no Widgets set").getAsJSON();
        }
    }

    /**
     *
     * @param request
     * @param widgets
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/widgets")
    public String setWidgets(@Context HttpServletRequest request, String widgets) {
        try {
            long id = sessions.isLoggedIn(request.getSession());
            System.out.println("User Id: " + id);
            Widgets w = gson.fromJson(widgets, Widgets.class);
            dao.setWidget(id, w);
            return "{\"success\": \"true\"}";
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param fbId
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fb/id/{fbid}")
    public String setfbId(@Context HttpServletRequest request, @PathParam("fbid") String fbId) {
        try {
            long id = sessions.isLoggedIn(request.getSession());

            dao.addFacebookId(id, fbId);
            return "{\"success\": \"true\"}";
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param fbId
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("fb/messenger/{fbid}")
    public String setmsngrId(@Context HttpServletRequest request, @PathParam("fbid") String fbId) {
        try {
            long id = sessions.isLoggedIn(request.getSession());

            dao.enableMessengerPermission(id, fbId);
            return "{\"success\": \"true\"}";
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param props
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("properties")
    public String addProperties(@Context HttpServletRequest request, String props) {
        try {
            long id = sessions.isLoggedIn(request.getSession());
            UserProperty prop = gson.fromJson(props, UserProperty.class);
            dao.addUserProperty(id, prop);
            return dao.addUserProperty(id, prop);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("properties")
    public String getProperties(@Context HttpServletRequest request) {
        try {
            long id = sessions.isLoggedIn(request.getSession());
            Set<UserProperty> prop = dao.getProperties(id);
            return gson.toJson(prop);
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param keyString
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("properties/{keyString}")
    public String getProperty(@Context HttpServletRequest request, @PathParam("keyString") String keyString) {
        try {
            long id = sessions.isLoggedIn(request.getSession());
            Set<UserProperty> prop = dao.getProperties(id);
            for (UserProperty p : prop) {
                if (p.getKeyString().equals(keyString)) {
                    return gson.toJson(p);
                }
            }
            return "{}";
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param key
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/properties/{key}")
    public String setNotifications(@Context HttpServletRequest request, @PathParam("key") String key) {
        try {

            long id = sessions.isLoggedIn(request.getSession());

            return dao.removeUserProperty(id, key);
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"False\"}";
        }
    }

    /**
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/notifications")
    public String getNotifications(@Context HttpServletRequest request) {
        long id = sessions.isLoggedIn(request.getSession());
        try {
            Set<NotificationPreferences> set = dao.getNotificationPref(id);
            if (set != null) {
                return gson.toJson(set);
            }
            
            return "{}";
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/notifications")
    public String addNotifications(@Context HttpServletRequest request, String json) {
        long id = sessions.isLoggedIn(request.getSession());
        try {
            NotificationPreferences prefs = gson.fromJson(json, NotificationPreferences.class);

            return dao.addNotificationPref(id, prefs);
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param platform
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/notifications/{platform}")
    public String deleteNotifications(@Context HttpServletRequest request, @PathParam("platform") String platform) {
        try {
            long id = sessions.isLoggedIn(request.getSession());

            dao.deleteNotificationPref(id, platform);
            return "{\"success\": \"true\"}";
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

    /**
     *
     * @param request
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/name")
    public String changeName(@Context HttpServletRequest request, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            long id = sessions.isLoggedIn(request.getSession());
            String value = dao.changeName(id, obj.getString("firstName"), obj.getString("lastName"));
            if(value.equals("true")){
                return "{\"success\": \"true\"}";
            }else{
                return "{\"success\": \"false\"}";
            }
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }
    
    /**
     *
     * @param request
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/password")
    public String changePassword(@Context HttpServletRequest request, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            System.out.println(obj.toString());
            long id = sessions.isLoggedIn(request.getSession());
            String value = dao.changePassword(id, obj.getString("password"));
            if(value.equals("true")){
                return "{\"success\": \"true\"}";
            }else{
                return "{\"success\": \"false\"}";
                
            }
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }
    
    /**
     *
     * @param request
     * @param json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/verifypassword")
    public String verifyPassword(@Context HttpServletRequest request, String json) {
        try {
            JSONObject obj = new JSONObject(json);
            long id = sessions.isLoggedIn(request.getSession());
            String value = dao.verifyPassword(id, obj.getString("password"));
            if(value.equals("true")){
                return "{\"success\": \"true\"}";
            }else{
                return "{\"success\": \"false\"}";
            }
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }
    
    /**
     *
     * @param request
     * @return
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public String deleteUser(@Context HttpServletRequest request) {
        try {
            long id = sessions.isLoggedIn(request.getSession());
            String value = dao.deleteUser(id);
            if(value.equals("true")){
                sessions.logout(request.getSession());
                return "{\"success\": \"true\"}";
            }else{
                return "{\"success\": \"false\"}";
            }
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }
    
    /**
     *
     * @param request
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/checkfbuser")
    public String checkFBUser(@Context HttpServletRequest request) {
        long id = sessions.isLoggedIn(request.getSession());
        try {
            String value = dao.checkFBUser(id);
            if(value.equals("true")){
                return "{\"success\": \"true\"}";
            }else{
                return "{\"success\": \"false\"}";
            }
        } catch (Exception e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "{\"success\": \"false\"}";
        }
    }

}
