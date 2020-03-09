package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.RegisterBoothActivity;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.HomeCateDataModel;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.fxn.pix.Pix.start;

public class AddProdImgsAdapter extends RecyclerView.Adapter<AddProdImgsAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<String> imgsArray;
    CustomItemClickListener listener;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    int selected_position;
    public static final String MyPREFERENCES = "MyPrefs";

    public AddProdImgsAdapter(Context mContext, ArrayList<String> imgsArray, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.imgsArray = imgsArray;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        view = inflater.inflate(R.layout.image_pick_item, parent, false);

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
    public void onBindViewHolder(@NonNull AddProdImgsAdapter.MyViewHolder holder, final int position) {
        if (position != imgsArray.size()){
           // holder.img.setBackgroundResource(R.drawable.ic_main_logo);
            holder.add_image.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
/*            holder.add_image.setBackgroundResource(R.drawable.add_icon);
            holder.img.setBackgroundResource(R.drawable.custom_dialog_background);*/
        }
        if (position == imgsArray.size()){
            holder.add_image.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> list = new ArrayList<>();
                list.set(position, String.valueOf(mContext.getResources().getDrawable(R.drawable.ic_main_logo)));
                imgsArray.add(String.valueOf(list));
                CheckOutForAddressRVModel addressmodel1 = new CheckOutForAddressRVModel();
                notifyDataSetChanged();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        int width = displayMetrics.widthPixels/6;
        holder.img.setMinimumWidth(width);
        holder.img.setMaxHeight(width);
        holder.img.setMaxWidth(width);
        holder.img.setMinimumHeight(width);
    }

    @Override
    public int getItemCount() {
        return imgsArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView img;
        LinearLayout add_image;

        public MyViewHolder(View itemView) {
            super(itemView);

            add_image = itemView.findViewById(R.id.add_image);
            img = itemView.findViewById(R.id.img);
        }
    }




}

