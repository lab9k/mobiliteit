# Mobi-01 - Verkeersdashboard Stad Gent

Met dit project trachten wij op een overzichtelijke manier een kijk te geven op de actuale situatie van het verkeer in Gent.
Zodat de gebruiker zich zo efficïent en comfortabel mogelijk kan verplaatsen in een oogopslag.

##Screenshot van dashboard
![Screenshot dashboard](https://github.ugent.be/iii-vop2017/mobi-01/blob/master/Analyse/Screenshots/Screenshot%20Dashboard.png?raw=true)


##Indeling Repository
- Analyse: bevat analyse en ontwerpdocumenten
- Verkeersdashboard: bevat de webapplicatie in een netbeans project

##Lokaal het project Deployen
- Download de MySQL installer (https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-web-community-5.7.17.0.msi) en run de installer
- Selecteer Custom Install en selecteer en installeer de volgende producten
 - Onder MySQL Servers en MySQL Server MySQL Server selecteren en corresponderend aan jouw systeem de 32 of 64 bit versie selecteren dan op het pijltje naar rechts te klikken
 - Onder Applications, MySQL Workbench, MySQL Workbench 6.3 MySQL Workbench selecteren dan op het pijltje naar rechts te klikken
 - Onder MySQL Connectors, Connector/J Connector/J 5.1 selecteren en dan op het pijltje naar rechts te klikken
- Na succesvol installeren van deze onderdelen volgt de configuratie van de MySQL server
 - Druk bij het "Type and Networking" scherm gewoon op "Next"
 - Gebruik als MySQL Root Password het volgende: test
 - Druk dan driemaal op "Next" en tenslotte op "Execute" om de configuratie toe te passen en laat de MySQL Workbench applicatie opstarten
- Tussen MySQL Connections zou er een connectie moeten staan, open die door er op te dubbelklikken
-  Klik links op "Users and Privileges"> "Add Account" > "Login Name" = 'tiwi' Password='test' > Tab Administrative roles: DBA aanvinken (alles) > apply
![Screenshot user privileges](https://github.ugent.be/iii-vop2017/mobi-01/blob/master/Analyse/Installatie/Users%20and%20Privileges.png?raw=true) 
- Klik in de werkbalk boven aan op "Create a new schema in the connected server" (het db-icoontje met een +) en maak een schema aan met als naam "dashboard" door de wizard te doorlopen
![Screenshot nieuw schema aanmaken](https://github.ugent.be/iii-vop2017/mobi-01/blob/master/Analyse/Installatie/New%20Schema.png?raw=true)
- Zorg dat je de recentste versie van Java hebt geïnstalleerd (https://java.com/nl/download/)
- Test of de installatie volledig gelukt is door in een commandovenster (Windows start->"cmd.exe" intypen en enter) het commando  "java -version" uit te voeren
- ![Screenshot cmd venster](https://github.ugent.be/iii-vop2017/mobi-01/blob/master/Analyse/Installatie/Screeshot%20java%20version.png?raw=true)
- Indien je hier "java not recognized as internal command" te zien krijgt, volg de hieronderstaande stappen
 - Open Windows Verkenner en rechtsklik om "Deze PC" en klik op "Eigenschappen"
 - Klik dan op 'Geavanceerde Rigenschappen" en dan op "Omgevingsvariablen"
 - Onder Systeemvariabelen, klik op de variable met naam "PATH" en klik op bewerken
 - Druk dan op "nieuw" en voeg als waarde het pad uit naar jouw bin folder van jouw java installatie (C:\Program Files\Java\jre1.8.0_102\bin) *Dit pad kan verschillen afhankelijk waar java is geïnstalleerd
 - Druk dan telkens ok om alle vensters te sluiten en test opnieuw het "java -version" commando uit
 - ![Screenshot omgevingsvariabelen](https://github.ugent.be/iii-vop2017/mobi-01/blob/master/Analyse/Installatie/Screenshot%20omgevingsvariabelen.png?)
- Download dit .rar bestand en pak het uit (https://drive.google.com/open?id=0B04IyFk0BHAZTFB1aURBTVpRU1U)
- Open de map "Verkeersdashboard met Glassfish" en dubbelklik op "Start Verkeersdashboard.bat", dit bestand zal de nodige glassfishconfiguratie uitvoeren en vervolgens onze website tonen in de defaultbrowser van de computer
- Nadat de server is opgestart, kan je die vervolgens afsluiten door op een toets te drukken in het geopende commandovenster of door het bestand "stop.bat" uit te voeren.


##Website op het internet bereiken
- http://mobi-01.project.tiwi.be:8181/ bezoeken met een (moderne) browser naar keuze.





##Contributors
- Ian Van der Mynsbrugge
- Robin Lievrouw
- Jens Asselman
- Kristof Neyt
