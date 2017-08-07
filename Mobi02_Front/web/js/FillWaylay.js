/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/* global data */

var widgetID = "waylaybar";
var errorMsg = "Er is geen informatie over vertragingen beschikbaar";

function fillWaylay(data) {
    try {
        document.getElementById(widgetID).innerHTML = "";
        var jsonc = data.Waylay;
        if(jsonc.hasOwnProperty('ApiHttpRequestException')){
            throw "exceptie";
        }
        var datac = [];
        for (var i = 0; i < jsonc.length; i++) {

            var type = jsonc[i].type;
            var subtype = jsonc[i].subtype;
            var street = jsonc[i].street;
            var trafj = {
                type: type,
                subtype: subtype,
                street: street
            };
            if(street !== undefined){
                datac.push(trafj);
            }

            

        }
        var template = $('#waylaydelay-template').html();
        var html = Mustache.to_html(template, datac);
        $('#waylaydelay_main').html(html); 
        
        $("table#waylayTable tr").click(function () {
            $("td", this).each(function (j) {
                if(j===0){route = $(this).text();}
                else if (j===1){type = $(this).text();}                
            });
            
            centerWaylay(route, type);
        });
        
        //makeDrag();
        

    } catch (err) {
        showError("waylaybar", "Er is geen informatie over vertragingen beschikbaar");
    }
}