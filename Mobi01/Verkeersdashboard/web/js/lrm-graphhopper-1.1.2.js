(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;
                if (!u && a)
                    return a(o, !0);
                if (i)
                    return i(o, !0);
                var f = new Error("Cannot find module '" + o + "'");
                throw f.code = "MODULE_NOT_FOUND", f
            }
            var l = n[o] = {exports: {}};
            t[o][0].call(l.exports, function (e) {
                var n = t[o][1][e];
                return s(n ? n : e)
            }, l, l.exports, e, t, n, r)
        }
        return n[o].exports
    }
    var i = typeof require == "function" && require;
    for (var o = 0; o < r.length; o++)
        s(r[o]);
    return s
})({1: [function (require, module, exports) {
            function corslite(url, callback, cors) {
                var sent = false;

                if (typeof window.XMLHttpRequest === 'undefined') {
                    return callback(Error('Browser not supported'));
                }

                if (typeof cors === 'undefined') {
                    var m = url.match(/^\s*https?:\/\/[^\/]*/);
                    cors = m && (m[0] !== location.protocol + '//' + location.domain +
                            (location.port ? ':' + location.port : ''));
                }

                var x = new window.XMLHttpRequest();

                function isSuccessful(status) {
                    return status >= 200 && status < 300 || status === 304;
                }

                if (cors && !('withCredentials' in x)) {
                    // IE8-9
                    x = new window.XDomainRequest();

                    // Ensure callback is never called synchronously, i.e., before
                    // x.send() returns (this has been observed in the wild).
                    // See https://github.com/mapbox/mapbox.js/issues/472
                    var original = callback;
                    callback = function () {
                        if (sent) {
                            original.apply(this, arguments);
                        } else {
                            var that = this, args = arguments;
                            setTimeout(function () {
                                original.apply(that, args);
                            }, 0);
                        }
                    }
                }

                function loaded() {
                    if (
                            // XDomainRequest
                            x.status === undefined ||
                            // modern browsers
                            isSuccessful(x.status))
                        callback.call(x, null, x);
                    else
                        callback.call(x, x, null);
                }

                // Both `onreadystatechange` and `onload` can fire. `onreadystatechange`
                // has [been supported for longer](http://stackoverflow.com/a/9181508/229001).
                if ('onload' in x) {
                    x.onload = loaded;
                } else {
                    x.onreadystatechange = function readystate() {
                        if (x.readyState === 4) {
                            loaded();
                        }
                    };
                }

                // Call the callback with the XMLHttpRequest object as an error and prevent
                // it from ever being called again by reassigning it to `noop`
                x.onerror = function error(evt) {
                    // XDomainRequest provides no evt parameter
                    callback.call(this, evt || true, null);
                    callback = function () { };
                };

                // IE9 must have onprogress be set to a unique function.
                x.onprogress = function () { };

                x.ontimeout = function (evt) {
                    callback.call(this, evt, null);
                    callback = function () { };
                };

                x.onabort = function (evt) {
                    callback.call(this, evt, null);
                    callback = function () { };
                };

                // GET is the only supported HTTP Verb by XDomainRequest and is the
                // only one supported here.
                x.open('GET', url, true);

                // Send the request. Sending data is not supported.
                x.send(null);
                sent = true;

                return x;
            }

            if (typeof module !== 'undefined')
                module.exports = corslite;

        }, {}], 2: [function (require, module, exports) {
            var polyline = {};

// Based off of [the offical Google document](https://developers.google.com/maps/documentation/utilities/polylinealgorithm)
//
// Some parts from [this implementation](http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/PolylineEncoder.js)
// by [Mark McClure](http://facstaff.unca.edu/mcmcclur/)

            function encode(coordinate, factor) {
                coordinate = Math.round(coordinate * factor);
                coordinate <<= 1;
                if (coordinate < 0) {
                    coordinate = ~coordinate;
                }
                var output = '';
                while (coordinate >= 0x20) {
                    output += String.fromCharCode((0x20 | (coordinate & 0x1f)) + 63);
                    coordinate >>= 5;
                }
                output += String.fromCharCode(coordinate + 63);
                return output;
            }

// This is adapted from the implementation in Project-OSRM
// https://github.com/DennisOSRM/Project-OSRM-Web/blob/master/WebContent/routing/OSRM.RoutingGeometry.js
            polyline.decode = function (str, precision) {
                var index = 0,
                        lat = 0,
                        lng = 0,
                        coordinates = [],
                        shift = 0,
                        result = 0,
                        byte = null,
                        latitude_change,
                        longitude_change,
                        factor = Math.pow(10, precision || 5);

                // Coordinates have variable length when encoded, so just keep
                // track of whether we've hit the end of the string. In each
                // loop iteration, a single coordinate is decoded.
                while (index < str.length) {

                    // Reset shift, result, and byte
                    byte = null;
                    shift = 0;
                    result = 0;

                    do {
                        byte = str.charCodeAt(index++) - 63;
                        result |= (byte & 0x1f) << shift;
                        shift += 5;
                    } while (byte >= 0x20);

                    latitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

                    shift = result = 0;

                    do {
                        byte = str.charCodeAt(index++) - 63;
                        result |= (byte & 0x1f) << shift;
                        shift += 5;
                    } while (byte >= 0x20);

                    longitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

                    lat += latitude_change;
                    lng += longitude_change;

                    coordinates.push([lat / factor, lng / factor]);
                }

                return coordinates;
            };

            polyline.encode = function (coordinates, precision) {
                if (!coordinates.length)
                    return '';

                var factor = Math.pow(10, precision || 5),
                        output = encode(coordinates[0][0], factor) + encode(coordinates[0][1], factor);

                for (var i = 1; i < coordinates.length; i++) {
                    var a = coordinates[i], b = coordinates[i - 1];
                    output += encode(a[0] - b[0], factor);
                    output += encode(a[1] - b[1], factor);
                }

                return output;
            };

            if (typeof module !== undefined)
                module.exports = polyline;

        }, {}], 3: [function (require, module, exports) {
            (function (global) {
                (function () {
                    'use strict';

                    var L = (typeof window !== "undefined" ? window.L : typeof global !== "undefined" ? global.L : null);
                    var corslite = require('corslite');
                    var polyline = require('polyline');

                    L.Routing = L.Routing || {};

                    L.Routing.GraphHopper = L.Class.extend({
                        options: {
                            serviceUrl: 'https://graphhopper.com/api/1/route',
                            timeout: 30 * 1000,
                            urlParameters: {},
                            delijnServiceUrl: 'https://www.delijn.be/rise-api-core/reisadvies/routes/',
                            delijnCoordConvertUrl: 'https://www.delijn.be/rise-api-core/coordinaten/convert/',
                            delijnTimeout: 30000,
                            delijnUrlParameters: {},
                            delijnRouteParam: {}
                        },

                        initialize: function (apiKey, options) {
                            this._apiKey = apiKey;
                            L.Util.setOptions(this, options);
                        },

                        route: function (waypoints, callback, context, options) {
                            var timedOut = false,
                                    delijnTimedOut = false,
                                    wps = [],
                                    url,
                                    delijnUrl,
                                    timer,
                                    delijnTimer,
                                    wp,
                                    i;

                            latestId = ++latestId % 1024;
                            latestRoutes = [];

                            options = options || {};
                            url = this.buildRouteUrl(waypoints, options);

                            // Create a copy of the waypoints, since they
                            // might otherwise be asynchronously modified while
                            // the request is being processed.
                            for (i = 0; i < waypoints.length; i++) {
                                wp = waypoints[i];
                                wps.push({
                                    latLng: wp.latLng,
                                    name: wp.name,
                                    options: wp.options
                                });
                            }

                            if (this.options.routeCar) {
                                timer = setTimeout(function () {
                                    timedOut = true;
                                    callback.call(context || callback, {
                                        status: -1,
                                        message: 'GraphHopper request timed out.'
                                    });
                                }, this.options.timeout);

                                corslite(url, L.bind(function (err, resp) {
                                    var data;

                                    clearTimeout(timer);
                                    if (!timedOut) {
                                        if (!err) {
                                            data = JSON.parse(resp.responseText);
                                            this._routeDone(data, wps, callback, context, latestId);
                                        } else {
                                            callback.call(context || callback, {
                                                status: -1,
                                                message: 'HTTP request failed: ' + err
                                            });
                                        }
                                    }
                                }, this));
                            }

                            if (this.options.delijnRouteParam.delijnbyBus || this.options.delijnRouteParam.delijnbyTram || this.options.delijnRouteParam.delijnbyMetro || this.options.delijnRouteParam.delijnbyBelbus || this.options.delijnRouteParam.delijnbyTrain) {
                                delijnTimer = setTimeout(function () {
                                    delijnTimedOut = true;
                                    callback.call(context || callback, {
                                        status: -1,
                                        message: 'deLijn request timed out.'
                                    });
                                }, this.options.timeout);

                                //convert delijn coordinates to Lambert '72
                                var lambertWps = new Array(wps.length),
                                        conversionDone = 0; //Keeps track of the number of waypoints that are done converting
                                for (var i = 0; i < wps.length; i++) {
                                    var thisthingy = this;
                                    //console.log(this.options.delijnCoordConvertUrl + wps[i].latLng.lat + '/' + wps[i].latLng.lng);
                                    var iCopy = i;
                                    /*$.getJSON(this.options.delijnCoordConvertUrl + wps[i].latLng.lat + '/' + wps[i].latLng.lng, function (data) {
                                     console.log(data);
                                     console.log(i);
                                     lambertWps[i] = {
                                     x: data.xCoordinaat,
                                     y: data.yCoordinaat,
                                     name: wps[iCopy].name ? wps[iCopy].name : '',
                                     options: wps[iCopy].options
                                     };
                                     conversionDone++;
                                     if (conversionDone === wps.length) {
                                     //All coordinates are converted, proceed route calculation
                                     thisthing._routeDelijn(lambertWps, callback, context);
                                     }
                                     });*/
                                    $.ajax({
                                        url: this.options.delijnCoordConvertUrl + wps[i].latLng.lat + '/' + wps[i].latLng.lng,
                                        dataType: 'json',
                                        context: {iCopy: i, thisthing: thisthingy, delijnTimer: delijnTimer, requestId: latestId},
                                        complete: function (data) {
                                            clearTimeout(this.delijnTimer);
                                            //console.log(data);
                                            lambertWps[this.iCopy] = {
                                                wp: wps[this.iCopy],
                                                x: data.responseJSON.xCoordinaat,
                                                y: data.responseJSON.yCoordinaat,
                                                name: wps[this.iCopy].name ? wps[this.iCopy].name : '',
                                                options: wps[this.iCopy].options
                                            };
                                            conversionDone++;
                                            if (conversionDone === wps.length) {
                                                //All coordinates are converted, proceed route calculation
                                                //console.log('conversion received, delijn routing starting..');
                                                this.thisthing._routeDelijn(lambertWps, callback, context, this.requestId);
                                            }
                                        },
                                        error: function (jqXHR, textStatus, errorThrown) {
                                            callback.call(context || callback, {
                                                status: -1,
                                                message: "Error: Delijn coördinates conversion failed: " + textStatus + "\n" + errorThrown
                                            });
                                        }
                                    });
                                }
                            }


                            return this;
                        },
                        _routeDelijn: function (waypoints, callback, context, requestId) {
                            var url = this.buildDelijnRouteUrl(waypoints);
                            //console.log('delijn route url: ' + url);
                            var thisThing = this;
                            var delijnTimeout = false;
                            var timer = setTimeout(function () {
                                delijnTimeout = true;
                                callback.call(context, {
                                    status: -1,
                                    message: "Error: Delijn request timed out."
                                });
                            }, 30000);

                            //request route from deLijn api
                            $.getJSON(url, function (data) {
                                if (!delijnTimeout) {
                                    console.log(data);
                                    clearTimeout(timer);
                                    thisThing._routeDelijnDone(data, waypoints, callback, context, requestId);
                                }
                            }).fail(function (jqxhr, textStatus, error) {
                                clearTimeout(timer);
                                callback.call(context || callback, {
                                    status: -1,
                                    message: "Error: Delijn request failed: " + textStatus + "\n" + error
                                });
                            });
                        },

                        buildDelijnRouteUrl: function (waypoints) {
                            console.log(this.options.delijnRouteParam);
                            var url = this.options.delijnServiceUrl
                                    + (waypoints[0].name ? waypoints[0].name : 'null') + '/'
                                    + (waypoints[1].name ? waypoints[1].name : 'null') + '/'
                                    + waypoints[0].x + '/'
                                    + waypoints[0].y + '/'
                                    + waypoints[1].x + '/'
                                    + waypoints[1].y + '/'
                                    + (this.options.delijnRouteParam.delijnDate ? this.options.delijnRouteParam.delijnDate : new Date()).getDate() + '-' + ((this.options.delijnRouteParam.delijnDate ? this.options.delijnRouteParam.delijnDate : new Date()).getMonth() + 1) + '-' + (this.options.delijnRouteParam.delijnDate ? this.options.delijnRouteParam.delijnDate : new Date()).getFullYear() + '/'
                                    + (this.options.delijnRouteParam.delijnDate ? this.options.delijnRouteParam.delijnDate : new Date()).getHours() + ':' + (this.options.delijnRouteParam.delijnDate ? this.options.delijnRouteParam.delijnDate : new Date()).getMinutes() + '/'
                                    + (this.options.delijnRouteParam.delijnArrival ? 2 : 1) + '/'
                                    + (this.options.delijnRouteParam.delijnbyBus ? 'on' : 'off') + '/'
                                    + (this.options.delijnRouteParam.delijnbyTram ? 'on' : 'off') + '/'
                                    + (this.options.delijnRouteParam.delijnbyMetro ? 'on' : 'off') + '/'
                                    + (this.options.delijnRouteParam.delijnbyTrain ? 'on' : 'off') + '/'
                                    + (this.options.delijnRouteParam.delijnbyBelbus ? 'on' : 'off') + '/'
                                    + (this.options.delijnRouteParam.delijnlanguage ? this.options.language : 'nl') + '/';
                            return url;
                        },

                        _routeDelijnDone: function (response, inputWaypoints, callback, context, requestId) {
                            if (response && response.reiswegen) {
                                var reiswegen = response.reiswegen;
                                var alts = [];
                                for (var i = 0; i < reiswegen.length; i++) {
                                    //coordinates
                                    var coordinates = [];
                                    for (var j = 0; j < reiswegen[i].reiswegStappen.length; j++) {
                                        for (var k = 0; k < reiswegen[i].reiswegStappen[j].coordinaten.length; k++) {
                                            coordinates.push(reiswegen[i].reiswegStappen[j].coordinaten[k]);
                                        }
                                    }
                                    var coords = new Array(coordinates.length);
                                    for (var l = 0; l < coordinates.length; l++) {
                                        coords[l] = new L.LatLng(coordinates[l].lt, coordinates[l].ln);
                                    }
                                    //instructions
                                    var instructions = [];
                                    var totalDistance = 0;
                                    var index = 0;
                                    for (var m = 0; m < reiswegen[i].reiswegStappen.length; m++) {
                                        instructions.push({
                                            type: '',
                                            text: (reiswegen[i].reiswegStappen[m].instructie ? reiswegen[i].reiswegStappen[m].instructie : reiswegen[i].reiswegStappen[m].lijn ? "Neem " + reiswegen[i].reiswegStappen[m].lijn.lijnType + " " + reiswegen[i].reiswegStappen[m].lijn.lijnNummerPubliek + " (" + reiswegen[i].reiswegStappen[m].lijn.omschrijving + ") Tot aan halte " + reiswegen[i].reiswegStappen[m].aankomstLocatie + ' (± ' + reiswegen[i].reiswegStappen[m].duration + 'min)' : (reiswegen[i].reiswegStappen[m].duration + 'min ' + reiswegen[i].reiswegStappen[m].type.toLowerCase() + " tot " + reiswegen[i].reiswegStappen[m].end)),
                                            distance: reiswegen[i].reiswegStappen[m].afstand,
                                            time: reiswegen[i].reiswegStappen[m].duration,
                                            index: index,
                                            exit: ''
                                        });
                                        index += reiswegen[i].reiswegStappen[m].coordinaten.length;
                                        totalDistance += Number(reiswegen[i].reiswegStappen[m].afstand);
                                        //console.log('afstand: ' + Number(reiswegen[i].reiswegStappen[m].afstand) + '\ttotalDistance: ' + totalDistance)
                                    }
                                    var time;
                                    var timestring = reiswegen[i].duration;
                                    timestring = timestring.slice(0, -3);
                                    var timearay = timestring.split("u");
                                    time = (Number(60 * timearay[0]) + Number(timearay[1])) * 60;

                                    //var inputWps = [];

                                    //collect data, add alternative route
                                    alts.push({
                                        name: 'OV ' + (i + 1),
                                        coordinates: coords,
                                        instructions: instructions,
                                        summary: {
                                            totalDistance: totalDistance,
                                            totalTime: time,
                                        },
                                        inputWaypoints: [inputWaypoints[0].wp, inputWaypoints[1].wp],
                                        actualWaypoints: [coords[0], coords[coords.length - 1]],
                                        waypointIndices: [0, coords.length - 1]
                                    });
                                }
                                //console.log(alts);
                                //callback.call(context, null, alts);
                                if (alts.length > 0)
                                    finishComputeFromSource(callback, context, alts, requestId);
                                else
                                    callback.call(context || callback, {
                                        status: -1,
                                        message: "Geen routes gevonden via DeLijn."
                                    });
                            } else {
                                callback.call(context || callback, {
                                    status: -1,
                                    message: "Error: Delijn failed."
                                });
                            }
                        },

                        _routeDone: function (response, inputWaypoints, callback, context, routeId) {
                            var alts = [],
                                    mappedWaypoints,
                                    coordinates,
                                    i,
                                    path;

                            context = context || callback;
                            if (response.info.errors && response.info.errors.length) {
                                callback.call(context, {
                                    // TODO: include all errors
                                    status: response.info.errors[0].details,
                                    message: response.info.errors[0].message
                                });
                                return;
                            }

                            for (i = 0; i < response.paths.length; i++) {
                                path = response.paths[i];
                                coordinates = this._decodePolyline(path.points);
                                mappedWaypoints =
                                        this._mapWaypointIndices(inputWaypoints, path.instructions, coordinates);

                                alts.push({
                                    name: 'Auto ' + (i + 1),
                                    coordinates: coordinates,
                                    instructions: this._convertInstructions(path.instructions),
                                    summary: {
                                        totalDistance: path.distance,
                                        totalTime: path.time / 1000,
                                    },
                                    inputWaypoints: inputWaypoints,
                                    actualWaypoints: mappedWaypoints.waypoints,
                                    waypointIndices: mappedWaypoints.waypointIndices
                                });
                            }
                            //console.log('graphhopper alts:');
                            //console.log(alts);
                            //callback.call(context, null, alts);
                            finishComputeFromSource(callback, context, alts, routeId);
                        },

                        _decodePolyline: function (geometry) {
                            var coords = polyline.decode(geometry, 5),
                                    latlngs = new Array(coords.length),
                                    i;
                            for (i = 0; i < coords.length; i++) {
                                latlngs[i] = new L.LatLng(coords[i][0], coords[i][1]);
                            }

                            return latlngs;
                        },

                        _toWaypoints: function (inputWaypoints, vias) {
                            var wps = [],
                                    i;
                            for (i = 0; i < vias.length; i++) {
                                wps.push({
                                    latLng: L.latLng(vias[i]),
                                    name: inputWaypoints[i].name,
                                    options: inputWaypoints[i].options
                                });
                            }

                            return wps;
                        },

                        buildRouteUrl: function (waypoints, options) {
                            var computeInstructions =
                                    /* Instructions are always needed, 
                                     since we do not have waypoint indices otherwise */
                                    true,
                                    //!(options && options.geometryOnly),
                                    locs = [],
                                    i,
                                    baseUrl;

                            for (i = 0; i < waypoints.length; i++) {
                                locs.push('point=' + waypoints[i].latLng.lat + ',' + waypoints[i].latLng.lng);
                            }

                            baseUrl = this.options.serviceUrl + '?' +
                                    locs.join('&');

                            return baseUrl + L.Util.getParamString(L.extend({
                                instructions: computeInstructions,
                                type: 'json',
                                key: this._apiKey
                            }, this.options.urlParameters), baseUrl);
                        },

                        _convertInstructions: function (instructions) {
                            var signToType = {
                                '-3': 'SharpLeft',
                                '-2': 'Left',
                                '-1': 'SlightLeft',
                                0: 'Straight',
                                1: 'SlightRight',
                                2: 'Right',
                                3: 'SharpRight',
                                4: 'DestinationReached',
                                5: 'WaypointReached',
                                6: 'Roundabout'
                            },
                                    result = [],
                                    i,
                                    instr;

                            for (i = 0; instructions && i < instructions.length; i++) {
                                instr = instructions[i];
                                result.push({
                                    type: signToType[instr.sign],
                                    text: instr.text,
                                    distance: instr.distance,
                                    time: instr.time / 1000,
                                    index: instr.interval[0],
                                    exit: instr.exit_number
                                });
                            }

                            return result;
                        },

                        _mapWaypointIndices: function (waypoints, instructions, coordinates) {
                            var wps = [],
                                    wpIndices = [],
                                    i,
                                    idx;

                            wpIndices.push(0);
                            wps.push(new L.Routing.Waypoint(coordinates[0], waypoints[0].name));

                            for (i = 0; instructions && i < instructions.length; i++) {
                                if (instructions[i].sign === 5) { // VIA_REACHED
                                    idx = instructions[i].interval[0];
                                    wpIndices.push(idx);
                                    wps.push({
                                        latLng: coordinates[idx],
                                        name: waypoints[wps.length + 1].name
                                    });
                                }
                            }

                            wpIndices.push(coordinates.length - 1);
                            wps.push({
                                latLng: coordinates[coordinates.length - 1],
                                name: waypoints[waypoints.length - 1].name
                            });

                            return {
                                waypointIndices: wpIndices,
                                waypoints: wps
                            };
                        }
                    });

                    L.Routing.graphHopper = function (apiKey, options) {
                        return new L.Routing.GraphHopper(apiKey, options);
                    };

                    module.exports = L.Routing.GraphHopper;
                })();

            }).call(this, typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})
        }, {"corslite": 1, "polyline": 2}]}, {}, [3]);

var latestId = 0;
var latestRoutes;

function finishComputeFromSource(callback, context, routes, requestId) {
    //console.log(latestId + "<>" + requestId);
    if (latestId === requestId) {
        if (!latestRoutes)
            latestRoutes = [];
        //add routes to the list
        for (var i = 0; i < routes.length; i++) {
            latestRoutes.push(routes[i]);
        }

        callback.call(context, null, latestRoutes);
    }
}