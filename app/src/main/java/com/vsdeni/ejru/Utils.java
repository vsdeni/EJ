package com.vsdeni.ejru;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Denis on 18.10.2014.
 */
public class Utils {
    public static int pixelsToSp(float px, Context context) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / scaledDensity);
    }

    public static class Prefs {
        public static final String FONT_SIZE = "font_size";

        public static String getString(String key, String defValue, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(key, defValue);
        }

        public static void saveString(String key, String value, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(key, value);
            editor.apply();
        }

        public static void saveInt(String key, int value, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(key, value);
            editor.apply();
        }

        public static Integer getInt(String key, Integer defValue, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getInt(key, defValue);
        }

        public static void saveLong(String key, long value, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(key, value);
            editor.apply();
        }

        public static Long getLong(String key, Long defValue, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getLong(key, defValue);
        }

        public static void saveBoolean(String key, boolean value, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }

        public static boolean getBoolean(String key, boolean defValue, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getBoolean(key, defValue);
        }

        public static void saveFloat(String key, float value, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat(key, value);
            editor.apply();
        }

        public static Float getFloat(String key, Float defValue, Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getFloat(key, defValue);
        }
    }
}
