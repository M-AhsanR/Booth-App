package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.SimilarProductsModel;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SimilarProductAdapter extends RecyclerView.Adapter<SimilarProductAdapter.MyViewHolder> {

    private ArrayList<SimilarProductsModel> list;
    private Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SimilarProductAdapter(ArrayList<SimilarProductsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.similar_product_adapter, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        Picasso.get().load(Constants.URL.IMG_URL + list.get(position).getProductImage()).placeholder(R.color.grey5).into(holder.imageView);

        holder.title.setText(list.get(position).getTitle());
        holder.booth_name.setText(list.get(position).getBoothName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                mEditor.putString("ProductID", list.get(position).getProductID()).apply();
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView imageView;
        TextView title, booth_name;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_products_mvhaa);
            title = itemView.findViewById(R.id.title);
            booth_name = itemView.findViewById(R.id.booth_name);
        }
    }
}
