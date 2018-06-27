package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.util.Log;

import com.example.android.waypointbasedindoornavigation.GeoCalulation;
import com.example.android.waypointbasedindoornavigation.Node;

import java.util.ArrayList;
import java.util.Collections;
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
    private static int distance = 0;
    private static Node[] tmp_path;
    public void set_path(Node[] tmp_path){ this.tmp_path = tmp_path; }
    public List<String> ana_signal(Queue q, int algo_Type, int weight_type) {
        List lq = new ArrayList<String>(q);
        List<String> tmp_data_list = new ArrayList<>();
        data_list.clear();
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
        List<String> location_range = new ArrayList<>();
        if (data_list.size() >1) {
            for (int i = 0; i <data_list.size(); i++)
                data_list.get(i).set_sort_way(1);
            Collections.sort(data_list);
            Log.i("tmp_path size2", String.valueOf(tmp_path.length)+"\t"+ String.valueOf(distance) );
            float tmp_dif = Math.abs(data_list.get(0).countavg() - data_list.get(1).countavg());
            float tmp_count_dif = ana_signal_5(data_list);

            if (tmp_dif > tmp_count_dif && data_list.get(0).countavg() >
                    dp.get_RSSI_threshold(data_list.get(0).getUuid())){
//                Log.i("def_range", "close " + data_list.get(0).getUuid());
                Log.i("def_range", "close " + data_list.get(0).getUuid()+ "\t"+
                        dp.get_RSSI_threshold(data_list.get(0).getUuid())+"\t"+String.valueOf(tmp_dif));
                location_range.add("close");
                for (siganl_data_type tmp_sdt : data_list){
                    location_range.add(tmp_sdt.getUuid());
                    location_range.add(String.valueOf(tmp_sdt.countavg()));
                }
            }
            else if (tmp_dif < 5) {
                Log.i("def_range", "middle of " + data_list.get(0).getUuid()
                        + " and " + data_list.get(1).getUuid());
                location_range.add(data_list.get(0).getUuid());
                location_range.add(data_list.get(1).getUuid());
            }
            else {
                Log.i("def_range", "near " + data_list.get(0).getUuid());
                location_range.add("near");
                location_range.add(data_list.get(0).getUuid());
            }
        }
        else {
            int tmp_dif = Math.round(data_list.get(0).countavg());

            if (tmp_dif > dp.get_RSSI_threshold(data_list.get(0).getUuid())) {
//                Log.i("def_range", "close " + data_list.get(0).getUuid()+ "\t"+
//                        dp.get_Paramater(data_list.get(0).getUuid()));
                location_range.add("close");
                location_range.add(data_list.get(0).getUuid());
            }
            else {
                Log.i("def_range", "near " + data_list.get(0).getUuid());
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
//        for (int i = 0; i < get_weight_data.size(); i++)
//            Log.i("SLWQ" + i, get_weight_data.get(i).getUuid() +
//                    "\t" + get_weight_data.get(i).getrssilist());
        List<siganl_data_type> count_data_weight = Positioning_Algorithm(get_weight_data, weight_list, algo_Type);
        List<String> tmp_return = new ArrayList<>();
        tmp_return.add(count_data_weight.get(0).getUuid());
        tmp_return.addAll(location_range);
//        if(data_list.size()>2){
//            Log.i("ASQ",ana_signal_4(data_list).toString());
//        }
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
        Log.i("tmp_path size", String.valueOf(tmp_path.length));
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
            (List<siganl_data_type> data_list) {
        Log.i("def_algo", "algo5");
//       計算距離
        Node[] tmp_dis_Node = new Node[2];
        Log.i("tmp_path size", String.valueOf(tmp_path.length));
        for (Node tmp_path_P:  tmp_path){
            if (data_list.get(0).getUuid().equals(tmp_path_P.getID())) tmp_dis_Node[0] = tmp_path_P;
            if (data_list.get(1).getUuid().equals(tmp_path_P.getID())) tmp_dis_Node[1] = tmp_path_P;
        }
        if (data_list.size()>1) {
            if (!tmp_dis_Node[1].equals(null) && !tmp_dis_Node[0].equals(null))
                distance = GeoCalulation.getDistance(tmp_dis_Node[0], tmp_dis_Node[1]);
            else distance = 0;
            Log.i("algo5", String.valueOf(distance));
            double[] tmp_disatnce = new double[2];
            tmp_disatnce[0] = count_Rd(data_list.get(0).getUuid(), 2);
            tmp_disatnce[1] = count_Rd(data_list.get(1).getUuid(), distance-2);
            Log.i("algo5", String.valueOf(tmp_disatnce[0])+"\t"+String.valueOf(tmp_disatnce[1]));
            float tmp_returen = (float) (tmp_disatnce[0]-tmp_disatnce[1]);
            return tmp_returen;
        }
        else
            return 0;

    }
    private double count_distance(String s,double Rd){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return Math.pow(10,((Rd-R0)/(10*n_vlaue)));
    }
    private double count_Rd(String s,float range){
        double R0 = dp.get_R0(s);
        double n_vlaue = dp.get_n(s);
        return R0+(10*n_vlaue*Math.log10(range/1.5));
    }
}