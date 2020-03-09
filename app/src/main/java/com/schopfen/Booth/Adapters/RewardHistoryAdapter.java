package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RewardHistoryAdapter extends RecyclerView.Adapter<RewardHistoryAdapter.MyViewHolder> {

    private ArrayList<String> rewardHistoryArray;
    private Context context;
    CustomItemClickListener listener;

    public RewardHistoryAdapter(Context context, ArrayList<String> rewardHistoryArray, CustomItemClickListener listener) {
        this.context = context;
        this.rewardHistoryArray = rewardHistoryArray;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.reward_hist_recyc_item, parent, false);
        final RewardHistoryAdapter.MyViewHolder viewHolder = new RewardHistoryAdapter.MyViewHolder(view);

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
//        MainCatData lisadapter = categoriesListModels.get(position);
//        holder.category_name.setText(lisadapter.getItemTitle());
//        holder.catsub_item.setText(lisadapter.getSubTitle());
//        Log.d("imgurl", lisadapter.getImg_url());
    }

    @Override
    public int getItemCount() {
        return rewardHistoryArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

//        TextView category_name,catsub_item;
//        RelativeLayout relativeLayout;
//        RoundedImageView category_img;

        MyViewHolder(View itemView) {
            super(itemView);
//            relativeLayout = itemView.findViewById(R.id.rl_cat_cla);
//            category_name = itemView.findViewById(R.id.tv_cattitle_cla);
//            catsub_item = itemView.findViewById(R.id.tv_catsubitem_cla);
//            category_img = itemView.findViewById(R.id.iv_imgcat_cla);
        }
    }
}