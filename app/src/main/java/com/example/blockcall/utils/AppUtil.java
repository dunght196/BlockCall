package com.example.blockcall.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class AppUtil {

    public static void setEnable(Context context, boolean value){
        PrefUtil.setBoolean(context,Constant.ENABLE_KEY,value);
    }

    public static boolean isEnable(Context context){
        return PrefUtil.getBoolean(context,Constant.ENABLE_KEY,false);
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
