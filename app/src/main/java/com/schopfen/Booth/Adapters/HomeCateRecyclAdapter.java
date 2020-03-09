package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.HomeCateDataModel;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class HomeCateRecyclAdapter extends RecyclerView.Adapter<HomeCateRecyclAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<ProductsData> homeCateDataArray;
    CustomItemClickListener listener;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    List<HomeCateDataModel> list;

    public HomeCateRecyclAdapter(Context mContext, ArrayList<ProductsData> homeCateData, CustomItemClickListener listener) {
        this.mContext = mContext;
        this.homeCateDataArray = homeCateData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        view = inflater.inflate(R.layout.categ_recyc_item1, parent, false);

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
    public void onBindViewHolder(@NonNull HomeCateRecyclAdapter.MyViewHolder holder, final int position) {

        Log.e("size", homeCateDataArray.size() + " ");

        CateViewPagerAdapter cateViewPagerAdapter = new CateViewPagerAdapter(mContext, homeCateDataArray);
        holder.viewPager.setClipToPadding(false);
//        holder.viewPager.setOffscreenPageLimit(2);
        holder.viewPager.setPadding(0, 0, 0, 0);
        holder.viewPager.setPageMargin(0);
        holder.viewPager.setAdapter(cateViewPagerAdapter);
    }

    @Override
    public int getItemCount() {
        return homeCateDataArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ViewPager viewPager;

        public MyViewHolder(View itemView) {
            super(itemView);

            viewPager = itemView.findViewById(R.id.cat_viewPager);
        }
    }
}

