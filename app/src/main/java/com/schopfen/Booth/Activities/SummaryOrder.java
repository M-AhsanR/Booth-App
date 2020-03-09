package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
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
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class SummaryOrder extends FragmentActivity implements OnMapReadyCallback {
    TextView booth_name, address_line, phone_number, emailTextView, total_items_count, total_price, vat_percentage, vat_price, grand_total,
            rejectBtn, approveBtn, order_name;
    TextView deliver_charges, discountEditText;
    RecyclerView product_list;
    ArrayList<OrderItemsData> orderItems = new ArrayList<>();
    GoogleMap mMap;
    String orderitemsfromIntent, username, address, mobileNumber, email, longitude, latitude, boothname, totalitems, vatpercentage,
            deliverycharges, discount, orderRequestID, aditionaldeliverycharges, activity, userID;
    String grandTotal, total_price_forserver;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    LatLng user_location;
    LinearLayout btns_layout;
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
            setContentView(R.layout.activity_summary_order);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            Bundle extras = getIntent().getExtras();

            Initialization();

            activity = extras.getString("activity");

            progressbar.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);

            if (activity.equals("normal")){
                orderitemsfromIntent = extras.getString("array");
                username = extras.getString("username");
                address = extras.getString("address");
                mobileNumber = extras.getString("mobile");
                email = extras.getString("email");
                longitude = extras.getString("longitude");
                latitude = extras.getString("latitude");
                boothname = extras.getString("boothname");
                totalitems = extras.getString("totalitems");
                vatpercentage = extras.getString("vatpercentage");
                deliverycharges = extras.getString("deliverycharges");
                discount = extras.getString("discount");
                orderRequestID = extras.getString("orderRequestID");
                userID = getIntent().getStringExtra("UserID");
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<OrderItemsData>>() {
                }.getType();
                orderItems = gson.fromJson(orderitemsfromIntent, type);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }

                booth_name.setText("@" + username);
                address_line.setText(address);
                phone_number.setText(mobileNumber);
                emailTextView.setText(email);
                total_items_count.setText("(" + totalitems + "Items" + ")");
                float totalprice = 0;
                for (int i = 0; i < orderItems.size(); i++) {
                    float propricefortotal = Float.parseFloat(orderItems.get(i).getProductPrice()) * Integer.parseInt(orderItems.get(i).getQuantity());
                    totalprice = totalprice + propricefortotal;
                    Log.e("Priceis", String.valueOf(totalprice));
                }
                total_price.setText(String.valueOf(totalprice) + " " + orderItems.get(0).getCurrency());
                total_price_forserver = String.valueOf(totalprice);
                vat_percentage.setText("(" + vatpercentage + " %" + ")");
                float VatPrice = (totalprice * Float.valueOf(vatpercentage) / 100);
                vat_price.setText(String.valueOf(VatPrice));
                order_name.setText(boothname);
                deliver_charges.setText(deliverycharges);
                discountEditText.setText(discount);
                grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf(discount) + Float.valueOf(deliverycharges));
                grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());

                product_list.setLayoutManager(new LinearLayoutManager(this));
                ApproveItemsAdapter adapter = new ApproveItemsAdapter(this, orderItems, "SummaryOrder");
                product_list.setAdapter(adapter);

                discountEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        float totalprice = 0;
                        for (int i = 0; i < orderItems.size(); i++) {
                            float propricefortotal = Float.parseFloat(orderItems.get(i).getProductPrice()) * Integer.parseInt(orderItems.get(i).getQuantity());
                            totalprice = totalprice + propricefortotal;
                            Log.e("Priceis", String.valueOf(totalprice));
                        }
                        if (!editable.toString().isEmpty()) {
                            float VatPrice = ((totalprice - Float.valueOf(editable.toString())) * Float.valueOf(vatpercentage) / 100);
                            vat_price.setText(String.valueOf(VatPrice));
                        }
                    }
                });
                progressbar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);

                booth_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SummaryOrder.this, OthersProfileActivity.class);
                        mEditor.putString("Booth", "Other").apply();
                        intent.putExtra("OtherUserID", userID);
                        startActivity(intent);
                    }
                });

            }else {
                orderRequestID = extras.getString("orderRequestID");
                OrderDetailApi();
            }
        }else {
            setContentView(R.layout.no_internet_screen);
        }



    }

    private void OrderDetailApi(){
        CustomLoader.showDialog(SummaryOrder.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", orderRequestID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getOrderRequestDetail, SummaryOrder.this, body, headers, new ServerCallback() {
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

                            longitude = AddressLongitude;
                            latitude = AddressLatitude;

                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            if (mapFragment != null) {
                                mapFragment.getMapAsync(SummaryOrder.this);
                            }

                            booth_name.setText("@" + UserName);
                            address_line.setText(ApartmentNo + Address1 + Address2 + AddressCity);
                            phone_number.setText(BoothMobile);
                            emailTextView.setText(BoothEmail);
                            total_items_count.setText("(" + String.valueOf(orderItems.size()) + "Items" + ")");
                            float totalprice = 0;
                            for (int i = 0; i < orderItems.size(); i++) {
                                float propricefortotal = Float.parseFloat(orderItems.get(i).getProductPrice()) * Integer.parseInt(orderItems.get(i).getQuantity());
                                totalprice = totalprice + propricefortotal;
                                Log.e("Priceis", String.valueOf(totalprice));
                            }
                            total_price.setText(String.valueOf(totalprice) + " " + orderItems.get(0).getCurrency());
                            total_price_forserver = String.valueOf(totalprice);
                            vat_percentage.setText("(" + VatPercentageApplied + " %" + ")");
                            vat_price.setText(String.valueOf(VatAmountApplied));
                            order_name.setText(BoothName);
                            float deliveryCharges = 0;
                            for (int i = 0; i < orderItems.size(); i++) {
                                float charges = Float.valueOf(orderItems.get(i).getDeliveryCharges());
                                deliveryCharges = deliveryCharges + charges;
                            }

                            deliver_charges.setText(String.valueOf(deliveryCharges));
                            discountEditText.setText(Discount);
                            grandTotal = String.valueOf(totalprice + Float.valueOf(VatAmountApplied) - Float.valueOf(Discount) + deliveryCharges);
                            grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());

                            product_list.setLayoutManager(new LinearLayoutManager(SummaryOrder.this));
                            ApproveItemsAdapter adapter = new ApproveItemsAdapter(SummaryOrder.this, orderItems, "SummaryOrder");
                            product_list.setAdapter(adapter);

                            progressbar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                            booth_name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(SummaryOrder.this, OthersProfileActivity.class);
                                    mEditor.putString("Booth", "Other").apply();
                                    intent.putExtra("OtherUserID", UserID);
                                    startActivity(intent);
                                }
                            });

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(SummaryOrder.this, message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SummaryOrder.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                    progressbar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void ApproveOrder() {
        CustomLoader.showDialog(SummaryOrder.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", orderRequestID);
        body.put("OrderStatus", "2");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("Discount", discountEditText.getText().toString());
        if (!Float.valueOf(deliver_charges.getText().toString()).equals(Float.valueOf((deliverycharges)))) {
            if (Float.valueOf(deliver_charges.getText().toString()) > Float.valueOf((deliverycharges))) {
                body.put("AdditionalDeliveryCharges", String.valueOf(Float.valueOf(deliver_charges.getText().toString()) - Float.valueOf((deliverycharges))));
            } else if (Float.valueOf((deliverycharges)) > Float.valueOf(deliver_charges.getText().toString())) {
                body.put("AdditionalDeliveryCharges", String.valueOf(Float.valueOf((deliverycharges)) - Float.valueOf(deliver_charges.getText().toString())));
            }
        } else {
            body.put("AdditionalDeliveryCharges", "0.0");
        }
        body.put("ActualDeliveryCharges", deliverycharges);
        body.put("TotalAmount", total_price_forserver);
        body.put("VatPercentageApplied", vatpercentage);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, SummaryOrder.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Approve", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            final Dialog addressDialog = new Dialog(SummaryOrder.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            addressDialog.setContentView(R.layout.alert_text_layout);
                            Button ok = addressDialog.findViewById(R.id.btn_ok);
                            TextView address_text = addressDialog.findViewById(R.id.address_text);

                            address_text.setText(message);

                            addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addressDialog.show();

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressDialog.dismiss();
                                    finish();
                                }
                            });

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(SummaryOrder.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(SummaryOrder.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }


    private void Initialization() {
        product_list = findViewById(R.id.approve_product_list);
        booth_name = findViewById(R.id.approve_booth_name);
        address_line = findViewById(R.id.approve_address_line);
        phone_number = findViewById(R.id.approve_number);
        emailTextView = findViewById(R.id.approve_email);
        total_items_count = findViewById(R.id.approve_totalItemsCount);
        total_price = findViewById(R.id.approve_totalPrice);
        vat_percentage = findViewById(R.id.approve_vatPercentage);
        vat_price = findViewById(R.id.approve_vatPrice);
        grand_total = findViewById(R.id.approve_grand_total);
        rejectBtn = findViewById(R.id.reject_btn);
        approveBtn = findViewById(R.id.approve_btn);
        order_name = findViewById(R.id.approve_order_name);
        deliver_charges = findViewById(R.id.approve_delivery_charges);
        discountEditText = findViewById(R.id.approve_discount);
        btns_layout = findViewById(R.id.btns_layout);
        progressbar = findViewById(R.id.progressbar);
        scrollView = findViewById(R.id.scroll_view);
        btns_layout.setVisibility(View.GONE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.

        if (!latitude.equals("null") && !longitude.equals("null")) {
            user_location = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        } else {
            user_location = new LatLng(0.0, 0.0);
        }

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(user_location);

        googleMap.addMarker(markerOptions);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Double.valueOf(latitude) + "," + Double.valueOf(longitude) + "("+ username +")");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }else {
                    Toast.makeText(SummaryOrder.this, "Please install Google Maps!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(user_location)        // Sets the center of the map to Mountain View
                .zoom(17)              // Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(0)               // Sets the tilt of the camera to 30 degrees
                .build();              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
