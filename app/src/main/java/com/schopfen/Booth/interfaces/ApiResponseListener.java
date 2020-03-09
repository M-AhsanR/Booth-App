package com.schopfen.Booth.interfaces;


import retrofit2.Response;


public interface ApiResponseListener {

        public  <T> void onSuccess(Response<T> response);
        public <T> void onFailure(Throwable t);

}
