package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.FollowersAdapter;
import com.schopfen.Booth.Adapters.SelectedCategoriesAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FollowersActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    ArrayList<FollowersData> followersData = new ArrayList<>();

    RecyclerView followersRecycler;
    RelativeLayout no_data_found;

    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;

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
            setContentView(R.layout.activity_followers);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            initializaViews();
            getFollowersList();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    public void onClick(View v) {

    }

    private void initializaViews(){
        followersRecycler = findViewById(R.id.followersRecycler);
        no_data_found = findViewById(R.id.no_data_found);
    }

    private void getFollowersList(){

        CustomLoader.showDialog(FollowersActivity.this);

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        Log.e("IDs", sharedpreferences.getString("UserID", " ") + "  " + sharedpreferences.getString("OtherUserID", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getFollowers + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + sharedpreferences.getString("OtherUserID", "")  + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&Type=booth", FollowersActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserFollowersResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            followersData.clear();

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String total_follower = jsonObject1.getString("total_follower");
                            JSONArray jsonArray = jsonObject1.getJSONArray("followers");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String BoothCoverImage = jsonObject2.getString("BoothCoverImage");
                                String BoothImage = jsonObject2.getString("BoothImage");
                                String BoothName = jsonObject2.getString("BoothName");
                                String UserName = jsonObject2.getString("UserName");
                                String BoothUserName = jsonObject2.getString("BoothUserName");
                                String CityTitle = jsonObject2.getString("CityTitle");
                                String CompressedBoothCoverImage = jsonObject2.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject2.getString("CompressedBoothImage");
                                String CompressedCoverImage = jsonObject2.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject2.getString("CompressedImage");
                                String FullName = jsonObject2.getString("FullName");
                                String Image = jsonObject2.getString("Image");
                                String UserID = jsonObject2.getString("UserID");
                                String LastState = jsonObject2.getString("LastState");

                                followersData.add(new FollowersData(UserName, BoothCoverImage, BoothImage, BoothName, CityTitle, CompressedBoothCoverImage, CompressedBoothImage,
                                        CompressedCoverImage, CompressedImage, FullName, Image, UserID, BoothUserName, LastState));
                            }

                            if (followersData.isEmpty()){
                                no_data_found.setVisibility(View.VISIBLE);
                                followersRecycler.setVisibility(View.GONE);
                            }else {
                                no_data_found.setVisibility(View.GONE);
                                followersRecycler.setVisibility(View.VISIBLE);
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FollowersActivity.this, RecyclerView.VERTICAL, false);
                            followersRecycler.setLayoutManager(layoutManager);

                            FollowersAdapter followersAdapter = new FollowersAdapter(FollowersActivity.this, followersData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    Intent intent = new Intent(FollowersActivity.this, OthersProfileActivity.class);
                                    intent.putExtra("OtherUserID", followersData.get(position).getUserID());
                                    startActivity(intent);
                                }
                            });

                            followersRecycler.addItemDecoration(new DividerItemDecoration(FollowersActivity.this, DividerItemDecoration.VERTICAL));
                            followersRecycler.setAdapter(followersAdapter);
                            followersAdapter.notifyDataSetChanged();


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(FollowersActivity.this, message, Toast.LENGTH_LONG).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FollowersActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
