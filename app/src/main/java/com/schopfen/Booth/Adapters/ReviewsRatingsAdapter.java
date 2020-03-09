package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.Models.ReviewsAndRatingsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsRatingsAdapter extends RecyclerView.Adapter<ReviewsRatingsAdapter.MyViewHolder> {

    private ArrayList<ReviewsAndRatingsData> reviewsAndRatingsData;
    private Context context;
    CustomItemClickListener listener;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public ReviewsRatingsAdapter(Context context, ArrayList<ReviewsAndRatingsData> reviewsAndRatingsData, CustomItemClickListener listener) {
        this.context = context;
        this.reviewsAndRatingsData = reviewsAndRatingsData;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.rating_reviews_item, parent, false);
        final ReviewsRatingsAdapter.MyViewHolder viewHolder = new ReviewsRatingsAdapter.MyViewHolder(view);

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
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        if (reviewsAndRatingsData.get(position).getLastState().equals("user")) {
            holder.username.setText("@" + reviewsAndRatingsData.get(position).getUserName());
            Picasso.get().load(Constants.URL.IMG_URL + reviewsAndRatingsData.get(position).getCompressedImage()).placeholder(R.drawable.user).into(holder.img);
            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", reviewsAndRatingsData.get(position).getUserID());
                    context.startActivity(intent);
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", reviewsAndRatingsData.get(position).getUserID());
                    context.startActivity(intent);
                }
            });
        } else if (reviewsAndRatingsData.get(position).getLastState().equals("booth")) {
            holder.username.setText("@" + reviewsAndRatingsData.get(position).getBoothUserName());
            Picasso.get().load(Constants.URL.IMG_URL + reviewsAndRatingsData.get(position).getCompressedBoothImage()).placeholder(R.drawable.darkgrey_circle).into(holder.img);
            holder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", reviewsAndRatingsData.get(position).getUserID());
                    context.startActivity(intent);
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", reviewsAndRatingsData.get(position).getUserID());
                    context.startActivity(intent);
                }
            });
        }
        holder.time.setText(TimeAgo.getTimeAgo(Long.parseLong(reviewsAndRatingsData.get(position).getCreatedAt())));
        holder.time.setVisibility(View.GONE);
        holder.rating.setRating(Float.parseFloat(reviewsAndRatingsData.get(position).getUserOrderRequestRating()));
        holder.comment.setText(reviewsAndRatingsData.get(position).getUserOrderRequestReview());

    }


    @Override
    public int getItemCount() {
        return reviewsAndRatingsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView img;
        TextView username, time, comment;
        RatingBar rating;

        MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            username = itemView.findViewById(R.id.username);
            time = itemView.findViewById(R.id.time);
            rating = itemView.findViewById(R.id.ratings);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}