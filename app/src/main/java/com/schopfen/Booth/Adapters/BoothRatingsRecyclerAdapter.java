package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.R;

import java.util.ArrayList;

public class BoothRatingsRecyclerAdapter extends RecyclerView.Adapter<BoothRatingsRecyclerAdapter.MyViewHolder> {

    private ArrayList<String> ratingsArray;
    private Context context;
    CustomItemClickListener listener;

    public BoothRatingsRecyclerAdapter(Context context, ArrayList<String> ratingsArray, CustomItemClickListener listener) {
        this.context = context;
        this.ratingsArray = ratingsArray;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.booth_reviews_view, parent, false);
        final BoothRatingsRecyclerAdapter.MyViewHolder viewHolder = new BoothRatingsRecyclerAdapter.MyViewHolder(view);

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

    }

    @Override
    public int getItemCount() {
        return ratingsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MyViewHolder(View itemView) {
            super(itemView);


        }
    }
}