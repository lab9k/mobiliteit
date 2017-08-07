/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Rest;

import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author ruben
 */
@Path("facebook")
public class FacebookRest {

    @Context
    private UriInfo context;

    @EJB
    private PropertyLoaderBean prop;
    
    /**
     * Creates a new instance of FacebookRest
     */
    public FacebookRest() {
    }

    /**
     *  Facebook sends periodic gets request to the webhook, this method returns the correct value so facebook will still recognise the server as active
     *
     * 
     * 
     * @param mode
     * @param challenge
     * @param token
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson(@QueryParam("hub.mode") String mode,
            @QueryParam("hub.challenge") String challenge,
            @QueryParam("hub.verify_token") String token) {
        if (mode.equals("subscribe") && token.equals("mobi02")) {
            return challenge;
        }
        return "error";
    }

    /**
     *
     * @param redirect
     * @param token
     * @return
     */
    @GET
    @Path("/link")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLink(@QueryParam("redirect_uri") String redirect,
            @QueryParam("account_linking_token") String token) {
        try {
            String url = prop.getProperty("FbSendApi")
                    + "&fields=recipient"
                    + "&account_linking_token=" + token;
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
            System.out.println(jsonResponse.getBody().toString());
            return jsonResponse.getBody().toString();
        } catch (UnirestException ex) {
            Logger.getLogger(FacebookRest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * PUT method for updating or creating an instance of FacebookRest
     *
     * @param content representation for the resource
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putJson(String content) {
        try {

            JSONObject object = new JSONObject(content);
            if (object.getString("object").equals("page")) {
                JSONArray arr = object.getJSONArray("entry");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    JSONArray mess = o.getJSONArray("messaging");
                    for (int j = 0; j < mess.length(); j++) {
                        if (mess.getJSONObject(j).has("optin")) {
                            sendLoginButton(mess.getJSONObject(j).getJSONObject("sender").getString("id"));
                        } else {
                            String sender = mess.getJSONObject(j).getJSONObject("sender").getString("id");
                            String message = mess.getJSONObject(j).getJSONObject("message").getString("text");
                            sendMessage(sender, "Je zei: " + message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Request: " + content);
            return Response.ok().build();
        }

    }

    /**
     *
     * @param userId
     * @param message
     * @return
     */
    public String sendMessage(String userId, String message) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://graph.facebook.com/v2.6/me/messages?access_token=EAALZA96ZBdjjwBAMm5fZAD3bIP6jspQhkBCZCZBdlcLHMixecV9ZBtzJHURUhx5EAN5w9i679jZBawE27nEZCvMrHDBiYCxcrGupDB92CjdFRcaC7CPE6yvNXUkwJT1omTqzQTR1g6e7MrOgAZApiX3y28vgDdGZA8Cru58HXTZC2PqZBQZDZD")
                    .header("Content-Type", "application/json")
                    .body("{"
                            + "  \"recipient\": {"
                            + "    \"id\": \"" + userId + "\""
                            + "  },"
                            + "  \"message\": {"
                            + "    \"text\": \"" + message + "\""
                            + "  }"
                            + "}").asJson();
            return jsonResponse.getBody().toString();
        } catch (UnirestException ex) {
            Logger.getLogger(FacebookRest.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public String sendLoginButton(String userId) {

        try {
            String body = "{\n"
                    + "  \"recipient\":{\n"
                    + "    \"id\":\"" + userId + " \"\n"
                    + "  },\n"
                    + "  \"message\":{\n"
                    + "    \"attachment\":{\n"
                    + "      \"type\":\"template\",\n"
                    + "      \"payload\":{\n"
                    + "        \"template_type\":\"button\",\n"
                    + "        \"text\":\"Met deze knop start je het identificatieproces. Hierdoor kunnen we je gepersonaliseerde berichten sturen via Messenger. Let op: dit proces mislukt bij het ingeven van een fout wachtwoord.\",\n"
                    + "         \"buttons\":[\n"
                    + "    	 {\n"
                    + "         \"type\": \"account_link\",\n"
                    + "         \"url\": \"https://mobi-02.project.tiwi.be:8181/messenger.html\"\n"
                    + "    		}\n"
                    + "    	]\n"
                    + "        \n"
                    + "      }\n"
                    + "    }\n"
                    + "  }\n"
                    + "}";

            HttpResponse<JsonNode> jsonResponse = Unirest.post("https://graph.facebook.com/v2.6/me/messages?access_token=EAALZA96ZBdjjwBAMm5fZAD3bIP6jspQhkBCZCZBdlcLHMixecV9ZBtzJHURUhx5EAN5w9i679jZBawE27nEZCvMrHDBiYCxcrGupDB92CjdFRcaC7CPE6yvNXUkwJT1omTqzQTR1g6e7MrOgAZApiX3y28vgDdGZA8Cru58HXTZC2PqZBQZDZD")
                    .header("Content-Type", "application/json")
                    .body(body).asJson();

            return jsonResponse.getBody().toString();
        } catch (UnirestException ex) {
            Logger.getLogger(FacebookRest.class.getName()).log(Level.SEVERE, null, ex);
            return "Error";
        }
    }
}
