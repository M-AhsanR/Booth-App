package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AboutUsActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    ImageView imageView;
    TextView textViewBody;

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
            setContentView(R.layout.activity_about_us);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            initilizeView();
            apiCall();
        }else {
            setContentView(R.layout.no_internet_screen);
        }
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

    // initilizing widgets
    private void initilizeView() {
        imageView = findViewById(R.id.about_image);
        textViewBody = findViewById(R.id.about_body);
    }

    private void apiCall(){
        CustomLoader.showDialog(AboutUsActivity.this);
        Map<String,String> params = new HashMap<>();
        HashMap<String,String> header= new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.about_us+ sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE +"&PageID=1", AboutUsActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("AboutUsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){
                            JSONObject user = jsonObject.getJSONObject("page_data");
                            String PageID = user.getString("PageID");
                            String Image = user.getString("Image");
                            String Title = user.getString("Title");
                            String Body = user.getString("Description");
                            Glide.with(AboutUsActivity.this).load("https://baac.booth-in.com/"+Image).apply(RequestOptions.centerCropTransform().placeholder(R.drawable.white_circle)).into(imageView);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                textViewBody.setText(Html.fromHtml(Body, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                textViewBody.setText(Html.fromHtml(Body));
                            }
                            CustomLoader.dialog.dismiss();
                        }else {
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(AboutUsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
