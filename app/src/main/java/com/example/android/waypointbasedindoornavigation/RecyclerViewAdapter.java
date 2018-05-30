package com.example.android.waypointbasedindoornavigation;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.waypointbasedindoornavigation.RecyclerViewAdapter.MyViewHolder;

import java.util.Collections;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private LayoutInflater inflater;

    List<Node> data = Collections.emptyList();

    Context context;

    public RecyclerViewAdapter(Context context, List<Node> data){

        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }
    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.rowitem, null);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Node current = data.get(position);

        // determine which of location information to be displayed on UI
        holder.title.setText(current.getName());
        holder.region.setText(current.get_regionID());

        // an onclick listener for location names in ListViewActivity
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send Name, ID and Region of the selected location
                //to MainActivity
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("name", current.getName());
                i.putExtra("id", current.getID());
                i.putExtra("region", current.get_regionID());
                context.startActivity(i);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {

        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, region;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.listText);
            region = (TextView) itemView.findViewById(R.id.listRegion);
        }
    }
}
