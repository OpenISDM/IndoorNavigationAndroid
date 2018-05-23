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

    static int mobility = 1;
    static String fileName = "buildingA.xml";

    public static int getMobilityValue() {

        return mobility;
    }

    public static void setMobilityValue(int m) {

        mobility = m;
    }

    public static String getFileName(){

        return fileName;
    }

    public static void setFileName(String f){

        fileName = f;

    }


}
