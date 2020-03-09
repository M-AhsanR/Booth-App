package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.schopfen.Booth.Adapters.BoothMainThemeAdapter;
import com.schopfen.Booth.Adapters.BoothRatingsRecyclerAdapter;
import com.schopfen.Booth.Adapters.SubCategoriesAdapter;
import com.schopfen.Booth.Adapters.SuggestedBoothsAdapter;
import com.schopfen.Booth.Adapters.WeekdaysAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.DataClasses.DrawableClickListener;
import com.schopfen.Booth.Models.BottomThemeStyleData;
import com.schopfen.Booth.Models.ThemesData;
import com.schopfen.Booth.Models.TopThemeStyleData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.fxn.pix.Pix.start;

public class SetupBoothProfileActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    CircleImageView circleImageView;
    CheckBox checkBox;
    ImageView btnProfileImg, btncoverCam, cover_img, mainTheme, topImg, bottomImg;
    int RequestCode,ResultCodeProfile,ResultCodeCover;
    int MIN_HRS = -1, MIN_MINT = -1, MIN_SEC = -1, TIME_ID;
    Button btn_confirm;
    EditText editText, vat_textView;
    TextView boothTitle;
    static long start_date;
    static long end_date;
    AutoCompleteTextView themeTop, themeBottom;
    TextView hrs_from, hrs_to, weekdaysEditText;
    EditText aboutProduct, mobilenum;
    Bitmap bitmap,bitmapCover;

    ArrayList<String> returnValue = new ArrayList<>();
    Uri myUri;
    ArrayList<Uri> uriArrayListProfile = new ArrayList<>();
    ArrayList<Uri> uriArrayListCover = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<Bitmap> bitmapArrayListCover = new ArrayList<>();
    Map<String, String> body = new HashMap<String, String>();

    ArrayList<TopThemeStyleData> topThemeStyleData;
    ArrayList<BottomThemeStyleData> bottomThemeStyleData;
    ArrayList<ThemesData> themesData;

    ArrayList<String> mainThemeImages = new ArrayList<>();
    ArrayList<String> topThemeNames = new ArrayList<>();
    ArrayList<String> bottomThemeNames = new ArrayList<>();
    ArrayList<String> weekdays = new ArrayList<>();
    ArrayList<String> shortWeekdays = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();

    String themeID = "", colorCode = "", TopStyleID = "", BottomStyleID = "", contactDays = "", about, contactTimeFrom = "", contactTimeTo = "", ContactNo, HideContactNo;
    int mainPosition, topPosition;

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
            setContentView(R.layout.activity_setup_booth_profile);

            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();

            getThemesApi();
            fillWeekDays();

            editText = findViewById(R.id.mobilenumber);
            editText.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    if (editText.getText().equals(".")) {
                        editText.setEnabled(false);
                    }
                }
            });
            initilizeViews();

            getBoothDetails();
        }else {
            setContentView(R.layout.no_internet_screen);
        }


    }

    // initilizing views
    private void initilizeViews() {

        // assigning id's
        circleImageView = findViewById(R.id.profPic);
        btnProfileImg = findViewById(R.id.profile_camera);
        btncoverCam = findViewById(R.id.cover_camera);
        cover_img = findViewById(R.id.cover_img);
        mainTheme = findViewById(R.id.mainTheme);
        boothTitle = findViewById(R.id.boothTitle);
        themeTop = findViewById(R.id.themeTop);
        themeBottom = findViewById(R.id.themeBottom);
        hrs_from = findViewById(R.id.hrs_from);
        hrs_to = findViewById(R.id.hrs_to);
        aboutProduct = findViewById(R.id.aboutProduct);
        checkBox = findViewById(R.id.checkBox);
        btn_confirm = findViewById(R.id.confirm_button);
        weekdaysEditText = findViewById(R.id.weekDays);
        mobilenum = findViewById(R.id.mobilenumber);
        topImg = findViewById(R.id.topImg);
        bottomImg = findViewById(R.id.bottomImg);
        vat_textView = findViewById(R.id.vat_textView);

        // passing context
        btnProfileImg.setOnClickListener(this);
        btncoverCam.setOnClickListener(this);
        mainTheme.setOnClickListener(this);
        hrs_from.setOnClickListener(this);
        hrs_to.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        weekdaysEditText.setOnClickListener(this);
    }

    // Click Listeners
    @Override
    public void onClick(View view) {
        if (view == btnProfileImg) {
            RequestCode = 1234;
            returnValue.clear();
            start(SetupBoothProfileActivity.this, RequestCode,
                    1);
        } else if (view == btncoverCam) {
            RequestCode = 1212;
            returnValue.clear();
            start(SetupBoothProfileActivity.this, RequestCode,
                    1);
        } else if (view == btn_confirm) {
            Animation shake = AnimationUtils.loadAnimation(SetupBoothProfileActivity.this, R.anim.shake);
            if(themeID.isEmpty()){
                mainTheme.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.selectColorSchemeFirst), Toast.LENGTH_LONG).show();
            }else if(TopStyleID.isEmpty()){
                themeTop.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseSetTheme), Toast.LENGTH_LONG).show();
            }else if(BottomStyleID.isEmpty()){
                themeBottom.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseSetTheme), Toast.LENGTH_LONG).show();
            }else if(aboutProduct.getText().toString().isEmpty() || aboutProduct.getText().toString().startsWith(" ")){
                aboutProduct.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_LONG).show();
            }else if(WeekdaysAdapter.selectedDays.isEmpty()){
                weekdaysEditText.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseSelectContactDays), Toast.LENGTH_LONG).show();
            }else if(contactTimeFrom.isEmpty()){
                hrs_from.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseSelectContactHours), Toast.LENGTH_LONG).show();
            }else if(contactTimeTo.isEmpty()){
                hrs_to.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseSelectContactHours), Toast.LENGTH_LONG).show();
            }else if (vat_textView.getText().toString().isEmpty()) {
                vat_textView.startAnimation(shake);
                Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseEntervatPercentage), Toast.LENGTH_LONG).show();
            }else {
                new UploadFileToServer().execute();
            }

        } else if (view == mainTheme) {
            mainThemeSelecter();
        } else if (view == hrs_from) {
            hrsFrom_TimePicker();
        } else if (view == hrs_to) {
            hrsTo_TimePicker();
        } else if (view == weekdaysEditText) {
            selectWeekDays();
        }
    }

    private void hrsFrom_TimePicker() {


        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                SetupBoothProfileActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(true);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(false);
        tpd.setAccentColor(Color.parseColor("#F38C0D"));
        tpd.setTimeInterval(1, 5, 10);
        TIME_ID = 0;
        tpd.show(getSupportFragmentManager(), "Timepickerdialog");
    }

    private void hrsTo_TimePicker() {

        if (MIN_HRS == -1 && MIN_MINT == -1 && MIN_SEC == -1) {
            Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleaseselectstarttimefirst), Toast.LENGTH_SHORT).show();
        } else {

            Calendar now = Calendar.getInstance();
            TimePickerDialog tpd = TimePickerDialog.newInstance(
                    SetupBoothProfileActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
            tpd.setThemeDark(true);
            tpd.vibrate(true);
            tpd.dismissOnPause(true);
            tpd.enableSeconds(false);
            tpd.setAccentColor(Color.parseColor("#F38C0D"));
            tpd.setMinTime(MIN_HRS, MIN_MINT, MIN_SEC);
            tpd.setTimeInterval(1, 5, 10);
            TIME_ID = 1;
            tpd.show(getSupportFragmentManager(), "Timepickerdialog");
        }
    }

    private void fillWeekDays() {
        weekdays.add(getResources().getString(R.string.monday));
        weekdays.add(getResources().getString(R.string.tuesday));
        weekdays.add(getResources().getString(R.string.wednesday));
        weekdays.add(getResources().getString(R.string.thursday));
        weekdays.add(getResources().getString(R.string.friday));
        weekdays.add(getResources().getString(R.string.saturday));
        weekdays.add(getResources().getString(R.string.sunday));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

            for (int a = 0; a < returnValue.size(); a++) {
                File imgFile = new File(returnValue.get(a));
//                Uri myUri = Uri.parse(returnValue.get(a));
                myUri = Uri.fromFile(new File(returnValue.get(a)));

                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    profilePic.setImageBitmap(myBitmap);
                }
                beginCrop(myUri);

            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    start(SetupBoothProfileActivity.this, RequestCode, 1);
                } else {
                    Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.approvepermissions), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void beginCrop(Uri source) {
        if(RequestCode == 1234){
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            UCrop.of(source, destination)
                    .withAspectRatio(5f, 5f)
                    .withMaxResultSize(1024, 1024)
                    .start(SetupBoothProfileActivity.this);
        }else if(RequestCode == 1212){
            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
            UCrop.of(source, destination)
                    .withAspectRatio(16, 9)
                    .withMaxResultSize(1024, 1024)
                    .start(this);
        }
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            if (RequestCode == 1234) {
                circleImageView.setImageBitmap(null);
                circleImageView.setImageURI(UCrop.getOutput(result));
                try {
                    ResultCodeProfile = 1234;
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
                    bitmapArrayList.add(bitmap);
                    saveToInternalStorageProfile(bitmap);
                    Log.e("chekingBit", bitmap.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (RequestCode == 1212) {
                cover_img.setImageBitmap(null);
                cover_img.setImageURI(UCrop.getOutput(result));

                try {
                    ResultCodeCover = 1212;
                    bitmapCover = MediaStore.Images.Media.getBitmap(this.getContentResolver(), UCrop.getOutput(result));
                    bitmapArrayListCover.add(bitmapCover);
                    saveToInternalStorageCover(bitmapCover);
                    Log.e("chekingBit", bitmapCover.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getThemesApi() {

        CustomLoader.showDialog(SetupBoothProfileActivity.this);

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.themes + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SetupBoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("themesResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            mainThemeImages.clear();

                            CustomLoader.dialog.dismiss();

                            JSONArray themes = jsonObject.getJSONArray("themes");
                            themesData = new ArrayList<>();
                            for (int i = 0; i < themes.length(); i++) {
                                JSONObject jsonObject1 = themes.getJSONObject(i);
                                String Color = jsonObject1.getString("Color");
                                String Image = jsonObject1.getString("Image");
                                String ThemeID = jsonObject1.getString("ThemeID");

                                topThemeStyleData = new ArrayList<>();
                                JSONArray top = jsonObject1.getJSONArray("tops");
                                for (int k = 0; k < top.length(); k++) {
                                    JSONObject topObject = top.getJSONObject(k);
                                    String TopStyleImage = topObject.getString("StyleImage");
                                    String TopStyleTitleAr = topObject.getString("StyleTitleAr");
                                    String TopStyleTitleEn = topObject.getString("StyleTitleEn");
                                    String TopThemeID = topObject.getString("ThemeID");
                                    String TopThemeStyleID = topObject.getString("ThemeStyleID");

                                    bottomThemeStyleData = new ArrayList<>();
                                    JSONArray bottom = topObject.getJSONArray("bottoms");
                                    for (int j = 0; j < bottom.length(); j++) {
                                        JSONObject bottomObject = bottom.getJSONObject(j);
                                        String BottomStyleImage = bottomObject.getString("StyleImage");
                                        String BottomStyleTitleAr = bottomObject.getString("StyleTitleAr");
                                        String BottomStyleTitleEn = bottomObject.getString("StyleTitleEn");
                                        String BottomThemeID = bottomObject.getString("ThemeID");
                                        String BottomThemeStyleID = bottomObject.getString("ThemeStyleID");

                                        bottomThemeStyleData.add(new BottomThemeStyleData(BottomStyleImage,BottomStyleTitleAr, BottomStyleTitleEn, BottomThemeID, BottomThemeStyleID));
                                    }

                                    topThemeStyleData.add(new TopThemeStyleData(TopStyleImage, TopStyleTitleAr, TopStyleTitleEn, TopThemeID, TopThemeStyleID, bottomThemeStyleData));
                                }

                                themesData.add(new ThemesData(Color, Image, ThemeID, topThemeStyleData));
                                mainThemeImages.add(Image);
                            }

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(SetupBoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SetupBoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void mainThemeSelecter() {

        final Dialog dialog = new Dialog(SetupBoothProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.booth_main_theme_dialog);
        RecyclerView themesRecycler = dialog.findViewById(R.id.mainThemeRecycler);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        Button updateBtn = dialog.findViewById(R.id.btn_update);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SetupBoothProfileActivity.this, RecyclerView.VERTICAL, false);
        themesRecycler.setLayoutManager(layoutManager);

        BoothMainThemeAdapter boothMainThemeAdapter = new BoothMainThemeAdapter(SetupBoothProfileActivity.this, themesData, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.e("position", position + " ");
                TopStyleID = "";
                BottomStyleID = "";
                topThemeNames.clear();
                bottomThemeNames.clear();
                boothTitle.setTextColor(Color.parseColor(themesData.get(position).getColor()));
                for (int i = 0; i < themesData.get(position).getTopThemeStyleData().size(); i++) {
                    topThemeNames.add(themesData.get(position).getTopThemeStyleData().get(i).getTopStyleTitleEn());
                }
                Picasso.get().load(Constants.URL.IMG_URL + themesData.get(position).getImage()).into(mainTheme);
                topImg.setImageResource(R.color.orange);
                bottomImg.setImageResource(R.color.yellow);
                mainPosition = position;
                themeID = themesData.get(position).getThemeID();
                colorCode = themesData.get(position).getColor();

                themeTop.setHint("Top");
                themeBottom.setHint("Bottom");
                fillThemeTopArray();
                dialog.dismiss();
            }
        });

        themesRecycler.addItemDecoration(new DividerItemDecoration(SetupBoothProfileActivity.this, DividerItemDecoration.VERTICAL));
        themesRecycler.setAdapter(boothMainThemeAdapter);
        boothMainThemeAdapter.notifyDataSetChanged();


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final Dialog subdialog = new Dialog(SetupBoothProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
                subdialog.setContentView(R.layout.edited_profile_sa);
                Button closeBtn = subdialog.findViewById(R.id.btn_close_sa);

                subdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                subdialog.show();

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subdialog.dismiss();
                    }
                });
            }
        });

    }

    private void fillThemeTopArray() {

        ArrayAdapter<String> subSubCateAutoCompAdapter = new ArrayAdapter<String>(SetupBoothProfileActivity.this,
                android.R.layout.simple_list_item_1, topThemeNames);
        subSubCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        themeTop.setAdapter(subSubCateAutoCompAdapter);

        themeTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeTop.showDropDown();
            }
        });
        themeTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    bottomThemeNames.clear();
                    bottomImg.setImageResource(R.color.yellow);
                    BottomStyleID = "";
                    Log.e("TopStyle", themesData.get(mainPosition).getTopThemeStyleData().get(position).getTopThemeStyleID());
                    TopStyleID = themesData.get(mainPosition).getTopThemeStyleData().get(position).getTopThemeStyleID();
                    Picasso.get().load(Constants.URL.IMG_URL + themesData.get(mainPosition).getTopThemeStyleData().get(position).getTopStyleImage()).into(topImg);
                    for (int i = 0; i < themesData.get(mainPosition).getTopThemeStyleData().get(position).getBottomArrayList().size(); i++) {
                        bottomThemeNames.add(themesData.get(mainPosition).getTopThemeStyleData().get(position).getBottomArrayList().get(i).getBottomStyleTitleEn());
                    }

                    topPosition = position;
                    fillThemeBottomArray();

                } catch (Exception e) {
                    Log.e("TopStyleError", e.getMessage());

                    e.printStackTrace();
                }
            }
        });
    }

    private void fillThemeBottomArray() {
        ArrayAdapter<String> subSubCateAutoCompAdapter = new ArrayAdapter<String>(SetupBoothProfileActivity.this,
                android.R.layout.simple_list_item_1, bottomThemeNames);
        subSubCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        themeBottom.setAdapter(subSubCateAutoCompAdapter);

        themeBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                themeBottom.showDropDown();
            }
        });
        themeBottom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    BottomStyleID = themesData.get(mainPosition).getTopThemeStyleData().get(topPosition).getBottomArrayList().get(position).getBottomThemeStyleID();
                    Picasso.get().load(Constants.URL.IMG_URL + themesData.get(mainPosition).getTopThemeStyleData().get(topPosition).getBottomArrayList().get(position).getBottomStyleImage()).into(bottomImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void selectWeekDays() {

        final Dialog dialog = new Dialog(SetupBoothProfileActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
        dialog.setContentView(R.layout.sub_categories_dialog);
        RecyclerView dialogRecycler = dialog.findViewById(R.id.recyclerView);
        Button confirmBtn = dialog.findViewById(R.id.confirmBtn);

        dialog.setCancelable(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SetupBoothProfileActivity.this, RecyclerView.VERTICAL, false);
        dialogRecycler.setLayoutManager(layoutManager);
        WeekdaysAdapter weekdaysAdapter = new WeekdaysAdapter(SetupBoothProfileActivity.this, weekdays, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                                            childModel = childlist.get(position).getSubCategoriesData().get(position).getTitle();
//                                            childArray.add(childModel);
            }
        });

        dialogRecycler.setAdapter(weekdaysAdapter);
        weekdaysAdapter.notifyDataSetChanged();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WeekdaysAdapter.selectedDays.size() == 0){
                    weekdaysEditText.setText("");
                }
                if (WeekdaysAdapter.selectedDays.size() > 0){
                    if (WeekdaysAdapter.selectedDays.size() > 3){
                        shortWeekdays.clear();
                        for (int i = 0; i < WeekdaysAdapter.selectedDays.size(); i++){
                            shortWeekdays.add(WeekdaysAdapter.selectedDays.get(i).substring(0, 3));
                        }
                        weekdaysEditText.setText(shortWeekdays.toString().substring(1, shortWeekdays.toString().length()-1));
                    }else {
                        weekdaysEditText.setText(WeekdaysAdapter.selectedDays.toString().substring(1, WeekdaysAdapter.selectedDays.toString().length()-1));
                    }

                }else {
                    weekdaysEditText.setText("");
                }
                //WeekdaysAdapter.selectedDays.clear();


                dialog.dismiss();
            }
        });

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        if (TIME_ID == 0) {
            String time_one = hourOfDay + ":" + minute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            Date date = null;
            try {
                date = fmt.parse(time_one);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            start_date = date.getTime() / 1000;
//            start_date = calendar.getTimeInMillis();
            Log.e("startDate", String.valueOf(start_date));
            contactTimeFrom = String.valueOf(start_date);
            MIN_HRS = hourOfDay;
            MIN_MINT = minute;
            MIN_SEC = second;
            hrs_to.setText("");

            hrs_from.setText(twelveHrsConverter(hourOfDay,minute));

        } else if (TIME_ID == 1) {
            String time_one = hourOfDay + ":" + minute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
            Date date = null;
            try {
                date = fmt.parse(time_one);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            end_date = date.getTime() / 1000;
//            end_date = calendar.getTimeInMillis();
            Log.e("startDate", String.valueOf(end_date));
            contactTimeTo = String.valueOf(end_date);
            hrs_to.setText(twelveHrsConverter(hourOfDay,minute));
            //hrs_to.setText(hourOfDay + ":" + minute);
        }
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero

//            progress = new Dialog(RegisterBoothActivity.this, android.R.style.ThemeOverlay_Material_Dialog_Alert);
//            progress.setContentView(R.layout.progress_dialog_circle);
//            circularProgressbar = progress.findViewById(R.id.circularProgressbar);
//            tv = progress.findViewById(R.id.tv);
//
//            Resources res = getResources();
//            Drawable drawable = res.getDrawable(R.drawable.circular);
//            mProgress = (ProgressBar) progress.findViewById(R.id.circularProgressbar);
//            mProgress.setProgress(0);   // Main Progress
//            mProgress.setSecondaryProgress(100); // Secondary Progress
//            mProgress.setMax(100); // Maximum Progress
//            mProgress.setProgressDrawable(drawable);
//
//            progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            progress.setCanc
//            elable(false);
//            progress.show();
            CustomLoader.showDialog(SetupBoothProfileActivity.this);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible

            // updating progress bar value
//            mProgress.setProgress(progress[0]);

            // updating percentage value
//            tv.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URL.setupboothprofile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (checkBox.isChecked()) {
                    HideContactNo = "1";
                } else if (!checkBox.isChecked()) {
                    HideContactNo = "0";
                } else {
                    Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleasemakesureyouwanttohidenumber), Toast.LENGTH_SHORT).show();
                }

                about = aboutProduct.getText().toString();
                ContactNo = mobilenum.getText().toString();
                contactDays = android.text.TextUtils.join(",", WeekdaysAdapter.selectedDays);

                if (uriArrayListProfile.size() > 0){
                    for (int i = 0; i < uriArrayListProfile.size(); i++){
                        File sourceFile = new File(uriArrayListProfile.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("BoothImage", new FileBody(sourceFile));
                    }
                }
                if (uriArrayListCover.size() > 0){
                    for (int i = 0; i < uriArrayListCover.size(); i++){
                        File sourceFile = new File(uriArrayListCover.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("BoothCoverImage", new FileBody(sourceFile));
                    }
                }

                entity.addPart("UserID", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("UserID", " ")));
                entity.addPart("ThemeID", new com.schopfen.Booth.ApiStructure.StringBody(themeID));
                entity.addPart("ColorCode", new com.schopfen.Booth.ApiStructure.StringBody(colorCode));
                entity.addPart("TopStyleID", new com.schopfen.Booth.ApiStructure.StringBody(TopStyleID));
                entity.addPart("BottomStyleID", new com.schopfen.Booth.ApiStructure.StringBody(BottomStyleID));
                entity.addPart("ContactDays", new com.schopfen.Booth.ApiStructure.StringBody(contactDays));
                entity.addPart("About", new com.schopfen.Booth.ApiStructure.StringBody(about));
                entity.addPart("ContactTimeFrom", new com.schopfen.Booth.ApiStructure.StringBody(contactTimeFrom));
                entity.addPart("ContactTimeTo", new com.schopfen.Booth.ApiStructure.StringBody(contactTimeTo));
                entity.addPart("ContactNo", new com.schopfen.Booth.ApiStructure.StringBody(ContactNo));
                entity.addPart("HideContactNo", new com.schopfen.Booth.ApiStructure.StringBody(HideContactNo));
                entity.addPart("VatPercentage", new com.schopfen.Booth.ApiStructure.StringBody(vat_textView.getText().toString()));
                entity.addPart("DeviceType", new com.schopfen.Booth.ApiStructure.StringBody("Android"));
                entity.addPart("DeviceToken", new com.schopfen.Booth.ApiStructure.StringBody("sharedpreferences.getString(\"DeviceToken\", \" \")"));
                entity.addPart("OS", new com.schopfen.Booth.ApiStructure.StringBody(Build.VERSION.RELEASE));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));

                // Extra parameters if you want to pass to server

//                Map<String, String> headers = new HashMap<>();
//                headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

//                totalSize = entity.getContentLength();
//                final StringEntity se = new StringEntity(entity, ContentType.APPLICATION_JSON);
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

            try {
                JSONObject jsonObject = new JSONObject(String.valueOf(result));
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");
                if (status == 200) {

                    WeekdaysAdapter.selectedDays.clear();

                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SetupBoothProfileActivity.this, ""+ message, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SetupBoothProfileActivity.this, BoothMainActivity.class);
                    startActivity(intent);
                    finishAffinity();

                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SetupBoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorageProfile(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(SetupBoothProfileActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        uriArrayListProfile.clear();
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayListProfile.add(myUri);

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
    private String saveToInternalStorageCover(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(SetupBoothProfileActivity.this);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        uriArrayListCover.clear();
        myUri = Uri.fromFile(new File(mypath.getAbsolutePath()));
        uriArrayListCover.add(myUri);

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

    private void setUpBoothApiCall() {

        if (checkBox.isChecked()) {
            HideContactNo = "1";
        } else if (!checkBox.isChecked()) {
            HideContactNo = "0";
        } else {
            Toast.makeText(SetupBoothProfileActivity.this, getResources().getString(R.string.pleasemakesureyouwanttohidenumber), Toast.LENGTH_SHORT).show();
        }

        about = aboutProduct.getText().toString();
        ContactNo = mobilenum.getText().toString();
        contactDays = android.text.TextUtils.join(",", WeekdaysAdapter.selectedDays);

        CustomLoader.showDialog(SetupBoothProfileActivity.this);


        imgToStringFunction(bitmap);
        coverimgToStringFunction(bitmapCover);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("ThemeID", themeID);
        body.put("ColorCode", colorCode);
        body.put("TopStyleID", TopStyleID);
        body.put("BottomStyleID", BottomStyleID);
        body.put("ContactDays", contactDays);
        body.put("About", about);
        body.put("ContactTimeFrom", contactTimeFrom);
        body.put("ContactTimeTo", contactTimeTo);
        body.put("ContactNo", ContactNo);
        body.put("HideContactNo", HideContactNo);
        body.put("VatPercentage", vat_textView.getText().toString());

        Log.e("body", body.toString());

        body.put("DeviceType", "Android");
        body.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        body.put("OS", Build.VERSION.RELEASE);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.setupboothprofile, SetupBoothProfileActivity.this, body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                CustomLoader.dialog.dismiss();
                if (ERROR.isEmpty()) {
                    Log.e("GetLoginResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200) {

                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SetupBoothProfileActivity.this, ""+ message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SetupBoothProfileActivity.this, BoothMainActivity.class);
                            startActivity(intent);
                            finishAffinity();

                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SetupBoothProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SetupBoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Map<String, String> imgToStringFunction(Bitmap bitmap) {

        if (bitmapArrayList.size() > 0 && ResultCodeProfile == 1234) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] original_data = original_image.toByteArray(); // Original
                body.put("BoothImage", Base64.encodeToString(original_data, Base64.NO_WRAP));
                Log.e("BoothImage", Base64.encodeToString(original_data, Base64.NO_WRAP));
            }
        }
        return body;
    }

    private Map<String, String> coverimgToStringFunction(Bitmap bitmapcover) {

        if (bitmapArrayListCover.size() > 0 && ResultCodeCover == 1212) {
            Log.e("sizee", bitmapArrayListCover.size() + "");
            for (int j = 0; j < bitmapArrayListCover.size(); j++) {
                bitmapcover = bitmapArrayListCover.get(j);
                Log.e("bitmap", bitmapcover+" ");
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmapcover.compress(Bitmap.CompressFormat.JPEG, 100, original_image); // Original
                byte[] original_data = original_image.toByteArray(); // Original
                body.put("BoothCoverImage", Base64.encodeToString(original_data, Base64.NO_WRAP));
                Log.e("BoothCoverImage", Base64.encodeToString(original_data, Base64.NO_WRAP));
            }
        }
        return body;
    }

    private  String twelveHrsConverter(int hrs,int mins){
        String convertedtime = "";
        final String time = hrs+":"+mins;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            convertedtime = new SimpleDateFormat("K:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return convertedtime;
    }

    private void getBoothDetails(){
//        CustomLoader.showDialog(SetupBoothProfileActivity.this);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ")  + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, SetupBoothProfileActivity.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()){
                    Log.e("UserDetailsResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200){

                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
                            int BoothAverageRating = user_info.getInt("BoothAverageRating");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String BoothCoverImage = user_info.getString("BoothCoverImage");
                            int BoothFollowersCount = user_info.getInt("BoothFollowersCount");
                            String BoothImage = user_info.getString("BoothImage");
                            String BoothName = user_info.getString("BoothName");
                            String BoothType = user_info.getString("BoothType");
                            String CityID = user_info.getString("CityID");
                            String CityTitle = user_info.getString("CityTitle");
                            String CompressedBoothCoverImage = user_info.getString("CompressedBoothCoverImage");
                            String CompressedBoothImage = user_info.getString("CompressedBoothImage");
                            String CompressedCoverImage = user_info.getString("CompressedCoverImage");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String ContactDays = user_info.getString("ContactDays");
                            String ContactTimeFrom = user_info.getString("ContactTimeFrom");
                            String ContactTimeTo = user_info.getString("ContactTimeTo");
                            String CoverImage = user_info.getString("CoverImage");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Email = user_info.getString("Email");
                            String FullName = user_info.getString("FullName");
                            String Gender = user_info.getString("Gender");
                            String HideContactNo = user_info.getString("HideContactNo");
                            String Image = user_info.getString("Image");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");
                            String LastState = user_info.getString("LastState");
                            String Mobile = user_info.getString("Mobile");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            int UserCategoriesCount = user_info.getInt("UserCategoriesCount");
                            int UserFollowedBooths = user_info.getInt("UserFollowedBooths");
                            int UserFollowersCount = user_info.getInt("UserFollowersCount");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");

                            JSONObject profileCustomization = user_info.getJSONObject("ProfileCustomization");
                            String BottomStyleImage = profileCustomization.getString("BottomStyleImage");
                            String ColorCode = profileCustomization.getString("ColorCode");
                            String ThemeImage = profileCustomization.getString("ThemeImage");
                            String TopStyleImage = profileCustomization.getString("TopStyleImage");
                            String BottomStyleTitleAr = profileCustomization.getString("BottomStyleTitleAr");
                            String BottomStyleTitleEn = profileCustomization.getString("BottomStyleTitleEn");
                            String TopStyleTitleAr = profileCustomization.getString("TopStyleTitleAr");
                            String TopStyleTitleEn = profileCustomization.getString("TopStyleTitleEn");
                            String ThemeID = profileCustomization.getString("ThemeID");
                            String topStyleID = profileCustomization.getString("TopStyleID");
                            String bottomStyleID = profileCustomization.getString("BottomStyleID");
                            String VatPercentage = profileCustomization.getString("VatPercentage");

                            if(!ThemeID.isEmpty()){
                                themeID = ThemeID;
                            }
                            if(!TopStyleTitleEn.isEmpty()){
                                TopStyleID = topStyleID;
                                themeTop.setHint(TopStyleTitleEn);
//                                themeTop.setHintTextColor(getResources().getColor(R.color.black));
                            }
                            if(!BottomStyleTitleEn.isEmpty()){
                                BottomStyleID = bottomStyleID;
                                themeBottom.setHint(BottomStyleTitleEn);
//                                themeBottom.setHintTextColor(getResources().getColor(R.color.black));
                            }
                            contactTimeFrom = ContactTimeFrom;
                            contactTimeTo = ContactTimeTo;
                            boothTitle.setText(BoothName);
                            boothTitle.setTextColor(Color.parseColor(ColorCode));
                            aboutProduct.setText(About);
                            hrs_from.setText(BaseClass.TimeStampToTime(ContactTimeFrom));
                            hrs_to.setText(BaseClass.TimeStampToTime(ContactTimeTo));
                            colorCode = ColorCode;
                            String[] ContactDaysArray = ContactDays.split(",");
                            if (ContactDaysArray.length > 3){
                                shortWeekdays.clear();
                                for (String s : ContactDaysArray) {
                                    shortWeekdays.add(s.substring(0, 3));
                                }
                                weekdaysEditText.setText(shortWeekdays.toString().substring(1, shortWeekdays.toString().length()-1));
                            }else {
                                weekdaysEditText.setText(ContactDays);
                            }
                            vat_textView.setText(VatPercentage);
                            WeekdaysAdapter.selectedDays.clear();
                            Log.e("ContactDays", ContactDays);
                            String word = ContactDays;
                            String[] alphabet = word.split(",");
                            for (int i=0; i<alphabet.length;i++){
                                Log.e("ContactDays", alphabet[i]);
                                WeekdaysAdapter.selectedDays.add(alphabet[i]);
                            }

                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothImage).placeholder(R.drawable.darkwhite_circle).into(circleImageView);
                            Picasso.get().load(Constants.URL.IMG_URL + CompressedBoothCoverImage).into(cover_img);
                            Picasso.get().load(Constants.URL.IMG_URL + TopStyleImage).into(topImg);
                            Picasso.get().load(Constants.URL.IMG_URL + BottomStyleImage).into(bottomImg);
                            Picasso.get().load(Constants.URL.IMG_URL + ThemeImage).into(mainTheme);

//                            CustomLoader.dialog.dismiss();

                        }else {
//                            CustomLoader.dialog.dismiss();
                            Toast.makeText(SetupBoothProfileActivity.this, String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }else {
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(SetupBoothProfileActivity.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
