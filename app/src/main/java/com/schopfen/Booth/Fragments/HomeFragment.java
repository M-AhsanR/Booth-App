package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.schopfen.Booth.Activities.Ask_QuestionActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.Adapters.Home_SectionsPageAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment  implements  View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    private static final String  TAG = "MainListActivity";
    Home_SectionsPageAdapter adapter;
    BottomNavigationView bottomNavigationView;
    RelativeLayout header_comment,header_cart,settings;
    ImageView inbox_img;
    TextView cart_count;
    TextView msg_count;

    private Home_SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    TabLayout tabLayout;

    private View root;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null){
            root = inflater.inflate(R.layout.fragment_home, container, false);
        }

        return root;
    }

    private void setupViewPager(ViewPager viewPager){
        adapter = new Home_SectionsPageAdapter(getChildFragmentManager());


        adapter.addFragment(new Home_Home_Fragment(),getActivity().getResources().getString(R.string.home));
        adapter.notifyDataSetChanged();
        adapter.addFragment(new Home_Booth_Fragment(),getActivity().getResources().getString(R.string.booths));
        adapter.notifyDataSetChanged();
        adapter.addFragment(new HomePeopleFragment(),getActivity().getResources().getString(R.string.friends));
        adapter.notifyDataSetChanged();

//        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        initilizeviews(view);
        sectionsPageAdapter = new Home_SectionsPageAdapter(getChildFragmentManager());

        getUserDetails();

        viewPager = view.findViewById(R.id.contain);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        bottomNavigationView = getActivity().findViewById(R.id.navigation);
//        setRetainInstance(true);
    }

    private void initilizeviews(View view) {
        header_comment = view.findViewById(R.id.header_comment);
        header_cart = view.findViewById(R.id.header_cart);
        settings = view.findViewById(R.id.settings);
        inbox_img = view.findViewById(R.id.inbox_img);
        cart_count = view.findViewById(R.id.cart_count);
        msg_count = view.findViewById(R.id.msg_count);

        header_comment.setOnClickListener(this);
        header_cart.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

//    @Override
//    public void onResume() {
//       super.onResume();
//       sectionsPageAdapter = new Home_SectionsPageAdapter(getChildFragmentManager());
//       setupViewPager(viewPager);
//       tabLayout.setupWithViewPager(viewPager);
//    }

    private void getUserDetails(){

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()){
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String CartCount = user_info.getString("CartCount");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");

                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            mEditor.putString("UserOrderCount", UserOrderCount);
                            mEditor.putString("BoothOrderCount", BoothOrderCount);
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
                            mEditor.commit();

                            if (UnreadMessageCount.equals("0")){
                                msg_count.setVisibility(View.GONE);
                            }else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(UnreadMessageCount);
                            }

//                            if (HasUnreadMessage.equals("yes")){
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                            }else {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                            }
                            if (CartCount.equals("0")){
                                cart_count.setVisibility(View.GONE);
                            }else {
                                cart_count.setVisibility(View.VISIBLE);
                                cart_count.setText(CartCount);
                            }

                        }else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401){
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.header_comment:
               startActivity(new Intent(getActivity(), InboxActivity.class));
                break;
            case R.id.header_cart:
                startActivity(new Intent(getActivity(), ShoppingCartActivity.class));
                break;
            case R.id.settings:
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
                break;
        }
    }
}
