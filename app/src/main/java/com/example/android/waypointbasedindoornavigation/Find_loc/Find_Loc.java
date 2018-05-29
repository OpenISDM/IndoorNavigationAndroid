package com.example.android.waypointbasedindoornavigation.Find_loc;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Find_Loc {
    private List<String> researchdata = new ArrayList<>();
    private ana_signal as = new ana_signal();
    private Queue<List<String>> data_queue = new LinkedList<>();
    private DateFormat df = new SimpleDateFormat("h:mm:ss.SSS");
    private List<String> tmp_back = new ArrayList<>();
    private Queue<String> path_queue = new LinkedList<>();
    private File file;
    private int algo_num = 3;
    public void setpath(Queue path_queue){
        this.path_queue = path_queue;
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
        if (ana_switch && !path_queue.isEmpty()){
            data_queue.add(data_list);
            if (data_queue.size() == 10) {
                researchdata.addAll(as.ana_signal(data_queue,algo_num,1));
                Log.i("LBD",researchdata.toString());
                data_queue.clear();
                wrtieFileOnInternalStorage("reLog.txt","LBD:"+researchdata.toString()
                        +"\t"+path_queue.toString());
                if(researchdata.get(1).equals(path_queue.peek())
                        && researchdata.get(2).equals("close")
                        && researchdata.get(1).equals(researchdata.get(3))){
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
        Log.d("Msg0", String.valueOf(file.exists()));
        if(!file.exists()){
            try
            {
                file.createNewFile();
                file.setExecutable(true,false);
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
            Log.d("Msg2", "success"+file.getAbsolutePath());
        }catch (Exception e){
            Log.d("Msg3", "fail"+file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
