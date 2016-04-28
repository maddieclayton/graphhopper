package com.graphhopper.routing.safety;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColorMapParser {

    public static HashMap<Long, HashMap<Long, Integer>> parseFile(HashMap<Long, NodeInformation> nodeMap, HashMap<Long, Way> wayMap) {
        HashMap<Long, HashMap<Long, Integer>> scores = new HashMap<Long, HashMap<Long, Integer>>();
        try {
            File file = new File("colorinfo.xls");
            FileInputStream fs = new FileInputStream(file);
            Workbook wb = WorkbookFactory.create(fs);
            Sheet sheet = wb.getSheetAt(0);
            Row row;
            Cell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            for(int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                boolean found = false;
                Way way = new Way(0);
                boolean unchangedScore = false;
                long startNode = 0;
                long endNode = 0;
                if(row != null) {
                    for(int c = 0; c < 4; c++) {
                        cell = row.getCell((short)c);
                        if (c == 0) {
                        	for (long l : wayMap.keySet()) {
                        		if (cell.toString().equals(wayMap.get(l).getName())) {
                        			way = wayMap.get(l);
                        			if (way.getIsRoad()) {
                        				found = true;
                        			}
                        		}
                        	}
                        	if (!found) {
                        		//System.out.println(cell.toString());
                        	}
                        }
                        else if (c == 1) {
                        	if (cell == null) {
                        		unchangedScore = true;
                        	}
                        	else if (cell.toString().contains("End-")) {
                        		if (cell.toString().equals("End-west")) {
                        			double minLat = Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (minLat > nodeMap.get(id).getLat()) {
                        					minLat = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			startNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-east")) {
                        			double maxLat = -Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (maxLat < nodeMap.get(id).getLat()) {
                        					maxLat = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			startNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-north")) {
                        			double maxLong = -Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (maxLong < nodeMap.get(id).getLat()) {
                        					maxLong = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			startNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-south")) {
                        			double minLong = Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (minLong > nodeMap.get(id).getLat()) {
                        					minLong = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			startNode = minRef;
                        		}
                        	}
                        	else {
                        		for (long id : way.getRefs()) {
                        			for (Way way1 : nodeMap.get(id).getWays()) {
                        				if (cell.toString().equals(way1.getName())) {
                        					startNode = id;
                        				}
                        			}
                        		}
                        	}
                        }
                        else if (c == 2) {
                        	if (cell == null) {
                        		unchangedScore = true;
                        	}
                        	else if (cell.toString().contains("End-")) {
                        		if (cell.toString().equals("End-east")) {
                        			double minLat = Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (minLat > nodeMap.get(id).getLat()) {
                        					minLat = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			endNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-west")) {
                        			double maxLat = -Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (maxLat < nodeMap.get(id).getLat()) {
                        					maxLat = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			endNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-north")) {
                        			double maxLong = -Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (maxLong < nodeMap.get(id).getLat()) {
                        					maxLong = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			endNode = minRef;
                        		}
                        		else if (cell.toString().equals("End-south")) {
                        			double minLong = Double.MAX_VALUE;
                        			long minRef = 0;
                        			for (long id : way.getRefs()) {
                        				if (minLong > nodeMap.get(id).getLat()) {
                        					minLong = nodeMap.get(id).getLat();
                        					minRef = nodeMap.get(id).getId();
                        				}
                        			}
                        			endNode = minRef;
                        		}
                        	}
                        	else {
                        		for (long id : way.getRefs()) {
                        			for (Way way1 : nodeMap.get(id).getWays()) {
                        				if (cell.toString().equals(way1.getName())) {
                        					endNode = id;
                        				}
                        			}
                        		}
                        	}
                        }
                        else if (c == 3) {
                        	int score = (int) Double.parseDouble(cell.toString());
                        	if (found) {
                        		if (unchangedScore) {
                        			for (long id : way.getRefs()) {
                        				for (long id1 : way.getRefs()) {
                        					if (id != id1) {
                        						if (scores.containsKey(id)) {
                        							HashMap<Long, Integer> temp = scores.get(id);
                        							temp.put(id1, score);
                        							scores.put(id, temp);
                        						}
                        						else {
                        							HashMap<Long, Integer> temp = new HashMap<Long, Integer>();
                        							temp.put(id1, score);
                        							scores.put(id, temp);
                        						}
                        					}
                        				}
                        			}
                        		}
                        		else {
                        			boolean inPath = false;
                        			List<Long> references = new ArrayList<Long>();
                        			for (long id : way.getRefs()) {
                        				if ((id == startNode || id == endNode)) {
                        					inPath = !inPath;
                        					references.add(id);
                        				}
                        				else if (inPath) {
                        					references.add(id);
                        				}
                        			}
                        			for (long id : references) {
                        				for (long id1 : references) {
                        					if (id != id1) {
                        						if (scores.containsKey(id)) {
                        							HashMap<Long, Integer> temp = scores.get(id);
                        							temp.put(id1, score);
                        							scores.put(id, temp);
                        						}
                        						else {
                        							HashMap<Long, Integer> temp = new HashMap<Long, Integer>();
                        							temp.put(id1, score);
                        							scores.put(id, temp);
                        						}
                        					}
                        				}
                        			}
                        		}
                        	}
                        }
                    }
                }
            }
            wb.close();
        } catch(Exception ioe) {
            ioe.printStackTrace();
        }
        return scores;
    }

    public static int getSafetyWeight(long first, long second, HashMap<Long, HashMap<Long, Integer>> scores) {
        for (long l : scores.keySet()) {
            if (first == l) {
                for (long l1 : scores.get(l).keySet()) {
                    if (second == l1) {
                        return scores.get(l).get(l1);
                    }
                }
            }
        }
        return 4;
    }

    public static void main(String[] args) {
    	HashMap<Long, NodeInformation> nodeMap = new HashMap<Long, NodeInformation>();
        HashMap<Long, Way> wayMap = new HashMap<Long, Way>();
        OSMParser.parseFile(nodeMap, wayMap);
    	HashMap<Long, HashMap<Long, Integer>> scores = parseFile(nodeMap, wayMap);
        for (Long key : scores.keySet()) {
        	for (Long key1 : scores.get(key).keySet()) {
        		System.out.println(key + " " + key1 + " " + 
        			scores.get(key).get(key1));
        	}
        }
    }
}