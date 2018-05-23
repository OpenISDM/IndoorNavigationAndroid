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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CompassActivity extends AppCompatActivity implements SensorEventListener {


    private static final int DURATION = 210;

    // define the display assembly compass picture
    private ImageView image;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    //
    TextView tvHeading, degreeText;
    RelativeLayout layout;

    // Number of degree passed from NavigationActivity
    int passedDegree;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            passedDegree = bundle.getInt("degree");

        layout = (RelativeLayout) findViewById(R.id.relativeLayoutID);
        image = (ImageView) findViewById(R.id.imageViewCompass);


        // TextView that will tell the user what number of degree is the heading
        tvHeading = (TextView) findViewById(R.id.tvHeading);

        // Shows the number of degree
        degreeText = (TextView) findViewById(R.id.degreeText);

        degreeText.setText("Turn to "+passedDegree+" degree");

        // Initialize android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree+passedDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(DURATION);

        // if the turning degree is matched to the designated degree, then the compass is closed
        if((int)degree == passedDegree)
            finish();

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }


}
