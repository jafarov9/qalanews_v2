package com.hajma.qalanews_android;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static SharedPreferences sp;


    public static String getTimeAgo(long time, SharedPreferences sp) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();

        String lang = sp.getString("selected_language", "az");

        Log.e("novtime", ""+now);
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            if(lang.equals("az")) {
                return "indi";
            }else if(lang.equals("ru")) {
                return "сейчас";
            }else {
                return "just now";
            }
        } else if (diff < 2 * MINUTE_MILLIS) {
            if(lang.equals("az")) {
                return "bir dəqiqə əvvəl";
            }else if(lang.equals("ru")) {
                return "минуту назад";
            }else {
                return "minute ago";
            }
        } else if (diff < 50 * MINUTE_MILLIS) {
            if(lang.equals("az")) {
                return diff / MINUTE_MILLIS + " dəq əvvəl";
            } else if(lang.equals("ru")) {
                return diff / MINUTE_MILLIS + " минуту назад";
            }else {
                return diff / MINUTE_MILLIS + " minute ago";
            }

        } else if (diff < 90 * MINUTE_MILLIS) {
            if(lang.equals("az")) {
                return "bir saat əvvəl";
            }else if(lang.equals("ru")) {
                return "час назад";
            }else {
                return "an hour ago";
            }
        } else if (diff < 24 * HOUR_MILLIS) {
            if(lang.equals("az")) {
                return diff / HOUR_MILLIS + " saat əvvəl";
            } else if(lang.equals("ru")) {
                return diff / HOUR_MILLIS + " час назад";
            }else {
                return diff / HOUR_MILLIS + " hours ago";
            }
        } else if (diff < 48 * HOUR_MILLIS) {
            if(lang.equals("az")) {
                return "dünən";
            } else if(lang.equals("ru")) {
                return "вчера";
            }else {
                return "yesterday";
            }
        } else {
            if(lang.equals("az")) {
                return diff / DAY_MILLIS + " gün əvvəl";
            } else if(lang.equals("ru")) {
                return diff / DAY_MILLIS + " дней назад";
            }else {
                return diff / DAY_MILLIS + " day ago";
            }
        }
    }

    public static long parseDate(String text) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            return dateFormat.parse(text).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
