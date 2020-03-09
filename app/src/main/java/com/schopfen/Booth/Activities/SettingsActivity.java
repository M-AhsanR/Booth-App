package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static com.fxn.pix.Pix.start;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    SharedPreferences BoothstoreProductsPrefes;
    SharedPreferences.Editor BoothstoreProductsPrefesEditor;
    String BoothProductsPREFERENCES = "BOOTHPRODUCTS";
    SharedPreferences storeProductsPrefes;
    SharedPreferences.Editor storeProductsPrefesEditor;
    String ProductsPREFERENCES = "PRODUCTS";
    SharedPreferences promotedProductsPrefes;
    SharedPreferences.Editor promotedProductsPrefesEditor;
    String PromotedPREFERENCES = "PROMOTEDPRODUCTS";

    ImageView dialogGif;
    LinearLayout lineareditprofile, linearChangePassword, contactUs, aboutUs, inviteFriends, switchAccount, viewMyProile, points_linear,
            view_transactions, change_language, blocked_layout;
    ArrayList<String> myreturnValue = new ArrayList<>();
    Uri myUri;
    TextView logout;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<UserDetailsData> userDetailsData = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<String> genderArray = new ArrayList<>();
    Map<String, String> params = new HashMap<String, String>();
    Bitmap bitmap;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    String contactUsDescription, FullName, Email, Facebook, Instagram, Linkedin, Twitter, Whatsapp;
    Dialog dialog;
    Button cancelBtn;
    Button createBtn;
    Button btn_yes, btn_no;
    TextView dialog_alert_text,language_text_of_arrabic;
    CountryCodePicker ccp_edit;
    Configuration configuration;
    TextView editIsVerify;
    CircleImageView profPicture;
    String CityID, fullnameString, emailString, boothnameString, phoneString;

    TextView editEmail, nameUsername, buildVersion;
    EditText editFullname, editPhone, editUsername;
    AutoCompleteTextView editCity, editGender;
    ImageView cam, facebook, twitter, instagram, linkedIn, mail;

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
            setContentView(R.layout.activity_settings);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            storeProductsPrefes = getSharedPreferences(ProductsPREFERENCES, Context.MODE_PRIVATE);
            storeProductsPrefesEditor = storeProductsPrefes.edit();
            promotedProductsPrefes = getSharedPreferences(PromotedPREFERENCES, Context.MODE_PRIVATE);
            promotedProductsPrefesEditor = promotedProductsPrefes.edit();
            BoothstoreProductsPrefes = getSharedPreferences(BoothProductsPREFERENCES, Context.MODE_PRIVATE);
            BoothstoreProductsPrefesEditor = storeProductsPrefes.edit();

            initializeViews();
            initializeClickListener();
            initializeEditProfileDialog();
            getUserDetails();
            getCities();
            contactUsApi();

            buildVersion.setText("Build 1.0");

            configuration = this.getResources().getConfiguration();
            if (configuration.getLayoutDirection() == 1) {
                language_text_of_arrabic.setText("English");
            } else if (configuration.getLayoutDirection() == 0) {
                language_text_of_arrabic.setText("العربية");
            }
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void VerifyDialog() {
        final Dialog verifyDialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                    Toast.makeText(verifyDialog.getContext(), getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_SHORT).show();
                } else {
                    VerifyOTP(codeEditText.getText().toString());
                    verifyDialog.dismiss();
                }
            }
        });
    }

    private void SendOTP() {

        CustomLoader.showDialog(SettingsActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, SettingsActivity.this, body, headers, new ServerCallback() {
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
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                            VerifyDialog();


                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void ReSendOTP() {

        CustomLoader.showDialog(SettingsActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, SettingsActivity.this, body, headers, new ServerCallback() {
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
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void VerifyOTP(String otp) {

        CustomLoader.showDialog(SettingsActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("OTP", otp);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VERIFYOTP, SettingsActivity.this, body, headers, new ServerCallback() {
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
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == lineareditprofile) {
            editProfileFunction();
        } else if (view == linearChangePassword) {
            changePasswordFunction();
        } else if (view == contactUs) {
            Intent i = new Intent(SettingsActivity.this, ContactUsActivity.class);
            mEditor.putString("id", "0").commit();
            mEditor.putString("FullName", fullnameString).commit();
            mEditor.putString("Email", emailString).commit();
            mEditor.putString("Mobile", phoneString).commit();
            mEditor.putString("contactUsDescription", contactUsDescription).commit();
            mEditor.putString("Activity", "Setting").commit();
            startActivity(i);
        } else if (view == aboutUs) {
            Intent i = new Intent(SettingsActivity.this, AboutUsActivity.class);
            startActivity(i);
        } else if (view == inviteFriends) {
            inviteFriendsClick();
        } else if (view == logout) {
            logoutFunction();
        } else if (view == facebook) {
            openFacebook();
        } else if (view == twitter) {
            openTwitter();
        } else if (view == instagram) {
            openInstagram();
        } else if (view == linkedIn) {
            openLinkedIn();
        } else if (view == mail) {
            openMail();
        } else if (view == switchAccount) {
            Dialog dialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            dialog.setContentView(R.layout.areyousureloader);
            // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            btn_no = dialog.findViewById(R.id.btn_no);
            btn_yes = dialog.findViewById(R.id.btn_yes);
            dialog_alert_text = dialog.findViewById(R.id.dialog_alert_text);
            dialog_alert_text.setText(getResources().getString(R.string.switch_account_string));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            btn_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchAccount();
                    dialog.dismiss();
                }
            });
        } else if (view == viewMyProile) {
            startActivity(new Intent(SettingsActivity.this, MyProfileActivity.class));
        } else if (view == points_linear) {
//            Toast.makeText(SettingsActivity.this, "Mmm, working on it!", Toast.LENGTH_SHORT).show();
        } else if (view == view_transactions) {
            Intent intent = new Intent(SettingsActivity.this, TransactionHistory.class);
            startActivity(intent);
        } else if (view == change_language) {
            changeLanguage();
          //  Toast.makeText(SettingsActivity.this, "Mmm, working on it!", Toast.LENGTH_SHORT).show();
        } else if (view == blocked_layout) {
            startActivity(new Intent(SettingsActivity.this, BlockUserActivity.class));
        }
    }

    public void initializeClickListener() {
        lineareditprofile.setOnClickListener(this);
        linearChangePassword.setOnClickListener(this);
        contactUs.setOnClickListener(this);
        aboutUs.setOnClickListener(this);
        inviteFriends.setOnClickListener(this);
        logout.setOnClickListener(this);
        facebook.setOnClickListener(this);
        twitter.setOnClickListener(this);
        instagram.setOnClickListener(this);
        linkedIn.setOnClickListener(this);
        mail.setOnClickListener(this);
        switchAccount.setOnClickListener(this);
        viewMyProile.setOnClickListener(this);
        points_linear.setOnClickListener(this);
        view_transactions.setOnClickListener(this);
        change_language.setOnClickListener(this);
        blocked_layout.setOnClickListener(this);
    }

    public void initializeViews() {
        lineareditprofile = findViewById(R.id.ll_editprofile_sa);
        linearChangePassword = findViewById(R.id.ll_change_password);
        contactUs = findViewById(R.id.contactUs);
        aboutUs = findViewById(R.id.aboutUs);
        inviteFriends = findViewById(R.id.inviteFriends);
        logout = findViewById(R.id.tv_logout);
        facebook = findViewById(R.id.openFacebook);
        twitter = findViewById(R.id.openTwitter);
        instagram = findViewById(R.id.openInsta);
        linkedIn = findViewById(R.id.openLinkedIn);
        mail = findViewById(R.id.openMail);
        nameUsername = findViewById(R.id.nameUsername);
        buildVersion = findViewById(R.id.buildVersion);
        switchAccount = findViewById(R.id.switchAccount);
        viewMyProile = findViewById(R.id.viewMyProfile);
        points_linear = findViewById(R.id.points_linear);
        view_transactions = findViewById(R.id.view_transactions);
        change_language = findViewById(R.id.change_language);
        blocked_layout = findViewById(R.id.blocked_layout);
        language_text_of_arrabic = findViewById(R.id.languagetext);

        // dialogGif.setVisibility(View.GONE);
    }

    private void changeLanguage(){
        Dialog dialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.areyousureloader);
        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes = dialog.findViewById(R.id.btn_yes);
        dialog_alert_text = dialog.findViewById(R.id.dialog_alert_text);
        dialog_alert_text.setText(getResources().getString(R.string.areyousure));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configuration = getResources().getConfiguration();
                if (configuration.getLayoutDirection() == 1) {
                    language_text_of_arrabic.setText("English");
                } else if (configuration.getLayoutDirection() == 0) {
                    language_text_of_arrabic.setText("العربية");
                }
                if (language_text_of_arrabic.getText().toString().equals("العربية")) {
                    language_text_of_arrabic.setText("English");
                    mEditor.putString("language", "AR");
                    mEditor.commit();
                    String languageToLoad = "ar";
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    configuration.locale = locale;
                    configuration = getResources().getConfiguration();
                    configuration.setLayoutDirection(new Locale("ar"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                } else if (language_text_of_arrabic.getText().toString().equals("English")) {
                    language_text_of_arrabic.setText("العربية");
                    mEditor.putString("language", "EN");
                    mEditor.commit();
                    String languageToLoad = "en";
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    configuration.locale = locale;
                    configuration = getResources().getConfiguration();
                    configuration.setLayoutDirection(new Locale("en"));
                    getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
                }
                UpdateLanguage();
                dialog.dismiss();
            }
        });
    }

    //This function will be called when change password will be clicked
    private void changePasswordFunction() {
        final Dialog changePassdialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        changePassdialog.setContentView(R.layout.change_password_dialog);
        Button cancelBtn = changePassdialog.findViewById(R.id.btn_cancel);
        Button updateBtn = changePassdialog.findViewById(R.id.btn_update);
        EditText password = changePassdialog.findViewById(R.id.et_password);
        EditText new_password = changePassdialog.findViewById(R.id.et_newpassword);
        EditText re_password = changePassdialog.findViewById(R.id.et_repassword);

        changePassdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        changePassdialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassdialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation shake = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.shake);
                if (password.getText().toString().isEmpty()) {
                    password.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (new_password.getText().toString().isEmpty()) {
                    new_password.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (re_password.getText().toString().isEmpty()) {
                    re_password.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (!new_password.getText().toString().equals(re_password.getText().toString())) {
                    new_password.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.passwordmismatch), Toast.LENGTH_SHORT).show();
                } else if (new_password.getText().toString().length() < 6) {
                    new_password.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.passwordmustcontainsixcharcters), Toast.LENGTH_SHORT).show();
                } else {
                    if (!password.getText().toString().equals(sharedpreferences.getString("Password", " "))) {
                        password.startAnimation(shake);
                        Toast.makeText(SettingsActivity.this, getResources().getString(R.string.passwordiswrong), Toast.LENGTH_SHORT).show();
                    } else {
                        changePasswordApiCall(password.getText().toString(), new_password.getText().toString());
                        changePassdialog.dismiss();
                    }
                }
            }
        });

    }

    private void changePasswordApiCall(String old_pass, String new_pass) {
        CustomLoader.showDialog(SettingsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("OldPassword", old_pass);
        body.put("NewPassword", new_pass);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.CHANGEPASSWORD, SettingsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            logoutApiCall();
                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    // This Function will be called when edit profile will be clicked.
    private void editProfileFunction() {

//        getUserDetails();

//        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        CityID = userDetailsData.get(0).getCityID();

        if (userDetailsData.get(0).getCompressedImage().isEmpty()) {
            dialogGif.setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editCity.setAdapter(adapter);

//        editCity.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(editCity) {
//            @Override
//            public boolean onDrawableClick() {
//                Log.e("drawableClicked", "Checked");
//                editCity.showDropDown();
//                hideKeyboard(editCity);
//                return true;
//            }
//        });
        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
                editCity.showDropDown();
            }
        });
        editCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CityID = citiesData.get(position).getCityID();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(SettingsActivity.this,
                android.R.layout.simple_list_item_1, genderArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editGender.setAdapter(genderAdapter);
//        editGender.setOnTouchListener(new DrawableClickListener.RightDrawableClickListener(editGender) {
//            @Override
//            public boolean onDrawableClick() {
//                hideKeyboard(editGender);
//                editGender.showDropDown();
//                return true;
//            }
//        });
        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGender.showDropDown();
            }
        });
        editGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("cameraClick", "yes");
                myreturnValue.clear();
                start(SettingsActivity.this,                    //Activity or Fragment Instance
                        1234,                //Request code for activity results
                        1);    //Number of images to restict selection count

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation shake = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.shake);
                if (editFullname.getText().toString().trim().length() == 0) {
                    editFullname.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editUsername.getText().toString().trim().length() == 0) {
                    editUsername.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editEmail.getText().toString().trim().length() == 0) {
                    editEmail.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (!ccp_edit.isValidFullNumber()) {
                    editPhone.getParent().requestChildFocus(editPhone, editPhone);
                    editPhone.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.mobilenumberisnotvalid), Toast.LENGTH_SHORT).show();
                } else if (editGender.getText().toString().trim().length() == 0) {
                    editGender.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editCity.getText().toString().trim().length() == 0) {
                    editCity.startAnimation(shake);
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
                        editEmail.startAnimation(shake);
                        Toast.makeText(SettingsActivity.this,getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
                    } else {
                        new UploadFileToServer().execute();
                        dialog.dismiss();
                    }
                }
            }
        });

    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) SettingsActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    // For Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            myreturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            for (int a = 0; a < myreturnValue.size(); a++) {
                File imgFile = new File(myreturnValue.get(a));
//                Uri myUri = Uri.parse(returnValue.get(a));
                myUri = Uri.fromFile(new File(myreturnValue.get(a)));

                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    profilePic.setImageBitmap(myBitmap);
                }
                beginCrop(myUri);

            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    // For Camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(SettingsActivity.this, 1234, 1);
                } else {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.approvepermissions), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    // Image Croper
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(SettingsActivity.this);
    }

    // Image Croper Handling
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profPicture.setImageBitmap(null);
            profPicture.setImageURI(UCrop.getOutput(result));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
                bitmapArrayList.add(bitmap);
                saveToInternalStorage(bitmap);
                Log.e("chekingBit", bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void inviteFriendsClick() {
        final Dialog dialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.invite_friends_dialog);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        Button updateBtn = dialog.findViewById(R.id.btn_update);
        EditText email_address = dialog.findViewById(R.id.email_address);
        EditText mobile_number = dialog.findViewById(R.id.mobile_number);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email_address.getText().toString().isEmpty()) {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_SHORT).show();
                } else if (mobile_number.getText().toString().isEmpty()) {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email_address.getText().toString()).matches()) {
                        Toast.makeText(SettingsActivity.this,getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
                    } else {
                        InviteFriend(email_address.getText().toString(), mobile_number.getText().toString());
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    void logoutFunction() {

        Button btn_no, btn_yes;
        TextView dialog_alert_text;

        Dialog dialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.areyousureloader);
        // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        btn_no = dialog.findViewById(R.id.btn_no);
        btn_yes = dialog.findViewById(R.id.btn_yes);
        dialog_alert_text = dialog.findViewById(R.id.dialog_alert_text);

        dialog_alert_text.setText(getResources().getString(R.string.logout_string));

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                logoutApiCall();
            }
        });
    }

    private void logoutApiCall() {
        CustomLoader.showDialog(SettingsActivity.this);

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.logout + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SettingsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("LogoutResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();

                            mEditor.clear().commit();
                            storeProductsPrefesEditor.clear().commit();
                            promotedProductsPrefesEditor.clear().commit();
                            BoothstoreProductsPrefesEditor.clear().commit();
                            Intent loginscreen = new Intent(SettingsActivity.this, SplashScreen.class);
                            loginscreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(loginscreen);
                            finishAffinity();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void InviteFriend(String email, String mobile) {
        CustomLoader.showDialog(SettingsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Mobile", mobile);
        body.put("Email", email);
        body.put("AndroidAppVersion", String.valueOf(Build.VERSION.RELEASE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.INVITE, SettingsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("Invite", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserDetails() {

        CustomLoader.showDialog(SettingsActivity.this);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SettingsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", " " + result);
                    try {
                        userDetailsData.clear();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            //dialogGif.setVisibility(View.GONE);
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
                            String cityID = user_info.getString("CityID");
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
                            String F_name = user_info.getString("FullName");
                            String BoothUserName = user_info.getString("BoothUserName");

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.apply();

                            userDetailsData.add(new UserDetailsData(About, AuthToken, BoothAverageRating, BoothCategoriesCount, BoothCoverImage, BoothFollowersCount, BoothImage, BoothName,
                                    BoothType, cityID, CityTitle, CompressedBoothCoverImage, CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays,
                                    ContactTimeFrom, ContactTimeTo, CoverImage, DeviceToken, Email, FullName, Gender, HideContactNo, Image, IsEmailVerified, IsMobileVerified,
                                    IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch, PaymentAccountHolderName, PaymentAccountNumber, UserCategoriesCount, UserFollowedBooths,
                                    UserFollowersCount, UserID, UserName, BoothUserName));


                            Pattern complie = Pattern.compile(" ");
                            String[] phonenUmber = complie.split(userDetailsData.get(0).getMobile());

                            fullnameString = F_name;
                            emailString = Email;
                            boothnameString = BoothName;
                            phoneString = Mobile;
                            profPicture.setVisibility(View.VISIBLE);
                            cam.setVisibility(View.VISIBLE);
                            dialogGif.setVisibility(View.GONE);
                            Picasso.get().load(Constants.URL.IMG_URL + userDetailsData.get(0).getCompressedImage()).placeholder(R.drawable.user).into(profPicture);
                            editCity.setText(userDetailsData.get(0).getCityTitle());
                            CityID = userDetailsData.get(0).getCityID();
                            editFullname.setText(userDetailsData.get(0).getFullName());
                            if (!userDetailsData.get(0).getGender().equals("")) {
                                String gender = "";
                                if (userDetailsData.get(0).getGender().equals("Male")){
                                    gender = genderArray.get(0);
                                }else {
                                    gender = genderArray.get(1);
                                }
                                editGender.setText(gender);
//                                editGender.setText(userDetailsData.get(0).getGender());
                            }
                            for (int i = 1; i < phonenUmber.length; i++) {
                                editPhone.append(phonenUmber[i]);
                            }

                            editUsername.setEnabled(false);
                            editUsername.setText(userDetailsData.get(0).getUserName());
                            editEmail.setText(userDetailsData.get(0).getEmail());
                            if (userDetailsData.get(0).getMobile().isEmpty()) {
                                editPhone.setText("");
                            } else {
                                int length = ccp_edit.getSelectedCountryCodeWithPlus().length();
                                editPhone.setText(userDetailsData.get(0).getMobile().substring(length));
                            }


                            if (userDetailsData.get(0).getMobile().isEmpty()) {
                                editIsVerify.setText("");
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("0")) {
                                editIsVerify.setText(getResources().getString(R.string.verify));
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("1")) {
                                editIsVerify.setText(getResources().getString(R.string.verified));
                            }

                            nameUsername.setText(userDetailsData.get(0).getFullName() + " | " + "@" + userDetailsData.get(0).getUserName());

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SettingsActivity.this, body, headers, new ServerCallback() {
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

//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
//                                    android.R.layout.simple_list_item_1, citiesNames);
//                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//                            editCity.setAdapter(adapter);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void UpdateLanguage(){

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("LastLangState", sharedpreferences.getString("language", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, SettingsActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {

                    Intent intent = new Intent(SettingsActivity.this, SplashScreen.class);
                    startActivity(intent);
                    finishAffinity();

                } else {
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            CustomLoader.showDialog(SettingsActivity.this);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.updateProfile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (uriArrayList.size() > 0){
                    for (int i = 0; i < uriArrayList.size(); i++){
                        File sourceFile = new File(uriArrayList.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("Image", new FileBody(sourceFile));
                    }
                }

                entity.addPart("RoleID", new com.schopfen.Booth.ApiStructure.StringBody("2"));
                entity.addPart("FullName", new com.schopfen.Booth.ApiStructure.StringBody(editFullname.getText().toString()));
                entity.addPart("UserName", new com.schopfen.Booth.ApiStructure.StringBody(editUsername.getText().toString()));
                entity.addPart("LastLangState", new StringBody(sharedpreferences.getString("language", "")));
                entity.addPart("Email", new com.schopfen.Booth.ApiStructure.StringBody(editEmail.getText().toString()));
                entity.addPart("Mobile", new com.schopfen.Booth.ApiStructure.StringBody(ccp_edit.getFullNumberWithPlus()));
                if (editGender.getText().toString().isEmpty()) {
                    String genderFinal = "";
                    if (userDetailsData.get(0).getGender().equals(genderArray.get(0))){
                        genderFinal = "Male";
                    }else {
                        genderFinal = "Female";
                    }
                    Log.e("Gender", "if");
                    entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                } else {
                    String genderFinal = "";
                    if (editGender.getText().toString().equals(genderArray.get(0))){
                        genderFinal = "Male";
                    }else {
                        genderFinal = "Female";
                    }
                    Log.e("Gender", "else");
                    entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                }
                entity.addPart("CityID", new com.schopfen.Booth.ApiStructure.StringBody(CityID));
                entity.addPart("DeviceType", new com.schopfen.Booth.ApiStructure.StringBody("Android"));
                entity.addPart("DeviceToken", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("DeviceToken", " ")));
                entity.addPart("UserID", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("UserID", " ")));
                entity.addPart("OS", new com.schopfen.Booth.ApiStructure.StringBody(Build.VERSION.RELEASE));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));

                // Extra parameters if you want to pass to server

//                Map<String, String> headers = new HashMap<>();
//                headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

//                totalSize = entity.getContentLength();
//                final StringEntity se = new StringEntity(entity, ContentType.APPLICATION_JSON);
                httppost.setEntity(entity);
                httppost.setHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ResponseResult", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);

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


                    mEditor.putString("UserID", UserID);
                    mEditor.putString("ApiToken", AuthToken);
                    mEditor.putString("DeviceToken", DeviceToken);
                    mEditor.putString("LastState", LastState);
                    mEditor.commit();

                    nameUsername.setText(FullName + " | " + "@" + UserName);
                    mEditor.putString("LastState", LastState).commit();

                    CustomLoader.dialog.dismiss();

                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(SettingsActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        uriArrayList.clear();
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayList.add(myUri);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void imageUploadFunction() {
        CustomLoader.showDialog(SettingsActivity.this);
        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("FullName", editFullname.getText().toString());
        params.put("UserName", editUsername.getText().toString());
        params.put("Email", editEmail.getText().toString());
        params.put("Mobile", ccp_edit.getFullNumberWithPlus());
        if (editGender.getText().toString().isEmpty()) {
            Log.e("Gender", "if");
            params.put("Gender", userDetailsData.get(0).getGender());
        } else {
            Log.e("Gender", "else");
            params.put("Gender", editGender.getText().toString());
        }
        params.put("CityID", CityID);
        params.put("DeviceType", "Android");
        params.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("updateUserProfileParams", params.toString());

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, SettingsActivity.this, params, header, new ServerCallback() {
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


                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.commit();

                            nameUsername.setText(FullName + " | " + "@" + UserName);
                            mEditor.putString("LastState", LastState).commit();

                            CustomLoader.dialog.dismiss();

                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
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

    private Map<String, String> imgToStringFunction(Bitmap bitmap) {

        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Compressed
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos); // Compressed
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] data = bos.toByteArray(); // Compressed
                byte[] original_data = original_image.toByteArray(); // Original
                // images[0] = Base64.encodeToString(data, Base64.DEFAULT);
                // images[1] = Base64.encodeToString(original_data, Base64.DEFAULT);
                params.put("Image", Base64.encodeToString(data, Base64.NO_WRAP));
            }
        }
        return params;
    }

    private void contactUsApi() {

//        CustomLoader.showDialog(SettingsActivity.this);

        Map<String, String> params = new HashMap<>();

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.about_us + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&PageID=2", SettingsActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("ContactUsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

//                            CustomLoader.dialog.dismiss();

                            JSONObject user = jsonObject.getJSONObject("page_data");
                            String Contact = user.getString("Contact");
                            contactUsDescription = user.getString("Description");
                            FullName = user.getString("FullName");
                            Email = user.getString("Email");
                            Facebook = user.getString("Facebook");
                            Instagram = user.getString("Instagram");
                            Linkedin = user.getString("Linkedin");
                            Twitter = user.getString("Twitter");
                            Whatsapp = user.getString("Whatsapp");

                        } else {
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SettingsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void openMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {Email};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
//        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject text here...");
//        intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here...");
//        intent.putExtra(Intent.EXTRA_CC,"mailcc@gmail.com");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    private void openFacebook() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebook));
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + facebook));
            startActivity(intent);
        }
    }

    private void openInstagram() {
//        if ((isPackageInstalled("com.instagram.android", getPackageManager()))) {
        String url = "https://instagram.com/" + Instagram;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
//        } else {
//            Toast.makeText(SettingsActivity.this, "Please Install Instagram first", Toast.LENGTH_SHORT).show();
//        }
    }

    private void openLinkedIn() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://add/%@" + linkedIn));
        final PackageManager packageManager = SettingsActivity.this.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=" + linkedIn));
        }
        startActivity(intent);
    }

    private void openTwitter() {
//        if ((isPackageInstalled("com.twitter.android", getPackageManager()))) {
        String url = "http://twitter.com/" + Twitter;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
//        } else {
//            Toast.makeText(SettingsActivity.this, "Please Install Twitter first", Toast.LENGTH_SHORT).show();
//        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    private void initializeEditProfileDialog() {
        dialog = new Dialog(SettingsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.edit_profile_dialog_sa);

        cancelBtn = dialog.findViewById(R.id.btn_cancel_sa);
        createBtn = dialog.findViewById(R.id.btn_update_sa);
        cam = dialog.findViewById(R.id.camera_dialog_sa);
        profPicture = dialog.findViewById(R.id.myprofilepic);
        editFullname = dialog.findViewById(R.id.fullname);
        editUsername = dialog.findViewById(R.id.userName);
        editPhone = dialog.findViewById(R.id.phone);
        editEmail = dialog.findViewById(R.id.email);
        editCity = dialog.findViewById(R.id.city);
        editGender = dialog.findViewById(R.id.gender);
        editIsVerify = dialog.findViewById(R.id.isVerify);
        dialogGif = dialog.findViewById(R.id.dialog_profile_gif);
        ccp_edit = dialog.findViewById(R.id.ccpicker_edit);

        ccp_edit.registerCarrierNumberEditText(editPhone);

        genderArray.clear();

        genderArray.add(getResources().getString(R.string.male));
        genderArray.add(getResources().getString(R.string.female));

        editIsVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editIsVerify.getText().toString().equals("Verify")) {
                    SendOTP();
                }
            }
        });
    }

    private void switchAccount() {
        CustomLoader.showDialog(SettingsActivity.this);
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("LastState", "booth");
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, SettingsActivity.this, params, header, new ServerCallback() {
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

                            mEditor.putString("LastState", LastState).commit();

                            storeProductsPrefesEditor.clear().commit();
                            promotedProductsPrefesEditor.clear().commit();
                            BoothstoreProductsPrefesEditor.clear().commit();

                            if (BoothName.isEmpty()) {
                                // startActivity(new Intent(SettingsActivity.this, EditBoothActivity.class));
                                // finish();
                                Intent intent = new Intent(SettingsActivity.this, EditBoothActivity.class);
                                intent.putExtra("Activity", "Yes");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else if (BoothCategoriesCount == 0) {
                                // startActivity(new Intent(SettingsActivity.this, CategoriesListActivity.class));
                                // finish();
                                Intent intent = new Intent(SettingsActivity.this, CategoriesListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("Activity", "SignUp");
                                startActivity(intent);
                            } else if (IsProfileCustomized.equals("0")) {
                                // startActivity(new Intent(SettingsActivity.this, SetupBoothProfileActivity.class));
                                //finish();
                                Intent intent = new Intent(SettingsActivity.this, SetupBoothProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
//                                startActivity(new Intent(SettingsActivity.this, BoothMainActivity.class));
//                                finish();
                                Intent intent = new Intent(SettingsActivity.this, BoothMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }

                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
