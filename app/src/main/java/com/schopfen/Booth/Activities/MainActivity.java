package com.schopfen.Booth.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leo.simplearcloader.SimpleArcDialog;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Fragments.HomeFragment;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Fragments.MyOrders_Fragment;
import com.schopfen.Booth.Fragments.MyProfileFragment;
import com.schopfen.Booth.Fragments.NotificationFragment;
import com.schopfen.Booth.Fragments.SearchFragment;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String UserID;
    SimpleArcDialog simpleArcDialog;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    HomeFragment homeFragment = new HomeFragment();
    NotificationFragment notificationsFragment = new NotificationFragment();
    SearchFragment searchFragment = new SearchFragment();
    MyProfileFragment myProfileFragment = new MyProfileFragment();
    MyOrders_Fragment myOrders_fragment = new MyOrders_Fragment();

    Fragment active = homeFragment;
    String activity = "";
    String type = "";
    String notidata = "";

    LinearLayout mes;
    ImageView inbox, notifications;
    TextView messagesCount;
    public String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int MULTIPLE_PERMISSIONS = 10;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (CustomLoader.dialog != null){
//            CustomLoader.dialog.dismiss();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (CustomLoader.dialog != null){
//            CustomLoader.dialog.dismiss();
//        }
//    }

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
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

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
//        activity = getIntent().getStringExtra("");


        mEditor.putString("LastState", "user").commit();


        Intent intent = new Intent(MainActivity.this, MyChatService.class);
//        i.putExtra("KEY1", "Value to be used by the service");
        startService(intent);

        // Log.e("notification", FirebaseInstanceId.getInstance().getToken());
        bottomNavigationView = findViewById(R.id.navigation);


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
//        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        frameLayout = (FrameLayout) findViewById(R.id.fragmentContainer);
//        homeFragment = new HomeFragment();
//        notificationsFragment = new NotificationFragment();
//        searchFragment = new SearchFragment();
//        myProfileFragment = new MyProfileFragment();
//        myOrders_fragment = new MyOrders_Fragment();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
//                        bottomNavigationView.setItemBackgroundResource(R.color.itemselectedcolor);

                        if (homeFragment.isHidden()) {
                            setFragment(homeFragment);
                            active = homeFragment;
                            hideKeyboard(bottomNavigationView);
                        } else {
                            Home_Home_Fragment.recyclerView.smoothScrollToPosition(0);
                        }

                        return true;

                    case R.id.orders:
//                        bottomNavigationView.setItemBackgroundResource(R.color.itemselectedcolor);
                        setFragment(myOrders_fragment);
                        active = myOrders_fragment;
                        hideKeyboard(bottomNavigationView);
                        return true;

                    case R.id.search:
//                        bottomNavigationView.setItemBackgroundResource(R.color.itemselectedcolor);
                        setFragment(searchFragment);
                        active = searchFragment;
                        hideKeyboard(bottomNavigationView);
                        return true;

                    case R.id.notifications:
//                        bottomNavigationView.setItemBackgroundResource(R.color.itemselectedcolor);
                        setFragment(notificationsFragment);
                        active = notificationsFragment;
                        hideKeyboard(bottomNavigationView);
                        return true;

                    case R.id.myAccount:
//                        bottomNavigationView.setItemBackgroundResource(R.color.itemselectedcolor);
                        setFragment(myProfileFragment);
                        active = myProfileFragment;
                        hideKeyboard(bottomNavigationView);
                        return true;

                    default:
                        return false;
                }
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainer, myProfileFragment, "5").hide(myProfileFragment);
        fragmentTransaction.add(R.id.fragmentContainer, notificationsFragment, "4").hide(notificationsFragment);
        fragmentTransaction.add(R.id.fragmentContainer, searchFragment, "3").hide(searchFragment);
        fragmentTransaction.add(R.id.fragmentContainer, myOrders_fragment, "2").hide(myOrders_fragment);
        fragmentTransaction.add(R.id.fragmentContainer, homeFragment, "1");
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

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, MainActivity.this, body, headers, new ServerCallback() {
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

                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.commit();

                            if (UserOrderCount.equals("0")) {
                                bottomNavigationView.getOrCreateBadge(R.id.orders).setNumber(Integer.parseInt(UserOrderCount));
                                bottomNavigationView.getBadge(R.id.orders).setVisible(false);
                            } else {
                                bottomNavigationView.getOrCreateBadge(R.id.orders).setNumber(Integer.parseInt(UserOrderCount));
                                bottomNavigationView.getBadge(R.id.orders).setVisible(true);
                            }

                            if (UserUnreadNotificationCount.equals("0")) {
                                bottomNavigationView.getOrCreateBadge(R.id.notifications).setNumber(Integer.parseInt(UserUnreadNotificationCount));
                                bottomNavigationView.getBadge(R.id.notifications).setVisible(false);
                            } else {
                                bottomNavigationView.getOrCreateBadge(R.id.notifications).setNumber(Integer.parseInt(UserUnreadNotificationCount));
                                bottomNavigationView.getBadge(R.id.notifications).setVisible(true);
                            }

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (CustomLoader.dialog != null){
//            CustomLoader.dialog.dismiss();
//        }
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
                        MainActivity.this.finish();
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

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) MainActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(active).show(fragment).commit();
    }
}