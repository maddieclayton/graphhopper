package com.graphhopper.routing.safety;

import java.util.ArrayList;
import java.util.List;

public class Way{

	private List<Long> references = new ArrayList<Long>();
	private String name = "Untitled Road";
	private long id = 0;
	private boolean isRoad = false;

	public Way(long wayId) {
		id = wayId;
	}
	
	public long getWayId() {
		return id;
	}

	public void addReference(long refId) {
		references.add(refId);
	}
	
	public void addRefs(List<Long> refs) {
		references = refs;
	}
	
	public List<Long> getRefs() {
		return references;
	}

	public void addName(String wayName) {
		name = wayName;
	}

	public String getName() {
		return name;
	}

	public void setIsRoad(boolean wayIsRoad) {
		isRoad = wayIsRoad;
	}

	public boolean getIsRoad() {
		return isRoad;
	}
}