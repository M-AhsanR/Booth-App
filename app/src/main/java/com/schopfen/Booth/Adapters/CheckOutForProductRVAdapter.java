package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MainCatData;
import com.schopfen.Booth.Social.CheckOutForProductsRVModel;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CheckOutForProductRVAdapter extends RecyclerView.Adapter<CheckOutForProductRVAdapter.MyViewHolder> {

    private List<CheckOutForProductsRVModel> checkOutForProductsRVModels;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<MainCatData> arraylistadapter;

    public CheckOutForProductRVAdapter(Context context, ArrayList<CheckOutForProductsRVModel> arraylistadapter, CustomItemClickListener listener) {
        this.checkOutForProductsRVModels = arraylistadapter;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public CheckOutForProductRVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.checkout_for_productrv_adapter, parent, false);
        final CheckOutForProductRVAdapter.MyViewHolder viewHolder = new CheckOutForProductRVAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CheckOutForProductRVAdapter.MyViewHolder holder, int position) {
        CheckOutForProductsRVModel lisadapter = checkOutForProductsRVModels.get(position);
        holder.title.setText(lisadapter.getPro_title());
        holder.items.setText(lisadapter.getNum_items());
        holder.price.setText(lisadapter.getPrice());
      //  Log.d("imgurl", lisadapter.getImg_url());
    }

    @Override
    public int getItemCount() {
        return checkOutForProductsRVModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title,items,price;

        MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_protitle_cofpra);
            items = itemView.findViewById(R.id.tv_items_cofpra);
            price = itemView.findViewById(R.id.tv_price_cofpra);
        }
    }
}
