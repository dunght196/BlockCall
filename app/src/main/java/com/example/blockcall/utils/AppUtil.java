package com.example.blockcall.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class AppUtil {

    public static void setEnableBlock(Context context, boolean value){
        PrefUtil.setBoolean(context,Constant.ENABLE_KEY_BLOCK,value);
    }

    public static void setEnableSyn(Context context, boolean value){
        PrefUtil.setBoolean(context,Constant.ENABLE_KEY_SYN,value);
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
}
