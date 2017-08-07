/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global data */
var check = 0; //als het 0 is staat temp in celcius, 1 is fahrenheit
var widgetID = "weatherbar";
var errorMsg = "Er is geen informatie over het weer beschikbaar";
var weerKnoppen = ["WindBox","TempBox"];

function fillWeather(data) {
    try {
        document.getElementById(widgetID).innerHTML = "";
        document.getElementById("weatherimg").setAttribute("src", data.Weather.iconUrl);
        document.getElementById("temp").innerHTML = data.Weather.celsiusMin + " °C - " + data.Weather.celsiusMax + " °C";
        document.getElementById("tempimg").setAttribute("src", "img/thermometer.png");
        document.getElementById("wind").innerHTML = data.Weather.avgWindKph + " Km/h";
        document.getElementById("windimg").setAttribute("src", "img/wind.png");
        document.getElementById("humidity").innerHTML = data.Weather.avehumidity + " % vochtigheid";
        document.getElementById("humidimg").setAttribute("src", "img/humidity.png");
        if (data.Weather.chanceRain !== 0) {
            document.getElementById("weatherbar").innerHTML = data.Weather.chanceRain + "% kans op regen";
            document.getElementById("weatherbar").setAttribute("aria-valuenow", data.Weather.chanceRain);
            document.getElementById("weatherbar").setAttribute("style", "width:" + data.Weather.chanceRain + "%");
        }else{
            document.getElementById("weatherbar").innerHTML = "0% kans op regen";
            document.getElementById("weatherbar").setAttribute("aria-valuenow", 100);
            document.getElementById("weatherbar").setAttribute("style", "width: 100" + "%; color:black; background-color:white");
        }
        toggleTemp();
        toggleWind();
        //makeDrag();

    } catch (err) {
        showError("weatherbar", "Er is geen informatie over het weer beschikbaar");
    }
}


function toggleTemp() {
    var temp1, temp2;
    if (document.getElementById("TempBox").checked === true) {
        temp1 = Math.round((Weather.Weather.celsiusMin * (9 / 5)) + 32);
        temp2 = Math.round((Weather.Weather.celsiusMax * (9 / 5)) + 32);

        document.getElementById("temp").innerHTML = temp1 + " °F - " + temp2 + " °F";
        localStorage.setItem(weerKnoppen[1],"true");
    } else {
        temp1 = Weather.Weather.celsiusMin;
        temp2 = Weather.Weather.celsiusMax;

        document.getElementById("temp").innerHTML = temp1 + " °C - " + temp2 + " °C";
        localStorage.setItem(weerKnoppen[1],"false");
    }
    
}
function toggleWind() {
    var wind;
    if (document.getElementById("WindBox").checked === true) {
        wind = Math.round((Weather.Weather.avgWindKph * 0.621371192));

        document.getElementById("wind").innerHTML = wind + " mph";
        localStorage.setItem(weerKnoppen[0],"true");
    } else {
        wind = Weather.Weather.avgWindKph;

        document.getElementById("wind").innerHTML = wind + " Km/h ";
        localStorage.setItem(weerKnoppen[0],"false");
    }
}