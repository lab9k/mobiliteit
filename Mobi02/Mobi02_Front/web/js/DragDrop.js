/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var grid;
var items;
var navOpen = false;
var colsetting = "col-md-4";

function makeGrid() {
    var $grid = $('.grid').packery({
        itemSelector: '.grid-item',
        columnWidth: '.grid-sizer',
        percentPosition: true
    });
}

function makeDrag() {
    //check if items is already defined and unbind them before binding other items
    if (typeof grid !== 'undefined' && typeof items !== 'undefined') {
        // unbind drag events to Packery
        grid.packery('unbindUIDraggableEvents', items);
    }

    grid = $('.grid').packery({
        itemSelector: '.grid-item',
        columnWidth: '.grid-sizer',
        percentPosition: true
    });

// make all items draggable
    if (document.getElementById("DragAndDropBox").checked === true) {
        items = grid.find('.grid-item').draggable({
            handle: "#dragIcon"
        });
    } else {
        items = grid.find('.grid-item').draggable({
            handle: "#null"
        });
        $(dragIcon).css("cursor", "auto");
    }
// bind drag events to Packery
    grid.packery('bindUIDraggableEvents', items);

}

function enlargeMap() {
    var panel = document.getElementById("mapitem");
    if (!panel.className.includes(colsetting)) {
        panel.className = "grid-item " + colsetting;
        document.getElementById("map").style.height = "300px";
        initMap();
        checkAll();
        document.getElementById("zoomiconMap").src = "img/zoomin.png";
        document.getElementById("zoomiconMap").title = "Vergroot de kaart";
        makeDrag();
    } else {
        panel.className = "grid-item col-md-12";
        document.getElementById("map").style.height = "600px";
        initMap(14);
        checkAll();
        document.getElementById("zoomiconMap").src = "img/zoomout.png"
        document.getElementById("zoomiconMap").title = "Verklein de kaart";
        makeDrag();
    }


}

function enlargeGraph() {
    var panel = document.getElementById("graphitem");
    var colvalue = localStorage.getItem("colvalue");
    var graphsetting = "col-md-" + (24 / colvalue);
    if (!panel.className.includes(colsetting)) {
        panel.className = "grid-item " + colsetting;
        //document.getElementById("graph").style.height = "auto";
        document.getElementById("zoomiconGraph").src = "img/zoomin.png";
        document.getElementById("zoomiconGraph").title = "Vergroot de grafiek";
        fillGraph(todayData,histData);
    } else {
        panel.className = "grid-item " + graphsetting;
        document.getElementById("graph").style.height = "auto";
        document.getElementById("zoomiconGraph").src = "img/zoomout.png"
        document.getElementById("zoomiconGraph").title = "Verklein de grafiek";
        fillGraph(todayData,histData);
    }


}

/* Set the width of the side navigation to 250px and the left margin of the page content to 250px */
function openNav() {
    document.getElementById("mySidenav").style.width = "270px";
//    document.getElementById("main").style.marginLeft = "270px";
}

/* Set the width of the side navigation to 0 and the left margin of the page content to 0 */
function closeNav() {
    document.getElementById("mySidenav").style.width = "0";
//    document.getElementById("main").style.marginLeft = "0";
}

function toggleNav() {
    if (navOpen) {
        closeNav();
        navOpen = false;
    } else {
        openNav();
        navOpen = true;
    }
}

function toggleDragAndDrop() {
    if (document.getElementById("DragAndDropBox").checked === false) {

        items = grid.find('.grid-item').draggable({
            handle: "#null"
        });
        //grid.packery('unbindUIDraggableEvents', items);
        $(dragIcon).css("cursor", "auto");
        localStorage.setItem("DragAndDropBox", false);
    } else {
        items = grid.find('.grid-item').draggable({
            handle: "#dragIcon"
        });
        $(dragIcon).css("cursor", "move");
        localStorage.setItem("DragAndDropBox", true);
    }
    //saveLocalStorage();
}

function changeCols(value) {
    var largeMap = false;
    if (document.getElementById("mapitem").className.includes("col-md-12")) {
        largeMap = true;
    }
    var largeGraph = false;
    if (document.getElementById("graphitem").className.includes("col-md-12")) {
        largeGraph = true;
    }
    $('.grid-sizer').removeClass(colsetting);
    $('.grid-item').removeClass(colsetting);

    document.getElementById("colcounter").innerText = value;
    colsetting = "col-md-" + (12 / value);
    $('.grid-sizer').addClass(colsetting);
    $('.grid-item').addClass(colsetting);
    localStorage.setItem("colvalue", value);
    if (largeMap === true) {
        document.getElementById("mapitem").className = "grid-item col-md-12";
    }
    if (largeGraph === true) {
        document.getElementById("graphitem").className = "grid-item col-md-12";
    }
    makeDrag();
    initMap();
    checkAll();
    //saveLocalStorage();
}

$(document).click(function (event) {
    if (event.pageX > 270 && navOpen) {
        closeNav();
        navOpen = false;
    }
});
