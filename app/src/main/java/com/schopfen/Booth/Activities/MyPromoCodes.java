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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.MyPromoCodeAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.MyPromoModel;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyPromoCodes extends AppCompatActivity {

    RecyclerView promo_recycler;
    Button promo_submit;
    RelativeLayout progressbar, mainLayout;
    public static LinearLayout button_layout;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String Currency;

    ArrayList<MyPromoModel> myPromoModelArrayList = new ArrayList<>();

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
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            setContentView(R.layout.activity_my_promo_codes);

            promo_recycler = findViewById(R.id.promo_recycler);
            promo_submit = findViewById(R.id.promo_submit);
            mainLayout = findViewById(R.id.mainLayout);
            progressbar = findViewById(R.id.progressbar);
            button_layout = findViewById(R.id.button_layout);

            button_layout.setVisibility(View.GONE);

            promo_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MyPromoCodes.this, AddPromoCode.class);
                    startActivity(intent);
                }
            });

            Currency = sharedpreferences.getString("Currency", "");

            MyPromoCode();
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()){
            MyPromoCode();
        }
    }

    private void MyPromoCode(){

        progressbar.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.getPromoCodes, MyPromoCodes.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("MyPromos", result);

                    try {
                        JSONObject jsonObject = new JSONObject(result);

                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            myPromoModelArrayList.clear();

                            JSONArray coupons = jsonObject.getJSONArray("coupons");

                            for (int i = 0; i < coupons.length(); i++){

                                JSONObject jsonObject1 = coupons.getJSONObject(i);

                                String CouponID = jsonObject1.getString("CouponID");
                                String UserID = jsonObject1.getString("UserID");
                                String CouponCode = jsonObject1.getString("CouponCode");
                                String DiscountType = jsonObject1.getString("DiscountType");
                                String DiscountFactor = jsonObject1.getString("DiscountFactor");
                                String ExpiryDate = jsonObject1.getString("ExpiryDate");
                                String UsageCount = jsonObject1.getString("UsageCount");
                                String TotalCount = jsonObject1.getString("TotalCount");
                                String IsActive = jsonObject1.getString("IsActive");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String CouponTextID = jsonObject1.getString("CouponTextID");
                                String Title = jsonObject1.getString("Title");
                                String SystemLanguageID = jsonObject1.getString("SystemLanguageID");

                                myPromoModelArrayList.add(new MyPromoModel(CouponID, UserID, CouponCode, DiscountType, DiscountFactor, ExpiryDate, UsageCount,
                                        TotalCount, IsActive, CreatedAt, CouponTextID, Title, SystemLanguageID));

                            }

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyPromoCodes.this);
                            promo_recycler.setLayoutManager(linearLayoutManager);
                            MyPromoCodeAdapter myPromoCodeAdapter = new MyPromoCodeAdapter("", Currency, "MyPromo", MyPromoCodes.this, myPromoModelArrayList, new MyPromoCodeAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            promo_recycler.setAdapter(myPromoCodeAdapter);

                            if (myPromoModelArrayList.size() >= 3){
                                button_layout.setVisibility(View.GONE);
                            }else {
                                button_layout.setVisibility(View.VISIBLE);
                            }

                            progressbar.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);

                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(MyPromoCodes.this, message, Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }

                }else {
                    Toast.makeText(MyPromoCodes.this, ERROR, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                }

            }
        });
    }

}
