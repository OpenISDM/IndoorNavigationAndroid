package com.example.android.waypointbasedindoornavigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

/**
 * Created by phil on 02/05/2018.
 */

class RegionGraph {


    // hashmap for storing region graph data
    HashMap<String, Region> regionData;

    // constructor of region graph
    RegionGraph(){

        this.regionData = new HashMap<>();
    }

    // get the region path, which means the travel order of region,
    //by performing shortest path algorithm on an unweighted connected graph (Region Graph)
    List<com.example.android.waypointbasedindoornavigation.Region> getRegionPath(
            String sourceRegion, String destinationRegion){

        Queue<Region> queue = new LinkedList<>();
        HashMap<String, com.example.android.waypointbasedindoornavigation.Region> path = new HashMap<>();
        queue.add(regionData.get(sourceRegion));
        path.put(regionData.get(sourceRegion)._regionName, null);
        regionData.get(sourceRegion).visited=true;

        while(!queue.isEmpty()){

            com.example.android.waypointbasedindoornavigation.Region regionNode = queue.remove();

            for(int i = 0; i<regionNode._adjacentRegions.size(); i++){

                String nameOfNeighbor = regionNode._adjacentRegions.get(i);

                if(regionData.get(nameOfNeighbor)!=null && !regionData.get(nameOfNeighbor).visited){

                    queue.add(regionData.get(nameOfNeighbor));
                    path.put(regionData.get(nameOfNeighbor)._regionName, regionNode);

                    regionData.get(nameOfNeighbor).visited = true;
                }
            }
        }

        List<Region> shortestRegionPath = new ArrayList<>();


        while(true){

            shortestRegionPath.add(regionData.get(destinationRegion));

            if(!regionData.get(destinationRegion)._regionName.equals(sourceRegion))
                destinationRegion = path.get(regionData.get(destinationRegion)._regionName)._regionName;
            else
                break;

        }

        Collections.reverse(shortestRegionPath);

        return shortestRegionPath;

    }

    public List<String> getAllRegionNames(){

        List<String> nameOfRegion = new ArrayList<>();

        for(Entry<String, Region> entry : regionData.entrySet()){

            Region region = entry.getValue();

            nameOfRegion.add(region._regionName);
        }

        return nameOfRegion;
    }

}





