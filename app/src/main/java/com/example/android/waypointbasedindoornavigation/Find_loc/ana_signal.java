package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
//Log.i("Queue2", o_member.toString());
public class ana_signal {
    private Queue<siganl_data_type> weight_queue = new LinkedList<>();
    private  List<siganl_data_type> data_list = new ArrayList<>();
    private int weight_size = 5;
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

        for (int i = 0; i <data_list.size(); i++)
            data_list.get(i).set_sort_way(1);
        Collections.sort(data_list);
        List<String> location_range = new ArrayList<>();
        if (data_list.size()>1) {
            int tmp_dif = Math.abs(Math.round(data_list.get(0).countavg() - data_list.get(1).countavg()));
            if (tmp_dif > 10 && data_list.get(0).countavg()>-65){
                Log.i("def_range", "close " + data_list.get(0).getUuid());
                location_range.add("close");
                location_range.add(data_list.get(0).getUuid());
            }
            else if (tmp_dif < 5) {
                Log.i("def_range", "middle of " + data_list.get(0).getUuid() + " and " + data_list.get(1).getUuid());
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
            if (tmp_dif > -65) {
                Log.i("def_range", "close " + data_list.get(0).getUuid());
                location_range.add("close");
                location_range.add(data_list.get(0).getUuid());
            }
            else {
                Log.i("def_range", "near " + data_list.get(0).getUuid());
                location_range.add("near");
                location_range.add(data_list.get(0).getUuid());
            }
        }
        List<Integer> weight_list = weight_type(weight_type);
        weight_queue.add(new siganl_data_type(
                data_list.get(0).getUuid(), Math.round(data_list.get(0).countavg())));
        if (weight_queue.size() > weight_size) {
            weight_queue.poll();
        }
        List<siganl_data_type> get_weight_data = new ArrayList<>(weight_queue);
        Collections.reverse(get_weight_data);
        for (int i = 0; i < get_weight_data.size(); i++)
            Log.i("SLWQ" + i, get_weight_data.get(i).getUuid() +
                    "\t" + get_weight_data.get(i).getrssilist());
        List<siganl_data_type> count_data_weight =
                Positioning_Algorithm(get_weight_data, weight_list, algo_Type);
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
    private List<Integer> weight_type(int T) {
        List<Integer> weight_list = new ArrayList<>();
        switch (T) {
            case 1:
                for (int i = 0; i < weight_size + 2; i++) {
//                weight_list.add((int) Math.pow(2, i));
                    if (i < 2)
                        weight_list.add(1);
                    else
                        weight_list.add(weight_list.get(i - 1) + weight_list.get(i - 2));
                }
                return weight_list;
            default:
                for (int i = 2; i < weight_size + 2; i++)
                    weight_list.add(i);
                return weight_list;
        }

    }

//    -------------------------------------------------------------------------------------
//    Positioning_Algorithm
    private List<siganl_data_type> Positioning_Algorithm
    (List<siganl_data_type> get_weight_data, List<Integer> weight_list, int T) {
        switch (T) {
            case 1:
                return ana_signal_1(get_weight_data, weight_list);
            case 2:
                return ana_signal_2(get_weight_data, weight_list);
            case 3:
                return ana_signal_3(get_weight_data,data_list ,weight_list);
            default:
                return ana_signal_1(get_weight_data, weight_list);
        }
    }

    private List<siganl_data_type> ana_signal_1
            (List<siganl_data_type> get_weight_data, List<Integer> weight_list) {
        Log.i("def_algo", "algo1");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();
        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , (get_weight_data.get(i).getrssi()) * weight_list.get(i)));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(get_weight_data.get(i).getrssi() * weight_list.get(i));
            }
        }
        tmplistUUID.clear();
        Collections.sort(count_data_weight);
        return count_data_weight;
    }
    private List<siganl_data_type> ana_signal_2
            (List<siganl_data_type> get_weight_data, List<Integer> weight_list) {
        Log.i("def_algo", "algo2");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();
        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , weight_list.get(i)));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(weight_list.get(i));
            }
        }
//        Log.i("SLW",count_data_weight.get(0).getrssilist());
        tmplistUUID.clear();
        Collections.sort(count_data_weight);
        return count_data_weight;
    }
    private List<siganl_data_type> ana_signal_3
            (List<siganl_data_type> get_weight_data,
             List<siganl_data_type> data_list,
             List<Integer> weight_list) {
        Log.i("def_algo", "algo3");
        List<String> tmplistUUID = new ArrayList<>();
        List<siganl_data_type> count_data_weight = new ArrayList<>();

        for (int i = 0; i < get_weight_data.size(); i++) {
            if (tmplistUUID.indexOf(get_weight_data.get(i).getUuid()) == -1) {
                tmplistUUID.add(get_weight_data.get(i).getUuid());
                count_data_weight.add(new siganl_data_type(get_weight_data.get(i).getUuid()
                        , get_weight_data.get(i).getrssi()*weight_list.get(i)));
            } else {
                count_data_weight.get(
                        tmplistUUID.indexOf(get_weight_data.get(i).getUuid())).
                        setvalue(get_weight_data.get(i).getrssi()*weight_list.get(i));
            }
        }
//        Log.i("SLW",count_data_weight.get(0).getrssilist());
        tmplistUUID.clear();

        Collections.sort(count_data_weight);
        return count_data_weight;
    }
}