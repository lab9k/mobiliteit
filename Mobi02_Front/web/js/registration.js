/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//{"firstName": "", "lastName": "", "userId": "", "password": "", "email": ""}
var loggedIn;
$(document).ready(function () {

    checkLoggedIn();

    if (localStorage.getItem("remember") === "true") {
        $("#loginName").val(localStorage.getItem("email"));
        $("#checkboxRemember").click();
    }
});

/*
 function pushEmptyUser() {
 var obj = {"firstName": "", "lastName": "", "userId": "", "password": "", "email": ""};
 var data = JSON.stringify(obj);
 postUser(data);
 }*/

//functie die de inlog en registratie knoppen aanpassen indien je ingelogged bent!
function fixHeader() {
    if (loggedIn === "True") {
        document.getElementById("registerHeader").innerHTML = "";
        document.getElementById("loginHeader").innerHTML =
                "<a onClick=\"logUit();\" class=\"navbarText clickable\"><span class=\"glyphicon glyphicon-log-out\"></span>  Uitloggen</a>";
        document.getElementById("myPageHeader").innerHTML = "<a href=\"/myPage.html\" class=\"navbarText\">Mijn Pagina</a>";
    } else {
        document.getElementById("registerHeader").innerHTML =
                "<a href=\"#\" data-toggle=\"modal\" data-target=\"#myRegister\" class=\"navbarText clickable\"><span class=\"glyphicon glyphicon-user\"></span>  Registreren</a>";
        document.getElementById("loginHeader").innerHTML =
                "<a href=\"#\" data-toggle=\"modal\" data-target=\"#myLogin\" class=\"navbarText clickable\"><span class=\"glyphicon glyphicon-log-in\"></span>  Inloggen</a>";
        document.getElementById("myPageHeader").innerHTML = "";
    }
    document.getElementById("refreshHeader").innerHTML =
            "<a onclick=\"fillIn();\" href=\"#\" class=\"navbarText\"><span id=\"refreshIcon\" class=\"fa fa-refresh \"></span> Refresh</a>";
    document.getElementById("refreshIcon").setAttribute("class", "fa fa-refresh fa-spin");
    document.getElementById("refreshIcon").setAttribute("class", "fa fa-refresh");
}

function logUit() {
    postLocalStorage();
    $.ajax({
        type: "GET",
        url: logoutUrl,
        contentType: "application/json",
        success: function (data) {
            loggedIn = "False";
            fixHeader();

        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}
function registerDatabase() {
    document.getElementById("registerError").innerHTML = "";
    var user = {
        firstName: $("#firstname").val(),
        lastName: $("#lastname").val(),
        password: $("#password").val(),
        email: $("#email").val()
    };
    var pwcheck = isOkPass($("#password").val());
    var checks = 0;
    if (pwcheck.result === true) {
        checks++;
    } else {

        postError(pwcheck.error, "");
    }
    if ($("#password").val() === $("#confirm").val()) {
        checks++;
    } else {

        postError("De ingegeven paswoorden komen niet overeen.", "");
    }
    if (validateEmail($("#email").val()) === true) {
        checks++;
    } else {

        postError("Gelieve een geldig email adres in te vullen.", "");
    }
    if (checks === 3) {

        var data = JSON.stringify(user);

        postUser(data);
    }
}
function checkLoggedIn() {
    $.ajax({
        type: "GET",
        url: loginUrl,
        contentType: "application/json",
        success: function (data) {
            if (data.loggedIn === "True") {
                loggedIn = "True";
                fixHeader();
            } else {
                loggedIn = "False";
                fixHeader();
            }

        },
        error: function (errMsg) {
            console.log(errMsg);

        }
    });
}
function logIn() {
    var user = {
        username: $("#loginName").val(),
        userpass: $("#loginPW").val()

    };

    var data = JSON.stringify(user);


    if (document.getElementById("checkboxRemember").checked) {

        localStorage.setItem("remember", "true");
        localStorage.setItem("email", $("#loginName").val());

    } else {
        localStorage.setItem("remember", "false");

        document.getElementById("checkboxRemember").checked = false;
    }
    if (localStorage.getItem("remember") === "true") {

        $("#loginName").val(localStorage.getItem("email"));
        if (!document.getElementById("checkboxRemember").checked) {
            document.getElementById("checkboxRemember").checked = true;
            console.log("checkbox op true zetten");
        }
    }
    postLogin(data);
    //saveLocalStorage();
}
function postLogin(data) {
    $.ajax({
        type: "POST",
        url: loginUrl,
        data: data,
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (der) {

            if (der.success === "True") {
                getLocalStorage();
                document.location.href = "myPage.html";
                document.getElementById("loginError").innerHTML = "";
            } else {

                $("#loginPW").val("");
                document.getElementById("loginError").innerHTML = "";
                var div1 = document.createElement("div");
                div1.setAttribute("class", "alert alert-warning");
                div1.innerHTML = "De ingegeven gegevens zijn foutief.";
                div1.setAttribute("style", "margin-bottom: 0px !important");
                document.getElementById("loginError").appendChild(div1);
            }

        },
        error: function (errMsg) {

            var obj = JSON.parse(errMsg.responseText);
            $("#loginPW").val("");
            document.getElementById("loginError").innerHTML = "";
            var div1 = document.createElement("div");
            div1.setAttribute("class", "alert alert-warning");
            div1.innerHTML = "De ingegeven gegevens zijn foutief.";
            div1.setAttribute("style", "margin-bottom: 0px !important");
            document.getElementById("loginError").appendChild(div1);

        }
    });
}
function postError(error, soort) {
    var div1 = document.createElement("div");
    div1.setAttribute("class", "alert alert-warning");
    div1.innerHTML = error;
    div1.setAttribute("style", "margin-bottom: 0px !important");
    document.getElementById("registerError").appendChild(div1);
    if (soort === "post") {
        $("#firstname").val("");
        $("#lastname").val("");
        $("#password").val("");
        $("#email").val("");
        $("#confirm").val("");
    } else if (soort === "success") {
        // functionaliteit email again enabled
      //  console.log("Mailfunctionaliteit wordt door ontbreken van certificaat nog niet toegestaan op server. Je kan wel inloggen met je mailadres.")
        var melding = "Registratie mobiliteitsdashboard";
        var textje = "Beste " + document.getElementById("firstname").value;
        var email = document.getElementById("email").value;
        var mailText2 = "{ \"mail\":\"" + email + "\", \"subject\":" + melding + ", \"text\":\"" + textje + ", uw registratie werd goed ontvangen!\" }";
        $.ajax({
            type: "POST",
            url: mailUrl,
            data: mailText2,
           headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            success: function () {
                document.getElementById("registerBody").innerHTML = "Registratie voltooid.";
            },
            error: function () {
                document.getElementById("registerBody").innerHTML = "Registratie mislukt, er kon geen mail naar uw adres verzonden worden.";
            }
        });

    } else {
        $("#password").val("");
        $("#confirm").val("");
    }
}

function postUser(data) {
    $.ajax({
        type: "POST",
        url: registerUrl,
        data: data,
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function () {
            console.log("post succes!");
            makeClearUser();
            postError("Registratie voltooid", "success");
        },
        error: function (errMsg) {
            console.log(errMsg.responseText);
            var obj = JSON.parse(errMsg.responseText);
            console.log(obj);

            if (obj.success === "True") {

                //postError(obj.message, "success");
            } else {
                postError(obj.message, "post");
            }
        }
    });
}

function makeClearUser() {
    localStorage.setItem("profilepic", "http://www.pi-cube.com/wp-content/uploads/2015/04/team-placeholder.jpg");
    var emptyArray = [];
    var arraysToEmpty = ["parkingTimes","BBparkingTimes","CoyoteTimes","WeatherTimes","NMBSFav","LijnFav","routesRight","routesLeft"]
    for (var i = 0; i < arraysToEmpty.length; i++) {
        localStorage.setItem(arraysToEmpty[i], JSON.stringify(emptyArray));
    }
    localStorage.setItem("TrainNotifCheck",false);
    var notifCheckboxNames = ["ParkingMessengerBox", "ParkingMailBox", "BBParkingMessengerBox", "BBParkingMailBox", "CoyoteMessengerBox", "CoyoteMailBox", "WeatherMessengerBox", "WeatherMailBox", "TrainMessengerBox", "TrainMailBox"];
    for (var i = 0; i < notifCheckboxNames.length; i++) {
        localStorage.setItem(notifCheckboxNames[i], false);
    }
    var myPanelParkings = ["vrijdagClick", "reepClick", "savaanClick", "smClick", "ramenClick", "sppClick", "gspClick","Sint-PietersClick", "DampoortClick"];
    for (var i = 0; i < myPanelParkings.length; i++) {
        localStorage.setItem(myPanelParkings[i], false);
    }
    getRoutes();
}

function getRoutes() {
    $.ajax({
        type: "GET",
        url: CoyoteRoutes,
        async: false,
        contentType: "application/json",
        success: function (data) {
           
            localStorage.setItem("routesLeft", JSON.stringify(data));

        },
        error: function (errMsg) {
            
            console.log(errMsg);
        }
    });
}

//function die checkt of email oké is
function validateEmail(email) {
    var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}
//function die checkt of PW oké is
function isOkPass(p) {

    var aLowerCase = /[a-z]/;
    var aNumber = /[0-9]/;

    var obj = {};
    obj.result = true;

    if (p.length < 6) {
        obj.result = false;
        obj.error = "Uw wachtwoord hoort minstens 6 karakters lang te zijn.";
        return obj;
    }

    var numLower = 0;
    var numNums = 0;

    for (var i = 0; i < p.length; i++) {

        if (aLowerCase.test(p[i]))
            numLower++;
        else if (aNumber.test(p[i]))
            numNums++;

    }

    if (numLower < 1 || numNums < 1) {
        obj.result = false;
        if (numLower < 1) {
            obj.error = "Uw wachtwoord bevat geen letters!";
        } else if (numNums < 1) {
            obj.error = "Uw wachtwoord bevat geen getal!";
        }

        return obj;
    }
    return obj;
}

//function checkLoginState() {
//
//    FB.getLoginStatus(function (response) {
//        statusChangeCallback(response);
//    }, {scope: 'public_profile,email'});
//}

function checkLoginState() {
    FB.getLoginStatus(function (response) {
        statusChangeCallback(response);
    });
}

function facebookLogin() {
    FB.login(function (response) {
        statusChangeCallback(response);
    }, {scope: 'public_profile,email'});
}

function statusChangeCallback(response) {
    //console.log('statusChangeCallback');
    //console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected') {
        FB.api('/me', {fields: 'name, email, picture'}, function (APIresponse) {
            //console.log(APIresponse);
            localStorage.setItem("profilepic", "http://graph.facebook.com/" + APIresponse.id + "/picture?type=large");
            var fullName = APIresponse.name;
            var fbuser = {
                page_id: APIresponse.id,
                firstname: fullName.split(' ').slice(0, 1).join(' '),
                lastname: fullName.split(' ').slice(1).join(' ')

            };

            var data = JSON.stringify(fbuser);

            postFBLogin(data);

//            var firstName = fullName.split(' ').slice(0, 1).join(' ');
//            var lastName = fullName.split(' ').slice(1).join(' ');
//            console.log('Good to see you, ' + firstName + '. Your last name is: ' + lastName);
        });
    } else {
        console.log('User cancelled login or did not fully authorize.');
    }
}

function postFBLogin(data) {
    $.ajax({
        type: "POST",
        url: fbloginUrl,
        data: data,
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function () {
            console.log("FB post succes!");
            document.location.href = "myPage.html";
            document.getElementById("loginError").innerHTML = "";
        },
        error: function (errMsg) {
        }
    });
}

$("#myLogin").keyup(function (event) {
    if (event.keyCode == 13) {
        $("#singlebutton").click();
    }
});
