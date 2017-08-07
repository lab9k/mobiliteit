/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function fillCoyoteMyPage(data) {
    
    var lijst = document.getElementById("CoyoteTable");
    lijst.innerHTML = "";
    for (var i = 0; i < routesLeft.length; i++) {
        var td1 = document.createElement("td");
        var tr1 = document.createElement("tr");
        var tussen = routesLeft[i];
        td1.innerHTML = tussen;
        td1.setAttribute("onClick", "moveLeftCoyote(\"" + routesLeft[i] + "\")");
        tr1.appendChild(td1);
        lijst.appendChild(tr1);
    }
    var lijst = document.getElementById("CoyoteTable2");
    lijst.innerHTML = "";
    for (var i = 0; i < routesRight.length; i++) {
        var td1 = document.createElement("td");
        var tr1 = document.createElement("tr");
        var tussen = routesRight[i];
        td1.innerHTML = tussen;
        td1.setAttribute("onClick", "moveRightCoyote(\"" + routesRight[i] + "\")");
        tr1.appendChild(td1);
        lijst.appendChild(tr1);
    }

    fillCoyote(data);
}
function moveRightCoyote(best) {
    document.getElementById("CoyoteInput2").value = best;

}
function moveLeftCoyote(best) {
    document.getElementById("CoyoteInput").value = best;

}

function CoyotePlus() {
    
    var index = routesLeft.indexOf(document.getElementById("CoyoteInput").value);
    if (index !== -1)
    {
        if (index > -1) {
            routesLeft.splice(index, 1);
        }
        routesRight.push(document.getElementById("CoyoteInput").value);
        var uniqueNames = [];
        $.each(routesRight, function (i, el) {
            if ($.inArray(el, uniqueNames) === -1)
                uniqueNames.push(el);
        });
        routesRight = uniqueNames;
        localStorage.setItem("routesRight", JSON.stringify(routesRight));
        localStorage.setItem("routesLeft", JSON.stringify(routesLeft));
        fillCoyoteMyPage(Coyote);
        document.getElementById("CoyoteInput").value = "";
        document.getElementById("CoyoteInput2").value = "";
        updateNotifs();
    }

}
function CoyoteMin() {
    var index = routesRight.indexOf(document.getElementById("CoyoteInput2").value);
    if (index !== -1)
    {
        if (index > -1) {
            routesRight.splice(index, 1);
        }
        routesLeft.push(document.getElementById("CoyoteInput2").value);
        var uniqueNames = [];
        $.each(routesLeft, function (i, el) {
            if ($.inArray(el, uniqueNames) === -1)
                uniqueNames.push(el);
        });
        routesLeft = uniqueNames;
        localStorage.setItem("routesRight", JSON.stringify(routesRight));
        localStorage.setItem("routesLeft", JSON.stringify(routesLeft));
        fillCoyoteMyPage(Coyote);
        document.getElementById("CoyoteInput").value = "";
        document.getElementById("CoyoteInput2").value = "";
        updateNotifs();
    }

}

function openCoyoteModal(getal) {
    var modal = document.getElementById('myCoyoteModal');
    var btn = document.getElementById("spanCoyoteClick");
    var span = document.getElementsByClassName("Coyoteclose")[0];
    document.getElementById("CoyoteModalName").innerHTML = "<b>" + Coyote.Coyote[getal].route;
    document.getElementById("CoyoteModalLength").innerHTML = "Totale lengte: " + Coyote.Coyote[getal].length + " km.";
    var nt = Coyote.Coyote[getal].normal_time;
    var rt = Coyote.Coyote[getal].real_time;
    var verhouding = nt / rt;
    var test = transfer(rt - nt);
    document.getElementById("CoyoteModalNormalTime").innerHTML = "Gemiddelde tijd: " + transfer(nt);
    document.getElementById("CoyoteModalRealTime").innerHTML = "Voorspelde tijd: " + transfer(rt);
    document.getElementById("CoyoteModalFileTime").innerHTML = "Minuten file: " + test;
    document.getElementById("CoyoteModalSuccess").innerHTML = transferSeconden(nt) + " min";
    document.getElementById("CoyoteModalSuccess").setAttribute("style", "width:" + verhouding * 100 + "%");
    document.getElementById("CoyoteModalDanger").innerHTML = transferSeconden(rt - nt) + " min";
    document.getElementById("CoyoteModalDanger").setAttribute("style", "width:" + (1 - verhouding) * 100 + "%");
    btn.onclick = function () {
        modal.style.display = "block";
    }
    btn.click();
    span.onclick = function () {
        modal.style.display = "none";
    }

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    }
}

function fillCoyote(data) {
    document.getElementById("coyote_main").innerHTML = "";
    if (data !== undefined) {
        var totalDelay = 0;
        var totalRT = 0;
        var totalNT = 0;
        var totalLength = 0;
        var json = data.Coyote;
        var data = [];
        for (var i = 0; i < json.length; i++) {
            if (json[i].hasOwnProperty("length")) {
                for (var j = 0; j < routesRight.length; j++) {
                    if (routesRight[j] === json[i].route) {
                        var obj = {
                            route: json[i].route,
                            minutes: transferSeconden(json[i].real_time - json[i].normal_time),
                            normal_time: json[i].normal_time,
                            real_time: json[i].real_time,
                            length: json[i].length,
                            idx: i
                        };
                        totalRT += json[i].real_time;
                        totalNT += json[i].normal_time;
                        totalLength += json[i].length;
                        data.push(obj);
                    }
                }
            }
        }
        totalDelay = totalRT - totalNT;

        document.getElementById("CoyoteModalSuccessTotal").innerHTML = transferSeconden(totalNT);
        document.getElementById("CoyoteModalDangerTotal").innerHTML = transferSeconden(totalDelay);
        document.getElementById("CoyoteDelay").innerHTML = transferSeconden(totalDelay) + " minuten vertraging";
        document.getElementById("CoyoteRealTime").innerHTML = "Voorspelde tijd: " + transferSeconden(totalRT);
        document.getElementById("CoyoteNormalTime").innerHTML = "Gemiddelde tijd: " + transferSeconden(totalNT);
        document.getElementById("CoyoteLength").innerHTML = "Totale lengte traject: " + Math.round(totalLength) + " km";
        var verhouding = totalNT / totalRT;
        document.getElementById("CoyoteModalSuccessTotal").setAttribute("style", "width:" + verhouding * 100 + "%");
        document.getElementById("CoyoteModalDangerTotal").setAttribute("style", "width:" + (1 - verhouding) * 100 + "%");
        if (data.length > 0) {
            var template = $('#coyote-template').html();
            var html = Mustache.to_html(template, data);
            $('#coyote_main').html(html);

        }
    }
}


function getRoutes() {
    $.ajax({
        type: "GET",
        url: CoyoteRoutes,
        async: false,
        contentType: "application/json",
        success: function (data) {
           
            localStorage.setItem("routesLeft", JSON.stringify(data));

        },
        error: function (errMsg) {
            
            console.log(errMsg);
        }
    });
}




function transfer(number) {
    var first = Math.floor(number);


    var inbetween = number - first;
    inbetween = inbetween * 60;
    inbetween = inbetween.toFixed(0);
    var sentence = first + " minuten en " + inbetween + " seconden";
    return sentence;
}

function transferSeconden(number) {
    var first = Math.floor(number);


    var inbetween = number - first;
    inbetween = inbetween * 60;
    inbetween = inbetween.toFixed(0);
    if (inbetween.toString().length == 1) {
        inbetween = "0" + inbetween;
    }
    var sentence = first + ":" + inbetween + "";
    return sentence;
}

function vulisIn() {
    document.getElementById("nt").innerHTML = transfer(normal_time);
}
