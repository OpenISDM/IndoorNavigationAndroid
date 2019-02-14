package eos.waypointbasedindoornavigation.Find_loc;

import android.util.Log;

import eos.waypointbasedindoornavigation.Node;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Find_Loc {
    private List<String> researchdata = new ArrayList<>();
    private ana_signal as = new ana_signal();
    private Queue<List<String>> data_queue = new LinkedList<>();
    private int algo_num = 3;
    private int weight_type = 3;
    private ReadWrite_File wf = new ReadWrite_File();
    private long startT = System.currentTimeMillis();
    private DeviceParameter dp = new DeviceParameter();
    public void setpath(List<Node> tmp_queue) {as.set_path(tmp_queue);}
    public void set_allWaypointData(HashMap<String, Node> allWaypointData){
        as.set_allWaypointData(allWaypointData);
        dp.set_allWaypointData(allWaypointData);
    }
    public List<String> Find_Loc(Beacon beacon, float remind_range, double offset){
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
        List<String> data_list = Arrays.asList((beacondata[1].concat(beacondata[2])),beacondata[3]);
        if(dp.our_Beacon(beacondata[1].concat(beacondata[2]))){
                data_queue.add(data_list);
                long endT = System.currentTimeMillis();
                if ((endT-startT)>1000){
                    startT = System.currentTimeMillis();
                    researchdata.addAll(as.ana_signal(data_queue,algo_num,weight_type, remind_range, offset));
//                    wf.writeFile("LBD:"+data_queue.toString() +"\t"
//                            +String.valueOf(data_queue.size()));
                    Log.i("LBD1",data_queue.toString());
                    Log.i("LBD2",researchdata.toString());
                    data_queue.clear();
                return researchdata;
            }
        }
        return researchdata;
    }
}