/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function saveLijn() {
    var fav = "";
    fav += document.getElementById("addressLine").value;
    fav += "?";
    for (var i = 0; i < Lijnstops.length; i++) {
        fav += Lijnstops[i].halteNummer;
        fav += "?";
    }


    LijnFav.push(fav);

    var uniqueNames = [];
    $.each(LijnFav, function (i, el) {
        if ($.inArray(el, uniqueNames) === -1)
            uniqueNames.push(el);
    });
    LijnFav = uniqueNames;
    localStorage.setItem("LijnFav", JSON.stringify(LijnFav));

    updateLijnFav();
}

function updateLijnFav() {
    document.getElementById("LijnFavTable").innerHTML = "";
    var data = [];
    for (var i = 0; i < LijnFav.length; i++) {
        var string = LijnFav[i].split("?");
        var LijnFavorite = {
            stop: string[0],
            index: i
        };
        data.push(LijnFavorite);

    }
    var template = $('#LijnFavo-template').html();
    var html = Mustache.to_html(template, data);
    $('#LijnFavTable').html(html);
}
function LijnLoadFav(index) {
    document.getElementById("loadIcon").setAttribute("class", "fa fa-refresh fa-spin fa-3x fa-fw");
    document.getElementById("loadIcon").setAttribute("style", "font-size:2em;color:white;");
    document.getElementById("locationBox").disabled = true;
    document.getElementById("addressBox").disabled = true;
    document.getElementById("radiusBar").disabled = true;
    document.getElementById("knopAdres").disabled = true;
    document.getElementById("addressLine").disabled = true;
    document.getElementById("addressLine").setAttribute("style", "background: #dddddd");
    //document.getElementById("knopAdres").setAttribute("style", "border: 1px solid #999999; background-color: #cccccc; color: #666666;");
    var toLoad = LijnFav[index];

    var array = toLoad.split("?");
    document.getElementById("addressLine").value = array[0];
    lengte = array.length - 2;
    aantal = 0;

    for (var i = 1; i < array.length - 1; i++) {
        try {
            var url = deLijnGeneric + array[i];
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


                    console.log(errMsg);
                }
            });

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
        }

    }

    UpdateMP();
}

function LijnDeleteFav(index) {
    LijnFav.splice(index, 1);
    updateLijnFav();
    localStorage.setItem("LijnFav", JSON.stringify(LijnFav));

}
