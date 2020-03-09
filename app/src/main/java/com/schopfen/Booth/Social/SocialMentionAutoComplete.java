package com.schopfen.Booth.Social;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.content.res.ResourcesCompat;
import androidx.collection.ArrayMap;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.MultiAutoCompleteTextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.schopfen.Booth.DataClasses.BaseClass;
import com.schopfen.Booth.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ameen on 15/1/18.
 * Happy Coding
 */

public class SocialMentionAutoComplete extends AppCompatMultiAutoCompleteTextView {

    MentionAutoCompleteAdapter mentionAutoCompleteAdapter;
    ArrayMap<String, MentionPerson> map = new ArrayMap<>();
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor mEditor;
    public static final String MyPREFERENCES = "MyPrefs";
    String formattedOfString = "@%s ";
    ArrayList<MentionPerson> mentionPeople = new ArrayList<>();
    String GroupID,userId;



    public SocialMentionAutoComplete(Context context,String userIdd,String groupId) {
        super(context);
        initializeComponents();
//        this.userId=userId;
//        this.GroupID=groupId;


    }

    public SocialMentionAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeComponents();
    }

    public SocialMentionAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeComponents();
    }

    private void initializeComponents() {
        addTextChangedListener(textWatcher);
        setOnItemClickListener(onItemSelectedListener);
        setTokenizer(new SpaceTokenizer());
    }

    AdapterView.OnItemClickListener onItemSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            MentionPerson mentionPerson = (MentionPerson) adapterView.getItemAtPosition(i);
            map.put("@" + mentionPerson.getFullName(), mentionPerson);
        }
    };


    /***
     *This function returns the contents of the AppCompatMultiAutoCompleteTextView into my desired Format
     *You can write your own function according to your needs
     **/

    public String getProcessedString() {

        String s = getText().toString();

        for (Map.Entry<String, MentionPerson> stringMentionPersonEntry : map.entrySet()) {
            s = s.replace(stringMentionPersonEntry.getKey(), stringMentionPersonEntry.getValue().getFullName());
        }
        return s;
    }

    /**
     * This function will process the incoming text into mention format
     * You have to implement the processing logic
     */
    public void setMentioningText(String text) {

        map.clear();

        Pattern p = Pattern.compile("\\[([^]]+)]\\(([^ )]+)\\)");
        Matcher m = p.matcher(text);

        String finalDesc = text;

        while (m.find()) {
            MentionPerson mentionPerson = new MentionPerson();
            String name = m.group(1);
            String id = m.group(2);
            //Processing Logic
            finalDesc = finalDesc.replace("@[" + name + "](" + id + ")", "@" + name);

            mentionPerson.setFullName(name);
            mentionPerson.setUserID(id);
            map.put("@" + name, mentionPerson);
        }
        int textColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
        Spannable spannable = new SpannableString(finalDesc);
        for (Map.Entry<String, MentionPerson> stringMentionPersonEntry : map.entrySet()) {
            int startIndex = finalDesc.indexOf(stringMentionPersonEntry.getKey());
            int endIndex = startIndex + stringMentionPersonEntry.getKey().length();
            spannable.setSpan(new ForegroundColorSpan(textColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannable);
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int lengthBefore, int lengthAfter) {

            if (!s.toString().isEmpty() && start < s.length()) {

                String name = s.toString();

                int lastTokenIndex = name.lastIndexOf(" @");
                int lastIndexOfSpace = name.lastIndexOf(" ");
                int nextIndexOfSpace = name.indexOf(" ", start);

                if (lastIndexOfSpace > 0 && lastTokenIndex < lastIndexOfSpace) {
                    String afterString = s.toString().substring(lastIndexOfSpace, s.length());
                    if (afterString.startsWith(" ")) return;
                }

                if (lastTokenIndex < 0) {
                    if (!name.isEmpty() && name.length() >= 1 && name.startsWith("@")) {
                        lastTokenIndex = 1;
                    } else
                        return;
                }

                int tokenEnd = lastIndexOfSpace;

                if (lastIndexOfSpace <= lastTokenIndex) {
                    tokenEnd = name.length();
                    if (nextIndexOfSpace != -1 && nextIndexOfSpace < tokenEnd) {
                        tokenEnd = nextIndexOfSpace;
                    }
                }

                if (lastTokenIndex >= 0) {
                    name = s.toString().substring(lastTokenIndex, tokenEnd).trim();
                    Pattern pattern = Pattern.compile("^(.+)\\s.+");
                    Matcher matcher = pattern.matcher(name);
                    if (!matcher.find()) {
                        name = name.replace("@", "").trim();
                        if (!name.isEmpty()) {
                            getUsers(name);
                        }
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    /*
     *This function returns results from the web server according to the user name
     * I have used Retrofit for Api Communications
     * */
    public void getUsers(String name) {
        sharedpreferences = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mEditor = sharedpreferences.edit();
        GroupID = sharedpreferences.getString("GroupID", "");
        userId = sharedpreferences.getString("UserID", "");
//        mentionPeople.clear();
        BaseClass baseClass4 = new BaseClass(getContext());
        RequestQueue queue4 = Volley.newRequestQueue(getContext());
        String url4 = baseClass4+ "getGroupMembers" + GroupID + "&LoggedinUserID=" +userId;
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
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    String FullName = jsonObject1.getString("FullName");
                                    String UserID = jsonObject1.getString("UserID");
//                                    MentionPerson grupUsersData = new MentionPerson();
                                    MentionPerson mentionPerson=new MentionPerson(FullName,UserID);
                                    mentionPeople.add(mentionPerson);


                                }
                                mentionAutoCompleteAdapter =
                                        new MentionAutoCompleteAdapter(getContext(), mentionPeople);
                                SocialMentionAutoComplete.this.setAdapter(mentionAutoCompleteAdapter);
                                SocialMentionAutoComplete.this.showDropDown();

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


    public class SpaceTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        public int findTokenStart(CharSequence text, int cursor) {

            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }
            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {

            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {

            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                // Returns colored text for selected token
                SpannableString sp = new SpannableString(String.format(formattedOfString, text));
                int textColor = ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null);
                sp.setSpan(new ForegroundColorSpan(textColor), 0, text.length() + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sp;
            }
        }
    }
}