package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Models.BoothProfile_Product_Data;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class BoothProfile_Product_Adapter extends RecyclerView.Adapter<BoothProfile_Product_Adapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<BoothProfile_Product_Data> commentsArray;

    public BoothProfile_Product_Adapter(Context context, ArrayList<BoothProfile_Product_Data> commentsArray, CustomItemClickListener listener) {
        this.commentsArray = commentsArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BoothProfile_Product_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.boothprofile_product_adapter, parent, false);
        final BoothProfile_Product_Adapter.MyViewHolder viewHolder = new BoothProfile_Product_Adapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BoothProfile_Product_Adapter.MyViewHolder holder, int position) {
        final int radius = 0;
        final int margin = 0;
        final Transformation transformation = new RoundedCornersTransformation(radius, margin);
        if (!commentsArray.get(position).getProduct_image().isEmpty()){
            Picasso.get()
                    .load(Constants.URL.IMG_URL + commentsArray.get(position).getProduct_image().get(0).getImage())
                    .transform(transformation)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
        }
    }
}