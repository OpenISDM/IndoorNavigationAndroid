package eos.waypointbasedindoornavigation;

/*--

Module Name:

    NavigationActivity.java

Abstract:

    This module works as follow:

    1. Provides a UI for navigational guidance

    2. Calculate a navigation path

    3. Background listening to Lbeacon signals

Author:

    Phil Wu 01-Feb-2018

--*/

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import eos.waypointbasedindoornavigation.Find_loc.DeviceParameter;
import eos.waypointbasedindoornavigation.Find_loc.Find_Loc;
import eos.waypointbasedindoornavigation.Find_loc.ReadWrite_File;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import static eos.waypointbasedindoornavigation.GeoCalulation.getDirectionFromBearing;
import static eos.waypointbasedindoornavigation.R.id.beginning;
import static eos.waypointbasedindoornavigation.R.id.imageView;
import static eos.waypointbasedindoornavigation.R.id.start;
import static eos.waypointbasedindoornavigation.Setting.getPreferenceValue;




public class NavigationActivity extends AppCompatActivity implements BeaconConsumer {

    private static final int USER_MODE = 3;
    private static final int TESTER_MODE = 4;

    private static final int NORMAL_WAYPOINT = 0;
    private static final int ELEVATOR_WAYPOINT = 1;
    private static final int STAIRWELL_WAYPOINT = 2;
    private static final int CONNECTPOINT = 3;
    private static final int ARRIVED_NOTIFIER = 0;
    private static final int WRONGWAY_NOTIFIER = 1;
    private static final int MAKETURN_NOTIFIER = 2;

    private static final int VIRTUAL_UP = 1;
    private static final int VIRTUAL_DOWN = 2;


    private static final String FRONT = "front";
    private static final String FRONT_RIGHTSIDE = "frontRightSide";
    private static final String FRONT_LEFTSIDE = "frontLeftSide";
    private static final String LEFT = "left";
    private static final String FRONT_LEFT = "frontLeft";
    private static final String REAR_LEFT = "rearLeft";
    private static final String RIGHT = "right";
    private static final String FRONT_RIGHT = "frontRight";
    private static final String REAR_RIGHT = "rearRight";
    private static final String ELEVATOR = "elevator";
    private static final String STAIR = "stair";
    private static final String ARRIVED = "arrived";
    private static final String WRONG = "wrong";


    private static final String NOW_GO_STRAIGHT_RIGHTSIDE = "請靠右";
    private static final String NOW_GO_STRAIGHT_LEFTSIDE = "請靠左";
    private static final String NOW_GO_STRAIGHT = "請繼續直走";
    private static final String NOW_TURN_LEFT = "請向左轉後";
    private static final String NOW_TURN_RIGHT = "請向右轉後";
    private static final String NOW_TURN_FRONT_LEFT = "請向左前方轉後";
    private static final String NOW_TURN__FRONT_RIGHT = "請向右前方轉後";
    private static final String NOW_TURN_REAR_LEFT = "請向左後方轉後";
    private static final String NOW_TURN__REAR_RIGHT = "請向右後方轉後";
    private static final String NOW_TAKE_ELEVATOR = "離開電梯後";
    private static final String NOW_WALK_UP_STAIR = "上樓梯後";
    private static final String NOW_WALK_DOWN_STAIR = "下樓梯後";


    private static final String GO_STRAIGHT_ABOUT = "直走約";
    private static final String THEN_GO_STRAIGHT = "然後直走";
    private static final String THEN_GO_STRAIGHT_RIGHTSIDE = "然後靠右直走";
    private static final String THEN_GO_STRAIGHT_LEFTSIDE = "然後靠左直走";
    private static final String THEN_TURN_LEFT = "然後向左轉";
    private static final String THEN_TURN_RIGHT = "然後向右轉";
    private static final String THEN_TURN_FRONT_LEFT = "然後向左前方轉";
    private static final String THEN_TURN__FRONT_RIGHT = "然後向右前方轉";
    private static final String THEN_TURN_REAR_LEFT = "然後向左後方轉";
    private static final String THEN_TURN__REAR_RIGHT = "然後向右後方轉";
    private static final String THEN_TAKE_ELEVATOR = "然後搭電梯";
    private static final String THEN_WALK_UP_STAIR = "然後爬樓梯";
    private static final String THEN_WALK_DOWN_STAIR = "然後下樓梯";
    private static final String WAIT_FOR_ELEVATOR = "電梯中請稍候";
    private static final String WALKING_UP_STAIR = "爬樓梯";
    private static final String WALKING_DOWN_STAIR = "下樓梯";


    private static final String YOU_HAVE_ARRIVE = "抵達目的地";
    private static final String GET_LOST = "糟糕，你走錯路了";
    private static final String METERS = "公尺";
    private static final String PLEASE_GO_STRAIGHT = "請直走";
    private static final String PLEASE_GO_STRAIGHT_RIGHTSIDE = "請靠右直走";
    private static final String PLEASE_GO_STRAIGHT_LEFTSIDE = "請靠左直走";
    private static final String PLEASE_TURN_LEFT = "請左轉";
    private static final String PLEASE_TURN_RIGHT = "請右轉";
    private static final String PLEASE_TURN_FRONT_LEFT = "請向左前方轉";
    private static final String PLEASE_TURN__FRONT_RIGHT = "請向右前方轉";
    private static final String PLEASE_TURN_REAR_LEFT = "請向左後方轉";
    private static final String PLEASE_TURN__REAR_RIGHT = "請向右後方轉";
    private static final String PLEASE_TAKE_ELEVATOR = "請搭電梯";
    private static final String PLEASE_WALK_UP_STAIR = "請走樓梯";

    private static final String toBasement = "至地下一樓";
    private static final String toFirstFloor = "至一樓";
    private static final String toSecondFloor = "至二樓";
    private static final String toThirdFloor = "至三樓";
    Node tmpNode;
    Node recordbeacon;
    int error_count = 0;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private long startT = System.currentTimeMillis();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    // start ---------- Variables used to record important values ------------

    // IDs and Regions of source and destination input by user on home screen
    String sourceID, destinationID, sourceRegion, destinationRegion, destinationName, wrongdestinationID, wrongdestinationRegion, tmpdestinationID, tmpdestinationRegion;
    String currentLocationName;

    boolean isFirstBeacon = true;
    boolean FirstTurn = true;
    boolean isInVirtualNode = false;
    boolean StairGoUp = false;
    boolean LastisSlash = false;
    boolean DirectCompute = false;
    boolean JumpNode = false;
    boolean arriveinwrong = false;
    boolean isLongerPath = false;
    boolean CallDirectionInWrong = false;
    boolean Calibration = false;
    Node startNode;
    Node endNode;
    Node lastNode;
    Node wrongWaypoint;
    Node chosestartNode;

    // integer to record how many waypoints have been traveled
    int walkedWaypoint = 0;

    int pathLength = 0;
    int twotimesClose;
    int regionIndex = 0;
    int passedGroupID = -1;
    String passedRegionID;
    List<String> tmpDestinationID = new ArrayList<>();

    // ---------- variables used to record important values ------------ end


    // start ---------- variables used to store routing data ----------

    // a list of NavigationSubgraph object representing a Navigation Graph
    List<NavigationSubgraph> navigationGraph = new ArrayList<>();
    List<NavigationSubgraph> navigationGraphForAllWaypoint = new ArrayList<>();


    // a list of Region object storing the information of regions that will be traveled through
    List<eos.waypointbasedindoornavigation.Region> regionPath = new ArrayList<>();

    // hashmap for storing region data
    RegionGraph regionGraph = new RegionGraph();
    //HashMap<String, .android.waypointbasedindoornavigation.Region> regionData = new HashMap<>();

    // a list of Node object representing a navigation path
    List<Node> navigationPath = new ArrayList<Node>();
    List<Node> virtualNodeUp = new ArrayList<Node>();
    List<Node> virtualNodeDown = new ArrayList<Node>();
    HashMap<String, String> navigationPath_ID_to_Name_Mapping = new HashMap<>();

    HashMap<String, String> mappingOfRegionNameAndID = new HashMap<>();
    HashMap<String, Node> allWaypointData = new HashMap<>();

    // ---------- variables used to store routing data ---------- end


    // start ---------- objects used to provide voice and text navigational guidance ----------

    // reminder for destination and current location
    TextView destinationReminder, currentLocationReminder;

    // textual navigational instruction
    TextView firstMovement, howFarToMove, nextTurnMovement, nowDoInstruction;

    // graphical navigational indicator
    ImageView imageTurnIndicator;

    // Indicator for popupwindow notifying user to make a turn at each waypoint
    String turnNotificationForPopup = null;

    // vocice engine for vocal navigational instrucation
    private TextToSpeech tts;

    // object for creating popupWindow
    private PopupWindow popupWindow;
    private LinearLayout positionOfPopup;

    // ---------- objects used to provide voice and text navigational guidance ---------- end


    // start ----------  manager and handlers to Lbeacon signals ----------

    //Beacon manager for ranging Lbeaon signal
    private BeaconManager beaconManager;
    private Region region;

    // thread for handling Lbeacon ID while in a navigation tour
    Thread threadForHandleLbeaconID;

    // handlers for "threadToHandleLbeaconID", receive message from the thread
    static Handler instructionHandler, currentPositiontHandler, walkedPointHandler, progressHandler;

    // synchronization between Lbeacon receiver and handler thread
    final Object sync = new Object();

    // string for storing currently received Lbeacon ID
    String currentLBeaconID = "EmptyString";

    // ----------  manager and handlers to Lbeacon signals ---------- end


    // start ---------- draw panel to display navigation progress bar ----------

    //Paint and drawpanel
    Paint paint = new Paint();
    Bitmap myBitmap;
    Bitmap workingBitmap;
    Bitmap mutableBitmap;
    Canvas canvas;
    // ---------- draw panel to display navigation progress bar ---------- end


    ProgressBar progressBar;
    TextView progressNumber;
    int progressStatus = 0;


    // variables created for demo purpose
    EditText waypointIDInput;
    Button waypointIDInputButton;
    Button exitButton;
    int whichWaypointOnProgressBar = 0;

    boolean inpath = false;
    // Find_loc part
    private Find_Loc LBD = new Find_Loc();
    private DateFormat df = new SimpleDateFormat("yy_MM_DD_hh_mm");
    private ReadWrite_File wf = new ReadWrite_File();
    private DeviceParameter dp;
    String receivebeacon;
    double offset;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        setTitle("台大雲林分院室內導航系統");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        offset = (double) pref.getFloat("offset",(float) 0);
        Log.i("xxx_wrong", "onCreate");
        Log.i("Navi_Create_Mem", "usedMemory: Heap/Allocated Heap "+ Debug.getNativeHeapSize() + "/" + Debug.getNativeHeapAllocatedSize());




        // find UI objects by IDs
        nowDoInstruction = (TextView)findViewById(R.id.insturction0);
        firstMovement = (TextView) findViewById(R.id.instruction1);
        howFarToMove = (TextView) findViewById(R.id.instruction2);
        nextTurnMovement = (TextView) findViewById(R.id.insturction3);
        destinationReminder = (TextView) findViewById(R.id.to);
        currentLocationReminder = (TextView) findViewById(R.id.nowAt);
        imageTurnIndicator = (ImageView) findViewById(imageView);
        positionOfPopup = (LinearLayout) findViewById(R.id.navigationLayout);
        waypointIDInput = (EditText) findViewById(R.id.inputID);
        waypointIDInputButton = (Button) findViewById(R.id.inputButton);
        //drawPanel = (ImageView) findViewById(R.id.drawpanel);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressNumber = (TextView) findViewById(R.id.progressNumber);

        // draw panel setup
        myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_drawpanel);
        workingBitmap = Bitmap.createBitmap(myBitmap);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);
        wf.setFile_name("Log" + df.format(Calendar.getInstance().getTime()));
        dp = new DeviceParameter();

        // voice engine setup
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.CHINESE);
                    Log.i("tts", "ttsSetUp");
                }
            }
        });

        if (Setting.getModeValue() == USER_MODE) {
            waypointIDInput.setVisibility(View.INVISIBLE);
            waypointIDInputButton.setVisibility(View.INVISIBLE);
        } else if (Setting.getModeValue() == TESTER_MODE) {
            waypointIDInput.setVisibility(View.VISIBLE);
            waypointIDInputButton.setVisibility(View.VISIBLE);
        }


        // receive value passed from MainActivity,
        //including IDs and Regions of source and destination
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            destinationName = bundle.getString("destinationName");
            destinationID = bundle.getString("destinationID");
            destinationRegion = bundle.getString("destinationRegion");
        }


        Log.i("abc", "Initial REgion ID:" + passedRegionID);

        // load region data from region graph
        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this,
                regionGraph.getAllRegionNames());

        navigationPath.add(new Node("empty", "empty", "empty", "empty"));
        //load all waypoint data for precise positioning
        loadAllWaypointData();
        virtualNodeUp = DataParser.getVirtualNode(this,VIRTUAL_UP);
        virtualNodeDown = DataParser.getVirtualNode(this,VIRTUAL_DOWN);
        destinationReminder.setText("目的地 : " + destinationName);
        // Lbeacon Manager setup
        beaconManagerSetup();


        // create a thread to handle the Lbeacon signal
        threadForHandleLbeaconID = new Thread(new NavigationTread());
        threadForHandleLbeaconID.start();


        instructionHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                // receive a turn direction message from threadForHandleLbeaconID
                String turnDirection = (String) msg.obj;

                // distance to the next waypoint
                int distance = 0;

                // if there are two or more waypoints to go
                if (navigationPath.size() >= 2)
                    distance = (int) GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));

                navigationInstructionDisplay(turnDirection, distance);

            }
        };

        //Handler for setting current location on UI
        currentPositiontHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                currentLocationName = (String) msg.obj;
                currentLocationReminder.setText("目前位置 : " + currentLocationName);

                //destinationReminder.setText("目的地 : " + navigationPath.get(navigationPath.size()-1)._waypointName);
            }
        };

        //Handler for number of waypoint traveled in this navigation route
        walkedPointHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                int numberOfWaypointTraveled = (int) msg.obj;

                //If it is the first waypoint of travel of a region, meaning that
                //heading correction is needed
                if (numberOfWaypointTraveled == 1 && (navigationPath.size() >= 2))
                    turnNotificationForPopup = null;
            }
        };

        // the max value of prgress bar is set to the size of navigation path
        progressBar.setMax(navigationPath.size());

        progressHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                Boolean isMakingProgress = (Boolean) msg.obj;

                if (isMakingProgress == true) {

                    progressStatus += 1;
                    progressBar.setProgress(progressStatus);
                    progressNumber.setText(progressStatus + "/" + progressBar.getMax());
                }
            }

        };

    }

    void navigationInstructionDisplay(String turnDirection, int distance) {

        SignalLog("----------指令模組開始----------");
        //關閉後面圖示顯示UI
        closeImage();

        Log.i("123456","navigationpath(0) = " + navigationPath.get(0)._waypointName);


        if(FirstTurn == true) {
            lastNode = navigationPath.get(0);
            if(navigationPath.size() == 2 && navigationPath.get(0)._elevation != navigationPath.get(1)._elevation)
                turnDirection = STAIR;
        }

        Log.i("xxx_stair","LastNode = " + lastNode._waypointName + " navigationPath(0) = " + navigationPath.get(0)._waypointName);
        //樓梯或電梯方向顯示
        if(navigationPath.size() >= 2 && !turnDirection.equals(WRONG))
            ShowDirectionFromConnectPoint();

        nextTurnMovement.setText("");
        switch (turnDirection) {

            case LEFT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                   // if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                    //else
                       // howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_LEFT);

                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_LEFT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN_LEFT);
                        break;

                }
                        /*
                        if(navigationPath.get(1)._nodeType == 1)
                            nextTurnMovement.setText(THEN_TAKE_ELEVATOR);
                        else if(navigationPath.get(1)._nodeType == 2)
                            nextTurnMovement.setText(THEN_WALK_UP_STAIR);
                        else
                            nextTurnMovement.setText(THEN_TURN_LEFT);*/


                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

                //showHintAtWaypoint();
                //imageTurnIndicator.setImageResource(R.drawable.straight_left);
                turnNotificationForPopup = LEFT;
                break;

            case FRONT_LEFT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                   // if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                  //  else
                    //    howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {
                    case ELEVATOR_WAYPOINT:
                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_FRONT_LEFT);
                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_FRONT_LEFT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN_FRONT_LEFT);
                        break;

                }
                        /*
                        if(navigationPath.get(1)._nodeType == 1)
                            nextTurnMovement.setText(THEN_TAKE_ELEVATOR);
                        else if(navigationPath.get(1)._nodeType == 2)
                            nextTurnMovement.setText(THEN_WALK_UP_STAIR);
                        else
                            nextTurnMovement.setText(THEN_TURN_FRONT_LEFT);*/

                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

                /*if(LastisSlash == true){
                    imageTurnIndicator.setImageResource(R.drawable.up_now);
                    nextTurnMovement.setText(THEN_GO_STRAIGHT);
                }
                else
                    imageTurnIndicator.setImageResource(R.drawable.left_up);*/
                turnNotificationForPopup = FRONT_LEFT;
                break;
            case REAR_LEFT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                    //if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                       // howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_REAR_LEFT);
                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_REAR_LEFT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN_REAR_LEFT);
                        break;

                }

                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

               /* if(LastisSlash == true){
                    imageTurnIndicator.setImageResource(R.drawable.up_now);
                    nextTurnMovement.setText(THEN_GO_STRAIGHT);
                }
                else
                    imageTurnIndicator.setImageResource(R.drawable.leftdown_now);*/
                turnNotificationForPopup = REAR_LEFT;
                break;

            case RIGHT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                   // if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                       // howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_RIGHT);
                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN_RIGHT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN_RIGHT);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

               // imageTurnIndicator.setImageResource(R.drawable.straight_right);
                turnNotificationForPopup = RIGHT;
                break;

            case FRONT_RIGHT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                    //if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                     //   howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN__FRONT_RIGHT);
                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN__FRONT_RIGHT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN__FRONT_RIGHT);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

               /* if(LastisSlash == true){
                    imageTurnIndicator.setImageResource(R.drawable.up_now);
                    nextTurnMovement.setText(THEN_GO_STRAIGHT);
                }
                else
                    imageTurnIndicator.setImageResource(R.drawable.right_up);*/
                turnNotificationForPopup = FRONT_RIGHT;
                break;


            case REAR_RIGHT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                   // if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                       // howFarToMove.setText("" + distance + " " + METERS);
                }
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN__REAR_RIGHT);
                        break;

                    case STAIRWELL_WAYPOINT:

                        if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                            elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                        //else
                            //nextTurnMovement.setText(THEN_TURN__REAR_RIGHT);
                        break;

                    case NORMAL_WAYPOINT:
                        //nextTurnMovement.setText(THEN_TURN__REAR_RIGHT);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);
              /*  if(LastisSlash == true) {
                    imageTurnIndicator.setImageResource(R.drawable.up_now);
                    nextTurnMovement.setText(THEN_GO_STRAIGHT);
                }
                else
                    imageTurnIndicator.setImageResource(R.drawable.rightdown_now);*/
                turnNotificationForPopup = REAR_RIGHT;
                break;

            case FRONT:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                  //  if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                      //  howFarToMove.setText("" + distance + " " + METERS);
                }
                Log.i("bbb", navigationPath.get(1)._waypointName);
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                                //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                        }
                        break;

                    case STAIRWELL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                                //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                        }
                        break;

                    case NORMAL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        }// else
                            //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

                //imageTurnIndicator.setImageResource(R.drawable.up_now);
                turnNotificationForPopup = FRONT;
                break;
//--------------------
            case FRONT_RIGHTSIDE:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                   // if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                   // else
                      //  howFarToMove.setText("" + distance + " " + METERS);
                }
                Log.i("bbb", navigationPath.get(1)._waypointName);
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                                //nextTurnMovement.setText(THEN_GO_STRAIGHT_RIGHTSIDE);
                        }
                        break;

                    case STAIRWELL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                               // nextTurnMovement.setText(THEN_GO_STRAIGHT_RIGHTSIDE);
                        }
                        break;

                    case NORMAL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } //else
                            //nextTurnMovement.setText(THEN_GO_STRAIGHT_RIGHTSIDE);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

               // if(LastisSlash == true)
                    //nextTurnMovement.setText(THEN_GO_STRAIGHT);

                //imageTurnIndicator.setImageResource(R.drawable.up_now);
                turnNotificationForPopup = FRONT_RIGHTSIDE;
                break;
//-----------------
            case FRONT_LEFTSIDE:
                firstMovement.setText(GO_STRAIGHT_ABOUT);
                if (navigationPath.size() > 1) {
                    //判斷下個目的地是否有LandMark
                    //if (NextLandMarkisEnglish == false)
                        howFarToMove.setText("" + distance + " " + METERS + "至" + navigationPath.get(1)._waypointName);
                    //else
                       // howFarToMove.setText("" + distance + " " + METERS);
                }
                Log.i("bbb", navigationPath.get(1)._waypointName);
                switch (navigationPath.get(1)._nodeType) {

                    case ELEVATOR_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                                //nextTurnMovement.setText(THEN_GO_STRAIGHT_LEFTSIDE);
                        }
                        break;

                    case STAIRWELL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } else {
                            if (!navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))
                                elevationDisplay(STAIRWELL_WAYPOINT, navigationPath.get(2)._elevation);
                            //else
                                //nextTurnMovement.setText(THEN_GO_STRAIGHT_LEFTSIDE);
                        }
                        break;

                    case NORMAL_WAYPOINT:
                        if (navigationPath.size() == 2) {
                            howFarToMove.setText("" + distance + " " + METERS);
                            nextTurnMovement.setText("抵達目的地");
                        } //else
                            //nextTurnMovement.setText(THEN_GO_STRAIGHT_LEFTSIDE);
                        break;

                }
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

                //if(LastisSlash == true)
                    //nextTurnMovement.setText(THEN_GO_STRAIGHT);

                //imageTurnIndicator.setImageResource(R.drawable.up_now);
                turnNotificationForPopup = FRONT_LEFTSIDE;
                break;
//------------------
            case STAIR:
                turnNotificationForPopup = STAIR;
                if(navigationPath.size() > 2) {
                    //上樓
                    if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation) {
                        switch (navigationPath.get(2)._elevation) {
                            case 0:
                                firstMovement.setText(WALKING_UP_STAIR + toBasement);
                                break;
                            case 1:
                                firstMovement.setText(WALKING_UP_STAIR + toFirstFloor);
                                break;
                            case 2:
                                firstMovement.setText(WALKING_UP_STAIR + toSecondFloor);
                                break;
                            case 3:
                                firstMovement.setText(WALKING_UP_STAIR + toThirdFloor);
                                break;
                        }
                    }else if(navigationPath.get(2)._elevation < navigationPath.get(0)._elevation){
                        switch (navigationPath.get(2)._elevation) {
                            case 0:
                                firstMovement.setText(WALKING_DOWN_STAIR + toBasement);
                                break;
                            case 1:
                                firstMovement.setText(WALKING_DOWN_STAIR + toFirstFloor);
                                break;
                            case 2:
                                firstMovement.setText(WALKING_DOWN_STAIR + toSecondFloor);
                                break;
                            case 3:
                                firstMovement.setText(WALKING_DOWN_STAIR + toThirdFloor);
                                break;
                        }
                    }
                }else if (navigationPath.size() == 2) {
                    //上樓
                    if (navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                        switch (navigationPath.get(1)._elevation) {
                            case 0:
                                firstMovement.setText(WALKING_UP_STAIR + toBasement);
                                break;
                            case 1:
                                firstMovement.setText(WALKING_UP_STAIR + toFirstFloor);
                                break;
                            case 2:
                                firstMovement.setText(WALKING_UP_STAIR + toSecondFloor);
                                break;
                            case 3:
                                firstMovement.setText(WALKING_UP_STAIR + toThirdFloor);
                                break;
                        }
                    } else if (navigationPath.get(1)._elevation < navigationPath.get(0)._elevation) {
                        switch (navigationPath.get(1)._elevation) {
                            case 0:
                                firstMovement.setText(WALKING_DOWN_STAIR + toBasement);
                                break;
                            case 1:
                                firstMovement.setText(WALKING_DOWN_STAIR + toFirstFloor);
                                break;
                            case 2:
                                firstMovement.setText(WALKING_DOWN_STAIR + toSecondFloor);
                                break;
                            case 3:
                                firstMovement.setText(WALKING_DOWN_STAIR + toThirdFloor);
                                break;
                        }
                    }
                }
                howFarToMove.setText("");
                nextTurnMovement.setText("");
                if (turnNotificationForPopup != null && FirstTurn == false)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);
                walkedWaypoint = 0;
                sourceID = navigationPath.get(1)._waypointID;
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                    imageTurnIndicator.setImageResource(R.drawable.stairs_up);
                else
                    imageTurnIndicator.setImageResource(R.drawable.stairs_down);
                break;

            case ELEVATOR:
                turnNotificationForPopup = ELEVATOR;
                firstMovement.setText(WAIT_FOR_ELEVATOR);
                howFarToMove.setText("");
                nextTurnMovement.setText("");
                imageTurnIndicator.setImageResource(R.drawable.elevator);
                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(MAKETURN_NOTIFIER);

                imageTurnIndicator.setImageResource(R.drawable.elevator);
                walkedWaypoint = 0;
                break;

            case ARRIVED:

                Log.i("renavigate", "arrived!");

                if (turnNotificationForPopup != null)
                    showHintAtWaypoint(ARRIVED_NOTIFIER);

                firstMovement.setText(" ");
                howFarToMove.setText(" ");
                nextTurnMovement.setText(" ");

                walkedWaypoint = 0;
                break;

            case WRONG:
                SignalLog("跳出重新計算路線指令");
                Log.i("wrong", "Out");
                List<Node> newPath = new ArrayList<>();
                List<Node> wrongPath = new ArrayList<>();
                //walkedWaypoint = 0;
                wrongWaypoint = allWaypointData.get(currentLBeaconID);
                currentLocationReminder.setText("目前位置 : " + currentLocationName);

                //WrongID = destination
                for (int i = 0;i < wrongWaypoint._attachIDs.size();i++) {
                    if ((endNode._mainID != 0 && endNode._mainID == wrongWaypoint._attachIDs.get(i))) {
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        arriveinwrong = true;
                        break;
                    }
                }
                if(wrongWaypoint._waypointID.equals(endNode._waypointID)) {
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    arriveinwrong = true;
                }

                if(arriveinwrong == false) {
                    isLongerPath = false;
                    //----------wrong way 從路線搜尋上個點------------------
                    JumpNode = true;
                    for (int i = 0; i < lastNode._adjacentWaypoints.size(); i++) {
                        if (lastNode._adjacentWaypoints.get(i).equals(wrongWaypoint._waypointID)) {
                            JumpNode = false;
                        }
                    }
                    if (JumpNode == true) {
                        Log.i("789456", "inJumpNode");
                        Log.i("In", "destinationID = " + destinationID + "endNode = " + endNode._waypointID + "wrong = " + wrongWaypoint._waypointID);

                        DirectCompute = true;
                        tmpdestinationID = destinationID;
                        tmpdestinationRegion = destinationRegion;

                        sourceID = startNode._waypointID;
                        sourceRegion = startNode._regionID;
                        destinationID = wrongWaypoint._waypointID;
                        destinationRegion = wrongWaypoint._regionID;
                        loadNavigationGraph();
                        wrongPath = startNavigation();

                        if (wrongPath.size() > 2)
                            lastNode = wrongPath.get(wrongPath.size() - 2);

                        Log.i("xxx_path", "wrongWay LastNode = " + lastNode._waypointName);
                        DirectCompute = false;
                        destinationID = tmpdestinationID;
                        destinationRegion = tmpdestinationRegion;
                    }
                    //---------------------------------------------
                    sourceID = wrongWaypoint._waypointID;
                    sourceRegion = wrongWaypoint._regionID;


                    loadNavigationGraph();
                    newPath = startNavigation();

                    Log.i("renavigate", "renavigate");

                    for (int i = 0; i < newPath.size(); i++)
                        Log.i("renavigate", "path node " + newPath.get(i)._waypointName);

                    for (int i = 0; i < newPath.size(); i++) {

                        if (newPath.get(i)._waypointName.equals(lastNode._waypointName)) {

                            isLongerPath = true;
                            break;
                        }

                        isLongerPath = false;
                    }
                    //Log.i("renavigate", "newPath next "+newPath.get(1)._waypointName);
                    //Log.i("renavigate", "lastWaypoint "+lastNode._waypointName);


                    if (isLongerPath) {
                        String gobackDirection = null;
                        appendLog("重新規劃路線，且是返回走");
                        currentLBeaconID = "EmptyString";
                        //navigationPath.add(0, lastNode);
                        //navigationPath.add(0, wrongWaypoint);

                        navigationPath = newPath;
                        progressBar.setMax(navigationPath.size());
                        progressStatus = 0;
                        nowDoInstruction.setText("請往回轉");
                        firstMovement.setText("然後等待指示");
                        howFarToMove.setText("");
                        nextTurnMovement.setText(" ");
                        turnNotificationForPopup = "goback";
                        imageTurnIndicator.setImageResource(R.drawable.turn_back);

                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                        turnNotificationForPopup = null;

                        //imageTurnIndicator.setImageResource(R.drawable.up_now);


                        // Show New Path
                        for (int xx = 0; xx < navigationPath.size(); xx++) {
                            Log.i("xxx_recompute", "1 - navigationPath(" + xx + ") = " + navigationPath.get(xx)._waypointName);
                        }


                    } else {
                        appendLog("重新規劃路線");
                        //showHintAtWaypoint(WRONGWAY_NOTIFIER);

                        navigationPath = newPath;
                        progressBar.setMax(navigationPath.size());
                        progressStatus = 1;
                        progressNumber.setText(progressStatus + "/" + progressBar.getMax());

                        turnNotificationForPopup = null;

                        firstMovement.setText(GO_STRAIGHT_ABOUT);
                        if (navigationPath.size() >= 2) {
                            howFarToMove.setText("" + GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1)) + " " + METERS + "至" + navigationPath.get(1)._waypointName);


                            turnNotificationForPopup = getDirectionFromBearing
                                    (lastNode, navigationPath.get(0), navigationPath.get(1));

                            Log.i("renavigate", "lastNode, 0, 1: " + lastNode._waypointName + ", "
                                    + navigationPath.get(0)._waypointName + ", " + navigationPath.get(1)._waypointName);
                            Log.i("renavigate","方向 popup" + turnNotificationForPopup);
                        }
                        currentLocationReminder.setText("目前位置:" + currentLocationName);


                        showHintAtWaypoint(MAKETURN_NOTIFIER);

                        //.setImageResource(R.drawable.up_now);
                        if (navigationPath.size() >= 3) {

                            if(navigationPath.get(2)._elevation == navigationPath.get(1)._elevation) {
                                turnNotificationForPopup = getDirectionFromBearing
                                        (lastNode, navigationPath.get(0), navigationPath.get(1));
                                Log.i("xxx_Direction", "navigationPath(0) = " + navigationPath.get(0)._waypointName);
                                Log.i("xxx_Direction", "navigationPath(1) = " + navigationPath.get(1)._waypointName);
                                Log.i("xxx_Direction", "navigationPath(2) = " + navigationPath.get(2)._waypointName);

                              /*  switch (turnNotificationForPopup) {

                                    case RIGHT:
                                        //nextTurnMovement.setText(THEN_TURN_RIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.straight_right);
                                        break;
                                    case LEFT:
                                        //nextTurnMovement.setText(THEN_TURN_LEFT);
                                        imageTurnIndicator.setImageResource(R.drawable.straight_left);
                                        break;
                                    case FRONT_RIGHT:
                                        //nextTurnMovement.setText(THEN_TURN__FRONT_RIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.right_up);
                                        break;
                                    case FRONT_LEFT:
                                       // nextTurnMovement.setText(THEN_TURN_FRONT_LEFT);
                                        imageTurnIndicator.setImageResource(R.drawable.left_up);
                                        break;
                                    case REAR_RIGHT:
                                        //nextTurnMovement.setText(THEN_TURN__REAR_RIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                                        break;
                                    case REAR_LEFT:
                                        //nextTurnMovement.setText(THEN_TURN_REAR_LEFT);
                                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                                        break;
                                    case FRONT:
                                        //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                                        break;
                                    case FRONT_LEFTSIDE:
                                        //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                                        break;
                                    case FRONT_RIGHTSIDE:
                                        //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                                        break;
                                }*/
                            }else if (navigationPath.get(2)._elevation > navigationPath.get(0)._elevation) {
                                ShowDirectionFromConnectPoint();
                                switch (navigationPath.get(2)._elevation) {
                                    case 0:
                                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toBasement);
                                        break;
                                    case 1:
                                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toFirstFloor);
                                        break;
                                    case 2:
                                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toSecondFloor);
                                        break;
                                    case 3:
                                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toThirdFloor);
                                        break;
                                }
                            } else if (navigationPath.get(2)._elevation < navigationPath.get(0)._elevation) {
                                ShowDirectionFromConnectPoint();
                                switch (navigationPath.get(2)._elevation) {
                                    case 0:
                                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toBasement);
                                        break;
                                    case 1:
                                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toFirstFloor);
                                        break;
                                    case 2:
                                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toSecondFloor);
                                        break;
                                    case 3:
                                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toThirdFloor);
                                        break;
                                }
                            }

                            Log.i("xxx_wrong", "lastNode & navigationPath(0) & (1) = " + lastNode._waypointName +"&" + navigationPath.get(0)._waypointName + "&" + navigationPath.get(1)._waypointName);
                            if (navigationPath.get(0)._connectPointID != 0 && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID) {
                                Log.i("xxx_wrong", "navigationPath(0) & (1) -2  = " + navigationPath.get(0)._waypointName + "&" + navigationPath.get(1)._waypointName);
                                elevationDisplay(ELEVATOR_WAYPOINT, navigationPath.get(1)._elevation);
                                //----------------
                                turnNotificationForPopup = STAIR;
                                if (navigationPath.size() > 2) {
                                    //上樓
                                    if (navigationPath.get(2)._elevation > navigationPath.get(0)._elevation) {
                                        switch (navigationPath.get(2)._elevation) {
                                            case 0:
                                                firstMovement.setText(WALKING_UP_STAIR + toBasement);
                                                break;
                                            case 1:
                                                firstMovement.setText(WALKING_UP_STAIR + toFirstFloor);
                                                break;
                                            case 2:
                                                firstMovement.setText(WALKING_UP_STAIR + toSecondFloor);
                                                break;
                                            case 3:
                                                firstMovement.setText(WALKING_UP_STAIR + toThirdFloor);
                                                break;
                                        }
                                    } else if (navigationPath.get(2)._elevation < navigationPath.get(0)._elevation) {
                                        switch (navigationPath.get(2)._elevation) {
                                            case 0:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toBasement);
                                                break;
                                            case 1:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toFirstFloor);
                                                break;
                                            case 2:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toSecondFloor);
                                                break;
                                            case 3:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toThirdFloor);
                                                break;
                                        }
                                    }
                                } else if (navigationPath.size() == 2) {
                                    //上樓
                                    if (navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                                        switch (navigationPath.get(1)._elevation) {
                                            case 0:
                                                firstMovement.setText(WALKING_UP_STAIR + toBasement);
                                                break;
                                            case 1:
                                                firstMovement.setText(WALKING_UP_STAIR + toFirstFloor);
                                                break;
                                            case 2:
                                                firstMovement.setText(WALKING_UP_STAIR + toSecondFloor);
                                                break;
                                            case 3:
                                                firstMovement.setText(WALKING_UP_STAIR + toThirdFloor);
                                                break;
                                        }
                                    } else if (navigationPath.get(1)._elevation < navigationPath.get(0)._elevation) {
                                        switch (navigationPath.get(1)._elevation) {
                                            case 0:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toBasement);
                                                break;
                                            case 1:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toFirstFloor);
                                                break;
                                            case 2:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toSecondFloor);
                                                break;
                                            case 3:
                                                firstMovement.setText(WALKING_DOWN_STAIR + toThirdFloor);
                                                break;
                                        }
                                    }
                                }
                                howFarToMove.setText("");
                                nextTurnMovement.setText("");
                                if (turnNotificationForPopup != null)
                                    showHintAtWaypoint(MAKETURN_NOTIFIER);
                                walkedWaypoint = 0;
                                sourceID = navigationPath.get(1)._waypointID;
                                if (navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                                    imageTurnIndicator.setImageResource(R.drawable.stairs_up);
                                else
                                    imageTurnIndicator.setImageResource(R.drawable.stairs_down);
                            }

                        } else {

                            nextTurnMovement.setText("然後抵達目的地");
                        }

                        // Show New Path
                        for (int xx = 0; xx < navigationPath.size(); xx++) {
                            Log.i("xxx_recompute", "navigationPath(" + xx + ") = " + navigationPath.get(xx)._waypointName);
                        }
                        //showHintAtWaypoint(MAKETURN_NOTIFIER);

                        Log.i("xxx_stair"," in WRONG Exit LastNode = " + lastNode._waypointName + " navigationPath(0) = " + navigationPath.get(0)._waypointName);
                        if(JumpNode == false) {
                           CallDirectionInWrong = true;
                           ShowDirectionFromConnectPoint();
                        }else{
                            turnNotificationForPopup = getDirectionFromBearing(lastNode,navigationPath.get(0),navigationPath.get(1));
                            showHintAtWaypoint(MAKETURN_NOTIFIER);
                            turnNotificationForPopup = getDirectionFromBearing(navigationPath.get(0),navigationPath.get(1),navigationPath.get(2));
                            //強制轉為直走in Wrong Case(僅修改跳出的圖)
                            if(navigationPath.size() > 2) {
                                if ((navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == lastNode._elevation) //只有兩個鄰居，且自己不在樓梯
                                        || (navigationPath.get(0)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0x8193bd410x540df142")) //病歷室大廳->核子->樓梯
                                        || (navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //核子->樓梯->新舊大樓連接走廊
                                        || (navigationPath.get(0)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0x8193bd410x540df142")) //電腦斷層大廳->抽血->樓梯
                                        || (navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //抽血->樓梯->連接走廊
                                        || (navigationPath.get(0)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0xf295c2410x63a8f042")) //病歷室大廳->核子->樓梯
                                        || (navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //核子->樓梯->新舊大樓連接走廊
                                        || (navigationPath.get(0)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0xf295c2410x63a8f042")) //電腦斷層大廳->抽血->樓梯
                                        || (navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //抽血->樓梯->連接走廊
                                        || (navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(2)._waypointID.equals("0xeb57ba410xfb95f042")) //核子->病歷室大廳->後門出口
                                        || (navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(2)._waypointID.equals("0xeb57ba410xfb95f042")) //抽血->電腦斷層大廳->後門出口
                                        ) {
                                    Log.i("xxx_Slash", "強制轉為直走in wrong");
                                    //imageTurnIndicator.setImageResource(R.drawable.up_now);
                                    turnNotificationForPopup = FRONT;
                                    //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                                    LastisSlash = true;
                                }
                            }
                        }
                        lastNode = navigationPath.get(0);
                        navigationPath.remove(0);
                        passedGroupID = navigationPath.get(0)._groupID;
                    }
                }
                break;
        }
        //After the navigational instruction for current waypoint is properly given,
        //the waypoint is removed from the top of the navigationPath


        // readNavigationInstruction();
        if(navigationPath.size() > 1) {
            if (!passedRegionID.equals(navigationPath.get(0)._regionID))
                regionIndex++;
            passedRegionID = navigationPath.get(0)._regionID;
            passedGroupID = navigationPath.get(0)._groupID;
            //強制轉換的Case in 台大醫院
            //判斷下個Node鄰居數與是否為電/樓梯()
            Log.i("xxx_Slash", "LastNode = " + lastNode._waypointName + " n(0) = " + navigationPath.get(0)._waypointName + " n(1) = " + navigationPath.get(1)._waypointName);
           if(!turnDirection.equals(WRONG) && navigationPath.size() > 2){
               if((navigationPath.get(0)._adjacentWaypoints.size() <= 2 && navigationPath.get(1)._adjacentWaypoints.size() <= 2 && navigationPath.get(0)._elevation == navigationPath.get(1)._elevation) //只有兩個鄰居，且自己不在樓梯
                       ||(navigationPath.get(0)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0x8193bd410x540df142")) //病歷室大廳->核子->樓梯(靠新大樓)
                       ||(navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //核子->樓梯(靠新大樓)->新舊大樓連接走廊
                       ||(navigationPath.get(0)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0x8193bd410x540df142")) //電腦斷層大廳->抽血->樓梯(靠新大樓)
                       ||(navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //抽血->樓梯(靠新大樓)->連接走廊
                       ||(navigationPath.get(0)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0xf295c2410x63a8f042")) //病歷室大廳->核子->樓梯(靠新大樓)
                       ||(navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //核子->樓梯->新舊大樓連接走廊
                       ||(navigationPath.get(0)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0xf295c2410x63a8f042")) //電腦斷層大廳->抽血->樓梯
                       ||(navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(2)._waypointID.equals("0x283ff0420x00000000")) //抽血->樓梯->連接走廊
                       ||(navigationPath.get(0)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(1)._waypointID.equals("0x0800b8410x0200f042") && navigationPath.get(2)._waypointID.equals("0xeb57ba410xfb95f042")) //核子->病歷室大廳->後門出口
                       ||(navigationPath.get(0)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(1)._waypointID.equals("0x3519b8410x4d06f042") && navigationPath.get(2)._waypointID.equals("0xeb57ba410xfb95f042")) //抽血->電腦斷層大廳->後門出口
                       ||(navigationPath.get(0)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0x0800b8410x0200f042") && FirstTurn == true) //樓梯(靠新大樓)->核子->病歷室大廳
                       ||(navigationPath.get(0)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(1)._waypointID.equals("0xc43af3420x00000000") && navigationPath.get(2)._waypointID.equals("0x0800b8410x0200f042") && FirstTurn == true) //樓梯->核子->病歷室大廳
                       ||(navigationPath.get(0)._waypointID.equals("0x8193bd410x540df142") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0x3519b8410x4d06f042") && FirstTurn == true) //樓梯(靠新大樓)->抽血->電腦斷層大廳
                       ||(navigationPath.get(0)._waypointID.equals("0xf295c2410x63a8f042") && navigationPath.get(1)._waypointID.equals("0xde57c8410x0721f342") && navigationPath.get(2)._waypointID.equals("0x3519b8410x4d06f042") && FirstTurn == true) //樓梯->抽血->電腦斷層大廳
                       ){
                   Log.i("xxx_Slash","強轉為直走");
                   //imageTurnIndicator.setImageResource(R.drawable.up_now);
                   turnNotificationForPopup = FRONT;
                   turnDirection = FRONT;
                   //nextTurnMovement.setText(THEN_GO_STRAIGHT);
                   showHintAtWaypoint(MAKETURN_NOTIFIER);
                   LastisSlash = true;
                   if (navigationPath.get(1)._waypointID.equals(endNode._waypointID))
                       nextTurnMovement.setText("然後抵達目的地");
               }
           }

        }

        if(arriveinwrong == false && FirstTurn == false)
            readNavigationInstruction();



        if (!turnDirection.equals(WRONG)) {
            lastNode = navigationPath.get(0);
            navigationPath.remove(0);
        }
        if(FirstTurn == false) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBackImage();
                    Log.i("xxx_timer", "Timercomplete");
                }
            }, 1000);
        }
        SignalLog("----------指令模組結束----------");
        FirstTurn = false;
    }


    void elevationDisplay(int transferPointType, int elevation) {

        if (transferPointType == ELEVATOR_WAYPOINT) {

            switch (elevation) {
                //修改TAKE_TAKE_ELEVATOR 成 THEN_WALK_UP_STAIR
                case 0:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + toBasement);
                    break;
                case 1:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + toFirstFloor);
                    break;
                case 2:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + toSecondFloor);
                    break;
                case 3:
                    nextTurnMovement.setText(THEN_TAKE_ELEVATOR + toThirdFloor);
                    break;
            }

        } else if (transferPointType == STAIRWELL_WAYPOINT) {
            Log.i("xxx_stair","n(0)_e = " + navigationPath.get(0)._elevation + "n(1)_e =" + navigationPath.get(1)._elevation);
            if(navigationPath.get(2)._elevation > navigationPath.get(0)._elevation) {
                switch (elevation) {

                    case 0:
                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toBasement);
                        break;
                    case 1:
                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toFirstFloor);
                        break;
                    case 2:
                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toSecondFloor);
                        break;
                    case 3:
                        nextTurnMovement.setText(THEN_WALK_UP_STAIR + toThirdFloor);
                        break;
                }
            }else{
                switch (elevation) {

                    case 0:
                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toBasement);
                        break;
                    case 1:
                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toFirstFloor);
                        break;
                    case 2:
                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toSecondFloor);
                        break;
                    case 3:
                        nextTurnMovement.setText(THEN_WALK_DOWN_STAIR + toThirdFloor);
                        break;
                }

            }

        }


    }


    // set up Lbeacon manager
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void beaconManagerSetup() {

        Log.i("beaconManager", "beaconManagerSetup");

        //Beacon manager setup
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.unbind(this);
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


        beaconManager.setForegroundScanPeriod(50);
        beaconManager.setForegroundBetweenScanPeriod(0);

        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        // Get the details for all the beacons we encounter.
        region = new Region("justGiveMeEverything", null, null, null);
        bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        //ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1001);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scanning for Beacons");
        Intent intent = new Intent(this, NavigationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification Channel ID",
                    "My Notification Name", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("My Notification Channel Description");
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(channel.getId());
        }
        beaconManager.enableForegroundServiceScanning(builder.build(), 456);
        beaconManager.setEnableScheduledScanJobs(false);
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.bind(NavigationActivity.this);
    }

    @Override
    protected void onDestroy() {
        Log.i("beaconManager", "onDestroy called");
        super.onDestroy();
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.unbind(this);
        System.gc();
        Log.i("Navi_Destroy_Mem", "usedMemory: Heap/Allocated Heap "+ Debug.getNativeHeapSize() + "/" + Debug.getNativeHeapAllocatedSize());
    }

    @Override
    public void onBeaconServiceConnect() {
        //Start scanning for Lbeacon signal
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i("beacon", "Beacono Size:" + beacons.size());
                Log.i("beaconManager", "beaconRanging");
                if(FirstTurn == true && Calibration == false){
                    long endT = System.currentTimeMillis();
                    if(endT - startT > 8000){
                        offset = offset - 5;
                        Calibration = true;
                    }
                }
                if (beacons.size() > 0) {
                    Iterator<Beacon> beaconIterator = beacons.iterator();
                    while (beaconIterator.hasNext()) {
                        Beacon beacon = beaconIterator.next();
                        logBeaconData(LBD.Find_Loc(beacon, 3, offset));
                    }
                }
            }

        });
        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId",
                    null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    // load beacon ID
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    private void logBeaconData(List<String> beacon) {

        if (beacon.size() > 2) {

            Log.i("beacon", "beacon 0: " + beacon.get(0));

            Log.i("beacon", "beacon 1: " + beacon.get(1));

            Log.i("beacon", "beacon 2: " + beacon.get(2));
            Node receiveNode;
            Boolean pass = false;

            wf.writeFile("NAP1:" + beacon.toString());
            Log.i("NAP1", beacon.toString());
            receivebeacon = null;

//            if (beacon.get(2).equals("close"))
  //              receivebeacon = beacon.get(3);

            //連續收兩次判斷，close才進去
            if (beacon.get(2).equals("close") && navigationPath.size() > 0) {
                tmpNode = allWaypointData.get(beacon.get(3));
                if(navigationPath.get(0)._waypointID.equals(beacon.get(3))){
                    //inpath Next equal receive close
                   receivebeacon = beacon.get(3);
                   error_count = 0;
                }else if(navigationPath.get(0)._groupID !=0 && navigationPath.get(0)._groupID == tmpNode._groupID){
                    //inpath Next equal receive group close
                    receivebeacon = beacon.get(3);
                    error_count = 0;
                }else if(!navigationPath.get(0)._waypointID.equals(beacon.get(3)) && error_count == 0){
                    //Not inpath First times
                    recordbeacon = allWaypointData.get(beacon.get(3));
                    error_count ++;
                }else if(!navigationPath.get(0)._waypointID.equals(beacon.get(3)) && error_count == 1){
                    //Not inpath Second times
                    if(recordbeacon._waypointID.equals(beacon.get(3))){
                        receivebeacon = beacon.get(3);
                        error_count = 0;
                    }else if(!recordbeacon._waypointID.equals(beacon.get(3)) && recordbeacon._groupID != 0){

                        if(tmpNode._groupID == recordbeacon._groupID){
                            receivebeacon = beacon.get(3);
                            error_count = 0;
                        }else{
                            error_count = 0;
                        }
                    } else{
                        error_count = 0;
                    }
                }else{
                    receivebeacon = beacon.get(3);
                    error_count = 0;
                }
            }
            Log.i("NAP1", beacon.toString() + receivebeacon);

            receiveNode = allWaypointData.get(receivebeacon);

            if (receiveNode != null)
                currentLocationReminder.setText("目前位置 : " + receiveNode._waypointName);


            Log.i("beaconManager", "receiveID: " + receivebeacon);

            if (isFirstBeacon && receiveNode != null) {
                chosestartNode = receiveNode;
                sourceID = receiveNode._waypointID;
                sourceRegion = receiveNode._regionID;
                passedRegionID = sourceRegion;
                loadNavigationGraph();
                navigationPath = startNavigation();
                progressBar.setMax(navigationPath.size());

                //sourceID = destination
                for (int i = 0;i < chosestartNode._attachIDs.size();i++) {
                    Log.i("xxx_firstarrive","choseNodeattachID = " + chosestartNode._attachIDs.get(i) + "mainID = " + endNode._mainID);
                    if ((endNode._mainID != 0 && endNode._mainID == chosestartNode._attachIDs.get(i))) {
                        showHintAtWaypoint(ARRIVED_NOTIFIER);
                        isFirstBeacon = false;
                    }
                }

                if(chosestartNode._waypointID.equals(endNode._waypointID)) {
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    isFirstBeacon = false;
                }

                appendLog("StartNavigation");
                //顯示初始方向指令
                String initDirectionName = null;
                if(navigationPath.size() > 1 && isFirstBeacon == true) {
                    initDirectionName = DataParser.getInitDirectionName(this, navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                    Log.i("xxx_initDirectionName", "" + initDirectionName);
                    //顯示初始方向指令
                    if(initDirectionName != null)
                        showPopupWindow(initDirectionName);
                }

                isFirstBeacon = false;
            }

            if (navigationPath.size() > 0) {
                if (receivebeacon != null && !currentLBeaconID.equals(receivebeacon) && receiveNode != null) {
                    Log.i("bbb_receivebeacon", "receivebeacon =" + receivebeacon);
                    Log.i("bbb_receiveNode", "receiveNode = " + receiveNode._waypointName);
                    Log.i("bbb_navigationPath", "navigationPath.get(0)ID = " + navigationPath.get(0)._waypointName);
                    if (receiveNode._groupID == navigationPath.get(0)._groupID &&
                            receiveNode._groupID != 0) {
                        Log.i("NAP2-1", receiveNode.getName());
                        Log.i("enter", "1");
                        currentLBeaconID = navigationPath.get(0)._waypointID;
                        pass = true;
                    } else if (receiveNode._groupID == passedGroupID && receiveNode._groupID != 0) {
                        Log.i("enter", "2");
                        pass = false;
                    } else {
                        Log.i("NAP2-2", receiveNode.getName());
                        Log.i("enter", "3");
                        currentLBeaconID = receivebeacon;
                        pass = true;
                    }
                } else {
                    Log.i("enter", "4");
                    pass = false;
                }
            }
            Log.i("renavigate", "CurrentID: " + currentLBeaconID);
            // block the Lbeacon ID the navigator just received
            if (pass) {

                appendLog(navigationPath.get(0)._waypointName);

                whichWaypointOnProgressBar += 1;

                synchronized (sync) {
                    sync.notify();
                }
            }
        }
    }


    // load waypoint data
    public void loadNavigationGraph() {

        regionGraph = DataParser.getRegionDataFromRegionGraph(this);
        mappingOfRegionNameAndID = DataParser.waypointNameAndIDMappings(this,
                regionGraph.getAllRegionNames());
        // regionPath for storing Region objects represent the regions
        //that the user passes by from source to destination
        regionPath = regionGraph.getRegionPath(sourceRegion, destinationRegion);

        // a list of String of region name in regionPath
        List<String> regionPathID = new ArrayList<>();

        for (int i = 0; i < regionPath.size(); i++)
            regionPathID.add(regionPath.get(i)._regionName);

        //Load waypoint data from the navigation subgraphs according to the regionPathID
        navigationGraph = DataParser.getWaypointDataFromNavigationGraph(this, regionPathID);

        // get the two Node objects that represent starting point and destination
        startNode = navigationGraph.get(0).nodesInSubgraph.get(sourceID);
        endNode = navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(destinationID);



    }

    public void loadAllWaypointData() {

        navigationGraphForAllWaypoint =
                DataParser.getWaypointDataFromNavigationGraph(this, regionGraph.getAllRegionNames());


        //allWaypointData 是HashMap(所有Node資料)     navigationGraphForAllWaypoint(regionID)
        for (int i = 0; i < navigationGraphForAllWaypoint.size(); i++)
            allWaypointData.putAll(navigationGraphForAllWaypoint.get(i).nodesInSubgraph);


        LBD.set_allWaypointData(allWaypointData);
        new DeviceParameter().setupDeviceParameter(this);

    }


    public List<Node> startNavigation() {

        List<Node> path = new ArrayList<>();
        int startNodeType = startNode._nodeType;

        // temporary variable to record connectPointID
        int connectPointID;

        // navigation in the same region
        if (navigationGraph.size() == 1) {

            path = computeDijkstraShortestPath(startNode, endNode);

            // preform typical dijkstra's algorithm with two given Node objects
            //navigationPath = computeDijkstraShortestPath(startNode, endNode);


        }
        // navigation between several regions
        else {

            // compute N-1 navigation paths for each region,
            // where N is the number of region to travel

            Log.i("bbb", "Navigation Graph Size " + navigationGraph.size());
            for (int i = 0; i < navigationGraph.size() - 1; i++) {

                // a destination vertex for each region
                Node destinationOfARegion = null;

                tmpDestinationID.clear();

                // the source vertex becomes a normal waypoint
                navigationGraph.get(i).nodesInSubgraph.get(sourceID)._nodeType = NORMAL_WAYPOINT;

                //If the elevation of the next region to travel is same as the current region
                if (regionPath.get(i)._elevation == regionPath.get(i + 1)._elevation) {

                    // compute a path to a transfer point of current region
                    // return the transfer point
                    destinationOfARegion = computePathToTraversePoint(
                            navigationGraph.get(i).nodesInSubgraph.get(sourceID), true, i + 1);

                    // sourceID is updated with the ID of transfer node for the next computation
                    // since the transfer node has the same ID in the same elevation
                    sourceID = destinationOfARegion.getID();

                }
                //If the elevation of the next region to travel is different from the current region
                else if (regionPath.get(i)._elevation != regionPath.get(i + 1)._elevation) {

                    Log.i("bbb", "region name " + regionPath.get(i)._regionID);
                    // compute a path to a transfer point(elevator or stairwell) of current region
                    // return the transfer point

                    //start point is a transfer point
                    if (startNodeType == Setting.getPreferenceValue() &&
                            find_SourceID_In_Next_Region(startNode._connectPointID, i + 1) != null) {
                        destinationOfARegion = startNode;

                        // get the connectPointID of the transfer node
                        connectPointID = destinationOfARegion._connectPointID;

                        sourceID = find_SourceID_In_Next_Region(connectPointID, i + 1);
                    } else {

                        String tmpSourceID = null;

                        // loop until find the correct source id for the next region
                        while (tmpSourceID == null) {

                            // if tmpDestinationID is not null, re-load navigation graph
                            // and change the waypoint into normal waypoint
                            if (tmpDestinationID.size() >= 1) {

                                loadNavigationGraph();

                                for (int count = 0; count < tmpDestinationID.size(); count++) {
                                    navigationGraph.get(i).nodesInSubgraph.get(tmpDestinationID.get(count))._nodeType = NORMAL_WAYPOINT;
                                }

                            }

                            destinationOfARegion = computePathToTraversePoint(
                                    navigationGraph.get(i).nodesInSubgraph.get(sourceID), false, i + 1);

                            // get the connectPointID of the transfer node
                            connectPointID = destinationOfARegion._connectPointID;

                            // find if the tmpDestination can connect to the next region
                            // if so, tmpSourceID is not null
                            // if not, tmpSourceID is null, then continue looping
                            tmpSourceID = find_SourceID_In_Next_Region(connectPointID, i + 1);

                            Log.i("bbb", "destination Of region " + destinationOfARegion._waypointName);

                        }

                        sourceID = tmpSourceID;

                        Log.i("bbb", "source ID in next region " + sourceID);

                    }

                }

                // add up all the navigation paths into one
                //navigationPath.addAll(getShortestPathToDestination(destinationOfARegion));
                path.addAll(getShortestPathToDestination(destinationOfARegion));

            }

            //Compute navigation path in the last region
            List<Node> pathInLastRegion = computeDijkstraShortestPath(
                    navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(sourceID),
                    endNode);

            Log.i("bbb", "Path in last region " + pathInLastRegion.size());

            // complete the navigation path
            //navigationPath.addAll(pathInLastRegion);
            path.addAll(pathInLastRegion);

            // remove duplicated waypoints which are used as connecting points in the same elevation
            for (int i = 1; i < path.size(); i++) {
                if (path.get(i)._waypointID.equals(path.get(i - 1)._waypointID))
                    path.remove(i);
            }
        }

        //Log.i("bbb", "path size"+navigationPath.size());

        for (int i = 0; i < path.size(); i++)
            navigationPath_ID_to_Name_Mapping.put(path.get(i)._waypointID,
                    path.get(i)._waypointName);
        //new DeviceParameter().setupDeviceParameter(this);
//        Queue<String> tmp_path = new LinkedList<>();
//        for (int i = 0; i<navigationPath.size(); i++) {
//            tmp_path.offer(navigationPath.get(i).getID());
//        }
        LBD.setpath(path);

        pathLength = GeoCalulation.getPathLength(path);

        Log.i("pathLength", "pathLength: " + pathLength);

        for (int i = 0; i < path.size(); i++)
            Log.i("path", path.get(i)._waypointName);

        //防止連續接收
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 1000);

        return path;

    }

    public String find_SourceID_In_Next_Region(int currentConnectID, int nextRegionIndex) {


        for (Entry<String, Node> entry : navigationGraph.get(nextRegionIndex).nodesInSubgraph.entrySet()) {

            Node v = entry.getValue();

            if (v._connectPointID == currentConnectID) {

                String id = v.getID();
                return id;
            }
        }

        return null;
    }


    // compute a shortest path with given starting point and destination
    public List<Node> computeDijkstraShortestPath(Node source, Node destination) {

        source.minDistance = 0.;
        PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
        nodeQueue.add(source);
        int destinationGroup = destination._mainID;

        while (!nodeQueue.isEmpty()) {
            Node v = nodeQueue.poll();

            Log.i("bbb", "In dijsk node name " + v._waypointName);

            if (destinationGroup != 0 && DirectCompute == false) {
                for(int i = 0 ; i < v._attachIDs.size(); i++) {
                    if (v._attachIDs.get(i) == destinationGroup) {
                        destination = navigationGraph.get(navigationGraph.size() - 1).nodesInSubgraph.get(v._waypointID);
                        Log.i("bbb", "destination is: " + destination._waypointName);
                        break;
                    }
                }
            }

            //Stop searching when reach the destination node
            if (v._waypointID.equals(destination._waypointID))
                break;
            // Visit each edge that is adjacent to v
            for (Edge e : v._edges) {
                Node a = e.target;
                Log.i("bbb", "node a " + a._waypointName);
                double weight = e.weight;
                double distanceThroughU = v.minDistance + weight;
                if (distanceThroughU < a.minDistance) {
                    nodeQueue.remove(a);
                    a.minDistance = distanceThroughU;
                    a.previous = v;
                    Log.i("bbb", "set previous");
                    nodeQueue.add(a);
                }
            }
        }
        Log.i("bbb", "destination is: " + destination._waypointName);

        return getShortestPathToDestination(destination);
    }

    // compute a shortest path from a given starting point to a transfer node (e.g., elevator, stairwell)
    public Node computePathToTraversePoint(Node source, Boolean sameElevation, int indexOfNextRegion) {

        Node backupTransferNode = null;
        boolean entered = false;
        source.minDistance = 0.;
        PriorityQueue<Node> nodeQueue = new PriorityQueue<Node>();
        nodeQueue.add(source);


        while (!nodeQueue.isEmpty()) {

            Node u = nodeQueue.poll();

            // Visit each edge exiting u
            for (Edge e : u._edges) {
                Node v = e.target;
                double weight = e.weight;
                double distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance) {
                    nodeQueue.remove(v);

                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    nodeQueue.add(v);
                }

                // if the elevation of the next region to travel is same as current region
                // find the nearest connect point and check if it is legal
                if (sameElevation == true && v._nodeType == CONNECTPOINT) {

                    // return v, only if the connect point is in the next region
                    if (navigationGraph.get(indexOfNextRegion).nodesInSubgraph.get(v._waypointID) != null)
                        return v;

                }

                // if the elevation of the next region to travel is different from current region
                // find the nearest elevator or stairwell based on user's preference
                else if (sameElevation == false && v._nodeType == getPreferenceValue()) {
                    tmpDestinationID.add(v._waypointID);
                    return v;
                } else if (sameElevation == false && v._nodeType != getPreferenceValue() && v._nodeType != NORMAL_WAYPOINT && entered == false) {
                    backupTransferNode = v;
                    entered = true;
                }
            }
        }

        if (backupTransferNode != null)
            return backupTransferNode;
        else
            return source;
    }


    // get shortest path by traversing previous waypoint back to the source
    public List<Node> getShortestPathToDestination(Node destination) {
        List<Node> path = new ArrayList<Node>();


        for (Node node = destination; node != null; node = node.previous) {
            Log.i("bbb", "get path " + node._waypointName);
            path.add(node);
        }


        // reverse path to get correct order 顛倒
        Collections.reverse(path);
        return path;
    }

    // popup window for turn direction notification
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    public void showPopupWindow(String popmsg){
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.unbind(this);
        Log.i("xxx_inpop","popstart");

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.popup, null);

        popupWindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        final Button popupButton = (Button) customView.findViewById(R.id.popupButton);
        TextView popupText = (TextView) customView.findViewById(R.id.popupText);
        TextView popupTitle = (TextView)customView.findViewById(R.id.popupText_waypointName);
            popupTitle.setText("提醒");
            popupTitle.setBackgroundColor(Color.BLUE);
            popupText.setText("請面對" + popmsg);
            tts.speak(popupText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            popupButton.setText("開始導航");

        popupButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    popupWindow.dismiss();
                    beaconManagerSetup();
                    readNavigationInstruction();
                    showBackImage();
            }
        });
        popupWindow.showAtLocation(positionOfPopup, Gravity.CENTER, 0, 0);

        imageTurnIndicator.setImageResource(R.drawable.up_now);
        nowDoInstruction.setText("請面對" + popmsg);

    }

    public void showHintAtWaypoint(final int instruction) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toast_layout));
        ImageView image = (ImageView) layout.findViewById(R.id.toast_image);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 25);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);

        // distance determine showhint time
        int DistanceForShowHint = 0;
        // if there are two or more waypoints to go
        //依距離顯示時間測試->到達的點會先被Remove掉
        if(navigationPath.size()>=2)
            DistanceForShowHint = (int) GeoCalulation.getDistance(navigationPath.get(0), navigationPath.get(1));


        //image.setImageResource(R.drawable.img_compass);
        String turnDirection = null;
        Vibrator myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);

        if (instruction == ARRIVED_NOTIFIER) {
            appendLog("EndNavigation");
            turnDirection = YOU_HAVE_ARRIVE;
            //image.setImageResource(R.drawable.arrived_image);
            //tts.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);
           // initToast(toast);
            myVibrator.vibrate(800);
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(NavigationActivity.this);
            dialog.setTitle("您已抵達目的地附近");
            dialog.setMessage("是否同意協助我們填寫導航滿意度調查問卷?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("不同意",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
            dialog.setNegativeButton("同意",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Uri uri = Uri.parse("https://forms.gle/5SHRm6utPDnYyFBW9");
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    //Intent i = new Intent(NavigationActivity.this, MainActivity.class);
                    //startActivity(i);
                    finish();
                }
            });
            dialog.show();
        } else if (instruction == WRONGWAY_NOTIFIER) {
            turnDirection = "正在幫您重新計算路線";
            //tts.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);
            Log.i("xxx_wrong", "wrongway");
            image.setImageResource(R.drawable.refresh);
            initToast(toast);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    toast.cancel();
                }
            }, 1000);
            myVibrator.vibrate(1000);
        } else if (instruction == MAKETURN_NOTIFIER) {
            //處理空白跳框情形
            // image.setImageResource(R.drawable.turn_back);
            Log.i("1227","turnNotififorPop = " + turnNotificationForPopup);
               switch (turnNotificationForPopup) {

                    case RIGHT:
                        nowDoInstruction.setText(NOW_TURN_RIGHT);
                        turnDirection = PLEASE_TURN_RIGHT;
                        imageTurnIndicator.setImageResource(R.drawable.right_now);
                        image.setImageResource(R.drawable.right_now);
                        LastisSlash = false;
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        SignalLog("跳出右轉指令");
                        break;
                    case LEFT:
                        nowDoInstruction.setText(NOW_TURN_LEFT);
                        turnDirection = PLEASE_TURN_LEFT;
                        imageTurnIndicator.setImageResource(R.drawable.left_now);
                        image.setImageResource(R.drawable.left_now);
                        LastisSlash = false;
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        SignalLog("跳出左轉指令");
                        break;
                    case FRONT_RIGHT:
                        turnDirection = PLEASE_TURN__FRONT_RIGHT;
                        if (LastisSlash == false) {
                            nowDoInstruction.setText(NOW_TURN__FRONT_RIGHT);
                            image.setImageResource(R.drawable.rightup_now);
                            imageTurnIndicator.setImageResource(R.drawable.rightup_now);
                            SignalLog("跳出右前指令");
                            LastisSlash = true;
                        } else {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT);
                            imageTurnIndicator.setImageResource(R.drawable.up_now);
                            image.setImageResource(R.drawable.up_now);
                            SignalLog("跳出直走指令");
                            LastisSlash = false;
                        }
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case FRONT_LEFT:
                        turnDirection = PLEASE_TURN_FRONT_LEFT;
                        if (LastisSlash == false) {
                            nowDoInstruction.setText(NOW_TURN_FRONT_LEFT);
                            imageTurnIndicator.setImageResource(R.drawable.leftup_now);
                            image.setImageResource(R.drawable.leftup_now);
                            SignalLog("跳出左前指令");
                            LastisSlash = true;
                        } else {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT);
                            imageTurnIndicator.setImageResource(R.drawable.up_now);
                            image.setImageResource(R.drawable.up_now);
                            SignalLog("跳出直走指令");
                            LastisSlash = false;
                        }
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case REAR_RIGHT:
                        nowDoInstruction.setText(NOW_TURN__REAR_RIGHT);
                        turnDirection = PLEASE_TURN__REAR_RIGHT;
                        imageTurnIndicator.setImageResource(R.drawable.rightdown_now);
                        image.setImageResource(R.drawable.rightdown_now);
                        LastisSlash = false;
                        SignalLog("跳出往右迴轉指令");
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case REAR_LEFT:
                        nowDoInstruction.setText(NOW_TURN__REAR_RIGHT);
                        turnDirection = PLEASE_TURN_REAR_LEFT;
                        imageTurnIndicator.setImageResource(R.drawable.leftdown_now);
                        image.setImageResource(R.drawable.leftdown_now);
                        LastisSlash = false;
                        SignalLog("跳出往左迴轉指令");
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case FRONT:
                        imageTurnIndicator.setImageResource(R.drawable.up_now);
                        nowDoInstruction.setText(NOW_GO_STRAIGHT);
                        turnDirection = PLEASE_GO_STRAIGHT;
                        image.setImageResource(R.drawable.up_now);
                        LastisSlash = false;
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        SignalLog("跳出直走指令");
                        break;
                    case FRONT_RIGHTSIDE:
                        turnDirection = PLEASE_GO_STRAIGHT_RIGHTSIDE;
                        if (LastisSlash == false) {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT_RIGHTSIDE);
                            imageTurnIndicator.setImageResource(R.drawable.up_rightside);
                            image.setImageResource(R.drawable.up_rightside);
                            SignalLog("跳出靠右指令");
                            LastisSlash = true;
                        } else {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT);
                            SignalLog("跳出直走指令");
                            imageTurnIndicator.setImageResource(R.drawable.up_now);
                            image.setImageResource(R.drawable.up_now);
                            LastisSlash = false;
                        }
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case FRONT_LEFTSIDE:
                        turnDirection = PLEASE_GO_STRAIGHT_LEFTSIDE;
                        if (LastisSlash == false) {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT_LEFTSIDE);
                            imageTurnIndicator.setImageResource(R.drawable.up_leftside);
                            image.setImageResource(R.drawable.up_leftside);
                            SignalLog("跳出靠左指令");
                            LastisSlash = true;
                        } else {
                            nowDoInstruction.setText(NOW_GO_STRAIGHT);
                            SignalLog("跳出直走指令");
                            imageTurnIndicator.setImageResource(R.drawable.up_now);
                            image.setImageResource(R.drawable.up_now);
                            LastisSlash = false;
                        }
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case ELEVATOR:
                        turnDirection = PLEASE_TAKE_ELEVATOR;
                        image.setImageResource(R.drawable.elevator);
                        LastisSlash = false;
                        SignalLog("跳出電梯指令");
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case STAIR:
                        turnDirection = PLEASE_WALK_UP_STAIR;
                        if (navigationPath.get(1)._elevation > navigationPath.get(0)._elevation)
                            image.setImageResource(R.drawable.stairs_up);
                        else
                            image.setImageResource(R.drawable.stairs_down);
                        LastisSlash = false;
                        SignalLog("跳出樓梯指令");
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        break;
                    case "goback":
                        turnDirection = " ";
                        imageTurnIndicator.setImageResource(R.drawable.turn_back);
                        image.setImageResource(R.drawable.turn_back);
                        SignalLog("跳出正後方迴轉指令");
                        Log.i("xxx_Direction", "跳出指令方向 = " + turnNotificationForPopup);
                        // setNowPostition();
                        break;

                }

        }
            tts.speak(turnDirection, TextToSpeech.QUEUE_ADD, null);
            Log.i("showHint", "showHint");
            if (turnNotificationForPopup != null) {
                Log.i("xxx_showhint", "DistanceForShowHint  = " + DistanceForShowHint);
                /*initToast(toast);
                if (DistanceForShowHint > 8) {
                    Log.i("xxx_showhint", "ShowHintLong");
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            initToast(toast);
                        }
                    }, 1000);
                }*/
                myVibrator.vibrate(new long[]{50, 100, 50}, -1);
            }

    }

    // voice engine read the navigation instruction shown on the screen
    public void readNavigationInstruction() {

        tts.speak(firstMovement.getText().toString() + howFarToMove.getText().toString() +
                nextTurnMovement.getText().toString(), TextToSpeech.QUEUE_ADD, null);

    }

    //Create a thread to handle the currently received Lbeacon ID
    class NavigationTread implements Runnable {

        @Override
        public void run() {

            // while the navigation path is not finished yet
            while (!navigationPath.isEmpty()) {

                // the thread waits for beacon manager to notify it when a new Lbeacon ID is received
                synchronized (sync) {
                    try {
                        sync.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.i("enter", "syncGo");

                // if the received ID matches the ID of the next waypoint in the navigation path
                if (navigationPath.get(0)._waypointID.equals(currentLBeaconID)) {
                    // three message objects send messages to corresponding handlers
                    Message messageFromInstructionHandler = instructionHandler.obtainMessage();
                    Message messageFromCurrentPositionHandler = currentPositiontHandler.obtainMessage();
                    Message messageFromWalkedPointHandler = walkedPointHandler.obtainMessage();
                    Message messageFromProgressHandler = progressHandler.obtainMessage();

                    // CurrentPositionHandler get the message of currently matched waypoint name
                    messageFromCurrentPositionHandler.obj = navigationPath.get(0)._waypointName;

                    // if the navigation path has more than three waypoints to travel
                    if (navigationPath.size() >= 3) {

                        // if the next two waypoints are in the same region as the current waypoint
                        // get the turn direction at the next waypoint
                        if (navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID) &&
                                navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID)) {
                            messageFromInstructionHandler.obj =
                                    getDirectionFromBearing(navigationPath.get(0),
                                            navigationPath.get(1), navigationPath.get(2));
                        }

                        // if the next two waypoints are not in the same region
                        // means that the next waypoint is the last waypoint of the region to travel
                        else if (!(navigationPath.get(1)._regionID.equals(navigationPath.get(2)._regionID))) {
                            messageFromInstructionHandler.obj = FRONT;
                        }

                        // if the current waypoint and the next waypoint are not in the same region
                        // transfer through elevator or stairwell
                        else if (!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))) {

                            if (navigationPath.get(0)._nodeType == ELEVATOR_WAYPOINT)
                                messageFromInstructionHandler.obj = ELEVATOR;
                            else if (navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFromInstructionHandler.obj = STAIR;
                            else if ((navigationPath.get(0)._nodeType == CONNECTPOINT))
                                messageFromInstructionHandler.obj =
                                        getDirectionFromBearing(navigationPath.get(0),
                                                navigationPath.get(1), navigationPath.get(2));
                            else if (navigationPath.get(0)._nodeType == NORMAL_WAYPOINT) {

                                if (Setting.getPreferenceValue() == ELEVATOR_WAYPOINT)
                                    messageFromInstructionHandler.obj = ELEVATOR;
                                else if (Setting.getPreferenceValue() == STAIRWELL_WAYPOINT)
                                    messageFromInstructionHandler.obj = STAIR;
                            }

                        }
                    }
                    // if there are two waypoints left in the navigation path
                    else if (navigationPath.size() == 2) {

                        // if the current waypoint and the next waypoint are not in the same region
                        if (!(navigationPath.get(0)._regionID.equals(navigationPath.get(1)._regionID))) {

                            if (navigationPath.get(0)._nodeType == ELEVATOR_WAYPOINT)
                                messageFromInstructionHandler.obj = ELEVATOR;
                            else if (navigationPath.get(0)._nodeType == STAIRWELL_WAYPOINT)
                                messageFromInstructionHandler.obj = STAIR;
                        }
                        // else go straight to the final waypoint
                        else
                            messageFromInstructionHandler.obj = FRONT;

                    }
                    // if there is only one waypoint left, the user has arrived
                    else if (navigationPath.size() == 1)
                        messageFromInstructionHandler.obj = ARRIVED;


                    // every time the received ID is matched,
                    // the user is considered to travel one more waypoint
                    walkedWaypoint++;

                    // WalkedPointHandler get the message of number
                    //of waypoint has been traveled in a region
                    messageFromWalkedPointHandler.obj = walkedWaypoint;

                    messageFromProgressHandler.obj = true;

                    // send the newly updated message to three handlers
                    walkedPointHandler.sendMessage(messageFromWalkedPointHandler);
                    instructionHandler.sendMessage(messageFromInstructionHandler);
                    currentPositiontHandler.sendMessage(messageFromCurrentPositionHandler);
                    progressHandler.sendMessage(messageFromProgressHandler);
                }
                // if the received ID does not match the ID of waypoint in the navigation path
                else if (!(navigationPath.get(0)._waypointID.equals(currentLBeaconID))) {

                    // send a "wrong" message to the handler
                    Message messageFromInstructionHandler = instructionHandler.obtainMessage();
                    messageFromInstructionHandler.obj = WRONG;
                    instructionHandler.sendMessage(messageFromInstructionHandler);

                }
            }
        }
    }

    // enter a waypoint ID to emulate the navigator receiving the corresponding Lbeacon ID (for demo)
    @RequiresApi(api = VERSION_CODES.JELLY_BEAN_MR2)
    public void enterWaypointID(View view) {

        if (popupWindow != null)
            popupWindow.dismiss();

        String nameOFWaypoint = waypointIDInput.getText().toString();
        String receiveID = null;
        Node receiveNode;
        Boolean pass = false;

        receiveID = mappingOfRegionNameAndID.get(nameOFWaypoint);
        receiveNode = allWaypointData.get(receiveID);

        currentLocationReminder.setText("目前位置 : " + receiveNode._waypointName);
        Log.i("receiveInfo", "ID: " + receiveNode._waypointID + " Region: " + receiveNode._regionID);

        if (isFirstBeacon && receiveNode != null) {
            Log.i("isFirst","isFirst");
            chosestartNode = receiveNode;
            sourceID = receiveNode._waypointID;
            sourceRegion = receiveNode._regionID;
            passedRegionID = sourceRegion;
            loadNavigationGraph();
            navigationPath = startNavigation();
            progressBar.setMax(navigationPath.size());


            //sourceID = destination
           for (int i = 0;i < chosestartNode._attachIDs.size();i++) {
                if ((endNode._mainID != 0 && endNode._mainID == chosestartNode._attachIDs.get(i))) {
                    Log.i("xxx_group","startNode = " + startNode._waypointName + "endNode =" + endNode._waypointName);
                    showHintAtWaypoint(ARRIVED_NOTIFIER);
                    isFirstBeacon = false;
                }
            }

            if(chosestartNode._waypointID.equals(endNode._waypointID)) {
                Log.i("xxx_group","startNode = " + chosestartNode._waypointName + "endNode =" + endNode._waypointName);
                showHintAtWaypoint(ARRIVED_NOTIFIER);
                isFirstBeacon = false;
            }





            appendLog("StartNavigation");

            //初始設定
            String initDirectionName = null;
            if(navigationPath.size() >= 1 && isFirstBeacon == true) {
                Log.i("xxx_initD","0 = " + navigationPath.get(0)._waypointName + "1 = " + navigationPath.get(1)._waypointName);
                initDirectionName = DataParser.getInitDirectionName(this, navigationPath.get(0)._waypointID, navigationPath.get(1)._waypointID);
                Log.i("xxx_initDirectionName", "" + initDirectionName);
                //顯示初始方向指令
                if(initDirectionName != null)
                    showPopupWindow(initDirectionName);
            }

            isFirstBeacon = false;


        }

        Log.i("receiveInfo", "navigationPath Size " + navigationPath.size());

        if (!receiveID.equals(currentLBeaconID)) {

            if (receiveNode._groupID == navigationPath.get(0)._groupID &&
                    receiveNode._groupID != 0) {
                //收到相同GroupID 強制將currentLBeaconID設成即將要收到的GroupID
                Log.i("enter", "1");
                currentLBeaconID = navigationPath.get(0)._waypointID;
                pass = true;
            } else if (receiveNode._groupID == passedGroupID && receiveNode._groupID != 0) {
                Log.i("enter", "2");
                pass = false;
            } else {

                Log.i("enter", "3");
                currentLBeaconID = receiveID;
                pass = true;
            }


        } else {

            Log.i("enter", "4");
            pass = false;

        }

        if (pass) {

            appendLog(navigationPath.get(0)._waypointName);
            synchronized (sync) {

                Log.i("enter", "sync");
                sync.notify();

            }
        }

    }

    public void clearEditText(View v) {

        waypointIDInput.getText().clear();
    }

    private void initToast(Toast toast) {
        toast.show();
    }

    private void closeImage(){
        //關閉後面圖示顯示UI
        nowDoInstruction.setVisibility(View.INVISIBLE);
        firstMovement.setVisibility(View.INVISIBLE);
        howFarToMove.setVisibility(View.INVISIBLE);
        nextTurnMovement.setVisibility(View.INVISIBLE);
        imageTurnIndicator.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        progressNumber.setVisibility(View.INVISIBLE);
    }

    private void showBackImage() {
        nowDoInstruction.setVisibility(View.VISIBLE);
        firstMovement.setVisibility(View.VISIBLE);
        howFarToMove.setVisibility(View.VISIBLE);
        nextTurnMovement.setVisibility(View.VISIBLE);
        imageTurnIndicator.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressNumber.setVisibility(View.VISIBLE);
    }

    private void ShowDirectionFromConnectPoint(){
        if(navigationPath.get(0)._connectPointID == 0)
            isInVirtualNode = false;

       // Log.i("xxx_stair","nowAt = " + receivebeacon);
        //Log.i("xxx_stair","LastNode = " + lastNode._waypointName + " navigationPath(0) = " + navigationPath.get(0)._waypointName);
        //選擇的起始點不是目前位置
        if(chosestartNode._waypointID != navigationPath.get(0)._waypointID) {
            //收到的ConnectID != 0 目前與下個點的conectID相同，進入樓梯階段
            if (navigationPath.get(0)._connectPointID != 0 && navigationPath.get(1)._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == false) {
                //判斷上下樓
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                    StairGoUp = true;
                }else{
                    StairGoUp = false;
                }//找到對應VirtualNode
                for (int i = 0; i < virtualNodeUp.size(); i++) {
                    if((virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID)){
                        if(StairGoUp == true){
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeDown.get(i));
                        }else {
                            turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeUp.get(i));
                        }
                        if(FirstTurn == false && CallDirectionInWrong == false)
                           showHintAtWaypoint(MAKETURN_NOTIFIER);
                        isInVirtualNode = true;
                    }
                }
            }//離開樓梯階段
            else if (navigationPath.get(0)._connectPointID != 0 && lastNode._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == true) {
                //判斷上下樓
                if(navigationPath.get(0)._elevation > lastNode._elevation) {
                    StairGoUp = true;
                }else{
                    StairGoUp = false;
                }
                for (int i = 0; i < virtualNodeUp.size(); i++) {
                    if((virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID)) {
                        if(StairGoUp == true){
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeUp.get(i), navigationPath.get(0), navigationPath.get(1));
                        }else {
                            turnNotificationForPopup = getDirectionFromBearing(virtualNodeDown.get(i), navigationPath.get(0), navigationPath.get(1));
                        }
                    }
                }
            }
        }//選擇起始點是目前位置，且進入樓梯
        else if(chosestartNode._connectPointID != 0 && chosestartNode._waypointID == navigationPath.get(0)._waypointID && navigationPath.get(0)._connectPointID == navigationPath.get(1)._connectPointID) {
                 //判斷是往上或往下走
                if(navigationPath.get(1)._elevation > navigationPath.get(0)._elevation) {
                    StairGoUp = true;
                }else{
                    StairGoUp = false;
                }
            for (int i = 0; i < virtualNodeUp.size(); i++) {
                //找到與目前位置相同ConnectID
                if (virtualNodeDown.get(i)._connectPointID == navigationPath.get(0)._connectPointID && isInVirtualNode == false) {
                    if(StairGoUp == true){
                        turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeDown.get(i));
                    }else{
                        turnNotificationForPopup = getDirectionFromBearing(lastNode, navigationPath.get(0), virtualNodeUp.get(i));
                    }
                    if(FirstTurn == false && CallDirectionInWrong == false)
                        showHintAtWaypoint(MAKETURN_NOTIFIER);
                    isInVirtualNode = true;
                }
            }
        }
        CallDirectionInWrong = false;
    }


    public void appendLog(String text)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss - ");
        Date date = new Date(System.currentTimeMillis());
        simpleDateFormat.format(date);
        File logFile = new File("sdcard/logfile.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            Writer buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile,true),"UTF-8"));
            buf.append( simpleDateFormat.format(date).toString());
            buf.append(text + "\n");
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void SignalLog(String text)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss - ");
        Date date = new Date(System.currentTimeMillis());
        simpleDateFormat.format(date);
        File logFile = new File("sdcard/signalLog.txt");
        if (!logFile.exists())
        {
            try
            {
                logFile.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            Writer buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile,true),"UTF-8"));
            buf.append( simpleDateFormat.format(date).toString());
            buf.append(text + "\n");
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        appendLog("EndNavigation");
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_home){
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            Intent intent = new Intent();
            intent = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        /*
        if(item.getItemId() == R.id.menu_previous_page){
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
            beaconManager.unbind(NavigationActivity.this);
            Intent intent = new Intent();
            intent = new Intent(NavigationActivity.this, ListViewActivity.class);
            intent.putExtra("Category", "各科門診");
            startActivity(intent);
            this.finish();
        }*/

        return super.onOptionsItemSelected(item);
    }

}
