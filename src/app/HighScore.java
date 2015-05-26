package app;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class HighScore {
	
	private HighScore() {}

	public static List<Score> getTopScores(int top) {
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 
		 Query q = pm.newQuery(Score.class);
		 //q.setFilter("lastName == lastNameParam");
		 q.setOrdering("score desc");
		 q.setRange(0, top);
		 //q.declareParameters("String lastNameParam");
		 
		 List<Score> results = null;
		 try {
			 results = (List<Score>) q.execute();

		 } finally {
		   q.closeAll();
		 }
		 	
		return results;
	}
	
	public static Boolean isHighScore(int newScore) {
		List<Score> scores = getTopScores(10);
		
		return newScore >= scores.get(scores.size() - 1).score;
	}
	
	public static void setNewHighScore(String playerName, int newScore) throws IOException, Exception {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Score score = new Score(playerName, newScore);
		pm.makePersistent(score);		
	}
	
	
	public static void main(String[] args) throws IOException, Exception {  
		  
//		for(int i=0; i<10; i++) {
//			PersistentManager.makePersistent( new Score("Player_"+i, 200 + 25*i) );
//		}
	   	
	}

}
