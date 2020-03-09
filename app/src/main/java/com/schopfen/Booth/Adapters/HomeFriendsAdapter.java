package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bachors.wordtospan.WordToSpan;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.HomeFriendsModel;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFriendsAdapter extends RecyclerView.Adapter<HomeFriendsAdapter.MyViewHolder> {

    Context context;
    ArrayList<HomeFriendsModel> list;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public HomeFriendsAdapter(Context context, ArrayList<HomeFriendsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.home_firends_adapter, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (position == 0) {
            holder.spacer.setVisibility(View.VISIBLE);
        } else {
            holder.spacer.setVisibility(View.GONE);
        }

        Picasso.get().load(Constants.URL.IMG_URL + list.get(position).getUserImage()).placeholder(R.drawable.user).into(holder.user_image);
        holder.user_name.setText("@" + list.get(position).getUserName());
//        holder.notification_text.setText(list.get(position).getNotificationText());
        holder.time.setText(TimeAgo.getTimeAgo(Long.parseLong(list.get(position).getCreatedAt())));


        WordToSpan link = new WordToSpan();
        link.setColorMENTION(context.getResources().getColor(R.color.orange))
//                .setColorURL(Color.MAGENTA)
//                .setColorPHONE(Color.RED)
//                .setColorMAIL(getResources().getColor(R.color.colorPrimary))
//                .setColorTAG(context.getResources().getColor(R.color.orange))
                .setUnderlineURL(true)
                .setLink(list.get(position).getNotificationText())
                .into(holder.notification_text)
                .setClickListener(new WordToSpan.ClickListener() {
                    @Override
                    public void onClick(String type, String text) {
                        // type: "tag", "mail", "url", "phone", "mention" or "custom"
//                        Toast.makeText(context, "Type: " + type + "\nText: " + text, Toast.LENGTH_LONG).show();
                        MentionFuntion(text.substring(1), position);
                    }
                });


        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getUserType().equals("user")) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getUserID());
                    context.startActivity(intent);
                } else if (list.get(position).getUserType().equals("booth")) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        holder.user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getUserType().equals("user")) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getUserID());
                    context.startActivity(intent);
                } else if (list.get(position).getUserType().equals("booth")) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!list.get(position).getType().equals("order")) {
                    if (!list.get(position).getQuestionID().equals("0") || !list.get(position).getProductID().equals("0")) {
                        if (list.get(position).getProductID().equals("0") || list.get(position).getProductID().isEmpty()) {
                            Intent intent = new Intent(context, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", list.get(position).getQuestionID()).apply();
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, ProductDetailsActivity.class);
                            mEditor.putString("ProductID", list.get(position).getProductID()).apply();
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });

        if ((list.get(position).getQuestionID().equals("0") || list.get(position).getQuestionID().isEmpty()) && (list.get(position).getProductID().equals("0") || list.get(position).getProductID().isEmpty())) {
            holder.recycler_view.setVisibility(View.GONE);
            holder.description.setVisibility(View.GONE);
        } else {
            if (list.get(position).getProductID().equals("0") || list.get(position).getProductID().isEmpty()) {
                holder.recycler_view.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.recycler_view.setLayoutManager(layoutManager);
                FriendsAdapterImagesAdapter adapter;
                adapter = new FriendsAdapterImagesAdapter(list.get(position).getQuestionID(), context, list.get(position).getQuestion().getProductImagesData(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                    }
                });

                holder.recycler_view.setAdapter(adapter);
            } else {
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
                holder.recycler_view.setLayoutManager(layoutManager);
                FriendsAdapterImagesAdapter adapter;
                ArrayList<ProductImagesData> imagesData = new ArrayList<>();
                for (int j = 0; j < list.get(position).getProduct().size(); j++) {
                    imagesData.add(new ProductImagesData(list.get(position).getProduct().get(j).getProductImagesData().get(0).getProductCompressedImage(), list.get(position).getProduct().get(j).getProductImagesData().get(0).getProductImage()));
                }
                adapter = new FriendsAdapterImagesAdapter(list.get(position).getProductID(), context, imagesData, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                    }
                });


                holder.recycler_view.setAdapter(adapter);
            }
        }

    }

    private void MentionFuntion(String name, int position) {
        for (int i = 0; i < list.get(position).getMentionedUsersInfos().size(); i++) {
            if (list.get(position).getMentionedUsersInfos().get(i).getMentionedName().contains(name)) {
                if (list.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("user")) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getMentionedUsersInfos().get(i).getUserID());
                    context.startActivity(intent);
                } else if (list.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("booth")) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", list.get(position).getMentionedUsersInfos().get(i).getUserID());
                    context.startActivity(intent);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView user_image;
        TextView user_name, notification_text, time, description, city;
        RecyclerView recycler_view;
        LinearLayout spacer, main_layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            user_image = itemView.findViewById(R.id.user_image);
            main_layout = itemView.findViewById(R.id.main_layout);
            user_name = itemView.findViewById(R.id.user_name);
            notification_text = itemView.findViewById(R.id.notification_text);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            recycler_view = itemView.findViewById(R.id.recycler_view);
            spacer = itemView.findViewById(R.id.spacer);
            city = itemView.findViewById(R.id.city);

        }
    }

    public void addfeed(ArrayList<HomeFriendsModel> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            list.add(feed.get(a));
        }
//        size = list.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }

}
