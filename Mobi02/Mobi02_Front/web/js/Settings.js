/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//Variabelen zijn true of false, houdt in of ze enabled zijn
//worden bij eerste keer opstarten allemaal op true gezet
//deze worden ingevuld adhv local storage bij refresh
//indien local storage leeg is worden ze standaard op true gezet
//hoort via databank ipv local storage later te gebeuren
var Knoppen = ["MapBox","ParkingBox","BlueBikeBox","WeerBox","TreinenBox","WaylayBox","IncidentenBox","WazeBox","R40Box","GraphBox","GipodBox"];
var Mains = ["MapMain","ParkingsMain","BlueBikeMain","WeerMain","TreinenMain","WaylayMain","IncidentenMain","WazeMain","R40Main","GraphMain","GipodMain"];



function toggleMain(){
   
    for (var i = 0; i < Knoppen.length; i++) {
        if (document.getElementById(Knoppen[i]).checked === true) {
            localStorage.setItem(Knoppen[i], true);
            document.getElementById(Mains[i]).setAttribute("style","display:true:");
            
        }else{
           localStorage.setItem(Knoppen[i],false);
           document.getElementById(Mains[i]).setAttribute("style","display:none;");
        }
    }
    fillIn();
    makeDrag();
    setTimeout(makeDrag, 1500);
    //saveLocalStorage();
}

