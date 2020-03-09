package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.schopfen.Booth.Adapters.Fragment_Cancelled_Order_Adapter;
import com.schopfen.Booth.Adapters.Fragment_Completed_Order_Adapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.Models.OrderRequestsData;
import com.schopfen.Booth.Models.OrdersData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserCancelledOrders extends Fragment {

    RecyclerView recyclerView;
    Fragment_Cancelled_Order_Adapter adapter;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    ArrayList<OrdersData> ordersData;
    ArrayList<OrderItemsData> orderItemsData;
    ArrayList<OrderRequestsData> orderRequestsData;
    SwipeRefreshLayout swipeRefreshLayout;
    RelativeLayout no_data_found;

    String laststate;
    ProgressBar order_progress;

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            getOrdersForUser();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_cancelled_orders, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        laststate = sharedpreferences.getString("LastState", "");

        recyclerView = view.findViewById(R.id.rv_cancelledorders);
        swipeRefreshLayout = view.findViewById(R.id.order_swipeRefresh);
        no_data_found = view.findViewById(R.id.no_data_found);
        order_progress = view.findViewById(R.id.order_progress);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setItemViewCacheSize(25);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        getOrdersForUser();

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getOrdersForUserRefresh();
                    }
                }
        );

    }

    private void getOrdersForUser(){

        order_progress.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Type", laststate);
        body.put("OrderStatus", "Cancelled");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETORDERS, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetOrdersForUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            ordersData = new ArrayList<>();
                            JSONArray orders = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject ordersObj = orders.getJSONObject(i);
                                String OrderRequestID = ordersObj.getString("OrderRequestID");
                                String OrderID = ordersObj.getString("OrderID");
                                String OrderTrackID = ordersObj.getString("OrderTrackID");
                                String OrderStatusID = ordersObj.getString("OrderStatusID");
                                String UserID = ordersObj.getString("UserID");
                                String BoothID = ordersObj.getString("BoothID");
                                String OrderLastStatusID = ordersObj.getString("OrderLastStatusID");
                                String OrderRequestVerificationCode = ordersObj.getString("OrderRequestVerificationCode");
                                String TotalAmount = ordersObj.getString("TotalAmount");
                                String ActualDeliveryCharges = ordersObj.getString("ActualDeliveryCharges");
                                String AdditionalDeliveryCharges = ordersObj.getString("AdditionalDeliveryCharges");
                                String Discount = ordersObj.getString("Discount");
                                String VatPercentageApplied = ordersObj.getString("VatPercentageApplied");
                                String VatAmountApplied = ordersObj.getString("VatAmountApplied");
                                String GrandTotal = ordersObj.getString("GrandTotal");
                                String BoothImage = ordersObj.getString("BoothImage");
                                String BoothUserName = ordersObj.getString("BoothUserName");
                                String BoothEmail = ordersObj.getString("BoothEmail");
                                String BoothMobile = ordersObj.getString("BoothMobile");
                                String BoothName = ordersObj.getString("BoothName");
                                String UserImage = ordersObj.getString("UserImage");
                                String UserName = ordersObj.getString("UserName");
                                String UserEmail = ordersObj.getString("UserEmail");
                                String UserMobile = ordersObj.getString("UserMobile");
                                String FullName = ordersObj.getString("FullName");
                                String OrderStatus = ordersObj.getString("OrderStatus");
                                String BoothCityTitle = ordersObj.getString("BoothCityTitle");
                                String UserCityTitle = ordersObj.getString("UserCityTitle");
                                String OrderReceivedAt = ordersObj.getString("OrderReceivedAt");
                                String AddressTitle = ordersObj.getString("AddressTitle");
                                String RecipientName = ordersObj.getString("RecipientName");
                                String AddressEmail = ordersObj.getString("AddressEmail");
                                String AddressMobile = ordersObj.getString("AddressMobile");
                                String AddressGender = ordersObj.getString("AddressGender");
                                String ApartmentNo = ordersObj.getString("ApartmentNo");
                                String Address1 = ordersObj.getString("Address1");
                                String Address2 = ordersObj.getString("Address2");
                                String AddressCity = ordersObj.getString("AddressCity");
                                String AddressLatitude = ordersObj.getString("AddressLatitude");
                                String AddressLongitude = ordersObj.getString("AddressLongitude");
                                String AddressIsDefault = ordersObj.getString("AddressIsDefault");
                                String VatPercentage = ordersObj.getString("VatPercentage");
                                String DeliveryDate = ordersObj.getString("DeliveryDate");

                                orderItemsData = new ArrayList<>();
                                JSONArray OrderItems = ordersObj.getJSONArray("OrderItems");
                                for (int k = 0; k < OrderItems.length(); k++) {
                                    JSONObject orderItemsObj = OrderItems.getJSONObject(k);
                                    String OrderItemID = orderItemsObj.getString("OrderItemID");
                                    String OrderRequestIDOrderItem = orderItemsObj.getString("OrderRequestID");
                                    String ProductID = orderItemsObj.getString("ProductID");
                                    String Quantity = orderItemsObj.getString("Quantity");
                                    String Price = orderItemsObj.getString("Price");
                                    String ProductTitle = orderItemsObj.getString("ProductTitle");
                                    String UserIDOrderItem = orderItemsObj.getString("UserID");
                                    String CategoryID = orderItemsObj.getString("CategoryID");
                                    String ProductVideo = orderItemsObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = orderItemsObj.getString("ProductVideoThumbnail");
                                    String ProductPrice = orderItemsObj.getString("ProductPrice");
                                    String Currency = orderItemsObj.getString("Currency");
                                    String CurrencySymbol = orderItemsObj.getString("CurrencySymbol");
                                    String DeliveryCharges = orderItemsObj.getString("DeliveryCharges");
                                    String DeliveryTime = orderItemsObj.getString("DeliveryTime");
                                    String ProductType = orderItemsObj.getString("ProductType");
                                    String OutOfStock = orderItemsObj.getString("OutOfStock");
                                    String IsPromotedProduct = orderItemsObj.getString("IsPromotedProduct");
                                    String ProductPromotionExpiresAt = orderItemsObj.getString("ProductPromotionExpiresAt");
                                    String IsPromotionApproved = orderItemsObj.getString("IsPromotionApproved");
                                    String SortOrder = orderItemsObj.getString("SortOrder");
                                    String Hide = orderItemsObj.getString("Hide");
                                    String IsActive = orderItemsObj.getString("IsActive");
                                    String CreatedAt = orderItemsObj.getString("CreatedAt");
                                    String UpdatedAt = orderItemsObj.getString("UpdatedAt");
                                    String CreatedBy = orderItemsObj.getString("CreatedBy");
                                    String UpdatedBy = orderItemsObj.getString("UpdatedBy");
                                    String ProductImage = orderItemsObj.getString("ProductImage");

                                    orderItemsData.add(new OrderItemsData(OrderItemID, OrderRequestIDOrderItem, ProductID, Quantity, Price, ProductTitle, UserIDOrderItem, CategoryID,
                                            ProductVideo, ProductVideoThumbnail, ProductPrice, Currency, CurrencySymbol, DeliveryCharges, DeliveryTime, ProductType,
                                            OutOfStock, IsPromotedProduct, ProductPromotionExpiresAt, IsPromotionApproved, SortOrder, Hide, IsActive, CreatedAt, UpdatedAt,
                                            CreatedBy, UpdatedBy, ProductImage));
                                }
                                ordersData.add(new OrdersData(DeliveryDate, OrderRequestID, OrderID, OrderTrackID, OrderStatusID, BoothID, OrderLastStatusID, OrderRequestVerificationCode,
                                        TotalAmount, ActualDeliveryCharges, AdditionalDeliveryCharges, Discount, VatPercentageApplied, VatAmountApplied, GrandTotal, BoothImage,
                                        BoothUserName, BoothEmail, BoothMobile, BoothName, UserID, UserImage, UserName, UserEmail, UserMobile, FullName, OrderStatus,
                                        BoothCityTitle, UserCityTitle, OrderReceivedAt, AddressTitle, RecipientName, AddressEmail, AddressMobile, AddressGender, ApartmentNo,
                                        Address1, Address2, AddressCity, AddressLatitude, AddressLongitude, AddressIsDefault, VatPercentage, orderItemsData));
                            }

                            if (ordersData.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                                swipeRefreshLayout.setVisibility(View.GONE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new Fragment_Cancelled_Order_Adapter(getActivity(), ordersData);
                            recyclerView.setAdapter(adapter);

                            order_progress.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        } else {

                            order_progress.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        order_progress.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                    order_progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void getOrdersForUserRefresh(){

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("Type", laststate);
        body.put("OrderStatus", "Cancelled");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETORDERS, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetOrdersForUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            swipeRefreshLayout.setRefreshing(false);
                            ordersData = new ArrayList<>();
                            JSONArray orders = jsonObject.getJSONArray("orders");
                            for (int i = 0; i < orders.length(); i++) {
                                JSONObject ordersObj = orders.getJSONObject(i);
                                String OrderRequestID = ordersObj.getString("OrderRequestID");
                                String OrderID = ordersObj.getString("OrderID");
                                String OrderTrackID = ordersObj.getString("OrderTrackID");
                                String OrderStatusID = ordersObj.getString("OrderStatusID");
                                String UserID = ordersObj.getString("UserID");
                                String BoothID = ordersObj.getString("BoothID");
                                String OrderLastStatusID = ordersObj.getString("OrderLastStatusID");
                                String OrderRequestVerificationCode = ordersObj.getString("OrderRequestVerificationCode");
                                String TotalAmount = ordersObj.getString("TotalAmount");
                                String ActualDeliveryCharges = ordersObj.getString("ActualDeliveryCharges");
                                String AdditionalDeliveryCharges = ordersObj.getString("AdditionalDeliveryCharges");
                                String Discount = ordersObj.getString("Discount");
                                String VatPercentageApplied = ordersObj.getString("VatPercentageApplied");
                                String VatAmountApplied = ordersObj.getString("VatAmountApplied");
                                String GrandTotal = ordersObj.getString("GrandTotal");
                                String BoothImage = ordersObj.getString("BoothImage");
                                String BoothUserName = ordersObj.getString("BoothUserName");
                                String BoothEmail = ordersObj.getString("BoothEmail");
                                String BoothMobile = ordersObj.getString("BoothMobile");
                                String BoothName = ordersObj.getString("BoothName");
                                String UserImage = ordersObj.getString("UserImage");
                                String UserName = ordersObj.getString("UserName");
                                String UserEmail = ordersObj.getString("UserEmail");
                                String UserMobile = ordersObj.getString("UserMobile");
                                String FullName = ordersObj.getString("FullName");
                                String OrderStatus = ordersObj.getString("OrderStatus");
                                String BoothCityTitle = ordersObj.getString("BoothCityTitle");
                                String UserCityTitle = ordersObj.getString("UserCityTitle");
                                String OrderReceivedAt = ordersObj.getString("OrderReceivedAt");
                                String AddressTitle = ordersObj.getString("AddressTitle");
                                String RecipientName = ordersObj.getString("RecipientName");
                                String AddressEmail = ordersObj.getString("AddressEmail");
                                String AddressMobile = ordersObj.getString("AddressMobile");
                                String AddressGender = ordersObj.getString("AddressGender");
                                String ApartmentNo = ordersObj.getString("ApartmentNo");
                                String Address1 = ordersObj.getString("Address1");
                                String Address2 = ordersObj.getString("Address2");
                                String AddressCity = ordersObj.getString("AddressCity");
                                String AddressLatitude = ordersObj.getString("AddressLatitude");
                                String AddressLongitude = ordersObj.getString("AddressLongitude");
                                String AddressIsDefault = ordersObj.getString("AddressIsDefault");
                                String VatPercentage = ordersObj.getString("VatPercentage");
                                String DeliveryDate = ordersObj.getString("DeliveryDate");

                                orderItemsData = new ArrayList<>();
                                JSONArray OrderItems = ordersObj.getJSONArray("OrderItems");
                                for (int k = 0; k < OrderItems.length(); k++) {
                                    JSONObject orderItemsObj = OrderItems.getJSONObject(k);
                                    String OrderItemID = orderItemsObj.getString("OrderItemID");
                                    String OrderRequestIDOrderItem = orderItemsObj.getString("OrderRequestID");
                                    String ProductID = orderItemsObj.getString("ProductID");
                                    String Quantity = orderItemsObj.getString("Quantity");
                                    String Price = orderItemsObj.getString("Price");
                                    String ProductTitle = orderItemsObj.getString("ProductTitle");
                                    String UserIDOrderItem = orderItemsObj.getString("UserID");
                                    String CategoryID = orderItemsObj.getString("CategoryID");
                                    String ProductVideo = orderItemsObj.getString("ProductVideo");
                                    String ProductVideoThumbnail = orderItemsObj.getString("ProductVideoThumbnail");
                                    String ProductPrice = orderItemsObj.getString("ProductPrice");
                                    String Currency = orderItemsObj.getString("Currency");
                                    String CurrencySymbol = orderItemsObj.getString("CurrencySymbol");
                                    String DeliveryCharges = orderItemsObj.getString("DeliveryCharges");
                                    String DeliveryTime = orderItemsObj.getString("DeliveryTime");
                                    String ProductType = orderItemsObj.getString("ProductType");
                                    String OutOfStock = orderItemsObj.getString("OutOfStock");
                                    String IsPromotedProduct = orderItemsObj.getString("IsPromotedProduct");
                                    String ProductPromotionExpiresAt = orderItemsObj.getString("ProductPromotionExpiresAt");
                                    String IsPromotionApproved = orderItemsObj.getString("IsPromotionApproved");
                                    String SortOrder = orderItemsObj.getString("SortOrder");
                                    String Hide = orderItemsObj.getString("Hide");
                                    String IsActive = orderItemsObj.getString("IsActive");
                                    String CreatedAt = orderItemsObj.getString("CreatedAt");
                                    String UpdatedAt = orderItemsObj.getString("UpdatedAt");
                                    String CreatedBy = orderItemsObj.getString("CreatedBy");
                                    String UpdatedBy = orderItemsObj.getString("UpdatedBy");
                                    String ProductImage = orderItemsObj.getString("ProductImage");

                                    orderItemsData.add(new OrderItemsData(OrderItemID, OrderRequestIDOrderItem, ProductID, Quantity, Price, ProductTitle, UserIDOrderItem, CategoryID,
                                            ProductVideo, ProductVideoThumbnail, ProductPrice, Currency, CurrencySymbol, DeliveryCharges, DeliveryTime, ProductType,
                                            OutOfStock, IsPromotedProduct, ProductPromotionExpiresAt, IsPromotionApproved, SortOrder, Hide, IsActive, CreatedAt, UpdatedAt,
                                            CreatedBy, UpdatedBy, ProductImage));
                                }
                                ordersData.add(new OrdersData(DeliveryDate, OrderRequestID, OrderID, OrderTrackID, OrderStatusID, BoothID, OrderLastStatusID, OrderRequestVerificationCode,
                                        TotalAmount, ActualDeliveryCharges, AdditionalDeliveryCharges, Discount, VatPercentageApplied, VatAmountApplied, GrandTotal, BoothImage,
                                        BoothUserName, BoothEmail, BoothMobile, BoothName, UserID, UserImage, UserName, UserEmail, UserMobile, FullName, OrderStatus,
                                        BoothCityTitle, UserCityTitle, OrderReceivedAt, AddressTitle, RecipientName, AddressEmail, AddressMobile, AddressGender, ApartmentNo,
                                        Address1, Address2, AddressCity, AddressLatitude, AddressLongitude, AddressIsDefault, VatPercentage, orderItemsData));
                            }

                            if (ordersData.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new Fragment_Cancelled_Order_Adapter(getActivity(), ordersData);
                            recyclerView.setAdapter(adapter);

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


}
