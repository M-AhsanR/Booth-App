package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.CategoriesListAdapter;
import com.schopfen.Booth.Adapters.CheckOutForAddressRVAdapter;
import com.schopfen.Booth.Adapters.CheckOutForProductRVAdapter;
import com.schopfen.Booth.Adapters.CheckoutPaymentAdapter;
import com.schopfen.Booth.Adapters.SubCategoriesAdapter;
import com.schopfen.Booth.Adapters.SummaryCheckoutAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.AddressData;
import com.schopfen.Booth.Models.CartBoothItemsData;
import com.schopfen.Booth.Models.CartBoothsData;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;
import com.schopfen.Booth.Social.CheckOutForProductsRVModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckOutActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    RecyclerView recyclerView;
    public static final String MyPREFERENCES = "MyPrefs";

    ArrayList<CheckOutForProductsRVModel> list = new ArrayList<>();
    ArrayList<CheckOutForAddressRVModel> addresslist = new ArrayList<>();
    ArrayList<CheckOutForAddressRVModel> paymentlist = new ArrayList<>();
    public static ArrayList<AddressData> addressData = new ArrayList<>();

    RecyclerView rvaddress, payment_recycler, summary_recycler;
    EditText name, email;
    Button confirm_checkout;
    CheckoutPaymentAdapter checkoutPaymentAdapter;

    CheckOutForAddressRVAdapter addressadapter;

    ArrayList<CartBoothsData> cartBoothsData;
    ArrayList<CartBoothItemsData> cartBoothItemsData;
    ArrayList<ProductImagesData> productImagesData;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckOutActivity.this, ShoppingCartActivity.class));
        finish();
    }

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
            setContentView(R.layout.activity_check_out);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            rvaddress = findViewById(R.id.rv_address_coa);
            confirm_checkout = findViewById(R.id.confirm_checkout);
            payment_recycler = findViewById(R.id.payment_recycler);
            summary_recycler = findViewById(R.id.summary_recycler);

            getAddressesApi();
            GetCartItems();

            CheckOutForProductsRVModel model;

            for (int i = 0; i < 3; i++) {
                model = new CheckOutForProductsRVModel("Product Title", "1 Item", "22.50 SAR");
                list.add(model);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            CheckOutActivity.this.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);

            paymentRecyclerFunction();

            confirm_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addressData.isEmpty()){
                        final Dialog addressDialog = new Dialog(CheckOutActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        addressDialog.setContentView(R.layout.alert_text_layout);
                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                        address_text.setText(getResources().getString(R.string.addaddresstocontinue));

                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        addressDialog.show();

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addressDialog.dismiss();
                            }
                        });
                    }else {
                        if (sharedpreferences.getString("IsMobileVerified", "").equals("0")){
                            Toast.makeText(CheckOutActivity.this, getResources().getString(R.string.mobileisnotvarified), Toast.LENGTH_SHORT).show();
                            SendOTP();
                        }else {
                            if (sharedpreferences.getString("IsMobileVerified", "").equals("0")){
                                Toast.makeText(CheckOutActivity.this, getResources().getString(R.string.emailisnotvarified), Toast.LENGTH_SHORT).show();
                            }else {
                                PlaceOrder();
                            }
                        }
                    }
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    private void SendOTP() {

        CustomLoader.showDialog(CheckOutActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, CheckOutActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();

                            VerifyDialog();


                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void VerifyDialog() {
        final Dialog verifyDialog = new Dialog(CheckOutActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.verify_mobile_dialog);
        EditText codeEditText = verifyDialog.findViewById(R.id.codeEditText);
        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);
        TextView resend = verifyDialog.findViewById(R.id.resend_code);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReSendOTP();
            }
        });

        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDialog.dismiss();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEditText.getText().toString().isEmpty()) {
                    Toast.makeText(verifyDialog.getContext(), getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_SHORT).show();
                } else {
                    VerifyOTP(codeEditText.getText().toString());
                    verifyDialog.dismiss();
                }
            }
        });
    }
    private void ReSendOTP() {

        CustomLoader.showDialog(CheckOutActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, CheckOutActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void VerifyOTP(String otp) {

        CustomLoader.showDialog(CheckOutActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("OTP", otp);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VERIFYOTP, CheckOutActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void GetCartItems(){

        Map<String, String> postParams = new HashMap<>();
        HashMap<String, String> headerParams;

        CustomLoader.showDialog(CheckOutActivity.this);

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.GetCartItems + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE , CheckOutActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCartItemsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            cartBoothsData = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("cart_items");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                String BoothID = jsonObject1.getString("BoothID");
                                String BoothName = jsonObject1.getString("BoothName");
                                String ProductID = jsonObject1.getString("ProductID");
                                String ProductQuantity = jsonObject1.getString("ProductQuantity");
                                String TempOrderID = jsonObject1.getString("TempOrderID");
                                String UserID = jsonObject1.getString("UserID");
                                String VatPercentage = jsonObject1.getString("VatPercentage");

                                cartBoothItemsData = new ArrayList<>();
                                JSONArray CartItems = jsonObject1.getJSONArray("CartItems");
                                for(int j=0; j<CartItems.length(); j++){
                                    JSONObject jsonObject2 = CartItems.getJSONObject(j);
                                    String BoothID1 = jsonObject2.getString("BoothID");
                                    String Currency = jsonObject2.getString("Currency");
                                    String CurrencySymbol = jsonObject2.getString("CurrencySymbol");
                                    String ProductID1 = jsonObject2.getString("ProductID");
                                    String ProductPrice = jsonObject2.getString("ProductPrice");
                                    String ProductQuantity1 = jsonObject2.getString("ProductQuantity");
                                    String ProductTitle = jsonObject2.getString("ProductTitle");
                                    String TempOrderID1 = jsonObject2.getString("TempOrderID");
                                    String UserID1 = jsonObject2.getString("UserID");
                                    String DeliveryCharges = jsonObject2.getString("DeliveryCharges");

                                    productImagesData = new ArrayList<>();
                                    JSONArray ProductImages = jsonObject2.getJSONArray("ProductImages");
                                    for(int k=0; k<ProductImages.length(); k++){
                                        JSONObject jsonObject3 = ProductImages.getJSONObject(k);
                                        String ProductCompressedImage = jsonObject3.getString("ProductCompressedImage");
                                        String ProductImage = jsonObject3.getString("ProductImage");

                                        productImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                    }

                                    cartBoothItemsData.add(new CartBoothItemsData(DeliveryCharges, BoothID1, Currency, CurrencySymbol, ProductID1, ProductPrice, ProductQuantity1, ProductTitle, TempOrderID1, UserID1, productImagesData));
                                }

                                cartBoothsData.add(new CartBoothsData(VatPercentage, BoothID, BoothName, ProductID, ProductQuantity, TempOrderID, UserID, cartBoothItemsData));
                            }

                            Log.e("CartBoothSize", cartBoothsData.size() + " ");

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CheckOutActivity.this, RecyclerView.VERTICAL, false);
                            summary_recycler.setLayoutManager(layoutManager);

                            SummaryCheckoutAdapter summaryCheckoutAdapter = new SummaryCheckoutAdapter(CheckOutActivity.this, cartBoothsData);

//                            shoppingCartRecycler.addItemDecoration(new DividerItemDecoration(ShoppingCartActivity.this, DividerItemDecoration.VERTICAL));
                            summary_recycler.setAdapter(summaryCheckoutAdapter);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_LONG).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }


    private void PlaceOrder() {

        CustomLoader.showDialog(CheckOutActivity.this);

        Map<String, String> addproductParams = new HashMap<String, String>();

        addproductParams.put("UserID", sharedpreferences.getString("UserID", ""));
        addproductParams.put("AddressID", CheckOutForAddressRVAdapter.addressID);
        addproductParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.PLACE_ORDER, CheckOutActivity.this, addproductParams, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("PlaceOrder", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            final Dialog addressDialog = new Dialog(CheckOutActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(CheckOutActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void paymentRecyclerFunction() {
        CheckOutForAddressRVModel addressmodel;

        addressmodel = new CheckOutForAddressRVModel();
        addressmodel.setAddress("Address 1");
        addressmodel.setDetails("fnsunf ijsdn seijcunsij seijcsnij");
        addresslist.add(addressmodel);

        CheckOutForAddressRVModel addressmodel1 = new CheckOutForAddressRVModel();
        addressmodel1.setAddress("Address 2");
        addressmodel1.setDetails("fnsunf sjfncs sdjcs sdjc sbd sdjsjd hds cjs ");
        addresslist.add(addressmodel1);

        CheckOutForAddressRVModel addressmodel2 = new CheckOutForAddressRVModel();
        addressmodel2.setAddress("Address 2");
        addressmodel2.setDetails("fnsunf sjfncs sdjcs sdjc sbd sdjsjd hds cjs ");
        addresslist.add(addressmodel2);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(CheckOutActivity.this, 3);
        payment_recycler.setLayoutManager(layoutManager);

        checkoutPaymentAdapter = new CheckoutPaymentAdapter(CheckOutActivity.this, addresslist, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getApplicationContext(),"Item "+position, Toast.LENGTH_SHORT).show();
            }
        });
        payment_recycler.setAdapter(checkoutPaymentAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (addressadapter != null){
            addressadapter.notifyDataSetChanged();
        }
    }

    private void getAddressesApi() {

        Map<String, String> postParams = new HashMap<String, String>();
        HashMap<String, String> headerParams;

//        CustomLoader.showDialog(CheckOutActivity.this);

        headerParams = new HashMap<String, String>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getAllAddress + sharedpreferences.getString("UserID", "") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, CheckOutActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("GetAddressesResp", result);
                    try {
                        addressData.clear();

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
//                            CustomLoader.dialog.dismiss();
                            JSONArray address_info = jsonObject.getJSONArray("address_info");
                            for (int i = 0; i < address_info.length(); i++) {
                                JSONObject jsonObject1 = address_info.getJSONObject(i);
                                String Address1 = jsonObject1.getString("Address1");
                                String Address2 = jsonObject1.getString("Address2");
                                String AddressID = jsonObject1.getString("AddressID");
                                String AddressTitle = jsonObject1.getString("AddressTitle");
                                String ApartementNo = jsonObject1.getString("ApartmentNo");
                                String City = jsonObject1.getString("City");
                                String Email = jsonObject1.getString("Email");
                                String Gender = jsonObject1.getString("Gender");
                                String IsDefault = jsonObject1.getString("IsDefault");
                                String Mobile = jsonObject1.getString("Mobile");
                                String RecipientName = jsonObject1.getString("RecipientName");
                                String UserID = jsonObject1.getString("UserID");

                                addressData.add(new AddressData(Address1, Address2, AddressID, AddressTitle, ApartementNo, City, Email, Gender, IsDefault, Mobile, RecipientName, UserID));
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CheckOutActivity.this, RecyclerView.HORIZONTAL, false);
                            rvaddress.setLayoutManager(layoutManager);

                            addressadapter = new CheckOutForAddressRVAdapter(CheckOutActivity.this, addressData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            rvaddress.setAdapter(addressadapter);

                        } else {
//                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
//                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(CheckOutActivity.this, ERROR, Toast.LENGTH_SHORT).show();
//                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }
}
