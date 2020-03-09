package com.schopfen.Booth.videoTrimmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.schopfen.Booth.Activities.AddProductActivity;
import com.schopfen.Booth.Activities.ChatActivity;
import com.schopfen.Booth.Fragments.AddProductFragment;
import com.schopfen.Booth.R;
import com.schopfen.Booth.videoTrimmer.interfaces.OnHgLVideoListener;
import com.schopfen.Booth.videoTrimmer.interfaces.OnTrimVideoListener;

import java.io.File;

public class VideoTrimmerActivity extends AppCompatActivity implements OnTrimVideoListener, OnHgLVideoListener {

    private HgLVideoTrimmer mVideoTrimmer;
    private ProgressDialog mProgressDialog;
    String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimmer);

        Intent extraIntent = getIntent();
        String path = "";
        int maxDuration = 10000;

        if (extraIntent != null) {
            path = extraIntent.getStringExtra(AddProductActivity.EXTRA_VIDEO_PATH);
//            maxDuration = extraIntent.getIntExtra(MainActivity.VIDEO_TOTAL_DURATION, 15);
            activity = extraIntent.getStringExtra("Activity");
        }

        //setting progressbar
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Trimming your video...");

        mVideoTrimmer = ((HgLVideoTrimmer) findViewById(R.id.timeLine));
        if (mVideoTrimmer != null) {


            /**
             * get total duration of video file
             */
            Log.e("tg", "maxDuration = " + maxDuration);
            //mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setMaxDuration(maxDuration);
            mVideoTrimmer.setOnTrimVideoListener(this);
            mVideoTrimmer.setOnHgLVideoListener(this);
            //mVideoTrimmer.setDestinationPath("/storage/emulated/0/DCIM/CameraCustom/");
            mVideoTrimmer.setVideoURI(Uri.parse(path));
            mVideoTrimmer.setVideoInformationVisibility(true);
        }
    }

    @Override
    public void onTrimStarted() {
        mProgressDialog.show();
    }

    @Override
    public void getResult(final Uri contentUri) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 Toast.makeText(VideoTrimmerActivity.this, "Video Saved at" + contentUri.getPath(), Toast.LENGTH_SHORT).show();
                 Log.e("Result", contentUri.getPath());

                //in a new Thread
//                boolean isCompressSuccess= MediaController.getInstance().convertVideo(contentUri.getPath(),contentUri.getPath());

                if (activity.equals("AddProduct")){
                    AddProductFragment.videoURI = contentUri;
                }else if (activity.equals("ChatActivity")){
                    ChatActivity.videoURI = contentUri;
                }else if (activity.equals("EditProduct")){
                    AddProductActivity.videoURI = contentUri;
                }
                 finish();

            }
        });

        try {


            String path = contentUri.getPath();
            File file = new File(path);
            Log.e("tg", " path1 = " + path + " uri1 = " + Uri.fromFile(file));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
            intent.setDataAndType(Uri.fromFile(file), "video/*");
            startActivity(intent);
            finish();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(VideoTrimmerActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    private void playUriOnVLC(Uri uri) {

        int vlcRequestCode = 42;
        Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
        vlcIntent.setPackage("org.videolan.vlc");
        vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
        vlcIntent.putExtra("title", "Kung Fury");
        vlcIntent.putExtra("from_start", false);
        vlcIntent.putExtra("position", 90000l);
        startActivityForResult(vlcIntent, vlcRequestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("tg", "resultCode = " + resultCode + " data " + data);
    }

    @Override
    public void cancelAction() {
        mProgressDialog.cancel();
        mVideoTrimmer.destroy();
        finish();
    }

    @Override
    public void onError(final String message) {
        mProgressDialog.cancel();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVideoPrepared() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
            }
        });
    }
}