package com.schopfen.Booth.Activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ContactBoothActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    TextView contactTime, contactDays, email, phoneNumber;
    LinearLayout mail, call;
    Button button, contact_booth_msg;
    String OthersUserresponseID, BoothUserName;
    LinearLayout message_call_layout;

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
            setContentView(R.layout.activity_contact_booth);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            OthersUserresponseID = sharedpreferences.getString("OtherUserID", " ");

            initializeViews();
            setTexts();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void initializeViews() {
        contactTime = findViewById(R.id.contactTime);
        contactDays = findViewById(R.id.contactDays);
        email = findViewById(R.id.email);
        phoneNumber = findViewById(R.id.phoneNumber);
        mail = findViewById(R.id.mail);
        call = findViewById(R.id.call);
        button = findViewById(R.id.btn_call);
        contact_booth_msg = findViewById(R.id.contact_booth_msg);
        message_call_layout = findViewById(R.id.message_call_layout);
        button.setOnClickListener(this);
        contact_booth_msg.setOnClickListener(this);
        BoothUserName = getIntent().getStringExtra("username");
    }

    private void setTexts() {
        if (sharedpreferences.getString("UserID", " ").equals(sharedpreferences.getString("OtherUserID", " "))){
            message_call_layout.setVisibility(View.GONE);
        }else {
            message_call_layout.setVisibility(View.VISIBLE);
        }
        contactTime.setText(TimeStampToTime(sharedpreferences.getString("boothFromTime", "")) + " " + getResources().getString(R.string.to) + " " + TimeStampToTime(sharedpreferences.getString("boothToTime", "")));
        contactDays.setText(sharedpreferences.getString("boothContactDays", ""));
        email.setText(sharedpreferences.getString("boothMail", ""));
        phoneNumber.setText(sharedpreferences.getString("boothMobile", ""));
    }

    public static String TimeStampToTime(String timestmp) {
        String localTime = "";
        try {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone(); /* debug: is it local time? */
            Log.d("Time zone: ", tz.getDisplayName()); /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(tz); /* print your timestamp and double check it's the date you expect */
            long timestamp = Long.parseLong(timestmp);
            Log.e("checkingtime", timestmp);
            localTime = sdf.format(new Date(timestamp * 1000));

            return localTime;
        }catch (NumberFormatException e){
            e.printStackTrace();
            return localTime;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_call:
             /*   Log.e("call", "clicked");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "03022378899"));
                Log.e("call", " " + Uri.parse("tel:" + phoneNumber.getText().toString()));
                if (ActivityCompat.checkSelfPermission(ContactBoothActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    Log.e("call", "NotGranted");
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
                Log.e("call", "Called");*/

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ phoneNumber.getText().toString()));
                startActivity(intent);

                break;
            case R.id.contact_booth_msg:

                Intent intent_msg = new Intent(ContactBoothActivity.this, ChatActivity.class);
                intent_msg.putExtra("UserName", BoothUserName);
                intent_msg.putExtra("OthersprofileID", OthersUserresponseID);
                intent_msg.putExtra("usertype", sharedpreferences.getString("LastState", ""));
                intent_msg.putExtra("othertype", "booth");
                startActivity(intent_msg);

                break;
        }
    }
}
