package com.schopfen.Booth.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.Models.CartBoothItemsData;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApproveItemsAdapter extends RecyclerView.Adapter<ApproveItemsAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<OrderItemsData> list;
    String activity;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public ApproveItemsAdapter(Context mContext, ArrayList<OrderItemsData> cartBoothItemsData, String Activity) {
        this.mContext = mContext;
        this.list = cartBoothItemsData;
        this.activity = Activity;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        sharedpreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        view = inflater.inflate(R.layout.summary_sub_adapter_item, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.producttitle.setText(list.get(position).getProductTitle());
        holder.productItemCount.setText(list.get(position).getQuantity());
        holder.productItemPrice.setText(list.get(position).getProductPrice() + " " + list.get(position).getCurrencySymbol());

        if (sharedpreferences.getString("LastState", "").equals("user")) {
            if (activity.equals("Completed")) {
                holder.item_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        dialog.setContentView(R.layout.review_dialog);
                        Button ok = dialog.findViewById(R.id.dialog_submit_btn);
                        TextView heading = dialog.findViewById(R.id.dialog_heading);
                        TextView textView = dialog.findViewById(R.id.dialog_text);
                        RatingBar ratingBar = dialog.findViewById(R.id.dialog_ratingBar);
                        ProgressBar dialog_progressBar = dialog.findViewById(R.id.dialog_progressBar);
                        TextView review = dialog.findViewById(R.id.dialog_review_text);

                        heading.setText(mContext.getResources().getString(R.string.how_was_the_product));
                        textView.setText(mContext.getResources().getString(R.string.rate_the_product));

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.show();

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ratingBar.getRating() < 1) {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.ratingsouldbeminone), Toast.LENGTH_SHORT).show();
                                } else {
                                    ok.setVisibility(View.GONE);
                                    dialog_progressBar.setVisibility(View.VISIBLE);

                                    float rating = ratingBar.getRating();
                                    String review_string = review.getText().toString();
                                    String link = Constants.URL.rateProduct;
                                    String pram = "Review";
                                    UserRate(link, position, rating, review_string, pram, dialog);
                                }

                            }
                        });

                    }
                });
            }
        }

    }

    private void UserRate(String link, int position, float rating, String review, String pram, Dialog dialog) {

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", list.get(position).getOrderRequestID());
        body.put("ProductID", list.get(position).getProductID());
        body.put("Rating", String.valueOf(rating));
        body.put(pram, review);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, link, mContext, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Rating", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            dialog.dismiss();
                            final Dialog addressDialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            addressDialog.setContentView(R.layout.alert_text_layout);
                            Button ok = addressDialog.findViewById(R.id.btn_ok);
                            TextView address_text = addressDialog.findViewById(R.id.address_text);

                            address_text.setText(message);

                            addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addressDialog.show();

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressDialog.dismiss();
                                }
                            });

                        } else {
                            dialog.dismiss();
                            final Dialog addressDialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            addressDialog.setContentView(R.layout.alert_text_layout);
                            Button ok = addressDialog.findViewById(R.id.btn_ok);
                            TextView address_text = addressDialog.findViewById(R.id.address_text);

                            address_text.setText(message);

                            addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addressDialog.show();

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressDialog.dismiss();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        final Dialog addressDialog = new Dialog(mContext, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        addressDialog.setContentView(R.layout.alert_text_layout);
                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                        address_text.setText("Something went wrong!");

                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        addressDialog.show();

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addressDialog.dismiss();
                            }
                        });
                    }

                } else {
                    Toast.makeText(mContext, ERROR, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView producttitle, productItemCount, productItemPrice;
        LinearLayout item_layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            producttitle = itemView.findViewById(R.id.producttitle);
            productItemCount = itemView.findViewById(R.id.productItemCount);
            productItemPrice = itemView.findViewById(R.id.productItemPrice);
            item_layout = itemView.findViewById(R.id.item_layout);

        }
    }
}