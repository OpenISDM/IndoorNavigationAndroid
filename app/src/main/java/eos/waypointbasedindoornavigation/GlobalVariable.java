package eos.waypointbasedindoornavigation;

import android.app.Application;

public class GlobalVariable extends Application {
    private double offset = 1;

    public  void setOffset(double n)
    {
        this.offset = n;
    }

    public void initOffset()
    {
        this.offset = 1;
    }
    public double getOffset()
    {
        return offset;
    }
}
