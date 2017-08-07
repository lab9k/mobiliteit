importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/3.9.0/firebase-messaging.js');

// Initialize Firebase
var config = {
    apiKey: "AIzaSyARlfe1LS1F36uGt8YmKkHHcbL4RJCvWQ8",
    authDomain: "verkeersdashboard-97910.firebaseapp.com",
    databaseURL: "https://verkeersdashboard-97910.firebaseio.com",
    projectId: "verkeersdashboard-97910",
    storageBucket: "verkeersdashboard-97910.appspot.com",
    messagingSenderId: "382382705211"
};
firebase.initializeApp(config);

// Retrieve an instance of Firebase Messaging so that it can handle background
// messages.
const messaging = firebase.messaging();

//Note: If you set notification fields in your HTTP or XMPP send request, those values take precedence over any values specified in the service worker.
messaging.setBackgroundMessageHandler(function (payload) {
    //console.log('[sw2.js] Received background message ', payload);
    // Customize notification here
    const notificationTitle = 'Er is een melding voor jou!';
    const notificationOptions = {
        body: 'We weten echter niet dewelke :s',
        icon: '/notificon.png'
    };

    return self.registration.showNotification(notificationTitle, notificationOptions);
});