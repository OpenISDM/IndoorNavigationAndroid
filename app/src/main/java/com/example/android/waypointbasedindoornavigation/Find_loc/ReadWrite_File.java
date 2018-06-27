package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ReadWrite_File {
    private File file;
    private static String file_name = "Log";
    private static boolean sb = false;
    private final File path  = new File(Environment.getExternalStorageDirectory() +
            File.separator +"WPBIN");
//    設定固定檔案名稱
    public ReadWrite_File(){
        if(!path.exists()) path.mkdir();
    }
    public void setFile_name (String s){
        Log.i("Msg", "set name "+s);
        this.file_name = s;
    }
//    以固定名稱寫入
    public void writeFile(String sBody){
        file = new File(path,file_name+".txt");
        writefunction(file,sBody,1);
    }
//    自定名稱寫入
    public void writeFile(String sFileName, String sBody){
        file = new File(path,sFileName+".txt");
        writefunction(file,sBody,1);
    }
    public void writejson(String j){
        file = new File(path,"DeviceParamation.json");
        writefunction(file,j,0);

    }
//    寫入含式
    private void writefunction(File file, String sBody, int T){
        if(sb) {
            Log.i("Msg0", String.valueOf(file.exists()));
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    file.setExecutable(true, false);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                if (T == 0) {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file, false));
                    buf.write(sBody);
                    buf.close();
                } else if (T == 1) {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
                    buf.append(sBody);
                    buf.newLine();
                    buf.close();
                }
                Log.i("Msg2", "success" + file.getAbsolutePath());
            } catch (Exception e) {
                Log.i("Msg3", "fail" + file.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }
    public JSONArray ReadJsonFile() {
        file = new File(path, "DeviceParamation.json");
        JSONArray jarray = null;
        if(file.exists()) {
            try {
                FileInputStream is = new FileInputStream(file);
                int tmp_size = is.available();
                byte[] buffer = new byte[tmp_size];
                is.read(buffer);
                is.close();
                String jsonText = new String(buffer, "UTF-8");
                jarray = new JSONArray(jsonText);
                Log.i("JSON","load json success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else   Log.i("JSON","don't have file");
        return jarray;
    }
}
