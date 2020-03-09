package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.ThemesData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BoothMainThemeAdapter extends RecyclerView.Adapter<BoothMainThemeAdapter.MyViewHolder> {

    private ArrayList<ThemesData> themesData;
    private Context context;
    CustomItemClickListener listener;

    public BoothMainThemeAdapter(Context context, ArrayList<ThemesData> themesData, CustomItemClickListener listener) {
        this.context = context;
        this.themesData = themesData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.booth_main_theme_recycler_item, parent, false);
        final BoothMainThemeAdapter.MyViewHolder viewHolder = new BoothMainThemeAdapter.MyViewHolder(view);

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
        Picasso.get().load(Constants.URL.IMG_URL + themesData.get(position).getImage()).into(holder.themeImg);
    }

    @Override
    public int getItemCount() {
        return themesData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView themeImg;
        ProgressBar progressBar;

        MyViewHolder(View itemView) {
            super(itemView);

            themeImg = itemView.findViewById(R.id.themeImg);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }
}