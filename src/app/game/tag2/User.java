package app.game.tag2;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User {
	
	

	public User(String id) {
		this.id = id;
	}

	@PrimaryKey
	@Persistent
	String id;
	
	@Persistent
	int hits = 0;
	
	@Persistent
	int deaths = 0;
	
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	int getRank() {
		return hits-deaths;
	}
	
	
}
