package app.game.tag2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import app.game.tag2.Game.State;
import app.game.tag2.Message.Type;
import app.game.tag2.Player.Role;

public class GameServlet extends HttpServlet {
	Map<String, Game> games;
	
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Gson json = new Gson();
//		String playerId = UserServiceFactory.getUserService().getCurrentUser().getEmail();	
		games  = (Map<String, Game>) getCache().get("app.game.tag.games");
		if(games == null) {
			games = new HashMap<String, Game>();
		}
		if( req.getPathInfo().startsWith("/getPendingsGames") ) {
			resp.getWriter().append(json.toJson(getPendings()));
		}
		else if( req.getPathInfo().startsWith("/createGame") ) {
			String gameId = req.getParameter("gameId");
			String gameOptionsStr = req.getParameter("gameOptions");
			GameOptions gameOptions = json.fromJson(gameOptionsStr, GameOptions.class);
			Game g = new Game(gameId, gameOptions);
			games.put(gameId, g);
			resp.getWriter().append(json.toJson(getPendings()));
		}
		else if( req.getPathInfo().startsWith("/joinGame") ) {
			String playerId = req.getParameter("playerId");
			String gameId = req.getParameter("gameId");
			String token = games.get(gameId).joinGame(Role.PLAYER, playerId);
			
			//send joining player to all players
			Message m = new Message(Type.PLAYER_UPDATE, games.get(gameId).getPlayerById(playerId));
			m.send(gameId, games.get(gameId).players) ;
			
			resp.getWriter().append(token);
		}
		else if( req.getPathInfo().startsWith("/sendMessage") ) {
			String playerId = req.getParameter("playerId");
			String gameId = req.getParameter("gameId");
			String content = req.getParameter("content");
			String token = games.get(gameId).joinGame(Role.PLAYER, playerId);
			
			String[] message = content.split(":");
			if(message.length == 1) {
				Message m = new Message(Type.MESSAGE, message[0]);
				m.send(gameId, games.get(gameId).players) ;
			}
			else{
				Message m = new Message(Type.MESSAGE, message[1]);
				m.send(gameId, message[0]) ;
			}
			resp.getWriter().append(token);
		}
		else if( req.getPathInfo().startsWith("/getPlayrList") ) {
			String gameId = req.getParameter("gameId");
			resp.getWriter().append(json.toJson(games.get(gameId).players));
		}
		else if( req.getPathInfo().startsWith("/updateLocation") ) {
			String playerId = req.getParameter("playerId");
			String gameId = req.getParameter("gameId");
			Double latitude = Double.parseDouble(req.getParameter("latitude"));
			Double longitude = Double.parseDouble(req.getParameter("longitude"));
			
			Player p = games.get(gameId).UpdatePlayerLocation(playerId, latitude, longitude);

			Message m = new Message(Type.PLAYER_UPDATE, p);
			m.send(gameId, games.get(gameId).players) ;
		}
		else if( req.getPathInfo().startsWith("/fire") ) {
			String playerId = req.getParameter("playerId");
			String gameId = req.getParameter("gameId");
			Double direction = Double.parseDouble(req.getParameter("direction"));
			Player p = games.get(gameId).getPlayerById(playerId);
			Player wasHit = games.get(gameId).fire(p, direction);
			if(wasHit != null) {
				resp.getWriter().append(json.toJson(wasHit));
				List<Player> body = new ArrayList<Player>();
				body.add(p);
				body.add(wasHit);
				Message m = new Message(Type.PLAYERS_UPDATE, body);
				m.send(gameId, games.get(gameId).players);
				
				m = new Message(Type.YOU_HAVE_BEEN_HIT, p);
				m.send(gameId, wasHit.id);
			}
			else {
				resp.getWriter().append("{\"result\":\"missed\"}");
				Message m = new Message(Type.PLAYER_UPDATE, p);
				m.send(gameId, games.get(gameId).players);
			}
		}
		
		getCache().put("app.game.tag.games", games);
	}
	
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		games  = (Map<String, Game>) getCache().get("app.game.tag.games");
		
		if( req.getPathInfo().startsWith("/disconnected") ) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(req);
			String clientId = presence.clientId();
			String gameName = clientId.split("-",2)[0];
			String playerId = clientId.split("-",2)[1];
			games.get(gameName).removePlayer(playerId);	
			Message m = new Message(Type.PLAYER_QUIT, playerId);
			m.send(gameName, games.get(gameName).players) ;
		}
		else if( req.getPathInfo().startsWith("/connected") ) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(req);
			String clientId = presence.clientId();
			String gameName = clientId.split("-",2)[0];
			String playerId = clientId.split("-",2)[1];
		}
		
		getCache().put("app.game.tag.games", games);
	}
	
	
	List<Game> getPendings(){
		List<Game> pendings = new ArrayList<Game>();
		for(String key : games.keySet()){
			Game game = games.get(key);
			if(game.state == State.PENDING)
				pendings.add(game);
		}
		return pendings;
	}
	
	
	Cache getCache(){
		Cache cache = null;
	    try {
	        CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
	        cache = cacheFactory.createCache(Collections.emptyMap());  
	    } catch (CacheException e) {
	        // ...
	    }
	    return cache;
	}
}
