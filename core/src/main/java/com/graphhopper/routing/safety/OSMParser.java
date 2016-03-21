import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OSMParser {

	public static void parseFile(double minLong, double minLat, double maxLong, double maxLat, 
		HashMap<Long, NodeInformation> nodeMap, HashMap<Long, Way> wayMap) {
		try {
    		URL yahoo = new URL("https://api.openstreetmap.org/api/0.6/map?bbox=" 
    			+ minLong + "," + minLat + "," + maxLong + "," + maxLat);
    		URLConnection yc = yahoo.openConnection();
    		BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
    		String inputLine;
	      	
        	while ((inputLine = in.readLine()) != null) {
        		// Parse nodes
        		String pattern = "<node.*>";
		    	Pattern r = Pattern.compile(pattern);
	      		Matcher m = r.matcher(inputLine);
        		if (m.find()) {
        			// Create node with correct id
        			pattern = "id=\"[0-9]*\"";
		    		r = Pattern.compile(pattern);
	      			m = r.matcher(inputLine);
        			if (m.find()) {
        				long nodeID = 
        					Long.parseLong(m.group(0).substring(4, m.group(0).length()-1));
        				NodeInformation node = new NodeInformation(nodeID);

        				// Add latitude and longitude
        				pattern = "lat=\"-?[0-9]*.[0-9]*\"";
		    			r = Pattern.compile(pattern);
	      				m = r.matcher(inputLine);
        				if (m.find()) {
        					double lat = Double.parseDouble(
        						m.group(0).substring(5, m.group(0).length()-1));
        					node.addLat(lat);
        				}
        				else {
        					System.out.println("Latitude not found for node: " + nodeID);
        				}
        				pattern = "lon=\"-?[0-9]*.[0-9]*\"";
		    			r = Pattern.compile(pattern);
	      				m = r.matcher(inputLine);
        				if (m.find()) {
        					double longitude = Double.parseDouble(
        						m.group(0).substring(5, m.group(0).length()-1));
        					node.addLong(longitude);
        				}
        				else {
        					System.out.println("Longitude not found for node: " + nodeID);
        				}

        				nodeMap.put(nodeID, node);
        			}
        			else {
        				System.out.println("ID not found for node: " + inputLine);
        			}
	      		}

        		// Parse ways
        		pattern = "<way.*>";
		    	r = Pattern.compile(pattern);
	      		m = r.matcher(inputLine);
        		if (m.find()) {
        			// make new Way with correct id
        			Way way = new Way(0);
        			pattern = "id=\"[0-9]*\"";
		    		r = Pattern.compile(pattern);
	      			m = r.matcher(inputLine);
	      			long wayID = 0;
        			if (m.find()) {
        				wayID = Long.parseLong(
        					m.group(0).substring(4, m.group(0).length()-1));
        				way = new Way(wayID);
        			}
        			while ((inputLine = in.readLine()) != null) {
        				// Check for end
        				pattern = "</way>";
        				r = Pattern.compile(pattern);
        				m = r.matcher(inputLine);
	         			if (m.find()) {
	         				break;
	         			}

	         			// Check for name
        				pattern = "\"name\"";
        				r = Pattern.compile(pattern);
        				m = r.matcher(inputLine);
	         			if (m.find()) {
	         				pattern = "v=\".*\"";
        					r = Pattern.compile(pattern);
        					m = r.matcher(inputLine);
        					if (m.find()) {
        						String name = m.group(0).substring(3, m.group(0).length()-1);
	         					way.addName(name);
	         				}
	         			}

	         			// Add references to correct nodes
	         			pattern = "ref=\"[0-9]*\"";
        				r = Pattern.compile(pattern);
        				m = r.matcher(inputLine);
	         			if (m.find()) {
        					long ref = Long.parseLong(
        						m.group(0).substring(5, m.group(0).length()-1));
        					way.addReference(ref);
        					if (nodeMap.containsKey(ref)) {
        						NodeInformation ni = nodeMap.get(ref);
        						ni.addWay(way);
        						nodeMap.put(ref, ni);
        					}
        					else {
        						System.out.println("No node found for ref: " + ref);
        					}
	         			}

	         			// Add isStreet info to ways
	         			pattern = "highway";
        				r = Pattern.compile(pattern);
        				m = r.matcher(inputLine);
        				if (m.find()) {
        					way.setIsRoad(true);
        				}

        				wayMap.put(wayID, way);
	         		}
	      		}
	        }
        	in.close();
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		HashMap<Long, NodeInformation> nodeMap = new HashMap<Long, NodeInformation>();
		HashMap<Long, Way> wayMap = new HashMap<Long, Way>();
		double maxLong = -74.65986;
		double minLat = 40.34993;
		double minLong = -74.66236;
		double maxLat = 40.35111;
		parseFile(minLong, minLat, maxLong, maxLat, nodeMap, wayMap);
		for (long l : wayMap.keySet()) {
			System.out.println(wayMap.get(l).getName() + " " + wayMap.get(l).getIsRoad());
		}
	}

}