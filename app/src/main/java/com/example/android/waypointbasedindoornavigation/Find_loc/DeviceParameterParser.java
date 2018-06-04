package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.os.Environment;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class DeviceParameterParser {
    private static List<siganl_data_type> data_list;
//    private final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private final File path = Environment.getExternalStorageDirectory();
    private File file;
    public void Change_paramation(String id, int  paramation){

    }
    public void DeviceParameterParser(List<String> s){
        file = new File(Environment.getExternalStorageDirectory() +
                File.separator +"waypointbasedindoornavigation");
        if(!file.exists()) file.mkdir();
        file = new File(path,"DeviceParamation.json");
        if(!file.exists()){
            try
            {
                file.createNewFile();
                file.setExecutable(true,false);
                initdivicejson(s);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void initdivicejson(List<String> s){
        JSONArray jarray = new JSONArray();
        try {
            for (String getdata : s){
                JSONObject jobject = new JSONObject();
                jobject.put("id", getdata);
                jobject.put("parameter", -65);
                jarray.put(jobject);
            }
            Log.i("JSON",jarray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
