package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class FriendsAdapterImagesAdapter extends RecyclerView.Adapter<FriendsAdapterImagesAdapter.MyViewHolder> {

    private ArrayList<ProductImagesData> myViewHolderAMAModels;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<ProductImagesData> arraylistadapter;
    ArrayList<String> imageViewerArray = new ArrayList<>();
    String ID;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public FriendsAdapterImagesAdapter(String id, Context context, ArrayList<ProductImagesData> arraylistadapter, CustomItemClickListener listener) {
        this.myViewHolderAMAModels = arraylistadapter;
        this.context = context;
        this.listener = listener;
        this.ID = id;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.myviewholder_ama_adapter, parent, false);
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

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Picasso.get().load(Constants.URL.IMG_URL + myViewHolderAMAModels.get(position).getProductCompressedImage()).placeholder(R.color.grey5).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] id = ID.split(",");
                if (id.length > 1){
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    mEditor.putString("ProductID", id[position]).commit();
                    context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    mEditor.putString("ProductID", id[0]).commit();
                    context.startActivity(intent);
                }
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

