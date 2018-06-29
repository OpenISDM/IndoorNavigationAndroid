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

    static int preferenceValue = 3;
    static boolean turnOnOK = false;
    static String fileName = "buildingA.xml";

    public static int getPreferenceValue() {

        return preferenceValue;
    }

    public static void setPreferenceValue(int m) {

        preferenceValue = m;
    }

    public static boolean getTurnOnOK() {

        return turnOnOK;
    }

    public static void setTurnOnOK(boolean m) {

        turnOnOK = m;
    }

    public static String getFileName(){

        return fileName;
    }

    public static void setFileName(String f){

        fileName = f;

    }


}
