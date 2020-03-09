package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.QuestionsModel;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MyViewHolderAMAModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BoothHomeFragmentAdapter extends RecyclerView.Adapter<BoothHomeFragmentAdapter.MyViewHolder> {

    ArrayList<QuestionsModel> list;
    Context context;
    public static int size;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public BoothHomeFragmentAdapter(ArrayList<QuestionsModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categ_recycle_item_3, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        size = list.size();

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        holder.quesUserName.setText("@" + list.get(position).getUserName());
        holder.quesCity.setText(list.get(position).getUserCityName());
        holder.quesDescription.setText(list.get(position).getQuestionDescription());
        holder.quesCommentCount.setText("(" + list.get(position).getCommentCount() + ")");
        holder.quesTime.setText(TimeAgo.getTimeAgo(Long.parseLong(list.get(position).getQuestionAskedAt())));
        Picasso.get().load(Constants.URL.IMG_URL + list.get(position).getUserImage()).placeholder(R.drawable.user).into(holder.quesUserImage);

        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "mmm, working on it!", Toast.LENGTH_SHORT).show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        MyViewHolderAMAAdapter adapter = new MyViewHolderAMAAdapter(context, list.get(position).getProductImagesData(), new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        holder.recyclerView.setAdapter(adapter);

        holder.comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Home_Home_Fragment.refreshposition = position;
                    Home_Home_Fragment.refresh = true;

                    Intent intent = new Intent(context, QuestionsDetailsActivity.class);
                    mEditor.putString("QuestionID", list.get(position).getQuestionID()).commit();
                    context.startActivity(intent);
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Home_Home_Fragment.refreshposition = position;
                    Home_Home_Fragment.refresh = true;

                    Intent intent = new Intent(context, QuestionsDetailsActivity.class);
                    mEditor.putString("QuestionID", list.get(position).getQuestionID()).commit();
                    context.startActivity(intent);
            }
        });

        holder.quesMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("UserID", "").equals(list.get(position).getUserID())) {
                    final PopupMenu projMangMore = new PopupMenu(context, view);
                    projMangMore.getMenuInflater().inflate(R.menu.deletemenu, projMangMore.getMenu());

                    projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.delete:
                                    final Dialog deletedialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    deletedialog.setContentView(R.layout.delete_question_item);
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
                                            deleteQuestionApiCall(position);
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
                                            reportQuestionApi(position, spamBtn.getText().toString());
                                            reportdialog.dismiss();
                                        }
                                    });

                                    inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            reportQuestionApi(position, inappropriateBtn.getText().toString());
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

        holder.quesUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BoothProfileActivity.class);
                mEditor.putString("Booth", "Other").commit();
                intent.putExtra("OtherUserID", list.get(position).getUserID());
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context,
                                holder.quesUserImage,
                                ViewCompat.getTransitionName(holder.quesUserImage));
                context.startActivity(intent, options.toBundle());
            }
        });
    }

    private void reportQuestionApi(int position, String reason) {

        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", list.get(position).getQuestionID());
        body.put("ReportReason", reason);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("DeviceType", "Android");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.REPORTQUESTION, context, body, header, new ServerCallback() {
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


    private void deleteQuestionApiCall(int position) {
        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", list.get(position).getQuestionID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEQUESTION, context, body, header, new ServerCallback() {
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
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            notifyItemRemoved(position);
                            list.remove(position);
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


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout, comment_layout, shareBtn;
        RecyclerView recyclerView;
        CircleImageView quesUserImage;
        TextView quesUserName, quesCity, quesTime, quesDescription, quesCommentCount;
        ImageView quesMore, quesComment, quesShare;


        public MyViewHolder(View itemView) {
            super(itemView);

            MyViewHolderAMAAdapter adapter;

            linearLayout = itemView.findViewById(R.id.ll_main_ama);
            recyclerView = itemView.findViewById(R.id.questRecyclerView);
            quesUserImage = itemView.findViewById(R.id.quesUserImage);
            quesUserName = itemView.findViewById(R.id.quesUserName);
            quesCity = itemView.findViewById(R.id.quesUserCity);
            quesTime = itemView.findViewById(R.id.quesTime);
            quesDescription = itemView.findViewById(R.id.quesDescription);
            quesCommentCount = itemView.findViewById(R.id.quesCommentCount);
            quesMore = itemView.findViewById(R.id.quesMore);
            quesComment = itemView.findViewById(R.id.quesComment);
            quesShare = itemView.findViewById(R.id.quesShare);
            comment_layout = itemView.findViewById(R.id.comment_layout);
            shareBtn = itemView.findViewById(R.id.questionShareBtn);
            MyViewHolderAMAModel model;
        }
    }

    public void addfeed(ArrayList<QuestionsModel> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            list.add(feed.get(a));
        }
        size = list.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }
}
