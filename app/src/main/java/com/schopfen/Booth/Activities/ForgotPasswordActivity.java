package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    EditText email;
    Button submitBtn;

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
            setContentView(R.layout.activity_forgot_password);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            initiatalizeViews();
            initializeClicks();
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

    @Override
    public void onClick(View v) {
        if (v == submitBtn){
            forgotPasswordFunction();
        }
    }

    void initiatalizeViews(){
        email = findViewById(R.id.email);
        submitBtn = findViewById(R.id.submitBtn);
    }

    void initializeClicks(){
        submitBtn.setOnClickListener(this);
    }

    void forgotPasswordFunction(){
        if (email.getText().toString().isEmpty()||email.getText().toString().startsWith(" ")) {
            Animation shake = AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.shake);
//                    login.setHintTextColor(getResources().getColor(R.color.drawerColor));
            email.startAnimation(shake);
            Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_LONG).show();
        }else{

            CustomLoader.showDialog(ForgotPasswordActivity.this);

            Map<String, String> body = new HashMap<String, String>();
            body.put("Query", email.getText().toString());
            body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
            body.put("OS", Build.VERSION.RELEASE);

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

            ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.forgotPassword, ForgotPasswordActivity.this, body, headers, new ServerCallback() {
                @Override
                public void onSuccess(String result, String ERROR) {
                    if (ERROR.isEmpty()){
                        Log.e("ForgotPasswordResponse", result);
                        try {

                            JSONObject jsonObject = new JSONObject(String.valueOf(result));
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            if (status == 200){

                                CustomLoader.dialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                                finish();

                            }else {
                                CustomLoader.dialog.dismiss();
                                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            CustomLoader.dialog.dismiss();
                            e.printStackTrace();
                        }
                    }else {
                        CustomLoader.dialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

}
