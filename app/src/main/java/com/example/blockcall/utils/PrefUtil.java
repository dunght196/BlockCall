package com.example.blockcall.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;
import static com.example.blockcall.utils.Constant.APP_PREF_NAME;

public class PrefUtil {

    public static void setBoolean(Context context, String name,boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREF_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.apply();
    }
    public static boolean getBoolean(Context context, String name,boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(APP_PREF_NAME, MODE_PRIVATE);
        return sp.getBoolean(name, defaultValue);
    }


}
