/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Api.Loader.HttpsClientBuilder;
import Exceptions.ApiRequestException;
import Model.Delay;
import Model.IApiModel;
import Model.Risk;
import Model.TravelTimes;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.JsonNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Gebruiker
 */
@Stateless
public class CoyoteGetter {

    @EJB
    private PropertyLoaderBean propertyBean;

    @EJB
    private HttpsClientBuilder httpBuilder;
    
    private static final String[] PROP_NAME = {"URLindexCoyote", "URLinfoCoyote", "URLlogoutCoyote"};
    private HttpClient client;
    private ArrayList<String> urls;
    private String login, password, cookie;
    private HttpURLConnection conn;
    private static final String PW_NAME = "loginCoyote";
    private static final String SECRET_NAME = "passwordCoyote";
    private ArrayList<IApiModel> trajectValues;
    private ArrayList<String> routes;

    @PostConstruct
    public void init() {
        try {
            //httpBuilder = new HttpsClientBuilder();
            client = httpBuilder.buildIgnoreSSL(true);
            urls = new ArrayList<>();
            int i = 0;
            for (String s : PROP_NAME) {
                urls.add(propertyBean.getProperty(s));
                i++;
            }
            login = propertyBean.getPassword(PW_NAME);
            password = propertyBean.getPassword(SECRET_NAME);
            routes = new ArrayList<>();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException ex) {
            Logger.getLogger(CoyoteGetter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public List<IApiModel> getDataModel() {
        JsonNode jn;
        routes = new ArrayList<>();
        try {
            jn = new JsonNode(getRawData());
            List<IApiModel> delays = createDelays(jn.getObject());
            List<IApiModel> risks = createRisks(jn.getObject());
            for (int i = 0; i < risks.size(); i++) {
                delays.add(risks.get(i));
            }
            return delays;
        } catch (IOException ex) {
            return new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage()).getModelList();
        } catch (ApiRequestException ex) {
            Logger.getLogger(CoyoteGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        }

    }

    /**
     *
     * @return @throws ApiRequestException
     */
    public String getRawData() throws ApiRequestException {

        try {
            //making post request (authentication with x-www-form-encoded)
            loginPost();
            //setting cookie for the subsequent requests
            
            //second request: getting access to page with information from Ghent (Gand)
            //JSONObject jObject = getInformation();
            //JsonNode js = new JsonNode(jObject.toString());
            JsonNode jn = getInformation();
            //log out
            //logOut();
            //System.out.println(jn.toString());
            return jn.toString();

        } catch (IOException ex) {
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public void loginPost() throws MalformedURLException, IOException {
        //URL url = new URL(urls.get(0));
        List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("login", login));
		urlParameters.add(new BasicNameValuePair("password", password));
        HttpPost loginPost = new HttpPost(urls.get(0));
        loginPost.setEntity(new UrlEncodedFormEntity(urlParameters));
        loginPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        loginPost.addHeader("charset", "utf-8");
        HttpResponse response = client.execute(loginPost);
        
        //CookieStore cookieStore = client.ge;
        // get Cookies
        //List<Cookie> cookies = cookieStore.getCookies();
        BufferedReader br = new BufferedReader(
                         new InputStreamReader((response.getEntity().getContent())));
        
        EntityUtils.consumeQuietly(response.getEntity());
        /*conn = (HttpURLConnection) url.openConnection();
        
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setUseCaches(false);
        conn.setDoInput(true);
        try (DataOutputStream output = new DataOutputStream(conn.getOutputStream())) {
            output.writeBytes(urlParameters);
        }*/
    }

   
    public JsonNode getInformation() throws MalformedURLException, ProtocolException, IOException {
        HttpGet get = new HttpGet(urls.get(1));
        
        

        HttpResponse response = client.execute(get);
        //reading answers body from get request --> get with the whole json information
        
        
        BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
                
                EntityUtils.consumeQuietly(response.getEntity());
            return new JsonNode(result.toString());

    }

    public List<IApiModel> createDelays(JSONObject jObject) throws ProtocolException, IOException {
        List<IApiModel> delays = new ArrayList<>();
        trajectValues = new ArrayList<>();
        double normalTime_clockwise = 0;
        double realTime_clockwise = 0;
        double normalTime_antiClockwise = 0;
        double realTime_antiClockwise = 0;
        double length_clockwise = 0;
        double length_antiClockwise = 0;
        TravelTimes tt = new TravelTimes();

        CharSequence cs = "(R40)";
        //clockwise cycle:
        CharSequence cs1 = "Rooigemlaan (R40) Northbound";
        CharSequence cs2 = "Heernislaan (R40) Southbound";
        CharSequence cs3 = "Dok-Noord (R40) Southbound";
        CharSequence cs4 = "Martelaarslaan (R40) Northbound";
        CharSequence cs5 = "Gasmeterlaan (R40) Eastbound";

        //JSONObject alertInfo = jObject.getJSONObject("alerts"); //when you want to use information from this type: eventually future use
        JSONObject ghentInfo = jObject.getJSONObject("Gand");
        for (Object key : ghentInfo.keySet()) {
            //based on key types
            String route = (String) key;
            routes.add(route);
            JSONObject keyvalue = (JSONObject) ghentInfo.get(route);
            double normal = (keyvalue.getDouble("normal_time")) / 60;
            double real = keyvalue.getDouble("real_time") / 60;
            double length = keyvalue.getDouble("length") / 1000; //omgezet naar km

            Delay d = new Delay(route, (int) Math.round(keyvalue.getDouble("diff_time") / 60), normal, real, length);

            try {
                JSONObject a = keyvalue.getJSONObject("alerts");

                d.setAlerts_on_route(getRisksHelp(a));

            } catch (JSONException e) {
                //no alerts on this traject
            }
            //JSONObject a = keyvalue.getJSONObject("alerts");

            //
            /*
            JSONObject a = keyvalue.getJSONObject("alerts");
                for (Object ke : a.keySet()) {
                    String r = (String) ke;
                    JSONObject value = (JSONObject) a.getJSONObject(r);
                    Risk risk = new Risk();
                    risk.setStreet(value.getString("road_name"));
                    risk.setSpeedLimit(value.getInt("speed_limit"));
                    risk.setLatitude(value.getDouble("lat"));
                    risk.setLongitude(value.getDouble("lng"));
                    risk.setTimestamp(value.getString("first_declaration"));
                    String subtype = value.getString("type_lbl").replaceAll(" ", "_").toLowerCase();
                    try {
                        risk.setSubtype(labels.getString(subtype));
                    } catch (MissingResourceException e) {
                        // System.out.println("subtype niet in local files");
                        risk.setSubtype(value.getString("type_lbl"));
                    }

                    risk.setType("Coyote alert");
                    risks.add(risk);
                }*/
 /* if (d.getMinutes() >= 5) {
                delays.add(d);
            }*/
            delays.add(d);

            if ((d.getRoute()).contains(cs)) {

                if ((d.getRoute()).contains(cs1) || (d.getRoute()).contains(cs2) || (d.getRoute()).contains(cs3) || (d.getRoute()).contains(cs4) || (d.getRoute()).contains(cs5)) {
                    normalTime_clockwise += d.getNormal_time();
                    realTime_clockwise += d.getReal_time();
                    length_clockwise += d.getLength();
                } else {
                    normalTime_antiClockwise += d.getNormal_time();
                    realTime_antiClockwise += d.getReal_time();
                    length_antiClockwise += d.getLength();
                }

            }
        }
        tt.setLength_antiClockwise(length_antiClockwise);
        tt.setLength_clockwise(length_clockwise);
        tt.setNormalTime_antiClockwise(normalTime_antiClockwise);
        tt.setNormalTime_clockwise(normalTime_clockwise);
        tt.setRealTime_antiClockwise(realTime_antiClockwise);
        tt.setRealTime_clockwise(realTime_clockwise);
        trajectValues.add(tt);
        return delays;
    }

    public ArrayList<String> getRoutes() {
        return routes;
    }

    public List<IApiModel> getTrajectValues() {
        //returns the values
        return trajectValues;
    }

    public List<IApiModel> createRisks(JSONObject jObject) {
        Locale locale = new Locale("nl");
        Locale.setDefault(locale);
        ResourceBundle labels = ResourceBundle.getBundle("Properties.Languages.LabelsBundle", Locale.getDefault());
        List<IApiModel> risks = new ArrayList<>();
        JSONObject alertInfo = jObject.getJSONObject("alerts");

        JSONObject ghentInfo = jObject.getJSONObject("Gand");
        for (Object k : ghentInfo.keySet()) {
            //based on key types
            String route = (String) k;

            JSONObject keyvalue = (JSONObject) ghentInfo.get(route);

            try {
                JSONObject a = keyvalue.getJSONObject("alerts");
                for (Risk r : getRisksHelp(a)) {
                    risks.add(r);
                }

            } catch (JSONException e) {
                //no alerts on this traject
            }
        }
        /*
        Iterator<?> keys = alertInfo.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (alertInfo.get(key) instanceof JSONObject) {
                JSONObject hulp = (JSONObject) alertInfo.get(key);
                Risk r = new Risk();
                r.setStreet(hulp.getString("road_name"));
                r.setSpeedLimit(hulp.getInt("speed_limit"));
                r.setLatitude(hulp.getDouble("lat"));
                r.setLongitude(hulp.getDouble("lng"));
                r.setTimestamp(hulp.getString("first_declaration"));
                String subtype = hulp.getString("type_lbl").replaceAll(" ", "_").toLowerCase();
                try {
                    r.setSubtype(labels.getString(subtype));
                } catch (MissingResourceException e) {
                    // System.out.println("subtype niet in local files");
                    r.setSubtype(hulp.getString("type_lbl"));
                }

                r.setType("Coyote alert");
                risks.add(r);
            }

        }*/
        try {
            for (Risk r : getRisksHelp(alertInfo)) {
                risks.add(r);
            }

        } catch (JSONException e) {
            //no alerts on this traject
        }

        return risks;
    }

    //hulpfunctie om duplicate code te vermijden
    private List<Risk> getRisksHelp(JSONObject a) {
        List<Risk> riskjes = new ArrayList<>();
        Locale locale = new Locale("nl");
        Locale.setDefault(locale);
        ResourceBundle labels = ResourceBundle.getBundle("Properties.Languages.LabelsBundle", Locale.getDefault());
        try {
            for (Object ke : a.keySet()) {
                String r = (String) ke;
                JSONObject value = (JSONObject) a.getJSONObject(r);
                Risk risk = new Risk();
                risk.setStreet(value.getString("road_name"));
                risk.setSpeedLimit(value.getInt("speed_limit"));
                risk.setLatitude(value.getDouble("lat"));
                risk.setLongitude(value.getDouble("lng"));
                risk.setTimestamp(value.getString("first_declaration"));
                String subtype = value.getString("type_lbl").replaceAll(" ", "_").toLowerCase();
                try {
                    risk.setSubtype(labels.getString(subtype));
                } catch (MissingResourceException e) {
                    // System.out.println("subtype niet in local files");
                    risk.setSubtype(value.getString("type_lbl"));
                }

                risk.setType("Coyote alert");
                riskjes.add(risk);
            }
        } catch (JSONException e) {
            //no alerts on this traject
        }
        return riskjes;
    }

    public void logOut() throws MalformedURLException, IOException {
       // client.
    }

}
