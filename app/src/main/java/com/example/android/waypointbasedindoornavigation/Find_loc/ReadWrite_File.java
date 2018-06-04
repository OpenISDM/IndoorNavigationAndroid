package com.example.android.waypointbasedindoornavigation.Find_loc;

import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class ReadWrite_File {
    private File file;
    private static String file_name = "Log";
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
        writefunction(file,sBody);
    }
//    自定名稱寫入
    public void writeFile(String sFileName, String sBody){
        file = new File(path,sFileName+".txt");
        writefunction(file,sBody);
    }
    public void writejson(String j){
        file = new File(path,"DeviceParamation.json");
        writefunction(file,j);
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
    public String  ReadJsonFile(){
        BufferedReader buf;
        try{
            buf = new BufferedReader(new FileReader(file));
            String line;
            while ((line = buf.readLine()) != null)
                buf.readLine();
            buf.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
