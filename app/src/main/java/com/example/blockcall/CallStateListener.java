package com.example.blockcall;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class CallStateListener extends PhoneStateListener {

    Context ctx;

    public CallStateListener(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // called when someone is ringing to this phone
                Toast.makeText(ctx, "Incoming: "+incomingNumber, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
