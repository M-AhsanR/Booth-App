package com.schopfen.Booth.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.Models.Chat_Data;
import com.schopfen.Booth.R;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

import static com.fxn.pix.Pix.start;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private Context context;
    Chat_Adapter.CustomItemClickListener listener;
    ArrayList<Chat_Data> commentsArray;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    private ArrayList<String> imageViewerArray = new ArrayList<>();
    public static final String MyPREFERENCES = "MyPrefs";

    public MessagesAdapter(Context context, ArrayList<Chat_Data> commentsArray, Chat_Adapter.CustomItemClickListener listener) {
        this.commentsArray = commentsArray;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.messages_adapter, parent, false);
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

        if (commentsArray.get(position).getSenderID().equals(sharedpreferences.getString("UserID", " "))){
            if(!commentsArray.get(position).getCompressedImage().isEmpty()){
                Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getCompressedImage()).into(holder.sendimage);
                holder.senderImgLayout.setVisibility(View.VISIBLE);
                holder.recieverImgLayout.setVisibility(View.GONE);
                holder.others_message.setVisibility(View.GONE);
                holder.my_message.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.GONE);
                holder.reciever_play_icon.setVisibility(View.GONE);
            }else if (!commentsArray.get(position).getMessage().isEmpty()){
                holder.my_message.setText(commentsArray.get(position).getMessage());
                holder.others_message.setVisibility(View.GONE);
                holder.my_message.setVisibility(View.VISIBLE);
                holder.senderImgLayout.setVisibility(View.GONE);
                holder.recieverImgLayout.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.GONE);
                holder.reciever_play_icon.setVisibility(View.GONE);
            }else if (!commentsArray.get(position).getVideo().isEmpty()){
                Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getVideoThumbnail()).into(holder.sendimage);
                holder.senderImgLayout.setVisibility(View.VISIBLE);
                holder.recieverImgLayout.setVisibility(View.GONE);
                holder.others_message.setVisibility(View.GONE);
                holder.my_message.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.VISIBLE);
                holder.reciever_play_icon.setVisibility(View.GONE);
            }
        }else {
            if(!commentsArray.get(position).getCompressedImage().isEmpty()){
                Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getCompressedImage()).into(holder.receiveimage);
                holder.senderImgLayout.setVisibility(View.GONE);
                holder.recieverImgLayout.setVisibility(View.VISIBLE);
                holder.others_message.setVisibility(View.GONE);
                holder.my_message.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.GONE);
                holder.reciever_play_icon.setVisibility(View.GONE);
            }else if (!commentsArray.get(position).getMessage().isEmpty()){
                holder.others_message.setText(commentsArray.get(position).getMessage());
                holder.others_message.setVisibility(View.VISIBLE);
                holder.my_message.setVisibility(View.GONE);
                holder.senderImgLayout.setVisibility(View.GONE);
                holder.recieverImgLayout.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.GONE);
                holder.reciever_play_icon.setVisibility(View.GONE);
            }else if (!commentsArray.get(position).getVideo().isEmpty()){
                Picasso.get().load(Constants.URL.IMG_URL + commentsArray.get(position).getVideoThumbnail()).into(holder.receiveimage);
                holder.senderImgLayout.setVisibility(View.GONE);
                holder.recieverImgLayout.setVisibility(View.VISIBLE);
                holder.others_message.setVisibility(View.GONE);
                holder.my_message.setVisibility(View.GONE);
                holder.sender_play_icon.setVisibility(View.GONE);
                holder.reciever_play_icon.setVisibility(View.VISIBLE);
            }
        }


        Fresco.initialize(context);


        holder.sendimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!commentsArray.get(position).getCompressedImage().isEmpty()){
                    imageViewerArray.clear();
                    imageViewerArray.add(Constants.URL.IMG_URL + commentsArray.get(position).getCompressedImage());
                    new ImageViewer.Builder(context, imageViewerArray)
                            .setStartPosition(0)
                            .show();
                }else if (!commentsArray.get(position).getVideo().isEmpty()){
                    VideoPlayer(position);
                }

            }
        });
        holder.receiveimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!commentsArray.get(position).getCompressedImage().isEmpty()) {
                    imageViewerArray.clear();
                    imageViewerArray.add(Constants.URL.IMG_URL + commentsArray.get(position).getCompressedImage());
                    new ImageViewer.Builder(context, imageViewerArray)
                            .setStartPosition(0)
                            .show();
                }else if (!commentsArray.get(position).getVideo().isEmpty()){
                    VideoPlayer(position);
                }

            }
        });

    }

    private void VideoPlayer(int position){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(Constants.URL.IMG_URL + commentsArray.get(position).getVideo()), "video/*");
        context.startActivity(Intent.createChooser(intent, "Complete action using"));
    }

    @Override
    public int getItemCount() {
        return commentsArray.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView my_message,others_message;
        RoundedImageView sendimage,receiveimage;
        ImageView sender_play_icon, reciever_play_icon;
        RelativeLayout senderImgLayout, recieverImgLayout;

        MyViewHolder(View itemView) {
            super(itemView);
            receiveimage = itemView.findViewById(R.id.recievedImg);
            sendimage = itemView.findViewById(R.id.sentImg);
            my_message = itemView.findViewById(R.id.mymessage);
            others_message = itemView.findViewById(R.id.othersmessage);
            senderImgLayout = itemView.findViewById(R.id.senderImgLayout);
            recieverImgLayout = itemView.findViewById(R.id.receivedImgLayout);
            sender_play_icon = itemView.findViewById(R.id.sender_play_icon);
            reciever_play_icon = itemView.findViewById(R.id.reciever_play_icon);
        }
    }

    public interface CustomItemClickListener {
        void onItemClick(View v, int position);
    }

}
