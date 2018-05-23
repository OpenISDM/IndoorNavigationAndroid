package com.example.android.waypointbasedindoornavigation;


/*--

Module Name:

    NavigationSubgraph.java

Abstract:

    This module construct an object to represent information of a navigation subgraph

Author:

    Phil Wu 01-Feb-2018

--*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


public class NavigationSubgraph {

    // hash map for storing all waypoint data of a navigation subgraph
    HashMap<String, Vertex> verticesInSubgraph;

    // Constructor
    public NavigationSubgraph(){

        this.verticesInSubgraph = new HashMap<>();
    }

    // all vertices are added with edge(s) to link their neighbors
    public void addEdges() {

        //For-loop retrieves all Vertices from HashMap
        for (Entry<String, Vertex> entry : verticesInSubgraph.entrySet()) {

            Vertex vertex = entry.getValue();

            // an ArrayList for storing Edge(s) for a Vertex object
            List<Edge> listOfEdge = new ArrayList<>();

            for(int i=0; i<vertex._neighbors.size(); i++){

                //Initialize an Edge object to represent a connection to a neighbor
                Edge e = new Edge(verticesInSubgraph.get(vertex._neighbors.get(i)),
                        GeoCalulation.getDistance(vertex,
                                verticesInSubgraph.get(vertex._neighbors.get(i))));

                listOfEdge.add(e);
            }

            // convert ArrayList into array
            vertex.adjacencies = listOfEdge.toArray(new Edge[0]);
        }
    }
}

