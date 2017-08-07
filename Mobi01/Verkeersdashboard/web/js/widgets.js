var KEY_MAP_MARKERS = "mapMarkers";
var PARKANDRIDESCHECKBOXID = "parkAndRidesCheckbox";
var GIPODIMPORTANTCHECKBOXID = "GIPODImportantCheckbox";
var GIPODCHECKBOXID = "GIPODCheckbox";
var COUNTPOINTCHECKBOXID = "countpointCheckbox";
var PARKINGCHECKBOXID = "parkingsCheckbox";
var COYOTESECTIONSCHECKBOXID = "coyoteRoadSectionsCheckbox";
var COYOTEALERTSCHECKBOXID = "coyoteMarkersCheckbox";
var PARKINGFEEAREACHECKBOXID = "parkingFeeAreaCheckbox";
var routingControl;
var startstation = "Gent Sint Pietersstation";
var socket;
var widgetConfig;
var WidgetEnum = {
    WEATHER: "WEATHER",
    BLUEBIKE: "BLUEBIKE",
    PARKING: "PARKING",
    TRAIN: "TRAIN",
    DELIJN: "DELIJN",
    PARKANDRIDE: "PARKANDRIDE",
    NMBS: "NMBS",
    COYOTE: "COYOTE",
    COUNTPOINT: "COUNTPOINT",
    GIPOD: "GIPOD",
    POLLUTION: "POLLUTION",
    VGS: "VGS",
    HISTORIC: "HISTORIC",
    PARKINGFEEAREA: "PARKINGFEEAREA"
};
var coyoteIcon = L.icon({
    iconUrl: 'img/coyote.png',
    iconSize: [20, 20], // size of the icon
    iconAnchor: [10, 10], // point of the icon which will correspond to marker's location
    popupAnchor: [0, -10] // point from which the popup should open relative to the iconAnchor
});
var parkAndRideIcon = L.icon({
    iconUrl: 'img/park-and-ride.png',
    iconSize: [20, 20], // size of the icon
    iconAnchor: [10, 10], // point of the icon which will correspond to marker's location
    popupAnchor: [0, -10] // point from which the popup should open relative to the iconAnchor
});
var parkingIcon = L.icon({
    iconUrl: 'img/parking.png',
    iconSize: [20, 20], // size of the icon
    iconAnchor: [10, 10], // point of the icon which will correspond to marker's location
    popupAnchor: [0, -10] // point from which the popup should open relative to the iconAnchor
});
var GIPODIcon = L.icon({
    iconUrl: 'img/GIPOD.png',
    iconSize: [20, 20], // size of the icon
    iconAnchor: [10, 10], // point of the icon which will correspond to marker's location
    popupAnchor: [0, -10] // point from which the popup should open relative to the iconAnchor
});

var GIPODIconImportant = L.icon({
    iconUrl: 'img/GIPODImportant.png',
    iconSize: [20, 20], // size of the icon
    iconAnchor: [10, 10], // point of the icon which will correspond to marker's location
    popupAnchor: [0, -10] // point from which the popup should open relative to the iconAnchor
});

var leafletMap;
var map;

//global variable to store shown/hidden-state of the different map features
var mapMarkers;
var routesInitialized = false;
var historicChart;

function onMessage(event) {
    var msg = JSON.parse(event.data);
    console.log(msg);
    switch (msg.action) {
        //in case we have data for a certain widget

        case "data":
            switch (msg.widgetType) {
                case WidgetEnum.PARKANDRIDE:
                    updateParkAndRidesLeaflet(msg);
                    break;
                case WidgetEnum.COUNTPOINT:
                    countUpdate(msg);
                    break;
                case WidgetEnum.WEATHER:
                    weatherUpdate(msg);
                    break;
                case WidgetEnum.NMBS:
                    nmbsUpdate(msg);
                    break;
                case WidgetEnum.BLUEBIKE:
                    bluebikeUpdate(msg);
                    break;
                case WidgetEnum.COYOTE:
                    coyoteUpdate(msg);
                    chartUpdate(msg);
                    if (!routesInitialized)
                        initializeRoutes(msg);
                    break;
                case WidgetEnum.GIPOD:
                    GIPODUpdate(msg);
                    break;
                case WidgetEnum.POLLUTION:
                    pollutionUpdate(msg);
                    break;
                case WidgetEnum.VGS:
                    VGSUpdate(msg);
                    break;
                case WidgetEnum.HISTORIC:
                    historicChartUpdate(msg);
                    break;
                case WidgetEnum.PARKING:
                    parkingUpdate(msg);
                    break;
                case WidgetEnum.PARKINGFEEAREA:
                    parkingFeeAreaUpdate(msg);
                    break;
                default:
                    break;
            }
            break;
            //in case we need to deactivate a widget
        case "remove":
            //deactive widget: for now the same as error handling
        case "error":
            switch (msg.widgetType) {
                case WidgetEnum.PARKANDRIDE:
                    deactivateMapFeatureCheckbox(PARKANDRIDESCHECKBOXID);
                    clearMap(parkAndRideMarkers);
                    break;
                case WidgetEnum.COUNTPOINT:
                    deactivateMapFeatureCheckbox(COUNTPOINTCHECKBOXID);
                    clearMap(countpointCircles);
                    break;
                case WidgetEnum.WEATHER:
                    resetWidget('weather');
                    break;
                case WidgetEnum.NMBS:
                    resetWidget('train');
                    break;
                case WidgetEnum.BLUEBIKE:
                    resetWidget('bluebike');
                    break;
                case WidgetEnum.COYOTE:
                    deactivateMapFeatureCheckbox(COYOTEALERTSCHECKBOXID);
                    deactivateMapFeatureCheckbox(COYOTESECTIONSCHECKBOXID);
                    clearMap(coyotePolylines);
                    clearMap(coyoteMarkers);
                    resetWidget('chart');
                    resetWidget('historic');
                    break;
                case WidgetEnum.GIPOD:
                    deactivateMapFeatureCheckbox(GIPODCHECKBOXID);
                    deactivateMapFeatureCheckbox(GIPODIMPORTANTCHECKBOXID);
                    leafletMap.removeLayer(GIPODGroupMarker);
                    leafletMap.removeLayer(GIPODImportantGroupMarker);
                    break;
                case WidgetEnum.POLLUTION:
                    resetWidget('pollution');
                    break;
                case WidgetEnum.VGS:
                    resetWidget('vgs');
                    break;
                case WidgetEnum.PARKING:
                    resetWidget('parkingBezetting');
                    deactivateMapFeatureCheckbox(PARKINGCHECKBOXID);
                    clearMap(parkingMarkers);
                    break;
                case WidgetEnum.PARKINGFEEAREA:
                    deactivateMapFeatureCheckbox(PARKINGFEEAREACHECKBOXID);
                    clearMap(parkingFeeAreaPolygons);
                    break;
                default:
                    break;
            }
            break;
            //log the user ID for use when we want to load this users configuration
        case "loginAuthenticated":
            lastLoggedID = msg.userID;
            if (msg.hasOwnProperty("widgetPref")) {
                widgetConfig = msg.widgetPref;
            }
            if (msg.hasOwnProperty("mobile_widgetpref")) {
                localStorage.setItem(KEY_LOCAL_STORAGE_ACTIVE_WIDGETS_MOBILE, msg.mobile_widgetpref);
            }
            if (msg.hasOwnProperty("notify_rain")) {
                if (msg.notify_rain === "1") {
                    $('#notify_rain').prop('checked', true);
                } else {
                    $('#notify_rain').prop('checked', false);
                }
            }
            if (msg.hasOwnProperty("notify_train")) {
                if (msg.notify_train === "1") {
                    $('#notify_train').prop('checked', true);
                    $("#treingegevens").show();
                } else {
                    $('#notify_train').prop('checked', false);
                    $("#treingegevens").hide();
                }
            }
            if (msg.hasOwnProperty("notify_mail")) {
                if (msg.notify_train === "1") {
                    $('#notify_mail').prop('checked', true);
                } else {
                    $('#notify_mail').prop('checked', false);
                }
            }
            if (msg.hasOwnProperty("notify_firebase")) {
                if (msg.notify_firebase === true) {
                    $('#notify_firebase').prop('checked', true);
                    saveNotifyMeansFirebase();
                } else {
                    $('#notify_firebase').prop('checked', false);
                }
            }
            if (msg.hasOwnProperty("trainSubscriptions")) {
                for (var i = 0; i < msg.trainSubscriptions.length; i++) {
                    var d = msg.trainSubscriptions[i];
                    addTrainToSubscriptions(d);
                }
            }
            break;
        case "subscribedTrain":
            addTrainToSubscriptions(msg);
            break;

        default:
            console.warn("unknown action from server: " + msg.action);
            break;
    }
}

function addTrainToSubscriptions(msg) {
    var tableRef = document.getElementById("trainSubscriptions").getElementsByTagName("tbody")[0];
    var newRow = tableRef.insertRow(0);
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
    var btnCell = newRow.insertCell(5);
    btnCell.innerHTML = "<button class='btn' id='" + msg.trainId + "' data-train-departuretime='" + msg.departureTime + "' data-train-endstation='" + msg.endStation + "'  data-train-dayofweek='" + msg.dayOfWeek + "' data-train-departurestation='" + msg.departureStation + "' onclick=unsubscribe(this)>Verwijder</button>";
    departDayCell.appendChild(document.createTextNode(msg.dayOfWeek));
    departTimeCell.appendChild(document.createTextNode(msg.departureTime));
    departingCell.appendChild(document.createTextNode(msg.departureStation));
    endCell.appendChild(document.createTextNode(msg.endStation));
    idCell.appendChild(document.createTextNode(msg.trainId));
}

function resetWidget(id) {
    var data = getStaticHTMLDataForWidgetId(id);
    $('#' + data.contentId).html(data.content);
}

function removeWidget(id) {
    var data = getStaticHTMLDataForWidgetId(id);
    var el = $('#' + data.id);
    grid.removeWidget(el);
}

function addWidgetByType(type) {
    if (activeItems && activeItems.length) {
        var i = 0;
        while (activeItems[i].type !== type) {
            i++;
        }
        if (i !== activeItems.length) {
            //found
            addWidget(activeItems[i], true);
            return;
        }
    }
    var node = {
        "type": type,
        "x": 0,
        "y": 0,
        "width": 3,
        "height": 4
    };
    addWidget(node, false);
}

function addWidget(node, active) {
    var data = getStaticHTMLDataForWidgetId(node.type);
    if (active) {
        grid.addWidget($(String.format(WIDGET_HTML_TEMPLATE, data.id, node.type, data.title, data.contentId, data.content)),
            node.x, node.y, node.width, node.height);
    } else {
        gridInactive.addWidget($(String.format(WIDGET_HTML_TEMPLATE, data.id, node.type, data.title, data.contentId, data.content)),
            node.x, node.y, node.width, node.height);
    }
}

function initWidgets() {
    initLeaflet();
    initRoutingEngine();
    initGIPODPrefs();
    initGIPODButtonClickHandler();
    initPRButtonClickHandler();
    initMapPopup();
    fixRouteInstructionsScroll();
    initDelijnAutocomplete();
    
    openWebsocket();
    $('[data-toggle="popover"]').popover({
        html: true,
        container: 'body'
    });
    $('body').on('click', function(e) {
        $('[data-toggle="popover"]').each(function() {
            //the 'is' for buttons that trigger popups
            //the 'has' for icons within a button that triggers a popup
            if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
                $(this).popover('hide');
            }
        });
    });
    searchPlacesDelijn(startstation);
}

function openWebsocket() {
    socket = new WebSocket("ws://localhost:8080/Verkeersdashboard/actions");
    socket.onmessage = onMessage;
    socket.onclose = connectionClosed;
    socket.onopen = function() {
        attempts = 1;
        letDie = false;
    };
}

var attempts = 1;
var letDie = false;

function connectionClosed() {
    if (!letDie) {
        var time = generateInterval(attempts);

        setTimeout(function() {
            attempts++;
            openWebsocket();
        }, time);
    }
}

function generateInterval(k) {
    if (k > 31)
        k = 31;
    return Math.min(30, ((2 << k) - 1)) * 1000;
}

function initGIPODButtonClickHandler() {
    $('#leafletMap').on('click', '.gipodDetailButton', function() {
        leafletMap.closePopup();
        requestGIPODDetail(this.value);
    });
}

function initGIPODPrefs() {
    if (typeof(Storage) !== "undefined") {
        mapMarkers = $.parseJSON(localStorage.getItem(KEY_MAP_MARKERS));
    }
    if (mapMarkers === null) {
        mapMarkers = new Object();
    }
}

function initLeaflet() {
    $("#leafletMap").html('');
    leafletMap = L.map('leafletMap').setView([51.0700, 3.7292], 11);
    var osmUrl = 'https://{s}.tile.openstreetmap.se/hydda/full/{z}/{x}/{y}.png';
    var osmAttrib = 'Map data © <a href="http://openstreetmap.org">OpenStreetMap</a> contributors and <a href="https://www.coyotesystems.be">Coyote</a>';
    var osm = new L.TileLayer(osmUrl, {
        minZoom: 5,
        maxZoom: 18,
        attribution: osmAttrib
    });
    leafletMap.addLayer(osm);
}

function initRoutingEngine() {

    function createButton(label, title, container) {
        var btn = L.DomUtil.create('button', 'dlButton dlButton' + title, container);
        btn.setAttribute('type', 'button');
        btn.innerHTML = label;
        btn.title = title;
        return btn;
    }

    // Create a plan for the routing engine
    // This plan will create specific geocoding buttons
    // Extend L.Routing.Plan to create a custom plan for GraphHopper
    var geoPlan = L.Routing.Plan.extend({

        createGeocoders: function() {

            var container = L.Routing.Plan.prototype.createGeocoders.call(this),
                timeContainer = L.DomUtil.create('div', 'dlTimeContainer dlButtonContainer', container),
                buttonContainer = L.DomUtil.create('div', 'dlButtonContainer', container),
                // Create a reverse waypoints button
                reverseButton = createButton('<span class="dlicon dlicon-swap"></span>', 'Wissel start- en eindpunt', buttonContainer),
                //http://gis.stackexchange.com/questions/193235/leaflet-routing-machine-how-to-dinamically-change-router-settings
                flexFill = L.DomUtil.create('div', 'flexFill', buttonContainer),
                loadingIcon = L.DomUtil.create('span', 'loadingRouteIcon hidden', buttonContainer),
                // Create a button for walking routes
                //walkButton = createButton('<span class="dlicon dlicon-type-walk"></span>', 'walk', container),
                carButton = createButton('<span class="caricon"></span>', 'Auto', buttonContainer),
                busButton = createButton('<span class="dlicon dlicon-type-bus"></span>', 'Bus', buttonContainer),
                tramButton = createButton('<span class="dlicon dlicon-type-tram"></span>', 'Tram', buttonContainer),
                metroButton = createButton('<span class="dlicon dlicon-type-subway"></span>', 'Metro', buttonContainer),
                trainButton = createButton('<span class="dlicon dlicon-type-train"></span>', 'Trein', buttonContainer),
                belBusButton = createButton('<span class="dlicon dlicon-type-belbus"></span>', 'Belbus', buttonContainer);

            timeContainer.innerHTML = '<button class="arrivalDepartureButton"><div class="arrivalDepartureText">Aankomst:</div></button><input type="date" id="dlDate" name="dlDate"><input type="time" id="dlTime" name="dlTime">';

            // Event to reverse the waypoints
            L.DomEvent.on(reverseButton, 'click', function() {
                var waypoints = this.getWaypoints();
                this.setWaypoints(waypoints.reverse());
            }, this);
            L.DomEvent.on(carButton, 'click', function() {
                routingControl.getRouter().options.routeCar = !routingControl.getRouter().options.routeCar;
                $('.dlButtonAuto').toggleClass('dlButtonActive', routingControl.getRouter().options.routeCar);
                routingControl.route();
            }, this);
            L.DomEvent.on(busButton, 'click', function() {
                routingControl.getRouter().options.delijnRouteParam.delijnbyBus = !routingControl.getRouter().options.delijnRouteParam.delijnbyBus;
                $('.dlButtonBus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBus);
                routingControl.route();
            }, this);
            L.DomEvent.on(trainButton, 'click', function() {
                routingControl.getRouter().options.delijnRouteParam.delijnbyTrain = !routingControl.getRouter().options.delijnRouteParam.delijnbyTrain;
                $('.dlButtonTrein').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTrain);
                routingControl.route();
            }, this);
            L.DomEvent.on(tramButton, 'click', function() {
                routingControl.getRouter().options.delijnRouteParam.delijnbyTram = !routingControl.getRouter().options.delijnRouteParam.delijnbyTram;
                $('.dlButtonTram').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTram);
                routingControl.route();
            }, this);
            L.DomEvent.on(metroButton, 'click', function() {
                routingControl.getRouter().options.delijnRouteParam.delijnbyMetro = !routingControl.getRouter().options.delijnRouteParam.delijnbyMetro;
                $('.dlButtonMetro').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyMetro);
                routingControl.route();
            }, this);
            L.DomEvent.on(belBusButton, 'click', function() {
                routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus = !routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus;
                $('.dlButtonBelbus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus);
                routingControl.route();
            }, this);
            return container;
        }
    });

    var plan = new geoPlan(
        // Empty waypoints
        [], {
            geocoder: L.Control.Geocoder.photon(),
            createGeocoder: function(i, tot, plan) {
                var container = L.DomUtil.create('div', 'input-group', plan.geocoder.container);
                var input = L.DomUtil.create('input', 'form-control', container);
                //var closeButton
                var geolocateSpan = L.DomUtil.create('span', 'input-group-btn geocoder-geolocate-btn', container);
                var geolocateBtn = L.DomUtil.create('button', 'btn btn-default btn-xs geocoder-geolocate-btn', geolocateSpan);
                geolocateBtn.setAttribute('type', 'button');
                geolocateBtn.innerHTML = '<span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span>';
                geolocateBtn.title = 'Bepaal mijn locatie';
                L.DomEvent.on(geolocateBtn, 'click', L.bind(function() {
                    geolocate(map, L.bind(function(err, p) {
                        if (err) {
                            // TODO: error message
                            return;
                        }
                        routingControl.spliceWaypoints(i, 1, p.latlng);
                    }, this));
                }, this));
                var geocoderElement = {
                    container,
                    input
                }; //, undefined};
                return geocoderElement;
            }
        });

    routingControl = L.Routing.control({
        /*waypoints: [  //korenmarkt naar Valentin Vaerwyckweg
         L.latLng(51.05446675, 3.7218942133855943),
         L.latLng(51.0333258, 3.6999149)
         ],*/
        plan: plan,
        routeWhileDragging: false,
        //geocoder: L.Control.Geocoder.photon(),
        router: L.Routing.graphHopper('12bba3e0-d130-4785-b274-bc5761c29771', {
            urlParameters: {
                locale: 'nl'
            },
            routeCar: true,
            delijnRouteParam: {
                delijnbyBus: true,
                delijnbyTram: true,
                delijnbyTrain: true,
                delijnbyMetro: true
            }
        }),
        /*router: L.Routing.graphHopper(undefined, {//werkt bij local instantie van graphhopper
         serviceUrl: 'http://localhost:8989/route',
         }),*/
        reverseWaypoints: true,
        showAlternatives: true,
        //addButtonClassName: 'hidden',
        collapsible: true,
        lineOptions: {
            styles: [{
                color: "#32bc00",
                opacity: .9,
                weight: 8
            }, {
                color: "green",
                opacity: .5,
                weight: 2,
                /*dashArray: "2,4"*/
            }, {
                color: "white",
                opacity: .3,
                weight: 6
            }]
        },
        altLineOptions: {
            styles: [{
                color: "#40007d",
                opacity: .4,
                weight: 8
            }, {
                color: "black",
                opacity: .5,
                weight: 2,
                dashArray: "2,4"
            }, {
                color: "white",
                opacity: .3,
                weight: 6
            }]
        }
    }).addTo(leafletMap);

    routingControl.on('routesfound', function() {
        $('.loadingRouteIcon').addClass('hidden');
    });
    routingControl.on('routingstart', function() {
        //Only start loading when there is at least one service selected
        if (routingControl.getRouter().options.routeCar ||
            routingControl.getRouter().options.delijnRouteParam.delijnbyBus || routingControl.getRouter().options.delijnRouteParam.delijnbyTram ||
            routingControl.getRouter().options.delijnRouteParam.delijnbyMetro ||
            routingControl.getRouter().options.delijnRouteParam.delijnbyTrain ||
            routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus) {
            $('.loadingRouteIcon').removeClass('hidden');
        }
    });
    routingControl.on('routingerror', function(errorEvent) {
        console.error(errorEvent);
        if (errorEvent.done) {
            $('.loadingRouteIcon').addClass('hidden');
        }
    });

    $('.dlButtonAuto').toggleClass('dlButtonActive', routingControl.getRouter().options.routeCar ? true : false);
    $('.dlButtonBus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBus ? true : false);
    $('.dlButtonTrein').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTrain ? true : false);
    $('.dlButtonTram').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTram ? true : false);
    $('.dlButtonMetro').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyMetro ? true : false);
    $('.dlButtonBelbus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus ? true : false);

    $('.arrivalDepartureButton').on('click', function() {
        $('.arrivalDepartureText').fadeOut(200, function() {
            if ($(this).text() === 'Aankomst:') {
                $(this).text('Vertrek:').fadeIn(200);
                routingControl.getRouter().options.delijnRouteParam.delijnArrival = false;
            } else {
                $(this).text('Aankomst:').fadeIn(200);
                routingControl.getRouter().options.delijnRouteParam.delijnArrival = true;
            }
            routingControl.route();
        });
    });
    var currentDate = new Date();
    document.getElementById('dlDate').valueAsDate = currentDate;
    document.getElementById('dlTime').value = currentDate.getHours() + ':' + currentDate.getMinutes();
    $('#dlDate').change(function() {
        if ($(this).val()) {
            var dateDate;
            try {
                dateDate = new Date($(this).val());
            } catch (ex) {
                console.warn("Date is not valid.");
                return;
            }
            if (!routingControl.getRouter().options.delijnRouteParam.delijnDate) {
                routingControl.getRouter().options.delijnRouteParam.delijnDate = currentDate;
            }
            routingControl.getRouter().options.delijnRouteParam.delijnDate.setDate(dateDate.getDate());
            routingControl.getRouter().options.delijnRouteParam.delijnDate.setMonth(dateDate.getMonth());
            routingControl.getRouter().options.delijnRouteParam.delijnDate.setYear(dateDate.getFullYear());
            routingControl.route();
        }
    });
    $('#dlTime').change(function() {
        if ($(this).val()) {
            var date;
            try {
                date = currentDate;
                var time = $(this).val().split(':');
                date.setHours(time[0]);
                date.setMinutes(time[1]);
            } catch (ex) {
                console.warn("Date is not valid. (time).");
                return;
            }
            if (!routingControl.getRouter().options.delijnRouteParam.delijnDate) {
                routingControl.getRouter().options.delijnRouteParam.delijnDate = currentDate;
            }
            routingControl.getRouter().options.delijnRouteParam.delijnDate.setHours(date.getHours());
            routingControl.getRouter().options.delijnRouteParam.delijnDate.setMinutes(date.getMinutes());
            routingControl.route();
        }
    });
    L.Routing.errorControl(routingControl).addTo(leafletMap);
    $('button.leaflet-routing-add-waypoint').addClass('hidden');
    $('.leaflet-routing-collapsible').addClass('leaflet-routing-container-hide');
    $('.addWaypointButton').before('<button></button>');
}

function activateMapFeatureCheckbox(id, text) {
    if (!$('#' + id).length) {
        $('#mapFeatureCheckboxList').append('<li><label class="mapCheckBox" for="' + id + '"><input onChange="toggleMarkers(this)" type="checkbox" class="mapCheckBox" id="' + id + '" checked > ' + text + ' </label></li>');
    }
    $('#' + id).prop('checked', ((mapMarkers === null || jQuery.isEmptyObject(mapMarkers)) ? true : ((mapMarkers[id] === true || mapMarkers[id] === undefined) ? true : false)));
}

function deactivateMapFeatureCheckbox(id) {
    var el = $('#mapFeatureCheckboxList').find('#' + id);
    el.parent().parent().remove();
}

var parkAndRideMarkers = [];

function updateParkAndRidesLeaflet(data) {
    activateMapFeatureCheckbox(PARKANDRIDESCHECKBOXID, 'Park-and-Rides');
    clearMap(parkAndRideMarkers);
    parkAndRideMarkers = [];
    var parkings = data.parkings;
    for (i = 0; i < parkings.length; i++) {
        var marker = L.marker([parkings[i].y, parkings[i].x], {
            icon: parkAndRideIcon
        }).bindPopup(getPRPopupHTML(parkings[i].name, parkings[i].y, parkings[i].x)); //.addTo(leafletMap);
        parkAndRideMarkers.push(marker);
    }
    if ($('#parkAndRidesCheckbox').is(':checked')) {
        addToMap(parkAndRideMarkers);
    }

}

function getPRPopupHTML(naam, x, y) {

    var html = '<label>' + naam + '</label>' +
        '<button class="btn btn-info PRDetailButton" value="' + x + ',' + y + '">Bereken de weg</button>';
    return html;
}

function initPRButtonClickHandler() {
    $('#leafletMap').on('click', '.PRDetailButton', function() {
        leafletMap.closePopup();
        requestPRDetail(this.value);
    });
}

function updateParkAndRidesLeaflet(data) {
    activateMapFeatureCheckbox(PARKANDRIDESCHECKBOXID, 'Park-and-Rides');
    clearMap(parkAndRideMarkers);
    parkAndRideMarkers = [];
    var parkings = data.parkings;
    for (i = 0; i < parkings.length; i++) {
        var marker = L.marker([parkings[i].y, parkings[i].x], {
            icon: parkAndRideIcon
        }).bindPopup(getPRPopupHTML(parkings[i].name, parkings[i].y, parkings[i].x)); //.addTo(leafletMap);
        parkAndRideMarkers.push(marker);
    }
    if ($('#parkAndRidesCheckbox').is(':checked')) {
        addToMap(parkAndRideMarkers);
    }

}

function getPRPopupHTML(naam, x, y) {

    var html = '<label>' + naam + '</label>' +
        '<button class="btn btn-info PRDetailButton" value="' + x + ',' + y + '">Bereken de route naar het centrum</button>';
    return html;
}

function initPRButtonClickHandler() {
    $('#leafletMap').on('click', '.PRDetailButton', function() {
        leafletMap.closePopup();
        requestPRDetail(this.value);
    });
}

function requestPRDetail(value) {
    var korx = 51.054802;
    var kory = 3.721965;
    var dest = value.split(',');
    routingControl.getRouter().options.routeCar = false;
    $('.dlButtonAuto').toggleClass('dlButtonActive', routingControl.getRouter().options.routeCar);

    routingControl.getRouter().options.delijnRouteParam.delijnbyBus = true;
    $('.dlButtonBus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBus);

    routingControl.getRouter().options.delijnRouteParam.delijnbyTrain = true;
    $('.dlButtonTrein').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTrain);


    routingControl.getRouter().options.delijnRouteParam.delijnbyTram = true;
    $('.dlButtonTram').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyTram);



    routingControl.getRouter().options.delijnRouteParam.delijnbyMetro = true;
    $('.dlButtonMetro').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyMetro);


    routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus = true;
    $('.dlButtonBelbus').toggleClass('dlButtonActive', routingControl.getRouter().options.delijnRouteParam.delijnbyBelbus);

    $('.leaflet-routing-collapsible').removeClass('leaflet-routing-container-hide');
    routingControl.setWaypoints([
        L.latLng(dest[0], dest[1]),
        L.latLng(korx, kory)
    ]);
    routingContol.route();

}


var circles = [];

var countpointCircles = [];

function countUpdate(data) {

    var countpoints = data.countpoints;
    /*for (i = 0; i < countpoints.length; i++) {
     var location = {lat: countpoints[i].y, lng: countpoints[i].x};
     var marker = new google.maps.Marker({
     position: location,
     map: map,
     title: (countpoints[i].count).toString(),
     icon: 'img/parking.png',
     
     });
     }*/

    // leaflet
    activateMapFeatureCheckbox('countpointCheckbox', 'Verkeerstellingen');
    clearMap(countpointCircles);
    countpointCircles = [];
    for (i = 0; i < countpoints.length; i++) {
        //var location = {lat: countpoints[i].y, lng: countpoints[i].x};
        var marker = L.circle([countpoints[i].y, countpoints[i].x], {
            radius: 50,
            fillColor: '#f03',
            fillOpacity: 0.5
        }); //.addTo(leafletMap);
        marker.bindPopup((countpoints[i].count.toString()) + " voertuigen");
        countpointCircles.push(marker);
    }
    if ($('#countpointCheckbox').is(':checked')) {
        addToMap(countpointCircles);
    }
}

var parkingMarkers;

function parkingUpdate(data) {
    activateMapFeatureCheckbox('parkingsCheckbox', 'Parkinglocaties');
    clearMap(parkingMarkers);
    parkingMarkers = [];
    $("#parkingOverview").html(100 - data.totalOccupied + "% bezet");
    $("#parkingOverview").css("background", getColorForPercentage(data.totalOccupied, "percent"));
    var toAdd = "";
    var parkings = data.parkings;
    for (i = 0; i < parkings.length; i++) {
        var p = parkings[i];
        var percent = 100 - p.freePlaces / p.capacity * 100;
        var occupied = p.capacity - p.freePlaces;
        var type;
        if (percent > 90)
            type = "progress-bar-danger";
        else if (percent > 75)
            type = "progress-bar-warning";
        else
            type = "progress-bar-success";
        var status = p.open ? occupied + "/" + p.capacity : "gesloten";
        toAdd += "<div class=\"progress\">" +
            "<div class=\"progress-bar " + type + "\" role=\"progressbar\" aria-valuenow=\"" + p.freePlaces + "\" aria-valuemin=\"0\" aria-valuemax=\"" + p.capacity + "\" style=\"width:" + percent + "%;\" >" +
            "<span>" + p.name + "  [" + status + "] </span>" +
            "</div>" +
            "</div>";
        var freePlaces = p.open ? p.freePlaces + " vrije plaatsen" : "gesloten";
        var marker = L.marker([p.lat, p.lng], {
            icon: parkingIcon
        }).bindPopup("<b>" + p.name + "</b> [" + freePlaces + "]"); //.addTo(leafletMap);
        parkingMarkers.push(marker);
    }
    if ($('#parkingsCheckbox').is(':checked')) {
        addToMap(parkingMarkers);
    }
    document.getElementById('parkings').innerHTML = toAdd;
}

function weatherUpdate(data) {
    var toAdd = "";
    var temp = "&nbsp;&nbsp;<i class=\"wi wi-thermometer\"></i>&ensp;";
    if (!data.hasOwnProperty("forecast")) {
        document.getElementById('weather').innerHTML = "De weatherwidget is tijdelijk buiten gebruik";
    } else {
        var icon = "wi wi-day" + data.forecast[0].weatherDescripton;
        $("#weatherOverview").html(data.forecast[0].temperature + "°C");
        $("#weatherOverview").css("background", getColorForPercentage((data.forecast[0].temperature + 10) * 2.5, "temperature"));
        $("#rainOverview").html(data.forecast[0].pop + "% kans op neerslag");
        $("#rainOverview").css("background", getColorForPercentage(data.forecast[0].pop, "rainPercent"));
        temp += data.forecast[0].temperature + "°C&ensp;&ensp;&ensp;<i class=\"wi wi-wu-" + data.forecast[0].weatherDescripton + "\"></i>";
        var humidity = "<br>&nbsp;<i class=\"wi wi-humidity\"></i>&ensp;" + data.forecast[0].humidity + " %";
        var windSpeed = "<br><i class=\"wi wi-windy\"></i>&ensp;" + data.forecast[0].windSpeed + " km/u";
        toAdd += temp + humidity + windSpeed;
        document.getElementById('weather0').innerHTML = "Vandaag: " + data.forecast[0].day + "<br></br>" + toAdd;
        toAdd += "<br><button onclick=\"document.getElementById('forecast').style.display='block'\" class='btn' id='voorspelling'>Komende dagen</button>";
        document.getElementById('weather').innerHTML = toAdd;
        for (i = 1; i < data.forecast.length; i++) {
            var content = "";
            var temp = data.forecast[i].day + "<br></br>" + "&nbsp;&nbsp;<i class=\"wi wi-thermometer\"></i>&ensp;";
            temp += data.forecast[i].temperature + "°C&ensp;&ensp;&ensp;<i class=\"wi wi-wu-" + data.forecast[i].weatherDescripton + "\"></i>";
            var humidity = "<br>&nbsp;<i class=\"wi wi-humidity\"></i>&ensp;" + data.forecast[i].humidity + " %";
            var windSpeed = "<br><i class=\"wi wi-windy\"></i>&ensp;" + data.forecast[i].windSpeed + " km/u";
            content += temp + humidity + windSpeed;
            document.getElementById("weather" + i).innerHTML = content;
        }
    }
}

function nmbsUpdate(data) {
    var delays = 0;
    var toAdd = "";
    var tableRef = document.getElementById("nmbsTable").getElementsByTagName("tbody")[0];
    tableRef.innerHTML = "";
    //check if table is there, if not make it
    var hasInfo = data.hasOwnProperty("departures");
    if (hasInfo) {
        var departures = data.departures;

        for (var i = 0; i < departures.length; i++) {
            var d = departures[i];
            // Insert a row in the table at the last row
            if (d.delay >= 300 || d.canceled === true) {
                delays++;
                var newRow = tableRef.insertRow(0);
                // Insert cells in the row at corresponding indexes
                var departTimeCell = newRow.insertCell(0);
                departTimeCell.setAttribute("headers", "Tijd");
                var departingCell = newRow.insertCell(1);
                departingCell.setAttribute("headers", "Vertrekstation");
                var endCell = newRow.insertCell(2);
                endCell.setAttribute("headers", "Eindstation");
                var delayCell = newRow.insertCell(3);
                delayCell.setAttribute("headers", "Vertraging");
                departTimeCell.appendChild(document.createTextNode(d.departureTime));
                departingCell.appendChild(document.createTextNode(d.departureStation));
                endCell.appendChild(document.createTextNode(d.endStation));
                var min = Math.round(d.delay / 60);
                if (d.canceled === true) {
                    newRow.className = "danger";
                    delayCell.appendChild(document.createTextNode('Afgeschaft!'));
                } else {
                    newRow.className = "warning";
                    delayCell.appendChild(document.createTextNode('+' + min + ' \''));
                }
            }

        }
        if (delays === 0) {
            tableRef.innerhtml = "";
            toAdd = "<tr><td class=\"titleLessTd\" colspan=\"4\">Er zijn geen vertragingen of afschaffingen van treinen op dit moment.</td></tr>";
            tableRef.innerHTML = toAdd;
        }
        var percentOnTime = parseInt(100 * (departures.length - delays) / departures.length);
        $("#nmbsOverview").html(percentOnTime + "% op tijd");
        $("#nmbsOverview").css("background", getColorForPercentage(percentOnTime, "percent"));
    } else {
        tableRef.innerhtml = "";
        toAdd = "<tr><td colspan=\"4\">Er kan momenteel geen data opgehaald worden over de treindoorkomsten, probeer het later nog eens.</td></tr>";
        tableRef.innerHTML = toAdd;
    }
}


var percentColors = [ //red to green
    {
        percent: 0,
        color: {
            r: 255,
            g: 0,
            b: 50
        }
    },
    {
        percent: 0.5,
        color: {
            r: 255,
            g: 255,
            b: 50
        }
    },
    {
        percent: 1.0,
        color: {
            r: 0,
            g: 255,
            b: 50
        }
    }
];

var temperatureColors = [ //blue to red, white as neutral
    {
        percent: 0,
        color: {
            r: 0,
            g: 0,
            b: 255
        }
    },
    {
        percent: 0.5,
        color: {
            r: 255,
            g: 255,
            b: 255
        }
    },
    {
        percent: 1.0,
        color: {
            r: 255,
            g: 0,
            b: 0
        }
    }
];

var rainyColors = [ //white to blue
    {
        percent: 0,
        color: {
            r: 255,
            g: 255,
            b: 255
        }
    },
    {
        percent: 1.0,
        color: {
            r: 50,
            g: 50,
            b: 255
        }
    }
];

var getColorForPercentage = function(percent, type) {
    switch (type) {
        case "percent":
            var gradientColors = percentColors;
            break;
        case "temperature":
            var gradientColors = temperatureColors;
            break;
        case "rainPercent":
            var gradientColors = rainyColors;
            break;
    }
    percent /= 100;
    for (var i = 1; i < gradientColors.length - 1; i++) {
        if (percent <= gradientColors[i].percent) {
            break;
        }
    }
    var lower = gradientColors[i - 1];
    var upper = gradientColors[i];
    var range = upper.percent - lower.percent;
    var rangePct = (percent - lower.percent) / range;
    var pctLower = 1 - rangePct;
    var pctUpper = rangePct;
    var color = {
        r: Math.floor(lower.color.r * pctLower + upper.color.r * pctUpper),
        g: Math.floor(lower.color.g * pctLower + upper.color.g * pctUpper),
        b: Math.floor(lower.color.b * pctLower + upper.color.b * pctUpper)
    };
    return 'rgb(' + [color.r, color.g, color.b].join(',') + ')';
};

function pollutionUpdate(data) {
    var toAdd = '<div class="panel-group" id="accordion">';
    var stations = data.stations;
    var color;
    var info;
    var ozon;
    $("#pollutionOverview").html("AQI: " + data.avgAqi);
    $("#pollutionOverview").css("background", getColorForPercentage(100 - data.avgAqi, "percent"));
    for (i = 0; i < stations.length; i++) {
        var s = stations[i];

        if (s.aqi > 0 && s.aqi <= 50) {
            color = "class=\"panel panel-success\"";
            info = "Air quality index: goed (" + s.aqi + ")";
        } else if (s.aqi > 50 && s.aqi <= 100) {
            color = "class=\"panel panel-warning\"";
            info = "Air quality index: matig (" + s.aqi + ")";
        } else if (s.aqi > 100 && s.aqi <= 150) {
            color = "class=\"panel panel-danger\"";
            info = "Air quality index: ongezond voor gevoelige groepen (" + s.aqi + ")";
        } else if (s.aqi > 150 && s.aqi <= 200) {
            color = "class=\"panel panel-danger\"";
            info = "Air quality index: ongezond (" + s.aqi + ")";
        }
        if (!s.O3 == 0) {
            ozon = s.O3 + ' µg/m³';
        } else {
            ozon = 'n.v.t.';
        }
        toAdd += ' <div ' + color + 'id="accordion">' +
            ' <div class="panel-heading collapsed">' +
            ' <h4 class="panel-title">' +
            '  <a class="accordion-toggle collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapse' + i + '">' +
            s.name + '<label>' + info + ' </label>' +
            '    </a>' +
            '   </h4>' +
            '  </div>' +
            ' <div id="collapse' + i + '" class="panel-collapse collapse">' +
            '  <div class="panel-body">' +
            '<div><ul>' +
            '<li>O3: ' + ozon + '</li>' +
            '<li>NO2: ' + s.No2 + ' µg/m³</li>' +
            '<li>PM1.0: ' + s.Pm10 + ' µg/m³</li>' +
            '<li>PM2.5: ' + s.Pm25 + ' µg/m³</li>' +
            '<li>Pressure: ' + s.P + ' Pa</li>' +
            '<li>Humidity: ' + s.H + '%</li>' +
            '</ul>' +
            '</div>' +
            '    </div>' +
            '  </div>' +
            ' </div>';
    }
    toAdd += '</div>';
    document.getElementById('pollution').innerHTML = toAdd;
}

function searchPlacesDelijn(query) {
    var choice = [];
    if (query.indexOf(',') >= 0) {
        var index = query.indexOf(",");
        var hulp = query.substring(0, index);
        var tweede = query.substring(index + 1, query.length);

        query = hulp + tweede;

    }
    if (query.indexOf('(') >= 0) {
        var index = query.indexOf("(");
        var hulp = query.substring(0, index);
        query = hulp;

    }
    if (query.indexOf('-') >= 0) {
        var index = query.indexOf("-");
        var hulp = query.substring(0, index);
        var tweede = query.substring(index + 1, query.length);

        query = hulp + tweede;

    }

    var request = new XMLHttpRequest();

    request.open('GET', 'https://www.delijn.be/rise-api-search/search/haltes/' + query + '/1');
    request.onreadystatechange = function() {
        if (this.readyState === 4) {
            var json = JSON.parse(this.responseText);
            var haltes = json["haltes"];
            var i = 0;
            var grootte = haltes.length;
            while (i < grootte) {

                choice[i] = haltes[i].halteNummer;
                i++;
            }

            var j = 0;
            var parts = "";
            while (j < (choice.length - 1)) {
                parts += choice[j] + "+";
                j++;
            }
            parts += choice[j];
            var url = "https://www.delijn.be/realtime/" + parts + "/10";

            $("#delijnframe").attr('src', url);

            $("#delijninput").attr('placeholder', query);
        }
    };

    request.send();

}

function delijnSearch() {
    var veld = document.getElementById('delijninput').value;

    searchPlacesDelijn(veld);

}

function initDelijnAutocomplete() {
    $("#delijninput").autocomplete({
        source: function(request, response) {
            $.ajax({
                method: "GET",
                dataType: "json",
                url: 'https://www.delijn.be/rise-api-search/search/haltes/' + request.term + '/1',
                success: function(data) {
                    var transformed = $.map(data.haltes, function(el) {
                        return {
                            label: el.omschrijvingLang,
                            id: el.halteNummer
                        };
                    });
                    response(transformed);
                },
                error: function() {
                    response([]);
                }
            });
        }
    });
}

function searchHaltes(query) {
    var choice = [];
    var request = new XMLHttpRequest();

    if (query.contains(",")) {
        var index = query.indexOf(",");
        var hulp = query.substring(0, index);
    }

    request.open('GET', 'https://www.delijn.be/rise-api-search/search/haltes/' + query + '/1');
    request.onreadystatechange = function() {
        if (this.readyState === 4) {
            var json = JSON.parse(this.responseText);
            var haltes = json["haltes"];
            var i = 0;
            var grootte = haltes.length;
            while (i < grootte) {

                choice[i] = haltes[i].halteNummer;
                i++;
            }
            return choice;
        }
    };
}

function bluebikeUpdate(data) {
    var toAdd = "";
    $("#bluebikeOverview").html(data.percentAvailable + "% vrij");
    $("#bluebikeOverview").css("background", getColorForPercentage(data.percentAvailable, "percent"));
    for (var i = 0; i < data.bikeCenters.length; i++) {
        var percent = 100 - data.bikeCenters[i].available / data.bikeCenters[i].total * 100;
        var occupied = data.bikeCenters[i].total - data.bikeCenters[i].available;
        var type = "progress-bar-success";
        if (percent > 90)
            type = "progress-bar-danger";
        else if (percent > 75)
            type = "progress-bar-warning";
        toAdd += "<div class=\"progress\"><div class=\"progress-bar " + type + "\" role=\"progressbar\" aria-valuenow=\"" + data.bikeCenters[i].available + "\" aria-valuemin=\"0\" aria-valuemax=\"" + data.bikeCenters[i].total + "\" style=\"width:" + percent + "%;\" >" +
            "<span>" + data.bikeCenters[i].location + " [" + occupied + "/" + data.bikeCenters[i].total + "] </span>" +
            "</div></div>";
    }
    //toAdd += "<div ALIGN=\"center\"><a href=\"http:\/\/www.blue-bike.be\/\"  class=\'btn\'>meer info <\/a><\/div>";
    document.getElementById('bikeCenters').innerHTML = toAdd;
}

var parkingFeeAreaPolygons = [];

function parkingFeeAreaUpdate(data) {
    if (data.zones !== undefined) {
        activateMapFeatureCheckbox(PARKINGFEEAREACHECKBOXID, 'Parkeertariefzones');
        clearMap(parkingFeeAreaPolygons);
        parkingFeeAreaPolygons = [];
        for (var i = 0; i < data.zones.length; i++) {
            var polygon;
            if (data.zones[i].zoneID.indexOf('GROEN') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'green',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else if (data.zones[i].zoneID.indexOf('ORANJE') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'orange',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else if (data.zones[i].zoneID.indexOf('ROOD') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'red',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else if (data.zones[i].zoneID.indexOf('GEEL') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'yellow',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else if (data.zones[i].zoneID.indexOf('BLAUW') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'blue',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else if (data.zones[i].zoneID.indexOf('LOOP') !== -1) {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'olive',
                    stroke: false,
                    weigth: 0.5,
                    fillOpacity: 0.4
                });
            } else {
                polygon = L.polygon(data.zones[i].polygon, {
                    color: 'grey',
                    stroke: false,
                    weight: 0.5,
                    fillOpacity: 0.4
                });
            }
            polygon.bindPopup(getParkingFeeAreaPopupHtml(data.zones[i]));
            parkingFeeAreaPolygons.push(polygon);
        }
    }
    if ($('#' + PARKINGFEEAREACHECKBOXID).is(':checked')) {
        addToMap(parkingFeeAreaPolygons);
    }
}

function getParkingFeeAreaPopupHtml(zone) {
    var html = '<h3>' + zone.zoneName + '</h3>';
    if (zone.zoneURL !== undefined)
        html += '<a target="_blank" href="' + zone.zoneURL + '" class="btn btn-info" >Meer info<a>';
    return html;
}

var coyoteSectionColors = ['green', 'orange', 'red'];
var coyotePolylines = [];
var coyoteMarkers = [];

function coyoteUpdate(data) {
    if (data.sections !== undefined) {
        $("#R40Clock").html("R40↻: " + data.R40Clockwise + "min");
        $("#R40Clock").css("background", getColorForPercentage(100 - ((data.R40Clockwise - 15) * 4.2), "percent"));
        $("#R40CounterClock").html("R40↺: " + data.R40CounterClockwise + "min");
        $("#R40CounterClock").css("background", getColorForPercentage(100 - ((data.R40CounterClockwise - 15) * 4.2), "percent"));
        activateMapFeatureCheckbox(COYOTESECTIONSCHECKBOXID, 'Actuele verkeersinfo op belangrijke wegen');
        clearMap(coyotePolylines);
        coyotePolylines = [];
        for (var i = 0; i < data.sections.length; i++) {
            var latlngs = [];
            for (var j = 0; j < data.sections[i].geometries.length; j++) {
                var latlng = [data.sections[i].geometries[j].lat, data.sections[i].geometries[j].lng];
                latlngs.push(latlng);
            }
            var ratio = data.sections[i].ratio;
            var polyline = L.polyline(latlngs, {
                color: coyoteSectionColors[ratio]
            }); //.addTo(leafletMap);
            coyotePolylines.push(polyline);
        }
        if ($('#' + COYOTESECTIONSCHECKBOXID).is(':checked')) {
            addToMap(coyotePolylines);
        }
    }
    if (data.alerts !== undefined) {
        activateMapFeatureCheckbox('coyoteMarkersCheckbox', 'Actuele verkeersincidenten');
        clearMap(coyoteMarkers);
        coyoteMarkers = [];
        for (var alert in data.alerts) {
            var jsonObjectAlert = data.alerts[alert];
            var y = jsonObjectAlert.lat;
            var x = jsonObjectAlert.lng;
            var mark = L.marker([y, x], {
                icon: coyoteIcon
            }).bindPopup(jsonObjectAlert.type_lbl);

            coyoteMarkers.push(mark);
        }
        if ($('#' + COYOTEALERTSCHECKBOXID).is(':checked')) {
            addToMap(coyoteMarkers);
        }
    }
}

var GIPODGroupMarker = L.markerClusterGroup({
    iconCreateFunction: function(cluster) {
        return L.divIcon({
            html: '<div class="gipodClusterIcon">' + cluster.getChildCount() + '</div>',
            iconSize: L.point(20, 20)
        });
    }
});
var GIPODImportantGroupMarker = L.markerClusterGroup({
    iconCreateFunction: function(cluster) {
        return L.divIcon({
            html: '<div class="gipodClusterIcon importantcluster">' + cluster.getChildCount() + '</div>',
            iconSize: L.point(20, 20)
        });
    }
});

function GIPODUpdate(data) {
    if (data.GIPOD !== undefined) {
        //Step 1: Make sure all static html is in place
        //add GIPOD checkboxes to mapFeatureCheckboxList
        activateMapFeatureCheckbox('GIPODImportantCheckbox', 'Wegenwerken die grote hinder veroorzaken');
        activateMapFeatureCheckbox('GIPODCheckbox', 'Overige wegenwerken');
        //Step 2: Fill data in on the page
        GIPODGroupMarker.clearLayers();
        GIPODImportantGroupMarker.clearLayers();
        GIPODGroupMarker = L.markerClusterGroup({
            iconCreateFunction: function(cluster) {
                return L.divIcon({
                    html: '<div class="gipodClusterIcon">' + cluster.getChildCount() + '</div>',
                    className: 'mycluster',
                    iconSize: L.point(20, 20)
                });
            }
        });
        GIPODImportantGroupMarker = L.markerClusterGroup({
            iconCreateFunction: function(cluster) {
                return L.divIcon({
                    html: '<div class="gipodClusterIcon importantcluster">' + cluster.getChildCount() + '</div>',
                    className: 'mycluster',
                    iconSize: L.point(20, 20)
                });
            }
        });
        var GIPODMarkers = [];
        var GIPODImportantMarkers = [];
        for (var i = 0; i < data.GIPOD.length; i++) {
            var latlngs = [];
            latlngs[0] = data.GIPOD[i].coordinate.coordinates[1];
            latlngs[1] = data.GIPOD[i].coordinate.coordinates[0];
            var mark = L.marker(latlngs, {
                icon: (data.GIPOD[i].importantHindrance) ? GIPODIconImportant : GIPODIcon
            }).bindPopup(getGIPODPopupHTML(data.GIPOD[i]), {
                maxWidth: leafletMap.getSize().x - 40
            });
            mark.imporatantHindrance = data.GIPOD[i].importantHindrance;
            if (mark.imporatantHindrance) {
                GIPODImportantMarkers.push(mark);
            } else {
                GIPODMarkers.push(mark);
            }
        }
        GIPODGroupMarker.addLayers(GIPODMarkers);
        GIPODImportantGroupMarker.addLayers(GIPODImportantMarkers);
        //if (document.getElementById('GIPODCheckbox').checked) {
        if ($('#GIPODCheckbox').is(':checked')) {
            leafletMap.addLayer(GIPODGroupMarker);
        }
        //if (document.getElementById('GIPODCheckbox').checked) {
        if ($('#GIPODImportantCheckbox').is(':checked')) {
            leafletMap.addLayer(GIPODImportantGroupMarker);
        }
    }
}

function getGIPODPopupHTML(GIPODEntry) {
    var startDate = new Date(GIPODEntry.startDateTime);
    var endDate = new Date(GIPODEntry.endDateTime);
    var html = '<table class="GIPODPopupTable">' +
        '<tr><td class="tableTitle">Beschrijving</td><td>' + GIPODEntry.description + '</td></tr>' +
        '<tr><td class="tableTitle">Eigenaar</td><td>' + GIPODEntry.owner + '</td></tr>' +
        '<tr><td class="tableTitle">Start</td><td>' + startDate.toLocaleDateString() + '</td></tr>' +
        '<tr><td class="tableTitle">Eind</td><td>' + endDate.toLocaleDateString() + '</td></tr>' +
        '</table>' +
        '<button class="btn btn-info gipodDetailButton" value="' + GIPODEntry.gipodId + '">Toon detail op kaart</button>';
    return html;
}

function toggleMarkers(element) {
    mapMarkers[element.id] = element.checked;
    //save Markers to local storage
    localStorage.setItem(KEY_MAP_MARKERS, JSON.stringify(mapMarkers));
    if (element.id === 'GIPODImportantCheckbox') {
        if (element.checked) {
            leafletMap.addLayer(GIPODImportantGroupMarker);
        } else {
            leafletMap.removeLayer(GIPODImportantGroupMarker);
        }
    } else if (element.id === 'GIPODCheckbox') {
        if (element.checked) {
            leafletMap.addLayer(GIPODGroupMarker);
        } else {
            leafletMap.removeLayer(GIPODGroupMarker);
        }
    } else if (element.id === COYOTESECTIONSCHECKBOXID) {
        if (element.checked) {
            addToMap(coyotePolylines);
        } else {
            clearMap(coyotePolylines);
        }
    } else if (element.id === COYOTEALERTSCHECKBOXID) {
        if (element.checked) {
            addToMap(coyoteMarkers);
        } else {
            clearMap(coyoteMarkers);
        }
    } else if (element.id === 'parkingsCheckbox') {
        if (element.checked) {
            addToMap(parkingMarkers);
        } else {
            clearMap(parkingMarkers);
        }
    } else if (element.id === 'parkAndRidesCheckbox') {
        if (element.checked) {
            addToMap(parkAndRideMarkers);
        } else {
            clearMap(parkAndRideMarkers);
        }
    } else if (element.id === 'countpointCheckbox') {
        if (element.checked) {
            addToMap(countpointCircles);
        } else {
            clearMap(countpointCircles);
        }
    } else if (element.id === PARKINGFEEAREACHECKBOXID) {
        if (element.checked) {
            addToMap(parkingFeeAreaPolygons);
        } else {
            clearMap(parkingFeeAreaPolygons);
        }
    }
}

var GIPODPolygons = [];

function requestGIPODDetail(id) {
    //request detail via http://api.gipod.vlaanderen.be/ws/v1/workassignment/[ID]
    var url = 'https://api.gipod.vlaanderen.be/ws/v1/workassignment/' + id;
    $.getJSON(url, function(data) {
        console.log("GIPOD Detail message: ", data);
        if (data.location.geometry.type === "Polygon") {
            var coord = [];
            for (var i = 0; i < data.location.geometry.coordinates.length; i++) {
                var coor = [];
                for (var j = 0; j < data.location.geometry.coordinates[i].length; j++) {
                    var co = [data.location.geometry.coordinates[i][j][1], data.location.geometry.coordinates[i][j][0]];
                    coor.push(co);
                }
                coord.push(coor);
            }
            clearMap(GIPODPolygons);
            GIPODPolygons = [];
            var polygon = L.polygon(coord, {
                color: 'blue'
            }).addTo(leafletMap);
            GIPODPolygons.push(polygon);
            leafletMap.fitBounds(polygon.getBounds());
        } else if (data.location.geometry.type === "MultiPolygon") {
            var linksbovenX; //smallest long
            var linksbovenY; //largest lat
            var rechtsonderX; //largest long
            var rechtsonderY; //smallest lat
            var multipolygons = [];
            clearMap(GIPODPolygons);
            GIPODPolygons = [];
            for (var i = 0; i < data.location.geometry.coordinates.length; i++) {
                var multipoly = [];
                for (var j = 0; j < data.location.geometry.coordinates[i].length; j++) {
                    var poly = [];
                    for (var k = 0; k < data.location.geometry.coordinates[i][j].length; k++) {
                        var punt = [data.location.geometry.coordinates[i][j][k][1], data.location.geometry.coordinates[i][j][k][0]];
                        poly.push(punt);
                        //calculate bounds
                        if (linksbovenY === undefined || linksbovenY < punt[0]) {
                            linksbovenY = punt[0];
                        }
                        if (rechtsonderY === undefined || rechtsonderY > punt[0]) {
                            rechtsonderY = punt[0];
                        }
                        if (linksbovenX === undefined || linksbovenX > punt[1]) {
                            linksbovenX = punt[1];
                        }
                        if (rechtsonderX === undefined || rechtsonderX < punt[1]) {
                            rechtsonderX = punt[1];
                        }
                    }
                    multipoly.push(poly);
                }
                multipolygons.push(multipoly);
                var polygon = L.polygon(multipoly, {
                    color: 'blue'
                }).addTo(leafletMap);
                GIPODPolygons.push(polygon);
            }
            leafletMap.fitBounds([
                [linksbovenY, linksbovenX],
                [rechtsonderY, rechtsonderX]
            ]);
        }
    });
}

function VGSUpdate(data) {
    if (data.texts !== undefined) {
        var html = "";
        if (data.texts.length === 0) {
            html += "<div>Momenteel worden er geen boodschappen weergegeven op de informatieborden.</div>";
        } else {
            for (var i = 0; i < data.texts.length; i++) {
                html += '<div class="VGSSign">';
                html += data.texts[i];
                html += '</div>';
            }
        }
        document.getElementById('vgs').innerHTML = html;
    }
}

function clearMap(layers) {
    if (layers !== undefined) {
        for (var i = 0; i < layers.length; i++) {
            leafletMap.removeLayer(layers[i]);
        }
    }
}

function addToMap(layers) {
    if (layers !== undefined) {
        for (var i = 0; i < layers.length; i++) {
            //layers[i].addTo(leafletMap);
            leafletMap.addLayer(layers[i]);
        }
    }
}

function initMapPopup() {
    $("#expandMapButton").click(function(e) {
        e.preventDefault();
        document.getElementById('leafletModalDialog').style.display = 'block';
        $("#leafletcontainerPopup").append($("#leafletMap"));
        setTimeout(function() {
            leafletMap.invalidateSize();
            fixRouteInstructionsScroll();
        }, 610);

    });
    $('#dialogclose').click(function() {
        $('#leafletModalDialog').hide();
        $("#leaflet").append($("#leafletMap"));
        leafletMap.invalidateSize();
        fixRouteInstructionsScroll();
    });
    $('#leafletModalDialog').click(function(event) {
        if (event.target.id === 'leafletModalDialog') {
            $(this).hide();
            $("#leaflet").append($("#leafletMap"));
            leafletMap.invalidateSize();
            fixRouteInstructionsScroll();
        }
    });

    $(window).resize(function() {
        leafletMap.invalidateSize();
        fixRouteInstructionsScroll();
    }).resize();
}

function fixRouteInstructionsScroll() {
    $('.leaflet-top.leaflet-right').each(function() {
        $(this).find('.leaflet-routing-alternatives-container').css('max-height', ($(this).height() - $(this).find('.leaflet-routing-geocoders').outerHeight() - 50) + "px");
    });
}


var allTypes = ['mainMap', 'parkingBezetting', 'weather', 'train', 'bluebike', 'buienradar', 'chart', 'pollution', 'vgs', 'historic', 'delijn'];


function getStaticHTMLDataForWidgetId(type) {
    var data = new Object;
    if (type === 'mainMap') {
        data.id = 'leafletContainer';
        data.title = '<button href="#" id="expandMapButton" class="btn btn-default btn-xs"><span class="btn glyphicon glyphicon-fullscreen" aria-hidden="true"></span></button>' +
            ' Gent in kaart <span id="R40Clock" class="badge">Off</span> <span id="R40CounterClock" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph"  title="Gent in kaart" data-toggle="popover" data-placement="left" data-content="Op deze kaart zijn volgende locaties aangeduid: wegenwerken, parkeergarages, park-and-rides, verkeerstellers (rode bollen) en incidenten (gevarendriehoeken). Bij het klikken op deze locaties krijgt u meer informatie. Oranje- of roodgekleurde trajecten wijzen op vertragingen met respectievelijk kleine of grote hinder. De rode, oranje, groene of gele gebieden duiden op de verschillende parkeerzone\'s. Als u bovenaan de pagina klikt op \'Pas widgets aan\' kan u selecteren welke informatie u op de kaart wenst te zien."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'leaflet';
        data.content = '<div id="mainMapCheckBoxDiv">' +
            '<ul id="mapFeatureCheckboxList">' +
            '</ul>' +
            '</div>' +
            '<div id="leafletMap">De map is buiten gebruik.</div>';
    } else if (type === 'parkingBezetting') {
        data.id = 'parkingsContainer';
        data.title = 'Bezetting parkings <span id="parkingOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Parkingwidget" data-toggle="popover" data-placement="left" data-content="Dit is een overzicht van de realtime bezetting van de Gentse parkeergarages. Tussen de vierkante haakjes staat het aantal bezette plaatsen en de totale capaciteit van de parking."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'parkings';
        data.content = 'De parkingwidget is tijdelijk buiten gebruik';
    } else if (type === 'weather') {
        data.id = 'weatherContainer';
        data.title = 'Weer Gent <span id="weatherOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Weerwidget" data-toggle="popover" data-placement="left" data-content="De weersvoorspelling voor vandaag. Achtereenvolgens staan de temperatuur (graden Celsius), de luchtvochtigheid (percentage) en de windsnelheid (in km/u). Een dergelijke voorspelling voor de volgende drie dagen kan bekomen worden door te klikken op \'Komende dagen\'."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'weather';
        data.content = 'De weatherwidget is tijdelijk buiten gebruik';
    } else if (type === 'train') {
        data.id = 'nmbsContainer';
        data.title = 'Treinvertragingen <span id="nmbsOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Treinwidget" data-toggle="popover" data-placement="left" data-content="Deze widget toont realtime vertragingen van treinen die vertrekken uit de Gentse NMBS-stations. In de kolom \'Vertraging\' is de vertraging weergegeven in minuten. Afgeschafte treinen worden weergegeven in het rood."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'NMBS';
        data.content = '<table class="table" id="nmbsTable"><thead><tr><th>Tijd<th>Vertrekstation</th><th>Eindstation</th><th>Vertraging</th></tr></thead><tbody id="nmbsTableBody"><tr><td class="titleLessTd" colspan=\"4\">Er kan momenteel geen data opgehaald worden over de treindoorkomsten, probeer het later nog eens.</td></tr></tbody></table>';
    } else if (type === 'bluebike') {
        data.id = 'bluebikeContainer';
        data.title = 'Bezetting Bluebikes <span id="bluebikeOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Bluebikewidget" data-toggle="popover" data-placement="left" data-html="true" data-content="Deze widget toont de hoeveelheid bluebikefietsen die momenteel bezet zijn en het totaal aantal bluebikefietsen per ontleningspunt. Voor meer informatie over dit concept, klik <a target=\'_blank\' href=\'https://www.blue-bike.be/\'>hier</a>." ><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'bikeCenters';
        data.content = 'De Bluebike-widget is buiten gebruik.';
    } else if (type === 'buienradar') {
        data.id = 'buienContainer';
        data.title = 'Buienradar <span id="rainOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Buienradarwidget" data-toggle="popover" data-placement="left" data-content="Wanneer er neerslag is voorspeld in een bepaald gebied, zal dit gebied op de kaart overdekt zijn met grijze wolken. Rechtsonder ziet u het tijdstip waarop deze neerslag voorspeld wordt."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'buienradar';
        data.content = '<iframe src="https://gadgets.buienradar.nl/gadget/zoommap/?lat=51.05&amp;lng=3.71667&amp;overname=2&amp;zoom=13&amp;naam=Gent&amp;size=2&amp;voor=1" scrolling="no" width="256" height="256" frameborder="no"></iframe>';
    } else if (type === 'chart') {
        data.id = 'chartContainer';
        data.title = 'Relatieve verkeersdrukte <span id="relatiefOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Relatieve verkeersdrukte widget" data-toggle="popover" data-placement="left" data-content="Deze widget geeft een overzicht voor de realtime reistijden op drukke Gentse trajecten in vergelijking met de normale reistijd bij vlot verkeer. Voor de meeste trajecten wordt er in beide rijrichtingen gemeten. De mogelijke richtingen zijn N(Noordwaarts), S(Zuidwaarts), E(Oostwaarts) en W(Westwaarts)."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'chart';
        data.content = 'De verkeersdrukte-widget is buiten gebruik.';
    } else if (type === 'pollution') {
        data.id = 'pollutionContainer';
        data.title = 'Luchtkwaliteit <span id="pollutionOverview" class="badge">Off</span><div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Luchtkwaliteitwidget" data-toggle="popover" data-placement="left" data-content="Deze widget biedt een overzicht van de luchtkwaliteit gemeten in verschillende meetstations in en rond Gent. De \'air quality index\' is een maat voor de luchtkwaliteit. Hoe lager dit getal, hoe zuiverder de lucht. Bij het klikken op een meetstation ziet u gedetailleerde meetresultaten."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'pollution';
        data.content = 'De luchtkwaliteitswidget is buiten gebruik.';
    } else if (type === 'vgs') {
        data.id = 'vgsContainer';
        data.title = 'Verkeersgeleidingssysteem <div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Verkeersgeleidingssysteemwidget" data-toggle="popover" data-placement="left" data-html="true" data-content="Deze widget toont de teksten die op de informatieborden van stad Gent staan. Deze borden staan verspreid in Gent langs de belangrijkste wegen. Ze geven realtime informatie weer over de verkeerssituatie in de stad. Voor meer informatie over dit concept, klik <a target=\'_blank\' href=\'https://mobiliteit.stad.gent/vgs\'>hier</a>." ><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'vgs';
        data.content = 'De Verkeersgeleidingssysteemwidget is buiten gebruik.';
    } else if (type === 'historic') {
        data.id = 'historicContainer';
        data.title = 'Historische verkeersdrukte <div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="Historische verkeersdrukte widget" data-toggle="popover" data-placement="left" data-content="De grafiek biedt voor de huidige weekdag een overzicht met zowel realtime als gemiddelde reistijden voor bepaalde routes. Standaard is \'Totaal\' geselecteerd (de som van alle gemeten tijden), maar als u hierop klikt kan u een specifiek traject selecteren. Voor de meeste trajecten wordt er in beide rijrichtingen gemeten. De mogelijke richtingen zijn N(Noordwaarts), S(Zuidwaarts), E(Oostwaarts) en W(Westwaarts)."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'historic';
        data.content = 'De historische verkeersdrukte-widget is buiten gebruik';
    } else if (type === 'delijn') {
        data.id = 'delijnContainer';
        data.title = 'De Lijn <div class="headerRight"><a class="mobileControl mobileUp btn"><span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span></a><a class="mobileControl mobileDown btn"><span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span></a><a class="mobileControl mobileClose btn"><span class="glyphicon glyphicon-remove" aria-hidden="true"></span></a><a class="infoglyph" title="De Lijn widget" data-toggle="popover" data-placement="left" data-content="Typ in het vakje de halte waarover u info wenst. Bij het klikken op \'Zoek\' zal dan de vertrekinfo over trams en bussen worden weergegeven zoals ze op de digitale borden staat aan die halte."><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span></a></div>';
        data.contentId = 'delijn';
        data.content = '<div class="delijninputveld"> ' +
            ' <div class="input-group"> ' +
            '<input id="delijninput" name="lijninput" type="text" class="form-control" placeholder=\"Kies uw halte...\" autocomplete=\"off\" > ' +
            ' <span class="input-group-btn"> ' +
            '<button type=\"submit\" class=\"btn btn-secondary\" id=\"delijnbutton\" onclick=\"delijnSearch()\" keydown="delijnSearch">Zoek<\/button>' +
            '</span>' +
            '</div>' +
            '</div>' +
            '<div class=\"delijniframe\"><iframe id=\"delijnframe\" width=\"100%\" height=\"100%\" frameborder="\yes\"><\/iframe><\/div>';
    }
    return data;
}

$(document).keyup(function(e) {
    if ($("#delijninput").is(":focus") && (e.keyCode == 13)) {
        delijnSearch();
    }
});

function chartUpdate(data) {
    var timing = data.timing;
    var trafficTimes = [];
    var myLabels = [];
    var realTimes = [];
    for (i = 0; i < timing.length; i++) {
        var info = timing[i];
        myLabels.push(info.name);
        trafficTimes.push(info.normal_time);
        realTimes.push(info.real_time);
    }
    var myData = {
        labels: myLabels,
        datasets: [{
                type: "line",
                label: "normale reistijd (s)",
                backgroundColor: "rgba(41,62,72,0.5)",
                data: trafficTimes
            },
            {
                type: "bar",
                label: "real-time reistijd (s)",
                backgroundColor: "rgb(0, 125, 179)",
                data: realTimes
            }
        ]
    };
    var fasterOrSlower = data.delayPercent < 0 ? "sneller" : "trager";
    $("#relatiefOverview").html((Math.abs(data.delayPercent)).toFixed(2) + "% " + fasterOrSlower);
    $("#relatiefOverview").css("background", getColorForPercentage(50 - (data.delayPercent) / 2, "percent"));
    document.getElementById('chart').innerHTML = "<canvas id=\"canvas\" width=\"800\" height=\"300\"></canvas>";
    var ctx = $("#canvas").get(0).getContext("2d");
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: myData,
        options: {
            maintainAspectRatio: false,
            scales: {
                xAxes: [{
                    ticks: {
                        autoSkip: false
                    }
                }]
            }
        }
    });
}

function historicChartUpdate(data) {
    var myData = {
        labels: data.avgTimes.timestamps,
        datasets: [{
            type: "line",
            label: "gemiddelde reistijd (s)",
            backgroundColor: "rgba(41,62,72,0.3)",
            data: data.avgTimes.avgTimes
        }, {
            borderColor: "rgba(240,0,0,1)",
            backgroundColor: "rgba(255,255,255,0)",
            type: "line",
            label: "reële reistijd (s)",
            data: data.realTimes.times
        }]
    };
    $("#historicCanvas").remove();
    $("#canvasContainer").append("<canvas id=\"historicCanvas\" width=\"800\" height=\"300\"></canvas>");
    var ctx = document.getElementById("historicCanvas").getContext("2d");
    var myChart = new Chart(ctx, {
        type: "bar",
        data: myData,
        options: {
            maintainAspectRatio: false,
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }],
                xAxes: [{
                    ticks: {
                        callback: function(tick, index, array) {
                            return (index % 6) ? "" : tick;
                        }
                    },
                    gridLines: {
                        display: false
                    }
                }]
            }
        }
    });
}

function loadSection(section) {
    var json = {
        action: "historicDataRequest",
        traject: section
    };
    socket.send(JSON.stringify(json));
    document.getElementById("sectionMenu").innerHTML = section + " <span class=\"caret\"></span>";
}

function initializeRoutes(data) {
    var dropdown = "<div class=\"dropdown\">Traject: <button class=\"btn btn-default dropdown-toggle\" type=\"button\" id=\"sectionMenu\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"true\">Kies een traject<span class=\"caret\"></span></button><ul class=\"dropdown-menu scrollable-menu\" aria-labelledby=\"sectionMenu\">";
    for (var key in data.timing) {
        var name = data.timing[key].name;
        dropdown += "<li><a id=\"" + name + "\">" + name + "</a></li>";
    }
    dropdown += "<li><a id=\"Totaal\">Totaal</a></li>";
    dropdown += "</ul></div><div id=\"canvasContainer\"><canvas id=\"historicCanvas\" width=\"800\" height=\"300\"></canvas></div>";
    document.getElementById('historic').innerHTML = dropdown;
    $(".dropdown-menu").on("click", "li", function(event) {
        loadSection(event.target.id);
    });
    $(".dropdown-menu").css({
        "height": $("#canvasContainer").height()
    });
    routesInitialized = true;
    loadSection("Totaal");
}

function sortTable(id) {
    var table, rows, switching, i, x, y, shouldSwitch;
    table = document.getElementById(id);
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