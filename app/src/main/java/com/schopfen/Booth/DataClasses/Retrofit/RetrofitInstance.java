package com.schopfen.Booth.DataClasses.Retrofit;


import com.schopfen.Booth.interfaces.SOService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static RetrofitInstance instance;
    private SOService service = null;

    public static String ImageURL = "http://hobbies.schopfen.com/";
    public String BASE_URL = "http://hobbies.schopfen.com/api/";

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build();

    private RetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(SOService.class);
    }


    public static RetrofitInstance getInstance() {
        if (instance == null) {
            instance = new RetrofitInstance();
        }
        return instance;
    }





    public   SOService getApiService() {
        return service;
    }
}