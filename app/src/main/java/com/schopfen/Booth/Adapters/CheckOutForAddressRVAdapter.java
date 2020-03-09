package com.schopfen.Booth.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.Activities.BoothMainActivity;
import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.Activities.CheckOutActivity;
import com.schopfen.Booth.Activities.CreateAddressDialog;
import com.schopfen.Booth.Activities.EditBoothActivity;
import com.schopfen.Booth.Activities.MapsActivity;
import com.schopfen.Booth.Activities.RegisterBayerActivity;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.AddressData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Social.CheckOutForAddressRVModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import static java.security.AccessController.getContext;

public class CheckOutForAddressRVAdapter extends RecyclerView.Adapter<CheckOutForAddressRVAdapter.MyViewHolder> {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    private List<AddressData> addressData;
    private Context context;
    CustomItemClickListener listener;
    ArrayList<CheckOutForAddressRVModel> arraylistadapter;
    List<TextView> list = new ArrayList<>();
    private int selected_position = 0;
    public static String addressID = "";

    public CheckOutForAddressRVAdapter(Context context, ArrayList<AddressData> addressData, CustomItemClickListener listener) {
        this.addressData = addressData;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public CheckOutForAddressRVAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.checkout_for_addressrv_adapter, parent, false);
        final CheckOutForAddressRVAdapter.MyViewHolder viewHolder = new CheckOutForAddressRVAdapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull final CheckOutForAddressRVAdapter.MyViewHolder holder, final int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        if (position != addressData.size()){
//            AddressData addressData = addressData.get(position);
            holder.address.setText(addressData.get(position).getAddress1());
            holder.addressTitle.setText(addressData.get(position).getAddressTitle());
            list.add(holder.address);
            holder.add_address.setVisibility(View.GONE);
            holder.linearLayout.setVisibility(View.VISIBLE);
            holder.address.setTextColor(context.getResources().getColor(R.color.black));
            holder.linearLayout.setBackgroundResource(R.drawable.custom_dialog_background);
        }
        if (position == addressData.size()){
            holder.add_address.setVisibility(View.VISIBLE);
            holder.linearLayout.setVisibility(View.GONE);
        }
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_position = position;
                notifyDataSetChanged();
            }
        });
        if (selected_position == position){
            if (!addressData.isEmpty()){
                holder.address.setTextColor(context.getResources().getColor(R.color.orange));
                holder.linearLayout.setBackgroundResource(R.drawable.co_address_selected_background);
                addressID = addressData.get(position).getAddressID();
            }
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        int width = displayMetrics.widthPixels/3;
        holder.linearLayout.setMinimumWidth(width);
        holder.add_address.setMinimumWidth(width);

        holder.add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAddressDialog(holder);
            }
        });

        holder.del_more.setOnClickListener(new View.OnClickListener() {
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
                                TextView message = deletedialog.findViewById(R.id.alert_message);
                                Button yesBtn = deletedialog.findViewById(R.id.btn_yes);
                                Button noBtn = deletedialog.findViewById(R.id.btn_no);

                                message.setText(context.getResources().getString(R.string.are_you_sure_you_want_to_delete_this));

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
                                        deleteProApiCall(position);
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

    }

    private void AddAddressDialog(MyViewHolder holder){
        context.startActivity(new Intent(context, CreateAddressDialog.class));
    }

    @Override
    public int getItemCount() {
        return addressData.size()+1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView addressTitle,address;
        LinearLayout linearLayout, add_address;
        ImageView del_more;
        int p;

        MyViewHolder(View itemView) {
            super(itemView);
            addressTitle = itemView.findViewById(R.id.addressTitle);
            address = itemView.findViewById(R.id.address);
            linearLayout = itemView.findViewById(R.id.ll_click_cofara);
            add_address = itemView.findViewById(R.id.add_address);
            del_more = itemView.findViewById(R.id.delete_more);
        }
    }

    private void AddAdressApi(String address_title, String recep_name,String recep_email,String recep_mobnum,String recep_address,String recep_city,String recep_buildingnum,MyViewHolder holder){

        CustomLoader.showDialog((Activity) context);
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AddressTitle",address_title);
        body.put("RecipientName",recep_name);
        body.put("Email",recep_email);
        body.put("Mobile",recep_mobnum);
        body.put("Address1",recep_address);
        body.put("City",recep_city);
        body.put("ApartmentNo",recep_buildingnum);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String,String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.addAddressUrl, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()){
                    Log.e("CreateAddressErrorResp", ""+result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject address_info = jsonObject.getJSONObject("address_info");
                            String Address1 = address_info.getString("Address1");
                            String Address2 = address_info.getString("Address2");
                            String AddressID = address_info.getString("AddressID");
                            String AddressTitle = address_info.getString("AddressTitle");
                            String ApartementNo = address_info.getString("ApartmentNo");
                            String City = address_info.getString("City");
                            String Email = address_info.getString("Email");
                            String Gender = address_info.getString("Gender");
                            String IsDefault = address_info.getString("IsDefault");
                            String Mobile = address_info.getString("Mobile");
                            String RecipientName = address_info.getString("RecipientName");
                            String UserID = address_info.getString("UserID");

                            addressData.add(new AddressData(Address1, Address2, AddressID, AddressTitle, ApartementNo, City, Email, Gender, IsDefault,
                                    Mobile, RecipientName, UserID));
                            notifyItemInserted(addressData.size()-1);
                            notifyDataSetChanged();
                            holder.addressTitle.setText(Address1);

                            final Dialog subdialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            subdialog.setContentView(R.layout.added_address_dialog);
                            Button closeBtn = subdialog.findViewById(R.id.btn_close);
                            subdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            subdialog.show();
                            closeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    subdialog.dismiss();
                                }
                            });

                            CustomLoader.dialog.dismiss();

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    // delete address function
    public void deleteProApiCall(int pos) {
        CustomLoader.showDialog((Activity) context);

        String addressID = addressData.get(pos).getAddressID();
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID",sharedpreferences.getString("UserID", " "));
        body.put("AddressID",addressID);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.deleteAddress, context, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("Result", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            addressData.remove(pos);
                            notifyItemRemoved(pos);
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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

}

