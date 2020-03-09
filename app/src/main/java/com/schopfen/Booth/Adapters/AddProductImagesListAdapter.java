package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.CreateAddressDialog;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Fragments.AddProductFragment;
import com.schopfen.Booth.Models.AddressData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProductImagesListAdapter extends RecyclerView.Adapter<AddProductImagesListAdapter.MyViewHolder> {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    private List<Bitmap> bitmapList;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<CheckOutForAddressRVModel> arraylistadapter;
    List<TextView> list = new ArrayList<>();
    private int selected_position = 0;
    public static String addressID = "";
    String activity;

    public AddProductImagesListAdapter(String Actiity, Context context, ArrayList<Bitmap> addressData, CustomItemClickListener listener) {
        this.bitmapList = addressData;
        this.context = context;
        this.listener = listener;
        this.activity = Actiity;
    }

    @Override
    public AddProductImagesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.addproduct_images_list_adapter, parent, false);
        final AddProductImagesListAdapter.MyViewHolder viewHolder = new AddProductImagesListAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull final AddProductImagesListAdapter.MyViewHolder holder, final int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        holder.image_view.setImageBitmap(bitmapList.get(position));

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 6;

        holder.image_view.setMinimumWidth(width);
        holder.image_view.setMaxHeight(width);
        holder.image_view.setMaxWidth(width);
        holder.image_view.setMinimumHeight(width);

        holder.delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog alertdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                alertdialog.setContentView(R.layout.view_del_dialog);
                LinearLayout detail = alertdialog.findViewById(R.id.choose_image);
                LinearLayout del = alertdialog.findViewById(R.id.choose_video);

                alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertdialog.setCancelable(true);
                alertdialog.show();

                detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertdialog.dismiss();
                        ShowImage(bitmapList.get(position));
                    }
                });

                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertdialog.dismiss();
                        if (activity.equals("activity")){
                            AddProductActivity.bitmapArrayList.remove(position);
                            AddProductActivity.uriArrayList.remove(position);
                        }else {
                            AddProductFragment.bitmapArrayList.remove(position);
                            AddProductFragment.uriArrayList.remove(position);
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        });

    }

    private void ShowImage(Bitmap bitmap){
        final Dialog alertdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        alertdialog.setContentView(R.layout.detail_image_dialog);
        ImageView detail = alertdialog.findViewById(R.id.detailImage);

        detail.setImageBitmap(bitmap);

        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertdialog.setCancelable(true);
        alertdialog.show();
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView image_view;
        ImageView delete_image;

        MyViewHolder(View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.image_view);
            delete_image = itemView.findViewById(R.id.delet_image);
        }
    }

}

