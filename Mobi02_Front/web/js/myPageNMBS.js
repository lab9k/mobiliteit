/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function fillNMBSMyPage(data) {

    var lijst = document.getElementById("myTable");
    document.getElementById("myTable").innerHTML = "";
    for (var i = 0; i < bestemmingen.length; i++) {

        var td1 = document.createElement("td");
        var tr1 = document.createElement("tr");
        var appel = bestemmingen[i];
        td1.innerHTML = appel;
        td1.setAttribute("onClick", "moveUpSource(\"" + bestemmingen[i] + "\")");
        tr1.appendChild(td1);
        lijst.appendChild(tr1);
    }
    var lijst = document.getElementById("myTable2");
    document.getElementById("myTable2").innerHTML = "";
    for (var i = 0; i < bestemmingen.length; i++) {

        var td1 = document.createElement("td");
        var tr1 = document.createElement("tr");
        var appel = bestemmingen[i];
        td1.innerHTML = appel;
        td1.setAttribute("onClick", "moveUpDest(\"" + bestemmingen[i] + "\")");
        tr1.appendChild(td1);
        lijst.appendChild(tr1);
    }
}

function moveUpSource(name) {
    document.getElementById("nmbsInput").value = name;
}
function moveUpDest(name) {
    document.getElementById("nmbsInput2").value = name;

}

function saveNMBS() {
    if (bestemmingen.includes(document.getElementById("NMBSOorsprong").value) === true &&
            bestemmingen.includes(document.getElementById("NMBSBestemming").value) === true) {
        var fav = "";
        fav += document.getElementById("NMBSOorsprong").value;
        fav += "?";
        fav += document.getElementById("NMBSBestemming").value;
        fav += "?";
        //eerst de tijd nog parsen naar enkel uur, datum moet die van vandaag worden, sla gewoon tijd op
        //en bij 't inladen via de knop doe je eerste de nu functie, erna pas je de tijd aan naar deze

        //console.log(document.getElementById("NMBSTime").value);
        var time = document.getElementById("NMBSTime").value;
        var splitTime = time.split("T");
        var justTime = splitTime[1].split(":");
        fav += justTime[0] + ":" + justTime[1];

        //console.log(fav);
        NMBSFav.push(fav);

        var uniqueNames = [];
        $.each(NMBSFav, function (i, el) {
            if ($.inArray(el, uniqueNames) === -1)
                uniqueNames.push(el);
        });
        NMBSFav = uniqueNames;
        localStorage.setItem("NMBSFav", JSON.stringify(NMBSFav));
    }
    updateNMBSFav();
}

function updateNMBSFav() {
    document.getElementById("NMBSFavTable").innerHTML = "";
    var data = [];
    for (var i = 0; i < NMBSFav.length; i++) {
        var string = NMBSFav[i].split("?");
        var datum = string[2];
        var NMBSFavorite = {
            origin: string[0],
            destination: string[1],
            time: datum,
            index: i
        };
        data.push(NMBSFavorite);
    }
    var template = $('#NMBSFavo-template').html();
    var html = Mustache.to_html(template, data);
    $('#NMBSFavTable').html(html);
    updateNotifs();
}
function NMBSLoadFav(index) {
    var toLoad = NMBSFav[index];
    //console.log(toLoad);
    var array = toLoad.split("?");
    document.getElementById("NMBSOorsprong").value = array[0];
    document.getElementById("NMBSBestemming").value = array[1];

    var currentdate = new Date();

    var month = (currentdate.getMonth() + 1);
    if (month.toString().length === 1) {
        month = "0" + month;
    }
    var date = currentdate.getDate();
    if (date.toString().length === 1) {
        date = "0" + date;
    }

    var datetime = currentdate.getFullYear() + "-"
            + month + "-"
            + date + "T"
            + array[2] + ":"
            + "00";



    //console.log(datetime);
    $("#NMBSTime").val(datetime);
    UpdateMP();
}

function NMBSDeleteFav(index) {
    NMBSFav.splice(index, 1);
    updateNMBSFav();
    localStorage.setItem("NMBSFav", JSON.stringify(NMBSFav));

}
function getNMBSRoutes() {
    if (bestemmingen.includes(document.getElementById("NMBSOorsprong").value) === true &&
            bestemmingen.includes(document.getElementById("NMBSBestemming").value) === true) {
        var time = document.getElementById("NMBSTime").value;
        //console.log(time);
        var reststring = myPageNMBS;
        var from = document.getElementById("NMBSOorsprong").value.split("/")[0].replace(" ", "+");
        //if there are multiple spaces
        while(from.indexOf(' ') >= 0){
            from = from.replace(" ","+");
        }
        //console.log(from);
        reststring += from;
        var to = document.getElementById("NMBSBestemming").value.split("/")[0].replace(" ", "+")
        while(to.indexOf(' ') >= 0){
            to = to.replace(" ","+");
        }
        reststring += "/" + to;
        reststring += "/";
        reststring += time.substring(8, 10);
        //console.log(reststring);
        reststring = reststring.concat(time.substring(5, 7));
        reststring = reststring.concat(time.substring(2, 4));
        reststring += "/";
        reststring = reststring.concat(time.substring(11, 13));
        reststring = reststring.concat(time.substring(14, 16));
        // HIER OPTIE VOOR ARRIVAL OF DEPART
        reststring += "/" + $('input[name="NMBSRadio"]:checked').val();
        
        document.getElementById("nmbsWarning").setAttribute("style", "display:none");
        routesNMBScall(reststring);
    } else {
        document.getElementById("nmbsWarning").setAttribute("style", "display:true");


    }

}

function routesNMBScall(string) {
    $.ajax({
        type: "GET",
        url: string,
        contentType: "application/json",
        success: function (data) {
            var json = [];
            nmbstrains = data;


            fillTrains(data, 2);
        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}

function routesNMBSStations() {
    $.ajax({
        type: "GET",
        url: stopsUrl,
        contentType: "application/json",
        success: function (data) {
            bestemmingen = data;
            bestemmingenR = data;
            UpdateMP();
        },
        error: function (errMsg) {
            console.log(errMsg);
        }
    });
}

function thisTime() {
    var currentdate = new Date();

    var month = (currentdate.getMonth() + 1);
    if (month.toString().length === 1) {
        month = "0" + month;
    }
    var date = currentdate.getDate();
    if (date.toString().length === 1) {
        date = "0" + date;
    }
    var hours = currentdate.getHours();
    if (hours.toString().length === 1) {
        hours = "0" + hours;
    }
    var mins = currentdate.getMinutes();
    if (mins.toString().length === 1) {
        mins = "0" + mins;
    }
    var secs = currentdate.getSeconds();
    if (secs.toString().length === 1) {
        secs = "0" + secs;
    }
    var datetime = currentdate.getFullYear() + "-"
            + month + "-"
            + date + "T"
            + hours + ":"
            + mins + ":"
            + secs;




    $("#NMBSTime").val(datetime);
}
function SourceAdd() {
    document.getElementById("NMBSOorsprong").value = document.getElementById("nmbsInput").value;
    UpdateMP();
}
function DestinationAdd() {
    document.getElementById("NMBSBestemming").value = document.getElementById("nmbsInput2").value;
    UpdateMP();
}

function fillTrains(data, amount) {
    try {
        var totalArray = data;
        //var json = data;
        var data = [];
        var delayed = 0;

        for (var j = 0; j < amount; j++) {
            var json = totalArray[j];
            //need to move the second station, aka arrival, to the back, so that via's are between arrival and departure
            //if to check that second is an arrival train
            if (json[1].routeType === "arrival") {
                var endstation = json[1];
                json.splice(1, 1);
                json.push(endstation);
            }



            for (var i = 0; i < json.length; i++) {

                delayed++;

                var d = new Date(0);
                d.setUTCSeconds(json[i].deptime);
                var minutes = d.getMinutes();
                if (minutes < 10) {
                    minutes = "0" + minutes;
                }
                var time = d.getHours() + ":" + minutes;
                var tdest = json[i].dest;
                var tdelay = json[i].delay / 60;
                var tplatf = json[i].platform;
                var tnumb = json[i].vehicle.substring(8, 14);
                var img;

                if (tnumb.charAt(0) === 'P') {
                    img = "http://www.belgianrail.be/as/hafas-res/img/products/p.png";
                } else if (tnumb.charAt(0) === 'I') {
                    img = "http://www.belgianrail.be/as/hafas-res/img/products/ic.png";
                } else if (tnumb.charAt(0) === 'L') {
                    img = "http://www.belgianrail.be/as/hafas-res/img/products/l.png";
                } else {
                    img = "http://www.belgianrail.be/as/hafas-res/img/products/ic.png";
                }
           
                var station = json[i].station;
                var type;
                var display = "true";
                var via = "none"; // is true if theres a need for a 'via' button
                var trclass = ""; //moet via zijn zodat die op display dervan op true kan komen!
                var btnid = "viaBtn" + j;
                if (json[i].routeType === "departure") {
                    type = "Vertrek";
                    if (json.length !== 2) { //no via knop needed if no via (aka array longer than 2)
                        via = "true";
                    }
                } else if (json[i].routeType === "arrival") {
                    type = "Aankomst";
                } else {
                    type = json[i].routeType;
                    display = "none";
                    trclass = "via" + j;
                }
                var train = {
                    dest: tdest,
                    delay: tdelay,
                    platf: tplatf,
                    deptime: time,
                    number: tnumb,
                    img: img,
                    station: station,
                    routeType: type,
                    display: display,
                    via: via,
                    amount: j,
                    btnid: btnid,
                    trclass: trclass
                };

                data.push(train);

            }
        }

        if (data.length > 0) {

            var template = $('#trains-template').html();
            var html = Mustache.to_html(template, data);
            $('#trains_main').html(html);
            amount += 1;
            document.getElementById("trains_button").innerHTML = "<button type=\"button\" class=\"btn btn-primary\" onclick=\"fillTrains(nmbstrains," + amount + "\)\">Later</button>";
            document.getElementById("nmbsWarningDown").setAttribute("style", "display:none");
            
        } else {
            

        }


    } catch (err) {
        document.getElementById("nmbsWarningDown").setAttribute("style", "display:true");
        document.getElementById("nmbsWarningDown").innerHTML="Er konden geen treinen gevonden worden.";
        document.getElementById("trains_main").innerHTML = "";
        document.getElementById("trains_button").innerHTML = "";
    }
}
//function to show and hide the via tab of trains (uses class and via = number behind it)
function toggleVia(via) {
    var x = document.getElementsByClassName("via" + via);
    for (var i = 0; i < x.length; i++) {
        if (x[i].getAttribute("style") === "display:true; background-color:LightGray;") {
            x[i].setAttribute("style", "display:none; background-color:LightGray;");
            document.getElementById("viaBtn" + via).innerHTML = "Toon </br> overstappen";
        } else {
            x[i].setAttribute("style", "display:true; background-color:LightGray;");
            document.getElementById("viaBtn" + via).innerHTML = "Verberg </br> overstappen";
        }
    }



}
