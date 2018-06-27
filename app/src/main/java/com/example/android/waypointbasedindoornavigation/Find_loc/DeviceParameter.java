package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.app.Application;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleToIntFunction;

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Integer.parseInt;
public class DeviceParameter {
    private static final String n_value = "n";
    private static final String id = "id";
    private static final String R0 = "R0";
    private static final String parameter = "parameter";

    private static JSONArray jarray = new JSONArray();
    private ReadWrite_File wf= new ReadWrite_File();
    private static Context c;
    public void setupDeviceParameter(Context c) {
        this.c = c;
        jarray = wf.ReadJsonFile();
        if (jarray == null) initdivice();
//        else{
//            try {
//                for (int i = 0; i < jarray.length(); i++) {
//                    JSONObject jsonObject = jarray.getJSONObject(i);
//                    String id = jsonObject.getString("id");
//                    String parameter = jsonObject.getString("parameter");
//                    Log.i("JSONPaser", "id:" + id + ", parameter:" + parameter);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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
        XmlPullParser pullParser = Xml.newPullParser();
        AssetManager assetManager = c.getAssets();
        JSONArray tmp_jarray = new JSONArray();
        try {
            InputStream is = assetManager.open("buildingA.xml");
            pullParser.setInput(is , "utf-8");
            int eventType = pullParser.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String tag = null;
                if (eventType == XmlPullParser.START_TAG) {
                    tag = pullParser.getName();
                    if(tag.equals("region")) {
                        while(true) {
                            if(eventType == XmlPullParser.END_TAG)
                                if(pullParser.getName().equals("region"))
                                    break;
                            if (eventType == XmlPullParser.START_TAG) {
                                if(pullParser.getName().equals("node")){
                                    JSONObject jobject = new JSONObject();
                                    jobject.put(this.id, pullParser.
                                            getAttributeValue(null, this.id));
                                    jobject.put(this.parameter, -65);
                                    jobject.put(this.R0, 0);
                                    jobject.put(this.n_value, 0);
                                    Log.i("JSONDP",jobject.toString());
                                    tmp_jarray.put(jobject);
                                }
                            }
                            eventType = pullParser.next();
                        }
                    }
                }
                eventType = pullParser.next();
            }
            wf.writejson(tmp_jarray.toString());
            jarray = tmp_jarray;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
