package com.schopfen.Booth.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.ProductComments;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Adapters.Answers_Details_Adapter;
import com.schopfen.Booth.Adapters.HomeHomePagerAdapter;
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

public class BoothProfile_Promotions_Fragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView commentsRecycler;
    ArrayList<CommentsModel> commentsArray = new ArrayList<>();
    Button addToCart;
    ViewPager viewPager;
    CircleIndicatorPager indicatorPager;
    ArrayList<ProductImagesData> productImagesData;
    ArrayList<ProductsData> productsData = new ArrayList<>();

    CircleImageView boothImage;
    TextView username, time, productName, likesCount, commentCount, brandName, productCondition, deliveryTime, contactHours, cityName,
            shippingCost, productDetails, productCategory, mainPrice, showing_commentscunt, writeacomment;
    ImageView more;
    LinearLayout shareLinear, commentsCountLinear, wishListLayout, addToCartLayout;
    LikeButton likeButton, wishListBtn;
    NestedScrollView nestedScroll;

    String IsLiked, commentscount;
    String ProductID, BoothID, IsInWishlist;
    RelativeLayout viewpager_parent;

    String cartprocedure = "";
    String sizeofArray = "0";
    String Path;
    String otherID;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
//            PromoteProductDetailsApiCall();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotions_boothprofile,container,false);

        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        initilizeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Path = sharedpreferences.getString("PromotedPath", " ");

        if (Path.equals("Activity")){
            otherID = BoothProfileActivity.otherID;
        }else if (Path.equals("Fragment")){
            otherID = sharedpreferences.getString("UserID", " ");
        }

        initializeClickListeners();
//        PromoteProductDetailsApiCall();

        wishListLayout.setVisibility(View.GONE);
//        nestedScroll.setFocusableInTouchMode(true);
//        nestedScroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

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
    }

    private void initilizeViews(View view) {
        viewPager = view.findViewById(R.id.pro_viewpager);
        commentsRecycler = view.findViewById(R.id.commentsRecycler);
        addToCart = view.findViewById(R.id.addtoCart);
        indicatorPager = view.findViewById(R.id.indicator);

        boothImage = view.findViewById(R.id.boothImg);
        username = view.findViewById(R.id.username);
        time = view.findViewById(R.id.time);
        productName = view.findViewById(R.id.productName);
        likesCount = view.findViewById(R.id.likesCount);
        commentCount = view.findViewById(R.id.commentCount);
        brandName = view.findViewById(R.id.brandName);
        productCondition = view.findViewById(R.id.productCondition);
        deliveryTime = view.findViewById(R.id.deliveryTime);
        contactHours = view.findViewById(R.id.contactHours);
        shippingCost = view.findViewById(R.id.shippingCost);
        productDetails = view.findViewById(R.id.productDetails);
        cityName = view.findViewById(R.id.cityName);
        more = view.findViewById(R.id.more);
        shareLinear = view.findViewById(R.id.shareLinear);
        commentsCountLinear = view.findViewById(R.id.commentsCountLinear);
        wishListLayout = view.findViewById(R.id.wishListLayout);
        productCategory = view.findViewById(R.id.productCategory);
        mainPrice = view.findViewById(R.id.mainPrice);
        likeButton = view.findViewById(R.id.likeIcon);
        wishListBtn = view.findViewById(R.id.wishListBtn);
        addToCartLayout = view.findViewById(R.id.addToCartLayout);
        nestedScroll = view.findViewById(R.id.nestedScroll);
        showing_commentscunt = view.findViewById(R.id.showing_commentscunt);
        writeacomment = view.findViewById(R.id.writecomment);
        viewpager_parent = view.findViewById(R.id.viewpager_parent);
    }
    private void PromoteProductDetailsApiCall() {

//        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.PROMOTEDPRODUCT + "?UserID=" + sharedpreferences.getString("UserID", " ") + "&BoothID=" + otherID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
//                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("promotedProduct", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject user_info = jsonObject.getJSONObject("product");

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
                            String UserTextID = user_info.getString("UserTextID");
                            String Verification = user_info.getString("Verification");
                            IsLiked = user_info.getString("IsLiked");
                            String IsPromoted = user_info.getString("IsPromoted");
                            IsInWishlist = user_info.getString("IsInWishlist");

                            commentsArray.clear();
                            JSONArray Comments = user_info.getJSONArray("Comments");
                            for (int i = 0; i < Comments.length(); i++){
                                JSONObject obj = Comments.getJSONObject(i);

                                String Comment = obj.getString("Comment");
                                String CreatedAtC = obj.getString("CreatedAt");
                                String CompressedImageC = obj.getString("CompressedImage");
                                String BoothCompressedImage = obj.getString("CompressedBoothImage");
                                String UserNameC = obj.getString("UserName");
                                String BoothUserName = obj.getString("BoothUserName");
                                String CityTitle = obj.getString("CityTitle");
                                String userID = obj.getString("UserID");
                                String ProductCommentID = obj.getString("ProductCommentID");
                                String CommentedAs = obj.getString("CommentedAs");
                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = obj.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++){
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserIDinner = object.getString("UserID");
                                    String FullNameinner = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserIDinner, FullNameinner, MentionedName, MentionedUserType));

                                }

                                commentsArray.add(new CommentsModel(ProductCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAtC, CompressedImageC, UserNameC, CityTitle, mentionedUsersInfos, BoothUserName));
                            }

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            BoothID = UserID;

                            if (sharedpreferences.getString("UserID", "").equals(UserID)) {
                                addToCartLayout.setVisibility(View.GONE);
                                setMargins(nestedScroll, 0, 0, 0, 0);
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
                                    Intent intent = new Intent(getActivity(), BoothProfileActivity.class);
                                    mEditor.putString("Booth", "Other").commit();
                                    intent.putExtra("OtherUserID", UserID);
                                    ActivityOptionsCompat options = ActivityOptionsCompat.
                                            makeSceneTransitionAnimation(getActivity(),
                                                    boothImage,
                                                    ViewCompat.getTransitionName(boothImage));
                                    startActivity(intent, options.toBundle());
                                }
                            });

                            SpannableString span1 = new SpannableString(Currency);
                            span1.setSpan(new AbsoluteSizeSpan(25), 0, Currency.length(), SPAN_INCLUSIVE_INCLUSIVE);
                            span1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange)), 0, Currency.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            float shippingcharges = Float.valueOf(ProductPrice) + Float.valueOf(DeliveryCharges);

                            SpannableString span2 = new SpannableString(String.valueOf(shippingcharges));
                            span2.setSpan(new AbsoluteSizeSpan(30), 0, String.valueOf(shippingcharges).length(), SPAN_INCLUSIVE_INCLUSIVE);

                            CharSequence finalText = TextUtils.concat(span1, " ", span2);

                            shippingCost.setText(String.valueOf(finalText));

                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothImage).into(boothImage);
                            Log.e("BoothImg", Constants.URL.IMG_URL + CompressedBoothImage);
                            username.setText("@" + UserName);
                            productName.setText(Title);
                            // productPrice.setText(Currency + " " + ProductPrice);
                            likesCount.setText(LikesCount);
                            commentCount.setText(commentscount);
                            brandName.setText(ProductBrandName);
                            productCondition.setText(ProductType);
                            deliveryTime.setText(DeliveryTime);
                            contactHours.setText(BaseClass.TimeStampToTime(ContactTimeFrom) + " - " + BaseClass.TimeStampToTime(ContactTimeTo));
                            // shippingCost.setText(Currency + " " + DeliveryCharges);
                            productDetails.setText(ProductDescription);
                            cityName.setText(CityName);
                            time.setText(TimeAgo.getTimeAgo(Long.parseLong(CreatedAt)));
                            productCategory.setText(CategoryName + " / " + SubCategoryName + " / " + SubSubCategoryName);
                            mainPrice.setText(Currency + " " + ProductPrice);

                            productImagesData = new ArrayList<>();
                            JSONArray productImages = user_info.getJSONArray("ProductImages");
                            for (int j = 0; j < productImages.length(); j++) {
                                JSONObject imagesObj = productImages.getJSONObject(j);
                                String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                String ProductImage = imagesObj.getString("ProductImage");

                                productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                            }
                            HomeHomePagerAdapter homeHomePagerAdapter;
                            if (ProductVideo.equals("")){
                                homeHomePagerAdapter = new HomeHomePagerAdapter("BoothProfile", getActivity(), productImagesData);
                            }else {
                                homeHomePagerAdapter = new HomeHomePagerAdapter("BoothProfile", getActivity(), productImagesData, ProductVideo, ProductVideoThumbnail);
                            }

                            viewPager.setClipToPadding(false);
                            viewPager.setOffscreenPageLimit(2);
                            viewPager.setPadding(0, 0, 0, 0);
                            viewPager.setPageMargin(0);
                            viewPager.setAdapter(homeHomePagerAdapter);

                            if (productImagesData.size() == 1) {
                                if (ProductVideo.equals("")){
                                    indicatorPager.setVisibility(View.GONE);
                                }
                            }

                            indicatorPager.setViewPager(viewPager);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManager);
                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(ProductID, "promotion", getActivity(), commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });

//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(ProductDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
                            commentsRecycler.setFocusable(false);

                        } else {
//                            CustomLoader.dialog.dismiss();
//                            Toast.makeText(getActivity(), String.valueOf(status), Toast.LENGTH_SHORT).show();
                            if (String.valueOf(status).equals("402")){
                                nestedScroll.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getContext(), ERROR, Toast.LENGTH_SHORT).show();
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
    private void initializeClickListeners() {
        wishListLayout.setOnClickListener(this);
        commentsCountLinear.setOnClickListener(this);
        shareLinear.setOnClickListener(this);
        more.setOnClickListener(this);
        addToCart.setOnClickListener(this);
        writeacomment.setOnClickListener(this);
    }
    private void inProgressToast() {
        Toast.makeText(getActivity(), getResources().getString(R.string.workingOnIt), Toast.LENGTH_LONG).show();
    }
    private void deleteProductApiCall() {

        CustomLoader.showDialog(getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deleteProduct, getActivity(), body, header, new ServerCallback() {
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
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                            finish();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void like() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("Type", "like");
        body.put("ProductID", ProductID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, getActivity(), body, header, new ServerCallback() {
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
                            likesCount.setText(String.valueOf(Integer.parseInt(likesCount.getText().toString()) + 1));
                        } else {
                            likeButton.setLiked(Boolean.FALSE);
                            IsLiked = "0";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
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

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, getActivity(), body, header, new ServerCallback() {
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
                            likesCount.setText(String.valueOf(Integer.parseInt(likesCount.getText().toString()) - 1));
                        } else {
                            likeButton.setLiked(Boolean.TRUE);
                            IsLiked = "1";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
//        if (v == wishListLayout) {
//            if (IsInWishlist.equals("1")){
//                RemoveFromWishList();
//                wishListBtn.setLiked(Boolean.TRUE);
//            }else {
//                AddtoWishList();
//                wishListBtn.setLiked(Boolean.FALSE);
//            }
//        } else
            if (v == commentsCountLinear) {
            nestedScroll.fullScroll(View.FOCUS_DOWN);
        } else if (v == shareLinear) {
            inProgressToast();
        } else if (v == more) {

            final PopupMenu projMangMore = new PopupMenu(getActivity(), v);
            projMangMore.getMenuInflater().inflate(R.menu.delet_cart_item_menu, projMangMore.getMenu());

            projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.deleteproduct:
                            final Dialog deletedialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                            Intent intent = new Intent(getActivity(), AddProductActivity.class);
                            intent.putExtra("ProID",ProductID);
                            startActivity(intent);
                            break;
                    }
                    return false;
                }
            });
            projMangMore.show();
        } else if (v == writeacomment) {
            Intent intent = new Intent(getActivity(), ProductComments.class);
            intent.putExtra("COUNT", commentCount.getText().toString());
            startActivity(intent);
        }
    }
}
