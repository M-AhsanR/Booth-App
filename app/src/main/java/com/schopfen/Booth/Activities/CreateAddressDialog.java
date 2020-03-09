package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.utility.PermUtil;
import com.schopfen.Booth.Adapters.CheckOutForAddressRVAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.AddressData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fxn.pix.Pix.start;

public class CreateAddressDialog extends AppCompatActivity {

    Button cancelBtn;
    Button createBtn;
    EditText addressTitle;
    EditText recipeintName;
    EditText email;
    EditText phone;
    EditText address, address2;
    EditText city;
    EditText buildingApartmentNo;
    Animation shake;
    double latitude = 0.0, longitude = 0.0;
    private static final int REQUEST = 112;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

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
            //android O fix bug orientation
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            setContentView(R.layout.add_address_dialog_coa);
            setTitle("");
            this.setFinishOnTouchOutside(false);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Initialization();
            getUserDetails();
            Actions();
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
    protected void onResume() {
        super.onResume();
        if (address != null) {
            address.setText(MapsActivity.AddressLine);
            city.setText(MapsActivity.cityLine);
            latitude = MapsActivity.lat;
            longitude = MapsActivity.lng;
            MapsActivity.AddressLine = "";
            MapsActivity.cityLine = "";
            MapsActivity.lat = 0.0;
            MapsActivity.lng = 0.0;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(new Intent(CreateAddressDialog.this, MapsActivity.class));
                } else {
                    Toast.makeText(CreateAddressDialog.this, getResources().getString(R.string.campermissionisnotallowed), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void getUserDetails() {

        CustomLoader.showDialog(CreateAddressDialog.this);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, CreateAddressDialog.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");

                            email.setText(Email);
                            recipeintName.setText(FullName);
                            phone.setText(Mobile);

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(CreateAddressDialog.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(CreateAddressDialog.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void Actions() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressTitle.getText().toString().trim().length() == 0) {
                    addressTitle.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (recipeintName.getText().toString().trim().length() == 0) {
                    recipeintName.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().trim().length() == 0) {
                    email.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.fillEmptyFields) , Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().trim().length() == 0) {
                    phone.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().trim().length() == 0) {
                    address.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (city.getText().toString().trim().length() == 0) {
                    city.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (buildingApartmentNo.getText().toString().trim().length() == 0) {
                    buildingApartmentNo.startAnimation(shake);
                    Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
                        email.startAnimation(shake);
//                        email.setText("");
                        Toast.makeText(CreateAddressDialog.this,getResources().getString(R.string.emailisnotvslid), Toast.LENGTH_SHORT).show();
                    } else {
                        AddAdressApi(address2.getText().toString(), String.valueOf(latitude), String.valueOf(longitude), addressTitle.getText().toString(), recipeintName.getText().toString(), email.getText().toString(), phone.getText().toString(), address.getText().toString(), city.getText().toString(), buildingApartmentNo.getText().toString());
                    }
                }
            }
        });
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
                    if (!hasPermissions(CreateAddressDialog.this, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) CreateAddressDialog.this, PERMISSIONS, REQUEST);
                    } else {
                        startActivity(new Intent(CreateAddressDialog.this, MapsActivity.class));
                    }
                } else {
                    startActivity(new Intent(CreateAddressDialog.this, MapsActivity.class));
                }
            }
        });
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

    private void AddAdressApi(String address2, String latitude, String longitude, String address_title, String recep_name, String recep_email, String recep_mobnum, String recep_address, String recep_city, String recep_buildingnum) {

        CustomLoader.showDialog(CreateAddressDialog.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AddressTitle", address_title);
        body.put("RecipientName", recep_name);
        body.put("Email", recep_email);
        body.put("Mobile", recep_mobnum);
        body.put("Address1", recep_address);
        body.put("Address2", address2);
        body.put("City", recep_city);
        body.put("ApartmentNo", recep_buildingnum);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("Latitude", latitude);
        body.put("Longitude", longitude);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addAddressUrl, CreateAddressDialog.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("CreateAddressErrorResp", "" + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");

                        if (status == 200) {
                            JSONObject address_info = jsonObject.getJSONObject("address_info");
                            String Address1 = address_info.getString("Address1");
                            String Address2 = address_info.getString("Address2");
                            String AddressID = address_info.getString("AddressID");
                            String AddressTitle = address_info.getString("AddressTitle");
                            String ApartementNo = address_info.getString("ApartmentNo");
                            String City = address_info.getString("City");
                            String Email = address_info.getString("Email");
                            String Gender = address_info.getString("Gender");
                            String IsDefault = address_info.getString("IsDefault");
                            String Mobile = address_info.getString("Mobile");
                            String RecipientName = address_info.getString("RecipientName");
                            String UserID = address_info.getString("UserID");

                            CheckOutActivity.addressData.add(new AddressData(Address1, Address2, AddressID, AddressTitle, ApartementNo, City, Email, Gender, IsDefault,
                                    Mobile, RecipientName, UserID));

//                            final Dialog subdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//                            subdialog.setContentView(R.layout.added_address_dialog);
//                            Button closeBtn = subdialog.findViewById(R.id.btn_close);
//                            subdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            subdialog.show();
//                            closeBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    subdialog.dismiss();
//                                }
//                            });

                            CustomLoader.dialog.dismiss();

                            finish();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(CreateAddressDialog.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void Initialization() {
        cancelBtn = findViewById(R.id.btn_cancel);
        createBtn = findViewById(R.id.btn_create);
        addressTitle = findViewById(R.id.addressTitle);
        recipeintName = findViewById(R.id.recipientName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        address2 = findViewById(R.id.address2);
        city = findViewById(R.id.city);
        buildingApartmentNo = findViewById(R.id.buildingNo);
        shake = AnimationUtils.loadAnimation(CreateAddressDialog.this, R.anim.shake);
    }
}
