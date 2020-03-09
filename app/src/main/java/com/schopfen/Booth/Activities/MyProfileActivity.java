package com.schopfen.Booth.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.schopfen.Booth.Adapters.WishlistAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.Models.WishListEtcData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.fxn.pix.Pix.start;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    private String mParam1;
    private String mParam2;
    ImageView cam;
    Button createBtn, cancelBtn;
    Uri myUri;
    Bitmap bitmap;
    Dialog current_dialog;
    ImageView gifimageView,dialogGif;
    BottomNavigationView bottomNavigationView;
    int codeRequest = 1234;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<String> myreturnValue = new ArrayList<>();
    ArrayList<UserDetailsData> userDetailsData = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<String> genderArray = new ArrayList<>();
    Map<String, String> params = new HashMap<String, String>();

    TextView username, address, fullName, rating_count;
    CircleImageView pro_image;
    RecyclerView wishlistRecycler, purchasesRecycler, likesRecycler;
    ArrayList<String> wishListArray = new ArrayList<>();
    LinearLayout viewFollowers, viewBooths;
    AutoCompleteTextView editCity, editGender;
    TextView editUsername, editEmail, editIsVerify;
    EditText editFullname, editPhone;
    CircleImageView dialogpro;
    LinearLayout wishlist_linear,likes_linear, purchases_linear;
    TextView wishlist_count, likes_count, purchases_count;

    ArrayList<WishListEtcData> likesData;
    ArrayList<ProductImagesData> likesImagesData;
    ArrayList<WishListEtcData> wishListData;
    ArrayList<ProductImagesData> wishListImagesData;
    ArrayList<WishListEtcData> purchasesData;
    ArrayList<ProductImagesData> purchaseImagesData;

    String CityID;

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
            setContentView(R.layout.activity_my_profile);

            genderArray.add(getResources().getString(R.string.male));
            genderArray.add(getResources().getString(R.string.female));

            pro_image = findViewById(R.id.myprofile_civ);
            wishlistRecycler = findViewById(R.id.wishlistRecycler);
            purchasesRecycler = findViewById(R.id.purchasesRecycler);
            likesRecycler = findViewById(R.id.likesRecycler);
            fullName = findViewById(R.id.fullName);
            rating_count = findViewById(R.id.rating_count);
            address = findViewById(R.id.address);
            username = findViewById(R.id.userName);
            viewFollowers = findViewById(R.id.viewFollowers);
            gifimageView = findViewById(R.id.gifmain);
            viewBooths = findViewById(R.id.viewBooths);
            wishlist_linear = findViewById(R.id.wishlist_linear);
            likes_linear = findViewById(R.id.likes_linear);
            purchases_linear = findViewById(R.id.purchases_linear);
            wishlist_count = findViewById(R.id.wishlist_count);
            likes_count = findViewById(R.id.likes_count);
            purchases_count = findViewById(R.id.purchases_count);

            initializeEditProfileDialog();

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            initiateListeners();
            getUserDetails();
            getCities();
            getUserOrdersLikesWishlistApiCall();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    @Override
    public void onClick(View v){
        if(v == viewFollowers){
            mEditor.putString("typetype", "user").commit();
            mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).commit();
            startActivity(new Intent(MyProfileActivity.this, FollowersNewActivity.class));
        }else if (v == viewBooths){
            mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).commit();
            startActivity(new Intent(MyProfileActivity.this, FollowingsActivity.class));
        }
    }

    private void editProfileFunction() {

        initializeEditProfileDialog();

        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        CityID = userDetailsData.get(0).getCityID();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyProfileActivity.this,
                android.R.layout.simple_list_item_1, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editCity.setAdapter(adapter);
        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
                editCity.showDropDown();
                hideKeyboard(editCity);
            }
        });
        editCity.setTextIsSelectable(true);
        editCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CityID = citiesData.get(position).getCityID();
//                    mEditor.putString("CityID", CityID).commit();
//                    mEditor.putString("City", citiesDataArrayList.get(position).getTitle()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(MyProfileActivity.this,
                android.R.layout.simple_list_item_1, genderArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editGender.setAdapter(genderAdapter);

        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(editGender);
                editGender.showDropDown();
            }
        });
        editGender.setTextIsSelectable(true);
        editGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        current_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        current_dialog.show();

        dialogGif.setVisibility(View.GONE);
        cam.setVisibility(View.VISIBLE);
        dialogpro.setVisibility(View.VISIBLE);
        Picasso.get().load(Constants.URL.IMG_URL + userDetailsData.get(0).getCompressedImage()).placeholder(R.drawable.user).into(dialogpro);
        editCity.setHint(userDetailsData.get(0).getCityTitle());
        editFullname.setText(userDetailsData.get(0).getFullName());
        editGender.setHint(userDetailsData.get(0).getGender());
        editPhone.setText(userDetailsData.get(0).getMobile());
        editUsername.setText(userDetailsData.get(0).getUserName());
        editEmail.setText(userDetailsData.get(0).getEmail());

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("cameraClick", "yes");
                myreturnValue.clear();
                start(MyProfileActivity.this,                    //Activity or Fragment Instance
                        codeRequest,                //Request code for activity results
                        1);    //Number of images to restict selection count

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUploadFunction();
            }
        });

    }

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) MyProfileActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) MyProfileActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, MyProfileActivity.this, body, headers, new ServerCallback() {
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
                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MyProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void initiateListeners() {
        viewFollowers.setOnClickListener(this);
        viewBooths.setOnClickListener(this);
    }

    private void getUserDetails() {

//        CustomLoader.showDialog(getActivity());
//        bottomNavigationView.setClickable(false);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, MyProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
//                            CustomLoader.dialog.dismiss();
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
                            String BoothUserName = user_info.getString("BoothUserName");
                            float UserAverageRating = user_info.getInt("UserAverageRating");
                            rating_count.setText(String.valueOf(UserAverageRating));

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.apply();

                            pro_image.setVisibility(View.VISIBLE);
                            Picasso.get().load(Constants.URL.IMG_URL + Image).placeholder(R.drawable.user).into(pro_image);
                            gifimageView.setVisibility(View.GONE);
                            fullName.setText(FullName);
                            username.setText("@" + UserName);
                            address.setText(CityTitle);

                            userDetailsData.add(new UserDetailsData(About, AuthToken, BoothAverageRating, BoothCategoriesCount, BoothCoverImage, BoothFollowersCount, BoothImage, BoothName,
                                    BoothType, CityID, CityTitle, CompressedBoothCoverImage, CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays,
                                    ContactTimeFrom, ContactTimeTo, CoverImage, DeviceToken, Email, FullName, Gender, HideContactNo, Image, IsEmailVerified, IsMobileVerified,
                                    IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch, PaymentAccountHolderName, PaymentAccountNumber, UserCategoriesCount, UserFollowedBooths,
                                    UserFollowersCount, UserID, UserName, BoothUserName));

                            dialogGif.setVisibility(View.GONE);
                            cam.setVisibility(View.VISIBLE);
                            dialogpro.setVisibility(View.VISIBLE);
                            Picasso.get().load(Constants.URL.IMG_URL + userDetailsData.get(0).getCompressedImage()).placeholder(R.drawable.user).into(dialogpro);
                            editCity.setHint(userDetailsData.get(0).getCityTitle());
                            editFullname.setText(userDetailsData.get(0).getFullName());
                            editGender.setHint(userDetailsData.get(0).getGender());
                            editPhone.setText(userDetailsData.get(0).getMobile());
                            editUsername.setText(userDetailsData.get(0).getUserName());
                            editEmail.setText(userDetailsData.get(0).getEmail());

                            if (userDetailsData.get(0).getMobile().isEmpty()) {
                                editIsVerify.setText("");
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("0")) {
                                editIsVerify.setText("Verify");
                            } else if (userDetailsData.get(0).getIsMobileVerified().equals("1")) {
                                editIsVerify.setText("Verified");
                            }

                        } else {
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                                finish();
                            }                        }
                    } catch (JSONException e) {
                        bottomNavigationView.setClickable(true);
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    bottomNavigationView.setClickable(true);
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeEditProfileDialog() {
        current_dialog = new Dialog(MyProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        current_dialog.setContentView(R.layout.edit_profile_dialog_sa);

        cancelBtn = current_dialog.findViewById(R.id.btn_cancel_sa);
        createBtn = current_dialog.findViewById(R.id.btn_update_sa);
        cam = current_dialog.findViewById(R.id.camera_dialog_sa);
        dialogpro = current_dialog.findViewById(R.id.myprofilepic);
        editFullname = current_dialog.findViewById(R.id.fullname);
        editUsername = current_dialog.findViewById(R.id.userName);
        editPhone = current_dialog.findViewById(R.id.phone);
        editEmail = current_dialog.findViewById(R.id.email);
        editCity = current_dialog.findViewById(R.id.city);
        editGender = current_dialog.findViewById(R.id.gender);
        editIsVerify = current_dialog.findViewById(R.id.isVerify);
        dialogGif = current_dialog.findViewById(R.id.dialog_profile_gif);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == codeRequest) {
            Log.e("check", String.valueOf(requestCode));
            myreturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.e("check", String.valueOf(myreturnValue));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(MyProfileActivity.this, 1234, 1);
                } else {
                    Toast.makeText(MyProfileActivity.this, "Approve permissions to open Camera", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void beginCrop(Uri source) {
        Log.e("check", "result ok");
        Uri destination = Uri.fromFile(new File(MyProfileActivity.this.getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(MyProfileActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            dialogpro.setImageBitmap(null);
            dialogpro.setImageURI(UCrop.getOutput(result));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(MyProfileActivity.this.getContentResolver(), UCrop.getOutput(result));
                bitmapArrayList.add(bitmap);
                Log.e("chekingBit", bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(MyProfileActivity.this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void imageUploadFunction() {

        CustomLoader.showDialog(MyProfileActivity.this);

        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("FullName", editFullname.getText().toString());
        params.put("UserName", editUsername.getText().toString());
        params.put("Email", editEmail.getText().toString());
        params.put("Mobile", editPhone.getText().toString());
        if (editGender.getText().toString().isEmpty()){
            Log.e("Gender", "if");
            params.put("Gender",userDetailsData.get(0).getGender());
        }else {
            Log.e("Gender", "else");
            params.put("Gender", editGender.getText().toString());
        }
        params.put("CityID", CityID);
        params.put("DeviceType", "Android");
        params.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, MyProfileActivity.this, params, header, new ServerCallback() {
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
                            String CityTitle = user_info.getString("CityTitle");

                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.commit();

                            //  nameUsername.setText(FullName + " | " + "@" + UserName);

                            dialogGif.setVisibility(View.GONE);
                            cam.setVisibility(View.VISIBLE);
                            dialogpro.setVisibility(View.VISIBLE);
                            Picasso.get().load(Constants.URL.IMG_URL + CompressedImage).placeholder(R.drawable.user).into(dialogpro);
                            fullName.setText(FullName);
                            address.setText(CityTitle);
                            pro_image.setVisibility(View.VISIBLE);
                            Glide.with(MyProfileActivity.this).load(Constants.URL.IMG_URL + Image).apply(RequestOptions.centerCropTransform().placeholder(R.drawable.user)).into(pro_image);

                            CustomLoader.dialog.dismiss();
                            current_dialog.dismiss();

                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            current_dialog.dismiss();
                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        current_dialog.dismiss();
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
//                params.put("CompressedImage", Base64.encodeToString(original_data, Base64.DEFAULT));
            }
        }
        return params;
    }

    private void getUserOrdersLikesWishlistApiCall(){

//        CustomLoader.showDialog(getActivity());
        Map<String,String> body = new HashMap<String, String>();

        HashMap<String,String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.WishListLikesETC + sharedpreferences.getString("UserID", " ")+ "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, MyProfileActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("WishListEtcResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            likesData = new ArrayList<>();
                            JSONArray likesArray = jsonObject.getJSONArray("liked_products");
                            for(int i=0; i<likesArray.length(); i++){
                                JSONObject likesObject = likesArray.getJSONObject(i);
                                String BoothID = likesObject.getString("BoothID");
                                String BoothName = likesObject.getString("BoothName");
                                String ProductID = likesObject.getString("ProductID");
                                String ProductLikeID = likesObject.getString("ProductLikeID");
                                String Title = likesObject.getString("Title");
                                String UserID = likesObject.getString("UserID");

                                likesImagesData = new ArrayList<>();
                                JSONArray imagesArray = likesObject.getJSONArray("ProductImages");
                                for(int j=0; j<imagesArray.length(); j++){
                                    JSONObject imagesObject = imagesArray.getJSONObject(j);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    likesImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                likesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, likesImagesData));
                            }

                            RecyclerView.LayoutManager likesLayoutManager = new LinearLayoutManager(MyProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            likesRecycler.setLayoutManager(likesLayoutManager);
                            WishlistAdapter likesAdapter = new WishlistAdapter(MyProfileActivity.this, likesData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", likesData.get(position).getProductID()).commit();
                                    startActivity(new Intent(MyProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });
                            if (likesData.isEmpty()){
                                likes_linear.setVisibility(View.GONE);
                            }else {
                                likes_count.setText("(" + String.valueOf(likesData.size()) + " " + "Items" + ")");
                                likesRecycler.setAdapter(likesAdapter);
                                likesAdapter.notifyDataSetChanged();
                            }

                            wishListData = new ArrayList<>();
                            JSONArray wishListArray = jsonObject.getJSONArray("wishlist_products");
                            for(int k=0; k<wishListArray.length(); k++){
                                JSONObject wishListObject = wishListArray.getJSONObject(k);
                                String BoothID = wishListObject.getString("BoothID");
                                String BoothName = wishListObject.getString("BoothName");
                                String ProductID = wishListObject.getString("ProductID");
                                String ProductLikeID = wishListObject.getString("UserWishlistID");
                                String Title = wishListObject.getString("Title");
                                String UserID = wishListObject.getString("UserID");

                                wishListImagesData = new ArrayList<>();
                                JSONArray imagesArray = wishListObject.getJSONArray("ProductImages");
                                for(int l=0; l<imagesArray.length(); l++){
                                    JSONObject imagesObject = imagesArray.getJSONObject(l);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    wishListImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                wishListData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, wishListImagesData));
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            wishlistRecycler.setLayoutManager(layoutManager);
                            WishlistAdapter wishlistAdapter = new WishlistAdapter(MyProfileActivity.this, wishListData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", wishListData.get(position).getProductID()).commit();
                                    startActivity(new Intent(MyProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });
                            if (wishListData.isEmpty()){
                                wishlist_linear.setVisibility(View.GONE);
                            }else {
                                wishlist_count.setText("(" + String.valueOf(wishListData.size()) + " " + "Items" + ")");
                                wishlistRecycler.setAdapter(wishlistAdapter);
                                wishlistAdapter.notifyDataSetChanged();
                            }

                            purchasesData = new ArrayList<>();
                            JSONArray orders = jsonObject.getJSONArray("orders");
                            for(int p = 0; p < orders.length(); p++){
                                JSONObject ordersObject = orders.getJSONObject(p);
                                String BoothID = ordersObject.getString("BoothID");
                                String BoothName = ordersObject.getString("BoothName");
                                String ProductLikeID = ordersObject.getString("OrderRequestID");
                                String UserID = ordersObject.getString("UserID");

                                String ProductID = "";
                                String ProductTitle = "";
                                purchaseImagesData = new ArrayList<>();
                                JSONArray OrderItems = ordersObject.getJSONArray("OrderItems");
                                for(int l=0; l < 1; l++){
                                    JSONObject orderObject = OrderItems.getJSONObject(l);
                                    String ProductCompressedImage = orderObject.getString("ProductImage");
                                    String ProductImage = orderObject.getString("ProductImage");
                                    ProductID = orderObject.getString("ProductID");
                                    ProductTitle = orderObject.getString("ProductTitle");

                                    purchaseImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                                }
                                purchasesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, ProductTitle, UserID, purchaseImagesData));

                            }

                            RecyclerView.LayoutManager layoutManagerpurchases = new LinearLayoutManager(MyProfileActivity.this, RecyclerView.HORIZONTAL, false);
                            purchasesRecycler.setLayoutManager(layoutManagerpurchases);
                            WishlistAdapter purchasesAdapter = new WishlistAdapter(MyProfileActivity.this, purchasesData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    mEditor.putString("ProductID", purchasesData.get(position).getProductID()).commit();
                                    startActivity(new Intent(MyProfileActivity.this, ProductDetailsActivity.class));
                                }
                            });

                            if (purchasesData.isEmpty()){
                                purchases_linear.setVisibility(View.GONE);
                            }else {
                                purchases_count.setText("(" + String.valueOf(purchasesData.size()) + " " + "Items" + ")");
                                purchasesRecycler.setAdapter(purchasesAdapter);
                                purchasesAdapter.notifyDataSetChanged();
                            }

                        } else {
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(MyProfileActivity.this, message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(MyProfileActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(MyProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
