package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.schopfen.Booth.Adapters.Answers_Details_Adapter;
import com.schopfen.Booth.Adapters.HomeBoothPagerAdapter;
import com.schopfen.Booth.Adapters.HomeHomePagerAdapter;
import com.schopfen.Booth.Adapters.SimilarProductAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.CommentsModel;
import com.schopfen.Booth.Models.MentionedUsersInfo;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.Models.SimilarProductsModel;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView commentsRecycler, similar_products_recycler;
    ArrayList<CommentsModel> commentsArray = new ArrayList<>();
    Button addToCart;
    ViewPager viewPager;
    CircleIndicatorPager indicatorPager;
    ArrayList<ProductImagesData> productImagesData;
    ArrayList<ProductsData> productsData = new ArrayList<>();
    ArrayList<SimilarProductsModel> similarProducts;

    SwipeRefreshLayout product_detail_swipe_reload;
    CircleImageView boothImage;
    TextView username, time, productName, likesCount, commentCount, brandName, productCondition, deliveryTime, contactHours, cityName,
            shippingCost, productDetails, productCategory, mainPrice, showing_commentscunt, writeacomment, productPrice, viewsCount, out_of_stock, returnPolicyText;
    RatingBar ratingBar;
    ImageView more, repost_img;
    LinearLayout shareLinear, commentsCountLinear, wishListLayout, addToCartLayout, similar_products_layout, blurring;
    LikeButton likeButton, wishListBtn;
    NestedScrollView nestedScroll;

    String IsLiked, commentscount;
    String ProductID, BoothID, IsInWishlist;
    RelativeLayout viewpager_parent;

    String cartprocedure = "";
    String sizeofArray = "0";
    String userIdMine = "";

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
            setContentView(R.layout.activity_product_details);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            mEditor.putString("activity", "product_detail").apply();

            initilizeViews();
            initializeClickListeners();
            productDetailsApiCall();
            GetCommentsApi();

            if (sharedpreferences.getString("LastState", "").equals("user")) {
                wishListBtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        AddtoWishList();
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        RemoveFromWishList();
                    }
                });
            } else {
                wishListBtn.setEnabled(false);
            }


            viewpager_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            product_detail_swipe_reload.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    productDetailsApiCall();
                    GetCommentsApi();
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isNetworkAvailable()){
            GetCommentsApi();
        }
    }

    private void GetCommentsApi() {
        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", sharedpreferences.getString("ProductID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Start", sizeofArray);
        body.put("BlockedUserType", sharedpreferences.getString("LastState", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETPRODUCTCOMMENTS, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Productcomments", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            commentsArray.clear();
                            JSONArray comments = jsonObject.getJSONArray("comments");

                            if (comments.length() > 3) {
                                for (int i = 0; i < 3; i++) {
                                    JSONObject obj = comments.getJSONObject(i);

                                    String Comment = obj.getString("Comment");
                                    String CreatedAt = obj.getString("CreatedAt");
                                    String CompressedImage = obj.getString("CompressedImage");
                                    String BoothCompressedImage = obj.getString("CompressedBoothImage");
                                    String UserName = obj.getString("UserName");
                                    String BoothUserName = obj.getString("BoothUserName");
                                    String CityTitle = obj.getString("CityTitle");
                                    String userID = obj.getString("UserID");
                                    String ProductCommentID = obj.getString("ProductCommentID");
                                    String CommentedAs = obj.getString("CommentedAs");
                                    ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                    JSONArray MentionedUsersInfo = obj.getJSONArray("MentionedUsersInfo");
                                    for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                        JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                        String UserID = object.getString("UserID");
                                        String FullName = object.getString("FullName");
                                        String MentionedName = object.getString("MentionedName");
                                        String MentionedUserType = object.getString("MentionedUserType");

                                        mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                                    }

                                    commentsArray.add(new CommentsModel(ProductCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));
                                }
                            } else {
                                for (int i = 0; i < comments.length(); i++) {
                                    JSONObject obj = comments.getJSONObject(i);

                                    String Comment = obj.getString("Comment");
                                    String CreatedAt = obj.getString("CreatedAt");
                                    String CompressedImage = obj.getString("CompressedImage");
                                    String BoothCompressedImage = obj.getString("CompressedBoothImage");
                                    String UserName = obj.getString("UserName");
                                    String BoothUserName = obj.getString("BoothUserName");
                                    String CityTitle = obj.getString("CityTitle");
                                    String userID = obj.getString("UserID");
                                    String ProductCommentID = obj.getString("ProductCommentID");
                                    String CommentedAs = obj.getString("CommentedAs");
                                    ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                    JSONArray MentionedUsersInfo = obj.getJSONArray("MentionedUsersInfo");
                                    for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                        JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                        String UserID = object.getString("UserID");
                                        String FullName = object.getString("FullName");
                                        String MentionedName = object.getString("MentionedName");
                                        String MentionedUserType = object.getString("MentionedUserType");

                                        mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                                    }

                                    commentsArray.add(new CommentsModel(ProductCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));
                                }
                            }

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this, RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManager);
                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(sharedpreferences.getString("ProductID", " "), "product",ProductDetailsActivity.this, commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });

//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(ProductDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
//                            commentsAdapter.notifyDataSetChanged();


                        } else {


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initilizeViews() {
        viewPager = findViewById(R.id.pro_viewpager);
        commentsRecycler = findViewById(R.id.commentsRecycler);
        similar_products_recycler = findViewById(R.id.similar_products_recycler);
        addToCart = findViewById(R.id.addtoCart);
        indicatorPager = findViewById(R.id.indicator);
        product_detail_swipe_reload = findViewById(R.id.product_detail_swipe_reload);

        boothImage = findViewById(R.id.boothImg);
        username = findViewById(R.id.username);
        time = findViewById(R.id.time);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        likesCount = findViewById(R.id.likesCount);
        commentCount = findViewById(R.id.commentCount);
        brandName = findViewById(R.id.brandName);
        productCondition = findViewById(R.id.productCondition);
        deliveryTime = findViewById(R.id.deliveryTime);
        contactHours = findViewById(R.id.contactHours);
        shippingCost = findViewById(R.id.shippingCost);
        productDetails = findViewById(R.id.productDetails);
        cityName = findViewById(R.id.cityName);
        more = findViewById(R.id.more);
        repost_img = findViewById(R.id.repost_img);
        shareLinear = findViewById(R.id.shareLinear);
        commentsCountLinear = findViewById(R.id.commentsCountLinear);
        wishListLayout = findViewById(R.id.wishListLayout);
        productCategory = findViewById(R.id.productCategory);
        mainPrice = findViewById(R.id.mainPrice);
        likeButton = findViewById(R.id.likeIcon);
        wishListBtn = findViewById(R.id.wishListBtn);
        addToCartLayout = findViewById(R.id.addToCartLayout);
        similar_products_layout = findViewById(R.id.similar_products_layout);
        blurring = findViewById(R.id.blurring);
        nestedScroll = findViewById(R.id.nestedScroll);
        showing_commentscunt = findViewById(R.id.showing_commentscunt);
        writeacomment = findViewById(R.id.writecomment);
        viewpager_parent = findViewById(R.id.viewpager_parent);
        ratingBar = findViewById(R.id.productRatingBar);
        viewsCount = findViewById(R.id.viewsCount);
        out_of_stock = findViewById(R.id.out_of_stock);
        returnPolicyText = findViewById(R.id.returnPolicyText);
    }

    private void productDetailsApiCall() {

        CustomLoader.showDialog(ProductDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.PRODUCT_DETAILS + sharedpreferences.getString("UserID", " ") + "&ProductID=" + sharedpreferences.getString("ProductID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Proresult", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            product_detail_swipe_reload.setRefreshing(false);

                            JSONObject user_info = jsonObject.getJSONObject("product");
                            JSONArray similar_products = jsonObject.getJSONArray("similar_products");

                            String About = user_info.getString("About");
                            String BoothCoverImage = user_info.getString("BoothCoverImage");
                            String BoothImage = user_info.getString("BoothImage");
                            String BoothName = user_info.getString("BoothName");
                            String BoothType = user_info.getString("BoothType");
                            String CategoryID = user_info.getString("CategoryID");
                            String CategoryName = user_info.getString("CategoryName");
                            String CityID = user_info.getString("CityID");
                            String CityName = user_info.getString("CityName");
                            commentscount = user_info.getString("CommentCount");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String CreatedAt = user_info.getString("CreatedAt");
                            String CreatedBy = user_info.getString("CreatedBy");
                            String Currency = user_info.getString("Currency");
                            String CurrencySymbol = user_info.getString("CurrencySymbol");
                            String DeliveryCharges = user_info.getString("DeliveryCharges");
                            String DeliveryTime = user_info.getString("DeliveryTime");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String DeviceType = user_info.getString("DeviceType");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String Hide = user_info.getString("Hide");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsActive = user_info.getString("IsActive");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            String LikesCount = user_info.getString("LikesCount");
                            String Mobile = user_info.getString("Mobile");
                            String Notification = user_info.getString("Notification");
                            String OnlineStatus = user_info.getString("OnlineStatus");
                            String OS = user_info.getString("OS");
                            String OTP = user_info.getString("OTP");
                            String OutOfStock = user_info.getString("OutOfStock");
                            String Password = user_info.getString("Password");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String ProductBrandName = user_info.getString("ProductBrandName");
                            String ProductDescription = user_info.getString("ProductDescription");
                            ProductID = user_info.getString("ProductID");
                            String ProductImages = user_info.getString("ProductImages");
                            String ProductPrice = user_info.getString("ProductPrice");
                            String ProductTextID = user_info.getString("ProductTextID");
                            String ProductType = user_info.getString("ProductType");
                            String ProductVideo = user_info.getString("ProductVideo");
                            String ProductVideoThumbnail = user_info.getString("ProductVideoThumbnail");
                            String RoleID = user_info.getString("RoleID");
                            String SortOrder = user_info.getString("SortOrder");
                            String SubCategoryName = user_info.getString("SubCategoryName");
                            String SubSubCategoryName = user_info.getString("SubSubCategoryName");
                            String SystemLanguageID = user_info.getString("SystemLanguageID");
                            String Title = user_info.getString("Title");
                            String UpdatedAt = user_info.getString("UpdatedAt");
                            String UpdatedBy = user_info.getString("UpdatedBy");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");
                            String UserTextID = user_info.getString("UserTextID");
                            String Verification = user_info.getString("Verification");
                            IsLiked = user_info.getString("IsLiked");
                            String IsPromoted = user_info.getString("IsPromoted");
                            IsInWishlist = user_info.getString("IsInWishlist");
                            String ProductAverageRating = user_info.getString("ProductAverageRating");
                            String ProductRatingsCount = user_info.getString("ProductRatingsCount");
                            String ViewsCount = user_info.getString("ViewsCount");
                            String IsPromotionApproved = user_info.getString("IsPromotionApproved");
                            String IsPromotedProduct = user_info.getString("IsPromotedProduct");
                            String ProductReturnPolicy = user_info.getString("ProductReturnPolicy");

                            if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                addToCart.setVisibility(View.GONE);
                                out_of_stock.setVisibility(View.GONE);
                            } else if (sharedpreferences.getString("LastState", "").equals("user")) {
                                if (OutOfStock.equals("0")) {
                                    addToCartLayout.setBackgroundColor(getResources().getColor(R.color.orange));
                                    addToCart.setVisibility(View.VISIBLE);
                                    out_of_stock.setVisibility(View.GONE);
                                } else if (OutOfStock.equals("1")) {
                                    addToCartLayout.setBackgroundColor(getResources().getColor(R.color.out_of_stock));
                                    addToCart.setVisibility(View.GONE);
                                    out_of_stock.setVisibility(View.VISIBLE);
                                }
                            }

                            if (sharedpreferences.getString("LastState", "").equals("booth")){
                                if (UserID.equals(sharedpreferences.getString("UserID", ""))){
                                    blurring.setVisibility(View.VISIBLE);
                                }else {
                                    blurring.setVisibility(View.GONE);
                                }
                            }

//                            if (IsPromotionApproved.equals("1")) {
//                                repost_img.setVisibility(View.VISIBLE);
//                            } else {
//                                repost_img.setVisibility(View.GONE);
//                            }

                            similarProducts = new ArrayList<>();
                            for (int i = 0; i < similar_products.length(); i++) {
                                JSONObject similarObject = similar_products.getJSONObject(i);

                                String similarProductID = similarObject.getString("ProductID");
                                String similarUserID = similarObject.getString("UserID");
                                String similarCategoryID = similarObject.getString("CategoryID");
                                String similarTitle = similarObject.getString("Title");
                                String similarBoothUserName = similarObject.getString("BoothUserName");
                                String similarBoothName = similarObject.getString("BoothName");
                                String similarProductImage = similarObject.getString("ProductImage");

                                similarProducts.add(new SimilarProductsModel(similarProductID, similarUserID, similarCategoryID,
                                        similarTitle, similarBoothUserName, similarBoothName, similarProductImage));
                            }

                            ratingBar.setRating(Float.valueOf(ProductAverageRating));

                            viewsCount.setText("(" + ViewsCount + " " + "Clicks" + ")");

                            BoothID = UserID;
                            userIdMine = UserID;

                            if (sharedpreferences.getString("LastState", "").equals("user")) {
                                likeButton.setOnLikeListener(new OnLikeListener() {
                                    @Override
                                    public void liked(LikeButton likeButton) {
                                        like();
                                    }

                                    @Override
                                    public void unLiked(LikeButton likeButton) {
                                        unlike();
                                    }
                                });
                            } else {
                                likeButton.setEnabled(false);
                            }

                            if (sharedpreferences.getString("UserID", "").equals(UserID)) {
                                addToCart.setVisibility(View.GONE);
                                out_of_stock.setVisibility(View.GONE);
                                setMargins(nestedScroll, 0, 0, 0, 0);
                                wishListBtn.setVisibility(View.GONE);
//                                productPrice.setText("Promote");
                                wishListLayout.setVisibility(View.GONE);
//                                addToCartLayout.setVisibility(View.GONE);
                            }

                            if (IsLiked.equals("1")) {
                                likeButton.setLiked(Boolean.TRUE);
                            } else {
                                likeButton.setLiked(Boolean.FALSE);
                            }

                            if (IsInWishlist.equals("1")) {
                                wishListBtn.setLiked(Boolean.TRUE);
                            } else {
                                wishListBtn.setLiked(Boolean.FALSE);
                            }

                            if (!UserID.equals(sharedpreferences.getString("UserID", " "))) {
                                more.setVisibility(View.GONE);
                            }

                            boothImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, BoothProfileActivity.class);
                                    mEditor.putString("Booth", "Other").commit();
                                    intent.putExtra("OtherUserID", UserID);
                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation(ProductDetailsActivity.this,
                                                    boothImage,
                                                    ViewCompat.getTransitionName(boothImage));
                                    startActivity(intent, options.toBundle());
                                }
                            });

                            username.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ProductDetailsActivity.this, BoothProfileActivity.class);
                                    mEditor.putString("Booth", "Other").commit();
                                    intent.putExtra("OtherUserID", UserID);
                                    startActivity(intent);
                                }
                            });

                            SpannableString span1 = new SpannableString(Currency);
                            span1.setSpan(new AbsoluteSizeSpan(25), 0, Currency.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            span1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, Currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            float shippingcharges = Float.valueOf(DeliveryCharges);

                            SpannableString span2 = new SpannableString(String.valueOf(shippingcharges));
                            span2.setSpan(new AbsoluteSizeSpan(30), 0, String.valueOf(shippingcharges).length(), SPAN_INCLUSIVE_INCLUSIVE);

                            CharSequence finalText = TextUtils.concat(span2, " ", span1);

                            shippingCost.setText(String.valueOf(finalText));

                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothImage).placeholder(R.drawable.user).into(boothImage);
                            Log.e("BoothImg", Constants.URL.IMG_URL + CompressedBoothImage);
                            username.setText("@" + BoothUserName);
                            productName.setText(Title);
                            // productPrice.setText(Currency + " " + ProductPrice);
                            likesCount.setText(LikesCount);
                            commentCount.setText("(" + ProductRatingsCount + ")");
                            brandName.setText(ProductBrandName);
                            productCondition.setText(ProductType);
                            deliveryTime.setText(DeliveryTime);
                            contactHours.setText(BaseClass.TimeStampToTime(ContactTimeFrom) + " - " + BaseClass.TimeStampToTime(ContactTimeTo));
                            // shippingCost.setText(Currency + " " + DeliveryCharges);
                            productDetails.setText(ProductDescription);
                            returnPolicyText.setText(ProductReturnPolicy);
                            cityName.setText(CityName);
                            time.setText(TimeAgo.getTimeAgo(Long.parseLong(CreatedAt)));
                            productCategory.setText(CategoryName + " / " + SubCategoryName + " / " + SubSubCategoryName);
                            mainPrice.setText(ProductPrice + " " + Currency);

                            productImagesData = new ArrayList<>();
                            JSONArray productImages = user_info.getJSONArray("ProductImages");
                            for (int j = 0; j < productImages.length(); j++) {
                                JSONObject imagesObj = productImages.getJSONObject(j);
                                String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                String ProductImage = imagesObj.getString("ProductImage");

                                productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                            }
                            HomeHomePagerAdapter homeHomePagerAdapter;
                            if (ProductVideo.equals("")) {
                                homeHomePagerAdapter = new HomeHomePagerAdapter("ProductDetail", ProductDetailsActivity.this, productImagesData);
                            } else {
                                productImagesData.add(new ProductImagesData("", ""));
                                homeHomePagerAdapter = new HomeHomePagerAdapter("ProductDetail", ProductDetailsActivity.this, productImagesData, ProductVideo, ProductVideoThumbnail);
                            }
                            viewPager.setClipToPadding(false);
                            viewPager.setOffscreenPageLimit(2);
                            viewPager.setPadding(0, 0, 0, 0);
                            viewPager.setPageMargin(0);
                            viewPager.setAdapter(homeHomePagerAdapter);

                            if (productImagesData.size() == 1) {
                                if (ProductVideo.equals("")) {
                                    indicatorPager.setVisibility(View.GONE);
                                }
                            }

                            indicatorPager.setViewPager(viewPager);

                            if (similarProducts.isEmpty()) {
                                similar_products_layout.setVisibility(View.GONE);
                            } else {
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this, RecyclerView.HORIZONTAL, false);
                                similar_products_recycler.setLayoutManager(layoutManager);
                                SimilarProductAdapter similarProductAdapter = new SimilarProductAdapter(similarProducts, ProductDetailsActivity.this);
                                similar_products_recycler.setAdapter(similarProductAdapter);
                            }

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(ProductDetailsActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == wishListLayout) {
            if (sharedpreferences.getString("LastState", "").equals("user")) {
                if (IsInWishlist.equals("1")) {
                    RemoveFromWishList();
                    wishListBtn.setLiked(Boolean.TRUE);
                } else {
                    AddtoWishList();
                    wishListBtn.setLiked(Boolean.FALSE);
                }
            }
        } else if (v == commentsCountLinear) {

            if (sharedpreferences.getString("LastState", "").equals("user")) {
                Intent intent = new Intent(ProductDetailsActivity.this, RatingAndReviewsActivity.class);
                intent.putExtra("Activity", "Product");
                intent.putExtra("ProductID", ProductID);
                startActivity(intent);
            }

        } else if (v == shareLinear) {
            if (sharedpreferences.getString("LastState", "").equals("user")) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Download App", "https://play.google.com/store/apps/details?id=com.schopfen.Booth");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.linkcopiedtoclipboard), Toast.LENGTH_SHORT).show();
            }

        } else if (v == more) {
            if (sharedpreferences.getString("LastState", "").equals("user")) {

            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                final PopupMenu projMangMore = new PopupMenu(ProductDetailsActivity.this, v);
                projMangMore.getMenuInflater().inflate(R.menu.delet_cart_item_menu, projMangMore.getMenu());

                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.deleteproduct:
                                final Dialog deletedialog = new Dialog(ProductDetailsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                deletedialog.setContentView(R.layout.delete_cart_item_dialog);
                                Button yesBtn = deletedialog.findViewById(R.id.btn_yes);
                                Button noBtn = deletedialog.findViewById(R.id.btn_no);

                                deletedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                deletedialog.show();

                                noBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deletedialog.dismiss();
                                    }
                                });

                                yesBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteProductApiCall();
                                        deletedialog.dismiss();
                                    }
                                });

                                break;

                            case R.id.editproduct:
                                Intent intent = new Intent(ProductDetailsActivity.this, AddProductActivity.class);
                                intent.putExtra("ProID", ProductID);
                                startActivity(intent);
                                break;
                            case R.id.repostProduct:
                                PromoteProduct(ProductID);
                                break;
                        }
                        return false;
                    }
                });
                projMangMore.show();
            }
            //inProgressToast();
        } else if (v == addToCart) {
            AddToCartApi();
        } else if (v == writeacomment) {
            if (sharedpreferences.getString("LastState", "").equals("user")) {
                mEditor.putString("ProductID", ProductID);
                Intent intent = new Intent(ProductDetailsActivity.this, ProductComments.class);
                intent.putExtra("COUNT", commentscount);
                startActivity(intent);
            } else {
                if (sharedpreferences.getString("UserID", "").equals(userIdMine)) {
                    mEditor.putString("ProductID", ProductID).commit();
                    Intent intent = new Intent(ProductDetailsActivity.this, ProductComments.class);
                    intent.putExtra("COUNT", commentscount);
                    startActivity(intent);
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Booths can't comment", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void PromoteProduct(String productID) {
        CustomLoader.showDialog(ProductDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ProductID", productID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("AddtoCartBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.PROMOTEMYPRODUCT, ProductDetailsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("promoteMy", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }

    private void initializeClickListeners() {
        wishListLayout.setOnClickListener(this);
        commentsCountLinear.setOnClickListener(this);
        shareLinear.setOnClickListener(this);
        more.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        writeacomment.setOnClickListener(this);
    }

    private void inProgressToast() {
        Toast.makeText(ProductDetailsActivity.this, getResources().getString(R.string.workingOnIt), Toast.LENGTH_LONG).show();
    }

    private void like() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "like");
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("likedResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            IsLiked = "1";
                            String LikesCount = jsonObject.getString("LikesCount");
                            likesCount.setText(LikesCount);
                        } else {
                            likeButton.setLiked(Boolean.FALSE);
                            IsLiked = "0";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void unlike() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "dislike");
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("likedResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            IsLiked = "0";
                            String LikesCount = jsonObject.getString("LikesCount");
                            likesCount.setText(LikesCount);
                        } else {
                            likeButton.setLiked(Boolean.TRUE);
                            IsLiked = "1";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void AddToCartApi() {

        CustomLoader.showDialog(ProductDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("BoothID", BoothID);
        body.put("ProductID", ProductID);
        body.put("ProductQuantity", "1");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("AddtoCartBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.AddToCart, ProductDetailsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddToCartResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
//                            Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();

                            CustomLoader.dialog.dismiss();

                            final Dialog alertdialog = new Dialog(ProductDetailsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            alertdialog.setContentView(R.layout.continue_or_checkout_dialog);
                            LinearLayout continueBtn = alertdialog.findViewById(R.id.continue_shop);
                            LinearLayout proceedBtn = alertdialog.findViewById(R.id.proceed_checkout);

                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.setCancelable(false);
                            alertdialog.show();

                            continueBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertdialog.dismiss();
                                }
                            });

                            proceedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(ProductDetailsActivity.this, ShoppingCartActivity.class));
                                    alertdialog.dismiss();
                                }
                            });

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }

    private void AddtoWishList() {
        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "add");
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addToWishlist, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("AddWishListResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (status == 200) {
                            IsInWishlist = "1";
                            wishListBtn.setLiked(Boolean.TRUE);
                        } else {
                            IsInWishlist = "0";
                            wishListBtn.setLiked(Boolean.FALSE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void RemoveFromWishList() {
        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "remove");
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addToWishlist, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("RemoveWishListResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        if (status == 200) {
                            IsInWishlist = "0";
                            wishListBtn.setLiked(Boolean.FALSE);
                        } else {
                            IsInWishlist = "1";
                            wishListBtn.setLiked(Boolean.TRUE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void deleteProductApiCall() {

        CustomLoader.showDialog((Activity) ProductDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deleteProduct, ProductDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                Log.e("Result", result);
                Log.e("ResultError", ERROR);

                if (ERROR.isEmpty()) {

                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(ProductDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                            finish();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
