package com.schopfen.Booth.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.Adapters.FollowersAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FllolwNew_FollowersFragment extends Fragment {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    ArrayList<FollowersData> followersData = new ArrayList<>();

    RecyclerView followersRecycler ;
    LinearLayout empty_message,data_linear;

    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follownew_followersfragment, container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        initializaViews(view);
        getFollowersList();
    }

    private void initializaViews(View view){
        followersRecycler = view.findViewById(R.id.followersRecycler);
        empty_message = view.findViewById(R.id.empty_linear);
        data_linear = view.findViewById(R.id.data_linear);
    }

    private void getFollowersList(){

        CustomLoader.showDialog(getActivity());

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        Log.e("IDs", sharedpreferences.getString("UserID", " ") + "  " + sharedpreferences.getString("OtherUserID", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getFollowers + sharedpreferences.getString("UserID", " ") + "&OtherUserID=" + sharedpreferences.getString("OtherUserID", "")  + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&Type=user", getActivity(), postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserFollowersResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            followersData.clear();

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String total_follower = jsonObject1.getString("total_follower");
                            JSONArray jsonArray = jsonObject1.getJSONArray("followers");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String BoothCoverImage = jsonObject2.getString("BoothCoverImage");
                                String BoothImage = jsonObject2.getString("BoothImage");
                                String BoothName = jsonObject2.getString("BoothName");
                                String UserName = jsonObject2.getString("UserName");
                                String BoothUserName = jsonObject2.getString("BoothUserName");
                                String CityTitle = jsonObject2.getString("CityTitle");
                                String CompressedBoothCoverImage = jsonObject2.getString("CompressedBoothCoverImage");
                                String CompressedBoothImage = jsonObject2.getString("CompressedBoothImage");
                                String CompressedCoverImage = jsonObject2.getString("CompressedCoverImage");
                                String CompressedImage = jsonObject2.getString("CompressedImage");
                                String FullName = jsonObject2.getString("FullName");
                                String Image = jsonObject2.getString("Image");
                                String UserID = jsonObject2.getString("UserID");
                                String LastState = jsonObject2.getString("LastState");

                                followersData.add(new FollowersData(UserName, BoothCoverImage, BoothImage, BoothName, CityTitle, CompressedBoothCoverImage, CompressedBoothImage,
                                        CompressedCoverImage, CompressedImage, FullName, Image, UserID, BoothUserName, LastState));
                            }

                            if (followersData.isEmpty()){
                                data_linear.setVisibility(View.GONE);
                                empty_message.setVisibility(View.VISIBLE);
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
                            followersRecycler.setLayoutManager(layoutManager);

                            FollowersAdapter followersAdapter = new FollowersAdapter(getActivity(), followersData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    Log.e("position", position + " ");
                                    Intent intent = new Intent(getActivity(), OthersProfileActivity.class);
                                    intent.putExtra("OtherUserID", followersData.get(position).getUserID());
                                    startActivity(intent);
                                }
                            });

                            followersRecycler.setAdapter(followersAdapter);
//                            followersAdapter.notifyDataSetChanged();


                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            CustomLoader.dialog.dismiss();
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
