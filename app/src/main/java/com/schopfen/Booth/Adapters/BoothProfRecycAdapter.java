package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import static java.security.AccessController.getContext;

public class BoothProfRecycAdapter extends RecyclerView.Adapter<BoothProfRecycAdapter.MyViewHolder> {

    private ArrayList<String> productsData;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<CheckOutForAddressRVModel> arraylistadapter;
    List<TextView> list = new ArrayList<>();
    int selected_position;

    public BoothProfRecycAdapter(Context context, ArrayList<String> productsData, CustomItemClickListener listener) {
        this.productsData = productsData;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.home_cate_grid_item, parent, false);
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
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        int width = displayMetrics.widthPixels/3;
//        holder.productImg.setMinimumWidth(width);
//        holder.productImg.setMaxWidth(width);
//        holder.productImg.setMinimumHeight(width);
//        holder.productImg.setMaxHeight(width);
//        Log.e("width", width + " ");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
//        ImageView img = grid.findViewById(R.id.img);
        int width = displayMetrics.widthPixels/3;
        holder.productImg.setMinimumWidth(width);
        holder.productImg.setMaxHeight(width);
        holder.productImg.setMaxWidth(width);
        holder.productImg.setMinimumHeight(width);
        Log.e("width", width + " ");

    }

    @Override
    public int getItemCount() {
        return productsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView productImg;

        MyViewHolder(View itemView) {
            super(itemView);

            productImg = itemView.findViewById(R.id.img);
        }
    }
}
