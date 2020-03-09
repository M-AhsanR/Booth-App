package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.schopfen.Booth.Adapters.Answers_Details_Adapter;
import com.schopfen.Booth.Adapters.HomeHomePagerAdapter;
import com.schopfen.Booth.Adapters.MyViewHolderAMAAdapter;
import com.schopfen.Booth.Adapters.SimilarProductAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.CommentsModel;
import com.schopfen.Booth.Models.MentionedUsersInfo;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.SimilarProductsModel;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class QuestionsDetailsActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView commentsRecycler, recyclerView;
    CircleImageView profileImage;
    LinearLayout previousComentsLayout, share_btn;
    TextView name, city, time, description, showing_commentscunt, comentCount;
    SocialAutoCompleteTextView postCommentText;
    ImageView postCommentbtn, more;
    NestedScrollView nestedscrollview;
    ArrayList<CommentsModel> commentsArray = new ArrayList<>();
    ArrayList<ProductImagesData> list = new ArrayList<>();
    MyViewHolderAMAAdapter adapter;
    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;
    private ArrayAdapter<Mention> defaultMentionAdapter;

    public static String commentsCount;
    ProgressBar progressmsg;
    public static String sizeofArray = "0";
    String lastmentionedPersons = "";

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
            setContentView(R.layout.activity_questions_details);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            commentsRecycler = findViewById(R.id.commentsRecycler);
            recyclerView = findViewById(R.id.rv_items_ama);
            profileImage = findViewById(R.id.profileImage);
            name = findViewById(R.id.name);
            city = findViewById(R.id.city);
            time = findViewById(R.id.time);
            description = findViewById(R.id.description);
            showing_commentscunt = findViewById(R.id.showing_commentscunt);
            comentCount = findViewById(R.id.comentCount);
            previousComentsLayout = findViewById(R.id.previousComentsLayout);
            share_btn = findViewById(R.id.share_btn);
            postCommentText = findViewById(R.id.postCommentText);
            postCommentbtn = findViewById(R.id.postCommentbtn);
            nestedscrollview = findViewById(R.id.nestedscrollview);
            more = findViewById(R.id.more);
            progressmsg = findViewById(R.id.post_msg_loader);

            productDetailsApiCall();
            Actions();
            getFollowersList();
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    private void getFollowersList(){
        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        Log.e("IDs", sharedpreferences.getString("UserID", " ") + "  " + sharedpreferences.getString("OtherUserID", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getFollowers + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&Type=user", QuestionsDetailsActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserFollowersResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            defaultMentionAdapter = new MentionArrayAdapter<>(QuestionsDetailsActivity.this);

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
                            Toast.makeText(QuestionsDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void productDetailsApiCall() {

        CustomLoader.showDialog(QuestionsDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("QuestionID", sharedpreferences.getString("QuestionID", " "));
        body.put("Type", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.QUESTION_DETAILS, QuestionsDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("QuestionResult", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

//                            product_detail_swipe_reload.setRefreshing(false);

                            JSONObject user_info = jsonObject.getJSONObject("questions");
                            String QuestionID = user_info.getString("QuestionID");
                            String UserID = user_info.getString("UserID");
                            String CategoryID = user_info.getString("CategoryID");
                            String QuestionDescription = user_info.getString("QuestionDescription");
                            String QuestionAskedAt = user_info.getString("QuestionAskedAt");
                            String UserImage = user_info.getString("UserImage");
                            String UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            String UserCityName = user_info.getString("UserCityName");
                            String SubCategoryName = user_info.getString("SubCategoryName");
                            String CategoryName = user_info.getString("CategoryName");
                            String ItemType = user_info.getString("ItemType");
                            commentsCount = user_info.getString("CommentCount");

                            GetCommentsApi();

                            ArrayList<ProductImagesData> imagesData = new ArrayList<>();
                            JSONArray productImages = user_info.getJSONArray("QuestionImages");
                            for (int j = 0; j < productImages.length(); j++) {
                                JSONObject imagesObj = productImages.getJSONObject(j);
                                String ProductCompressedImage = imagesObj.getString("CompressedImage");
                                String ProductImage = imagesObj.getString("Image");

                                imagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                            }

                            profileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(QuestionsDetailsActivity.this, OthersProfileActivity.class);
                                    mEditor.putString("Booth", "Other").apply();
                                    intent.putExtra("OtherUserID", UserID);
                                    startActivity(intent);
                                }
                            });
                            name.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(QuestionsDetailsActivity.this, OthersProfileActivity.class);
                                    mEditor.putString("Booth", "Other").apply();
                                    intent.putExtra("OtherUserID", UserID);
                                    startActivity(intent);
                                }
                            });

                            Picasso.get().load(Constants.URL.IMG_URL + UserImage).placeholder(R.drawable.user).into(profileImage);

                            name.setText("@" + UserName);
                            city.setText(UserCityName);
                            time.setText(TimeAgo.getTimeAgo(Long.parseLong(QuestionAskedAt)));
                            description.setText(QuestionDescription);
                            comentCount.setText("(" + commentsCount + ")");

                            if (Integer.parseInt(commentsCount) > 20) {
                                previousComentsLayout.setVisibility(View.VISIBLE);
                            } else {
                                previousComentsLayout.setVisibility(View.GONE);
                            }


                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(QuestionsDetailsActivity.this, RecyclerView.HORIZONTAL, false);
                            recyclerView.setLayoutManager(layoutManager);
                            adapter = new MyViewHolderAMAAdapter(QuestionsDetailsActivity.this, imagesData, new CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            recyclerView.setAdapter(adapter);


                            more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (UserID.equals(sharedpreferences.getString("UserID", ""))) {
                                        final PopupMenu projMangMore = new PopupMenu(QuestionsDetailsActivity.this, view);
                                        projMangMore.getMenuInflater().inflate(R.menu.deletemenu, projMangMore.getMenu());

                                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.delete:
                                                        final Dialog deletedialog = new Dialog(QuestionsDetailsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                                        deletedialog.setContentView(R.layout.delete_cart_item_dialog);
                                                        Button yesBtn = deletedialog.findViewById(R.id.btn_yes);
                                                        Button noBtn = deletedialog.findViewById(R.id.btn_no);

                                                        deletedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        deletedialog.show();

                                                        noBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                deletedialog.dismiss();
                                                            }
                                                        });

                                                        yesBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                deleteQuestionApiCall();
                                                                deletedialog.dismiss();
                                                            }
                                                        });

                                                        break;

                                                }
                                                return false;
                                            }
                                        });
                                        projMangMore.show();
                                    } else {
                                        final PopupMenu projMangMore = new PopupMenu(QuestionsDetailsActivity.this, view);
                                        projMangMore.getMenuInflater().inflate(R.menu.home_more_otheruser_menu, projMangMore.getMenu());

                                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                            @Override
                                            public boolean onMenuItemClick(MenuItem menuItem) {
                                                switch (menuItem.getItemId()) {
                                                    case R.id.report:
                                                        final Dialog reportdialog = new Dialog(QuestionsDetailsActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                                        reportdialog.setContentView(R.layout.report_dialog);
                                                        Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                                                        Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);

                                                        reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                        reportdialog.show();

                                                        spamBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                reportApi(sharedpreferences.getString("QuestionID", ""), spamBtn.getText().toString());
                                                                reportdialog.dismiss();
                                                            }
                                                        });

                                                        inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                reportApi(sharedpreferences.getString("QuestionID", ""), inappropriateBtn.getText().toString());
                                                                reportdialog.dismiss();
                                                            }
                                                        });
                                                        break;
                                                }
                                                return false;
                                            }
                                        });
                                        projMangMore.show();
                                    }


                                }
                            });




                        } else {
                            Toast.makeText(QuestionsDetailsActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Actions() {

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Download App", "https://play.google.com/store/apps/details?id=com.schopfen.Booth");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(QuestionsDetailsActivity.this, "Link copied to ClipBoard!", Toast.LENGTH_SHORT).show();
            }
        });

        previousComentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeofArray = String.valueOf(commentsArray.size());
                GetCommentsApi();
            }
        });

        postCommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postCommentText.getText().toString().isEmpty() || postCommentText.getText().toString().startsWith(" ")) {
                    Toast.makeText(QuestionsDetailsActivity.this, "Enter the comment first", Toast.LENGTH_SHORT).show();
                } else {
                    hideKeyboard(QuestionsDetailsActivity.this);
                    PostCommentsApi(postCommentText.getText().toString());
                }
            }
        });

    }

    private void reportApi(String questionID, String reason) {

        CustomLoader.showDialog((Activity) QuestionsDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", questionID);
        body.put("ReportReason", reason);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
//        body.put("DeviceType", "Android");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.REPORTQUESTION, QuestionsDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();

                if (ERROR.isEmpty()) {
                    Log.e("reportQResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(QuestionsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void deleteQuestionApiCall() {
        CustomLoader.showDialog((Activity) QuestionsDetailsActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", sharedpreferences.getString("QuestionID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEQUESTION, QuestionsDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("deleteQResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(QuestionsDetailsActivity.this, message, Toast.LENGTH_LONG).show();
                            Home_Home_Fragment.refresh = true;
                            finish();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void GetCommentsApi() {
        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", sharedpreferences.getString("QuestionID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Start", sizeofArray);
        body.put("BlockedUserType", sharedpreferences.getString("LastState", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETQUESTIONCOMMENTS, QuestionsDetailsActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("questioncomments", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

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
                                String QuestionCommentID = obj.getString("QuestionCommentID");
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

                                commentsArray.add(new CommentsModel(QuestionCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));
                            }

//                            Collections.reverse(commentsArray);



                            if (commentsArray.size() == Integer.parseInt(commentsCount)) {
                                previousComentsLayout.setVisibility(View.GONE);
                            }

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(sharedpreferences.getString("QuestionID", " "), "question",QuestionsDetailsActivity.this, commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            RecyclerView.LayoutManager layoutManagerone = new LinearLayoutManager(QuestionsDetailsActivity.this, RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManagerone);
//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(QuestionsDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
                            commentsAdapter.notifyDataSetChanged();


                        } else {


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        body.put("QuestionID", sharedpreferences.getString("QuestionID", " "));
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Comment", postCommentText.getText().toString());
        body.put("CommentedAs", sharedpreferences.getString("LastState", ""));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("MentionedUsers", lastmentionedPersons);

        lastmentionedPersons = "";

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.QUESTIONCOMMENT, QuestionsDetailsActivity.this, body, header, new ServerCallback() {
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
                            String QuestionCommentID = data.getString("QuestionCommentID");
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

                            commentsArray.add(0, new CommentsModel(QuestionCommentID, BoothCompressedImage, CommentedAs, userID, Comment, CreatedAt, CompressedImage, UserName, CityTitle, mentionedUsersInfos, BoothUserName));

//                            Collections.reverse(commentsArray);

                            showing_commentscunt.setText("(" + String.valueOf(commentsArray.size()) + ")");

                            commentsCount = String.valueOf(Integer.parseInt(commentsCount) + 1);

                            comentCount.setText("(" + commentsCount + ")");

                            Answers_Details_Adapter commentsAdapter = new Answers_Details_Adapter(sharedpreferences.getString("QuestionID", " "), "question",QuestionsDetailsActivity.this, commentsArray, new com.schopfen.Booth.interfaces.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });
                            RecyclerView.LayoutManager layoutManagerone = new LinearLayoutManager(QuestionsDetailsActivity.this, RecyclerView.VERTICAL, false);
                            commentsRecycler.setLayoutManager(layoutManagerone);
//                            commentsRecycler.addItemDecoration(new DividerItemDecoration(QuestionsDetailsActivity.this, DividerItemDecoration.VERTICAL));
                            commentsRecycler.setAdapter(commentsAdapter);
                            nestedscrollview.fullScroll(View.FOCUS_UP);

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
                    Toast.makeText(QuestionsDetailsActivity.this, ERROR, Toast.LENGTH_SHORT).show();
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
