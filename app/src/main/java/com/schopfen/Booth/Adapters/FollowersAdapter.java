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

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {

    private ArrayList<FollowersData> followersData;
    private Context context;
    CustomItemClickListener listener;

    public FollowersAdapter(Context context, ArrayList<FollowersData> followersData, CustomItemClickListener listener) {
        this.context = context;
        this.followersData = followersData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.followers_view, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

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
        holder.img.setVisibility(View.VISIBLE);
        Picasso.get().load(Constants.URL.IMG_URL + followersData.get(position).getCompressedImage()).placeholder(R.drawable.darkgrey_circle).into(holder.img);
        holder.city.setText(followersData.get(position).getCityTitle());
        holder.username.setText("@" + followersData.get(position).getUserName());

    }

    @Override
    public int getItemCount() {
        return followersData.size();
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