/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var timeout = 180000;
var Weather;
var parking;
var BlueBike;
var Trains;
var Coyote;
function refreshSingleApi(url, callback) {
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: function (data) {
            //console.log(data);
            obj = data;
            saveLocally(obj, url);
            callback(data);
        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}

$(document).ready(function () {
    //getLocalStorage();
    setBackground();
    setCSS();
    fillIn();
    setInterval(fillIn, timeout);
    //console.log("net fill in gedaan");
    setInterval(postLocalStorage, timeout);
    document.getElementById("radiusLabel").innerHTML = document.getElementById("radiusBar").value;
    fillSelects();
    checkLoggedIn();
    checkFBUser();
});
function fillIn() {
    refreshSingleApi(WeatherREST, fillWeatherMyPage);
    refreshSingleApi(ParkingREST, fillParkingMyPage);
    refreshSingleApi(BlueBikeREST, fillBlueBikeMyPage);
    refreshSingleApi(TrainREST, fillNMBSMyPage);
    refreshSingleApi(CoyoteREST, fillCoyoteMyPage);
}


function fillWeatherMyPage(data) {
    try {

        document.getElementById("weatherimg").setAttribute("src", data.Weather.iconUrl);
        document.getElementById("temp").innerHTML = data.Weather.celsiusMin + " °C - " + data.Weather.celsiusMax + " °C";
        document.getElementById("tempimg").setAttribute("src", "img/thermometer.png");
        document.getElementById("wind").innerHTML = data.Weather.avgWindKph + " Km/h";
        document.getElementById("windimg").setAttribute("src", "img/wind.png");
        document.getElementById("humidity").innerHTML = data.Weather.avehumidity + " % vochtigheid";
        document.getElementById("humidimg").setAttribute("src", "img/humidity.png");
        document.getElementById("rain").innerHTML = data.Weather.chanceRain + " % kans op regen";
        document.getElementById("rainimg").setAttribute("src", "img/rain.ico");
        getName();
    } catch (err) {

    }
}
function getName() {
    $.ajax({
        type: "GET",
        url: userUrl,
        contentType: "application/json",
        success: function (data) {
            var obj = data;

            if (obj.exception === "You are not logged in.") {
                document.getElementById("weatherMessage").innerHTML = "U bent niet ingelogged!";


            } else {
                document.getElementById("weatherMessage").innerHTML = "Hallo " + obj.firstname + "!";

            }

        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}

function logUit() {
    postLocalStorage();
    $.ajax({
        type: "GET",
        url: logoutUrl,
        contentType: "application/json",
        success: function (data) {
            loggedIn = "False";
            document.location.href = "index.html";

        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}

function checkLoggedIn() {
    $.ajax({
        type: "GET",
        url: loginUrl,
        contentType: "application/json",
        success: function (data) {
            if (data.loggedIn === "True") {
                document.getElementById("myPageContent").setAttribute("style", "display:true");
                document.getElementById("myPageLogin").setAttribute("style", "display:none");
            } else {
                document.getElementById("myPageContent").setAttribute("style", "display:none");
                document.getElementById("myPageLogin").setAttribute("style", "display:true");
            }

        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}

function checkFBUser() {
    $.ajax({
        type: "GET",
        url: checkFBUserUrl,
        contentType: "application/json",
        success: function (data) {
            if (data.success === "true") {
                document.getElementById("changePWRowBtn").setAttribute("style", "width:25%; display:none; ");
                document.getElementById("profilepicRow").setAttribute("style", "display:none; ");
                var rows = document.getElementById("notifTypeTable").rows;
                for (var row = 0; row < rows.length; row++) {
                    if ([0, 1, 3, 5, 7, 9].includes(row)) {
                        var cols = rows[row].cells;
                        cols[5].style.display = 'none';
                        var notifCheckboxNames = ["ParkingMailBox", "BBParkingMailBox","CoyoteMailBox","WeatherMailBox", "TrainMailBox"];
                        for (var i = 0; i < notifCheckboxNames.length; i++) {
                            localStorage.setItem(notifCheckboxNames[i], false);
                        }
                    }
                }

            } else {
                document.getElementById("changePWRowBtn").setAttribute("style", "width:25%; display:true; ");
                document.getElementById("profilepicRow").setAttribute("style", "display:true; ");
                var rows = document.getElementById("notifTypeTable").rows;
                for (var row = 0; row < rows.length; row++) {
                    if ([0, 1, 3, 5, 7, 9].includes(row)) {
                        var cols = rows[row].cells;
                        cols[5].style.display = 'true';
                    }
                }
            }
        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}

function saveLocally(geg, url) {
    if (url === ParkingREST) {
        parking = geg;
    } else if (url === BlueBikeREST) {
        BlueBike = geg;
    } else if (url === WeatherREST) {
        Weather = geg;
    } else if (url === TrainREST) {
        Trains = geg;
    } else if (url === CoyoteREST) {
        Coyote = geg;
    }
}

function showError(element, errormsg) {
//    document.getElementById(element).innerHTML = errormsg;
//    document.getElementById(element).setAttribute("aria-valuenow", 100);
//    document.getElementById(element).setAttribute("style", "width: 100%");
}
