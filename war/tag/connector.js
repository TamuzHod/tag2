function getPendingsGames() {
	var http = new XMLHttpRequest();
	http.open( "GET", "/tag2/getPendingsGames", false );
	http.send( null );
	
	var pendingsGames = JSON.parse(http.responseText);
	
	return pendingsGames;
}

function createGame(gameId, gameOptions) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/createGame?gameId=" + gameId + "&gameOptions=" + JSON.stringify(gameOptions), false );
	http.send( null );
	
	var token = http.responseText;
	
	return token;
}

function joinGame(gameId, playerId) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/joinGame?gameId=" + gameId + "&playerId=" + playerId, false );
	http.send( null );
	
	var token = http.responseText;
	
	return token;
}

function sendMessage(gameId, playerId, content) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/sendMessage?gameId=" + gameId + "&playerId=" + playerId + "&content=" + content, false );
	http.send( null );
}

function getPlayrList(gameId) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/getPlayrList?gameId=" + gameId, false );
	http.send( null );
	
	var playerList = JSON.parse(http.responseText);
	
	return playerList;
}


function updateLocation(gameId, playerId, latitude, longitude) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/updateLocation?gameId=" + gameId  + "&playerId=" + playerId + "&latitude=" + latitude + "&longitude=" + longitude, false );
	http.send( null );
}

function fire(gameId, playerId, direction) {
	var http = new XMLHttpRequest();
	
	http.open( "GET", "/tag2/fire?gameId=" + gameId  + "&playerId=" + playerId + "&direction=" + direction, false );
	http.send( null );
	
	var playerWhoGotSot = JSON.parse(http.responseText);
	if(playerWhoGotSot.id)
		return playerWhoGotSot;
	else
		return null;
}










