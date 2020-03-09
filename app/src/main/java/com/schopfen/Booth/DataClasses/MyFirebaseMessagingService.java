package com.schopfen.Booth.DataClasses;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pusher.client.channel.User;
import com.schopfen.Booth.Activities.BoothMainActivity;
import com.schopfen.Booth.Activities.MainActivity;
import com.schopfen.Booth.Models.MessageModel;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "NotiData";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    String title, message;
    Intent intent;
    String type;
    MessageModel messageModel;

    public static boolean fromNotification = false;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sharedpreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Notification Data: " + remoteMessage.getData());

        title = remoteMessage.getNotification().getTitle();
        message = remoteMessage.getNotification().getBody();
        String click_action = remoteMessage.getNotification().getClickAction();
        Log.e("ClickAction", click_action);


        sendNotification(remoteMessage);

    }

    private void sendNotification(RemoteMessage remote_message) {

//        String type = remote_message.getData().get("UserType");

        try {
            JSONObject data = new JSONObject(remote_message.getData().get("notification_data"));
            type = data.getString("UserType");
            if (type != null) {
                if (type.equals("user")) {
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("acitivity", "notification");
                } else if (type.equals("booth")) {
                    intent = new Intent(this, BoothMainActivity.class);
                    intent.putExtra("acitivity", "notification");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        switch (type) {
//
//            case "1": {
//
//                // 1 type for invite for supervision notification
//
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                intent=new Intent(this, AccountableAcceptRejectActivity.class);
//                intent.putExtra("FromNotification","true");
//
//                int count = Utilities.getInt(MyFirebaseMessagingService.this,"accept_reject_count");
//                count =  count + 1 ;
//
//                Utilities.saveInt(MyFirebaseMessagingService.this,"accept_reject_count",count);
//
//
//                break;
//            }
//            case "2": {
//
////            if accept invite or reject invite
//                String goal_id = "",action_id = "";
//
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//                    goal_id = jsonObject.getString("goal_id");
//                    action_id = jsonObject.getString("action_id");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
////                intent=new Intent(this, HomeActivity.class);
////                intent.putExtra("open_tab_number","");
////                intent.putExtra("goal_id",goal_id);
////                intent.putExtra("action_id",action_id);
//
////                fromNotification = true;
//
//
//                if(action_id.equals("")){
//
//                    intent = new Intent(this, GoalDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
////                    intent.putExtra("action_id",action_id);
////                    intent.putExtra("from_accountable","false");
//                    intent.putExtra("FromNotification","true");
//
//                }
//                else {
//
//                    intent = new Intent(this, ActionDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
//                    intent.putExtra("action_id", action_id);
//                    intent.putExtra("FromNotification","true");
//
//                }
//
//
//
//
//                break;
//            }
//            case "3": {
//                // type 3 is comment notification
//
//                String goal_id = "",action_id = "",name = "";
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//                    goal_id = jsonObject.getString("goal_id");
//                    action_id = jsonObject.getString("action_id");
//                    name = jsonObject.getString("name");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//
//                intent = new Intent(this, GoalCommentsActivity.class);
//                intent.putExtra("goal_id",goal_id);
//                intent.putExtra("action_id",action_id);
//                intent.putExtra("goal_name",name);
//                intent.putExtra("from_accountable","false");
//                intent.putExtra("comming_from","notification");
//
//
//
//
//                break;
//            }
//
//            case "4": {
//                // type 3 is comment notification
//
//                String goal_id = "",action_id = "";
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//                    goal_id = jsonObject.getString("goal_id");
//                    action_id = jsonObject.getString("action_id");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//
//                if(action_id.equals("")){
//
//                    intent = new Intent(this, GoalDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
////                    intent.putExtra("action_id",action_id);
////                    intent.putExtra("from_accountable","false");
//                    intent.putExtra("FromNotification","true");
//
//                }
//                else {
//
//                    intent = new Intent(this, ActionDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
//                    intent.putExtra("action_id", action_id);
//                    intent.putExtra("FromNotification","true");
//
//                }
//
//
//
//                break;
//            }
//            case "5": {
//                // type 5 is when user delete their accountable partner
//
//                String goal_id = "",action_id = "";
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//                    goal_id = jsonObject.getString("goal_id");
//                    action_id = jsonObject.getString("action_id");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                if(action_id.equals("")){
//
//                    intent = new Intent(this, AccountabeProjectDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
//                    intent.putExtra("action_id",action_id);
////                    intent.putExtra("from_accountable","false");
////                    intent.putExtra("comming_from","notification");
//
//                }
//                else {
//
//                    intent = new Intent(this, AccountableActionDetailActivity.class);
//                    intent.putExtra("goal_id",goal_id);
//                    intent.putExtra("action_id", action_id);
////                    intent.putExtra("goal_name", "");
//
//                }
//
//
//
//
//
//                break;
//            }
//            case "6": {
//                // type 6 for expire subscription
//
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(String.valueOf(data));
//
//                    title = jsonObject.getString("title");
//                    message = jsonObject.getString("message");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                intent = new Intent(this, HomeActivity.class);
//                intent.putExtra("open_tab_number", "");
//
//
//                break;
//            }
//
//        }


        String abc = "";


        // set badge count

//        SharedPreferences mPrefs2 = getSharedPreferences("Notification_badges_count", Context.MODE_PRIVATE);
//
//        int count = mPrefs2.getInt("count", 0);
//        count = count + 1;
//
//        SharedPreferences.Editor editor2 = mPrefs2.edit();
//        editor2.putInt("count", count);
//        editor2.apply();


        // set badge count


        int color = 0xff123456;
        color = getResources().getColor(R.color.white);
        color = ContextCompat.getColor(MyFirebaseMessagingService.this, R.color.white);


        // set response code new

        int code = sharedpreferences.getInt("code", 0);
        code = code + 1;

        if (code >= 100) {
            code = 0;
        }
        mEditor.putInt("code", code);
        mEditor.apply();


        // set response code new

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificatin_builder = new NotificationCompat.Builder(this);

        notificatin_builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setColor(color)


//                .setCustomContentView(normal_layout)


                .setPriority(android.app.Notification.PRIORITY_HIGH);

        NotificationManager notification_manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // for notification in oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notification_manager != null;
            notificatin_builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notification_manager.createNotificationChannel(notificationChannel);
        }
        assert notification_manager != null;

        // for notification in oreo

        notification_manager.notify(code, notificatin_builder.build());


//        int countt = Utilities.getInt(MyFirebaseMessagingService.this, "accept_reject_count");


        // app icon badge

//        if (countt > 0) {
//            ShortcutBadger.applyCount(MyFirebaseMessagingService.this, countt); //for 1.1.4+
//        }

        // app icon badge


    }
}