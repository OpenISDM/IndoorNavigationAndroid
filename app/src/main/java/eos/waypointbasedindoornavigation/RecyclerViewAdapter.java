package eos.waypointbasedindoornavigation;

/*--

Module Name:

    RecyclerViewAdapter.java

Abstract:

    This module works as an adapter between waypoint information
    and location UI display

Author:

    Phil Wu 01-Feb-2018

--*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import eos.waypointbasedindoornavigation.RecyclerViewAdapter.MyViewHolder;

import java.util.Collections;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private LayoutInflater inflater;

    List<Node> data = Collections.emptyList();
    Boolean clicked = false;
    Context context;

    public RecyclerViewAdapter(Context context, List<Node> data){
        Log.i("xxx_List","RecyclerViewAdapter");
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }
    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("xxx_List","onCreateViewHolder");
        View view = inflater.inflate(R.layout.rowitem, null);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Log.i("xxx_List", "onBindViewHolder Postion = " + position);
        final Node current = data.get(position);
        // determine which of location information to be displayed on UI
        holder.title.setText(current.getName());
        //holder.region.setText(current.get_regionID());
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        holder.title.setWidth(width);
        // an onclick listener for location names in ListViewActivity

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked == false) {
                    clicked = true;
                    Log.i("xxx_List", "title");
                    // send Name, ID and Region of the selected location
                    //to MainActivity
                    Intent i = new Intent(context, NavigationActivity.class);
                    i.putExtra("destinationName", current.getName());
                    i.putExtra("destinationID", current.getID());
                    i.putExtra("destinationRegion", current.get_regionID());
                    context.startActivity(i);
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private Button title;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (Button) itemView.findViewById(R.id.listText);
          /*  region = (TextView) itemView.findViewById(R.id.listRegion);*/

        }
    }
}
