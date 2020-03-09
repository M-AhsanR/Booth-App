package com.schopfen.Booth.Adapters;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.utility.Utility;
import com.google.gson.Gson;
import com.schopfen.Booth.Activities.ApproveOrderActivity;
import com.schopfen.Booth.Activities.BoothProfileActivity;
import com.schopfen.Booth.Activities.ChatActivity;
import com.schopfen.Booth.Activities.CheckOutActivity;
import com.schopfen.Booth.Activities.ContactUsActivity;
import com.schopfen.Booth.Activities.OthersProfileActivity;
import com.schopfen.Booth.Activities.PayActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.SummaryOrder;
import com.schopfen.Booth.Activities.UserSummaryOrder;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Fragments.Home_Home_Fragment;
import com.schopfen.Booth.Fragments.MyCurrent_Orders_Fragment;
import com.schopfen.Booth.Models.CancelationReasonModel;
import com.schopfen.Booth.Models.OrderItemsData;
import com.schopfen.Booth.Models.OrderRequestsData;
import com.schopfen.Booth.Models.OrdersData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class Fragment_Current_Order_Adapter extends RecyclerView.Adapter<Fragment_Current_Order_Adapter.MyViewHolder> {

    private Context context;
    ArrayList<OrdersData> ordersData;
    CRLCallbacks crlCallbacks;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String currency;


    public Fragment_Current_Order_Adapter(Context context, ArrayList<OrdersData> ordersData, CRLCallbacks crlCallbacks) {
        this.context = context;
        this.crlCallbacks = crlCallbacks;
        this.ordersData = ordersData;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.current_order_adapter, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (position == 0) {
            holder.uppergap.setVisibility(View.VISIBLE);
        } else {
            holder.uppergap.setVisibility(View.GONE);
        }

        if (sharedpreferences.getString("LastState", "").equals("user")) {
            Picasso.get().load(Constants.URL.IMG_URL + ordersData.get(position).getBoothImage()).placeholder(R.drawable.user).into(holder.profileImage);
            holder.username.setText("@" + ordersData.get(position).getBoothUserName());
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            Picasso.get().load(Constants.URL.IMG_URL + ordersData.get(position).getUserImage()).placeholder(R.color.grey5).into(holder.profileImage);
            holder.username.setText("@" + ordersData.get(position).getUserName());
        }
        holder.date.setText(TimeAgo.getTimeAgo(Long.parseLong(ordersData.get(position).getOrderReceivedAt())));
        holder.orderNumber.setText(ordersData.get(position).getOrderTrackID());

        holder.currency.setText(ordersData.get(position).getTotalAmount() + " " + ordersData.get(position).getOrderItems().get(0).getCurrency());

        if (!ordersData.get(position).getDeliveryDate().equals("")){
            holder.delivery_details_hint.setText(BaseClass.TimeStampToDate(ordersData.get(position).getDeliveryDate()));
        }else {
            holder.delivery_details_hint.setVisibility(View.GONE);
        }

        switch (ordersData.get(position).getOrderStatusID()) {
            case "1":
                holder.firstStatus.setBackground(context.getResources().getDrawable(R.drawable.order_status_completed_row));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    holder.cancleOrder.setVisibility(View.VISIBLE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.VISIBLE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                }
                break;
            case "2":
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.startingcompletedstatus));
                holder.secondStatus.setBackground(context.getResources().getDrawable(R.drawable.endingcompletedstatus));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.VISIBLE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                }
                break;
            case "3":
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.payment_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.startingcompletedstatus));
                holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.orderstatuscolor));
                holder.thirdStatus.setBackground(context.getResources().getDrawable(R.drawable.endingcompletedstatus));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));

                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.VISIBLE);
                    holder.complete_order.setVisibility(View.GONE);
                }

                break;
            case "7":
                holder.approval_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.payment_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.delivery_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.startingcompletedstatus));
                holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.orderstatuscolor));
                holder.orderdispatched.setBackground(context.getResources().getDrawable(R.drawable.endingcompletedstatus));
                holder.fourthStatus.setBackground(context.getResources().getDrawable(R.drawable.order_status_completed_row));
                holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                holder.fourthDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.GONE);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    holder.cancleOrder.setVisibility(View.GONE);
                    holder.approve_order.setVisibility(View.GONE);
                    holder.payment_btn.setVisibility(View.GONE);
                    holder.dispatche_order.setVisibility(View.GONE);
                    holder.complete_order.setVisibility(View.VISIBLE);
                }
                break;
        }

        holder.orderDetailsRV.setLayoutManager(new LinearLayoutManager(context));
        Order_Details_Adapter adapter = new Order_Details_Adapter(context, ordersData.get(position).getOrderItems());
        holder.orderDetailsRV.setAdapter(adapter);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                    context.startActivity(intent);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", ordersData.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    Intent intent = new Intent(context, BoothProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                    context.startActivity(intent);
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    Intent intent = new Intent(context, OthersProfileActivity.class);
                    mEditor.putString("Booth", "Other").apply();
                    intent.putExtra("OtherUserID", ordersData.get(position).getUserID());
                    context.startActivity(intent);
                }
            }
        });

        holder.message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                if (sharedpreferences.getString("LastState", "").equals("user")) {
                    intent.putExtra("OthersprofileID", ordersData.get(position).getBoothID());
                    intent.putExtra("UserName", ordersData.get(position).getBoothUserName());
                    intent.putExtra("usertype", sharedpreferences.getString("LastState", ""));
                    intent.putExtra("othertype", "booth");
                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                    intent.putExtra("OthersprofileID", ordersData.get(position).getUserID());
                    intent.putExtra("UserName", ordersData.get(position).getUserName());
                    intent.putExtra("usertype", sharedpreferences.getString("LastState", ""));
                    intent.putExtra("othertype", "user");
                }
                context.startActivity(intent);
            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.summary.getVisibility() == View.VISIBLE) {
                    holder.summary.setVisibility(View.GONE);
                    holder.orderDetailsRV.setVisibility(View.GONE);
                    holder.action_buttons_layout.setVisibility(View.GONE);
                    holder.arrow_down.setImageResource(R.drawable.arrow_down);
                } else {
                    holder.summary.setVisibility(View.VISIBLE);
                    holder.orderDetailsRV.setVisibility(View.VISIBLE);
                    holder.action_buttons_layout.setVisibility(View.VISIBLE);
                    holder.arrow_down.setImageResource(R.drawable.arrow_up);
                }
            }
        });

        if (sharedpreferences.getString("LastState", "").equals("user")) {
            holder.moreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ordersData.get(position).getOrderStatusID().equals("1")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:
                                        Intent intent = new Intent(context, UserSummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("activity2", "normal");
                                        intent.putExtra("username", ordersData.get(position).getFullName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                        intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("actualdeliverycharges", ordersData.get(position).getActualDeliveryCharges());
                                        intent.putExtra("additionaldeliverycharges", ordersData.get(position).getAdditionalDeliveryCharges());
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, BoothProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.complainOrder:
                                        final Dialog complaintDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        complaintDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button complaintDialogno = complaintDialog.findViewById(R.id.btn_no);
                                        Button complaintDialogyes = complaintDialog.findViewById(R.id.btn_yes);
                                        TextView complaintDialogalert_message = complaintDialog.findViewById(R.id.alert_message);

                                        complaintDialogalert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_do_complaint));

                                        complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        complaintDialog.show();

                                        complaintDialogno.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                complaintDialog.dismiss();
                                            }
                                        });

                                        complaintDialogyes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                complaintDialog.dismiss();
                                                Intent i = new Intent(context, ContactUsActivity.class);
                                                if (sharedpreferences.getString("LastState", "").equals("user")) {
                                                    mEditor.putString("id", "0").commit();
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("FullName", Home_Home_Fragment.fullName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                                    mEditor.putString("id", "1");
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("BoothName", Home_Home_Fragment.boothName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                }
                                                context.startActivity(i);
                                            }
                                        });
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("2")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.current_order_menu_full, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:
                                        Intent intent = new Intent(context, UserSummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("activity2", "normal");
                                        intent.putExtra("username", ordersData.get(position).getFullName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                        intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("actualdeliverycharges", ordersData.get(position).getActualDeliveryCharges());
                                        intent.putExtra("additionaldeliverycharges", ordersData.get(position).getAdditionalDeliveryCharges());
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("UserID", ordersData.get(position).getBoothID());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.cancleOrder:
                                        final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button no = deleteDialog.findViewById(R.id.btn_no);
                                        Button yes = deleteDialog.findViewById(R.id.btn_yes);
                                        TextView alert_message = deleteDialog.findViewById(R.id.alert_message);

                                        alert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_cancel_the_order));

                                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        deleteDialog.show();

                                        no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                            }
                                        });

                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                                CancelationProcess(position);
                                            }
                                        });
                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, BoothProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.complainOrder:
                                        final Dialog complaintDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        complaintDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button complaintDialogno = complaintDialog.findViewById(R.id.btn_no);
                                        Button complaintDialogyes = complaintDialog.findViewById(R.id.btn_yes);
                                        TextView complaintDialogalert_message = complaintDialog.findViewById(R.id.alert_message);

                                        complaintDialogalert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_do_complaint));

                                        complaintDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        complaintDialog.show();

                                        complaintDialogno.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                complaintDialog.dismiss();
                                            }
                                        });

                                        complaintDialogyes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                complaintDialog.dismiss();
                                                Intent i = new Intent(context, ContactUsActivity.class);
                                                if (sharedpreferences.getString("LastState", "").equals("user")) {
                                                    mEditor.putString("id", "0").commit();
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("FullName", Home_Home_Fragment.fullName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                                    mEditor.putString("id", "1");
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("BoothName", Home_Home_Fragment.boothName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                }
                                                context.startActivity(i);
                                            }
                                        });
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("3")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:
                                        Intent intent = new Intent(context, UserSummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("activity2", "normal");
                                        intent.putExtra("username", ordersData.get(position).getFullName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                        intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("actualdeliverycharges", ordersData.get(position).getActualDeliveryCharges());
                                        intent.putExtra("additionaldeliverycharges", ordersData.get(position).getAdditionalDeliveryCharges());
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent);

                                        break;

                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, BoothProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.complainOrder:
                                        final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button no = deleteDialog.findViewById(R.id.btn_no);
                                        Button yes = deleteDialog.findViewById(R.id.btn_yes);
                                        TextView alert_message = deleteDialog.findViewById(R.id.alert_message);

                                        alert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_do_complaint));

                                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        deleteDialog.show();

                                        no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                            }
                                        });

                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                                Intent i = new Intent(context, ContactUsActivity.class);
                                                if (sharedpreferences.getString("LastState", "").equals("user")) {
                                                    mEditor.putString("id", "0").commit();
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("FullName", Home_Home_Fragment.fullName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                                    mEditor.putString("id", "1");
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("BoothName", Home_Home_Fragment.boothName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                }
                                                context.startActivity(i);
                                            }
                                        });
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("7")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:
                                        Intent intent = new Intent(context, UserSummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("activity2", "normal");
                                        intent.putExtra("username", ordersData.get(position).getFullName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                        intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("actualdeliverycharges", ordersData.get(position).getActualDeliveryCharges());
                                        intent.putExtra("additionaldeliverycharges", ordersData.get(position).getAdditionalDeliveryCharges());
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, BoothProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getBoothID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.complainOrder:
                                        final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button no = deleteDialog.findViewById(R.id.btn_no);
                                        Button yes = deleteDialog.findViewById(R.id.btn_yes);
                                        TextView alert_message = deleteDialog.findViewById(R.id.alert_message);

                                        alert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_do_complaint));

                                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        deleteDialog.show();

                                        no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                            }
                                        });

                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                                Intent i = new Intent(context, ContactUsActivity.class);
                                                if (sharedpreferences.getString("LastState", "").equals("user")) {
                                                    mEditor.putString("id", "0").commit();
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("FullName", Home_Home_Fragment.fullName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                                    mEditor.putString("id", "1");
                                                    mEditor.putString("orderNumber", ordersData.get(position).getOrderTrackID()).commit();
                                                    mEditor.putString("BoothName", Home_Home_Fragment.boothName).commit();
                                                    mEditor.putString("Email", Home_Home_Fragment.email).commit();
                                                    mEditor.putString("Mobile", Home_Home_Fragment.mobile).commit();
                                                    mEditor.putString("Activity", "ComplainOrder").commit();
                                                }
                                                context.startActivity(i);
                                            }
                                        });

                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
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
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            holder.moreMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ordersData.get(position).getOrderStatusID().equals("1")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.booth_current_order_menu_full, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:

                                        float deliveryCharges = 0;
                                        for (int i = 0; i < ordersData.get(position).getOrderItems().size(); i++) {
                                            float charges = Float.valueOf(ordersData.get(position).getOrderItems().get(i).getDeliveryCharges());
                                            deliveryCharges = deliveryCharges + charges;
                                        }
                                        Intent intent = new Intent(context, SummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("username", ordersData.get(position).getUserName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        if (ordersData.get(position).getAddressLatitude() != null && !ordersData.get(position).getAddressLatitude().equals("")) {
                                            intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                            intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        } else {
                                            intent.putExtra("longitude", "0.0");
                                            intent.putExtra("latitude", "0.0");
                                        }
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("UserID", ordersData.get(position).getUserID());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.cancleOrder:
                                        final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                                        Button no = deleteDialog.findViewById(R.id.btn_no);
                                        Button yes = deleteDialog.findViewById(R.id.btn_yes);
                                        TextView alert_message = deleteDialog.findViewById(R.id.alert_message);

                                        alert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_cancel_the_order));

                                        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        deleteDialog.show();

                                        no.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                            }
                                        });

                                        yes.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                deleteDialog.dismiss();
                                                CancelationProcess(position);
                                            }
                                        });
                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, OthersProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("2")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.booth_current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:

                                        float deliveryCharges = 0;
                                        for (int i = 0; i < ordersData.get(position).getOrderItems().size(); i++) {
                                            float charges = Float.valueOf(ordersData.get(position).getOrderItems().get(i).getDeliveryCharges());
                                            deliveryCharges = deliveryCharges + charges;
                                        }
                                        Intent intent = new Intent(context, SummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("username", ordersData.get(position).getUserName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        if (ordersData.get(position).getAddressLatitude() != null && !ordersData.get(position).getAddressLatitude().equals("")) {
                                            intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                            intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        } else {
                                            intent.putExtra("longitude", "0.0");
                                            intent.putExtra("latitude", "0.0");
                                        }
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, OthersProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("3")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.booth_current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:

                                        float deliveryCharges = 0;
                                        for (int i = 0; i < ordersData.get(position).getOrderItems().size(); i++) {
                                            float charges = Float.valueOf(ordersData.get(position).getOrderItems().get(i).getDeliveryCharges());
                                            deliveryCharges = deliveryCharges + charges;
                                        }
                                        Intent intent = new Intent(context, SummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("username", ordersData.get(position).getUserName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        if (ordersData.get(position).getAddressLatitude() != null && !ordersData.get(position).getAddressLatitude().equals("")) {
                                            intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                            intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        } else {
                                            intent.putExtra("longitude", "0.0");
                                            intent.putExtra("latitude", "0.0");
                                        }
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, OthersProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
                                            }
                                        });
                                        break;
                                }
                                return false;
                            }
                        });
                        projMangMore.show();
                    } else if (ordersData.get(position).getOrderStatusID().equals("7")) {
                        final PopupMenu projMangMore = new PopupMenu(context, view);
                        projMangMore.getMenuInflater().inflate(R.menu.booth_current_order_menu_half, projMangMore.getMenu());

                        projMangMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.viewdetail:

                                        float deliveryCharges = 0;
                                        for (int i = 0; i < ordersData.get(position).getOrderItems().size(); i++) {
                                            float charges = Float.valueOf(ordersData.get(position).getOrderItems().get(i).getDeliveryCharges());
                                            deliveryCharges = deliveryCharges + charges;
                                        }
                                        Intent intent = new Intent(context, SummaryOrder.class);
                                        Gson gson = new Gson();
                                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                                        intent.putExtra("array", array);
                                        intent.putExtra("activity", "normal");
                                        intent.putExtra("username", ordersData.get(position).getUserName());
                                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                                        if (ordersData.get(position).getAddressLatitude() != null && !ordersData.get(position).getAddressLatitude().equals("")) {
                                            intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                                            intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                                        } else {
                                            intent.putExtra("longitude", "0.0");
                                            intent.putExtra("latitude", "0.0");
                                        }
                                        intent.putExtra("boothname", ordersData.get(position).getBoothUserName());
                                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                                        intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                                        intent.putExtra("UserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent);

                                        break;
                                    case R.id.viewProfile:
                                        Intent intent1 = new Intent(context, OthersProfileActivity.class);
                                        mEditor.putString("Booth", "Other").apply();
                                        intent1.putExtra("OtherUserID", ordersData.get(position).getUserID());
                                        context.startActivity(intent1);
                                        break;
                                    case R.id.viewAddress:
                                        final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                        addressDialog.setContentView(R.layout.dialog_address_show);
                                        Button ok = addressDialog.findViewById(R.id.btn_ok);
                                        TextView address_text = addressDialog.findViewById(R.id.address_text);

                                        address_text.setText(ordersData.get(position).getAddress1());

                                        addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        addressDialog.show();

                                        ok.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                addressDialog.dismiss();
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

        holder.dispatche_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                deleteDialog.setContentView(R.layout.dialog_dispatch_order_item);
                Button no = deleteDialog.findViewById(R.id.btn_no);
                Button yes = deleteDialog.findViewById(R.id.btn_yes);

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DispatchOrder(position, holder);
                        deleteDialog.dismiss();
                    }
                });
            }
        });

        holder.complete_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                deleteDialog.setContentView(R.layout.dialog_complete_order_item);
                EditText codeTextView = deleteDialog.findViewById(R.id.codeTextView);
                Button yes = deleteDialog.findViewById(R.id.btn_yes);

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (codeTextView.getText().toString().isEmpty()) {
                            Toast.makeText(deleteDialog.getContext(), "Code field is empty", Toast.LENGTH_SHORT).show();
                        } else {
                            CompleteOrder(position, holder, codeTextView.getText().toString());
                            deleteDialog.dismiss();
                        }
                    }
                });
            }
        });

        holder.cancleOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                Button no = deleteDialog.findViewById(R.id.btn_no);
                Button yes = deleteDialog.findViewById(R.id.btn_yes);
                TextView alert_message = deleteDialog.findViewById(R.id.alert_message);

                alert_message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_cancel_the_order));

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                        CancelationProcess(position);
                    }
                });

            }
        });

        holder.approve_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                deleteDialog.setContentView(R.layout.dialog_approve_order_item);
                Button no = deleteDialog.findViewById(R.id.btn_no);
                Button yes = deleteDialog.findViewById(R.id.btn_yes);

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();

                        float deliveryCharges = 0;
                        for (int i = 0; i < ordersData.get(position).getOrderItems().size(); i++) {
                            float charges = Float.valueOf(ordersData.get(position).getOrderItems().get(i).getDeliveryCharges());
                            deliveryCharges = deliveryCharges + charges;
                        }

                        Intent intent = new Intent(context, ApproveOrderActivity.class);
                        Gson gson = new Gson();
                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                        intent.putExtra("array", array);
                        intent.putExtra("username", ordersData.get(position).getFullName());
                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                        if (ordersData.get(position).getAddressLatitude() != null && !ordersData.get(position).getAddressLatitude().equals("")) {
                            intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                            intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                        } else {
                            intent.putExtra("longitude", "0.0");
                            intent.putExtra("latitude", "0.0");
                        }
                        intent.putExtra("boothname", ordersData.get(position).getBoothName());
                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                        intent.putExtra("deliverycharges", String.valueOf(deliveryCharges));
                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                        context.startActivity(intent);
                    }
                });

            }
        });

        holder.payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                deleteDialog.setContentView(R.layout.dialog_payment_order_item);
                Button no = deleteDialog.findViewById(R.id.btn_no);
                Button yes = deleteDialog.findViewById(R.id.btn_yes);

                deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                deleteDialog.show();

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteDialog.dismiss();

                        Intent intent = new Intent(context, PayActivity.class);
                        Gson gson = new Gson();
                        String array = gson.toJson(ordersData.get(position).getOrderItems());
                        intent.putExtra("array", array);
                        intent.putExtra("username", ordersData.get(position).getFullName());
                        intent.putExtra("address", ordersData.get(position).getApartmentNo() + ordersData.get(position).getAddress1() + ordersData.get(position).getAddress2() + ordersData.get(position).getAddressCity());
                        intent.putExtra("mobile", ordersData.get(position).getAddressMobile());
                        intent.putExtra("email", ordersData.get(position).getAddressEmail());
                        intent.putExtra("longitude", ordersData.get(position).getAddressLongitude());
                        intent.putExtra("latitude", ordersData.get(position).getAddressLatitude());
                        intent.putExtra("boothname", ordersData.get(position).getBoothName());
                        intent.putExtra("totalitems", String.valueOf(ordersData.get(position).getOrderItems().size()));
                        intent.putExtra("vatpercentage", ordersData.get(position).getVatPercentage());
                        intent.putExtra("actualdeliverycharges", ordersData.get(position).getActualDeliveryCharges());
                        intent.putExtra("additionaldeliverycharges", ordersData.get(position).getAdditionalDeliveryCharges());
                        intent.putExtra("discount", ordersData.get(position).getDiscount());
                        intent.putExtra("orderRequestID", ordersData.get(position).getOrderRequestID());
                        context.startActivity(intent);

                    }
                });

            }
        });


    }

    private void CancelationProcess(int position) {

        final Dialog deleteDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        deleteDialog.setContentView(R.layout.dialog_delete_order_item);
        Button yes = deleteDialog.findViewById(R.id.btn_yes);
        RecyclerView reasons_list = deleteDialog.findViewById(R.id.reasons_list);
        ProgressBar progressbar = deleteDialog.findViewById(R.id.progressbar);

        progressbar.setVisibility(View.VISIBLE);
        reasons_list.setVisibility(View.GONE);

        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.setCancelable(false);
        deleteDialog.show();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                                    if (reasonID.isEmpty()) {
//                                        Toast.makeText(context, "Select any reason first", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        CancelOrder(position, reasonID);
//                                        deleteDialog.dismiss();
//                                    }
                deleteDialog.dismiss();
            }
        });


        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.cancellation_reasons + sharedpreferences.getString("UserID", "") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("cancelReasons", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONArray cancellation_reasons = jsonObject.getJSONArray("cancellation_reasons");

                            ArrayList<CancelationReasonModel> reasonModelArrayList = new ArrayList<>();
                            for (int i = 0; i < cancellation_reasons.length(); i++) {
                                JSONObject jsonObject1 = cancellation_reasons.getJSONObject(i);
                                String OrderCancellationReasonID = jsonObject1.getString("OrderCancellationReasonID");
                                String CancellationReasonEn = jsonObject1.getString("CancellationReasonEn");
                                String CancellationReasonAr = jsonObject1.getString("CancellationReasonAr");
                                String IsActive = jsonObject1.getString("IsActive");
                                reasonModelArrayList.add(new CancelationReasonModel(OrderCancellationReasonID, CancellationReasonEn, CancellationReasonAr, IsActive));

                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            reasons_list.setLayoutManager(linearLayoutManager);
                            ReasonSelectionAdapter reasonSelectionAdapter = new ReasonSelectionAdapter(context, reasonModelArrayList, new ReasonSelectionAdapter.CustomItemClickListener() {
                                @Override
                                public void onItemClick(View v, int innerPosition) {

//                                    if (!reasonID.isEmpty()){
//                                        for (int i = 0; i < reasonModelArrayList.size(); i++){
//                                            if (reasonModelArrayList.get(position).getOrderCancellationReasonID().equals(reasonID)){
//                                                reasonID = "";
//                                            }
//                                        }
//                                    }else {
//                                        reasonID = reasonModelArrayList.get(position).getOrderCancellationReasonID();
//                                    }
                                    progressbar.setVisibility(View.VISIBLE);
                                    reasons_list.setVisibility(View.GONE);
                                    CancelOrder(position, reasonModelArrayList.get(innerPosition).getOrderCancellationReasonID(), deleteDialog);

                                }
                            });
                            reasons_list.setAdapter(reasonSelectionAdapter);

                            progressbar.setVisibility(View.GONE);
                            reasons_list.setVisibility(View.VISIBLE);

                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            progressbar.setVisibility(View.GONE);
                            reasons_list.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressbar.setVisibility(View.GONE);
                        reasons_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                    reasons_list.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void CancelOrder(int position, String reason, Dialog dialog) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", ordersData.get(position).getOrderRequestID());
        body.put("OrderCancellationReasonID", reason);
        if (sharedpreferences.getString("LastState", "").equals("user")) {
            body.put("OrderStatus", "6");
        } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
            body.put("OrderStatus", "5");
        }
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Canceled", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            String message = jsonObject.getString("message");

                            dialog.dismiss();

                            final Dialog addressDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            addressDialog.setContentView(R.layout.alert_text_layout);
                            Button ok = addressDialog.findViewById(R.id.btn_ok);
                            TextView address_text = addressDialog.findViewById(R.id.address_text);

                            address_text.setText(message);

                            addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            addressDialog.setCancelable(false);
                            addressDialog.show();

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addressDialog.dismiss();
                                    ordersData.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, ordersData.size());
                                    if (ordersData.isEmpty()) {
                                        MyCurrent_Orders_Fragment.bottomNavigationView.getBadge(R.id.orders).setVisible(false);
                                    }
                                }
                            });

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }

    private void PaymentDone(int position, MyViewHolder holder) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", ordersData.get(position).getOrderRequestID());
        body.put("OrderStatus", "3");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Canceled", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");

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

                            holder.approval_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                            holder.payment_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                            holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.startingcompletedstatus));
                            holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.orderstatuscolor));
                            holder.thirdStatus.setBackground(context.getResources().getDrawable(R.drawable.endingcompletedstatus));
                            holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));

                            if (sharedpreferences.getString("LastState", "").equals("user")) {
                                holder.cancleOrder.setVisibility(View.GONE);
                                holder.approve_order.setVisibility(View.GONE);
                                holder.payment_btn.setVisibility(View.GONE);
                                holder.dispatche_order.setVisibility(View.GONE);
                                holder.complete_order.setVisibility(View.GONE);
                            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                holder.cancleOrder.setVisibility(View.GONE);
                                holder.approve_order.setVisibility(View.GONE);
                                holder.payment_btn.setVisibility(View.GONE);
                                holder.dispatche_order.setVisibility(View.VISIBLE);
                                holder.complete_order.setVisibility(View.GONE);
                            }

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void DispatchOrder(int position, MyViewHolder holder) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", ordersData.get(position).getOrderRequestID());
        body.put("OrderStatus", "7");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Canceled", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            String message = jsonObject.getString("message");

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

                            holder.approval_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                            holder.payment_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                            holder.delivery_details.setTextColor(context.getResources().getColor(R.color.orderstatustextcolor));
                            holder.approved_pendingpayment.setBackground(context.getResources().getDrawable(R.drawable.startingcompletedstatus));
                            holder.approved_paymentdone.setBackgroundColor(context.getResources().getColor(R.color.orderstatuscolor));
                            holder.orderdispatched.setBackground(context.getResources().getDrawable(R.drawable.endingcompletedstatus));
                            holder.fourthStatus.setBackground(context.getResources().getDrawable(R.drawable.order_status_completed_row));
                            holder.firstDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            holder.secondDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            holder.thirdDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            holder.fourthDot.setBackground(context.getResources().getDrawable(R.drawable.selected_circle_orders));
                            if (sharedpreferences.getString("LastState", "").equals("user")) {
                                holder.cancleOrder.setVisibility(View.GONE);
                                holder.approve_order.setVisibility(View.GONE);
                                holder.payment_btn.setVisibility(View.GONE);
                                holder.dispatche_order.setVisibility(View.GONE);
                                holder.complete_order.setVisibility(View.GONE);
                            } else if (sharedpreferences.getString("LastState", "").equals("booth")) {
                                holder.cancleOrder.setVisibility(View.GONE);
                                holder.approve_order.setVisibility(View.GONE);
                                holder.payment_btn.setVisibility(View.GONE);
                                holder.dispatche_order.setVisibility(View.GONE);
                                holder.complete_order.setVisibility(View.VISIBLE);
                            }

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void CompleteOrder(int position, MyViewHolder holder, String code) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("OrderRequestID", ordersData.get(position).getOrderRequestID());
        body.put("OrderStatus", "4");
        body.put("VerificationCode", code);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.UPDATEORDERSTATE, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("Canceled", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String message = jsonObject.getString("message");
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
                                    ordersData.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, ordersData.size());
                                    if (ordersData.isEmpty()) {
                                        MyCurrent_Orders_Fragment.bottomNavigationView.getBadge(R.id.orders).setVisible(false);
                                    }
                                }
                            });

                        } else {

                            String message = jsonObject.getString("message");
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView delivery_details_hint, username, summary, date, orderNumber, cancleOrder, approval_details, payment_details, delivery_details, currency, approve_order, payment_btn, dispatche_order, complete_order;
        ImageView moreMenu, arrow_down;
        RecyclerView orderDetailsRV;
        View approved_pendingpayment, approved_paymentdone, orderdispatched, firstDot, secondDot, thirdDot, fourthDot;
        LinearLayout mainLayout, uppergap;
        RelativeLayout firstStatus, secondStatus, thirdStatus, fourthStatus, message_layout, action_buttons_layout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.userProfileImage);
            username = itemView.findViewById(R.id.userNameTextView);
            arrow_down = itemView.findViewById(R.id.arrow_down);
            delivery_details_hint = itemView.findViewById(R.id.delivery_details_hint);
            summary = itemView.findViewById(R.id.summaryTextView);
            date = itemView.findViewById(R.id.dateTextView);
            orderNumber = itemView.findViewById(R.id.orderNumberTextView);
            cancleOrder = itemView.findViewById(R.id.cancel_order);
            approve_order = itemView.findViewById(R.id.approve_order);
            approval_details = itemView.findViewById(R.id.approval_details);
            payment_details = itemView.findViewById(R.id.payment_details);
            delivery_details = itemView.findViewById(R.id.delivery_details);
            moreMenu = itemView.findViewById(R.id.moreMenuIcon);
            orderDetailsRV = itemView.findViewById(R.id.orderDetailRecycler);
            approved_pendingpayment = itemView.findViewById(R.id.approved_pendingpayment);
            approved_paymentdone = itemView.findViewById(R.id.approved_paymentdone);
            orderdispatched = itemView.findViewById(R.id.orderdispatched);
            mainLayout = itemView.findViewById(R.id.mainlayout);
            action_buttons_layout = itemView.findViewById(R.id.action_buttons_layout);
            currency = itemView.findViewById(R.id.currencyTextView);
            firstStatus = itemView.findViewById(R.id.orderfirststatus);
            firstDot = itemView.findViewById(R.id.firstDot);
            secondDot = itemView.findViewById(R.id.secondDot);
            thirdDot = itemView.findViewById(R.id.thirdDot);
            fourthDot = itemView.findViewById(R.id.fourthDot);
            secondStatus = itemView.findViewById(R.id.orderSecondStatus);
            thirdStatus = itemView.findViewById(R.id.orderThirdStatus);
            fourthStatus = itemView.findViewById(R.id.orderFourthStatus);
            uppergap = itemView.findViewById(R.id.uppergap);
            payment_btn = itemView.findViewById(R.id.payment_btn);
            dispatche_order = itemView.findViewById(R.id.dispatche_order);
            complete_order = itemView.findViewById(R.id.complete_order);
            message_layout = itemView.findViewById(R.id.message_layout);

        }
    }

    public interface CRLCallbacks {
        void onItemClick(int position);
    }
}



