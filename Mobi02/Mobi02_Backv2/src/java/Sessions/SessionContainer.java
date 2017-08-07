/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Sessions;

import Database.Entities.PersonalUser;
import java.util.HashMap;
import java.util.HashSet;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ruben
 */
@Singleton(name = "sessions")
public class SessionContainer {

    private HashMap<HttpSession, Long> sessions;
    
    /**
     *
     */
    @PostConstruct
    public void init(){
        sessions = new HashMap<>();
    }
    
    /**
     *
     * @param ses
     * @param userId
     */
    public void login(HttpSession ses, Long userId){
        sessions.put(ses,userId);
    }

    /**
     *
     * @param ses
     * @return
     */
    public long isLoggedIn(HttpSession ses){
        if(sessions.containsKey(ses)){
            return sessions.get(ses);
        }
        else{
            return -1;
        }
    }
    
    /**
     *
     * @param ses
     */
    public void logout(HttpSession ses){
        sessions.remove(ses);
    }
    
}
