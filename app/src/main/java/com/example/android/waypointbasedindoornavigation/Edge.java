package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    Edge.java

Abstract:

    This module represent an edge of a vertex
    with given target vertex and weight

Author:

    Phil Wu 01-Feb-2018

--*/

class Edge
{
    // specify which waypoint the edge is connected to
    final Vertex target;

    // the distance of the edge
    final double weight;

    // constructor of Edge object
    Edge(Vertex targetVertex, double weightedDistanceToTarget) {
        target = targetVertex;
        weight = weightedDistanceToTarget;
    }
}

