package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schopfen.Booth.Adapters.ApproveItemsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserSummaryOrder extends AppCompatActivity {

    TextView payNow, total_items_count, total_price, vat_percentage, vat_price, grand_total, order_name, delivery_total_items, delivery_charges, discount_textview,
            promo_apply, promo_discount;
    RelativeLayout cod_payment, mada_payment, visa_payment;
    EditText promo_edit_text;
    RecyclerView product_list;

    String item_total_amount, orderitemsfromIntent, username, address, mobileNumber, email, longitude, latitude, boothname, totalitems, vatpercentage,
            additionaldeliverycharges, actualdeliverycharges, discount, orderRequestID, deliverycharges, PromoDiscountAvailed, activity, activity2, userID;
    ArrayList<OrderItemsData> orderItems = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    LinearLayout progressbar;
    ScrollView scrollView;

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
            setContentView(R.layout.activity_user_summary_order);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Initialization();
            activity = getIntent().getStringExtra("activity");

            progressbar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            if (activity.equals("normal")){
                orderitemsfromIntent = getIntent().getStringExtra("array");
                username = getIntent().getStringExtra("username");
                address = getIntent().getStringExtra("address");
                mobileNumber = getIntent().getStringExtra("mobile");
                email = getIntent().getStringExtra("email");
                longitude = getIntent().getStringExtra("longitude");
                latitude = getIntent().getStringExtra("latitude");
                boothname = getIntent().getStringExtra("boothname");
                totalitems = getIntent().getStringExtra("totalitems");
                vatpercentage = getIntent().getStringExtra("vatpercentage");
                actualdeliverycharges = getIntent().getStringExtra("actualdeliverycharges");
                additionaldeliverycharges = getIntent().getStringExtra("additionaldeliverycharges");
                discount = getIntent().getStringExtra("discount");
                orderRequestID = getIntent().getStringExtra("orderRequestID");
                activity2 = getIntent().getStringExtra("activity2");
                userID = getIntent().getStringExtra("UserID");

                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<OrderItemsData>>() {}.getType();
                orderItems = gson.fromJson(orderitemsfromIntent, type);

                total_items_count.setText("(" + totalitems + ")");
                float totalprice = 0;
                for (int i = 0; i < orderItems.size(); i++) {
                    float propricefortotal = Float.parseFloat(orderItems.get(i).getProductPrice()) * Integer.parseInt(orderItems.get(i).getQuantity());
                    totalprice = totalprice + propricefortotal;
                    Log.e("Priceis", String.valueOf(totalprice));
                }

                total_price.setText(String.valueOf(totalprice) + " " + orderItems.get(0).getCurrencySymbol());
                item_total_amount = String.valueOf(totalprice - Float.valueOf(discount));
                vat_percentage.setText("(" + vatpercentage + " %" + ")");
                float VatPrice = ((totalprice - Float.valueOf(discount)) * Float.valueOf(vatpercentage)/100);
                vat_price.setText(String.valueOf(VatPrice) + " " + orderItems.get(0).getCurrencySymbol());
                order_name.setText("@" + boothname);

                delivery_total_items.setText("(" + totalitems + ")");
                deliverycharges = String.valueOf(Float.valueOf(actualdeliverycharges) + Float.valueOf(additionaldeliverycharges));
                delivery_charges.setText(deliverycharges + " " + orderItems.get(0).getCurrencySymbol());
                discount_textview.setText("-" + discount + " " + orderItems.get(0).getCurrencySymbol());

                grand_total.setText(String.valueOf(totalprice + VatPrice - Float.valueOf(discount) + Float.valueOf(deliverycharges)) + " " + orderItems.get(0).getCurrencySymbol());

                product_list.setLayoutManager(new LinearLayoutManager(this));
                ApproveItemsAdapter adapter = new ApproveItemsAdapter(this, orderItems, activity2);
                product_list.setAdapter(adapter);
                progressbar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                order_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserSummaryOrder.this, BoothProfileActivity.class);
                        mEditor.putString("Booth", "Other").apply();
                        intent.putExtra("OtherUserID", userID);
                        startActivity(intent);
                    }
                });

            }else {
                orderRequestID = getIntent().getStringExtra("orderRequestID");
                activity2 = getIntent().getStringExtra("activity2");
                OrderDetailApi();
            }

            cod_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            mada_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            visa_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    private void OrderDetailApi(){
        CustomLoader.showDialog(UserSummaryOrder.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", orderRequestID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getOrderRequestDetail, UserSummaryOrder.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("orderDetail", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            JSONObject ordersObj = jsonObject.getJSONObject("order_request");
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

                            orderItems = new ArrayList<>();
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

                                orderItems.add(new OrderItemsData(OrderItemID, OrderRequestIDOrderItem, ProductID, Quantity, Price, ProductTitle, UserIDOrderItem, CategoryID,
                                        ProductVideo, ProductVideoThumbnail, ProductPrice, Currency, CurrencySymbol, DeliveryCharges, DeliveryTime, ProductType,
                                        OutOfStock, IsPromotedProduct, ProductPromotionExpiresAt, IsPromotionApproved, SortOrder, Hide, IsActive, CreatedAt, UpdatedAt,
                                        CreatedBy, UpdatedBy, ProductImage));
                            }

                            total_items_count.setText("(" + String.valueOf(orderItems.size()) + getResources().getString(R.string.items) + ")");
                            float totalprice = 0;
                            for (int i = 0; i < orderItems.size(); i++) {
                                float propricefortotal = Float.parseFloat(orderItems.get(i).getProductPrice()) * Integer.parseInt(orderItems.get(i).getQuantity());
                                totalprice = totalprice + propricefortotal;
                                Log.e("Priceis", String.valueOf(totalprice));
                            }

                            total_price.setText(String.valueOf(totalprice) + " " + orderItems.get(0).getCurrencySymbol());
                            item_total_amount = String.valueOf(totalprice - Float.valueOf(Discount));
                            vat_percentage.setText("(" + VatPercentageApplied + " %" + ")");
                            float VatPrice = ((totalprice - Float.valueOf(Discount)) * Float.valueOf(VatPercentageApplied)/100);
                            vat_price.setText(VatAmountApplied + " " + orderItems.get(0).getCurrencySymbol());
                            order_name.setText("@" + BoothUserName);

                            delivery_total_items.setText("(" + String.valueOf(orderItems.size()) + getResources().getString(R.string.items) + ")");
                            deliverycharges = String.valueOf(Float.valueOf(ActualDeliveryCharges) + Float.valueOf(AdditionalDeliveryCharges));
                            delivery_charges.setText(deliverycharges + " " + orderItems.get(0).getCurrencySymbol());
                            discount_textview.setText("-" + Discount + " " + orderItems.get(0).getCurrencySymbol());

                            grand_total.setText(String.valueOf(totalprice + Float.valueOf(VatAmountApplied) - Float.valueOf(Discount) + Float.valueOf(deliverycharges)) + " " + orderItems.get(0).getCurrencySymbol());

                            product_list.setLayoutManager(new LinearLayoutManager(UserSummaryOrder.this));
                            ApproveItemsAdapter adapter = new ApproveItemsAdapter(UserSummaryOrder.this, orderItems, activity2);
                            product_list.setAdapter(adapter);

                            progressbar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                            order_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(UserSummaryOrder.this, BoothProfileActivity.class);
                                    mEditor.putString("Booth", "Other").apply();
                                    intent.putExtra("OtherUserID", BoothID);
                                    startActivity(intent);
                                }
                            });


                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(UserSummaryOrder.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                            progressbar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                        progressbar.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(UserSummaryOrder.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                    progressbar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void Initialization(){
        payNow = findViewById(R.id.pay_now);
        cod_payment = findViewById(R.id.cod_payment);
        mada_payment = findViewById(R.id.mada_payment);
        visa_payment = findViewById(R.id.visa_payment);
        product_list = findViewById(R.id.pay_product_list);
        total_items_count = findViewById(R.id.pay_totalItemsCount);
        total_price = findViewById(R.id.pay_totalPrice);
        vat_percentage = findViewById(R.id.pay_vatPercentage);
        vat_price = findViewById(R.id.pay_vatPrice);
        grand_total = findViewById(R.id.pay_grand_total);
        order_name = findViewById(R.id.pay_order_name);
        delivery_total_items = findViewById(R.id.pay_delivery_totalItemsCount);
        delivery_charges = findViewById(R.id.pay_delivery_charges);
        discount_textview = findViewById(R.id.pay_discount);
        promo_apply = findViewById(R.id.pay_promo_apply);
        promo_discount = findViewById(R.id.pay_code_discount);
        progressbar = findViewById(R.id.progressbar);
        scrollView = findViewById(R.id.scroll_view);
        promo_edit_text = findViewById(R.id.pay_promo_code_edit_text);
    }
}