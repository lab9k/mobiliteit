/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/* global data, Mustache */

var widgetID = "trafficbar";
var widgetID2 = "accidentsbar";
var errorMsg = "Er is geen informatie over vertragingen beschikbaar";

function fillCoyote(data) {
    try {
        document.getElementById(widgetID).innerHTML = "";
        document.getElementById(widgetID2).innerHTML = "";
        var jsonc = data.Coyote;
        var datac = [];
        var datac2 = [];
        var delaytotc = 0;
        
        if(jsonc.length === undefined){
            throw "error";
        }
        for (var i = 0; i < jsonc.length; i++) {
            if (jsonc[i].hasOwnProperty('minutes')) {
                var minutes = jsonc[i].minutes;
                var route = jsonc[i].route;
                var trafj = {
                    minutes: minutes,
                    route: route
                };
                
                if(minutes>4){
                    datac.push(trafj);
                    delaytotc += minutes;
                }
                
            } else {

                var street = jsonc[i].street;
                var type = jsonc[i].subtype;
                var speed = jsonc[i].speedLimit;
                var trafj2 = {
                    street: street,
                    type: type,
                    speed: speed
                };
                if (trafj2.street !== "") {
                    datac2.push(trafj2);
                }
            }
        }
        var template = $('#trafficjam-template').html();
        var html = Mustache.to_html(template, datac);
        $('#trafficjam_main').html(html);
        document.getElementById("trafficbar").innerHTML = delaytotc + " minuten vertraging.";

        var template = $('#accidents-template').html();
        var html = Mustache.to_html(template, datac2);
        $('#accidents_main').html(html);
        

        $("table#accidentsTable tr").click(function () {
            $("td", this).each(function (j) {
                if(j===0){route = $(this).text();}
                else if (j===1){type = $(this).text();}                
            });
            
            centerAccidents(route, type);
        });
        
        //makeDrag();
        
    } catch (err) {
        showError("trafficbar", "Er is geen informatie over vertragingen beschikbaar");
        showError("accidentsbar", "Er is geen informatie over ongevallen beschikbaar");
    }


}