package com.schopfen.Booth.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.fxn.pix.Pix;
import com.google.android.material.tabs.TabLayout;
import com.schopfen.Booth.Activities.AboutBoothActivity;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.BoothSettingsActivity;
import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.Activities.ContactBoothActivity;
import com.schopfen.Booth.Activities.FollowersActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.MainActivity;
import com.schopfen.Booth.Activities.MyPromoCodes;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.RatingAndReviewsActivity;
import com.schopfen.Booth.Activities.SelectedCategoriesList;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Adapters.BoothProfileSectionPagerAdapter;
import com.schopfen.Booth.Adapters.BoothProfile_Product_Adapter;
import com.schopfen.Booth.Adapters.MyPromoCodeAdapter;
import com.schopfen.Booth.Adapters.ReasonSelectionAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.BoothProfile_Product_Data;
import com.schopfen.Booth.Models.CancelationReasonModel;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.MyPromoModel;
import com.schopfen.Booth.Models.ProductImagesModel;
import com.schopfen.Booth.NonSwipeableViewPager;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.fxn.pix.Pix.start;

public class Booth_Profile_Fragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    SharedPreferences BoothstoreProductsPrefes;
    SharedPreferences.Editor BoothstoreProductsPrefesEditor;
    String BoothProductsPREFERENCES = "BOOTHPRODUCTS";
    SharedPreferences storeProductsPrefes;
    SharedPreferences.Editor storeProductsPrefesEditor;
    String ProductsPREFERENCES = "PRODUCTS";
    SharedPreferences promotedProductsPrefes;
    SharedPreferences.Editor promotedProductsPrefesEditor;
    String PromotedPREFERENCES = "PROMOTEDPRODUCTS";

    RecyclerView productsRecycler;
    ArrayList<String> productsData = new ArrayList<>();

    TextView boothName, boothCity, followersCount, boothRating, boothCategoriesCount, username;
    CircleImageView boothImg;
    ImageView boothCoverImg, topImg, bottomImg;
    RelativeLayout moreIcon, header_comment, header_switch;
    TextView aboutBoothBtn, contactBoothBtn, separator1, separator2;
    ImageView inbox_img;
    ArrayList<String> profileArray = new ArrayList<>();
    ArrayList<String> coverarray = new ArrayList<>();
    String themeColor;

    private static final String TAG = "RouteScheduleActivity";

    private BoothProfileSectionPagerAdapter sectionsPageAdapter;
    private NonSwipeableViewPager viewPager;
    TabLayout tabLayout;
    NestedScrollView nest_scrollview;
    String booth_title, booth_username, booth_address, booth_description, CompressedImage, CityTitle, Mobile, BoothUserName,
            Email, IsMobileVerified, Currency;
    LinearLayout linearLayout_follow, linearLayout_rating, linearLayout_cat;
    public static String otherID;
    SwipeRefreshLayout swipe_profile;
    String longitude, latitude, BoothAddress, FullName, CityID;
    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    Dialog dialog;
    Button cancelBtn;
    Button createBtn;
    TextView editEmail;
    EditText editFullname, editPhone, editUsername;
    AutoCompleteTextView editCity, editGender;
    ArrayList<String> genderArray = new ArrayList<>();
    TextView editIsVerify, msg_count;
    CircleImageView profPicture;
    ImageView cam, maroof_verified, promoListButton;
    ArrayList<String> myreturnValue = new ArrayList<>();
    Uri myUri;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    Bitmap bitmap;
    Map<String, String> params = new HashMap<String, String>();


    BoothProfile_Product_Adapter adapter;
    BoothProfile_Product_Data data;
    ArrayList<BoothProfile_Product_Data> list = new ArrayList<>();
    ArrayList<ProductImagesModel> imagesList;
    RecyclerView recyclerView;
    BoothProfile_Product_Data boothProfile_product_data;

    ColorStateList iconsColorStates;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.booth_profile_fragment, container, false);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getBoothDetails();
            getCities();
            ProductApi();

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        storeProductsPrefes = getActivity().getSharedPreferences(ProductsPREFERENCES, Context.MODE_PRIVATE);
        storeProductsPrefesEditor = storeProductsPrefes.edit();
        promotedProductsPrefes = getActivity().getSharedPreferences(PromotedPREFERENCES, Context.MODE_PRIVATE);
        promotedProductsPrefesEditor = promotedProductsPrefes.edit();
        BoothstoreProductsPrefes = getActivity().getSharedPreferences(BoothProductsPREFERENCES, Context.MODE_PRIVATE);
        BoothstoreProductsPrefesEditor = storeProductsPrefes.edit();

        mEditor.putString("Booth", "User").apply();
        mEditor.putString("PromotedPath", "Fragment").apply();

        initializeViews(view);
        Fresco.initialize(getActivity());

//        nest_scrollview.setFocusableInTouchMode(true);
//        nest_scrollview.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

//        sectionsPageAdapter = new BoothProfileSectionPagerAdapter(getChildFragmentManager());

        viewPager = view.findViewById(R.id.contain);
//        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager);

        swipe_profile.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBoothDetails();
                getCities();
                ProductApi();
            }
        });

        promoListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PromoCodesListDialog();
            }
        });

    }

    private void PromoCodesListDialog() {

        final Dialog dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.promo_list_dialog);
        Button yes = dialog.findViewById(R.id.btn_yes);
        RecyclerView promo_list = dialog.findViewById(R.id.promo_list);
        ProgressBar progressbar = dialog.findViewById(R.id.progressbar);

        progressbar.setVisibility(View.VISIBLE);
        promo_list.setVisibility(View.GONE);

        yes.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(themeColor),
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
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getPromoCodes, getActivity(), body, headers, new ServerCallback() {
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

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            promo_list.setLayoutManager(linearLayoutManager);
                            MyPromoCodeAdapter myPromoCodeAdapter = new MyPromoCodeAdapter(themeColor, Currency, "MyProfile", getActivity(), myPromoModelArrayList, new MyPromoCodeAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            promo_list.setAdapter(myPromoCodeAdapter);

                            progressbar.setVisibility(View.GONE);
                            promo_list.setVisibility(View.VISIBLE);

                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                            promo_list.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                        promo_list.setVisibility(View.VISIBLE);
                    }

                }else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    promo_list.setVisibility(View.VISIBLE);
                }

            }
        });

    }

    private void ProductApi() {
        Map<String, String> postparams = new HashMap<String, String>();
        postparams.put("UserID", sharedpreferences.getString("UserID", " "));
        postparams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        if (sharedpreferences.getString("Booth", "").equals("Other")) {
            postparams.put("OtherUserID", getActivity().getIntent().getStringExtra("OtherUserID"));
        }

//        Log.e("postparams", postparams.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.products, getActivity(), postparams, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("productResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            list.clear();

                            JSONArray products = jsonObject.getJSONArray("products");

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject product_items = products.getJSONObject(i);
                                boothProfile_product_data = new BoothProfile_Product_Data();

                                String ProductID = product_items.getString("ProductID");
                                String title = product_items.getString("Title");
                                JSONArray product_images = product_items.getJSONArray("ProductImages");
                                imagesList = new ArrayList<>();
                                for (int j = 0; j < product_images.length(); j++) {
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

                            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

                            adapter = new BoothProfile_Product_Adapter(getActivity(), list, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
//                                    Toast.makeText(getActivity(), "Item "+position, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                    mEditor.putString("ProductID", list.get(position).getProduct_id()).commit();
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(adapter);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        sectionsPageAdapter = new BoothProfileSectionPagerAdapter(getChildFragmentManager());

        sectionsPageAdapter.addFragment(new BoothProfile_Products_Fragment(), "Products");
        sectionsPageAdapter.addFragment(new BoothProfile_Promotions_Fragment(), "Promotions");
        viewPager.setAdapter(sectionsPageAdapter);
    }

    private void getBoothDetails() {
//        CustomLoader.showDialog(getActivity());
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            swipe_profile.setRefreshing(false);
//                            CustomLoader.dialog.dismiss();
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
                            CityID = user_info.getString("CityID");
                            CityTitle = user_info.getString("CityTitle");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String DeviceToken = user_info.getString("DeviceToken");
                            Email = user_info.getString("Email");
                            FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            int UserFollowersCount = user_info.getInt("UserFollowersCount");
                            otherID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            BoothUserName = user_info.getString("BoothUserName");
                            latitude = user_info.getString("BoothLat");
                            longitude = user_info.getString("BoothLong");
                            BoothAddress = user_info.getString("BoothAddress");
                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String MaroofVerified = user_info.getString("MaroofVerified");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");
                            String BoothCouponCheck = user_info.getString("BoothCouponCheck");
                            Currency = user_info.getString("Currency");

                            if (BoothUnreadMessageCount.equals("0")) {
                                msg_count.setVisibility(View.GONE);
                            } else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(BoothUnreadMessageCount);
                            }

                            if (MaroofVerified.equals("1")) {
                                maroof_verified.setVisibility(View.VISIBLE);
                            } else {
                                maroof_verified.setVisibility(View.GONE);
                            }

//                            if (BoothHasUnreadMessage.equals("yes")) {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                            } else {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                            }

                            JSONObject profileCustomization = user_info.getJSONObject("ProfileCustomization");
                            String BottomStyleImage = profileCustomization.getString("BottomStyleImage");
                            String ColorCode = profileCustomization.getString("ColorCode");
                            String ThemeImage = profileCustomization.getString("ThemeImage");
                            String TopStyleImage = profileCustomization.getString("TopStyleImage");

                            themeColor = ColorCode;

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.putString("Currency", Currency);
                            mEditor.commit();

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
//                                promoListButton.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
//                                        Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 150));
                                promoListButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(ColorCode)));
                                boothImg.setBackground(getResources().getDrawable(R.drawable.white_dropshadow));
                            }else {
                                promoListButton.setVisibility(View.GONE);
                                boothImg.setBackground(getResources().getDrawable(R.drawable.invisible_circle));
                            }

                            tabLayout.setSelectedTabIndicatorColor(Color.parseColor(ColorCode));
                            tabLayout.setTabTextColors(getResources().getColor(R.color.font_color), Color.parseColor(ColorCode));

                            aboutBoothBtn.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
                                    Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 50));
                            contactBoothBtn.setBackgroundDrawable(new DrawableGradient(new int[]{Color.parseColor(ColorCode),
                                    Color.parseColor(ColorCode), Color.parseColor(ColorCode)}, 50));
                            separator1.setBackgroundColor(Color.parseColor(ColorCode));
                            separator2.setBackgroundColor(Color.parseColor(ColorCode));

                            boothImg.setBorderColor(Color.parseColor(ColorCode));

                            boothName.setText(BoothName);
                            //boothName.setTextColor(Color.parseColor(ColorCode));
                            boothCity.setText(CityTitle);
                            followersCount.setText(String.valueOf(BoothFollowersCount));
                            boothCategoriesCount.setText(String.valueOf(BoothCategoriesCount));
                            boothRating.setText(String.valueOf(BoothAverageRating));
                            username.setText("@" + BoothUserName);
                            Picasso.get().load(Constants.URL.IMG_URL + BoothImage).placeholder(R.drawable.user).into(boothImg);
                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothCoverImage).into(boothCoverImg);
                            Picasso.get().load(Constants.URL.IMG_URL + TopStyleImage).into(topImg);
                            Picasso.get().load(Constants.URL.IMG_URL + BottomStyleImage).into(bottomImg);

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
                                        new ImageViewer.Builder(getActivity(), profileArray)
                                                .setStartPosition(0)
                                                .show();
                                    }
                                });
                            }
                            if (!CompressedBoothCoverImage.isEmpty()){
                                coverarray.clear();
                                coverarray.add(Constants.URL.IMG_URL + CompressedBoothCoverImage);
                                boothCoverImg.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new ImageViewer.Builder(getActivity(), coverarray)
                                                .setStartPosition(0)
                                                .show();
                                    }
                                });
                            }

                            booth_title = BoothName;
                            booth_username = "@" + BoothUserName;
                            booth_address = CityTitle;
                            booth_description = About;
                        } else {
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
// public DrawableGradient SetTransparency(int transparencyPercent) {
// this.setAlpha(255 - ((255 * transparencyPercent) / 100));
//
// return this;
// }
    }

    @Override
    public void onClick(View v) {
        if (v == moreIcon) {
            startActivity(new Intent(getActivity(), BoothSettingsActivity.class));
        } else if (v == contactBoothBtn) {
            Intent intent = new Intent(getActivity(), ContactBoothActivity.class);
            mEditor.putString("OtherUserID", otherID).commit();
            intent.putExtra("username", BoothUserName);
            startActivity(intent);
        } else if (v == aboutBoothBtn) {
            Intent intent = new Intent(getActivity(), AboutBoothActivity.class);
            intent.putExtra("BOOTH_NAME", booth_title);
            intent.putExtra("BOOTH_USERNAME", booth_username);
            intent.putExtra("BOOTH_ADDRESS", booth_address);
            intent.putExtra("BOOTH_DESCRIPTION", booth_description);
            intent.putExtra("Address", BoothAddress);
            intent.putExtra("Latitude", latitude);
            intent.putExtra("Longitude", longitude);
            startActivity(intent);
        } else if (v == linearLayout_follow) {
            mEditor.putString("typetype", "booth").commit();
            mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).commit();
            startActivity(new Intent(getActivity(), FollowersActivity.class));
//            Toast.makeText(getActivity(), "Mmm, working on it!", Toast.LENGTH_SHORT).show();
        } else if (v == linearLayout_cat) {
            Intent intent = new Intent(getActivity(), SelectedCategoriesList.class);
            intent.putExtra("state", "booth");
            intent.putExtra("otheruserID", otherID);
            startActivity(intent);
        } else if (v == linearLayout_rating) {
            Intent intent = new Intent(getActivity(), RatingAndReviewsActivity.class);
            intent.putExtra("userID", sharedpreferences.getString("UserID", " "));
            intent.putExtra("Activity", "MyBooth");
            startActivity(intent);
        } else if (v == header_comment) {
            startActivity(new Intent(getActivity(), InboxActivity.class));
        } else if (v == header_switch) {
            if (isNetworkAvailable()){
                if (FullName.isEmpty()) {
                    Dialog dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    dialog.setContentView(R.layout.areyousureloader);
                    // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    Button btn_no = dialog.findViewById(R.id.btn_no);
                    Button btn_yes = dialog.findViewById(R.id.btn_yes);
                    TextView dialog_alert_text = dialog.findViewById(R.id.dialog_alert_text);
                    dialog_alert_text.setText(getResources().getString(R.string.switch_account_string));

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            editUserProfile();
                            dialog.dismiss();
                        }
                    });

                } else {
                    Dialog dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    dialog.setContentView(R.layout.areyousureloader);
                    // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    Button btn_no = dialog.findViewById(R.id.btn_no);
                    Button btn_yes = dialog.findViewById(R.id.btn_yes);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    btn_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switchAccount();
                            dialog.dismiss();
                        }
                    });

                }
            }else {
                Toast.makeText(getActivity(), "Cannot connect to internet... please check your connection!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void switchAccount() {

        CustomLoader.showDialog(getActivity());
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("LastState", "user");
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, getActivity(), params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");

                            mEditor.putString("LastState", LastState).apply();
                            // startActivity(new Intent(BoothSettingsActivity.this, MainActivity.class));
                            // finish();

                            storeProductsPrefesEditor.clear().commit();
                            promotedProductsPrefesEditor.clear().commit();
                            BoothstoreProductsPrefesEditor.clear().commit();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void editUserProfile() {

        dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.edit_profile_dialog_sa);

        cancelBtn = dialog.findViewById(R.id.btn_cancel_sa);
        createBtn = dialog.findViewById(R.id.btn_update_sa);
        cam = dialog.findViewById(R.id.camera_dialog_sa);
        profPicture = dialog.findViewById(R.id.myprofilepic);
        editFullname = dialog.findViewById(R.id.fullname);
        editUsername = dialog.findViewById(R.id.userName);
        editPhone = dialog.findViewById(R.id.phone);
        editEmail = dialog.findViewById(R.id.email);
        editCity = dialog.findViewById(R.id.city);
        editGender = dialog.findViewById(R.id.gender);
        editIsVerify = dialog.findViewById(R.id.isVerify);

        Picasso.get().load(Constants.URL.IMG_URL + CompressedImage).placeholder(R.drawable.user).into(profPicture);
        editCity.setText(CityTitle);
        editFullname.setText(FullName);
        cam.setVisibility(View.VISIBLE);
//                            editGender.setHint(userDetailsData.get(0).getGender());
        editPhone.setText(Mobile);
        editUsername.setText(BoothUserName);
        editEmail.setText(Email);

        if (Mobile.isEmpty()) {
            editIsVerify.setText("");
        } else if (IsMobileVerified.equals("0")) {
            editIsVerify.setText("Verify");
        } else if (IsMobileVerified.equals("1")) {
            editIsVerify.setText("Verified");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editCity.setAdapter(adapter);

        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
                editCity.showDropDown();
                hideKeyboard(editCity);
            }
        });
//        editCity.setTextIsSelectable(true);
        editCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CityID = citiesData.get(position).getCityID();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, genderArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editGender.setAdapter(genderAdapter);

        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGender.showDropDown();
                hideKeyboard(editGender);
            }
        });
//        editGender.setTextIsSelectable(true);
        editGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("cameraClick", "yes");
                myreturnValue.clear();
                start(getActivity(),
                        1234,
                        1);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
                if (editFullname.getText().toString().trim().length() == 0) {
                    editFullname.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editUsername.getText().toString().trim().length() == 0) {
                    editUsername.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editEmail.getText().toString().trim().length() == 0) {
                    editEmail.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                }
//                else if (!ccp_edit.isValidFullNumber()) {
//                    editPhone.getParent().requestChildFocus(editPhone, editPhone);
//                    editPhone.startAnimation(shake);
//                    Toast.makeText(getActivity(), "Mobile Number is not valid", Toast.LENGTH_SHORT).show();
//                }
                else if (editGender.getText().toString().trim().length() == 0) {
                    editGender.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editCity.getText().toString().trim().length() == 0) {
                    editCity.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
                        editEmail.startAnimation(shake);
                        Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_SHORT).show();
                    } else {
                        new UploadFileToServer().execute();
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

//            progress = new Dialog(RegisterBoothActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//            progress.setContentView(R.layout.progress_dialog_circle);
//            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
//            tv = progress.findViewById(R.id.tv);
//
//            Resources res = getResources();
//            Drawable drawable = res.getDrawable(R.drawable.circular);
//            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
//            mProgress.setProgress(0);   // Main Progress
//            mProgress.setSecondaryProgress(100); // Secondary Progress
//            mProgress.setMax(100); // Maximum Progress
//            mProgress.setProgressDrawable(drawable);
//
//            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progress.setCanc
//            elable(false);
//            progress.show();
            CustomLoader.showDialog(getActivity());
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value
//            mProgress.setProgress(progress[0]);

            // updating percentage value
//            tv.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.updateProfile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (uriArrayList.size() > 0) {
                    for (int i = 0; i < uriArrayList.size(); i++) {
                        File sourceFile = new File(uriArrayList.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("Image", new FileBody(sourceFile));
                    }
                }

                entity.addPart("RoleID", new com.schopfen.Booth.ApiStructure.StringBody("2"));
                entity.addPart("FullName", new com.schopfen.Booth.ApiStructure.StringBody(editFullname.getText().toString()));
                entity.addPart("UserName", new com.schopfen.Booth.ApiStructure.StringBody(editUsername.getText().toString()));
                entity.addPart("Email", new com.schopfen.Booth.ApiStructure.StringBody(editEmail.getText().toString()));
                entity.addPart("Mobile", new com.schopfen.Booth.ApiStructure.StringBody(editPhone.getText().toString()));
                String genderFinal = "";
                if (editGender.getText().toString().equals(genderArray.get(0))){
                    genderFinal = "Male";
                }else {
                    genderFinal = "Female";
                }
                entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                entity.addPart("LastState", new com.schopfen.Booth.ApiStructure.StringBody("user"));
                entity.addPart("CityID", new com.schopfen.Booth.ApiStructure.StringBody(CityID));
                entity.addPart("DeviceType", new com.schopfen.Booth.ApiStructure.StringBody("Android"));
                entity.addPart("DeviceToken", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("DeviceToken", " ")));
                entity.addPart("UserID", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("UserID", " ")));
                entity.addPart("OS", new com.schopfen.Booth.ApiStructure.StringBody(Build.VERSION.RELEASE));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));

                // Extra parameters if you want to pass to server

//                Map<String, String> headers = new HashMap<>();
//                headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

//                totalSize = entity.getContentLength();
//                final StringEntity se = new StringEntity(entity, ContentType.APPLICATION_JSON);
                httppost.setEntity(entity);
                httppost.setHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ResponseResult", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");

                if (status == 200) {
                    JSONObject user_info = jsonObject.getJSONObject("user_info");
                    String UserID = user_info.getString("UserID");
                    String RoleID = user_info.getString("RoleID");
                    String UserName = user_info.getString("UserName");
                    String Email = user_info.getString("Email");
                    String Mobile = user_info.getString("Mobile");
                    String Gender = user_info.getString("Gender");
                    String CityID = user_info.getString("CityID");
                    String CompressedImage = user_info.getString("CompressedImage");
                    String Image = user_info.getString("Image");
                    String LastState = user_info.getString("LastState");
                    String BoothType = user_info.getString("BoothType");
                    String IsEmailVerified = user_info.getString("IsEmailVerified");
                    String IsMobileVerified = user_info.getString("IsMobileVerified");
                    String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                    String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                    String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                    String DeviceToken = user_info.getString("DeviceToken");
                    String Notification = user_info.getString("Notification");
                    String AuthToken = user_info.getString("AuthToken");
                    String FullName = user_info.getString("FullName");
                    String BoothName = user_info.getString("BoothName");

                    mEditor.putString("UserID", UserID);
                    mEditor.putString("ApiToken", AuthToken);
                    mEditor.putString("DeviceToken", DeviceToken);
                    mEditor.putString("LastState", LastState);
                    mEditor.apply();

                    mEditor.putString("LastState", LastState).apply();

                    Intent intent = new Intent(getActivity(), CategoriesListActivity.class);
                    intent.putExtra("Activity", "SignUp");
                    startActivity(intent);
                    getActivity().finishAffinity();

                    CustomLoader.dialog.dismiss();

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, Calendar.getInstance().getTime() + ".jpg");
        uriArrayList.clear();
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayList.add(myUri);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void imageUploadFunction() {
        CustomLoader.showDialog(getActivity());
        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("FullName", editFullname.getText().toString());
        params.put("UserName", editUsername.getText().toString());
        params.put("Email", editEmail.getText().toString());
        params.put("Mobile", editPhone.getText().toString());
        params.put("Gender", editGender.getText().toString());
        params.put("CityID", CityID);
        params.put("DeviceType", "Android");
        params.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, getActivity(), params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");

                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.apply();

                            mEditor.putString("LastState", LastState).apply();

                            Intent intent = new Intent(getActivity(), CategoriesListActivity.class);
                            intent.putExtra("Activity", "SignUp");
                            startActivity(intent);
                            getActivity().finishAffinity();

                            CustomLoader.dialog.dismiss();

                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        Log.e("saveBookErrorResponse1", e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Map<String, String> imgToStringFunction(Bitmap bitmap) {

        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Compressed
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos); // Compressed
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] data = bos.toByteArray(); // Compressed
                byte[] original_data = original_image.toByteArray(); // Original
                // images[0] = Base64.encodeToString(data, Base64.DEFAULT);
                // images[1] = Base64.encodeToString(original_data, Base64.DEFAULT);
                params.put("Image", Base64.encodeToString(data, Base64.NO_WRAP));
//                params.put("CompressedImage", Base64.encodeToString(original_data, Base64.DEFAULT));
            }
        }
        return params;
    }

    private void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCitiesResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONArray citiesArray = jsonObject.getJSONArray("cities");
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject jsonObject1 = citiesArray.getJSONObject(i);

                                String CityID = jsonObject1.getString("CityID");
                                String CityLat = jsonObject1.getString("CityLat");
                                String CityLong = jsonObject1.getString("CityLong");
                                String CityPlaceID = jsonObject1.getString("CityPlaceID");
                                String Title = jsonObject1.getString("Title");
                                citiesNames.add(Title);
                                citiesData.add(new CitiesData(CityID, CityLat, CityLong, CityPlaceID, Title));
                            }

//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
//                                    android.R.layout.simple_list_item_1, citiesNames);
//                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//                            editCity.setAdapter(adapter);


                        } else {
                            Toast.makeText(getActivity(), String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeViews(View view) {
        nest_scrollview = view.findViewById(R.id.nest_scrollview);
        recyclerView = view.findViewById(R.id.recyclerView);
        boothName = view.findViewById(R.id.boothName);
        boothCity = view.findViewById(R.id.boothCity);
        followersCount = view.findViewById(R.id.followersCount);
        boothCategoriesCount = view.findViewById(R.id.boothCategoriesCount);
        boothRating = view.findViewById(R.id.boothRating);
        boothImg = view.findViewById(R.id.boothImg);
        boothCoverImg = view.findViewById(R.id.boothCoverImg);
        topImg = view.findViewById(R.id.topImg);
        bottomImg = view.findViewById(R.id.bottomImg);
        maroof_verified = view.findViewById(R.id.maroof_verified);
        swipe_profile = view.findViewById(R.id.swipe_profile);
        username = view.findViewById(R.id.username);
        moreIcon = view.findViewById(R.id.more);
        header_switch = view.findViewById(R.id.header_switch);
        inbox_img = view.findViewById(R.id.inbox_img);
        header_comment = view.findViewById(R.id.header_comment);
        aboutBoothBtn = view.findViewById(R.id.aboutBoothBtn);
        contactBoothBtn = view.findViewById(R.id.contactBoothBtn);
        linearLayout_follow = view.findViewById(R.id.linear_follow);
        promoListButton = view.findViewById(R.id.promoListButton);
        linearLayout_rating = view.findViewById(R.id.linear_rating);
        separator1 = view.findViewById(R.id.separator1);
        separator2 = view.findViewById(R.id.separator2);
        linearLayout_cat = view.findViewById(R.id.linear_cat);
        msg_count = view.findViewById(R.id.msg_count);

        linearLayout_follow.setOnClickListener(this);
        linearLayout_rating.setOnClickListener(this);
        linearLayout_cat.setOnClickListener(this);
        moreIcon.setOnClickListener(this);
        header_switch.setOnClickListener(this);
        header_comment.setOnClickListener(this);
        aboutBoothBtn.setOnClickListener(this);
        contactBoothBtn.setOnClickListener(this);

        genderArray.clear();
        genderArray.add(getResources().getString(R.string.male));
        genderArray.add(getResources().getString(R.string.female));

        getBoothDetails();
        getCities();
        ProductApi();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Booth_Profile_Fragment test = (Booth_Profile_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("5");
        if (test != null && test.isVisible()) {
            if (resultCode == RESULT_OK && requestCode == 1234) {
                myreturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                for (int a = 0; a < myreturnValue.size(); a++) {
                    File imgFile = new File(myreturnValue.get(a));
//                Uri myUri = Uri.parse(returnValue.get(a));
                    myUri = Uri.fromFile(new File(myreturnValue.get(a)));

                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    profilePic.setImageBitmap(myBitmap);
                    }
                    beginCrop(myUri);

                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
        }
    }

    // Image Croper
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(getActivity());
    }

    // Image Croper Handling
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profPicture.setImageBitmap(null);
            profPicture.setImageURI(UCrop.getOutput(result));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), UCrop.getOutput(result));
                bitmapArrayList.add(bitmap);
                saveToInternalStorage(bitmap);
                Log.e("chekingBit", bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
