package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SelectedCateModel;
import com.schopfen.Booth.Models.SelectedCategories;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SelectedCategoriesAdapter extends RecyclerView.Adapter<SelectedCategoriesAdapter.MyViewHolder> {

    private ArrayList<SelectedCateModel> Data;
    private Context context;
    CustomItemClickListener listener;

    public SelectedCategoriesAdapter(Context context, ArrayList<SelectedCateModel> followersData, CustomItemClickListener listener) {
        this.context = context;
        this.Data = followersData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.selected_categories_adapter, parent, false);
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
        holder.img.setVisibility(View.GONE);
        Picasso.get().load(Constants.URL.IMG_URL + Data.get(position).getImage()).placeholder(R.drawable.darkgrey_circle).into(holder.img);
        holder.city.setVisibility(View.GONE);
        holder.username.setText(Data.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return Data.size();
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