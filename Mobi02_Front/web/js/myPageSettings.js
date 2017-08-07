/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var notifCheckboxNames = ["ParkingMessengerBox", "ParkingMailBox", "BBParkingMessengerBox", "BBParkingMailBox", "CoyoteMessengerBox", "CoyoteMailBox", "WeatherMessengerBox", "WeatherMailBox", "TrainMessengerBox", "TrainMailBox"];

function updateNotifs() {
    $.ajax({
        type: "DELETE",
        url: notifUrl + "/messenger",
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (response) {
            //console.log(response);
            if (response.success === "true") {
                $.ajax({
                    type: "DELETE",
                    url: notifUrl + "/mail",
                    dataType: 'json',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json',
                    },
                    success: function (response) {
                        //console.log(response);
                        if (response.success === "true") {
                            if (localStorage.getItem("parkingTimes") !== null) {
                                var parkingTimes = JSON.parse(localStorage.getItem("parkingTimes"));
                                for (index = 0; index < parkingTimes.length; ++index) {
                                    hour = parkingTimes[index].split(":")[0];
                                    minutes = parkingTimes[index].split(":")[1];
                                    setParkingNotif(hour, minutes);
                                }
                            }
                            if (localStorage.getItem("BBparkingTimes") !== null) {
                                var BBparkingTimes = JSON.parse(localStorage.getItem("BBparkingTimes"));
                                for (index = 0; index < BBparkingTimes.length; ++index) {
                                    hour = BBparkingTimes[index].split(":")[0];
                                    minutes = BBparkingTimes[index].split(":")[1];
                                    setBBParkingNotif(hour, minutes);
                                }
                            }
                            if (localStorage.getItem("CoyoteTimes") !== null) {
                                var CoyoteTimes = JSON.parse(localStorage.getItem("CoyoteTimes"));
                                for (index = 0; index < CoyoteTimes.length; ++index) {
                                    hour = CoyoteTimes[index].split(":")[0];
                                    minutes = CoyoteTimes[index].split(":")[1];
                                    setCoyoteNotif(hour, minutes);
                                }
                            }
                            if (localStorage.getItem("WeatherTimes") !== null) {
                                var WeatherTimes = JSON.parse(localStorage.getItem("WeatherTimes"));
                                for (index = 0; index < WeatherTimes.length; ++index) {
                                    hour = WeatherTimes[index].split(":")[0];
                                    minutes = WeatherTimes[index].split(":")[1];
                                    setWeatherNotif(hour, minutes);
                                }
                            }
                            if (localStorage.getItem("TrainNotifCheck") !== null) {
                                if (localStorage.getItem("TrainNotifCheck") === "true") {
                                    document.getElementById("TrainNotifCheck").checked = true;
                                    setTrainNotifs();
                                } else {
                                    document.getElementById("TrainNotifCheck").checked = false;
                                }
                            }
                            for (var i = 0; i < notifCheckboxNames.length; i++) {
                                if (document.getElementById(notifCheckboxNames[i]).checked === true) {
                                    localStorage.setItem(notifCheckboxNames[i], true);
                                } else {
                                    localStorage.setItem(notifCheckboxNames[i], false);
                                }
                            }
                        } else {
                            console.log("Mail delete failed!");
                        }
                    },
                    error: function (errMsg) {

                    }
                });
            } else {
                console.log("Messenger delete failed!");
            }
        },
        error: function (errMsg) {

        }
    });
}


function setParkingNotif(hour, minutes) {
    var parkingKeywords = "";
    for (i = 0; i < parkingNamesMP.length; i++) {
        if (localStorage.getItem(parkingFunctions[i]) === "true") {
            parkingKeywords += "," + parkingNamesMP[i];
        }
    }
    parkingKeywords = parkingKeywords.substring(1);
    if (parkingKeywords.length > 0) {
        if (document.getElementById("ParkingMessengerBox").checked) {
            postNotif("messenger", hour, minutes, "Parkings", parkingKeywords);
        }
        if (document.getElementById("ParkingMailBox").checked) {
            postNotif("mail", hour, minutes, "Parkings", parkingKeywords);
        }
    }
}


function setBBParkingNotif(hour, minutes) {
    var BBparkingKeywords = "";
    for (i = 0; i < BBNamesBack.length; i++) {
        if (localStorage.getItem(BBFunctions[i]) === "true") {
            BBparkingKeywords += "," + BBNamesBack[i];
        }
    }
    BBparkingKeywords = BBparkingKeywords.substring(1);
    if (BBparkingKeywords.length > 0) {
        if (document.getElementById("BBParkingMessengerBox").checked) {
            postNotif("messenger", hour, minutes, "BlueBike", BBparkingKeywords);
        }
        if (document.getElementById("BBParkingMailBox").checked) {
            postNotif("mail", hour, minutes, "BlueBike", BBparkingKeywords);
        }
    }
}

function setCoyoteNotif(hour, minutes) {
    var CoyoteKeywords = "";
    var CoyoteRoutes = JSON.parse(localStorage.getItem("routesRight"));
    for (i = 0; i < CoyoteRoutes.length; i++) {
        CoyoteKeywords += "," + CoyoteRoutes[i];
    }
    CoyoteKeywords = CoyoteKeywords.substring(1);
    if (CoyoteKeywords.length > 0) {
        if (document.getElementById("CoyoteMessengerBox").checked) {
            postNotif("messenger", hour, minutes, "Coyote", CoyoteKeywords);
        }
        if (document.getElementById("CoyoteMailBox").checked) {
            postNotif("mail", hour, minutes, "Coyote", CoyoteKeywords);
        }
    }
}

function setWeatherNotif(hour, minutes) {
    console.log("set weather");
    if (document.getElementById("WeatherMessengerBox").checked) {
        postNotif("messenger", hour, minutes, "Weather", "");
    }
    if (document.getElementById("WeatherMailBox").checked) {
        postNotif("mail", hour, minutes, "Weather", "");
    }
}

function setTrainNotifs() {
    if (document.getElementById("TrainNotifCheck").checked) {
        localStorage.setItem("TrainNotifCheck", true);
        var favorites = JSON.parse(localStorage.getItem("NMBSFav"));
        for (i = 0; i < favorites.length; i++) {
            var favoArray = favorites[i].split("?");
            var TrainKeywords = favoArray[0] + "," + favoArray[1];
            var hour = favoArray[2].split(":")[0];
            var minutes = favoArray[2].split(":")[1];
            if (document.getElementById("TrainMessengerBox").checked) {
                postNotif("messenger", hour, minutes, "Trains", TrainKeywords);
            }
            if (document.getElementById("TrainMailBox").checked) {
                postNotif("mail", hour, minutes, "Trains", TrainKeywords);
            }
        }
    } else {
        localStorage.setItem("TrainNotifCheck", false);
        updateNotifs();
    }
}

function fillSelects() {
    var notifNames = ["parking", "BBparking", "Coyote", "Weather"];
    for (var j = 0; j < notifNames.length; j++) {
        $("#" + notifNames[i] + "HourField").empty();
        $("#" + notifNames[i] + "MinuteField").empty();
        var sel = document.getElementById(notifNames[j] + "HourField");
        for (var i = 0; i < 24; i++) {
            var opt = document.createElement('option');
            if (i.toString().length === 1) {
                var time = "0" + i;
            } else {
                var time = i;
            }
            opt.innerHTML = time;
            opt.value = time;
            sel.appendChild(opt);
        }
        var sel = document.getElementById(notifNames[j] + "MinuteField");
        for (var i = 0; i < 60; i += 15) {
            var opt = document.createElement('option');
            if (i.toString().length === 1) {
                var time = "0" + i;
            } else {
                var time = i;
            }
            opt.innerHTML = time;
            opt.value = time;
            sel.appendChild(opt);
        }
    }


}

function postNotif(platform, sendHour, sendMinutes, type, keywords) {
    var notif = {
        platform: platform,
        sendHour: sendHour,
        sendMinutes: sendMinutes,
        type: type,
        keywords: keywords
    };

    var data = JSON.stringify(notif);
    $.ajax({
        type: "POST",
        url: notifUrl,
        data: data,
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (response) {
            if (response.success === "true") {
                console.log("post succes!");
            } else {
                console.log("Notif post failed!");
            }
        },
        error: function (errMsg) {

        }
    });
}

$(document).ready(function () {
    if (localStorage.getItem("parkingTimes") !== null) {
        var parkingTimes = JSON.parse(localStorage.getItem("parkingTimes"));
        for (index = 0; index < parkingTimes.length; ++index) {
            time = parkingTimes[index];
            var td1 = document.createElement("td");
            var btn = document.createElement("button");
            btn.setAttribute("onclick", "removeParkingHour(this)");
            btn.setAttribute("type", "button-object");
            btn.setAttribute("class", "btn btn-default");
            btn.innerHTML = time + " ";
            var ic = document.createElement("i");
            ic.setAttribute("class", "fa fa-minus");
            ic.setAttribute("style", "color:red");
            btn.appendChild(ic);
            td1.appendChild(btn);
            document.getElementById("parkingHourRow").appendChild(td1);
            //$("#parkingHourRow").appendChild('<td><button onclick="removeParkingHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        }
    }
    if (localStorage.getItem("BBparkingTimes") !== null) {
        var BBparkingTimes = JSON.parse(localStorage.getItem("BBparkingTimes"));
        for (index = 0; index < BBparkingTimes.length; ++index) {
            time = BBparkingTimes[index];
            var td1 = document.createElement("td");
            var btn = document.createElement("button");
            btn.setAttribute("onclick", "removeBBParkingHour(this)");
            btn.setAttribute("type", "button-object");
            btn.setAttribute("class", "btn btn-default");
            btn.innerHTML = time + " ";
            var ic = document.createElement("i");
            ic.setAttribute("class", "fa fa-minus");
            ic.setAttribute("style", "color:red");
            btn.appendChild(ic);
            td1.appendChild(btn);
            document.getElementById("BBparkingHourRow").appendChild(td1);
            //$("#BBparkingHourRow").appendChild('<td><button onclick="removeBBParkingHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        }
    }
    if (localStorage.getItem("CoyoteTimes") !== null) {
        var CoyoteTimes = JSON.parse(localStorage.getItem("CoyoteTimes"));
        for (index = 0; index < CoyoteTimes.length; ++index) {
            time = CoyoteTimes[index];
            var td1 = document.createElement("td");
            var btn = document.createElement("button");
            btn.setAttribute("onclick", "removeCoyoteHour(this)");
            btn.setAttribute("type", "button-object");
            btn.setAttribute("class", "btn btn-default");
            btn.innerHTML = time + " ";
            var ic = document.createElement("i");
            ic.setAttribute("class", "fa fa-minus");
            ic.setAttribute("style", "color:red");
            btn.appendChild(ic);
            td1.appendChild(btn);
            document.getElementById("CoyoteHourRow").appendChild(td1);
            //$("#CoyoteHourRow").appendChild('<td><button onclick="removeCoyoteHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        }
    }
    if (localStorage.getItem("WeatherTimes") !== null) {
        var WeatherTimes = JSON.parse(localStorage.getItem("WeatherTimes"));
        for (index = 0; index < WeatherTimes.length; ++index) {
            time = WeatherTimes[index];
            var td1 = document.createElement("td");
            var btn = document.createElement("button");
            btn.setAttribute("onclick", "removeWeatherHour(this)");
            btn.setAttribute("type", "button-object");
            btn.setAttribute("class", "btn btn-default");
            btn.innerHTML = time + " ";
            var ic = document.createElement("i");
            ic.setAttribute("class", "fa fa-minus");
            ic.setAttribute("style", "color:red");
            btn.appendChild(ic);
            td1.appendChild(btn);
            document.getElementById("WeatherHourRow").appendChild(td1);
            //$("#WeatherHourRow").appendChild('<td><button onclick="removeWeatherHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        }
    }
    if (localStorage.getItem("TrainNotifCheck") === true) {
        document.getElementById("TrainNotifCheck").checked = true;
    }
    for (var i = 0; i < notifCheckboxNames.length; i++) {
        if (localStorage.getItem(notifCheckboxNames[i]) === "true") {
            document.getElementById(notifCheckboxNames[i]).checked = true;
        } else {
            document.getElementById(notifCheckboxNames[i]).checked = false;
        }
    }


});

function addParkingHour() {
    var time;
    var hour = document.getElementById("parkingHourField").value;
    var minutes = document.getElementById("parkingMinuteField").value;
    document.getElementById("parkingHourMessage").innerHTML = '';
    if (localStorage.getItem("parkingTimes") === null) {
        var parkingTimes = [];
    } else {
        var parkingTimes = JSON.parse(localStorage.getItem("parkingTimes"));
    }
    time = hour + ":" + minutes;
    if (parkingTimes.indexOf(time) === -1) {
        parkingTimes.push(time);
        localStorage.setItem("parkingTimes", JSON.stringify(parkingTimes));
        var td1 = document.createElement("td");
        var btn = document.createElement("button");
        btn.setAttribute("onclick", "removeParkingHour(this)");
        btn.setAttribute("type", "button-object");
        btn.setAttribute("class", "btn btn-default");
        btn.innerHTML = time + " ";
        var ic = document.createElement("i");
        ic.setAttribute("class", "fa fa-minus");
        ic.setAttribute("style", "color:red");
        btn.appendChild(ic);
        td1.appendChild(btn);
        document.getElementById("parkingHourRow").appendChild(td1);
        //$("#parkingHourRow").appendChild('<td><button onclick="removeParkingHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        setParkingNotif(hour, minutes);
    } else {
        document.getElementById("parkingHourMessage").innerHTML = "Dit uur is al toegevoegd";
    }
}

function removeParkingHour(value) {
    value.parentNode.parentNode.removeChild(value.parentNode);
    var parkingTimes = JSON.parse(localStorage.getItem("parkingTimes"));
    time = value.innerText.trim();
    var index = parkingTimes.indexOf(time);    // <-- Not supported in <IE9
    if (index !== -1) {
        parkingTimes.splice(index, 1);
    }
    localStorage.setItem("parkingTimes", JSON.stringify(parkingTimes));
    updateNotifs();
    //saveLocalStorage();
}

function addBBParkingHour() {
    var time;
    var hour = document.getElementById("BBparkingHourField").value;
    var minutes = document.getElementById("BBparkingMinuteField").value;
    document.getElementById("BBparkingHourMessage").innerHTML = '';
    if (localStorage.getItem("BBparkingTimes") === null) {
        var BBparkingTimes = [];
    } else {
        var BBparkingTimes = JSON.parse(localStorage.getItem("BBparkingTimes"));
    }
    time = hour + ":" + minutes;
    if (BBparkingTimes.indexOf(time) === -1) {
        BBparkingTimes.push(time);
        localStorage.setItem("BBparkingTimes", JSON.stringify(BBparkingTimes));
        var td1 = document.createElement("td");
        var btn = document.createElement("button");
        btn.setAttribute("onclick", "removeBBParkingHour(this)");
        btn.setAttribute("type", "button-object");
        btn.setAttribute("class", "btn btn-default");
        btn.innerHTML = time + " ";
        var ic = document.createElement("i");
        ic.setAttribute("class", "fa fa-minus");
        ic.setAttribute("style", "color:red");
        btn.appendChild(ic);
        td1.appendChild(btn);
        document.getElementById("BBparkingHourRow").appendChild(td1);
        //$("#BBparkingHourRow").appendChild('<td><button onclick="removeBBParkingHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        setBBParkingNotif(hour, minutes);
    } else {
        document.getElementById("BBparkingHourMessage").innerHTML = "Dit uur is al toegevoegd";
    }
    //saveLocalStorage();
}

function removeBBParkingHour(value) {
    value.parentNode.parentNode.removeChild(value.parentNode);
    var BBparkingTimes = JSON.parse(localStorage.getItem("BBparkingTimes"));
    time = value.innerText.trim();
    var index = BBparkingTimes.indexOf(time);    // <-- Not supported in <IE9
    if (index !== -1) {
        BBparkingTimes.splice(index, 1);
    }
    localStorage.setItem("BBparkingTimes", JSON.stringify(BBparkingTimes));
    updateNotifs();
    //saveLocalStorage();
}

function addCoyoteHour() {
    var time;
    var hour = document.getElementById("CoyoteHourField").value;
    var minutes = document.getElementById("CoyoteMinuteField").value;
    document.getElementById("CoyoteHourMessage").innerHTML = '';
    if (localStorage.getItem("CoyoteTimes") === null) {
        var CoyoteTimes = [];
    } else {
        var CoyoteTimes = JSON.parse(localStorage.getItem("CoyoteTimes"));
    }
    time = hour + ":" + minutes;
    if (CoyoteTimes.indexOf(time) === -1) {
        CoyoteTimes.push(time);
        localStorage.setItem("CoyoteTimes", JSON.stringify(CoyoteTimes));
        var td1 = document.createElement("td");
        var btn = document.createElement("button");
        btn.setAttribute("onclick", "removeCoyoteHour(this)");
        btn.setAttribute("type", "button-object");
        btn.setAttribute("class", "btn btn-default");
        btn.innerHTML = time + " ";
        var ic = document.createElement("i");
        ic.setAttribute("class", "fa fa-minus");
        ic.setAttribute("style", "color:red");
        btn.appendChild(ic);
        td1.appendChild(btn);
        document.getElementById("CoyoteHourRow").appendChild(td1);
        //$("#CoyoteHourRow").appendChild('<td><button onclick="removeCoyoteHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        setCoyoteNotif(hour, minutes);
    } else {
        document.getElementById("CoyoteHourMessage").innerHTML = "Dit uur is al toegevoegd";
    }
    //saveLocalStorage();
}

function removeCoyoteHour(value) {
    value.parentNode.parentNode.removeChild(value.parentNode);
    var CoyoteTimes = JSON.parse(localStorage.getItem("CoyoteTimes"));
    time = value.innerText.trim();
    var index = CoyoteTimes.indexOf(time);    // <-- Not supported in <IE9
    if (index !== -1) {
        CoyoteTimes.splice(index, 1);
    }
    localStorage.setItem("CoyoteTimes", JSON.stringify(CoyoteTimes));
    updateNotifs();
    //saveLocalStorage();
}

function addWeatherHour() {
    var time;
    var hour = document.getElementById("WeatherHourField").value;
    var minutes = document.getElementById("WeatherMinuteField").value;
    document.getElementById("WeatherHourMessage").innerHTML = '';
    if (localStorage.getItem("WeatherTimes") === null) {
        var WeatherTimes = [];
    } else {
        var WeatherTimes = JSON.parse(localStorage.getItem("WeatherTimes"));
    }
    time = hour + ":" + minutes;
    if (WeatherTimes.indexOf(time) === -1) {
        WeatherTimes.push(time);
        localStorage.setItem("WeatherTimes", JSON.stringify(WeatherTimes));
        var td1 = document.createElement("td");
        var btn = document.createElement("button");
        btn.setAttribute("onclick", "removeWeatherHour(this)");
        btn.setAttribute("type", "button-object");
        btn.setAttribute("class", "btn btn-default");
        btn.innerHTML = time + " ";
        var ic = document.createElement("i");
        ic.setAttribute("class", "fa fa-minus");
        ic.setAttribute("style", "color:red");
        btn.appendChild(ic);
        td1.appendChild(btn);
        document.getElementById("WeatherHourRow").appendChild(td1);
        //$("#WeatherHourRow").appendChild('<td><button onclick="removeWeatherHour(this)" type="button" class="btn btn-default">' + time + ' <i class="fa fa-minus" style="color:red"></i></button></td>');
        setWeatherNotif(hour, minutes);
    } else {
        document.getElementById("WeatherHourMessage").innerHTML = "Dit uur is al toegevoegd";
    }
    //saveLocalStorage();
}

function removeWeatherHour(value) {
    value.parentNode.parentNode.removeChild(value.parentNode);
    var WeatherTimes = JSON.parse(localStorage.getItem("WeatherTimes"));
    time = value.innerText.trim();
    var index = WeatherTimes.indexOf(time);    // <-- Not supported in <IE9
    if (index !== -1) {
        WeatherTimes.splice(index, 1);
    }
    localStorage.setItem("WeatherTimes", JSON.stringify(WeatherTimes));
    updateNotifs();
    //saveLocalStorage();
}

function changePWVisible() {
    document.getElementById("changePWRow").setAttribute("style", "display:true");
    document.getElementById("changePWRowBtn").setAttribute("style", "display:none");
}
function changePWInvisible() {
    document.getElementById("changePWRow").setAttribute("style", "display:none");
    document.getElementById("changePWRowBtn").setAttribute("style", "display:true; width:25%");
}
function changePW() {
    document.getElementById("changePWRow").setAttribute("style", "display:none");
    document.getElementById("changePWRowBtn").setAttribute("style", "display:true; width:25%");
    saveNewPassword();
}

function saveProfilePic() {
    if (validateURL(document.getElementById("profilepicurl").value)) {
        $("<img>", {
            src: document.getElementById("profilepicurl").value,
            error: function () {
                document.getElementById("profilepicMessage").innerHTML = "Dit is geen foto";
                document.getElementById("profilepicurl").value = "";
            },
            load: function () {
                localStorage.setItem("profilepic", document.getElementById("profilepicurl").value);
                setProfilePic();
                document.getElementById("profilepicMessage").innerHTML = "Foto opgeslagen!";
                document.getElementById("profilepicurl").value = "";
            }
        });
    } else {
        document.getElementById("profilepicMessage").innerHTML = "Geef een geldige url in.";
    }
}

function validateURL(textval) {
    var urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
    return urlregex.test(textval);
}

function setProfilePic() {
    if (localStorage.getItem("profilepic") !== null) {
        document.getElementById("profilepic").setAttribute("src", localStorage.getItem("profilepic"));
    }
}

function saveNewName() {
    var newFirstName = document.getElementById("newFirstName").value;
    var newLastName = document.getElementById("newLastName").value;
    var change = {
        firstName: newFirstName,
        lastName: newLastName
    };

    var data = JSON.stringify(change);
    $.ajax({
        type: "POST",
        url: changeNameUrl,
        data: data,
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (response) {
            if (response.success === "true") {
                document.getElementById("changeNameMessage").innerHTML = "Naam opgeslagen!";
                document.getElementById("newFirstName").value = "";
                document.getElementById("newLastName").value = "";
                document.getElementById("weatherMessage").innerHTML = "Hallo " + newFirstName + "!";
            } else {
                console.log("NewName post failed!");
                document.getElementById("changeNameMessage").innerHTML = "Er is iets misgegaan, probeer later opnieuw.";
            }
        },
        error: function (errMsg) {

        }
    });

}

function saveNewPassword() {
    var oldPW = document.getElementById("oldPW").value;
    var newPW = document.getElementById("newPW").value;
    var newPWr = document.getElementById("newPWr").value;
    document.getElementById("oldPW").value = "";
    document.getElementById("newPW").value = "";
    document.getElementById("newPWr").value = "";
    var pwTest = isOkPass(newPW);
    if (oldPW === "" || newPW === "" || newPWr === "") {
        document.getElementById("changePWMessage").innerHTML = "Vul alle velden in";
    } else if (newPW !== newPWr) {
        document.getElementById("changePWMessage").innerHTML = "Nieuw wachtwoord moet in beide velden hetzelfde zijn.";
    } else if (pwTest.result === false) {
        document.getElementById("changePWMessage").innerHTML = pwTest.error;
    } else {
        var oldpassword = {
            password: oldPW
        };

        var data = JSON.stringify(oldpassword);
        $.ajax({
            type: "POST",
            url: verifyPasswordUrl,
            data: data,
            dataType: 'json',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            success: function (response) {
                if (response.success === "true") {
                    var newpassword = {
                        password: newPW
                    };

                    var data2 = JSON.stringify(newpassword);
                    $.ajax({
                        type: "POST",
                        url: changePasswordUrl,
                        data: data2,
                        dataType: 'json',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json',
                        },
                        success: function (response) {
                            if (response.success === "true") {
                                document.getElementById("changePWMessage").innerHTML = "Wachtwoord wijzigen geslaagd.";
                                document.getElementById("changePWRow").setAttribute("style", "display:none");
                                document.getElementById("changePWRowBtn").setAttribute("style", "display:true; width:25%");
                            } else {
                                document.getElementById("changePWMessage").innerHTML = "Er is iets misgegaan, probeer later opnieuw.";
                            }
                        },
                        error: function (errMsg) {
                            document.getElementById("changePWMessage").innerHTML = "Er is iets misgegaan, probeer later opnieuw.";
                        }
                    });
                } else {
                    document.getElementById("changePWMessage").innerHTML = "Oud wachtwoord foutief.";
                }
            },
            error: function (errMsg) {

            }
        });
    }


}

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

function deleteAccount() {
    var r = confirm("Wilt u echt uw account verwijderen? Dit is definitief!");
    if (r === true) {
        $.ajax({
            type: "DELETE",
            url: deleteUserUrl,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            success: function (response) {
                if (response.success === "true") {
                    document.getElementById("deleteAccountMessage").innerHTML = "Account verwijderd!";
                    document.location.href = "index.html";
                } else {
                    console.log("Delete post failed!");
                    document.getElementById("deleteAccountMessage").innerHTML = "Er is iets misgegaan, probeer later opnieuw.";
                }
            },
            error: function (errMsg) {

            }
        });
    }
}

function setBackground() {
    var backgroundIndex = localStorage.getItem("backgroundIndex");
    if (backgroundIndex === 0 || backgroundIndex === null || backgroundIndex === undefined) {
        $('body').css('background-image', 'none');
    } else {
        $('body').css('background-image', 'url(img/background' + backgroundIndex + '.jpg)');
    }
}

function setCSS() {
    var setting = localStorage.getItem("CSSSwitch");
    if (setting === "true") {
        //Dark
        //console.log("dark");
        changeCSS("css/custom2.css");
    } else {
        //Light
        //console.log("light");
        changeCSS("css/custom.css");
    }
}

function changeCSS(cssFile) {
    var oldlink;
    var oldlinks = document.getElementsByTagName("link");
    for (i = 0; i < oldlinks.length; i++)
    {
        if (oldlinks[i].href.includes("custom")) {
            oldlink = oldlinks[i];
        }

    }
    //console.log(oldlink);

    var newlink = document.createElement("link");
    newlink.setAttribute("rel", "stylesheet");
    newlink.setAttribute("type", "text/css");
    newlink.setAttribute("href", cssFile);

    //console.log(document.getElementsByTagName("head"));

    document.getElementsByTagName("head").item(0).replaceChild(newlink, oldlink);
}

