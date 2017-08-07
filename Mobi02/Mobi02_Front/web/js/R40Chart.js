/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



var R40Chart;

var labels = [];
var todayData = [];
var histData = [];

function toggleGraph() {
    if (document.getElementById("GraphSwitchBox").checked === true) {
        //Reistijd
        fillTraveltimeSelect();
        localStorage.setItem("GraphSwitch", true);
    } else {
        //Tellingen
        fillStationSelect();
        localStorage.setItem("GraphSwitch", false);
    }
}

function fillStationTables(station) {
    todayData = [];
    histData = [];
    if (document.getElementById("R40DayPicker").value === "Vandaag") {
        getTodayStationData(station);
    }
    getAvgTodayStationData(station);
    fillGraph(todayData, histData);
}

function fillTraveltimeTables(orientation) {
    todayData = [];
    histData = [];
    if (document.getElementById("R40DayPicker").value === "Vandaag") {
        getTodayTraveltimeData(orientation);
    }
    getAvgTodayTraveltimeData(orientation);
    fillGraph(todayData, histData);
}

function getTodayTraveltimeData(orientation) {
    $.ajax({
        type: "GET",
        url: traveltimeUrl,
        contentType: "application/json",
        success: function (data) {
            if (orientation === "Wijzerzin") {
                var orientIndex = 2;
            } else {
                orientIndex = 1;
            }
            for (i = 0; i < data.length; i++) {
                todayData[data[i][0]] = data[i][orientIndex];
            }
            fillGraph(todayData, histData);

        },
        error: function (errMsg) {
            console.log(errMsg);
        }

    });
}


function getAvgTodayTraveltimeData(orientation) {
    var dayNr = $("#R40DayPicker option:selected").index();
    $.ajax({
        type: "GET",
        url: traveltimeUrl + "hist/" + dayNr + "/",
        contentType: "application/json",
        success: function (data) {
            if (orientation === "Wijzerzin") {
                var orientIndex = 2;
            } else {
                orientIndex = 1;
            }
            for (i = 0; i < data.length; i++) {
                histData[data[i][0]] = data[i][orientIndex];
            }
            fillGraph(todayData, histData);
        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}


function getTodayStationData(station) {
    if (station === "Totaal") {
        var value = "total";
    } else {
        var value = station;
    }
    $.ajax({
        type: "GET",
        url: graphUrl + value,
        contentType: "application/json",
        success: function (data) {
            for (i = 0; i < data.length; i++) {
                todayData[data[i][0]] = data[i][1];
            }
            fillGraph(todayData, histData);

        },
        error: function (errMsg) {
            console.log(errMsg);
        }

    });
}


function getAvgTodayStationData(station) {
    var dayNr = $("#R40DayPicker option:selected").index();
    if (station === "Totaal") {
        var value = "total";
    } else {
        var value = station;
    }
    $.ajax({
        type: "GET",
        url: graphUrl + "hist/" + value + "/" + dayNr + "/",
        contentType: "application/json",
        success: function (data) {
            for (i = 0; i < data.length; i++) {
                histData[data[i][0]] = data[i][1];
            }
            fillGraph(todayData, histData);
        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}


function selectChange() {
    if (document.getElementById("GraphSwitchBox").checked === false) {
        fillStationTables(document.getElementById('R40Picker').value);
        centerR40(document.getElementById('R40Picker').value);
    } else {
        fillTraveltimeTables(document.getElementById('R40Picker').value);
    }
}

function fillStationSelect() {
    $.ajax({
        type: "GET",
        url: selectUrl,
        contentType: "application/json",
        success: function (data) {
            $("#R40Picker").empty();
            var sel = document.getElementById('R40Picker');
            var opt = document.createElement('option');
            var point = "Totaal";
            opt.innerHTML = point;
            opt.value = point;
            sel.appendChild(opt);
            for (var i = 0; i < data.length; i++) {
                var opt = document.createElement('option');
                var point = data[i];
                opt.innerHTML = point;
                opt.value = point;
                sel.appendChild(opt);
            }
            $("#R40Picker").selectpicker('refresh');

            fillStationTables(document.getElementById('R40Picker').value);

        },
        error: function (errMsg) {
            console.log(errMsg);
        }

    });

    labels = [];
    for (i = 0; i < 24; i++) {
        //labels
        labels[i] = i + 1 + ":00";
    }

    fillDays();

}

function fillTraveltimeSelect() {
    $("#R40Picker").empty();
    var sel = document.getElementById('R40Picker');
    var opt = document.createElement('option');
    var point = "Wijzerzin";
    opt.innerHTML = point;
    opt.value = point;
    sel.appendChild(opt);
    var opt = document.createElement('option');
    var point = "Tegen wijzerzin";
    opt.innerHTML = point;
    opt.value = point;
    sel.appendChild(opt);
    $("#R40Picker").selectpicker('refresh');

    labels = [];
    for (i = 0; i < 24; i++) {
        //labels
        labels[i] = i + 1 + ":00";
    }

    fillDays();

    fillTraveltimeTables(document.getElementById('R40Picker').value);


}

function fillDays() {
    var d = new Date();
    var todayNr = d.getDay();
    var days = ["Zondag", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag"];
    $("#R40DayPicker").empty();
    var sel = document.getElementById('R40DayPicker');
    for (var k = 0; k < 7; k++) {
        if (k === todayNr) {
            var opt = document.createElement('option');
            var point = "Vandaag";
            opt.innerHTML = point;
            opt.value = point;
            opt.selected = true;
            sel.appendChild(opt);
        } else {
            var opt = document.createElement('option');
            var point = days[k];
            opt.innerHTML = point;
            opt.value = point;
            sel.appendChild(opt);
        }
    }
    $("#R40DayPicker").selectpicker('refresh');
}

function fillGraph(todayData, histData) {
    if (R40Chart) {
        R40Chart.destroy();
    }
    var ctx = document.getElementById("R40Chart");
    R40Chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: "Vandaag",
                    data: todayData,
                    borderColor: 'rgba(255, 0, 0, 1)',
                    backgroundColor: 'rgba(255, 0, 0, 0.2)'
                },
                {
                    label: "Gemiddeld",
                    data: histData,
                    borderColor: 'rgba(0, 255, 0, 1)',
                    backgroundColor: 'rgba(0, 255, 0, 0.2)'
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: true,
            scales: {
                yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
            }
        }
    });
    makeDrag();
}
