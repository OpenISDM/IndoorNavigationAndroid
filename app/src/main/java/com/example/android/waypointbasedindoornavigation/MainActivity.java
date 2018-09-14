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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.android.waypointbasedindoornavigation.Find_loc.Find_Loc;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {



    private static final int SOURCE_SEARCH_BAR = 1;
    private static final int DESTINATION_SEARCH_BAR = 2;
    private static final int UNDEFINED = -1;
    private static final int ELEVATOR = 1;
    private static final int STAIRWELL = 2;
    private static final int USER_MODE = 3;
    private static final int TESTER_MODE = 4;

    Node currentWaypoint;
    String currentWaypointID;

    //Two search bars, one for source and one for destination
    EditText searchBarForSource, searchBarForDestination;

    //Variables to record a location's name, id and region passed from ListViewActivity
    String namePassedFromListView, IDPassedFromListView, regionPassedFromListView;

    //Define which search bar is to be filled with location information
    static int whichSearchBar = UNDEFINED;

    //Used to record number of time switching between MainActivity and ListViewActivity
    static int switchTime = 0;

    //Store names, IDs and Regions of source and destination
    static String sourceName, destinationName, sourceID, destinationID, sourceRegion, destinationRegion;

    //PopupWindow to notify user search bar can not be blank when start a navigation tour
    private PopupWindow popupWindow;
    private LinearLayout positionOfPopup;


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

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1001);
        //Get the position of popupwindow (center of phone screen)
        positionOfPopup = (LinearLayout) findViewById(R.id.mainActivityLayout);

        //Receive location information passed from ListViewActivity
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            namePassedFromListView = bundle.getString("name");
            IDPassedFromListView = bundle.getString("id");
            regionPassedFromListView = bundle.getString("region");
        }

        //Find UI objects by ID
        searchBarForSource = (EditText) findViewById(R.id.start);
        searchBarForDestination = (EditText) findViewById(R.id.destination);

        loadWaypointInformation();

        //Decide which search bar to be set value
        if(whichSearchBar == SOURCE_SEARCH_BAR){
            sourceName = namePassedFromListView;
            sourceID = IDPassedFromListView;
            sourceRegion = regionPassedFromListView;
            searchBarForSource.setText(sourceName);
            switchTime++;
        }
        else if(whichSearchBar == DESTINATION_SEARCH_BAR) {
            destinationName = namePassedFromListView;
            destinationID = IDPassedFromListView;
            destinationRegion = regionPassedFromListView;
            searchBarForDestination.setText(destinationName);
            switchTime++;
        }

        //If switch time is greater or equals to 2, both search bars are set
        if(switchTime >=1){
            searchBarForSource.setText(sourceName);
            searchBarForDestination.setText(destinationName);
        }

    }

    // Switch to ListView Activity when one of the search bars is clicked
    public void switchToListView(View v){

        //Create Intent variable to switch to ListViewActivity
        Intent i = new Intent(this, ListViewActivity.class);

        //Check which EditText is clicked
        if(v.getId() == searchBarForSource.getId())
            whichSearchBar = SOURCE_SEARCH_BAR;
        if(v.getId() == searchBarForDestination.getId())
            whichSearchBar = DESTINATION_SEARCH_BAR;

        //start ListViewActivity
        startActivity(i);
        finish();
    }

    //Press "Start" button to start navigation
    public void startNavigation(View view){

        //Start NavigationActivity and pass IDs and Regions of source and destination to it
        if((sourceID == null) || (destinationID == null))
            showPopupWindow();//One of the search bar is blank, show popupwindow to notify user
        else{
           Intent i = new Intent(this, NavigationActivity.class);
            i.putExtra("sourceID", sourceID);
            i.putExtra("destinationID", destinationID);
            i.putExtra("sourceRegion", sourceRegion);
            i.putExtra("destinationRegion", destinationRegion);

            //Initialize values of static variables
            switchTime = 0;
            whichSearchBar = UNDEFINED;
            namePassedFromListView = null;
            IDPassedFromListView = null;
            regionPassedFromListView = null;
            sourceName = null;
            destinationName = null;
            sourceID = null;
            destinationID = null;
            sourceRegion = null;
            destinationRegion = null;

            startActivity(i);
            finish();
        }
    }

    //Show popupwindow to notify user one of the search bar is left blank
    public void showPopupWindow(){
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup, null);

        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        /*Button popupButton1 = (Button) customView.findViewById(R.id.popupButton1);
        Button popupButton2 = (Button) customView.findViewById(R.id.popupButton2);
        Button popupButton3 = (Button) customView.findViewById(R.id.popupButton3);*/
        TextView popupText = (TextView) customView.findViewById(R.id.popupText);

        popupText.setText("Empyt Starting Point or Destination!");
//        popupButton.setText("PK");
//        popupButton1.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                popupWindow.dismiss();
//            }
//        });
//        popupButton2.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                popupWindow.dismiss();
//            }
//        });
//        popupButton3.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                popupWindow.dismiss();
//            }
//        });
        popupWindow.showAtLocation(positionOfPopup, Gravity.CENTER, 0, 0);
    }

    // set up Lbeacon manager
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void searchStartingPoint(View view){



        Log.i("beaconManager","beaconManagerSetup");

        beaconManager =  BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-15,i:16-19,i:20-23,p:24-24"));

        //setBeaconLayout("m:2-3=0215,i:4-19,i:20-23,i:24-27,p:28-28"));
        // Detect the Eddystone main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

        // Detect the Eddystone telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

        // Detect the Eddystone URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20"));

        //beaconManager.setForegroundScanPeriod(ONE_SECOND);
        //beaconManager.setForegroundBetweenScanPeriod(2*ONE_SECOND);


        beaconManager.setForegroundScanPeriod(200);
        beaconManager.setForegroundBetweenScanPeriod(0);
        beaconManager.removeAllMonitorNotifiers();
        //beaconManager.removeAllRangeNotifiers();

        // Get the details for all the beacons we encounter.
        region = new org.altbeacon.beacon.Region("justGiveMeEverything",
                null, null, null);
        bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);

    }

    @Override
    protected void onDestroy() {
        Log.i("beaconManager","onDestroy called");
        super.onDestroy();

        if(beaconManager != null){
            beaconManager.unbind(this);
            beaconManager.removeAllRangeNotifiers();
        }
    }

    @Override
    public void onBeaconServiceConnect() {

        Log.i("beaconManager", "OnBeaconServiceConnect");
        //Start scanning for Lbeacon signal
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons,
                                                org.altbeacon.beacon.Region region) {
                Log.i("beaconManager","ranging");
                if (beacons.size() > 0) {
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    while (beaconIterator.hasNext()) {
                        Beacon beacon = beaconIterator.next();
                        logBeaconData(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new org.altbeacon.beacon.Region(
                    "myRangingUniqueId",null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    // Load Lbeacon ID
    private void logBeaconData(Beacon beacon) {


        String hexString = beacon.getId1().toString().concat(beacon.getId2().toString());

        hexString = hexString.substring(2, 26).concat(hexString.substring(28, 36));

        String uuid = hexString.toUpperCase();

        uuid = uuid.substring(0, 8) + "-"
                + uuid.substring(8, 12) + "-"
                + uuid.substring(12, 16) + "-"
                + uuid.substring(16, 20) + "-"
                + uuid.substring(20, 32);


        // currently received Lbeacon ID
        currentWaypointID = uuid;

        Log.i("beaconManager", currentWaypointID);

        // use the reveived ID to retrieve the corresponding waypoint information
        currentWaypoint = allWaypointData.get(currentWaypointID);

        sourceID = currentWaypoint._waypointID;
        sourceRegion = currentWaypoint._regionID;
        sourceName = currentWaypoint._waypointName;

        runOnUiThread(new Runnable(){
            @Override
            public void run() {

                searchBarForSource.setText(currentWaypoint._waypointName);

            }

        });

        if(beaconManager != null)
            beaconManager.unbind(this);


    }

    // Load waypoint information for comparing with the received waypoint ID
    private void loadWaypointInformation(){

        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        navigationGraphForAllWaypoint =
                DataParser.getWaypointDataFromNavigationGraph(this,
                        regionGraph.getAllRegionNames());

        for(int i=0; i<navigationGraphForAllWaypoint.size(); i++)
            allWaypointData.putAll(navigationGraphForAllWaypoint.get(i).nodesInSubgraph);
    }



    // Set preference value
    public void onPreferenceButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // check which radio button is clicked
        switch(view.getId()) {
            case R.id.p1:
                if (checked)
                    Setting.setPreferenceValue(ELEVATOR);
                    break;
            case R.id.p2:
                if (checked)
                    Setting.setPreferenceValue(STAIRWELL);
                    break;
            case R.id.p3:
                if (checked)
                    Setting.setModeValue(USER_MODE);
                break;
            case R.id.p4:
                if (checked)
                    Setting.setModeValue(TESTER_MODE);
                break;

                /*
            case R.id.p5:
                if (checked)
                    Setting.setTurnOnOK(false);
                break;
            case R.id.p6:
                if (checked)
                    Setting.setTurnOnOK(true);
                break;*/
        }
    }

}
