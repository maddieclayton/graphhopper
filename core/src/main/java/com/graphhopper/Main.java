package com.graphhopper;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.GPXEntry;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

public class Main {
	public static void main(String[] args) {
		// create one GraphHopper instance
		GraphHopper hopper = new GraphHopper().forServer();
		hopper.setCHEnable(false);
		hopper.setOSMFile("mapsEdit.osm");
		hopper.setGraphHopperLocation("graphhopper/temp");
		hopper.setEncodingManager(new EncodingManager("bike"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		hopper.importOrLoad();
		// simple configuration of the request object, see the GraphHopperServlet class for more possibilities.
		GHRequest req = new GHRequest(40.35174, -74.70405, 40.35899, -74.64734).
		    setWeighting("safety").
		    setLocale(Locale.US);
		GHResponse rsp = hopper.route(req);

		// first check for errors
		if(rsp.hasErrors()) {
		   // handle them!
		   System.out.println(rsp.getErrors());
		   return;
		}

		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = rsp.getBest();

		// points, distance in meters and time in millis of the full path
		PointList pointList = path.getPoints();
		double distance = path.getDistance();
		long timeInMs = path.getTime();

		InstructionList il = path.getInstructions();
		
		// iterate over every turn instruction
		for(Instruction instruction : il) {
		   instruction.getDistance();
		   System.out.println(instruction);
		}
	}
}
