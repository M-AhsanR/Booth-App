package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.Order_Details_Model;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class Order_Details_Adapter extends RecyclerView.Adapter<Order_Details_Adapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<OrderItemsData> productData;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public Order_Details_Adapter(Context context, ArrayList<OrderItemsData> productData) {
        this.productData = productData;
        this.context = context;
    }

    @Override
    public Order_Details_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.orders_details_adapter, parent, false);
        final Order_Details_Adapter.MyViewHolder viewHolder = new Order_Details_Adapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Order_Details_Adapter.MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Picasso.get().load(Constants.URL.IMG_URL + productData.get(position).getProductImage()).into(holder.productImage);
        holder.productTitle.setText(productData.get(position).getProductTitle());
        holder.price.setText(productData.get(position).getPrice());
        holder.currency.setText(productData.get(position).getCurrency());

        holder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                mEditor.putString("ProductID", productData.get(position).getProductID()).apply();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView productImage;
        TextView productTitle, currency, price;

        MyViewHolder(View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productTitle = itemView.findViewById(R.id.productTitle);
            currency = itemView.findViewById(R.id.currencyText);
            price = itemView.findViewById(R.id.price_text);

        }
    }
}

