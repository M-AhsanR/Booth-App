package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SelectedCategories;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<SubCategoriesData> subCategoriesData;
    public static String checkbox_checking = "";
    ArrayList<String> selection_checking = new ArrayList<>();
    ArrayList<SelectedCategories> selectedCategories;
    String activity;

    public SubCategoriesAdapter(String Activity, Context context, ArrayList<SubCategoriesData> subCategoriesData, ArrayList<SelectedCategories> SelectedCategories, CustomItemClickListener listener) {
        this.subCategoriesData = subCategoriesData;
        this.context = context;
        this.listener = listener;
        this.selectedCategories = SelectedCategories;
        this.activity = Activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.subcategories_recyc_item, parent, false);
        final SubCategoriesAdapter.MyViewHolder viewHolder = new SubCategoriesAdapter.MyViewHolder(view);

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
        holder.textView.setText(subCategoriesData.get(position).getTitle());
        Log.e("subCategoriesSize", subCategoriesData.size() + " ");
        Log.e("selectedItemsId", CategoriesListActivity.selectedItemsId.size() + " ");

        if (activity.equals("Profile")) {
            for (int i = 0; i < selectedCategories.size(); i++) {
                if (selectedCategories.get(i).getCategoryID().equals(subCategoriesData.get(position).getCategoryID())) {
                    holder.checkBox.setChecked(true);
//                CategoriesListActivity.selectedItemsId.add(subCategoriesData.get(position).getCategoryID());
                    selection_checking.add("abc");
//                checkbox_checking = "checked";
                    Log.e("Selecteditemscount", String.valueOf(CategoriesListActivity.selectedItemsId.size()));
                }
            }
        }

        holder.linear_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(true);
                    CategoriesListActivity.selectedItemsId.add(subCategoriesData.get(position).getCategoryID());
                    selection_checking.add("abc");
                    checkbox_checking = "checked";
                    Log.e("Selecteditemscount", String.valueOf(CategoriesListActivity.selectedItemsId.size()));
                    selectedCategories.add(new SelectedCategories(subCategoriesData.get(position).getCategoryID(), "", "", "", ""));

                } else if (holder.checkBox.isChecked()) {
                    holder.checkBox.setChecked(false);
                    CategoriesListActivity.selectedItemsId.remove(subCategoriesData.get(position).getCategoryID());
                    if (selection_checking.size() > 0) {
                        selection_checking.remove(selection_checking.size() - 1);
                    }
                    if (!selectedCategories.isEmpty()) {
                        for (int i = 0; i < selectedCategories.size(); i++) {
                            if (subCategoriesData.get(position).getCategoryID().equals(selectedCategories.get(i).getCategoryID())) {
                                selectedCategories.remove(i);
                            }
                        }
                    }
                }
                if (selection_checking.isEmpty()) {
                    checkbox_checking = "unchecked";
                }
            }
        });

//        if (activity.equals("SignUp")) {
            for (int j = 0; j < CategoriesListActivity.selectedItemsId.size(); j++) {
                if (subCategoriesData.get(position).getCategoryID().equals(CategoriesListActivity.selectedItemsId.get(j))) {
                    holder.checkBox.setChecked(true);
                    selection_checking.add("abc");
                }
            }
//        }

    }

    @Override
    public int getItemCount() {
        return subCategoriesData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        TextView textView;
        LinearLayout linear_days;

        MyViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.sub_cat_checkbox);
            textView = itemView.findViewById(R.id.sub_cat_name);
            linear_days = itemView.findViewById(R.id.linear_days);
        }
    }
}