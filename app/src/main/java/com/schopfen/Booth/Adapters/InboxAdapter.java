package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.schopfen.Booth.Activities.ChatActivity;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.DataClasses.TimeAgo;
import com.schopfen.Booth.Models.InboxModel;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<InboxModel> inboxArray;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    MessagesAdapter adapter;

    public InboxAdapter(Context context, ArrayList<InboxModel> InboxArray, CustomItemClickListener listener) {
        this.inboxArray = InboxArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.inbox_adapter, parent, false);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        if (sharedpreferences.getString("UserID", " ").equals(inboxArray.get(position).getConversationReceiverID())){
            Picasso.get().load(Constants.URL.IMG_URL + inboxArray.get(position).getConversationSenderImage()).placeholder(R.drawable.user).into(holder.sender_image);
            holder.inbox_name.setText("@" + inboxArray.get(position).getConversationSenderUserName());
            holder.inbox_text.setText(inboxArray.get(position).getMessage());
            holder.inbox_time.setText(TimeAgo.getTimeAgo(Long.parseLong(inboxArray.get(position).getCreatedAt())));

            if (inboxArray.get(position).getIsReadByReceiver().equals("no")){
                holder.inbox_name.setTypeface(null, Typeface.BOLD);
                holder.inbox_text.setTypeface(null, Typeface.BOLD);
                holder.inbox_time.setTypeface(null, Typeface.BOLD);
            }else if (inboxArray.get(position).getIsReadByReceiver().equals("yes")){
                holder.inbox_name.setTypeface(null, Typeface.NORMAL);
                holder.inbox_text.setTypeface(null, Typeface.NORMAL);
                holder.inbox_time.setTypeface(null, Typeface.NORMAL);
            }

        }else if (sharedpreferences.getString("UserID", " ").equals(inboxArray.get(position).getConversationSenderID())){
            Picasso.get().load(Constants.URL.IMG_URL + inboxArray.get(position).getConversationReceiverImage()).placeholder(R.drawable.user).into(holder.sender_image);
            holder.inbox_name.setText("@" + inboxArray.get(position).getConversationReceiverUserName());
            holder.inbox_text.setText(inboxArray.get(position).getMessage());
            holder.inbox_time.setText(TimeAgo.getTimeAgo(Long.parseLong(inboxArray.get(position).getCreatedAt())));

            if (inboxArray.get(position).getIsReadBySender().equals("no")){
                holder.inbox_name.setTypeface(null, Typeface.BOLD);
                holder.inbox_text.setTypeface(null, Typeface.BOLD);
                holder.inbox_time.setTypeface(null, Typeface.BOLD);
            }else if (inboxArray.get(position).getIsReadBySender().equals("yes")){
                holder.inbox_name.setTypeface(null, Typeface.NORMAL);
                holder.inbox_text.setTypeface(null, Typeface.NORMAL);
                holder.inbox_time.setTypeface(null, Typeface.NORMAL);
            }

        }

        if (position == inboxArray.size()-1){
            holder.separator.setVisibility(View.GONE);
        }

        holder.inbox_item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedpreferences.getString("UserID", " ").equals(inboxArray.get(position).getConversationReceiverID())){

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("OthersprofileID", inboxArray.get(position).getConversationSenderID());
                    intent.putExtra("UserName", inboxArray.get(position).getConversationSenderUserName());
                    intent.putExtra("usertype", inboxArray.get(position).getReceiverType());
                    intent.putExtra("othertype", inboxArray.get(position).getType());
                    context.startActivity(intent);

                }else if (sharedpreferences.getString("UserID", " ").equals(inboxArray.get(position).getConversationSenderID())){

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("OthersprofileID", inboxArray.get(position).getConversationReceiverID());
                    intent.putExtra("UserName", inboxArray.get(position).getConversationReceiverUserName());
                    intent.putExtra("usertype", inboxArray.get(position).getType());
                    intent.putExtra("othertype", inboxArray.get(position).getReceiverType());
                    context.startActivity(intent);

                }
            }
        });

    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == cal.get(Calendar.DATE) ) {
            return "Today";
        } else if (now.get(Calendar.DATE) - cal.get(Calendar.DATE) == 1  ){
            return "Yesterday";
        } else if (now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            return DateFormat.format("dd-MM-yyyy", cal).toString();
        } else {
            return DateFormat.format("dd-MM-yyyy", cal).toString();
        }

//        return date;
    }

    @Override
    public int getItemCount() {
        return inboxArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView sender_image;
        TextView inbox_name, inbox_text, inbox_time;
        LinearLayout separator, inbox_item_layout;

        MyViewHolder(View itemView) {
            super(itemView);
            sender_image = itemView.findViewById(R.id.inbox_sender_image);
            inbox_name = itemView.findViewById(R.id.inbox_name);
            inbox_text = itemView.findViewById(R.id.inbox_text);
            inbox_time = itemView.findViewById(R.id.inbox_time);
            separator = itemView.findViewById(R.id.inbox_item_separator);
            inbox_item_layout = itemView.findViewById(R.id.inbox_item_layout);
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
