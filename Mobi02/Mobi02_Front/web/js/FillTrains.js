/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global data, Mustache */

var widgetID = "trainDelayBar";
var errorMsg = "Er is geen informatie over treinen beschikbaar";

function fillTrains(data) {
    try {
        document.getElementById(widgetID).innerHTML = "";
        //var json = data.TrainsGhent;
        var json = data.Trains;
        var data = [];
        var delayed = 0;
        for (var i = 0; i < json.length; i++) {
            if (json[i].delay > 0) {
                delayed++;
                //console.log(json[i].delay);
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
                }
                var station = json[i].station;
                var train = {
                    dest: tdest,
                    delay: tdelay,
                    platf: tplatf,
                    deptime: time,
                    number: tnumb,
                    img: img,
                    station: station
                };
                //console.log(train);
                data.push(train);
            }
        }
        if (data.length > 0) {
            var template = $('#trains-template').html();
            var html = Mustache.to_html(template, data);
            $('#trains_main').html(html);
            var onTimeProcent = (1 - (delayed / json.length)) * 100;
            document.getElementById("trainDelayBar").innerHTML = onTimeProcent.toFixed(0) + "% op tijd";
            document.getElementById("trainDelayBar").setAttribute("aria-valuenow", onTimeProcent);
            document.getElementById("trainDelayBar").setAttribute("style", "width:" + onTimeProcent + "%");
        } else {
            document.getElementById("trains_main").innerHTML = "<td colspan=\"4\" style=\"border-bottom: none;\">Geen enkele trein heeft vertraging</td>";
            document.getElementById("trains_main").setAttribute("style", "color:black");
            document.getElementById("trainDelayBar").innerHTML = "100% op tijd";
            document.getElementById("trainDelayBar").setAttribute("aria-valuenow", 100);
            document.getElementById("trainDelayBar").setAttribute("style", "width: 100%");
        }
        
        //makeDrag();


    } catch (err) {
        showError("trainDelayBar", "Er is geen informatie over treinen beschikbaar");
    }
}

