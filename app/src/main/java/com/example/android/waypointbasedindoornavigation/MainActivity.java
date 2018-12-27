package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    MainActivity.java

Abstract:

    This module create the UI of start screen

Author:

    Phil Wu 01-Feb-2018

--*/

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.android.waypointbasedindoornavigation.Find_loc.DeviceParameter;
import com.example.android.waypointbasedindoornavigation.Find_loc.Find_Loc;

import org.altbeacon.beacon.BeaconManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Serializable {


    private static final long serialVersionUID = -6470574927973900913L;
    private static final int SOURCE_SEARCH_BAR = 1;
    private static final int DESTINATION_SEARCH_BAR = 2;
    private static final int UNDEFINED = -1;
    private static final int ELEVATOR = 1;
    private static final int STAIRWELL = 2;
    private static final int USER_MODE = 3;
    private static final int TESTER_MODE = 4;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    //Two search bars, one for source and one for destination
    Button searchBarForDestination;
    Button StartButton;
    //Variables to record a location's name, id and region passed from ListViewActivity
    String namePassedFromListView, IDPassedFromListView, regionPassedFromListView;

    //Define which search bar is to be filled with location information
    static boolean searchBarClicked = false;

    //Store names, IDs and Regions of source and destination
    static String destinationName, destinationID, destinationRegion;

    //PopupWindow to notify user search bar can not be blank when start a navigation tour
    private PopupWindow popupWindow;
    private LinearLayout positionOfPopup;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static File file;
    //public static File path  = new File(Environment.getExternalStorageDirectory() +
    // File.separator +"NAVIGATION_GRAPH");

    public static File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    //A string list to store all the categories names
    List<String> categoryList = new ArrayList<>();
    //A HashMap which has String as key and list of vertice as value to be retrieved
    HashMap<String, List<Node>> categorizedDataList = new HashMap<>();
    //List of vertice for storing location data from regionData
    List<Node> listForStoringAllNodes = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    List<Node> CList = new ArrayList<Node>();

    // Variables used to store waypoint infomration of a building
    List<NavigationSubgraph> navigationGraph = new ArrayList<>();
    HashMap<String, Node> allWaypointData= new HashMap<>();
    List<NavigationSubgraph> navigationGraphForAllWaypoint = new ArrayList<>();
    RegionGraph regionGraph = new RegionGraph();

    private BeaconManager beaconManager;
    private Find_Loc LBD = new Find_Loc();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private org.altbeacon.beacon.Region region;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    //UI design
    Intent intent;
    ViewPager viewPager;
    Button btn_stethoscope, btn_bill, btn_exit, btn_medicent, btn_shop, btn_wc;
    TextView tv_description;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_regulate,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.gear){
            Intent intent = new Intent();
            intent = new Intent(MainActivity.this, initSignal.class);
            startActivity(intent);
        }else if(item.getItemId() == R.id.information){
            Intent intent = new Intent();
            intent = new Intent(MainActivity.this, author_list.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("台大雲林分院室內導航系統");
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( enableIntent, REQUEST_ENABLE_BT ); }

        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1001);


        viewPager = (ViewPager)findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);



        //Get the position of popupwindow (center of phone screen)
       // positionOfPopup = (LinearLayout) findViewById(R.id.mainActivityLayout);

        //Receive location information passed from ListViewActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            namePassedFromListView = bundle.getString("name");
            IDPassedFromListView = bundle.getString("id");
            regionPassedFromListView = bundle.getString("region");
            searchBarClicked = true;
        }


        //Find UI objects by ID
        btn_stethoscope = (Button) findViewById(R.id.btn_stethoscope);
        btn_bill = (Button)findViewById(R.id.btn_bill);
        btn_exit = (Button)findViewById(R.id.btn_exit);
        btn_medicent = (Button)findViewById(R.id.btn_medicent);
        btn_shop = (Button)findViewById(R.id.btn_shop);
        btn_wc = (Button)findViewById(R.id.btn_wc);
        tv_description = (TextView)findViewById(R.id.tv_description);

        //Decide which search bar to be set value
        if(searchBarClicked == true) {
            destinationName = namePassedFromListView;
            destinationID = IDPassedFromListView;
            destinationRegion = regionPassedFromListView;
            searchBarForDestination.setText(destinationName);
            StartButton.setVisibility(View.VISIBLE);
        }

        loadLocationDatafromRegionGraph();
        List<Node> data = Collections.emptyList();
        for(int i = 0 ; i < categoryList.size() ; i++) {
            Log.i("xxx_List", "Categorylist = " + categoryList.get(i));
        }



    }
    // Switch to ListView Activity when one of the search bars is clicked
    public void switchToListView(View v){

        //Create Intent variable to switch to ListViewActivity
        Intent i = new Intent(this, ListViewActivity.class);

        //start ListViewActivity
        startActivity(i);
        finish();
    }


    //Press "Start" button to start navigation
    public void startNavigation(View view){
        //Start NavigationActivity and pass IDs and Regions of source and destination to it
            Intent i = new Intent(this, NavigationActivity.class);
            i.putExtra("destinationID", destinationID);
            i.putExtra("destinationRegion", destinationRegion);
            //Initialize values of static variables
            searchBarClicked = false;
            namePassedFromListView = null;
            IDPassedFromListView = null;
            regionPassedFromListView = null;
            destinationName = null;
            destinationID = null;
            destinationRegion = null;

            startActivity(i);
            finish();
    }


    public void exitProgram(View view){
        android.os.Process.killProcess(android.os.Process.myPid());
        Log.i("xxx", "InexitProgram");
    }

    public  void signalInit(View view){
        Intent i = new Intent(this, initSignal.class);
        startActivity(i);
    }

    public void resetSignal(View view){
        //取得目前offset設定值
        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //float offset = pref.getFloat("offset",1);

        //寫入offset初始值至內存
        double offset = 1.155;
        SharedPreferences offsetPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = offsetPref.edit();
        editor.putFloat("offset", (float) offset);
        editor.commit();

        //印出目前offset值
        AlertDialog.Builder dialogBuilder =  new AlertDialog.Builder(this);
        dialogBuilder.setMessage(String.format("%.2f ", Float.valueOf((float)offset)));
        dialogBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        //Tester MODE
        Setting.setModeValue(TESTER_MODE);

    }

    public void onClick(View view){
        switch (view.getId()) {
            case R.id.btn_stethoscope:
                for(int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if(listForStoringAllNodes.get(i)._category.equals("各科門診")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if(CList.size() == 1){
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent  i = new Intent(MainActivity.this,NavigationActivity.class);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                }else if (CList.size() > 1){
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "各科門診");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_exit:
                for(int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if(listForStoringAllNodes.get(i)._category.equals("檢查室")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if(CList.size() == 1){
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent  i = new Intent(MainActivity.this,NavigationActivity.class);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                }else if (CList.size() > 1){
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "檢查室");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_shop:
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "其他");
                    startActivity(intent);
                    finish();
                break;

            case R.id.btn_bill:
                for(int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if(listForStoringAllNodes.get(i)._category.equals("批價櫃檯")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if(CList.size() == 1){
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent  i = new Intent(MainActivity.this,NavigationActivity.class);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                }else if (CList.size() > 1){
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "批價櫃檯");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_medicent:
                for(int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if(listForStoringAllNodes.get(i)._category.equals("領藥處")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if(CList.size() == 1){
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent  i = new Intent(MainActivity.this,NavigationActivity.class);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                }else if (CList.size() > 1){
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "領藥處");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_wc:
                for(int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if(listForStoringAllNodes.get(i)._category.equals("廁所")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if(CList.size() == 1){
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent  i = new Intent(MainActivity.this,NavigationActivity.class);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                }else if (CList.size() > 1){
                    intent = new Intent(MainActivity.this,ListViewActivity.class);
                    intent.putExtra("Category", "廁所");
                    startActivity(intent);
                    finish();
                }
                break;

        }
    }

    public void loadLocationDatafromRegionGraph() {
        Log.i("xxx_List","loadLocationDatafromRegionGraph");
        //A HashMap to store region data, use region name as key to retrieve data
        RegionGraph regionGraph = DataParser.getRegionDataFromRegionGraph(this);



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

        for(int i = 0; i < listForStoringAllNodes.size(); i++){
            Log.i("xxx_List","all node = " + listForStoringAllNodes.get(i)._waypointName);
        }

    }




}
