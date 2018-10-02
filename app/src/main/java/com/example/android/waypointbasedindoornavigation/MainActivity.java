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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends AppCompatActivity {



    private static final int SOURCE_SEARCH_BAR = 1;
    private static final int DESTINATION_SEARCH_BAR = 2;
    private static final int UNDEFINED = -1;
    private static final int ELEVATOR = 1;
    private static final int STAIRWELL = 2;
    private static final int USER_MODE = 3;
    private static final int TESTER_MODE = 4;


    //Two search bars, one for source and one for destination
    EditText searchBarForDestination;

    Button selectGraph;

    //Variables to record a location's name, id and region passed from ListViewActivity
    String namePassedFromListView, IDPassedFromListView, regionPassedFromListView;

    //Define which search bar is to be filled with location information
    static boolean searchBarClicked = false;

    //Store names, IDs and Regions of source and destination
    static String destinationName, destinationID, destinationRegion;

    //PopupWindow to notify user search bar can not be blank when start a navigation tour
    private PopupWindow popupWindow;
    private LinearLayout positionOfPopup;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public static File file;
    //public static File path  = new File(Environment.getExternalStorageDirectory() +
           // File.separator +"NAVIGATION_GRAPH");

    public static File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

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
            searchBarClicked = true;
        }

            //if(!path.exists()) path.mkdir();

        //file_download("https://goo.gl/op2w9N");


        //Find UI objects by ID
        searchBarForDestination = (EditText) findViewById(R.id.destination);
        selectGraph = (Button) findViewById(R.id.select_navigation_graph);


        //Decide which search bar to be set value
       if(searchBarClicked == true) {
            destinationName = namePassedFromListView;
            destinationID = IDPassedFromListView;
            destinationRegion = regionPassedFromListView;
            searchBarForDestination.setText(destinationName);
        }

        //If switch time is greater or equals to 2, both search bars are set
        if(searchBarClicked == true)
            searchBarForDestination.setText(destinationName);


    }

    // Switch to ListView Activity when one of the search bars is clicked
    public void switchToListView(View v){

        //Create Intent variable to switch to ListViewActivity
        Intent i = new Intent(this, ListViewActivity.class);

        if(v.getId()==searchBarForDestination.getId())
            i.putExtra("type", "POI");
        else
            i.putExtra("type", "GN");

        //start ListViewActivity
        startActivity(i);
        finish();
    }


    //Press "Start" button to start navigation
    public void startNavigation(View view){

        //Start NavigationActivity and pass IDs and Regions of source and destination to it
        if(destinationID == null)
            showPopupWindow();//One of the search bar is blank, show popupwindow to notify user
        else{
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
    }

    //Show popupwindow to notify user one of the search bar is left blank
    public void showPopupWindow(){

        popupWindow.setFocusable(true);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup, null);

        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


        final Button popupButton = (Button) customView.findViewById(R.id.popupButton);
        TextView popupText = (TextView) customView.findViewById(R.id.popupText);


        popupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });

        popupText.setText("Destination can not be empty!");

        popupWindow.showAtLocation(positionOfPopup, Gravity.CENTER, 0, 0);
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


    public void startDownload(View view){

//        popupWindow.setFocusable(true);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup, null);

        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);


        Button popupButton = (Button) customView.findViewById(R.id.popupButton);
        TextView popupText = (TextView) customView.findViewById(R.id.popupText);


        popupButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String url = "https://drive.google.com/drive/u/2/folders/19_TvCwGeaCvOURAYRMg5OS7jGr7bzgOn";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                popupWindow.dismiss();
            }
        });

        popupText.setText("按確認前往下載地圖，並於下載完畢後解壓縮");

        popupWindow.showAtLocation(positionOfPopup, Gravity.CENTER, 0, 0);

    }


}
