package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Models.AccountsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchPeopleAdapter extends RecyclerView.Adapter<SearchPeopleAdapter.MyViewHolder> {

    Context context;
    ArrayList<AccountsData> arrayList;
    private ItemClick itemClick;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public SearchPeopleAdapter(Context context, ArrayList<AccountsData> arrayList, ItemClick itemClick) {
        this.context = context;
        this.arrayList = arrayList;
        this.itemClick = itemClick;
    }

    @NonNull
    @Override
    public SearchPeopleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.peoplesearchadapter, parent, false);

        final SearchPeopleAdapter.MyViewHolder viewHolder = new SearchPeopleAdapter.MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClick.onItemClick(view, viewHolder.getPosition());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchPeopleAdapter.MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Picasso.get().load(Constants.URL.IMG_URL + arrayList.get(position).getCompressedImage()).placeholder(R.drawable.user).into(holder.profile_pic);
        holder.boothname.setText("@"+arrayList.get(position).getUserName());
      //  holder.boothusername.setText(arrayList.get(position).getBoothUserName());
//           holder.categories.setText(arrayList.get(position));
        holder.city.setText(arrayList.get(position).getCityTitle());

        holder.main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OthersProfileActivity.class);
                mEditor.putString("Booth", "Other").apply();
                intent.putExtra("OtherUserID", arrayList.get(position).getUserID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView more_btn;
        CircleImageView profile_pic;
        TextView boothname,boothusername, city;
        LinearLayout main_layout;

        MyViewHolder(View itemView) {
            super(itemView);

            more_btn = itemView.findViewById(R.id.more_btn);
            profile_pic = itemView.findViewById(R.id.profile_img);
            boothname = itemView.findViewById(R.id.username);
            city = itemView.findViewById(R.id.city);
            main_layout = itemView.findViewById(R.id.main_layout);
        }
    }
    public interface ItemClick {
        void onItemClick(View v, int position);
    }
}