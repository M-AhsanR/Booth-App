package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schopfen.Booth.Activities.BoothSettingsActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.SelectedCategoriesList;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Adapters.AdapterBoothHome;
import com.schopfen.Booth.Adapters.AdapterMainActivity;
import com.schopfen.Booth.Adapters.BoothHomeFragmentAdapter;
import com.schopfen.Booth.Adapters.HomeCateGridAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.Models.PromotedProductsData;
import com.schopfen.Booth.Models.QuestionsModel;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Booth_Home_Fragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public static final String tag = "DropFragment";
    RecyclerView recyclerView;
//    RecyclerView gridView;
    LinearLayoutManager layoutManager;
//    ImageView listViewBtn;
//    NestedScrollView nestedScrollView;
    BottomNavigationView bottomNavigationView;
    RelativeLayout inbox, settings;
    ImageView inbox_img;
    TextView msg_count;
    RelativeLayout no_data_found;

    ArrayList<ProductsData> productsData = new ArrayList<>();
    ArrayList<ProductsData> productsDataGrid = new ArrayList<>();
    public static ArrayList<PromotedProductsData> promotedProductsData = new ArrayList<>();
    ArrayList<ProductImagesData> productImagesData;
    ArrayList<ProductImagesData> promotedProductImagesData;

    boolean isLoading = true;
    ProgressBar progressBar;
    AdapterBoothHome homeCateRecyclAdapter;

    SharedPreferences storeProductsPrefes;
    SharedPreferences.Editor storeProductsPrefesEditor;
    String ProductsPREFERENCES = "BOOTHPRODUCTS";
    Gson gson = new Gson();

//    TextView categoriesCount;
    SwipeRefreshLayout swipeRefreshLayout;


    public static int refreshposition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.booth_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        storeProductsPrefes = getActivity().getSharedPreferences(ProductsPREFERENCES, Context.MODE_PRIVATE);
        storeProductsPrefesEditor = storeProductsPrefes.edit();

        initiateViews(view);
        progressBar.setVisibility(View.VISIBLE);
        mEditor.putString("activity", "home_home").apply();
        getUserDetails();
//        getProductsApi();
        recyclerView.setVisibility(View.VISIBLE);

        if (storeProductsPrefes.getString("Boothposition", "") != null && !storeProductsPrefes.getString("Boothposition", "").isEmpty()){
            if (!storeProductsPrefes.getString("Boothposition", "").equals("0") && !storeProductsPrefes.getString("Boothposition", "").equals("1")){
                ArrayList<ProductsData> arrayList = new ArrayList<>();
                String json = storeProductsPrefes.getString(ProductsPREFERENCES, "");
                if (!(json.equals(null) || json.equals(""))) {
                    Type type = new TypeToken<List<ProductsData>>() {
                    }.getType();
                    arrayList = gson.fromJson(json, type);
                    if (arrayList.size() > 0) {
                        productsData = (ArrayList<ProductsData>) arrayList;
                        Log.e("productsdata", productsData.toString());
                        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                        homeCateRecyclAdapter = new AdapterBoothHome(getActivity(), productsData, new AdapterBoothHome.CRLCallbacks() {
                            @Override
                            public void onItemClick(int position) {
//                                        Toast.makeText(getContext(),"item "+position, Toast.LENGTH_SHORT).show();

                            }
                        });
                        recyclerView.setAdapter(homeCateRecyclAdapter);
                        recyclerView.setAdapter(homeCateRecyclAdapter);
                        if (storeProductsPrefes.getString("Boothposition", "") != null && !storeProductsPrefes.getString("Boothposition", "").isEmpty()){
                            recyclerView.scrollToPosition(Integer.parseInt(storeProductsPrefes.getString("Boothposition", "")));
                        }
                        recyclerView.setOnScrollListener(recyclerViewOnScrollListener);

                        progressBar.setVisibility(View.GONE);

                    } else {
                        getProductsApi();
                    }
                } else {
                    getProductsApi();
                }
            }else {
                getProductsApi();
            }
        }else {
            getProductsApi();
        }


        inbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InboxActivity.class));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), BoothSettingsActivity.class);
                startActivity(i);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getProductsApi();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

    }

    private void initiateViews(View view) {
        recyclerView = view.findViewById(R.id.booth_home_recycler);
        bottomNavigationView = getActivity().findViewById(R.id.boothnavigation);
        swipeRefreshLayout = view.findViewById(R.id.booth_home_swipe_recycler);
        progressBar = view.findViewById(R.id.progressbar);
        inbox = view.findViewById(R.id.inbox);
        inbox_img = view.findViewById(R.id.inbox_img);
        msg_count = view.findViewById(R.id.msg_count);
        no_data_found = view.findViewById(R.id.no_data_found);
        settings = view.findViewById(R.id.settings);
        recyclerView.setItemViewCacheSize(25);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void getUserDetails(){

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()){
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String AuthToken = user_info.getString("AuthToken");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            String CityID = user_info.getString("CityID");
                            String CityTitle = user_info.getString("CityTitle");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String RoleID = user_info.getString("RoleID");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");
                            String Verification = user_info.getString("Verification");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothName = user_info.getString("BoothName");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            if (BoothUnreadMessageCount.equals("0")){
                                msg_count.setVisibility(View.GONE);
                            }else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(BoothUnreadMessageCount);
                            }

//                            if (BoothHasUnreadMessage.equals("yes")){
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                            }else {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                            }

                            mEditor.putString("HasUnreadMessage", HasUnreadMessage);
                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.putString("UserName", UserName);
                            mEditor.putString("BoothUserName", BoothUserName);
                            mEditor.putString("UserOrderCount", UserOrderCount);
                            mEditor.putString("BoothOrderCount", BoothOrderCount);
                            mEditor.commit();

//                            categoriesCount.setText(String.valueOf(UserCategoriesCount) + " " + getResources().getString(R.string.interests));

                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getProductsApi() {

        CustomLoader.showDialog(getActivity());
        bottomNavigationView.setClickable(false);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", "0");
        body.put("Type", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.HomeProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
//                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            swipeRefreshLayout.setRefreshing(false);
                            productsData.clear();
                            promotedProductsData.clear();
//                            CustomLoader.dialog.dismiss();
                            JSONArray products = jsonObject.getJSONArray("products");
                            JSONArray promoted_products = jsonObject.getJSONArray("promoted_products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
                                String ItemType = productObj.getString("ItemType");
                                if (ItemType.equals("Product")) {
                                    String BoothImage = productObj.getString("BoothImage");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CityName = productObj.getString("CityName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                    String Currency = productObj.getString("Currency");
                                    String CurrencySymbol = productObj.getString("CurrencySymbol");
                                    String DeliveryTime = productObj.getString("DeliveryTime");
                                    String LikesCount = productObj.getString("LikesCount");
                                    String OutOfStock = productObj.getString("OutOfStock");
                                    String ProductDescription = productObj.getString("ProductDescription");
                                    String ProductID = productObj.getString("ProductID");
                                    String ProductPrice = productObj.getString("ProductPrice");
                                    String ProductType = productObj.getString("ProductType");
                                    String ProductVideo = productObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                    String Title = productObj.getString("Title");
                                    String UserID = productObj.getString("UserID");
                                    String UserName = productObj.getString("UserName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                    String ProductBrandName = productObj.getString("ProductBrandName");
                                    String CreatedAt = productObj.getString("CreatedAt");
                                    String IsLiked = productObj.getString("IsLiked");
                                    String BoothUserName = productObj.getString("BoothUserName");
                                    String IsPromotionApproved = productObj.getString("IsPromotionApproved");
                                    String IsPromotedProduct = productObj.getString("IsPromotedProduct");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray productImages = productObj.getJSONArray("ProductImages");
                                    for (int j = 0; j < productImages.length(); j++) {
                                        JSONObject imagesObj = productImages.getJSONObject(j);
                                        String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                        String ProductImage = imagesObj.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    productsData.add(new ProductsData("", ItemType, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                            LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                                } else if (ItemType.equals("Question")) {
                                    String QuestionID = productObj.getString("QuestionID");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String QuestionDescription = productObj.getString("QuestionDescription");
                                    String QuestionAskedAt = productObj.getString("QuestionAskedAt");
                                    String UserImage = productObj.getString("UserImage");
                                    String UserName = productObj.getString("UserName");
                                    String UserCityName = productObj.getString("UserCityName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String UserID = productObj.getString("UserID");
                                    String BoothUserName = productObj.getString("BoothUserName");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray QuestionImages = productObj.getJSONArray("QuestionImages");
                                    for (int j = 0; j < QuestionImages.length(); j++) {
                                        JSONObject imagesObj = QuestionImages.getJSONObject(j);
                                        String CompressedImage = imagesObj.getString("CompressedImage");
                                        String Image = imagesObj.getString("Image");

                                        productImagesData.add(new ProductImagesData(CompressedImage, Image));
                                    }

                                    productsData.add(new ProductsData(QuestionID, ItemType, UserImage, CategoryID, CategoryName, UserCityName, CommentCount, "", "", "", "",
                                            "", "", QuestionDescription, "", "", "", "", "", "", UserID, UserName, SubCategoryName, "", "", QuestionAskedAt, "0", productImagesData, "", BoothUserName, ""));
                                }
                            }

                            String json = gson.toJson(productsData);
                            storeProductsPrefesEditor.putString(ProductsPREFERENCES, json);
                            storeProductsPrefesEditor.commit();

                            for (int a = 0; a < promoted_products.length(); a++) {
                                JSONObject productObj = promoted_products.getJSONObject(a);
                                String BoothImage = productObj.getString("BoothImage");
                                String CategoryID = productObj.getString("CategoryID");
                                String CategoryName = productObj.getString("CategoryName");
                                String CityName = productObj.getString("CityName");
                                String CommentCount = productObj.getString("CommentCount");
                                String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                String Currency = productObj.getString("Currency");
                                String CurrencySymbol = productObj.getString("CurrencySymbol");
                                String BoothName = productObj.getString("BoothName");
                                String LikesCount = productObj.getString("LikesCount");
                                String OutOfStock = productObj.getString("OutOfStock");
                                String ProductDescription = productObj.getString("ProductDescription");
                                String ProductID = productObj.getString("ProductID");
                                String ProductPrice = productObj.getString("ProductPrice");
                                String ProductType = productObj.getString("ProductType");
                                String ProductVideo = productObj.getString("ProductVideo");
                                String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                String Title = productObj.getString("Title");
                                String UserID = productObj.getString("UserID");
                                String UserName = productObj.getString("UserName");
                                String SubCategoryName = productObj.getString("SubCategoryName");
                                String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                String ProductBrandName = productObj.getString("ProductBrandName");
                                String CreatedAt = productObj.getString("CreatedAt");
                                String PromotionImage = productObj.getString("PromotionImage");
                                String PromotionDescription = productObj.getString("PromotionTitle");
                                String BoothUserName = productObj.getString("BoothUserName");
                                String PromotionType = productObj.getString("PromotionType");
                                String PromotionUrl = productObj.getString("PromotionUrl");

                                promotedProductImagesData = new ArrayList<>();
                                JSONArray productImages = productObj.getJSONArray("ProductImages");
                                for (int k = 0; k < productImages.length(); k++) {
                                    JSONObject imagesObj = productImages.getJSONObject(k);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    promotedProductImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                promotedProductsData.add(new PromotedProductsData(PromotionType, PromotionUrl, PromotionDescription, PromotionImage, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, BoothName,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, BoothUserName));
                            }

                            if (productsData.isEmpty()) {
                                no_data_found.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            } else {
                                no_data_found.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }

                            layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            homeCateRecyclAdapter = new AdapterBoothHome(getActivity(), productsData, new AdapterBoothHome.CRLCallbacks() {
                                @Override
                                public void onItemClick(int position) {
//                                        Toast.makeText(getContext(),"item "+position, Toast.LENGTH_SHORT).show();

                                }
                            });
                            recyclerView.setAdapter(homeCateRecyclAdapter);
                            recyclerView.setOnScrollListener(recyclerViewOnScrollListener);

                            progressBar.setVisibility(View.GONE);
                            CustomLoader.dialog.dismiss();
                            bottomNavigationView.setClickable(true);

                        } else {
                            bottomNavigationView.setClickable(true);
                            CustomLoader.dialog.dismiss();
                            progressBar.setVisibility(View.GONE);
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }                        }
                    } catch (JSONException e) {
                        bottomNavigationView.setClickable(true);
                        CustomLoader.dialog.dismiss();
                        progressBar.setVisibility(View.GONE);

                        e.printStackTrace();
                    }
                } else {
                    bottomNavigationView.setClickable(true);
                    CustomLoader.dialog.dismiss();
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getProductsApiGrid() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", "0");
        body.put("Type", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.HomeProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            productsDataGrid.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
                                String ItemType = productObj.getString("ItemType");
                                if (ItemType.equals("Product")) {
                                    String BoothImage = productObj.getString("BoothImage");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CityName = productObj.getString("CityName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                    String Currency = productObj.getString("Currency");
                                    String CurrencySymbol = productObj.getString("CurrencySymbol");
                                    String DeliveryTime = productObj.getString("DeliveryTime");
                                    String LikesCount = productObj.getString("LikesCount");
                                    String OutOfStock = productObj.getString("OutOfStock");
                                    String ProductDescription = productObj.getString("ProductDescription");
                                    String ProductID = productObj.getString("ProductID");
                                    String ProductPrice = productObj.getString("ProductPrice");
                                    String ProductType = productObj.getString("ProductType");
                                    String ProductVideo = productObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                    String Title = productObj.getString("Title");
                                    String UserID = productObj.getString("UserID");
                                    String UserName = productObj.getString("UserName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                    String ProductBrandName = productObj.getString("ProductBrandName");
                                    String CreatedAt = productObj.getString("CreatedAt");
                                    String IsLiked = productObj.getString("IsLiked");
                                    String BoothUserName = productObj.getString("BoothUserName");
                                    String IsPromotionApproved = productObj.getString("IsPromotionApproved");
                                    String IsPromotedProduct = productObj.getString("IsPromotedProduct");

                                    productImagesData = new ArrayList<>();
                                    JSONArray productImages = productObj.getJSONArray("ProductImages");
                                    for (int j = 0; j < productImages.length(); j++) {
                                        JSONObject imagesObj = productImages.getJSONObject(j);
                                        String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                        String ProductImage = imagesObj.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    productsDataGrid.add(new ProductsData("", ItemType, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                            LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                                }


                            }

//                            gridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                            HomeCateGridAdapter adapter = new HomeCateGridAdapter("BoothHome", getContext(), productsDataGrid, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
//                            gridView.setAdapter(adapter);
//                            progressBar.setVisibility(View.GONE);

                        } else {
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_LONG).show();
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

    private void refreshProducts() {
        bottomNavigationView.setClickable(false);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", "0");
        body.put("Type", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.HomeProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
//                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            swipeRefreshLayout.setRefreshing(false);
                            productsData.clear();
                            promotedProductsData.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            JSONArray promoted_products = jsonObject.getJSONArray("promoted_products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
                                String ItemType = productObj.getString("ItemType");
                                if (ItemType.equals("Product")) {
                                    String BoothImage = productObj.getString("BoothImage");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CityName = productObj.getString("CityName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                    String Currency = productObj.getString("Currency");
                                    String CurrencySymbol = productObj.getString("CurrencySymbol");
                                    String BoothName = productObj.getString("BoothName");
                                    String LikesCount = productObj.getString("LikesCount");
                                    String OutOfStock = productObj.getString("OutOfStock");
                                    String ProductDescription = productObj.getString("ProductDescription");
                                    String ProductID = productObj.getString("ProductID");
                                    String ProductPrice = productObj.getString("ProductPrice");
                                    String ProductType = productObj.getString("ProductType");
                                    String ProductVideo = productObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                    String Title = productObj.getString("Title");
                                    String UserID = productObj.getString("UserID");
                                    String UserName = productObj.getString("UserName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                    String ProductBrandName = productObj.getString("ProductBrandName");
                                    String CreatedAt = productObj.getString("CreatedAt");
                                    String IsLiked = productObj.getString("IsLiked");
                                    String BoothUserName = productObj.getString("BoothUserName");
                                    String IsPromotionApproved = productObj.getString("IsPromotionApproved");
                                    String IsPromotedProduct = productObj.getString("IsPromotedProduct");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray productImages = productObj.getJSONArray("ProductImages");
                                    for (int j = 0; j < productImages.length(); j++) {
                                        JSONObject imagesObj = productImages.getJSONObject(j);
                                        String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                        String ProductImage = imagesObj.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    productsData.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, BoothName,
                                            LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                                } else if (ItemType.equals("Question")) {
                                    String QuestionID = productObj.getString("QuestionID");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String QuestionDescription = productObj.getString("QuestionDescription");
                                    String QuestionAskedAt = productObj.getString("QuestionAskedAt");
                                    String UserImage = productObj.getString("UserImage");
                                    String UserName = productObj.getString("UserName");
                                    String UserCityName = productObj.getString("UserCityName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String UserID = productObj.getString("UserID");
                                    String BoothUserName = productObj.getString("BoothUserName");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray QuestionImages = productObj.getJSONArray("QuestionImages");
                                    for (int j = 0; j < QuestionImages.length(); j++) {
                                        JSONObject imagesObj = QuestionImages.getJSONObject(j);
                                        String CompressedImage = imagesObj.getString("CompressedImage");
                                        String Image = imagesObj.getString("Image");

                                        productImagesData.add(new ProductImagesData(CompressedImage, Image));
                                    }

                                    productsData.add(new ProductsData(QuestionID, ItemType, UserImage, CategoryID, CategoryName, UserCityName, CommentCount, "", "", "", "",
                                            "", "", QuestionDescription, "", "", "", "", "", "", UserID, UserName, SubCategoryName, "", "", QuestionAskedAt, "0", productImagesData, "", BoothUserName, ""));

                                }


                            }
                            for (int a = 0; a < promoted_products.length(); a++) {
                                JSONObject productObj = promoted_products.getJSONObject(a);
                                String BoothImage = productObj.getString("BoothImage");
                                String CategoryID = productObj.getString("CategoryID");
                                String CategoryName = productObj.getString("CategoryName");
                                String CityName = productObj.getString("CityName");
                                String CommentCount = productObj.getString("CommentCount");
                                String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                String Currency = productObj.getString("Currency");
                                String CurrencySymbol = productObj.getString("CurrencySymbol");
                                String DeliveryTime = productObj.getString("DeliveryTime");
                                String LikesCount = productObj.getString("LikesCount");
                                String OutOfStock = productObj.getString("OutOfStock");
                                String ProductDescription = productObj.getString("ProductDescription");
                                String ProductID = productObj.getString("ProductID");
                                String ProductPrice = productObj.getString("ProductPrice");
                                String ProductType = productObj.getString("ProductType");
                                String ProductVideo = productObj.getString("ProductVideo");
                                String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                String Title = productObj.getString("Title");
                                String UserID = productObj.getString("UserID");
                                String UserName = productObj.getString("UserName");
                                String SubCategoryName = productObj.getString("SubCategoryName");
                                String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                String ProductBrandName = productObj.getString("ProductBrandName");
                                String CreatedAt = productObj.getString("CreatedAt");
                                String PromotionImage = productObj.getString("PromotionImage");
                                String PromotionDescription = productObj.getString("PromotionTitle");
                                String BoothUserName = productObj.getString("BoothUserName");
                                String PromotionType = productObj.getString("PromotionType");
                                String PromotionUrl = productObj.getString("PromotionUrl");

                                promotedProductImagesData = new ArrayList<>();
                                JSONArray productImages = productObj.getJSONArray("ProductImages");
                                for (int k = 0; k < productImages.length(); k++) {
                                    JSONObject imagesObj = productImages.getJSONObject(k);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    promotedProductImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                promotedProductsData.add(new PromotedProductsData(PromotionType, PromotionUrl, PromotionDescription, PromotionImage, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, BoothUserName));
                            }

                            // Promoted Products View Pager
//                            if (!promotedProductsData.isEmpty()) {
//                                HomeHomePromotedAdapter cateViewPagerAdapter = new HomeHomePromotedAdapter(getActivity(), promotedProductsData);
//                                viewPager.setClipToPadding(false);
////                                viewPager.setOffscreenPageLimit(2);
//                                viewPager.setPadding(0, 0, 0, 0);
//                                viewPager.setPageMargin(0);
//                                viewPager.setAdapter(cateViewPagerAdapter);
//                            } else {
//                                viewPager.setVisibility(View.GONE);
//                            }
                            // Promoted Products View Pager

                            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            homeCateRecyclAdapter = new AdapterBoothHome(getContext(), productsData, new AdapterBoothHome.CRLCallbacks() {
                                @Override
                                public void onItemClick(int position) {
//
                                }
                            });
                            recyclerView.setAdapter(homeCateRecyclAdapter);
                            recyclerView.setOnScrollListener(recyclerViewOnScrollListener);

                            bottomNavigationView.setClickable(true);
                        } else {
                            bottomNavigationView.setClickable(true);
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }                        }
                    } catch (JSONException e) {
                        bottomNavigationView.setClickable(true);
                        e.printStackTrace();
                    }
                } else {
                    bottomNavigationView.setClickable(true);
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            Log.e("scroll", String.valueOf(((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            storeProductsPrefesEditor.putString("Boothposition", String.valueOf(((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition()));
            storeProductsPrefesEditor.commit();
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
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", String.valueOf(AdapterBoothHome.size));
        body.put("LastState", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.HomeProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {

                        isLoading = true;
                        ArrayList<ProductsData> test = new ArrayList<>();

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONArray products = jsonObject.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
                                String ItemType = productObj.getString("ItemType");
                                if (ItemType.equals("Product")) {
                                    String BoothImage = productObj.getString("BoothImage");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CityName = productObj.getString("CityName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String CompressedBoothImage = productObj.getString("CompressedBoothImage");
                                    String Currency = productObj.getString("Currency");
                                    String CurrencySymbol = productObj.getString("CurrencySymbol");
                                    String DeliveryTime = productObj.getString("DeliveryTime");
                                    String LikesCount = productObj.getString("LikesCount");
                                    String OutOfStock = productObj.getString("OutOfStock");
                                    String ProductDescription = productObj.getString("ProductDescription");
                                    String ProductID = productObj.getString("ProductID");
                                    String ProductPrice = productObj.getString("ProductPrice");
                                    String ProductType = productObj.getString("ProductType");
                                    String ProductVideo = productObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = productObj.getString("ProductVideoThumbnail");
                                    String Title = productObj.getString("Title");
                                    String UserID = productObj.getString("UserID");
                                    String UserName = productObj.getString("UserName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String SubSubCategoryName = productObj.getString("SubSubCategoryName");
                                    String ProductBrandName = productObj.getString("ProductBrandName");
                                    String CreatedAt = productObj.getString("CreatedAt");
                                    String IsLiked = productObj.getString("IsLiked");
                                    String BoothUserName = productObj.getString("BoothUserName");
                                    String IsPromotionApproved = productObj.getString("IsPromotionApproved");
                                    String IsPromotedProduct = productObj.getString("IsPromotedProduct");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray productImages = productObj.getJSONArray("ProductImages");
                                    for (int j = 0; j < productImages.length(); j++) {
                                        JSONObject imagesObj = productImages.getJSONObject(j);
                                        String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                        String ProductImage = imagesObj.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    productsData.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                            LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                                } else if (ItemType.equals("Question")) {
                                    String QuestionID = productObj.getString("QuestionID");
                                    String CategoryID = productObj.getString("CategoryID");
                                    String QuestionDescription = productObj.getString("QuestionDescription");
                                    String QuestionAskedAt = productObj.getString("QuestionAskedAt");
                                    String UserImage = productObj.getString("UserImage");
                                    String UserName = productObj.getString("UserName");
                                    String UserCityName = productObj.getString("UserCityName");
                                    String SubCategoryName = productObj.getString("SubCategoryName");
                                    String CategoryName = productObj.getString("CategoryName");
                                    String CommentCount = productObj.getString("CommentCount");
                                    String UserID = productObj.getString("UserID");
                                    String BoothUserName = productObj.getString("BoothUserName");

//                                mEditor.putString("ProductID",ProductID).commit();

                                    productImagesData = new ArrayList<>();
                                    JSONArray QuestionImages = productObj.getJSONArray("QuestionImages");
                                    for (int j = 0; j < QuestionImages.length(); j++) {
                                        JSONObject imagesObj = QuestionImages.getJSONObject(j);
                                        String CompressedImage = imagesObj.getString("CompressedImage");
                                        String Image = imagesObj.getString("Image");

                                        productImagesData.add(new ProductImagesData(CompressedImage, Image));
                                    }

                                    productsData.add(new ProductsData(QuestionID, ItemType, UserImage, CategoryID, CategoryName, UserCityName, CommentCount, "", "", "", "",
                                            "", "", QuestionDescription, "", "", "", "", "", "", UserID, UserName, SubCategoryName, "", "", QuestionAskedAt, "0", productImagesData, "", BoothUserName, ""));
                                    productsDataGrid.add(new ProductsData(QuestionID, ItemType, UserImage, CategoryID, CategoryName, UserCityName, CommentCount, "", "", "", "",
                                            "", "", QuestionDescription, "", "", "", "", "", "", UserID, UserName, SubCategoryName, "", "", QuestionAskedAt, "0", productImagesData, "", BoothUserName, ""));

                                }


                            }

                            String json = gson.toJson(productsData);
                            storeProductsPrefesEditor.putString(ProductsPREFERENCES, json);
                            storeProductsPrefesEditor.commit();

                            progressBar.setVisibility(View.GONE);
                            homeCateRecyclAdapter.addfeed(test);

                        } else {
                            progressBar.setVisibility(View.GONE);
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mEditor.putString("activity", "home_home").commit();

    }
}