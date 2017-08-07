/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Train;
import Model.TrainRoute;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author ruben
 */
@Stateless
@LocalBean
public class TrainRouteGetter {

    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String[] PROP_NAME = {"URLTrainsRoute", "TrainFromQuery", "TrainToQuery", "TrainDateQuery", "TrainTimeQuery", "TrainTimeSelQuery"};
    private String url;

    @PostConstruct
    public void init() {
        url = propertyBean.getProperty(PROP_NAME[0]);
    }

    public List<IApiModel> createTrains(String data) throws ApiRequestException {
        List<IApiModel> models = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();

            //Document doc = db.parse(new URL(url).openStream());
            InputSource is = new InputSource(new StringReader(data));
            Document doc = db.parse(is);
            //String station = doc.getDocumentElement().getElementsByTagName("station").item(0).getAttributes().getNamedItem("standardname").getNodeValue();

            Node connections = doc.getDocumentElement();
            NodeList connectionList = connections.getChildNodes();

            // System.out.println(alldepartures.getLength());
            for (int i = 0; i < connectionList.getLength(); i++) {
                //System.out.println("Ik ga in de loop");
                NodeList connection = connectionList.item(i).getChildNodes();
                //System.out.println(connection);
                TrainRoute conn = new TrainRoute();
                String name = "";
                for (int j = 0; j < connection.getLength(); j++) {
                    Train t = new Train();
                    Node currentConnection = connection.item(j);
                    name = currentConnection.getNodeName();
                    if (name.equals("duration")) {
                        //System.out.println("Setting total travel time");
                        conn.setTotalTravelTime(currentConnection.getNodeValue());
                    } else if (name.equals("arrival") || name.equals("departure")) {
                        NodeList properties = currentConnection.getChildNodes();
                        //System.out.println(name);

                        t.setRouteType(name);
                        //t.setDelay(Integer.parseInt(currentConnection.getAttributes().getNamedItem("delay").getNodeValue()));
                        for (int k = 0; k < properties.getLength(); k++) {
                            try {
                                switch (properties.item(k).getNodeName()) {
                                    case "station":
                                       Element elem = (Element) properties.item(k);
                                        t.setStation(elem.getAttribute("standardname"));
                                        if(t.getStation() == null)
                                        t.setStation(properties.item(k).getFirstChild().getNodeValue());
                                        break;
                                    case "direction":
                                        t.setDest(properties.item(k).getFirstChild().getNodeValue());
                                        break;
                                    case "time":
                                        t.setDeptime(Integer.parseInt(properties.item(k).getFirstChild().getNodeValue()));
                                        break;
                                    case "vehicle":
                                        t.setVehicle(properties.item(k).getFirstChild().getNodeValue());
                                        break;
                                    case "platform":
                                        //System.out.println("Properties:" + name +" " +properties.item(k));
                                        Node n = properties.item(k).getFirstChild();

                                        String platform = n.getNodeValue();
                                        if (platform != null) {
                                            t.setPlatform(properties.item(k).getFirstChild().getNodeValue());
                                        }
                                        break;

                                }
                            } catch (NullPointerException ex) {
                            }
                        }
                        conn.add(t);
                    } else if (name.equals("vias")) {
                        NodeList vias = currentConnection.getChildNodes();
                            
                        for(int k = 0; k < vias.getLength(); k++){
                            Train tr = new Train();
                            
                            NodeList currentVia = vias.item(k).getChildNodes();
                            for(int l = 0; l < currentVia.getLength(); l++){
                                Node currentNode = currentVia.item(l);
                                if(currentNode.getNodeName().equals("departure")){
                                    NodeList departure = currentNode.getChildNodes();
                                    for(int m = 0; m < departure.getLength(); m++){
                                        Node curr = departure.item(m);
                                        switch (curr.getNodeName()){
                                            case "time":
                                                tr.setDeptime(Integer.parseInt(curr.getFirstChild().getNodeValue()));
                                                break;
                                            case "platform":
                                                tr.setPlatform(curr.getFirstChild().getNodeValue());
                                                break;                                                
                                        } 
                                    }
                                }
                                else if(currentNode.getNodeName().equals("station")){
                                    Element el = (Element) currentNode;
                                    tr.setVehicle(el.getAttribute("id"));
                                    tr.setStation(el.getAttribute("standardname"));
                                    if(tr.getStation() == null)
                                        tr.setStation(currentNode.getFirstChild().getNodeValue());
                                }                                
                                else if(currentNode.getNodeName().equals("direction")){
                                    tr.setDest(currentNode.getFirstChild().getNodeValue());
                                }
                        }
                            tr.setRouteType("Via" + k);
                        conn.add(tr);
                        }
                    }
                }
                
                if(conn.size() > 2){
                    List<Train> tr = conn.subList(2, conn.size());
                    //de destination van de volgende via, is eigenlijk de destination van de vorige
                    for(int j = 0; j < (tr.size() -1) ;j++){
                        tr.get(j).setDest(tr.get(j+1).getDest());
                    }
                    //2e index is de aankomst, die heeft als bestemming de bestemming van de laatste overstaptrein
                    conn.get(conn.size() - 1).setDest(conn.get(1).getDest());
                }
                
                models.add(conn);
            }
        } catch (Exception ex) {
            Logger.getLogger(TrainGetter.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }
        return models;
    }

    public List<IApiModel> getDataModel(String from, String to, String date, String time, String timesel) {
        HashMap<String, String> params = new HashMap<>();
        params.put(propertyBean.getProperty(PROP_NAME[1]), from);
        params.put(propertyBean.getProperty(PROP_NAME[2]), to);
        if (date != null) {
            params.put(propertyBean.getProperty(PROP_NAME[3]), date);
        }
        if (time != null) {
            params.put(propertyBean.getProperty(PROP_NAME[4]), time);
        }
        if (timesel != null) {
            params.put(propertyBean.getProperty(PROP_NAME[5]), timesel);
        }
        try {
            String data = getRawData(url, params);

            return createTrains(data);
        } catch (ApiRequestException ex) {
            Logger.getLogger(TrainGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        }
    }

    public String getRawData(String url, HashMap<String, String> params) throws ApiRequestException {
        try {
            for (String key : params.keySet()) {
                url += key + params.get(key);
            }
            System.out.println("Train Route url: " + url);
            HttpResponse<String> response = Unirest.get(url).asString();
            String resp = response.getBody();
            //System.out.println(resp);
            return resp;
        } catch (UnirestException ex) {
            //System.out.println("URL: " + url);
            // Logger.getLogger(TrainsGhent.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

}
