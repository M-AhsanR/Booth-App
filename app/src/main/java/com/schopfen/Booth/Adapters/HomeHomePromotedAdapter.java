package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Adapters.AdapterMainActivity;
import com.schopfen.Booth.Adapters.AdapterMainActivityHomeBooth;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.PromotedProductsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeHomePromotedAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<PromotedProductsData> homeCateData;
    private LayoutInflater layoutInflater;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    CircleIndicatorPager indicatorPager;
    ImageView promoted_images;
    Button readmore_btn;
    TextView promoted_text, promoted_text_secondry;
    String activity;

    public HomeHomePromotedAdapter(String Activity, Context context, ArrayList<PromotedProductsData> homeCateData) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.homeCateData = homeCateData;
        this.activity = Activity;
    }

    @Override
    public int getCount() {
        return homeCateData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        View view = this.layoutInflater.inflate(R.layout.cat_viewpager_item, container, false);

        indicatorPager = view.findViewById(R.id.indicator);
        promoted_images = view.findViewById(R.id.promoted_images);
        readmore_btn = view.findViewById(R.id.readmore_btn);
        promoted_text = view.findViewById(R.id.promoted_text);
        promoted_text_secondry = view.findViewById(R.id.promoted_text_secondry);

        indicatorPager.setViewPager(AdapterMainActivity.HeaderViewHolder.viewPager);


        if (homeCateData.size() == 1) {
            indicatorPager.setVisibility(View.GONE);
        }
        if (homeCateData.get(position).getPromotionType().equals("0")){
            readmore_btn.setVisibility(View.INVISIBLE);
        }else {
            readmore_btn.setVisibility(View.VISIBLE);
        }

        promoted_text.setText(homeCateData.get(position).getPromotionDescription());
        promoted_text_secondry.setText(homeCateData.get(position).getBoothName());
        Picasso.get().load(Constants.URL.IMG_URL + homeCateData.get(position).getPromotionImage())
                .placeholder(R.color.grey5).into(promoted_images);

        if (!activity.equals("Explore")) {
            readmore_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (homeCateData.get(position).getPromotionType().equals("1")){
                        Intent intent = new Intent(context, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", homeCateData.get(position).getProductID()).apply();
                        context.startActivity(intent);
                    }else if (homeCateData.get(position).getPromotionType().equals("2")){
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeCateData.get(position).getPromotionUrl()));
                        context.startActivity(browserIntent);
                    }

                }
            });
        } else {
            readmore_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginDialog();
                }
            });
        }

        container.addView(view);
        return view;
    }

    private void LoginDialog() {
        final Dialog verifyDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.login_first_dialog);

        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);

        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDialog.dismiss();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                ((Activity) context).finishAffinity();
                verifyDialog.dismiss();
            }
        });
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}