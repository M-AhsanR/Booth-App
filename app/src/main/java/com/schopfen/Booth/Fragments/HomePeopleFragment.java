package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.ProductComments;
import com.schopfen.Booth.Adapters.HomeFriendsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.Models.CommentsModel;
import com.schopfen.Booth.Models.HomeFriendsModel;
import com.schopfen.Booth.Models.MentionedUsersInfo;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomePeopleFragment extends Fragment {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView home_friends_recycler;
    LinearLayoutManager layoutManager;
    boolean isLoading = true;
    ProgressBar progressBar;
    ArrayList<HomeFriendsModel> friendsModelArrayList = new ArrayList<>();
    ArrayList<ProductImagesData> productImagesData;
    ArrayList<ProductsData> productsData;
    CommentsModel productComment;
    ArrayList<ProductImagesData> questionImagesData;
    ProductsData questionsData;
    CommentsModel questionComment;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout no_data_found;
    HomeFriendsAdapter homeFriendsAdapter;
    public static boolean refresh = false;


    public HomePeopleFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (refresh) {
            progressBar.setVisibility(View.VISIBLE);
            FriendsApi();
            refresh = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (refresh) {
                progressBar.setVisibility(View.VISIBLE);
                FriendsApi();
                refresh = false;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_people, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        home_friends_recycler = view.findViewById(R.id.home_friends_recycler);
        progressBar = view.findViewById(R.id.progressbar);
        swipeRefreshLayout = view.findViewById(R.id.home_people_swipe);
        no_data_found = view.findViewById(R.id.no_data_found);

        FriendsApi();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        FriendsApi();
                    }
                }
        );

    }

    private void FriendsApi() {

        progressBar.setVisibility(View.GONE);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        String Apitoken = sharedpreferences.getString("ApiToken", " ");

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.GETFRIENDSACTIVITIES + sharedpreferences.getString("UserID", " ") + "&Start=" + "0" + "&Type=" + "user" + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("FriendsActivityList", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            swipeRefreshLayout.setRefreshing(false);

                            friendsModelArrayList.clear();

                            JSONArray activities = jsonObject.getJSONArray("activities");

                            for (int i = 0; i < activities.length(); i++) {
                                JSONObject jsonObject1 = activities.getJSONObject(i);

                                String UserFriendActivityID = jsonObject1.getString("UserFriendActivityID");
                                String LoggedInUserID = jsonObject1.getString("LoggedInUserID");
                                String UserType = jsonObject1.getString("UserType");
                                String Type = jsonObject1.getString("Type");
                                String UserID = jsonObject1.getString("UserID");
                                String NotificationTypeID = jsonObject1.getString("NotificationTypeID");
                                String NotificationTextEn = jsonObject1.getString("NotificationTextEn");
                                String NotificationTextAr = jsonObject1.getString("NotificationTextAr");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductCommentID = jsonObject1.getString("ProductCommentID");
                                String QuestionID = jsonObject1.getString("QuestionID");
                                String QuestionCommentID = jsonObject1.getString("QuestionCommentID");
                                String IsRead = jsonObject1.getString("IsRead");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String LoggedInUserName = jsonObject1.getString("LoggedInUserName");
                                String LoggedInUserImage = jsonObject1.getString("LoggedInUserImage");
                                String UserName = jsonObject1.getString("UserName");
                                String UserImage = jsonObject1.getString("UserImage");
                                String NotificationText = jsonObject1.getString("NotificationText");
                                productsData = new ArrayList<>();
                                if (!ProductID.equals("0")) {
                                    JSONArray products = jsonObject1.getJSONArray("Product");
                                    for (int j = 0; j < products.length(); j++) {
                                        JSONObject productObj = products.getJSONObject(j);
                                        String BoothImage = productObj.getString("BoothImage");
                                        String CategoryID = productObj.getString("CategoryID");
                                        String CategoryName = productObj.getString("CategoryName");
                                        String CityName = productObj.getString("CityName");
                                        String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                        String Currency = productObj.getString("Currency");
                                        String CurrencySymbol = productObj.getString("CurrencySymbol");
                                        String DeliveryTime = productObj.getString("DeliveryTime");
                                        String OutOfStock = productObj.getString("OutOfStock");
                                        String ProductDescription = productObj.getString("ProductDescription");
                                        String ProductIDpro = productObj.getString("ProductID");
                                        String ProductPrice = productObj.getString("ProductPrice");
                                        String ProductType = productObj.getString("ProductType");
                                        String ProductVideo = productObj.getString("ProductVideo");
                                        String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                        String Title = productObj.getString("Title");
                                        String UserIDpro = productObj.getString("UserID");
                                        String UserNamepro = productObj.getString("UserName");
                                        String SubCategoryName = productObj.getString("SubCategoryName");
                                        String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                        String ProductBrandName = productObj.getString("ProductBrandName");
                                        String CreatedAtpro = productObj.getString("CreatedAt");
                                        String BoothUserName = productObj.getString("BoothUserName");
                                        String IsPromotionApproved = productObj.getString("IsPromotionApproved");

                                        productImagesData = new ArrayList<>();
                                        JSONArray productImages = productObj.getJSONArray("ProductImages");
                                        for (int k = 0; k < productImages.length(); k++) {
                                            JSONObject imagesObj = productImages.getJSONObject(k);
                                            String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                            String ProductImage = imagesObj.getString("ProductImage");

                                            productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                        }

                                        productsData.add(new ProductsData("", null, BoothImage, CategoryID, CategoryName, CityName, null, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                                null, OutOfStock, ProductDescription, ProductIDpro, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserIDpro, UserNamepro
                                                , SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAtpro, "1", productImagesData, null, BoothUserName, IsPromotionApproved));

                                    }
                                }

                                if (!QuestionID.equals("0")) {
                                    try {
                                        JSONObject productObj = jsonObject1.getJSONObject("Question");
                                        String UserImageInn = productObj.getString("UserImage");
                                        String CategoryID = productObj.getString("CategoryID");
                                        String CategoryName = productObj.getString("CategoryName");
                                        String UserCityName = productObj.getString("UserCityName");
                                        String QuestionIDinn = productObj.getString("QuestionID");
                                        String CompressedBoothImage = productObj.getString("UserImage");
                                        String QuestionDescription = productObj.getString("QuestionDescription");
                                        String UserIDpro = productObj.getString("UserID");
                                        String UserNamepro = productObj.getString("UserName");
                                        String SubCategoryName = productObj.getString("SubCategoryName");
                                        String CreatedAtpro = productObj.getString("QuestionAskedAt");
                                        String BoothUserName = productObj.getString("BoothUserName");

                                        questionImagesData = new ArrayList<>();
                                        JSONArray productImages = productObj.getJSONArray("QuestionImages");
                                        for (int k = 0; k < productImages.length(); k++) {
                                            JSONObject imagesObj = productImages.getJSONObject(k);
                                            String ProductCompressedImage = imagesObj.getString("CompressedImage");
                                            String ProductImage = imagesObj.getString("Image");

                                            questionImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                        }

                                        questionsData = new ProductsData(QuestionIDinn, null, UserImageInn, CategoryID, CategoryName, UserCityName, null, CompressedBoothImage, "", "", "",
                                                null, "", QuestionDescription, "", "", "", "", "", "", UserIDpro, UserNamepro
                                                , SubCategoryName, "", "", CreatedAtpro, "1", questionImagesData, null, BoothUserName, "");

                                    }catch (Exception e){
                                        questionImagesData = new ArrayList<>();
                                        questionImagesData.add(new ProductImagesData("", ""));
                                        questionsData = new ProductsData("", "", "", "", "", "", "", "", "", "", "",
                                                "", "", "", "", "", "", "", "", "", "", ""
                                                , "", "", "", "", "", questionImagesData, "", "", "");
                                    }
                                }

                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = jsonObject1.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserIDinner = object.getString("UserID");
                                    String FullName = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserIDinner, FullName, MentionedName, MentionedUserType));

                                }

                                if (!ProductCommentID.equals("0")) {
                                    try {
                                        JSONObject ProductComment = jsonObject1.getJSONObject("ProductComment");
                                        String Comment = ProductComment.getString("Comment");
                                        String CreatedAtcomment = ProductComment.getString("CreatedAt");
                                        String CompressedImage = ProductComment.getString("CompressedImage");
                                        String BoothCompressedImage = ProductComment.getString("CompressedBoothImage");
                                        String UserNamecomment = ProductComment.getString("UserName");
                                        String BoothUserName = ProductComment.getString("BoothUserName");
                                        String CityTitle = ProductComment.getString("CityTitle");
                                        String UserIDcomment = ProductComment.getString("UserID");
                                        String ProductCommentID1 = ProductComment.getString("ProductCommentID");
                                        String CommentedAs = ProductComment.getString("CommentedAs");

                                        productComment = new CommentsModel(ProductCommentID1, BoothCompressedImage, Comment, CreatedAtcomment, CompressedImage, UserNamecomment, CityTitle, UserIDcomment, CommentedAs, null, BoothUserName);

                                    }catch (Exception e){
                                        productComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");
                                    }

                                } else {
                                    productComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");
                                }
                                if (!QuestionCommentID.equals("0")) {
                                    try {
                                        JSONObject QuestionComment = jsonObject1.getJSONObject("QuestionComment");
                                        String Comment = QuestionComment.getString("Comment");
                                        String CreatedAtcomment = QuestionComment.getString("CreatedAt");
                                        String CompressedImage = QuestionComment.getString("CompressedImage");
                                        String BoothCompressedImage = QuestionComment.getString("CompressedBoothImage");
                                        String UserNamecomment = QuestionComment.getString("UserName");
                                        String BoothUserName = QuestionComment.getString("BoothUserName");
                                        String CityTitle = QuestionComment.getString("CityTitle");
                                        String UserIDcomment = QuestionComment.getString("UserID");
                                        String QuestionCommentID1 = QuestionComment.getString("QuestionCommentID");
                                        String CommentedAs = QuestionComment.getString("CommentedAs");

                                        questionComment = new CommentsModel(QuestionCommentID1, BoothCompressedImage, Comment, CreatedAtcomment, CompressedImage, UserNamecomment, CityTitle, UserIDcomment, CommentedAs, null, BoothUserName);

                                    }catch (Exception e){
                                        questionComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");
                                    }

                                } else {

                                    questionComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");

                                }

                                friendsModelArrayList.add(new HomeFriendsModel(UserFriendActivityID, LoggedInUserID, UserType, Type, UserID,
                                        NotificationTypeID, NotificationTextEn, NotificationTextAr, ProductID, ProductCommentID, QuestionID,
                                        QuestionCommentID, IsRead, CreatedAt, LoggedInUserName, LoggedInUserImage, UserName, UserImage, NotificationText,
                                        productsData, productComment, questionsData, questionComment, mentionedUsersInfos));

                            }

                            if (friendsModelArrayList.isEmpty()) {
                                no_data_found.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            } else {
                                no_data_found.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }

                            layoutManager = new LinearLayoutManager(getActivity());
                            home_friends_recycler.setLayoutManager(layoutManager);
                            homeFriendsAdapter = new HomeFriendsAdapter(getActivity(), friendsModelArrayList);
                            home_friends_recycler.setAdapter(homeFriendsAdapter);
                            home_friends_recycler.setOnScrollListener(recyclerViewOnScrollListener);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
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

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount, totalItemCount, pastVisiblesItems, lastVisibleItem, threshhold = 1;
            if (dy > 0) {

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();


//            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                int a = 10;
                int b = a;

//                if (!isLoading && !isLastPage) {
//                    if (visibleItemCount >= totalItemCount
//
//                            && totalItemCount >= PAGE_SIZE) {
//                        loadMoreItems();
//                    }
//                }

                if (isLoading) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)

//                    if ( totalItemCount  <= (lastVisibleItem + threshhold))

                    {
                        isLoading = false;
                        progressBar.setVisibility(View.VISIBLE);
                        loadMoreItems();
                    }

                }
            }
        }
    };

    private void loadMoreItems() {
        isLoading = false;


        PaginationApi();
    }

    private void PaginationApi() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        String Apitoken = sharedpreferences.getString("ApiToken", " ");

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.GETFRIENDSACTIVITIES + sharedpreferences.getString("UserID", " ") + "&Start=" + String.valueOf(friendsModelArrayList.size()) + "&Type=" + "user" + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("FriendsActivityList", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            isLoading = true;
                            ArrayList<HomeFriendsModel> test = new ArrayList<>();

                            JSONArray activities = jsonObject.getJSONArray("activities");

                            for (int i = 0; i < activities.length(); i++) {
                                JSONObject jsonObject1 = activities.getJSONObject(i);

                                String UserFriendActivityID = jsonObject1.getString("UserFriendActivityID");
                                String LoggedInUserID = jsonObject1.getString("LoggedInUserID");
                                String UserType = jsonObject1.getString("UserType");
                                String Type = jsonObject1.getString("Type");
                                String UserID = jsonObject1.getString("UserID");
                                String NotificationTypeID = jsonObject1.getString("NotificationTypeID");
                                String NotificationTextEn = jsonObject1.getString("NotificationTextEn");
                                String NotificationTextAr = jsonObject1.getString("NotificationTextAr");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductCommentID = jsonObject1.getString("ProductCommentID");
                                String QuestionID = jsonObject1.getString("QuestionID");
                                String QuestionCommentID = jsonObject1.getString("QuestionCommentID");
                                String IsRead = jsonObject1.getString("IsRead");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String LoggedInUserName = jsonObject1.getString("LoggedInUserName");
                                String LoggedInUserImage = jsonObject1.getString("LoggedInUserImage");
                                String UserName = jsonObject1.getString("UserName");
                                String UserImage = jsonObject1.getString("UserImage");
                                String NotificationText = jsonObject1.getString("NotificationText");
                                productsData = new ArrayList<>();
                                if (!ProductID.equals("0")) {
                                    JSONArray products = jsonObject1.getJSONArray("Product");
                                    for (int j = 0; j < products.length(); j++) {
                                        JSONObject productObj = products.getJSONObject(j);
                                        String BoothImage = productObj.getString("BoothImage");
                                        String CategoryID = productObj.getString("CategoryID");
                                        String CategoryName = productObj.getString("CategoryName");
                                        String CityName = productObj.getString("CityName");
                                        String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                        String Currency = productObj.getString("Currency");
                                        String CurrencySymbol = productObj.getString("CurrencySymbol");
                                        String DeliveryTime = productObj.getString("DeliveryTime");
                                        String OutOfStock = productObj.getString("OutOfStock");
                                        String ProductDescription = productObj.getString("ProductDescription");
                                        String ProductIDpro = productObj.getString("ProductID");
                                        String ProductPrice = productObj.getString("ProductPrice");
                                        String ProductType = productObj.getString("ProductType");
                                        String ProductVideo = productObj.getString("ProductVideo");
                                        String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                        String Title = productObj.getString("Title");
                                        String UserIDpro = productObj.getString("UserID");
                                        String UserNamepro = productObj.getString("UserName");
                                        String SubCategoryName = productObj.getString("SubCategoryName");
                                        String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                        String ProductBrandName = productObj.getString("ProductBrandName");
                                        String CreatedAtpro = productObj.getString("CreatedAt");
                                        String BoothUserName = productObj.getString("BoothUserName");
                                        String IsPromotionApproved = productObj.getString("IsPromotionApproved");

                                        productImagesData = new ArrayList<>();
                                        JSONArray productImages = productObj.getJSONArray("ProductImages");
                                        for (int k = 0; k < productImages.length(); k++) {
                                            JSONObject imagesObj = productImages.getJSONObject(k);
                                            String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                            String ProductImage = imagesObj.getString("ProductImage");

                                            productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                        }

                                        productsData.add(new ProductsData("", null, BoothImage, CategoryID, CategoryName, CityName, null, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                                null, OutOfStock, ProductDescription, ProductIDpro, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserIDpro, UserNamepro
                                                , SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAtpro, "1", productImagesData, null, BoothUserName, IsPromotionApproved));

                                    }
                                }

                                if (!QuestionID.equals("0")) {
                                    try {
                                        JSONObject productObj = jsonObject1.getJSONObject("Question");
                                        String UserImageInn = productObj.getString("UserImage");
                                        String CategoryID = productObj.getString("CategoryID");
                                        String CategoryName = productObj.getString("CategoryName");
                                        String UserCityName = productObj.getString("UserCityName");
                                        String QuestionIDinn = productObj.getString("QuestionID");
                                        String CompressedBoothImage = productObj.getString("UserImage");
                                        String QuestionDescription = productObj.getString("QuestionDescription");
                                        String UserIDpro = productObj.getString("UserID");
                                        String UserNamepro = productObj.getString("UserName");
                                        String SubCategoryName = productObj.getString("SubCategoryName");
                                        String CreatedAtpro = productObj.getString("QuestionAskedAt");
                                        String BoothUserName = productObj.getString("BoothUserName");

                                        questionImagesData = new ArrayList<>();
                                        JSONArray productImages = productObj.getJSONArray("QuestionImages");
                                        for (int k = 0; k < productImages.length(); k++) {
                                            JSONObject imagesObj = productImages.getJSONObject(k);
                                            String ProductCompressedImage = imagesObj.getString("CompressedImage");
                                            String ProductImage = imagesObj.getString("Image");

                                            questionImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                        }

                                        questionsData = new ProductsData(QuestionIDinn, null, UserImageInn, CategoryID, CategoryName, UserCityName, null, CompressedBoothImage, "", "", "",
                                                null, "", QuestionDescription, "", "", "", "", "", "", UserIDpro, UserNamepro
                                                , SubCategoryName, "", "", CreatedAtpro, "1", questionImagesData, null, BoothUserName, "");


                                    }catch (Exception e){
                                        questionImagesData = new ArrayList<>();
                                        questionImagesData.add(new ProductImagesData("", ""));
                                        questionsData = new ProductsData("", "", "", "", "", "", "", "", "", "", "",
                                                "", "", "", "", "", "", "", "", "", "", ""
                                                , "", "", "", "", "", questionImagesData, "", "", "");

                                    }

                                }

                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = jsonObject1.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserIDinner = object.getString("UserID");
                                    String FullName = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserIDinner, FullName, MentionedName, MentionedUserType));

                                }

                                if (!ProductCommentID.equals("0")) {
                                    try {
                                        JSONObject ProductComment = jsonObject1.getJSONObject("ProductComment");
                                        String Comment = ProductComment.getString("Comment");
                                        String CreatedAtcomment = ProductComment.getString("CreatedAt");
                                        String CompressedImage = ProductComment.getString("CompressedImage");
                                        String BoothCompressedImage = ProductComment.getString("CompressedBoothImage");
                                        String UserNamecomment = ProductComment.getString("UserName");
                                        String BoothUserName = ProductComment.getString("BoothUserName");
                                        String CityTitle = ProductComment.getString("CityTitle");
                                        String UserIDcomment = ProductComment.getString("UserID");
                                        String ProductCommentID1 = ProductComment.getString("ProductCommentID");
                                        String CommentedAs = ProductComment.getString("CommentedAs");

                                        productComment = new CommentsModel(ProductCommentID1, BoothCompressedImage, Comment, CreatedAtcomment, CompressedImage, UserNamecomment, CityTitle, UserIDcomment, CommentedAs, null, BoothUserName);

                                    }catch (Exception e){
                                        productComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");
                                    }

                                    //                                    productComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");

                                } else {
                                    productComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");
                                }
                                if (!QuestionCommentID.equals("0")) {
                                    try {
                                        JSONObject QuestionComment = jsonObject1.getJSONObject("QuestionComment");
                                        String Comment = QuestionComment.getString("Comment");
                                        String CreatedAtcomment = QuestionComment.getString("CreatedAt");
                                        String CompressedImage = QuestionComment.getString("CompressedImage");
                                        String BoothCompressedImage = QuestionComment.getString("CompressedBoothImage");
                                        String UserNamecomment = QuestionComment.getString("UserName");
                                        String BoothUserName = QuestionComment.getString("BoothUserName");
                                        String CityTitle = QuestionComment.getString("CityTitle");
                                        String UserIDcomment = QuestionComment.getString("UserID");
                                        String QuestionCommentID1 = QuestionComment.getString("QuestionCommentID");
                                        String CommentedAs = QuestionComment.getString("CommentedAs");

                                        questionComment = new CommentsModel(QuestionCommentID1, BoothCompressedImage, Comment, CreatedAtcomment, CompressedImage, UserNamecomment, CityTitle, UserIDcomment, CommentedAs, null, BoothUserName);

                                    }catch (Exception e){
                                        questionComment = new CommentsModel("" ,"","", "", "", "", "", "", "", null, "");
                                    }

//                                    questionComment = new CommentsModel("", "", "", "", "", "", "", "", "", null, "");

                                }
                                else {
                                    questionComment = new CommentsModel("" ,"","", "", "", "", "", "", "", null, "");
                                }

                                friendsModelArrayList.add(new HomeFriendsModel(UserFriendActivityID, LoggedInUserID, UserType, Type, UserID,
                                        NotificationTypeID, NotificationTextEn, NotificationTextAr, ProductID, ProductCommentID, QuestionID,
                                        QuestionCommentID, IsRead, CreatedAt, LoggedInUserName, LoggedInUserImage, UserName, UserImage, NotificationText,
                                        productsData, productComment, questionsData, questionComment, mentionedUsersInfos));

                            }

                            progressBar.setVisibility(View.GONE);
                            homeFriendsAdapter.addfeed(test);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }

            }
        });

    }


}
