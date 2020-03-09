package com.schopfen.Booth.ApiStructure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.schopfen.Booth.DataClasses.BaseClass;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by M. Ahsan Rafiq on 03/01/2018.
 */

public class ApiModelClass {

    private static final int MY_SOCKET_TIMEOUT_MS = 120000;

    public static void GetApiResponse(int method, String url , final Context ctxt, final Map<String, String> postParam, final HashMap<String, String> headers, final ServerCallback callback) {

        final String MyPREFERENCES = "MyPrefs";
        Context context = ctxt;
        String urlFinal = url;
        RequestQueue queue = null;
        final SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


//        RequestQueue queue = Volley.newRequestQueue(ctxt);

        if (queue == null) {
            queue = Volley.newRequestQueue(ctxt);
            Log.e("queueTag: ", "Setting a new request queue");
        }

        if (method == Request.Method.GET){
            urlFinal = url + "&Language=" + sharedPreferences.getString("language", "");
        }

        final StringRequest stringRequest = new StringRequest(method, urlFinal,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("MyVolleyResponse", response);
                        callback.onSuccess(response, "");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String json_js = null;


                NetworkResponse response = error.networkResponse;
                if(response != null && response.data != null){
                    int codee=response.statusCode;
                    switch(response.statusCode){
                        case 400:
                            json = new String(response.data);
                            json = trimMessage(json, "message");
                            if(json != null) displayMessage(json);
//                            json_js=json;
//                            JSONObject jj=json;
                            callback.onSuccess(json_js,json);
                            break;
                        case 401:
                            callback.onSuccess(json_js,"401");
                            break;
                        case 405:

                            callback.onSuccess(json_js,"Sending Wrong Request");
                            break;
                        default:
                            callback.onSuccess(json_js,error.toString());

                    }
                    //Additional cases
                }
                else {
                    String message;
                    if (error instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        callback.onSuccess(json_js, message);

                    } else if (error instanceof ServerError) {
                        message = "The server could not be found. Please try again after some time!!";
                        callback.onSuccess(json_js, message);

                    } else if (error instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        callback.onSuccess(json_js, message);

                    } else if (error instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                        callback.onSuccess(json_js, message);

                    } else if (error instanceof NoConnectionError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                        callback.onSuccess(json_js, message);

                    } else if (error instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                        callback.onSuccess(json_js, message);

                    }
                    else {
                        callback.onSuccess(json_js, error.toString());
                    }
                }
            }
            private void displayMessage(String json) {

                Toast.makeText(ctxt, json, Toast.LENGTH_LONG).show();

//                SharedPreferences mPrefs = ctxt.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
//                SharedPreferences.Editor mEditor = mPrefs.edit();
//                mEditor.putString("USER_EMAIL", "");
//                mEditor.putString("USER_PASSWORD", "");
//                mEditor.putString("USER_TOKEN", "");
//                mEditor.apply();



//                (() ctxt).finish();
//                Intent in=new Intent(((FirstActivity) ctxt),FirstActivity.class);
//                ((FirstActivity) ctxt).startActivity(in);


            }

            private String trimMessage(String json, String message) {
                String trimmedString = null;

                try{
                    JSONObject obj = new JSONObject(json);
                    trimmedString = obj.getString(message);
                } catch(JSONException e){
                    e.printStackTrace();
                    return null;
                }

                return trimmedString;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final  Map<String, String> body = new HashMap<String, String>();
                body.putAll(postParam);
                if (method == Method.POST){
                    body.put("Language", sharedPreferences.getString("language", ""));
                }
                
                return body;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };



        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);

    }
//    public static void GetApiResponseArray(int method, String url , final Context ctxt, Map<String, List<String>> postParam, final HashMap<String, String> headers, final ServerCallback callback) {
//
//
//        RequestQueue queue = Volley.newRequestQueue(ctxt);
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(method,
//                url, new JSONObject(postParam),
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                        callback.onSuccess(response,""); // call call back function here
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                String json = null;
//                JSONObject json_js = null;
//
//
//                NetworkResponse response = error.networkResponse;
//                if(response != null && response.data != null){
//                    int codee=response.statusCode;
//                    switch(response.statusCode){
//                        case 400:
//                            json = new String(response.data);
//                            json = trimMessage(json, "message");
//                            if(json != null) displayMessage(json);
////                            json_js=json;
////                            JSONObject jj=json;
//                            callback.onSuccess(json_js,json);
//                            break;
//                            case 401:
////
//                            callback.onSuccess(json_js,"401");
//                            break;
//                        case 405:
//
//                            callback.onSuccess(json_js,"Sending Wrong Request");
//                            break;
//                            default:
//                                callback.onSuccess(json_js,error.toString());
//
//                    }
//                    //Additional cases
//                }
//                else {
//                    String message;
//                    if (error instanceof NetworkError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                        callback.onSuccess(json_js, message);
//
//                    } else if (error instanceof ServerError) {
//                        message = "The server could not be found. Please try again after some time!!";
//                        callback.onSuccess(json_js, message);
//
//                    } else if (error instanceof AuthFailureError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                        callback.onSuccess(json_js, message);
//
//                    } else if (error instanceof ParseError) {
//                        message = "Parsing error! Please try again after some time!!";
//                        callback.onSuccess(json_js, message);
//
//                    } else if (error instanceof NoConnectionError) {
//                        message = "Cannot connect to Internet...Please check your connection!";
//                        callback.onSuccess(json_js, message);
//
//                    } else if (error instanceof TimeoutError) {
//                        message = "Connection TimeOut! Please check your internet connection.";
//                        callback.onSuccess(json_js, message);
//
//                    }
//                    else {
//                        callback.onSuccess(json_js, error.toString());
//                    }
//                }
//
//            }
//
//            private void displayMessage(String json) {
//
//                Toast.makeText(ctxt, json, Toast.LENGTH_LONG).show();
//
////                SharedPreferences mPrefs = ctxt.getSharedPreferences("USER_DATA", Context.MODE_PRIVATE);
////                SharedPreferences.Editor mEditor = mPrefs.edit();
////                mEditor.putString("USER_EMAIL", "");
////                mEditor.putString("USER_PASSWORD", "");
////                mEditor.putString("USER_TOKEN", "");
////                mEditor.apply();
//
//
//
////                (() ctxt).finish();
////                Intent in=new Intent(((FirstActivity) ctxt),FirstActivity.class);
////                ((FirstActivity) ctxt).startActivity(in);
//
//
//            }
//
//            private String trimMessage(String json, String message) {
//                String trimmedString = null;
//
//                try{
//                    JSONObject obj = new JSONObject(json);
//                    trimmedString = obj.getString(message);
//                } catch(JSONException e){
//                    e.printStackTrace();
//                    return null;
//                }
//
//                return trimmedString;
//            }
//        }) {
//
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return headers;
//            }
//
//        };
//
//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
//                MY_SOCKET_TIMEOUT_MS,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        queue.add(jsonObjReq);
//
//    }
}
