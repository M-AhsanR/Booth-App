package com.schopfen.Booth.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.SelectedCategories;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MainCatData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.MyViewHolder> {

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    private Context context;
    CustomItemClickListener listener;
    ArrayList<MainCategoriesData> mainCategoriesData;
    ArrayList<SubCategoriesData> compairing_list = new ArrayList<>();
    String activity;

    ArrayList<SelectedCategories> userSelectedCategories = new ArrayList<>();
    ArrayList<SelectedCategories> boothSelectedCategories = new ArrayList<>();

    public CategoriesListAdapter(String Activity, Context context, ArrayList<MainCategoriesData> mainCategoriesData, ArrayList<SelectedCategories> UserSelectedCategories, ArrayList<SelectedCategories> BoothSelectedCateg, CustomItemClickListener listener) {
        this.mainCategoriesData = mainCategoriesData;
        this.context = context;
        this.listener = listener;
        this.userSelectedCategories = UserSelectedCategories;
        this.boothSelectedCategories = BoothSelectedCateg;
        this.activity = Activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.adapter_categories_list, parent, false);
        final CategoriesListAdapter.MyViewHolder viewHolder = new CategoriesListAdapter.MyViewHolder(view);
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

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
        holder.category_name.setText(mainCategoriesData.get(position).getTitle());
        holder.catsub_item.setText(mainCategoriesData.get(position).getSubCategoriesData().size() + " " + context.getResources().getString(R.string.sub_items));
        Glide.with(context).load("https://baac.booth-in.com/" + mainCategoriesData.get(position).getImage()).apply(RequestOptions.centerCropTransform().placeholder(R.drawable.white_circle)).into(holder.category_img);


        if (sharedpreferences.getString("LastState", "").equals("user")){
            if (!userSelectedCategories.isEmpty()) {
                compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                OuterLoop:
                for (int i = 0; i < compairing_list.size(); i++) {
                    if (userSelectedCategories.size() > 0) {
                        for (int j = 0; j < userSelectedCategories.size(); j++) {
                            if (compairing_list.get(i).getCategoryID().equals(userSelectedCategories.get(j).getCategoryID())) {
                                holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                break OuterLoop;
                            } else {
                                holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                            }
//                        SubCategoriesAdapter.selectedItemsId.add(userSelectedCategories.get(i).getCategoryID());
                        }
                    } else {
                        holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }
                for (int a = 0; a < mainCategoriesData.get(position).getSubCategoriesData().size(); a++){
                    for (int b = 0; b < userSelectedCategories.size(); b++){
                        if (userSelectedCategories.get(b).getCategoryID().equals(mainCategoriesData.get(position).getSubCategoriesData().get(a).getCategoryID())){
                            CategoriesListActivity.selectedMainCategoriesList.add("abc");
                            break;
                        }
                    }
                }
            }
        }else if (sharedpreferences.getString("LastState", "").equals("booth")){
            if (!boothSelectedCategories.isEmpty()) {
                compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                OuterLoop:
                for (int i = 0; i < compairing_list.size(); i++) {
                    if (boothSelectedCategories.size() > 0) {
                        for (int j = 0; j < boothSelectedCategories.size(); j++) {
                            if (compairing_list.get(i).getCategoryID().equals(boothSelectedCategories.get(j).getCategoryID())) {
                                holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                break OuterLoop;
                            } else {
                                holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                            }
                        }
                    } else {
                        holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                    }
//                    SubCategoriesAdapter.selectedItemsId.add(boothSelectedCategories.get(i).getCategoryID());
                }
                for (int a = 0; a < mainCategoriesData.get(position).getSubCategoriesData().size(); a++){
                    for (int b = 0; b < boothSelectedCategories.size(); b++){
                        if (boothSelectedCategories.get(b).getCategoryID().equals(mainCategoriesData.get(position).getSubCategoriesData().get(a).getCategoryID())){
                            CategoriesListActivity.selectedMainCategoriesList.add("abc");
                            break;
                        }
                    }
                }
            }
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedpreferences.getString("LastState", "").equals("user")) {

                    int color = holder.category_name.getCurrentTextColor();
                    if (color == -816115) {
                        final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        dialog.setContentView(R.layout.sub_categories_dialog);
                        dialog.setCancelable(false);
                        RecyclerView dialogRecycler = dialog.findViewById(R.id.recyclerView);
                        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
                        TextView cat_title = dialog.findViewById(R.id.cat_title);
                        TextView cat_count = dialog.findViewById(R.id.cat_count);

                        cat_title.setText(mainCategoriesData.get(position).getTitle());
                        cat_count.setText(mainCategoriesData.get(position).getSubCategoriesData().size() +" "+context.getResources().getString(R.string.subCategories));
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                        dialogRecycler.setLayoutManager(layoutManager);
                        SubCategoriesAdapter subCategoriesAdapter = new SubCategoriesAdapter(activity, context, mainCategoriesData.get(position).getSubCategoriesData(), userSelectedCategories, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
//                                            childModel = childlist.get(position).getSubCategoriesData().get(position).getTitle();
//                                            childArray.add(childModel);
                            }
                        });

                        dialogRecycler.setAdapter(subCategoriesAdapter);
                        subCategoriesAdapter.notifyDataSetChanged();

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        for (int i = 0; i < childArray.size(); i++) {
//                            Log.d("childlist", " " + childArray.get(i));
//                        }
                                dialog.dismiss();
                                compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                                OuterLoop:
                                for (int i = 0; i < compairing_list.size(); i++) {
                                    if (CategoriesListActivity.selectedItemsId.size() > 0) {
                                        for (int j = 0; j < CategoriesListActivity.selectedItemsId.size(); j++) {
                                            if (compairing_list.get(i).getCategoryID().equals(CategoriesListActivity.selectedItemsId.get(j))) {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                                break OuterLoop;
                                            } else {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                            }
                                        }
                                    } else {
                                        holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                    }
                                }

                                if (SubCategoriesAdapter.checkbox_checking.equals("unchecked")) {
                                    if (!CategoriesListActivity.selectedMainCategoriesList.isEmpty()){
                                        CategoriesListActivity.selectedMainCategoriesList.remove(CategoriesListActivity.selectedMainCategoriesList.size() - 1);
                                    }
                                    Log.e("removing", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                    SubCategoriesAdapter.checkbox_checking = "";
                                }
                            }
                        });
                    } else {
                        final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        dialog.setContentView(R.layout.sub_categories_dialog);
                        dialog.setCancelable(false);
                        RecyclerView dialogRecycler = dialog.findViewById(R.id.recyclerView);
                        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
                        TextView cat_title = dialog.findViewById(R.id.cat_title);
                        TextView cat_count = dialog.findViewById(R.id.cat_count);

                        cat_title.setText(mainCategoriesData.get(position).getTitle());
                        cat_count.setText(mainCategoriesData.get(position).getSubCategoriesData().size() +" "+context.getResources().getString(R.string.subCategories));
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                        dialogRecycler.setLayoutManager(layoutManager);
                        SubCategoriesAdapter subCategoriesAdapter = new SubCategoriesAdapter(activity, context, mainCategoriesData.get(position).getSubCategoriesData(), userSelectedCategories, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
//                                            childModel = childlist.get(position).getSubCategoriesData().get(position).getTitle();
//                                            childArray.add(childModel);
                            }
                        });

                        dialogRecycler.setAdapter(subCategoriesAdapter);
                        subCategoriesAdapter.notifyDataSetChanged();

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        for (int i = 0; i < childArray.size(); i++) {
//                            Log.d("childlist", " " + childArray.get(i));
//                        }
                                dialog.dismiss();
                                compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                                OuterLoop:
                                for (int i = 0; i < compairing_list.size(); i++) {
                                    if (CategoriesListActivity.selectedItemsId.size() > 0) {
                                        for (int j = 0; j < CategoriesListActivity.selectedItemsId.size(); j++) {
                                            if (compairing_list.get(i).getCategoryID().equals(CategoriesListActivity.selectedItemsId.get(j))) {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                                break OuterLoop;
                                            } else {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                            }
                                        }
                                    } else {
                                        holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                        CategoriesListActivity.selectedMainCategoriesList.clear();
                                    }
                                }
                                if (!SubCategoriesAdapter.checkbox_checking.isEmpty()) {
                                    Log.e("Checkbox_checking", SubCategoriesAdapter.checkbox_checking);
                                    if (SubCategoriesAdapter.checkbox_checking.equals("unchecked")) {
                                        if (!CategoriesListActivity.selectedMainCategoriesList.isEmpty()){
                                            CategoriesListActivity.selectedMainCategoriesList.remove(CategoriesListActivity.selectedMainCategoriesList.size() - 1);
                                        }
                                        Log.e("removing", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                        SubCategoriesAdapter.checkbox_checking = "";
                                    } else if (SubCategoriesAdapter.checkbox_checking.equals("checked")) {
                                        CategoriesListActivity.selectedMainCategoriesList.add("abc");
                                        Log.e("selectedsize", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                        SubCategoriesAdapter.checkbox_checking = "";
                                    }
                                }
                            }
                        });
                    }
                }
                else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    if (CategoriesListActivity.selectedMainCategoriesList.size() < 3) {
                        final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        dialog.setContentView(R.layout.sub_categories_dialog);
                        dialog.setCancelable(false);
                        TextView cat_title = dialog.findViewById(R.id.cat_title);
                        TextView cat_count = dialog.findViewById(R.id.cat_count);
                        RecyclerView dialogRecycler = dialog.findViewById(R.id.recyclerView);
                        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);

                        cat_title.setText(mainCategoriesData.get(position).getTitle());
                        cat_count.setText(mainCategoriesData.get(position).getSubCategoriesData().size() + " "+context.getResources().getString(R.string.subCategories));
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                        dialogRecycler.setLayoutManager(layoutManager);
                        SubCategoriesAdapter subCategoriesAdapter = new SubCategoriesAdapter(activity, context, mainCategoriesData.get(position).getSubCategoriesData(), boothSelectedCategories, new CustomItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
//                                            childModel = childlist.get(position).getSubCategoriesData().get(position).getTitle();
//                                            childArray.add(childModel);
                            }
                        });

                        dialogRecycler.setAdapter(subCategoriesAdapter);
                        subCategoriesAdapter.notifyDataSetChanged();

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        confirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        for (int i = 0; i < childArray.size(); i++) {
//                            Log.d("childlist", " " + childArray.get(i));
//                        }
                                dialog.dismiss();
                                compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                                OuterLoop:
                                for (int i = 0; i < compairing_list.size(); i++) {
                                    if (CategoriesListActivity.selectedItemsId.size() > 0) {
//                                        CategoriesListActivity.selectedMainCategoriesList.add("abc");
                                        for (int j = 0; j < CategoriesListActivity.selectedItemsId.size(); j++) {
                                            if (compairing_list.get(i).getCategoryID().equals(CategoriesListActivity.selectedItemsId.get(j))) {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                                break OuterLoop;
                                            } else {
                                                holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                            }
                                        }
                                    } else {
                                        holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                        CategoriesListActivity.selectedMainCategoriesList.clear();
                                    }
                                }
                                if (SubCategoriesAdapter.checkbox_checking.equals("unchecked")) {
                                    if (!CategoriesListActivity.selectedMainCategoriesList.isEmpty()) {
                                        CategoriesListActivity.selectedMainCategoriesList.remove(CategoriesListActivity.selectedMainCategoriesList.size() - 1);
                                    }
                                    Log.e("removing", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                    SubCategoriesAdapter.checkbox_checking = "";
                                } else if (SubCategoriesAdapter.checkbox_checking.equals("checked")) {
                                    CategoriesListActivity.selectedMainCategoriesList.add("abc");
                                    Log.e("selectedsize", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                    SubCategoriesAdapter.checkbox_checking = "";
                                }
                            }
                        });
                    } else {
                        int color = holder.category_name.getCurrentTextColor();
                        if (color == -816115) {
                            final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            dialog.setContentView(R.layout.sub_categories_dialog);
                            dialog.setCancelable(false);
                            RecyclerView dialogRecycler = dialog.findViewById(R.id.recyclerView);
                            Button confirmBtn = dialog.findViewById(R.id.confirmBtn);
                            TextView cat_title = dialog.findViewById(R.id.cat_title);
                            TextView cat_count = dialog.findViewById(R.id.cat_count);

                            cat_title.setText(mainCategoriesData.get(position).getTitle());
                            cat_count.setText(mainCategoriesData.get(position).getSubCategoriesData().size() + " "+context.getResources().getString(R.string.subCategories));
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                            dialogRecycler.setLayoutManager(layoutManager);
                            SubCategoriesAdapter subCategoriesAdapter = new SubCategoriesAdapter(activity, context, mainCategoriesData.get(position).getSubCategoriesData(), boothSelectedCategories, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
//                                            childModel = childlist.get(position).getSubCategoriesData().get(position).getTitle();
//                                            childArray.add(childModel);
                                }
                            });

                            dialogRecycler.setAdapter(subCategoriesAdapter);
                            subCategoriesAdapter.notifyDataSetChanged();

                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();

                            confirmBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                        for (int i = 0; i < childArray.size(); i++) {
//                            Log.d("childlist", " " + childArray.get(i));
//                        }
                                    dialog.dismiss();
                                    compairing_list = mainCategoriesData.get(position).getSubCategoriesData();
                                    OuterLoop:
                                    for (int i = 0; i < compairing_list.size(); i++) {
                                        if (CategoriesListActivity.selectedItemsId.size() > 0) {
                                            for (int j = 0; j < CategoriesListActivity.selectedItemsId.size(); j++) {
                                                if (compairing_list.get(i).getCategoryID().equals(CategoriesListActivity.selectedItemsId.get(j))) {
                                                    holder.category_name.setTextColor(context.getResources().getColor(R.color.orange));
                                                    break OuterLoop;
                                                } else {
                                                    holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                                }
                                            }
                                        } else {
                                            holder.category_name.setTextColor(context.getResources().getColor(R.color.white));
                                        }
                                    }
                                    if (SubCategoriesAdapter.checkbox_checking.equals("unchecked")) {
                                        if (!CategoriesListActivity.selectedMainCategoriesList.isEmpty()) {
                                            CategoriesListActivity.selectedMainCategoriesList.remove(CategoriesListActivity.selectedMainCategoriesList.size() - 1);
                                        }
                                        Log.e("removing", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
                                        SubCategoriesAdapter.checkbox_checking = "";
                                    }
//                                    else {
//                                        CategoriesListActivity.selectedMainCategoriesList.add("abc");
//                                        Log.e("selectedsize", String.valueOf(CategoriesListActivity.selectedMainCategoriesList.size()));
//                                    }
                                }
                            });
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.selectmaximumthreeinterests), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        Log.e("Position", String.valueOf(mainCategoriesData.get(position).getSubCategoriesData().size()));


    }

    @Override
    public int getItemCount() {
        return mainCategoriesData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView category_name, catsub_item;
        RelativeLayout relativeLayout;
        RoundedImageView category_img;

        MyViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_cat_cla);
            category_name = itemView.findViewById(R.id.tv_cattitle_cla);
            catsub_item = itemView.findViewById(R.id.tv_catsubitem_cla);
            category_img = itemView.findViewById(R.id.iv_imgcat_cla);
        }
    }
}