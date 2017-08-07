/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//var changes = false;

//function saveLocalStorage() {
//    $.ajax({
//        type: "GET",
//        url: loginUrl,
//        dataType: 'json',
//        headers: {
//            'Accept': 'application/json',
//            'Content-Type': 'application/json',
//        },
//        success: function (data) {
//            if (data.hasOwnProperty("loggedIn")) {
//                if (data.loggedIn === "True") {
//                    changes = true;
//                    document.getElementById("saveLocalStorageButton").setAttribute("style", "display:true");
//                }
//            }
//        },
//        error: function (errMsg) {
//        }
//    });
//
//
//}

function postLocalStorage() {
    var valueString = JSON.stringify(localStorage);
    var storage = {
        keyString: "LocalStorage",
        valueString: valueString
    };
    var data = JSON.stringify(storage);
    $.ajax({
        type: "POST",
        url: propertiesUrl,
        data: data,
        dataType: 'json',
        async: false,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (response) {
            if (response.success === "true") {
               // console.log("LocalStorage post succes!");
                //changes = false;
                //document.getElementById("saveLocalStorageButton").setAttribute("style", "display:none");
            } else {
                console.log("LocalStorage post failed!");
            }
        },
        error: function (errMsg) {

        }
    });
}

function getLocalStorage(callback) {
    $.ajax({
        type: "GET",
        url: propertiesUrl + "/LocalStorage",
        dataType: 'json',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        success: function (data) {
            console.log("get LocalStorage successful");
            if (data.hasOwnProperty("valueString")) {
                var o = JSON.parse(data.valueString);
                for (var property in o) {
                    if (o.hasOwnProperty(property)) {
                        localStorage.setItem(property, o[property]);
                    }
                }
            }

            if (callback !== undefined) {
                callback();
            }
        },
        error: function (errMsg) {
            if (callback !== undefined) {
                callback();
            }
        }
    });
}

window.onbeforeunload = function () {
    postLocalStorage();
};