package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

public class WeekdaysAdapter extends RecyclerView.Adapter<WeekdaysAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<String> weekdays;
    public static ArrayList<String> selectedDays = new ArrayList<>();

    public WeekdaysAdapter(Context context, ArrayList<String> weekdays, CustomItemClickListener listener) {
        this.weekdays = weekdays;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.subcategories_recyc_item, parent, false);
        final WeekdaysAdapter.MyViewHolder viewHolder = new WeekdaysAdapter.MyViewHolder(view);

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
        holder.textView.setText(weekdays.get(position));
        holder.linear_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("linearday", "clicked");
                if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    selectedDays.remove(weekdays.get(position));
                   // Log.e("selectedDays", selectedDays.toString());
                } else if (!holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    selectedDays.add(weekdays.get(position));
                   // Log.e("selectedDays", selectedDays.toString());
                }
                for (int i = 0; i < selectedDays.size(); i++) {
                    Log.e("selectedday", selectedDays.get(i));
                }
            }
        });
        for (int j = 0; j < selectedDays.size(); j++) {
            if (weekdays.get(position).equals(selectedDays.get(j))) {
                holder.checkBox.setChecked(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return weekdays.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView textView;
        LinearLayout linear_days;

        MyViewHolder(View itemView) {
            super(itemView);
            linear_days = itemView.findViewById(R.id.linear_days);
            checkBox = itemView.findViewById(R.id.sub_cat_checkbox);
            textView = itemView.findViewById(R.id.sub_cat_name);
        }
    }
}