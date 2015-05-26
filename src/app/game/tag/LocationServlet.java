package app.game.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;


import com.google.gson.Gson;

import app.PMF;

@SuppressWarnings("serial")
public class LocationServlet extends HttpServlet  {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		if( req.getPathInfo().startsWith("/updateLocation") ) {
	    	Double lat = Double.parseDouble( req.getParameter("lat") );
	      	Double lon = Double.parseDouble( req.getParameter("lon") );
	      	String playerID = req.getParameter("id");
	      	Location newLoc = new Location(lat, lon, playerID);
			PersistenceManager pm = PMF.get().getPersistenceManager();
			pm.makePersistent(newLoc);
			
			List<Location> newLocs = new ArrayList<Location>();
			newLocs.add(newLoc);
			sendPlayersLocationToAllOtherUsers(newLocs);
			
			resp.setStatus(200);
			resp.setContentType("text/plain");
			resp.getWriter().print("Update last Location at: " + newLoc.timeStamp + " for user: " + newLoc.userID);
			resp.getWriter().close();
		}

	}
	
	private void sendPlayersLocationToAllOtherUsers(List<Location> newLoc) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
	    ChannelService channelService = ChannelServiceFactory.getChannelService();
	    Query q = pm.newQuery(Player.class);
	    List<Player> players =  (List<Player>) q.execute();

	    Gson json = new Gson();
	    String locJson = json.toJson(newLoc);
		for (Player p : players) {
			if(newLoc.size() == 1 && p.userID.equals(newLoc.get(0).userID))
				continue;	
			channelService.sendMessage(new ChannelMessage(p.key, locJson));
		}
	      
	}
	
	

}


