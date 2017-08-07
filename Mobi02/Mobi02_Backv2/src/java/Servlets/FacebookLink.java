/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import Database.Dao.UserDao;
import Sessions.SessionContainer;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to handle facebook account linking. Still not optimal. The hardcoded url should be replaced in a production environment
 * 
 * 
 * @author ruben
 */
public class FacebookLink extends HttpServlet {

    @EJB
    private SessionContainer sessions;

    @EJB
    private UserDao dao;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String facebookId = request.getParameter("fb_id");
            String username = request.getParameter("username");
            String userpass = request.getParameter("userpass");
            String token = request.getParameter("token");
            String redirect = request.getParameter("redirect");
            /*for (String[] ss : request.getParameterMap().values()){
                for( String s: ss){
                    System.out.println(s);
                }
            }*/

            System.out.println("facebookId: " + facebookId + " username: " + username + " userpass: " + userpass + " token: " + token + " redirect: " + redirect);
            String url = "https://graph.facebook.com/v2.6/me?access_token=EAALZA96ZBdjjwBAMm5fZAD3bIP6jspQhkBCZCZBdlcLHMixecV9ZBtzJHURUhx5EAN5w9i679jZBawE27nEZCvMrHDBiYCxcrGupDB92CjdFRcaC7CPE6yvNXUkwJT1omTqzQTR1g6e7MrOgAZApiX3y28vgDdGZA8Cru58HXTZC2PqZBQZDZD"
                    + "&fields=recipient"
                    + "&account_linking_token=" + token;
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
            String id = jsonResponse.getBody().getObject().getString("recipient");
            if (facebookId != null && !facebookId.isEmpty()) {
                if(dao.setMessengerId(facebookId,null, id, true))
                    redirect += "&authorization_code=fb";
            } else {
                if(dao.setMessengerId(username,userpass, id, false))
                redirect += "&authorization_code=acc";

            }
            response.sendRedirect(redirect);
        } catch (Exception ex) {
            Logger.getLogger(FacebookLink.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (sessions.isLoggedIn(request.getSession()) > 0) {
            processRequest(request, response);
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
