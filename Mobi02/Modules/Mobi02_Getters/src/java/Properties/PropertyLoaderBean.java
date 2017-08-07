/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Properties;

import Api.ApiType;
import Exceptions.ApiNotFoundException;
import Exceptions.ApiRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author ruben
 */
@Singleton(name="properties")
@Startup
public class PropertyLoaderBean {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    Properties prop;
    Properties passwords;
    Properties parkings;
    Properties rest;
    private static final String PROP_FILE = "Properties/config.properties", PW_FILE ="Properties/password.properties";
    private static final String PARK_FILE = "Properties/parkings.properties";
    private static final String REST_FILE = "Properties/rest.properties";
    
    //Map to get the database type equivalent for the requested filename
    

    @PostConstruct
    public void init() {
        
        prop = loadProperties(PROP_FILE);
        passwords = loadProperties(PW_FILE);
        parkings = loadProperties(PARK_FILE);
        rest = loadProperties(REST_FILE);
    }
    
    private String getTypeFromFileName(String file){
        //remove the Properties/
        String s = file.substring(11);
        //get the name without extension
        s = s.split(".")[0];
        return s;
    }
    
    public Properties loadProperties(String file){
        Properties currProp = new Properties();
        
        try {
            
            InputStream inputStream = this.getClass().getClassLoader()
                    
                    .getResourceAsStream(file);
 
            currProp = new Properties();
           // System.out.println("InputStream is: " + inputStream);
            // Loading the properties
            currProp.load(inputStream);
 
            // Printing the properties
            //System.out.println("Read Properties."+currProp);
        } catch (IOException ex) {
            Logger.getLogger(PropertyLoaderBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return currProp;
    }
    
    public String getProperty(String key){
        return prop.getProperty(key, null);
    }
    
    public String getParking(String key){
        return parkings.getProperty(key, null); //defaultwaarde
    }
    
    public String getUrlRest(String key){
        return parkings.getProperty(key,null);
    }
    
    public String getPassword(String key){
        return passwords.getProperty(key,null);
    }
    
    public ApiType[] getApiTypes(){
        return ApiType.BLUEBIKE.getDeclaringClass().getEnumConstants();
    }
    
    public ApiType getTypeFromString(String type) throws ApiRequestException{
        type = type.toLowerCase();
        ApiType[] possibleValues = getApiTypes();
        for(ApiType t : possibleValues){
            if(t.getNameCI().equals(type)){
                return t;
            }
        }
        throw new ApiNotFoundException();
    }
    
     public int[] getXandYCoordinates(double lat, double lon) {
        int[] coordinates = convertToLambert(lat, lon);
        return new int[]{coordinates[0], coordinates[1]};

    }

    private int[] convertToLambert(double lat, double lon) {
        double[] tussenresultaat = conversieDeel1(lat, lon);
        int[] res = conversieDeel2(tussenresultaat[0], tussenresultaat[1]);
        return res;
    }

    private double[] conversieDeel1(double lat, double lon) {

        double hoogte = 0;   //Altitude
        double LatBel, LngBel;
        double DLat, DLng;
        double Dh;
        double dy, dx, dz;
        double da, df;
        double LWa, Rm, Rn, LWb;
        double LWf, LWe2;
        double SinLat, SinLng;
        double CoSinLat;
        double CoSinLng;

        double Adb;

        //conversion to radians
        lat = (Math.PI / 180) * lat;
        lon = (Math.PI / 180) * lon;

        SinLat = Math.sin(lat);
        SinLng = Math.sin(lon);
        CoSinLat = Math.cos(lat);
        CoSinLng = Math.cos(lon);

        dx = 125.8;
        dy = -79.9;
        dz = 100.5;
        da = 251.0;
        df = 0.000014192702;

        LWf = 1 / 297;
        LWa = 6378388;
        LWb = (1 - LWf) * LWa;
        LWe2 = (2 * LWf) - (LWf * LWf);
        Adb = 1 / (1 - LWf);

        Rn = LWa / Math.sqrt(1 - LWe2 * SinLat * SinLat);
        Rm = LWa * (1 - LWe2) / Math.pow((1 - LWe2 * lat * lat), 1.5);

        DLat = -dx * SinLat * CoSinLng - dy * SinLat * SinLng + dz * CoSinLat;
        DLat = DLat + da * (Rn * LWe2 * SinLat * CoSinLat) / LWa;
        DLat = DLat + df * (Rm * Adb + Rn / Adb) * SinLat * CoSinLat;
        DLat = DLat / (Rm + hoogte);

        DLng = (-dx * SinLng + dy * CoSinLng) / ((Rn + hoogte) * CoSinLat);
        Dh = dx * CoSinLat * CoSinLng + dy * CoSinLat * SinLng + dz * SinLat;
        //Dh = Dh - da * LWa / Rn + df * Rn * lat * lat / Adb;

        LatBel = ((lat + DLat) * 180) / Math.PI;
        LngBel = ((lon + DLng) * 180) / Math.PI;

        double[] coords = new double[2];
        coords[0] = LatBel;
        coords[1] = LngBel;
        return coords;

    }

    private int[] conversieDeel2(double latitude, double longitude) {

        /*'
   '       Conversion from spherical coordinates to Lambert 72
   '       Input parameters : lat, lng (spherical coordinates)
   '       Spherical coordinates are in decimal degrees converted to Belgium datum!
   '*/
 
        double LongRef = 0.076042943;        //4°21'24"983
        double bLamb = (double) 6378388.0 * (1.0 - (1.0 / 297.0));

        double aCarre = Math.pow(6378388, 2);
        double eCarre = (aCarre - Math.pow(bLamb, 2)) / aCarre;
        double KLamb = 11565915.812935;
        double nLamb = 0.7716421928;
        double eLamb = Math.sqrt(eCarre);
        double eSur2 = eLamb / 2;

        //conversion to radians
        double lat = (Math.PI / 180) * latitude;
        double lng = (Math.PI / 180) * longitude;

        double eSinLatitude = eLamb * Math.sin(lat);
        double TanZDemi = (Math.tan((Math.PI / 4) - (lat / 2))) * (Math.pow((1 + (eSinLatitude)) / (1 - (eSinLatitude)), (eSur2)));

        double RLamb = KLamb * (Math.pow(TanZDemi, nLamb));

        double Teta = nLamb * (lng - LongRef);

        double x, y;

        x = 150000 + 0.01256 + RLamb * Math.sin(Teta - 0.000142043);

        y = 5400000 + 88.4378 - RLamb * Math.cos(Teta - 0.000142043);
        int[] coords = new int[2];
        coords[0] = (int) Math.round(x);
        coords[1] = (int) Math.round(y);
        return coords;
    }

    public boolean getSummertime() {
        String p = prop.getProperty("time");
        if(p.equals("summer")){
            System.out.println("in propertie");
            return true;
        } 
        return false;
    }
}
