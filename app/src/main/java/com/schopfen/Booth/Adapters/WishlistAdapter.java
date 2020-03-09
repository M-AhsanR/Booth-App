package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.WishListEtcData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MainCatData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder> {

    private ArrayList<WishListEtcData> wishlistArray;
    private Context context;
    CustomItemClickListener listener;

    public WishlistAdapter(Context context, ArrayList<WishListEtcData> wishlistArray, CustomItemClickListener listener) {
        this.context = context;
        this.wishlistArray = wishlistArray;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.wishlist_item, parent, false);
        final WishlistAdapter.MyViewHolder viewHolder = new WishlistAdapter.MyViewHolder(view);

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
        holder.boothTitle.setText(wishlistArray.get(position).getBoothName());
        holder.productTitle.setText(wishlistArray.get(position).getTitle());
        Picasso.get().load(Constants.URL.IMG_URL + wishlistArray.get(position).getProductImagesData().get(0).getProductCompressedImage()).into(holder.productImg);
    }

    @Override
    public int getItemCount() {
        return wishlistArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView productTitle, boothTitle;
        RoundedImageView productImg;

        MyViewHolder(View itemView) {
            super(itemView);
            productImg = itemView.findViewById(R.id.productImg);
            productTitle = itemView.findViewById(R.id.productTitle);
            boothTitle = itemView.findViewById(R.id.boothTitle);
        }
    }
}