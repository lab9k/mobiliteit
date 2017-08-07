var ADMINAUTH;
var socket;
var statusJson;
var latestClickedWidgetType;

var jsonMap = {
    WEATHER: "",
    BLUEBIKE: "",
    PARKING: "",
    // TRAIN: "",
    //DELIJN: "",
    PARKANDRIDE: "",
    NMBS: "",
    COYOTE: "",
    COUNTPOINT: "",
    GIPOD: ""
};

function onMessage(event) {
    var data = JSON.parse(event.data);
    console.log(data);
    if (data.action === "settings") {
        $("#settingsTitle").html(data.content.title);
        if (data.content.schema) {
            document.getElementById("schemaForm:schemaButton").style.display = 'block';
            document.getElementById("schemaForm:schemaFile").value = data.content.widgetType;
            document.getElementById("schemaForm:schemaType").value = data.content.schemaType;
        } else
            document.getElementById("schemaForm:schemaButton").style.display = 'none';
        $("#settingsDescription").html(data.content.description);
        document.getElementById("urlField").value = data.content.url;

    } else {
        jsonMap[data.widgetType] = data;
        parseMessage(data);
    }

}
window.onload = init;

function init() {
    socket = new WebSocket("ws://localhost:8080/Verkeersdashboard/actions");
    socket.onmessage = onMessage;
    $("#passwordField").on('keydown', function (e) {
        if (e.which === 13) {
            authenticate();
        }
    });
    try {
        initLogin();
    } catch (ex) {
        console.log('Exception: initialisation of user login failed.');
    }
}

function sendMessageWhenConnected(socket, msg) {
    waitForSocketConnection(socket, function () {
        socket.send(msg);
    });
}
;

function waitForSocketConnection(socket, callback) {
    setTimeout(
            function () {
                if (socket.readyState === 1) {
                    if (callback !== undefined) {
                        callback();
                    }
                    return;
                } else {
                    waitForSocketConnection(socket, callback);
                }
            }, 100);
}

function parseMessage(data)
{
    if (data.action === "adminDisabledWidgets") {
        var disabled = data.disabledWidgets;
        if (disabled.length > 0)
        {
            for (var i = 0; i < disabled.length; i++) {
                insertDisabledCell(disabled[i]);
                sortTable();
            }
        }
    } else if (data.action === "data" || data.action === "error") {
        insertCell(data);
        sortTable();

    } else if (data.action === "remove")
    {
        insertDisabledCell(data.widgetType, false);
        sortTable();
    } else if (data.action === "adminMessage")
    {
        if (data.authSuccess === true)
        {
            $("#authentication").hide();
            var subscribeMessage = {
                action: "subscribeAsAdmin",
                auth: ADMINAUTH
            };
            sendMessageWhenConnected(socket, JSON.stringify(subscribeMessage));
            $("#statusTable").show();
            $("#wrongpassword").show();
        } else {
            $("#wrongpassword").html("\nHet ingevoerde paswoord is incorrect, probeer het opnieuw.");
            //show info that the login is wrong
        }
    }
}

function authenticate()
{
    value = document.getElementById("passwordField").value;
    ADMINAUTH = value;
    var WidgetAction = {
        action: "authenticate",
        auth: ADMINAUTH
    };
    socket.send(JSON.stringify(WidgetAction));
}

function insertCell(data) {
    deleteRow(data.widgetType);
    var table = document.getElementById("statusBody");
    var row = table.insertRow(0);
    row.id = data.widgetType;
    // Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
    var cell0 = row.insertCell(0);
    cell0.setAttribute("headers", "Widget");
    var cell1 = row.insertCell(1);
    cell1.setAttribute("headers", "Widgetstatus");
    cell1.classList.add("statusCell");
    var cell2 = row.insertCell(2);
    cell2.setAttribute("headers", "Last Updated");
    var cell3 = row.insertCell(3);
    //cell3.setAttribute("headers", "");
    var cell4 = row.insertCell(4);
    //cell4.setAttribute("headers", "");
    cell0.innerHTML = data.widgetType;
    if (data.action === "data")
    {
        cell1.innerHTML = "running";
        row.className = "success";
    } else {
        cell1.innerHTML = data.status;
        row.className = "warning";
    }
    cell2.innerHTML = data.lastUpdate;
    var jsonButtonId = data.widgetType + "_JSON";
    var sliderId = data.widgetType + "_SLIDER";
    var urlButtonId = data.widgetType + "_URL";
    //check if the widget is disabled so the Json button gets disabled
    //apply corresponding table row class for color
    //set slider correctly corresponding to status 
    cell3.innerHTML = '<button type="button" id=' + jsonButtonId + ' class="btn btn-primary" valign="center" onclick="showjson(this.id)" data-toggle="modal" data-target="#jsonModal">JSON</button> <button type="button" id=' + urlButtonId + ' class="btn btn-primary" onclick="showurl(this.id)" data-toggle="modal" data-target="#settingsModal">SETTINGS</button>';
    cell3.classList.add("titleLessTd");
    cell4.innerHTML = '<label class="switch"> <input type="checkbox" onchange="sliderChange(this.id)" id=' + sliderId + ' checked="true"><div class="slider round"></div></input></label>';
    cell4.classList.add("titleLessTd");
}

function deleteRow(id)
{
    var row = document.getElementById(id);
    if (row !== null) {
        row.parentNode.removeChild(row);
    }
}

function showjson(id)
{
    var jsonId = id.substr(0, id.lastIndexOf("_"));
    var json = jsonMap[jsonId];
    $('#json-viewer').jsonViewer(json, {collapsed: true, withQuotes: true});
}


function insertDisabledCell(key, disabling)
{
    if (typeof key === "undefined")
    {
        console.log("UNDEFINED KEY!!!!!");
    } else {
        deleteRow(key);
        var table = document.getElementById("statusTable");
        var row = table.insertRow(1);
        row.id = key;
        // Insert new cells (<td> elements) at the 1st and 2nd position of the "new" <tr> element:
        var cell0 = row.insertCell(0);
        cell0.setAttribute("headers", "Widget");
        var cell1 = row.insertCell(1);
        cell1.setAttribute("headers", "Widgetstatus");
        var cell2 = row.insertCell(2);
        cell2.setAttribute("headers", "Last Updated");
        var cell3 = row.insertCell(3);
        //cell3.setAttribute("headers", "");
        var cell4 = row.insertCell(4);
        //cell4.setAttribute("headers", "");
        if (disabling) {
            row.className = "warning";
            cell1.innerHTML = "Disabling Widget";
        } else {
            row.className = "danger";
            cell1.innerHTML = "disabled";
        }
        var jsonButtonId = key + "_JSON";
        var sliderId = key + "_SLIDER";
        var urlButtonId = key + "_URL";
        cell0.innerHTML = key;
        cell2.innerHTML = "";
        cell3.innerHTML = '<button type="button" id=' + jsonButtonId + ' class="btn btn-primary" valign="center" onclick="showjson(this.id)" disabled>JSON</button> <button type="button" id=' + urlButtonId + ' class="btn btn-primary" onclick="showurl(this.id)" data-toggle="modal" data-target="#settingsModal" disabled>SETTINGS</button>';
        cell4.innerHTML = '<label class="switch"> <input type="checkbox" onchange="sliderChange(this.id)" id=' + sliderId + '><div class="slider round"></div></input></label>';
    }
}

function showurl(id) {
    latestClickedWidgetType = id.substr(0, id.lastIndexOf("_"));
    var message = {
        action: "getConfiguration",
        widgetType: latestClickedWidgetType,
        auth: ADMINAUTH
    };
    sendMessageWhenConnected(socket, JSON.stringify(message));
}

function saveSettings() {
    var settings = document.getElementById("urlField").value;
    var message = {
        action: "saveSettings",
        widgetType: latestClickedWidgetType,
        auth: ADMINAUTH,
        settings: settings
    };
    sendMessageWhenConnected(socket, JSON.stringify(message));
}

function sliderChange(sliderId)
{
    var sl = document.getElementById(sliderId);
    var wtype = sliderId.substr(0, sliderId.lastIndexOf("_"));
    //the slider is now enabled -> start the widget
    if (sl.checked)
    {
        var WidgetAction = {
            action: "enableWidget",
            widgetType: wtype,
            auth: ADMINAUTH
        };
        socket.send(JSON.stringify(WidgetAction));

    }
    //the slides is now disabled ->disable the widget
    else {
        //insertDisabledCell(wtype,true);
        var WidgetAction = {
            action: "disableWidget",
            widgetType: wtype,
            auth: ADMINAUTH
        };
        socket.send(JSON.stringify(WidgetAction));
        jsonMap[wtype] = "";
    }
}

function sortTable() {
    var table, rows, switching, i, x, y, shouldSwitch;
    table = document.getElementById("statusTable");
    switching = true;
    /*Make a loop that will continue until
     no switching has been done:*/
    while (switching) {
        //start by saying: no switching is done:
        switching = false;
        rows = table.getElementsByTagName("TR");
        /*Loop through all table rows (except the
         first, which contains table headers):*/
        for (i = 1; i < (rows.length - 1); i++) {
            //start by saying there should be no switching:
            shouldSwitch = false;
            /*Get the two elements you want to compare,
             one from current row and one from the next:*/
            x = rows[i].getElementsByTagName("TD")[0];
            y = rows[i + 1].getElementsByTagName("TD")[0];
            //check if the two rows should switch place:
            if (x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase()) {
                //if so, mark as a switch and break the loop:
                shouldSwitch = true;
                break;
            }
        }
        if (shouldSwitch) {
            /*If a switch has been marked, make the switch
             and mark that a switch has been done:*/
            rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
            switching = true;
        }
    }
}