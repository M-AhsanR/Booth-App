package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.leo.simplearcloader.SimpleArcDialog;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Fragments.ExploreHomeFragment;
import com.schopfen.Booth.Fragments.HomeFragment;
import com.schopfen.Booth.Fragments.MyOrders_Fragment;
import com.schopfen.Booth.Fragments.MyProfileFragment;
import com.schopfen.Booth.Fragments.NotificationFragment;
import com.schopfen.Booth.Fragments.SearchFragment;
import com.schopfen.Booth.R;

public class ExploreAppActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String UserID;
    SimpleArcDialog simpleArcDialog;

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;
    ExploreHomeFragment homeFragment = new ExploreHomeFragment();

    Fragment active = homeFragment;


    LinearLayout mes;
    ImageView inbox, notifications;
    TextView messagesCount;
    public String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int MULTIPLE_PERMISSIONS = 10;

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
            setContentView(R.layout.activity_explore_app);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

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

            frameLayout = (FrameLayout) findViewById(R.id.fragmentContainer);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home :
                            setFragment(homeFragment);
                            active = homeFragment;
                            return true;

                        case R.id.orders :

                        case R.id.notifications :

                        case R.id.search :

                        case R.id.myAccount :
                            LoginDialog();
                            return false;

                        default:
                            return false;
                    }
                }
            });

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragmentContainer, homeFragment, "1");
            fragmentTransaction.commit();
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(active).show(fragment).commit();
    }

    private void LoginDialog(){
        final Dialog verifyDialog = new Dialog(ExploreAppActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.login_first_dialog);

        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);

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

                Intent intent = new Intent(ExploreAppActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
                verifyDialog.dismiss();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setMessage("Are you sure you want to exit?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        ExploreAppActivity.this.finish();
//                    }
//                })
//                .setNegativeButton("No", null)
//                .show();
//    }
}
