/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var messaging;

/*self.addEventListener('push', function (event) {
    console.log('[Service Worker] Push Received.');
    console.log(`[Service Worker] Push had this data: "${event.data.text()}"`);

    const title = 'Push Codelab';
    const options = {
        body: 'Yay it works.',
        icon: 'images/icon.png',
        badge: 'images/badge.png'
    };

    event.waitUntil(self.registration.showNotification(title, options));
});*/

var CACHE_NAME = 'verkeersdashboard-cache-v1';
var urlsToCache = [
    //'/',
    '/Verkeersdashboard/verkeersdashboard/css/widgets-style.css'//,
            //'/script/main.js'
];

self.addEventListener('install', function (event) {
    // Perform install steps
    console.log("installing worker...");
    event.waitUntil(
            caches.open(CACHE_NAME)
            .then(function (cache) {
                console.log('Opened cache');
                return cache.addAll(urlsToCache);
            })
            );
});

self.addEventListener('fetch', function (event) {
    event.respondWith(
            caches.match(event.request)
            .then(function (response) {
                // Cache hit - return response
                if (response) {
                    return response;
                }

                // IMPORTANT: Clone the request. A request is a stream and
                // can only be consumed once. Since we are consuming this
                // once by cache and once by the browser for fetch, we need
                // to clone the response.
                var fetchRequest = event.request.clone();

                return fetch(fetchRequest)/*.then(      //Recursief toevoegen aan cache
                 function(response) {
                 // Check if we received a valid response
                 if(!response || response.status !== 200 || response.type !== 'basic') {
                 return response;
                 }
                 
                 // IMPORTANT: Clone the response. A response is a stream
                 // and because we want the browser to consume the response
                 // as well as the cache consuming the response, we need
                 // to clone it so we have two streams.
                 var responseToCache = response.clone();
                 
                 caches.open(CACHE_NAME)
                 .then(function(cache) {
                 cache.put(event.request, responseToCache);
                 });
                 
                 return response;
                 }
                 )*/;
            })
            );
});

self.addEventListener('activate', function (event) {
    console.log("worker activated");

    var cacheWhitelist = ['verkeersdashboard-cache-v1'];

    event.waitUntil(
            caches.keys().then(function (cacheNames) {
        return Promise.all(
                cacheNames.map(function (cacheName) {
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
                );
    })
            );

    importScripts("https://www.gstatic.com/firebasejs/3.9.0/firebase-app.js");
    importScripts("https://www.gstatic.com/firebasejs/3.9.0/firebase-messaging.js");
    var config = {
        apiKey: "AIzaSyARlfe1LS1F36uGt8YmKkHHcbL4RJCvWQ8",
        authDomain: "verkeersdashboard-97910.firebaseapp.com",
        databaseURL: "https://verkeersdashboard-97910.firebaseio.com",
        projectId: "verkeersdashboard-97910",
        storageBucket: "verkeersdashboard-97910.appspot.com",
        messagingSenderId: "382382705211"
    };
    firebase.initializeApp(config);
    console.log("activating Firebase...");
    activateFirebaseMessaging();
    console.log("firebase activated.");
    //messaging = firebase.messaging();
});

function activateFirebaseMessaging() {
    // [START get_messaging_object]
    // Retrieve Firebase Messaging object.
    messaging = firebase.messaging();
    // [END get_messaging_object]
    // IDs of divs that display Instance ID token UI or request permission UI.
    //const tokenDivId = 'token_div';
    //const permissionDivId = 'permission_div';
    // [START refresh_token]
    // Callback fired if Instance ID token is updated.
    messaging.onTokenRefresh(function () {
        messaging.getToken()
                .then(function (refreshedToken) {
                    console.log('Token refreshed: ' + refreshedToken);
                    // Indicate that the new Instance ID token has not yet been sent to the
                    // app server.
                    setTokenSentToServer(false);
                    // Send Instance ID token to app server.
                    sendTokenToServer(refreshedToken);
                    // [START_EXCLUDE]
                    // Display new Instance ID token and clear UI of all previous messages.
                    //resetUI();
                    // [END_EXCLUDE]
                })
                .catch(function (err) {
                    console.log('Unable to retrieve refreshed token ', err);
                    //showToken('Unable to retrieve refreshed token ', err);
                });
    });
    // [END refresh_token]
    // [START receive_message]
    // Handle incoming messages. Called when:
    // - a message is received while the app has focus
    // - the user clicks on an app notification created by a sevice worker
    //   `messaging.setBackgroundMessageHandler` handler.
    messaging.onMessage(function (payload) {
        console.log("Message received. ", payload);
        // [START_EXCLUDE]
        // Update the UI to include the received message.
        appendMessage(payload);
        // [END_EXCLUDE]
    });
    // [END receive_message]
    /*function resetUI() {
        clearMessages();
        showToken('loading...');
        // [START get_token]
        // Get Instance ID token. Initially this makes a network call, once retrieved
        // subsequent calls to getToken will return from cache.
        messaging.getToken()
                .then(function (currentToken) {
                    if (currentToken) {
                        sendTokenToServer(currentToken);
                        updateUIForPushEnabled(currentToken);
                    } else {
                        // Show permission request.
                        console.log('No Instance ID token available. Request permission to generate one.');
                        // Show permission UI.
                        updateUIForPushPermissionRequired();
                        setTokenSentToServer(false);
                    }
                })
                .catch(function (err) {
                    console.log('An error occurred while retrieving token. ', err);
                    showToken('Error retrieving Instance ID token. ', err);
                    setTokenSentToServer(false);
                });
    }*/
    // [END get_token]
    /*function showToken(currentToken) {
        // Show token in console and UI.
        var tokenElement = document.querySelector('#token');
        tokenElement.textContent = currentToken;
    }*/
    // Send the Instance ID token your application server, so that it can:
    // - send messages back to this app
    // - subscribe/unsubscribe the token from topics
    function sendTokenToServer(currentToken) {
        if (!isTokenSentToServer()) {
            console.log('Sending token to server...');
            // TODO(developer): Send the current token to your server.
            setTokenSentToServer(true);
        } else {
            console.log('Token already sent to server so won\'t send it again ' +
                    'unless it changes');
        }
    }
    function isTokenSentToServer() {
        return window.localStorage.getItem('sentToServer') == 1;
    }
    function setTokenSentToServer(sent) {
        window.localStorage.setItem('sentToServer', sent ? 1 : 0);
    }
    /*function showHideDiv(divId, show) {
        const div = document.querySelector('#' + divId);
        if (show) {
            div.style = "display: visible";
        } else {
            div.style = "display: none";
        }
    }*/
    function requestPermission() {
        console.log('Requesting permission...');
        // [START request_permission]
        messaging.requestPermission()
                .then(function () {
                    console.log('Notification permission granted.');
                    // TODO(developer): Retrieve an Instance ID token for use with FCM.
                    // [START_EXCLUDE]
                    // In many cases once an app has been granted notification permission, it
                    // should update its UI reflecting this.
                    //resetUI();
                    // [END_EXCLUDE]
                })
                .catch(function (err) {
                    console.log('Unable to get permission to notify.', err);
                });
        // [END request_permission]
    }
    function deleteToken() {
        // Delete Instance ID token.
        // [START delete_token]
        messaging.getToken()
                .then(function (currentToken) {
                    messaging.deleteToken(currentToken)
                            .then(function () {
                                console.log('Token deleted.');
                                setTokenSentToServer(false);
                                // [START_EXCLUDE]
                                // Once token is deleted update UI.
                                //resetUI();
                                // [END_EXCLUDE]
                            })
                            .catch(function (err) {
                                console.log('Unable to delete token. ', err);
                            });
                    // [END delete_token]
                })
                .catch(function (err) {
                    console.log('Error retrieving Instance ID token. ', err);
                    //showToken('Error retrieving Instance ID token. ', err);
                });
    }
    // Add a message to the messages element.
    /*function appendMessage(payload) {
        const messagesElement = document.querySelector('#messages');
        const dataHeaderELement = document.createElement('h5');
        const dataElement = document.createElement('pre');
        dataElement.style = 'overflow-x:hidden;'
        dataHeaderELement.textContent = 'Received message:';
        dataElement.textContent = JSON.stringify(payload, null, 2);
        messagesElement.appendChild(dataHeaderELement);
        messagesElement.appendChild(dataElement);
    }*/
    // Clear the messages element of all children.
    /*function clearMessages() {
        const messagesElement = document.querySelector('#messages');
        while (messagesElement.hasChildNodes()) {
            messagesElement.removeChild(messagesElement.lastChild);
        }
    }*/
    /*function updateUIForPushEnabled(currentToken) {
        showHideDiv(tokenDivId, true);
        showHideDiv(permissionDivId, false);
        showToken(currentToken);
    }*/
    /*function updateUIForPushPermissionRequired() {
        showHideDiv(tokenDivId, false);
        showHideDiv(permissionDivId, true);
    }*/
    //resetUI();
}