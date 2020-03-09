package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.schopfen.Booth.Adapters.WishlistAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Fragments.HomePeopleFragment;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.Models.WishListEtcData;
import com.schopfen.Booth.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OthersProfileActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView wishlistRecycler, purchasesRecycler, likesRecycler;
    LinearLayout editProfileBtn;
    LinearLayout follow_btn, messageBtn;
    TextView follow_btn_text;
    ImageView pro_image, gifimageView;
    RelativeLayout more;
    TextView fullName, addressText, username, rating_count;
    LinearLayout viewBooths, viewFollowers, categories_count_layout;
    LinearLayout wishlist_linear, likes_linaer, purchases_linear;
    TextView wishlist_count, likes_count, purchases_count;
    LinearLayout ratingLinear;
    LinearLayout wishlist_nodata, likes_nodata, purchases_nodata;
    String UserName;
    String Following;
    String otherUserID;
    String OthersUserresponseID;
    ArrayList<UserDetailsData> userDetailsData = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    ArrayList<String> profileArray = new ArrayList<>();
    ArrayList<WishListEtcData> likesData;
    ArrayList<ProductImagesData> likesImagesData;
    ArrayList<WishListEtcData> wishListData;
    ArrayList<ProductImagesData> wishListImagesData;
    ArrayList<WishListEtcData> purchasesData;
    ArrayList<ProductImagesData> purchaseImagesData;
    int UserFollowingCount;

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
            setContentView(R.layout.activity_other_profile);
            initilizeviews();
            Fresco.initialize(OthersProfileActivity.this);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            otherUserID = getIntent().getStringExtra("OtherUserID");

            if (sharedpreferences.getString("UserID", "").equals(otherUserID)){
                more.setVisibility(View.GONE);
            }else {
                more.setVisibility(View.VISIBLE);
            }
            OtherUserProfileApi();
            getUserOrdersLikesWishlistApiCall();

            follow_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Following.equals("1")) {
                        UnfollowOtherUserApi();
                    } else {
                        FollowOtherUserApi();
                    }
                }
            });

            categories_count_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(OthersProfileActivity.this, SelectedCategoriesList.class);
                    intent.putExtra("state", "user");
                    intent.putExtra("otheruserID", otherUserID);
                    startActivity(intent);
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void initilizeviews() {
        wishlistRecycler = findViewById(R.id.wishlistRecycler);
        purchasesRecycler = findViewById(R.id.purchasesRecycler);
        likesRecycler = findViewById(R.id.likesRecycler);
        editProfileBtn = findViewById(R.id.editProfileBtn);
        pro_image = findViewById(R.id.profile_image);
        gifimageView = findViewById(R.id.gifmain);
        fullName = findViewById(R.id.fullName);
        addressText = findViewById(R.id.address_line);
        username = findViewById(R.id.username);
        rating_count = findViewById(R.id.rating_count);
        follow_btn = findViewById(R.id.follow_btn);
        viewBooths = findViewById(R.id.viewBooths);
        messageBtn = findViewById(R.id.message_btn);
        follow_btn_text = findViewById(R.id.follow_btn_text);
        viewFollowers = findViewById(R.id.viewFollowers);
        categories_count_layout = findViewById(R.id.categories_count_layout);
        more = findViewById(R.id.more);
        likes_linaer = findViewById(R.id.likes_linear);
        purchases_linear = findViewById(R.id.purchases_linear);
        wishlist_count = findViewById(R.id.wishlist_count);
        likes_count = findViewById(R.id.likes_count);
        purchases_count = findViewById(R.id.purchases_count);
        wishlist_linear = findViewById(R.id.wishlist_linear);
        ratingLinear = findViewById(R.id.rating);
        wishlist_nodata = findViewById(R.id.wishlist_nodata);
        likes_nodata = findViewById(R.id.likes_nodata);
        purchases_nodata = findViewById(R.id.purchases_nodata);

        viewBooths.setOnClickListener(this);
        viewFollowers.setOnClickListener(this);
        more.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        ratingLinear.setOnClickListener(this);

    }

    private void FollowOtherUserApi() {

        CustomLoader.showDialog(OthersProfileActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Following", otherUserID);
        body.put("Follow", "1");
        body.put("DeviceType", "Android");
        body.put("Type", "user");
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("followBoothsBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.follow, OthersProfileActivity.this, body, headers, new ServerCallback() {
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
                            HomePeopleFragment.refresh = true;

                            Following = "1";

                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            follow_btn_text.setText(getResources().getString(R.string.unfollow));
//                            followers_count.setText(String.valueOf(Integer.valueOf(followers_count.getText().toString()) + 1));
                        } else {
                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_SHORT).show();
                            String message = jsonObject.getString("message");
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UnfollowOtherUserApi() {

        CustomLoader.showDialog(OthersProfileActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Following", otherUserID);
        body.put("Follow", "0");
        body.put("DeviceType", "Android");
        body.put("Type", "user");
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("followBoothsBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.follow, OthersProfileActivity.this, body, headers, new ServerCallback() {
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

                            HomePeopleFragment.refresh = true;

                            Following = "0";

                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            follow_btn_text.setText(getResources().getString(R.string.follow));
//                            followers_count.setText(String.valueOf(Integer.valueOf(followers_count.getText().toString()) - 1));
                        } else {
                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_SHORT).show();
                            String message = jsonObject.getString("message");
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void ReportUser(){
//        CustomLoader.showDialog(OthersProfileActivity.this);
//
//        Map<String, String> body = new HashMap<String, String>();
//        body.put("UserID", sharedpreferences.getString("UserID", " "));
//        body.put("ReportedUserID", otherUserID);
////        body.put("Type", "user");
////        body.put("OS", Build.VERSION.RELEASE);
//        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//
//        Log.e("ReportUserBody", body.toString());
//
//        HashMap<String, String> headers = new HashMap<String, String>();
//        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
//        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ReportUser, OthersProfileActivity.this, body, headers, new ServerCallback() {
//            @Override
//            public void onSuccess(String result, String ERROR) {
//                if (ERROR.isEmpty()) {
//                    Log.e("ReportUserResp", result);
//                    try {
//                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
//                        int status = jsonObject.getInt("status");
//                        if (status == 200) {
//
//                            String message = jsonObject.getString("message");
//                            CustomLoader.dialog.dismiss();
//
////                            Following = "1";
//
//                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_SHORT).show();
////                            follow_btn.setText(getResources().getString(R.string.unfollow));
//                        } else {
//                            CustomLoader.dialog.dismiss();
////                            String message = jsonObject.getString("message");
////                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
//                        e.printStackTrace();
//                    }
//                } else {
//                    CustomLoader.dialog.dismiss();
//                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    private void BlockUser() {
        CustomLoader.showDialog(OthersProfileActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("BlockedUserID", otherUserID);
        body.put("Type", "block");
        body.put("UserType", sharedpreferences.getString("LastState", ""));
        body.put("BlockedUserType", "user");
//        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("BlockUserBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BlockUser, OthersProfileActivity.this, body, headers, new ServerCallback() {
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
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ReportUser(String reason) {

        CustomLoader.showDialog(OthersProfileActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ReportedUserID", otherUserID);
        body.put("ReportedUserType", "user");
//        body.put("Type", "user");
//        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("ReportReason", reason);

        Log.e("ReportUserBody", body.toString());

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ReportUser, OthersProfileActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();

                if (ERROR.isEmpty()) {
                    Log.e("ReportUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void OtherUserProfileApi() {
        CustomLoader.showDialog(OthersProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + otherUserID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&Type=user", OthersProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OtherUserDetail", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
                            float BoothAverageRating = user_info.getInt("BoothAverageRating");
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
                            UserFollowingCount = user_info.getInt("UserFollowingCount");
                            String UserID = user_info.getString("UserID");
                            UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");

                            Following = user_info.getString("Following");

                            OthersUserresponseID = UserID;
                            float UserAverageRating = user_info.getInt("UserAverageRating");
                            rating_count.setText(String.valueOf(UserAverageRating));

                            if (sharedpreferences.getString("LastState", "").equals("user")) {
                                if (sharedpreferences.getString("UserID", "").equals(UserID)) {
                                    messageBtn.setVisibility(View.GONE);
                                    follow_btn.setVisibility(View.GONE);
                                }
                            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                follow_btn.setVisibility(View.GONE);
                                if (sharedpreferences.getString("UserID", "").equals(UserID)) {
                                    messageBtn.setVisibility(View.GONE);
                                    follow_btn.setVisibility(View.GONE);
                                }
                            }


                            if (Following.equals("1")) {
                                follow_btn_text.setText(getResources().getString(R.string.unfollow));
                            } else {
                                follow_btn_text.setText(getResources().getString(R.string.follow));
                            }

                            mEditor.putString("OtherUserID", UserID).apply();

                            pro_image.setVisibility(View.VISIBLE);
                            Glide.with(OthersProfileActivity.this).load(Constants.URL.IMG_URL + Image).apply(RequestOptions.centerCropTransform().placeholder(R.drawable.user)).into(pro_image);
                            gifimageView.setVisibility(View.GONE);
                            fullName.setText(FullName);
                            username.setText("@" + UserName);
                            addressText.setText(CityTitle);

                            profileArray.clear();
                            profileArray.add(Constants.URL.IMG_URL + Image);
                            pro_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new ImageViewer.Builder(OthersProfileActivity.this, profileArray)
                                            .setStartPosition(0)
                                            .show();
                                }
                            });
//                            categories_count.setText(String.valueOf(UserCategoriesCount));
//                            booth_count.setText(String.valueOf(UserFollowedBooths));
//                            followers_count.setText(String.valueOf(UserFollowersCount));

                            userDetailsData.add(new UserDetailsData(About, AuthToken, BoothAverageRating, BoothCategoriesCount, BoothCoverImage, BoothFollowersCount, BoothImage, BoothName,
                                    BoothType, CityID, CityTitle, CompressedBoothCoverImage, CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays,
                                    ContactTimeFrom, ContactTimeTo, CoverImage, DeviceToken, Email, FullName, Gender, HideContactNo, Image, IsEmailVerified, IsMobileVerified,
                                    IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch, PaymentAccountHolderName, PaymentAccountNumber, UserCategoriesCount, UserFollowedBooths,
                                    UserFollowersCount, UserID, UserName, BoothUserName));
                            CustomLoader.dialog.dismiss();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == viewBooths) {
            startActivity(new Intent(OthersProfileActivity.this, FollowingsActivity.class));
        } else if (v == viewFollowers) {
            mEditor.putString("typetype", "user").commit();
            mEditor.putString("OtherUserID", otherUserID).commit();
            mEditor.putString("followers", String.valueOf(userDetailsData.get(0).getUserFollowersCount())).commit();
            mEditor.putString("followings", String.valueOf(UserFollowingCount)).commit();
            startActivity(new Intent(OthersProfileActivity.this, FollowersNewActivity.class));
        } else if (v == more) {

            final PopupMenu moreMenu = new PopupMenu(OthersProfileActivity.this, v);
            moreMenu.getMenuInflater().inflate(R.menu.other_user_menu, moreMenu.getMenu());

            moreMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.report:
                            final Dialog reportdialog = new Dialog(OthersProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
        } else if (v == messageBtn) {
            Intent intent = new Intent(OthersProfileActivity.this, ChatActivity.class);
            intent.putExtra("OthersprofileID", OthersUserresponseID);
            intent.putExtra("UserName", UserName);
            intent.putExtra("usertype", sharedpreferences.getString("LastState", ""));
            intent.putExtra("othertype", "user");
            startActivity(intent);
        } else if (v == ratingLinear) {
            Intent intent = new Intent(OthersProfileActivity.this, RatingAndReviewsActivity.class);
            intent.putExtra("userID", OthersUserresponseID);
            intent.putExtra("Activity", "Other");
            startActivity(intent);
        }
    }

    private void getUserOrdersLikesWishlistApiCall() {

//        CustomLoader.showDialog(getActivity());
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.WishListLikesETC + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + otherUserID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, OthersProfileActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("WishListEtcResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            likesData = new ArrayList<>();
                            JSONArray likesArray = jsonObject.getJSONArray("liked_products");
                            for (int i = 0; i < likesArray.length(); i++) {
                                JSONObject likesObject = likesArray.getJSONObject(i);
                                String BoothID = likesObject.getString("BoothID");
                                String BoothName = likesObject.getString("BoothName");
                                String ProductID = likesObject.getString("ProductID");
                                String ProductLikeID = likesObject.getString("ProductLikeID");
                                String Title = likesObject.getString("Title");
                                String UserID = likesObject.getString("UserID");

                                likesImagesData = new ArrayList<>();
                                JSONArray imagesArray = likesObject.getJSONArray("ProductImages");
                                for (int j = 0; j < imagesArray.length(); j++) {
                                    JSONObject imagesObject = imagesArray.getJSONObject(j);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    likesImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                likesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, likesImagesData));
                            }

                            RecyclerView.LayoutManager likesLayoutManager = new LinearLayoutManager(OthersProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            likesRecycler.setLayoutManager(likesLayoutManager);
                            WishlistAdapter likesAdapter = new WishlistAdapter(OthersProfileActivity.this, likesData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", likesData.get(position).getProductID()).commit();
                                    startActivity(new Intent(OthersProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });
                            if (likesData.isEmpty()) {
                                likes_nodata.setVisibility(View.VISIBLE);
                                likesRecycler.setVisibility(View.GONE);
                            } else {
                                likes_nodata.setVisibility(View.GONE);
                                likesRecycler.setVisibility(View.VISIBLE);
                                likes_count.setText("(" + String.valueOf(likesData.size()) + " " + "Items" + ")");
                                likesRecycler.setAdapter(likesAdapter);
                                likesAdapter.notifyDataSetChanged();
                            }

                            wishListData = new ArrayList<>();
                            JSONArray wishListArray = jsonObject.getJSONArray("wishlist_products");
                            for (int k = 0; k < wishListArray.length(); k++) {
                                JSONObject wishListObject = wishListArray.getJSONObject(k);
                                String BoothID = wishListObject.getString("BoothID");
                                String BoothName = wishListObject.getString("BoothName");
                                String ProductID = wishListObject.getString("ProductID");
                                String ProductLikeID = wishListObject.getString("UserWishlistID");
                                String Title = wishListObject.getString("Title");
                                String UserID = wishListObject.getString("UserID");

                                wishListImagesData = new ArrayList<>();
                                JSONArray imagesArray = wishListObject.getJSONArray("ProductImages");
                                for (int l = 0; l < imagesArray.length(); l++) {
                                    JSONObject imagesObject = imagesArray.getJSONObject(l);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    wishListImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                wishListData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, wishListImagesData));
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OthersProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            wishlistRecycler.setLayoutManager(layoutManager);
                            WishlistAdapter wishlistAdapter = new WishlistAdapter(OthersProfileActivity.this, wishListData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", wishListData.get(position).getProductID()).commit();
                                    startActivity(new Intent(OthersProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });
                            if (wishListData.isEmpty()) {
                                wishlist_nodata.setVisibility(View.VISIBLE);
                                wishlistRecycler.setVisibility(View.GONE);
                            } else {
                                wishlist_nodata.setVisibility(View.GONE);
                                wishlistRecycler.setVisibility(View.VISIBLE);
                                wishlist_count.setText("(" + String.valueOf(wishListData.size()) + " " + "Items" + ")");
                                wishlistRecycler.setAdapter(wishlistAdapter);
                                wishlistAdapter.notifyDataSetChanged();
                            }

                            purchasesData = new ArrayList<>();
                            JSONArray orders = jsonObject.getJSONArray("orders");
                            for (int p = 0; p < orders.length(); p++) {
                                JSONObject ordersObject = orders.getJSONObject(p);
                                String BoothID = ordersObject.getString("BoothID");
                                String BoothName = ordersObject.getString("BoothName");
                                String ProductLikeID = ordersObject.getString("OrderRequestID");
                                String UserID = ordersObject.getString("UserID");

                                String ProductID = "";
                                String ProductTitle = "";
                                purchaseImagesData = new ArrayList<>();
                                JSONArray OrderItems = ordersObject.getJSONArray("OrderItems");
                                for (int l = 0; l < 1; l++) {
                                    JSONObject orderObject = OrderItems.getJSONObject(l);
                                    String ProductCompressedImage = orderObject.getString("ProductImage");
                                    String ProductImage = orderObject.getString("ProductImage");
                                    ProductID = orderObject.getString("ProductID");
                                    ProductTitle = orderObject.getString("ProductTitle");

                                    purchaseImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                                }
                                purchasesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, ProductTitle, UserID, purchaseImagesData));

                            }

                            RecyclerView.LayoutManager layoutManagerpurchases = new LinearLayoutManager(OthersProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            purchasesRecycler.setLayoutManager(layoutManagerpurchases);
                            WishlistAdapter purchasesAdapter = new WishlistAdapter(OthersProfileActivity.this, purchasesData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", purchasesData.get(position).getProductID()).commit();
                                    startActivity(new Intent(OthersProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });

                            if (purchasesData.isEmpty()) {
                                purchases_nodata.setVisibility(View.VISIBLE);
                                purchasesRecycler.setVisibility(View.GONE);
                            } else {
                                purchases_nodata.setVisibility(View.GONE);
                                purchasesRecycler.setVisibility(View.VISIBLE);
                                purchases_count.setText("(" + String.valueOf(purchasesData.size()) + " " + "Items" + ")");
                                purchasesRecycler.setAdapter(purchasesAdapter);
                                purchasesAdapter.notifyDataSetChanged();
                            }


                        } else {
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(OthersProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(OthersProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(OthersProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}