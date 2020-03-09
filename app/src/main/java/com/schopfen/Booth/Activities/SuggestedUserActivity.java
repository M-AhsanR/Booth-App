package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.SuggestedUserAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SuggestedBoothsData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SuggestedUserActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView suggestedBoothsRecycler;
    StaggeredGridLayoutManager gaggeredGridLayoutManager;
    ArrayList<SuggestedBoothsData> suggestedBoothsData = new ArrayList<>();
    Button confirmBtn;

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
            setContentView(R.layout.activity_suggested_user);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            initializeViews();
            initializeClickListeners();
            getSuggestedBooths();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    void initializeViews(){
        suggestedBoothsRecycler = findViewById(R.id.suggestedBoothsRecycler);
        confirmBtn = findViewById(R.id.confirmBtn);
    }

    void initializeClickListeners(){
        confirmBtn.setOnClickListener(this);
    }

    void getSuggestedBooths(){

        CustomLoader.showDialog(SuggestedUserActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("DeviceType", "Android");
        body.put("Type", "user");
        body.put("DeviceToken", sharedpreferences.getString("DeviceToken", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);

        Log.e("tokens", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.suggestedBooths, SuggestedUserActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()){
                    Log.e("GetSuggestedUsersResp", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
//                        String message = jsonObject.getString("message");
                        if (status == 200){

                            CustomLoader.dialog.dismiss();

                            JSONArray suggested_booths = jsonObject.getJSONArray("suggested_booths");
                            for(int i=0; i<suggested_booths.length(); i++){
                                JSONObject jsonObject1 = suggested_booths.getJSONObject(i);
                                String BoothCoverImage = jsonObject1.getString("BoothCoverImage");
                                String BoothImage = jsonObject1.getString("Image");
                                String BoothName = jsonObject1.getString("FullName");
                                String BoothType = jsonObject1.getString("BoothType");
                                String CityID = jsonObject1.getString("CityID");
                                String CompressedBoothCoverImage = jsonObject1.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject1.getString("CompressedImage");
                                String CompressedCoverImage = jsonObject1.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String OnlineStatus = jsonObject1.getString("OnlineStatus");
                                String CityTitle = jsonObject1.getString("CityTitle");
                                String UserID = jsonObject1.getString("UserID");

                                suggestedBoothsData.add(new SuggestedBoothsData(BoothCoverImage, BoothImage, BoothName, BoothType, CityID, CompressedCoverImage,
                                        CompressedBoothImage, CompressedCoverImage, CompressedImage, OnlineStatus, CityTitle, UserID));
                            }

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SuggestedUserActivity.this);
                            suggestedBoothsRecycler.setLayoutManager(linearLayoutManager);

                            SuggestedUserAdapter suggestedBoothsAdapter = new SuggestedUserAdapter(SuggestedUserActivity.this, suggestedBoothsData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            suggestedBoothsRecycler.setAdapter(suggestedBoothsAdapter);
                            suggestedBoothsAdapter.notifyDataSetChanged();

                        }else {
                            CustomLoader.dialog.dismiss();
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SuggestedUserActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void sendSelectedBooths(){

        CustomLoader.showDialog(SuggestedUserActivity.this);

//        String selectedBoothsString = android.text.TextUtils.join(",", Collections.singleton(SuggestedBoothsAdapter.boothsIDs));
        String selectedBoothsString = android.text.TextUtils.join(",", SuggestedUserAdapter.boothsIDs);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Following", selectedBoothsString);
        body.put("Follow", "1");
        body.put("DeviceType", "Android");
        body.put("Type", "user");
        body.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("followBoothsBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.follow, SuggestedUserActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("FollowBoothResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            JSONObject user_info = jsonObject.getJSONObject("user_info");

//
//                                    Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_SHORT).show();
                            SuggestedUserAdapter.boothsIDs.clear();
                            Intent intent = new Intent(SuggestedUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            Toast.makeText(SuggestedBoothsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SuggestedUserActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v == confirmBtn){
            if (SuggestedUserAdapter.boothsIDs.isEmpty()){
                Intent intent = new Intent(SuggestedUserActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
//                mEditor.putString("CategoriesCount", String.valueOf(SuggestedBoothsAdapter.boothsIDs.size())).apply();
                sendSelectedBooths();
            }
        }
    }
}
