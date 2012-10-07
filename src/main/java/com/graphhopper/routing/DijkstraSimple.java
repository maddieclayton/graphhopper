/*
 *  Copyright 2012 Peter Karich 
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.routing;

import com.graphhopper.storage.EdgeEntry;
import com.graphhopper.storage.Graph;
import com.graphhopper.util.EdgeIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.util.PriorityQueue;

/**
 * @author Peter Karich,
 */
public class DijkstraSimple extends AbstractRoutingAlgorithm {

    protected TIntHashSet visited = new TIntHashSet();
    private TIntObjectMap<EdgeEntry> map = new TIntObjectHashMap<EdgeEntry>();
    private PriorityQueue<EdgeEntry> heap = new PriorityQueue<EdgeEntry>();

    public DijkstraSimple(Graph graph) {
        super(graph);
    }

    @Override
    public DijkstraSimple clear() {
        visited.clear();
        map.clear();
        heap.clear();
        return this;
    }

    @Override public Path calcPath(int from, int to) {
        EdgeEntry fromEntry = new EdgeEntry(from, 0);
        EdgeEntry currEdge = fromEntry;
        while (true) {
            int neighborNode = currEdge.node;
            EdgeIterator iter = graph.getOutgoing(neighborNode);
            while (iter.next()) {
                int tmpV = iter.node();
                if (visited.contains(tmpV))
                    continue;

                double tmpWeight = weightCalc.getWeight(iter) + currEdge.weight;
                EdgeEntry nEdge = map.get(tmpV);
                if (nEdge == null) {
                    nEdge = new EdgeEntry(tmpV, tmpWeight);
                    nEdge.prevEntry = currEdge;
                    map.put(tmpV, nEdge);
                    heap.add(nEdge);
                } else if (nEdge.weight > tmpWeight) {
                    heap.remove(nEdge);
                    nEdge.weight = tmpWeight;
                    nEdge.prevEntry = currEdge;
                    heap.add(nEdge);
                }

                updateShortest(nEdge, neighborNode);
            }
            if (finished(currEdge, to))
                break;

            visited.add(neighborNode);
            currEdge = heap.poll();
            if (currEdge == null)
                return null;
        }

        if (currEdge.node != to)
            return null;

        // extract path from shortest-path-tree
        Path path = new Path(weightCalc);
        while (currEdge.node != from) {
            int tmpFrom = currEdge.node;
            path.add(tmpFrom);
            currEdge = currEdge.prevEntry;
            path.calcWeight(graph.getIncoming(tmpFrom), currEdge.node);
        }
        path.add(fromEntry.node);
        path.reverseOrder();
        return path;
    }

    public boolean finished(EdgeEntry curr, int to) {
        return curr.node == to;
    }
}