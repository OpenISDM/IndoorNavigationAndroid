package eos.waypointbasedindoornavigation;

/*--

Module Name:

    Node.java

Abstract:

    This module construct an object represents information of a waypoint

Author:

    Phil Wu 01-Feb-2018

--*/

import java.util.List;

public class Node implements Comparable<Node> {

    String _waypointID;
    String _waypointName;
    double _lat;
    double _lon;
    String _regionID;
    String _category;
    int _nodeType;
    int _connectPointID;
    int _groupID;
    int _mainID;
    int _elevation;

    List<String> _adjacentWaypoints;
    List<Integer> _attachIDs;
    Edge[] _edges;
    double minDistance = Double.POSITIVE_INFINITY;
    Node previous;


    // constructor of Node object for route computation
    Node(String id, String name, double lat, double lon, List<String> adjacentNode,
         String region, String category, int nodeType, int connectPointID, int groupID, int mainID, List<Integer> attachID, int elevation) {

        this._waypointID = id;
        this._waypointName = name;
        this._lat = lat;
        this._lon = lon;
        this._adjacentWaypoints = adjacentNode;
        this._regionID = region;
        this._category = category;
        this._nodeType = nodeType;
        this._connectPointID = connectPointID;
        this._groupID = groupID;
        this._mainID = mainID;
        this._attachIDs = attachID;
        this._elevation = elevation;

    }

    //Virtual Node information
    public Node(String id, double lat, double lon, int connectPointID) {
        this._waypointID = id;
        this._lat = lat;
        this._lon = lon;
        this._connectPointID = connectPointID;

    }


    // constructor for a Node object for UI display
    public Node(String id, String name, String region, String category) {

        this._waypointID = id;
        this._waypointName = name;
        this._regionID = region;
        this._category = category;

    }

    public List<String> getNeighborIDs(){

        return this._adjacentWaypoints;
    }


    //Ｇet ID
    public String getID() {
        return this._waypointID;
    }

    //Ｓet ID
    public void setID(String id) {
        this._waypointID = id;
    }

    //Ｇet name
    public String getName() {
        return this._waypointName;
    }


    //Ｇet _regionID
    public String get_regionID() {
        return this._regionID;
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
        this._waypointName = name;
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
    public int compareTo(Node other) {
        return Double.compare(minDistance, other.minDistance);
    }
}
