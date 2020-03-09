package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.DrawableClickListener;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditBoothActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    ArrayList<UserDetailsData> userDetailsData = new ArrayList<>();

    EditText boothName, phone, boothVarification_Maroof, branchName, paymentAccount_IBAN, accountHolderName, BoothUserName;
    TextView isVerify;
    TextView email;
    AutoCompleteTextView boothCity, boothType;
    Button confirmBtn, cancelBtn;

    Map<String, String> params = new HashMap<String, String>();

    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    ArrayList<String> boothTypeList = new ArrayList<>();
    String cityID = " ";
    String activity;
    String MaroofLink;

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
            setContentView(R.layout.activity_edit_booth);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            activity = getIntent().getStringExtra("Activity");
            Initialization();
            getCities();
            Actions();
            getUserDetails();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void FinalStep() {
        Animation shake = AnimationUtils.loadAnimation(EditBoothActivity.this, R.anim.shake);
        if (boothName.getText().toString().trim().length()==0) {
//            boothName.getParent().requestChildFocus(boothName,boothName);
            boothName.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (BoothUserName.getText().toString().trim().length()==0) {
//            boothUsername.getParent().requestChildFocus(boothUsername,boothUsername);
            BoothUserName.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().trim().length()==0) {
//            emailTextView.getParent().requestChildFocus(emailTextView,emailTextView);
            email.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (phone.getText().toString().trim().length()==0) {
//            boothPhone.getParent().requestChildFocus(boothPhone,boothPhone);
            phone.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this,getResources().getString(R.string.mobilenumberisnotvalid), Toast.LENGTH_SHORT).show();
        } else if (cityID.equals(" ")) {
//            city.getParent().requestChildFocus(city,city);
            boothCity.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (boothType.getText().toString().trim().length()==0) {
//            boothType.getParent().requestChildFocus(boothType,boothType);
            boothType.startAnimation(shake);
            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        }
//        else if (boothVarification.getText().toString().isEmpty()){
//            boothVarification.getParent().requestChildFocus(boothVarification,boothVarification);
//            boothVarification.startAnimation(shake);
//        }
//        else if (branchName.getText().toString().isEmpty()) {
////            branchName.getParent().requestChildFocus(branchName,branchName);
//            branchName.startAnimation(shake);
//            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//        } else if (paymentAccount_IBAN.getText().toString().isEmpty()) {
////            paymentAccount.getParent().requestChildFocus(paymentAccount,paymentAccount);
//            paymentAccount_IBAN.startAnimation(shake);
//            Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//        }
        else {
            if (!isTextValid(boothName.getText().toString())) {
                boothName.getParent().requestChildFocus(boothName, boothName);
                boothName.startAnimation(shake);
                Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.nameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!isUsernameValid(BoothUserName.getText().toString())) {
                BoothUserName.getParent().requestChildFocus(BoothUserName, BoothUserName);
                BoothUserName.startAnimation(shake);
                Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.usernameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                email.getParent().requestChildFocus(email, email);
                email.startAnimation(shake);
                Toast.makeText(EditBoothActivity.this, getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
            }
//            else if (boothPhone.getText().toString().length() <= 12){
//                boothPhone.getParent().requestChildFocus(boothPhone,boothPhone);
//                boothPhone.startAnimation(shake);
//                boothPhone.setText("");
//                Toast.makeText(RegisterBoothActivity.this, "Mobile Number is not valid", Toast.LENGTH_SHORT).show();
//            }
//            else if (!isTextValid(accountHolderName.getText().toString())) {
//                accountHolderName.getParent().requestChildFocus(accountHolderName, accountHolderName);
//                accountHolderName.startAnimation(shake);
//                accountHolderName.setText("");
//                Toast.makeText(EditBoothActivity.this, "Account Holder Name is not valid", Toast.LENGTH_SHORT).show();
//            }
            else {
                CustomLoader.showDialog(EditBoothActivity.this);
                EditProfileApi();
                //  new ImageUploadTask1().execute(1 + "", "user.jpg");
            }
        }
    }

    private void EditProfileApi() {

//        imgToStringFunction(bitmap);


        params.put("RoleID", "2");
        params.put("BoothName", boothName.getText().toString());
        params.put("BoothUserName", BoothUserName.getText().toString());
        params.put("Email", email.getText().toString());
        params.put("Mobile", phone.getText().toString());
        params.put("CityID", cityID);
        if (!boothVarification_Maroof.getText().toString().equals(MaroofLink)){
            params.put("MaroofLink", boothVarification_Maroof.getText().toString());
        }
        params.put("PaymentAccountNumber", paymentAccount_IBAN.getText().toString());
        params.put("PaymentAccountHolderName", accountHolderName.getText().toString());
        params.put("PaymentAccountBankBranch", branchName.getText().toString());
        params.put("LastState", "booth");
        if (boothType.getText().toString().isEmpty()) {
            params.put("BoothType", userDetailsData.get(0).getBoothType());
        } else {
            params.put("BoothType", boothType.getText().toString());
        }
        params.put("DeviceType", "Android");
        params.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, EditBoothActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");

                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.commit();

                            if (BoothCategoriesCount == 0) {
                                Intent intent = new Intent(EditBoothActivity.this, CategoriesListActivity.class);
                                intent.putExtra("Activity", "SignUp");
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(EditBoothActivity.this, BoothMainActivity.class);
                                startActivity(intent);
                            }

                            CustomLoader.dialog.dismiss();

                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        Log.e("saveBookErrorResponse1", e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static boolean isUsernameValid(String user_name) {
        String expression = "^[a-zA-Z0-9._]{3,}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(user_name);
        return matcher.matches();
    }

    public static boolean isTextValid(String text) {
        String expression = "^[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private void Initialization() {
        boothName = findViewById(R.id.boothName);
        BoothUserName = findViewById(R.id.BoothUserName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        boothVarification_Maroof = findViewById(R.id.boothVarification_Maroof);
        branchName = findViewById(R.id.branchName);
        paymentAccount_IBAN = findViewById(R.id.paymentAccount_IBAN);
        accountHolderName = findViewById(R.id.accountHolderName);
        boothCity = findViewById(R.id.boothCity);
        boothType = findViewById(R.id.boothType);
        confirmBtn = findViewById(R.id.confirmBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        isVerify = findViewById(R.id.isVerify);
    }

    private void Actions() {

        isVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVerify.getText().toString().equals("Verify")){
                    SendOTP();
                }
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinalStep();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.equals("Yes")){
                    switchAccount();
                }else {
                    finish();
                }
            }
        });

//        boothCity.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(boothCity) {
//            @Override
//            public boolean onDrawableClick() {
//                Log.e("drawableClicked", "Checked");
//                hideKeyboard(boothCity);
//                boothCity.showDropDown();
//                return true;
//            }
//        });

        boothCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
                hideKeyboard(boothCity);
                boothCity.showDropDown();
            }
        });

//        boothCity.setTextIsSelectable(true);
        boothCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    cityID = citiesData.get(position).getCityID();
//                    mEditor.putString("CityID", CityID).commit();
//                    mEditor.putString("City", citiesDataArrayList.get(position).getTitle()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        boothTypeList.add(getResources().getString(R.string.physical));
        boothTypeList.add(getResources().getString(R.string.virtual));
        ArrayAdapter<String> boothTypeAdapter = new ArrayAdapter<String>(EditBoothActivity.this,
                android.R.layout.simple_list_item_1, boothTypeList);
        boothTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        boothType.setAdapter(boothTypeAdapter);

//        boothType.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(boothType) {
//            @Override
//            public boolean onDrawableClick() {
//                hideKeyboard(boothType);
//                boothType.showDropDown();
//                return true;
//            }
//        });

        boothType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(boothType);
                boothType.showDropDown();
            }
        });

//        boothType.setTextIsSelectable(true);
        boothType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void switchAccount() {

        CustomLoader.showDialog(EditBoothActivity.this);
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("LastState", "user");
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, EditBoothActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");

                            mEditor.putString("LastState", LastState).apply();
                            // startActivity(new Intent(BoothSettingsActivity.this, MainActivity.class));
                            // finish();

                            Intent intent = new Intent(EditBoothActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void VerifyDialog() {
        final Dialog verifyDialog = new Dialog(EditBoothActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.verify_mobile_dialog);
        EditText codeEditText = verifyDialog.findViewById(R.id.codeEditText);
        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);
        TextView resend = verifyDialog.findViewById(R.id.resend_code);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReSendOTP();
            }
        });

        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDialog.dismiss();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEditText.getText().toString().isEmpty()) {
                    Toast.makeText(verifyDialog.getContext(), "Code field is empty", Toast.LENGTH_SHORT).show();
                } else {
                    VerifyOTP(codeEditText.getText().toString());
                    verifyDialog.dismiss();
                }
            }
        });
    }

    private void SendOTP() {

        CustomLoader.showDialog(EditBoothActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, EditBoothActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();

                            VerifyDialog();


                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(EditBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ReSendOTP() {

        CustomLoader.showDialog(EditBoothActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, EditBoothActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(EditBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void VerifyOTP(String otp) {

        CustomLoader.showDialog(EditBoothActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("OTP", otp);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VERIFYOTP, EditBoothActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(EditBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) EditBoothActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void getUserDetails() {

        CustomLoader.showDialog(EditBoothActivity.this);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, EditBoothActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", result);
                    try {
                        userDetailsData.clear();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
                            float BoothAverageRating = user_info.getInt("BoothAverageRating");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothCoverImage = user_info.getString("BoothCoverImage");
                            int BoothFollowersCount = user_info.getInt("BoothFollowersCount");
                            String BoothImage = user_info.getString("BoothImage");
                            String BoothName = user_info.getString("BoothName");
                            String BoothType = user_info.getString("BoothType");
                            String CityID = user_info.getString("CityID");
                            String CityTitle = user_info.getString("CityTitle");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            int UserFollowersCount = user_info.getInt("UserFollowersCount");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String BoothUserNamee = user_info.getString("BoothUserName");
                            MaroofLink = user_info.getString("MaroofLink");

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.apply();

                            userDetailsData.add(new UserDetailsData(About, AuthToken, BoothAverageRating, BoothCategoriesCount, BoothCoverImage, BoothFollowersCount, BoothImage, BoothName,
                                    BoothType, CityID, CityTitle, CompressedBoothCoverImage, CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays,
                                    ContactTimeFrom, ContactTimeTo, CoverImage, DeviceToken, Email, FullName, Gender, HideContactNo, Image, IsEmailVerified, IsMobileVerified,
                                    IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch, PaymentAccountHolderName, PaymentAccountNumber, UserCategoriesCount, UserFollowedBooths,
                                    UserFollowersCount, UserID, UserName, BoothUserNamee));

                            if (!userDetailsData.get(0).getCityTitle().isEmpty()) {
                                boothCity.setHint(userDetailsData.get(0).getCityTitle());
                                boothCity.setHintTextColor(getResources().getColor(R.color.font_color));
                            }
                            if (!userDetailsData.get(0).getBoothType().isEmpty()) {
                                boothType.setHint(userDetailsData.get(0).getBoothType());
                                boothType.setHintTextColor(getResources().getColor(R.color.font_color));
                            }

                            cityID = userDetailsData.get(0).getCityID();
                            boothName.setText(userDetailsData.get(0).getBoothName());
                            phone.setText(userDetailsData.get(0).getMobile());
                            BoothUserName.setText(userDetailsData.get(0).getBoothUserName());
                            email.setText(userDetailsData.get(0).getEmail());
                            branchName.setText(userDetailsData.get(0).getPaymentAccountBankBranch());
                            paymentAccount_IBAN.setText(userDetailsData.get(0).getPaymentAccountNumber());
                            accountHolderName.setText(userDetailsData.get(0).getPaymentAccountHolderName());

                            if (userDetailsData.get(0).getMobile().isEmpty()) {
                                isVerify.setText("");
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("0")) {
                                isVerify.setText(getResources().getString(R.string.verify));
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("1")) {
                                isVerify.setText(getResources().getString(R.string.verified));
                            }


                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(EditBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(EditBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, EditBoothActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCitiesResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONArray citiesArray = jsonObject.getJSONArray("cities");
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject jsonObject1 = citiesArray.getJSONObject(i);

                                String CityID = jsonObject1.getString("CityID");
                                String CityLat = jsonObject1.getString("CityLat");
                                String CityLong = jsonObject1.getString("CityLong");
                                String CityPlaceID = jsonObject1.getString("CityPlaceID");
                                String Title = jsonObject1.getString("Title");
                                citiesNames.add(Title);
                                citiesData.add(new CitiesData(CityID, CityLat, CityLong, CityPlaceID, Title));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(EditBoothActivity.this,
                                    android.R.layout.simple_list_item_1, citiesNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            boothCity.setAdapter(adapter);


                        } else {
                            Toast.makeText(EditBoothActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(EditBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
