package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Adapters.HomeHomePagerAdapter;
import com.schopfen.Booth.Adapters.InboxAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.InboxModel;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class InboxActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RecyclerView inbox_recycler;
    SwipeRefreshLayout swipe_inbox;
    TextView inbox_size_text, nochats_text;
    ArrayList<InboxModel> inboxModelArrayList = new ArrayList<>();

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
            Intent intent = new Intent(InboxActivity.this, MyChatService.class);
//        i.putExtra("KEY1", "Value to be used by the service");
            startService(intent);

            setContentView(R.layout.activity_inbox);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            inbox_recycler = findViewById(R.id.inbox_recycler);
            swipe_inbox = findViewById(R.id.swipe_inbox);
            inbox_size_text = findViewById(R.id.inbox_size_text);
            nochats_text = findViewById(R.id.nochats_text);

            InboxApi();

            swipe_inbox.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    InboxApi();
                }
            });
        }else {
            setContentView(R.layout.activity_inbox);
        }
    }

    private void InboxApi(){
        CustomLoader.showDialog(InboxActivity.this);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("Type", sharedpreferences.getString("LastState", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.INBOX, InboxActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Inbox", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            swipe_inbox.setRefreshing(false);
                            inboxModelArrayList.clear();
                            JSONArray ChatRooms = jsonObject.getJSONArray("ChatRooms");

                            for (int i = 0; i < ChatRooms.length(); i++){
                                JSONObject jsonObject1 = ChatRooms.getJSONObject(i);

                                String ChatID = jsonObject1.getString("ChatID");
                                String ConversationSenderID = jsonObject1.getString("ConversationSenderID");
                                String ConversationSenderName = jsonObject1.getString("ConversationSenderName");
                                String ConversationSenderUserName = jsonObject1.getString("ConversationSenderUserName");
                                String ConversationSenderImage = jsonObject1.getString("ConversationSenderImage");
                                String ConversationReceiverID = jsonObject1.getString("ConversationReceiverID");
                                String ConversationReceiverName = jsonObject1.getString("ConversationReceiverName");
                                String ConversationReceiverUserName = jsonObject1.getString("ConversationReceiverUserName");
                                String ConversationReceiverImage = jsonObject1.getString("ConversationReceiverImage");
                                String Type = jsonObject1.getString("Type");
                                String ReceiverType = jsonObject1.getString("ReceiverType");
                                String ChatMessageID = jsonObject1.getString("ChatMessageID");
                                String SenderID = jsonObject1.getString("SenderID");
                                String ReceiverID = jsonObject1.getString("ReceiverID");
                                String IsReadBySender = jsonObject1.getString("IsReadBySender");
                                String IsReadByReceiver = jsonObject1.getString("IsReadByReceiver");
                                String Image = jsonObject1.getString("Image");
                                String CompressedImage = jsonObject1.getString("CompressedImage");
                                String Message = jsonObject1.getString("Message");
                                String CreatedAt = jsonObject1.getString("CreatedAt");
                                String HasUnreadMessage = jsonObject1.getString("HasUnreadMessage");
                                String UnreadMessageCount = jsonObject1.getString("UnreadMessageCount");

                                inboxModelArrayList.add(new InboxModel(ChatID, ConversationSenderID, ConversationSenderName, ConversationSenderUserName,
                                        ConversationSenderImage, ConversationReceiverID,
                                        ConversationReceiverName, ConversationReceiverUserName, ConversationReceiverImage,
                                        Type, ReceiverType, ChatMessageID, SenderID, ReceiverID,
                                        IsReadBySender, IsReadByReceiver, Image, CompressedImage, Message, CreatedAt, HasUnreadMessage,
                                        UnreadMessageCount));
                            }

                            if (inboxModelArrayList.isEmpty()){
                                nochats_text.setVisibility(View.VISIBLE);
                                inbox_recycler.setVisibility(View.GONE);
                            }else {
                                nochats_text.setVisibility(View.GONE);
                                inbox_recycler.setVisibility(View.VISIBLE);
                            }

                            inbox_size_text.setText("(" + String.valueOf(inboxModelArrayList.size()) + ")");

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(InboxActivity.this);
                            inbox_recycler.setLayoutManager(linearLayoutManager);
                            InboxAdapter inboxAdapter = new InboxAdapter(InboxActivity.this, inboxModelArrayList, new InboxAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {

                                }
                            });

                            inbox_recycler.setAdapter(inboxAdapter);


                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(InboxActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(InboxActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
