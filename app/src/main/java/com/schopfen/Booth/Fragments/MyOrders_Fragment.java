package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.LabelVisibility;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.schopfen.Booth.Activities.BoothSettingsActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.Adapters.Home_SectionsPageAdapter;
import com.schopfen.Booth.Adapters.MyOrder_SectionsPageAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyOrders_Fragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    private String mParam1;
    private String mParam2;

    MyOrder_SectionsPageAdapter adapter;
    RelativeLayout header_comment,header_cart,header_more;
    ImageView inbox_img;
    TextView cart_count;
    TextView msg_count;

    private MyOrder_SectionsPageAdapter sectionsPageAdapter;
    private ViewPager viewPager;
    TabLayout tabLayout;

    public static MyOrders_Fragment newInstance(String param1, String param2) {
        MyOrders_Fragment fragment = new MyOrders_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        return inflater.inflate(R.layout.fragment_myorders, container, false);

    }

    private void setupViewPager(ViewPager viewPager){
        adapter = new MyOrder_SectionsPageAdapter(getChildFragmentManager());


        if (sharedpreferences.getString("LastState", "").equals("user")){
            if (sharedpreferences.getString("UserOrderCount", "").equals("0")){
                adapter.addFragment(new MyCurrent_Orders_Fragment(),getActivity().getResources().getString(R.string.pending));
            }else {
                adapter.addFragment(new MyCurrent_Orders_Fragment(),getActivity().getResources().getString(R.string.pending) + " " + "(" + sharedpreferences.getString("UserOrderCount", "") + ")");
            }
        }else if (sharedpreferences.getString("LastState", "").equals("booth")){
            if (sharedpreferences.getString("BoothOrderCount", "").equals("0")){
                adapter.addFragment(new MyCurrent_Orders_Fragment(),getActivity().getResources().getString(R.string.pending));
            }else {
                adapter.addFragment(new MyCurrent_Orders_Fragment(),getActivity().getResources().getString(R.string.pending) + " " + "(" + sharedpreferences.getString("BoothOrderCount", "") + ")");
            }
        }

        adapter.addFragment(new MyCompleted_Orders_Fragment(),getActivity().getResources().getString(R.string.completed));
        adapter.addFragment(new UserCancelledOrders(),getActivity().getResources().getString(R.string.cancelled));
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            getUserDetails();
            tabLayout.setupWithViewPager(viewPager);
            setupViewPager(viewPager);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initilizeviews(view);
        sectionsPageAdapter = new MyOrder_SectionsPageAdapter(getChildFragmentManager());

        if (sharedpreferences.getString("LastState", " ").equals("booth")){
            header_cart.setVisibility(View.GONE);
//            header_comment.setVisibility(View.GONE);
        }else {
            header_cart.setVisibility(View.VISIBLE);
            header_comment.setVisibility(View.VISIBLE);
        }

        viewPager = view.findViewById(R.id.contain);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
//        tabLayout.getTabAt(0).setCustomView(R.layout.tab_custom_view);

        getUserDetails();


    }

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
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");

                            mEditor.putString("UserOrderCount", UserOrderCount).commit();
                            mEditor.putString("BoothOrderCount", BoothOrderCount).commit();

//                            if(tabLayout != null && tabLayout.getTabAt(0).getCustomView() != null) {
//                                TextView b = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.badge);
//                                if(b != null) {
//                                    b.setText(UserOrderCount);
//                                }
//                                View v = tabLayout.getTabAt(0).getCustomView().findViewById(R.id.badgeCotainer);
//                                if(v != null) {
//                                    v.setVisibility(View.VISIBLE);
//                                }
//                            }

                            if (sharedpreferences.getString("LastState", "").equals("user")){
                                if (UnreadMessageCount.equals("0")){
                                    msg_count.setVisibility(View.GONE);
                                }else {
                                    msg_count.setVisibility(View.VISIBLE);
                                    msg_count.setText(UnreadMessageCount);
                                }
                            }else if (sharedpreferences.getString("LastState", "").equals("booth")){
                                if (BoothUnreadMessageCount.equals("0")){
                                    msg_count.setVisibility(View.GONE);
                                }else {
                                    msg_count.setVisibility(View.VISIBLE);
                                    msg_count.setText(BoothUnreadMessageCount);
                                }
                            }
                            if (UnreadMessageCount.equals("0")){
                                msg_count.setVisibility(View.GONE);
                            }else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(UnreadMessageCount);
                            }

//                            if (sharedpreferences.getString("LastState", "").equals("user")){
//                                if (HasUnreadMessage.equals("yes")){
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                                }else {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                                }
//                            }else if (sharedpreferences.getString("LastState", "").equals("booth")){
//                                if (BoothHasUnreadMessage.equals("yes")){
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                                }else {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                                }
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


    private void initilizeviews(View view) {
        header_comment = view.findViewById(R.id.header_comment);
        header_cart = view.findViewById(R.id.header_cart);
        header_more = view.findViewById(R.id.header_more);
        inbox_img = view.findViewById(R.id.inbox_img);
        cart_count = view.findViewById(R.id.cart_count);
        msg_count = view.findViewById(R.id.msg_count);

        header_comment.setOnClickListener(this);
        header_cart.setOnClickListener(this);
        header_more.setOnClickListener(this);
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        sectionsPageAdapter = new MyOrder_SectionsPageAdapter(getChildFragmentManager());
//
//        setupViewPager(viewPager);
//        tabLayout.setupWithViewPager(viewPager);
//
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.header_comment:
                startActivity(new Intent(getActivity(), InboxActivity.class));
                break;
            case R.id.header_cart:
                startActivity(new Intent(getActivity(), ShoppingCartActivity.class));
                break;
            case R.id.header_more:
                if (sharedpreferences.getString("LastState", " ").equals("booth")){
                    Intent i = new Intent(getActivity(), BoothSettingsActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(i);
                }
                break;
        }
    }
}
