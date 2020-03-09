package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MyViewHolderAMAModel;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolderAMAAdapter extends RecyclerView.Adapter<MyViewHolderAMAAdapter.MyViewHolder> {

    private ArrayList<ProductImagesData> myViewHolderAMAModels;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<ProductImagesData> arraylistadapter;
    ArrayList<String> imageViewerArray = new ArrayList<>();

    public MyViewHolderAMAAdapter(Context context, ArrayList<ProductImagesData> arraylistadapter, CustomItemClickListener listener) {
        this.myViewHolderAMAModels = arraylistadapter;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolderAMAAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.myviewholder_ama_adapter, parent, false);
        final MyViewHolderAMAAdapter.MyViewHolder viewHolder = new MyViewHolderAMAAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolderAMAAdapter.MyViewHolder holder, int position) {

        Fresco.initialize(context);

        Picasso.get().load(Constants.URL.IMG_URL + myViewHolderAMAModels.get(position).getProductCompressedImage()).placeholder(R.color.grey5).into(holder.imageView);

        imageViewerArray.clear();
        for (int i = 0; i < myViewHolderAMAModels.size(); i++){
            imageViewerArray.add("https://baac.booth-in.com/" + myViewHolderAMAModels.get(i).getProductCompressedImage());
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(context, imageViewerArray)
                        .setStartPosition(0)
                        .show();
            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
//        (getContext()).getWindowManager()
//                .getDefaultDisplay()
//                .getMetrics(displayMetrics);
//        ImageView img = grid.findViewById(R.id.img);
        int width = displayMetrics.widthPixels/5;
        holder.imageView.setMinimumWidth(width);
        holder.imageView.setMaxHeight(width);
        holder.imageView.setMaxWidth(width);
        holder.imageView.setMinimumHeight(width);
    }

    @Override
    public int getItemCount() {
        return myViewHolderAMAModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

      RoundedImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_products_mvhaa);
        }
    }
}
