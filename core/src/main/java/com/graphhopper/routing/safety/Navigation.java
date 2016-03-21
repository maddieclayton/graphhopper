import java.util.HashMap;

public class Navigation {
	public static Way getClosestRoadNode(NodeInformation ni, 
			HashMap<Long, Way> wayMap, HashMap<Long, NodeInformation> nodeMap) {
		Way nodeWay = new Way(0);
		for (long id : wayMap.keySet()) {
			if (ni.getUnparsedStreet().equals(wayMap.get(id).getName())) {
				nodeWay = wayMap.get(id);
				break;
			}
		}
		return nodeWay;
	}
	
	public static void main(String[] args) {
		// Find start and end points
		String start = "Halo%20Pub%02008540";
		String end = "J%20Crew%2008540";
    	NodeInformation startPoint = EndpointParser.findEndpoint(start);
    	NodeInformation endPoint = EndpointParser.findEndpoint(end);
    	
    	// Create box
    	double topLong = Math.max(startPoint.getLong(), endPoint.getLong());
		double bottomLat = Math.min(startPoint.getLat(), endPoint.getLat());
		double bottomLong = Math.min(startPoint.getLong(), endPoint.getLong());
		double topLat = Math.max(startPoint.getLat(), endPoint.getLat());
		double distance = NodeInformation.findDistance(startPoint, endPoint);
		double maxLong = topLong + distance;
		double minLong = bottomLong - distance;
		double maxLat = topLat + distance;
		double minLat = bottomLat - distance;
		
		// Parse information in box given
		HashMap<Long, NodeInformation> nodeMap = new HashMap<Long, NodeInformation>();
    	HashMap<Long, Way> wayMap = new HashMap<Long, Way>();
    	HashMap<Long, HashMap<Long, Integer>> scores = new HashMap<Long, HashMap<Long, Integer>>();
		OSMParser.parseFile(minLong, minLat, maxLong, maxLat, nodeMap, wayMap);
		ColorMapParser.parseFile(nodeMap, wayMap, scores);
		
		Way nearestToStart = getClosestRoadNode(startPoint, wayMap, nodeMap);
		Way nearestToEnd = getClosestRoadNode(endPoint, wayMap, nodeMap);
	}
}