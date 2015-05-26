package app;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class GameRequestHandler extends HttpServlet  {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	
		PrintWriter out = resp.getWriter();

		if( req.getPathInfo().startsWith("/getTopScoreList") ) {
//    		out.println("HTTP/1.1 200 OK");
//	    	out.println("Content-Type: application/json");	 
//	    	
			resp.setStatus(200);
			resp.setContentType("application/json");
	    	StringBuffer json  = new StringBuffer();
	    	
	    	json.append("[");
	    	
	    	List<Score> topScores = HighScore.getTopScores(10);
//	    	List<Score> topScores = new ArrayList<Score>();
//	    	for(int i=0; i<10; i++) {
//	    		topScores.add(new Score("Player_" + i, 10*i));
//	    	}
	    	for(int i=0; i<topScores.size(); i++) {
	    		json.append("\t{\"name\":\""+ topScores.get(i).name + "\", \"score\":" + topScores.get(i).score + "}" + (i==topScores.size()-1?"": ","));
	    	}
	    	
	    	json.append("]");

//	    	out.println("Content-Length: " + json.length());
	    	
	    	out.println();
	    	
	    	out.println(json);
	    	
	    	out.close();
    	}
		else if( req.getPathInfo().startsWith("/isHighScore") ) {
//    		out.println("HTTP/1.1 200 OK");
//	    	out.println("Content-Type: text/plain");	 
	    	
	    	resp.setStatus(200);
			resp.setContentType("text/plain");
	    	
	    	int newScore = Integer.parseInt( req.getParameter("newScore") );
	    	out.print(HighScore.isHighScore(newScore));
//	    	out.println("true");

	    	out.close();
		}
		else if( req.getPathInfo().startsWith("/setNewHighScore") ) {
//    		out.println("HTTP/1.1 200 OK");
//	    	out.println("Content-Type: text/plain");
	    	
	    	resp.setStatus(200);
			resp.setContentType("application/json");
	    	out.println();
	    	
	    	String playerName = req.getParameter("playerName");
	    	int newScore = Integer.parseInt( req.getParameter("newScore") );
	    	try {
	    		HighScore.setNewHighScore(playerName, newScore);
			} catch (Exception e) {
				throw new IOException(e);
			}
	    	
	    	StringBuffer json  = new StringBuffer();
	    	
	    	json.append("[");
	    	
	    	List<Score> topScores = HighScore.getTopScores(10);

	    	for(int i=0; i<topScores.size(); i++) {
	    		json.append("\t{\"name\":\""+ topScores.get(i).name + "\", \"score\":" + topScores.get(i).score + "}" + (i==topScores.size()-1?"": ","));
	    	}
	    	
	    	json.append("]");
	    	
	    	out.println(json);
	    	out.close();
		}	
	}

}
