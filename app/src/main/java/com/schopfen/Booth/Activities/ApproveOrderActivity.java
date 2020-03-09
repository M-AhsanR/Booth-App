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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schopfen.Booth.Adapters.ApproveItemsAdapter;
import com.schopfen.Booth.Adapters.Fragment_Current_Order_Adapter;
import com.schopfen.Booth.Adapters.Order_Details_Adapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveOrderActivity extends FragmentActivity implements OnMapReadyCallback {

    TextView booth_name, address_line, phone_number, emailTextView, total_items_count, total_price, vat_percentage, vat_price, grand_total,
            rejectBtn, approveBtn, order_name;
    EditText deliver_charges, discountEditText, delivery_days;
    RecyclerView product_list;
    ArrayList<OrderItemsData> orderItems = new ArrayList<>();
    GoogleMap mMap;
    String orderitemsfromIntent, username, address, mobileNumber, email, longitude, latitude, boothname, totalitems, vatpercentage,
            deliverycharges, discount, orderRequestID, aditionaldeliverycharges;
    String grandTotal, total_price_forserver;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    LatLng user_location;
    float VatPrice;
    Animation shake;

    @Override
    protected void onStop() {
        super.onStop();
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
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isNetworkAvailable()){
            setContentView(R.layout.activity_approve_order);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
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
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<OrderItemsData>>() {
                }.getType();
                orderItems = gson.fromJson(orderitemsfromIntent, type);
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
            Initialization();
            booth_name.setText(username);
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
            VatPrice = (totalprice * Float.valueOf(vatpercentage) / 100);
            vat_price.setText(String.valueOf(VatPrice));
            order_name.setText(boothname);
            deliver_charges.setText(deliverycharges);
            discountEditText.setText(discount);
            grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf(discountEditText.getText().toString()) + Float.valueOf(deliverycharges));
            grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());
            product_list.setLayoutManager(new LinearLayoutManager(this));
            ApproveItemsAdapter adapter = new ApproveItemsAdapter(this, orderItems, "ApproveOrderActivity");
            product_list.setAdapter(adapter);
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            approveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (delivery_days.getText().toString().trim().length() == 0){
                        delivery_days.startAnimation(shake);
                        Toast.makeText(ApproveOrderActivity.this, getResources().getString(R.string.enterdeliverydays), Toast.LENGTH_SHORT).show();
                    }else {
                        if (!delivery_days.getText().toString().equals("0")){
                            ApproveOrder();
                        }
                    }
                }
            });
            deliver_charges.addTextChangedListener(new TextWatcher() {
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
                        grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf(discountEditText.getText().toString()) + Float.valueOf(editable.toString()));
                        grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());
                    } else {
                        grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf(discountEditText.getText().toString()) + Float.valueOf("0.0"));
                        grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());
                    }
                }
            });
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
                        grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf(discountEditText.getText().toString()) + Float.valueOf(deliver_charges.getText().toString()));
                        grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());
                    } else {
                        VatPrice = ((totalprice - Float.valueOf("0.0")) * Float.valueOf(vatpercentage) / 100);
                        vat_price.setText(String.valueOf(VatPrice));
                        grandTotal = String.valueOf(totalprice + VatPrice - Float.valueOf("0.0") + Float.valueOf(deliver_charges.getText().toString()));
                        grand_total.setText(grandTotal + " " + orderItems.get(0).getCurrency());
                    }
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void ApproveOrder() {
        CustomLoader.showDialog(ApproveOrderActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", orderRequestID);
        body.put("OrderStatus", "2");
        body.put("DeliveryDays", delivery_days.getText().toString());
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
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, ApproveOrderActivity.this, body, headers, new ServerCallback() {
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
                            final Dialog addressDialog = new Dialog(ApproveOrderActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                            Toast.makeText(ApproveOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(ApproveOrderActivity.this, ERROR, Toast.LENGTH_SHORT).show();
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
        delivery_days = findViewById(R.id.delivery_days);
        shake = AnimationUtils.loadAnimation(ApproveOrderActivity.this, R.anim.shake);
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
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(user_location)        // Sets the center of the map to Mountain View
                .zoom(17)              // Sets the zoom
                .bearing(90)           // Sets the orientation of the camera to east
                .tilt(0)               // Sets the tilt of the camera to 30 degrees
                .build();              // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}