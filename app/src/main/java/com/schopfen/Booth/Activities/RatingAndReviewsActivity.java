package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.FollowingsAdapter;
import com.schopfen.Booth.Adapters.InboxAdapter;
import com.schopfen.Booth.Adapters.ReviewsRatingsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.InboxModel;
import com.schopfen.Booth.Models.ReviewsAndRatingsData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RatingAndReviewsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPREFERENCES = "MyPrefs";

    TextView averageRating, noOfSellers;
    RecyclerView ratingsReviewsRecycler;
    String OtherUserID, activity, productID;
    ArrayList<ReviewsAndRatingsData> reviewsAndRatingsData = new ArrayList<>();

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
            setContentView(R.layout.activity_rating_and_reviews);

            initializeViews();

            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            activity = getIntent().getStringExtra("Activity");

            OtherUserID = getIntent().getStringExtra("userID");
            switch (activity) {
                case "My":
                    RatingAndReviewsApi();
                    break;
                case "MyBooth":
                    BoothRatingAndReviewsApi();
                    break;
                case "Other":
                    RatingAndReviewsApi();
                    break;
                case "BoothOther":
                    BoothRatingAndReviewsApi();
                    break;
                case "Product":
                    productID = getIntent().getStringExtra("ProductID");
                    RatingAndReviewsApiProducts();
                    break;
            }
        }else {
            setContentView(R.layout.no_internet_screen);
        }



    }


    private void initializeViews() {
        averageRating = findViewById(R.id.averageRating);
        noOfSellers = findViewById(R.id.noOfSellers);
        ratingsReviewsRecycler = findViewById(R.id.ratingsReviewsRecycler);
    }

    private void RatingAndReviewsApiProducts() {
        CustomLoader.showDialog(RatingAndReviewsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("ProductID", productID);

        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getProductRatings, RatingAndReviewsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Reviews&RatingResp", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();

                            JSONObject boothRating = jsonObject.getJSONObject("product");
                            String UserAverageRating = boothRating.getString("ProductAverageRating");
                            String UserRated1Count = boothRating.getString("ProductRated1Count");
                            String UserRated2Count = boothRating.getString("ProductRated2Count");
                            String UserRated3Count = boothRating.getString("ProductRated3Count");
                            String UserRated4Count = boothRating.getString("ProductRated4Count");
                            String UserRated5Count = boothRating.getString("ProductRated5Count");
                            String UserRatingsCount = boothRating.getString("ProductRatingsCount");

                            if (UserAverageRating.equals("0")){
                                averageRating.setText(UserAverageRating);
                                noOfSellers.setText(UserRatingsCount);
                            }else {
                                averageRating.setText(UserAverageRating + " " + "Starts");
                                noOfSellers.setText(UserRatingsCount + " " + "User(s)");
                            }


                            JSONArray UserRatings = boothRating.getJSONArray("ProductRatingsByUsers");
                            for (int i = 0; i < UserRatings.length(); i++) {
                                JSONObject jsonObject1 = UserRatings.getJSONObject(i);

                                String BoothCoverImage = jsonObject1.getString("BoothCoverImage");
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CityID = jsonObject1.getString("CityID");
                                String CompressedBoothCoverImage = jsonObject1.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String CompressedCoverImage = jsonObject1.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String ContactDays = jsonObject1.getString("ContactDays");
                                String ContactTimeFrom = jsonObject1.getString("ContactTimeFrom");
                                String ContactTimeTo = jsonObject1.getString("ContactTimeTo");
                                String CoverImage = jsonObject1.getString("CoverImage");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String Email = jsonObject1.getString("Email");
                                String FullName = jsonObject1.getString("FullName");
                                String Gender = jsonObject1.getString("Gender");
                                String HideContactNo = jsonObject1.getString("HideContactNo");
                                String Image = jsonObject1.getString("Image");
                                String IsActive = jsonObject1.getString("IsActive");
                                String IsEmailVerified = jsonObject1.getString("IsEmailVerified");
                                String IsMobileVerified = jsonObject1.getString("IsMobileVerified");
                                String IsProfileCustomized = jsonObject1.getString("IsProfileCustomized");
                                String LastState = jsonObject1.getString("LastState");
                                String Mobile = jsonObject1.getString("Mobile");
                                String PaymentAccountBankBranch = jsonObject1.getString("PaymentAccountBankBranch");
                                String PaymentAccountHolderName = jsonObject1.getString("PaymentAccountHolderName");
                                String PaymentAccountNumber = jsonObject1.getString("PaymentAccountNumber");
                                String Points = jsonObject1.getString("Points");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String UserTextID = jsonObject1.getString("UserTextID");
                                String UserOrderRequestRating = jsonObject1.getString("Rating");
                                String UserOrderRequestReview = jsonObject1.getString("Review");

                                reviewsAndRatingsData.add(new ReviewsAndRatingsData(BoothCoverImage, BoothImage, BoothName, BoothType, BoothUserName, CityID, CompressedBoothCoverImage,
                                        CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays, ContactTimeFrom, ContactTimeTo, CoverImage, CreatedAt, Email, FullName,
                                        Gender, HideContactNo, Image, IsActive, IsEmailVerified, IsMobileVerified, IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch,
                                        PaymentAccountHolderName, PaymentAccountNumber, Points, UserID, UserName, UserTextID, UserOrderRequestRating, UserOrderRequestReview));

                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RatingAndReviewsActivity.this, RecyclerView.VERTICAL, false);
                            ratingsReviewsRecycler.setLayoutManager(layoutManager);
                            ReviewsRatingsAdapter reviewsRatingsAdapter = new ReviewsRatingsAdapter(RatingAndReviewsActivity.this, reviewsAndRatingsData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            ratingsReviewsRecycler.setAdapter(reviewsRatingsAdapter);

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(RatingAndReviewsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(RatingAndReviewsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void RatingAndReviewsApi() {
        CustomLoader.showDialog(RatingAndReviewsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        if (activity.equals("My")) {
            body.put("UserID", sharedPreferences.getString("UserID", " "));
        } else if (activity.equals("Other")) {
            body.put("UserID", sharedPreferences.getString("UserID", " "));
            body.put("OtherUserID", OtherUserID);
        }
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getUserRatings, RatingAndReviewsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Reviews&RatingResp", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();

                            JSONObject boothRating = jsonObject.getJSONObject("booth_rating");
                            String UserAverageRating = boothRating.getString("UserAverageRating");
                            String UserRated1Count = boothRating.getString("UserRated1Count");
                            String UserRated2Count = boothRating.getString("UserRated2Count");
                            String UserRated3Count = boothRating.getString("UserRated3Count");
                            String UserRated4Count = boothRating.getString("UserRated4Count");
                            String UserRated5Count = boothRating.getString("UserRated5Count");
                            String UserRatingsCount = boothRating.getString("UserRatingsCount");

                            if (UserAverageRating.equals("0")){
                                averageRating.setText(UserAverageRating);
                                noOfSellers.setText(UserRatingsCount);
                            }else {
                                averageRating.setText(UserAverageRating + " " + "Starts");
                                noOfSellers.setText(UserRatingsCount + " " + "Seller(s)");
                            }


                            JSONArray UserRatings = boothRating.getJSONArray("UserRatings");
                            for (int i = 0; i < UserRatings.length(); i++) {
                                JSONObject jsonObject1 = UserRatings.getJSONObject(i);

                                String BoothCoverImage = jsonObject1.getString("BoothCoverImage");
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CityID = jsonObject1.getString("CityID");
                                String CompressedBoothCoverImage = jsonObject1.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String CompressedCoverImage = jsonObject1.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String ContactDays = jsonObject1.getString("ContactDays");
                                String ContactTimeFrom = jsonObject1.getString("ContactTimeFrom");
                                String ContactTimeTo = jsonObject1.getString("ContactTimeTo");
                                String CoverImage = jsonObject1.getString("CoverImage");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String Email = jsonObject1.getString("Email");
                                String FullName = jsonObject1.getString("FullName");
                                String Gender = jsonObject1.getString("Gender");
                                String HideContactNo = jsonObject1.getString("HideContactNo");
                                String Image = jsonObject1.getString("Image");
                                String IsActive = jsonObject1.getString("IsActive");
                                String IsEmailVerified = jsonObject1.getString("IsEmailVerified");
                                String IsMobileVerified = jsonObject1.getString("IsMobileVerified");
                                String IsProfileCustomized = jsonObject1.getString("IsProfileCustomized");
                                String LastState = jsonObject1.getString("LastState");
                                String Mobile = jsonObject1.getString("Mobile");
                                String PaymentAccountBankBranch = jsonObject1.getString("PaymentAccountBankBranch");
                                String PaymentAccountHolderName = jsonObject1.getString("PaymentAccountHolderName");
                                String PaymentAccountNumber = jsonObject1.getString("PaymentAccountNumber");
                                String Points = jsonObject1.getString("Points");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String UserTextID = jsonObject1.getString("UserTextID");
                                String UserOrderRequestRating = jsonObject1.getString("UserOrderRequestRating");
                                String UserOrderRequestReview = jsonObject1.getString("UserOrderRequestReview");

                                reviewsAndRatingsData.add(new ReviewsAndRatingsData(BoothCoverImage, BoothImage, BoothName, BoothType, BoothUserName, CityID, CompressedBoothCoverImage,
                                        CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays, ContactTimeFrom, ContactTimeTo, CoverImage, CreatedAt, Email, FullName,
                                        Gender, HideContactNo, Image, IsActive, IsEmailVerified, IsMobileVerified, IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch,
                                        PaymentAccountHolderName, PaymentAccountNumber, Points, UserID, UserName, UserTextID, UserOrderRequestRating, UserOrderRequestReview));

                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RatingAndReviewsActivity.this, RecyclerView.VERTICAL, false);
                            ratingsReviewsRecycler.setLayoutManager(layoutManager);
                            ReviewsRatingsAdapter reviewsRatingsAdapter = new ReviewsRatingsAdapter(RatingAndReviewsActivity.this, reviewsAndRatingsData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            ratingsReviewsRecycler.setAdapter(reviewsRatingsAdapter);

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(RatingAndReviewsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(RatingAndReviewsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void BoothRatingAndReviewsApi() {
        CustomLoader.showDialog(RatingAndReviewsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        if (activity.equals("MyBooth")) {
            body.put("UserID", sharedPreferences.getString("UserID", " "));
            body.put("BoothID", sharedPreferences.getString("UserID", " "));
        } else if (activity.equals("BoothOther")) {
            body.put("UserID", sharedPreferences.getString("UserID", " "));
            body.put("BoothID", OtherUserID);
        }
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getBoothRatings, RatingAndReviewsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Reviews&RatingResp", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();

                            JSONObject boothRating = jsonObject.getJSONObject("booth_rating");
                            String BoothAverageRating = boothRating.getString("BoothAverageRating");
                            String BoothRated1Count = boothRating.getString("BoothRated1Count");
                            String BoothRated2Count = boothRating.getString("BoothRated2Count");
                            String BoothRated3Count = boothRating.getString("BoothRated3Count");
                            String BoothRated4Count = boothRating.getString("BoothRated4Count");
                            String BoothRated5Count = boothRating.getString("BoothRated5Count");
                            String BoothRatingsCount = boothRating.getString("BoothRatingsCount");

                            if (BoothAverageRating.equals("0")){
                                averageRating.setText(BoothAverageRating);
                                noOfSellers.setText(BoothRatingsCount);
                            }else {
                                averageRating.setText(BoothAverageRating + " " + "Starts");
                                noOfSellers.setText(BoothRatingsCount + " " + "User(s)");
                            }


                            JSONArray UserRatings = boothRating.getJSONArray("BoothRatings");
                            for (int i = 0; i < UserRatings.length(); i++) {
                                JSONObject jsonObject1 = UserRatings.getJSONObject(i);

                                String BoothCoverImage = jsonObject1.getString("BoothCoverImage");
                                String BoothImage = jsonObject1.getString("BoothImage");
                                String BoothName = jsonObject1.getString("BoothName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CityID = jsonObject1.getString("CityID");
                                String CompressedBoothCoverImage = jsonObject1.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String CompressedCoverImage = jsonObject1.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String ContactDays = jsonObject1.getString("ContactDays");
                                String ContactTimeFrom = jsonObject1.getString("ContactTimeFrom");
                                String ContactTimeTo = jsonObject1.getString("ContactTimeTo");
                                String CoverImage = jsonObject1.getString("CoverImage");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String Email = jsonObject1.getString("Email");
                                String FullName = jsonObject1.getString("FullName");
                                String Gender = jsonObject1.getString("Gender");
                                String HideContactNo = jsonObject1.getString("HideContactNo");
                                String Image = jsonObject1.getString("Image");
                                String IsActive = jsonObject1.getString("IsActive");
                                String IsEmailVerified = jsonObject1.getString("IsEmailVerified");
                                String IsMobileVerified = jsonObject1.getString("IsMobileVerified");
                                String IsProfileCustomized = jsonObject1.getString("IsProfileCustomized");
                                String LastState = jsonObject1.getString("LastState");
                                String Mobile = jsonObject1.getString("Mobile");
                                String PaymentAccountBankBranch = jsonObject1.getString("PaymentAccountBankBranch");
                                String PaymentAccountHolderName = jsonObject1.getString("PaymentAccountHolderName");
                                String PaymentAccountNumber = jsonObject1.getString("PaymentAccountNumber");
                                String Points = jsonObject1.getString("Points");
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String UserTextID = jsonObject1.getString("UserTextID");
                                String OrderRequestRating = jsonObject1.getString("OrderRequestRating");
                                String OrderRequestReview = jsonObject1.getString("OrderRequestReview");

                                reviewsAndRatingsData.add(new ReviewsAndRatingsData(BoothCoverImage, BoothImage, BoothName, BoothType, BoothUserName, CityID, CompressedBoothCoverImage,
                                        CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays, ContactTimeFrom, ContactTimeTo, CoverImage, CreatedAt, Email, FullName,
                                        Gender, HideContactNo, Image, IsActive, IsEmailVerified, IsMobileVerified, IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch,
                                        PaymentAccountHolderName, PaymentAccountNumber, Points, UserID, UserName, UserTextID, OrderRequestRating, OrderRequestReview));

                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RatingAndReviewsActivity.this, RecyclerView.VERTICAL, false);
                            ratingsReviewsRecycler.setLayoutManager(layoutManager);
                            ReviewsRatingsAdapter reviewsRatingsAdapter = new ReviewsRatingsAdapter(RatingAndReviewsActivity.this, reviewsAndRatingsData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            ratingsReviewsRecycler.setAdapter(reviewsRatingsAdapter);

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(RatingAndReviewsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(RatingAndReviewsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
