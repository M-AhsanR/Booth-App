package com.schopfen.Booth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.DataClasses.Retrofit.RetrofitInstance;

import org.apache.james.mime4j.message.Disposable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FileUploadService extends JobIntentService {
    private static final String TAG = "FileUploadService";
    Disposable mDisposable;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String productID, UserID, video, thumb;
    /**
     * Unique job ID for this service.
     */
    private static final int JOB_ID = 102;

    public static void enqueueWork(Context context, Intent intent) {
        enqueueWork(context, FileUploadService.class, JOB_ID, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        /**
         * Download/Upload of file
         * The system or framework is already holding a wake lock for us at this point
         */

        // get file file here

        sharedpreferences = getSharedPreferences(MyPREFERENCES, this.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        UserID = sharedpreferences.getString("UserID", "");
        productID = sharedpreferences.getString("productID","");
        video = sharedpreferences.getString("video", "");
        thumb = sharedpreferences.getString("thumb", "");
        //TODO do something useful

        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", UserID);
        body.put("ProductID", productID);
        body.put("ProductVideo", video);
        body.put("ProductVideoThumbnail", thumb);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VIDEOUPLOAD, FileUploadService.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("AddProductResult", " " + result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            Toast.makeText(FileUploadService.this, "Video is added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(FileUploadService.this, "Video is too heavy. You can add it again by editing the product!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FileUploadService.this, "Video is too heavy. You can add it again by editing the product!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}