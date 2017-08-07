/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database.Dao;

import Database.Entities.FacebookUserInfo;
import Database.Entities.NotificationPreferences;
import Database.Entities.PersonalUser;
import Database.Entities.UserProperty;
import Database.Entities.Widgets;
import Database.Exceptions.FacebookException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author ruben
 * 
 * Calls related to users, most will return a json string for easy verification in the front
 * 
 */
@Stateless
public class UserDao {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "Mobi02PU")
    private EntityManager em;

    /**
     *
     * @param user
     * @return
     */
    public String addUser(PersonalUser user) {
        try {
            if (checkEmail(user.getEmail())) {
                em.persist(user);
                return "{\"success\": \"True\", \"message\": \"User added succesfully\"}";
            } else {
                return "{\"success\": \"False\", \"message\": \"User creation failed: email exists already!\"}";
            }
        } catch (PersistenceException ex) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",ex);
            return "{\"message\": \"User creation failed\"}";
        }
    }

    /**
     * Check if a user exists with the given parameters and returns its id, returns -1 when no user is found
     *
     * @param name
     * @param pass
     * @return long
     */
    public long findUser(String name, String pass) {
        Query query = em.createQuery("Select p from PersonalUser p where p.email = :name and p.password = :pass");
        query.setParameter("name", name);
        query.setParameter("pass", pass);
        try {
            Object u = query.getSingleResult();
            return ((PersonalUser) u).getId();
        } catch (javax.persistence.NoResultException e) {
            //Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return -1;
        }
    }

    /**
     *
     * @param userid
     * @param firstName
     * @param lastName
     * @return
     */
    public String changeName(long userid, String firstName, String lastName) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        try {
            if (!firstName.equals("")) {
                user.setFirstName(firstName);
            }
            if (!lastName.equals("")) {
                user.setLastName(lastName);
            }
            return "true";
        } catch (javax.persistence.NoResultException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "false";
        }

    }

    /**
     *
     * @param userid
     * @param password
     * @return
     */
    public String changePassword(long userid, String password) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        try {
            if (!password.equals("")) {
                user.setPassword(password);
                return "true";
            } else {
                return "false";
            }

        } catch (javax.persistence.NoResultException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "false";
        }

    }
    
    /**
     *
     * @param userid
     * @param password
     * @return
     */
    public String verifyPassword(long userid, String password) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        try {
            if (password.equals(user.getPassword())) {
                return "true";
            } else {
                return "false";
            }

        } catch (javax.persistence.NoResultException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "false";
        }

    }
    
    /**
     *
     * @param userid
     * @return
     */
    public String deleteUser(long userid) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        try {
            em.remove(user);
            return "true";

        } catch (javax.persistence.NoResultException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "false";
        }

    }
    
    /**
     *
     * @param userid
     * @return
     */
    public String checkFBUser(long userid) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        try {
            if (user.getEmail()== null) {
                return "true";
            } else {
                return "false";
            }

        } catch (javax.persistence.NoResultException e) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",e);
            return "false";
        }

    }

    /**
     *
     * @param email
     * @return
     */
    public boolean checkEmail(String email) {
        Query query = em.createQuery("Select p from PersonalUser p where p.email = :email");
        query.setParameter("email", email);
        List list = query.getResultList();
        return list.isEmpty();
    }

    /**
     *
     * @param id
     * @return
     */
    public PersonalUser getUser(long id) {
        return em.find(PersonalUser.class, id);
    }

    /**
     *
     * @param userId
     * @return
     */
    public Widgets getWidget(long userId) {
        try {
            Query q = em.createQuery("select p.widgets from PersonalUser p where p.id = :id");
            q.setParameter("id", userId);
            List<Widgets> l = q.getResultList();
            if (!l.isEmpty()) {
                return l.get(0);
            } else {
                return null;
            }
        } catch (Exception ex) {
            Logger.getLogger((this.getClass().getSimpleName())).log(Level.WARNING,"Exceptie",ex);
            return null;
        }
    }

    /**
     *
     * @param userid
     * @param widget
     */
    public void setWidget(long userid, Widgets widget) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        if (user != null) {
            if (user.getWidgets() != null) {
                Widgets old = user.getWidgets();
                em.remove(old);
            }
            user.setWidgets(widget);
        }
        //em.(user);
    }

    /**
     *
     * @param userid
     * @param pageId
     */
    public void addFacebookId(long userid, String pageId) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        if (user != null) {
            FacebookUserInfo info = user.getFacebookInfo();
            if (info == null) {
                info = new FacebookUserInfo();
                info.setHasSendPermission(false);
            }
            info.setPageId(pageId);
            user.setFacebookInfo(info);
        }
    }

    //needs change

    /**
     *
     * @param userid
     * @param messengerId
     * @return
     */
    public String enableMessengerPermission(long userid, String messengerId) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        if (user != null) {
            FacebookUserInfo info = user.getFacebookInfo();
            if (info != null && info.getPageId() != null) {
                info.setHasSendPermission(true);
                info.setMessengerId(messengerId);
                return CommonResponses.succesJSON(true);
            } else {
                return new FacebookException("Facebook pageid not set").getAsJSON();
            }
        } else {
            return new FacebookException("Trying to give permission to unlinked account").getAsJSON();
        }
    }

    /**
     *
     * @param id
     * @param password
     * @param messengerId
     * @param isFacebook
     * @return
     */
    public boolean setMessengerId(String id,String password, String messengerId, boolean isFacebook){
        try{
        Query q = null;
        if(isFacebook){
            q = em.createQuery("select p from PersonalUser p where p.facebookInfo.pageId = :id");
        }
        else{
            q = em.createQuery("select p from PersonalUser p where p.email = :id and p.password = :password");
            q.setParameter("password", password);
        }
            System.out.println(isFacebook + " " + id);
        q.setParameter("id", id);
        
        PersonalUser p = (PersonalUser) q.getResultList().get(0);
        FacebookUserInfo fInfo = p.getFacebookInfo();
        if(fInfo == null){
            fInfo = new FacebookUserInfo();
        }
        fInfo.setMessengerId(messengerId);
        p.setFacebookInfo(fInfo);
        em.remove(p);
        em.persist(p);
        return true;
        }catch(Exception ex ){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Exception:", ex);
            return false;
        }
    }
    
    /**
     *
     * @param pageId
     * @param firstName
     * @param lastName
     * @return
     */
    public String registerFacebook(String pageId, String firstName, String lastName) {
        Query q = em.createQuery("select p from PersonalUser p where p.facebookInfo.pageId = :pageId");
        q.setParameter("pageId", pageId);
        List<PersonalUser> users = q.getResultList();
        if (users.isEmpty()) {
            PersonalUser entity = new PersonalUser();
            entity.setFirstName(firstName);
            entity.setLastName(lastName);
            FacebookUserInfo fbinfo = new FacebookUserInfo();
            fbinfo.setPageId(pageId);
            fbinfo.setHasSendPermission(false);
            entity.setFacebookInfo(fbinfo);
            em.persist(entity);
        }
        return CommonResponses.succesJSON(true);
    }

    /**
     *
     * @param pageId
     * @return
     */
    public long loginWithFacebook(String pageId) {
        Query q = em.createQuery("select p from PersonalUser p where p.facebookInfo.pageId = :pageId");
        q.setParameter("pageId", pageId);
        List<PersonalUser> users = q.getResultList();
        if (users.isEmpty()) {
            return -1;
        } else {
            return users.get(0).getId();
        }
    }

    /**
     *
     * @param userid
     * @param entity
     * @return
     */
    public String addNotificationPref(long userid, NotificationPreferences entity) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        if (user != null) {
            Set<NotificationPreferences> prefs = user.getNotification();
            if (prefs == null) {
                prefs = new HashSet<>();
            }
            prefs.add(entity);
            user.setNotification(prefs);
            return CommonResponses.succesJSON(true);
        } else {
            return CommonResponses.genericErrorJson("There is no user with that id");
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public Set<NotificationPreferences> getNotificationPref(long userId) {
        PersonalUser user = em.find(PersonalUser.class, userId);
        if (user != null) {
            return user.getNotification();
        }
        return null;
    }

    /**
     *
     * @param userid
     * @param platform
     * @return
     */
    public String deleteNotificationPref(long userid, String platform) {
        PersonalUser user = em.find(PersonalUser.class, userid);
        if (user != null) {
            Set<NotificationPreferences> prefs = user.getNotification();
            if (prefs != null && !prefs.isEmpty()) {
                ArrayList<NotificationPreferences> toRemove = new ArrayList<>();
                for (NotificationPreferences pref : prefs) {
                    if (pref.getPlatform().equals(platform)) {
                        toRemove.add(pref);
                    }
                }
                for (int i = 0; i < toRemove.size(); i++) {
                    prefs.remove(toRemove.get(i));
                    em.remove(toRemove.get(i));
                }

            }
            return CommonResponses.succesJSON(true);
        } else {
            return CommonResponses.genericErrorJson("There is no user with that id");
        }
    }

    /**
     *
     * @param time
     * @return
     */
    public List<PersonalUser> getSubscribedUsers(int time) {
        Query q = em.createQuery("select p from PersonalUser p where :hour = ANY(select n.sendHour from p.notification n)");
        q.setParameter("hour", time);
        return q.getResultList();
    }

    /**
     *
     * @param id
     * @param prop
     * @return
     */
    public String addUserProperty(long id, UserProperty prop) {
        PersonalUser user = em.find(PersonalUser.class, id);
        if (user != null) {
            //remove the property before adding one with the same key
            removeUserProperty(id, prop.getKeyString());
            user.addProperty(prop);
            return CommonResponses.succesJSON(true);
        } else {
            return CommonResponses.succesJSON(false);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public Set<UserProperty> getProperties(long id) {
        PersonalUser user = em.find(PersonalUser.class, id);
        if (user != null) {
            return user.getProperties();
        } else {
            return null;
        }
    }

    /**
     *
     * @param id
     * @param key
     * @return
     */
    public String removeUserProperty(long id, String key) {
        PersonalUser user = em.find(PersonalUser.class, id);
        if (user != null && user.getProperties() != null) {
            UserProperty toRemove = null;
            for (UserProperty prop : user.getProperties()) {
                if (prop.getKeyString().equals(key)) {
                    toRemove = prop;
                    break;
                }
            }
            if (toRemove != null) {
                user.getProperties().remove(toRemove);
                em.remove(toRemove);
            }

            return CommonResponses.succesJSON(true);
        } else {
            return CommonResponses.succesJSON(false);
        }
    }

}
