/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//var widgetID = "busPersDelayBar";
var errorMsg = "Er is geen informatie over bussen beschikbaar";


/*Addresses*/
var geocoder;
var map;
var Lijnstops;

function bussesAdress() {
    document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh fa-spin fa-3x fa-fw");
    document.getElementById("loadIcon").setAttribute("style", "font-size:2em;color:white;");
    if (!document.getElementById("addressBox").checked) {
        radiobtn = document.getElementById("addressBox");
        radiobtn.checked = true;
    }
    geocoder = new google.maps.Geocoder();
    codeAddress();
}

//method called when data is asked --> returns stops in environment
function getBussesByLocation(url, callback) {
    document.getElementById("locationBox").disabled = true;
    document.getElementById("addressBox").disabled = true;
    document.getElementById("radiusBar").disabled = true;
    document.getElementById("knopAdres").disabled = true;
    document.getElementById("addressLine").disabled = true;
    document.getElementById("addressLine").setAttribute("style", "background: #dddddd");
    //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #999999; background-color: #cccccc; color: #666666;");
    $.ajax({
        type: "GET",
        url: url,
        contentType: "application/json",
        success: function (data) {
            obj = data;
            callback(data);

        },
        error: function (errMsg) {
            document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus gevonden</td>";
            document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh");
            document.getElementById("loadIcon").setAttribute("style", "display:none");
            document.getElementById("locationBox").disabled = false;
            document.getElementById("addressBox").disabled = false;
            document.getElementById("radiusBar").disabled = false;
            document.getElementById("knopAdres").disabled = false;
            document.getElementById("addressLine").disabled = false;
            document.getElementById("addressLine").setAttribute("style", "background: #ffffff");
            //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #dddddd; background: #f2f2f2");
            // console.log(errMsg);
        }
    });
}

function compare(a, b) {
    if (parseInt((a.time).substring(0, 2)) < parseInt((b.time).substring(0, 2))) {
        return -1;
    } else {
        if (parseInt((a.time).substring(3, 5)) < parseInt((b.time).substring(3, 5))) {
            return -1;
        } else {
            return 1;
        }
    }
    return 0;
}

/*location*/

function bussesLocation() {
    document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh fa-spin fa-3x fa-fw");
    document.getElementById("loadIcon").setAttribute("style", "font-size:2em; color:white;");
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    } else {
        document.getElementById("locationBox").disabled = true;
    }
}



function showPosition(position) {
    var url = urlREST + position.coords.latitude
            + "/" + position.coords.longitude
            + "/" + document.getElementById("radiusBar").value;
    getBussesByLocation(url, getBussesFromHaltes);
}



function reloadBusses() {
    showVal();
    document.getElementById("locationBox").disabled = true;
    document.getElementById("addressBox").disabled = true;
    document.getElementById("radiusBar").disabled = true;
    document.getElementById("knopAdres").disabled = true;
    document.getElementById("addressLine").disabled = true;
    //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #999999; background-color: #cccccc; color: #666666;");
    document.getElementById("addressLine").setAttribute("style", "background: #dddddd");
    if (document.getElementById("locationBox").checked) {
        bussesLocation();
    } else if (document.getElementById("addressBox").checked) {
        bussesAdress();
    }
}

function codeAddress() {
    var address = document.getElementById('addressLine').value;
    geocoder.geocode({'address': address}, function (results, status) {
        if (status === 'OK') {
            var url = urlREST + results["0"].geometry.location.lat()
                    + "/" + results["0"].geometry.location.lng()
                    + "/" + document.getElementById("radiusBar").value;

            getBussesByLocation(url, getBussesFromHaltes);

        } else {
            alert('Geocode was not successful for the following reason: ' + status);
        }
    });
}

function showVal() {

    var x = document.getElementById("radiusBar").value;
    document.getElementById("radiusLabel").innerHTML = x;

}
var aantal = 0;
var lengte = 2;

function getBussesFromHaltes(data) {
    //gets a json array with max 3 stops.
    //needs to do a ajax call for Lijn busses at stops in the environment
    try {
        var json = data.DeLijnPersonal; //has stops
        Lijnstops = json;


        if (json.length < 2) {
            lengte = json.length;
        }
        for (var i = 0; i < lengte; i++) {
            try {
                var url = deLijnGeneric + json[i].halteNummer;
                $.ajax({
                    type: "GET",
                    url: url,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    success: function (data) {
                        aantal++;
                        fillDeLijnPersonal(data);

                    },
                    error: function (errMsg) {
                        //showError("busPersDelayBar", "Er is geen informatie over bussen beschikbaar");
                        document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus gevonden</td>";


                        //console.log(errMsg);
                    }
                });

            } catch (error) {
                document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus gevonden</td>";
            }

        }
        
    } catch (error) {
        document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus gevonden</td>";
        document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh");
        document.getElementById("loadIcon").setAttribute("style", "display:none");
        document.getElementById("locationBox").disabled = false;
        document.getElementById("addressBox").disabled = false;
        document.getElementById("radiusBar").disabled = false;
        document.getElementById("knopAdres").disabled = false;
        //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #dddddd;  background: #f2f2f2");
        document.getElementById("addressLine").setAttribute("style", "background: #ffffff");
        document.getElementById("addressLine").disabled = false;
        document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh");
        document.getElementById("loadIcon").setAttribute("style", "display:none");
        aantal = 0;
        //showError("busPersDelayBar", "Er is geen informatie over bussen beschikbaar");
    }



}






function fillDeLijnPersonal(data) {
    try {

        document.getElementById("radiusLabel").innerHTML = document.getElementById("radiusBar").value;
        //document.getElementById(widgetID).innerHTML = "";

        var json = data;
        var data = [];
        var delayed = 0;

        for (var i = 0; i < json.length; i++) {
            

            try {
                var bgcol = json[i].BackgroundColor;
                var bgcoled = json[i].BackgroundEdgeColor;
                var desc = json[i].Description;
                var fgcol = json[i].ForegroundColor;
                var fgcoled = json[i].ForegroundEdgeColor;
                var per = json[i].Platform;

                var stop = json[i].StopName;
                var target = json[i].Target;




                var plannedTimeRaw = json[i].PlannedTime;
                /*format: yyyy-mm-dd hh:mm:ss.ssss - only needed: hh:mm*/
                var index = plannedTimeRaw.indexOf(':'); //index of first :
                var plannedTimeFilterd = plannedTimeRaw.substring(index - 2, index + 3);
                var time = plannedTimeFilterd;


                var hulp = json[i].SortTime;
                var sortTimeRaw = hulp.date;

                /*format: yyyy-mm-dd hh:mm:ss.ssss - only needed: hh:mm*/
                var index = sortTimeRaw.indexOf(':'); //index of first :
                var sortTimeFilterd = sortTimeRaw.substring(index - 2, index + 3);
                if (json[i].PublicId == "L" || json[i].PublicId == "IC" || json[i].PublicId == "P") {
                    //it's not a bus but a train
                    throw "not a bus";
                } else {
                    var id = json[i].PublicId;
                }


                /*calculating delay*/
                var h1 = parseInt(plannedTimeFilterd.substring(0, 2));
                var h2 = parseInt(sortTimeFilterd.substring(0, 2));
                var m1 = parseInt(plannedTimeFilterd.substring(3, 5));
                var m2 = parseInt(sortTimeFilterd.substring(3, 5));
                var delay = (h2 - h1) * 60 + (m2 - m1);
                var hasDelay;
                var posDelay;
                if (delay > 0) {
                    delayed++;
                    hasDelay = true;
                    posDelay = true;
                } else if (delay < 0) {
                    posDelay = false;
                    hasDelay = true;
                    delayed++;
                } else if (delay === 0) {
                    hasDelay = false;
                }
                var img;
                if ((json[i].Vehicle).toLowerCase() === "bus") {
                    img = "img/bus.png";
                } else {
                    img = "img/tram.png";
                }
                var bus = {
                    bgcol: bgcol,
                    bgcoled: bgcoled,
                    desc: desc,
                    fgcol: fgcol,
                    fgcoled: fgcoled,
                    per: per,
                    time: time,
                    id: id,
                    delay: delay,
                    hasDelay: hasDelay,
                    posDelay: posDelay,
                    stop: stop,
                    target: target,
                    img: img
                };
                data.push(bus);
            } catch (error) {
                document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus gevonden</td>";
            }

        }
        
        document.getElementById("LijnFavBtn").disabled = false;


        data.sort(compare);
        if (data.length > 0) {
            var template = $('#bus-template').html();
            var html = Mustache.to_html(template, data);
            $('#busPers_main').html(html);
            var onTimeProcent = (1 - (delayed / json.length)) * 100;

            //document.getElementById("busPersDelayBar").innerHTML = onTimeProcent.toFixed(0) + "% op tijd";
            //document.getElementById("busPersDelayBar").setAttribute("aria-valuenow", onTimeProcent);
            //document.getElementById("busPersDelayBar").setAttribute("style", "width:" + onTimeProcent + "%");
            
        } else {
            document.getElementById("busPers_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele bus beschikbaar</td>";
            document.getElementById("busPers_main").setAttribute("style", "color:black");
            //document.getElementById("busPersDelayBar").innerHTML = "Geen bussen beschikbaar";
            //document.getElementById("busPersDelayBar").setAttribute("aria-valuenow", 100);
            //document.getElementById("busPersDelayBar").setAttribute("style", "width: 100%");
            //document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh");
            //document.getElementById("loadIcon").setAttribute("style", "display:none");
        }
    } catch (err) {
        //showError("busPersDelayBar", "Er is geen informatie over bussen beschikbaar");
    }
    if (aantal === lengte) {
        document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh");
        document.getElementById("loadIcon").setAttribute("style", "display:none");
        document.getElementById("locationBox").disabled = false;
        document.getElementById("addressBox").disabled = false;
        document.getElementById("radiusBar").disabled = false;
        document.getElementById("knopAdres").disabled = false;
        document.getElementById("addressLine").disabled = false;
        //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #dddddd; background:#f2f2f2");
        document.getElementById("addressLine").setAttribute("style", "background: #ffffff");
        aantal = 0;
    }
}



