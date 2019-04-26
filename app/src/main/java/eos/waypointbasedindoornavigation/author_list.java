package eos.waypointbasedindoornavigation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import eos.waypointbasedindoornavigation.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class author_list extends AppCompatActivity {
    // Array of strings for ListView Title
    String[] listviewTitle = new String[]{
            "Smashicons", "DinosoftLabs", "Eucalyp ", "DinosoftLabs",
            "Smashicons", "Freepik", "Prosymbols",
    };


    int[] listviewImage = new int[]{
            R.drawable.stethoscope2, R.drawable.cash, R.drawable.examination, R.drawable.pill,
            R.drawable.route, R.drawable.bathroom, R.drawable.home, R.drawable.stethoscope,
    };

    String[] listviewShortDescription = new String[]{
            "Icon made by Smashicons from www.flaticon.com", "Icon made by DinosoftLabs from www.flaticon.com", "Icon made by Eucalyp from www.freepik.com ", "Icon made by DinosoftLabs from www.flaticon.com ",
            "Icon made Smashicons from www.freepik.com", "Icon made by Freepik from www.flaticon.com", "Icon made by Prosymbols from www.freepik.com",
    };

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String,List<String>> listHash;

    //--------------------------------------------------------------------------------------------- 1/15 information 返回健
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(eos.waypointbasedindoornavigation.R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_home){
            Intent intent = new Intent();
            intent = new Intent(author_list.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(eos.waypointbasedindoornavigation.R.layout.activity_author_list);
        setTitle("相關資訊");


    /*
        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 8; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }
        String[] from = {"listview_image", "listview_title", "listview_discription"};
        //int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
        //ListView androidListView = (ListView) findViewById(R.id.ex_list);
        //androidListView.setAdapter(simpleAdapter);
        */
        listView = (ExpandableListView) findViewById(R.id.ex_list);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);



    }

    private void initData() {
        listDataHeader = new ArrayList<>();
        listDataHeader.add("版本資訊");
        listDataHeader.add("計畫名稱");
        listDataHeader.add("參與團隊");
        listDataHeader.add("圖片作者");

        listHash = new HashMap<>();


        List<String> version = new ArrayList<>();
        version.add("版本:1.0.0.0   ( 2019.04.26 )");

        List<String> profect_name = new ArrayList<>();
        profect_name.add("台大醫院雲林分院跨學界計畫");
        profect_name.add("科技部萌芽計畫");
        profect_name.add("中央研究院主題計畫");

        List<String> cooperation = new ArrayList<>();
        cooperation.add("雲科大嵌入式作業系統實驗室");
        cooperation.add("台大醫院雲林分院資訊室");
        cooperation.add("中研院資訊所張韻詩老師實驗室");

        List<String> author = new ArrayList<>();
        for(int i=0; i<listviewTitle.length;i++){
            author.add(listviewTitle[i] +"\n\n" +listviewShortDescription[i]);
        }


        listHash.put(listDataHeader.get(0),version);
        listHash.put(listDataHeader.get(1),profect_name);
        listHash.put(listDataHeader.get(2),cooperation);
        listHash.put(listDataHeader.get(3),author);



    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent = new Intent(author_list.this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}

