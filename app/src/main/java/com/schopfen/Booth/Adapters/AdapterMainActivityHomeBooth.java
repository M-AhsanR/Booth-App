package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.bikomobile.circleindicatorpager.CircleIndicatorPager;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.ProductComments;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.QuestionsDetailsActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Fragments.Home_Booth_Fragment;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.MyViewHolderAMAModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class AdapterMainActivityHomeBooth extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static ArrayList<ProductsData> products;
    public static int size;
    private Context mContext;
    //    ArrayList<HomeCateDataModel> homeCateDataArray;
    CustomItemClickListener listener;
    private CRLCallbacks callbacks;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public static int last_position_for_video = 0;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private String IsLiked;
    String likeString;
    String cartprocedure = "";

    VideoView video_player;

    public AdapterMainActivityHomeBooth(Context mContext, ArrayList<ProductsData> productsData, CRLCallbacks callbacks) {
        this.mContext = mContext;
        this.products = productsData;
        this.callbacks = callbacks;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {

        String type = products.get(position).getType();
//        Log.e("type", type);
        if (type != null) {
            return Integer.parseInt(type);
        }
        return Integer.parseInt(type);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categ_recycle_item_3, parent, false);
                return new MyViewHolder(view);
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cate_recycl_item2, parent, false);
                return new MyViewHolder1(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
                return new HeaderViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int i) {

        String type = products.get(i).getType();

        size = products.size();
        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();


        switch (type) {
            case "0":
                ((MyViewHolder) holder).quesUserName.setText("@" + products.get(i).getUserName());
                ((MyViewHolder) holder).quesCity.setText(products.get(i).getCityName());
                ((MyViewHolder) holder).quesDescription.setText(products.get(i).getProductDescription());
                ((MyViewHolder) holder).quesCommentCount.setText("(" + products.get(i).getCommentCount() + ")");
                ((MyViewHolder) holder).quesTime.setText(TimeAgo.getTimeAgo(Long.parseLong(products.get(i).getCreatedAt())));
                Picasso.get().load(Constants.URL.IMG_URL + products.get(i).getBoothImage()).placeholder(R.drawable.user).into(((MyViewHolder) holder).quesUserImage);
                if (products.get(i).getSubSubCategoryName() != null && products.get(i).getSubSubCategoryName().isEmpty()) {
                    ((MyViewHolder) holder).questionCategory.setText(products.get(i).getCategoryName() + " / " + products.get(i).getSubCategoryName());
                } else {
                    ((MyViewHolder) holder).questionCategory.setText(products.get(i).getCategoryName() + " / " + products.get(i).getSubCategoryName() + " / " + products.get(i).getSubSubCategoryName());
                }
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                ((MyViewHolder) holder).recyclerView.setLayoutManager(layoutManager);
                MyViewHolderAMAAdapter adapter = new MyViewHolderAMAAdapter(mContext, products.get(i).getProductImagesData(), new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                    }
                });
                ((MyViewHolder) holder).recyclerView.setAdapter(adapter);

                ((MyViewHolder) holder).questionShareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Download App", "https://play.google.com/store/apps/details?id=com.schopfen.Booth");
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.linkcopiedtoclipboard), Toast.LENGTH_SHORT).show();
                    }
                });
                ((MyViewHolder) holder).comment_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (products.get(i).getType().equals("0")) {

                            Home_Home_Fragment.refreshposition = i;
                            Home_Home_Fragment.refresh = true;

                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", products.get(i).getQuestionID()).commit();
                            mContext.startActivity(intent);
                        } else if (products.get(i).getType().equals("1")) {
                            Home_Home_Fragment.refreshposition = i;
                            Home_Home_Fragment.refresh = true;
                            Intent intent = new Intent(mContext, ProductComments.class);
                            mEditor.putString("ProductID", products.get(i).getProductID()).commit();
                            intent.putExtra("COUNT", products.get(i).getCommentCount());
                            mContext.startActivity(intent);
                        }
                    }
                });

                ((MyViewHolder) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (products.get(i).getType().equals("0")) {

                            Home_Home_Fragment.refreshposition = i;

                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", products.get(i).getQuestionID()).commit();
                            mContext.startActivity(intent);
                        } else if (products.get(i).getType().equals("1")) {
                            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                            mEditor.putString("ProductID", products.get(i).getProductID()).commit();
                            mContext.startActivity(intent);
                        }
                    }
                });

                ((MyViewHolder) holder).quesMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (sharedpreferences.getString("UserID", "").equals(products.get(i).getUserID())) {
                            final PopupMenu projMangMore = new PopupMenu(mContext, view);
                            projMangMore.getMenuInflater().inflate(R.menu.deletemenu, projMangMore.getMenu());

                            projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.delete:
                                            final Dialog deletedialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                                    deleteQuestionApiCall(i);
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
                            final PopupMenu projMangMore = new PopupMenu(mContext, view);
                            projMangMore.getMenuInflater().inflate(R.menu.home_more_otheruser_menu, projMangMore.getMenu());

                            projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.report:
                                            final Dialog reportdialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                            reportdialog.setContentView(R.layout.report_dialog);
                                            Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                                            Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);

                                            reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            reportdialog.show();

                                            spamBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    reportQuestionApi(i, spamBtn.getText().toString());
                                                    reportdialog.dismiss();
                                                }
                                            });

                                            inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    reportQuestionApi(i, inappropriateBtn.getText().toString());
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


                break;
            case "1":
                HomeBoothPagerAdapter homeHomePagerAdapter;
                if (products.get(i).getProductVideo().equals("")) {
                    homeHomePagerAdapter = new HomeBoothPagerAdapter(mContext, products.get(i).getProductImagesData());
                } else {
                    products.get(i).getProductImagesData().add(new ProductImagesData("", ""));
                    homeHomePagerAdapter = new HomeBoothPagerAdapter(mContext, products.get(i).getProductImagesData(), products.get(i).getProductVideo(), products.get(i).getProductVideoThumbnail());
                }

                ((MyViewHolder1) holder).viewPager.setClipToPadding(false);
//                ((MyViewHolder1)holder).viewPager.setOffscreenPageLimit(2);
                ((MyViewHolder1) holder).viewPager.setPadding(0, 0, 0, 0);
                ((MyViewHolder1) holder).viewPager.setPageMargin(0);
                homeHomePagerAdapter.notifyDataSetChanged();
                ((MyViewHolder1) holder).viewPager.setAdapter(homeHomePagerAdapter);

                if (products.get(i).getIsPromotionApproved().equals("1")) {
                    ((MyViewHolder1) holder).repost_img.setVisibility(View.VISIBLE);
                } else {
                    ((MyViewHolder1) holder).repost_img.setVisibility(View.GONE);
                }
                ((MyViewHolder1) holder).viewPager.setTag(i);

                ((MyViewHolder1) holder).indicatorPager.setViewPager(((MyViewHolder1) holder).viewPager);

                if (products.get(i).getProductImagesData().size() == 1) {
                    if (products.get(i).getProductVideo().equals("")) {
                        ((MyViewHolder1) holder).indicatorPager.setVisibility(View.GONE);
                    }
                }

                ((MyViewHolder1) holder).boothImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, BoothProfileActivity.class);
                        mEditor.putString("Booth", "Other").commit();
                        intent.putExtra("OtherUserID", products.get(i).getUserID());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) mContext,
                                        ((MyViewHolder1) holder).boothImg,
                                        ViewCompat.getTransitionName(((MyViewHolder1) holder).boothImg));
                        mContext.startActivity(intent, options.toBundle());
                    }
                });

                ((MyViewHolder1) holder).viewPager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                ((MyViewHolder1) holder).likeIcon.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {

                    }
                });

                ((MyViewHolder1) holder).more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(mContext, "Mmm, working on it!", Toast.LENGTH_SHORT).show();
                        if (sharedpreferences.getString("LastState", "").equals("user")) {
                            if (sharedpreferences.getString("UserID", "").equals(products.get(i).getUserID())) {
//                                final PopupMenu projMangMore = new PopupMenu(mContext, view);
//                                projMangMore.getMenuInflater().inflate(R.menu.home_more_mine_menu, projMangMore.getMenu());
//
//                                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                                    @Override
//                                    public boolean onMenuItemClick(MenuItem menuItem) {
//                                        switch (menuItem.getItemId()) {
//                                            case R.id.edit:
//                                                Intent intent = new Intent(mContext, AddProductActivity.class);
//                                                intent.putExtra("ProID", products.get(i).getProductID());
//                                                mContext.startActivity(intent);
//                                                break;
//                                            case R.id.delete:
//                                                final Dialog deletedialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//                                                deletedialog.setContentView(R.layout.delete_cart_item_dialog);
//                                                Button yesBtn = deletedialog.findViewById(R.id.btn_yes);
//                                                Button noBtn = deletedialog.findViewById(R.id.btn_no);
//
//                                                deletedialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                                deletedialog.show();
//
//                                                noBtn.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        deletedialog.dismiss();
//                                                    }
//                                                });
//
//                                                yesBtn.setOnClickListener(new View.OnClickListener() {
//                                                    @Override
//                                                    public void onClick(View view) {
//                                                        deleteProductApiCall(i);
//                                                        deletedialog.dismiss();
//                                                    }
//                                                });
//                                                break;
//                                            case R.id.promote:
//                                                PromoteProduct(i);
//                                                break;
//                                        }
//                                        return false;
//                                    }
//                                });
//                                projMangMore.show();
                            }
                            else {
                                final PopupMenu projMangMore = new PopupMenu(mContext, view);
                                projMangMore.getMenuInflater().inflate(R.menu.home_more_otheruser_menu, projMangMore.getMenu());

                                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {
                                            case R.id.report:
                                                final Dialog reportdialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                                reportdialog.setContentView(R.layout.report_dialog);
                                                Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                                                Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);

                                                reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                reportdialog.show();

                                                spamBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        reportProductApi(i, spamBtn.getText().toString());
                                                        reportdialog.dismiss();
                                                    }
                                                });

                                                inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        reportProductApi(i, inappropriateBtn.getText().toString());
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
                        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                            if (sharedpreferences.getString("UserID", "").equals(products.get(i).getUserID())) {
                                final PopupMenu projMangMore = new PopupMenu(mContext, view);
                                projMangMore.getMenuInflater().inflate(R.menu.home_more_mine_menu, projMangMore.getMenu());

                                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {
                                            case R.id.edit:
                                                Intent intent = new Intent(mContext, AddProductActivity.class);
                                                intent.putExtra("ProID", products.get(i).getProductID());
                                                mContext.startActivity(intent);
                                                break;
                                            case R.id.delete:
                                                final Dialog deletedialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                                        deleteProductApiCall(i);
                                                        deletedialog.dismiss();
                                                    }
                                                });
                                                break;
                                            case R.id.promote:
                                                PromoteProduct(i);
                                                break;
                                        }
                                        return false;
                                    }
                                });
                                projMangMore.show();
                            }
                            else {
                                final PopupMenu projMangMore = new PopupMenu(mContext, view);
                                projMangMore.getMenuInflater().inflate(R.menu.home_more_otheruser_menu, projMangMore.getMenu());

                                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        switch (menuItem.getItemId()) {
                                            case R.id.report:
                                                final Dialog reportdialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                                reportdialog.setContentView(R.layout.report_dialog);
                                                Button spamBtn = reportdialog.findViewById(R.id.btn_cancel);
                                                Button inappropriateBtn = reportdialog.findViewById(R.id.btn_ok);

                                                reportdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                reportdialog.show();

                                                spamBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        reportProductApi(i, spamBtn.getText().toString());
                                                        reportdialog.dismiss();
                                                    }
                                                });

                                                inappropriateBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        reportProductApi(i, inappropriateBtn.getText().toString());
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

                    }
                });

                SpannableString span1 = new SpannableString(products.get(i).getCurrency());
                span1.setSpan(new AbsoluteSizeSpan(25), 0, products.get(i).getCurrency().length(), SPAN_INCLUSIVE_INCLUSIVE);
                span1.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.orange)), 0, products.get(i).getCurrency().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                SpannableString span2 = new SpannableString(products.get(i).getProductPrice());
                span2.setSpan(new AbsoluteSizeSpan(30), 0, products.get(i).getProductPrice().length(), SPAN_INCLUSIVE_INCLUSIVE);

                CharSequence finalText = TextUtils.concat(span1, " ", span2);

                // productPrice.setText(finalText);

                ((MyViewHolder1) holder).username.setText("@" + products.get(i).getBoothUserName());
                Picasso.get().load(Constants.URL.IMG_URL + products.get(i).getCompressedBoothImage()).placeholder(R.drawable.user).into(((MyViewHolder1) holder).boothImg);
                ((MyViewHolder1) holder).boothCity.setText(products.get(i).getCityName());
                ((MyViewHolder1) holder).productName.setText(products.get(i).getTitle());
                ((MyViewHolder1) holder).categoryName.setText(products.get(i).getCategoryName() + " / " + products.get(i).getSubCategoryName() + " / " + products.get(i).getSubSubCategoryName());
                ((MyViewHolder1) holder).likesCount.setText(products.get(i).getLikesCount());
                ((MyViewHolder1) holder).commentsCount.setText(products.get(i).getCommentCount());
                ((MyViewHolder1) holder).productPrice.setText(finalText);
                ((MyViewHolder1) holder).time.setText(TimeAgo.getTimeAgo(Long.parseLong(products.get(i).getCreatedAt())));

                if (products.get(i).getIsLiked().equals("1")) {
                    ((MyViewHolder1) holder).likeIcon.setLiked(Boolean.TRUE);
                } else {
                    ((MyViewHolder1) holder).likeIcon.setLiked(Boolean.FALSE);
                }

                ((MyViewHolder1) holder).likeIcon.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        Map<String, String> body = new HashMap<String, String>();
                        body.put("Type", "like");
                        body.put("ProductID", products.get(i).getProductID());
                        body.put("UserID", sharedpreferences.getString("UserID", " "));
                        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

                        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, mContext, body, header, new ServerCallback() {
                            @Override
                            public void onSuccess(String result, String ERROR) {
                                if (ERROR.isEmpty()) {
                                    Log.e("likedResult", result);
                                    try {
                                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                        int status = jsonObject.getInt("status");
                                        String message = jsonObject.getString("message");
                                        if (status == 200) {
                                            IsLiked = "1";
                                            String LikesCount = jsonObject.getString("LikesCount");
                                            ((MyViewHolder1) holder).likesCount.setText(LikesCount);
                                        } else {
                                            likeButton.setLiked(Boolean.FALSE);
                                            IsLiked = "0";
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    CustomLoader.dialog.dismiss();
                                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        Map<String, String> body = new HashMap<String, String>();
                        body.put("Type", "dislike");
                        body.put("ProductID", products.get(i).getProductID());
                        body.put("UserID", sharedpreferences.getString("UserID", " "));
                        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

                        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.LikeProduct, mContext, body, header, new ServerCallback() {
                            @Override
                            public void onSuccess(String result, String ERROR) {
                                if (ERROR.isEmpty()) {
                                    Log.e("likedResult", result);
                                    try {
                                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                                        int status = jsonObject.getInt("status");
                                        String message = jsonObject.getString("message");
                                        if (status == 200) {
                                            IsLiked = "0";
                                            String LikesCount = jsonObject.getString("LikesCount");
                                            ((MyViewHolder1) holder).likesCount.setText(LikesCount);
                                        } else {
                                            likeButton.setLiked(Boolean.TRUE);
                                            IsLiked = "1";
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    CustomLoader.dialog.dismiss();
                                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                ((MyViewHolder1) holder).productShareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Download App", "https://play.google.com/store/apps/details?id=com.schopfen.Booth");
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.linkcopiedtoclipboard), Toast.LENGTH_SHORT).show();
                    }
                });

                if (sharedpreferences.getString("UserID", "").equals(products.get(i).getUserID())) {
                    ((MyViewHolder1) holder).addToCart.setVisibility(View.GONE);
                } else {
                    ((MyViewHolder1) holder).addToCart.setVisibility(View.VISIBLE);
                }

                ((MyViewHolder1) holder).addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (products.get(i).getOutOfStock().equals("1")) {
                            Toast.makeText(mContext, mContext.getResources().getString(R.string.productisoutofstock), Toast.LENGTH_SHORT).show();
                        } else {
                            AddToCartApi(i);
                        }
                    }
                });

                ((MyViewHolder1) holder).commentsCountLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (products.get(i).getType().equals("0")) {
                            Home_Home_Fragment.refreshposition = i;
                            Home_Home_Fragment.refresh = true;
                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", products.get(i).getQuestionID()).commit();
                            mContext.startActivity(intent);
                        } else if (products.get(i).getType().equals("1")) {
                            Home_Home_Fragment.refreshposition = i;
                            Home_Home_Fragment.refresh = true;
                            Intent intent = new Intent(mContext, ProductComments.class);
                            mEditor.putString("ProductID", products.get(i).getProductID()).commit();
                            intent.putExtra("COUNT", products.get(i).getCommentCount());
                            mContext.startActivity(intent);
                        }
                    }
                });

                ((MyViewHolder1) holder).linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (products.get(i).getType().equals("0")) {
                            Intent intent = new Intent(mContext, QuestionsDetailsActivity.class);
                            mEditor.putString("QuestionID", products.get(i).getQuestionID()).commit();
                            mContext.startActivity(intent);
                        } else if (products.get(i).getType().equals("1")) {
                            Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                            mEditor.putString("ProductID", products.get(i).getProductID()).commit();
                            mContext.startActivity(intent);
                        }
                    }
                });

                break;
            case "2":
                // Promoted Products View Pager
                if (!Home_Booth_Fragment.homeCateData.isEmpty()) {
                    HomeBoothPromotedAdapter cateViewPagerAdapter = new HomeBoothPromotedAdapter(mContext, Home_Booth_Fragment.homeCateData);
                    HeaderViewHolder.viewPager.setClipToPadding(false);
//                                viewPager.setOffscreenPageLimit(2);
//                    HeaderViewHolder.viewPager.setPadding(0, 0, 0, 0);
//                    HeaderViewHolder.viewPager.setPageMargin(0);
//                    cateViewPagerAdapter.notifyDataSetChanged();
                    HeaderViewHolder.viewPager.setAdapter(cateViewPagerAdapter);
                } else {
                    HeaderViewHolder.viewPager.setVisibility(View.GONE);
                }

//                ((HeaderViewHolder) holder).viewPager.setVisibility(View.GONE);

                // Promoted Products View Pager
                break;
        }
    }

    private void AddToCartApi(int position) {

        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("BoothID", products.get(position).getUserID());
        body.put("ProductID", products.get(position).getProductID());
        body.put("ProductQuantity", "1");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("AddtoCartBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.AddToCart, mContext, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddToCartResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
//                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                            CustomLoader.dialog.dismiss();

                            final Dialog alertdialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            alertdialog.setContentView(R.layout.continue_or_checkout_dialog);
                            LinearLayout continueBtn = alertdialog.findViewById(R.id.continue_shop);
                            LinearLayout proceedBtn = alertdialog.findViewById(R.id.proceed_checkout);

                            alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            alertdialog.setCancelable(false);
                            alertdialog.show();

                            continueBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertdialog.dismiss();
                                }
                            });

                            proceedBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mContext.startActivity(new Intent(mContext, ShoppingCartActivity.class));
                                    alertdialog.dismiss();
                                }
                            });

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout linearLayout, comment_layout, questionShareBtn;
        RecyclerView recyclerView;
        CircleImageView quesUserImage;
        TextView quesUserName, quesCity, quesTime, quesDescription, quesCommentCount, questionCategory;
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
            questionShareBtn = itemView.findViewById(R.id.questionShareBtn);
            questionCategory = itemView.findViewById(R.id.questionCategory);
            MyViewHolderAMAModel model;
        }
    }

    private void deleteQuestionApiCall(int position) {
        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", products.get(position).getQuestionID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEQUESTION, mContext, body, header, new ServerCallback() {
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
                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                            notifyItemRemoved(position);
                            products.remove(position);
                            notifyItemRangeChanged(position, products.size());
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public static ViewPager viewPager;

        HeaderViewHolder(View itemView) {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.viewPager);
        }
    }

    class MyViewHolder1 extends RecyclerView.ViewHolder {

        LinearLayout linearLayout, commentsCountLinear, addToCart, productShareBtn;
        ViewPager viewPager;
        CircleIndicatorPager indicatorPager;
        CircleImageView boothImg;
        LikeButton likeIcon;
        TextView username, boothCity, productName, categoryName, productPrice, commentsCount, likesCount, time;
        ImageView more, repost_img;

        MyViewHolder1(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            repost_img = itemView.findViewById(R.id.repost_img);
            boothCity = itemView.findViewById(R.id.boothCity);
            productName = itemView.findViewById(R.id.productName);
            categoryName = itemView.findViewById(R.id.productCategory);
            productPrice = itemView.findViewById(R.id.productPrice);
            commentsCount = itemView.findViewById(R.id.commentsCount);
            likesCount = itemView.findViewById(R.id.likesCount);
            boothImg = itemView.findViewById(R.id.boothImg);
            indicatorPager = itemView.findViewById(R.id.indicator);
            viewPager = itemView.findViewById(R.id.cat_viewPager);
            linearLayout = itemView.findViewById(R.id.ll_cri);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            time = itemView.findViewById(R.id.time);
            more = itemView.findViewById(R.id.more);
            commentsCountLinear = itemView.findViewById(R.id.commentsCountLinear);
            addToCart = itemView.findViewById(R.id.addToCart);
            productShareBtn = itemView.findViewById(R.id.productShareBtn);
        }
    }

    public interface CRLCallbacks {
        void onItemClick(int position);
    }

    private void reportProductApi(int position, String reason) {

        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", products.get(position).getProductID());
        body.put("ReportReason", reason);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
//        body.put("DeviceType", "Android");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ReportProduct, mContext, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();

                if (ERROR.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void reportQuestionApi(int position, String reason) {

        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("QuestionID", products.get(position).getQuestionID());
        body.put("ReportReason", reason);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
//        body.put("DeviceType", "Android");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.REPORTQUESTION, mContext, body, header, new ServerCallback() {
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
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void PromoteProduct(int position) {
        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ProductID", products.get(position).getProductID());
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        Log.e("AddtoCartBody", body.toString());

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.PROMOTEMYPRODUCT, mContext, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("promoteMy", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }

    private void deleteProductApiCall(int position) {
        CustomLoader.showDialog((Activity) mContext);

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", products.get(position).getProductID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEPRODUCT, mContext, body, header, new ServerCallback() {
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
                            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                            products.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, products.size());
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void addfeed(ArrayList<ProductsData> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            products.add(feed.get(a));
        }
        size = products.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }

    public void addfeedbooth(ArrayList<ProductsData> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            products.add(feed.get(a));
        }
        size = products.size();
//            notifyItemChanged(getItemCount());
        notifyItemInserted(getItemCount());

        int fv = getItemCount();
//        notifyDataSetChanged();

    }


}
