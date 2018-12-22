package com.example.android.waypointbasedindoornavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class author_list extends AppCompatActivity {
    // Array of strings for ListView Title
    String[] listviewTitle = new String[]{
            "Smashicons", "DinosoftLabs", "Eucalyp ", "DinosoftLabs",
            "Smashicons", "Freepik", "Freepik", "Prosymbols",
    };


    int[] listviewImage = new int[]{
            R.drawable.stethoscope2, R.drawable.cash, R.drawable.examination, R.drawable.pill,
            R.drawable.route, R.drawable.bathroom, R.drawable.home, R.drawable.stethoscope,
    };

    String[] listviewShortDescription = new String[]{
            "Icon made by Smashicons from www.flaticon.com", "Icon made by DinosoftLabs from www.flaticon.com", "Icon made by Eucalyp from www.freepik.com ", "Icon made by DinosoftLabs from www.flaticon.com ",
            "Icon made Smashicons from www.freepik.com", "Icon made by Freepik from www.flaticon.com", "Icon made by Freepik from www.flaticon.com", "Icon made by Prosymbols from www.freepik.com",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_list);
        setTitle("圖片作者清單");

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < 8; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_home || item.getItemId() == R.id.menu_home2){
            Intent intent = new Intent();
            intent = new Intent(author_list.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
