/*
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.schopfen.Booth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.schopfen.Booth.DataClasses.BaseClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple class to get suggestions from a JSONArray (represented as a file on disk), which can then
 * be mentioned by the user by tapping on the suggestion.
 */
public abstract class MentionsLoader<T extends Mentionable> {
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    protected T[] mData;
    private static final String TAG = MentionsLoader.class.getSimpleName();
    String GroupID, userId;

    public MentionsLoader(Context context) {
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        GroupID = sharedpreferences.getString("GroupID", "");
        userId = sharedpreferences.getString("UserID", "");
//        mentionPeople.clear();
        BaseClass baseClass4 = new BaseClass(context);
        RequestQueue queue4 = Volley.newRequestQueue(context);
        String url4 = baseClass4 + GroupID + "&LoggedinUserID=" + userId;
        Log.e("111", "" + url4);

        JsonObjectRequest jsonObjectRequest4 = new JsonObjectRequest(Request.Method.GET, url4,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        hobbyGroupsDataList.clear();
                        Log.e("Responseeeeeeee", response.toString());
                        try {
                            JSONObject mainObject = new JSONObject(response.toString());
                            boolean status = mainObject.getBoolean("status");

                            if (status) {


                                JSONArray jsonArray = mainObject.getJSONArray("group_members");
                                JSONArray jsonArray1 = new JSONArray();

                                for (int a = 0; a < jsonArray.length(); a++) {

                                    JSONObject jsonObject = jsonArray.getJSONObject(a);
                                    String UserID = jsonObject.getString("UserID");

                                    if (!userId.equals(UserID)) {

                                        jsonArray1.put(jsonArray.get(a));
                                    }
                                }


                                mData = loadData(jsonArray1);
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
//                                    String FullName = jsonObject1.getString("FullName");
//                                    String UserID = jsonObject1.getString("UserID");
////                                    MentionPerson grupUsersData = new MentionPerson();
//                                    MentionPerson mentionPerson=new MentionPerson(FullName,UserID);
//                                    mentionPeople.add(mentionPerson);
//
//
//                                }

                            }
                        } catch (JSONException e) {
                            Log.e("Response", e.toString());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("Response", error.toString());
            }
        });
        int socketTimeout4 = 30000;
        RetryPolicy policy4 = new DefaultRetryPolicy(socketTimeout4, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest4.setRetryPolicy(policy4);
        queue4.add(jsonObjectRequest4);

    }

    public abstract T[] loadData(JSONArray arr);

    // Returns a subset
    public List<T> getSuggestions(QueryToken queryToken) {
        String prefix = queryToken.getKeywords().toLowerCase();
        List<T> suggestions = new ArrayList<>();
        if (mData != null) {
            for (T suggestion : mData) {
                String name = suggestion.getSuggestiblePrimaryText().toLowerCase();
                if (name.startsWith(prefix)) {
                    suggestions.add(suggestion);
                }
            }
        }
        return suggestions;
    }

    // Loads data from JSONArray file, defined in the raw resources folder
    private class LoadJSONArray extends AsyncTask<Void, Void, JSONArray> {

        private final WeakReference<Resources> mRes;
        private final int mResId;

        public LoadJSONArray(Resources res, int resId) {
            mRes = new WeakReference<>(res);
            mResId = resId;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            InputStream fileReader = mRes.get().openRawResource(mResId);
            Writer writer = new StringWriter();
            JSONArray arr = null;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileReader, "UTF-8"));
                String line = reader.readLine();
                while (line != null) {
                    writer.write(line);
                    line = reader.readLine();
                }
                String jsonString = writer.toString();
                arr = new JSONArray(jsonString);
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while reading JSON", e);
            } finally {
                try {
                    fileReader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Unhandled exception while closing JSON file", e);
                }
            }
            return arr;
        }

        @Override
        protected void onPostExecute(JSONArray arr) {
            super.onPostExecute(arr);
            mData = loadData(arr);
        }
    }
}
