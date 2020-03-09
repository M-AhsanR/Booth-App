package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.Models.TransactionHistoryData;
import com.schopfen.Booth.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    private Context context;
    TransactionHistoryAdapter.CustomItemClickListener listener;
    ArrayList<TransactionHistoryData> commentsArray;


    public TransactionHistoryAdapter(Context context, ArrayList<TransactionHistoryData> commentsArray, TransactionHistoryAdapter.CustomItemClickListener listener) {
        this.commentsArray = commentsArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public TransactionHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.transactionhistoryadapter, parent, false);
        final TransactionHistoryAdapter.MyViewHolder viewHolder = new TransactionHistoryAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TransactionHistoryAdapter.MyViewHolder holder, int position) {

        String time = BaseClass.TimeStampToTime(commentsArray.get(position).getCreatedAt());
        String date = BaseClass.TimeStampToDate(commentsArray.get(position).getCreatedAt());

        holder.points_time.setText(time);

       if (commentsArray.get(position).getTransactionType().equals("Received")){
           holder.points_type.setTextColor(context.getResources().getColor(R.color.transactiongreen));
           holder.points_order_number.setVisibility(View.GONE);
           holder.points_type.setText("Received Points: "+ commentsArray.get(position).getPoints()+" Points");
           holder.points_date.setText("Points Date: "+ date);
       }else if (commentsArray.get(position).getTransactionType().equals("Used")){
           holder.points_type.setTextColor(context.getResources().getColor(R.color.transactionred));
           holder.points_order_number.setVisibility(View.VISIBLE);
           holder.points_type.setText("Used Points: "+ commentsArray.get(position).getPoints()+" Points");
           holder.points_date.setText("Order Date: "+ date);
           holder.points_order_number.setText("Order No: "+commentsArray.get(position).getOrderNumber());
       }

    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView points_type,points_time,points_date, points_order_number;

        MyViewHolder(View itemView) {
            super(itemView);
            points_type = itemView.findViewById(R.id.points_type);
            points_time = itemView.findViewById(R.id.points_time);
            points_date = itemView.findViewById(R.id.points_date);
            points_order_number = itemView.findViewById(R.id.points_order_no);

        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }


}
