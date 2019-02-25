package com.example.blockcall.fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import com.example.blockcall.R;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference swBlockcall, swSynchornize;
    private List<ContactObj> listBlack = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String account;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        addPreferencesFromResource(R.xml.pref_main);
        swBlockcall = (SwitchPreference) getPreferenceScreen().findPreference("key_block");
        swSynchornize = (SwitchPreference) getPreferenceScreen().findPreference("key_syn");
        swBlockcall.setOnPreferenceChangeListener(this);
        swSynchornize.setOnPreferenceChangeListener(this);

        account = AppUtil.getAccount(getActivity(),"");
        mDatabase = FirebaseDatabase.getInstance().getReference(account);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == swBlockcall) {
            boolean enableValue =  AppUtil.isEnableBlock(getActivity());
            AppUtil.setEnableBlock(getActivity(),!enableValue);
            AppUtil.enableService(getActivity(),!enableValue);
        }else if(preference == swSynchornize) {
            listBlack.clear();
            listBlack.addAll(BlacklistData.Instance(getActivity()).getAllBlacklist());
            Boolean isCheck = (Boolean)newValue;
            if (isCheck) {
                if(!account.equals("")) {
                    for(ContactObj c : listBlack) {
                        String contactID = mDatabase.push().getKey();
                        mDatabase.child(contactID).setValue(c);
                    }
                }
                AppUtil.setEnableSyn(getActivity(),isCheck);
            }else {
                AppUtil.setEnableSyn(getActivity(),isCheck);
            }

        }
        return true;
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Register permission
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE}, 1);
        }
    }
}
