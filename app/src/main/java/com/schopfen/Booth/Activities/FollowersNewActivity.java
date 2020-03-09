package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.schopfen.Booth.Adapters.FollowNewPagerAdapter;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Fragments.FllolwNew_FollowersFragment;
import com.schopfen.Booth.Fragments.FollowMe_FollowingFragment;
import com.schopfen.Booth.R;

public class FollowersNewActivity extends AppCompatActivity {

    FollowNewPagerAdapter adapter;

    private FollowNewPagerAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    TabLayout tabLayout;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

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
            setContentView(R.layout.activity_followers_new);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            sectionsPageAdapter = new FollowNewPagerAdapter(getSupportFragmentManager());

            viewPager = findViewById(R.id.contain);
            setupViewPager(viewPager);

            tabLayout = findViewById(R.id.tabs);

            tabLayout.setupWithViewPager(viewPager);
        }else {
            setContentView(R.layout.no_internet_screen);
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new FollowNewPagerAdapter(getSupportFragmentManager());


        adapter.addFragment(new FllolwNew_FollowersFragment(), this.getResources().getString(R.string.followers) + " " + "(" + sharedpreferences.getString("followers", "") + ")");
        adapter.addFragment(new FollowMe_FollowingFragment(), this.getResources().getString(R.string.followings) + " " + "(" + sharedpreferences.getString("followings", "") + ")");

        viewPager.setAdapter(adapter);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
    }



}
