/*
 *  Licensed to GraphHopper and Peter Karich under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for 
 *  additional information regarding copyright ownership.
 * 
 *  GraphHopper licenses this file to you under the Apache License, 
 *  Version 2.0 (the "License"); you may not use this file except in 
 *  compliance with the License. You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing.util;

import java.io.IOException;
import java.util.HashMap;

import com.graphhopper.reader.OSMReader;
import com.graphhopper.routing.safety.ColorMapParser;
import com.graphhopper.routing.safety.NodeInformation;
import com.graphhopper.routing.safety.OSMParser;
import com.graphhopper.routing.safety.Way;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.util.EdgeIteratorState;

/**
 * Calculates the shortest route - independent of a vehicle as the calculation is based on the
 * distance only.
 * <p>
 * @author Peter Karich
 */
public class SafetyWeighting extends PriorityWeighting
{
	private HashMap<Long, NodeInformation> nodeMap = new HashMap<Long, NodeInformation>();
	private HashMap<Long, Way> wayMap = new HashMap<Long, Way>();
	private HashMap<Long, HashMap<Long, Integer>> scores;
	HashMap<Integer, Long> encoder;
	
    public SafetyWeighting( FlagEncoder flagEncoder, LocationIndex locationIndex )
    {
        super(flagEncoder);
        OSMParser.parseFile(nodeMap, wayMap);
        scores = ColorMapParser.parseFile(nodeMap, wayMap);
        encoder = new HashMap<Integer, Long>();
        for (long id : nodeMap.keySet()) {
        	int hashedId = locationIndex.findID(nodeMap.get(id).getLat(), nodeMap.get(id).getLong());
        	encoder.put(hashedId, id);
        }
    }

    @Override
    public double getMinWeight( double currDistToGoal )
    {
        return currDistToGoal;
    }

    @Override
    public double calcWeight( EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId )
    {
    	double weight = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);
        if (Double.isInfinite(weight))
            return Double.POSITIVE_INFINITY;
    	if (encoder.containsKey(edgeState.getEdge()) && encoder.containsKey(edgeState.getAdjNode())) {
    		long startId = encoder.get(edgeState.getEdge());
        	long endId = encoder.get(edgeState.getAdjNode());
        	if(scores.get(startId) == null || scores.get(startId).get(endId) == null);
        	else
        	{
        		double score = scores.get(startId).get(endId);
        		//return weight*score;
        		return edgeState.getDistance()*score;
        	}	
    	}
        //return weight*2.5;
    	return edgeState.getDistance();
    }

    @Override
    public String getName()
    {
        return "safest";
    }
}
