package com.example.blockcall.fragment;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.activity.SettingSynActivity;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private SwitchPreference swBlockcall, swSynchornize;
    private Preference prefAccount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        addPreferencesFromResource(R.xml.pref_main);
        swBlockcall = (SwitchPreference) getPreferenceScreen().findPreference("key_block");
        prefAccount = (Preference)getPreferenceScreen().findPreference("account");
        swBlockcall.setOnPreferenceChangeListener(this);
        prefAccount.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == swBlockcall) {
            boolean enableValue =  AppUtil.isEnableBlock(getActivity());
            AppUtil.setEnableBlock(getActivity(),!enableValue);
            AppUtil.enableService(getActivity(),!enableValue);
        }
        return true;
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Register permission
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE}, 1);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == prefAccount) {
            AppUtil.setAccount(getActivity(), prefAccount.getTitle().toString());
            startActivity(new Intent(getActivity(), SettingSynActivity.class));
        }
        return true;
    }
}
