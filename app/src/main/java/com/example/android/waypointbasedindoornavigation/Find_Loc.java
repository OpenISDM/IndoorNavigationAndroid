package com.example.android.waypointbasedindoornavigation;

import android.os.Environment;
import android.util.Log;

import org.altbeacon.beacon.Beacon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Find_Loc {
    private List<String> researchdata = new ArrayList<>();
    private ana_signal as = new ana_signal();
    private Queue<List<String>> data_queue = new LinkedList<>();
    private DateFormat df = new SimpleDateFormat("h:mm:ss.SSS");
    private List<String> tmp_back = new ArrayList<>();
    private Queue<List<String>> path_queue = new LinkedList<>();
    private File file;
    private int algo_num = 2;
    public void setpath(Queue path_queue){
        this.path_queue = path_queue;
    }
    public void set_algo_num(int algo_num){
        this.algo_num = algo_num;
    }
    public List<String> Find_Loc(Beacon beacon, boolean ana_switch){
//    public List<String> logBeaconData(Beacon beacon, boolean ana_switch){
        String[] beacondata = new String[]{
                beacon.getId1().toString(),
                beacon.getId2().toString(),
                beacon.getId3().toString(),
                String.valueOf(beacon.getRssi()),
                String.valueOf(beacon.getDistance()),
                String.valueOf(beacon.getBeaconTypeCode()),
                String.valueOf(beacon.getIdentifiers())
        };
        researchdata.clear();
        researchdata.add(beacondata[1].concat(beacondata[2]));
        List<String> data_list = Arrays.asList(beacondata[1].concat(beacondata[2]),beacondata[3]);
//        Log.i("LBD",data_list.toString());
        if (ana_switch && !path_queue.isEmpty()){
            data_queue.add(data_list);
            if (data_queue.size() == 10) {
//                researchdata.add(as.ana_signal_1(data_queue));
                researchdata.add(as.ana_signal_2(data_queue, 5));
                researchdata.add(as.ana_signal_3(data_queue, 5));
//                Log.i("LBD2",researchdata.toString());
                data_queue.clear();
//                wrtieFileOnInternalStorage("Log.txt","LBD:"+researchdata.toString()
//                        +"\t"+path_queue.toString());
                if(researchdata.get(algo_num).equals(path_queue.peek())){
                    path_queue.poll();
                    tmp_back.clear();
                    tmp_back.addAll(researchdata);
//                    wrtieFileOnInternalStorage("Log.txt","LBD2:"+researchdata.toString()
//                            +"\t"+path_queue.toString());
                    Log.i("LBD2", researchdata.toString()+"\t"+path_queue.toString());
                    return (researchdata);
                }
                else {
                    Log.i("LBD3", tmp_back.toString());
//                    wrtieFileOnInternalStorage("Log.txt","LBD3:"+tmp_back.toString());
                    return tmp_back;
                }
            }
        }
//        Log.i("LBD2",researchdata.toString());
//        wrtieFileOnInternalStorage("Log.txt","LBD2:"+researchdata.toString());
        return researchdata;
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
//            Log.i("sBody", sBody);
            buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(sBody);
            buf.newLine();
            buf.close();
//            Log.d(Tag, "success"+file.getAbsolutePath());
        }catch (Exception e){
//            Log.d(Tag, "fail"+file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
