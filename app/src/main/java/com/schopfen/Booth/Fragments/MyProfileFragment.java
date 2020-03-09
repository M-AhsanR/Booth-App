package com.schopfen.Booth.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Base64;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hbb20.CountryCodePicker;
import com.schopfen.Booth.Activities.BoothMainActivity;
import com.schopfen.Booth.Activities.CategoriesListActivity;
import com.schopfen.Booth.Activities.EditBoothActivity;
import com.schopfen.Booth.Activities.FollowersActivity;
import com.schopfen.Booth.Activities.FollowersNewActivity;
import com.schopfen.Booth.Activities.FollowingsActivity;
import com.schopfen.Booth.Activities.InboxActivity;
import com.schopfen.Booth.Activities.LoginActivity;
import com.schopfen.Booth.Activities.ProductDetailsActivity;
import com.schopfen.Booth.Activities.RatingAndReviewsActivity;
import com.schopfen.Booth.Activities.RegisterBoothActivity;
import com.schopfen.Booth.Activities.SettingsActivity;
import com.schopfen.Booth.Activities.SetupBoothProfileActivity;
import com.schopfen.Booth.Activities.ShoppingCartActivity;
import com.schopfen.Booth.Adapters.WishlistAdapter;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.ApiStructure.StringBody;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.DataClasses.CustomItemClickListener;
import com.schopfen.Booth.Models.CitiesData;
import com.schopfen.Booth.Models.ProductImagesData;
import com.schopfen.Booth.Models.UserDetailsData;
import com.schopfen.Booth.Models.WishListEtcData;
import com.schopfen.Booth.R;
import com.schopfen.Booth.Retrofit.AndroidMultiPartEntity;
import com.squareup.picasso.Picasso;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.yalantis.ucrop.UCrop;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.fxn.pix.Pix.start;


public class MyProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    NestedScrollView nestedScrollView;
    SwipeRefreshLayout profile_swipe;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

    SharedPreferences BoothstoreProductsPrefes;
    SharedPreferences.Editor BoothstoreProductsPrefesEditor;
    String BoothProductsPREFERENCES = "BOOTHPRODUCTS";
    SharedPreferences storeProductsPrefes;
    SharedPreferences.Editor storeProductsPrefesEditor;
    String ProductsPREFERENCES = "PRODUCTS";
    SharedPreferences promotedProductsPrefes;
    SharedPreferences.Editor promotedProductsPrefesEditor;
    String PromotedPREFERENCES = "PROMOTEDPRODUCTS";

    private String mParam1;
    private String mParam2;
    ImageView cam;
    Button createBtn, cancelBtn;
    CountryCodePicker ccp;
    Uri myUri;
    ArrayList<Uri> uriArrayList = new ArrayList<>();
    Bitmap bitmap;
    Dialog current_dialog;
    ImageView gifimageView, dialogGif, inbox_img;
    TextView cart_count, msg_count;
    BottomNavigationView bottomNavigationView;
    RelativeLayout header_comment, header_cart, header_switch;
    int codeRequest = 1234;
    LinearLayout wishlist_nodata, likes_nodata, purchases_nodata;
    Context thiscontext;
    Switch wishlist_switch, likes_switch, purchases_switch;
    ArrayList<String> profileArray = new ArrayList<>();

    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ArrayList<String> myreturnValue = new ArrayList<>();
    ArrayList<UserDetailsData> userDetailsData = new ArrayList<>();
    ArrayList<CitiesData> citiesData = new ArrayList<>();
    ArrayList<String> citiesNames = new ArrayList<>();
    ArrayList<String> genderArray = new ArrayList<>();
    Map<String, String> params = new HashMap<String, String>();
    ArrayList<WishListEtcData> likesData;
    ArrayList<ProductImagesData> likesImagesData;
    ArrayList<WishListEtcData> wishListData;
    ArrayList<ProductImagesData> wishListImagesData;
    ArrayList<WishListEtcData> purchasesData;
    ArrayList<ProductImagesData> purchaseImagesData;

    TextView wishlist_count, likes_count, purchases_count, rating_count;
    TextView username, address, fullName;
    CircleImageView pro_image;
    RecyclerView wishlistRecycler, purchasesRecycler, likesRecycler;
    LinearLayout editProfileBtn;
    ArrayList<String> wishListArray = new ArrayList<>();
    LinearLayout viewFollowers, viewBooths;
    LinearLayout likes_linear, wishlist_linear, categories_count_layout, purchases_linear;
    RelativeLayout settings;
    AutoCompleteTextView editCity, editGender;
    TextView editEmail, editIsVerify;
    EditText editFullname, editPhone, editUsername;
    CircleImageView dialogpro;
    LinearLayout ratingLinear;
    String wishlist_switch_data = "0";
    String likes_switch_data = "0";
    String purchases_switch_data = "0";
    int UserFollowingCount;
    String CityID;

    public MyProfileFragment() {
    }

    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        genderArray.clear();
        genderArray.add(getResources().getString(R.string.male));
        genderArray.add(getResources().getString(R.string.female));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getUserOrdersLikesWishlistApiCall();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thiscontext = container.getContext();
        return inflater.inflate(R.layout.fragment_my_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pro_image = view.findViewById(R.id.myprofile_civ);
        wishlist_nodata = view.findViewById(R.id.wishlist_nodata);
        likes_nodata = view.findViewById(R.id.likes_nodata);
        purchases_nodata = view.findViewById(R.id.purchases_nodata);
        wishlistRecycler = view.findViewById(R.id.wishlistRecycler);
        purchasesRecycler = view.findViewById(R.id.purchasesRecycler);
        likesRecycler = view.findViewById(R.id.likesRecycler);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        fullName = view.findViewById(R.id.fullName);
        address = view.findViewById(R.id.address);
        username = view.findViewById(R.id.userName);
        viewFollowers = view.findViewById(R.id.viewFollowers);
        settings = view.findViewById(R.id.settings);
        gifimageView = view.findViewById(R.id.gifmain);
        nestedScrollView = view.findViewById(R.id.nestedscrollview);
        profile_swipe = view.findViewById(R.id.profile_swipe);
        viewBooths = view.findViewById(R.id.viewBooths);
        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        header_comment = view.findViewById(R.id.header_comment);
        header_cart = view.findViewById(R.id.header_cart);
        header_switch = view.findViewById(R.id.header_switch);
        inbox_img = view.findViewById(R.id.inbox_img);
        cart_count = view.findViewById(R.id.cart_count);
        msg_count = view.findViewById(R.id.msg_count);
        wishlist_linear = view.findViewById(R.id.wishlist_linear);
        likes_linear = view.findViewById(R.id.likes_linear);
        categories_count_layout = view.findViewById(R.id.categories_count_layout);
        purchases_linear = view.findViewById(R.id.purchases_linear);
        wishlist_count = view.findViewById(R.id.wishlist_count);
        likes_count = view.findViewById(R.id.likes_count);
        purchases_count = view.findViewById(R.id.purchases_count);
        ratingLinear = view.findViewById(R.id.rating);
        rating_count = view.findViewById(R.id.rating_count);
        wishlist_switch = view.findViewById(R.id.wishlist_switch);
        likes_switch = view.findViewById(R.id.likes_switch);
        purchases_switch = view.findViewById(R.id.purchases_switch);

        Fresco.initialize(getActivity());

        categories_count_layout.setOnClickListener(this);
        ratingLinear.setOnClickListener(this);

        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        storeProductsPrefes = getActivity().getSharedPreferences(ProductsPREFERENCES, Context.MODE_PRIVATE);
        storeProductsPrefesEditor = storeProductsPrefes.edit();
        promotedProductsPrefes = getActivity().getSharedPreferences(PromotedPREFERENCES, Context.MODE_PRIVATE);
        promotedProductsPrefesEditor = promotedProductsPrefes.edit();
        BoothstoreProductsPrefes = getActivity().getSharedPreferences(BoothProductsPREFERENCES, Context.MODE_PRIVATE);
        BoothstoreProductsPrefesEditor = storeProductsPrefes.edit();

        nestedScrollView.setFocusableInTouchMode(true);
        nestedScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        initiateListeners();
        getUserDetails();
        getCities();
        getUserOrdersLikesWishlistApiCall();

        profile_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserDetails();
                getCities();
                getUserOrdersLikesWishlistApiCall();
            }
        });

        wishlist_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wishlist_switch.isChecked()) {
                    wishlist_switch.setChecked(false);
                    wishlist_switch_data = "0";
                    wish_likes_purchases_Switch_data();
                } else {
                    wishlist_switch.setChecked(true);
                    wishlist_switch_data = "1";
                    wish_likes_purchases_Switch_data();
                }
            }
        });

        likes_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (likes_switch.isChecked()) {
                    likes_switch.setChecked(false);
                    likes_switch_data = "0";
                    wish_likes_purchases_Switch_data();
                } else {
                    likes_switch.setChecked(true);
                    likes_switch_data = "1";
                    wish_likes_purchases_Switch_data();
                }
            }
        });

        purchases_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (purchases_switch.isChecked()) {
                    purchases_switch.setChecked(false);
                    purchases_switch_data = "0";
                    wish_likes_purchases_Switch_data();
                } else {
                    purchases_switch.setChecked(true);
                    purchases_switch_data = "1";
                    wish_likes_purchases_Switch_data();
                }
            }
        });

//        RecyclerView.LayoutManager purchaseslayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
//        purchasesRecycler.setLayoutManager(purchaseslayoutManager);
//        WishlistAdapter purchasesAdapter = new WishlistAdapter(getActivity(), wishListArray, new CustomItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//                Log.e("position", position + " ");
//            }
//        });
//        purchasesRecycler.setAdapter(purchasesAdapter);
//        purchasesAdapter.notifyDataSetChanged();

    }

    private void wish_likes_purchases_Switch_data() {

        Map<String, String> params_switch = new HashMap<String, String>();

        params_switch.put("UserID", sharedpreferences.getString("UserID", " "));
        params_switch.put("IsLikesPrivate", likes_switch_data);
        params_switch.put("IsWishListPrivate", wishlist_switch_data);
        params_switch.put("IsOrdersPrivate", purchases_switch_data);
        params_switch.put("OS", Build.VERSION.RELEASE);
        params_switch.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, getActivity(), params_switch, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } else {
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
        });
    }

    @Override
    public void onClick(View v) {
        if (v == settings) {
            Intent i = new Intent(getActivity(), SettingsActivity.class);
            startActivity(i);
        } else if (v == viewFollowers) {
            mEditor.putString("typetype", "user").apply();
            mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).commit();
            mEditor.putString("followers", String.valueOf(userDetailsData.get(0).getUserFollowersCount())).commit();
            mEditor.putString("followings", String.valueOf(UserFollowingCount)).commit();
            startActivity(new Intent(getActivity(), FollowersNewActivity.class));
        } else if (v == editProfileBtn) {
            editProfileFunction();
        } else if (v == viewBooths) {
            mEditor.putString("OtherUserID", sharedpreferences.getString("UserID", "")).apply();
            startActivity(new Intent(getActivity(), FollowingsActivity.class));
        } else if (v == header_comment) {
//            getUserOrdersLikesWishlistApiCall();
            startActivity(new Intent(getActivity(), InboxActivity.class));
        } else if (v == header_cart) {
            startActivity(new Intent(getActivity(), ShoppingCartActivity.class));
        } else if (v == categories_count_layout) {
            Intent intent = new Intent(getActivity(), CategoriesListActivity.class);
            intent.putExtra("Activity", "Profile");
            startActivity(intent);
        } else if (v == ratingLinear) {
            Intent intent = new Intent(getActivity(), RatingAndReviewsActivity.class);
            intent.putExtra("userID", sharedpreferences.getString("UserID", " "));
            intent.putExtra("Activity", "My");
            startActivity(intent);
        } else if (v == header_switch) {
            if (isNetworkAvailable()){
                Dialog dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
                dialog.setContentView(R.layout.areyousureloader);
                // dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                Button btn_no = dialog.findViewById(R.id.btn_no);
                Button btn_yes = dialog.findViewById(R.id.btn_yes);
                TextView dialog_alert_text = dialog.findViewById(R.id.dialog_alert_text);
                dialog_alert_text.setText(getResources().getString(R.string.switch_account_string));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btn_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switchAccount();
                        dialog.dismiss();
                    }
                });
            }else {
                Toast.makeText(getActivity(), "Cannot connect to internet... please check your connection!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void switchAccount() {
        CustomLoader.showDialog(getActivity());
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("LastState", "booth");
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, getActivity(), params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            int BoothCategoriesCount = user_info.getInt("BoothCategoriesCount");
                            String IsProfileCustomized = user_info.getString("IsProfileCustomized");

                            mEditor.putString("LastState", LastState).commit();

                            storeProductsPrefesEditor.clear().commit();
                            promotedProductsPrefesEditor.clear().commit();
                            BoothstoreProductsPrefesEditor.clear().commit();

                            if (BoothName.isEmpty()) {
                                // startActivity(new Intent(SettingsActivity.this, EditBoothActivity.class));
                                // finish();
                                Intent intent = new Intent(getActivity(), EditBoothActivity.class);
                                intent.putExtra("Activity", "Yes");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else if (BoothCategoriesCount == 0) {
                                // startActivity(new Intent(SettingsActivity.this, CategoriesListActivity.class));
                                // finish();
                                Intent intent = new Intent(getActivity(), CategoriesListActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("Activity", "SignUp");
                                startActivity(intent);
                            } else if (IsProfileCustomized.equals("0")) {
                                // startActivity(new Intent(SettingsActivity.this, SetupBoothProfileActivity.class));
                                //finish();
                                Intent intent = new Intent(getActivity(), SetupBoothProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
//                                startActivity(new Intent(SettingsActivity.this, BoothMainActivity.class));
//                                finish();
                                Intent intent = new Intent(getActivity(), BoothMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }

                            CustomLoader.dialog.dismiss();
                        } else {
                            CustomLoader.dialog.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finishAffinity();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // This Function will be called when edit profile button will be clicked.
    private void editProfileFunction() {

        current_dialog = new Dialog(getActivity(), android.R.style.ThemeOverlay_Material_Dialog_Alert);
        current_dialog.setContentView(R.layout.edit_profile_dialog_sa);

        cancelBtn = current_dialog.findViewById(R.id.btn_cancel_sa);
        createBtn = current_dialog.findViewById(R.id.btn_update_sa);
        cam = current_dialog.findViewById(R.id.camera_dialog_sa);
        dialogpro = current_dialog.findViewById(R.id.myprofilepic);
        editFullname = current_dialog.findViewById(R.id.fullname);
        editUsername = current_dialog.findViewById(R.id.userName);
        editPhone = current_dialog.findViewById(R.id.phone);
        editEmail = current_dialog.findViewById(R.id.email);
        editCity = current_dialog.findViewById(R.id.city);
        editGender = current_dialog.findViewById(R.id.gender);
        editIsVerify = current_dialog.findViewById(R.id.isVerify);
        dialogGif = current_dialog.findViewById(R.id.dialog_profile_gif);
        ccp = current_dialog.findViewById(R.id.ccpicker_edit);
        ccp.registerCarrierNumberEditText(editPhone);

        Picasso.get().load(Constants.URL.IMG_URL + userDetailsData.get(0).getCompressedImage()).placeholder(R.drawable.user).into(dialogpro);
        dialogGif.setVisibility(View.GONE);
        cam.setVisibility(View.VISIBLE);
        dialogpro.setVisibility(View.VISIBLE);
        editCity.setText(userDetailsData.get(0).getCityTitle());
        editFullname.setText(userDetailsData.get(0).getFullName());

        String gender = "";
        if (userDetailsData.get(0).getGender().equals("Male")){
            gender = genderArray.get(0);
        }else {
            gender = genderArray.get(1);
        }

        editGender.setText(gender);
        if (userDetailsData.get(0).getMobile().isEmpty()) {
            editPhone.setText("");
        } else {
            int length = ccp.getSelectedCountryCodeWithPlus().length();
            editPhone.setText(userDetailsData.get(0).getMobile().substring(length));
        }

        editUsername.setText(userDetailsData.get(0).getUserName());
        editEmail.setText(userDetailsData.get(0).getEmail());


        if (userDetailsData.get(0).getMobile().isEmpty()) {
            editIsVerify.setText("");
        } else if (userDetailsData.get(0).getIsMobileVerified().equals("0")) {
            editIsVerify.setText("Verify");
        } else if (userDetailsData.get(0).getIsMobileVerified().equals("1")) {
            editIsVerify.setText("Verified");
        }

        editPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        CityID = userDetailsData.get(0).getCityID();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editCity.setAdapter(adapter);
        editCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("drawableClicked", "Checked");
                editCity.showDropDown();
                hideKeyboard(editCity);
            }
        });
        editCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {
                    CityID = citiesData.get(position).getCityID();
//                    mEditor.putString("CityID", CityID).commit();
//                    mEditor.putString("City", citiesDataArrayList.get(position).getTitle()).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, genderArray);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        editGender.setAdapter(genderAdapter);

        editGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editGender.showDropDown();
                hideKeyboard(editGender);
            }
        });
        editGender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        current_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        current_dialog.show();

        editCity.setHint(userDetailsData.get(0).getCityTitle());
        editFullname.setText(userDetailsData.get(0).getFullName());
        if (!userDetailsData.get(0).getGender().equals("")) {
            editGender.setHint(userDetailsData.get(0).getGender());
        }
//        if (userDetailsData.get(0).getMobile().isEmpty()) {
//            editPhone.setText("");
//        } else {
//            editPhone.setText(userDetailsData.get(0).getMobile().substring(3));
//        }

        editUsername.setEnabled(false);
        editUsername.setText(userDetailsData.get(0).getUserName());
        editEmail.setText(userDetailsData.get(0).getEmail());

        editIsVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editIsVerify.getText().toString().equals("Verify")) {
                    SendOTP();
                }
            }
        });

        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("cameraClick", "yes");
                myreturnValue.clear();
                start(getActivity(),                    //Activity or Fragment Instance
                        codeRequest);    //Number of images to restict selection count

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_dialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

                if (editFullname.getText().toString().trim().length() == 0) {
                    editFullname.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editUsername.getText().toString().trim().length() == 0) {
                    editUsername.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editEmail.getText().toString().trim().length() == 0) {
                    editEmail.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (!ccp.isValidFullNumber()) {
                    editPhone.getParent().requestChildFocus(editPhone, editPhone);
                    editPhone.startAnimation(shake);
                    Toast.makeText(getActivity(), "Mobile Number is not valid", Toast.LENGTH_SHORT).show();
                } else if (editGender.getText().toString().trim().length() == 0) {
                    editGender.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else if (editCity.getText().toString().trim().length() == 0) {
                    editCity.startAnimation(shake);
                    Toast.makeText(getActivity(), getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
                } else {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editEmail.getText().toString()).matches()) {
                        editEmail.startAnimation(shake);
                        Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_SHORT).show();
                    } else {
                        new UploadFileToServer().execute();

                    }
                }
            }
        });

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

    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm.isAcceptingText()){
        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//        }
    }

    void getCities() {

        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.CITIES + "?AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("GetCitiesResponse", result);
                    try {

                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {

                            JSONArray citiesArray = jsonObject.getJSONArray("cities");
                            for (int i = 0; i < citiesArray.length(); i++) {
                                JSONObject jsonObject1 = citiesArray.getJSONObject(i);

                                String CityID = jsonObject1.getString("CityID");
                                String CityLat = jsonObject1.getString("CityLat");
                                String CityLong = jsonObject1.getString("CityLong");
                                String CityPlaceID = jsonObject1.getString("CityPlaceID");
                                String Title = jsonObject1.getString("Title");
                                citiesNames.add(Title);
                                citiesData.add(new CitiesData(CityID, CityLat, CityLong, CityPlaceID, Title));
                            }

//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
//                                    android.R.layout.simple_list_item_1, citiesNames);
//                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//                            editCity.setAdapter(adapter);


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

    public void initiateListeners() {
        editProfileBtn.setOnClickListener(this);
        viewFollowers.setOnClickListener(this);
        settings.setOnClickListener(this);
        viewBooths.setOnClickListener(this);
        header_comment.setOnClickListener(this);
        header_cart.setOnClickListener(this);
        header_switch.setOnClickListener(this);
    }

    // Getting User Detals
    private void getUserDetails() {

        bottomNavigationView.setClickable(false);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getUserDetail + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE + "&OS=" + Build.VERSION.RELEASE, getActivity(), body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UserDetailsResponse", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            userDetailsData.clear();
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String About = user_info.getString("About");
                            String AuthToken = user_info.getString("AuthToken");
                            float BoothAverageRating = user_info.getInt("BoothAverageRating");
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
                            UserFollowingCount = user_info.getInt("UserFollowingCount");
                            String UserID = user_info.getString("UserID");
                            String UserName = user_info.getString("UserName");
                            String BoothUserName = user_info.getString("BoothUserName");
                            String HasUnreadMessage = user_info.getString("HasUnreadMessage");
                            String CartCount = user_info.getString("CartCount");
                            String IsWishListPrivate = user_info.getString("IsWishListPrivate");
                            String IsLikesPrivate = user_info.getString("IsLikesPrivate");
                            String IsOrdersPrivate = user_info.getString("IsOrdersPrivate");
                            String UnreadMessageCount = user_info.getString("UnreadMessageCount");
                            String BoothUnreadMessageCount = user_info.getString("BoothUnreadMessageCount");
                            String BoothHasUnreadMessage = user_info.getString("BoothHasUnreadMessage");
                            String UserOrderCount = user_info.getString("UserOrderCount");
                            String BoothOrderCount = user_info.getString("BoothOrderCount");
                            mEditor.putString("UserOrderCount", UserOrderCount);
                            mEditor.putString("BoothOrderCount", BoothOrderCount);
                            if (UnreadMessageCount.equals("0")){
                                msg_count.setVisibility(View.GONE);
                            }else {
                                msg_count.setVisibility(View.VISIBLE);
                                msg_count.setText(UnreadMessageCount);
                            }

                            if (IsWishListPrivate.equals("1")) {
                                wishlist_switch.setChecked(false);
                                wishlist_switch_data = "1";
                            } else {
                                wishlist_switch.setChecked(true);
                                wishlist_switch_data = "0";
                            }
                            if (IsLikesPrivate.equals("1")) {
                                likes_switch.setChecked(false);
                                likes_switch_data = "1";
                            } else {
                                likes_switch.setChecked(true);
                                likes_switch_data = "0";
                            }
                            if (IsOrdersPrivate.equals("1")) {
                                purchases_switch.setChecked(false);
                                purchases_switch_data = "1";
                            } else {
                                purchases_switch.setChecked(true);
                                purchases_switch_data = "0";
                            }

                            mEditor.putString("CategoriesCount", String.valueOf(UserCategoriesCount));
                            mEditor.putString("UserFollowedBooths", String.valueOf(UserFollowedBooths));
                            mEditor.putString("IsEmailVerified", IsEmailVerified);
                            mEditor.putString("IsMobileVerified", IsMobileVerified);
//                            mEditor.putString("HasUnreadMessage", HasUnreadMessage);
                            mEditor.commit();

                            float UserAverageRating = user_info.getInt("UserAverageRating");
                            rating_count.setText(String.valueOf(UserAverageRating));

                            pro_image.setVisibility(View.VISIBLE);
                            Picasso.get().load(Constants.URL.IMG_URL + Image).placeholder(R.drawable.user).into(pro_image);
                            gifimageView.setVisibility(View.GONE);
                            fullName.setText(FullName);
                            username.setText("@" + UserName);
                            address.setText(CityTitle);

                            profileArray.clear();
                            profileArray.add(Constants.URL.IMG_URL + Image);
                            pro_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new ImageViewer.Builder(getActivity(), profileArray)
                                            .setStartPosition(0)
                                            .show();
                                }
                            });

//                            if (HasUnreadMessage.equals("yes")) {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_red);
//                            } else {
//                                inbox_img.setImageResource(R.drawable.ic_comment_icon_orange);
//                            }
                            if (CartCount.equals("0")){
                                cart_count.setVisibility(View.GONE);
                            }else {
                                cart_count.setVisibility(View.VISIBLE);
                                cart_count.setText(CartCount);
                            }

                            userDetailsData.add(new UserDetailsData(About, AuthToken, BoothAverageRating, BoothCategoriesCount, BoothCoverImage, BoothFollowersCount, BoothImage, BoothName,
                                    BoothType, CityID, CityTitle, CompressedBoothCoverImage, CompressedBoothImage, CompressedCoverImage, CompressedImage, ContactDays,
                                    ContactTimeFrom, ContactTimeTo, CoverImage, DeviceToken, Email, FullName, Gender, HideContactNo, Image, IsEmailVerified, IsMobileVerified,
                                    IsProfileCustomized, LastState, Mobile, PaymentAccountBankBranch, PaymentAccountHolderName, PaymentAccountNumber, UserCategoriesCount, UserFollowedBooths,
                                    UserFollowersCount, UserID, UserName, BoothUserName));


//                            initializeEditProfileDialog();

                            bottomNavigationView.setClickable(true);

                        } else {
                            bottomNavigationView.setClickable(true);
//                            CustomLoader.dialog.dismiss();
                            String message = jsonObject.getString("message");
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        bottomNavigationView.setClickable(true);
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    bottomNavigationView.setClickable(true);
//                    CustomLoader.dialog.dismiss();
//                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeEditProfileDialog() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == codeRequest) {
            Log.e("check", String.valueOf(requestCode));
            myreturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.e("check", String.valueOf(myreturnValue));

            for (int a = 0; a < myreturnValue.size(); a++) {
                File imgFile = new File(myreturnValue.get(a));
//                Uri myUri = Uri.parse(returnValue.get(a));
                myUri = Uri.fromFile(new File(myreturnValue.get(a)));

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
                    start(getActivity(), 1234, 1);
                } else {
                    Toast.makeText(getActivity(), "Approve permissions to open Camera", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void beginCrop(Uri source) {
        Log.e("check", "result ok");
        Uri destination = Uri.fromFile(new File(getContext().getCacheDir(), "cropped"));
        UCrop.of(source, destination)
                .withAspectRatio(5f, 5f)
                .withMaxResultSize(1024, 1024)
                .start(getActivity());
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            dialogpro.setImageBitmap(null);
            dialogpro.setImageURI(UCrop.getOutput(result));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), UCrop.getOutput(result));
                bitmapArrayList.add(bitmap);
                saveToInternalStorage(bitmap);
                Log.e("chekingBit", bitmap.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(getActivity(), UCrop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
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
            CustomLoader.showDialog(getActivity());
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
            HttpPost httppost = new HttpPost(Constants.URL.updateProfile);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if (uriArrayList.size() > 0){
                    for (int i = 0; i < uriArrayList.size(); i++){
                        File sourceFile = new File(uriArrayList.get(i).getPath());
                        // Adding file data to http body
                        entity.addPart("Image", new FileBody(sourceFile));
                    }
                }

                entity.addPart("RoleID", new com.schopfen.Booth.ApiStructure.StringBody("2"));
                entity.addPart("FullName", new com.schopfen.Booth.ApiStructure.StringBody(editFullname.getText().toString()));
                entity.addPart("UserName", new com.schopfen.Booth.ApiStructure.StringBody(editUsername.getText().toString()));
                entity.addPart("Email", new com.schopfen.Booth.ApiStructure.StringBody(editEmail.getText().toString()));
                entity.addPart("Mobile", new com.schopfen.Booth.ApiStructure.StringBody(ccp.getFullNumberWithPlus()));

                if (editGender.getText().toString().isEmpty()) {
                    String genderFinal = "";
                    if (userDetailsData.get(0).getGender().equals(genderArray.get(0))){
                        genderFinal = "Male";
                    }else {
                        genderFinal = "Female";
                    }
                    Log.e("Gender", "if");
                    entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                } else {
                    String genderFinal = "";
                    if (editGender.getText().toString().equals(genderArray.get(0))){
                        genderFinal = "Male";
                    }else {
                        genderFinal = "Female";
                    }
                    Log.e("Gender", "else");
                    entity.addPart("Gender", new com.schopfen.Booth.ApiStructure.StringBody(genderFinal));
                }
                entity.addPart("CityID", new com.schopfen.Booth.ApiStructure.StringBody(CityID));
                entity.addPart("DeviceType", new com.schopfen.Booth.ApiStructure.StringBody("Android"));
                entity.addPart("DeviceToken", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("DeviceToken", " ")));
                entity.addPart("UserID", new com.schopfen.Booth.ApiStructure.StringBody(sharedpreferences.getString("UserID", " ")));
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
                JSONObject jsonObject = new JSONObject(result);
                int status = jsonObject.getInt("status");
                String message = jsonObject.getString("message");

                if (status == 200) {
                    JSONObject user_info = jsonObject.getJSONObject("user_info");
                    String UserID = user_info.getString("UserID");
                    String RoleID = user_info.getString("RoleID");
                    String UserName = user_info.getString("UserName");
                    String Email = user_info.getString("Email");
                    String Mobile = user_info.getString("Mobile");
                    String Gender = user_info.getString("Gender");
                    String CityID = user_info.getString("CityID");
                    String CompressedImage = user_info.getString("CompressedImage");
                    String Image = user_info.getString("Image");
                    String LastState = user_info.getString("LastState");
                    String BoothType = user_info.getString("BoothType");
                    String IsEmailVerified = user_info.getString("IsEmailVerified");
                    String IsMobileVerified = user_info.getString("IsMobileVerified");
                    String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                    String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                    String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                    String DeviceToken = user_info.getString("DeviceToken");
                    String Notification = user_info.getString("Notification");
                    String AuthToken = user_info.getString("AuthToken");
                    String FullName = user_info.getString("FullName");
                    String BoothName = user_info.getString("BoothName");
                    String CityTitle = user_info.getString("CityTitle");

                    mEditor.putString("UserID", UserID);
                    mEditor.putString("ApiToken", AuthToken);
                    mEditor.putString("DeviceToken", DeviceToken);
                    mEditor.putString("LastState", LastState);
                    mEditor.commit();

                    //  nameUsername.setText(FullName + " | " + "@" + UserName);

                    fullName.setText(FullName);
                    address.setText(CityTitle);
                    pro_image.setVisibility(View.VISIBLE);
                    Picasso.get().load(Constants.URL.IMG_URL + Image).placeholder(R.drawable.user).into(pro_image);

                    CustomLoader.dialog.dismiss();
                    current_dialog.dismiss();

                    getUserDetails();

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    CustomLoader.dialog.dismiss();
                    current_dialog.dismiss();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    if (status == 401) {
                        mEditor.clear().apply();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                CustomLoader.dialog.dismiss();
            }

        }

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getActivity());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory, Calendar.getInstance().getTime()+".jpg");
        uriArrayList.clear();
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

    private void imageUploadFunction() {

        CustomLoader.showDialog(getActivity());

        imgToStringFunction(bitmap);
        params.put("RoleID", "2");
        params.put("FullName", editFullname.getText().toString());
        params.put("UserName", editUsername.getText().toString());
        params.put("Email", editEmail.getText().toString());
        params.put("Mobile", ccp.getFullNumberWithPlus());
        if (editGender.getText().toString().isEmpty()) {
            Log.e("Gender", "if");
            params.put("Gender", userDetailsData.get(0).getGender());
        } else {
            Log.e("Gender", "else");
            params.put("Gender", editGender.getText().toString());
        }
        params.put("CityID", CityID);
        params.put("DeviceType", "Android");
        params.put("DeviceToken", sharedpreferences.getString("DeviceToken", " "));
        params.put("UserID", sharedpreferences.getString("UserID", " "));
        params.put("OS", Build.VERSION.RELEASE);
        params.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));


        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.updateProfile, getActivity(), params, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("UpdateResponse", "  " + result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");

                        if (status == 200) {
                            JSONObject user_info = jsonObject.getJSONObject("user_info");
                            String UserID = user_info.getString("UserID");
                            String RoleID = user_info.getString("RoleID");
                            String UserName = user_info.getString("UserName");
                            String Email = user_info.getString("Email");
                            String Mobile = user_info.getString("Mobile");
                            String Gender = user_info.getString("Gender");
                            String CityID = user_info.getString("CityID");
                            String CompressedImage = user_info.getString("CompressedImage");
                            String Image = user_info.getString("Image");
                            String LastState = user_info.getString("LastState");
                            String BoothType = user_info.getString("BoothType");
                            String IsEmailVerified = user_info.getString("IsEmailVerified");
                            String IsMobileVerified = user_info.getString("IsMobileVerified");
                            String PaymentAccountNumber = user_info.getString("PaymentAccountNumber");
                            String PaymentAccountHolderName = user_info.getString("PaymentAccountHolderName");
                            String PaymentAccountBankBranch = user_info.getString("PaymentAccountBankBranch");
                            String DeviceToken = user_info.getString("DeviceToken");
                            String Notification = user_info.getString("Notification");
                            String AuthToken = user_info.getString("AuthToken");
                            String FullName = user_info.getString("FullName");
                            String BoothName = user_info.getString("BoothName");
                            String CityTitle = user_info.getString("CityTitle");

                            mEditor.putString("UserID", UserID);
                            mEditor.putString("ApiToken", AuthToken);
                            mEditor.putString("DeviceToken", DeviceToken);
                            mEditor.putString("LastState", LastState);
                            mEditor.commit();

                            //  nameUsername.setText(FullName + " | " + "@" + UserName);

                            fullName.setText(FullName);
                            address.setText(CityTitle);
                            pro_image.setVisibility(View.VISIBLE);
                            Picasso.get().load(Constants.URL.IMG_URL + Image).placeholder(R.drawable.user).into(pro_image);

                            CustomLoader.dialog.dismiss();
                            current_dialog.dismiss();

                            getUserDetails();

                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        } else {
                            CustomLoader.dialog.dismiss();
                            current_dialog.dismiss();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                            if (status == 401) {
                                mEditor.clear().apply();
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }
                    } catch (JSONException e) {
                        CustomLoader.dialog.dismiss();
                        current_dialog.dismiss();
                        Log.e("saveBookErrorResponse1", e.toString());
                        e.printStackTrace();
                    }
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
                ByteArrayOutputStream bos = new ByteArrayOutputStream(); // Compressed
                ByteArrayOutputStream original_image = new ByteArrayOutputStream(); // Original
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos); // Compressed
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, original_image); // Original
                byte[] data = bos.toByteArray(); // Compressed
                byte[] original_data = original_image.toByteArray(); // Original
                // images[0] = Base64.encodeToString(data, Base64.DEFAULT);
                // images[1] = Base64.encodeToString(original_data, Base64.DEFAULT);
                params.put("Image", Base64.encodeToString(data, Base64.NO_WRAP));

            }
        }
        return params;
    }

    private void getUserOrdersLikesWishlistApiCall() {

//        CustomLoader.showDialog(getActivity());
        bottomNavigationView.setClickable(false);
        Map<String, String> body = new HashMap<String, String>();

        HashMap<String, String> header = new HashMap<String, String>();
        header.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));

        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.WishListLikesETC + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, getActivity(), body, header, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("WishListEtcResp", result);
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(result));
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            profile_swipe.setRefreshing(false);
                            likesData = new ArrayList<>();
                            JSONArray likesArray = jsonObject.getJSONArray("liked_products");
                            for (int i = 0; i < likesArray.length(); i++) {
                                JSONObject likesObject = likesArray.getJSONObject(i);
                                String BoothID = likesObject.getString("BoothID");
                                String BoothName = likesObject.getString("BoothName");
                                String ProductID = likesObject.getString("ProductID");
                                String ProductLikeID = likesObject.getString("ProductLikeID");
                                String Title = likesObject.getString("Title");
                                String UserID = likesObject.getString("UserID");

                                likesImagesData = new ArrayList<>();
                                JSONArray imagesArray = likesObject.getJSONArray("ProductImages");
                                for (int j = 0; j < imagesArray.length(); j++) {
                                    JSONObject imagesObject = imagesArray.getJSONObject(j);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    likesImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                likesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, likesImagesData));
                            }

                            if (likesData.isEmpty()) {
                                likes_nodata.setVisibility(View.VISIBLE);
                                likesRecycler.setVisibility(View.GONE);
                            } else {
//                                likes_linear.setVisibility(View.VISIBLE);
                                likes_nodata.setVisibility(View.GONE);
                                likesRecycler.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager likesLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                                likesRecycler.setLayoutManager(likesLayoutManager);
                                WishlistAdapter likesAdapter = new WishlistAdapter(getActivity(), likesData, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        Log.e("position", position + " ");
                                        mEditor.putString("ProductID", likesData.get(position).getProductID()).commit();
                                        startActivity(new Intent(getActivity(), ProductDetailsActivity.class));
                                    }
                                });
                                likes_count.setText("(" + String.valueOf(likesData.size()) + " " + getResources().getString(R.string.items) + ")");
                                likesRecycler.setAdapter(likesAdapter);
                            }

                            wishListData = new ArrayList<>();
                            JSONArray wishListArray = jsonObject.getJSONArray("wishlist_products");
                            for (int k = 0; k < wishListArray.length(); k++) {
                                JSONObject wishListObject = wishListArray.getJSONObject(k);
                                String BoothID = wishListObject.getString("BoothID");
                                String BoothName = wishListObject.getString("BoothName");
                                String ProductID = wishListObject.getString("ProductID");
                                String ProductLikeID = wishListObject.getString("UserWishlistID");
                                String Title = wishListObject.getString("Title");
                                String UserID = wishListObject.getString("UserID");

                                wishListImagesData = new ArrayList<>();
                                JSONArray imagesArray = wishListObject.getJSONArray("ProductImages");
                                for (int l = 0; l < imagesArray.length(); l++) {
                                    JSONObject imagesObject = imagesArray.getJSONObject(l);
                                    String ProductCompressedImage = imagesObject.getString("ProductCompressedImage");
                                    String ProductImage = imagesObject.getString("ProductImage");

                                    wishListImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));
                                }
                                wishListData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, Title, UserID, wishListImagesData));
                            }

                            if (wishListData.isEmpty()) {
                                wishlist_nodata.setVisibility(View.VISIBLE);
                                wishlistRecycler.setVisibility(View.GONE);
                            } else {
//                                wishlist_linear.setVisibility(View.VISIBLE);
                                wishlist_nodata.setVisibility(View.GONE);
                                wishlistRecycler.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                                wishlistRecycler.setLayoutManager(layoutManager);
                                WishlistAdapter wishlistAdapter = new WishlistAdapter(getActivity(), wishListData, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        Log.e("position", position + " ");
                                        mEditor.putString("ProductID", wishListData.get(position).getProductID()).commit();
                                        startActivity(new Intent(getActivity(), ProductDetailsActivity.class));
                                    }
                                });
                                wishlist_count.setText("(" + String.valueOf(wishListData.size()) + " " + getResources().getString(R.string.items) + ")");
                                wishlistRecycler.setAdapter(wishlistAdapter);
                            }

                            purchasesData = new ArrayList<>();
                            JSONArray orders = jsonObject.getJSONArray("orders");
                            for (int p = 0; p < orders.length(); p++) {
                                JSONObject ordersObject = orders.getJSONObject(p);
                                String BoothID = ordersObject.getString("BoothID");
                                String BoothName = ordersObject.getString("BoothName");
                                String ProductLikeID = ordersObject.getString("OrderRequestID");
                                String UserID = ordersObject.getString("UserID");

                                String ProductID = "";
                                String ProductTitle = "";
                                purchaseImagesData = new ArrayList<>();
                                JSONArray OrderItems = ordersObject.getJSONArray("OrderItems");
                                for (int l = 0; l < 1; l++) {
                                    JSONObject orderObject = OrderItems.getJSONObject(l);
                                    String ProductCompressedImage = orderObject.getString("ProductImage");
                                    String ProductImage = orderObject.getString("ProductImage");
                                    ProductID = orderObject.getString("ProductID");
                                    ProductTitle = orderObject.getString("ProductTitle");

                                    purchaseImagesData.add(new ProductImagesData(ProductCompressedImage, ProductImage));

                                }
                                purchasesData.add(new WishListEtcData(BoothID, BoothName, ProductID, ProductLikeID, ProductTitle, UserID, purchaseImagesData));

                            }

                            if (purchasesData.isEmpty()) {
                                purchases_nodata.setVisibility(View.VISIBLE);
                                purchasesRecycler.setVisibility(View.GONE);
                            } else {
//                                purchases_linear.setVisibility(View.VISIBLE);
                                purchases_nodata.setVisibility(View.GONE);
                                purchasesRecycler.setVisibility(View.VISIBLE);
                                RecyclerView.LayoutManager layoutManagerpurchases = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
                                purchasesRecycler.setLayoutManager(layoutManagerpurchases);
                                WishlistAdapter purchasesAdapter = new WishlistAdapter(getActivity(), purchasesData, new CustomItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int position) {
                                        Log.e("position", position + " ");
                                        mEditor.putString("ProductID", purchasesData.get(position).getProductID()).commit();
                                        startActivity(new Intent(getActivity(), ProductDetailsActivity.class));
                                    }
                                });
                                purchases_count.setText("(" + String.valueOf(purchasesData.size()) + " " + getResources().getString(R.string.items) + ")");
                                purchasesRecycler.setAdapter(purchasesAdapter);
                            }

                            bottomNavigationView.setClickable(true);

                        } else {
                            bottomNavigationView.setClickable(true);
//                            CustomLoader.dialog.dismiss();
                            Toast.makeText(getActivity(), String.valueOf(status), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        bottomNavigationView.setClickable(true);
//                        CustomLoader.dialog.dismiss();
                        e.printStackTrace();
                    }
                } else {
                    bottomNavigationView.setClickable(true);
//                    CustomLoader.dialog.dismiss();
                    Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserDetails();
        getUserOrdersLikesWishlistApiCall();
    }


}

