package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    Region.java

Abstract:

    This module construct an object to represent information of a region

Author:

    Phil Wu 01-Feb-2018

--*/

import java.util.List;


class Region {

    String _regionID;
    String _regionName;
    List<String> _adjacentRegions;
    List<Node> _transferNodes;
    List<Node> _locationsOfRegion;

    int _elevation;
    boolean visited;

    // constructor of Region object
    Region(String id, String name, List<String> adjacentRegions,
           List<Node> locationsOfRegion, int elevation){

        this._regionID = id;
        this._regionName = name;
        this._adjacentRegions = adjacentRegions;

        this._locationsOfRegion = locationsOfRegion;
        this._elevation = elevation;
        this.visited = false;
    }

}

