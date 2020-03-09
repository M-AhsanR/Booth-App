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
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.schopfen.Booth.Activities.FollowingsActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Adapters.AdapterMainActivityHomeBooth;
import com.schopfen.Booth.Adapters.HomeCateGridAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.Models.PromotedProductsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Home_Booth_Fragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public static final String tag = "DropFragment";
    RecyclerView recyclerView;
    RecyclerView gridView;
    RelativeLayout no_data_found;
    LinearLayoutManager layoutManager;
    GridLayoutManager gridLayoutManager;
    ImageView listViewBtn;
    public static ViewPager viewPager;
    BottomNavigationView bottomNavigationView;

    public static ArrayList<PromotedProductsData> homeCateData = new ArrayList<>();
    ArrayList<ProductsData> productsData = new ArrayList<>();
    ArrayList<ProductsData> productsDataGrid = new ArrayList<>();
    ArrayList<ProductImagesData> productImagesData = new ArrayList<>();
    ArrayList<ProductImagesData> promotedProductImagesData;

    TextView categoriesCount;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean isLoading = true;
    boolean isLoadingGrid = true;
    AdapterMainActivityHomeBooth homeCateRecyclAdapter;
    HomeCateGridAdapter gridadapter;
    ProgressBar progressBar;
    public static boolean refresh = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment_booths, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        initiateViews();
        progressBar.setVisibility(View.VISIBLE);
        getProductsApi();
        getProductsApiGrid();

        mEditor.putString("activity", "home_booth").apply();

        getUserDetails();

        gridView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        categoriesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).apply();
                startActivity(new Intent(getActivity(), FollowingsActivity.class));
            }
        });
        listViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerView.getVisibility() == View.GONE){
                    listViewBtn.setBackgroundResource(R.drawable.grid_icon);
                    recyclerView.setVisibility(View.VISIBLE);
                    gridView.setVisibility(View.GONE);
                    Log.e("grid", "grid");
                }else if (recyclerView.getVisibility() == View.VISIBLE){
                    listViewBtn.setBackgroundResource(R.drawable.listview_icon);
                    recyclerView.setVisibility(View.GONE);
                    gridView.setVisibility(View.VISIBLE);
                    Log.e("recycler", "recycler");
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                            getProductsApi();
                            getProductsApiGrid();
                            getUserDetails();
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {

    }

    private void initiateViews(){
        recyclerView = getView().findViewById(R.id.rv_categories_hcf);
        no_data_found = getView().findViewById(R.id.no_data_found);
        gridView = getView().findViewById(R.id.gv_categories_hcf);
        listViewBtn = getView().findViewById(R.id.image);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        categoriesCount = getView().findViewById(R.id.boothsCount);
        swipeRefreshLayout = getView().findViewById(R.id.swipeRefresh);
        progressBar = getView().findViewById(R.id.progressbarboothhome);

        recyclerView.setItemViewCacheSize(25);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

//        nestedScrollView.setNestedScrollingEnabled(true);
//        recyclerView.setNestedScrollingEnabled(false);
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
                            String Verification = user_info.getString("Verification");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothName = user_info.getString("BoothName");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            mEditor.putString("UserOrderCount", UserOrderCount);
                            mEditor.putString("BoothOrderCount", BoothOrderCount);

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.commit();

                            categoriesCount.setText(String.valueOf(UserFollowedBooths) + " " + getResources().getString(R.string.boothtitle));

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
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getProductsApi(){
        bottomNavigationView.setClickable(false);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", "0");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BoothProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("HomeBoothProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            swipeRefreshLayout.setRefreshing(false);
                            productsData.clear();
                            productsData.add(new ProductsData("", "", "", "", "", "", "", "", "", "", "",
                                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "2", productImagesData, "", "", ""));
                            homeCateData.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            JSONArray promoted_products = jsonObject.getJSONArray("promoted_products");
                            for(int i=0; i<products.length(); i++){
                                JSONObject productObj = products.getJSONObject(i);
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
                                for(int j=0; j<productImages.length(); j++){
                                    JSONObject imagesObj = productImages.getJSONObject(j);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                productsData.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                            }
                            for(int a=0; a<promoted_products.length(); a++){
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
                                JSONArray productImages = productObj.getJSONArray("ProductImages");

                                promotedProductImagesData = new ArrayList<>();
                                for(int k=0; k<productImages.length(); k++){
                                    JSONObject imagesObj = productImages.getJSONObject(k);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    promotedProductImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                homeCateData.add(new PromotedProductsData(PromotionType, PromotionUrl, PromotionDescription, PromotionImage, BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, BoothName,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, BoothUserName));
                            }


                            // promoted Products

//                            if (!homeCateData.isEmpty()){
//                                HomeBoothPromotedAdapter cateViewPagerAdapter = new HomeBoothPromotedAdapter(getActivity(), homeCateData);
//                                viewPager.setClipToPadding(false);
//                                viewPager.setPadding(0, 0, 0, 0);
//                                viewPager.setPageMargin(0);
//                                viewPager.setAdapter(cateViewPagerAdapter);
//                            }else {
//                                viewPager.setVisibility(View.GONE);
//                            }


                            // promoted Products

                            if (productsData.isEmpty() || productsData.size() == 1){
                                no_data_found.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }


                            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            homeCateRecyclAdapter = new AdapterMainActivityHomeBooth(getContext(), productsData, new AdapterMainActivityHomeBooth.CRLCallbacks() {
                                @Override
                                public void onItemClick(int position) {
//                                        Toast.makeText(getContext(),"item "+position, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), ProductDetailsActivity.class);
                                    mEditor.putString("ProductID", productsData.get(position).getProductID()).commit();
                                    startActivity(intent);
                                }
                            });
                            recyclerView.setAdapter(homeCateRecyclAdapter);
                            recyclerView.setOnScrollListener(recyclerViewOnScrollListener);
                            // recyclerView.addItemDecoration(new DividerItemDecoration(getActivity() DividerItemDecoration.VERTICAL));
                            progressBar.setVisibility(View.GONE);
                            bottomNavigationView.setClickable(true);
                        }else {
                            bottomNavigationView.setClickable(true);
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
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }else {
                    bottomNavigationView.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getProductsApiGrid(){

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", "0");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BoothProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("HomeBoothProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            swipeRefreshLayout.setRefreshing(false);
                            productsDataGrid.clear();
//                            homeCateData.clear();
                            JSONArray products = jsonObject.getJSONArray("products");
                            JSONArray promoted_products = jsonObject.getJSONArray("promoted_products");
                            for(int i=0; i<products.length(); i++){
                                JSONObject productObj = products.getJSONObject(i);
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
                                for(int j=0; j<productImages.length(); j++){
                                    JSONObject imagesObj = productImages.getJSONObject(j);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                productsDataGrid.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                            }

                            if (productsDataGrid.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }

                            gridLayoutManager = new GridLayoutManager(getContext(), 3);
                            gridView.setLayoutManager(gridLayoutManager);
                            gridadapter = new HomeCateGridAdapter("HomeHome", getContext(), productsDataGrid, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            gridView.setAdapter(gridadapter);
//                            gridView.setOnScrollListener(recyclerViewOnScrollListenerGrid);
                            gridView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    gridLayoutManager = GridLayoutManager.class.cast(recyclerView.getLayoutManager());
                                    int totalItemCount = gridLayoutManager.getItemCount();
                                    int lastVisible = gridLayoutManager.findLastVisibleItemPosition();
                                    int pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                                    int visibleItemCount = gridLayoutManager.getChildCount();

                                    boolean endHasBeenReached = (visibleItemCount + pastVisiblesItems) >= totalItemCount;
                                    if (isLoadingGrid){
                                        if (totalItemCount > 0 && endHasBeenReached){
                                            //you have reached to the bottom of your recycler view
                                            Log.e("GridView", "LastPosition Visibled");
                                            isLoadingGrid = false;
                                            progressBar.setVisibility(View.VISIBLE);
                                            loadMoreItemsGrid();
                                        }
                                    }
                                }
                            });
                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
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
    private void loadMoreItemsGrid() {
        isLoadingGrid = false;


        PaginationApiGrid();
    }

    private void PaginationApiGrid() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", String.valueOf(productsDataGrid.size()));
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

                            ArrayList<ProductsData> test = new ArrayList<>();

                            JSONArray products = jsonObject.getJSONArray("products");
                            for(int i=0; i<products.length(); i++){
                                JSONObject productObj = products.getJSONObject(i);
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
                                for(int j=0; j<productImages.length(); j++){
                                    JSONObject imagesObj = productImages.getJSONObject(j);
                                    String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                    String ProductImage = imagesObj.getString("ProductImage");

                                    productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }

                                productsDataGrid.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));

                            }

                            progressBar.setVisibility(View.GONE);
                            gridadapter.addfeed(test);

                            isLoadingGrid = true;

                        } else {
                            progressBar.setVisibility(View.GONE);
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_LONG).show();
                        }
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

    private void PaginationApi() {

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Start", String.valueOf(AdapterMainActivityHomeBooth.size));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BoothProducts, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {

                        isLoading = true;
                        ArrayList<ProductsData> test=new ArrayList<>();

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONArray products = jsonObject.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject productObj = products.getJSONObject(i);
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

                                test.add(new ProductsData("", "", BoothImage, CategoryID, CategoryName, CityName, CommentCount, CompressedBoothImage, Currency, CurrencySymbol, DeliveryTime,
                                        LikesCount, OutOfStock, ProductDescription, ProductID, ProductPrice, ProductType, ProductVideoThumbnail, ProductVideo, Title, UserID, UserName, SubCategoryName, SubSubCategoryName, ProductBrandName, CreatedAt, "1", productImagesData, IsLiked, BoothUserName, IsPromotionApproved));
                            }

                            progressBar.setVisibility(View.GONE);
                            homeCateRecyclAdapter.addfeedbooth(test);

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
        mEditor.putString("activity", "home_booth").commit();
        if (refresh){
            progressBar.setVisibility(View.VISIBLE);
            getUserDetails();
            getProductsApi();
            refresh = false;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            if (refresh){
                progressBar.setVisibility(View.VISIBLE);
                getUserDetails();
                getProductsApi();
                refresh = false;
            }
        }
    }
}
