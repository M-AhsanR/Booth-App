package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;

import com.google.firebase.iid.FirebaseInstanceId;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.schopfen.Booth.DataClasses.BaseClass.Login;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    Map<String, String> postParam;
    HashMap<String, String> headerparameters;
    EditText user_name,password;
    String USERNAME,PASSWORD,TOKEN;
    TextView login;
    TextView forget_pass, reg_acc, lang, exploreApp;
    String deviceToken;
    Configuration configuration;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        initilizeviews();

        TOKEN = sharedpreferences.getString("ApiToken", " ");
        Log.e("UserID", sharedpreferences.getString("UserID", " "));
        deviceToken = sharedpreferences.getString("DeviceToken", "");


        configuration = this.getResources().getConfiguration();
        if (configuration.getLayoutDirection() == 1) {
            lang.setText("English");
        } else if (configuration.getLayoutDirection() == 0) {
            lang.setText("العربية");
        }
    }

    // LogIn Function
    public void logInFunction() {

        USERNAME = user_name.getText().toString();
        PASSWORD = password.getText().toString();


        if (TextUtils.isEmpty(USERNAME) || USERNAME.startsWith(" ")) {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            user_name.startAnimation(shake);
        } else if (TextUtils.isEmpty(PASSWORD) || PASSWORD.startsWith(" ")) {
            Animation shake = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake);
            password.startAnimation(shake);
        } else {
//            Toast.makeText(getApplicationContext(), "i am login button", Toast.LENGTH_SHORT).show();
            CustomLoader.showDialog(LoginActivity.this);

            Map<String, String> body = new HashMap<String, String>();
            body.put("Email", user_name.getText().toString());
            body.put("Password", password.getText().toString());
            body.put("DeviceType", "Android");
            body.put("DeviceToken", deviceToken);
            body.put("LastLangState", sharedpreferences.getString("language", ""));
            body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
            body.put("OS", Build.VERSION.RELEASE);

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Verifytoken", TOKEN);

            ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LOGIN, LoginActivity.this, body, headers, new ServerCallback() {
                @Override
                public void onSuccess(String result, String ERROR) {

                    if (ERROR.isEmpty()){
                        Log.e("GetLoginResponse", result);
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            if (status == 200){

                                JSONObject user = jsonObject.getJSONObject("user_info");
                                String UserID = user.getString("UserID");
                                String RoleID = user.getString("RoleID");
                                String UserName = user.getString("UserName");
                                String Email = user.getString("Email");
                                String Mobile = user.getString("Mobile");
                                String Gender = user.getString("Gender");
                                String CityID = user.getString("CityID");
                                String CompressedImage = user.getString("CompressedImage");
                                String Image = user.getString("Image");
                                String LastState = user.getString("LastState");
                                String IsEmailVerified = user.getString("IsEmailVerified");
                                String IsMobileVerified = user.getString("IsMobileVerified");
                                String Verification = user.getString("Verification");
                                String PaymentAccountNumber = user.getString("PaymentAccountNumber");
                                String PaymentAccountHolderName = user.getString("PaymentAccountHolderName");
                                String PaymentAccountBankBranch = user.getString("PaymentAccountBankBranch");
                                String DeviceType = user.getString("DeviceType");
                                String DeviceToken = user.getString("DeviceToken");
                                String IsActive = user.getString("IsActive");
                                String Notification = user.getString("Notification");
                                String OnlineStatus = user.getString("OnlineStatus");
                                String SortOrder = user.getString("SortOrder");
                                String CreatedAt = user.getString("CreatedAt");
                                String UpdatedAt = user.getString("UpdatedAt");
                                String Currency = user.getString("Currency");
                                String CurrencySymbol = user.getString("CurrencySymbol");
                                String FullName = user.getString("FullName");
                                String CityTitle = user.getString("CityTitle");
                                String AuthToken = user.getString("AuthToken");
                                int UserCategoriesCount = user.getInt("UserCategoriesCount");
                                String BoothName = user.getString("BoothName");
                                int BoothCategoriesCount = user.getInt("BoothCategoriesCount");
                                String IsProfileCustomized = user.getString("IsProfileCustomized");
                                int UserFollowedBooths = user.getInt("UserFollowedBooths");
                                String HasUnreadMessage = user.getString("HasUnreadMessage");

                                mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                                mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                                mEditor.putString("IsEmailVerified", IsEmailVerified);
                                mEditor.putString("IsMobileVerified", IsMobileVerified);
                                mEditor.putString("Currency", Currency);
                                mEditor.putString("CurrencySymbol", CurrencySymbol);
                                mEditor.putString("HasUnreadMessage", HasUnreadMessage);
                                mEditor.putString("Password", password.getText().toString());

                                Log.e("UserId", UserID);
                                mEditor.putString("UserID", UserID);
                                mEditor.putString("ApiToken", AuthToken);
                                mEditor.putString("LastState", LastState);
                                mEditor.apply();

                                CustomLoader.dialog.dismiss();

                                if(LastState.equals("user")){
                                    if (UserCategoriesCount != 0){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Intent intent = new Intent(LoginActivity.this, CategoriesListActivity.class);
                                        intent.putExtra("Activity", "SignUp");
                                        startActivity(intent);
                                        finish();
                                    }
                                }else if(LastState.equals("booth")){
                                    if(BoothName.isEmpty()){
                                        Intent intent = new Intent(LoginActivity.this, EditBoothActivity.class);
                                        intent.putExtra("Activity", "No");
                                        startActivity(intent);
                                        finish();
                                    }else if(BoothCategoriesCount == 0){
                                        Intent intent = new Intent(LoginActivity.this, CategoriesListActivity.class);
                                        intent.putExtra("Activity", "SignUp");
                                        startActivity(intent);
                                        finish();
                                    }else if(IsProfileCustomized.equals("0")){
                                        startActivity(new Intent(LoginActivity.this, SetupBoothProfileActivity.class));
                                        finish();
                                    }else{
                                        Intent intent = new Intent(LoginActivity.this, BoothMainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                            }else {
                                CustomLoader.dialog.dismiss();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            CustomLoader.dialog.dismiss();
                            e.printStackTrace();
                        }
                    }else {
                        CustomLoader.dialog.dismiss();
                        Toast.makeText(LoginActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }

    // ForgetPassword Function
    private  void forgetPasswordFunction(){
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
    }

    // Initilize views
    private void initilizeviews() {

        // initilization of widgets;
        user_name = findViewById(R.id.et_username_la);
        password = findViewById(R.id.et_password_la);
        login = findViewById(R.id.btn_login_la);
        reg_acc =  findViewById(R.id.registerNow);
        forget_pass = findViewById(R.id.tv_forgetpass_la);
        lang = findViewById(R.id.changeLanguage);
        exploreApp = findViewById(R.id.exploreApp);

        //passing context to widgets
        login.setOnClickListener(this);
        forget_pass.setOnClickListener(this);
        reg_acc.setOnClickListener(this);
        lang.setOnClickListener(this);
        exploreApp.setOnClickListener(this);
    }

    // Click Listeners
    @Override
    public void onClick(View view) {
        if (view == login){
            logInFunction();
        }else if (view == forget_pass){
            forgetPasswordFunction();
        }else if (view == reg_acc){
            startActivity(new Intent(LoginActivity.this, SelectUserTypeActivity.class));
        }else if (view == lang){
            configuration = this.getResources().getConfiguration();
            if (configuration.getLayoutDirection() == 1) {
                lang.setText("English");
            } else if (configuration.getLayoutDirection() == 0) {
                lang.setText("العربية");
            }
            if (lang.getText().toString().equals("العربية")) {
                lang.setText("English");
                mEditor.putString("language", "AR");
                mEditor.commit();
                String languageToLoad = "ar";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                configuration.locale = locale;
                configuration = this.getResources().getConfiguration();
                configuration.setLayoutDirection(new Locale("ar"));
                this.getResources().updateConfiguration(configuration, this.getResources().getDisplayMetrics());
            } else if (lang.getText().toString().equals("English")) {
                lang.setText("العربية");
                mEditor.putString("language", "EN");
                mEditor.commit();
                String languageToLoad = "en";
                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                configuration.locale = locale;
                configuration = this.getResources().getConfiguration();
                configuration.setLayoutDirection(new Locale("en"));
                this.getResources().updateConfiguration(configuration, this.getResources().getDisplayMetrics());
            }
            Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
            startActivity(intent);
            finishAffinity();
           // inProgressToast();
//            startActivity(new Intent(LoginActivity.this, CategoriesListActivity.class));
        }else if (view == exploreApp){
            Intent intent = new Intent(LoginActivity.this, ExploreAppActivity.class);
            startActivity(intent);
        }
    }

    private void inProgressToast(){
        Toast.makeText(LoginActivity.this, getResources().getString(R.string.workingOnIt), Toast.LENGTH_LONG).show();
    }
}