/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var widgetID = "gipodbar";
var errorMsg = "Er is geen informatie over vertragingen beschikbaar";

function fillGipod(data) {
    try {
        document.getElementById(widgetID).innerHTML = "";
        var jsonc = data.Gipod;
        if(jsonc.hasOwnProperty('ApiHttpRequestException')){
            throw "exceptie";
        }
        var datac = [];
        for (var i = 0; i < jsonc.length; i++) {

            var begin = jsonc[i].startDate;
            var end = jsonc[i].endDate;
            var description = jsonc[i].description;
            var gipodItem = {
                begin: begin,
                end: end,
                description: description
            };
            datac.push(gipodItem);
        }
        var template = $('#gipod-template').html();
        var html = Mustache.to_html(template, datac);
        $('#gipod_main').html(html); 
        
        $("table#gipodTable tr").click(function () {
            $("td", this).each(function (j) {
                if(j===2){description = $(this).text();}                
            });
            
            centerGipod(description);
        });
       
        //makeDrag();

    } catch (err) {
        showError("gipodbar", "Er is geen informatie over wegenwerken beschikbaar");
    }
}
