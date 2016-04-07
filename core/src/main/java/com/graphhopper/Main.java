package com.graphhopper;

import java.util.List;
import java.util.Map;

import com.graphhopper.routing.AlgorithmOptions;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

public class Main {
	public static void main(String[] args) {
		GraphHopper hopper = new GraphHopper().forServer();
		hopper.setCHEnable(false);
		hopper.setOSMFile("maps.osm");
		hopper.setGraphHopperLocation("graphhopper/temp");
		hopper.setEncodingManager(new EncodingManager("bike"));

		hopper.importOrLoad();

		GHRequest req = new GHRequest(40.3524738, -74.6511219, 40.3884395, -74.6551584).
		    setVehicle("bike").setAlgorithm(AlgorithmOptions.ASTAR);
		GHResponse res = hopper.route(req);
		
		// use the best path, see the GHResponse class for more possibilities.
		PathWrapper path = res.getBest();

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

		// or get the json
		List<Map<String, Object>> iList = il.createJson();

	}
}
