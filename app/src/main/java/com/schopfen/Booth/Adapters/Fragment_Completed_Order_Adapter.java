package com.schopfen.Booth.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.SummaryOrder;
import com.schopfen.Booth.Activities.UserSummaryOrder;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.Fragment_Completed_Order_Model;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.Models.OrdersData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.CustomItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

public class Fragment_Completed_Order_Adapter extends RecyclerView.Adapter<Fragment_Completed_Order_Adapter.MyViewHolder> {

    private Context context;
    ArrayList<OrdersData> list;
    ArrayList<OrderItemsData> tempOrderList = new ArrayList<>();

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    public Fragment_Completed_Order_Adapter(Context context, ArrayList<OrdersData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment_Completed_Order_Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.completed_order_adapter, parent, false);
        final Fragment_Completed_Order_Adapter.MyViewHolder viewHolder = new Fragment_Completed_Order_Adapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Fragment_Completed_Order_Adapter.MyViewHolder holder, int position) {

        tempOrderList = list.get(position).getOrderItems();

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        holder.date.setText(BaseClass.TimeStampToDate(list.get(position).getOrderReceivedAt()));
        holder.orderNumber.setText(list.get(position).getOrderTrackID());

        int totalCurrency = 0;
        for (int i = 0; i < list.get(position).getOrderItems().size(); i++) {
            totalCurrency = totalCurrency + Integer.parseInt(list.get(position).getOrderItems().get(i).getPrice());
        }

        holder.currencyText.setText(String.valueOf(totalCurrency) + " " + list.get(position).getOrderItems().get(0).getCurrency());

        holder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    Intent intent = new Intent(context, UserSummaryOrder.class);
                    Gson gson = new Gson();
                    String array = gson.toJson(list.get(position).getOrderItems());
                    intent.putExtra("array", array);
                    intent.putExtra("activity", "normal");
                    intent.putExtra("activity2", "Completed");
                    intent.putExtra("username", list.get(position).getFullName());
                    intent.putExtra("address", list.get(position).getApartmentNo() + list.get(position).getAddress1() + list.get(position).getAddress2() + list.get(position).getAddressCity());
                    intent.putExtra("mobile", list.get(position).getAddressMobile());
                    intent.putExtra("email", list.get(position).getAddressEmail());
                    intent.putExtra("longitude", list.get(position).getAddressLongitude());
                    intent.putExtra("latitude", list.get(position).getAddressLatitude());
                    intent.putExtra("boothname", list.get(position).getBoothUserName());
                    intent.putExtra("totalitems", String.valueOf(list.get(position).getOrderItems().size()));
                    intent.putExtra("vatpercentage", list.get(position).getVatPercentage());
                    intent.putExtra("actualdeliverycharges", list.get(position).getActualDeliveryCharges());
                    intent.putExtra("additionaldeliverycharges", list.get(position).getAdditionalDeliveryCharges());
                    intent.putExtra("discount", list.get(position).getDiscount());
                    intent.putExtra("orderRequestID", list.get(position).getOrderRequestID());
                    intent.putExtra("UserID", list.get(position).getBoothID());
                    context.startActivity(intent);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    float deliveryCharges = 0;
                    for (int i = 0; i < list.get(position).getOrderItems().size(); i++) {
                        float charges = Float.valueOf(list.get(position).getOrderItems().get(i).getDeliveryCharges());
                        deliveryCharges = deliveryCharges + charges;
                    }
                    Intent intent = new Intent(context, SummaryOrder.class);
                    Gson gson = new Gson();
                    String array = gson.toJson(list.get(position).getOrderItems());
                    intent.putExtra("array", array);
                    intent.putExtra("activity", "normal");
                    intent.putExtra("username", list.get(position).getUserName());
                    intent.putExtra("address", list.get(position).getApartmentNo() + list.get(position).getAddress1() + list.get(position).getAddress2() + list.get(position).getAddressCity());
                    intent.putExtra("mobile", list.get(position).getAddressMobile());
                    intent.putExtra("email", list.get(position).getAddressEmail());
                    if (list.get(position).getAddressLatitude() != null && !list.get(position).getAddressLatitude().equals("")) {
                        intent.putExtra("longitude", list.get(position).getAddressLongitude());
                        intent.putExtra("latitude", list.get(position).getAddressLatitude());
                    } else {
                        intent.putExtra("longitude", "0.0");
                        intent.putExtra("latitude", "0.0");
                    }
                    intent.putExtra("boothname", list.get(position).getBoothUserName());
                    intent.putExtra("totalitems", String.valueOf(list.get(position).getOrderItems().size()));
                    intent.putExtra("vatpercentage", list.get(position).getVatPercentage());
                    intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                    intent.putExtra("discount", list.get(position).getDiscount());
                    intent.putExtra("orderRequestID", list.get(position).getOrderRequestID());
                    intent.putExtra("UserID", list.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        holder.more_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PopupMenu projMangMore = new PopupMenu(context, view);
                projMangMore.getMenuInflater().inflate(R.menu.completed_menu, projMangMore.getMenu());

                projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.review:
                                if (sharedpreferences.getString("LastState", "").equals("user")) {
                                    final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    dialog.setContentView(R.layout.review_dialog);
                                    Button ok = dialog.findViewById(R.id.dialog_submit_btn);
                                    TextView heading = dialog.findViewById(R.id.dialog_heading);
                                    TextView textView = dialog.findViewById(R.id.dialog_text);
                                    RatingBar ratingBar = dialog.findViewById(R.id.dialog_ratingBar);
                                    ProgressBar dialog_progressBar = dialog.findViewById(R.id.dialog_progressBar);
                                    TextView review = dialog.findViewById(R.id.dialog_review_text);
                                    LinearLayout rating_layout = dialog.findViewById(R.id.rating_layout);
                                    LinearLayout progressbarRating = dialog.findViewById(R.id.progressbarRating);

                                    heading.setText(context.getResources().getString(R.string.how_was_the_booth));
                                    textView.setText(context.getResources().getString(R.string.rate_the_booth));

                                    rating_layout.setVisibility(View.VISIBLE);
                                    progressbarRating.setVisibility(View.GONE);

                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    dialog.setCancelable(false);
                                    dialog.show();

                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ratingBar.getRating() < 1) {
                                                Toast.makeText(context, "Rating should be minimum 1.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                ok.setVisibility(View.GONE);
                                                dialog_progressBar.setVisibility(View.VISIBLE);

                                                float rating = ratingBar.getRating();
                                                String review_string = review.getText().toString();
                                                String link = Constants.URL.rateOrderRequest;
                                                String pram = "OrderRequestReview";
                                                String pram2 = "OrderRequestRating";
                                                UserRate(link, position, rating, review_string, pram, pram2, dialog, rating_layout, progressbarRating, "user");
                                            }

                                        }
                                    });
                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                    final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                    dialog.setContentView(R.layout.review_dialog);
                                    Button ok = dialog.findViewById(R.id.dialog_submit_btn);
                                    TextView heading = dialog.findViewById(R.id.dialog_heading);
                                    TextView textView = dialog.findViewById(R.id.dialog_text);
                                    RatingBar ratingBar = dialog.findViewById(R.id.dialog_ratingBar);
                                    ProgressBar dialog_progressBar = dialog.findViewById(R.id.dialog_progressBar);
                                    TextView review = dialog.findViewById(R.id.dialog_review_text);
                                    LinearLayout rating_layout = dialog.findViewById(R.id.rating_layout);
                                    LinearLayout progressbarRating = dialog.findViewById(R.id.progressbarRating);

                                    heading.setText(context.getResources().getString(R.string.how_was_the_customer));
                                    textView.setText(context.getResources().getString(R.string.rate_the_customer));

                                    rating_layout.setVisibility(View.VISIBLE);
                                    progressbarRating.setVisibility(View.GONE);

                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                                    dialog.setCancelable(false);
                                    dialog.show();

                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (ratingBar.getRating() < 1) {
                                                Toast.makeText(context, context.getResources().getString(R.string.ratingsouldbeminone), Toast.LENGTH_SHORT).show();
                                            } else {
                                                ok.setVisibility(View.GONE);
                                                dialog_progressBar.setVisibility(View.VISIBLE);

                                                float rating = ratingBar.getRating();
                                                String review_string = review.getText().toString();
                                                String link = Constants.URL.rateUserForOrderRequest;
                                                String pram = "UserOrderRequestReview";
                                                String pram2 = "UserOrderRequestRating";
                                                UserRate(link, position, rating, review_string, pram, pram2, dialog, rating_layout, progressbarRating, "booth");
                                            }

                                        }
                                    });
                                }
                                break;
                        }
                        return false;
                    }
                });
                projMangMore.show();
            }
        });
    }

    private void ProductRate(String link, int position, float rating, String review, String pram, Dialog dialog, LinearLayout rating_layout, LinearLayout progress_layout) {

        rating_layout.setVisibility(View.GONE);
        progress_layout.setVisibility(View.VISIBLE);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", tempOrderList.get(position).getOrderRequestID());
        body.put("ProductID", tempOrderList.get(position).getProductID());
        body.put("Rating", String.valueOf(rating));
        body.put(pram, review);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, link, context, body, headers, new ServerCallback() {
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
                            final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                    tempOrderList.remove(0);
                                    if (!tempOrderList.isEmpty()) {
                                        for (int i = 0; i < tempOrderList.size(); i++) {
                                            final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                            dialog.setContentView(R.layout.review_dialog);
                                            Button ok = dialog.findViewById(R.id.dialog_submit_btn);
                                            TextView heading = dialog.findViewById(R.id.dialog_heading);
                                            TextView textView = dialog.findViewById(R.id.dialog_text);
                                            RatingBar ratingBar = dialog.findViewById(R.id.dialog_ratingBar);
                                            ProgressBar dialog_progressBar = dialog.findViewById(R.id.dialog_progressBar);
                                            TextView review = dialog.findViewById(R.id.dialog_review_text);
                                            LinearLayout rating_layout = dialog.findViewById(R.id.rating_layout);
                                            LinearLayout progressbarRating = dialog.findViewById(R.id.progressbarRating);
                                            int Proposition = i;
                                            rating_layout.setVisibility(View.VISIBLE);
                                            progressbarRating.setVisibility(View.GONE);

                                            heading.setText(context.getResources().getString(R.string.how_was_the_product));
                                            textView.setText(context.getResources().getString(R.string.rate_the_product));

                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.setCancelable(false);
                                            dialog.show();

                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (ratingBar.getRating() < 1) {
                                                        Toast.makeText(context, context.getResources().getString(R.string.ratingsouldbeminone), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        ok.setVisibility(View.GONE);
                                                        dialog_progressBar.setVisibility(View.VISIBLE);

                                                        float rating = ratingBar.getRating();
                                                        String review_string = review.getText().toString();
                                                        String link = Constants.URL.rateProduct;
                                                        String pram = "Review";
                                                        ProductRate(link, Proposition, rating, review_string, pram, dialog, rating_layout, progressbarRating);
                                                    }

                                                }
                                            });

                                            break;
                                        }
                                    }
                                }
                            });

                        } else {
                            dialog.dismiss();
                            final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

    }

    private void UserRate(String link, int position, float rating, String review, String pram, String pram2, Dialog dialog, LinearLayout rating_linear, LinearLayout progressRating, String user) {

        rating_linear.setVisibility(View.GONE);
        progressRating.setVisibility(View.VISIBLE);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", list.get(position).getOrderRequestID());
        body.put(pram2, String.valueOf(rating));
        body.put(pram, review);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, link, context, body, headers, new ServerCallback() {
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
                            final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                    if (user.equals("user")){
                                        for (int i = 0; i < tempOrderList.size(); i++) {
                                            final Dialog dialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                            dialog.setContentView(R.layout.review_dialog);
                                            Button ok = dialog.findViewById(R.id.dialog_submit_btn);
                                            TextView heading = dialog.findViewById(R.id.dialog_heading);
                                            TextView textView = dialog.findViewById(R.id.dialog_text);
                                            RatingBar ratingBar = dialog.findViewById(R.id.dialog_ratingBar);
                                            ProgressBar dialog_progressBar = dialog.findViewById(R.id.dialog_progressBar);
                                            TextView review = dialog.findViewById(R.id.dialog_review_text);
                                            LinearLayout rating_layout = dialog.findViewById(R.id.rating_layout);
                                            LinearLayout progressbarRating = dialog.findViewById(R.id.progressbarRating);
                                            int Proposition = i;
                                            rating_layout.setVisibility(View.VISIBLE);
                                            progressbarRating.setVisibility(View.GONE);

                                            heading.setText(context.getResources().getString(R.string.how_was_the_product));
                                            textView.setText(context.getResources().getString(R.string.rate_the_product));

                                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                            dialog.setCancelable(false);
                                            dialog.show();

                                            ok.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (ratingBar.getRating() < 1) {
                                                        Toast.makeText(context, context.getResources().getString(R.string.ratingsouldbeminone), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        ok.setVisibility(View.GONE);
                                                        dialog_progressBar.setVisibility(View.VISIBLE);

                                                        float rating = ratingBar.getRating();
                                                        String review_string = review.getText().toString();
                                                        String link = Constants.URL.rateProduct;
                                                        String pram = "Review";
                                                        ProductRate(link, Proposition, rating, review_string, pram, dialog, rating_layout, progressbarRating);
                                                    }

                                                }
                                            });
                                            break;
                                        }

                                    }
                                }
                            });

                        } else {
                            dialog.dismiss();
                            final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                        addressDialog.setContentView(R.layout.alert_text_layout);
                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                        address_text.setText(context.getResources().getString(R.string.somethingwentwrong));

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
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView more_menu;
        TextView date, orderNumber, currencyText;
        RelativeLayout mainlayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            more_menu = itemView.findViewById(R.id.more_menu);
            date = itemView.findViewById(R.id.dateTextView);
            orderNumber = itemView.findViewById(R.id.orderNumberTextView);
            currencyText = itemView.findViewById(R.id.currencyTextView);
            mainlayout = itemView.findViewById(R.id.mainlayout);
        }
    }

}