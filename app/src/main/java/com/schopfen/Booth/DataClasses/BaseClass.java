package com.schopfen.Booth.DataClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class BaseClass {

    public Context mContext;

    public BaseClass(Context context) {
        mContext = context;
    }


    public static String ImageURL = "";
    public static String BaseURL = "https://baac.booth-in.com/api/";


    public static final String GenerateToken = BaseURL+"generateTokenByApi";
    public static final String Login = BaseURL+"login";



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

    public static String encode(String s)
    {
        return StringEscapeUtils.escapeJava(s);

    }

    public static String decode(String s)
    {
        return StringEscapeUtils.unescapeJava(s);

    }
    //Functions
    public static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" +
                    "(" +
                    "." +
                    "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" +
                    ")+"
    );

    public static boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static String TimeStampToTime(String timestmp) {
        String localTime = "";
        try {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone(); /* debug: is it local time? */
            Log.d("Time zone: ", tz.getDisplayName()); /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(tz); /* print your timestamp and double check it's the date you expect */
            long timestamp = Long.parseLong(timestmp);
            Log.e("checkingtime", timestmp);
            localTime = sdf.format(new Date(timestamp * 1000));

            return localTime;
        }catch (NumberFormatException e){
            e.printStackTrace();
            return localTime;
        }
    }

    public static String TimeStampToDate(String timestmp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone(); /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName()); /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MMM.YYYY");
        sdf.setTimeZone(tz); /* print your timestamp and double check it's the date you expect */
        long timestamp = Long.parseLong(timestmp);
        Log.e("checkingtime", timestmp);
        String localTime = sdf.format(new Date(timestamp * 1000));

        return localTime;
    }
}
