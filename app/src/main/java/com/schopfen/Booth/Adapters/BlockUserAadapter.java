package com.schopfen.Booth.Adapters;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BlockUerData;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlockUserAadapter extends RecyclerView.Adapter<BlockUserAadapter.MyViewHolder> {

    private Context context;
    BlockUserAadapter.CustomItemClickListener listener;
    ArrayList<BlockUerData> blocksArray;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    MessagesAdapter adapter;

    public BlockUserAadapter(Context context, ArrayList<BlockUerData> commentsArray, BlockUserAadapter.CustomItemClickListener listener) {
        this.blocksArray = commentsArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public BlockUserAadapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.blockuser_adapter, parent, false);
        final BlockUserAadapter.MyViewHolder viewHolder = new BlockUserAadapter.MyViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final BlockUserAadapter.MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (blocksArray.get(position).getType().equals("user")){
            holder.username.setText(blocksArray.get(position).getUserName());
            Picasso.get().load(Constants.URL.IMG_URL + blocksArray.get(position).getCompressedImage()).placeholder(R.drawable.user).into(holder.profile);
        }else if (blocksArray.get(position).getType().equals("booth")){
            holder.username.setText(blocksArray.get(position).getBoothUserName());
            Picasso.get().load(Constants.URL.IMG_URL + blocksArray.get(position).getBoothCompressImage()).placeholder(R.drawable.user).into(holder.profile);
        }



        holder.unblock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog unblockDialog = new Dialog(context, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                unblockDialog.setContentView(R.layout.delete_cart_item_dialog);
                Button yesBtn1 = unblockDialog.findViewById(R.id.btn_yes);
                Button noBtn1 = unblockDialog.findViewById(R.id.btn_no);
                TextView alert_message = unblockDialog.findViewById(R.id.alert_message);

                if (blocksArray.get(position).getType().equals("user")){
                    alert_message.setText(context.getResources().getString(R.string.are_you_sure_block_user));
                }else if (blocksArray.get(position).getType().equals("booth")){
                    alert_message.setText(context.getResources().getString(R.string.are_you_sure_block_booth));
                }

                unblockDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                unblockDialog.show();

                noBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unblockDialog.dismiss();
                    }
                });

                yesBtn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UnBlockUser(position);
                        unblockDialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return blocksArray.size();
    }

    private void UnBlockUser(int position){
        CustomLoader.showDialog((Activity) context);

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("BlockedUserID", blocksArray.get(position).getUserID());
        body.put("Type", "unblock");
        body.put("UserType", sharedpreferences.getString("LastState", ""));
        body.put("BlockedUserType", blocksArray.get(position).getType());
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.BlockUser, context, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("BlockUserResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            String message = jsonObject.getString("message");
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            blocksArray.remove(position);
                            notifyItemRemoved(position);
                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
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


    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile;
        TextView username;
        Button unblock_button;
        MyViewHolder(View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.profilepic);
            username = itemView.findViewById(R.id.username);
            unblock_button = itemView.findViewById(R.id.unblock_button);
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}

