package com.example.blockcall.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.internal.telephony.ITelephony;
import com.example.blockcall.service.BlockCallService;

import java.lang.reflect.Method;

public class AppUtil {

    public static void setEnableBlock(Context context, boolean value){
        PrefUtil.setBoolean(context,Constant.ENABLE_KEY_BLOCK,value);
    }

    public static void setEnableSyn(Context context, boolean value){
        PrefUtil.setBoolean(context,Constant.ENABLE_KEY_SYN,value);
    }

    public static void setAccount(Context context, String value){
        PrefUtil.setString(context,Constant.ACCOUNT_SYN,value);
    }

    public static String getAccount(Context context, String defaultVlaue){
        return PrefUtil.getString(context,Constant.ACCOUNT_SYN,defaultVlaue);
    }

    public static boolean isEnableBlock(Context context){
        return PrefUtil.getBoolean(context,Constant.ENABLE_KEY_BLOCK,false);
    }

    public static boolean isEnableSyn(Context context){
        return PrefUtil.getBoolean(context,Constant.ENABLE_KEY_SYN,false);
    }


    public static void endCall(Context context) {
        try{
            TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            Class c = Class.forName(manager.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephony = (ITelephony)m.invoke(manager);
            telephony.endCall();
        } catch(Exception e){
            Log.d("AppUtil",e.getMessage());
        }
    }

    public static void enableService(Context context, boolean enable){
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, BlockCallService.class));
            } else {
                context.startService(new Intent(context, BlockCallService.class));
            }
        } else {
            context.stopService(new Intent(context, BlockCallService.class));
        }
    }
}
