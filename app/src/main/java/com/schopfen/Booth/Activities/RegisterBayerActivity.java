package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.hbb20.CountryCodePicker;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.DrawableClickListener;
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

public class RegisterBayerActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    Map<String, String> params = new HashMap<String, String>();
    CountryCodePicker ccp;

    EditText full_name, username, email, phone, password, confirmPassword;
    TextView registerAgreement;
    AutoCompleteTextView city, gender;
    Button btn_cancle, confirmSignup;
    RadioButton boothRadio, customerRadio;
    LinearLayout boothLinearLayout, customerLinearLayout;
    CircleImageView profPic;
    ImageView camera;
    LinearLayout parent_layout;
    CheckBox checkBox;
    String CityID = " ";

    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<String> gendersList = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();

    ArrayList<String> returnValue = new ArrayList<>();
    Uri myUri;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    Bitmap bitmap;
    MultipartEntity entity;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    RelativeLayout main_linearLayout;
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
            setContentView(R.layout.activity_register_bayer);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            Bundle bundle = getIntent().getExtras();
            int backgroundid = (int) bundle.get("backgroundkey");
            deviceToken = sharedpreferences.getString("DeviceToken", "");

            initializeViews();
            initializeClicks();
            getCities();
            Action();
            ccp.registerCarrierNumberEditText(phone);

            gendersList.add(getResources().getString(R.string.male));
            gendersList.add(getResources().getString(R.string.female));
            ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(RegisterBayerActivity.this,
                    android.R.layout.simple_list_item_1, gendersList);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
            gender.setAdapter(genderAdapter);

            gender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    gender.showDropDown();
                    hideKeyboard(gender);
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

            boothRadio.setChecked(true);
            customerLinearLayout.setVisibility(View.GONE);

            SpannableString str = new SpannableString(getResources().getString(R.string.register_agreement));
            str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 15, 35, 15);
            registerAgreement.setText(str);
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == confirmSignup) {
//            startActivity(new Intent(RegisterBayerActivity.this, MainCategoriesActivity.class));
            FinalStep();
        } else if (view == boothRadio) {
            boothRadioFunction();
        } else if (view == customerRadio) {
            customerRadioFunction();
        } else if (view == profPic) {
            Log.e("cameraClick", "yes");
            returnValue.clear();
            start(RegisterBayerActivity.this,                    //Activity or Fragment Instance
                    1234,                //Request code for activity results
                    1);    //Number of images to restict selection count
        } else if (view == registerAgreement) {
            TermsDialog();
        }
    }

    private void TermsDialog() {
        final Dialog dialog = new Dialog(RegisterBayerActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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

    private void apiCall(TextView msg, TextView title, ProgressBar progressBar){

        Map<String,String> params = new HashMap<>();

        HashMap<String,String> header= new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.about_usTerms + "AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE +"&PageID=3", RegisterBayerActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("TermsConditions", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){

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
                }else {
                    Toast.makeText(RegisterBayerActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //This function will be executed when user press customerRadio
    public void customerRadioFunction() {
        customerRadio.setChecked(true);
//        boothLinearLayout.setVisibility(View.GONE);
//        customerLinearLayout.setVisibility(View.VISIBLE);
        customerRadio.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        customerRadio.setTextColor(getResources().getColor(R.color.white));
        boothRadio.setBackgroundResource(0);
        boothRadio.setTextColor(getResources().getColor(R.color.font_color));
        boothRadio.setChecked(false);

        if (Build.VERSION.SDK_INT >= 21) {

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{

                            new int[]{android.R.attr.state_checked}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{

                            Color.WHITE, //disabled,
//                                    Color.WHITE //enabled
                            Color.rgb(243, 140, 13)

                    }
            );

            boothRadio.setButtonTintList(colorStateList);//set the color tint list
            customerRadio.setButtonTintList(colorStateList);
            boothRadio.invalidate(); //could not be necessary
        }
    }

    // This function will be executed when user press boothRadio
    public void boothRadioFunction() {
        boothRadio.setChecked(true);
//        customerLinearLayout.setVisibility(View.GONE);
//        boothLinearLayout.setVisibility(View.VISIBLE);
        boothRadio.setBackgroundResource(R.drawable.rounded_corners_orange_btn);
        boothRadio.setTextColor(getResources().getColor(R.color.white));
        customerRadio.setBackgroundResource(0);
        customerRadio.setTextColor(getResources().getColor(R.color.font_color));
        customerRadio.setChecked(false);

        if (Build.VERSION.SDK_INT >= 21) {

            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{

                            new int[]{android.R.attr.state_checked}, //disabled
                            new int[]{android.R.attr.state_enabled} //enabled
                    },
                    new int[]{

                            Color.WHITE, //disabled,
//                                     Color.WHITE //enabled
                            Color.rgb(243, 140, 13)

                    }
            );

            customerRadio.setButtonTintList(colorStateList);//set the color tint list
            boothRadio.setButtonTintList(colorStateList);
            customerRadio.invalidate(); //could not be necessary
        }
    }

    // This function is to fill city dropdown and is called in onCreate
    public void fillCityDropDown() {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item,
//                cities){
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View view =super.getView(position, convertView, parent);
//
//                TextView textView= view.findViewById(android.R.id.text1);
//
//                /*YOUR CHOICE OF COLOR*/
//                textView.setTextSize(12);
//                textView.setTextColor(Color.parseColor("#F38C0D"));
//
//                return view;
//            }
//        };
//        city.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

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

    private void FinalStep() {
        Animation shake = AnimationUtils.loadAnimation(RegisterBayerActivity.this, R.anim.shake);
        if (full_name.getText().toString().trim().length()==0) {
//            full_name.getParent().requestChildFocus(full_name,full_name);
            full_name.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (username.getText().toString().trim().length()==0) {
//            username.getParent().requestChildFocus(username,username);
            username.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (email.getText().toString().trim().length()==0) {
//            emailTextView.getParent().requestChildFocus(emailTextView,emailTextView);
            email.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
            //} else if (!phone.getText().toString().isEmpty() && phone.getText().toString().length() < 13) {
//            phone.getParent().requestChildFocus(phone,phone);
            //  phone.startAnimation(shake);
            // Toast.makeText(RegisterBayerActivity.this, "Mobile Number is not valid must be +XXXXXXXXXXXXX", Toast.LENGTH_SHORT).show();
        } else if (!ccp.isValidFullNumber()) {
            phone.getParent().requestChildFocus(phone, phone);
            phone.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.mobilenumberisnotvalid), Toast.LENGTH_SHORT).show();
        } else if (city.getText().toString().trim().length()==0) {
//            city.getParent().requestChildFocus(city,city);
            city.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (password.getText().toString().isEmpty()) {
//            password.getParent().requestChildFocus(password,password);
            password.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (confirmPassword.getText().toString().isEmpty()) {
//            confirmPassword.getParent().requestChildFocus(confirmPassword,confirmPassword);
            confirmPassword.startAnimation(shake);
            Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else {
            if (!isTextValid(full_name.getText().toString()) && full_name.getText().toString().trim().equals("")) {
//                full_name.getParent().requestChildFocus(full_name,full_name);
                full_name.startAnimation(shake);
//                full_name.setText("");
                Toast.makeText(RegisterBayerActivity.this, getResources().getString(R.string.nameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!isUsernameValid(username.getText().toString())) {
//                username.getParent().requestChildFocus(username,username);
                username.startAnimation(shake);
//                username.setText("");
                Toast.makeText(RegisterBayerActivity.this,getResources().getString(R.string.usernameisnotvalid), Toast.LENGTH_SHORT).show();
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
//                emailTextView.getParent().requestChildFocus(emailTextView,emailTextView);
                email.startAnimation(shake);
//                email.setText("");
                Toast.makeText(RegisterBayerActivity.this,getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
            }
//            else if (phone.getText().toString().length() <= 12){
//                phone.getParent().requestChildFocus(phone,phone);
//                phone.startAnimation(shake);
//                phone.setText("");
//                Toast.makeText(RegisterBayerActivity.this, "Mobile Number is not valid", Toast.LENGTH_SHORT).show();
//            }
            else if (password.getText().toString().length() < 6) {
//                password.getParent().requestChildFocus(password,password);
                password.startAnimation(shake);
//                password.setText("");

                if (!confirmPassword.getText().toString().isEmpty()) {
//                    confirmPassword.getParent().requestChildFocus(confirmPassword,confirmPassword);
//                    confirmPassword.setText("");
                }
                Toast.makeText(RegisterBayerActivity.this, this.getResources().getString(R.string.passwordmustcontainsixcharcters), Toast.LENGTH_SHORT).show();
            } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
//                password.getParent().requestChildFocus(password,password);
                password.startAnimation(shake);
                confirmPassword.startAnimation(shake);
//                password.setText("");
//                confirmPassword.setText("");
                Toast.makeText(RegisterBayerActivity.this, this.getResources().getString(R.string.passwordmismatch), Toast.LENGTH_SHORT).show();
            } else {
                if (!checkBox.isChecked()) {
                    Toast.makeText(RegisterBayerActivity.this, this.getResources().getString(R.string.readandagreetheterms), Toast.LENGTH_SHORT).show();
                } else {
//                    CustomLoader.showDialog(RegisterBayerActivity.this);
                    // Log.e("Mobile", ccp.getFullNumberWithPlus());
//                    imageUploadingTask();
                    new UploadFileToServer().execute();
                    // new ImageUploadTask1().execute(1 + "", "user.jpg");
                }
            }
        }
    }

    /*class ImageUploadTask1 extends AsyncTask<String, Void, String> {

        String sResponse = null;
        String responseBody = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String url = Constants.URL.SIGNUP;
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpPost httpPost = new HttpPost(url);
                Log.e("URL", " " + url);
                httpPost.addHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));
                entity = new MultipartEntity();
                entity.addPart("RoleID", new StringBody("2"));
                entity.addPart("FullName", new StringBody(full_name.getText().toString()));
                entity.addPart("UserName", new StringBody(username.getText().toString()));
                entity.addPart("Email", new StringBody(emailTextView.getText().toString()));
                entity.addPart("Mobile", new StringBody(phone.getText().toString()));
                entity.addPart("Gender", new StringBody(gender.getText().toString()));
                entity.addPart("CityID", new StringBody(CityID));
                entity.addPart("Password", new StringBody(password.getText().toString()));
                entity.addPart("DeviceType", new StringBody("Android"));
                entity.addPart("DeviceToken", new StringBody(sharedpreferences.getString("DeviceToken", " ")));
                entity.addPart("LastState", new StringBody("buyer"));
                entity.addPart("OS", new StringBody(Build.VERSION.RELEASE));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));
                if (bitmapArrayList.size() > 0) {
                    Log.e("sizee", bitmapArrayList.size() + "");
                    for (int j = 0; j < bitmapArrayList.size(); j++) {
                        Bitmap bitmap = bitmapArrayList.get(j);
                        Log.e("bitmap", bitmap + " ");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Compressed
                        ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos); // Compressed
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, original_image); // Original
                        byte[] data = bos.toByteArray(); // Compressed
                        byte[] original_data = original_image.toByteArray(); // Original
                        entity.addPart("CompressedImage", new ByteArrayBody(data,
                                "Image/jpeg", params[1])); // Compressed
                        entity.addPart("Image", new ByteArrayBody(original_data,
                                "Image/jpeg", params[1])); // for Normal Image
                    }
                }
                httpPost.setEntity(entity);
                HttpResponse response = httpClient.execute(httpPost,
                        localContext);
                sResponse = EntityUtils.getContentCharSet(response.getEntity());
                responseBody = EntityUtils.toString(response.getEntity());
                Log.e("saveBookingResponse1", responseBody);

            } catch (Exception e) {
                CustomLoader.dialog.dismiss();
                Log.e("entity", e.toString());
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(String sResponsee) {

            Log.e("URL", responseBody);
            if (responseBody != null) {
                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");

                    if (status == 200) {

                        CustomLoader.dialog.dismiss();

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
                        String DeviceToken = user_info.getString("DeviceToken");
                        String Notification = user_info.getString("Notification");
                        String AuthToken = user_info.getString("AuthToken");
                        int CategoriesCount = user_info.getInt("CategoriesCount");

                        mEditor.putString("UserID", UserID);
                        mEditor.putString("ApiToken", AuthToken);
                        mEditor.putString("DeviceToken", DeviceToken);
                        mEditor.commit();

                        Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RegisterBayerActivity.this, CategoriesListActivity.class));
                        finish();

                    }else {
                        CustomLoader.dialog.dismiss();
                        Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    CustomLoader.dialog.dismiss();
                    Log.e("saveBookErrorResponse1", e.toString());
                    e.printStackTrace();
                }
            }
        }
    }*/

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
            CustomLoader.showDialog(RegisterBayerActivity.this);
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
                        entity.addPart("Image", new FileBody(sourceFile));
                    }
                }

                entity.addPart("RoleID", new com.schopfen.Booth.ApiStructure.StringBody("2"));
                entity.addPart("FullName", new com.schopfen.Booth.ApiStructure.StringBody(full_name.getText().toString()));
                entity.addPart("UserName", new com.schopfen.Booth.ApiStructure.StringBody(username.getText().toString()));
                entity.addPart("Email", new com.schopfen.Booth.ApiStructure.StringBody(email.getText().toString()));
                entity.addPart("Mobile", new com.schopfen.Booth.ApiStructure.StringBody(ccp.getFullNumberWithPlus()));
                entity.addPart("LastLangState", new StringBody(sharedpreferences.getString("language", "")));
                String genderFinal = "";
                if (gender.getText().toString().equals(gendersList.get(0))){
                    genderFinal = "Male";
                }else {
                    genderFinal = "Female";
                }
                entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                entity.addPart("CityID", new com.schopfen.Booth.ApiStructure.StringBody(CityID));
                entity.addPart("Password", new com.schopfen.Booth.ApiStructure.StringBody(password.getText().toString()));
                entity.addPart("DeviceType", new com.schopfen.Booth.ApiStructure.StringBody("Android"));
                entity.addPart("DeviceToken", new com.schopfen.Booth.ApiStructure.StringBody(deviceToken));
                entity.addPart("LastState", new com.schopfen.Booth.ApiStructure.StringBody("user"));
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

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");

                if (status == 200) {
                    CustomLoader.dialog.dismiss();
                    JSONObject user_info = jsonObject.getJSONObject("user_info");
                    String UserID = user_info.getString("UserID");
//                            String RoleID = user_info.getString("RoleID");
//                            String UserName = user_info.getString("UserName");
//                            String Email = user_info.getString("Email");
//                            String Mobile = user_info.getString("Mobile");
//                            String Gender = user_info.getString("Gender");
//                            String CityID = user_info.getString("CityID");
//                            String CompressedImage = user_info.getString("CompressedImage");
//                            String Image = user_info.getString("Image");
                    String LastState = user_info.getString("LastState");
//                            String BoothType = user_info.getString("BoothType");
//                            String IsEmailVerified = user_info.getString("IsEmailVerified");
//                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                    String DeviceToken = user_info.getString("DeviceToken");
                    String Currency = user_info.getString("Currency");
                    String CurrencySymbol = user_info.getString("CurrencySymbol");
//                            String Notification = user_info.getString("Notification");
                    String AuthToken = user_info.getString("AuthToken");
                    int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
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

                    Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
                    mEditor.putString("AccountType", "user").apply();
                    Intent intent = new Intent(RegisterBayerActivity.this, CategoriesListActivity.class);
                    intent.putExtra("Activity", "SignUp");
                    startActivity(intent);
                    finishAffinity();
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(RegisterBayerActivity.this);
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

        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("FullName", full_name.getText().toString());
        params.put("UserName", username.getText().toString());
        params.put("Email", email.getText().toString());
        // params.put("Mobile", phone.getText().toString());
        params.put("Mobile", ccp.getFullNumberWithPlus());
        params.put("Gender", gender.getText().toString());
        params.put("CityID", CityID);
        params.put("Password", password.getText().toString());
        params.put("DeviceType", "Android");
        params.put("DeviceToken", deviceToken);
        params.put("LastState", "user");
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));


        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SIGNUP, RegisterBayerActivity.this, params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("BuyerResponse", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {

                            CustomLoader.dialog.dismiss();

                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
//                            String RoleID = user_info.getString("RoleID");
//                            String UserName = user_info.getString("UserName");
//                            String Email = user_info.getString("Email");
//                            String Mobile = user_info.getString("Mobile");
//                            String Gender = user_info.getString("Gender");
//                            String CityID = user_info.getString("CityID");
//                            String CompressedImage = user_info.getString("CompressedImage");
//                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
//                            String BoothType = user_info.getString("BoothType");
//                            String IsEmailVerified = user_info.getString("IsEmailVerified");
//                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Currency = user_info.getString("Currency");
                            String CurrencySymbol = user_info.getString("CurrencySymbol");
//                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
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

                            Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
                            mEditor.putString("AccountType", "user").apply();
                            Intent intent = new Intent(RegisterBayerActivity.this, CategoriesListActivity.class);
                            intent.putExtra("Activity", "SignUp");
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(RegisterBayerActivity.this, message, Toast.LENGTH_LONG).show();
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
                params.put("Image", Base64.encodeToString(original_data, Base64.DEFAULT));
//                params.put("CompressedImage", Base64.encodeToString(data, Base64.DEFAULT));
            }
        }
        return params;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(RegisterBayerActivity.this, 1234, 1);
                } else {
                    Toast.makeText(RegisterBayerActivity.this, "Approve permissions to open Camera", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(RegisterBayerActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profPic.setImageBitmap(null);
            profPic.setImageURI(UCrop.getOutput(result));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
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

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, RegisterBayerActivity.this, body, headers, new ServerCallback() {
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

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterBayerActivity.this,
                                    android.R.layout.simple_list_item_1, citiesNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                            city.setAdapter(adapter);


                        } else {
                            Toast.makeText(RegisterBayerActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(RegisterBayerActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void Action() {
        parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
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

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) RegisterBayerActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    void initializeViews() {
        full_name = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        city = findViewById(R.id.city);
        gender = findViewById(R.id.gender);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        confirmSignup = findViewById(R.id.confirmSignUp);
        boothRadio = findViewById(R.id.boothRadio);
        customerRadio = findViewById(R.id.customerRadio);
        boothLinearLayout = findViewById(R.id.boothLinear);
        customerLinearLayout = findViewById(R.id.customerLinear);
        registerAgreement = findViewById(R.id.register_agreement);
        profPic = findViewById(R.id.profPic);
        camera = findViewById(R.id.camera);
        parent_layout = findViewById(R.id.mainLayout);
        checkBox = findViewById(R.id.checkBox);
        main_linearLayout = findViewById(R.id.parent_layout);
        ccp = findViewById(R.id.ccpicker);

        email.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }

    void initializeClicks() {
        city.setOnClickListener(this);
        confirmSignup.setOnClickListener(this);
        boothRadio.setOnClickListener(this);
        customerRadio.setOnClickListener(this);
        profPic.setOnClickListener(this);
        registerAgreement.setOnClickListener(this);
    }
}
