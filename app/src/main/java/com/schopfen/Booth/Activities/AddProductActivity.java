package com.schopfen.Booth.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Adapters.AddProductImagesListAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.DrawableClickListener;
import com.schopfen.Booth.FileUploadService;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.schopfen.Booth.Retrofit.Config;
import com.schopfen.Booth.videoTrimmer.VideoTrimmerActivity;
import com.schopfen.Booth.videoTrimmer.utils.FileUtils;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.fxn.pix.Pix.start;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    RoundedImageView video;
    EditText productTitle, productPrice, brandName, productDetail, deliveryCost;
    AutoCompleteTextView category, subCategory, subSubCategory, itemCondition, estimatedDeliveryTime;
    Switch availabilitySwitch;
    Button confirmBtn;
    ArrayList<ProductImagesData> imagesdata = new ArrayList<>();
    Map<String, String> addproductParams = new HashMap<String, String>();
    Bitmap bitmap;
    Bitmap thumbnailbitmap;
    String subsubCategoryID;
    String outofStock = "0";
    Currency currency;
    AddProductImagesListAdapter addProductImagesListAdapter;
    RecyclerView imgesList;
    RelativeLayout seventh_layout;
    RoundedImageView seventh_image;
    ImageView add_image_camera, add_video_camera, delete_video;
    Intent videointent;
    int PICK_FROM_GALLERY = 7894;
    String filemanagerstring;
    String selectedImagePath;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_CAMERA_ACCESS_PERMISSION = 102;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";
    ArrayList<String> returnValue = new ArrayList<>();
    ArrayList<String> aftercrop_list = new ArrayList<>();
    Uri myUri;
    public static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<MainCategoriesData> mainCategoryData;
    ArrayList<SubCategoriesData> subCategoriesData;
    ArrayList<SubSubCategoriesData> subSubCategoriesData;
    Map<String, String> postParams = new HashMap<>();
    HashMap<String, String> headerParams;
    ArrayList<String> mainCategoriesNames = new ArrayList<>();
    ArrayList<String> subCategoriesNames = new ArrayList<>();
    ArrayList<String> subSubCategoriesNames = new ArrayList<>();
    ArrayList<String> itemConditionsList = new ArrayList<>();
    ArrayList<String> estimatedDeliverytimeList = new ArrayList<>();
    int mainPosition;
    int subPosition;
    String id;
    ByteArrayOutputStream byteBuffer;
    String videofilePath;
    String videoBase64Data = "";
    public static Uri videoURI = null;
    String thumbBase64 = "";
    Dialog progress;
    ProgressBar circularProgressbar;
    TextView tv;
    ProgressBar mProgress;
    long totalSize = 0;
    public static ArrayList<Uri> uriArrayList = new ArrayList<>();

    @Override
    protected void onStop() {
        super.onStop();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CustomLoader.dialog != null){
            CustomLoader.dialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isNetworkAvailable()){
            setContentView(R.layout.activity_add_product);
            Bundle bundle = getIntent().getExtras();
            id = bundle.getString("ProID");
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            Initialization();
            initiateListeners();
            ImagesDimensions();
            Actions();
            getCategoriesApi();
            EstimatedandItemconditionActions();
            Locale defaultLocale = Locale.getDefault();
            displayCurrencyInfoForLocale(defaultLocale);
            availabilitySwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (availabilitySwitch.isChecked()) {
                        availabilitySwitch.setChecked(true);
                        outofStock = "0";
                    } else {
                        availabilitySwitch.setChecked(false);
                        outofStock = "1";
                    }
                }
            });
            progress = new Dialog(AddProductActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
            progress.setContentView(R.layout.progress_dialog_circle);
            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
            tv = progress.findViewById(R.id.tv);
            Resources res = getResources();
            Drawable drawable = res.getDrawable(R.drawable.circular);
            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
            mProgress.setProgress(0);   // Main Progress
            mProgress.setSecondaryProgress(100); // Secondary Progress
            mProgress.setMax(100); // Maximum Progress
            mProgress.setProgressDrawable(drawable);
            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            getProductDetailsApiCall();
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void EstimatedandItemconditionActions() {
        itemConditionsList.add(getResources().getString(R.string.newtext));
        itemConditionsList.add("Used");
        itemConditionsList.add("Refurbished");
        estimatedDeliverytimeList.add("1 - 3 Days");
        estimatedDeliverytimeList.add("3 - 5 Days");
        estimatedDeliverytimeList.add("5 - 10 Days");
        estimatedDeliverytimeList.add("10 - 15 Days");
        estimatedDeliverytimeList.add("15 - 20 Days");
        estimatedDeliverytimeList.add("approx. 45 Days");
        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, itemConditionsList);
        ArrayAdapter<String> edtAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, estimatedDeliverytimeList);
        itemCondition.setAdapter(itemConditionAdapter);
        estimatedDeliveryTime.setAdapter(edtAdapter);
        itemCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCondition.showDropDown();
            }
        });
        estimatedDeliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                estimatedDeliveryTime.showDropDown();
            }
        });
    }

    private Map<String, String> imgToStringFunction(Bitmap bitmap) {
        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] original_data = original_image.toByteArray(); // Original
                addproductParams.put("ProductImage" + String.valueOf(j + 1), Base64.encodeToString(original_data, Base64.DEFAULT));
            }
        }
        return addproductParams;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoURI != null) {
            Uri selectedVideoUri = videoURI;
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoURI.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            video.setImageBitmap(thumb);
            add_video_camera.setVisibility(View.GONE);
            delete_video.setVisibility(View.VISIBLE);
            bitmap = thumb;
            ByteArrayOutputStream thumb_imageOS = new ByteArrayOutputStream();
            if (bitmap != null){
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, thumb_imageOS); // Original
            }
            byte[] thumb_imageData = thumb_imageOS.toByteArray();
            thumbBase64 = Base64.encodeToString(thumb_imageData, Base64.NO_WRAP);
            // Setting the thumbnail of the video in to the image view
            InputStream inputStream = null;
            // Converting the video in to the bytes
            try {
                inputStream = getContentResolver().openInputStream(Uri.parse("file://" + selectedVideoUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            byteBuffer = new ByteArrayOutputStream();
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Converting bytes into base64
            videoBase64Data = Base64.encodeToString(byteBuffer.toByteArray(), Base64.NO_WRAP);
            Log.d("VideoBase64", videoBase64Data);
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progress.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            // updating progress bar value
            mProgress.setProgress(progress[0]);
            // updating percentage value
            tv.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.updateProduct);
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                if (uriArrayList.size() > 0){
                    for (int i = 0; i < uriArrayList.size(); i++){
                        File sourceFile = new File(uriArrayList.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("ProductImage" + String.valueOf(i+1), new FileBody(sourceFile));
                    }
                    entity.addPart("ProductImagesCount", new StringBody(String.valueOf(uriArrayList.size())));
                }
                if (videoURI != null){
                    entity.addPart("ProductVideo", new FileBody(new File(videoURI.getPath())));
                }
                entity.addPart("UserID", new StringBody(sharedpreferences.getString("UserID", "")));
                entity.addPart("CategoryID", new StringBody(subsubCategoryID));
                entity.addPart("ProductID", new StringBody(id));
                entity.addPart("ProductPrice", new StringBody(productPrice.getText().toString()));
                entity.addPart("DeliveryTime", new StringBody(estimatedDeliveryTime.getText().toString()));
                entity.addPart("ProductType", new StringBody(itemCondition.getText().toString()));
                entity.addPart("OutOfStock", new StringBody(outofStock));
                entity.addPart("IsActive", new StringBody("1"));
                entity.addPart("Title", new StringBody(productTitle.getText().toString()));
                entity.addPart("ProductDescription", new StringBody(productDetail.getText().toString()));
                entity.addPart("Currency", new StringBody(sharedpreferences.getString("Currency", "")));
                entity.addPart("CurrencySymbol", new StringBody(sharedpreferences.getString("CurrencySymbol", "")));
                entity.addPart("DeliveryCharges", new StringBody(deliveryCost.getText().toString()));
                entity.addPart("ProductBrandName", new StringBody(brandName.getText().toString()));
                entity.addPart("ProductImagesCount", new StringBody(String.valueOf(bitmapArrayList.size())));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);
                httppost.setHeader("Verifytoken", sharedpreferences.getString("ApiToken", " "));
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ResponseResult", "Response from server: " + result);
            // showing the server response in an alert dialog
            super.onPostExecute(result);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");
                if (status == 200){
                    progress.dismiss();
                    final Dialog addressDialog = new Dialog(AddProductActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                            finish();
                        }
                    });
                }else {
                    progress.dismiss();
                    Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(AddProductActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void AddproductApi() {
        CustomLoader.showDialog(AddProductActivity.this);
        if (!bitmapArrayList.isEmpty()){
            imgToStringFunction(bitmap);
            addproductParams.put("ProductImagesCount", String.valueOf(bitmapArrayList.size()));
        }
        addproductParams.put("UserID", sharedpreferences.getString("UserID", ""));
        addproductParams.put("CategoryID", subsubCategoryID);
        addproductParams.put("ProductID", id);
        addproductParams.put("ProductPrice", productPrice.getText().toString());
        addproductParams.put("DeliveryTime", estimatedDeliveryTime.getText().toString());
        addproductParams.put("ProductType", itemCondition.getText().toString());
        addproductParams.put("OutOfStock", outofStock);
        addproductParams.put("IsActive", "1");
        addproductParams.put("Title", productTitle.getText().toString());
        addproductParams.put("ProductDescription", productDetail.getText().toString());
        addproductParams.put("Currency", sharedpreferences.getString("Currency", ""));
        addproductParams.put("CurrencySymbol", sharedpreferences.getString("CurrencySymbol", ""));
        addproductParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        Log.e("addProductParams", addproductParams.toString());
        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProduct, AddProductActivity.this, addproductParams, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddProductResult", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            if (!videoBase64Data.isEmpty()) {
                                Intent mIntent = new Intent(AddProductActivity.this, FileUploadService.class);
                                mEditor.putString("productID", id).commit();
                                mEditor.putString("video", videoBase64Data).commit();
                                mEditor.putString("thumb", thumbBase64).commit();
                                FileUploadService.enqueueWork(AddProductActivity.this, mIntent);
                                final Dialog addressDialog = new Dialog(AddProductActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                                addressDialog.setContentView(R.layout.alert_text_layout);
                                Button ok = addressDialog.findViewById(R.id.btn_ok);
                                TextView address_text = addressDialog.findViewById(R.id.address_text);
                                address_text.setText(message + " " + "Your video will be added soon after uploading process");
                                addressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                addressDialog.show();
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        addressDialog.dismiss();
                                        finish();
                                    }
                                });
                                CustomLoader.dialog.dismiss();
                            } else {
                                CustomLoader.dialog.dismiss();
                                final Dialog addressDialog = new Dialog(AddProductActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                        finish();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(AddProductActivity.this, message, Toast.LENGTH_SHORT).show();
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomLoader.dialog.dismiss();
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                    CustomLoader.dialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (int a = 0; a < returnValue.size(); a++) {
                myUri = Uri.fromFile(new File(returnValue.get(a)));
                beginCrop(myUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_TRIMMER) {
            final Uri selectedUri = data.getData();
            if (selectedUri != null) {
                startTrimActivity(selectedUri);
            } else {
                Toast.makeText(AddProductActivity.this, getResources().getString(R.string.cannotretrivevideo), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(AddProductActivity.this, VideoTrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(this, uri));
        intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        intent.putExtra("Activity", "EditProduct");
        startActivity(intent);
    }

    private int getMediaDuration(Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(this, uriOfFile);
        int duration = mp.getDuration();
        return duration;
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(512, 512)
                .start(AddProductActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
                saveToInternalStorage(bitmap);
                bitmapArrayList.add(bitmap);
                Log.e("chekingBit", bitmap.toString());
                Log.e("bitmapsize", String.valueOf(bitmapArrayList.size()));
                if (bitmapArrayList.size() == 6){
                    seventh_layout.setVisibility(View.GONE);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddProductActivity.this, RecyclerView.HORIZONTAL, false);
                imgesList.setLayoutManager(linearLayoutManager);
                addProductImagesListAdapter = new AddProductImagesListAdapter("activity", AddProductActivity.this, bitmapArrayList, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                    }
                });
                imgesList.setAdapter(addProductImagesListAdapter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Actions() {
        availabilitySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (availabilitySwitch.isChecked()) {
                    availabilitySwitch.setChecked(false);
                    outofStock = "1";
                } else {
                    availabilitySwitch.setChecked(true);
                    outofStock = "0";
                }
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });
        productPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        deliveryCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 2)});
        seventh_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapArrayList.size() < 6) {
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                    start(AddProductActivity.this,                    //Activity or Fragment Instance
                            1234,                //Request code for activity results
                            1);
                }
            }
        });
    }

    private void ImagesDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        AddProductActivity.this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 6;
        video.setMinimumWidth(width);
        video.setMaxHeight(width);
        video.setMaxWidth(width);
        video.setMinimumHeight(width);
        seventh_image.setMinimumWidth(width);
        seventh_image.setMaxHeight(width);
        seventh_image.setMaxWidth(width);
        seventh_image.setMinimumHeight(width);
    }

    private void Initialization() {
        video = findViewById(R.id.video);
        add_image_camera = findViewById(R.id.add_image_camera);
        add_video_camera = findViewById(R.id.add_video_camera);
        delete_video = findViewById(R.id.delete_video);
        seventh_layout = findViewById(R.id.seventh_layout);
        seventh_image = findViewById(R.id.seventh_image);
        category = findViewById(R.id.mainCategory);
        subCategory = findViewById(R.id.subCategory);
        subSubCategory = findViewById(R.id.subSubCategory);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        availabilitySwitch = findViewById(R.id.availabilitySwitch);
        brandName = findViewById(R.id.brandName);
        itemCondition = findViewById(R.id.itemCondition);
        imgesList = findViewById(R.id.images_list);
        productDetail = findViewById(R.id.productDetail);
        deliveryCost = findViewById(R.id.deliveryCost);
        estimatedDeliveryTime = findViewById(R.id.estimatedDeliveryTime);
        confirmBtn = findViewById(R.id.confirmBtn);
    }

    private void initiateListeners() {
        confirmBtn.setOnClickListener(this);
    }

    private void getCategoriesApi() {
        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.categories + "AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, AddProductActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    Log.e("MyError", "result is   " + ERROR);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            mainCategoriesNames.clear();
                            JSONArray categories = jsonObject.getJSONArray("categories");
                            mainCategoryData = new ArrayList<>();
                            for (int i = 0; i < categories.length(); i++) {
                                JSONObject mainCateObject = categories.getJSONObject(i);
                                String CategoryID = mainCateObject.getString("CategoryID");
                                String Image = mainCateObject.getString("Image");
                                String Title = mainCateObject.getString("Title");
                                JSONArray subCateArray = mainCateObject.getJSONArray("SubCategories");
                                subCategoriesData = new ArrayList<>();
                                for (int j = 0; j < subCateArray.length(); j++) {
                                    JSONObject subCateObject = subCateArray.getJSONObject(j);
                                    String CategoryID2 = subCateObject.getString("CategoryID");
                                    String Image2 = subCateObject.getString("Image");
                                    String Title2 = subCateObject.getString("Title");
                                    JSONArray subSubCateArray = subCateObject.getJSONArray("SubSubCategories");
                                    subSubCategoriesData = new ArrayList<>();
                                    for (int k = 0; k < subSubCateArray.length(); k++) {
                                        JSONObject subSubCateObject = subSubCateArray.getJSONObject(k);
                                        String CategoryID3 = subSubCateObject.getString("CategoryID");
                                        String Image3 = subSubCateObject.getString("Image");
                                        String Title3 = subSubCateObject.getString("Title");
                                        subSubCategoriesData.add(new SubSubCategoriesData(CategoryID3, Image3, Title3));
                                    }
                                    subCategoriesData.add(new SubCategoriesData(CategoryID2, Image2, Title2, subSubCategoriesData));
                                }
                                mainCategoryData.add(new MainCategoriesData(CategoryID, Image, Title, subCategoriesData));
                                mainCategoriesNames.add(Title);
                            }
                            fillMainCategories();

                        } else {
                            Toast.makeText(AddProductActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddProductActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillMainCategories() {
        ArrayAdapter<String> mainCateAutoCompAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, mainCategoriesNames);
        mainCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        category.setAdapter(mainCateAutoCompAdapter);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category.showDropDown();
            }
        });
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    subCategory.setText("");
                    subSubCategory.setText("");
                    subCategory.setEnabled(true);
                    subSubCategory.setEnabled(false);
                    mainPosition = position;
                    subCategoriesNames.clear();
                    fillSubCategories();
                    for (int i = 0; i < mainCategoryData.get(position).getSubCategoriesData().size(); i++) {
                        subCategoriesNames.add(mainCategoryData.get(position).getSubCategoriesData().get(i).getTitle());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillSubCategories() {
        ArrayAdapter<String> subCateAutoCompAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, subCategoriesNames);
        subCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subCategory.setAdapter(subCateAutoCompAdapter);
        subCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCategory.showDropDown();
            }
        });
        subCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    subSubCategory.setText("");
                    subCategory.setEnabled(true);
                    subSubCategory.setEnabled(true);
                    subSubCategoriesNames.clear();
                    fillSubSubCategories();
                    for (int i = 0; i < mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().size(); i++) {
                        subSubCategoriesNames.add(mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().get(i).getTitle());
                    }
                    subPosition = position;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillSubSubCategories() {
        ArrayAdapter<String> subSubCateAutoCompAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.simple_list_item_1, subSubCategoriesNames);
        subSubCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subSubCategory.setAdapter(subSubCateAutoCompAdapter);
        subSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subSubCategory.showDropDown();
            }
        });
        subSubCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                subsubCategoryID = mainCategoryData.get(mainPosition).getSubCategoriesData().get(subPosition).getSubSubCategoriesData().get(position).getCategoryID();
                Log.e("categoryID", subsubCategoryID);
            }
        });
    }

    private void deleteProductApiCall(String proID) {
        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", proID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEPRODUCT, AddProductActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("deleteQResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void addProduct() {
        Animation shake = AnimationUtils.loadAnimation(AddProductActivity.this, R.anim.shake);
        if (productTitle.getText().toString().isEmpty() || productTitle.getText().toString().startsWith(" ")) {
            productTitle.getParent().requestChildFocus(productTitle, productTitle);
            productTitle.startAnimation(shake);
        } else if (category.getText().toString().isEmpty()) {
            category.getParent().requestChildFocus(category, category);
            category.startAnimation(shake);
        } else if (subCategory.getText().toString().isEmpty()) {
            subCategory.getParent().requestChildFocus(subCategory, subCategory);
            subCategory.startAnimation(shake);
        } else if (subSubCategory.getText().toString().isEmpty()) {
            subSubCategory.getParent().requestChildFocus(subSubCategory, subSubCategory);
            subSubCategory.startAnimation(shake);
        } else if (productPrice.getText().toString().isEmpty() || productPrice.getText().toString().startsWith(" ")) {
            productPrice.getParent().requestChildFocus(productPrice, productPrice);
            productPrice.startAnimation(shake);
        } else if (brandName.getText().toString().isEmpty() || brandName.getText().toString().startsWith(" ")) {
            brandName.getParent().requestChildFocus(brandName, brandName);
            brandName.startAnimation(shake);
        } else if (itemCondition.getText().toString().isEmpty()) {
            itemCondition.getParent().requestChildFocus(itemCondition, itemCondition);
            itemCondition.startAnimation(shake);
        } else if (productDetail.getText().toString().isEmpty()) {
            productDetail.getParent().requestChildFocus(productDetail, productDetail);
            productDetail.startAnimation(shake);
        } else if (deliveryCost.getText().toString().isEmpty() || deliveryCost.getText().toString().startsWith(" ")) {
            deliveryCost.getParent().requestChildFocus(deliveryCost, deliveryCost);
            deliveryCost.startAnimation(shake);
        } else if (estimatedDeliveryTime.getText().toString().isEmpty()) {
            estimatedDeliveryTime.getParent().requestChildFocus(estimatedDeliveryTime, estimatedDeliveryTime);
            estimatedDeliveryTime.startAnimation(shake);
        } else {
            new UploadFileToServer().execute();
        }
    }

    public void displayCurrencyInfoForLocale(Locale locale) {
        Log.e("Locale", locale.getDisplayName());
        currency = Currency.getInstance(locale);
        Log.e("Currency Code:", currency.getCurrencyCode());
        Log.e("Symbol:", currency.getSymbol());
        Log.e("DefaultFractionDigits:", currency.getDefaultFractionDigits() + " ");
        String pricehint = "Enter price in " + currency.getCurrencyCode();
        productPrice.setHint(pricehint);
    }

    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;
        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }
    }

    private void showDialogforVideoChoice() {
        new AlertDialog.Builder(AddProductActivity.this)
                .setTitle("Select video")
                .setMessage("Please choose video selection type")
                .setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openVideoCapture();
                    }
                })
                .setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        pickFromGallery();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void openVideoCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CAMERA_ACCESS_PERMISSION);
            } else {
                Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
            }
        } else {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_READ_ACCESS_PERMISSION);
            } else {
                Intent intent = new Intent();
                intent.setTypeAndNormalize("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
            }
        } else {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_READ_ACCESS_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setTypeAndNormalize("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_TRIMMER);
        } else if (requestCode == REQUEST_CAMERA_ACCESS_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent videoCapture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(videoCapture, REQUEST_VIDEO_TRIMMER);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == confirmBtn) {
            addProduct();
        }
    }


    private void getProductDetailsApiCall() {
        CustomLoader.showDialog(AddProductActivity.this);
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.PRODUCT_DETAILS + sharedpreferences.getString("UserID", " ") + "&ProductID=" + id + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, AddProductActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetHomeProductsResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("product");
                            JSONArray similar_products = jsonObject.getJSONArray("similar_products");
                            String About = user_info.getString("About");
                            String BoothCoverImage = user_info.getString("BoothCoverImage");
                            String BoothImage = user_info.getString("BoothImage");
                            String BoothName = user_info.getString("BoothName");
                            String BoothType = user_info.getString("BoothType");
                            String CategoryID = user_info.getString("CategoryID");
                            String CategoryName = user_info.getString("CategoryName");
                            String CityID = user_info.getString("CityID");
                            String CityName = user_info.getString("CityName");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String CreatedAt = user_info.getString("CreatedAt");
                            String CreatedBy = user_info.getString("CreatedBy");
                            String Currency = user_info.getString("Currency");
                            String CurrencySymbol = user_info.getString("CurrencySymbol");
                            String DeliveryCharges = user_info.getString("DeliveryCharges");
                            String DeliveryTime = user_info.getString("DeliveryTime");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String DeviceType = user_info.getString("DeviceType");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String Hide = user_info.getString("Hide");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsActive = user_info.getString("IsActive");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            String LikesCount = user_info.getString("LikesCount");
                            String Mobile = user_info.getString("Mobile");
                            String Notification = user_info.getString("Notification");
                            String OnlineStatus = user_info.getString("OnlineStatus");
                            String OS = user_info.getString("OS");
                            String OTP = user_info.getString("OTP");
                            String OutOfStock = user_info.getString("OutOfStock");
                            String Password = user_info.getString("Password");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String ProductBrandName = user_info.getString("ProductBrandName");
                            String ProductDescription = user_info.getString("ProductDescription");
                            String ProductID = user_info.getString("ProductID");
                            String ProductImages = user_info.getString("ProductImages");
                            String ProductPrice = user_info.getString("ProductPrice");
                            String ProductTextID = user_info.getString("ProductTextID");
                            String ProductType = user_info.getString("ProductType");
                            String ProductVideo = user_info.getString("ProductVideo");
                            String ProductVideoThumbnail = user_info.getString("ProductVideoThumbnail");
                            String RoleID = user_info.getString("RoleID");
                            String SortOrder = user_info.getString("SortOrder");
                            String SubCategoryName = user_info.getString("SubCategoryName");
                            String SubSubCategoryName = user_info.getString("SubSubCategoryName");
                            String SystemLanguageID = user_info.getString("SystemLanguageID");
                            String Title = user_info.getString("Title");
                            String UpdatedAt = user_info.getString("UpdatedAt");
                            String UpdatedBy = user_info.getString("UpdatedBy");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");
                            String UserTextID = user_info.getString("UserTextID");
                            String Verification = user_info.getString("Verification");
                            String IsPromoted = user_info.getString("IsPromoted");
                            String ProductAverageRating = user_info.getString("ProductAverageRating");
                            String ProductRatingsCount = user_info.getString("ProductRatingsCount");
                            String ViewsCount = user_info.getString("ViewsCount");
                            if (OutOfStock.equals("0")) {
                                availabilitySwitch.setChecked(true);
                                outofStock = "0";
                            } else if (OutOfStock.equals("1")) {
                                availabilitySwitch.setChecked(false);
                                outofStock = "1";
                            }
                            subsubCategoryID = CategoryID;
                            productTitle.setText(Title);
                            category.setText(CategoryName);
                            subCategory.setText(SubCategoryName);
                            subSubCategory.setText(SubSubCategoryName);
                            productPrice.setText(ProductPrice);
                            brandName.setText(ProductBrandName);
                            productDetail.setText(ProductDescription);
                            estimatedDeliveryTime.setText(DeliveryTime);
                            for (int i = 0; i < itemConditionsList.size(); i++){
                                if (ProductType.equals(itemConditionsList.get(i))){
                                    itemCondition.setText(itemConditionsList.get(i));
                                }
                            }
                            deliveryCost.setText(DeliveryCharges);
                            int count = 0;
                            JSONArray productImages = user_info.getJSONArray("ProductImages");
                            for (int j = 0; j < productImages.length(); j++) {
                                JSONObject imagesObj = productImages.getJSONObject(j);
                                String ProductCompressedImage = imagesObj.getString("ProductCompressedImage");
                                String ProductImage = imagesObj.getString("ProductImage");
                                imagesdata.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                count++;
                            }
                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(AddProductActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // image saver from bitmap
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(AddProductActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayList.add(myUri);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
