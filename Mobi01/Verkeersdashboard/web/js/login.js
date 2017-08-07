//var socket;
var loggedIn;
var lastLoggedID;
var lastLoggedEmail;
var facebookMail;

var daysOfWeekShuffle = ["Zondag", "Maandag", "Dinsdag", "Woensdag", "Donderdag", "Vrijdag", "Zaterdag"];

var days = ['Zondag', 'Maandag', 'Dinsdag', 'Woensdag', 'Donderdag', 'Vrijdag', 'Zaterdag'];

var Gentsp = [
    {display: "Antwerpen-Centraal", value: "Antwerpen-Centraal"},
    {display: "Blankenberge", value: "Blankenberge"},
    {display: "Brugge", value: "Brugge"},
    {display: "Brussels Airport - Zaventem", value: "Brussels Airport - Zaventem"},
    {display: "De Panne", value: "De Panne"},
    {display: "Eeklo", value: "Eeklo"},
    {display: "Eupen", value: "Eupen"},
    {display: "Genk", value: "Genk"},
    {display: "Geraardsbergen", value: "Geraardsbergen"},
    {display: "Knokke", value: "Knokke"},
    {display: "Kortrijk", value: "Kortrijk"},
    {display: "Landen", value: "Landen"},
    {display: "Leuven", value: "Leuven"},
    {display: "Mechelen", value: "Mechelen"},
    {display: "Oostende", value: "Oostende"},
    {display: "Poperinge", value: "Poperinge"},
    {display: "Ronse / Renaix", value: "Ronse / Renaix"},
    {display: "Tongeren", value: "Tongeren"},
    {display: "Welkenraedt", value: "Welkenraedt"},
    {display: "Zeebrugge-Dorp", value: "Zeebrugge-Dorp"}
];
var Gentdp = [
    {display: "Antwerpen-Centraal", value: "Antwerpen-Centraal"},
    {display: "Eeklo", value: "Eeklo"},
    {display: "Gent-Sint-Pieters", value: "Gent-Sint-Pieters"},
    {display: "Kortrijk", value: "Kortrijk"},
    {display: "Lille Flandres(f)", value: "Lille Flandres(f)"},
    {display: "Mechelen", value: "Mechelen"},
    {display: "Oostende", value: "Oostende"},
    {display: "Poperinge", value: "Poperinge"},
    {display: "Ronse / Renaix", value: "Ronse / Renaix"},
    {display: "Sint-Niklaas", value: "Sint-Niklaas"}
];

function initLogin() {
    var loginModal = document.getElementById('id01');
    var forecastModal = document.getElementById('forecast');
    var notificationModal = document.getElementById('notificationprefs');
    loggedIn = false;
// When the user clicks anywhere outside of the modal, close it
    window.onclick = function (event) {
        if (event.target === loginModal) {
            loginModal.style.display = "none";
        }
        if (event.target === forecastModal) {
            forecastModal.style.display = "none";
        }
        if (event.target === notificationModal) {
            notificationModal.style.display = "none";
            saveNotificationPreferences();
            var tableRef = document.getElementById("possibleDepartures").getElementsByTagName("tbody")[0];
            tableRef.innerHTML = "";
        }
    };
    $("#notify_train").click(function () {
        $("#treingegevens").toggle(this.checked);
    });
    list(Gentsp);
    $("#actualDepartures").hide();
}

function authenticateLogin(type, token, email) {
    var message = {
        action: "loginAuthentication",
        provider: type,
        token: token,
        email: email
    };
    lastLoggedEmail = email;
    sendMessageWhenConnected(socket,JSON.stringify(message));
    console.log(message);
}

//load facebook login JS
window.fbAsyncInit = function () {
    FB.init({
        appId: '684757125040046',
        cookie: true,
        xfbml: true,
        status: true,
        version: 'v2.9'
    });
    FB.AppEvents.logPageView();
    checkLoginState();
    $("#googlelogout").hide();
    $("#facebooklogout").hide();

};

// Load the SDK asynchronously
(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id))
        return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));

function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected' && !loggedIn) {
        loggedIn = true;
        FB.api('/me', {fields: 'name, email'}, function (response) {
            //UserID of the facebook user
            //console.log(response);
            setName(response.name);
            console.log(response);
            // console.log("Facebook: UserID: " + response.id);
            //console.log("Facebook: User Name: " + response.name);
            setProfilePicture('http://graph.facebook.com/' + response.id + '/picture?type=square');
            onLogin("Facebook", response.id, response.email);
            lastLoggedID = response.id;
        });
    } else if (loggedIn) {
        console.log("Double login case");
        FB.logout(function (response) {

        });
    } else {
        console.log("Wrong login credentials");
    }
};

function checkLoginState() {
    FB.getLoginStatus(function (response) {
        statusChangeCallback(response);
    });
}

function onLogOut() {
    loggedIn = false;
    $("#profilepicture").html("");
    $("#userName").hide();
    $("#facebooklogin").show();
    $("#googlelogin").show();
    $("#facebooklogout").hide();
    $("#googlelogout").hide();
    $("#loginscreen").html("Log in");
    $("#loadConfigButton").hide();
    $("#notifications").hide();
}

function onLogin(type, token, email) {
    loggedIn = true;
    if (type === "Facebook") {
        $("#googlelogin").hide();
        $("#facebooklogin").hide();
        $("#facebooklogout").show();
        $("#googlelogin").hide();
    } else if (type === "Google") {
        $("#googlelogout").show();
        $("#facebooklogin").hide();
        $("#facebooklogout").hide();
        $("#googlelogin").hide();
    }
    authenticateLogin(type, token, email);
    $("#loginscreen").html("Log out");
    $("#notifications").show();
    $("#id01").hide();
    $("#loadConfigButton").show();
}

function facebookLogout() {
    FB.logout(function (response) {
        onLogOut();
        console.log("We logged out of facebook so now our loggedIn state is : " + loggedIn);
        // Person is now logged out
    });
}

//can't really use this as notifications don't get sent to mobile users
function makeFacebookNotification(msg) {
    FB.api(
            '/1446843465327998/notifications',
            'POST',
            {"access_token": "684757125040046|fojar79nn-MdAFkYrccpi5R3LmY", "template": msg},
            function (response) {
                console.log(response);
                // Insert your code here
            }
    );
}

function setName(name) {
    $("#userName").html("Hello, " + name + " !");
    $("#userName").show();
}

function setProfilePicture(url) {
    $("#profilepicture").html('<img width="40" src=' + url + '>');
}

function onSignIn(googleUser) {
    if (loggedIn) {
        console.log("I'm already loggedIn with Facebook, logging out of Google....");
        var auth2 = gapi.auth2.getAuthInstance();
        auth2.signOut();
    } else {
        loggedIn = true;
        var accestoken = googleUser.getAuthResponse().id_token;
        var profile = googleUser.getBasicProfile();
        setName(profile.getName());
        console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
        console.log('Name: ' + profile.getName());
        console.log('Image URL: ' + profile.getImageUrl());
        var email = profile.getEmail();
        // This is null if the 'email' scope is not present.
        //console.log('id_token: '+accestoken); 
        onLogin("Google", accestoken, email);
        setProfilePicture(profile.getImageUrl());
    }
}

function googleSignout() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
    onLogOut();
}

function saveNotificationPreferences() {
    console.log("test");
    var rain = document.getElementById("notify_rain").checked;
    var train = document.getElementById("notify_train").checked;
    var msg = {
        action: "saveNotificationPreferences",
        userID: lastLoggedID,
        notify_rain: rain,
        notify_train: train,
        notify_mail:  document.getElementById("notify_mail").checked
    };
    var tableRef = document.getElementById("possibleDepartures").getElementsByTagName("tbody")[0];
    tableRef.innerHTML = "";
    /* if (train) {
     var selecteddepart = document.getElementById("departurestation").value;
     var selecteddayofweek = document.getElementById("dayofweek").value;
     var selectedendstation = document.getElementById("endstation").value;
     trainstring = selecteddayofweek + "_" + $("#departurehour").val() + "_" + selecteddepart + "_" + selectedendstation;
     }
     msg.train = trainstring;*/
    console.log(msg);
    $('#notificationprefs').hide();

    sendMessageWhenConnected(socket,JSON.stringify(msg));
    
}

function saveNotifyMeans() {
    saveNotifyMeansFirebase();
}

function sendMessageWhenConnected(socket, msg) {
    waitForSocketConnection(socket, function () {
        socket.send(msg);
    });
};

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

function saveNotifyMeansFirebase() {
    var firebaseChecked = document.getElementById("notify_firebase").checked;
    if (firebaseChecked) {
        document.getElementById("notify_firebase").checked = false;
        activeerFirebaseWebNotificaties(function (token) {
            if (token !== undefined) {
                //send token to server
                var msg = {
                    action: "saveNotificationFirebaseToken",
                    userID: lastLoggedID,
                    firebaseToken: token
                }
                console.log("sending firebase-token to server...", msg);
                sendMessageWhenConnected(socket,JSON.stringify(msg));
                if (token !== undefined) {
                    document.getElementById("notify_firebase").checked = true;
                }
            } else {
                deleteFirebaseTokenOnServer();
            }
        });
    } else {
        deleteFirebaseTokenOnServer();
    }
}

function deleteFirebaseTokenOnServer() {
    var msg = {
        action: "saveNotificationFirebaseToken",
        userID: lastLoggedID,
        firebaseToken: null
    };
    console.log("deleting firebase-token on server...", msg);
    sendMessageWhenConnected(socket,JSON.stringify(msg));
}

function list(array_list)
{
    $("#endstation").html(""); //reset child options
    $(array_list).each(function (i) { //populate child options
        $("#endstation").append("<option value='" + array_list[i].value + "'>" + array_list[i].display + "</option>");
    });
}

function subscribe(id) {
    var msg = {
        action: "subscribeTrain",
        userID: lastLoggedID,
        trainId: id,
        departureStation: document.getElementById(id).getAttribute("data-train-departurestation"),
        endStation: document.getElementById(id).getAttribute("data-train-endstation"),
        dayOfWeek: document.getElementById(id).getAttribute("data-train-dayofweek"),
        departureTime: document.getElementById(id).getAttribute("data-train-departuretime"),
        email: lastLoggedEmail
    };
    console.log(msg);
    sendMessageWhenConnected(socket,JSON.stringify(msg));
}

function unsubscribe(btn) {
    id = btn.getAttribute("id");
    var msg = {
        action: "unsubscribeTrain",
        userID: lastLoggedID,
        trainId: id,
        departureStation: btn.getAttribute("data-train-departurestation"),
        dayOfWeek: btn.getAttribute("data-train-dayofweek"),
        departureTime: btn.getAttribute("data-train-departuretime")
    };
    console.log('sending unsubscribe message to server: ', msg);
    sendMessageWhenConnected(socket,JSON.stringify(msg));
    var row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
}

/*var msg={
 action:"tryTrain",
 userID:lastLoggedID,
 departureStation:document.getElementById("departurestation").value,
 endStation: document.getElementById("endstation").value,
 dayOfWeek:document.getElementById("dayofweek").value,
 departureHour:$("#departurehour").val(),   
 email:lastLoggedEmail
 };
 console.log(msg);
 socket.send(JSON.stringify(msg));*/
function getDateForWeekDay(day) {
    var d = new Date();
    var dayName = days[d.getDay()];
    while (daysOfWeekShuffle[0] !== dayName) {
        var toShift = daysOfWeekShuffle.shift();
        daysOfWeekShuffle[daysOfWeekShuffle.length] = toShift;
    }
    var i = 0;
    while (daysOfWeekShuffle[i] !== day) {
        i++;
    }
    d.setDate(d.getDate() + i);
    console.log(d.getDate());
    return d;
}

function timeChanged() {
    console.log(document.getElementById("dayofweek").value);
    var d = getDateForWeekDay(document.getElementById("dayofweek").value);
    var dayOfMonth = d.getDate();
    if (dayOfMonth < 10) {
        dayOfMonth = "0" + dayOfMonth;
    }
    console.log(dayOfMonth);
    var month = d.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    console.log(month);
    var year = d.getYear() - 100;
    console.log(year);
    var date = "" + dayOfMonth + month + year;
    console.log(date);
    //$("#possibleDepartures").html('');
    var irail = "https://api.irail.be/connections/?to=" + document.getElementById("endstation").value + "&from=" + document.getElementById("departurestation").value + "&timeSel=depart&time=" + $("#departurehour").val().replace(":", "") + "&format=json&date=" + date;
    //console.log(irail);
    var tableRef = document.getElementById("possibleDepartures").getElementsByTagName("tbody")[0];
    tableRef.innerHTML = "";
    console.log(tableRef);
    $.getJSON(irail, function (data) {
        //console.log(data);
        var departures = data.connection;
        /* departures.sort(function(a, b){
         return a.departure.time-b.departure.time;
         });*/
        for (var i = 0; i < departures.length; i++) {
            //console.log(departures.length);
            var tr = departures[i];
            var train = tr.departure;
            var utcSeconds = parseInt(train.time);
            var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
            d.setUTCSeconds(utcSeconds);
            var prettyTime = "" + d.getHours() + ":" + d.getMinutes();
            //console.log(prettyTime);
            var departingStation = train.stationinfo.standardname;
            var newRow = tableRef.insertRow(-1);
            // Insert cells in the row at corresponding indexes  
            var departDayCell = newRow.insertCell(0);
            departDayCell.setAttribute("headers", "Dag");
            var departTimeCell = newRow.insertCell(1);
            departTimeCell.setAttribute("headers", "Uur");
            var departingCell = newRow.insertCell(2);
            departingCell.setAttribute("headers", "Vertrekstation");
            var endCell = newRow.insertCell(3);
            endCell.setAttribute("headers", "Eindstation");
            var idCell = newRow.insertCell(4);
            idCell.setAttribute("headers", "Trein");
            var trainid = train.vehicle;
            var endstation = train.direction.name;
            departDayCell.appendChild(document.createTextNode(document.getElementById("dayofweek").value));
            departTimeCell.appendChild(document.createTextNode(prettyTime));
            departingCell.appendChild(document.createTextNode(departingStation));
            endCell.appendChild(document.createTextNode(endstation));
            idCell.appendChild(document.createTextNode(trainid));
            var cell4 = newRow.insertCell(5);
            cell4.innerHTML = "<button class='btn' id='" + trainid + "' data-train-departuretime='" + prettyTime + "' data-train-departurestation='" + departingStation + "' data-train-dayofweek='" + document.getElementById("dayofweek").value + "' data-train-endstation='" + endstation + "' onclick=subscribe(this.id)>Abboneer</button>";

        }
        //sortTable("#actualDepartures");
        $("#actualDepartures").show();
    });
}
