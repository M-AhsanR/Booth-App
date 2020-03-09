package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bachors.wordtospan.WordToSpan;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.ChatActivity;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.Activities.SummaryOrder;
import com.schopfen.Booth.Activities.TransactionHistory;
import com.schopfen.Booth.Activities.UserSummaryOrder;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.NotificationsData;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {

    ArrayList<NotificationsData> notificationsData;
    Context mContext;
    //    ArrayList<HomeCateDataModel> homeCateDataArray;
    CustomItemClickListener listener;
    CRLCallbacks callbacks;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public NotificationsAdapter(Context mContext, ArrayList<NotificationsData> notificationsData, CRLCallbacks callbacks) {
        this.mContext = mContext;
        this.notificationsData = notificationsData;
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view1, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (notificationsData.get(position).getIsRead().equals("0")){
            holder.notificationText.setTypeface(null, Typeface.BOLD);
        }else if (notificationsData.get(position).getIsRead().equals("1")){
            holder.notificationText.setTypeface(null, Typeface.NORMAL);
        }

//        holder.userName.setText("@" + notificationsData.get(position).getUserName());
//        holder.notificationText.setText(notificationsData.get(position).getNotificationText());
        WordToSpan link = new WordToSpan();
        link.setColorMENTION(mContext.getResources().getColor(R.color.orange))
//                .setColorURL(Color.MAGENTA)
//                .setColorPHONE(Color.RED)
//                .setColorMAIL(getResources().getColor(R.color.colorPrimary))
//                .setColorTAG(context.getResources().getColor(R.color.orange))
                .setUnderlineURL(true)
                .setLink(notificationsData.get(position).getNotificationText())
                .into(holder.notificationText)
                .setClickListener(new WordToSpan.ClickListener() {
                    @Override
                    public void onClick(String type, String text) {
                        // type: "tag", "mail", "url", "phone", "mention" or "custom"
//                        Toast.makeText(context, "Type: " + type + "\nText: " + text, Toast.LENGTH_LONG).show();
                        MentionFuntion(text.substring(1), position);
                    }
                });
        holder.timeText.setText(TimeAgo.getTimeAgo(Long.parseLong(notificationsData.get(position).getCreatedAt())));
        if (notificationsData.get(position).getType().equals("order")){
            if (sharedpreferences.getString("LastState", "").equals("user")){
                Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getBoothImage()).placeholder(R.drawable.user).into(holder.userImage);
            }else {
                Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getUserImage()).placeholder(R.drawable.user).into(holder.userImage);
            }
        }else if (notificationsData.get(position).getType().equals("comment")){
            if (notificationsData.get(position).getActivityDoneAs().equals("user")){
                Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getUserImage()).placeholder(R.drawable.user).into(holder.userImage);
            }else {
                Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getBoothImage()).placeholder(R.drawable.user).into(holder.userImage);
            }
        }else if (notificationsData.get(position).getType().equals("promocode")){
            Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getBoothImage()).placeholder(R.drawable.user).into(holder.userImage);
        }else {
            Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getUserImage()).placeholder(R.drawable.user).into(holder.userImage);
        }

        if (notificationsData.get(position).getProductImage() != null && !notificationsData.get(position).getProductImage().isEmpty()){
            holder.detailedImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getProductImage()).into(holder.detailedImage);
        }else if (notificationsData.get(position).getQuestionImage() != null && !notificationsData.get(position).getQuestionImage().isEmpty()){
            holder.detailedImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Constants.URL.IMG_URL + notificationsData.get(position).getQuestionImage()).into(holder.detailedImage);
        }else {
            holder.detailedImage.setVisibility(View.GONE);
        }

        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < notificationsData.get(position).getMentionedUsersInfos().size(); i++){
                    if (notificationsData.get(position).getMentionedUsersInfos().get(i).getUserID().contains(notificationsData.get(position).getUserIDofNoti())){
                        if (notificationsData.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("user")){
                            Intent intent = new Intent(mContext, OthersProfileActivity.class);
                            mEditor.putString("Booth", "Other").apply();
                            intent.putExtra("OtherUserID", notificationsData.get(position).getMentionedUsersInfos().get(i).getUserID());
                            mContext.startActivity(intent);
                        }else if (notificationsData.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("booth")){
                            Intent intent = new Intent(mContext, BoothProfileActivity.class);
                            mEditor.putString("Booth", "Other").apply();
                            intent.putExtra("OtherUserID", notificationsData.get(position).getMentionedUsersInfos().get(i).getUserID());
                            mContext.startActivity(intent);
                        }
                    }
                }
            }
        });

        holder.notificationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (notificationsData.get(position).getType()) {
                    case "order":

                        if (sharedpreferences.getString("LastState", "").equals("user")) {
                            Intent intent = new Intent(mContext, UserSummaryOrder.class);
                            intent.putExtra("activity", "notification");
                            intent.putExtra("activity2", "notification");
                            intent.putExtra("orderRequestID", notificationsData.get(position).getOrderRequestID());
                            mContext.startActivity(intent);
                        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                            Intent intent = new Intent(mContext, SummaryOrder.class);
                            intent.putExtra("activity", "notification");
                            intent.putExtra("orderRequestID", notificationsData.get(position).getOrderRequestID());
                            mContext.startActivity(intent);
                        }


                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "comment":
                        if (notificationsData.get(position).getProductID().equals("0")) {
                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", notificationsData.get(position).getQuestionID()).commit();
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                            mEditor.putString("ProductID", notificationsData.get(position).getProductID()).commit();
                            mContext.startActivity(intent);
                        }
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "follow":
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "like":
                        Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", notificationsData.get(position).getProductID()).commit();
                        mContext.startActivity(intent);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "invite":
                        Intent intent1 = new Intent(mContext, TransactionHistory.class);
                        mContext.startActivity(intent1);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "added_to_wishlist":
                        Intent intent2 = new Intent(mContext, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", notificationsData.get(position).getProductID()).commit();
                        mContext.startActivity(intent2);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "promocode":
                        Intent intent3 = new Intent(mContext, BoothProfileActivity.class);
                        mEditor.putString("Booth", "Other").commit();
                        intent3.putExtra("OtherUserID", notificationsData.get(position).getUserIDofNoti());
                        mContext.startActivity(intent3);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                }
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (notificationsData.get(position).getType()) {
                    case "order":

                        if (sharedpreferences.getString("LastState", "").equals("user")) {
                            Intent intent = new Intent(mContext, UserSummaryOrder.class);
                            intent.putExtra("activity", "notification");
                            intent.putExtra("activity2", "notification");
                            intent.putExtra("orderRequestID", notificationsData.get(position).getOrderRequestID());
                            mContext.startActivity(intent);
                        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                            Intent intent = new Intent(mContext, SummaryOrder.class);
                            intent.putExtra("activity", "notification");
                            intent.putExtra("orderRequestID", notificationsData.get(position).getOrderRequestID());
                            mContext.startActivity(intent);
                        }


                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "comment":
                        if (notificationsData.get(position).getProductID().equals("0")) {
                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", notificationsData.get(position).getQuestionID()).apply();
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                            mEditor.putString("ProductID", notificationsData.get(position).getProductID()).apply();
                            mContext.startActivity(intent);
                        }
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "follow":
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "like":
                        Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", notificationsData.get(position).getProductID()).apply();
                        mContext.startActivity(intent);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "invite":
                        Intent intent1 = new Intent(mContext, TransactionHistory.class);
                        mContext.startActivity(intent1);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "added_to_wishlist":
                        Intent intent2 = new Intent(mContext, ProductDetailsActivity.class);
                        mEditor.putString("ProductID", notificationsData.get(position).getProductID()).apply();
                        mContext.startActivity(intent2);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                    case "promocode":
                        Intent intent3 = new Intent(mContext, BoothProfileActivity.class);
                        mEditor.putString("Booth", "Other").commit();
                        intent3.putExtra("OtherUserID", notificationsData.get(position).getUserIDofNoti());
                        mContext.startActivity(intent3);
                        NotificationRead(notificationsData.get(position).getUserNotificationID(), holder, position);
                        break;
                }
            }
        });

    }

    private void NotificationRead(String NotiID, MyViewHolder holder, int position){

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.markNotificationAsRead + sharedpreferences.getString("UserID", "") + "&NotificationID=" + NotiID + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, mContext, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()){
                    Log.e("ReadNoti", result);

                    if (ERROR.isEmpty()){
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            int status = jsonObject.getInt("status");
                            if (status == 200){
                                notificationsData.get(position).setIsRead("1");
                                notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }



                }else {
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void MentionFuntion(String name, int position){
        for (int i = 0; i < notificationsData.get(position).getMentionedUsersInfos().size(); i++){
            if (notificationsData.get(position).getMentionedUsersInfos().get(i).getMentionedName().contains(name)){
                if (notificationsData.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("user")){
                    Intent intent = new Intent(mContext, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", notificationsData.get(position).getMentionedUsersInfos().get(i).getUserID());
                    mContext.startActivity(intent);
                }else if (notificationsData.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("booth")){
                    Intent intent = new Intent(mContext, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", notificationsData.get(position).getMentionedUsersInfos().get(i).getUserID());
                    mContext.startActivity(intent);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationsData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout;
        CircleImageView userImage;
        RoundedImageView detailedImage;
        TextView timeText, notificationText;

        public MyViewHolder(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.view1Linear);
            userImage = itemView.findViewById(R.id.userImage);
            detailedImage = itemView.findViewById(R.id.detailImage);
//            userName = itemView.findViewById(R.id.userName);
            timeText = itemView.findViewById(R.id.timeText);
            notificationText = itemView.findViewById(R.id.notificationText);

        }
    }

    public interface CRLCallbacks {
        void onItemClick(int position);
    }
    public void addfeed(ArrayList<NotificationsData> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            notificationsData.add(feed.get(a));
        }
//        size = products.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }
}
