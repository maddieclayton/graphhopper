package com.graphhopper.routing.safety;

import java.util.ArrayList;
import java.util.List;

public class NodeInformation {

	private List<Way> ways = new ArrayList<Way>();
	private String unparsedStreet = "";
	private long id = 0;
	private double wayLat = 0;
	private double wayLong = 0;
	private boolean isRoad = false;

	public NodeInformation(long nodeID) {
		id = nodeID;
	}
	
	public long getId() {
		return id;
	}
	
	public void addUnparsedStreet(String street) {
		unparsedStreet = street;
	}
	
	public String getUnparsedStreet() {
		return unparsedStreet;
	}

	public void addWay(Way way) {
		ways.add(way);
	}
	
	public List<Way> getWays() {
		return ways;
	}

	public void addLat(double wayLatitude) {
		wayLat = wayLatitude;
	}
	
	public double getLat() {
		return wayLat;
	}

	public void addLong(double wayLongitude) {
		wayLong = wayLongitude;
	}
	
	public double getLong() {
		return wayLong;
	}

	public void setIsRoad(boolean nodeIsRoad) {
		isRoad = nodeIsRoad;
	}
	
	public boolean getIsRoad() {
		return isRoad;
	}

	public static double findDistance(NodeInformation n1, NodeInformation n2) {
		double length = Math.abs(n1.wayLat - n2.wayLat);
		double width = Math.abs(n1.wayLong - n2.wayLong);
		return Math.sqrt(length*length + width*width);
	}

	public static void main(String[] args) {
		NodeInformation ni = new NodeInformation(1);
		Way way = new Way(1);
		ni.addWay(way);
		System.out.println(ni.ways);
	}
}