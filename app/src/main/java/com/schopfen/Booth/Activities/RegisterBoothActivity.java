package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import de.hdodenhof.circleimageview.CircleImageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.hbb20.CountryCodePicker;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.DrawableClickListener;
import com.schopfen.Booth.Models.Chat_Data;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.fxn.pix.Pix.start;
import static com.schopfen.Booth.Activities.RegisterBayerActivity.isTextValid;
import static com.schopfen.Booth.Activities.RegisterBayerActivity.isUsernameValid;

public class RegisterBoothActivity extends AppCompatActivity implements View.OnClickListener {

    TextView registerAgreement;
    AutoCompleteTextView city, boothType;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    Map<String, String> params = new HashMap<String, String>();
    CountryCodePicker ccp_booth;
    private static final int REQUEST = 112;

    EditText boothName, boothUsername, boothPhone, boothVarification, paymentAccount, accountHolderName, branchName, password, confirmPassword, email, address;
    double latitude = 0.0, longitude = 0.0;
    TextView boothLogoBtn;
    Button confirmBtn;
    CircleImageView boothLogo;
    CheckBox checkBox;
    String CityID = " ";

    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<String> boothTypeList = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    ArrayList<String> returnValue = new ArrayList<>();
    Uri myUri;
   ArrayList<Uri> uriArrayList = new ArrayList<>();
    Bitmap bitmap;
    MultipartEntity entity;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    String deviceToken;

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
            setContentView(R.layout.activity_register_booth);

            Bundle bundle = getIntent().getExtras();
            int backgroundid = (int) bundle.get("backgroundkey");

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            deviceToken = sharedpreferences.getString("DeviceToken", "");

            initilizeViews();
            inititalizeClickListeners();
            getCities();
            ccp_booth.registerCarrierNumberEditText(boothPhone);

            boothTypeList.add(getResources().getString(R.string.physical));
            boothTypeList.add(getResources().getString(R.string.virtual));
            ArrayAdapter<String> boothTypeAdapter = new ArrayAdapter<String>(RegisterBoothActivity.this,
                    android.R.layout.simple_list_item_1, boothTypeList);
            boothTypeAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            boothType.setAdapter(boothTypeAdapter);

            boothType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boothType.showDropDown();
                }
            });

            boothType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    try {

                        if (boothTypeList.get(position).equals(getResources().getString(R.string.physical))) {
                            address.setVisibility(View.VISIBLE);
                        } else {
                            address.setVisibility(View.GONE);
                            address.setText("");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
                        if (!hasPermissions(RegisterBoothActivity.this, PERMISSIONS)) {
                            ActivityCompat.requestPermissions((Activity) RegisterBoothActivity.this, PERMISSIONS, REQUEST);
                        } else {
                            startActivity(new Intent(RegisterBoothActivity.this, MapsActivity.class));
                        }
                    } else {
                        startActivity(new Intent(RegisterBoothActivity.this, MapsActivity.class));
                    }

                }
            });

            city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("drawableClicked", "Checked");
                    city.showDropDown();
                }
            });

            city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    try {
                        CityID = citiesData.get(position).getCityID();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            SpannableString str = new SpannableString(getResources().getString(R.string.register_agreement));
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 15, 35, 15);
            registerAgreement.setText(str);
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) RegisterBoothActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (CustomLoader.dialog != null){
//            CustomLoader.dialog.dismiss();
//        }
        if (address != null) {
            address.setText(MapsActivity.AddressLine);
            latitude = MapsActivity.lat;
            longitude = MapsActivity.lng;
            MapsActivity.AddressLine = "";
            MapsActivity.cityLine = "";
            MapsActivity.lat = 0.0;
            MapsActivity.lng = 0.0;
        }
    }

    // Initilized Widgets
    private void initilizeViews() {
        boothName = findViewById(R.id.boothName);
        boothUsername = findViewById(R.id.BoothUserName);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        boothPhone = findViewById(R.id.boothPhone);
        boothVarification = findViewById(R.id.boothVarification_Maroof);
        paymentAccount = findViewById(R.id.paymentAccount_IBAN);
        accountHolderName = findViewById(R.id.accountHolderName);
        branchName = findViewById(R.id.branchName);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        registerAgreement = findViewById(R.id.register_agreement);
        boothLogoBtn = findViewById(R.id.logoBtn);
        boothLogo = findViewById(R.id.boothLogo);
        city = findViewById(R.id.boothCity);
        boothType = findViewById(R.id.boothType);
        confirmBtn = findViewById(R.id.confirmBtn);
        checkBox = findViewById(R.id.checkBox);
        ccp_booth = findViewById(R.id.ccpicker_booth);

        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        email.setOnClickListener(this);
    }

    private void inititalizeClickListeners() {
        confirmBtn.setOnClickListener(this);
        boothLogo.setOnClickListener(this);
        registerAgreement.setOnClickListener(this);
    }

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, RegisterBoothActivity.this, body, headers, new ServerCallback() {
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

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterBoothActivity.this,
                                    android.R.layout.simple_list_item_1, citiesNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            city.setAdapter(adapter);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(RegisterBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RegisterBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void FinalStep() {
        Animation shake = AnimationUtils.loadAnimation(RegisterBoothActivity.this, R.anim.shake);
        if (boothName.getText().toString().trim().length() == 0) {
//            boothName.getParent().requestChildFocus(boothName,boothName);
            boothName.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (boothUsername.getText().toString().trim().length() == 0) {
//            boothUsername.getParent().requestChildFocus(boothUsername,boothUsername);
            boothUsername.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().trim().length() == 0) {
//            emailTextView.getParent().requestChildFocus(emailTextView,emailTextView);
            email.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (!ccp_booth.isValidFullNumber()) {
//            boothPhone.getParent().requestChildFocus(boothPhone,boothPhone);
            boothPhone.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.mobilenumbermustbexxxx), Toast.LENGTH_SHORT).show();
        } else if (city.getText().toString().trim().length() == 0) {
//            city.getParent().requestChildFocus(city,city);
            city.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (boothType.getText().toString().trim().length() == 0) {
//            boothType.getParent().requestChildFocus(boothType,boothType);
            boothType.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        }
//        else if (address.getVisibility() == View.VISIBLE){
//            if (address.getText().toString().trim().length()==0){
//                address.startAnimation(shake);
//                Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//            }
//        }
//        else if (boothVarification.getText().toString().isEmpty()){
//            boothVarification.getParent().requestChildFocus(boothVarification,boothVarification);
//            boothVarification.startAnimation(shake);
//        }
//        else if (paymentAccount.getText().toString().isEmpty()){
////            paymentAccount.getParent().requestChildFocus(paymentAccount,paymentAccount);
//            paymentAccount.startAnimation(shake);
//            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//        }else if (accountHolderName.getText().toString().isEmpty()){
////            accountHolderName.getParent().requestChildFocus(accountHolderName,accountHolderName);
//            accountHolderName.startAnimation(shake);
//            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//        }else if (branchName.getText().toString().isEmpty()){
////            branchName.getParent().requestChildFocus(branchName,branchName);
//            branchName.startAnimation(shake);
//            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
//        }
        else if (password.getText().toString().isEmpty()) {
//            password.getParent().requestChildFocus(password,password);
            password.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (confirmPassword.getText().toString().isEmpty()) {
//            confirmPassword.getParent().requestChildFocus(confirmPassword,confirmPassword);
            confirmPassword.startAnimation(shake);
            Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else {
            if (!isTextValid(boothName.getText().toString()) && boothName.getText().toString().trim().equals("")) {
                boothName.getParent().requestChildFocus(boothName, boothName);
                boothName.startAnimation(shake);
//                boothName.setText("");
                Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.nameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!isUsernameValid(boothUsername.getText().toString())) {
                boothUsername.getParent().requestChildFocus(boothUsername, boothUsername);
                boothUsername.startAnimation(shake);
//                boothUsername.setText("");
                Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.usernameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                email.getParent().requestChildFocus(email, email);
                email.startAnimation(shake);
//                email.setText("");
                Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
            }
//            else if (boothPhone.getText().toString().length() <= 12){
//                boothPhone.getParent().requestChildFocus(boothPhone,boothPhone);
//                boothPhone.startAnimation(shake);
//                boothPhone.setText("");
//                Toast.makeText(RegisterBoothActivity.this, "Mobile Number is not valid", Toast.LENGTH_SHORT).show();
//            }
//            else if (!isTextValid(accountHolderName.getText().toString())){
//                accountHolderName.getParent().requestChildFocus(accountHolderName,accountHolderName);
//                accountHolderName.startAnimation(shake);
////                accountHolderName.setText("");
//                Toast.makeText(RegisterBoothActivity.this, "Account Holder Name is not valid", Toast.LENGTH_SHORT).show();
//            }
            else if (password.getText().toString().length() < 6) {
                password.getParent().requestChildFocus(password, password);
                password.startAnimation(shake);
//                password.setText("");
                if (!confirmPassword.getText().toString().isEmpty()) {
                    confirmPassword.getParent().requestChildFocus(confirmPassword, confirmPassword);
//                    confirmPassword.setText("");
                }
                Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.passwordmustcontainsixcharcters), Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                password.getParent().requestChildFocus(password, password);
                password.startAnimation(shake);
                confirmPassword.startAnimation(shake);
//                password.setText("");
//                confirmPassword.setText("");
                Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.passwordmismatch), Toast.LENGTH_SHORT).show();
            } else {
                if (!checkBox.isChecked()) {
                    Toast.makeText(RegisterBoothActivity.this,getResources().getString(R.string.readandagreetheterms), Toast.LENGTH_SHORT).show();
                } else {

//                    imageUploadingTask();
                    new UploadFileToServer().execute();
                    //  new ImageUploadTask1().execute(1 + "", "user.jpg");
                }
            }
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

//            progress = new Dialog(RegisterBoothActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//            progress.setContentView(R.layout.progress_dialog_circle);
//            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
//            tv = progress.findViewById(R.id.tv);
//
//            Resources res = getResources();
//            Drawable drawable = res.getDrawable(R.drawable.circular);
//            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
//            mProgress.setProgress(0);   // Main Progress
//            mProgress.setSecondaryProgress(100); // Secondary Progress
//            mProgress.setMax(100); // Maximum Progress
//            mProgress.setProgressDrawable(drawable);
//
//            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progress.setCanc
//            elable(false);
//            progress.show();
            CustomLoader.showDialog(RegisterBoothActivity.this);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value
//            mProgress.setProgress(progress[0]);

            // updating percentage value
//            tv.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.SIGNUP);

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
                        entity.addPart("BoothImage", new FileBody(sourceFile));
                    }
                }

                entity.addPart("RoleID", new StringBody("2"));
                entity.addPart("BoothName", new StringBody(boothName.getText().toString()));
                entity.addPart("BoothUserName", new StringBody(boothUsername.getText().toString()));
                entity.addPart("Email", new StringBody(email.getText().toString()));
                entity.addPart("Mobile", new StringBody(ccp_booth.getFullNumberWithPlus()));
                entity.addPart("Gender", new StringBody(""));
                entity.addPart("LastLangState", new StringBody(sharedpreferences.getString("language", "")));
                entity.addPart("CityID", new StringBody(CityID));
                if (boothType.getText().toString().equals("Physical")) {
                    entity.addPart("BoothAddress", new StringBody(address.getText().toString()));
                    entity.addPart("BoothLat", new StringBody(String.valueOf(latitude)));
                    entity.addPart("BoothLong", new StringBody(String.valueOf(longitude)));
                } else {
                    entity.addPart("BoothAddress", new StringBody(""));
                    entity.addPart("BoothLat", new StringBody(""));
                    entity.addPart("BoothLong", new StringBody(""));
                }
                entity.addPart("Verification", new StringBody(""));
                entity.addPart("PaymentAccountNumber", new StringBody(paymentAccount.getText().toString()));
                entity.addPart("PaymentAccountHolderName", new StringBody(accountHolderName.getText().toString()));
                entity.addPart("PaymentAccountBankBranch", new StringBody(branchName.getText().toString()));
                entity.addPart("Password", new StringBody(password.getText().toString()));
                entity.addPart("DeviceType", new StringBody("Android"));
                entity.addPart("DeviceToken", new StringBody(deviceToken));
                entity.addPart("LastState", new StringBody("booth"));
                entity.addPart("BoothType", new StringBody(boothType.getText().toString()));
                entity.addPart("OS", new StringBody(Build.VERSION.RELEASE));
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");

                if (status == 200) {
                    CustomLoader.dialog.dismiss();
                    String message = jsonObject.getString("message");
                    JSONObject user_info = jsonObject.getJSONObject("user_info");
//                           String About = user_info.getString("About");
                    String AuthToken = user_info.getString("AuthToken");
//                           String BoothAverageRating = user_info.getString("BoothAverageRating");
//                           String BoothCategoriesCount = user_info.getString("BoothCategoriesCount");
//                           String BoothCoverImage = user_info.getString("BoothCoverImage");
//                           String BoothFollowersCount = user_info.getString("BoothFollowersCount");
//                           String BoothImage = user_info.getString("BoothImage");
//                           String BoothName = user_info.getString("BoothName");
//                           String BoothType = user_info.getString("BoothType");
//                           String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
//                           String CompressedBoothImage = user_info.getString("CompressedBoothImage");
//                           String CompressedCoverImage = user_info.getString("CompressedCoverImage");
//                           String CompressedImage = user_info.getString("CompressedImage");
//                           String ContactDays = user_info.getString("ContactDays");
//                           String ContactNo = user_info.getString("ContactNo");
//                           String ContactTimeFrom = user_info.getString("ContactTimeFrom");
//                           String ContactTimeTo = user_info.getString("ContactTimeTo");
//                           String CoverImage = user_info.getString("CoverImage");
                    String DeviceToken = user_info.getString("DeviceToken");
//                           String Email = user_info.getString("Email");
//                           String FullName = user_info.getString("FullName");
//                           String Gender = user_info.getString("Gender");
//                           String HideContactNo = user_info.getString("HideContactNo");
//                           String Image = user_info.getString("Image");
//                           String IsEmailVerified = user_info.getString("IsEmailVerified");
//                           String IsMobileVerified = user_info.getString("IsMobileVerified");
//                           String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                    String LastState = user_info.getString("LastState");
//                           String Mobile = user_info.getString("Mobile");
//                           String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
//                           String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
//                           String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
//                           String UserCategoriesCount = user_info.getString("UserCategoriesCount");
//                           String UserFollowedBooths = user_info.getString("UserFollowedBooths");
//                           String UserFollowersCount = user_info.getString("UserFollowersCount");
                    String Currency = user_info.getString("Currency");
                    String CurrencySymbol = user_info.getString("CurrencySymbol");
                    String UserID = user_info.getString("UserID");
                    int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                    int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                    String IsEmailVerified = user_info.getString("IsEmailVerified");
                    String IsMobileVerified = user_info.getString("IsMobileVerified");

                    mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                    mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                    mEditor.putString("IsEmailVerified", IsEmailVerified);
                    mEditor.putString("IsMobileVerified", IsMobileVerified);
                    mEditor.putString("Currency", Currency);
                    mEditor.putString("CurrencySymbol", CurrencySymbol);

                    mEditor.putString("UserID", UserID);
                    mEditor.putString("ApiToken", AuthToken);
                    mEditor.putString("DeviceToken", DeviceToken);
                    mEditor.putString("LastState", LastState);
                    mEditor.apply();

                    Toast.makeText(RegisterBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                    mEditor.putString("AccountType", "booth").apply();
                    Intent intent = new Intent(RegisterBoothActivity.this, CategoriesListActivity.class);
                    intent.putExtra("Activity", "SignUp");
                    startActivity(intent);
                    finishAffinity();
                } else {
                    CustomLoader.dialog.dismiss();
                    String message = jsonObject.getString("message");
                    Toast.makeText(RegisterBoothActivity.this, message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(RegisterBoothActivity.this);
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

    private void imageUploadingTask() {
        CustomLoader.showDialog(RegisterBoothActivity.this);
        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("BoothName", boothName.getText().toString());
        params.put("BoothUserName", boothUsername.getText().toString());
        params.put("Email", email.getText().toString());
        params.put("Mobile", ccp_booth.getFullNumberWithPlus());
        params.put("Gender", " ");
        params.put("CityID", CityID);
        if (boothType.getText().toString().equals("Physical")) {
            params.put("BoothAddress", address.getText().toString());
            params.put("BoothLat", String.valueOf(latitude));
            params.put("BoothLong", String.valueOf(longitude));
        } else {
            params.put("BoothAddress", "");
            params.put("BoothLat", "");
            params.put("BoothLong", "");
        }
        params.put("Verification", " ");
        params.put("PaymentAccountNumber", paymentAccount.getText().toString());
        params.put("PaymentAccountHolderName", accountHolderName.getText().toString());
        params.put("PaymentAccountBankBranch", branchName.getText().toString());
        params.put("Password", password.getText().toString());
        params.put("DeviceType", "Android");
        params.put("DeviceToken", deviceToken);
        params.put("LastState", "booth");
        params.put("BoothType", boothType.getText().toString());
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));


        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SIGNUP, RegisterBoothActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("BoothResponse", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        CustomLoader.dialog.dismiss();
                        if (status == 200) {
                            String message = jsonObject.getString("message");
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
//                           String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
//                           String BoothAverageRating = user_info.getString("BoothAverageRating");
//                           String BoothCategoriesCount = user_info.getString("BoothCategoriesCount");
//                           String BoothCoverImage = user_info.getString("BoothCoverImage");
//                           String BoothFollowersCount = user_info.getString("BoothFollowersCount");
//                           String BoothImage = user_info.getString("BoothImage");
//                           String BoothName = user_info.getString("BoothName");
//                           String BoothType = user_info.getString("BoothType");
//                           String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
//                           String CompressedBoothImage = user_info.getString("CompressedBoothImage");
//                           String CompressedCoverImage = user_info.getString("CompressedCoverImage");
//                           String CompressedImage = user_info.getString("CompressedImage");
//                           String ContactDays = user_info.getString("ContactDays");
//                           String ContactNo = user_info.getString("ContactNo");
//                           String ContactTimeFrom = user_info.getString("ContactTimeFrom");
//                           String ContactTimeTo = user_info.getString("ContactTimeTo");
//                           String CoverImage = user_info.getString("CoverImage");
                            String DeviceToken = user_info.getString("DeviceToken");
//                           String Email = user_info.getString("Email");
//                           String FullName = user_info.getString("FullName");
//                           String Gender = user_info.getString("Gender");
//                           String HideContactNo = user_info.getString("HideContactNo");
//                           String Image = user_info.getString("Image");
//                           String IsEmailVerified = user_info.getString("IsEmailVerified");
//                           String IsMobileVerified = user_info.getString("IsMobileVerified");
//                           String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
//                           String Mobile = user_info.getString("Mobile");
//                           String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
//                           String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
//                           String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
//                           String UserCategoriesCount = user_info.getString("UserCategoriesCount");
//                           String UserFollowedBooths = user_info.getString("UserFollowedBooths");
//                           String UserFollowersCount = user_info.getString("UserFollowersCount");
                            String Currency = user_info.getString("Currency");
                            String CurrencySymbol = user_info.getString("CurrencySymbol");
                            String UserID = user_info.getString("UserID");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.putString("Currency", Currency);
                            mEditor.putString("CurrencySymbol", CurrencySymbol);

                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.apply();

                            Toast.makeText(RegisterBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                            mEditor.putString("AccountType", "booth").apply();
                            Intent intent = new Intent(RegisterBoothActivity.this, CategoriesListActivity.class);
                            intent.putExtra("Activity", "SignUp");
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(RegisterBoothActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("saveBookErrorResponse1", e.toString());
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();

                    }
                } else {
                    Toast.makeText(RegisterBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Image to string converter
    private Map<String, String> imgToStringFunction(Bitmap bitmap) {

        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
//                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Compressed
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos); // Compressed
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
//                byte[] data = bos.toByteArray(); // Compressed
                byte[] original_data = original_image.toByteArray(); // Original
                // images[0] = Base64.encodeToString(data, Base64.DEFAULT);
                // images[1] = Base64.encodeToString(original_data, Base64.DEFAULT);
                params.put("BoothImage", Base64.encodeToString(original_data, Base64.DEFAULT));
//                params.put("CompressedImage", Base64.encodeToString(data, Base64.DEFAULT));
            }
        }
        return params;
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

    @Override
    public void onClick(View view) {

        if (view == confirmBtn) {
            FinalStep();
        } else if (view == boothLogo) {
            Log.e("cameraClick", "yes");
            returnValue.clear();
            start(RegisterBoothActivity.this,                    //Activity or Fragment Instance
                    1234,                //Request code for activity results
                    1);    //Number of images to restict selection count
        } else if (view == registerAgreement) {
            TermsDialog();
        }

    }

    private void TermsDialog() {
        final Dialog dialog = new Dialog(RegisterBoothActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.terms_dialog);
        TextView alert_message = dialog.findViewById(R.id.alert_message);
        TextView title = dialog.findViewById(R.id.title);
        ProgressBar progressbar = dialog.findViewById(R.id.progressbar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        alert_message.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);


        apiCall(alert_message, title, progressbar);


    }

    private void apiCall(TextView msg, TextView title, ProgressBar progressBar) {

        Map<String, String> params = new HashMap<>();

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.about_usTerms+ "AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&PageID=3", RegisterBoothActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("TermsConditions", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject page_data = jsonObject.getJSONObject("page_data");

                            String Title = page_data.getString("Title");
                            String Description = page_data.getString("Description");

                            title.setText(Title);
                            msg.setText(Description);

                            title.setVisibility(View.VISIBLE);
                            msg.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RegisterBoothActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.e("getdata", "---->" + returnValue.size());
            Log.e("getdata", "---->" + resultCode);
            Log.e("getdata", "---->" + requestCode);
            Log.e("getdata", "---->" + UCrop.REQUEST_CROP);
            Log.e("getdata", "---->" + Activity.RESULT_OK);
            for (int a = 0; a < returnValue.size(); a++) {
                File imgFile = new File(returnValue.get(a));
//                Uri myUri = Uri.parse(returnValue.get(a));
                myUri = Uri.fromFile(new File(returnValue.get(a)));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(RegisterBoothActivity.this, 1234, 1);
                } else {
                    Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.approvepermissions), Toast.LENGTH_LONG).show();
                }
                return;
            }
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(RegisterBoothActivity.this, MapsActivity.class));
                } else {
                    Toast.makeText(RegisterBoothActivity.this, getResources().getString(R.string.theappwasnotallowedtoaccessyourlocation), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(RegisterBoothActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            boothLogo.setImageBitmap(null);
            boothLogo.setImageURI(UCrop.getOutput(result));
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

}
