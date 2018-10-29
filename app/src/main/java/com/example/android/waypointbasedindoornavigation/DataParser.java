package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    DataParser.java

Abstract:

    This module contains the methods to parse data from
    Region Graph and Navigation Graph which are of XML file format.

Author:

    Phil Wu 01-Feb-2018

--*/

//testcommit3

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Integer.parseInt;


public class DataParser {

    // a list of String to store a list of category for UI display
    static List<String> categoryList = new ArrayList<>();

    public static File file;
    public static File path  =  Environment.getExternalStoragePublicDirectory
            (Environment.DIRECTORY_DOWNLOADS+File.separator+"WGRAPH_雲林台大醫院地圖");


    //Parse data from Region Graph  抓取Download裡面的資料夾將BuildingA讀取
    public static RegionGraph getRegionDataFromRegionGraph(Context context) {

        file = new File(path, "buildingA.xml");

        // a hashmap of Region, the key is the name of the Region object
        RegionGraph regionGraph = new RegionGraph();
        //HashMap<String, Region> hashMapOfRegion = new HashMap<>();

        XmlPullParser pullParser = Xml.newPullParser();

        //get XML file from asset folder
        AssetManager assetManager = context.getAssets();
    //將BulidA.xml檔裡面的資料抓出來
        try
        {
            InputStream is = new FileInputStream(file);
            //is = assetManager.open("buildingA.xml"); //read the XML file
            pullParser.setInput(is , "utf-8");
            int eventType = pullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String tag = null;

                if (eventType == XmlPullParser.START_TAG) {

                    tag = pullParser.getName();

                    if(tag.equals("region")) {

                        List<Node> listOfLoactions = new ArrayList<>();

                        String regionID = null;
                        String regionName = null;
                        List<String> neighbors = new ArrayList<>();
                        List<String> transforNodes = new ArrayList<>();
                        int elevation = 0;

                        // get the information of each Region
                        regionID = pullParser.getAttributeValue(null, "id");
                        regionName = pullParser.getAttributeValue(null, "name");
                        if (!pullParser.getAttributeValue(null,
                                "elevation").isEmpty())
                            elevation = parseInt(pullParser.getAttributeValue(null,
                                    "elevation"));


                        for(int i=0; i<pullParser.getAttributeCount(); i++){

                            String attributeName = pullParser.getAttributeName(i);

                            if(attributeName.length()>=8){

                                if(attributeName.substring(0, 8).equals("neighbor")){

                                    if(!pullParser.getAttributeValue(i).isEmpty())
                                        neighbors.add(pullParser.getAttributeValue(i));

                                }
                            }

                        }

                        // get the location information of each Region
                        while(true) {

                            if(eventType == XmlPullParser.END_TAG)
                                if(pullParser.getName().equals("region"))
                                    break;

                            if (eventType == XmlPullParser.START_TAG) {

                                if(pullParser.getName().equals("node")){

                                String id = null;
                                String name = null;
                                String region = null;
                                String category = null;

                                id = pullParser.getAttributeValue(null, "id");
                                name = pullParser.getAttributeValue(null, "name");
                                region = pullParser.getAttributeValue(null,
                                        "region");
                                category = pullParser.getAttributeValue(null,
                                        "category");


                            // Add a category to categoryList if it is a new one
                            if(!categoryExist(categoryList, category) && !category.isEmpty())
                                categoryList.add(category);

                            // add the information of a waypoint to a list belongs to a Region
                            Node node = new Node(id, name, region, category);
                            listOfLoactions.add(node);

                                }
                            }

                            eventType = pullParser.next();
                        }

                        // a Region object is created
                        Region region = new Region(regionID, regionName, neighbors, listOfLoactions ,elevation);

                        // put the Region object into hashmap with its name as the key
                        regionGraph.regionData.put(region._regionName, region);
                        //hashMapOfRegion.put(region._waypointName, region);
                    }

                }
                eventType = pullParser.next();
            }
        } catch (IOException e) {
        } catch (XmlPullParserException e) {
        }

        return regionGraph;
        //return hashMapOfRegion;
    }

    // Parse data from Navigation Graph
    public static List<NavigationSubgraph> getWaypointDataFromNavigationGraph(Context context, List<String> regionsToBeLoaded) {


        // create a list of navigation subgraph used as routing data
        List<NavigationSubgraph> routingData = new ArrayList<>();

        // load navigation subgraphs according to the regions that will be traveled
        //把list裡面的每個String抓出來,那個s = 當下的string
        for(String s : regionsToBeLoaded) {

            // create a navigationSubgraph object
            NavigationSubgraph navigationSubgraph = new NavigationSubgraph();

            XmlPullParser pullParser = Xml.newPullParser();
            AssetManager assetManager = context.getAssets();
            //要改Region1名稱檔名也需要更改
            file =  new File(path, "buildingA_"+s+".xml");

            try {
                InputStream is = new FileInputStream(file);
                pullParser.setInput(is, "utf-8");
                int eventType = pullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tag = null;
                    String id = null;
                    String name = null;
                    double lon = 0;
                    double lat = 0;
                    String region = null;
                    String category = null;
                    int nodeType = 0;
                    int connectPointID = 0;
                    int groupID = 0;
                    int elevation = 0;
                    List<String> adjacentNodes = new ArrayList<>();

                    // get complete waypoint data from navigation subgraph
                    if (eventType == XmlPullParser.START_TAG) {

                        tag = pullParser.getName();

                       if (tag.equals("node")) {




                            id = pullParser.getAttributeValue(null, "id");
                            lon = Double.parseDouble(pullParser.getAttributeValue(null,
                                    "lat"));
                            lat = Double.parseDouble(pullParser.getAttributeValue(null,
                                    "lon"));
                            name = pullParser.getAttributeValue(null, "name");
                            region = pullParser.getAttributeValue(null, "region");
                            category = pullParser.getAttributeValue(null, "category");

                           for(int i=0; i<pullParser.getAttributeCount(); i++){

                               String attributeName = pullParser.getAttributeName(i);

                               if(attributeName.length()>=8){

                                   if(attributeName.substring(0, 8).equals("neighbor")){

                                       if(!pullParser.getAttributeValue(i).isEmpty())
                                           adjacentNodes.add(pullParser.getAttributeValue(i));

                                   }
                               }

                           }


                            if (!pullParser.getAttributeValue(null, "nodeType").isEmpty())
                                nodeType = parseInt(pullParser.getAttributeValue(null,
                                        "nodeType"));

                            if (!pullParser.getAttributeValue(null, "connectPointID").isEmpty())
                                connectPointID = parseInt(pullParser.getAttributeValue(null,
                                        "connectPointID"));

                           if (!pullParser.getAttributeValue(null, "groupID").isEmpty())
                               groupID = parseInt(pullParser.getAttributeValue(null,
                                       "groupID"));

                            if (!pullParser.getAttributeValue(null, "elevation").isEmpty())
                                elevation = parseInt(pullParser.getAttributeValue(null,
                                        "elevation"));


                            // create a Node object initialized with the retrieved data
                            Node node = new Node(id, name, lon, lat, adjacentNodes, region,
                                    category, nodeType, connectPointID, groupID, elevation);

                            // put each Node object into a navigationSubgraph object
                            navigationSubgraph.nodesInSubgraph.put(id, node);

                        }
                    }
                    eventType = pullParser.next();
                }
            } catch (IOException e) {
            } catch (XmlPullParserException e) {
            }

            // add edge(s) to each waypoint in the subgraph
            navigationSubgraph.addEdges();

            // the navigation subgraph is included in the routing data
            routingData.add(navigationSubgraph);

        }

        return routingData;
    }


    // Parse data from Navigation Graph
    public static HashMap<String, String> waypointNameAndIDMappings(Context context, List<String> regionsToBeLoaded) {

        HashMap<String, String> mappingOFIDAndName = new HashMap<>();

        for(String s : regionsToBeLoaded) {

            // create a navigationSubgraph object
            NavigationSubgraph navigationSubgraph = new NavigationSubgraph();

            XmlPullParser pullParser = Xml.newPullParser();
            AssetManager assetManager = context.getAssets();

            file = new File(path, "buildingA_"+s+".xml");
            try {
                InputStream is = new FileInputStream(file);
                pullParser.setInput(is, "utf-8");
                int eventType = pullParser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    String tag = null;
                    String id = null;
                    String name = null;


                    // get complete waypoint data from navigation subgraph
                    if (eventType == XmlPullParser.START_TAG) {

                        tag = pullParser.getName();

                        if (tag.equals("node")) {

                            id = pullParser.getAttributeValue(null, "id");
                            name = pullParser.getAttributeValue(null, "name");

                            mappingOFIDAndName.put(name, id);

                        }
                    }
                    eventType = pullParser.next();
                }
            } catch (IOException e) {
            } catch (XmlPullParserException e) {
            }

        }

        return mappingOFIDAndName;
    }


    // return a list of category
    public static List<String> getCategoryList(){

        return categoryList;
    }

    // clear the category list
    public static void clearCategoryList(){

        categoryList.clear();
    }

    // check if the category is existing in the list
    public static boolean categoryExist(List<String> arr, String target){

        for (String s : arr){
            if(s.equals(target))
                return true;
        }
        return false;
    }
}
