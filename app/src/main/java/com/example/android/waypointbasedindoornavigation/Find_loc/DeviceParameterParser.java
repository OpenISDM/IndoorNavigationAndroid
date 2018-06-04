package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeviceParameterParser {
    private static List<siganl_data_type> data_list;
    private ReadWrite_File wf= new ReadWrite_File();
    public void Change_paramation(String id, int  paramation){

    }
    public void initdivice(List<String> s){
        List<String> tmp_list = new ArrayList<>();
        Log.i("JSON00",s.toString());
        wf.writejson(initdivicejson(s));
    }
    private String initdivicejson(List<String> s){
        JSONArray jarray = new JSONArray();
        try {
            for (String getdata  : s){
                JSONObject jobject = new JSONObject();
                jobject.put("id", getdata);
                jobject.put("parameter", -65);
                jarray.put(jobject);
            }
            Log.i("JSON",jarray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jarray.toString();
    }
}
