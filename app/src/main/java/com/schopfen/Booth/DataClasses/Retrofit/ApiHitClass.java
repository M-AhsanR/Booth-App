package com.schopfen.Booth.DataClasses.Retrofit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.schopfen.Booth.R;
import com.schopfen.Booth.interfaces.ApiResponseListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ApiHitClass {

    SimpleArcDialog simpleArcDialog;

    public <T> void callRetrofit(final Activity context, final Call<T> apiCall, final ApiResponseListener responseListener, final boolean shouldCloseActivityAgainstNoInternet, final boolean shouldShowLoadingDialog) {
        if (NetworkUtil.isNetworkAvailable(context)) {
            if (shouldShowLoadingDialog) {
                simpleArcDialog = showProgressDialog(context);
                simpleArcDialog.show();
            }
            apiCall.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    if (shouldShowLoadingDialog) {
                        simpleArcDialog.dismiss();
                    }
                    if (response.isSuccessful()) {
                        responseListener.onSuccess(response);
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {
                    if (shouldShowLoadingDialog) {
                        simpleArcDialog.dismiss();
                    }
                    responseListener.onFailure(t);
                }
            });

        } else {
            Toast.makeText(context, "Check your internet connection", Toast.LENGTH_LONG).show();

        }
    }

    public static SimpleArcDialog showProgressDialog(Context context) {

        SimpleArcDialog mDialog;
        mDialog = new SimpleArcDialog(context);
        int[] colorss = new int[]{Color.parseColor("#FD573C"), Color.parseColor("#CA3031")};
        ArcConfiguration configuration = new ArcConfiguration(context);
        configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
        configuration.setColors(colorss);
        configuration.setText("");
        mDialog.setConfiguration(configuration);
        mDialog.setCancelable(false);
        mDialog.show();
        return mDialog;
    }
}

