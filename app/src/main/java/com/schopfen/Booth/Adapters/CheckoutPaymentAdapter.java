package com.schopfen.Booth.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CheckoutPaymentAdapter extends RecyclerView.Adapter<CheckoutPaymentAdapter.MyViewHolder> {

    private List<CheckOutForAddressRVModel> checkOutForAddressRVModels;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<CheckOutForAddressRVModel> arraylistadapter;
    List<TextView> list = new ArrayList<>();
    int selected_position;

    public CheckoutPaymentAdapter(Context context, ArrayList<CheckOutForAddressRVModel> arraylistadapter, CustomItemClickListener listener) {
        this.checkOutForAddressRVModels = arraylistadapter;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.checkout_payment_items, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int position) {

        if (position != checkOutForAddressRVModels.size()) {
            CheckOutForAddressRVModel lisadapter = checkOutForAddressRVModels.get(position);
            holder.add_payment.setVisibility(View.GONE);
            holder.payment_linear.setVisibility(View.VISIBLE);
            holder.payment_linear.setBackgroundResource(R.drawable.custom_dialog_background);
        }
        if (position == checkOutForAddressRVModels.size()) {
            holder.add_payment.setVisibility(View.VISIBLE);
            holder.payment_linear.setVisibility(View.GONE);
        }
        holder.payment_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_position = position;
                notifyDataSetChanged();
            }
        });
        if (selected_position == position) {
            holder.payment_linear.setBackgroundResource(R.drawable.co_address_selected_background);
        }
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        int width = displayMetrics.widthPixels / 3;
//        holder.payment_linear.setMinimumWidth(width);
//        holder.add_payment.setMinimumWidth(width);

        holder.add_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AddAddressDialog();
            }
        });

    }

//    private void AddAddressDialog() {
//        final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//        dialog.setContentView(R.layout.add_address_dialog_coa);
//        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
//        Button createBtn = dialog.findViewById(R.id.btn_create);
//        final EditText name = dialog.findViewById(R.id.et_recepientname_coa);
//        final EditText email = dialog.findViewById(R.id.et_email_coa);
//
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.show();
//
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        createBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                CheckOutForAddressRVModel addressmodel1 = new CheckOutForAddressRVModel();
//                addressmodel1.setAddress(name.getText().toString());
//                addressmodel1.setDetails(email.getText().toString());
//                checkOutForAddressRVModels.add(addressmodel1);
//                notifyDataSetChanged();
//                dialog.dismiss();
//                final Dialog subdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//                subdialog.setContentView(R.layout.added_address_dialog);
//                Button closeBtn = subdialog.findViewById(R.id.btn_close);
//
//                subdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                subdialog.show();
//
//                closeBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        subdialog.dismiss();
//                    }
//                });
//            }
//        });
//
//    }

    @Override
    public int getItemCount() {
        return checkOutForAddressRVModels.size() + 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout payment_linear, add_payment;
        int p;

        MyViewHolder(View itemView) {
            super(itemView);
            payment_linear = itemView.findViewById(R.id.payment_linear);
            add_payment = itemView.findViewById(R.id.add_paymentmethod);
        }
    }
}
