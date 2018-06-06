package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.app.Application;
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

import static java.lang.Integer.numberOfLeadingZeros;
import static java.lang.Integer.parseInt;

public class DeviceParameter {
    private static JSONArray jarray = null;
    private ReadWrite_File wf= new ReadWrite_File();
    private static Context c;
    public void setupDeviceParameter(Context c) {
        this.c = c;
        jarray = wf.ReadJsonFile();
        if (jarray == null) initdivice();
        else{
            try {
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jsonObject = jarray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String parameter = jsonObject.getString("parameter");
                    Log.i("JSONPaser", "id:" + id + ", parameter:" + parameter);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public int get_Paramater(String s){
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString("id").equals(s)){
                    return Integer.parseInt(tmp_jobject.getString("parameter"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    public void Change_paramation(String id, int  parameter){
        JSONArray tmp_jarray = new JSONArray();
        for (int i=0; i < jarray.length(); i ++){
            try {
                JSONObject tmp_jobject = jarray.getJSONObject(i);
                if(tmp_jobject.getString("id").equals(id)){
                    JSONObject tmp_jobject2 = new JSONObject();
                    tmp_jobject2.put("id",id);
                    tmp_jobject2.put("parameter",parameter);
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
    public void initdivice(){
        XmlPullParser pullParser = Xml.newPullParser();
        AssetManager assetManager = c.getAssets();
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
                                    jobject.put("id", pullParser.
                                            getAttributeValue(null, "id"));
                                    jobject.put("parameter", -65);
                                    jarray.put(jobject);
                                }
                            }
                            eventType = pullParser.next();
                        }
                    }
                }
                eventType = pullParser.next();
            }
            wf.writejson(jarray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
