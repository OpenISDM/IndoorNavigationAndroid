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


class NavigationSubgraph {

    // hash map for storing all waypoint data of a navigation subgraph
    HashMap<String, Node> nodesInSubgraph;

    // Constructor
    NavigationSubgraph(){

        this.nodesInSubgraph = new HashMap<>();
    }

    // all vertices are added with edge(s) to link their neighbors
    void addEdges() {

        //For-loop retrieves all Vertices from HashMap
        for (Entry<String, Node> entry : nodesInSubgraph.entrySet()) {

            Node node = entry.getValue();

            // an ArrayList for storing Edge(s) for a Node object
            List<Edge> listOfEdge = new ArrayList<>();

            for(int i = 0; i< node._adjacentWaypoints.size(); i++){

                //Initialize an Edge object to represent a connection to a neighbor
                Edge e = new Edge(nodesInSubgraph.get(node._adjacentWaypoints.get(i)),
                        GeoCalulation.getDistance(node,
                                nodesInSubgraph.get(node._adjacentWaypoints.get(i))));

                listOfEdge.add(e);
            }

            // convert ArrayList into array
            node._edges = listOfEdge.toArray(new Edge[0]);
        }
    }
}

