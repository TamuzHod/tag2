var gameId = "G1";
var playerId;

var players = {};

function handlePendingGames(elementId) {
	var element = document.getElementById(elementId);
	
	var pendingsGames = getPendingsGames();
	
	element.innerHTML = JSON.stringify(pendingsGames);
}

function handleCreateGame(elementId) {
	var element = document.getElementById(elementId);
	
	var token = createGame(gameId, {
		numOfPlayers: 6,
		radius: 1000,
		centerLon: 37.354130, 
		centerLat: -122.034719,
		startingAmmo: 10,
		startingLife: 3,
		numOfTeams:2
	});
	
	element.innerHTML = token;
}

var delta = 0;
function handleUpdateLocation() {
	delta += 0.001;
	
	var latitude = currentPos.coords.latitude + delta;
	var longitude = currentPos.coords.longitude + delta
	
	var x = document.getElementById("location");
    x.innerHTML="Latitude: " + latitude + 
        "<br>Longitude: " + longitude;	
    
	var myLatlng = new google.maps.LatLng(latitude, longitude);
	
//	marker.position = myLatlng;
//	marker.setMap(map);
	
	updateLocation(gameId, playerId, latitude, longitude); 

}

function handleJoinGame(elementId) {
	var element = document.getElementById(elementId);
	while(!playerId) { 
		playerId = prompt("please enter your nick name", "");
	}
	var token = joinGame(gameId, playerId);
	
	element.innerHTML = token;
	openChannel(token);
	initializeLocation();
	
    // workaround
	var sound = document.getElementById('gotShot');
	sound.play();
	sound.pause();
}

function initilizePlayerList() {
	 var playerList = getPlayrList(gameId);
	 for(var i in playerList) {
		 var p = playerList[i];
		 updatePlayer(p);
	 }
	 refreshPlayrsList();
}

function refreshPlayrsList() {
	var list = "";
	for(var i in players) {
		list += players[i].player.id + ": team (" + players[i].player.team + ") life (" + players[i].player.life + ") ammo" +
				" (" + players[i].player.ammo + ") rank (" + players[i].player.rank + ") <br/>";
	}
	var element = document.getElementById("players-list");
	element.innerHTML = list;
}

function handleSendMessage() {
	var content;
	while(!content) { 
		content = prompt("please enter your Message.", "playerId: message bodey.");
	}
	sendMessage(gameId, playerId, content);
}

function handleFire() {
	var element = document.getElementById("action-result");
	if(!players[playerId].player.ammo) {
		element.innerHTML = "you need to relode (not yet implemented!)";
		return;
	}
	if(!direction)
		direction = 90;
	var playerWhoGotSot = fire(gameId, playerId, direction);
	if(!playerWhoGotSot) {
		element.innerHTML = "you nissed!";
	}
	else {
		element.innerHTML = "you hit:" + playerWhoGotSot.id;
	}
}

//------------  Channel -------------------------------------------

function openChannel(token) {
	var element = document.getElementById("channel-message");
	
	channel = new goog.appengine.Channel(token);
	socket = channel.open();
	
	socket.onopen = function() {
		element.innerHTML = "Channel oppend !";
	};
	
	socket.onmessage = function(messege) {
		var messege = JSON.parse(messege.data);
		 
		 switch (messege.type) {
			 case "PLAYER_UPDATE":
				 var p = messege.body;
				 updatePlayer(p);
				 break;
			 case "PLAYERS_UPDATE":
				 var playerList = messege.body;
				 for(var i in playerList) {
					 var p = playerList[i];
					 updatePlayer(p);
				 }
				 break;
			 case "PLAYER_QUIT":
				 var playerId = messege.body;
				 players[playerId].marker.setMap(null);
				 delete players[playerId];
				 
				 element.innerHTML = playerId + " quit";
				 refreshPlayrsList();
				 break;
			 case "MESSAGE":
				 element.innerHTML = messege.body;
				 break;
			 case "YOU_HAVE_BEEN_HIT":
				 element.innerHTML = "YOU HAVE BEEN HIT By: " + messege.body.id;
				 var sound = document.getElementById('gotShot');
				 sound.play();
				 break;
		 }
		 
		
	};
	
	socket.onerror = function(error) {
		element.innerHTML = error;
	};
	
	socket.onclose = function() {
		element.innerHTML = "Channel close !";
	};

}

//------------  Channel -------------------------------------------

function updatePlayer(p) {
	var element = document.getElementById("channel-message");
	 var latlng = new google.maps.LatLng(p.latitude, p.longitude);
	 if(players[p.id] == null) {	
		 if(p.id == playerId)
			 icon = symbolMe;
		 else if(p.team == players[playerId].player.team)
			 icon = symbolMyTeam;
		 else
			 icon = symbolOpponent;
		 
	  	var marker = new google.maps.Marker({
	      	position: latlng,
	      	map: map,
	      	icon: icon,
	      	title: p.id
	  	});
	  	players[p.id] = { player:p, marker: marker};
	  	marker.setMap(map);
	 }
	 else {
		 players[p.id].player = p;
		 players[p.id].marker.position = latlng;
		 players[p.id].marker.setMap(map);
	}
		 
	 //element.innerHTML = p.id + " " +p.latitude+ " " + p.longitude;
	 refreshPlayrsList();
}
