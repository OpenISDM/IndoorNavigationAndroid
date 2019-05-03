package eos.waypointbasedindoornavigation;

/*--

Module Name:

    MainActivity.java

Abstract:

    This module create the UI of start screen

Author:

    Phil Wu 01-Feb-2018

--*/

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import eos.waypointbasedindoornavigation.Find_loc.DeviceParameter;
import eos.waypointbasedindoornavigation.Find_loc.Find_Loc;

import org.altbeacon.beacon.BeaconManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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
    boolean ButtonClicked = false;
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
    Button btn_stethoscope, btn_bill, btn_exit, btn_medicent, btn_convenience_store, btn_wc,btn_exsanguinate,btn_examination_room,btn_other;
    TextView tv_description;
    TextView localVersionText = null;
    //PHP
    String url = "http://140.125.45.113/test/index.php";// 要加上"http://" 否則會連線失敗
    String phpVersion = null;
    String VersionCode = "1.0.2";
    private static int count = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_regulate,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /* if(item.getItemId() == R.id.gear){
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("系統");
            dialog.setMessage("是否開始進行訊號校正?");
            dialog.setCancelable(false);
            dialog.setNegativeButton("確定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent intent = new Intent();
                    intent = new Intent(MainActivity.this, initSignal.class);
                    startActivity(intent);
                }
            });
            dialog.setPositiveButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });

            dialog.show();
        }else*/
            if(item.getItemId() == R.id.information){
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
        Log.i("Main_Create_Mem", "usedMemory: Heap/Allocated Heap "+ Debug.getNativeHeapSize() + "/" + Debug.getNativeHeapAllocatedSize());

        if(count == 0) {//僅在首次開啟app時檢查
            count = 1;
            //檢測網路權限
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) { //網路有開啟，檢查擷取php版本
                new Thread(runnable).start();//啟動執行序runnable
            }
        }
      //檢查藍芽權限
        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( enableIntent, REQUEST_ENABLE_BT ); }

        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1001);

        //BLE權限
        if(!getPackageManager().hasSystemFeature(getPackageManager().FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "您的裝置不支援BLE藍牙功能！", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 定位權限要求
       LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!providerEnabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("系統");
            dialog.setMessage("請開啟定位權限");
            dialog.setCancelable(false);
            dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    Intent locationintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(locationintent);
                }
            });
            dialog.show();

        }



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
        btn_convenience_store = (Button)findViewById(R.id.btn_convenience_store);
        btn_wc = (Button)findViewById(R.id.btn_wc);
        btn_examination_room = (Button)findViewById(R.id.btn_examination_room);
        btn_other = (Button)findViewById(R.id.btn_other);
        btn_exsanguinate = (Button)findViewById(R.id.btn_exsanguinate);
        tv_description = (TextView)findViewById(R.id.tv_description);
        localVersionText = (TextView)findViewById(R.id.VersiontextView);
        localVersionText.setText("v" + VersionCode);
        loadLocationDatafromRegionGraph();
        List<Node> data = Collections.emptyList();
        for(int i = 0 ; i < categoryList.size() ; i++) {
            Log.i("xxx_List", "Categorylist = " + categoryList.get(i));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDestroy() {
        btn_stethoscope.setBackground(null);
        btn_bill.setBackground(null);
        btn_exit.setBackground(null);
        btn_medicent.setBackground(null);
        btn_convenience_store.setBackground(null);
        btn_wc.setBackground(null);
        btn_examination_room.setBackground(null);
        btn_other.setBackground(null);
        btn_exsanguinate.setBackground(null);
        tv_description.setBackground(null);
        System.gc();
        Log.i("Main_Destroy_Mem", "usedMemory: Heap/Allocated Heap "+ Debug.getNativeHeapSize() + "/" + Debug.getNativeHeapAllocatedSize());
        super.onDestroy();
    }

    public void resetSignal(View view){
        //取得目前offset設定值
        //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //float offset = pref.getFloat("offset",1);

        //寫入offset初始值至內存
        double offset = 0;
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

    public void onClick(View view) {
        if(ButtonClicked == false){
        switch (view.getId()) {
            case R.id.btn_stethoscope:
                ButtonClicked = true;
                //取出listForStoringAllNodes中的Category與各科門診相同的Node加至CList
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("各科門診")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                //如果該種類只有一種，直接導入導航頁面，否則導入目的地選單
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "各科門診");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_examination_room:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("檢查室")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "檢查室");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_other:
                ButtonClicked = true;
                intent = new Intent(MainActivity.this, ListViewActivity.class);
                intent.putExtra("Category", "其他");
                startActivity(intent);
                finish();
                break;

            case R.id.btn_bill:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("批價櫃檯")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "批價櫃檯");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_medicent:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("領藥處")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "領藥處");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.btn_wc:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("廁所")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "廁所");
                    startActivity(intent);
                    finish();
                }
                break;
            //出口
            case R.id.btn_exit:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("出口")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "出口");
                    startActivity(intent);
                    finish();
                }
                break;
            //萊爾富
            case R.id.btn_convenience_store:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("超商")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "超商");
                    startActivity(intent);
                    finish();
                }
                break;
            //抽血處
            case R.id.btn_exsanguinate:
                ButtonClicked = true;
                for (int i = 0; i < listForStoringAllNodes.size(); i++) {
                    Log.i("asdd", listForStoringAllNodes.get(i)._category);
                    if (listForStoringAllNodes.get(i)._category.equals("抽血處")) {
                        Log.i("asdd", listForStoringAllNodes.get(i)._category + "2");
                        CList.add(listForStoringAllNodes.get(i));
                    }
                }
                if (CList.size() == 1) {
                    destinationName = CList.get(0)._waypointName;
                    destinationID = CList.get(0)._waypointID;
                    destinationRegion = CList.get(0)._regionID;
                    Intent i = new Intent(MainActivity.this, NavigationActivity.class);
                    i.putExtra("destinationName", destinationName);
                    i.putExtra("destinationID", destinationID);
                    i.putExtra("destinationRegion", destinationRegion);
                    startActivity(i);
                    finish();
                } else if (CList.size() > 1) {
                    intent = new Intent(MainActivity.this, ListViewActivity.class);
                    intent.putExtra("Category", "抽血處");
                    startActivity(intent);
                    finish();
                }
                break;

        }
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

    //PHP
    Handler handler_Success = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");//取出key中的字串存入val
            //Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show();
            try {
                phpVersion = getVersionFormphp(val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("xxx_update","phpVersion = " + phpVersion);
            if(!VersionCode.equals(phpVersion) && phpVersion != null){

                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("系統");
                dialog.setMessage("偵測到有最新版本，是否進行更新?(建議使用Wi-Fi下載)");
                dialog.setCancelable(false);
                dialog.setPositiveButton("確定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://drive.google.com/drive/folders/1imN5pw_g38IMHgdqT98hlHUX_unKz4X0?fbclid=IwAR2eHY-TVgPM1G4yRWs_H7SG8O-egQnKiMygWwQe4mGqrrBP7cJnugnXZ7Y"));
                        startActivity(intent);
                    }
                });
                dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
                dialog.show();
            }

        }

    };

    Handler handler_Error = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");
            Log.i("xxx_update","" + val);
        }
    };

    Handler handler_Nodata = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("key");
            Log.i("xxx_update","" + val);
        }
    };

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            //
            // TODO: http request.
            //
            Message msg = new Message();
            Bundle data = new Bundle();
            msg.setData(data);
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost method = new HttpPost(url);//連線到 url網址
                HttpResponse response = httpclient.execute(method);
                HttpEntity entity = response.getEntity();

                if(entity != null){
                    data.putString("key", EntityUtils.toString(entity));//如果成功將網頁內容存入key
                    handler_Success.sendMessage(msg);
                }
                else{
                    data.putString("key","無資料");
                    handler_Nodata.sendMessage(msg);
                }
            }
            catch(Exception e){
                data.putString("key","連線失敗");
                handler_Error.sendMessage(msg);
            }

        }
    };

    public String getVersionFormphp(String all) throws JSONException {

        JSONObject jsonObject = new JSONObject(all);
        String version = jsonObject.getString("version");

        return version;
    }
}




