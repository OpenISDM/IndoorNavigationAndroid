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

    String _id;
    String _name;
    List<String> _neighbors;
    List<Vertex> _locationsOfRegion;
    int _elevation;
    boolean visited;

    // constructor of Region object
    Region(String name, List<String> neighbors,
           List<Vertex> locationsOfRegion, int elevation){

        this._name = name;
        this._neighbors = neighbors;
        this._locationsOfRegion = locationsOfRegion;
        this._elevation = elevation;
        this.visited = false;
    }

}

