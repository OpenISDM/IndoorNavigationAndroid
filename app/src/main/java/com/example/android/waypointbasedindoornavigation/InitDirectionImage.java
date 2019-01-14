package com.example.android.waypointbasedindoornavigation;

/*--

Module Name:

    CompassActivity.java

Abstract:

    This module emulates the embedded compass of the device
    which is used for heading correction

Author:

    Phil Wu 01-Feb-2018

--*/


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class InitDirectionImage extends AppCompatActivity{
    private ImageView image;
    int passedDegree;
    String nowWaypointID;
    String nextWaypointID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.init_direction_image);
        image = (ImageView) findViewById(R.id.init_image);
        //getpassDegree
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            passedDegree = bundle.getInt("degree");
            nowWaypointID = bundle.getString("nowID");
            nextWaypointID = bundle.getString("nextID");
        }
        Log.i("degree","degree = " + passedDegree);
        //find ID


    switch (nowWaypointID) {
        //C23
        case "0x0454bd410x0155f142":
            if ((passedDegree >= 0 && passedDegree <= 45) || (passedDegree <= 360 && passedDegree > 315))
                image.setImageResource(R.drawable.c23_01);
            else if (passedDegree > 45 && passedDegree <= 135)
                image.setImageResource(R.drawable.c23_02);
            else if (passedDegree > 135 && passedDegree <= 225)
                image.setImageResource(R.drawable.c23_03);
            else if (passedDegree > 225 && passedDegree <= 315)
                image.setImageResource(R.drawable.c23_04);
        break;

}



    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void goNavigation(View view) {
       image.setImageDrawable(null);
       finish();
    }



}
