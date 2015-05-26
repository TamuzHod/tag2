package app.game.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;



import com.google.gson.Gson;

import app.PMF;


@SuppressWarnings("serial")
public class GameServlet extends HttpServlet  {
	static final Double R = 1.26;
	static PersistenceManager pm = PMF.get().getPersistenceManager();
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		if( req.getPathInfo().startsWith("/cleanGame") ) {
			claenGame();
		}
		else if( req.getPathInfo().startsWith("/addPlayer") ) {
	      	addPlayer(req, resp);
		}
		else if( req.getPathInfo().startsWith("/revealAll") ) {
			revAll(resp);
		}
		else if( req.getPathInfo().startsWith("/fire") ) {
			handleFire(req, resp);
		}
		else {
			LocationServlet locServlet = new LocationServlet();
			locServlet.doGet(req, resp);
		}

	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if( req.getPathInfo().startsWith("/disconnected") ) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(req);
			String clientId = presence.clientId();
			Player p = (Player) pm.getObjectById(Player.class, clientId);
			pm.deletePersistent(p);
			
		}
		else if( req.getPathInfo().startsWith("/connected") ) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			ChannelPresence presence = channelService.parsePresence(req);
			String clientId = presence.clientId();
		}
	}


	private void handleFire(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setStatus(200);
		resp.setContentType("application/json");
		
		
    	Double lat = Double.parseDouble( req.getParameter("lat") );
      	Double lon = Double.parseDouble( req.getParameter("lon") );
      	String playerID = req.getParameter("id");
      	Double direction = Double.parseDouble( req.getParameter("direction") );
      	final Location newLoc = new Location(lat, lon, playerID);
      	
      	List<Location> locations = getAllPlayersLocations();
      	
		Collections.sort(locations, new Comparator<Location>() {
			@Override
			public int compare(Location p1, Location p2) {
				Double diff = distTwoPoints(p1, newLoc) - distTwoPoints(p2, newLoc);
				return diff<0 ? -1 : diff==0 ? 0 : 1;
			}
		});
		
		Player gotHit = null;
		for(Location loc : locations) {
			if(loc.userID.equals(playerID) ) continue;

			if(hit(newLoc, loc, direction)) {
				gotHit = (Player) pm.getObjectById(Player.class, loc.userID + "_" + loc.gameID);
				break;
			}
		}
		
		if(gotHit == null) {
			resp.getWriter().print("{\"hit\":false}");
		}
		else {
			StringBuffer result  = new StringBuffer();
			result.append("{\"hit\":");		
			Gson g = new Gson();
			result.append(g.toJson(gotHit));
			result.append("}");
	
			resp.getWriter().println(result);
			
			result  = new StringBuffer();
			result.append("{\"hitBy\":");	
			result.append(g.toJson(playerID));
			result.append("}");
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			channelService.sendMessage(new ChannelMessage(gotHit.key, result.toString()));
		}
		
		resp.getWriter().close();
		
	}
	
	private boolean hit(Location newLoc, Location loc, Double direction) {
//		Double P1X = newLoc.lon*R*1000;
//		Double P1Y = newLoc.lat*1000;
//		
//		Double P2X = newLoc.lon*R*1000 + Math.cos(direction*Math.PI/180)*1000;
//		Double P2Y = newLoc.lat + Math.sin(direction*Math.PI/180)*1000;
//		
//		Double P0X = loc.lon*R*1000;
//		Double P0Y = loc.lat*1000;
//		
//		Double numerater = (P2Y-P1Y)*P0X - (P2X-P1X)*P0Y + P2X*P1Y - P2Y*P1X;
//		Double denominator = Math.pow(P2Y-P1Y, 2)  + Math.pow(P2X-P1X, 2);
//		
//		Double dist =  Math.abs(numerater)/Math.sqrt(denominator);
		
		return (distTwoPoints(newLoc, loc)<15);
	}


	private double distTwoPoints(Location p1, Location p2) {
		//return Math.sinh(Math.pow(p2.lat-p1.lat, 2) + Math.pow(p2.lon*R-p1.lon*R, 2));	
		Double dist = distance(p1.lat, p2.lat, p1.lon, p2.lon, 0, 0);
		return dist;
	}
	
	private double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    Double latDistance = deg2rad(lat2 - lat1);
	    Double lonDistance = deg2rad(lon2 - lon1);
	    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;
	    distance = Math.pow(distance, 2) + Math.pow(height, 2);
	    return Math.sqrt(distance);
	}

	private double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	
	

	private void revAll(HttpServletResponse resp)
			throws IOException {
		resp.setStatus(200);
		resp.setContentType("application/json");
		StringBuffer json  = new StringBuffer();
		
		List<Location> locations = getAllPlayersLocations();
		json.append("[");
		long now = (new Date()).getTime();
		for(int i=0; i<locations.size(); i++) {
			json.append("\t{" +
					"\"userID\":\""+ locations.get(i).userID + 
					"\", \"age\":" + (now - locations.get(i).timeStamp.getTime()) + 
					", \"lat\":" + locations.get(i).lat + 
					", \"lon\":" + locations.get(i).lon + "}" + 
					(i==locations.size()-1?"": ","));
		}
		
		json.append("]");
		
		resp.getWriter().println(json);
		resp.getWriter().close();
	}


	private void addPlayer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String playerID = req.getParameter("id");
		Player p = new Player(playerID);
		
		pm.makePersistent(p);
		
		resp.setStatus(200);
		resp.setContentType("text/plain");
		
		ChannelService channelService = ChannelServiceFactory.getChannelService();

		String token = channelService.createChannel(p.key);
		    
		resp.getWriter().print(token);
		resp.getWriter().close();
	}


	private void claenGame() {
		Query q = pm.newQuery(Location.class);
		q.deletePersistentAll();
		
		Query q1 = pm.newQuery(Player.class);
		q1.deletePersistentAll();
	}

	
	public static List<Location> getAllPlayersLocations() {
		
		List<Location> locations = new ArrayList<Location>();
		
		Query q = pm.newQuery(Player.class);
		List<Player> players =  (List<Player>) q.execute();
		for (Player p : players) {
			Query q2 = pm.newQuery(Location.class); 
			q2.setFilter("userID == '" + p.userID + "'");
			List<Location> temp = (List<Location>) q2.execute();
			q2.setOrdering("timeStamp desc");
			q2.setRange(0, 1);
			try {
				temp = (List<Location>) q2.execute();
				if(temp.size() < 1) continue;
				locations.add((temp.get(0)) );

			} finally {
				q2.closeAll();
			}
			 	
		} 
		return locations;
	}
	

}
