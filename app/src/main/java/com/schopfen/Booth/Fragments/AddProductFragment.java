package com.schopfen.Booth.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.fxn.pix.Pix;
import com.makeramen.roundedimageview.RoundedImageView;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Adapters.AddProductImagesListAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.MainCategoriesData;
import com.schopfen.Booth.Models.SubCategoriesData;
import com.schopfen.Booth.Models.SubSubCategoriesData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.schopfen.Booth.Retrofit.Config;
import com.schopfen.Booth.VideoCompressor.MediaController;
import com.schopfen.Booth.videoTrimmer.VideoTrimmerActivity;
import com.schopfen.Booth.videoTrimmer.utils.FileUtils;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
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

public class AddProductFragment extends Fragment implements View.OnClickListener {

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    RoundedImageView video;
    ImageView add_video_camera, delete_video;
    EditText productTitle, productPrice, brandName, productDetail, deliveryCost, returnPolicyEditText;
    AutoCompleteTextView category, subCategory, subSubCategory, itemCondition, estimatedDeliveryTime;
    Switch availabilitySwitch;
    Button confirmBtn;

    Map<String, String> addproductParams = new HashMap<String, String>();
    Bitmap bitmap, thumbBitmap;
    Bitmap thumbnailbitmap;
    String subsubCategoryID;
    String outofStock = "0";
    Currency currency;
    AddProductImagesListAdapter addProductImagesListAdapter;
    RecyclerView imgesList;

    RelativeLayout seventh_layout;
    RoundedImageView seventh_image;
    ImageView add_image_camera;

    long totalSize = 0;
    private String filePath = null;

    Intent videointent;
    int PICK_FROM_GALLERY = 7894;
    String filemanagerstring;
    String selectedImagePath;
    //    Cursor cursor;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    private static final int REQUEST_CAMERA_ACCESS_PERMISSION = 102;
    public static final String EXTRA_VIDEO_PATH = "EXTRA_VIDEO_PATH";
    static final String VIDEO_TOTAL_DURATION = "VIDEO_TOTAL_DURATION";

    ArrayList<String> returnValue = new ArrayList<>();
    String CropedreturnValue = "";
    ArrayList<String> aftercrop_list = new ArrayList<>();
    Uri myUri;
    Uri thumbUri;
    public static ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();

    ArrayList<MainCategoriesData> mainCategoryData;
    ArrayList<SubCategoriesData> subCategoriesData;
    ArrayList<SubSubCategoriesData> subSubCategoriesData;
    HashMap<String, String> headerParams;

    ArrayList<String> mainCategoriesNames = new ArrayList<>();
    ArrayList<String> subCategoriesNames = new ArrayList<>();
    ArrayList<String> subSubCategoriesNames = new ArrayList<>();
    ArrayList<String> itemConditionsList = new ArrayList<>();
    ArrayList<String> estimatedDeliverytimeList = new ArrayList<>();
    private ProgressDialog mProgressDialog, progressdialog;
    int intstatus = 0;
    Handler handler = new Handler();

    int mainPosition;
    int subPosition;
    public static Uri videoURI = null;
    ByteArrayOutputStream byteBuffer;
    String videofilePath;
    String videoBase64Data = "";
    String thumbBase64 = "";

    Dialog progress;
    ProgressBar circularProgressbar;
    TextView tv;
    ProgressBar mProgress;

    public static ArrayList<Uri> uriArrayList = new ArrayList<>();

    private static final int MY_SOCKET_TIMEOUT_MS = 20000;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();

        Initialization(view);
        initiateListeners();
        ImagesDimensions();
        Actions();
        getCategoriesApi();
        EstimatedandItemconditionActions();

        Locale defaultLocale = Locale.getDefault();
        displayCurrencyInfoForLocale(defaultLocale);

        progress = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
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

    }

    public void displayCurrencyInfoForLocale(Locale locale) {
        Log.e("Locale", locale.getDisplayName());
//        currency = Currency.getInstance(locale);
//        Log.e("Currency Code:", currency.getCurrencyCode());
//        Log.e("Symbol:", currency.getSymbol());
//        Log.e("DefaultFractionDigits:", currency.getDefaultFractionDigits() + " ");

        String pricehint = getResources().getString(R.string.price)+" " + sharedpreferences.getString("CurrencySymbol", "");
        productPrice.setHint(pricehint);
    }

    private void EstimatedandItemconditionActions() {
        itemConditionsList.add(getResources().getString(R.string.newtext));
        itemConditionsList.add(getResources().getString(R.string.used));
        itemConditionsList.add(getResources().getString(R.string.refurbished));
        estimatedDeliverytimeList.add("1 - 3 "+getResources().getString(R.string.days));
        estimatedDeliverytimeList.add("3 - 5 "+getResources().getString(R.string.days));
        estimatedDeliverytimeList.add("5 - 10 "+getResources().getString(R.string.days));
        estimatedDeliverytimeList.add("10 - 15 "+getResources().getString(R.string.days));
        estimatedDeliverytimeList.add("15 - 20 "+getResources().getString(R.string.days));
        estimatedDeliverytimeList.add("approx. 45 "+getResources().getString(R.string.days));

        ArrayAdapter<String> itemConditionAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, itemConditionsList);
        ArrayAdapter<String> edtAdapter = new ArrayAdapter<String>(getActivity(),
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

    private void getCategoriesApi() {
        Map<String, String> postParams = new HashMap<>();
        postParams.put("UserID", sharedpreferences.getString("UserID", ""));
        postParams.put("Type", "booth");
        postParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        postParams.put("OS", String.valueOf(Build.VERSION.RELEASE));

        headerParams = new HashMap<>();
        headerParams.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.GETCATEGORIES, getActivity(), postParams, headerParams, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("CategoriesResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            mainCategoriesNames.clear();

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
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void fillMainCategories() {

        ArrayAdapter<String> mainCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, mainCategoriesNames);
        mainCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        category.setAdapter(mainCateAutoCompAdapter);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(category);
                category.showDropDown();
            }
        });

        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    subCategory.setText("");
                    subSubCategory.setText("");
                    mainPosition = position;
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

        ArrayAdapter<String> subCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, subCategoriesNames);
        subCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subCategory.setAdapter(subCateAutoCompAdapter);

        subCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(subCategory);
                subCategory.showDropDown();
            }
        });

        subCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    subSubCategory.setText("");
                    subSubCategoriesNames.clear();
                    for (int i = 0; i < mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().size(); i++) {
                        subSubCategoriesNames.add(mainCategoryData.get(mainPosition).getSubCategoriesData().get(position).getSubSubCategoriesData().get(i).getTitle());
                    }

                    subPosition = position;

                    fillSubSubCategories();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void fillSubSubCategories() {

        ArrayAdapter<String> subSubCateAutoCompAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, subSubCategoriesNames);
        subSubCateAutoCompAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        subSubCategory.setAdapter(subSubCateAutoCompAdapter);

        subSubCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(subSubCategory);
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void Actions() {

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

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickFromGallery();
            }
        });

        delete_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoURI != null) {
                    final Dialog alertdialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
                    alertdialog.setContentView(R.layout.view_del_dialog);
                    LinearLayout detail = alertdialog.findViewById(R.id.choose_image);
                    LinearLayout del = alertdialog.findViewById(R.id.choose_video);

                    alertdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertdialog.setCancelable(true);
                    alertdialog.show();

                    detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertdialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(videoURI.getPath()), "video/*");
                            startActivity(Intent.createChooser(intent, "Complete action using"));
                        }
                    });

                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertdialog.dismiss();
                            add_video_camera.setVisibility(View.VISIBLE);
                            delete_video.setVisibility(View.GONE);
                            thumbBase64 = "";
                            videoBase64Data = "";
                            video.setImageBitmap(null);
                            video.setImageResource(R.drawable.rounded_corners_orange_btn);
                        }
                    });
                }

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
                    start(getActivity(),                    //Activity or Fragment Instance
                            1234,                //Request code for activity results
                            1);
                }
            }
        });

    }

    private void showDialogforVideoChoice() {

        new AlertDialog.Builder(getActivity())
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

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

    private void openVideoCapture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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

    private void ImagesDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager()
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

    private void initiateListeners() {
        confirmBtn.setOnClickListener(this);
    }

    private void Initialization(View view) {
        video = view.findViewById(R.id.video);
        add_video_camera = view.findViewById(R.id.add_video_camera);
        delete_video = view.findViewById(R.id.delete_video);
        imgesList = view.findViewById(R.id.images_list);
        add_image_camera = view.findViewById(R.id.add_image_camera);
        seventh_layout = view.findViewById(R.id.seventh_layout);
        seventh_image = view.findViewById(R.id.seventh_image);
        category = view.findViewById(R.id.mainCategory);
        subCategory = view.findViewById(R.id.subCategory);
        subSubCategory = view.findViewById(R.id.subSubCategory);
        productTitle = view.findViewById(R.id.productTitle);
        productPrice = view.findViewById(R.id.productPrice);
        availabilitySwitch = view.findViewById(R.id.availabilitySwitch);
        brandName = view.findViewById(R.id.brandName);
        itemCondition = view.findViewById(R.id.itemCondition);
        productDetail = view.findViewById(R.id.productDetail);
        deliveryCost = view.findViewById(R.id.deliveryCost);
        returnPolicyEditText = view.findViewById(R.id.returnPolicyEditText);
        estimatedDeliveryTime = view.findViewById(R.id.estimatedDeliveryTime);
        confirmBtn = view.findViewById(R.id.confirmBtn);
    }

    private void addProduct() {
        Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
        if (bitmapArrayList.isEmpty()) {
            Toast.makeText(getActivity(), "Choose item photos", Toast.LENGTH_SHORT).show();
        } else if (productTitle.getText().toString().isEmpty() || productTitle.getText().toString().startsWith(" ")) {
            productTitle.getParent().requestChildFocus(productTitle, productTitle);
            productTitle.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Product Title", Toast.LENGTH_SHORT).show();
        } else if (category.getText().toString().isEmpty()) {
            category.getParent().requestChildFocus(category, category);
            Toast.makeText(getActivity(), "Select Category", Toast.LENGTH_SHORT).show();
            category.startAnimation(shake);
        } else if (subCategory.getText().toString().isEmpty()) {
            subCategory.getParent().requestChildFocus(subCategory, subCategory);
            subCategory.startAnimation(shake);
            Toast.makeText(getActivity(), "Select Sub Category", Toast.LENGTH_SHORT).show();
        } else if (subSubCategory.getText().toString().isEmpty()) {
            subSubCategory.getParent().requestChildFocus(subSubCategory, subSubCategory);
            subSubCategory.startAnimation(shake);
            Toast.makeText(getActivity(), "Select Sub Sub Category", Toast.LENGTH_SHORT).show();
        } else if (productPrice.getText().toString().isEmpty() || productPrice.getText().toString().startsWith(" ")) {
            productPrice.getParent().requestChildFocus(productPrice, productPrice);
            productPrice.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Product Price", Toast.LENGTH_SHORT).show();
        } else if (brandName.getText().toString().isEmpty() || brandName.getText().toString().startsWith(" ")) {
            brandName.getParent().requestChildFocus(brandName, brandName);
            brandName.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Brand Name", Toast.LENGTH_SHORT).show();
        } else if (itemCondition.getText().toString().isEmpty()) {
            itemCondition.getParent().requestChildFocus(itemCondition, itemCondition);
            itemCondition.startAnimation(shake);
            Toast.makeText(getActivity(), "Select Item Condition", Toast.LENGTH_SHORT).show();
        } else if (productDetail.getText().toString().isEmpty()) {
            productDetail.getParent().requestChildFocus(productDetail, productDetail);
            productDetail.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Product Description", Toast.LENGTH_SHORT).show();
        } else if (returnPolicyEditText.getText().toString().isEmpty()) {
            returnPolicyEditText.getParent().requestChildFocus(returnPolicyEditText, returnPolicyEditText);
            returnPolicyEditText.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Product Return Policy", Toast.LENGTH_SHORT).show();
        } else if (deliveryCost.getText().toString().isEmpty() || deliveryCost.getText().toString().startsWith(" ")) {
            deliveryCost.getParent().requestChildFocus(deliveryCost, deliveryCost);
            deliveryCost.startAnimation(shake);
            Toast.makeText(getActivity(), "Enter Delivery Cost", Toast.LENGTH_SHORT).show();
        } else if (estimatedDeliveryTime.getText().toString().isEmpty()) {
            estimatedDeliveryTime.getParent().requestChildFocus(estimatedDeliveryTime, estimatedDeliveryTime);
            estimatedDeliveryTime.startAnimation(shake);
        } else {
            if (sharedpreferences.getString("IsMobileVerified", "").equals("0")){
                Toast.makeText(getActivity(), getResources().getString(R.string.mobileisnotvarified), Toast.LENGTH_SHORT).show();
                SendOTP();
            }else {
                if (sharedpreferences.getString("IsMobileVerified", "").equals("0")){
                    Toast.makeText(getActivity(), getResources().getString(R.string.emailisnotvarified), Toast.LENGTH_SHORT).show();
                }else {
                    new UploadFileToServer().execute();
                }
            }

        }
    }

    private void SendOTP() {

        CustomLoader.showDialog(getActivity());
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                            VerifyDialog();


                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void VerifyDialog() {
        final Dialog verifyDialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
        verifyDialog.setContentView(R.layout.verify_mobile_dialog);
        EditText codeEditText = verifyDialog.findViewById(R.id.codeEditText);
        Button apply = verifyDialog.findViewById(R.id.btn_apply);
        Button cancel = verifyDialog.findViewById(R.id.btn_cancel);
        TextView resend = verifyDialog.findViewById(R.id.resend_code);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReSendOTP();
            }
        });

        verifyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDialog.dismiss();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (codeEditText.getText().toString().isEmpty()) {
                    Toast.makeText(verifyDialog.getContext(), getResources().getString(R.string.fillTheRequirdFoeld), Toast.LENGTH_SHORT).show();
                } else {
                    VerifyOTP(codeEditText.getText().toString());
                    verifyDialog.dismiss();
                }
            }
        });
    }
    private void ReSendOTP() {

        CustomLoader.showDialog(getActivity());
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.SENDOTP, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void VerifyOTP(String otp) {

        CustomLoader.showDialog(getActivity());
        Map<String, String> body = new HashMap<String, String>();
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("OTP", otp);
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VERIFYOTP, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("OTPResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            CustomLoader.dialog.dismiss();

                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        } else {
                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    /**
     * Uploading the file to server
     * */
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
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

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

                if (videoURI != null){
                    entity.addPart("ProductVideo", new FileBody(new File(videoURI.getPath())));
                    entity.addPart("ProductVideoThumbnail", new FileBody(new File(thumbUri.getPath())));
                }

                entity.addPart("UserID", new StringBody(sharedpreferences.getString("UserID", "")));
                entity.addPart("CategoryID", new StringBody(subsubCategoryID));
                entity.addPart("ProductPrice", new StringBody(productPrice.getText().toString()));
                entity.addPart("DeliveryTime", new StringBody(estimatedDeliveryTime.getText().toString()));
                entity.addPart("ProductType", new StringBody(itemCondition.getText().toString()));
                entity.addPart("OutOfStock", new StringBody(outofStock));
                entity.addPart("IsActive", new StringBody("1"));
                entity.addPart("Title", new StringBody(productTitle.getText().toString()));
                entity.addPart("ProductDescription", new StringBody(productDetail.getText().toString()));
                entity.addPart("ProductReturnPolicy", new StringBody(returnPolicyEditText.getText().toString()));
                entity.addPart("Currency", new StringBody(sharedpreferences.getString("Currency", "")));
                entity.addPart("CurrencySymbol", new StringBody(sharedpreferences.getString("CurrencySymbol", "")));
                entity.addPart("DeliveryCharges", new StringBody(deliveryCost.getText().toString()));
                entity.addPart("ProductBrandName", new StringBody(brandName.getText().toString()));
                entity.addPart("ProductImagesCount", new StringBody(String.valueOf(bitmapArrayList.size())));
                entity.addPart("AndroidAppVersion", new StringBody(String.valueOf(BuildConfig.VERSION_CODE)));

                // Extra parameters if you want to pass to server

//                Map<String, String> headers = new HashMap<>();
//                headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

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
                    final Dialog addressDialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                        }
                    });
                    RefreshItems();
                }else {
                    progress.dismiss();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


























    private void AddproductApi() {

        intstatus = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (intstatus < 50) {

                    intstatus += 1;

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            circularProgressbar.setProgress(intstatus);
                            tv.setText(intstatus + "%");

                        }
                    });
                }
            }
        }).start();

//        imgToStringFunction(bitmap);
        addproductParams.put("UserID", sharedpreferences.getString("UserID", ""));
        addproductParams.put("CategoryID", subsubCategoryID);
        addproductParams.put("ProductPrice", productPrice.getText().toString());
        addproductParams.put("DeliveryTime", estimatedDeliveryTime.getText().toString());
        addproductParams.put("ProductType", itemCondition.getText().toString());
        addproductParams.put("OutOfStock", outofStock);
        addproductParams.put("IsActive", "1");
        addproductParams.put("Title", productTitle.getText().toString());
        addproductParams.put("ProductDescription", productDetail.getText().toString());
        addproductParams.put("Currency", sharedpreferences.getString("Currency", ""));
        addproductParams.put("CurrencySymbol", sharedpreferences.getString("CurrencySymbol", ""));
        addproductParams.put("DeliveryCharges", deliveryCost.getText().toString());
        addproductParams.put("ProductBrandName", brandName.getText().toString());
        addproductParams.put("ProductImagesCount", String.valueOf(bitmapArrayList.size()));
        addproductParams.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.ADDPRODUCT, getActivity(), addproductParams, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddProductResult", " " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {

                            JSONObject product = jsonObject.getJSONObject("product");
                            String ProductID = product.getString("ProductID");

                            if (!videoBase64Data.isEmpty()) {
//                                VideoUpload(ProductID);
//                                Intent mIntent = new Intent(getActivity(), FileUploadService.class);
//                                mEditor.putString("productID", ProductID).commit();
//                                mEditor.putString("video", videoBase64Data).commit();
//                                mEditor.putString("thumb", thumbBase64).commit();
//                                FileUploadService.enqueueWork(getActivity(), mIntent);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (intstatus < 75) {

                                            intstatus += 1;

                                            try {
                                                Thread.sleep(200);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    circularProgressbar.setProgress(intstatus);
                                                    tv.setText(intstatus + "%");

                                                }
                                            });
                                        }
                                    }
                                }).start();

                                Map<String, String> body = new HashMap<String, String>();
                                body.put("UserID", sharedpreferences.getString("UserID", ""));
                                body.put("ProductID", ProductID);
                                body.put("ProductVideo", videoBase64Data);
                                body.put("ProductVideoThumbnail", thumbBase64);
                                body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

                                HashMap<String, String> header = new HashMap<String, String>();
                                header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

                                ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.VIDEOUPLOAD, getActivity(), body, header, new ServerCallback() {
                                    @Override
                                    public void onSuccess(String result, String ERROR) {

                                        if (ERROR.isEmpty()) {
                                            Log.e("AddProductResult", " " + result);
                                            JSONObject jsonObject = null;
                                            try {
                                                jsonObject = new JSONObject(result);
                                                int status = jsonObject.getInt("status");
                                                if (status == 200) {
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            while (intstatus < 100) {

                                                                intstatus += 1;

                                                                try {
                                                                    Thread.sleep(100);
                                                                } catch (InterruptedException e) {
                                                                    e.printStackTrace();
                                                                }

                                                                handler.post(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                        circularProgressbar.setProgress(intstatus);
                                                                        tv.setText(intstatus + "%");

                                                                        if (intstatus == 100) {
                                                                            progress.dismiss();
                                                                            final Dialog addressDialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                                                                }
                                                                            });
                                                                            RefreshItems();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }).start();
                                                } else {
                                                    progress.dismiss();
                                                    deleteProductApiCall(ProductID);
                                                    String message = jsonObject.getString("message");
                                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
                                                progress.dismiss();
                                                deleteProductApiCall(ProductID);
                                                e.printStackTrace();
                                            }
                                        } else {
                                            progress.dismiss();
                                            deleteProductApiCall(ProductID);
                                            Toast.makeText(getActivity(), ERROR, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                /*final Dialog addressDialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
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
                                    }
                                });*/

                            }

                            else {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (intstatus < 100) {

                                            intstatus += 1;

                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {

                                                    circularProgressbar.setProgress(intstatus);
                                                    tv.setText(intstatus + "%");

                                                    if (intstatus == 100) {

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }).start();

                            }

                        } else {
                            progress.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        progress.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    progress.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteProductApiCall(String proID) {
//        CustomLoader.showDialog((Activity) getActivity());

        Map<String, String> body = new HashMap<String, String>();
        body.put("ProductID", proID);
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        body.put("OS", Build.VERSION.RELEASE);

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.DELETEPRODUCT, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {

                if (ERROR.isEmpty()) {
                    Log.e("deleteQResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
//                            CustomLoader.dialog.dismiss();
//                            String message = jsonObject.getString("message");
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
//                    CustomLoader.dialog.dismiss();
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RefreshItems() {
        bitmapArrayList.clear();
        videoURI = null;
        productTitle.setText("");
        category.setText("");
        subCategory.setText("");
        subSubCategory.setText("");
        productPrice.setText("");
        brandName.setText("");
        productDetail.setText("");
        returnPolicyEditText.setText("");
        deliveryCost.setText("");
        itemCondition.setText("");
        estimatedDeliveryTime.setText("");
        availabilitySwitch.setChecked(false);
        add_video_camera.setVisibility(View.VISIBLE);
        delete_video.setVisibility(View.GONE);
        thumbBase64 = "";
        videoBase64Data = "";
        video.setImageBitmap(null);
        video.setImageResource(R.drawable.rounded_corners_orange_btn);

        bitmapArrayList.clear();
        uriArrayList.clear();
        seventh_layout.setVisibility(View.VISIBLE);
        addProductImagesListAdapter.notifyDataSetChanged();

    }

    private Map<String, String> imgToStringFunction(Bitmap bitmap) {

        if (bitmapArrayList.size() > 0) {
            Log.e("sizee", bitmapArrayList.size() + "");
            for (int j = 0; j < bitmapArrayList.size(); j++) {
                bitmap = bitmapArrayList.get(j);
                Log.e("bitmap", bitmap + " ");
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, original_image); // Original
                byte[] original_data = original_image.toByteArray(); // Original
                addproductParams.put("ProductImage" + String.valueOf(j + 1), Base64.encodeToString(original_data, Base64.NO_WRAP));
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
            saveThumbnailInternalStorage(thumb);
            video.setImageBitmap(thumb);
            add_video_camera.setVisibility(View.GONE);
            delete_video.setVisibility(View.VISIBLE);
            bitmap = thumb;
            ByteArrayOutputStream thumb_imageOS = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, thumb_imageOS); // Original
            }
            byte[] thumb_imageData = thumb_imageOS.toByteArray();
            thumbBase64 = Base64.encodeToString(thumb_imageData, Base64.NO_WRAP);
            // Setting the thumbnail of the video in to the image view
            InputStream inputStream = null;
            // Converting the video in to the bytes
            try {
                inputStream = getActivity().getContentResolver().openInputStream(Uri.parse("file://" + selectedVideoUri));
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
            Log.d("CompressionComplete","Compression successfully!");

        }
    }

    class VideoCompressor extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
            Log.d("StartCompression","Start video compression");
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return MediaController.getInstance().convertVideo(videoURI.getPath());
        }

        @Override
        protected void onPostExecute(Boolean compressed) {
            super.onPostExecute(compressed);
//            progressBar.setVisibility(View.GONE);
            if(compressed){

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AddProductFragment test = (AddProductFragment) getActivity().getSupportFragmentManager().findFragmentByTag("3");
        if (test != null && test.isVisible()) {
            if (resultCode == Activity.RESULT_OK && requestCode == 1234) {
                returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                for (int a = 0; a < returnValue.size(); a++) {

                    myUri = Uri.fromFile(new File(returnValue.get(a)));

                    beginCrop(myUri);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startTrimActivity(selectedUri);
                } else {
                    Toast.makeText(getActivity(), "Cannot retrieve selected video", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getActivity().getCacheDir(), "cropped"));

        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(getActivity());
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
        intent.putExtra(EXTRA_VIDEO_PATH, FileUtils.getPath(getActivity(), uri));
        intent.putExtra(VIDEO_TOTAL_DURATION, getMediaDuration(uri));
        intent.putExtra("Activity", "AddProduct");
        startActivity(intent);
    }

    private int getMediaDuration(Uri uriOfFile) {
        MediaPlayer mp = MediaPlayer.create(getActivity(), uriOfFile);
        int duration = mp.getDuration();
        return duration;
    }

    public static final long CAMERA_IMAGE_MAX_DESIRED_SIZE_IN_BYTES = 2524970;
    public static final double CAMERA_IMAGE_MAX_SIZE_AFTER_COMPRESSSION_IN_BYTES = 1893729.0;

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == getActivity().RESULT_OK) {

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), UCrop.getOutput(result));
//                CropedreturnValue = ;
                saveToInternalStorage(bitmap);



                //                Bitmap compressed = getResizedBitmapLessThan500KB(bitmap, 200, 200000);
                bitmapArrayList.add(bitmap);
                Log.e("chekingBit", bitmap.toString());
                Log.e("bitmapsize", String.valueOf(bitmapArrayList.size()));
                if (bitmapArrayList.size() == 6) {
                    seventh_layout.setVisibility(View.GONE);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                imgesList.setLayoutManager(linearLayoutManager);
                addProductImagesListAdapter = new AddProductImagesListAdapter("fragment", getActivity(), bitmapArrayList, new CustomItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                    }
                });
                imgesList.setAdapter(addProductImagesListAdapter);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
    public void onClick(View view) {
        if (view == confirmBtn) {
            if (sharedpreferences.getString("IsEmailVerified", "").equals("1") && sharedpreferences.getString("IsMobileVerified", "").equals("1")) {
                addProduct();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.pleaseVerifyYourAccount), Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden == false) {
            Log.e("result", "called");
            showdDisclimer();
        }
    }

    public void showdDisclimer() {
        Map<String, String> body = new HashMap<>();

        HashMap<String, String> header = new HashMap<>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
//        + "&Language=" + sharedpreferences.getString("language", "")
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.GETPRODUCTDISCLAIMER + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    try {
                        JSONObject mainObject = new JSONObject(String.valueOf(result));
                        int status = mainObject.getInt("status");
                        if (status == 200) {
                            String message = mainObject.getString("ProductDisclaimer");

                            final Dialog disclimar = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
                            disclimar.setContentView(R.layout.productdisclimer);
                            Button yesBtn = disclimar.findViewById(R.id.btn_yes);
                            TextView alert_message = disclimar.findViewById(R.id.alert_message);

                            alert_message.setText(Html.fromHtml(message));
                            disclimar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            disclimar.show();

                            yesBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    disclimar.dismiss();
                                }
                            });

                        } else {
                            String message = mainObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // image saver from bitmap
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
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
    // image saver from bitmap
    private String saveThumbnailInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");

        thumbUri = Uri.fromFile(new File(mypath.getAbsolutePath()));

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