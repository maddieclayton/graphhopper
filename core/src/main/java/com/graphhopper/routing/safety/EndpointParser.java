package com.graphhopper.routing.safety;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EndpointParser {

    public static NodeInformation findEndpoint(String query) {
    	String key = "01fa5850-2d3d-407f-ba51-123944197407";
    	String limit = "1";
    	try {
    		URL yahoo = new URL("https://graphhopper.com/api/1/geocode?q=" + query + "&limit=" 
    			+ limit + "&debug=true&key=" + key);
    		URLConnection yc = yahoo.openConnection();
    		BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
    		String inputLine;
	      	
	      	long nodeID = 0;
	      	double lat = 0;
	      	double lng = 0;
	      	String unparsedStreet = "";
        	while ((inputLine = in.readLine()) != null) {
        		String pattern = "osm_id\": [0-9]*,";
		    	Pattern r = Pattern.compile(pattern);
	      		Matcher m = r.matcher(inputLine);
        		if (m.find()) {
        			nodeID = Long.parseLong(m.group(0).substring(9, m.group(0).length()-1));
	      		}
        		pattern = "lng\": -?[0-9]*.[0-9]*,";
		    	r = Pattern.compile(pattern);
	      		m = r.matcher(inputLine);
        		if (m.find()) {
        			lng = Double.parseDouble(m.group(0).substring(6, m.group(0).length()-1));
	      		}
        		pattern = "lat\": -?[0-9]*.[0-9]*";
		    	r = Pattern.compile(pattern);
	      		m = r.matcher(inputLine);
        		if (m.find()) {
        			lat = Double.parseDouble(m.group(0).substring(6, m.group(0).length()));
	      		}
        		pattern = "street\": \".*\"";
		    	r = Pattern.compile(pattern);
	      		m = r.matcher(inputLine);
        		if (m.find()) {
        			unparsedStreet = m.group(0).substring(10, m.group(0).length()-1);
	      		}
	        }
        	in.close();
        	NodeInformation ni = new NodeInformation(nodeID);
        	ni.addLat(lat);
        	ni.addLong(lng);
        	ni.addUnparsedStreet(unparsedStreet);
        	return ni;
    	} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

    public static void main(String[] args) {
    	String query = "Pyne%20Hall%2008544";
    	findEndpoint(query);
    }

}