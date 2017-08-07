/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Getters;

import Exceptions.ApiRequestException;
import Model.IApiModel;
import Model.Train;
import Properties.PropertyLoaderBean;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author ruben
 */
@Stateless
public class TrainGetter{

    
    @EJB
    private PropertyLoaderBean propertyBean;

    private static final String[] PROP_NAME = {"URL1TrainsGhent", "URL2TrainsGhent"};
    private ArrayList<String> urls;
    
    @PostConstruct
    public void init() {
        urls = new ArrayList<>();
        for(String s : PROP_NAME){
            urls.add(propertyBean.getProperty(s));
        }
    }
    
    public void createTrains(String url, List<IApiModel> trains) throws ApiRequestException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();

            //Document doc = db.parse(new URL(url).openStream());
            InputSource is = new InputSource(new StringReader(getRawData(url)));
            Document doc = db.parse(is);
            String station = doc.getDocumentElement().getElementsByTagName("station").item(0).getAttributes().getNamedItem("standardname").getNodeValue();

            NodeList alldepartures = doc.getDocumentElement().getElementsByTagName("departures");

           // System.out.println(alldepartures.getLength());
            NodeList departures = alldepartures.item(0).getChildNodes();
            for (int i = 0; i < departures.getLength(); i++) {
                Node departure = departures.item(i);
                NodeList properties = departure.getChildNodes();
                Train t = new Train();
                t.setDelay(Integer.parseInt(departure.getAttributes().getNamedItem("delay").getNodeValue()));
                for (int j = 0; j < properties.getLength(); j++) {

                    switch (properties.item(j).getNodeName()) {
                        case "station":
                            t.setDest(properties.item(j).getFirstChild().getNodeValue());
                            break;
                        case "time":
                            t.setDeptime(Integer.parseInt(properties.item(j).getFirstChild().getNodeValue()));
                            break;
                        case "vehicle":
                            t.setVehicle(properties.item(j).getFirstChild().getNodeValue());
                            break;
                        case "platform":
                            t.setPlatform(properties.item(j).getFirstChild().getNodeValue());
                            break;
                    }
                    t.setStation(station);
                }
                //System.out.println(t.toString());
                trains.add(t);
            }
        } catch (Exception ex) {
            throw new ApiRequestException(this.getClass().getSimpleName(),ex.getMessage());
        }

    }

    public List<IApiModel> getDataModel(){
        try {
            List<IApiModel> trains = new ArrayList<>();
            createTrains(urls.get(0), trains);
            createTrains(urls.get(1), trains);
            Collections.sort(trains, new Comparator<IApiModel>() {
                @Override
                public int compare(IApiModel o1, IApiModel o2) {
                    Train t1 = (Train) o1;
                    Train t2 = (Train) o2;
                    return t1.getDeptime() - t2.getDeptime();
                }
            });
            return trains;
        } catch (ApiRequestException ex) {
            //Logger.getLogger(TrainGetter.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getModelList();
        }
    }

    public String getRawData(String url) throws ApiRequestException {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            return response.getBody();
        } catch (UnirestException ex) {
            //System.out.println("URL: " + url);
           // Logger.getLogger(TrainsGhent.class.getName()).log(Level.SEVERE, null, ex);
            throw new ApiRequestException(this.getClass().getSimpleName(), ex.getMessage());
        }
    }

    public String getRawData() throws ApiRequestException {
        
        String result = "";
        for (int i = 0; i < urls.size(); i++) {

            result += getRawData(urls.get(i)) + ((i == (urls.size() - 1)) ? "" : ",");
        }
        System.out.println(result);
        return result;
    }
}
