package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.util.Log;
import android.util.LongSparseArray;

import com.example.android.waypointbasedindoornavigation.GeoCalulation;
import com.example.android.waypointbasedindoornavigation.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.transform.dom.DOMLocator;

//Log.i("Queue2", o_member.toString());
public class ana_signal {
    private Queue<siganl_data_type> weight_queue = new LinkedList<>();
    private  List<siganl_data_type> data_list = new ArrayList<>();
    private int weight_size = 5;
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
    public List<String> ana_signal(Queue q, int algo_Type, int weight_type, float remind_range) {
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
        if (data_list.size() >1) {
            for (int i = 0; i < data_list.size(); i++)
                data_list.get(i).set_sort_way(1);
            Collections.sort(data_list);
            List<Float> tmp_count_dif = ana_signal_6(data_list, remind_range);
            if (tmp_count_dif != null)
                if (tmp_count_dif.size() > 2) {
                    float tmp_dif = Math.abs(data_list.get(0).countavg()
                            - data_list.get(Math.round(tmp_count_dif.get(2))).countavg());
                    Log.i("tmp_count_dif1",  data_list.get(0).getUuid()+
                            data_list.get(0).getrssilist().toString() + " " +
                            String.valueOf(data_list.get(0).countavg()) + "\t" +
                            data_list.get(Math.round(tmp_count_dif.get(2))).getUuid() +
                            data_list.get(Math.round(tmp_count_dif.get(2))).getrssilist().toString() + " " +
                            String.valueOf(data_list.get(1).countavg()));
                    if (tmp_dif > tmp_count_dif.get(0) &&
                            data_list.get(0).countavg() > tmp_count_dif.get(1)) {
                        Log.i("def_range", "close " + data_list.get(0).getUuid());
                        Log.i("tmp_count","threshold = " + tmp_count_dif.get(1));
                        location_range.add("close");
                        location_range.add(data_list.get(0).getUuid());
                    }
//                else if (tmp_dif < ana_signal_5(data_list,near_range) &&
//                        data_list.get(1).countavg() > count_Rd(data_list.get(1).getUuid(),distance-near_range)) {
//                    Log.i("def_range", "middle of " + data_list.get(0).getUuid()
//                            + " and " + data_list.get(1).getUuid());
//                    location_range.add(data_list.get(0).getUuid());
//                    location_range.add(data_list.get(1).getUuid());
//                }
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
            if (tmp_dif > dp.get_RSSI_threshold(data_list.get(0).getUuid())) {
                Log.i("tmp_count_RSSI","threshold = " + dp.get_RSSI_threshold(data_list.get(0).getUuid()));
//                Log.i("def_range", "close " + data_list.get(0).getUuid()+ "\t"+
//                        dp.get_Paramater(data_list.get(0).getUuid()));
                location_range.add("close");
                location_range.add(data_list.get(0).getUuid());
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
                    weight_list.add((float) Math.pow(2, i));
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
//        Log.i("SLW",count_data_weight.get(0).getrssilist());
        tmplistUUID.clear();

        Collections.sort(count_data_weight);
        return count_data_weight;
    }
    private List<String> ana_signal_4
            (List<siganl_data_type> data_list) {
        Log.i("def_algo", "algo4");
//       計算距離
        Node[] tmp_dis_Node = new Node[2];
        for (Node tmp_path_P:  tmp_path){
            if (data_list.get(0).getUuid().equals(tmp_path_P.getID())) tmp_dis_Node[0] = tmp_path_P;
            if (data_list.get(1).getUuid().equals(tmp_path_P.getID())) tmp_dis_Node[1] = tmp_path_P;
        }
        if (data_list.size()>2) {
            if (!tmp_dis_Node[1].equals(null) && !tmp_dis_Node[0].equals(null))
                distance = GeoCalulation.getDistance(tmp_dis_Node[0], tmp_dis_Node[1]);
            else distance = 0;
            double[] tmp_disatnce = new double[2];
            tmp_disatnce[0] = count_distance(data_list.get(0).getUuid(), data_list.get(0).countavg());
            tmp_disatnce[1] = count_distance(data_list.get(1).getUuid(), data_list.get(1).countavg());
            double tmp = tmp_disatnce[0] * distance / (tmp_disatnce[0] + tmp_disatnce[1]);
            tmp_disatnce[1] = tmp_disatnce[1] * distance / (tmp_disatnce[0] + tmp_disatnce[1]);
            tmp_disatnce[0] = tmp;
            List<String> tmp_returen = new ArrayList<>();
            for (int i = 0; i < 2; i++){
                tmp_returen.add(data_list.get(i).getUuid());
                tmp_returen.add(String.valueOf(tmp_disatnce[i]));
            }
            return tmp_returen;
        }
        else
            return null;

    }
    private float ana_signal_5
            (List<siganl_data_type> data_list, float range) {
        Log.i("def_algo", "algo5");
        Log.i("algo5", String.valueOf(tmp_path.length));
//       計算距離
        Node[] tmp_dis_Node = new Node[2];
        for (Node tmp_path_P:  tmp_path){
            Log.i("TDN",tmp_path_P.getID()+"\t"+data_list.get(0).getUuid()
                    +"\t"+data_list.get(1).getUuid());
            if (data_list.get(0).getUuid().equals(tmp_path_P.getID()))tmp_dis_Node[0] = tmp_path_P;
            if (data_list.get(1).getUuid().equals(tmp_path_P.getID()))tmp_dis_Node[1] = tmp_path_P;
        }
        try {
            if (data_list.size() > 1) {
                if (tmp_dis_Node[1] != null && tmp_dis_Node[0]!=null)
                    distance = GeoCalulation.getDistance(tmp_dis_Node[0], tmp_dis_Node[1]);
                else {
                    distance = 0;
                    return 9999;
                }
                Log.i("algo5", String.valueOf(distance));
                double[] tmp_difference = new double[2];
                tmp_difference[0] = count_Rd(data_list.get(0).getUuid(), range);
                tmp_difference[1] = count_Rd(data_list.get(1).getUuid(), distance - range);
                Log.i("algo5", String.valueOf(tmp_difference[0]) + "\t" + String.valueOf(tmp_difference[1]));
                float tmp_returen = (float) (tmp_difference[0] - tmp_difference[1]);
                return tmp_returen;
            } else
                return 9999;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TDN2","null error");
            return 0;
        }
    }
    List<Float> tmp_returen = new ArrayList<>();
    private List<Float> ana_signal_6
            (List<siganl_data_type> data_list, float remind_range) {
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
                double t_range = count_real_Rd(data_list.get(0).getUuid(),remind_range);
                tmp_difference[0] = count_Rd(data_list.get(0).getUuid(), t_range);
                t_range = count_real_Rd(data_list.get(1).getUuid(),distance - remind_range);
                tmp_difference[1] = count_Rd(data_list.get(1).getUuid(), t_range);
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
                (Math.pow(dp.get_install_hight(s),2)
                        +Math.pow(range+dp.get_Paramater(s),2)));
        return r_return;
    }
    private double count_distance(String s,double Rd){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return Math.pow(10,((Rd-R0)/(10*n_vlaue)))/dp.get_install_hight(s);
//        return Math.pow(10,((Rd-R0)/(10*n_vlaue))/1.3);
    }
    private double count_Rd(String s,double range){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return R0+(10*n_vlaue*Math.log10(range/1.5));
    }
}