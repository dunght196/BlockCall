package com.example.blockcall;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CallStateListener extends PhoneStateListener {

    Context mContext;

    public CallStateListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                // called when someone is ringing to this phone
                if(AppUtil.isEnable(mContext) && isBlacknumber(incomingNumber)){
                    AppUtil.endCall(mContext);

                    // Insert db Blockcall object
                    ContactObj contactObj = new ContactObj();
                    contactObj.setPhoneNum(incomingNumber);
                    contactObj.setUserName(getUsername(incomingNumber));
                    contactObj.setDateBlock(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));
                    contactObj.setTimeBlock(new SimpleDateFormat("hh:mm a").format(Calendar.getInstance().getTime()));
                    BlockcallData.Instance(mContext).add(contactObj);
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
