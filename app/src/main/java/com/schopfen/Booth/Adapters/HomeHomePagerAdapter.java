package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class HomeHomePagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<ProductImagesData> productImagesData;
    private LayoutInflater layoutInflater;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    ArrayList<String> imageViewerArray = new ArrayList<>();
    CircleIndicatorPager indicatorPager;
    String activity;

    private String videoURL = "";
    private String videoThumbnail = "";

    public HomeHomePagerAdapter(String Activity, Context context, ArrayList<ProductImagesData> productImagesData, String videoURL, String videothumbnail) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.productImagesData = productImagesData;
        this.videoURL = videoURL;
        this.videoThumbnail = videothumbnail;
        this.activity = Activity;
    }

    public HomeHomePagerAdapter(String Activity, Context context, ArrayList<ProductImagesData> productImagesData) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        this.productImagesData = productImagesData;
        this.activity = Activity;
    }

    @Override
    public int getCount() {
        if (videoURL.isEmpty()) {
            return productImagesData.size();
        } else {
            return productImagesData.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {

        Log.e("Container", String.valueOf(container.getTag()));
        Fresco.initialize(context);

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        View view = this.layoutInflater.inflate(R.layout.home_pager_item, container, false);
        ImageView img = view.findViewById(R.id.img);
        ImageView video_player = view.findViewById(R.id.video_player);
        ImageView video_play_btn = view.findViewById(R.id.play_video_btn);
        RelativeLayout videoViewLayout = view.findViewById(R.id.videoViewLayout);
        RelativeLayout image_layout = view.findViewById(R.id.image_layout);
        if (videoURL.isEmpty()) {
            videoViewLayout.setVisibility(View.GONE);
            image_layout.setVisibility(View.VISIBLE);

        } else {
            if (position == productImagesData.size() - 1) {
                image_layout.setVisibility(View.GONE);
                videoViewLayout.setVisibility(View.VISIBLE);
                Picasso.get().load(Constants.URL.IMG_URL + videoThumbnail).into(video_player);
                video_play_btn.setVisibility(View.VISIBLE);
            }
        }
//        }else {
//            img.setVisibility(View.VISIBLE);
//        }

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;

//        video_player.setMinimumWidth(width);

        if (!activity.equals("Explore")) {
            video_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(Constants.URL.IMG_URL + videoURL), "video/*");
                    context.startActivity(Intent.createChooser(intent, "Complete action using"));
                }
            });

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!String.valueOf(img.getTag()).equals("null")) {
                        Log.e("ImgTag", String.valueOf(img.getTag()));
                        Intent intent = new Intent(context, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", AdapterMainActivity.products.get(Integer.parseInt(String.valueOf(img.getTag()))).getProductID()).apply();
                        context.startActivity(intent);
                    } else {
                        new ImageViewer.Builder(context, imageViewerArray)
                                .setStartPosition(0)
                                .show();
                    }
                }
            });

        } else {
            video_player.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoginDialog();
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginDialog();
                }
            });
        }


        if (position != productImagesData.size()) {
            img.setTag(String.valueOf(container.getTag()));
            Picasso.get().load(Constants.URL.IMG_URL + productImagesData.get(position).getProductCompressedImage()).into(img);
        }
        imageViewerArray.clear();
        for (int i = 0; i < productImagesData.size(); i++) {
            imageViewerArray.add("https://baac.booth-in.com/" + productImagesData.get(i).getProductCompressedImage());
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