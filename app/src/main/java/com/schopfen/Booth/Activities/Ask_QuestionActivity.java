package com.schopfen.Booth.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.schopfen.Booth.Retrofit.Config;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import com.schopfen.Booth.ApiStructure.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.fxn.pix.Pix.start;

public class Ask_QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView categ, subCateg;
    EditText ques_detail;
    Button btn_submitQues;
    ArrayList<String> mainCategoriesNames = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<String> returnValue = new ArrayList<>();
    ArrayList<MainCategoriesData> mainCategoryData;
    ArrayList<SubCategoriesData> subCategoriesData;
    ArrayList<SubSubCategoriesData> subSubCategoriesData;
    ArrayList<String> subCategoriesNames = new ArrayList<>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String CategID;
    Bitmap bitmap;
    Dialog progress;
    ProgressBar circularProgressbar;
    TextView tv;
    ProgressBar mProgress;
    long totalSize = 0;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    Uri myUri;
    Map<String, String> body = new HashMap<>();
    RelativeLayout first_layout, second_layout, third_layout, fourth_layout, fifth_layout, seventh_layout;
    RoundedImageView first_image, second_image, third_image, fourth_image, fifth_image, seventh_image;
    ImageView delete_first_image, delete_second_image, delete_third_image, delete_fourth_image, delete_fifth_image, add_image_camera;

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
            setContentView(R.layout.activity_ask__question);
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            InitilizeViews();
            initiateListeners();
            ImagesDimensions();
            Actions();
            getCategoriesApi();
            progress = new Dialog(Ask_QuestionActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void InitilizeViews() {
        categ = findViewById(R.id.et_categ);
        subCateg = findViewById(R.id.et_subCateg);
        ques_detail = findViewById(R.id.et_quesDetails);
        btn_submitQues = findViewById(R.id.btn_submitQues);
        first_layout = findViewById(R.id.first_layout);
        second_layout = findViewById(R.id.second_layout);
        third_layout = findViewById(R.id.third_layout);
        fourth_layout = findViewById(R.id.fourth_layout);
        fifth_layout = findViewById(R.id.fifth_layout);
        first_image = findViewById(R.id.first_image);
        second_image = findViewById(R.id.second_image);
        third_image = findViewById(R.id.third_image);
        fourth_image = findViewById(R.id.fourth_image);
        fifth_image = findViewById(R.id.fifth_image);
        delete_first_image = findViewById(R.id.delete_first_image);
        delete_second_image = findViewById(R.id.delete_second_image);
        delete_third_image = findViewById(R.id.delete_third_image);
        delete_fourth_image = findViewById(R.id.delete_fourth_image);
        delete_fifth_image = findViewById(R.id.delete_fifth_image);
        add_image_camera = findViewById(R.id.add_image_camera);
        seventh_layout = findViewById(R.id.seventh_layout);
        seventh_image = findViewById(R.id.seventh_image);
    }

    private void initiateListeners() {
        btn_submitQues.setOnClickListener(this);
    }

    private void Actions() {
        seventh_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmapArrayList.size() < 6) {
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                    start(Ask_QuestionActivity.this,                    //Activity or Fragment Instance
                            1234,                //Request code for activity results
                            1);
                }
            }
        });
        delete_first_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapArrayList.remove(bitmapArrayList.get(0));
                uriArrayList.remove(uriArrayList.get(0));
                first_image.setImageBitmap(null);
                first_layout.setVisibility(View.GONE);
                seventh_layout.setVisibility(View.VISIBLE);
            }
        });
        delete_second_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapArrayList.remove(bitmapArrayList.get(0));
                uriArrayList.remove(uriArrayList.get(0));
                second_image.setImageBitmap(null);
                second_layout.setVisibility(View.GONE);
                seventh_layout.setVisibility(View.VISIBLE);
            }
        });
        delete_third_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapArrayList.remove(bitmapArrayList.get(1));
                uriArrayList.remove(uriArrayList.get(1));
                third_image.setImageBitmap(null);
                third_layout.setVisibility(View.GONE);
                seventh_layout.setVisibility(View.VISIBLE);
            }
        });
        delete_fourth_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapArrayList.remove(bitmapArrayList.get(2));
                uriArrayList.remove(uriArrayList.get(2));
                fourth_image.setImageBitmap(null);
                fourth_layout.setVisibility(View.GONE);
                seventh_layout.setVisibility(View.VISIBLE);
            }
        });
        delete_fifth_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmapArrayList.remove(bitmapArrayList.size() - 2);
                uriArrayList.remove(uriArrayList.size()-2);
                fifth_image.setImageBitmap(null);
                fifth_layout.setVisibility(View.GONE);
                seventh_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getCategoriesApi() {
        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("UserID", sharedpreferences.getString("UserID", ""));
        postParams.put("Type", sharedpreferences.getString("LastState", ""));
        postParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        postParams.put("OS", String.valueOf(Build.VERSION.RELEASE));
        HashMap<String, String> headerParams = new HashMap<String, String>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETCATEGORIES, Ask_QuestionActivity.this, postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            JSONArray categories = jsonObject.getJSONArray("user_categories");
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
                            Toast.makeText(Ask_QuestionActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Ask_QuestionActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillMainCategories() {
        ArrayAdapter<String> mainCateAutoCompAdapter = new ArrayAdapter<String>(Ask_QuestionActivity.this,
                android.R.layout.simple_list_item_1, mainCategoriesNames);
        mainCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        categ.setAdapter(mainCateAutoCompAdapter);
        categ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categ.showDropDown();
                hideKeyboard(categ);
            }
        });
        categ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CategID = mainCategoryData.get(position).getSubCategoriesData().get(position).getCategoryID();
                    Log.e("categoryID", CategID);
                    subCateg.setText("");
                    subCategoriesNames.clear();
                    for (int i = 0; i < mainCategoryData.get(position).getSubCategoriesData().size(); i++) {
                        subCategoriesNames.add(mainCategoryData.get(position).getSubCategoriesData().get(i).getTitle());
                    }
                    fillSubCategories();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillSubCategories() {
        ArrayAdapter<String> subCateAutoCompAdapter = new ArrayAdapter<String>(Ask_QuestionActivity.this,
                android.R.layout.simple_list_item_1, subCategoriesNames);
        subCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subCateg.setAdapter(subCateAutoCompAdapter);
        subCateg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subCateg.showDropDown();
                hideKeyboard(subCateg);
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            InputMethodManager in = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            for (int a = 0; a < returnValue.size(); a++) {
                myUri = Uri.fromFile(new File(returnValue.get(a)));
                beginCrop(myUri);
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(Ask_QuestionActivity.this.getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(Ask_QuestionActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Ask_QuestionActivity.this.getContentResolver(), UCrop.getOutput(result));
                saveToInternalStorage(bitmap);
                bitmapArrayList.add(bitmap);
                Log.e("chekingBit", bitmap.toString());
                Log.e("bitmapsize", String.valueOf(bitmapArrayList.size()));
                if (bitmapArrayList.size() == 1) {
                    first_layout.setVisibility(View.VISIBLE);
                    first_image.setImageBitmap(bitmapArrayList.get(0));
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                } else if (bitmapArrayList.size() == 2) {
                    second_layout.setVisibility(View.VISIBLE);
                    second_image.setImageBitmap(bitmapArrayList.get(1));
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                } else if (bitmapArrayList.size() == 3) {
                    third_layout.setVisibility(View.VISIBLE);
                    third_image.setImageBitmap(bitmapArrayList.get(2));
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                } else if (bitmapArrayList.size() == 4) {
                    fourth_layout.setVisibility(View.VISIBLE);
                    fourth_image.setImageBitmap(bitmapArrayList.get(3));
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                } else if (bitmapArrayList.size() == 5) {
                    fifth_layout.setVisibility(View.VISIBLE);
                    fifth_image.setImageBitmap(bitmapArrayList.get(4));
                    seventh_layout.setVisibility(View.GONE);
                    Log.e("cameraClick", "yes");
                    returnValue.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(Ask_QuestionActivity.this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void ImagesDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels / 6;
        first_image.setMinimumWidth(width);
        first_image.setMaxHeight(width);
        first_image.setMaxWidth(width);
        first_image.setMinimumHeight(width);
        second_image.setMinimumWidth(width);
        second_image.setMaxHeight(width);
        second_image.setMaxWidth(width);
        second_image.setMinimumHeight(width);
        third_image.setMinimumWidth(width);
        third_image.setMaxHeight(width);
        third_image.setMaxWidth(width);
        third_image.setMinimumHeight(width);
        fourth_image.setMinimumWidth(width);
        fourth_image.setMaxHeight(width);
        fourth_image.setMaxWidth(width);
        fourth_image.setMinimumHeight(width);
        fifth_image.setMinimumWidth(width);
        fifth_image.setMaxHeight(width);
        fifth_image.setMaxWidth(width);
        fifth_image.setMinimumHeight(width);
    }

    private void ask_A_Question() {
        Animation shake = AnimationUtils.loadAnimation(Ask_QuestionActivity.this, R.anim.shake);
        if (categ.getText().toString().isEmpty()) {
            categ.getParent().requestChildFocus(categ, categ);
            categ.startAnimation(shake);
            Toast.makeText(Ask_QuestionActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (subCateg.getText().toString().isEmpty()) {
            subCateg.getParent().requestChildFocus(subCateg, subCateg);
            Toast.makeText(Ask_QuestionActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
            subCateg.startAnimation(shake);
        } else if (ques_detail.getText().toString().isEmpty()) {
            ques_detail.getParent().requestChildFocus(ques_detail, ques_detail);
            ques_detail.startAnimation(shake);
            Toast.makeText(Ask_QuestionActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else {
            new UploadFileToServer().execute();
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
            HttpPost httppost = new HttpPost(Constants.URL.ask_A_Question);
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
                }
                entity.addPart("UserID", new StringBody(sharedpreferences.getString("UserID", "")));
                entity.addPart("CategoryID", new StringBody(CategID));
                entity.addPart("QuestionDescription", new StringBody(ques_detail.getText().toString()));
                entity.addPart("ProductImagesCount", new StringBody(String.valueOf(uriArrayList.size())));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));
                // Extra parameters if you want to pass to server
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
                    final Dialog addressDialog = new Dialog(Ask_QuestionActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                    Toast.makeText(Ask_QuestionActivity.this, message, Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(Ask_QuestionActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void ask_A_QuestionApiCall() {
        CustomLoader.showDialog(Ask_QuestionActivity.this);
        if (!bitmapArrayList.isEmpty()) {
            imgToStringFunction(bitmap);
        }
        body.put("UserID", sharedpreferences.getString("UserID", ""));
        body.put("CategoryID", CategID);
        body.put("QuestionDescription", ques_detail.getText().toString());
        body.put("ProductImagesCount", String.valueOf(bitmapArrayList.size()));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ask_A_Question, Ask_QuestionActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    CustomLoader.dialog.dismiss();
                    Log.e("AddProductResult", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(Ask_QuestionActivity.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(Ask_QuestionActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(Ask_QuestionActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
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
                body.put("ProductImage" + String.valueOf(j + 1), Base64.encodeToString(original_data, Base64.DEFAULT));
                Log.e("ProductImage" + String.valueOf(j + 1), Base64.encodeToString(original_data, Base64.DEFAULT));
            }
        }
        return body;
    }

    @Override
    public void onClick(View view) {
        if (view == btn_submitQues) {
            ask_A_Question();
        }
    }
    // image saver from bitmap
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(Ask_QuestionActivity.this);
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