package app;

import java.util.UUID;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class Score {

	public Score (String name, int score) {
		this.key = UUID.randomUUID().toString();
		this.name = name;
		this.score = score;
	}
	
	@PrimaryKey
	@Persistent
	public String key; 
	
	@Persistent
	public String name;
	
	@Persistent
	public int score;

}
