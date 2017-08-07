/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var host = "https://mobi-02.project.tiwi.be:8181"; //changes var host to localhost if you aren't working on server.
//var host = "https://localhost:8181"; 

var urlREST = host + "/back/res/apidata/";
var BlueBikeREST = urlREST+ "bluebike";
var CoyoteREST = urlREST+ "coyote";
var ParkingREST = urlREST+ "parkings";
var TrainREST = urlREST+ "trains";
var WaylayREST = urlREST+ "waylay";
var WazeREST = urlREST+ "waze";
var WeatherREST = urlREST+ "weather";
var R40REST = urlREST+ "r40";
var GipodREST = urlREST + "gipod";
var DeLijnREST = urlREST + "delijn";
var DeLijnPersonalDefaultREST = urlREST + "51.0308096/3.7428096/500" ;
var graphUrl = host + "/back/res/statistics/r40/avg/";
var traveltimeUrl = host + "/back/res/statistics/traveltimes/";
var selectUrl = host + "/back/res/statistics/r40/stations";
var registerUrl = host + "/back/res/register";
var loginUrl = host + "/back/res/userlogin";
var fbloginUrl = loginUrl + "/facebook";
var logoutUrl = loginUrl + "/logout";
var userUrl = host + "/back/res/user";
var notifUrl = userUrl + "/notifications";
var propertiesUrl = userUrl + "/properties";
var changeNameUrl = userUrl + "/name";
var changePasswordUrl = userUrl + "/password";
var verifyPasswordUrl = userUrl + "/verifypassword";
var deleteUserUrl = userUrl + "/delete";
var checkFBUserUrl = userUrl + "/checkfbuser";
var deLijnGeneric= "https://api.haltelink.be/stopdata.json?stop=BE.DELIJN.";
var CoyoteRoutes = urlREST + "routes";
var myPageNMBS = urlREST + "train/";
var mailUrl = loginUrl + "/sendMail/";
//*

var stopsUrl = "https://mobi-02.project.tiwi.be:8181/back/res/apidata/train/stops"