package app.game.tag2;

import java.util.List;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;

public class Message {
	
	public enum Type{
		PLAYER_UPDATE,
		PLAYER_QUIT,
		PLAYERS_UPDATE,
		GAME_STATUS,
		MESSAGE,
		YOU_HAVE_BEEN_HIT
	}
	
	
	public Message(Type type, Object body) {
		this.body = body;
		this.type = type;
	}
	Object body;
	
	Type type;
	
	public void send(String gameId, List<Player> players){
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		Gson gson = new Gson();
		String message = gson.toJson(this);
		for(Player p : players) {
			channelService.sendMessage(new ChannelMessage(gameId + "-" + p.id ,message));
		}
	}
	
	public void send(String gameId, String playerId){
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		Gson gson = new Gson();
		String message = gson.toJson(this);
		channelService.sendMessage(new ChannelMessage(gameId + "-" + playerId ,message));
	}
	
	
	
}
