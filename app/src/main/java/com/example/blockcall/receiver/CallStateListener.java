package com.example.blockcall.receiver;

import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.example.blockcall.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CallStateListener extends PhoneStateListener {

    private Context mContext;

    public CallStateListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // called when someone is ringing to this phone
                if(AppUtil.isEnableBlock(mContext) && isBlacknumber(incomingNumber)){
                    AppUtil.endCall(mContext);
                    // Insert db Blockcall object
                    ContactObj contactObj = new ContactObj();
                    contactObj.setPhoneNum(incomingNumber);
                    contactObj.setUserName(getUsername(incomingNumber));
                    contactObj.setDateBlock(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
                    contactObj.setTimeBlock(new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));
                    BlockcallData.Instance(mContext).add(contactObj);
                    //Send action broadcast
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(Constant.mBroadcastAction);
                    mContext.sendBroadcast(broadcastIntent);
                }
                break;
        }
    }

    private Boolean isBlacknumber(String number) {
        ArrayList<ContactObj> listBlack = BlacklistData.Instance(mContext).getAllBlacklist();
        for(ContactObj c : listBlack) {
            if (c.getPhoneNum().equals(number)) {
                return true;
            }
        }
        return false;
    }

    private String getUsername(String number) {
        String s = "";
        ArrayList<ContactObj> listBlack = BlacklistData.Instance(mContext).getAllBlacklist();
        for(ContactObj c : listBlack) {
            if (c.getPhoneNum().equals(number)) {
                return c.getUserName();
            }
        }
        return s;
    }
}
