package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.HomeCateDataModel;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;

public class CateViewPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<ProductsData> homeCateData;
    private LayoutInflater layoutInflater;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    CircleIndicatorPager indicatorPager;
    ImageView promoted_images;
    Button readmore_btn;

    public CateViewPagerAdapter(Context context, ArrayList<ProductsData> homeCateData) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.homeCateData = homeCateData;
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

        indicatorPager.setViewPager(AdapterMainActivity.HeaderViewHolder.viewPager);

        if(homeCateData.size()==1){
            indicatorPager.setVisibility(View.GONE);
        }

        Picasso.get().load(Constants.URL.IMG_URL + homeCateData.get(position).getProductImagesData().get(0).getProductCompressedImage())
                .into(promoted_images);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}