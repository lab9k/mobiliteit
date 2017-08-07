/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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

function setCSS(){
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

function toggleCSS() {
    if (document.getElementById("CSSSwitchBox").checked === true) {
        //Dark
        //console.log("dark");
        changeCSS("css/custom2.css");
        localStorage.setItem("CSSSwitch", true);
    } else {
        //Light
        //console.log("light");
        changeCSS("css/custom.css");
        localStorage.setItem("CSSSwitch", false);
    }
}



$("#myCarousel").on('slid.bs.carousel', function () {
    //alert('The carousel has finished sliding from one item to another!');
    var backgroundIndex = $('div.active').index();
    if (backgroundIndex === 0 || backgroundIndex === null) {
        $('body').css('background-image', 'none');
    } else {
        $('body').css('background-image', 'url(img/background' + backgroundIndex + '.jpg)');
    }
    localStorage.setItem("backgroundIndex", backgroundIndex);

});

function setBackground() {
    var backgroundIndex = localStorage.getItem("backgroundIndex");
    $("#myCarousel").carousel(parseInt(backgroundIndex));
    if (backgroundIndex === 0 || backgroundIndex === null || backgroundIndex === undefined) {
        $('body').css('background-image', 'none');
    } else {
        $('body').css('background-image', 'url(img/background' + backgroundIndex + '.jpg)');
    }
}
