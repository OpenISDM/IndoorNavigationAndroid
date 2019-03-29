package eos.waypointbasedindoornavigation.Find_loc;

import android.util.Log;

import eos.waypointbasedindoornavigation.Node;

import org.altbeacon.beacon.Beacon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
}


//new version of LBeacon Format uuid search
/*
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
    }*/
 /*   public List<String> Find_Loc(Beacon beacon, float remind_range, double offset){
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

        String hexString = beacondata[0].concat(beacondata[1]);

        hexString = hexString.substring(2, 26).concat(hexString.substring(28, 36));

        String uuid = hexString.toUpperCase();

        uuid = uuid.substring(0, 8) + "-"
                + uuid.substring(8, 12) + "-"
                + uuid.substring(12, 16) + "-"
                + uuid.substring(16, 20) + "-"
                + uuid.substring(20, 32);


        Log.i("beaconManager", "UUID: "+uuid);
        researchdata.clear();

        researchdata.add(uuid);
        List<String> data_list = Arrays.asList(uuid,beacondata[3]);

        if(dp.our_Beacon(uuid)){
                data_queue.add(data_list);
                long endT = System.currentTimeMillis();
            Log.i("beacon", "researchData1");
                if ((endT-startT)>500){
                    startT = System.currentTimeMillis();
                    Log.i("beacon", "researchData");
                    researchdata.addAll(as.ana_signal(data_queue,algo_num,weight_type, remind_range,offset));
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
}*/

