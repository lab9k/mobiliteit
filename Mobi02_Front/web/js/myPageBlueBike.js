/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function fillBlueBikeTab() {
    document.getElementById("BlueBikeTable").innerHTML = "";
    for (var i = 0; i < BBNamesMP.length; i++) {
        if (localStorage.getItem(BBFunctions[i]) !== "true") {
            var mp = document.getElementById("BlueBikeTable");
            var td = document.createElement("td");
            var tr = document.createElement("tr");
            var btn = document.createElement("button");
            btn.type = "button";
            btn.setAttribute("class", "btn btn-primary");
            btn.setAttribute("style", "width:100%");

            btn.setAttribute("onclick", "localStorage.setItem(\"" + BBFunctions[i] + "\",\"true\"); fillBlueBikeTab(); fillBlueBikeMyPage(BlueBike);");
            btn.innerHTML = BBNamesMP[i];
            td.appendChild(btn);
            tr.appendChild(td);
            mp.appendChild(tr);
        }
    }
    updateNotifs();
}

function fillBlueBikeMyPage(data) {

    try {
        var freePieters = 0;
        var totalPieters = 0;
        var maintPieters = 0;
        var usedPieters = 0;

        var geg = [];
        for (var i = 0; i < BBNamesBack.length; i++) {
            freePieters = data.BlueBike.find(x => x.name === BBNamesBack[i]).available;
            totalPieters = data.BlueBike.find(x => x.name === BBNamesBack[i]).totalCap;
            maintPieters = data.BlueBike.find(x => x.name === BBNamesBack[i]).inMaintenance;
            usedPieters = data.BlueBike.find(x => x.name === BBNamesBack[i]).inUse;
            if (localStorage.getItem(BBFunctions[i]) === "true") {
                var obj = {
                    station: BBNamesMP[i],
                    successWidth: freePieters / totalPieters * 100,
                    warningWidth: maintPieters / totalPieters * 100,
                    dangerWidth: usedPieters / totalPieters * 100,
                    successCount: freePieters,
                    warningCount: maintPieters,
                    dangerCount: usedPieters,
                    function: BBFunctions[i]

                };
                geg.push(obj);
            }
        }
    } catch (err) {
        for (var i = 0; i < BBNamesBack.length; i++) {
            if (localStorage.getItem(BBFunctions[i]) === "true") {
                var obj = {
                    station: BBNamesMP[i],
                    successWidth: 0,
                    warningWidth: 0,
                    dangerWidth: 100,
                    successCount: "",
                    warningCount: "",
                    dangerCount: "Er zijn momenteel geen gegevens beschikbaar!",
                    function: BBFunctions[i]
                };
                geg.push(obj);
            }
        }
    }
    document.getElementById("MPBlueBikeTable").innerHTML = "";
    var template = $('#bluebike-template').html();
    var html = Mustache.to_html(template, geg);
    $('#MPBlueBikeTable').html(html);
}