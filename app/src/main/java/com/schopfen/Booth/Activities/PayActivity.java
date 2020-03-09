package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.schopfen.Booth.Adapters.ApproveItemsAdapter;
import com.schopfen.Booth.Adapters.Fragment_Current_Order_Adapter;
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
import java.util.Map;

public class PayActivity extends AppCompatActivity {

    TextView payNow, total_items_count, total_price, vat_percentage, vat_price, grand_total, order_name, delivery_total_items, delivery_charges, discount_textview,
            promo_apply, promo_discount;
    RelativeLayout mada_payment, visa_payment, cod_payment;
    EditText promo_edit_text;
    RecyclerView product_list;

    String item_total_amount, orderitemsfromIntent, username, address, mobileNumber, email, longitude, latitude, boothname, totalitems, vatpercentage,
            additionaldeliverycharges, actualdeliverycharges, discount, orderRequestID, deliverycharges, PromoDiscountAvailed;
    ArrayList<OrderItemsData> orderItems = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null) {
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null) {
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
            setContentView(R.layout.activity_pay);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
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

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<OrderItemsData>>() {
            }.getType();
            orderItems = gson.fromJson(orderitemsfromIntent, type);

            Initialization();

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
            float VatPrice = ((totalprice - Float.valueOf(discount)) * Float.valueOf(vatpercentage) / 100);
            vat_price.setText(String.valueOf(VatPrice) + " " + orderItems.get(0).getCurrencySymbol());
            order_name.setText(boothname);

            delivery_total_items.setText("(" + totalitems + ")");
            deliverycharges = String.valueOf(Float.valueOf(actualdeliverycharges) + Float.valueOf(additionaldeliverycharges));
            delivery_charges.setText(deliverycharges + " " + orderItems.get(0).getCurrencySymbol());
            discount_textview.setText("-" + discount + " " + orderItems.get(0).getCurrencySymbol());

            grand_total.setText(String.valueOf(totalprice + VatPrice - Float.valueOf(discount) + Float.valueOf(deliverycharges)) + " " + orderItems.get(0).getCurrencySymbol());

            product_list.setLayoutManager(new LinearLayoutManager(this));
            ApproveItemsAdapter adapter = new ApproveItemsAdapter(this, orderItems, "PayActivity");
            product_list.setAdapter(adapter);

            promo_apply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (promo_edit_text.getText().toString().isEmpty()) {
                        final Dialog addressDialog = new Dialog(PayActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        addressDialog.setContentView(R.layout.alert_text_layout);
                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                        address_text.setText("Field is empty.\nPlease write a valid Promo Code");

                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        addressDialog.show();

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addressDialog.dismiss();
                            }
                        });
                    } else {
                        PromoCode();
                    }
                }
            });

            payNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PaymentDone();
                }
            });

            cod_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            mada_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(PayActivity.this, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                }
            });
            visa_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(PayActivity.this, getResources().getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void PromoCode() {
        CustomLoader.showDialog(PayActivity.this);
        Map<String, String> body = new HashMap<String, String>();
//        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("CouponCode", promo_edit_text.getText().toString());
        body.put("Amount", item_total_amount);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.APPLYCODE, PayActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("promocode", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");

                            JSONObject coupon = jsonObject.getJSONObject("coupon");

                            String DiscountedAmount = coupon.getString("DiscountedAmount");
                            PromoDiscountAvailed = coupon.getString("DiscountAvailed");
                            String DiscountAppliedFactor = coupon.getString("DiscountAppliedFactor");
                            String CouponID = coupon.getString("CouponID");

                            float VatPrice = ((Float.valueOf(DiscountedAmount)) * Float.valueOf(vatpercentage) / 100);
                            vat_price.setText(String.valueOf(VatPrice + " " + orderItems.get(0).getCurrencySymbol()));
//                            discount_textview.setText(DiscountAvailed + " " + orderItems.get(0).getCurrencySymbol());
                            promo_discount.setText("-" + PromoDiscountAvailed + " " + orderItems.get(0).getCurrencySymbol());
                            grand_total.setText(String.valueOf((Float.valueOf(DiscountedAmount)) + VatPrice + Float.valueOf(deliverycharges)) + " " + orderItems.get(0).getCurrencySymbol());

//                            promo_edit_text.setText("");

                            final Dialog addressDialog = new Dialog(PayActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                }
                            });

                        } else {

                            promo_edit_text.setText("");
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            final Dialog addressDialog = new Dialog(PayActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                        promo_edit_text.setText("");
                    }
                } else {
                    promo_edit_text.setText("");
                    Toast.makeText(PayActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }

    private void PaymentDone() {
        CustomLoader.showDialog(PayActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", orderRequestID);
        body.put("OrderStatus", "3");
        if (!promo_edit_text.getText().toString().isEmpty()) {
            body.put("PromoCodeUsed", promo_edit_text.getText().toString());
            body.put("PromoCodeDiscount", PromoDiscountAvailed);
        }
        body.put("PointsUsed", "2");
        body.put("PaymentMethod", "COD");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, PayActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Canceled", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");

                            final Dialog addressDialog = new Dialog(PayActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                            Toast.makeText(PayActivity.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(PayActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }


    private void Initialization() {
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
        promo_edit_text = findViewById(R.id.pay_promo_code_edit_text);
    }
}
