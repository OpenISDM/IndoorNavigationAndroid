package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.app.Application;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import com.example.android.waypointbasedindoornavigation.DataParser;
import com.example.android.waypointbasedindoornavigation.Node;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.DoubleToIntFunction;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Integer.parseInt;
public class DeviceParameter {
    private static final String n_value = "n";
    private static final String id = "id";
    private static final String R0 = "R0";
    private static final String parameter = "parameter";
    private static final String install_hight = "install_hight";
    private static HashMap<String, Node> allWaypointData = new HashMap<>();
    private static JSONArray jarray = new JSONArray();
    private ReadWrite_File wf= new ReadWrite_File();
    private static Context c;
    public void DeviceParameter(){
        Log.i("init", "DeviceParameter set");
    }
    public void set_allWaypointData(HashMap<String, Node> allWaypointData){
        this.allWaypointData = allWaypointData;
        Log.i("init", "set allWaypointData");
    }
    public void setupDeviceParameter(Context c) {
        Log.i("setupDeviceParameter","setupDeviceParameter");
        this.c = c;
        jarray = wf.ReadJsonFile();
        if (jarray == null) initdivice();
        else {
            try {
                List<String> con_dif0 = new ArrayList<>();
                List<String> con_dif1 = new ArrayList<>();
                for (Node tmp_node : allWaypointData.values()) {
                    con_dif0.add(tmp_node.getID());
                }
                Log.i("JSONtag",String.valueOf(jarray.length()));
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject tmp_jobject = jarray.getJSONObject(i);
                    Log.i("JSONtag",tmp_jobject.getString(this.id));
                    con_dif1.add(tmp_jobject.getString(this.id));
                }
                Log.i("JSONtag0",con_dif0.toString());
                Log.i("JSONtag1",con_dif1.toString());
                con_dif0.removeAll(con_dif1);
                Log.i("JSONtag1",con_dif0.toString());
                if (!con_dif0.isEmpty()){
                    Log.i("JSONtag2",jarray.toString());
                    for (String tmp_node: con_dif0){
                        JSONObject tmp_add_jobject = new JSONObject();
                        tmp_add_jobject.put(this.id, tmp_node);
                        tmp_add_jobject.put(this.parameter, -65);
                        tmp_add_jobject.put(this.R0, -45);
                        tmp_add_jobject.put(this.n_value, -2.14);
                        tmp_add_jobject.put(this.install_hight, 1.5);
                        jarray.put(tmp_add_jobject);
                    }
                    Log.i("JSONtag3",jarray.toString());
                    wf.writejson(jarray.toString());
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public int get_RSSI_threshold(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(s)){
                    return Integer.parseInt(tmp_jobject.getString(this.parameter));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public double get_install_hight(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.install_hight).equals(s)){
                    return tmp_jobject.getDouble(this.install_hight);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public double get_R0(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(s)){
                    return tmp_jobject.getDouble(this.R0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public double get_n(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(s)){
                    return tmp_jobject.getDouble(this.n_value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public double get_Paramater(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(s)){
                    return tmp_jobject.getDouble(this.parameter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public void Change_paramation(String id, int parameter){
        JSONArray tmp_jarray = new JSONArray();
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(id)){
                    JSONObject tmp_jobject2 = new JSONObject();
                    tmp_jobject2.put(this.id,id);
                    tmp_jobject2.put(this.parameter,
                             tmp_jobject.getInt(this.parameter)+parameter);
                    tmp_jobject2.put(this.R0,tmp_jobject.getInt(this.R0));
                    tmp_jobject2.put(this.n_value,tmp_jobject.getInt(this.n_value));
                    tmp_jobject2.put(this.install_hight,tmp_jobject.getInt(this.install_hight));
                    tmp_jarray.put(tmp_jobject2);
                }
                else tmp_jarray.put(tmp_jobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jarray = tmp_jarray;
        wf.writejson(jarray.toString());
    }
    public Boolean our_Beacon(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(s)){
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public void count_dis(String id, int R0, int n){
        JSONArray tmp_jarray = new JSONArray();
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString(this.id).equals(id)){
                    JSONObject tmp_jobject2 = new JSONObject();
                    tmp_jobject2.put(this.id,id);
                    tmp_jobject2.put(this.parameter, tmp_jobject.getInt(this.parameter));
                    if (R0 != 0)tmp_jobject2.put(this.R0, R0);
                    if (n != 0)tmp_jobject2.put(this.n_value, n);
                    tmp_jobject2.put(this.install_hight,tmp_jobject.getInt(this.install_hight));
                    tmp_jarray.put(tmp_jobject2);
                }
                else tmp_jarray.put(tmp_jobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jarray = tmp_jarray;
        Log.i("JSONCP", jarray.toString());
        wf.writejson(jarray.toString());
    }

    public void Direct_change_paramation(String id, int  parameter){
        JSONArray tmp_jarray = new JSONArray();
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString("id").equals(id)){
                    JSONObject tmp_jobject2 = new JSONObject();
                    tmp_jobject2.put("id",id);
                    tmp_jobject2.put("parameter", parameter);
                    tmp_jobject2.put(this.R0,tmp_jobject.getInt(this.R0));
                    tmp_jobject2.put(this.n_value,tmp_jobject.getInt(this.n_value));
                    tmp_jobject2.put(this.install_hight,tmp_jobject.getInt(this.install_hight));
                    tmp_jarray.put(tmp_jobject2);
                }
                else tmp_jarray.put(tmp_jobject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        jarray = tmp_jarray;
        Log.i("JSONCP", jarray.toString());
        wf.writejson(jarray.toString());
    }

    private void initdivice(){
        try {
            JSONArray tmp_jarray = new JSONArray();
            for (Node tmp_node : allWaypointData.values()) {
                JSONObject tmp_add_jobject = new JSONObject();
                tmp_add_jobject.put(this.id, tmp_node.getID());
                tmp_add_jobject.put(this.parameter, 0);
                tmp_add_jobject.put(this.R0, -45);
                tmp_add_jobject.put(this.n_value, -2.14);
                tmp_add_jobject.put(this.install_hight, 1.5);
                tmp_jarray.put(tmp_add_jobject);
            }
            Log.i("inijson", tmp_jarray.toString());
            jarray = tmp_jarray;
            wf.writejson(tmp_jarray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
