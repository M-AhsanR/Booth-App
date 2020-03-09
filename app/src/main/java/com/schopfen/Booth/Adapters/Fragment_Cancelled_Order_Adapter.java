package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.schopfen.Booth.Activities.SummaryOrder;
import com.schopfen.Booth.Activities.UserSummaryOrder;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.OrdersData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

public class Fragment_Cancelled_Order_Adapter extends RecyclerView.Adapter<Fragment_Cancelled_Order_Adapter.MyViewHolder> {

    private Context context;
    ArrayList<OrdersData> list;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";


    public Fragment_Cancelled_Order_Adapter(Context context, ArrayList<OrdersData> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.cancel_order_adapter, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        holder.date.setText(BaseClass.TimeStampToDate(list.get(position).getOrderReceivedAt()));
        holder.orderNumber.setText(list.get(position).getOrderTrackID());

        int totalCurrency = 0;
        for (int i = 0; i < list.get(position).getOrderItems().size(); i++) {
            totalCurrency = totalCurrency + Integer.parseInt(list.get(position).getOrderItems().get(i).getPrice());
        }

        holder.currencyText.setText(String.valueOf(totalCurrency) + " " + list.get(position).getOrderItems().get(0).getCurrency());

            if(list.get(position).getOrderLastStatusID().equals("1")) {
                holder.firstStatus.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_row));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
            }else if (list.get(position).getOrderLastStatusID().equals("2")) {
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.starting_cancel_order));
                holder.secondStatus.setBackground(context.getResources().getDrawable(R.drawable.ending_cancel_order));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
            }else if (list.get(position).getOrderLastStatusID().equals("3")) {
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.payment_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.starting_cancel_order));
                holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.thirdStatus.setBackground(context.getResources().getDrawable(R.drawable.ending_cancel_order));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
            }else if (list.get(position).getOrderLastStatusID().equals("7")){
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.payment_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.delivery_details.setTextColor(context.getResources().getColor(R.color.red));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.starting_cancel_order));
                holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.red));
                holder.orderdispatched.setBackground(context.getResources().getDrawable(R.drawable.ending_cancel_order));
                holder.fourthStatus.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_row));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
                holder.fourthDot.setBackground(context.getResources().getDrawable(R.drawable.order_status_cancel_dot));
        }


        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("LastState", "").equals("user")){
                    Intent intent = new Intent(context, UserSummaryOrder.class);
                    Gson gson = new Gson();
                    String array = gson.toJson(list.get(position).getOrderItems());
                    intent.putExtra("activity", "normal");
                    intent.putExtra("activity2", "Completed");
                    intent.putExtra("array", array);
                    intent.putExtra("username", list.get(position).getFullName());
                    intent.putExtra("address", list.get(position).getApartmentNo() + list.get(position).getAddress1() + list.get(position).getAddress2() + list.get(position).getAddressCity());
                    intent.putExtra("mobile", list.get(position).getAddressMobile());
                    intent.putExtra("email", list.get(position).getAddressEmail());
                    intent.putExtra("longitude", list.get(position).getAddressLongitude());
                    intent.putExtra("latitude", list.get(position).getAddressLatitude());
                    intent.putExtra("boothname", list.get(position).getBoothUserName());
                    intent.putExtra("totalitems", String.valueOf(list.get(position).getOrderItems().size()));
                    intent.putExtra("vatpercentage", list.get(position).getVatPercentage());
                    intent.putExtra("actualdeliverycharges", list.get(position).getActualDeliveryCharges());
                    intent.putExtra("additionaldeliverycharges", list.get(position).getAdditionalDeliveryCharges());
                    intent.putExtra("discount", list.get(position).getDiscount());
                    intent.putExtra("orderRequestID", list.get(position).getOrderRequestID());
                    intent.putExtra("UserID", list.get(position).getBoothID());
                    context.startActivity(intent);
                }else if (sharedpreferences.getString("LastState", "").equals("booth")){
                    float deliveryCharges = 0;
                    for (int i = 0; i < list.get(position).getOrderItems().size(); i++) {
                        float charges = Float.valueOf(list.get(position).getOrderItems().get(i).getDeliveryCharges());
                        deliveryCharges = deliveryCharges + charges;
                    }
                    Intent intent = new Intent(context, SummaryOrder.class);
                    Gson gson = new Gson();
                    String array = gson.toJson(list.get(position).getOrderItems());
                    intent.putExtra("array", array);
                    intent.putExtra("activity", "normal");
                    intent.putExtra("username", list.get(position).getUserName());
                    intent.putExtra("address", list.get(position).getApartmentNo() + list.get(position).getAddress1() + list.get(position).getAddress2() + list.get(position).getAddressCity());
                    intent.putExtra("mobile", list.get(position).getAddressMobile());
                    intent.putExtra("email", list.get(position).getAddressEmail());
                    if (list.get(position).getAddressLatitude() != null && !list.get(position).getAddressLatitude().equals("")) {
                        intent.putExtra("longitude", list.get(position).getAddressLongitude());
                        intent.putExtra("latitude", list.get(position).getAddressLatitude());
                    } else {
                        intent.putExtra("longitude", "0.0");
                        intent.putExtra("latitude", "0.0");
                    }
                    intent.putExtra("boothname", list.get(position).getBoothUserName());
                    intent.putExtra("totalitems", String.valueOf(list.get(position).getOrderItems().size()));
                    intent.putExtra("vatpercentage", list.get(position).getVatPercentage());
                    intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                    intent.putExtra("discount", list.get(position).getDiscount());
                    intent.putExtra("orderRequestID", list.get(position).getOrderRequestID());
                    intent.putExtra("UserID", list.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView more_menu;
        TextView date, orderNumber, currencyText, approval_details, payment_details, delivery_details;
        View approved_pendingpayment, approved_paymentdone, orderdispatched, firstDot, secondDot, thirdDot, fourthDot;
        RelativeLayout firstStatus, secondStatus, thirdStatus, fourthStatus;
        RelativeLayout mainlayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            more_menu = itemView.findViewById(R.id.more_menu);
            date = itemView.findViewById(R.id.dateTextView);
            orderNumber = itemView.findViewById(R.id.orderNumberTextView);
            currencyText = itemView.findViewById(R.id.currencyTextView);
            approved_pendingpayment = itemView.findViewById(R.id.approved_pendingpayment);
            approved_paymentdone = itemView.findViewById(R.id.approved_paymentdone);
            orderdispatched = itemView.findViewById(R.id.orderdispatched);
            firstStatus = itemView.findViewById(R.id.orderfirststatus);
            firstDot = itemView.findViewById(R.id.firstDot);
            secondDot = itemView.findViewById(R.id.secondDot);
            thirdDot = itemView.findViewById(R.id.thirdDot);
            fourthDot = itemView.findViewById(R.id.fourthDot);
            secondStatus = itemView.findViewById(R.id.orderSecondStatus);
            thirdStatus = itemView.findViewById(R.id.orderThirdStatus);
            fourthStatus = itemView.findViewById(R.id.orderFourthStatus);
            approval_details = itemView.findViewById(R.id.approval_details);
            payment_details = itemView.findViewById(R.id.payment_details);
            delivery_details = itemView.findViewById(R.id.delivery_details);
            mainlayout = itemView.findViewById(R.id.mainlayout);
        }
    }
}
