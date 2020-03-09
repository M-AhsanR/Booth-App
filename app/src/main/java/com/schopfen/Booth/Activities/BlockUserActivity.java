package com.schopfen.Booth.Activities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.BlockUserAadapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BlockUerData;
import com.schopfen.Booth.Models.Chat_Data;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockUserActivity extends AppCompatActivity {

    RecyclerView blockeduserRecycler;
    RelativeLayout no_data_found;
    ArrayList<BlockUerData> arrayListBlockUsers = new ArrayList<>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

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
            setContentView(R.layout.activity_block_user);
            sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedPreferences.edit();
            initilizeViews();
            blockusersApiCall();
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void blockusersApiCall() {
        CustomLoader.showDialog(BlockUserActivity.this);
        Map<String, String> body = new HashMap<>();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedPreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.BLOCKEDUSERS + sharedPreferences.getString("UserID", " ")+ "&UserType=" + sharedPreferences.getString("LastState", "") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, BlockUserActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("BlockedusersResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONArray blocked_users = jsonObject.getJSONArray("blocked_users");
                            for (int i = 0; i < blocked_users.length(); i++) {
                                JSONObject jsonObject1 = blocked_users.getJSONObject(i);
                                String UserID = jsonObject1.getString("UserID");
                                String UserName = jsonObject1.getString("UserName");
                                String BoothUserName = jsonObject1.getString("BoothUserName");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String CompressedBoothImage = jsonObject1.getString("CompressedBoothImage");
                                String BlockedUserType = jsonObject1.getString("BlockedUserType");
                                String BlockedAt = jsonObject1.getString("BlockedAt");
                                String CityID = jsonObject1.getString("CityID");
                                arrayListBlockUsers.add(new BlockUerData(CityID, BlockedAt, UserID, UserName, CompressedImage, CompressedBoothImage, BlockedUserType, BoothUserName));
                            }
                            if (arrayListBlockUsers.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BlockUserActivity.this);
                                blockeduserRecycler.setLayoutManager(linearLayoutManager);
                                blockeduserRecycler.addItemDecoration(new DividerItemDecoration(BlockUserActivity.this, DividerItemDecoration.VERTICAL));
                                BlockUserAadapter blockUserAadapter = new BlockUserAadapter(BlockUserActivity.this, arrayListBlockUsers, new BlockUserAadapter.CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                    }
                                });
                                blockeduserRecycler.setAdapter(blockUserAadapter);
                            }
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(BlockUserActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ChatSendmessageResp", ERROR);
                    Toast.makeText(BlockUserActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initilizeViews() {
        blockeduserRecycler = findViewById(R.id.blockeduserrecycler);
        no_data_found = findViewById(R.id.no_data_found);
    }
}