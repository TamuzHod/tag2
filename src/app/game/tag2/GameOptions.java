package app.game.tag2;

import java.io.Serializable;

public class GameOptions implements Serializable {

	private static final long serialVersionUID = 6462045017534933355L;
	
	int numOfPlayers;
	int radius;
	Double centerLon;
	Double centerLat;
	int startingAmmo;
	int startingLife;
	int numOfTeams = 2;
	
}
