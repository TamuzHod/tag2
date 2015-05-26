var map;
var marker;
var markers = [];

var lastUpdate = new Date().getTime();
var lastPos;
var currentPos;
var direction;

startOrientationWatch();
function startOrientationWatch() {
	if (window.DeviceOrientationEvent) {
		window.addEventListener("deviceorientation", deviceOrientationListener);
	} else {
		console.log("Sorry, your browser doesn't support Device Orientation");
	}


	function deviceOrientationListener(event) {
		if(event.webkitCompassHeading) {
			direction = event.webkitCompassHeading;
		}
		else
			direction =  event.alpha;
		
		document.getElementById("angle").innerHTML = "direction:" + direction;
	}
} 

function getLocation() {
    if (navigator.geolocation) {
    	navigator.geolocation.getCurrentPosition(initialize)
        navigator.geolocation.watchPosition(showPos, showError, {enableHighAccuracy:true, maximumAge:1000});
    } else { 
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
    
}





//google.maps.event.addDomListener(window, 'load', initializeLocation);
google.maps.event.addDomListener(window, 'load');

function initializeLocation() {
    if (navigator.geolocation) {
    	navigator.geolocation.getCurrentPosition(initializeMap)
        navigator.geolocation.watchPosition(showPos, showError, {enableHighAccuracy:true, maximumAge:1000});
    } else { 
        x.innerHTML = "Geolocation is not supported by this browser.";
    }
    
}

function initializeMap(position) {
	lastUpdate = new Date().getTime();
	lastPos = position;
	currentPos = position;
//	addPlayer();
	
	var x = document.getElementById("location");
    x.innerHTML="Latitude: " + position.coords.latitude + 
        "<br>Longitude: " + position.coords.longitude;	
    var myLatlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
  	
    var mapOptions = {
    	zoom: 18,
    	center: myLatlng,
    	rotateControl: true
 	}
  	map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
//
//  	marker = new google.maps.Marker({
//      	position: myLatlng,
//      	map: map,
//      	title: 'My Position'
//  	});
//  	
//  	marker.setMap(map);
  	
  	var player = {id:playerId, latitude:position.coords.latitude, longitude: position.coords.longitude};
  	updatePlayer(player)
  	updateLocation(gameId, playerId, position.coords.latitude, position.coords.longitude); 
  	
  	initilizePlayerList();
  	
}


function showPos(position) {
	
	x = document.getElementById("location");
    x.innerHTML="Latitude: " + position.coords.latitude + 
        "<br>Longitude: " + position.coords.longitude;	
    
	currentPos = position;
	var myLatlng = new google.maps.LatLng(position.coords.latitude, position.coords.longitude);
	
//	marker.position = myLatlng;
//	marker.setMap(map);
	
	var now = new Date().getTime();
	var dist = distanceInMeters(position.coords.latitude, position.coords.longitude, lastPos.coords.latitude, lastPos.coords.longitude);
	if(now - lastUpdate < 2000 || dist < 5 ) 
		return;
	
	updateLocation(gameId, playerId, position.coords.latitude, position.coords.longitude); 
	lastUpdat = now;
	lastPos = position;
}

function distanceInMeters(lat1, lon1, lat2, lon2) {
	  var R = 6371; // Radius of the earth in km
	  var dLat = (lat2-lat1).toRad();  // Javascript functions in radians
	  var dLon = (lon2-lon1).toRad(); 
	  var a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	          Math.cos(lat1.toRad()) * Math.cos(lat2.toRad()) * 
	          Math.sin(dLon/2) * Math.sin(dLon/2); 
	  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
	  var d = R * c; // Distance in km
	  return d * 1000;
}

/** Converts numeric degrees to radians */
if (typeof(Number.prototype.toRad) === "undefined") {
	Number.prototype.toRad = function() {
  	return this * Math.PI / 180;
	}
}


function showError(error) {
    switch(error.code) {
        case error.PERMISSION_DENIED:
            x.innerHTML = "User denied the request for Geolocation."
            break;
        case error.POSITION_UNAVAILABLE:
            x.innerHTML = "Location information is unavailable."
            break;
        case error.TIMEOUT:
            x.innerHTML = "The request to get user location timed out."
            break;
        case error.UNKNOWN_ERROR:
            x.innerHTML = "An unknown error occurred."
            break;
    }
}


var symbolMe = {
	    path: 'M -8,-8 8,8 M 8,-8 -8,8',
	    strokeColor: '#292',
	    strokeWeight: 8
};

var symbolOpponent = {
		path: 'M -8,0 0,-8 8,0 0,8 z',
	    strokeColor: '#000',
	    fillColor: '#F00',
	    fillOpacity: 1
};

var symbolMyTeam = {
		path: 'M -8,0 0,-8 8,0 0,8 z',
	    strokeColor: '#000',
	    fillColor: '#0F0',
	    fillOpacity: 1
};