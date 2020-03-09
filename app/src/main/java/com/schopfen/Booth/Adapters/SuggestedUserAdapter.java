package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.Models.SuggestedBoothsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedUserAdapter extends RecyclerView.Adapter<SuggestedUserAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<SuggestedBoothsData> suggestedBoothsData;
    public static ArrayList<String> boothsIDs = new ArrayList<>();

    public SuggestedUserAdapter(Context context, ArrayList<SuggestedBoothsData> suggestedBoothsData, CustomItemClickListener listener) {
        this.suggestedBoothsData = suggestedBoothsData;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SuggestedUserAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.suggested_user_adapter, parent, false);
        final SuggestedUserAdapter.MyViewHolder viewHolder = new SuggestedUserAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SuggestedUserAdapter.MyViewHolder holder, int position) {

        Picasso.get().load(Constants.URL.IMG_URL + suggestedBoothsData.get(position).getCompressedImage()).placeholder(R.drawable.user).into(holder.img);
        holder.city.setText(suggestedBoothsData.get(position).getCityTitle());
        holder.username.setText(suggestedBoothsData.get(position).getBoothName());

        holder.view1Linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.selector.getVisibility() == View.VISIBLE){
                    holder.selector.setVisibility(View.GONE);
                    boothsIDs.remove(suggestedBoothsData.get(position).getUserID());
                    Log.e("boothID", boothsIDs.toString());
                }else {
                    holder.selector.setVisibility(View.VISIBLE);
                    boothsIDs.add(suggestedBoothsData.get(position).getUserID());
                    Log.e("boothID", boothsIDs.toString());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return suggestedBoothsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView username, city;
        ImageView selector;
        LinearLayout view1Linear;

        MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            username = itemView.findViewById(R.id.username);
            city = itemView.findViewById(R.id.city);
            selector = itemView.findViewById(R.id.user_selection);
            view1Linear = itemView.findViewById(R.id.view1Linear);

        }
    }
}