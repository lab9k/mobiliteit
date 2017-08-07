/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global google */
var coyoteMarkers = [];
var parkingMarkers = [];
var bbParkingMarkers = [];
var r40Markers = [];
var waylayMarkers = [];
var wazeMarkers = [];
var gipodMarkers = [];
var map;
var coyote;
var coyoteAlerts = [];
var parking;
var parkingAlerts = [];
var bbParking;
var bbParkingAlerts = [];
var r40;
var r40Alerts = [];
var waylay;
var waylayAlerts = [];
var waze;
var wazeAlerts = [];
var gipod;
var gipodAlerts = [];
var locationMarker;
var trafficLayer;

var mapKnoppen = ["MapCoyoteBox", "MapParkingBox", "MapBlueBikeBox", "MapR40Box", "MapLocationBox", "MapWaylayBox", "MapWazeBox", "MapGipodBox"];


function initMap(zoom = 13) {
    map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 51.053737, lng: 3.718543},
        zoom: zoom
    });
    trafficLayer = new google.maps.TrafficLayer();
}

function checkAll() {
    checkBlueBike();
    checkCoyote();
    checkParking();
    checkR40();
    checkWaylay();
    checkWaze();
    checkMapLocation();
    toggleTraffic();
}

function checkCoyote() {
    var checkbox = document.getElementById("MapCoyoteBox");
    if (checkbox.checked) {
        getCoyote();
        localStorage.setItem(mapKnoppen[0], "true");
    } else {
        deleteCoyoteMarkers();
        localStorage.setItem(mapKnoppen[0], "false");
    }
    //saveLocalStorage();
}

function checkParking() {
    var checkbox = document.getElementById("MapParkingBox");
    if (checkbox.checked) {
        getParkings();
        localStorage.setItem(mapKnoppen[1], "true");
    } else {
        deleteParkingMarkers();
        localStorage.setItem(mapKnoppen[1], "false");
    }
    //saveLocalStorage();
}

function checkBlueBike() {
    var checkbox = document.getElementById("MapBlueBikeBox");
    if (checkbox.checked) {
        getBBParkings();
        localStorage.setItem(mapKnoppen[2], "true");
    } else {
        deleteBBParkingMarkers();
        localStorage.setItem(mapKnoppen[2], "false");
    }
    //saveLocalStorage();
}

function checkR40() {
    var checkbox = document.getElementById("MapR40Box");
    if (checkbox.checked) {
        getR40();
        localStorage.setItem(mapKnoppen[3], "true");
    } else {
        deleteR40Markers();
        localStorage.setItem(mapKnoppen[3], "false");
    }
    //saveLocalStorage();
}

function checkMapLocation() {
    var checkbox = document.getElementById("MapLocationBox");
    if (checkbox.checked) {

        getMapLocation();
        localStorage.setItem(mapKnoppen[4], "true");
    } else {
        if (locationMarker !== undefined)
            locationMarker.setMap(null);
        localStorage.setItem(mapKnoppen[4], "false");
    }
    //saveLocalStorage();
}

function checkWaylay() {
    var checkbox = document.getElementById("MapWaylayBox");
    if (checkbox.checked) {
        getWaylay();
        localStorage.setItem(mapKnoppen[5], "true");
    } else {
        deleteWaylayMarkers();
        localStorage.setItem(mapKnoppen[5], "false");
    }
    //saveLocalStorage();
}

function checkWaze() {
    var checkbox = document.getElementById("MapWazeBox");
    if (checkbox.checked) {
        getWaze();
        localStorage.setItem(mapKnoppen[6], "true");
    } else {
        deleteWazeMarkers();
        localStorage.setItem(mapKnoppen[6], "false");
    }
    //saveLocalStorage();
}

function checkGipod() {
    var checkbox = document.getElementById("MapGipodBox");
    if (checkbox.checked) {
        getGipod();
        localStorage.setItem(mapKnoppen[7], "true");
    } else {
        deleteGipodMarkers();
        localStorage.setItem(mapKnoppen[7], "false");
    }
    //saveLocalStorage();
}

function getCoyote() {
    $.ajax({
        url: CoyoteREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            coyote = data;
            //console.log(coyote);
            getCoyoteAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getCoyoteAlerts() {
    coyoteAlerts = [];
    for (i = 0; i < coyote["Coyote"].length; i++) {
        var item = coyote["Coyote"][i];
        if (item.hasOwnProperty("type") && item["type"] === "Coyote alert") {
            var alert = {street: item["street"], subtype: item["subtype"], pos: {lat: item["latitude"], lng: item["longitude"]}};
            coyoteAlerts.push(alert);
        }
    }
    //console.log(coyoteAlerts);
    createCoyoteMarkers();
}

function createCoyoteMarkers() {
    //coyoteMarkers = [];
    var image = {
        url: "img/alert.png",
        scaledSize: new google.maps.Size(20, 18)
    };
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < coyoteAlerts.length; i++) {
        marker = new google.maps.Marker({
            position: coyoteAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent(coyoteAlerts[i]["subtype"]);
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        coyoteMarkers.push(marker);
    }
}

function deleteCoyoteMarkers() {
    for (i = 0; i < coyoteMarkers.length; i++) {
        coyoteMarkers[i].setMap(null);
    }
    coyoteMarkers = [];
    coyoteAlerts = [];
}

function getParkings() {
    $.ajax({
        url: ParkingREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            parking = data;
            //console.log(parking);
            getParkingAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getParkingAlerts() {
    parkingAlerts = [];
    for (i = 0; i < parking["Parkings"].length; i++) {
        try {
            var item = parking["Parkings"][i];
            if (document.getElementById((item["name"] + "Box").replace('.', '').split(" ").slice(1).join("")).checked === true) {
                var alert = {name: item["name"], availCap: item["availableCapacity"], totalCap: item["totalCapacity"], pos: {lat: item["latitude"], lng: item["longitude"]}};
                parkingAlerts.push(alert);
            }
        } catch (err) {
        }


    }
    //console.log(parkingAlerts);
    createParkingMarkers();
}

function createParkingMarkers(name) {
    //parkingMarkers = [];

    for (i = 0; i < parkingMarkers.length; i++) {
        parkingMarkers[i].setMap(null);
    }
    parkingMarkers = [];

    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < parkingAlerts.length; i++) {
        if (parkingAlerts[i]["name"] === name) {
            var image = {
                url: "img/parkingSelected.png",
                scaledSize: new google.maps.Size(25, 25)
            };
        } else {
            var image = {
                url: "img/parking.png",
                scaledSize: new google.maps.Size(20, 20)
            };
        }

        marker = new google.maps.Marker({
            position: parkingAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + parkingAlerts[i]["name"] + "<br />" +
                        parkingAlerts[i]["availCap"] + " / " + parkingAlerts[i]["totalCap"] + " vrij </p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        parkingMarkers.push(marker);
    }


}

function deleteParkingMarkers() {
    for (i = 0; i < parkingMarkers.length; i++) {
        parkingMarkers[i].setMap(null);
    }
    parkingMarkers = [];
    parkingAlerts = [];
}

function getBBParkings() {
    $.ajax({
        url: BlueBikeREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            bbParking = data;
            //console.log(bbParking);
            getBBParkingAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getBBParkingAlerts() {
    bbParkingAlerts = [];
    for (i = 0; i < bbParking["BlueBike"].length; i++) {
        try {
            var item = bbParking["BlueBike"][i];
            if (document.getElementById("BB" + item["name"].substr(16).split("-").join("") + "Box").checked === true) {
                var alert = {name: item["name"], availCap: item["available"], totalCap: item["totalCap"], pos: {lat: item["latitude"], lng: item["longitude"]}};
                bbParkingAlerts.push(alert);
            }
        } catch (err) {
        }

    }
    //console.log(bbParkingAlerts);
    createBBParkingMarkers();
}

function createBBParkingMarkers() {
    //bbParkingMarkers = [];
    var image = {
        url: "img/bluebike2.png",
        scaledSize: new google.maps.Size(20, 20)
    };
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < bbParkingAlerts.length; i++) {
        marker = new google.maps.Marker({
            position: bbParkingAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + bbParkingAlerts[i]["name"] + "<br />" +
                        bbParkingAlerts[i]["availCap"] + " / " + bbParkingAlerts[i]["totalCap"] + " vrij </p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        bbParkingMarkers.push(marker);
    }
}

function deleteBBParkingMarkers() {
    for (i = 0; i < bbParkingMarkers.length; i++) {
        bbParkingMarkers[i].setMap(null);
    }
    bbParkingMarkers = [];
    bbParkingAlerts = [];
}

function getR40() {
    $.ajax({
        url: R40REST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            r40 = data;
            console.log(r40);
            getR40Alerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getR40Alerts() {
    r40Alerts = [];
    for (i = 0; i < r40["R40"].length; i++) {
        var item = r40["R40"][i];
        if (item["count"] !== 0) {
            var alert = {name: item["contextEntity"], count: item["count"], speed: item["speed"], pos: {lat: item["latitude"], lng: item["longitude"]}};
            r40Alerts.push(alert);
        }

    }
    //console.log(r40Alerts);
    createR40Markers();
}

function createR40Markers(name) {
    for (i = 0; i < r40Markers.length; i++) {
        r40Markers[i].setMap(null);
    }
    r40Markers = [];
    //r40Markers = [];
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < r40Alerts.length; i++) {
        if (r40Alerts[i]["name"] === name) {
            var image = {
                url: "img/carSelected.png",
                scaledSize: new google.maps.Size(25, 25)
            };
        } else {
            var image = {
                url: "img/car.png",
                scaledSize: new google.maps.Size(15, 15)
            };
        }
        marker = new google.maps.Marker({
            position: r40Alerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + r40Alerts[i]["name"] + "<br />" +
                        r40Alerts[i]["count"] + " auto's/uur <br />" +
                        r40Alerts[i]["speed"] + "km/h </p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        marker.addListener('click', function () {
            var name = r40Alerts.find(x => x.pos.lat === this.position.lat()).name;
            s = document.getElementById('R40Picker');
            for (var j = 0; j < s.options.length; j++) {
                if (s.options[j].value === name) {
                    s.options[j].selected = true;
                    selectChange()
                    return;
                }
            }
        });
        r40Markers.push(marker);
    }
}

function deleteR40Markers() {
    for (i = 0; i < r40Markers.length; i++) {
        r40Markers[i].setMap(null);
    }
    r40Markers = [];
    r40Alerts = [];
}

function getWaylay() {
    $.ajax({
        url: WaylayREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            waylay = data;
            //console.log(coyote);
            getWaylayAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getWaylayAlerts() {
    waylayAlerts = [];
    for (i = 0; i < waylay["Waylay"].length; i++) {
        var item = waylay["Waylay"][i];
            var alert = {street: item["street"], subtype: item["subtype"], pos: {lat: item["latitude"], lng: item["longitude"]}};
            waylayAlerts.push(alert);
    }
    //console.log(waylayAlerts);
    createWaylayMarkers();
}

function createWaylayMarkers(route, type) {
    //waylayMarkers = [];
    for (i = 0; i < waylayMarkers.length; i++) {
        waylayMarkers[i].setMap(null);
    }
    waylayMarkers = [];
    
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < waylayAlerts.length; i++) {
        if (waylayAlerts[i]["street"] === route && waylayAlerts[i]["subtype"] === type) {
            var image = {
                url: "img/trafficjamSelected.png",
                scaledSize: new google.maps.Size(25, 25)
            };
        } else {
            var image = {
                url: "img/trafficjam.png",
                scaledSize: new google.maps.Size(20, 20)
            };
        }
        marker = new google.maps.Marker({
            position: waylayAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + waylayAlerts[i]["subtype"] +"</p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        waylayMarkers.push(marker);
    }
}

function deleteWaylayMarkers() {
    for (i = 0; i < waylayMarkers.length; i++) {
        waylayMarkers[i].setMap(null);
    }
    waylayMarkers = [];
    waylayAlerts = [];
}

function getWaze() {
    $.ajax({
        url: WazeREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            waze = data;
            //console.log(coyote);
            getWazeAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getWazeAlerts() {
    wazeAlerts = [];
    for (i = 0; i < waze["Waze"].length; i++) {
        var item = waze["Waze"][i];
            var alert = {route: item["street"], des: item["description"], pos: {lat: item["latitude"], lng: item["longitude"]}};
            wazeAlerts.push(alert);
    }
    //console.log(wazeAlerts);
    createWazeMarkers();
}

function createWazeMarkers(route) {
    //wazeMarkers = [];
    for (i = 0; i < wazeMarkers.length; i++) {
        wazeMarkers[i].setMap(null);
    }
    wazeMarkers = [];
    
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < wazeAlerts.length; i++) {
        if (wazeAlerts[i]["route"] === route) {
            var image = {
                url: "img/roadworks.png",
                scaledSize: new google.maps.Size(25, 25)
            };
        } else {
            var image = {
                url: "img/roadworks.png",
                scaledSize: new google.maps.Size(20, 20)
            };
        }
        marker = new google.maps.Marker({
            position: wazeAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + wazeAlerts[i]["des"] +"</p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        wazeMarkers.push(marker);
    }
}

function deleteWazeMarkers() {
    for (i = 0; i < wazeMarkers.length; i++) {
        wazeMarkers[i].setMap(null);
    }
    wazeMarkers = [];
    wazeAlerts = [];
}

function getGipod() {
    $.ajax({
        url: GipodREST,
        type: 'GET',
        contentType: 'application/json',
        success: function (data, textStatus, jQxhr) {
            gipod = data;
            //console.log(coyote);
            getGipodAlerts();
        },
        error: function (jqXhr, textStatus, errorThrown) {
            console.log(errorThrown);
        }
    });
}

function getGipodAlerts() {
    gipodAlerts = [];
    for (i = 0; i < gipod["Gipod"].length; i++) {
        var item = gipod["Gipod"][i];
            var alert = {begin: item["startDate"], end: item["endDate"], description: item["description"], pos: {lat: item["latitude"], lng: item["longitude"]}};
            gipodAlerts.push(alert);
    }
    //console.log(gipodAlerts);
    createGipodMarkers();
}

function createGipodMarkers(description) {
    //gipodMarkers = [];
    for (i = 0; i < gipodMarkers.length; i++) {
        gipodMarkers[i].setMap(null);
    }
    gipodMarkers = [];
    
    var infowindow = new google.maps.InfoWindow();
    var marker, i;

    for (i = 0; i < gipodAlerts.length; i++) {
        if (gipodAlerts[i]["description"] === description) {
            var image = {
                url: "img/roadworks.png",
                scaledSize: new google.maps.Size(25, 25)
            };
        } else {
            var image = {
                url: "img/roadworks.png",
                scaledSize: new google.maps.Size(20, 20)
            };
        }
        marker = new google.maps.Marker({
            position: gipodAlerts[i]["pos"],
            map: map,
            icon: image
        });
        google.maps.event.addListener(marker, 'mouseover', (function (marker, i) {
            return function () {
                infowindow.setContent("<p>" + gipodAlerts[i]["begin"] + " - " + gipodAlerts[i]["end"] + "</br>" + 
                        gipodAlerts[i]["description"]+"</p>");
                infowindow.open(map, marker);
            };
        })(marker, i));
        google.maps.event.addListener(marker, 'mouseout', (function (marker, i) {
            return function () {
                infowindow.close(map, marker);
            };
        })(marker, i));
        gipodMarkers.push(marker);
    }
}

function deleteGipodMarkers() {
    for (i = 0; i < gipodMarkers.length; i++) {
        gipodMarkers[i].setMap(null);
    }
    gipodMarkers = [];
    gipodAlerts = [];
}

function centerParking(self) {
    if (parkingAlerts.length === 0) {
        getParkings();
        document.getElementById("MapParkingBox").checked = true;
    }
    createParkingMarkers(self.textContent);
    pos = parkingAlerts.find(x => x.name === self.textContent).pos;
    map.setCenter(pos);
}

function centerR40(name) {
    if (document.getElementById("MapR40Box").checked === true) {
        createR40Markers(name);
        pos = r40Alerts.find(x => x.name === name).pos;
        map.setCenter(pos);
    }

}

function centerBBParking(self) {
    if (bbParkingAlerts.length === 0) {
        getBBParkings();
        document.getElementById("MapBlueBikeBox").checked = true;
    }
    pos = bbParkingAlerts.find(x => x.name.substring(16) === self.textContent).pos;
    map.setCenter(pos);
}

function centerAccidents(route, type) {
    if (coyoteAlerts.length === 0) {
        getCoyote();
        document.getElementById("MapCoyoteBox").checked = true;
    }

    pos = coyoteAlerts.find(x => x.street === route && x.subtype === type).pos;
    map.setCenter(pos);
}

function centerWaylay(route, type) {
    if (waylayAlerts.length === 0) {
        getWaylay();
        document.getElementById("MapWaylayBox").checked = true;
    }
    createWaylayMarkers(route, type)
    pos = waylayAlerts.find(x => x.street === route && x.subtype === type).pos;
    map.setCenter(pos);
}

function centerWaze(route) {
    if (wazeAlerts.length === 0) {
        getWaze();
        document.getElementById("MapWazeBox").checked = true;
    }
    createWazeMarkers(route);
    pos = wazeAlerts.find(x => x.route === route).pos;
    map.setCenter(pos);
}

function centerGipod(description) {
    if (gipodAlerts.length === 0) {
        getGipod();
        document.getElementById("MapGipodBox").checked = true;
    }
    createGipodMarkers(description)
    pos = gipodAlerts.find(x => x.description === description).pos;
    map.setCenter(pos);
}

function getMapLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showMapPosition, showLocationError);
    } else {
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
}

function showMapPosition(position) {
    if (locationMarker !== undefined)
        locationMarker.setMap(null);
    var image = {
        url: "img/location.png",
        scaledSize: new google.maps.Size(20, 20)
    };
    var marker;
    var alert = {pos: {lat: position.coords.latitude, lng: position.coords.longitude}};


    marker = new google.maps.Marker({
        position: alert["pos"],
        map: map,
        icon: image
    });
    locationMarker = marker;

}

function toggleTraffic(){
    var box = document.getElementById("MapTrafficBox");
    
    if(box.checked){
        trafficLayer.setMap(map);
    }else{
        trafficLayer.setMap(null);
    }
}

function showLocationError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            console.log("User denied the request for Geolocation.");
            break;
        case error.POSITION_UNAVAILABLE:
            console.log("Location information is unavailable.");
            break;
        case error.TIMEOUT:
            console.log("The request to get user location timed out.");
            break;
        case error.UNKNOWN_ERROR:
            console.log("An unknown error occurred.");
            break;
    }
}

$('ul.dropdown-menu').on('click', function(event){
    //The event won't be propagated to the document NODE and 
    // therefore events delegated to document won't be fired
    event.stopPropagation();
});