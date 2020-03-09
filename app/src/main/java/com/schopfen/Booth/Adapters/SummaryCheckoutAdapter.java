package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CartBoothsData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

public class SummaryCheckoutAdapter extends RecyclerView.Adapter<SummaryCheckoutAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<CartBoothsData> cartBoothsData;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SummaryCheckoutAdapter(Context mContext, ArrayList<CartBoothsData> cartBoothsData) {
        this.mContext = mContext;
        this.cartBoothsData = cartBoothsData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        view = inflater.inflate(R.layout.checkout_summary_items, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.totalItemsCount.setText("(" + cartBoothsData.get(position).getCartBoothItemsData().size() + ")");
        holder.boothName.setText(cartBoothsData.get(position).getBoothName());

        float beforeprice = 0;
        for (int i = 0; i < cartBoothsData.get(position).getCartBoothItemsData().size(); i++) {
            float propricefortotal = Float.parseFloat(cartBoothsData.get(position).getCartBoothItemsData().get(i).getProductPrice()) * Integer.parseInt(cartBoothsData.get(position).getCartBoothItemsData().get(i).getProductQuantity1());
            beforeprice = beforeprice + propricefortotal;
            Log.e("Priceis", String.valueOf(beforeprice));
        }

        holder.totalPrice.setText(String.valueOf(beforeprice) + " " + cartBoothsData.get(position).getCartBoothItemsData().get(0).getCurrency());
        float VatPrice = (beforeprice * Float.valueOf(cartBoothsData.get(position).getVatPercentage())/100);
        holder.vatPercentage.setText("(" + cartBoothsData.get(position).getVatPercentage() + "%" + ")");

        float deliveryCharges = 0;
        for (int i = 0; i < cartBoothsData.get(position).getCartBoothItemsData().size(); i++) {
            float dc = Float.parseFloat(cartBoothsData.get(position).getCartBoothItemsData().get(i).getDeliveryCharges());
            deliveryCharges = deliveryCharges + dc;
            Log.e("Priceis", String.valueOf(deliveryCharges));
        }

        holder.deliveryCharges.setText(String.valueOf(deliveryCharges) + " " + cartBoothsData.get(position).getCartBoothItemsData().get(0).getCurrency());


        holder.vatPrice.setText(String.valueOf(VatPrice) + " " + cartBoothsData.get(position).getCartBoothItemsData().get(0).getCurrency());
        holder.grandTotalItems.setText("(" + cartBoothsData.get(position).getCartBoothItemsData().size() + ")");
        holder.grandTotalPrice.setText(String.valueOf(beforeprice + VatPrice + deliveryCharges) + " " + cartBoothsData.get(position).getCartBoothItemsData().get(0).getCurrency());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        holder.productsList.setLayoutManager(layoutManager);
//        holder.productsList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        SummaryCheckoutSubAdapter shopCartSubRecycAdapter = new SummaryCheckoutSubAdapter(mContext, cartBoothsData.get(position).getCartBoothItemsData());
        holder.productsList.setAdapter(shopCartSubRecycAdapter);

    }

    @Override
    public int getItemCount() {
        return cartBoothsData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView boothName, totalItemsCount, totalPrice, vatPercentage, vatPrice, grandTotalItems, grandTotalPrice, deliveryCharges;
        RecyclerView productsList;

        public MyViewHolder(View itemView) {
            super(itemView);

            boothName = itemView.findViewById(R.id.boothName);
            totalItemsCount = itemView.findViewById(R.id.totalItemsCount);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            vatPercentage = itemView.findViewById(R.id.vatPercentage);
            vatPrice = itemView.findViewById(R.id.vatPrice);
            grandTotalItems = itemView.findViewById(R.id.grandTotalItems);
            grandTotalPrice = itemView.findViewById(R.id.grandTotalPrice);
            productsList = itemView.findViewById(R.id.productsList);
            deliveryCharges = itemView.findViewById(R.id.deliveryCharges);

        }
    }
}
