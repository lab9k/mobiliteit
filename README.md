Sprint 3: verduidelijking project
----------------------
Opmerking: Vermits de server een beperkt geheugen heeft, en deze nogal snel out of memory blijkt te gaan door een hoog verbruik van de Glassfish server, staat op de online versie een beperkte versie, namelijk diegene zonder notifications. Deze versie zit ook ter beschikking in het mapje 'Server version without notifications'.


###Optie 1: online versie testen
Via de link : https://mobi-02.project.tiwi.be:8181/ is de website zelf terug te vinden, zoals deze gedeployed staat op de server. Hierbij kunt u de functionaliteiten testen zoals in de gebruikershandleiding worden beschreven (terug te vinden op de Wiki). Enkele aandachtspunten hierbij zijn:
- Aanmelden via Facebook: dit gaat enkel indien u als developer/administrator werd toegevoegd, dit omdat Facebook het had moeten reviewen, wat voor deze tijdelijke website niet nuttig was. 
   Een optie is om via dit account in te loggen: 
    - Gebruikersnaam: Mobi Liteit (mailadres: mobi02.gent@gmail.com)
    - Wachtwoord: Mobi02istop

Opmerking: 
- notifications van messenger is nog in ontwikkelingsfase
- Mailingserver: deze werkt online niet omdat uitgaande SMTP vermoedelijk wordt geblokkeerd op DigitalOcean en er geen geldig certificaat is. Vermits het een tijdelijke server is via DigitalOcean en de notifications een uitbreiding zijn die in sprint 3 verwezenlijkt werden, werd dit niet meer uitgewerkt. Lokaal werkt deze functionaliteit wel, zie optie 2.
- De laatste versie van de back end lijkt op piekmomenten meer geheugen te gebruiken dan voordien, hierdoor stopt de server soms met reageren. (1GB ram is dus niet voldoende)


###Optie 2: Via GitHub code: lokaal
Met behulp van dit project op GitHub, is het mogelijk om het project lokaal te laten werken. De installatiehandleiding legt uit welke software en installatiestappen hiervoor nodig zijn.
Lokaal kan ook de functionaliteit getest worden zoals in de gebruikershandleiding beschreven wordt.
Enkele opmerkingen hierbij:
- Aanmelden via Facebook: dit gaat niet lokaal. Reden hiervoor is dat toegestane links ingesteld worden via Facebook zelf, en van Facebook uit 'localhost:8181' niet toegevoegd kan worden. (Facebook gebruikt webhook = https://mobi-02.project.tiwi.be:8181/back/res/facebook)
- Mailingserver: de poort dient ingesteld te staan op 587, wat in deze eindversie het geval is. Het gaat hierbij om de code in Mobi02_back > Source Packages > Notifications.Mail > MailDao.java bij de instellingen van de properties:
``` {language="json" startFrom="1"}
properties.put("mail.smtp.port", "587");
properties.put("mail.smtp.socketFactory.port", "587");
```
- Zoals ook vermeld wordt in de installatiehandleiding, dient in het frontend project de juiste 'host' link ingesteld te worden in constant.js. Op de definitieve githubversie staat deze reeds correct. Het gaat hierbij om de code in 
Mobi02_front > Web Pages > js > constants.js
``` {language="json" startFrom="1"}
var host = "https://localhost:8181"; 
```
- Tot slot dient er rekening gehouden te worden met het basisuur van de Glassfish Server. De server op DigitalOcean staat ingesteld op GMT +0 en houdt bovendien geen rekening met het zomeruur. Om dit te corrigeren wordt een tijdsverschil van 2 uren ingesteld. Lokaal is dit niet nodig, en dient deze propertie bijgevolg op 0 te staan zoals in de eindversie op GitHub het geval is. Het gaat heirbij om de code in Mobi02_back > Source Packages > Api.Caching.properties > timoeouts.properties:
``` {language="json" startFrom="1"}
timezone_correction=0
```

In de code die gepusht werd met commit "Definitieve versie" staat dit alles zo ingesteld.

Installatiehandleiding
----------------------

### Beschrijving software

**Java webcontainer**

De applicatie is een Java web applicatie. Die heeft een compatible webomgeving nodig om te functioneren. Tijdens het
ontwikkelen en testen wordt gebruikgemaakt van een Glassfish
webcontainer. De installatieomschrijving zal dan ook specifieke
Glassfish instellingen en screenshots bespreken. Het is sterk aangeraden
om dus ook een Glassfish server te gebruiken, maar een andere
compatibele webcontainer zou hetzelfde resultaat moeten
geven.

**Database**

Elke database die de MySQL standaard implementeert is
compatibel met de applicatie. Andere SQL-database varianten echter
niet.

**Applicatiebestanden**

De applicatie wordt gebundeld in twee .war
bestand. Deze bevatten de frontend en de backend, deze worden bij de
installatie gedeployed in de webcontainer.

### Benodigdheden

-   Glassfish webcontainer

-   Html, CSS en Javascript compatibele webserver

-   MySQL database

-   Applicatie .war bestand

-   Front end website (html/css/js compatible)

-   glassfish-resources.xml configuratiebestand

### Installatie en configuratie van de database

**Installatie**

Bekijk voor de installatie van de MySQL database de documentatie van de database die wordt gebruikt. Enkele voorbeelden
zijn: MySQL Database (mysql.com), MariaDB (mariadb.com)...

**Configuratie**

Na het installeren moet er een nieuwe database worden geconfigureerd. Stappen 3 en 4 zijn optioneel, maar het
is sterk aangeraden om niet met de root-gebruiker te werken in de
applicatie.

1.  Open na de installatie een MySQL command promt en log in met het
    root account.

2.  Maak een nieuwe database

3.  Maak een nieuwe gebruiker

4.  Geef de nieuwe gebruiker rechten op de database

**SQL-script voor het aanmaken van een nieuwe database en gebruiker:**

``` {language="json" startFrom="1"}
create database <Databasenaam>;
create user <Gebruikersnaam> identified by <Wachtwoord>;
grant ALL on <Databasenaam> to <Gebruikersnaam>;
```

Opm: Onthoud zeker de databasenaam,gebruikersnaam en wachtwoord. Die
zijn in de volgende stappen nodig om de Glassfish te installeren.

Opm: Om de stations voor de apicalls ook lokaal te kunnen gebruiken, dient het bestandje "Stops.sql", te vinden in de map 'SQL', uitgevoerd worden.

**Stored Procedures**

De back end maakt gebruik van een aantal stored procedures, die geven allemaal een dubbele array teruggeven afhankelijk van de gevraagde waarde. De scripts zijn te vinden op: https://github.ugent.be/iii-vop2017/mobi-02/wiki/MySQL-Stored-Procedures. Andere implementaties met hetzelfde resultaat zijn uiteraard ook mogelijk.



### Installatie en configuratie van de Glassfish webcontainer

Bekijk de documentatie van Glassfish voor de initiële instalatie van de
container.

**Een nieuw domein aanmaken en starten**

Na de installatie is het nodig om een domein aan te maken en het domein te starten. Hiervoor
wordt gebruikgemaakt van de **asadmin** tool, te vinden in de bin/
folder van de Glassfish map.

**Asadmin commando’s voor het aanmaken van een domein:**

``` {language="json" startFrom="1"}
asadmin create-domain <domeinnaam>
asadmin start-domain <domeinnaam>
```

**Glassfish configureren voor communicatie met de database**

Voor het volgende deel van de configuratie wordt gebruikgemaakt van de admin
console. Dit is een grafische interface, te bereiken via de url
’https://localhost:4848’.

De configuratie van de database gebeurt via een configuratiebestand. Dit
is het bestand ’glassfish-resources.xml’.

**Fragment uit glassfish-resources.xml**

``` {language="json" startFrom="1"}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE resources PUBLIC "-//GlassFish.org//DTD GlassFish Application Server 3.1 Resource Definitions//EN" "http://glassfish.org/dtds/glassfish-resources_1_5.dtd">
<resources>
  <jdbc-resource enabled="true" jndi-name="jdbc/mobi02" object-type="user" pool-name="mobidb">
    <description/>
  </jdbc-resource>
  <jdbc-connection-pool allow-non-component-callers="false" ... wrap-jdbc-objects="false">
    <property name="URL" value="jdbc:mysql://localhost:3306/**{Databasenaam}**?zeroDateTimeBehavior=convertToNull"/>
    <property name="User" value="**{Gebruikersnaam}**"/>
    <property name="Password" value="**{Wachtwoord}**"/>
  </jdbc-connection-pool>
</resources>
```
Pas de variabelen ’Databasenaam’, ’Gebruikersnaam’ en ’Wachtwoord’ aan
naar de gegevens van de database. Vervolgens kan de webcontainer
ingesteld worden voor communicatie met de database:

1.  Glassfish heeft een connector nodig voor MySQL, die is te vinden op
    de site: ’https://dev.mysql.com/downloads/connector/j/5.1.html’

2.  Plaats de .jar in de lib-folder van het ingestelde domein. Dit is
    meestal: ’glassfish-installatie-pad\\domains\\domein-naam\\lib’

3.  Herstart de Glassfish server

4.  Vervolgens kan het aangevulde configuratiebestand,
    ’glassfish-resources.xml’, worden opgeladen via de admin console.
    Dit kan door het bestand te uploaden in de subsectie ’Resources’.
    Druk vervolgens op ’Add Resources’ en specifieer het bestand.

5.  Voer een database-ping uit ter controle. Dit kan via de subsectie
    ’JDBC’ , druk vervolgens op ’JDBC Connection Pools’ en kies de
    ’mobidb’ connection pool.

6.  Als alle voorgaande stappen correct zijn uitgevoerd, verschijnt nu
    het pingen het bericht: ’Ping Succeeded’. Is niet het geval,
    verifieer dan of stap 2 en 3 correct zijn uitgevoerd en of de juiste
    databasegebruiker is ingesteld. Dit laatste kan worden gecontroleerd
    in het tablad ’Additional Properties’ in de subsectie ’JDBC
    Connection Pools’.

**Private Certificaten**

Voor bepaalde functionaliteiten heeft de server een geldig https-certificaat nodig. Op deze pagina meer info over het importeren van een certificaat in glassfish via Letsencrypt: https://github.ugent.be/iii-vop2017/mobi-02/wiki/Certificaat-installeren-in-glassfish-(via-letsencrypt)

opm: Locatiebepaling in Chrome en Messenger integratie zal niet werken zonder geldig certificaat. Self-signed lijkt niet voldoende te zijn.

**Publieke Certificaten**

Om er zeker van te zijn dat de back end enkel communiceert met vertrouwde servers moeten de publieke certificaten worden toegevoegd aan de glassfish trust-store (cacerts.jks). In de map 'Public Certificaten' op in de root van de repository zijn de certificaten verzameld met een shellscript die de juiste imports zal uitvoeren. 


### Installatie van de applicatie

**Deployen van de back end in de Glassfish webcontainer**

De versie op github bevat apikeys en urls verkregen van stad Gent of van eigen accounts, maak deze dus niet publiek. Het is mogelijk om deze aan te passen door het .war bestand te openen via bepaalde applicaties, zoals Winrar, 7-zip, winzip..
De bestanden zijn:

 - Caching Timeouts en eventuele tijdscorrectie: \WEB-INF\classes\Api\Caching\Properties\timeouts.properties
 - API urls: \WEB-INF\lib\Mobi02_Getters.jar\Properties\config.properties
 - **Private** keys: \WEB-INF\lib\Mobi02_Getters.jar\Properties\password.properties


Het deployen van de applicatie gebeurt via de sectie ’Applications’ in de admin
console. Kies daar voor ’Deploy’ en kies vervolgens het juiste .war
bestand. Na het deployen is het mogelijk om de applicatie te starten via
hetzelfde menu.

**Configuratie en installatie van de front end**

De front end is een simpel web project. Het is echter wel nodig om het javascript
bestand: ’constants.js’ aan te passen. De variabele host moet naar het
extern ip adress van de back end wijzen. Bij een juiste configuratie van
zowel de front als back end, wordt de data nu correct
ingeladen.
Als voorbeeld (indien je ook de server lokaal draait, kan je hier localhost gebruiken):

**constants.js met back end Ip-adress 95.85.33.162:**

``` {language="json" startFrom="1"}
var host = "http://95.85.33.162:8080";
```


# Handleiding hoofdpagina
## Site algemeen 
De hoofdpagina van de site geeft standaard alle widgets weer. In elke widget is de informatie over één specifiek onderwerp terug te vinden. Dit onderwerp wordt weergegeven door het logo linksboven elk widget. Voor nog meer informatie, kan over het logo gehoverd worden met de muis, waarna een tekstuele beschrijving verschijnt. Bovendien is het ook mogelijk om widgets te verplaatsen door middel van een drag-and-drop mechanisme. De site past zichzelf continu aan en plaats alles weer in een raster, er is sprake van een responsive design.

## Widgetinstellingen 
Bij sommige widgets is er rechtsboven een icoontje voor instellingen te vinden. Door hierop te klikken ziet u de specifieke instellingen van deze widget. Zo kan bij de parkingwidget bijvoorbeeld ingesteld worden welke parkings wel of niet weergegeven worden, en kan men bijvoorbeeld bij de weerwidget de eenheden aanpassen. Alle widgetinstellingen worden opgeslagen en zullen nog steeds hetzelfde zijn indien u de site later nogmaals bezoekt.

## Algemene instellingen 
In de linkerbovenhoek van de site is ook een instellingenknop te vinden. Hier kan je de algemene dingen op de site instellen. Zo is het mogelijk om elke widget naar believen te verwijderen of weer toe te voegen. Ook kan de drag-and-drop functie uitgeschakeld (en weer ingeschakeld) worden, en kan het aantal kolommen waarin de widgets worden weergegeven ingesteld worden. Tot slot kan het thema van de site veranderd worden en kan de achtergrond gewijzigd worden. Dit instellingenpaneel kan weer gesloten worden door opnieuw op de instellingenknop de klikken, door op het kruisje rechtsboven te klikken indien de instellingenknop niet meer zichtbaar is, of door gewoon ergens naast het paneel te klikken. Alle instellingen worden opgeslagen en zullen steeds hetzelfde zijn indien u de site later opnieuw bezoekt.

## Kaartje 
Het kaartje (de widget die origineel rechtsboven staat) heeft nog wat extra functionaliteiten. Deze zijn te vinden in het dropdown-menu bovenaan de widget. Eerst en vooral kan u uw eigen locatie weergeven op de kaart. Dit doet u door het locatie-vakje aan te vinken. Indien het de eerste keer is dat u dit doet zal uw browser u om toestemming vragen om uw locatie door te geven. Dit moet u accepteren om deze functionaliteit te laten werken. Vervolgens kan veel informatie uit de andere widgets ook op de kaart weergegeven worden. Als u dan in die widget klikt op een bepaald onderdeel van de widget, zal dit onderdeel gecentreerd worden op de kaart. Tot slot kan de kaart ook vergroot worden door op het vergrootglasicoontje rechtsboven in de widget te klikken. Hierdoor wordt de kaart de breedte van het volledige scherm. Door nogmaals op dit icoontje te klikken wordt de widget weer dezelfde grootte als de andere widgets.

## Tellingen R40 
De widget met de tellingen van de R40 heeft ook wat extra functionaliteit. Deze widget staat standaard rechtsboven. Origineel wordt hier de gemiddelde telling van alle meetpunten van vandaag getoond (in het groen), ten opzichte van de gemiddelde telling van alle meetpunten in het verleden (in het rood). Om de informatie te zien van één specifiek meetpunt, kan dit meetpunt geselecteerd worden in het drop-down menu rechtsboven de widget. Ook kunnen alle meetpunten op de kaart getoond worden en kan je het weer te geven meetpunt selecteren door er gewoonweg op te klikken op de kaart. Op de grafiek wordt per uur een telling weergegeven. Je kan het precieze getal van deze telling zien door over het punt op de grafiek te hoveren. Ook is het mogelijk om één van de grafieken uit te schakelen door er in de legende op te klikken. Bovendien kan u, via de instellingen van die widget, de gegevens op de grafiek veranderen naar de gemiddelde reistijd rond de R40, zowel in wijzerzin als in tegenwijzerzin. Zowel deze data als de tellingen op de verschillende meetpunten kan weegegeven worden voor elke dag van de week, door de gewenste dag te selecteren in de instellingen. Tot slot kan ook deze widget vergroot worden door op het vergrootglasicoontje rechtsboven in de widget te klikken. Hierdoor wordt de widget 2/3 van de breedte van het volledige scherm. Door nogmaals op dit icoontje te klikken wordt de widget weer dezelfde grootte als de andere widgets.

# Gebruikershandleiding persoonlijke pagina
## Registreren en inloggen 
Rechtsboven de pagina vindt u een registratie- en loginknop. Hier kan u zich registreren als nieuwe gebruiker of inloggen in het geval u al een account hebt. Inloggen via Facebook is ook mogelijk. Eens ingelogd hebt u toegang tot uw persoonlijke pagina.

##Mijn NMBS
Hier kan u uw NMBS route plannen. Uw begin- en eindstation kan geselecteerd worden uit de lijst met stations, mogelijks door de zoekfunctie. Bij tijdstip kan u zelf een datum en tijd invullen via de kalender-widget of gewoon het huidige moment kiezen door op de NU-knop de klikken. Eens op de OK-knop geklikt is wordt een overzicht getoond van de eerstvolgende mogelijke routes, inclusief overstappen. Indien u deze route vaak gebruikt, kan deze opgeslagen worden bij favorieten. Vanuit favorieten kan u dan makkelijk uw opgeslagen routes weer inladen.

##De Lijn
Hier kan u de verschillende bussen en hun vertraging van een specifieke halte bekijken. Deze halte wordt bepaald door ofwel te zoeken in een bepaalde in te stellen radius rond uw eigen locatie, of rond een bepaald adres. Eens de haltes ingeladen zijn kan u die locatie ook instellen als favoriet en vanuit favorieten weer inladen.

##Mijn Routes
Hier kan u uw routes toevoegen. Routes zijn korte stukjes van de R40, R4 of N70. Dit toevoegen doet u door, eens op de Toevoegen-knop geklikt te hebben, een route te selecteren uit de linkertabel en deze toe te voegen aan de rechtertabel. Voor al uw toegevoegde routes kan u zien hoeveel vertraging er individueel is. Voor meer info kan u op het info-symbooltje naast elke route klikken. Bovenaan de widget ziet u dan de totale vertraging op al uw routes.

##Instellingen
Ook hier is in de linkerbovenhoek een instellingen-knop te vinden. Hierdoor opent u het instellingenvenster. Op de eerste tabpagina van dit venster kan u algemene instellingen over uw account wijzigen, zoals bijvoorbeeld uw wachtwoord, uw profielfoto of uw naam. U kan uw account hier ook verwijderen.
Op de tweede tabpagina kan u notificaties instellen. Dit is mogelijk voor elk onderdeel van Mijn Pagina. Om te zien waar u precies notificaties over zal krijgen, kan u hoveren over het vraagtegen-symbool naast elke soort notificatie. U kan de uren van deze notificaties instellen door een uur te selecteren uit de dropdown en op het plusje te drukken. Ook het type kan u selecteren door het juiste vakje aan te vinken naast elk type. Voor Messenger-notificaties moet u wel eerst toestemming geven vanop uw Facebook-account. Voor meer info hierover, hover over het vraagteken-symbool.
