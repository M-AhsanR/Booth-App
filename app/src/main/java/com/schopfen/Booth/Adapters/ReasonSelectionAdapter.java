package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Models.CancelationReasonModel;
import com.schopfen.Booth.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReasonSelectionAdapter extends RecyclerView.Adapter<ReasonSelectionAdapter.MyViewHolder> {

    Context context;
    ArrayList<CancelationReasonModel> list;
    ReasonSelectionAdapter.CustomItemClickListener listener;
    ArrayList<String> IDlist = new ArrayList<>();

    public ReasonSelectionAdapter(Context context, ArrayList<CancelationReasonModel> list, ReasonSelectionAdapter.CustomItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.reason_selection_adapter, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.reason_text.setText(list.get(position).getCancellationReasonEn());

//        holder.reason_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!holder.checkBox.isChecked()) {
//                    if (!IDlist.isEmpty()) {
//                        for (int i = 0; i < IDlist.size(); i++) {
//                            if (list.get(position).getOrderCancellationReasonID().equals(IDlist.get(i))) {
//                                holder.checkBox.setChecked(false);
//                            }
//                        }
//                        holder.checkBox.setChecked(true);
//                        IDlist.add(list.get(position).getOrderCancellationReasonID());
//                    } else {
//                        IDlist.add(list.get(position).getOrderCancellationReasonID());
//                    }
//                } else {
//                    holder.checkBox.setChecked(false);
//                    for (int i = 0; i < IDlist.size(); i++) {
//                        if (list.get(position).getOrderCancellationReasonID().equals(IDlist.get(i))) {
//                            IDlist.remove(i);
//                        }
//                    }
//                }
//            }
//        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView reason_text;
        LinearLayout reason_layout;

        MyViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
            reason_text = itemView.findViewById(R.id.reason_text);
            reason_layout = itemView.findViewById(R.id.reason_layout);

        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
