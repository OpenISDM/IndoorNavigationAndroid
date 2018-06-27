package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.content.Context;
import android.util.Log;

import com.example.android.waypointbasedindoornavigation.GeoCalulation;

import org.altbeacon.beacon.Beacon;
import com.example.android.waypointbasedindoornavigation.Node;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Find_Loc {
    private List<String> researchdata = new ArrayList<>();
    private ana_signal as = new ana_signal();
    private Queue<List<String>> data_queue = new LinkedList<>();
    private List<String> tmp_back = new ArrayList<>();
    private int algo_num = 3;
    private int weight_type = 3;
    private ReadWrite_File wf = new ReadWrite_File();
    private long startT = System.currentTimeMillis();
    private DeviceParameter dp = new DeviceParameter();
    public void setpath(Node[] tmp_queue) {
        as.set_path(tmp_queue);
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
        if (ana_switch){
                data_queue.add(data_list);
                long endT = System.currentTimeMillis();
                if ((endT-startT)>1900){
                    startT = System.currentTimeMillis();
//                Log.i("LBD_time", String.valueOf(endT)+"\t"
//                        +String.valueOf(startT)+"\t"+String.valueOf(endT-startT));
//                Log.i("LBD_queue", String.valueOf(data_queue.size()));
//                    as.set_distance(GeoCalulation.getDistance(path_queue.get(0),path_queue.get(1)));
                    researchdata.addAll(as.ana_signal(data_queue,algo_num,weight_type));
                    wf.writeFile("LBD:"+data_queue.toString() +"\t"
                            +String.valueOf(data_queue.size()));
                    Log.i("LBD",researchdata.toString());
                data_queue.clear();
                if(researchdata.get(2).equals("close")
                        && researchdata.get(1).equals(researchdata.get(3))){
//                    path_queue.remove(0);
                    tmp_back.clear();
                    tmp_back.addAll(researchdata);
                    wf.writeFile("LBD2:"+researchdata.toString());
                    Log.i("LBD2", researchdata.toString());
                    return researchdata;
                }
                else {
                    Log.i("LBD3", tmp_back.toString());
                    wf.writeFile("LBD3:"+tmp_back.toString());
                    return tmp_back;
                }
            }
        }
//        Log.i("LBD2",researchdata.toString());
//        wrtieFileOnInternalStorage("Log.txt","LBD2:"+researchdata.toString());
        return researchdata;
    }
}
