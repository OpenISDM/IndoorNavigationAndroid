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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class ListViewActivity extends AppCompatActivity implements Serializable{


    //A string list to store all the categories names
    List<String> categoryList = new ArrayList<>();

    //List of vertice for storing location data from regionData
    List<Node> listForStoringAllNodes = new ArrayList<>();
    List<Node> tmpList1 = new ArrayList<>();
    List<Node> tmpList2 = new ArrayList<>();
    List<Node> tmpList3 = new ArrayList<>();
    List<Node> sortList = new ArrayList<>();
    List<List<Node>> segment = new ArrayList<>();

    //A HashMap which has String as key and list of vertice as value to be retrieved
    HashMap<String, List<Node>> categorizedDataList = new HashMap<>();

    //RecyclerView provide a smooth sliding list for user to view location information
    private RecyclerView recyclerView;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);
        setTitle("台大雲林分院室內導航系統");
        List<Node> ReceivedList = new ArrayList<>();

        List<String> ReceivedListID = new ArrayList<String>();
        Bundle bundle = getIntent().getExtras();
        String x = bundle.getString("Category");

        //Find UI objects by IDs
        recyclerView = (RecyclerView) findViewById(R.id.displayList);

        loadLocationDatafromRegionGraph();

        //String array of size of categoryList
        String[] stringArray = new String[categoryList.size()];

        //Copy data of categoryList to stringArray
        stringArray = categoryList.toArray(stringArray);

        //Feed category list data to spinner
        ArrayAdapter<String> categoryList =
                new ArrayAdapter<String>(this, R.layout.spinner_item, stringArray);

       // Log.i("123123","comein");

        if(!x.equals("其他"))
            adapter = new RecyclerViewAdapter(this, categorizedDataList.get(x));
        else
            adapter = new RecyclerViewAdapter(this, sortList);

        //Separate every selectable item with divider line
        DividerItemDecoration divider = new
                DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));

        //RecyclerView set with an adapter with selected data list,
        //then feed the data list into UI display
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(divider);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Clear current category list in case
        //that other Region Graph is to be loaded
        DataParser.clearCategoryList();
    }

    //Load location data from Region graph
    public void loadLocationDatafromRegionGraph() {
        Log.i("xxx_List","loadLocationDatafromRegionGraph");
        //A HashMap to store region data, use region name as key to retrieve data
        RegionGraph regionGraph = DataParser.getRegionDataFromRegionGraph(this);


        //Get all category names of POI(point of interest) of the test building
        categoryList = DataParser.getCategoryList();
        int index = 0;
        //Retrieve all location information from regionData and store it as a list of vertice
        for(Region r : regionGraph.regionData.values()){
            listForStoringAllNodes.addAll(r._locationsOfRegion);
            if(index == 0)
                tmpList1.addAll(listForStoringAllNodes);
            else if (index == 1)
                tmpList2.addAll(listForStoringAllNodes);
            else if (index == 2)
                tmpList3.addAll(listForStoringAllNodes);
            index++;
        }
        for(int i = 0 ; i < tmpList2.size();i++) {
            tmpList3.removeAll(tmpList2);
        }
        for(int i = 0 ; i < tmpList1.size();i++) {
            tmpList2.removeAll(tmpList1);
        }
        sortList.addAll(tmpList3);
        sortList.addAll(tmpList2);
        sortList.addAll(tmpList1);


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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_home){
            Intent intent = new Intent();
            intent = new Intent(ListViewActivity.this, MainActivity.class);
            startActivity(intent);

            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}