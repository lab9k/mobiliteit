/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global data */

var widgetID = "roadeventbar";
var errorMsg = "Er is geen informatie over incidenten beschikbaar";

function fillWaze(data){
    
    //"pubDate":"1489000283889","type":"ROAD_CLOSED","subtype":"ROAD_CLOSED_EVENT",
     //       "city":"Eke","street":"Kortebosstraat","latitude":50.973417,"longitude":3.652298
    try {
        document.getElementById(widgetID).innerHTML = "";
        var json = data.Waze;
        var data = [];
        var straten = [];
        
        for (var i = 0; i < json.length; i++) {
            var rtype = json[i].type;
            var rsubtype = json[i].subtype;
            var rcity = json[i].city;
            var rstreet = json[i].street;
            var rdescription = json[i].description;
            var roadevent = {
                type: rtype,
                subtype: rsubtype,
                city: rcity,
                street: rstreet,
                description: rdescription
            };
            
            if ($.inArray(rstreet, straten) === -1) {
                straten.push(rstreet);
                data.push(roadevent);
                //console.log(rstreet);
            }
            
        }
        
       
        if (data.length > 0) {
            var template = $('#roadevent-template').html();
            var html = Mustache.to_html(template, data);
            $('#roadevent_main').html(html);
        }
        
        $("table#wazeTable tr").click(function () {
            $("td", this).each(function (j) {
                if(j===0){route = $(this).text();}               
            });
            
            centerWaze(route);
        });
        
        //makeDrag();
        
        
        
    }catch (err) {
        showError("roadeventbar", "Er is geen informatie over incidenten beschikbaar");
    }
}