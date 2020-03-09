package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.appersiano.progressimage.ProgressImage;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    Map<String, String> postParam;
    public static final String MyPREFERENCES = "MyPrefs";
    String TOKEN;
    HashMap<String, String> headerparameters;
    ImageView image;

    String deviceToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        image = findViewById(R.id.pi_image);

        Intent i = new Intent(SplashScreen.this, MyChatService.class);
//        i.putExtra("KEY1", "Value to be used by the service");
        startService(i);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();


        String lang = sharedpreferences.getString("language", "");
        Log.e("lang", sharedpreferences.getString("language", ""));

        Configuration configuration;
        configuration = this.getResources().getConfiguration();

        if (lang.equals("")){
            mEditor.putString("language", "EN");
        }else {
            if (lang.equals("EN")){
                String languageToLoad = "en";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                configuration.locale = locale;
                configuration = this.getResources().getConfiguration();
                configuration.setLayoutDirection(new Locale("en"));
                this.getResources().updateConfiguration(configuration, this.getResources().getDisplayMetrics());
            }else if (lang.equals("AR")){
                String languageToLoad = "ar";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                configuration.locale = locale;
                configuration = this.getResources().getConfiguration();
                configuration.setLayoutDirection(new Locale("ar"));
                this.getResources().updateConfiguration(configuration, this.getResources().getDisplayMetrics());
            }
        }



        TOKEN = sharedpreferences.getString("ApiToken", " ");
        Log.e("UserID", sharedpreferences.getString("UserID", ""));
        String androidOS = Build.VERSION.RELEASE;
        Log.e("AndroidOS", androidOS);

        deviceToken = sharedpreferences.getString("DeviceToken", "");


        if (TextUtils.isEmpty(deviceToken)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SplashScreen.this, new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String newToken = instanceIdResult.getToken();
                    Log.e("newToken", newToken);
                    mEditor.putString("DeviceToken", newToken);
                    mEditor.apply();
                }
            });
        }

        hitApiFunction();
        splashFunction();

    }


    // Splash Timer Function
    private void splashFunction() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!sharedpreferences.getString("UserID", " ").equals(" ")) {
                    getUserDetails();
                } else {
                    Log.e("UserID", "NotFound");
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
//                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
//                finish();
            }
        }, 5000);
    }

    // Volley Function to get HeaderToken
    private void hitApiFunction() {
        postParam = new HashMap<String, String>();
        postParam.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        postParam.put("OS", Build.VERSION.RELEASE);

        headerparameters = new HashMap<String, String>();
        headerparameters.put("Newtoken", String.valueOf(true));
        headerparameters.put("UserID", sharedpreferences.getString("UserID", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GENERATETOKEN, SplashScreen.this, postParam, headerparameters, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("GetTokenResponse", String.valueOf(result));
                Log.e("GetTokenError", String.valueOf(ERROR));

                if (ERROR.isEmpty()) {
                    try {
                        JSONObject mainObject = new JSONObject(String.valueOf(result));
                        int status = mainObject.getInt("status");
                        if (status == 200) {
                            String token = mainObject.getString("token");
                            mEditor.putString("ApiToken", token).commit();
                        } else {
                            String message = mainObject.getString("message");
                            Toast.makeText(SplashScreen.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SplashScreen.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void getUserDetails() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SplashScreen.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String AuthToken = user_info.getString("AuthToken");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            String CityID = user_info.getString("CityID");
                            String CityTitle = user_info.getString("CityTitle");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String RoleID = user_info.getString("RoleID");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String Verification = user_info.getString("Verification");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothName = user_info.getString("BoothName");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            String Currency = user_info.getString("Currency");

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.putString("LastState", LastState);
                            mEditor.putString("UserID", UserID);
                            mEditor.putString("HasUnreadMessage", HasUnreadMessage);
                            mEditor.putString("UserOrderCount", UserOrderCount);
                            mEditor.putString("BoothOrderCount", BoothOrderCount);
                            mEditor.putString("Currency", Currency);
                            mEditor.commit();

                            if (LastState.equals("user")) {
                                if (UserCategoriesCount == 0) {
                                    Intent intent = new Intent(SplashScreen.this, CategoriesListActivity.class);
                                    intent.putExtra("Activity", "SignUp");
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else if (LastState.equals("booth")) {
                                if (BoothName.isEmpty()) {
                                    Intent intent = new Intent(SplashScreen.this, EditBoothActivity.class);
                                    intent.putExtra("Activity", "No");
                                    startActivity(intent);
                                    finish();
                                } else if (BoothCategoriesCount == 0) {
                                    Intent intent = new Intent(SplashScreen.this, CategoriesListActivity.class);
                                    intent.putExtra("Activity", "SignUp");
                                    startActivity(intent);
                                    finish();
                                } else if (IsProfileCustomized.equals("0")) {
                                    startActivity(new Intent(SplashScreen.this, SetupBoothProfileActivity.class));
                                    finish();
                                } else {
                                    Intent intent = new Intent(SplashScreen.this, BoothMainActivity.class);
                                    startActivity(intent);

                                    finish();
                                }
                            }


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(SplashScreen.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SplashScreen.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
