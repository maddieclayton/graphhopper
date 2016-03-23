package com.graphhopper.routing.safety;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ColorMapParser {

    public static void parseFile(HashMap<Long, NodeInformation> nodeMap, 
        HashMap<Long, Way> wayMap, HashMap<Long, HashMap<Long, Integer>> scores) {
        try {
            File file = new File("ColorInfo.xlsx");
            FileInputStream fs = new FileInputStream(file);
            XSSFWorkbook wb = new XSSFWorkbook(fs);
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;

            int rows; // No of rows
            rows = sheet.getPhysicalNumberOfRows();

            int cols = 4; // No of columns

            for(int r = 0; r < rows; r++) {
                row = sheet.getRow(r);
                boolean found = false;
                Way way = new Way(0);
                boolean unchangedScore = false;
                long startNode = 0;
                long endNode = 0;
                if(row != null) {
                    for(int c = 0; c < cols; c++) {
                        cell = row.getCell((short)c);
                        if (cell == null) {
                        	break;
                        }
                        if (c == 0) {
                        	for (long l : wayMap.keySet()) {
                        		if (cell.toString().equals(wayMap.get(l).getName())) {
                        			way = wayMap.get(l);
                        			if (way.getIsRoad()) {
                        				found = true;
                        			}
                        		}
                        	}
                        }
                        if (c == 1) {
                        	if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                        		unchangedScore = true;
                        	}
                        	else {
                        		for (long id : way.getRefs()) {
                        			for (Way way1 : nodeMap.get(id).getWays()) {
                        				if (cell.toString().equals(way1.getName())) {
                        					startNode = way1.getWayId();
                        				}
                        			}
                        		}
                        	}
                        }
                        if (c == 2) {
                        	if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
                        		unchangedScore = true;
                        	}
                        	else {
                        		for (long id : way.getRefs()) {
                        			for (Way way1 : nodeMap.get(id).getWays()) {
                        				if (cell.toString().equals(way1.getName())) {
                        					endNode = way1.getWayId();
                        				}
                        			}
                        		}
                        	}
                        }
                        if (c == 3) {
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
        HashMap<Long, HashMap<Long, Integer>> scores = new HashMap<Long, HashMap<Long, Integer>>();
        double maxLong = -74.65986;
        double minLat = 40.34993;
        double minLong = -74.66236;
        double maxLat = 40.35111;
        OSMParser.parseFile(minLong, minLat, maxLong, maxLat, nodeMap, wayMap);
        parseFile(nodeMap, wayMap, scores);
    	System.out.println(getSafetyWeight(103994789, 104040288, scores));
    }
}