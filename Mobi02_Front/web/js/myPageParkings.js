/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function fillParkingTab() {
    document.getElementById("ParkingsMPTable").innerHTML = "";
    for (var i = 0; i < parkingNamesMP.length; i++) {
        if (localStorage.getItem(parkingFunctions[i]) !== "true") {
            var mp = document.getElementById("ParkingsMPTable");
            var td = document.createElement("td");
            var tr = document.createElement("tr");
            var btn = document.createElement("button");
            btn.type = "button";
            btn.setAttribute("class", "btn btn-primary");
            btn.setAttribute("style", "width:100%")

            btn.setAttribute("onclick", "localStorage.setItem(\"" + parkingFunctions[i] + "\",\"true\"); fillParkingTab(); fillParkingMyPage(parking);");
            btn.innerHTML = parkingNamesMP[i];
            td.appendChild(btn);
            tr.appendChild(td);
            mp.appendChild(tr);
        }
    }
    updateNotifs();
}

function fillParkingMyPage(data) {
    try {
        var procentP01, procentP02, procentP04, procentP07, procentP08, procentP10, procentPNMBS;
        var procentNames = [procentP01, procentP02, procentP04, procentP07, procentP08, procentP10, procentPNMBS];
        var parkingNames = ["P01 Vrijdagmarkt", "P02 Reep", "P04 Savaanstraat", "P07 Sint-Michiels", "P08 Ramen", "P10 Sint-Pietersplein", "Parking Gent St. Pieters"];

        var geg = [];

        document.getElementById("MPTable").innerHTML = "";
        for (i = 0; i < procentNames.length; i++) {
            try {

                if (localStorage.getItem(parkingFunctions[i]) === "true") {
                    var bezet = data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity - data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity;
                    var name = parkingNames[i] + " [" + bezet + "/" + data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity + "]";
                    var progressClass;
                    var procent = (1 - data.Parkings.find(x => x.name === parkingNames[i]).availableCapacity / data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity) * 100;
                    if (procent > 80) {
                        progressClass = "progress-bar progress-bar-danger my-progress-bar";
                    } else if (procent > 60) {
                        progressClass = "progress-bar progress-bar-warning my-progress-bar";
                    } else {
                        progressClass = "progress-bar progress-bar-success my-progress-bar";
                    }
                    var totalCap = data.Parkings.find(x => x.name === parkingNames[i]).totalCapacity;
                    var procentFixed = procent.toFixed(0) + "%";
                    if (data.Parkings.find(x => x.name === parkingNames[i]).open === false) {
                        progressClass = "progress-bar progress-bar-danger my-progress-bar";
                        procentFixed = "Closed";
                        procent = "100";
                    }
                    var obj = {
                        parkingName: name,
                        progressClass: progressClass,
                        procent: procent,
                        totalCap: totalCap,
                        procentFixed: procentFixed,
                        functionLS: parkingFunctions[i]
                    };
                    geg.push(obj);
                }
                var template = $('#parkings-template').html();
                var html = Mustache.to_html(template, geg);
                $('#MPTable').html(html);
            } catch (err) {
                if (localStorage.getItem(parkingFunctions[i]) === "true") {
                    var name = parkingNames[i] + " [Momenteel geen gegevens beschikbaar]";
                    var totalCap = 100;
                    var progressClass = "progress-bar progress-bar-danger my-progress-bar";
                    var procentFixed = "Closed";
                    var procent = "100";

                    var obj = {
                        parkingName: name,
                        progressClass: progressClass,
                        procent: procent,
                        totalCap: totalCap,
                        procentFixed: procentFixed,
                        functionLS: parkingFunctions[i]
                    };
                    geg.push(obj);
                }
            }
        }
    } catch (err) {
        console.log(err);
    }
    var table = document.getElementById("MPTable");
    document.getElementById("MPPaneel").innerHTML = "";
    document.getElementById("MPPaneel").appendChild(table);
}

