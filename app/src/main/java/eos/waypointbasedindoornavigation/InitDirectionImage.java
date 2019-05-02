package eos.waypointbasedindoornavigation;

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
            nowWaypointID = bundle.getString("nowID");
            nextWaypointID = bundle.getString("nextID");
        }
        Log.i("degree","degree = " + passedDegree);
        //find ID


    switch (nowWaypointID) {
        //C23
        case "0x6029b8410x580af042": //A2(心臟內科/內科/體檢區)
            if(nextWaypointID .equals("0xbf52c8410x3323f542") || nextWaypointID .equals("0xeb57ca410x0e21f342")) //服務台(A4 & A5)
                image.setImageResource(R.drawable.a2);
            break;
        case "0x0f3eb8410x3921f342": //A3(心臟內科/內科/體檢區)
            if(nextWaypointID .equals("0xbf52c8410x3323f542") || nextWaypointID .equals("0xeb57ca410x0e21f342")) //服務台(A4 & A5)
                image.setImageResource(R.drawable.a3);
            break;
        case "0xcf90b8410x3424f042": //A11批價
            if(nextWaypointID .equals("0x0154bd410x0055f142")) //30~41診走廊交叉口
                image.setImageResource(R.drawable.a11);
            break;
        case "0xdeceb8410xb833f042": //A13 26~29診走廊出口
            if(nextWaypointID .equals("0x2ebab8410x8c2ef042")) //精神科
                image.setImageResource(R.drawable.a13);
            break;
        case "0x3ef8b8410x0f3ef042": //A14耳鼻喉科
            if(nextWaypointID .equals("0xff53bd410x0055f142") || nextWaypointID .equals("0xbb3fc8410x0721f342")) //健康教育中心(A14 & A15)
                image.setImageResource(R.drawable.a14);
            break;
        case "0xee0cb9410x3b43f042": //A15耳鼻喉科
            if(nextWaypointID .equals("0xff53bd410x0055f142") || nextWaypointID .equals("0xbb3fc8410x0721f342")) //健康教育中心(A14 & A15)
                image.setImageResource(R.drawable.a15);
            break;
        case "0xff53bd410x0055f142": //A16健康教育中心
            if(nextWaypointID .equals("0xbf52c8410x3323f542") || nextWaypointID .equals("0xeb57ca410x0e21f342")) //服務台(A4 & A5)
                image.setImageResource(R.drawable.a16);
            break;
        case "0xbb3fc8410x0721f342": //A17健康教育中心
            if(nextWaypointID .equals("0xbf52c8410x3323f542") || nextWaypointID .equals("0xeb57ca410x0e21f342")) //服務台(A4 & A5)
                image.setImageResource(R.drawable.a16);
            break;
        case "0x0154bd410x0055f142": //A19 30~41診走廊交叉口
            if(nextWaypointID .equals("0x4d36b9410x934df042")) //樓梯
                image.setImageResource(R.drawable.a19_1);
            if(nextWaypointID .equals(" 0x7cf0b9410x1f7cf042")) //42~49診走廊交叉口
                image.setImageResource(R.drawable.a19_2);
            break;
        case "0x0254bd410x0055f142": //A20 外科/骨科/牙科
            if(nextWaypointID .equals("0x0154bd410x0055f142")) //A19 30~41診走廊交叉口
                image.setImageResource(R.drawable.a20);
            break;
        case "0x0254bd410x0155f142": //A21 外科/骨科/牙科
            if(nextWaypointID .equals("0x0154bd410x0055f142")) //A19 30~41診走廊交叉口
                image.setImageResource(R.drawable.a21);
            break;
        case "0xed4cc8410x0e21f342": //A22 腎臟科/腎膽腸內科/新陳代謝分泌科
            if(nextWaypointID .equals("0x1cc7b9410xc771f042")) //A23 無障礙領藥窗口
                image.setImageResource(R.drawable.a22);
            break;
        case "0x1cc7b9410xc771f042": //A23 無障礙領藥窗口
            if(nextWaypointID .equals("0xff53bd410x0055f142") || nextWaypointID .equals("0xbb3fc8410x0721f342")) //A16 & A17 健康教育中心
                image.setImageResource(R.drawable.a23);
            break;
        case "0x2c05ba410x4b81f042": //A27 眼科/皮膚科
            if(nextWaypointID .equals("0x7cf0b9410x1f7cf042")) //A19 30~41診走廊交叉口
                image.setImageResource(R.drawable.a27);
            break;
        case "0xdc19ba410x7786f042": //A28 眼科/皮膚科
            if(nextWaypointID .equals("0x7cf0b9410x1f7cf042")) //A19 30~41診走廊交叉口
                image.setImageResource(R.drawable.a28);
            break;
        case "0x8b2eba410xa38bf042": //A29 42~49診走廊出口
            if(nextWaypointID .equals("0x2c05ba410x4b81f042") || nextWaypointID .equals("0xdc19ba410x7786f042")) //A27 & A28眼科/皮膚科
                image.setImageResource(R.drawable.a29);
            break;
        case "0x0193bd410x780df142": //B1 樓梯
            if(nextWaypointID .equals("0x5c93bd410x4f0df142")) //B3 X光報到處
                image.setImageResource(R.drawable.b1);
            break;
        case "0x5c93bd410x4f0df142": //B3 X光報到處
            if(nextWaypointID .equals("0x0193bd410x780df142"))  //B1 樓梯
                image.setImageResource(R.drawable.b3);
            break;
        case "0x0800b8410x0200f042": //C6 大廳(病歷室前)
            if(nextWaypointID .equals("0x3519b8410x4d06f042")) //C9 大廳(電腦斷層室前)
                image.setImageResource(R.drawable.c6);
            break;
        case "0x3319b8410x4d06f042": //D1  樓梯
            if(nextWaypointID .equals("0x3219b8410x4d06f042")) //D2 岔路
                image.setImageResource(R.drawable.d1);
            break;
        case "0x021234110x00020000": //D2 神經部檢查室
            if(nextWaypointID .equals("0x3219b8410x4d06f042")) //D2 岔路
                image.setImageResource(R.drawable.d2);
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
