package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.bachors.wordtospan.WordToSpan;
import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.CommentsModel;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Answers_Details_Adapter extends RecyclerView.Adapter<Answers_Details_Adapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<CommentsModel> commentsArray;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String activity;
    String productID;

    public Answers_Details_Adapter(String ProductID, String Activity, Context context, ArrayList<CommentsModel> commentsArray, CustomItemClickListener listener) {
        this.commentsArray = commentsArray;
        this.context = context;
        this.listener = listener;
        this.activity = Activity;
        this.productID = ProductID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.answers_details_adapter, parent, false);
        final Answers_Details_Adapter.MyViewHolder viewHolder = new Answers_Details_Adapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (commentsArray.get(position).getCommentedAs().equals("user")){
            holder.comenterName.setText("@" + commentsArray.get(position).getUserName());
            Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getImage()).placeholder(R.drawable.user).into(holder.comenterImg);
        }else if (commentsArray.get(position).getCommentedAs().equals("booth")){
            holder.comenterName.setText("@" + commentsArray.get(position).getBoothUserName());
            Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getBoothimage()).placeholder(R.drawable.user).into(holder.comenterImg);
        }
//        holder.commentText.setLinkText(BaseClass.decode(commentsArray.get(position).getComment()));
        holder.commentcity.setText(commentsArray.get(position).getCity());
        holder.commentTime.setText(TimeAgo.getTimeAgo(Long.parseLong(commentsArray.get(position).getCreatedAt())));

        WordToSpan link = new WordToSpan();
        link.setColorMENTION(context.getResources().getColor(R.color.orange))
//                .setColorURL(Color.MAGENTA)
//                .setColorPHONE(Color.RED)
//                .setColorMAIL(getResources().getColor(R.color.colorPrimary))
//                .setColorTAG(context.getResources().getColor(R.color.orange))
                .setUnderlineURL(true)
                .setLink(commentsArray.get(position).getComment())
                .into(holder.commentText)
                .setClickListener(new WordToSpan.ClickListener() {
                    @Override
                    public void onClick(String type, String text) {
                        // type: "tag", "mail", "url", "phone", "mention" or "custom"
//                        Toast.makeText(context, "Type: " + type + "\nText: " + text, Toast.LENGTH_LONG).show();
                        MentionFuntion(text.substring(1), position);
                    }
                });

        holder.commentText.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
            @Override
            public void onLinkClicked(int i, String s) {

                MentionFuntion(s, position);

            }
        });

        holder.comenterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentsArray.get(position).getCommentedAs().equals("user")){
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getUserID());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.comenterImg,
                            ViewCompat.getTransitionName(holder.comenterImg));
                    context.startActivity(intent, options.toBundle());
                }else if (commentsArray.get(position).getCommentedAs().equals("booth")){
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getUserID());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.comenterImg,
                            ViewCompat.getTransitionName(holder.comenterImg));
                    context.startActivity(intent, options.toBundle());
                }
            }
        });

        holder.comenterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (commentsArray.get(position).getCommentedAs().equals("user")){
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getUserID());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.comenterImg,
                            ViewCompat.getTransitionName(holder.comenterImg));
                    context.startActivity(intent, options.toBundle());
                }else if (commentsArray.get(position).getCommentedAs().equals("booth")){
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getUserID());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            holder.comenterImg,
                            ViewCompat.getTransitionName(holder.comenterImg));
                    context.startActivity(intent, options.toBundle());
                }
            }
        });

        holder.comment_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("UserID", "").equals(commentsArray.get(position).getUserID())){

                    final PopupMenu projMangMore = new PopupMenu(context, view);
                    projMangMore.getMenuInflater().inflate(R.menu.deletemenu, projMangMore.getMenu());

                    projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    final Dialog deletedialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                            if (activity.equals("product")){
                                                deleteCommentApiCall(position);
                                            }else if (activity.equals("question")){
                                                deleteAnswerApiCall(position);
                                            }
                                            deletedialog.dismiss();
                                        }
                                    });
                                    break;
                            }
                            return false;
                        }
                    });
                    projMangMore.show();

                }else {
                    final PopupMenu projMangMore = new PopupMenu(context, view);
                    projMangMore.getMenuInflater().inflate(R.menu.home_more_otheruser_menu, projMangMore.getMenu());

                    projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.report:
                                    final Dialog reportdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    reportdialog.setContentView(R.layout.report_dialog);
                                    Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                                    Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);

                                    reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    reportdialog.show();

                                    spamBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (activity.equals("question")){
                                                reportCommentApi(position, spamBtn.getText().toString(), "question");
                                            }else if (activity.equals("product")){
                                                reportCommentApi(position, spamBtn.getText().toString(), "product");
                                            }
                                            reportdialog.dismiss();
                                        }
                                    });

                                    inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (activity.equals("question")){
                                                reportCommentApi(position, inappropriateBtn.getText().toString(), "question");
                                            }else if (activity.equals("product")){
                                                reportCommentApi(position, inappropriateBtn.getText().toString(), "product");
                                            }
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

    }

    private void deleteCommentApiCall(int position) {
        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductCommentID", commentsArray.get(position).getCommentID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ProductID", productID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deleteProductComment, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("deleteResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            commentsArray.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, commentsArray.size());
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteAnswerApiCall(int position) {
        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionCommentID", commentsArray.get(position).getCommentID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("QuestionID", productID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deletequestionComment, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("deleteResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            commentsArray.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, commentsArray.size());
                            QuestionsDetailsActivity.sizeofArray = String.valueOf(commentsArray.size());
                            QuestionsDetailsActivity.commentsCount = String.valueOf(Integer.parseInt(QuestionsDetailsActivity.commentsCount) - 1);
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reportCommentApi(int position, String reason, String reasonType) {

        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("CommentID", commentsArray.get(position).getCommentID());
        body.put("ReportReason", reason);
        body.put("ReportType", reasonType);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.reportComment, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();

                if (ERROR.isEmpty()) {
                    Log.e("Report", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void MentionFuntion(String name, int position){
        for (int i = 0; i < commentsArray.get(position).getMentionedUsersInfos().size(); i++){
            if (commentsArray.get(position).getMentionedUsersInfos().get(i).getMentionedName().contains(name)){
                if (commentsArray.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("user")){
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getMentionedUsersInfos().get(i).getUserID());
                    context.startActivity(intent);
                }else if (commentsArray.get(position).getMentionedUsersInfos().get(i).getMentionedUserType().contains("booth")){
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", commentsArray.get(position).getMentionedUsersInfos().get(i).getUserID());
                    context.startActivity(intent);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView comenterImg;
        TextView comenterName, commentcity, commentTime;
        SocialTextView commentText;
        ImageView comment_more;

        MyViewHolder(View itemView) {
            super(itemView);

            comenterImg = itemView.findViewById(R.id.comenterImg);
            comenterName = itemView.findViewById(R.id.comenterName);
            commentcity = itemView.findViewById(R.id.commentcity);
            commentTime = itemView.findViewById(R.id.commentTime);
            commentText = itemView.findViewById(R.id.commentText);
            comment_more = itemView.findViewById(R.id.comment_more);

        }
    }
}
