package com.example.blockcall.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.example.blockcall.receiver.CallStateListener;
import com.example.blockcall.R;
import com.example.blockcall.activity.MainActivity;

public class BlockCallService extends Service {

    private CallStateListener mCallStateListener;
    private TelephonyManager mTelephonyManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        mCallStateListener  = new CallStateListener (this);
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(mCallStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( mCallStateListener != null && mTelephonyManager != null) {
            mTelephonyManager.listen(mCallStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }


    private void createNotification() {
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("10001", getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
            notificationChannel.setSound(null, null);
            notificationChannel.setShowBadge(false);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder mBuilder = new  NotificationCompat.Builder(this,"10001");
        mBuilder.setSmallIcon(R.drawable.ic_block);
        mBuilder.setContentTitle("Call blocking is on")
                .setAutoCancel(false)
                .setOngoing(true)
                .setChannelId("10001")
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(resultPendingIntent);
        //mNotificationManager.notify(NOTIFICATION_ID , mBuilder.build())
        startForeground(1, mBuilder.build());
    }
}
