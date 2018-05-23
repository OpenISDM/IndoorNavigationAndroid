package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    Vertex.java

Abstract:

    This module construct an object represents information of a waypoint

Author:

    Phil Wu 01-Feb-2018

--*/

import java.util.List;

public class Vertex implements Comparable<Vertex> {

    String _id;
    String _name;
    double _lat;
    double _lon;
    String _region;
    String _category;
    int _nodeType;
    int _connectPointID;

    List<String> _neighbors;
    Edge[] adjacencies;
    double minDistance = Double.POSITIVE_INFINITY;
    Vertex previous;


    // constructor of Vertex object for route computation
    Vertex(String id, String name, double lat, double lon, List<String> neighbors,
           String region, String category, int nodeType, int connectPointID) {

        this._id = id;
        this._name = name;
        this._lat = lat;
        this._lon = lon;
        this._neighbors = neighbors;
        this._region = region;
        this._category = category;
        this._nodeType = nodeType;
        this._connectPointID = connectPointID;

    }

    // constructor for a Vertex object for UI display
    public Vertex(String id, String name, String region, String category) {

        this._id = id;
        this._name = name;
        this._region = region;
        this._category = category;

    }


    //Ｇet ID
    public String getID() {
        return this._id;
    }

    //Ｓet ID
    public void setID(String id) {
        this._id = id;
    }

    //Ｇet name
    public String getName() {
        return this._name;
    }


    //Ｇet _region
    public String get_region() {
        return this._region;
    }

    //Ｇet _category
    public String get_category() {
        return this._category;
    }

    //Ｇet node type
    public int get_nodeType() {
        return this._nodeType;
    }

    //set name
    public void setName(String name) {
        this._name = name;
    }

    //Ｇet X Coordinate
    public double getXCoordinate() {
        return this._lat;
    }

    //Ｓet X Coordinate
    public void setXCoordinate(int _x_coordinate) {
        this._lat = _x_coordinate;
    }

    //Ｇet Y Coordinate
    public double getYCoordinate() {
        return this._lon;
    }

    //Ｓet Y Coordinate
    public void setYCoordinate(int _y_coordinate) {
        this._lon = _y_coordinate;
    }


    @Override
    public int compareTo(Vertex other) {
        return Double.compare(minDistance, other.minDistance);
    }
}
