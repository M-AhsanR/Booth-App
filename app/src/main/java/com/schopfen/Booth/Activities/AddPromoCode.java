package com.schopfen.Booth.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.schopfen.Booth.ApiStructure.ApiModelClass;
import com.schopfen.Booth.ApiStructure.Constants;
import com.schopfen.Booth.ApiStructure.ServerCallback;
import com.schopfen.Booth.BuildConfig;
import com.schopfen.Booth.CustomLoader;
import com.schopfen.Booth.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddPromoCode extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    TextView disclaimer, valid_till;
    EditText promo_code, amount, usage, promo_title;
    AutoCompleteTextView discount_type;
    Button submitBtn;
    ArrayList<String> discount = new ArrayList<>();
    String DiscountType;
    String dateStamp;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";

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
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            mEditor = sharedpreferences.edit();
            setContentView(R.layout.activity_add_promo_code);
            PromoCodeDisclaimer();
            Initialization();
            Action();
        }else {
            setContentView(R.layout.no_internet_screen);
        }
    }

    private void Action() {
        discount.add("Percentage (%)");
        discount.add("Fixed");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddPromoCode.this,
                android.R.layout.simple_list_item_1, discount);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        discount_type.setAdapter(adapter);
        discount_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discount_type.showDropDown();
            }
        });
        discount_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        valid_till.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinalStep();
            }
        });
    }

    private void FinalStep() {
        Animation shake = AnimationUtils.loadAnimation(AddPromoCode.this, R.anim.shake);
        if (promo_title.getText().toString().isEmpty()) {
            promo_title.startAnimation(shake);
            Toast.makeText(AddPromoCode.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (promo_code.getText().toString().isEmpty()) {
            promo_code.startAnimation(shake);
            Toast.makeText(AddPromoCode.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (valid_till.getText().toString().isEmpty()) {
            valid_till.startAnimation(shake);
            Toast.makeText(AddPromoCode.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (discount_type.getText().toString().isEmpty()) {
            discount_type.startAnimation(shake);
            Toast.makeText(AddPromoCode.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        } else if (amount.getText().toString().isEmpty()) {
            amount.startAnimation(shake);
            Toast.makeText(AddPromoCode.this, getResources().getString(R.string.fillEmptyFields), Toast.LENGTH_SHORT).show();
        }
        else {
            AddPromoCodeApi();
        }
    }

    private void AddPromoCodeApi() {
        if (discount_type.getText().toString().equals(discount.get(0))) {
            DiscountType = "Percentage";
        } else if (discount_type.getText().toString().equals(discount.get(1))) {
            DiscountType = "Fixed";
        }
        Map<String, String> body = new HashMap<String, String>();
        body.put("Title", promo_title.getText().toString());
        body.put("UserID", sharedpreferences.getString("UserID", " "));
        body.put("CouponCode", promo_code.getText().toString());
        body.put("DiscountType", DiscountType);
        body.put("DiscountFactor", amount.getText().toString());
        body.put("ExpiryDate", valid_till.getText().toString());
        body.put("UsageCount", usage.getText().toString());
        body.put("IsActive", "1");
        body.put("AndroidAppVersion", String.valueOf(BuildConfig.VERSION_CODE));
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.POST, Constants.URL.createPromoCode, AddPromoCode.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("AddPromo", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 200){
                            Toast.makeText(AddPromoCode.this, message, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(AddPromoCode.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddPromoCode.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void PromoCodeDisclaimer() {
        Map<String, String> body = new HashMap<String, String>();
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Verifytoken", sharedpreferences.getString("ApiToken", " "));
        ApiModelClass.GetApiResponse(Request.Method.GET, Constants.URL.getPromoCodeDisclaimer + sharedpreferences.getString("UserID", " ") + "&AndroidAppVersion=" + BuildConfig.VERSION_CODE, AddPromoCode.this, body, headers, new ServerCallback() {
            @Override
            public void onSuccess(String result, String ERROR) {
                if (ERROR.isEmpty()) {
                    Log.e("DisclaimerResult", result);
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        int status = jsonObject.getInt("status");
                        if (status == 200) {
                            String PromoCodeDisclaimer = jsonObject.getString("PromoCodeDisclaimer");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                disclaimer.setText(Html.fromHtml(PromoCodeDisclaimer, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                disclaimer.setText(Html.fromHtml(PromoCodeDisclaimer));
                            }
                        } else {
                            String message = jsonObject.getString("message");
                            Toast.makeText(AddPromoCode.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(AddPromoCode.this, ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void datePicker() {
        String currentDate = DateFormat.getDateInstance().format(new Date().getDay());
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                AddPromoCode.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.show(getSupportFragmentManager(), "Datepickerdialog");
    }
    private void Initialization() {
        disclaimer = findViewById(R.id.promo_disclaimer);
        valid_till = findViewById(R.id.valid_till);
        promo_code = findViewById(R.id.promo_code);
        amount = findViewById(R.id.amount);
        usage = findViewById(R.id.usage);
        promo_title = findViewById(R.id.promo_title);
        discount_type = findViewById(R.id.discount_type);
        submitBtn = findViewById(R.id.promo_submit);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String date = format.format(calendar.getTime());
        valid_till.setText(date);
        dateStamp = String.valueOf(calendar.getTime().getTime() / 1000);
    }
}
