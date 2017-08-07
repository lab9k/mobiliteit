/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/*
 *  Al de functies, zoals 'vrijdagClick' worden in localstorage bijgehouden, indien ze op true staan houdt dit in
 *  dat ze worden weergegeven in het venster maar niet in de modal.
 * 
 * 
 *  TO DO:
 *  add int engels
 *  lijnen tussen voegen
 *  en de innerhtml van de trains wissen indien de rzijn
 *  ook de tekstvakken terug leegmaken ;)
 */

var timeout = 180000;
var count = 0;
var parking;
var BlueBike;
var nmbstrains; //frontend bijhouden van alle trainen zodat er geen nieuwe call moet komen 
var Weather;
//routes left, right bestmemingen en bestmemignen R horen leeg te zijn eens de calls er zijn, doe later
var routesLeft = [];
var routesRight = [];
var parkingNamesMP = ["P01 Vrijdagmarkt", "P02 Reep", "P04 Savaanstraat", "P07 Sint-Michiels", "P08 Ramen", "P10 Sint-Pietersplein", "Parking Gent St. Pieters"];
var parkingFunctions = ["vrijdagClick", "reepClick", "savaanClick", "smClick", "ramenClick", "sppClick", "gspClick"];
var BBNamesMP = ["Sint-Pieters", "Dampoort"];
var BBNamesBack = ["Blue-bikes_Gent-Sint-Pieters", "Blue-bikes_Gent_Dampoort"];
var BBFunctions = ["Sint-PietersClick", "DampoortClick"];
var bestemmingen = [];
var bestemmingenR = [];
var NMBSFav = [];
var LijnFav = [];

$(document).ready(function () {
    firstTime();    
    fillTabs();
    UpdateMP();
    setProfilePic();
    //inladen van gegevens gebeurt in myPageLoader.js

});

function firstTime() {
    if (localStorage.getItem("firstTime") !== "true") {
        
        localStorage.setItem("firstTime", "true");
        getRoutes();
        var routesDR  = [];
        localStorage.setItem("routesRight", JSON.stringify(routesDR));
        
        var NMBSFavLS = [];
        localStorage.setItem("NMBSFav", JSON.stringify(NMBSFavLS));
        var LijnFavLS = [];
        localStorage.setItem("LijnFav", JSON.stringify(LijnFavLS));
        window.location.reload();
    }
    if (localStorage.getItem("routesRight") === "null") {
        var routesDR = [];
        localStorage.setItem("routesRight", JSON.stringify(routesDR));
        getRoutes();
        window.location.reload();
    }
    if (localStorage.getItem("NMBSFav" === "null")) {
        var NMBSFavLS = [];
        localStorage.setItem("NMBSFav", JSON.stringify(NMBSFavLS));
        window.location.reload();
    }
    if (localStorage.getItem("LijnFav") === "null") {
        var LijnFavLS = [];
        localStorage.setItem("LijnFav", JSON.stringify(LijnFavLS));
        window.location.reload();
    }

}


function fillTabs() {
    fillParkingTab();
    fillBlueBikeTab();
    routesNMBSStations();
    routesLeft = JSON.parse(localStorage.getItem("routesLeft"));
    routesRight = JSON.parse(localStorage.getItem("routesRight"));
    if (localStorage.getItem("NMBSFav") !== null) {
        NMBSFav = JSON.parse(localStorage.getItem("NMBSFav"));
        updateNMBSFav();
    }
    if (localStorage.getItem("LijnFav") !== null) {
        LijnFav = JSON.parse(localStorage.getItem("LijnFav"));
        updateLijnFav();
    }
}

function UpdateMP() {
    //just change max to whatever the highest id is of the modal you have
    var max = 7;
    for (var i = 1; i <= 7; i++) {
        document.getElementById("id0" + i).style.display = 'none';
    }
    fillParkingMyPage(parking);
    fillBlueBikeMyPage(BlueBike);
    fillNMBSMyPage(Trains);
    fillCoyoteMyPage(Coyote);

}


function autoComplete(inputString, tableString) {
    // Declare variables 
    var input, filter, table, tr, td, i;
    input = document.getElementById(inputString);
    filter = input.value.toUpperCase();
    table = document.getElementById(tableString);
    tr = table.getElementsByTagName("tr");

    // Loop through all table rows, and hide those who don't match the search query
    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td")[0];
        if (td) {
            if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}


