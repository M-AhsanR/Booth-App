package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.TransactionHistoryAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.Models.TransactionHistoryData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionHistory extends AppCompatActivity {

    RecyclerView rv_transactionHistory;
    ArrayList<TransactionHistoryData> transactionHistoryData = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RelativeLayout no_data_found;

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
            setContentView(R.layout.activity_transaction_history);
            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedPreferences.edit();
            initilizeViwe();
            transactionHistoryApiCall();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void initilizeViwe() {
        rv_transactionHistory = findViewById(R.id.rv_transactionhistory);
        no_data_found = findViewById(R.id.no_data_found);
    }

    private void transactionHistoryApiCall() {
        CustomLoader.showDialog(TransactionHistory.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedPreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.TRANSACTIONHISTORY, TransactionHistory.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("TransactionHistory", result + " ");
                if (ERROR.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            JSONArray storearray = jsonObject.getJSONArray("message");

                            for (int i = 0; i < storearray.length(); i++) {

                                JSONObject object = storearray.getJSONObject(i);

                                String CreatedAt = object.getString("CreatedAt");
                                String OrderDate = object.getString("OrderDate");
                                String OrderNumber = object.getString("OrderNumber");
                                String Points = object.getString("Points");
                                String TransactionType = object.getString("TransactionType");
                                String Type = object.getString("Type");
                                String UserID = object.getString("UserID");
                                String UserPointHistoryID = object.getString("UserPointHistoryID");

                                transactionHistoryData.add(new TransactionHistoryData(CreatedAt,OrderDate,OrderNumber,Points,TransactionType,Type,UserID,UserPointHistoryID));
                            }

                            if (transactionHistoryData.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                                rv_transactionHistory.setVisibility(View.GONE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                rv_transactionHistory.setVisibility(View.VISIBLE);
                            }

                            rv_transactionHistory.setLayoutManager(new LinearLayoutManager(TransactionHistory.this));
                            TransactionHistoryAdapter adapter = new TransactionHistoryAdapter(TransactionHistory.this, transactionHistoryData, new TransactionHistoryAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            rv_transactionHistory.setAdapter(adapter);

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(TransactionHistory.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                startActivity(new Intent(TransactionHistory.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(TransactionHistory.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
