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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.schopfen.Booth.Adapters.SuggestedBoothsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    EditText fullname, email, phoneNumber, details, orderNumber;
    TextView contactUsDesc;
    Button submitBtn;
    String contactUsDescription;

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
            setContentView(R.layout.activity_contact_us);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            String id = sharedpreferences.getString("id", "");

            initializeViews();
            initializeClickListeners();

            if (sharedpreferences.getString("Activity", "").equals("Setting")){
                orderNumber.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    contactUsDesc.setText(Html.fromHtml(sharedpreferences.getString("contactUsDescription", ""), Html.FROM_HTML_MODE_COMPACT));
                    Log.e("contactUs", sharedpreferences.getString("contactUsDescription", ""));

                } else {
                    contactUsDesc.setText(Html.fromHtml(sharedpreferences.getString("contactUsDescription", "")));
                }
                if (id.equals("1")){
                    fullname.setText(sharedpreferences.getString("BoothName", ""));
                    email.setText(sharedpreferences.getString("Email", ""));
                    phoneNumber.setText(sharedpreferences.getString("Mobile", ""));
                    fullname.setEnabled(false);
                    email.setEnabled(false);
                    phoneNumber.setEnabled(false);
                }else if (id.equals("0")) {
                    fullname.setText(sharedpreferences.getString("FullName", ""));
                    email.setText(sharedpreferences.getString("Email", ""));
                    phoneNumber.setText(sharedpreferences.getString("Mobile", ""));
                    fullname.setEnabled(false);
                    email.setEnabled(false);
                    phoneNumber.setEnabled(false);
                }
            }else {
                orderNumber.setVisibility(View.VISIBLE);
                contactUsApi();
                if (id.equals("1")){
                    fullname.setText(sharedpreferences.getString("BoothName", ""));
                    orderNumber.setText(sharedpreferences.getString("orderNumber", ""));
                    email.setText(sharedpreferences.getString("Email", ""));
                    phoneNumber.setText(sharedpreferences.getString("Mobile", ""));
                    fullname.setEnabled(false);
                    orderNumber.setEnabled(false);
                    email.setEnabled(false);
                    phoneNumber.setEnabled(false);
                }else if (id.equals("0")) {
                    fullname.setText(sharedpreferences.getString("FullName", ""));
                    orderNumber.setText(sharedpreferences.getString("orderNumber", ""));
                    email.setText(sharedpreferences.getString("Email", ""));
                    phoneNumber.setText(sharedpreferences.getString("Mobile", ""));
                    fullname.setEnabled(false);
                    orderNumber.setEnabled(false);
                    email.setEnabled(false);
                    phoneNumber.setEnabled(false);
                }
            }
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    public void onClick(View v) {

        if(v==submitBtn){
            Animation shake = AnimationUtils.loadAnimation(ContactUsActivity.this, R.anim.shake);
            if(fullname.getText().toString().isEmpty() || fullname.getText().toString().startsWith(" ")){
                fullname.startAnimation(shake);
            }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                email.startAnimation(shake);
                Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.invalidEmail), Toast.LENGTH_LONG).show();
            }else if(email.getText().toString().isEmpty() || email.getText().toString().startsWith(" ")){
                email.startAnimation(shake);
            }else if(details.getText().toString().isEmpty() || details.getText().toString().startsWith(" ")){
                details.startAnimation(shake);
            }else {
                sendFeedback();
            }
        }

    }

    private void contactUsApi() {

        Map<String, String> params = new HashMap<>();

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.about_us + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&PageID=2", ContactUsActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("ContactUsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject user = jsonObject.getJSONObject("page_data");
                            String Contact = user.getString("Contact");
                            contactUsDescription = user.getString("Description");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                contactUsDesc.setText(Html.fromHtml(contactUsDescription, Html.FROM_HTML_MODE_COMPACT));
                                Log.e("contactUs", contactUsDescription);

                            } else {
                                contactUsDesc.setText(Html.fromHtml(contactUsDescription));
                            }

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(ContactUsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(ContactUsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ContactUsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initializeViews(){
        fullname = findViewById(R.id.fullName);
        email = findViewById(R.id.emailAddress);
        orderNumber = findViewById(R.id.orderNumber);
        phoneNumber = findViewById(R.id.phoneNumber);
        details = findViewById(R.id.details);
        submitBtn = findViewById(R.id.submitBtn);
        contactUsDesc = findViewById(R.id.contactUsDesctiption);
    }

    private void initializeClickListeners(){
        submitBtn.setOnClickListener(this);
    }

    private void sendFeedback(){
        CustomLoader.showDialog(ContactUsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("FullName", fullname.getText().toString());
        body.put("Email", email.getText().toString());
        body.put("MobileNo", phoneNumber.getText().toString());
        body.put("OrderNumber", orderNumber.getText().toString());
        body.put("Message", details.getText().toString());
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("sendFeedbackBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.giveFeedback, ContactUsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("sendFeedbackResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200){

                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            JSONObject user_info = jsonObject.getJSONObject("user_info");

//
                            Toast.makeText(ContactUsActivity.this, message, Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(ContactUsActivity.this, MainActivity.class));
//                            finish();
                        }else {
                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
                            Toast.makeText(ContactUsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ContactUsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
