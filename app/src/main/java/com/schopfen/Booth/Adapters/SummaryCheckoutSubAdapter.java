package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CartBoothItemsData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

public class SummaryCheckoutSubAdapter extends RecyclerView.Adapter<SummaryCheckoutSubAdapter.MyViewHolder>{

    Context mContext;
    ArrayList<CartBoothItemsData> cartBoothItemsData;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SummaryCheckoutSubAdapter(Context mContext, ArrayList<CartBoothItemsData> cartBoothItemsData) {
        this.mContext = mContext;
        this.cartBoothItemsData = cartBoothItemsData;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        view = inflater.inflate(R.layout.summary_sub_adapter_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.producttitle.setText(cartBoothItemsData.get(position).getProductTitle());
        holder.productItemCount.setText(cartBoothItemsData.get(position).getProductQuantity1() + " " + "qty");
        holder.productItemPrice.setText(cartBoothItemsData.get(position).getProductPrice() + " " + cartBoothItemsData.get(position).getCurrencySymbol());

    }

    @Override
    public int getItemCount() {
        return cartBoothItemsData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView producttitle, productItemCount, productItemPrice;

        public MyViewHolder(View itemView) {
            super(itemView);

            producttitle = itemView.findViewById(R.id.producttitle);
            productItemCount = itemView.findViewById(R.id.productItemCount);
            productItemPrice = itemView.findViewById(R.id.productItemPrice);

        }
    }
}
