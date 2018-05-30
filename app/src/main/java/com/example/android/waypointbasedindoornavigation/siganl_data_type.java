package com.example.android.waypointbasedindoornavigation;

import java.util.ArrayList;
import java.util.List;

public class siganl_data_type {
    private List<Integer> rssi = new ArrayList<>();
    private String uuid = null;
    public siganl_data_type(String s, int i){
        rssi.add(i);
        uuid = s;
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
}
