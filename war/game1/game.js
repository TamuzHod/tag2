var life = 10;
var score = 0;
var inte1;
var inte2;
var playerName = null;
var passowerd = null;


function goLeft() {
	var posLeft = document.getElementById("platform").style.marginLeft;
	posLeft = posLeft.substring(0, posLeft.indexOf('px'));
	if (posLeft > 0 && posLeft < 550) {
		posLeft = parseInt(posLeft) - 7;
		document.getElementById("platform").style.marginLeft = posLeft + "px";
	}
}

function goRight() {
	var posLeft = document.getElementById("platform").style.marginLeft;
	posLeft = posLeft.substring(0, posLeft.indexOf('px'));
	if (posLeft > -10 && posLeft < 545) {
		posLeft = parseInt(posLeft) + 7;
		document.getElementById("platform").style.marginLeft = posLeft + "px";
	}
}

function makeEnemis(difucolty) {
		var rock = document.createElement('div');
		rock.id = "rock_";
		rock.className = "rock";
		var rand = Math.random() * 600;
		rock.style.top = "66px";
		rock.style.left = (rand)+"px";
		if(difucolty == 3)
			rock.style.background = "black";
		else if(difucolty == 2)
			rock.style.background = "blue";
		else
			rock.style.background = "pink";
		document.getElementById("gameBored").appendChild(rock);
}

function enomyMovements(difucolty) {
    var array = document.getElementsByClassName("rock");
	for(var i=0; i<array.length; i++) {
		var height = array[i].style.top;
		height = height.substring(0, height.indexOf('px'));
		height = parseInt(height) + 1;
		if(height >= 600) {
			array[i].remove();
			var scoreOutpot = document.getElementById("score");
			scoreOutpot.innerText = score+= difucolty;
			continue;
		}
		array[i].style.top = height + "px";
		colisionDetectorDestroysObjectOne(array[i], document.getElementById("platform"));
	}
}


function colisionDetector(obj1, obj2) {
	var height1 = obj1.clientHeight;
	var width1 = obj1.clientWidth;
	var left1 = obj1.offsetLeft;
	var top1 = obj1.offsetTop;
	
	var height2 = obj2.clientHeight;
	var width2 = obj2.clientWidth;
	var left2 = obj2.offsetLeft;
	var top2 = obj2.offsetTop;
	
	
	if( (top1 < height2+top2) && (top2 < height1+top1) && (left1 < left2+width2) && (left2 < left1+width1) ){
		return true;
	}
}

function colisionDetectorDestroysObjectOne(obj1, obj2) {
	if (colisionDetector(obj1, obj2)) {
		obj1.remove();
		var lifeOutpot = document.getElementById("life");
		lifeOutpot.innerText = life--;
		if(life < 0) {
			clearInterval(inte1);
			clearInterval(inte2);
			document.getElementById("lifeH").innerText = "Game Over";
			document.getElementById("scoreH").innerText = "Your final Score is: ";
			document.getElementById("endOfGame").style.display = "inline";
			document.getElementById("endOfGame").addEventListener("click", endOfGame);
			document.getElementById("pause").innerText = "play";
			var array = document.getElementsByClassName("rock");
						
			for(var i=0; i<array.length;) {
				array[i].remove();
			}
			var ishighScore = isHighScore(score);
			if(ishighScore == 'true') {
				alert("New High Score");
				var scoreList = setNewHighScore(score);
				renderScorList(scoreList);
			}
		}
	}

}

function renderScorList(scoreList) {
	for(var i=0; i<10; i++) {
		var outpot = document.getElementById(i);
		outpot.innerText = scoreList[i] && (scoreList[i].name + ": " + scoreList[i].score) || "";
 	}
}

function endOfGame() {
	score = 0;
	life = 10;
	document.getElementById("lifeH").innerText = "Life : ";
	document.getElementById("scoreH").innerHTML = 'score : ';
	document.getElementById("endOfGame").style.display = "none";
	document.getElementById("score").innerText = score;
	document.getElementById("life").innerText = life;
	
	
	pausePlay()
}

function keyPresed(e) {
	var keyNum = e.keyCode;
	if(keyNum == 37)
		goLeft();
	else if(keyNum == 39)
		goRight();
}

function pausePlay() {
	if(document.getElementById("pause").innerText == "play"){
		document.getElementById("pause").innerText = "pause";
		if(document.getElementById("img") != null)
			document.getElementById("img").remove();
		makeEnemis();
		nourmal();
	}
	else {
		clearInterval(inte1);
		clearInterval(inte2);
		document.getElementById("pause").innerText = "play";
	}
}

function easy() {
	clearInterval(inte1);
	clearInterval(inte2);
	inte1 = setInterval(function(){ makeEnemis(1) }, 500);
	inte2 = setInterval(function(){ enomyMovements(1) }, 10);
}
function nourmal() {
	clearInterval(inte1);
	clearInterval(inte2);
	inte1 = setInterval(function(){ makeEnemis(2) }, 300);
	inte2 = setInterval(function(){ enomyMovements(2) }, 8);
}
function hard() {
	clearInterval(inte1);
	clearInterval(inte2);
	inte1 = setInterval(function(){ makeEnemis(3) }, 100);
	inte2 = setInterval(function(){ enomyMovements(3) }, 4);
}

function init() {
	document.getElementById("pause").addEventListener("click", pausePlay);
	document.getElementById("easy").addEventListener("click", easy);
	document.getElementById("nourmal").addEventListener("click", nourmal);
	document.getElementById("hard").addEventListener("click", hard);
	document.getElementById("body").addEventListener("keydown", keyPresed);
	playerName = prompt("please create user \n enter name", "");
	getTopScoreList( function(scoreList) {
		renderScorList(scoreList);
	});

}