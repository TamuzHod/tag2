package app.game.tag;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@PersistenceCapable
public class Location  {
	
	public Location(Double lat, Double lon, String playerID) {
		this.lat = lat;
		this.lon = lon;
		
		userID = playerID;
		
//		UserService userService = UserServiceFactory.getUserService();
//		userID = userService.getCurrentUser().getEmail();
		
		timeStamp = new Date();
		gameID = "game1";  // TODO: implement
//		key = userID + "_" + timeStamp.getTime();
		key = userID + "_" + gameID;
	}
	
	@PrimaryKey
	@Persistent
	public String key; 

	@Persistent
	public String userID;
	
	@Override
	public String toString() {
		return "Location [userID=" + userID + ", timeStamp=" + timeStamp
				+ ", lat=" + lat + ", lon=" + lon + "]";
	}

	@Persistent
	public Date timeStamp;
	
	@Persistent
	public Double lat;
	
	@Persistent
	public Double lon;
	
	@Persistent
	public String gameID; 

}
