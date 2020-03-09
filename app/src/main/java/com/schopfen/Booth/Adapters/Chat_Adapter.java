package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.schopfen.Booth.Activities.ChatActivity;
import com.schopfen.Booth.Models.Chat_Data;
import com.schopfen.Booth.Models.MainChatData;
import com.schopfen.Booth.Models.ProductsData;
import com.schopfen.Booth.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.LongAccumulator;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.MyViewHolder> {

    private Context context;
    CustomItemClickListener listener;
    ArrayList<MainChatData> commentsArray;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    MessagesAdapter adapter;

    public Chat_Adapter(Context context, ArrayList<MainChatData> commentsArray, CustomItemClickListener listener) {
        this.commentsArray = commentsArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.chat_adapter, parent, false);
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

        holder.time_stamp.setText(commentsArray.get(position).getDate());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        holder.messages.setLayoutManager(linearLayoutManager);

        adapter = new MessagesAdapter(context, commentsArray.get(position).getChat_data(), new Chat_Adapter.CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        });
        holder.messages.setAdapter(adapter);
        holder.messages.scrollToPosition(commentsArray.get(position).getChat_data().size() - 1);

        holder.time_stamp.setText(getDate(Long.valueOf(commentsArray.get(position).getDate())));
//        holder.messages.setHasFixedSize(true);


    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.DATE) == cal.get(Calendar.DATE) ) {
            return context.getResources().getString(R.string.today);
        } else if (now.get(Calendar.DATE) - cal.get(Calendar.DATE) == 1  ){
            return context.getResources().getString(R.string.yesterday);
        } else if (now.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            return DateFormat.format("dd-MM-yyyy", cal).toString();
        } else {
            return DateFormat.format("dd-MM-yyyy", cal).toString();
        }

//        return date;
    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView time_stamp;
        RecyclerView messages;

        MyViewHolder(View itemView) {
            super(itemView);
            time_stamp = itemView.findViewById(R.id.time_stamp_messages);
            messages = itemView.findViewById(R.id.messages);
        }
    }

    public void addMessage(ArrayList<Chat_Data> feed) {

        int v = getItemCount();

        for (int a = 0; a < feed.size(); a++) {
            commentsArray.get(0).getChat_data().add(feed.get(a));
        }
//        size = products.size();
//            notifyItemChanged(getItemCount());
        adapter.notifyItemInserted(adapter.getItemCount());
//        adapter.notifyDataSetChanged();

        int fv = getItemCount();
//        notifyDataSetChanged();

    }
    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }
}
