package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.SelectedCategoriesAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.SelectedCateModel;
import com.schopfen.Booth.Models.SelectedCategories;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedCategoriesList extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    RecyclerView selected_categories_recycler;
    TextView categories_title;

    ArrayList<SelectedCateModel> userSelectedCategories = new ArrayList<>();
    ArrayList<SelectedCateModel> boothSelectedCategories = new ArrayList<>();

    String LastState;
    String otherUserID;

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
            setContentView(R.layout.activity_selected_categories_list);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            LastState = getIntent().getStringExtra("state");
            otherUserID = getIntent().getStringExtra("otheruserID");

            selected_categories_recycler = findViewById(R.id.selected_categories_recycler);
            categories_title = findViewById(R.id.categories_title);

            getUserDetails();
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    private void getUserDetails() {

        CustomLoader.showDialog(SelectedCategoriesList.this);

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + otherUserID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&Type=" + LastState, SelectedCategoriesList.this, body, headers, new ServerCallback() {
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
                                String Image = subCateObject.getString("Image");
                                userSelectedCategories.add(new SelectedCateModel(UserCategoryID, UserIDInner, CategoryID, Type, Title, Image));
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
                                String Image = subCateObject.getString("Image");
                                boothSelectedCategories.add(new SelectedCateModel(UserCategoryID, UserIDInner, CategoryID, Type, Title, Image));
                            }
                            if (LastState.equals("user")) {
                                categories_title.setText(getResources().getString(R.string.interests));
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SelectedCategoriesList.this);
                                selected_categories_recycler.setLayoutManager(linearLayoutManager);
                                selected_categories_recycler.addItemDecoration(new DividerItemDecoration(SelectedCategoriesList.this, DividerItemDecoration.VERTICAL));
                                SelectedCategoriesAdapter selectedCategoriesAdapter = new SelectedCategoriesAdapter(SelectedCategoriesList.this, userSelectedCategories, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                    }
                                });
                                selected_categories_recycler.setAdapter(selectedCategoriesAdapter);
                            } else if (LastState.equals("booth")) {
                                categories_title.setText(getResources().getString(R.string.categories));
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SelectedCategoriesList.this);
                                selected_categories_recycler.setLayoutManager(linearLayoutManager);
                                selected_categories_recycler.addItemDecoration(new DividerItemDecoration(SelectedCategoriesList.this, DividerItemDecoration.VERTICAL));
                                SelectedCategoriesAdapter selectedCategoriesAdapter = new SelectedCategoriesAdapter(SelectedCategoriesList.this, boothSelectedCategories, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                    }
                                });
                                selected_categories_recycler.setAdapter(selectedCategoriesAdapter);
                            }

                            CustomLoader.dialog.dismiss();
                        } else {
                            Toast.makeText(SelectedCategoriesList.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(SelectedCategoriesList.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }


}
