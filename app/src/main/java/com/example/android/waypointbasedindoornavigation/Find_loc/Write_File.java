package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Write_File {
    private File file;
    private static String file_name = "Log";
    private final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//    設定固定檔案名稱
    public void setFile_name (String s){
        Log.i("Msg", "set name "+s);
        this.file_name = s;
    }
//    以固定名稱寫入
    public void writeFile(String sBody){
        file = new File(path,file_name+".txt");
        writefunction(file,sBody);
    }
//    自定名稱寫入
    public void writeFile(String sFileName, String sBody){
        file = new File(path,sFileName+".txt");
        writefunction(file,sBody);
    }
    public void writejson(JSONObject j){
        file = new File(path,"DeviceParamation.json");
        writejsonfunction(file,j);
    }
//    寫入含式
    private void writefunction(File file, String sBody){
        BufferedWriter buf;
        Log.i("Msg0", String.valueOf(file.exists()));
        if(!file.exists()){
            try
            {
                file.createNewFile();
                file.setExecutable(true,false);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try{
            buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(sBody);
            buf.newLine();
            buf.close();
            Log.i("Msg2", "success"+file.getAbsolutePath());
        }catch (Exception e){
            Log.i("Msg3", "fail"+file.getAbsolutePath());
            e.printStackTrace();
        }
    }
    private void writejsonfunction(File file, JSONObject j){
        BufferedWriter buf;
        Log.i("Msg0", String.valueOf(this.file.exists()));
        if(!this.file.exists()){
            try
            {
                this.file.createNewFile();
                this.file.setExecutable(true,false);
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try{
            buf = new BufferedWriter(new FileWriter(this.file, true));
            buf.write(j.toString());
            buf.close();
            Log.i("Msg2", "success"+ this.file.getAbsolutePath());
        }catch (Exception e){
            Log.i("Msg3", "fail"+ this.file.getAbsolutePath());
            e.printStackTrace();
        }
    }
}
