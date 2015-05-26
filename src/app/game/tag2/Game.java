package app.game.tag2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;

import app.game.tag2.Player.Role;

public class Game implements Serializable{

	private static final long serialVersionUID = 447686981966243374L;
	
	public Game(String name, GameOptions options) {
		this.name = name;
		this.options = options;
		for(int i=0; i<options.numOfTeams; i++){
			teams.put(i, new ArrayList<Player>());
		}
		state = State.PENDING;
	}

	String name;
	List<Player> players = new ArrayList<Player>();
	Map<Integer, List<Player>> teams = new HashMap<Integer, List<Player>>();
	Player admin;
	GameOptions options;
	State state;
	public enum State{
		PENDING,
		IN_GAME,
		FINISHED;
	}
	
	public String joinGame(Role role, String playerId) {
		int teamNum = assignedTeam();
		Player p = new Player(playerId, role, options.startingLife, options.startingAmmo, teamNum);
		players.add(p);
		teams.get(teamNum).add(p);	
		if(role == Role.ADMIN)
			admin = p;
		return getChannelToken(playerId, name);
	}
	
	private int assignedTeam() {
		int team = 0;	
		for(int i=1; i<options.numOfTeams; i++){
			if(teams.get(i).size() < teams.get(team).size()) {
				team = i;
			}
		}
		return team;
	}
	
	private String getChannelToken(String gameName, String playerId) {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		String token = channelService.createChannel(playerId + "-" + gameName);
		return token;
	}

	public void removePlayer(String playerId) {
		Player p = getPlayerById(playerId);
		players.remove(p);
		teams.get(p.team).remove(p);
	}

	public Player getPlayerById(String playerId) {
		for(Player p : players) {
			if(p.id.equals(playerId)){
				return p;
			}
		}
		return null;
	}

	public Player UpdatePlayerLocation(String playerId, Double latitude, Double longitude) {
		Player p = getPlayerById(playerId);
		p.latitude = latitude;
		p.longitude = longitude;
		return p;
	}

	public Player fire(Player p, Double direction) {
		p.ammo--;
		if(p.ammo <= 0)
			return null;
		List<Player> enamyTeams = new ArrayList<Player>();
		for(int i : teams.keySet()) {
			if(i != p.team)
				enamyTeams.addAll(teams.get(i));
		}
		Player wasHit = whoWasHit(enamyTeams, direction);
		
		if(wasHit == null) {
			return null;
		}
		wasHit.life--;
		p.promote();

		if(wasHit.life <= 0) {
			wasHit.demote();
		}
		return wasHit;
	}

private Player whoWasHit(List<Player> enamyTeams, Double direction) {
	for(Player p : enamyTeams) {
		if(Math.random() > 0.6)
			return p;
	}
	return null;
}

	
}
