package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SuggestedBoothsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedBoothsAdapter extends RecyclerView.Adapter<SuggestedBoothsAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<SuggestedBoothsData> suggestedBoothsData;
    public static ArrayList<String> boothsIDs = new ArrayList<>();

    public SuggestedBoothsAdapter(Context context, ArrayList<SuggestedBoothsData> suggestedBoothsData, CustomItemClickListener listener) {
        this.suggestedBoothsData = suggestedBoothsData;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.suggested_booth_item, parent, false);
        final SuggestedBoothsAdapter.MyViewHolder viewHolder = new SuggestedBoothsAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        if (position % 2 == 0){
            holder.layout_one.setVisibility(View.VISIBLE);
            holder.layout_two.setVisibility(View.GONE);
            holder.boothCity1.setText(suggestedBoothsData.get(position).getCityTitle());
            holder.boothName1.setText(suggestedBoothsData.get(position).getBoothName());
            Picasso.get().load(Constants.URL.IMG_URL + suggestedBoothsData.get(position).getCompressedBoothCoverImage()).placeholder(R.drawable.suggested_booth_placeholder).into(holder.coverImg1);
            Picasso.get().load(Constants.URL.IMG_URL + suggestedBoothsData.get(position).getCompressedBoothImage()).placeholder(R.drawable.user).into(holder.boothImg1);

            holder.layout_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       holder.isSelected1.setVisibility(View.VISIBLE);
                       boothsIDs.add(suggestedBoothsData.get(position).getUserID());
                       Log.e("boothID", boothsIDs.toString());
                }
            });
            holder.isSelected1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        holder.isSelected1.setVisibility(View.GONE);
                        boothsIDs.remove(suggestedBoothsData.get(position).getUserID());
                        Log.e("boothID", boothsIDs.toString());
                }
            });

        }else {
            holder.layout_one.setVisibility(View.GONE);
            holder.layout_two.setVisibility(View.VISIBLE);

            holder.boothCity2.setText(suggestedBoothsData.get(position).getCityTitle());
            holder.boothName2.setText(suggestedBoothsData.get(position).getBoothName());
            Picasso.get().load(Constants.URL.IMG_URL + suggestedBoothsData.get(position).getCompressedBoothCoverImage()).placeholder(R.drawable.suggested_booth_placeholder).into(holder.coverImg2);
            Picasso.get().load(Constants.URL.IMG_URL + suggestedBoothsData.get(position).getCompressedBoothImage()).placeholder(R.drawable.user).into(holder.boothImg2);

            holder.layout_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.isSelected2.setVisibility(View.VISIBLE);
                    boothsIDs.add(suggestedBoothsData.get(position).getUserID());
                    Log.e("boothID", boothsIDs.toString());
                }
            });
            holder.isSelected2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.isSelected2.setVisibility(View.GONE);
                    boothsIDs.remove(suggestedBoothsData.get(position).getUserID());
                    Log.e("boothID", boothsIDs.toString());
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return suggestedBoothsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout_one, layout_two, isSelected1, isSelected2;
        TextView boothName1, boothCity1, boothName2, boothCity2;
        ImageView coverImg1, coverImg2;
        CircleImageView boothImg1, boothImg2;

        MyViewHolder(View itemView) {
            super(itemView);

            layout_one = itemView.findViewById(R.id.layout_one);
            layout_two = itemView.findViewById(R.id.layout_two);
            isSelected1 = itemView.findViewById(R.id.isSelected1);
            isSelected2 = itemView.findViewById(R.id.isSelected2);
            boothName1 = itemView.findViewById(R.id.boothName1);
            boothCity1 = itemView.findViewById(R.id.boothCity1);
            boothName2 = itemView.findViewById(R.id.boothName2);
            boothCity2 = itemView.findViewById(R.id.boothCity2);
            coverImg1 = itemView.findViewById(R.id.coverImg1);
            coverImg2 = itemView.findViewById(R.id.coverImg2);
            boothImg1 = itemView.findViewById(R.id.boothImg1);
            boothImg2 = itemView.findViewById(R.id.boothImg2);

        }
    }
}