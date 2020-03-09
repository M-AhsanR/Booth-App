package com.schopfen.Booth.interfaces;


import com.schopfen.Booth.DataClasses.UpdateProfileRetrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SOService {




    @POST("updateProfile")
    Call<UpdateProfileRetrofit> createUser(@Body UpdateProfileRetrofit user);




}