package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.pusher.client.channel.User;
import com.schopfen.Booth.Adapters.CategoriesListAdapter;
import com.schopfen.Booth.Adapters.SubCategoriesAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.SelectedCategories;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CategoriesListChildModel;
import com.schopfen.Booth.Social.MainCatData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesListActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences.Editor mEditor;
    RecyclerView recyclerView;
    CategoriesListAdapter listAdapter;
    ArrayList<MainCategoriesData> parentlist = new ArrayList<>();
    ArrayList<MainCategoriesData> childlist = new ArrayList<>();
    Button confirmButton;
    String TOKEN;
    String LastState;
    TextView screenTitle, subtitle_line, mainTitle;
    NestedScrollView nestedScrollView;
    ArrayList<String> childArray = new ArrayList<>();
    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;
    ArrayList<MainCategoriesData> mainCategoryData;
    ArrayList<SubCategoriesData> subCategoriesData;
    ArrayList<SubSubCategoriesData> subSubCategoriesData;
    ArrayList<SelectedCategories> userSelectedCategories = new ArrayList<>();
    ArrayList<SelectedCategories> boothSelectedCategories = new ArrayList<>();
    public static final ArrayList<String> selectedItemsId = new ArrayList<>();
    public static ArrayList<String> selectedMainCategoriesList = new ArrayList<>();
    String Type;
    String activity;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        selectedMainCategoriesList.clear();
        selectedItemsId.clear();
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
            setContentView(R.layout.activity_categories_list);
            activity = getIntent().getStringExtra("Activity");
            initilizedViews();
            if (activity.equals("Profile")){
                confirmButton.setText(getResources().getString(R.string.savechanges));
            }else {
                confirmButton.setText(getResources().getString(R.string.Confirm));
            }
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            TOKEN = sharedpreferences.getString("ApiToken", " ");
            Log.e("UserID", sharedpreferences.getString("UserID", " "));
            LastState = sharedpreferences.getString("LastState", "");
            if (LastState.equals("user")) {
                screenTitle.setText(getResources().getString(R.string.chooseInterests));
                subtitle_line.setText(getResources().getString(R.string.chooseInterests_subline));
                Type = "user";
            } else if (LastState.equals("booth")) {
                screenTitle.setText(getResources().getString(R.string.chooseCategories));
                subtitle_line.setText(getResources().getString(R.string.chooseCategories_subline));
                Type = "booth";
            }
            if (activity.equals("SignUp")) {
                selectedMainCategoriesList.clear();
                cateGoriesList();
                if (LastState.equals("user")) {
                    mainTitle.setText(getResources().getString(R.string.chooseInterests));
                    Type = "user";
                } else if (LastState.equals("booth")) {
                    mainTitle.setText(getResources().getString(R.string.chooseCategories));
                    Type = "booth";
                }
            } else if (activity.equals("Profile")) {
                getUserDetails();
                if (LastState.equals("user")) {
                    mainTitle.setText(getResources().getString(R.string.updateinterests));
                    Type = "user";
                } else if (LastState.equals("booth")) {
                    mainTitle.setText(getResources().getString(R.string.updatecategories));
                    Type = "booth";
                }
            }
            nestedScrollView.setFocusableInTouchMode(true);
            nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            List<String> listtoadditems = new ArrayList<String>();
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void getUserDetails() {
        CustomLoader.showDialog(CategoriesListActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, CategoriesListActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            JSONArray UserSelectedCategories = user_info.getJSONArray("UserSelectedCategories");
                            userSelectedCategories.clear();
                            for (int i = 0; i < UserSelectedCategories.length(); i++) {
                                JSONObject subCateObject = UserSelectedCategories.getJSONObject(i);
                                String UserCategoryID = subCateObject.getString("UserCategoryID");
                                String UserIDInner = subCateObject.getString("UserID");
                                String CategoryID = subCateObject.getString("CategoryID");
                                String Type = subCateObject.getString("Type");
                                String Title = subCateObject.getString("Title");
                                userSelectedCategories.add(new SelectedCategories(UserCategoryID, UserIDInner, CategoryID, Type, Title));
                            }
                            JSONArray BoothSelectedCategories = user_info.getJSONArray("BoothSelectedCategories");
                            boothSelectedCategories.clear();
                            for (int i = 0; i < BoothSelectedCategories.length(); i++) {
                                JSONObject subCateObject = BoothSelectedCategories.getJSONObject(i);
                                String UserCategoryID = subCateObject.getString("UserCategoryID");
                                String UserIDInner = subCateObject.getString("UserID");
                                String CategoryID = subCateObject.getString("CategoryID");
                                String Type = subCateObject.getString("Type");
                                String Title = subCateObject.getString("Title");
                                boothSelectedCategories.add(new SelectedCategories(UserCategoryID, UserIDInner, CategoryID, Type, Title));
                            }
                            if (LastState.equals("user")) {
                                if (!userSelectedCategories.isEmpty()) {
                                    if (selectedItemsId.isEmpty()) {
                                        for (int i = 0; i < userSelectedCategories.size(); i++) {
                                            selectedItemsId.add(userSelectedCategories.get(i).getCategoryID());
                                        }
                                    }
                                }
                            } else if (LastState.equals("booth")) {
                                if (!boothSelectedCategories.isEmpty()) {
                                    if (selectedItemsId.isEmpty()) {
                                        for (int i = 0; i < boothSelectedCategories.size(); i++) {
                                            selectedItemsId.add(boothSelectedCategories.get(i).getCategoryID());
                                        }
                                    }
                                }
                            }
                            cateGoriesList();
                        } else {
                            Toast.makeText(CategoriesListActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(CategoriesListActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //sending selected categories to server
    private void sendingSelectedCats() {
        CustomLoader.showDialog(CategoriesListActivity.this);
        String categoriesString = android.text.TextUtils.join(",", selectedItemsId);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Categories", categoriesString);
        body.put("DeviceType", "Android");
        body.put("Type", Type);
        body.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, CategoriesListActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("SendCategoriesResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            if (activity.equals("SignUp")) {
                                if (LastState.equals("user")) {
                                    startActivity(new Intent(CategoriesListActivity.this, SuggestedBoothsActivity.class));
                                    selectedItemsId.clear();
                                    finish();
                                } else if (LastState.equals("booth")) {
                                    startActivity(new Intent(CategoriesListActivity.this, SetupBoothProfileActivity.class));
                                    selectedItemsId.clear();
                                    finish();
                                }
                            } else {
                                selectedItemsId.clear();
                                selectedMainCategoriesList.clear();
                                Intent intent = new Intent(CategoriesListActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(CategoriesListActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CategoriesListActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    String url = "https://baac.booth-in.com/api/categories?";
    // Populate categories list with items
    private void cateGoriesList() {
        if (activity.equals("SignUp")) {
            CustomLoader.showDialog(CategoriesListActivity.this);
        }
        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", TOKEN);
        Log.d("MyError", "result is   " + TOKEN);
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.categories + "AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, CategoriesListActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            mainCategoryData = new ArrayList<>();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject mainCateObject = categories.getJSONObject(i);
                                String CategoryID = mainCateObject.getString("CategoryID");
                                String Image = mainCateObject.getString("Image");
                                String Title = mainCateObject.getString("Title");
                                JSONArray subCateArray = mainCateObject.getJSONArray("SubCategories");
                                subCategoriesData = new ArrayList<>();
                                for (int j = 0; j < subCateArray.length(); j++) {
                                    JSONObject subCateObject = subCateArray.getJSONObject(j);
                                    String CategoryID2 = subCateObject.getString("CategoryID");
                                    String Image2 = subCateObject.getString("Image");
                                    String Title2 = subCateObject.getString("Title");
                                    JSONArray subSubCateArray = subCateObject.getJSONArray("SubSubCategories");
                                    subSubCategoriesData = new ArrayList<>();
                                    for (int k = 0; k < subSubCateArray.length(); k++) {
                                        JSONObject subSubCateObject = subSubCateArray.getJSONObject(k);
                                        String CategoryID3 = subSubCateObject.getString("CategoryID");
                                        String Image3 = subSubCateObject.getString("Image");
                                        String Title3 = subSubCateObject.getString("Title");
                                        subSubCategoriesData.add(new SubSubCategoriesData(CategoryID3, Image3, Title3));
                                    }
                                    subCategoriesData.add(new SubCategoriesData(CategoryID2, Image2, Title2, subSubCategoriesData));
                                }
                                mainCategoryData.add(new MainCategoriesData(CategoryID, Image, Title, subCategoriesData));
                            }
                            listAdapter = new CategoriesListAdapter(activity, CategoriesListActivity.this, mainCategoryData, userSelectedCategories, boothSelectedCategories, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                }
                            });
                            recyclerView.setAdapter(listAdapter);
                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(CategoriesListActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }
    // Initilization of views
    private void initilizedViews() {
        //initilizing widgets
        selectedMainCategoriesList.clear();
        recyclerView = findViewById(R.id.rv_catlist_cla);
        confirmButton = findViewById(R.id.confirmBtn);
        nestedScrollView = findViewById(R.id.nestedsv);
        screenTitle = findViewById(R.id.screentitle);
        subtitle_line = findViewById(R.id.subtitle_line);
        mainTitle = findViewById(R.id.mainTitle);
        // passing context
        confirmButton.setOnClickListener(this);
    }
    // Click Listeners
    @Override
    public void onClick(View view) {
        if (view == confirmButton) {
            if (LastState.equals("user")) {
                if (selectedMainCategoriesList.size() < 3) {
                    Toast.makeText(CategoriesListActivity.this, "Please select atleast 3 interests", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("size", selectedItemsId.size() + "  ");
                    String string = android.text.TextUtils.join(",", Collections.singleton(selectedItemsId));
                    Log.e("commaSeparated", string);
                    sendingSelectedCats();
                }
            } else if (LastState.equals("booth")) {
                if (selectedMainCategoriesList.size() == 0) {
                    Toast.makeText(CategoriesListActivity.this, "Select one category atleast", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("size", selectedItemsId.size() + "  ");
                    String string = android.text.TextUtils.join(",", Collections.singleton(selectedItemsId));
                    Log.e("commaSeparated", string);
                    sendingSelectedCats();
                }
            }
        }
    }
}