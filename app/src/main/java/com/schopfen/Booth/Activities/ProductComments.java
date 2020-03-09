package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hendraanggrian.appcompat.socialview.Mention;
import com.hendraanggrian.appcompat.widget.MentionArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.linkedin.android.spyglass.ui.MentionsEditText;
import com.schopfen.Booth.Adapters.Answers_Details_Adapter;
import com.schopfen.Booth.Adapters.FollowersAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CommentsModel;
import com.schopfen.Booth.Models.FollowersData;
import com.schopfen.Booth.Models.MentionedUsersInfo;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProductComments extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    RecyclerView commentsRecycler;
    ArrayList<CommentsModel> commentsArray = new ArrayList<>();
    TextView showing_commentscunt;
    LinearLayout previousComentsLayout;
    String sizeofArray = "0";
    ImageView postCommentbtn;
    SocialAutoCompleteTextView postCommentText;
    private ArrayAdapter<Mention> defaultMentionAdapter;
    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;
    String commentCount;
    ProgressBar progressmsg;
    String mentionedPersons = "";
    String lastmentionedPersons = "";
    boolean mention = false;

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
            setContentView(R.layout.activity_product_comments);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            commentCount = getIntent().getStringExtra("COUNT");

            Initialization();
            GetCommentsApi();

            if (Integer.parseInt(commentCount) > 20) {
                previousComentsLayout.setVisibility(View.VISIBLE);
            } else {
                previousComentsLayout.setVisibility(View.GONE);
            }

            getFollowersList();

            postCommentbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (postCommentText.getText().toString().isEmpty() || postCommentText.getText().toString().startsWith(" ")) {
                        Toast.makeText(ProductComments.this, "Enter the comment first", Toast.LENGTH_SHORT).show();
                    } else {
                        hideKeyboard(ProductComments.this);
                        PostCommentsApi(postCommentText.getText().toString());
                    }
                }
            });

            previousComentsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GetCommentsApi();
                }
            });
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    private void getFollowersList(){
        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        Log.e("IDs", sharedpreferences.getString("UserID", " ") + "  " + sharedpreferences.getString("OtherUserID", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getFollowers + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&Type=user", ProductComments.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserFollowersResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            defaultMentionAdapter = new MentionArrayAdapter<>(ProductComments.this);

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            JSONArray jsonArray = jsonObject1.getJSONArray("followers");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String UserName = jsonObject2.getString("UserName");
                                String Image = jsonObject2.getString("Image");
                                String FullName = jsonObject2.getString("FullName");
                                if (Image.equals("")){
                                    defaultMentionAdapter.add(new Mention(UserName, FullName, R.drawable.user));
                                }else {
                                    defaultMentionAdapter.add(new Mention(UserName, FullName, Constants.URL.IMG_URL + Image));
                                }

                            }

//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ProductComments.this,
//                                    android.R.layout.simple_list_item_1, mentioned_users);
//                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

                            postCommentText.setMentionAdapter(defaultMentionAdapter);

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(ProductComments.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ProductComments.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Initialization() {
        showing_commentscunt = findViewById(R.id.showing_commentscunt);
        commentsRecycler = findViewById(R.id.commentsRecycler);
        postCommentbtn = findViewById(R.id.postCommentbtn);
        postCommentText = findViewById(R.id.postCommentText);
        previousComentsLayout = findViewById(R.id.previousComentsLayout);
        progressmsg = findViewById(R.id.post_msg_loader);
    }

    private void PostCommentsApi(String text) {

        String[] splited = text.split("\\s+");

        for (int i = 0; i < splited.length; i++){
            if (splited[i].startsWith("@")){
                if (lastmentionedPersons.length() > 0){
                    lastmentionedPersons = lastmentionedPersons + "," + splited[i].substring(1, splited[i].length());
                }else {
                    lastmentionedPersons = splited[i].substring(1, splited[i].length());
                }
            }
        }

        postCommentbtn.setVisibility(View.GONE);
        progressmsg.setVisibility(View.VISIBLE);

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", sharedpreferences.getString("ProductID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Comment", postCommentText.getText().toString());
        body.put("CommentedAs", sharedpreferences.getString("LastState", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("MentionedUsers", lastmentionedPersons);

        Log.e("lastmentionedPersons", lastmentionedPersons);
        lastmentionedPersons = "";

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.PRODUCTCOMMENT, ProductComments.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("postComment", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            postCommentText.setText("");

                            JSONObject data = jsonObject.getJSONObject("data");

                            String Comment = data.getString("Comment");
                            String CreatedAt = data.getString("CreatedAt");
                            String CompressedImage = data.getString("CompressedImage");
                            String BoothCompressedImage = data.getString("CompressedBoothImage");
                            String UserName = data.getString("UserName");
                            String BoothUserName = data.getString("BoothUserName");
                            String CityTitle = data.getString("CityTitle");
                            String userID = data.getString("UserID");
                            String ProductCommentID = data.getString("ProductCommentID");
                            String CommentedAs = data.getString("CommentedAs");
                            ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                            JSONArray MentionedUsersInfo = data.getJSONArray("MentionedUsersInfo");
                            for (int k = 0; k < MentionedUsersInfo.length(); k++){
                                JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                String UserID = object.getString("UserID");
                                String FullName = object.getString("FullName");
                                String MentionedName = object.getString("MentionedName");
                                String MentionedUserType = object.getString("MentionedUserType");

                                mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                            }

                            commentsArray.add(0, new CommentsModel(ProductCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));

//                            Collections.reverse(commentsArray);

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(sharedpreferences.getString("ProductID", " "), "product",ProductComments.this, commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            RecyclerView.LayoutManager layoutManagerone = new LinearLayoutManager(ProductComments.this, RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManagerone);
//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(QuestionsDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
//                            commentsRecycler.scrollToPosition(commentsArray.size() - 1);

                            postCommentbtn.setVisibility(View.VISIBLE);
                            progressmsg.setVisibility(View.GONE);

                        } else {
                            postCommentbtn.setVisibility(View.VISIBLE);
                            progressmsg.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        postCommentbtn.setVisibility(View.VISIBLE);
                        progressmsg.setVisibility(View.GONE);
                    }
                } else {
                    postCommentbtn.setVisibility(View.VISIBLE);
                    progressmsg.setVisibility(View.GONE);
                    Toast.makeText(ProductComments.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void GetCommentsApi() {
        CustomLoader.showDialog(ProductComments.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", sharedpreferences.getString("ProductID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Start", sizeofArray);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETPRODUCTCOMMENTS, ProductComments.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Productcomments", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            JSONArray comments = jsonObject.getJSONArray("comments");

                            for (int i = 0; i < comments.length(); i++) {
                                JSONObject obj = comments.getJSONObject(i);

                                String Comment = obj.getString("Comment");
                                String CreatedAt = obj.getString("CreatedAt");
                                String CompressedImage = obj.getString("CompressedImage");
                                String BoothCompressedImage = obj.getString("CompressedBoothImage");
                                String UserName = obj.getString("UserName");
                                String BoothUserName = obj.getString("BoothUserName");
                                String CityTitle = obj.getString("CityTitle");
                                String userID = obj.getString("UserID");
                                String ProductCommentID = obj.getString("ProductCommentID");
                                String CommentedAs = obj.getString("CommentedAs");

                                ArrayList<MentionedUsersInfo> mentionedUsersInfos = new ArrayList<>();
                                JSONArray MentionedUsersInfo = obj.getJSONArray("MentionedUsersInfo");
                                for (int k = 0; k < MentionedUsersInfo.length(); k++){
                                    JSONObject object = MentionedUsersInfo.getJSONObject(k);

                                    String UserID = object.getString("UserID");
                                    String FullName = object.getString("FullName");
                                    String MentionedName = object.getString("MentionedName");
                                    String MentionedUserType = object.getString("MentionedUserType");

                                    mentionedUsersInfos.add(new MentionedUsersInfo(UserID, FullName, MentionedName, MentionedUserType));

                                }

                                commentsArray.add(new CommentsModel(ProductCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));
                            }

                            sizeofArray = String.valueOf(commentsArray.size());
//                            Collections.reverse(commentsArray);

                            if (commentsArray.size() >= Integer.parseInt(commentCount)) {
                                previousComentsLayout.setVisibility(View.GONE);
                            }

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProductComments.this, RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManager);
                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(sharedpreferences.getString("ProductID", " "), "product",ProductComments.this, commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });

//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(ProductDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
                            commentsAdapter.notifyDataSetChanged();


                        } else {

                            CustomLoader.dialog.dismiss();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ProductComments.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}