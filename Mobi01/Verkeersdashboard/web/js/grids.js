/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var socket;
var KEY_LOCAL_STORAGE_ACTIVE_WIDGETS = 'ACTIVE_WIDGETS';
var KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE = 'ACTIVE_WIDGETS_MOBILE';
var WIDGET_HTML_TEMPLATE = '<div class="widget" id="{0}" data-widget-id="{1}">'
        + '<div class="panel panel-default grid-stack-item-content">'
        + '<div class="panel-heading portlet-header">'
        + '<h3 class="panel-title header">{2}</h3>'
        + '</div>'
        + '<div class="panel-body content" id="{3}">'
        + '{4}'
        + '</div>'
        + '</div>'
        + '</div>';
var slide;
var grid;
var gridInactive;
var lastLoggedID;
var loggedIn;
var widgetConfig;

window.onload = init;

function init() {
    slide = $('.targetslide');
    try {
        initLogin();
    } catch (ex) {
        console.warn('Exception: initialisation of user login failed.');
    }
    loadGrid();
}

function loadConfiguration() {
    if (typeof (widgetConfig) !== "undefined") {
        console.info("Er is een configuratie aanwezig");
        if (typeof (Storage) !== "undefined") {

            localStorage.setItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS, widgetConfig);
            loadDefaultConfiguration(false);
        }
    }
    $("#mainMapCheckBoxDiv").toggle($("#buttoneditwidgets").hasClass("active"));
}

function loadGrid() {
    var options = {
        width: 9,
        heigth: 50,
        float: false,
        acceptWidgets: '.grid-stack-item',
        resizable: {handles: 'e, se, s, sw, w'},
        alwaysShowResizeHandle: (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) && !(window.innerWidth<768))
    };
    $('#grid-active-widgets').gridstack(options);
    $('#grid-inactive-widgets').gridstack(_.defaults({
        width: 3
    }, options));

    grid = $('#grid-active-widgets').data('gridstack');
    gridInactive = $('#grid-inactive-widgets').data('gridstack');

    var activeItems;
    if (grid._isOneColumnMode()) {
        activeItems = loadMobileConfig(false);
    } else {
        activeItems = loadDefaultConfig(false);
    }

    loadWidgetsInGrid(activeItems);

    grid.setAnimation(true);
    grid.disable();
    initWidgets();
    initMobileSystem();
    prevMobileState = grid._isOneColumnMode();
}

function loadMobileConfig(hardReset) {
    var activeItemsMobile;
    if (!hardReset && typeof (Storage) !== "undefined" && localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE) !== null) {
        console.info("Er is een mobiele configuratie opgeslagen");
        activeItemsMobile = $.parseJSON(localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE));
    } else {
        activeItemsMobile = [{"type": "mainMap", "x": 0, "y": 0, "width": 9, "height": 7},
            {"type": "buienradar", "x": 0, "y": 8, "width": 2, "height": 5},
            {"type": "weather", "x": 0, "y": 14, "width": 2, "height": 3},
            {"type": "parkingBezetting", "x": 0, "y": 18, "width": 2, "height": 5},
            {"type": "train", "x": 0, "y": 24, "width": 4, "height": 5},
            {"type": "bluebike", "x": 0, "y": 28, "width": 2, "height": 3},
            {"type": "chart", "x": 0, "y": 34, "width": 5, "height": 5},
            {"type": "historic", "x": 0, "y": 40, "width": 5, "height": 6},
            {"type": "vgs", "x": 0, "y": 47, "width": 3, "height": 3},
            {"type": "pollution", "x": 0, "y": 51, "width": 3, "height": 4},
            {"type": "delijn", "x": 0, "y": 56, "width": 5, "height": 5}];
    }
    return activeItemsMobile;
}

function loadDefaultConfig(hardReset) {
    var activeItemsDefault;
    if (!hardReset && typeof (Storage) !== "undefined" && localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS) !== null) {
        activeItemsDefault = $.parseJSON(localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS));
    } else {
        activeItemsDefault = [{"type": "mainMap", "x": 0, "y": 0, "width": 9, "height": 7},
            {"type": "historic", "x": 0, "y": 15, "width": 9, "height": 6},
            {"type": "buienradar", "x": 0, "y": 7, "width": 3, "height": 5},
            {"type": "pollution", "x": 3, "y": 7, "width": 3, "height": 5},
            {"type": "parkingBezetting", "x": 6, "y": 7, "width": 3, "height": 5},
            {"type": "bluebike", "x": 6, "y": 12, "width": 3, "height": 3},
            {"type": "weather", "x": 4, "y": 12, "width": 2, "height": 3},
            {"type": "delijn", "x": 0, "y": 21, "width": 5, "height": 5},
            {"type": "vgs", "x": 0, "y": 12, "width": 4, "height": 3},
            {"type": "train", "x": 5, "y": 21, "width": 4, "height": 5},
            {"type": "chart", "x": 0, "y": 26, "width": 9, "height": 6}];
    }
    return activeItemsDefault;
}

function loadWidgetsInGrid(activeItems) {
    var inactiveItems;

    _.each(activeItems, function (node) {
        addWidget(node, true);
    }, this);

    inactiveItems = allTypes.slice();

    for (var i = 0; i < activeItems.length; i++) {
        var index = inactiveItems.indexOf(activeItems[i].type);
        inactiveItems.splice(index, 1);
    }

    for (var i = 0; i < inactiveItems.length; i++) {
        var node = {"type": inactiveItems[i], "x": 0, "y": 0, "width": 3, "height": 4};
        addWidget(node, false);
    }
}

function initMobileSystem() {
    $(document).on('click', '.mobileUp', function () {
        $(this).parents('.widget').after($(this).parents('.widget').prev());
    });
    $(document).on('click', '.mobileDown', function () {
        $(this).parents('.widget').before($(this).parents('.widget').next());
    });
    $(document).on('click', '.mobileClose', function () {
        if ($(this).parents('#grid-inactive-widgets').length) {
            $(this).parents('.widget').prependTo($('#grid-active-widgets'));
        } else {
            $(this).parents('.widget').prependTo($('#grid-inactive-widgets'));
        }
        fixMobileDisplay();
    });
}

function editwidgets() {
    try {
        slide.toggle('slow');
        $("#factorySettings").toggle(300);
        $(".active-widgets").toggleClass("green");
        $(".active-widgets").toggleClass("transparent");
        $("#mainMapCheckBoxDiv").toggle(1000, 'easeInOutBack');
        $("#glyphSpanEditWidgetsButton").toggleClass('glyphicon-floppy-save');
        $("#glyphSpanEditWidgetsButton").toggleClass('glyphicon-pencil');
        $("#userInstructionsEditMode").slideToggle('slow');

        $("#buttoneditwidgets").contents().filter(function () {
            return (this.nodeType === 3);
        }).remove();

        if ($("#buttoneditwidgets").hasClass("active")) {
            //Go to normal mode

            $("#buttoneditwidgets").removeClass("active");

            $("#buttoneditwidgets").append("Pas widgets aan");

            grid.disable();

            if (typeof (Storage) !== "undefined") {
                var res = _.map($('#grid-active-widgets .grid-stack-item:visible'), function (el) {
                    el = $(el);
                    var node = el.data('_gridstack_node');
                    return {
                        type: el.attr('data-widget-id'),
                        x: node.x,
                        y: node.y,
                        width: node.width,
                        height: node.height
                    };
                });
                var data = JSON.stringify(res);
                if (grid._isOneColumnMode()) {
                    localStorage.setItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE, data);
                } else {
                    localStorage.setItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS, data);
                }
                if (loggedIn && lastLoggedID !== null) {
                    var message = {
                        action: "saveWidgetPreferences",
                        widgetpref: $.parseJSON(localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS)),
                        mobile_widgetpref: $.parseJSON(localStorage.getItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE)),
                        userID: lastLoggedID
                    };
                    socket.send(JSON.stringify(message));
                    widgetConfig = data;
                    console.log(message);
                }
            } else {
                console.warn("Can't store widget position preferences.");
            }
        } else {
            //Go to edit mode
            $("#buttoneditwidgets").append("Sla widgets op");
            $("#buttoneditwidgets").addClass("active");
            grid.enable();
        }
        fixMobileDisplay();
        fixRouteInstructionsScroll();
        leafletMap.invalidateSize();
    } catch (ex) {
        console.warn('Exception: page probably isn\'t fully loaded yet.');
    }
}

function fixMobileDisplay() {
    if (grid._isOneColumnMode()) {  //state = mobile
        if (!$("#buttoneditwidgets").hasClass("active")) {
            //enter edit mode in mobile state
            $('.infoglyph').show();
            $('.mobileControl').hide();
        } else {
            //leave edit mode in mobile state
            $('.infoglyph').hide();
            $('.mobileControl').show();
        }
        $('#grid-inactive-widgets .mobileClose>span').removeClass('glyphicon-remove').addClass('glyphicon-plus');
        $('#grid-active-widgets .mobileClose>span').removeClass('glyphicon-plus').addClass('glyphicon-remove');
    }
}

function loadDefaultConfiguration(hardReset) {
    grid.removeAll();
    gridInactive.removeAll();
    if (grid._isOneColumnMode()) {
        loadWidgetsInGrid(loadMobileConfig(hardReset));
    } else {
        loadWidgetsInGrid(loadDefaultConfig(hardReset));
    }
    letDie = true;
    socket.close();
    routesInitialized = false;
    initWidgets();
    $("#mainMapCheckBoxDiv").toggle(true);
}

var prevMobileState;
var init = true;
$(window).on('resize', function () {
    try {
        if (prevMobileState != grid._isOneColumnMode() && !init) {
            console.info('clearing and restoring grid');
            $("#leaflet").append($("#leafletMap"));
            $('#leafletModalDialog').hide();
            $("#buttoneditwidgets").contents().filter(function () {
                return (this.nodeType === 3);
            }).remove();
            $("#buttoneditwidgets").append("Pas widgets aan");
            
            grid.removeAll();
            gridInactive.removeAll();
            if (grid._isOneColumnMode()) {
                loadWidgetsInGrid(loadMobileConfig(false));
            } else {
                loadWidgetsInGrid(loadDefaultConfig(false));
            }
            prevMobileState = grid._isOneColumnMode();
            letDie = true;
            socket.close();
            routesInitialized = false;
            if ($("#buttoneditwidgets").hasClass("active")) {
                $("#buttoneditwidgets").removeClass("active");
                //editwidgets();
                slide.toggle(false);
                $("#buttoneditwidgets").removeClass("active")
                $(".active-widgets").removeClass("green");
                $(".active-widgets").addClass("transparent");
                $("#mainMapCheckBoxDiv").toggle(false);
                $("#glyphSpanEditWidgetsButton").removeClass('glyphicon-floppy-save');
                $("#glyphSpanEditWidgetsButton").addClass('glyphicon-pencil');
                $("#userInstructionsEditMode").toggle(false);
            }
            initWidgets();
            grid.disable();
        }
        init = false;
    } catch (e) {
        console.error(e);
    }
});

if (!String.format) {
    String.format = function (format) {
        var args = Array.prototype.slice.call(arguments, 1);
        return format.replace(/{(\d+)}/g, function (match, number) {
            return typeof args[number] !== 'undefined'
                    ? args[number]
                    : match
                    ;
        });
    };
}