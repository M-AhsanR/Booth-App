package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.schopfen.Booth.DataClasses.SearchExploreArrayData;
import com.schopfen.Booth.DataClasses.SearchQuestionsArrayData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchExploreAdapter extends RecyclerView.Adapter<SearchExploreAdapter.MyViewHolder> {

    Context context;
    ArrayList<SearchExploreArrayData> arrayList;

    public SearchExploreAdapter(Context context, ArrayList<SearchExploreArrayData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_explore_adapter_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.explore_image.setImageResource(R.drawable.camera_image);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView explore_image;

        MyViewHolder(View itemView) {
            super(itemView);

            explore_image = itemView.findViewById(R.id.explore_image);

        }
    }
}