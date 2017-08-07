/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var messages = document.getElementById("messages");
var timeout = 180000;
var count = 0;
var parking;
var BlueBike;
var Weather;
var TurnedOffKnoppen = ["WaylayBox", "IncidentenBox", "WazeBox", "R40Box", "GipodBox"];

function refreshSingleApi(url, callback) {
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: function (data) {
           // console.log(data);
            obj = data;
            saveLocally(obj, url);
            callback(data);
            makeDrag();
        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}

function browserCheck() {
    // Opera 8.0+
    var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;

    // Firefox 1.0+
    var isFirefox = typeof InstallTrigger !== 'undefined';

    // Safari 3.0+ "[object HTMLElementConstructor]" 
    var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) {
        return p.toString() === "[object SafariRemoteNotification]";
    })(!window['safari'] || safari.pushNotification);

    // Internet Explorer 6-11
    var isIE = /*@cc_on!@*/false || !!document.documentMode;

    // Edge 20+
    var isEdge = !isIE && !!window.StyleMedia;

    // Chrome 1+
    var isChrome = !!window.chrome && !!window.chrome.webstore;

    // Blink engine detection
    var isBlink = (isChrome || isOpera) && !!window.CSS;

    if (isIE) {
        alert("Deze site is geoptimaliseerd voor moderne browsers en zal niet naar behoren werken in Internet Explorer. Gelieve een andere browser te gebruiken");
    }
}

$(document).ready(function () {
    makeGrid();
    browserCheck();

    getLocalStorage(onReady);

});

function onReady() {
    firstTime();
    setBackground();
    setCSS();
    destroyAllBoxes();
    fillAllBoxes();
    buildAllBoxes();
    toggleMain();
    toggleGraph();
    fillIn();


    setInterval(fillIn, timeout);
    setTimeout(makeDrag, 1500);
}

// alles al in localstorage op true !
// Indien je een reset wilt doen aka alles op null zetten doe je dit via 'resetAll'
//Gelieve indien je hier dingen aanpast dat daar ook te doen
function firstTime() {
    if (localStorage.getItem("MapBox") === null) {
        for (var i = 0; i < Knoppen.length; i++) {
            if (TurnedOffKnoppen.includes(Knoppen[i])) {
                localStorage.setItem(Knoppen[i], false);
                //console.log(Knoppen[i] + ": off");
            } else {
                localStorage.setItem(Knoppen[i], true);
                //console.log(Knoppen[i] + ": on");
            }

        }
    }
    // Parkings
    if (localStorage.getItem("RamenBox") === null) {
        for (var i = 0; i < ParkingKnoppen.length; i++) {
            localStorage.setItem(ParkingKnoppen[i], true);
        }
    }
    // BBParkings
    if (localStorage.getItem("BBSintPietersBox") === null) {
        for (var i = 0; i < BBParkingKnoppen.length; i++) {
            localStorage.setItem(BBParkingKnoppen[i], true);
        }
    }
    // Colvalue
    if (localStorage.getItem("colvalue") === null) {
        localStorage.setItem("colvalue", 3);
    }
    //Drag and drop
    if (localStorage.getItem("DragAndDropBox") === null) {
        localStorage.setItem("DragAndDropBox", true);
    }
    //GraphSwitch
    if (localStorage.getItem("GraphSwitch") === null) {
        localStorage.setItem("GraphSwitch", true);
    }
    //CSSSwitch
    if (localStorage.getItem("CSSSwitch") === null) {
        localStorage.setItem("CSSSwitch", false);
    }
    //Background
    if (localStorage.getItem("backgroundIndex") === null) {
        localStorage.setItem("backgroundIndex", 1);
    }//weather
    if(localStorage.getItem("WindBox") === null){
       for (var i = 0; i < weerKnoppen.length; i++) {
            localStorage.setItem(weerKnoppen[i], false);
        }
    }

}


function fillAllBoxes() {
    //voor algemene settings
    for (var i = 0; i < Knoppen.length; i++) {
        if (localStorage.getItem(Knoppen[i]) === "true") {
            document.getElementById(Knoppen[i]).checked = true;
        } else {
            document.getElementById(Knoppen[i]).checked = false;
        }
    }

    //voor de kaart
    for (var i = 0; i < mapKnoppen.length; i++) {
        if (localStorage.getItem(mapKnoppen[i]) === "true") {
            document.getElementById(mapKnoppen[i]).checked = true;
        } else {
            document.getElementById(mapKnoppen[i]).checked = false;
        }
    }

    //voor parkings:
    for (var i = 0; i < ParkingKnoppen.length; i++) {
        if (localStorage.getItem(ParkingKnoppen[i]) === "true") {
            document.getElementById(ParkingKnoppen[i]).checked = true;
        } else {
            document.getElementById(ParkingKnoppen[i]).checked = false;
        }
    }

    //voor BBparkings:
    for (var i = 0; i < BBParkingKnoppen.length; i++) {
        if (localStorage.getItem(BBParkingKnoppen[i]) === "true") {
            document.getElementById(BBParkingKnoppen[i]).checked = true;
        } else {
            document.getElementById(BBParkingKnoppen[i]).checked = false;
        }
    }
    // voor Weather
     for (var i = 0; i < weerKnoppen.length; i++) {
        if (localStorage.getItem(weerKnoppen[i]) === "true") {
            document.getElementById(weerKnoppen[i]).checked = true;
        } else {
            document.getElementById(weerKnoppen[i]).checked = false;
        }
    }
    var colvalue = localStorage.getItem("colvalue");
    document.getElementById("colbar").setAttribute("value", colvalue);
    document.getElementById("colcounter").innerText = colvalue;
    changeCols(colvalue);

    if (localStorage.getItem("DragAndDropBox") === "true") {
        document.getElementById("DragAndDropBox").checked = true;
    } else {
        document.getElementById("DragAndDropBox").checked = false;
    }

    if (localStorage.getItem("GraphSwitch") === "true") {
        document.getElementById("GraphSwitchBox").checked = true;
    } else {
        document.getElementById("GraphSwitchBox").checked = false;
    }

    if (localStorage.getItem("CSSSwitch") === "true") {
        document.getElementById("CSSSwitchBox").checked = true;
    } else {
        document.getElementById("CSSSwitchBox").checked = false;
    }
    //toggleCSS();




}
// destroy alles
function destroyAllBoxes() {
    for (var i = 0; i < Knoppen.length; i++) {
        var tussen = '#' + Knoppen[i];
        $(tussen).bootstrapToggle('destroy');
    }
    //voor parkings:
    for (var i = 0; i < ParkingKnoppen.length; i++) {
        var tussen = '#' + ParkingKnoppen[i];
        $(tussen).bootstrapToggle('destroy');
    }
    //voor BBparkings:
    for (var i = 0; i < BBParkingKnoppen.length; i++) {
        var tussen = '#' + BBParkingKnoppen[i];
        $(tussen).bootstrapToggle('destroy');
    }
    //voor weather
    for (var i = 0; i < weerKnoppen.length; i++) {
        var tussen = '#' + weerKnoppen[i];
        $(tussen).bootstrapToggle('destroy');
    }

    $('#DragAndDropBox').bootstrapToggle('destroy');
    $('#GraphSwitchBox').bootstrapToggle('destroy');
    $('#CSSSwitchBox').bootstrapToggle('destroy');
}

function buildAllBoxes() {
    for (var i = 0; i < Knoppen.length; i++) {
        var tussen = '#' + Knoppen[i];
        $(tussen).bootstrapToggle();
    }
    // voor parkings:
    for (var i = 0; i < ParkingKnoppen.length; i++) {
        var tussen = '#' + ParkingKnoppen[i];
        $(tussen).bootstrapToggle();
    }
    // voor BBparkings:
    for (var i = 0; i < BBParkingKnoppen.length; i++) {
        var tussen = '#' + BBParkingKnoppen[i];
        $(tussen).bootstrapToggle();
    }
    // voor Weather
    for (var i = 0; i < weerKnoppen.length; i++) {
        var tussen = '#' + weerKnoppen[i];
        $(tussen).bootstrapToggle();
    }


    $('#DragAndDropBox').bootstrapToggle();
    $('#GraphSwitchBox').bootstrapToggle();
    $('#CSSSwitchBox').bootstrapToggle();



}

function resetAll() {
    for (var i = 0; i < Knoppen.length; i++) {
        localStorage.setItem(Knoppen[i], true);
    }
    for (var i = 0; i < ParkingKnoppen.length; i++) {
        localStorage.setItem(ParkingKnoppen[i], true);
    }
    //saveLocalStorage();

}

//Fills in the interface
function fillIn() {

    if (document.getElementById("BlueBikeBox").checked === true) {
        refreshSingleApi(BlueBikeREST, toggleBlueBike);
    }
    if (document.getElementById("IncidentenBox").checked === true) {
        refreshSingleApi(CoyoteREST, fillCoyote);
    }
    if (document.getElementById("ParkingBox").checked === true) {
        refreshSingleApi(ParkingREST, ToggleParking);
    }
    if (document.getElementById("TreinenBox").checked === true) {
        refreshSingleApi(TrainREST, fillTrains);
    }
    if (document.getElementById("WeerBox").checked === true) {
        refreshSingleApi(WeatherREST, fillWeather);
    }
    if (document.getElementById("WaylayBox").checked === true) {
        refreshSingleApi(WaylayREST, fillWaylay);
    }
    if (document.getElementById("WazeBox").checked === true) {
        refreshSingleApi(WazeREST, fillWaze);
    }
    if (document.getElementById("GipodBox").checked === true) {
        refreshSingleApi(GipodREST, fillGipod);
    }
    checkAll();

}

//functie die wordt opgeroepen telkens een api een error genereert
function showError(element, errormsg) {

    document.getElementById(element).innerHTML = errormsg;
    document.getElementById(element).setAttribute("aria-valuenow", 100);
    document.getElementById(element).setAttribute("style", "width: 100%");
}
//functie die de gegevens in lokale variabelen opslaat, worden om de refresh rate aangepast
//anders zou er per aanpassing in instellingen een nieuwe restcall gedaan worden
//en dit zou teveel spam genereren
function saveLocally(geg, url) {
    if (url === ParkingREST) {
        parking = geg;
    } else if (url === BlueBikeREST) {
        BlueBike = geg;
    } else if (url === WeatherREST) {
        Weather = geg;
    }
}

//window.onload = function () {
//    makeDrag();
//
//}

$(window).load(function(){
  makeDrag();
});