package com.example.blockcall.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.example.blockcall.CallStateListener;

public class CallDetectService extends Service {

    CallStateListener mCallStateListener;
    TelephonyManager tm;

    public CallDetectService() {
    }

    @Override
    public void onCreate() {
        mCallStateListener  = new CallStateListener (this);
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mCallStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        tm.listen(mCallStateListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }
}
