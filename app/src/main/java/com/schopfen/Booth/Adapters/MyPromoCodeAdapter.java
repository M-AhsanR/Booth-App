package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.MyPromoCodes;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.MyPromoModel;
import com.schopfen.Booth.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyPromoCodeAdapter extends RecyclerView.Adapter<MyPromoCodeAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyPromoModel> list;
    MyPromoCodeAdapter.CustomItemClickListener listener;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String activity;
    String currency;
    String colorCode;

    public MyPromoCodeAdapter(String ColorCode, String Currency, String Activity, Context context, ArrayList<MyPromoModel> list, MyPromoCodeAdapter.CustomItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
        this.activity = Activity;
        this.currency = Currency;
        this.colorCode = ColorCode;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.my_promo_code_adapter, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (!colorCode.isEmpty()){
            holder.title.setTextColor(Color.parseColor(colorCode));
            holder.codeTextView.setTextColor(Color.parseColor(colorCode));
        }

        holder.title.setText(list.get(position).getTitle());
        holder.codeTextView.setText(list.get(position).getCouponCode());
        holder.valid_till.setText(getDate(Long.parseLong(list.get(position).getExpiryDate())));
//        holder.usage.setText(list.get(position).getUsageCount());
        if (list.get(position).getDiscountType().equals("Percentage")){
            holder.discount.setText(list.get(position).getDiscountFactor() + "%");
        }else {
            holder.discount.setText(list.get(position).getDiscountFactor() + " " + currency);
        }

        if (activity.equals("MyPromo")){
//            if (position == list.size() - 1) {
//                holder.space.setVisibility(View.VISIBLE);
//            } else {
                holder.space.setVisibility(View.GONE);
//            }
            holder.more.setVisibility(View.VISIBLE);
            holder.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
                                            deletePromoApiCall(position);
                                            deletedialog.dismiss();
                                        }
                                    });
                                    break;
                            }
                            return false;
                        }
                    });
                    projMangMore.show();
                }
            });
        }else {
            holder.more.setVisibility(View.GONE);
            holder.space.setVisibility(View.GONE);
        }



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd MMM yyyy", cal).toString();
        return date;
    }

    private void deletePromoApiCall(int position) {
        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("CouponID", list.get(position).getCouponID());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
//        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deletePromoCode, context, body, header, new ServerCallback() {
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
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, list.size());
                            if (list.size() < 3){
                                MyPromoCodes.button_layout.setVisibility(View.VISIBLE);
                            }
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


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, valid_till, discount, usage, codeTextView;
        RelativeLayout more;
        LinearLayout space;

        MyViewHolder(View itemView) {
            super(itemView);

            more = itemView.findViewById(R.id.more);
            title = itemView.findViewById(R.id.title);
            valid_till = itemView.findViewById(R.id.valid_till);
            discount = itemView.findViewById(R.id.discount);
            usage = itemView.findViewById(R.id.usage);
            space = itemView.findViewById(R.id.space);
            codeTextView = itemView.findViewById(R.id.codeTextView);

        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
