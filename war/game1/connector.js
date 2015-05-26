function getTopScoreList(callback) {
    http = new XMLHttpRequest();
    
    http.onreadystatechange = function() {
	    if (http.readyState==4 && http.status==200){
	    	var records = JSON.parse(http.responseText);
	        callback( records.sort(comp) );
	    }
    }
    
    http.open( "GET", "/api/getTopScoreList", true );
    http.send( null );
} 


function setNewHighScore(newScore) {
    http = new XMLHttpRequest();
//    http.open( "GET", "/api/setNewHighScore?newScore=" + newScore +
//    		"&newName=" + playerName + "&passowerd=" + passowerd , false );
    http.open( "GET", "/api/setNewHighScore?newScore=" + newScore +
    		"&newName=" + playerName, false );
    http.send( null );
//    if(http.status == 401) {
//    	registerAndLogin();
//    	return setNewHighScore(newScore);
//    }
	var records = JSON.parse(http.responseText);
	return records;
}

function registerAndLogin() {
	if (confirm('Are you a new user')) {
		playerName = prompt("please create user \n enter name", "");
		passowerd = prompt( "enter passowerd", "");
		http = new XMLHttpRequest();
	    http.open( "GET", "/api/setNewUser?newName=" + playerName + "&passowerd=" + passowerd , false );
	    http.send( null );
	    var response = http.responseText; 
	    if(response == "usere name alredy taken") {
	    	confirm('usere name alredy taken');
	    	registerAndLogin();
	    }
   	
	} else {
		playerName = prompt("please Login \n enter name", "");
		passowerd = prompt( "enter passowerd", "");
	}
}

function comp(a, b){
	return b.score - a.score;
	}

function isHighScore(newScore) {
	http = new XMLHttpRequest();
	http.open( "GET", "/api/isHighScore?newScore=" + newScore, false );
	http.send( null );
	
	return http.responseText;
}
