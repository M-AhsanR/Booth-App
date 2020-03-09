package com.schopfen.Booth.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.schopfen.Booth.Adapters.Chat_Adapter;
import com.schopfen.Booth.Adapters.WishlistAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.Chat_Data;
import com.schopfen.Booth.Models.MainChatData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.WishListEtcData;
import com.schopfen.Booth.MyChatService;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.schopfen.Booth.videoTrimmer.VideoTrimmerActivity;
import com.schopfen.Booth.videoTrimmer.utils.FileUtils;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import com.schopfen.Booth.ApiStructure.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.fxn.pix.Pix.start;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<Chat_Data> startChat;
    ArrayList<Chat_Data> sample;
    ArrayList<MainChatData> chatData = new ArrayList<>();
    Chat_Data data;
    public static final String MyPREFERENCES = "MyPrefs";
    Map<String, String> imgbody = new HashMap<String, String>();
    RecyclerView msgRecycler;
    ImageView attatchement, postMessageBtn;
    EditText messageTextView;
    TextView chat_username;
    ArrayList<String> returnValue = new ArrayList<>();
    Uri myUri;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    Bitmap bitmap;
    MultipartEntity entity;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    String otheruserid;
    String userType;
    String otherUserType;
    String ChatID = " ";
    String ts;
    Chat_Adapter adapter;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";
    public static Uri videoURI = null;
    ByteArrayOutputStream byteBuffer;
    String videofilePath;
    String videoBase64Data = "";
    String thumbBase64 = "";
    String Type;
    long totalSize = 0;
    Dialog progress;
    ProgressBar circularProgressbar;
    TextView tv;
    ProgressBar mProgress;

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(myReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable()){
            if (videoURI != null) {
                Uri selectedVideoUri = videoURI;
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoURI.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                bitmap = thumb;
                saveToInternalStorage(thumb);
                ByteArrayOutputStream thumb_imageOS = new ByteArrayOutputStream();
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, thumb_imageOS); // Original
                }
                byte[] thumb_imageData = thumb_imageOS.toByteArray();
                thumbBase64 = Base64.encodeToString(thumb_imageData, Base64.NO_WRAP);
                // Setting the thumbnail of the video in to the image view
                InputStream inputStream = null;
                // Converting the video in to the bytes
                try {
                    inputStream = ChatActivity.this.getContentResolver().openInputStream(Uri.parse("file://" + selectedVideoUri));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                byteBuffer = new ByteArrayOutputStream();
                int len = 0;
                try {
                    while ((len = inputStream.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Converting bytes into base64
                videoBase64Data = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
                Log.d("VideoBase64", videoBase64Data);
                new UploadFileToServerVideo().execute();
            }
        }
    }
    // image saver from bitmap
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(ChatActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNetworkAvailable()){
            setContentView(R.layout.activity_chat);
            Intent intent = new Intent(ChatActivity.this, MyChatService.class);
            startService(intent);
            try {
                registerReceiver(myReceiver, new IntentFilter((MyChatService.INTENT_FILTER)));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            initilizeviews();
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                otheruserid = bundle.getString("OthersprofileID");
                userType = bundle.getString("usertype");
                otherUserType = bundle.getString("othertype");
            }
            startChatApiCall();
            msgRecycler.scrollToPosition(chatData.size() - 1);
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }
    @Override
    public void onBackPressed() {
        deleteChatRoomApiCall();
        finish();
    }
    private void startChatApiCall() {
        CustomLoader.showDialog(ChatActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ReceiverID", otheruserid);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("Type", userType);
        body.put("ReceiverType", otherUserType);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.STARTCHAT, ChatActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("ChatResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            chatData.clear();
                            ChatID = jsonObject.getString("ChatID");
                            JSONArray messagesArray = jsonObject.getJSONArray("ChatMessages");
                            for (int i = 0; i < messagesArray.length(); i++) {
                                JSONObject messagesMainObject = messagesArray.getJSONObject(i);
                                String Date = messagesMainObject.getString("Date");
                                JSONArray Messages = messagesMainObject.getJSONArray("Messages");
                                startChat = new ArrayList<>();
                                for (int j = 0; j < Messages.length(); j++) {
                                    JSONObject messages = Messages.getJSONObject(j);
                                    String ChatMessageID = messages.getString("ChatMessageID");
                                    String ChatID = messages.getString("ChatID");
                                    String SenderID = messages.getString("SenderID");
                                    String ReceiverID = messages.getString("ReceiverID");
                                    String UserType = messages.getString("UserType");
                                    String Message = messages.getString("Message");
                                    String Image = messages.getString("Image");
                                    String CompressedImage = messages.getString("CompressedImage");
                                    String VideoThumbnail = messages.getString("VideoThumbnail");
                                    String Video = messages.getString("Video");
                                    String IsReadBySender = messages.getString("IsReadBySender");
                                    String IsReadByReceiver = messages.getString("IsReadByReceiver");
                                    String CreatedAt = messages.getString("CreatedAt");
                                    startChat.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                                            Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                                }
                                Collections.reverse(startChat);
                                chatData.add(new MainChatData(Date, startChat));
                            }
                            Collections.reverse(chatData);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
                            linearLayoutManager.setReverseLayout(true);
                            msgRecycler.setLayoutManager(linearLayoutManager);
                            adapter = new Chat_Adapter(ChatActivity.this, chatData, new Chat_Adapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int position) {
                                }
                            });
                            msgRecycler.setAdapter(adapter);
                            AllReadMessages(ChatID);
                        } else {
                            Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(ChatActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void AllReadMessages(String ChatID) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("ChatID", ChatID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.markAllMsgReadForChat, ChatActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("ReadMessages", result);
                } else {
                    Toast.makeText(ChatActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteChatRoomApiCall() {
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.DELETECHATROOM + sharedpreferences.getString("UserID", " ") + "&ChatID=" + ChatID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, ChatActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("ChatDelResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            ChatID = jsonObject.getString("ChatID");
                            JSONObject user_info = jsonObject.getJSONObject("ChatMessages");
                        } else {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void sendMessageChatApiCall() {
        Long tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();
        CustomLoader.showDialog(ChatActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("ChatID", ChatID);
        if (sharedpreferences.getString("LastState", "").equals("user")) {
            body.put("SenderUserName", sharedpreferences.getString("UserName", ""));
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            body.put("SenderUserName", sharedpreferences.getString("BoothUserName", ""));
        }
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ReceiverID", otheruserid);
            body.put("UserType", otherUserType);
        body.put("Message", messageTextView.getText().toString());
        body.put("CreatedAt", ts);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("body", body.toString());
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDMESSAGE, ChatActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("ChatSendmessageResp", result);
                    try {
                        sample = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            messageTextView.setText("");
                            JSONObject message = jsonObject.getJSONObject("message");
                            String Date = message.getString("Date");
                            JSONObject messages = message.getJSONObject("Message");
                            String ChatMessageID = messages.getString("ChatMessageID");
                            String ChatID = messages.getString("ChatID");
                            String SenderID = messages.getString("SenderID");
                            String ReceiverID = messages.getString("ReceiverID");
                            String UserType = messages.getString("UserType");
                            String Message = messages.getString("Message");
                            String Image = messages.getString("Image");
                            String CompressedImage = messages.getString("CompressedImage");
                            String VideoThumbnail = messages.getString("VideoThumbnail");
                            String Video = messages.getString("Video");
                            String IsReadBySender = messages.getString("IsReadBySender");
                            String IsReadByReceiver = messages.getString("IsReadByReceiver");
                            String CreatedAt = messages.getString("CreatedAt");
                            sample.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                                    Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                            if (chatData.isEmpty()) {
                                startChatApiCall();
                            } else {
                                adapter.addMessage(sample);
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Log.e("ChatSendmessageResp", ERROR);
                    Toast.makeText(ChatActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendImageApiCall() {
        Long tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();
        CustomLoader.showDialog(ChatActivity.this);
        imgToStringFunction(bitmap);
        imgbody.put("ChatID", ChatID);
        if (sharedpreferences.getString("LastState", "").equals("user")) {
            imgbody.put("SenderUserName", sharedpreferences.getString("UserName", ""));
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            imgbody.put("SenderUserName", sharedpreferences.getString("BoothUserName", ""));
        }
        imgbody.put("UserID", sharedpreferences.getString("UserID", " "));
        imgbody.put("ReceiverID", otheruserid);
            imgbody.put("UserType", userType);
        imgbody.put("Message", "");
        imgbody.put("CreatedAt", ts);
        imgbody.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("body", imgbody.toString());
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDMESSAGE, ChatActivity.this, imgbody, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("ChatSendmessageResp", result);
                    try {
                        sample = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            messageTextView.setText("");
                            JSONObject message = jsonObject.getJSONObject("message");
                            String Date = message.getString("Date");
                            JSONObject messages = message.getJSONObject("Message");
                            String ChatMessageID = messages.getString("ChatMessageID");
                            String ChatID = messages.getString("ChatID");
                            String SenderID = messages.getString("SenderID");
                            String ReceiverID = messages.getString("ReceiverID");
                            String UserType = messages.getString("UserType");
                            String Message = messages.getString("Message");
                            String Image = messages.getString("Image");
                            String CompressedImage = messages.getString("CompressedImage");
                            String VideoThumbnail = messages.getString("VideoThumbnail");
                            String Video = messages.getString("Video");
                            String IsReadBySender = messages.getString("IsReadBySender");
                            String IsReadByReceiver = messages.getString("IsReadByReceiver");
                            String CreatedAt = messages.getString("CreatedAt");
                            sample.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                                    Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                            if (chatData.isEmpty()) {
                                startChatApiCall();
                            } else {
                                adapter.addMessage(sample);
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Log.e("ChatSendmessageResp", ERROR);
                    Toast.makeText(ChatActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class UploadFileToServerImage extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progress = new Dialog(ChatActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            progress.setContentView(R.layout.progress_dialog_circle);
            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
            tv = progress.findViewById(R.id.tv);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.circular);
            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
            mProgress.setProgress(0);   // Main Progress
            mProgress.setSecondaryProgress(100); // Secondary Progress
            mProgress.setMax(100); // Maximum Progress
            mProgress.setProgressDrawable(drawable);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            progress.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            // updating progress bar value
            mProgress.setProgress(progress[0]);
            // updating percentage value
            tv.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.SENDMESSAGE);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Long tsLong = System.currentTimeMillis() / 1000;
                ts = tsLong.toString();
                if (uriArrayList.size() > 0){
                    for (int i = 0; i < uriArrayList.size(); i++){
                        File sourceFile = new File(uriArrayList.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("Image", new FileBody(sourceFile));
                    }
                }
                entity.addPart("ChatID", new StringBody(ChatID));
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    entity.addPart("SenderUserName", new StringBody(sharedpreferences.getString("UserName", "")));
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    entity.addPart("SenderUserName", new StringBody(sharedpreferences.getString("BoothUserName", "")));
                }
                entity.addPart("UserID", new StringBody(sharedpreferences.getString("UserID", " ")));
                entity.addPart("ReceiverID", new StringBody(otheruserid));
                    entity.addPart("UserType", new StringBody(userType));
                entity.addPart("Message", new StringBody(""));
                entity.addPart("CreatedAt", new StringBody(ts));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));
                // Extra parameters if you want to pass to server
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                httppost.setHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e("ResponseResult", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);
            try {
                sample = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                int status = jsonObject.getInt("status");
                if (status == 200) {
                    progress.dismiss();
                    uriArrayList.clear();
                    messageTextView.setText("");
                    JSONObject message = jsonObject.getJSONObject("message");
                    String Date = message.getString("Date");
                    JSONObject messages = message.getJSONObject("Message");
                    String ChatMessageID = messages.getString("ChatMessageID");
                    String ChatID = messages.getString("ChatID");
                    String SenderID = messages.getString("SenderID");
                    String ReceiverID = messages.getString("ReceiverID");
                    String UserType = messages.getString("UserType");
                    String Message = messages.getString("Message");
                    String Image = messages.getString("Image");
                    String CompressedImage = messages.getString("CompressedImage");
                    String VideoThumbnail = messages.getString("VideoThumbnail");
                    String Video = messages.getString("Video");
                    String IsReadBySender = messages.getString("IsReadBySender");
                    String IsReadByReceiver = messages.getString("IsReadByReceiver");
                    String CreatedAt = messages.getString("CreatedAt");
                    sample.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                            Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                    if (chatData.isEmpty()) {
                        startChatApiCall();
                    } else {
                        adapter.addMessage(sample);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
        }
    }

    private String saveToInternalStorageImage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(ChatActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        uriArrayList.clear();
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayList.add(myUri);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private class UploadFileToServerVideo extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progress = new Dialog(ChatActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            progress.setContentView(R.layout.progress_dialog_circle);
            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
            tv = progress.findViewById(R.id.tv);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.circular);
            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
            mProgress.setProgress(0);   // Main Progress
            mProgress.setSecondaryProgress(100); // Secondary Progress
            mProgress.setMax(100); // Maximum Progress
            mProgress.setProgressDrawable(drawable);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            progress.show();
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            // updating progress bar value
            mProgress.setProgress(progress[0]);
            // updating percentage value
            tv.setText(String.valueOf(progress[0]) + "%");
        }
        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }
        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.SENDMESSAGE);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                Long tsLong = System.currentTimeMillis() / 1000;
                ts = tsLong.toString();
                if (videoURI != null){
                    entity.addPart("VideoThumbnail", new FileBody(new File(myUri.getPath())));
                    entity.addPart("Video", new FileBody(new File(videoURI.getPath())));
                }
                entity.addPart("ChatID", new StringBody(ChatID));
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    entity.addPart("SenderUserName", new StringBody(sharedpreferences.getString("UserName", "")));
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    entity.addPart("SenderUserName", new StringBody(sharedpreferences.getString("BoothUserName", "")));
                }
                entity.addPart("UserID", new StringBody(sharedpreferences.getString("UserID", " ")));
                entity.addPart("ReceiverID", new StringBody(otheruserid));
                    entity.addPart("UserType", new StringBody(userType));
                entity.addPart("Message", new StringBody(""));
                entity.addPart("CreatedAt", new StringBody(ts));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                httppost.setHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e("ResponseResult", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                if (status == 200){
                    sample = new ArrayList<>();
                    progress.dismiss();
                    videoURI = null;
                    messageTextView.setText("");
                    JSONObject message = jsonObject.getJSONObject("message");
                    String Date = message.getString("Date");
                    JSONObject messages = message.getJSONObject("Message");
                    String ChatMessageID = messages.getString("ChatMessageID");
                    String ChatID = messages.getString("ChatID");
                    String SenderID = messages.getString("SenderID");
                    String ReceiverID = messages.getString("ReceiverID");
                    String UserType = messages.getString("UserType");
                    String Message = messages.getString("Message");
                    String Image = messages.getString("Image");
                    String CompressedImage = messages.getString("CompressedImage");
                    String VideoThumbnail = messages.getString("VideoThumbnail");
                    String Video = messages.getString("Video");
                    String IsReadBySender = messages.getString("IsReadBySender");
                    String IsReadByReceiver = messages.getString("IsReadByReceiver");
                    String CreatedAt = messages.getString("CreatedAt");
                    sample.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                            Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                    if (chatData.isEmpty()) {
                        startChatApiCall();
                    } else {
                        adapter.addMessage(sample);
                    }
                }else {
                    progress.dismiss();
                    Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
        }
    }
    private void sendVideoApiCall(String thumb, String video) {
        Long tsLong = System.currentTimeMillis() / 1000;
        ts = tsLong.toString();
        CustomLoader.showDialog(ChatActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        body.put("VideoThumbnail", thumb);
        body.put("Video", video);
        body.put("ChatID", ChatID);
        if (sharedpreferences.getString("LastState", "").equals("user")) {
            body.put("SenderUserName", sharedpreferences.getString("UserName", ""));
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            body.put("SenderUserName", sharedpreferences.getString("BoothUserName", ""));
        }
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ReceiverID", otheruserid);
            body.put("UserType", userType);
        body.put("Message", "");
        body.put("CreatedAt", ts);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDMESSAGE, ChatActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("ChatSendmessageResp", result);
                    try {
                        sample = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            messageTextView.setText("");
                            JSONObject message = jsonObject.getJSONObject("message");
                            String Date = message.getString("Date");
                            JSONObject messages = message.getJSONObject("Message");
                            String ChatMessageID = messages.getString("ChatMessageID");
                            String ChatID = messages.getString("ChatID");
                            String SenderID = messages.getString("SenderID");
                            String ReceiverID = messages.getString("ReceiverID");
                            String UserType = messages.getString("UserType");
                            String Message = messages.getString("Message");
                            String Image = messages.getString("Image");
                            String CompressedImage = messages.getString("CompressedImage");
                            String VideoThumbnail = messages.getString("VideoThumbnail");
                            String Video = messages.getString("Video");
                            String IsReadBySender = messages.getString("IsReadBySender");
                            String IsReadByReceiver = messages.getString("IsReadByReceiver");
                            String CreatedAt = messages.getString("CreatedAt");
                            sample.add(new Chat_Data(ChatMessageID, ChatID, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                                    Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                            if (chatData.isEmpty()) {
                                startChatApiCall();
                            } else {
                                adapter.addMessage(sample);
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Log.e("ChatSendmessageResp", ERROR);
                    Toast.makeText(ChatActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initilizeviews() {
        msgRecycler = findViewById(R.id.messages_recycler);
        postMessageBtn = findViewById(R.id.postMessagebtn);
        attatchement = findViewById(R.id.post_attatchment);
        messageTextView = findViewById(R.id.postMessageText);
        chat_username = findViewById(R.id.chat_username);
        postMessageBtn.setOnClickListener(this);
        attatchement.setOnClickListener(this);
        String user_name = getIntent().getStringExtra("UserName");
        chat_username.setText("@" + user_name);
    }
    @Override
    public void onClick(View view) {
        if (view == postMessageBtn) {
            if (messageTextView.getText().toString().isEmpty()) {
                Toast.makeText(ChatActivity.this, "Type a message to proceed", Toast.LENGTH_SHORT).show();
            } else {
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(this.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                sendMessageChatApiCall();
            }
        } else if (view == attatchement) {
            AttatchmentDialog();
        }
    }
    private void AttatchmentDialog() {
        final Dialog alertdialog = new Dialog(ChatActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        alertdialog.setContentView(R.layout.video_pic_dialog);
        LinearLayout chooseImage = alertdialog.findViewById(R.id.choose_image);
        LinearLayout chooseVideo = alertdialog.findViewById(R.id.choose_video);
        alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertdialog.setCancelable(true);
        alertdialog.show();
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertdialog.dismiss();
                Log.e("cameraClick", "yes");
                returnValue.clear();
                start(ChatActivity.this,         //Activity or Fragment Instance
                        1234,                //Request code for activity results
                        1);                 //Number of images to restict selection count
            }
        });
        chooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertdialog.dismiss();
                pickFromGallery();
            }
        });
    }
    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                Intent intent = new Intent();
                intent.setTypeAndNormalize("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
            }
        } else {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
            }
        }
    }
    // For Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (int a = 0; a < returnValue.size(); a++) {
                File imgFile = new File(returnValue.get(a));
                myUri = Uri.fromFile(new File(returnValue.get(a)));
                if (imgFile.exists()) {
                    Bitmap myBitmap = getThumbnailBitmap(imgFile.getAbsolutePath(), 50);
                }
                beginCrop(myUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_TRIMMER) {
            final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                Toast.makeText(ChatActivity.this, "Cannot retrieve selected video", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(ChatActivity.this, VideoTrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(ChatActivity.this, uri));
        intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        intent.putExtra("Activity", "ChatActivity");
        startActivity(intent);
    }
    private int getMediaDuration(Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(ChatActivity.this, uriOfFile);
        int duration = mp.getDuration();
        return duration;
    }
    private Bitmap getThumbnailBitmap(final String path, final int thumbnailSize) {
        Bitmap bitmap;
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1)) {
            bitmap = null;
        }
        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / thumbnailSize;
        bitmap = BitmapFactory.decodeFile(path, opts);
        return bitmap;
    }
    // For Camera permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(ChatActivity.this, 1234, 1);
                } else {
                    Toast.makeText(ChatActivity.this, "Approve permissions to open Camera", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    // Image Croper
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(ChatActivity.this);
    }
    // Image Croper Handling
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
                bitmapArrayList.add(bitmap);
                saveToInternalStorageImage(bitmap);
                Log.e("chekingBit", bitmap.toString());
                new UploadFileToServerImage().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    // Image to string converter
    private void imgToStringFunction(Bitmap bitmap) {
        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] original_data = original_image.toByteArray(); // Original
                imgbody.put("Image", Base64.encodeToString(original_data, Base64.DEFAULT));
            }
        }
    }
    BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int a = 0;
            Log.e("intentchecking", intent.getExtras().getString("message"));
            try {
                sample = new ArrayList<>();
                JSONObject jsonObject = new JSONObject(intent.getExtras().getString("message"));
                String Date = jsonObject.getString("Date");
                JSONObject messages = jsonObject.getJSONObject("Message");
                String ChatMessageID = messages.getString("ChatMessageID");
                String ChatID1 = messages.getString("ChatID");
                String SenderID = messages.getString("SenderID");
                String ReceiverID = messages.getString("ReceiverID");
                String UserType = messages.getString("UserType");
                String Message = messages.getString("Message");
                String Image = messages.getString("Image");
                String CompressedImage = messages.getString("CompressedImage");
                String VideoThumbnail = messages.getString("VideoThumbnail");
                String Video = messages.getString("Video");
                String IsReadBySender = messages.getString("IsReadBySender");
                String IsReadByReceiver = messages.getString("IsReadByReceiver");
                String CreatedAt = messages.getString("CreatedAt");
                if (ChatID1 != null) {
                    if (ChatID1.equals(ChatID)) {
                        if (!SenderID.equals(sharedpreferences.getString("UserID", ""))) {
                            sample.add(new Chat_Data(ChatMessageID, ChatID1, SenderID, ReceiverID, UserType, Message, Image, CompressedImage, VideoThumbnail,
                                    Video, IsReadBySender, IsReadByReceiver, CreatedAt));
                            if (chatData.isEmpty()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startChatApiCall();
                                        // Stuff that updates the UI
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.addMessage(sample);
                                        // Stuff that updates the UI
                                    }
                                });
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}