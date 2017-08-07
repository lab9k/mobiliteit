'use strict';

function activeerFirebaseWebNotificaties_() {
    activeerFirebaseWebNotificaties(function () {});
}

var serviceWorkerRegistration;

function activeerFirebaseWebNotificaties(callback) {
    if ('serviceWorker' in navigator) {
        //console.log('Service Workers are supported');

        if (!serviceWorkerRegistration) {
            navigator.serviceWorker.register('js/firebase-messaging-sw.js').then(function (registration) {
                // Registration was successful
                console.log('ServiceWorker registration successful with scope: ', registration.scope);
                var messaging = firebase.messaging();
                messaging.useServiceWorker(registration);
                serviceWorkerRegistration = registration;

                getTokenfunction(callback);

                messaging.onMessage(function (payload) {
                    console.log("Message received. ", payload);
                    var notification = new Notification(payload.notification.title, {
                        icon: payload.notification.icon,
                        body: payload.notification.body
                    });
                    notification.onclick = function () {
                        window.focus();
                    };
                });
            }, function (err) {
                // registration failed :(
                console.log('ServiceWorker registration failed: ', err);
                callback(undefined);
            });
        } else {
            getTokenfunction(callback);
        }
    } else {
        console.warn('Service Workers are not supported');
        callback(undefined);
    }
}

function getTokenfunction(callback) {
    var messaging = firebase.messaging();
    messaging.getToken()
            .then((currentToken) => {
                if (currentToken) {
                    callback(currentToken);
                } else {
                    console.warn("Nog geen toestemming om push-notificaties te tonen.");
                    messaging.requestPermission()
                            .then(function () {
                                console.log('Notification permission granted.');
                                // TODO(developer): Retrieve an Instance ID token for use with FCM.
                                // ...
                                //activeerFirebaseWebNotificaties(callback);
                                getTokenfunction(callback);
                            })
                            .catch(function (err) {
                                console.log('Unable to get permission to notify.', err);
                                callback(undefined);
                            });

                    // you don't have permission to show notifications
                    // detect whether they are blocked or not, then show your custom UI  
                }
            })
            .catch((err) => {
                // retrieving token failed, analyze the error
                callback(undefined);
                console.warn("Firebase-token ophalen mislukt. ", err);
            });
}