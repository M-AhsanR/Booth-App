package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.MainCategories_Model;
import com.schopfen.Booth.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MainCategories_Adapter extends RecyclerView.Adapter<MainCategories_Adapter.MyViewHolder> {

    private List<MainCategories_Model> mainCategories_models;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<MainCategories_Model> arraylistadapter;

    public MainCategories_Adapter(Context context, ArrayList<MainCategories_Model> arraylistadapter, CustomItemClickListener listener) {
        this.mainCategories_models = arraylistadapter;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MainCategories_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.adapter_categories_list, parent, false);
        final MainCategories_Adapter.MyViewHolder viewHolder = new MainCategories_Adapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainCategories_Adapter.MyViewHolder holder, int position) {
        MainCategories_Model lisadapter = mainCategories_models.get(position);
        holder.category_name.setText(lisadapter.getItemTitle());
        holder.catsub_item.setText(lisadapter.getSubTitle());
        Log.d("imgurl", lisadapter.getImg_url());
    }

    @Override
    public int getItemCount() {
        return mainCategories_models.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category_name,catsub_item;
        RelativeLayout relativeLayout;
        RoundedImageView category_img;

        MyViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_cat_cla);
            category_name = itemView.findViewById(R.id.tv_cattitle_cla);
            catsub_item = itemView.findViewById(R.id.tv_catsubitem_cla);
            category_img = itemView.findViewById(R.id.iv_imgcat_cla);
        }
    }
}
