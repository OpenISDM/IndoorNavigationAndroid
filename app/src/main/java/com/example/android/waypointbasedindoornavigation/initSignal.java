package com.example.android.waypointbasedindoornavigation;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.waypointbasedindoornavigation.Find_loc.DeviceParameter;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

import static java.lang.Thread.sleep;


public class initSignal extends AppCompatActivity implements BeaconConsumer {
    //    turn on bluetooth
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    protected final String Tag = "BeaconSearch";
    private HashMap<String,Integer> map = new HashMap<String,Integer>();
    private HashMap<String,Double> rssimap = new HashMap<String,Double>();
    private ArrayList<Double> list = new ArrayList<Double>();
    int max = 0;
    //    flash screen
    private static Handler mHandler;
    //    time when receive beacon
    private DateFormat df = new SimpleDateFormat("h:mm:ss.SSS");
    //    UI text
    private TextView showtxt,showlocation;
    private ScrollView scrollView;
    private String researchdata,get_location="";
    private Button turnBack;
    private int i = 0;
    private String UUID;
    private long startT;
    private long endT;
    //    write out data
    private File file;
    private int write_location_index;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    //    Beacon manager for ranging Lbeaon signal
    private BeaconManager beaconManager;
    private Region region;
    private int ScanPeriod = 1000,SleepTime = 2000;
    private Boolean testcolorchangemsg = true,temp_msg = true;
    private Queue<List<String>> data_queue = new LinkedList<>();
    private analysis_signal as = new analysis_signal();
    private UUIDtoID trtoid = new UUIDtoID();
    private String maxUuid;

    //DeviceParameter
    private static DeviceParameter dp = new DeviceParameter();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_signal);
//        init UI objects
        showtxt = (TextView)findViewById(R.id.textView1);
        showlocation = (TextView)findViewById(R.id.locationtext);
        scrollView = (ScrollView) findViewById(R.id.scrollview1);
        turnBack = (Button) findViewById(R.id.back);
        mHandler = new Handler(); //UI text flash
//        Beacon manager setup
        beaconManager =  BeaconManager.getInstanceForApplication(this);
//        Detect the LBeacon frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-15,i:16-19,i:20-23,p:24-24"));

//        setBeaconLayout("m:2-3=0215,i:4-19,i:20-23,i:24-27,p:28-28"));
//        Detect the Eddystone main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));

//         Detect the Eddystone telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

//         Detect the Eddystone URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20"));

        beaconManager.setForegroundScanPeriod(ScanPeriod);
        beaconManager.setForegroundBetweenScanPeriod(SleepTime);
        region = new Region("justGiveMeEverything", null, null, null);
        bluetoothManager = (BluetoothManager)
                getSystemService(Context.BLUETOOTH_SERVICE);
        ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, 1001);
        beaconManager.unbind(this);

        startT = System.currentTimeMillis();
        ScanPeriod = 100;
        SleepTime = 0;
        beaconManager.setForegroundScanPeriod(ScanPeriod);
        beaconManager.setForegroundBetweenScanPeriod(SleepTime);
        showtxt.setText("");
        write_location_index = 0;
        beaconManager.bind(initSignal.this);
//        beaconManager.bind(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    public void onBeaconServiceConnect() {
//Start scanning for Lbeacon signal
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i("AAA","Beacon Size:"+beacons.size());
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
            beaconManager.startRangingBeaconsInRegion(
                    new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 1:
                    //showtxt.append(researchdata+"\n");
                    showlocation.setText("Now at :"+trtoid.trUUID(get_location));
                    Log.i("Beacon UUID", trtoid.trUUID(get_location).toString());
                    scrollView.fullScroll(View.FOCUS_DOWN);

                    /*
                    i++;
                    if(i>100) {
                        showtxt.setText("");
                        i=0;
                    }
                    */

                    break;
                default:
                    break;
            }
        }
    };

    //    Parser Beacon data
    private void logBeaconData(Beacon beacon) {



        String[] beacondata = new String[]{
                beacon.getId1().toString(),
                beacon.getId2().toString(),
                beacon.getId3().toString(),
                String.valueOf(beacon.getRssi()),
                String.valueOf(beacon.getDistance()),
                String.valueOf(beacon.getBeaconTypeCode()),
                String.valueOf(beacon.getIdentifiers())

        };
        endT = System.currentTimeMillis();
        String date = df.format(Calendar.getInstance().getTime());
        researchdata = beacondata[1]+" "+beacondata[2]+"\t"+date+"\t"+beacondata[3];
        Log.i("researchdata",beacondata[1]+" "+beacondata[2]+"\t"+date+"\t"+beacondata[3]);
        /***************************************************/


        UUID = beacondata[1]+beacondata[2];
        UUID = trtoid.ptr(UUID); //find the corresponding name to uuid
        String rssi = beacondata[3];

        //if map have the corresponding uuid than +1 else put 1 and find the max value
        if(map.containsKey(UUID)){
            map.put(UUID,map.get(UUID)+1);
            if(map.get(UUID) > max) max = map.get(UUID);
            Log.i("max",max+"");
            //Log.i("max value",map.get(max).toString());
        }else{
            map.put(UUID,1);
        }

        if(rssimap.containsKey(UUID)){
            rssimap.put(UUID,rssimap.get(UUID)+Double.parseDouble(rssi));
        }else{
            rssimap.put(UUID,Double.parseDouble(rssi));
        }

        // Log.i("map UUID",map.get(UUID).toString());





        /***************************************************/
        List<String> data_list = Arrays.asList(beacondata[1].concat(beacondata[2]),beacondata[3]);

        Message msg = new Message();
        msg.what = 1;
        mHandler2.sendMessage(msg);
//        Log.i("AAA","beacon:"+researchdata);
        Log.e("endTime",endT+"");
        Log.e("startTime",startT+"");
        if ((endT-startT)>5000){
            startT = System.currentTimeMillis();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //print the uuid and rssi
                    beaconManager.unbind(initSignal.this);
                    showtxt.setText("");
                    showtxt.append("-----------------------------------------RSSI----------------------------------------- "+'\n');
                    for (Object key : rssimap.keySet()) {
                        Log.e("*RSSI / count",Double.valueOf(rssimap.get(key))+"");
                        Log.e("RSSI / *count",Double.valueOf(map.get(key))+"");

                        //count the average of rssi
                        double rssi = Math.abs(Double.valueOf(rssimap.get(key)) / Double.valueOf(map.get(key)));
                        list.add(rssi);
                        Log.e("ListData",list+"");
                    }
                    Collections.sort(list);
                    Log.e("ListData Sort",list+"");
                    Log.e("keysize()",list.size()+"");

                    //find the uuid corresponding to rssi
                    for(int i=0; i<list.size(); i++){
                        for (Object key : rssimap.keySet()) {
                            //  Log.e("(2)*RSSI / count",Double.valueOf(rssimap.get(key))+"");
                            //Log.e("(2)RSSI / *count",Double.valueOf(map.get(key))+"");
                            Log.e("rssimap / count  = ", String.valueOf(Math.abs(Double.valueOf(rssimap.get(key)))+"   /   "+ Double.valueOf(map.get(key))) +" = " +Math.abs(Double.valueOf(rssimap.get(key)) / Double.valueOf(map.get(key))));
                            Log.e("list.get(i)", String.valueOf(list.get(i)));
                            if(Math.abs(Double.valueOf(rssimap.get(key)) / Double.valueOf(map.get(key))) == list.get(i)){
                                showtxt.append("UUID =  " + key + " RSSI  = " + String.format("%.2f ", Double.valueOf(rssimap.get(key)) / Double.valueOf(map.get(key))) + '\n');

                                if(i==0) //get the uuid of highest rssi
                                    maxUuid = key.toString();

                            }
                        }
                    }

                    showtxt.append("MaxUUID =  " + maxUuid + '\n');
                    showtxt.append(" "+'\n');
                    showtxt.append("--------------------------------Paclage Count---------------------------------- "+'\n');
                    for(int i=max; i >= 0; i--) {
                        for (Object key : map.keySet()) {
                            if (map.get(key) == i) {
                                showtxt.append("UUID =  " + key + " count  = " + map.get(key).toString() + '\n');
                                Log.i("UUID", key + "");
                                Log.i("count", map.get(key) + "");
                            }
                        }
                    }

                    countOffset(maxUuid,3);
                    showtxt.append(" "+'\n');
                    GlobalVariable gv = (GlobalVariable)getApplicationContext();
                    double n = gv.getOffset();
                    showtxt.append("調整比率 = " + String.format("%.2f ", Double.valueOf(n)) + '\n');
                    showtxt.append("調整完成"+'\n');
                    showtxt.append(" "+'\n');
                    list.clear();
                    rssimap.clear();
                    map.clear();
                    max = 0;

                    turnBack.setVisibility(View.VISIBLE);

                }
            });

        }
    }

    public void countOffset(String uuid,double range)
    {
        double R0 = dp.get_R0(uuid);
        double n_vlaue = dp.get_n(uuid);
        double estimate = R0+(10*n_vlaue*Math.log10(range/1.5));
        double actualRssi = list.get(0);
        double offset;
        GlobalVariable gv = (GlobalVariable)getApplicationContext();
        if(dp.our_Beacon(uuid))
        {
            R0 = dp.get_R0(uuid);
            n_vlaue = dp.get_n(uuid);
            estimate = R0+(10*n_vlaue*Math.log10(range/1.5));
            actualRssi = list.get(0);
            offset = actualRssi / estimate;
            gv.setOffset(offset);
        }
        else
            gv.initOffset();

    }
    public void Clickevent(View view){
        switch (view.getId()){
            case R.id.back:
                this.finish();
                break;
        }
    }
    public void wrtieFileOnInternalStorage(String sFileName, String sBody){
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        file = new File(path,sFileName);
        BufferedWriter buf;
        if(!file.exists()){
            try
            {
                file.createNewFile();
                file.setExecutable(true,false);
                buf = new BufferedWriter(new FileWriter(file, true));
                buf.append("ScanPeriod:"+ScanPeriod+"\tSleepTime:"+SleepTime);
                buf.newLine();
                buf.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try{
            buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(sBody);
            buf.newLine();
            buf.close();
            Log.d(Tag, "success"+file.getAbsolutePath());
        }catch (Exception e){
            Log.d(Tag, "fail"+file.getAbsolutePath());
            e.printStackTrace();
        }
    }
    public String bytesToHex(byte[] bytes) {

        char[] hexArray = "0123456789ABCDEF".toCharArray();

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0;
        }
        double ratio = rssi * 1.0 / txPower;

        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double accuracy = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
            return accuracy;
        }
    }


}
