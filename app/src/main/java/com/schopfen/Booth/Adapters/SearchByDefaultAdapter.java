package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.SearchByDefaultData;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SearchByDefaultAdapter extends RecyclerView.Adapter<SearchByDefaultAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<ProductsData> searchByDefaultData;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SearchByDefaultAdapter(Context context, ArrayList<ProductsData> productsData, CustomItemClickListener listener) {
        this.searchByDefaultData = productsData;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SearchByDefaultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.searchbydefaultadapter, parent, false);
        final SearchByDefaultAdapter.MyViewHolder viewHolder = new SearchByDefaultAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SearchByDefaultAdapter.MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();


            Picasso.get().load(Constants.URL.IMG_URL + searchByDefaultData.get(position).getProductImagesData().get(0).getProductCompressedImage()).into(holder.imageView);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                mEditor.putString("ProductID", searchByDefaultData.get(position).getProductID()).apply();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchByDefaultData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        LinearLayout imageLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_product);
            imageLayout = itemView.findViewById(R.id.imageLayout);
        }
    }
}