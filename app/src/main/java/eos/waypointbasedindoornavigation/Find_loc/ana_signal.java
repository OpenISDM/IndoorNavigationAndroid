package eos.waypointbasedindoornavigation.Find_loc;

import android.util.Log;
import android.util.LongSparseArray;

import eos.waypointbasedindoornavigation.GeoCalulation;
import eos.waypointbasedindoornavigation.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.transform.dom.DOMLocator;

import static java.lang.Math.pow;

//Log.i("Queue2", o_member.toString());
public class ana_signal {
    private Queue<siganl_data_type> weight_queue = new LinkedList<>();
    private  List<siganl_data_type> data_list = new ArrayList<>();
    private int weight_size = 5;
    private boolean FirstTime = true;
    private static DeviceParameter dp = new DeviceParameter();
    private static ReadWrite_File wf = new ReadWrite_File();
    private static float distance = 0;
    private static Node[] tmp_path;
    private static HashMap<String, Node> allWaypointData = new HashMap<>();
    public void set_path(List<Node> tmp_path){
        this.tmp_path = new Node[tmp_path.size()];
        for (int i = 0; i < tmp_path.size(); i++) {
            this.tmp_path[i] = tmp_path.get(i);
            Log.i("path", tmp_path.get(i).getID());
        }
    }
    public void set_allWaypointData(HashMap<String, Node> allWaypointData){
        this.allWaypointData = allWaypointData;
    }
    public List<String> ana_signal(Queue q, int algo_Type, int weight_type, float remind_range, double offset) {
        List lq = new ArrayList<String>(q);
        List<String> tmp_data_list = new ArrayList<>();
        data_list.clear();
//        class the datalist to UUID,RSSIList
//        List<siganl_data_type> data_list = new ArrayList<>();
        for (int i = 0; i < q.size(); i++){
            if (tmp_data_list.indexOf(((List<String>) lq.get(i)).get(0)) == -1) {
                tmp_data_list.add(((List<String>) lq.get(i)).get(0));
                data_list.add(new siganl_data_type(((List<String>) lq.get(i)).get(0),
                        Integer.parseInt(((List<String>) lq.get(i)).get(1))));
            }
            else{
                data_list.get(tmp_data_list.indexOf(((List<String>) lq.get(i)).get(0))).
                        setvalue(Integer.parseInt(((List<String>) lq.get(i)).get(1)));
            }
        }
//        find difference between first and second higher RSSI of UUID
        List<String> location_range = new ArrayList<>();
        if (data_list.size() > 0) {
            for (int i = 0; i < data_list.size(); i++)
                data_list.get(i).set_sort_way(1);
            Collections.sort(data_list);

            //data_list.get(0).setvalue_extreme_value();
            //data_list.get(1).setvalue_extreme_value();
            for(int i = 0; i< data_list.size(); i++)
                Log.i("xxx_datalist","Value(" + i +") = " + data_list.get(i).getUuid() + " Rssilist = " + data_list.get(i).getrssilist() + " Rssi = " + data_list.get(i).countavg());
            List<Float> tmp_count_dif = ana_signal_6(data_list, remind_range, offset);
            if (tmp_count_dif != null)
                if (tmp_count_dif.size() > 2) {
                    float tmp_dif = Math.abs(data_list.get(0).countavg()
                            - data_list.get(Math.round(tmp_count_dif.get(2))).countavg());
                    Log.i("tmp_count_dif1",  data_list.get(0).getUuid_Name()+
                            data_list.get(0).getrssilist().toString() + " " +
                            String.valueOf(data_list.get(0).countavg()) + "\t" +
                            data_list.get(Math.round(tmp_count_dif.get(2))).getUuid_Name() +
                            data_list.get(Math.round(tmp_count_dif.get(2))).getrssilist().toString() + " " +
                            String.valueOf(data_list.get(1).countavg()));
                   if(data_list.get(0).countavg() > tmp_count_dif.get(1)) {
                       if(FirstTime == true &&  data_list.get(0).countavg() > tmp_count_dif.get(1)){
                           Log.i("def_range", "close " + data_list.get(0).getUuid());
                           Log.i("tmp_count", "threshold = " + tmp_count_dif.get(1));
                           location_range.add("close");
                           location_range.add(data_list.get(0).getUuid());
                           //SignalLog("Close Beacon");
                           FirstTime = false;
                       }else if (FirstTime == false && tmp_dif > tmp_count_dif.get(0) &&
                              data_list.get(0).countavg() > tmp_count_dif.get(1)) {
                           /*SignalLog("-------定位模組開始---------");
                           SignalLog("----------------二次曲線預估-------------");
                           SignalLog("Remind Range : " + (remind_range+dp.get_Paramater(data_list.get(0).getUuid())));
                           SignalLog("預估最強UUID : " + data_list.get(0).getUuid_Name() + " 預估RSSI：" + String.valueOf(tmp_count_dif.get(1)));
                           SignalLog("預估次強值UUID : " + data_list.get(1).getUuid_Name() + " 預估RSSI" + String.valueOf(tmp_count_dif.get(1) - tmp_count_dif.get(0)));
                           SignalLog("預估差值：" + String.valueOf(tmp_count_dif.get(0)));
                           SignalLog("經緯度計算兩顆Beacon距離：" + distance);
                           SignalLog("Threshold : " + String.valueOf(tmp_count_dif.get(1)));
                           SignalLog("訊號調整值  = " + offset);
                           SignalLog("----------------二次曲線結束-------------");
                           SignalLog("實地量測資料 ：" + data_list.get(0).getUuid_Name() +
                                   data_list.get(0).getrssilist().toString() + " 平均：" +
                                   String.valueOf(data_list.get(0).countavg()) + " 中位數：" + String.valueOf(data_list.get(0).getmiddlenum()) + " 標準差：" + String.valueOf(data_list.get(0).countStandard_Deviation()) + " \t " +
                                   data_list.get(Math.round(tmp_count_dif.get(2))).getUuid_Name() +
                                   data_list.get(Math.round(tmp_count_dif.get(2))).getrssilist().toString() + " 平均：" +
                                   String.valueOf(data_list.get(1).countavg()) + " 中位數：" + String.valueOf(data_list.get(1).getmiddlenum()) + " 標準差：" + String.valueOf(data_list.get(1).countStandard_Deviation()));
                           SignalLog("實際訊號差：" +  String.valueOf(data_list.get(0).countavg() - data_list.get(1).countavg()));
                           SignalLog("Close Beacon");
                           SignalLog("-------定位模組結束---------");*/
                           Log.i("def_range", "close " + data_list.get(0).getUuid());
                           Log.i("tmp_count", "threshold = " + tmp_count_dif.get(1));
                           location_range.add("close");
                           location_range.add(data_list.get(0).getUuid());

                       }
                   }
                    else {
                        Log.i("def_range", "near " + data_list.get(0).getUuid());
                        Log.i("tmp_count","threshold = " + tmp_count_dif.get(1));
                        location_range.add("near");
                        location_range.add(data_list.get(0).getUuid());
                    }
                }else {
                    int tmp_dif2 = Math.round(data_list.get(0).countavg());
                    if (tmp_dif2 > dp.get_RSSI_threshold(data_list.get(0).getUuid())) {
                        Log.i("tmp_count_RSSI","threshold = " + dp.get_RSSI_threshold(data_list.get(0).getUuid()));
//                Log.i("def_range", "close " + data_list.get(0).getUuid()+ "\t"+
//                        dp.get_Paramater(data_list.get(0).getUuid()));
                        location_range.add("close");
                        location_range.add(data_list.get(0).getUuid());
                        //SignalLog("Close Beacon");
                    }
                    else {
                        Log.i("def_range", "near " + data_list.get(0).getUuid());
                        Log.i("tmp_count_RSSI","threshold = " + dp.get_RSSI_threshold(data_list.get(0).getUuid()));
                        location_range.add("near");
                        location_range.add(data_list.get(0).getUuid());
                    }
                }
        }
        else {
            int tmp_dif = Math.round(data_list.get(0).countavg());
            if (tmp_dif > -60) {
                Log.i("tmp_count_RSSI","threshold = " + dp.get_RSSI_threshold(data_list.get(0).getUuid()));
//                Log.i("def_range", "close " + data_list.get(0).getUuid()+ "\t"+
//                        dp.get_Paramater(data_list.get(0).getUuid()));
                location_range.add("close");
                location_range.add(data_list.get(0).getUuid());
                //SignalLog("Close Beacon");
            }
            else {
                Log.i("def_range", "near " + data_list.get(0).getUuid());
                Log.i("tmp_count_RSSI","threshold = " + dp.get_RSSI_threshold(data_list.get(0).getUuid()));
                location_range.add("near");
                location_range.add(data_list.get(0).getUuid());
            }
        }
        List<Float> weight_list = weight_type(weight_type);
        weight_queue.add(new siganl_data_type(
                data_list.get(0).getUuid(), Math.round(data_list.get(0).countavg())));
        if (weight_queue.size() > weight_size) {
            weight_queue.poll();
        }
        List<siganl_data_type> get_weight_data = new ArrayList<>(weight_queue);
        Collections.reverse(get_weight_data);
        List<siganl_data_type> count_data_weight = Positioning_Algorithm(get_weight_data, weight_list, algo_Type);
        List<String> tmp_return = new ArrayList<>();
        tmp_return.add(count_data_weight.get(0).getUuid());
        tmp_return.addAll(location_range);
        return tmp_return;
    }
    //    -------------------------------------------------------------------------------------
//    set part
    private void set_weight_size(int weight_size) {
        this.weight_size = weight_size;
    }

    //    -------------------------------------------------------------------------------------
//    weight type list
    private List<Float> weight_type(int T) {
        List<Float> weight_list = new ArrayList<>();
        switch (T) {
            case 1:
                for (int i = 0; i < weight_size + 2; i++) {
//                weight_list.add((int) Math.pow(2, i));
                    if (i < 2)
                        weight_list.add((float) 1);
                    else
                        weight_list.add(weight_list.get(i - 1) + weight_list.get(i - 2));
                }
                Log.i("weight1", weight_list.toString());
                return weight_list;
            case 2:
                for (int i = 0; i < weight_size + 2; i++)
                    weight_list.add((float) pow(2, i));
                return weight_list;
            case 3:
                for (int i = 0; i < weight_size + 2; i++)
                    weight_list.add((float)(Math.log10(i)*10));
                Log.i("weight3", weight_list.toString());
                return weight_list;
            default:
                for (int i = 2; i < weight_size + 2; i++)
                    weight_list.add((float)i);
                return weight_list;
        }
    }
    //    -------------------------------------------------------------------------------------
//    Positioning_Algorithm
    private List<siganl_data_type> Positioning_Algorithm
    (List<siganl_data_type> get_weight_data, List<Float> weight_list, int T) {
        switch (T) {
            case 1:
                return ana_signal_1(get_weight_data, weight_list);
            case 2:
                return ana_signal_2(get_weight_data, weight_list);
            case 3:
                return ana_signal_3(get_weight_data, weight_list);
            default:
                return ana_signal_1(get_weight_data, weight_list);
        }
    }

    private List<siganl_data_type> ana_signal_1
            (List<siganl_data_type> get_weight_data, List<Float> weight_list) {
        Log.i("def_algo", "algo1");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();
        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , Math.round((get_weight_data.get(i).getrssi()) * weight_list.get(i))));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(Math.round(get_weight_data.get(i).getrssi() * weight_list.get(i)));
            }
        }
        tmplistUUID.clear();
        Collections.sort(count_data_weight);
        return count_data_weight;
    }
    private List<siganl_data_type> ana_signal_2
            (List<siganl_data_type> get_weight_data, List<Float> weight_list) {
        Log.i("def_algo", "algo2");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();
        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , Math.round(weight_list.get(i))));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(Math.round(weight_list.get(i)));
            }
        }
//        Log.i("SLW",count_data_weight.get(0).getrssilist());
        tmplistUUID.clear();
        Collections.sort(count_data_weight);
        return count_data_weight;
    }
    private List<siganl_data_type> ana_signal_3
            (List<siganl_data_type> get_weight_data,
             List<Float> weight_list) {
        Log.i("def_algo", "algo3");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();

        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , Math.round(get_weight_data.get(i).getrssi()*weight_list.get(i))));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(Math.round(get_weight_data.get(i).getrssi()*weight_list.get(i)));
            }
        }
        tmplistUUID.clear();

        Collections.sort(count_data_weight);
        return count_data_weight;
    }

    List<Float> tmp_returen = new ArrayList<>();
    private List<Float> ana_signal_6
            (List<siganl_data_type> data_list, float remind_range,double offset) {
//       計算距離
        Log.i("algo6", "in algo6" + String.valueOf(data_list.size()));
        Node[] tmp_dis_Node = new Node[2];
        tmp_dis_Node[0] = allWaypointData.get(data_list.get(0).getUuid());
        Log.i("algo6 tmp_dis_Node[0]", tmp_dis_Node[0].getID());
        List<String> Neighbornodes = tmp_dis_Node[0].getNeighborIDs();
        Log.i("algo6 neig", Neighbornodes.toString());
        boolean tmp_br = false;
        int tmp_compare = 99999;
        for (int i= 1; i< data_list.size(); i++) {
            for (String tmp_Neighbornodes : Neighbornodes) {
                Log.i("algo6 neigpa", tmp_Neighbornodes + "***\t" + data_list.get(i).getUuid());
                if (tmp_Neighbornodes.equals(data_list.get(i).getUuid())) {
                    tmp_dis_Node[1] = allWaypointData.get(tmp_Neighbornodes);
                    tmp_compare = i;
                    tmp_br = true;
                    break;
                }else
                    tmp_dis_Node[1] = null;
            }
            if (tmp_br) break;
        }
        try {
            if (data_list.size() > 1) {
                Log.i("algo6","in try");
                if (tmp_dis_Node[1] != null && tmp_dis_Node[0]!=null)
                    distance = GeoCalulation.getDistance(tmp_dis_Node[0], tmp_dis_Node[1]);
                else {
//                    distance = 0;
                    Log.i("algo6TDN0","null error");
                    return null;
                }
                tmp_returen.clear();
                Log.i("algo6", String.valueOf(distance));
                double[] tmp_difference = new double[2];
                //-------------------------log 函數------------------------
              /*  double t_range = count_real_Rd(data_list.get(0).getUuid(),remind_range);
                double test_log0,testlog1;
                test_log0 = count_Rd(data_list.get(0).getUuid(), t_range, offset);
                t_range = count_real_Rd(data_list.get(1).getUuid(),distance - remind_range);
                testlog1 = count_Rd(data_list.get(1).getUuid(), t_range, offset);
                SignalLog("----------------log預估-------------");
                SignalLog("預估最強UUID : " + data_list.get(0).getUuid_Name() + " 預估RSSI：" + String.valueOf(test_log0));
                SignalLog("預估次強值UUID : " + data_list.get(1).getUuid_Name() + " 預估RSSI" + String.valueOf(testlog1));
                SignalLog("預估差值：" + String.valueOf((test_log0 - testlog1)));
                SignalLog("經緯度計算兩顆Beacon距離：" + distance);
                SignalLog("Threshold : " + String.valueOf(test_log0));
                SignalLog("----------------log預估結束-------------");*/
                //----------------------二次曲線--------------------------------------
                tmp_difference[0] = count_Quadratic(data_list.get(0).getUuid(), remind_range, offset);
                tmp_difference[1] = count_Quadratic(data_list.get(1).getUuid(), distance - remind_range, offset);
                Log.i("algo6", String.valueOf(tmp_difference[0]) + "\t" + String.valueOf(tmp_difference[1]));
                tmp_returen.add((float) Math.abs(tmp_difference[0] - tmp_difference[1]));
                tmp_returen.add((float) (tmp_difference[0]));
                tmp_returen.add((float) tmp_compare);
//                0: dif, 1: Threshold
                Log.i("algo6 return", tmp_returen.toString());
                return tmp_returen;
            } else {
                Log.i("algo6TDN1","null error1");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("algo6TDN2","null error2");
            return null;
        }
    }
    private double count_real_Rd(String s,double range){
        double r_return = 0;
//        r_return = Math.sqrt(
//                (Math.pow(dp.get_install_hight(s),2)
//                        +Math.pow(range,2)));
        r_return = Math.sqrt(
                (pow(dp.get_install_hight(s),2)
                        + pow(range+dp.get_Paramater(s),2)));
        return r_return;
    }
    private double count_distance(String s,double Rd){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return pow(10,((Rd-R0)/(10*n_vlaue)))/dp.get_install_hight(s);
//        return Math.pow(10,((Rd-R0)/(10*n_vlaue))/1.3);
    }
    private double count_Rd(String s,double t_range,double offset){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return (R0+(10*n_vlaue*Math.log10(t_range/1.5))) + offset;
    }

    private double count_Quadratic(String s,double range, double offset){
        double a_value = dp.get_a(s);
        double b_value = dp.get_b(s);
        double c_vaule = dp.get_c(s);

        return (a_value*pow(range + dp.get_Paramater(s),2) + b_value* (range + dp.get_Paramater(s)) + c_vaule) + offset;
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