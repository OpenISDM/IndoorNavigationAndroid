package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    MainActivity.java

Abstract:

    This module create an UI of home screen

Author:

    Phil Wu 01-Feb-2018

--*/

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int SOURCE_SEARCH_BAR = 1;
    private static final int DESTINATION_SEARCH_BAR = 2;
    private static final int UNDEFINED = -1;
    private static final int ELEVATOR = 1;
    private static final int STAIRWELL = 2;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if(switchTime >=2){
            searchBarForSource.setText(sourceName);
            searchBarForDestination.setText(destinationName);
        }

    }

    //Function called by clicking one of the two search bars
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

    //Preference setting option
    public void onPreferenceButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // check which radio button is clicked
        switch(view.getId()) {
            /*case R.id.p1:
                if (checked)
                    Setting.setPreferenceValue(ELEVATOR);
                    break;
            case R.id.p2:
                if (checked)
                    Setting.setPreferenceValue(STAIRWELL);
                    break;*/
            case R.id.p3:
                if (checked)
                    Setting.setPreferenceValue(3);
                break;
            case R.id.p4:
                if (checked)
                    Setting.setPreferenceValue(4);
                break;

            case R.id.p5:
                if (checked)
                    Setting.setTurnOnOK(false);
                break;
            case R.id.p6:
                if (checked)
                    Setting.setTurnOnOK(true);
                break;
        }
    }

}
