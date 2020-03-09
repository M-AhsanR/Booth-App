package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowingsAdapter extends RecyclerView.Adapter<FollowingsAdapter.MyViewHolder> {

    private ArrayList<FollowersData> followingData;
    private Context context;
    CustomItemClickListener listener;
    String activity;

    public FollowingsAdapter(String Activity, Context context, ArrayList<FollowersData> followingData, CustomItemClickListener listener) {
        this.context = context;
        this.followingData = followingData;
        this.listener = listener;
        this.activity = Activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.followers_view, parent, false);
        final FollowingsAdapter.MyViewHolder viewHolder = new FollowingsAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if (activity.equals("booth")){
            holder.city.setText(followingData.get(position).getCityTitle());
            holder.username.setText("@" + followingData.get(position).getBoothUserName());
            Picasso.get().load(Constants.URL.IMG_URL + followingData.get(position).getCompressedBoothImage()).placeholder(R.drawable.darkgrey_circle).into(holder.img);
        }else if (activity.equals("user")){
            holder.city.setText(followingData.get(position).getCityTitle());
            holder.username.setText("@" + followingData.get(position).getUserName());
            Picasso.get().load(Constants.URL.IMG_URL + followingData.get(position).getCompressedImage()).placeholder(R.drawable.darkgrey_circle).into(holder.img);
        }

    }

    @Override
    public int getItemCount() {
        return followingData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView username, city;

        MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            username = itemView.findViewById(R.id.username);
            city = itemView.findViewById(R.id.city);

        }
    }
}