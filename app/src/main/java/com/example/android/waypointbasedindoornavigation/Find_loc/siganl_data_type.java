package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class siganl_data_type implements Comparable<siganl_data_type>{
    private int sort_ways = 0;
    private List<Integer> rssi = new ArrayList<>();
    private String uuid = null;
    private int parameter;
    public siganl_data_type(String s, int i){
        uuid = s;
        rssi.add(i);
    }
    public siganl_data_type(String s, int i, int j){
        uuid = s;
        rssi.add(i);
        parameter = j;
    }
    public void setvalue(String s){
        uuid = s;
    }
    public void setvalue(int i){
        List<Integer> tmp_list = new ArrayList<>();
        tmp_list.add(i);
        tmp_list.addAll(rssi);
        rssi.clear();
        rssi.addAll(tmp_list);
        tmp_list.clear();
    }
    public String getUuid(){
        return uuid;
    }
    public int getrssi(int i){
        return rssi.get(i);
    }
    public String getrssilist(){
        return rssi.toString();
    }
    public String getrssilistsize(){
        return String.valueOf(rssi.size());
    }
    public int getrssi(){
        return rssi.get(rssi.size()-1);
    }
    public int countsum(){
        int count=0;
        for (int i:rssi){
            count += i;
        }
        return count;
    }
    public float countavg(){
        int count=0,num=0;
        for (int i:rssi){
           count += i;
           num ++;
        }
        return (float)(count/num);
    }
    public void set_sort_way(int sort_way){
        this.sort_ways = sort_way;
    }

    @Override
    public int compareTo(@NonNull siganl_data_type f) {
        switch (sort_ways) {
            case 1:
                if (this.countavg() < f.countavg()) {
                    return 1;
                } else if (this.countavg() > f.countavg()) {
                    return -1;
                } else {
                    return 0;
                }
            default:
                if (this.countsum() < f.countsum()) {
                    return 1;
                } else if (this.countsum() > f.countsum()) {
                    return -1;
                } else {
                    return 0;
                }
        }
    }
}
