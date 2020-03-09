package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Fragments.AddProductFragment;
import com.schopfen.Booth.Fragments.Booth_Home_Fragment;
import com.schopfen.Booth.Fragments.Booth_Profile_Fragment;
import com.schopfen.Booth.Fragments.MyOrders_Fragment;
import com.schopfen.Booth.Fragments.NotificationFragment;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BoothMainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String UserID;
    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    Booth_Home_Fragment homeFragment = new Booth_Home_Fragment();
    NotificationFragment notificationsFragment = new NotificationFragment();
    Booth_Profile_Fragment myProfileFragment = new Booth_Profile_Fragment();
    AddProductFragment addProductFragment = new AddProductFragment();
    MyOrders_Fragment myOrders_fragment = new MyOrders_Fragment();
    Fragment active = homeFragment;
    String activity = "";
    String type = "";
    String notidata = "";
    public String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

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
        setContentView(R.layout.activity_booth_main);
        Intent intent = new Intent(BoothMainActivity.this, MyChatService.class);
        startService(intent);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //bundle must contain all info sent in "data" field of the notification
            notidata = bundle.getString("notification_data");
            if (notidata != null){
                try {
                    JSONObject data = new JSONObject(notidata);
                    type = data.getString("UserType");
                    if (type != null) {
                        if (type.equals("user")) {
                            activity = "notification";
                        } else if (type.equals("booth")) {
                            activity = "notification";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                activity = bundle.getString("acitivity");
            }
        }
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        mEditor.putString("LastState", "booth").commit();
        bottomNavigationView = findViewById(R.id.boothnavigation);
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            final View iconView = menuView.getChildAt(i).findViewById(com.google.android.material.R.id.icon);
            final ViewGroup.LayoutParams layoutParams = iconView.getLayoutParams();
            final DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            // set your height here
            layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
            // set your width here
            layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, displayMetrics);
            iconView.setLayoutParams(layoutParams);
        }
        frameLayout = findViewById(R.id.boothfragmentContainer);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        setFragment(homeFragment);
                        active = homeFragment;
                        return true;
                    case R.id.orders:
                        setFragment(myOrders_fragment);
                        active = myOrders_fragment;
                        return true;
                    case R.id.notifications:
                        setFragment(notificationsFragment);
                        active = notificationsFragment;
                        return true;
                    case R.id.myAccount:
                        mEditor.putString("Booth", "Mine").apply();
                        setFragment(myProfileFragment);
                        active = myProfileFragment;
                        return true;
                    case R.id.addProduct:
                        setFragment(addProductFragment);
                        active = addProductFragment;
                        return true;
                    default:
                        return false;
                }
            }
        });
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.boothfragmentContainer, myProfileFragment, "5").hide(myProfileFragment);
        fragmentTransaction.add(R.id.boothfragmentContainer, notificationsFragment, "4").hide(notificationsFragment);
        fragmentTransaction.add(R.id.boothfragmentContainer, addProductFragment, "3").hide(addProductFragment);
        fragmentTransaction.add(R.id.boothfragmentContainer, myOrders_fragment, "2").hide(myOrders_fragment);
        fragmentTransaction.add(R.id.boothfragmentContainer, homeFragment, "1");
        fragmentTransaction.commit();
        if (activity.equals("notification")) {
            bottomNavigationView.setSelectedItemId(R.id.notifications);
        }
        getUserDetails();
    }

    private void getUserDetails() {
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, BoothMainActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            String UserUnreadNotificationCount = user_info.getString("UserUnreadNotificationCount");
                            String BoothUnreadNotificationCount = user_info.getString("BoothUnreadNotificationCount");
                            if (BoothOrderCount.equals("0")) {
                                bottomNavigationView.getOrCreateBadge(R.id.orders).setNumber(Integer.parseInt(BoothOrderCount));
                                bottomNavigationView.getBadge(R.id.orders).setVisible(false);
                            } else {
                                bottomNavigationView.getOrCreateBadge(R.id.orders).setNumber(Integer.parseInt(BoothOrderCount));
                                bottomNavigationView.getBadge(R.id.orders).setVisible(true);
                            }
                            if (BoothUnreadNotificationCount.equals("0")) {
                                bottomNavigationView.getOrCreateBadge(R.id.notifications).setNumber(Integer.parseInt(BoothUnreadNotificationCount));
                                bottomNavigationView.getBadge(R.id.notifications).setVisible(false);
                            } else {
                                bottomNavigationView.getOrCreateBadge(R.id.notifications).setNumber(Integer.parseInt(BoothUnreadNotificationCount));
                                bottomNavigationView.getBadge(R.id.notifications).setVisible(true);
                            }
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(BoothMainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(BoothMainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView != null) {
            getUserDetails();
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.areyousureyouwanttoexit))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        BoothMainActivity.this.finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), null)
                .show();
    }
    // In your activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(active).show(fragment).commit();
    }
}