package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.Question_Search_Images_Data;
import com.schopfen.Booth.Models.AccountsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Question_Search_Recycler_Adapter extends RecyclerView.Adapter<Question_Search_Recycler_Adapter.MyViewHolder> {

    Context context;
    ArrayList<Question_Search_Images_Data> arrayList;
    ArrayList<String> imageViewerArray = new ArrayList<>();

    public Question_Search_Recycler_Adapter(Context context, ArrayList<Question_Search_Images_Data> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Question_Search_Recycler_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_search_recycler_adapter, parent, false);
        return new Question_Search_Recycler_Adapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Question_Search_Recycler_Adapter.MyViewHolder holder, int position) {
        Fresco.initialize(context);
        Picasso.get().load(Constants.URL.IMG_URL + arrayList.get(position).getImage()).into(holder.pro_image);
        for(int i=0; i<arrayList.size(); i++){
            imageViewerArray.add(Constants.URL.IMG_URL + arrayList.get(i).getImage());
        }
        holder.pro_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageViewer.Builder(context, imageViewerArray)
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView pro_image;

        MyViewHolder(View itemView) {
            super(itemView);
            pro_image = itemView.findViewById(R.id.iv_question_search_recycler);
        }
    }
}