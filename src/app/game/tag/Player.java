package app.game.tag;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@PersistenceCapable
public class Player  {

	public Player(String playerID) {
		userID = playerID;
		gameID = "game1";
		key = userID + "_" + gameID;
	}
	
	@PrimaryKey
	@Persistent
	public String key; 
	
	@Override
	public String toString() {
		return "Player [userID=" + userID + ", gameID=" + gameID + "]";
	}

	@Persistent
	public String userID;

	@Persistent
	public String gameID; 

}
