package com.example.blockcall.fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.activity.MainActivity;
import com.example.blockcall.activity.SettingsActivity;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.service.BlockCallService;
import com.example.blockcall.utils.AppUtil;
import com.example.blockcall.utils.Constant;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference swBlockcall, swSynchornize;
    List<ContactObj> listBlack;
    DatabaseReference mDatabase;
    IntentFilter mIntentFilter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        swBlockcall = (SwitchPreference) getPreferenceScreen().findPreference("key_block");
        swSynchornize = (SwitchPreference) getPreferenceScreen().findPreference("key_syn");
        swBlockcall.setOnPreferenceChangeListener(this);
        swSynchornize.setOnPreferenceChangeListener(this);

    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == swBlockcall) {
            boolean enableValue =  AppUtil.isEnableBlock(getActivity());
            AppUtil.setEnableBlock(getActivity(),!enableValue);
            AppUtil.enableService(getActivity(),!enableValue);
        }else if(preference == swSynchornize) {
            listBlack = BlacklistData.Instance(getActivity()).getAllBlacklist();
            Boolean isCheck = (Boolean)newValue;
            if (isCheck) {
                mDatabase = FirebaseDatabase.getInstance().getReference("contacts");
                String contactID = mDatabase.push().getKey();
                for(ContactObj c : listBlack) {
                    mDatabase.child(contactID).setValue(c);
                }
                AppUtil.setEnableSyn(getActivity(), isCheck);
            }else {
                AppUtil.setEnableSyn(getActivity(), isCheck);
            }

        }
        return true;
    }
}
