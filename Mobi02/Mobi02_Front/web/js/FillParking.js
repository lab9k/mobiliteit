/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global data */
var widgetID = "avgParkingFree";
var errorMsg = "Er is geen informatie over parkeergarages beschikbaar";
var parkingNames = ["P01 Vrijdagmarkt", "P02 Reep", "P04 Savaanstraat", "P07 Sint-Michiels", "P08 Ramen", "P10 Sint-Pietersplein", "Parking Gent St. Pieters"];
var ParkingKnoppen = ["VrijdagmarktBox", "ReepBox", "SavaanstraatBox", "Sint-MichielsBox", "RamenBox", "Sint-PieterspleinBox", "GentStPietersBox"];
var ParkingMains = ["VrijdagmarktMain", "ReepMain", "SavaanstraatMain", "Sint-MichielsMain", "RamenMain", "Sint-PieterspleinMain", "GentStPietersMain"];
var totaalBezet, totaalMax;
function toggleParking(data) {
    try {

        document.getElementById(widgetID).innerHTML = "";
        var procentP01, procentP02, procentP04, procentP07, procentP08, procentP10, procentPNMBS;
        var procentNames = [procentP01, procentP02, procentP04, procentP07, procentP08, procentP10, procentPNMBS];
        var barIDs = ["P01", "P02", "P04", "P07", "P08", "P10", "PNMBS"];
        var labelIDs = ["P01avail", "P02avail", "P04avail", "P07avail", "P08avail", "P10avail", "PNMBSavail"];

        for (i = 0; i < procentNames.length; i++) {
            try {
                procentNames[i] = (1 - data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity / data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity) * 100;
                if (procentNames[i] > 80) {
                    document.getElementById(barIDs[i]).setAttribute("class", "progress-bar progress-bar-danger");
                } else if (procentNames[i] > 60) {
                    document.getElementById(barIDs[i]).setAttribute("class", "progress-bar progress-bar-warning");
                } else {
                    document.getElementById(barIDs[i]).setAttribute("class", "progress-bar progress-bar-success");
                }
                document.getElementById(barIDs[i]).innerHTML = procentNames[i].toFixed(0) + "%";
                document.getElementById(barIDs[i]).setAttribute("aria-valuenow", procentNames[i]);
                document.getElementById(barIDs[i]).setAttribute("style", "width:" + procentNames[i] + "%");
                if (data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity > data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity)
                    throw "error";
                document.getElementById(labelIDs[i]).innerHTML = data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity;
                if (data.Parkings.find(x => x.name === parkingNames[i]).open === false) {
                    document.getElementById(barIDs[i]).setAttribute("class", "progress-bar progress-bar-danger");
                    document.getElementById(barIDs[i]).innerHTML = "Closed";
                    document.getElementById(barIDs[i]).setAttribute("aria-valuenow", 100);
                    document.getElementById(barIDs[i]).setAttribute("style", "width: 100%");
                    document.getElementById(labelIDs[i]).innerHTML = "X";
                }
            } catch (err) {
                document.getElementById(barIDs[i]).setAttribute("class", "progress-bar progress-bar-danger");
                document.getElementById(barIDs[i]).innerHTML = "Geen gegevens beschikbaar";
                document.getElementById(barIDs[i]).setAttribute("aria-valuenow", 100);
                document.getElementById(barIDs[i]).setAttribute("style", "width: 100%");
                document.getElementById(labelIDs[i]).innerHTML = "X";
            }

        }
        var totaal = ParkingKnoppen.length;
        totaalMax = 0;
        totaalBezet = 0;
        for (var i = 0; i < ParkingKnoppen.length; i++) {
            try {

                if (document.getElementById(ParkingKnoppen[i]).checked === true) {
                    try {
                        totaalMax += data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity;
                        totaalBezet += (data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity - data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity);
                    } catch (err) {
                    }
                    document.getElementById(ParkingMains[i]).setAttribute("style", "display: true;");
                    localStorage.setItem(ParkingKnoppen[i], true);
                    if (data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity > data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity)
                        throw "error";
                } else {
                    document.getElementById(ParkingMains[i]).setAttribute("style", "display: none;");
                    localStorage.setItem(ParkingKnoppen[i], false);
                    totaal--;
                    procentNames[i] = 0;
                }
            } catch (err) {
            }
        }

        toggleParkingBar();

        makeDrag();
        //saveLocalStorage();
    } catch (err) {
        showError("avgParkingFree", "Er is geen informatie over parkeergarages beschikbaar");
    }
}

function toggleParkingBar() {
    var totalFree = totaalMax - totaalBezet;
    if (document.getElementById("ParkingBarBox").checked === true) {
        var avgProcent = totaalBezet / totaalMax * 100;
        document.getElementById("avgParkingFree").classList.remove("progress-bar-success");
        document.getElementById("avgParkingFree").innerHTML = avgProcent.toFixed(2) + "%";
        //document.getElementById("avgParkingFree").setAttribute("aria-valuenow", avgProcent);
        document.getElementById("avgParkingFree").setAttribute("style", "width:" + avgProcent + "%");
        document.getElementById("avgParkingUsed").innerHTML = totaalBezet;
        document.getElementById("avgParkingUsed").setAttribute("style", "width: 0%");
    } else {
        document.getElementById("avgParkingFree").classList.add("progress-bar-success");
        document.getElementById("avgParkingFree").innerHTML = totalFree;
        document.getElementById("avgParkingFree").setAttribute("style", "width:" + totalFree / totaalMax * 100 + "%");
        document.getElementById("avgParkingUsed").innerHTML = totaalBezet;
        document.getElementById("avgParkingUsed").setAttribute("style", "width:" + totaalBezet / totaalMax * 100 + "%");
        
    }
}

function ToggleParking() {

    toggleParking(parking);
    deleteParkingMarkers();
    checkParking();
}

function ToggleAllParkings() {
    for (var i = 0; i < ParkingKnoppen.length; i++) {
        $('#' + ParkingKnoppen[i]).bootstrapToggle('on');
    }
    ToggleParking();

}

