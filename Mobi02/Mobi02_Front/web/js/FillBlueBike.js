/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global obj */

var widgetID = "avgUsed";
var errorMsg = "Er is geen informatie over BlueBike beschikbaar";
var BBParkingKnoppen = ["BBSintPietersBox", "BBDampoortBox"];

function toggleBlueBike(data) {
    
    try {
        document.getElementById(widgetID).innerHTML = "";
        var freePieters = 0,freeDampoort = 0;
        var totalPieters = 0,totalDampoort = 0;
        var maintPieters = 0,maintDampoort = 0;
        var usedPieters = 0;
        var usedDampoort = 0;
        
        if(document.getElementById("BBSintPietersBox").checked === true){
           
           document.getElementById("BBSintPieters").setAttribute("style","display: true;")
             freePieters = data.BlueBike.find(x => x.name === "Blue-bikes_Gent-Sint-Pieters").available;
             totalPieters = data.BlueBike.find(x => x.name === "Blue-bikes_Gent-Sint-Pieters").totalCap;
            document.getElementById("PietersFree").innerHTML = freePieters;
            document.getElementById("PietersFree").setAttribute("style", "width:" + freePieters / totalPieters * 100 + "%");
            maintPieters = data.BlueBike.find(x => x.name === "Blue-bikes_Gent-Sint-Pieters").inMaintenance;
            document.getElementById("PietersMaint").innerHTML = maintPieters;
            document.getElementById("PietersMaint").setAttribute("style", "width:" + maintPieters / totalPieters * 100 + "%");
             usedPieters = data.BlueBike.find(x => x.name === "Blue-bikes_Gent-Sint-Pieters").inUse;
            document.getElementById("PietersUsed").innerHTML = usedPieters;
            document.getElementById("PietersUsed").setAttribute("style", "width:" + usedPieters / totalPieters * 100 + "%");
            localStorage.setItem(BBParkingKnoppen[0], true);
        }else{
            document.getElementById("BBSintPieters").setAttribute("style","display: none;");
            localStorage.setItem(BBParkingKnoppen[0], false);
        }
        
        if(document.getElementById("BBDampoortBox").checked === true){
           
            document.getElementById("BBDampoort").setAttribute("style","display: true;")
             freeDampoort = data.BlueBike.find(x => x.name === "Blue-bikes_Gent_Dampoort").available;
            totalDampoort = data.BlueBike.find(x => x.name === "Blue-bikes_Gent_Dampoort").totalCap;
            document.getElementById("DampoortFree").innerHTML = freeDampoort;
            document.getElementById("DampoortFree").setAttribute("style", "width:" + freeDampoort / totalDampoort * 100 + "%");
             maintDampoort = data.BlueBike.find(x => x.name === "Blue-bikes_Gent_Dampoort").inMaintenance;
            document.getElementById("DampoortMaint").innerHTML = maintDampoort;
            document.getElementById("DampoortMaint").setAttribute("style", "width:" + maintDampoort / totalDampoort * 100 + "%");
             usedDampoort = data.BlueBike.find(x => x.name === "Blue-bikes_Gent_Dampoort").inUse;
            document.getElementById("DampoortUsed").innerHTML = usedDampoort;
            document.getElementById("DampoortUsed").setAttribute("style", "width:" + usedDampoort / totalDampoort * 100 + "%");
            localStorage.setItem(BBParkingKnoppen[1], true);
        }else{
            document.getElementById("BBDampoort").setAttribute("style","display: none;");
            localStorage.setItem(BBParkingKnoppen[1], false);
        }
        

        var freeAvg = freePieters + freeDampoort;
        var totalAvg = totalPieters + totalDampoort;
        document.getElementById("avgFree").innerHTML = freeAvg;
        document.getElementById("avgFree").setAttribute("style", "width:" + freeAvg / totalAvg * 100 + "%");
        var maintAvg = maintPieters + maintDampoort;
        document.getElementById("avgMaint").innerHTML = maintAvg;
        document.getElementById("avgMaint").setAttribute("style", "width:" + maintAvg / totalAvg * 100 + "%");
        var usedAvg = usedPieters + usedDampoort;
        document.getElementById("avgUsed").innerHTML = usedAvg;
        document.getElementById("avgUsed").setAttribute("style", "width:" + usedAvg / totalAvg * 100 + "%");
           
       //makeDrag();
       //saveLocalStorage();
    } catch (err) {
        showError("avgUsed", "Er is geen informatie over BlueBike beschikbaar");
    }
}

function ToggleBlueBike(){
   
    toggleBlueBike(BlueBike);
    deleteBBParkingMarkers();
    checkBlueBike();
}