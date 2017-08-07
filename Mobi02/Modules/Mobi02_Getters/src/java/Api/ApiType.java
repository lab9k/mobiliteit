package Api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author ruben
 */
public enum ApiType {
    PARKINGHENT("ParkingGhent"),BLUEBIKE("BlueBike"), COYOTE("Coyote"), PARKINGNMBS("ParkingNmbs"), R40TRAFFIC("R40"),TRAINSGHENT("Trains"),TRAINROUTE("TrainRoute"),GIPOD("Gipod"),
    WEATHER("Weather"),WAYLAY("Waylay"),PARKINGS("Parkings"),WAZE("Waze"),DELIJN("DeLijn"),EXCEPTION("Exception"),DELIJNPERSONAL("DeLijnPersonal"),TRAVELTIMES("Traveltimes");
    
    private final String name;
    
    private ApiType(String name){
        this.name = name;
    }
    
    public String getNameCI(){
        return name.toLowerCase();
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    public static ApiType[] getAllValues(){
        return ApiType.BLUEBIKE.getDeclaringClass().getEnumConstants();
    }
    
    
}
