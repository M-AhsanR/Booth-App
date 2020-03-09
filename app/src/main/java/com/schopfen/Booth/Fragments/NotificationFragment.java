package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.schopfen.Booth.Activities.BoothSettingsActivity;
import com.schopfen.Booth.Activities.FollowersActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.RegisterBoothActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.Adapters.NotificationsAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.DataClasses.NotificationsData;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.MentionedUsersInfo;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends Fragment {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    RecyclerView NotifRecyc;
    RelativeLayout noNotificationlayout, progressLayout;
    RelativeLayout header_comment, header_cart, header_more;
    ImageView inbox_img;
    TextView cart_count;
    TextView msg_count;
    SwipeRefreshLayout swipe_notification;
    BottomNavigationView bottomNavigationView;
    LinearLayoutManager layoutManager;
    boolean isLoading = true;
    ProgressBar progressBar;
    NotificationsAdapter newNotificationsAdapter;

    ArrayList<NotificationsData> NotificationsArray = new ArrayList<>();

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getNotifications();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Initialization(view);

        if (sharedpreferences.getString("LastState", " ").equals("booth")) {
            header_cart.setVisibility(View.GONE);
//            header_comment.setVisibility(View.GONE);
        } else {
            header_cart.setVisibility(View.VISIBLE);
            header_comment.setVisibility(View.VISIBLE);
        }

        getUserDetails();

        getNotifications();

        swipe_notification.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotifications();
                getUserDetails();
            }
        });

    }

    private void getUserDetails() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                Log.e("UserDetailsResponse", result + " ");
                if (ERROR.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONObject user_info = jsonObject.getJSONObject("user_info");

                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String CartCount = user_info.getString("CartCount");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            String UserUnreadNotificationCount = user_info.getString("UserUnreadNotificationCount");
                            String BoothUnreadNotificationCount = user_info.getString("BoothUnreadNotificationCount");
                            mEditor.putString("UserOrderCount", UserOrderCount).commit();
                            mEditor.putString("BoothOrderCount", BoothOrderCount).commit();

                            if (bottomNavigationView != null) {
                                if (sharedpreferences.getString("LastState", "").equals("user")) {
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
                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
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
                                }
                            }

                            if (sharedpreferences.getString("LastState", "").equals("user")) {
                                if (UnreadMessageCount.equals("0")) {
                                    msg_count.setVisibility(View.GONE);
                                } else {
                                    msg_count.setVisibility(View.VISIBLE);
                                    msg_count.setText(UnreadMessageCount);
                                }
                            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                if (BoothUnreadMessageCount.equals("0")) {
                                    msg_count.setVisibility(View.GONE);
                                } else {
                                    msg_count.setVisibility(View.VISIBLE);
                                    msg_count.setText(BoothUnreadMessageCount);
                                }
                            }

//                            if (sharedpreferences.getString("LastState", "").equals("user")) {
//                                if (HasUnreadMessage.equals("yes")) {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                                } else {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                                }
//                            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
//                                if (BoothHasUnreadMessage.equals("yes")) {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                                } else {
//                                    inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                                }
//                            }

                            if (CartCount.equals("0")) {
                                cart_count.setVisibility(View.GONE);
                            } else {
                                cart_count.setVisibility(View.VISIBLE);
                                cart_count.setText(CartCount);
                            }

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void Initialization(View view) {
        if (sharedpreferences.getString("LastState", "").equals("user")) {
            bottomNavigationView = getActivity().findViewById(R.id.navigation);
        } else {
            bottomNavigationView = getActivity().findViewById(R.id.boothnavigation);
        }
        NotifRecyc = view.findViewById(R.id.NotificationRecycler);
        noNotificationlayout = view.findViewById(R.id.noNotificationMainLayout);
        progressLayout = view.findViewById(R.id.progressLayout);
        header_more = view.findViewById(R.id.notifi_header_more);
        inbox_img = view.findViewById(R.id.inbox_img);
        cart_count = view.findViewById(R.id.cart_count);
        msg_count = view.findViewById(R.id.msg_count);
        progressBar = view.findViewById(R.id.progressbar);
        header_comment = view.findViewById(R.id.notifi_header_comment);
        header_cart = view.findViewById(R.id.notifi_header_cart);
        swipe_notification = view.findViewById(R.id.swipe_notification);

        header_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("LastState", " ").equals("booth")) {
                    Intent i = new Intent(getActivity(), BoothSettingsActivity.class);
                    startActivity(i);
                } else {
                    Intent i = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(i);
                }
            }
        });
        header_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ShoppingCartActivity.class));
            }
        });
        header_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InboxActivity.class));
            }
        });
    }


    private void getNotifications() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getNotifications + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&Type=" + sharedpreferences.getString("LastState", "") + "&Start=" + "0", getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("NotificationsResponse", result);
                    try {

                        swipe_notification.setRefreshing(false);

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            NotificationsArray.clear();

                            JSONArray notifications = jsonObject.getJSONArray("notifications");
                            for (int i = 0; i < notifications.length(); i++) {
                                JSONObject jsonObject1 = notifications.getJSONObject(i);

                                String UserNotificationID = jsonObject1.getString("UserNotificationID");
                                String UserType = jsonObject1.getString("UserType");
                                String Type = jsonObject1.getString("Type");
                                String UserIDofNoti = jsonObject1.getString("UserID");
                                String ProductID = jsonObject1.getString("ProductID");
                                String QuestionID = jsonObject1.getString("QuestionID");
                                String QuestionCommentID = jsonObject1.getString("QuestionCommentID");
                                String IsRead = jsonObject1.getString("IsRead");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String UserName = jsonObject1.getString("UserName");
                                String UserImage = jsonObject1.getString("UserImage");
                                String NotificationText = jsonObject1.getString("NotificationText");
                                String ProductImage = jsonObject1.optString("ProductImage");
                                String QuestionImage = jsonObject1.optString("QuestionImage");
                                String ActivityDoneAs = jsonObject1.optString("ActivityDoneAs");
                                String BoothUserName = jsonObject1.optString("BoothUserName");
                                String BoothImage = jsonObject1.optString("BoothImage");
                                String OrderRequestID = jsonObject1.optString("OrderRequestID");

                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = jsonObject1.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserID = object.getString("UserID");
                                    String FullName = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                                }


                                NotificationsArray.add(new NotificationsData(OrderRequestID, ActivityDoneAs, BoothUserName, BoothImage, UserNotificationID, UserType, Type, UserIDofNoti, ProductID,
                                        QuestionID, QuestionCommentID, IsRead, CreatedAt, UserName, UserImage, NotificationText, ProductImage, QuestionImage, mentionedUsersInfos));
                            }

                            if (NotificationsArray.isEmpty()) {
                                NotifRecyc.setVisibility(View.GONE);
                                progressLayout.setVisibility(View.GONE);
                                noNotificationlayout.setVisibility(View.VISIBLE);
                            } else {
                                NotifRecyc.setVisibility(View.VISIBLE);
                                progressLayout.setVisibility(View.GONE);
                                noNotificationlayout.setVisibility(View.GONE);
                                layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                                NotifRecyc.setLayoutManager(layoutManager);
                                newNotificationsAdapter = new NotificationsAdapter(getActivity(), NotificationsArray, new NotificationsAdapter.CRLCallbacks() {
                                    @Override
                                    public void onItemClick(int position) {

                                    }
                                });
                                NotifRecyc.setAdapter(newNotificationsAdapter);
                                NotifRecyc.setOnScrollListener(recyclerViewOnScrollListener);

                            }

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount, totalItemCount, pastVisiblesItems, lastVisibleItem, threshhold = 1;
            if (dy > 0) {

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();


//            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                int a = 10;
                int b = a;

//                if (!isLoading && !isLastPage) {
//                    if (visibleItemCount >= totalItemCount
//
//                            && totalItemCount >= PAGE_SIZE) {
//                        loadMoreItems();
//                    }
//                }

                if (isLoading) {

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount)

//                    if ( totalItemCount  <= (lastVisibleItem + threshhold))

                    {
                        isLoading = false;
                        progressBar.setVisibility(View.VISIBLE);
                        loadMoreItems();
                    }

                }
            }
        }
    };

    private void loadMoreItems() {
        isLoading = false;


        pagination();
    }


    private void pagination() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getNotifications + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE + "&Type=" + sharedpreferences.getString("LastState", "") + "&Start=" + String.valueOf(NotificationsArray.size()), getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("NotificationsResponse", result);
                    try {

                        isLoading = true;
                        ArrayList<NotificationsData> test = new ArrayList<>();

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONArray notifications = jsonObject.getJSONArray("notifications");
                            for (int i = 0; i < notifications.length(); i++) {
                                JSONObject jsonObject1 = notifications.getJSONObject(i);

                                String UserNotificationID = jsonObject1.getString("UserNotificationID");
                                String UserType = jsonObject1.getString("UserType");
                                String Type = jsonObject1.getString("Type");
                                String UserIDofNoti = jsonObject1.getString("UserID");
                                String ProductID = jsonObject1.getString("ProductID");
                                String QuestionID = jsonObject1.getString("QuestionID");
                                String QuestionCommentID = jsonObject1.getString("QuestionCommentID");
                                String IsRead = jsonObject1.getString("IsRead");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String UserName = jsonObject1.getString("UserName");
                                String UserImage = jsonObject1.getString("UserImage");
                                String NotificationText = jsonObject1.getString("NotificationText");
                                String ProductImage = jsonObject1.optString("ProductImage");
                                String QuestionImage = jsonObject1.optString("QuestionImage");
                                String ActivityDoneAs = jsonObject1.optString("ActivityDoneAs");
                                String BoothUserName = jsonObject1.optString("BoothUserName");
                                String BoothImage = jsonObject1.optString("BoothImage");
                                String OrderRequestID = jsonObject1.optString("OrderRequestID");

                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = jsonObject1.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++) {
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserID = object.getString("UserID");
                                    String FullName = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                                }


                                NotificationsArray.add(new NotificationsData(OrderRequestID, ActivityDoneAs, BoothUserName, BoothImage, UserNotificationID, UserType, Type, UserIDofNoti, ProductID,
                                        QuestionID, QuestionCommentID, IsRead, CreatedAt, UserName, UserImage, NotificationText, ProductImage, QuestionImage, mentionedUsersInfos));
                            }


                            progressBar.setVisibility(View.GONE);
                            newNotificationsAdapter.addfeed(test);


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
