package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.HomeCateDataModel;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.BoothProfile_Product_Data;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;


public class HomeCateGridAdapter extends RecyclerView.Adapter<HomeCateGridAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<ProductsData> ProductsData;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    public static int size;
    String activity;

    public HomeCateGridAdapter(String Activity, Context context, ArrayList<ProductsData> productsData, CustomItemClickListener listener) {
        this.ProductsData = productsData;
        this.context = context;
        this.listener = listener;
        this.activity = Activity;
    }

    @Override
    public HomeCateGridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.boothprofile_product_adapter, parent, false);
        final HomeCateGridAdapter.MyViewHolder viewHolder = new HomeCateGridAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomeCateGridAdapter.MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        if (!ProductsData.get(position).getProductImagesData().isEmpty()) {
            Picasso.get().load(Constants.URL.IMG_URL + ProductsData.get(position).getProductImagesData().get(0).getProductCompressedImage()).into(holder.imageView);
        }
        if (!activity.equals("Explore")){
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Home_Home_Fragment.refreshposition = position;
                    Home_Home_Fragment.refresh = true;
                    Intent intent = new Intent(context, ProductDetailsActivity.class);
                    mEditor.putString("ProductID", ProductsData.get(position).getProductID()).apply();
                    context.startActivity(intent);
                }
            });
        }else {
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginDialog();
                }
            });
        }

    }

    private void LoginDialog() {
        final Dialog verifyDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.login_first_dialog);

        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);

        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDialog.dismiss();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity) context).finishAffinity();
                verifyDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout imageLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            imageLayout = itemView.findViewById(R.id.imageLayout);
        }
    }
    public void addfeed(ArrayList<ProductsData> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            ProductsData.add(feed.get(a));
        }
        size = ProductsData.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }
}