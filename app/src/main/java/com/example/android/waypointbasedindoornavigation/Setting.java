package com.example.android.waypointbasedindoornavigation;


/*--

Module Name:

    Setting.java

Abstract:

    This module work as preference setting function

Author:

    Phil Wu 01-Feb-2018

--*/

import android.app.Application;


public class Setting extends Application{

    static int preferenceValue = 2;
    static int modeValue = 3;
    static boolean turnOnOK = false;
    static String fileName = "buildingA.xml";

    public static int getPreferenceValue() {

        return preferenceValue;
    }

    public static int getModeValue() {

        return modeValue;
    }

    public static void setPreferenceValue(int p) {

        preferenceValue = p;
    }

    public static void setModeValue(int m) {

        modeValue = m;
    }
    
    public static String getFileName(){

        return fileName;
    }

    public static void setFileName(String f){

        fileName = f;

    }


}
