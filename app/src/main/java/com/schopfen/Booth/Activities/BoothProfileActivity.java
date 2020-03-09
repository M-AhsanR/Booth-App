package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.tabs.TabLayout;
import com.schopfen.Booth.Adapters.BoothProfileSectionPagerAdapter;
import com.schopfen.Booth.Adapters.BoothProfile_Product_Adapter;
import com.schopfen.Booth.Adapters.MyPromoCodeAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Fragments.BoothProfile_Products_Fragment;
import com.schopfen.Booth.Fragments.BoothProfile_Promotions_Fragment;
import com.schopfen.Booth.Fragments.Booth_Home_Fragment;
import com.schopfen.Booth.Fragments.Booth_Profile_Fragment;
import com.schopfen.Booth.Fragments.Home_Booth_Fragment;
import com.schopfen.Booth.Models.BoothProfile_Product_Data;
import com.schopfen.Booth.Models.MyPromoModel;
import com.schopfen.Booth.Models.ProductImagesModel;
import com.schopfen.Booth.NonSwipeableViewPager;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BoothProfileActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    RecyclerView productsRecycler;
    CircleImageView boothImg;
    ArrayList<String> productsData = new ArrayList<>();
    NestedScrollView nestedScrollView;
    ImageView cover_image;
    TextView user_name;
    ImageView top_head,bottom_head, promoListButton;
    TextView booth_name,booth_address, separator1, separator2;
    TextView booth_about_count,booth_about;
    TextView booth_rating_count,booth_rating;
    TextView booth_following_count,booth_following, boothFollowersCount, boothCategoriesCount;
    String booth_title,booth_username,booth_addres,booth_description;
    ArrayList<String> profileArray = new ArrayList<>();
    ArrayList<String> coverarray = new ArrayList<>();
    TextView btn_about,btn_message,follow_btn;
    public static String otherID;
    String OthersUserresponseID, BoothUserName, longitude, latitude, BoothAddress;
    ImageView maroof_verified;
    private static final String TAG = "RouteScheduleActivity";
    private BoothProfileSectionPagerAdapter sectionsPageAdapter;
    private NonSwipeableViewPager viewPager;
    TabLayout tabLayout;
    String themeColor;
    String otherUserID;
    LinearLayout linear_followers, linear_ratting, linear_categories, header_umbrella_layout;
    ImageView more_header;
    ColorStateList iconsColorStates;
    String Following, Currency;
    BoothProfile_Product_Adapter adapter;
    BoothProfile_Product_Data data;
    ArrayList<BoothProfile_Product_Data> list = new ArrayList<>();
    ArrayList<ProductImagesModel> imagesList;
    RecyclerView recyclerView;
    BoothProfile_Product_Data boothProfile_product_data;

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isNetworkAvailable()){
            setContentView(R.layout.activity_booth_profile);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            otherUserID = getIntent().getStringExtra("OtherUserID");
            Log.e("OtherIDviaIntent", otherUserID);
            mEditor.putString("PromotedPath", "Activity").apply();
            otherID = otherUserID;
            Fresco.initialize(BoothProfileActivity.this);
            initializeViews();
            getBoothDetails();
            ProductApi();
            if (otherUserID.equals(sharedpreferences.getString("UserID", " "))){
                more_header.setVisibility(View.GONE);
                follow_btn.setVisibility(View.GONE);
            }else {
                more_header.setVisibility(View.VISIBLE);
                follow_btn.setVisibility(View.VISIBLE);
            }
            sectionsPageAdapter = new BoothProfileSectionPagerAdapter(getSupportFragmentManager());
            viewPager = findViewById(R.id.contain);
            setupViewPager(viewPager);
            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
            follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Following.equals("1")){
                        UnfollowOtherBoothApi();
                        Home_Booth_Fragment.refresh = true;
                    }else {
                        FollowOtherBoothApi();
                        Home_Booth_Fragment.refresh = true;
                    }
                }
            });
            promoListButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PromoCodesListDialog();
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void PromoCodesListDialog() {
        final Dialog dialog = new Dialog(BoothProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.promo_list_dialog);
        Button yes = dialog.findViewById(R.id.btn_yes);
        RecyclerView promo_list = dialog.findViewById(R.id.promo_list);
        ProgressBar progressbar = dialog.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);
        promo_list.setVisibility(View.GONE);
        yes.setBackgroundDrawable(new Booth_Profile_Fragment.DrawableGradient(new int[]{Color.parseColor(themeColor),
                Color.parseColor(themeColor), Color.parseColor(themeColor)}, 50));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OtherUserID", otherUserID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getPromoCodes, BoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("MyPromos", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            ArrayList<MyPromoModel> myPromoModelArrayList = new ArrayList<>();
                            JSONArray coupons = jsonObject.getJSONArray("coupons");
                            for (int i = 0; i < coupons.length(); i++){
                                JSONObject jsonObject1 = coupons.getJSONObject(i);
                                String CouponID = jsonObject1.getString("CouponID");
                                String UserID = jsonObject1.getString("UserID");
                                String CouponCode = jsonObject1.getString("CouponCode");
                                String DiscountType = jsonObject1.getString("DiscountType");
                                String DiscountFactor = jsonObject1.getString("DiscountFactor");
                                String ExpiryDate = jsonObject1.getString("ExpiryDate");
                                String UsageCount = jsonObject1.getString("UsageCount");
                                String TotalCount = jsonObject1.getString("TotalCount");
                                String IsActive = jsonObject1.getString("IsActive");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String CouponTextID = jsonObject1.getString("CouponTextID");
                                String Title = jsonObject1.getString("Title");
                                String SystemLanguageID = jsonObject1.getString("SystemLanguageID");
                                myPromoModelArrayList.add(new MyPromoModel(CouponID, UserID, CouponCode, DiscountType, DiscountFactor, ExpiryDate, UsageCount,
                                        TotalCount, IsActive, CreatedAt, CouponTextID, Title, SystemLanguageID));
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BoothProfileActivity.this);
                            promo_list.setLayoutManager(linearLayoutManager);
                            MyPromoCodeAdapter myPromoCodeAdapter = new MyPromoCodeAdapter(themeColor, Currency , "MyProfile", BoothProfileActivity.this, myPromoModelArrayList, new MyPromoCodeAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                }
                            });
                            promo_list.setAdapter(myPromoCodeAdapter);
                            progressbar.setVisibility(View.GONE);
                            promo_list.setVisibility(View.VISIBLE);
                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                            promo_list.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                        promo_list.setVisibility(View.VISIBLE);
                    }
                }else {
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    promo_list.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        sectionsPageAdapter = new BoothProfileSectionPagerAdapter(getSupportFragmentManager());
        sectionsPageAdapter.addFragment(new BoothProfile_Products_Fragment(),getResources().getString(R.string.products));
        sectionsPageAdapter.addFragment(new BoothProfile_Promotions_Fragment(),getResources().getString(R.string.promotions));
        viewPager.setAdapter(sectionsPageAdapter);
    }

    private void ProductApi(){
        Map<String, String> postparams = new HashMap<String, String>();
        postparams.put("UserID", sharedpreferences.getString("UserID", " "));
        postparams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        if(sharedpreferences.getString("Booth", "").equals("Other")){
            postparams.put("OtherUserID", getIntent().getStringExtra("OtherUserID"));
        }
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.products, BoothProfileActivity.this, postparams, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("productResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            list.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++){
                                JSONObject product_items = products.getJSONObject(i);
                                boothProfile_product_data = new BoothProfile_Product_Data();
                                String ProductID = product_items.getString("ProductID");
                                String title = product_items.getString("Title");
                                JSONArray product_images = product_items.getJSONArray("ProductImages");
                                imagesList = new ArrayList<>();
                                for (int j = 0; j < product_images.length(); j++){
                                    JSONObject image_items = product_images.getJSONObject(j);
                                    ProductImagesModel productImagesModel = new ProductImagesModel();
                                    String image = image_items.getString("ProductImage");
                                    String compressedImage = image_items.getString("ProductCompressedImage");
                                    productImagesModel.setImage(image);
                                    productImagesModel.setCompressedImage(compressedImage);
                                    imagesList.add(productImagesModel);
                                }
                                boothProfile_product_data.setProduct_id(ProductID);
                                boothProfile_product_data.setProduct_name(title);
                                boothProfile_product_data.setProduct_image(imagesList);
                                list.add(boothProfile_product_data);
                            }
                            recyclerView.setLayoutManager(new GridLayoutManager(BoothProfileActivity.this,3));
                            adapter = new BoothProfile_Product_Adapter(BoothProfileActivity.this, list, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
//                                    Toast.makeText(getActivity(), "Item "+position, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(BoothProfileActivity.this, ProductDetailsActivity.class);
                                    mEditor.putString("ProductID", list.get(position).getProduct_id()).commit();
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(BoothProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void initializeViews() {
        productsRecycler = findViewById(R.id.recyclerView);
        recyclerView = findViewById(R.id.recyclerView);
        boothImg = findViewById(R.id.boothImg);
        nestedScrollView = findViewById(R.id.nestedsv);
        top_head = findViewById(R.id.top_head);
        bottom_head =  findViewById(R.id.bottom_head);
        promoListButton =  findViewById(R.id.promoListButton);
        booth_name = findViewById(R.id.booth_name);
        booth_address = findViewById(R.id.booth_address);
        separator1 = findViewById(R.id.separator1);
        separator2 = findViewById(R.id.separator2);
        boothCategoriesCount = findViewById(R.id.boothCategoriesCount);
        booth_about = findViewById(R.id.booth_about);
        booth_rating_count = findViewById(R.id.booth_rating_count);
        booth_rating = findViewById(R.id.booth_rating);
        boothFollowersCount = findViewById(R.id.boothFollowersCount);
        booth_following = findViewById(R.id.booth_following);
        btn_about = findViewById(R.id.btn_about);
        btn_message = findViewById(R.id.btn_message);
        user_name = findViewById(R.id.user_name);
        cover_image = findViewById(R.id.cover_img);
        follow_btn = findViewById(R.id.btn_follow);
        maroof_verified = findViewById(R.id.maroof_verified);
        linear_followers = findViewById(R.id.linear_followers);
        linear_categories = findViewById(R.id.linear_categories);
        header_umbrella_layout = findViewById(R.id.header_umbrella_layout);
        linear_ratting = findViewById(R.id.linear_ratting);
        more_header = findViewById(R.id.header_more);
        linear_followers.setOnClickListener(this);
        linear_ratting.setOnClickListener(this);
        linear_categories.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_message.setOnClickListener(this);
        more_header.setOnClickListener(this);
    }
    public static class DrawableGradient extends GradientDrawable {
        public DrawableGradient(int[] colors, int cornerRadius) {
            super(Orientation.LEFT_RIGHT, colors);
            try {
                this.setShape(GradientDrawable.RECTANGLE);
                this.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                this.setCornerRadius(cornerRadius);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void UnfollowOtherBoothApi() {
        CustomLoader.showDialog(BoothProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Following", otherUserID);
        body.put("Follow", "0");
        body.put("DeviceType", "Android");
        body.put("Type", "booth");
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("followBoothsBody", body.toString());
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.follow, BoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("FollowBoothResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            follow_btn.setText("Follow");
                            Following = "0";
                            boothFollowersCount.setText(String.valueOf(Integer.valueOf(boothFollowersCount.getText().toString()) - 1));
                        } else {
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FollowOtherBoothApi() {
        CustomLoader.showDialog(BoothProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Following", otherUserID);
        body.put("Follow", "1");
        body.put("DeviceType", "Android");
        body.put("Type", "booth");
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("followBoothsBody", body.toString());
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.follow, BoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("FollowBoothResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            follow_btn.setText("Unfollow");
                            Following = "1";
                            boothFollowersCount.setText(String.valueOf(Integer.valueOf(boothFollowersCount.getText().toString()) + 1));
                        } else {
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getBoothDetails(){
        CustomLoader.showDialog(BoothProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + otherUserID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE  + "&Type=booth", BoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("BoothDetailsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            CustomLoader.dialog.dismiss();
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
                            int BoothAverageRating = user_info.getInt("BoothAverageRating");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothCoverImage = user_info.getString("BoothCoverImage");
                            int BoothFollowersCount = user_info.getInt("BoothFollowersCount");
                            String BoothImage = user_info.getString("BoothImage");
                            String BoothName = user_info.getString("BoothName");
                            String BoothType = user_info.getString("BoothType");
                            String CityID = user_info.getString("CityID");
                            String CityTitle = user_info.getString("CityTitle");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            int UserFollowersCount = user_info.getInt("UserFollowersCount");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String MaroofVerified = user_info.getString("MaroofVerified");
                            BoothUserName = user_info.getString("BoothUserName");
                            Following = user_info.getString("Following");
                            latitude = user_info.getString("BoothLat");
                            longitude = user_info.getString("BoothLong");
                            BoothAddress = user_info.getString("BoothAddress");
                            String BoothCouponCheck = user_info.getString("BoothCouponCheck");
                            Currency = user_info.getString("Currency");
                            if (MaroofVerified.equals("1")){
                                maroof_verified.setVisibility(View.VISIBLE);
                            }else {
                                maroof_verified.setVisibility(View.GONE);
                            }
                            OthersUserresponseID = UserID;
                            mEditor.putString("OtherUserID", UserID).commit();
                            if(Following.equals("1")){
                                follow_btn.setText(getResources().getString(R.string.unfollow));
                            }else {
                                follow_btn.setText(getResources().getString(R.string.follow));
                            }
                            JSONObject profileCustomization = user_info.getJSONObject("ProfileCustomization");
                            String BottomStyleImage = profileCustomization.getString("BottomStyleImage");
                            String ColorCode = profileCustomization.getString("ColorCode");
                            String ThemeImage = profileCustomization.getString("ThemeImage");
                            String TopStyleImage = profileCustomization.getString("TopStyleImage");

                            themeColor = ColorCode;

                            iconsColorStates = new ColorStateList(
                                    new int[][]{
                                            new int[]{-android.R.attr.state_checked},
                                            new int[]{android.R.attr.state_checked},
                                            new int[]{android.R.attr.state_selected},
                                            new int[]{-android.R.attr.state_selected}
                                    },
                                    new int[]{
                                            Color.parseColor(ColorCode),
                                            Color.parseColor(ColorCode),
                                            Color.parseColor(ColorCode),
                                            Color.parseColor(ColorCode)
                                    });
                            if (BoothCouponCheck.equals("1")){
                                promoListButton.setVisibility(View.VISIBLE);
//                                promoListButton.setBackgroundTintList(iconsColorStates);
                                promoListButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ColorCode)));
                                boothImg.setBackground(getResources().getDrawable(R.drawable.white_dropshadow));
                            }else {
                                promoListButton.setVisibility(View.GONE);
                                boothImg.setBackground(getResources().getDrawable(R.drawable.invisible_circle));
                            }
                            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(ColorCode));
                            tabLayout.setTabTextColors(getResources().getColor(R.color.font_color), Color.parseColor(ColorCode));
                            btn_about.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
                                    Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 50));
                            btn_message.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
                                    Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 50));
                            follow_btn.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
                                    Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 50));
                            separator1.setBackgroundColor(Color.parseColor(ColorCode));
                            separator2.setBackgroundColor(Color.parseColor(ColorCode));

                            boothImg.setBorderColor(Color.parseColor(ColorCode));

                            booth_name.setText(BoothName);
                            booth_address.setText(CityTitle);
                            boothCategoriesCount.setText(String.valueOf(BoothCategoriesCount));
                            boothFollowersCount.setText(String.valueOf(BoothFollowersCount));
                            booth_rating_count.setText(String.valueOf(BoothAverageRating));
                            user_name.setText("@" + BoothUserName);
                            Picasso.get().load(Constants.URL.IMG_URL + BoothImage).placeholder(R.drawable.user).into(boothImg);
                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothCoverImage).into(cover_image);
                            Picasso.get().load(Constants.URL.IMG_URL + TopStyleImage).into(top_head);
                            Picasso.get().load(Constants.URL.IMG_URL + BottomStyleImage).into(bottom_head);
                            mEditor.putString("boothMobile", Mobile);
                            mEditor.putString("boothMail", Email);
                            mEditor.putString("boothFromTime", ContactTimeFrom);
                            mEditor.putString("boothToTime", ContactTimeTo);
                            mEditor.putString("boothContactDays", ContactDays);
                            mEditor.commit();
                            if (!BoothImage.isEmpty()){
                                profileArray.clear();
                                profileArray.add(Constants.URL.IMG_URL + BoothImage);
                                boothImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new ImageViewer.Builder(BoothProfileActivity.this, profileArray)
                                                .setStartPosition(0)
                                                .show();
                                    }
                                });
                            }
                            if (!CompressedBoothCoverImage.isEmpty()){
                                coverarray.clear();
                                coverarray.add(Constants.URL.IMG_URL + CompressedBoothCoverImage);
                                cover_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new ImageViewer.Builder(BoothProfileActivity.this, coverarray)
                                                .setStartPosition(0)
                                                .show();
                                    }
                                });
                            }
                            header_umbrella_layout.setVisibility(View.VISIBLE);
                            booth_title = BoothName;
                            booth_username = "@"+BoothUserName;
                            booth_addres = CityTitle;
                            booth_description = About;
                        }else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == linear_followers){
            mEditor.putString("typetype", "booth").commit();
            startActivity(new Intent(BoothProfileActivity.this, FollowersActivity.class));
        }else if (v == linear_categories){
            Intent intent = new Intent(BoothProfileActivity.this, SelectedCategoriesList.class);
            intent.putExtra("state", "booth");
            intent.putExtra("otheruserID", OthersUserresponseID);
            startActivity(intent);
        }else if (v == linear_ratting){
            Intent intent = new Intent(BoothProfileActivity.this, RatingAndReviewsActivity.class);
            intent.putExtra("userID", OthersUserresponseID);
            intent.putExtra("Activity", "BoothOther");
            startActivity(intent);
        }else if (v == btn_about){
            Intent intent = new Intent(BoothProfileActivity.this, AboutBoothActivity.class);
            intent.putExtra("BOOTH_NAME", booth_title);
            intent.putExtra("BOOTH_USERNAME", booth_username);
            intent.putExtra("BOOTH_ADDRESS", booth_addres);
            intent.putExtra("BOOTH_DESCRIPTION", booth_description);
            intent.putExtra("Address", BoothAddress);
            intent.putExtra("Latitude", latitude);
            intent.putExtra("Longitude", longitude);
            startActivity(intent);
        }else if (v == btn_message){
            Intent intent = new Intent(BoothProfileActivity.this, ContactBoothActivity.class);
            intent.putExtra("username", BoothUserName);
            startActivity(intent);
        }else if (v == more_header){
            final PopupMenu moreMenu = new PopupMenu(BoothProfileActivity.this, v);
            moreMenu.getMenuInflater().inflate(R.menu.other_user_menu, moreMenu.getMenu());
            moreMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.report:
                            final Dialog reportdialog = new Dialog(BoothProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            reportdialog.setContentView(R.layout.report_dialog);
                            Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                            Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);
                            reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            reportdialog.show();
                            spamBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ReportUser(spamBtn.getText().toString());
                                    reportdialog.dismiss();
                                }
                            });
                            inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ReportUser(inappropriateBtn.getText().toString());
                                    reportdialog.dismiss();
                                }
                            });
                            break;
                        case R.id.block:
                            BlockUser();
                            break;
                    }
                    return false;
                }
            });
            moreMenu.show();
        }
    }

    private void BlockUser(){
        CustomLoader.showDialog(BoothProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("BlockedUserID", otherUserID);
        body.put("Type", "block");
        body.put("UserType", sharedpreferences.getString("LastState", ""));
        body.put("BlockedUserType", "booth");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("BlockUserBody", body.toString());
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BlockUser, BoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("BlockUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ReportUser(String reason){
        CustomLoader.showDialog(BoothProfileActivity.this);
        Map<String,String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ReportedUserID", otherUserID);
        body.put("ReportedUserType", "booth");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("ReportReason", reason);
        Log.e("ReportUserBody", body.toString());
        HashMap<String,String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ReportUser, BoothProfileActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()){
                    Log.e("ReportUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200){
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(BoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(BoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}