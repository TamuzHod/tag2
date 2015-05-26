package app.game.tag2;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import app.game.tag2.PMF;

public class Player implements Serializable{
	

	private static final long serialVersionUID = -2414236122467224397L;

	public Player(String playerId, Role role, int life, int ammo, int team) {
		id = playerId;
		this.role = role; 
		this.life = life;
		this.ammo = ammo;
		this.team = team;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		User user = null;
		try {
			user = pm.getObjectById(User.class, id);
		}catch (Exception e) {}
		
		if(user != null){
			rank = user.getRank();
		}
		else{
			rank = 0;
			user = new User(id);
			pm.makePersistent(user);
		}
	}
	
	void saveNewRank(int deaths, int losses) {
		
	}

	String id;
	Double latitude;
	Double longitude;
	Role role;
	int life;
	int ammo;
	int rank;
	int team;
	
	public static enum Role {
		MEDIC,
		AMM_CARIER,
		FLAG,
		ADMIN,
		PLAYER;
	}

	public void promote() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		User user = pm.getObjectById(User.class, id);
		user.setHits((user.getHits() + 1));
		pm.makePersistent(user);
		rank = user.getRank();
	}


	public void demote() {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		User user = pm.getObjectById(User.class, id);
		user.setDeaths((user.getDeaths() + 1));
		pm.makePersistent(user);		
		rank = user.getRank();
	}
	
}

