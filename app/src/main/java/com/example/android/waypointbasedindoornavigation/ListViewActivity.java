package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    ListViewActivity.java

Abstract:

    This module create an UI for user to view all the
    location information organized in list

Author:

    Phil Wu 01-Feb-2018

--*/

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListViewActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    //A spinner at the top of ListViewActivity, showing categories of location information
    Spinner spinner;

    //A string list to store all the categories names
    List<String> categoryList = new ArrayList<>();

    //A HashMap which has String as key and list of vertice as value to be retrieved
    HashMap<String, List<Node>> categorizedDataList = new HashMap<>();

    //RecyclerView provide a smooth sliding list for user to view location information
    private RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        //Find UI objects by IDs
        spinner = (Spinner) findViewById(R.id.spinner);
        recyclerView = (RecyclerView) findViewById(R.id.displayList);

        loadLocationDatafromRegionGraph();

        //String array of size of categoryList
        String[] stringArray = new String[categoryList.size()];

        //Copy data of categoryList to stringArray
        stringArray = categoryList.toArray(stringArray);

        //Feed category list data to spinner
        ArrayAdapter<String> categoryList =
                new ArrayAdapter<String>(this, R.layout.spinner_item, stringArray);
        spinner.setAdapter(categoryList);

        //Set an OnItemSelected listener to spinner
        spinner.setOnItemSelectedListener(this);

        //Clear current category list in case
        //that other Region Graph is to be loaded
        DataParser.clearCategoryList();
    }

    //Load location data from Region graph
    public void loadLocationDatafromRegionGraph() {

        //A HashMap to store region data, use region name as key to retrieve data
        RegionGraph regionGraph = DataParser.getRegionDataFromRegionGraph(this);

        //List of vertice for storing location data from regionData
        List<Node> listForStoringAllNodes = new ArrayList<>();

        //Get all category names of POI(point of interest) of the test building
        categoryList = DataParser.getCategoryList();

        //Retrieve all location information from regionData and store it as a list of vertice
        for(Region r : regionGraph.regionData.values()){
            listForStoringAllNodes.addAll(r._locationsOfRegion);
        }

        //Categorize Vertices into data list,
        //the Vertices in the same data list have the same category
        for(int i = 0; i< categoryList.size(); i++){

            List<Node> tmpDataList = new ArrayList<>();

            for(Node v : listForStoringAllNodes){

                if(v._category.equals(categoryList.get(i)))
                tmpDataList.add(v);
            }

            categorizedDataList.put(categoryList.get(i),tmpDataList);
        }
    }


    @Override
    //Onclick listener for a category is selected on spinner
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        TextView textView = (TextView) view;
        String selectedCategory = (String) textView.getText();

        //Create an object of RecyclerViewAdapter with input of data list in same category
        adapter = new RecyclerViewAdapter(this, categorizedDataList.get(selectedCategory));

        //Separate every selectable item with divider line
        DividerItemDecoration divider = new
                DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));

        //RecyclerView set with an adapter with selected data list,
        //then feed the data list into UI display
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}