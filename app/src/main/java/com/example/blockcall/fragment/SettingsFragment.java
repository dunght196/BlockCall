package com.example.blockcall.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.example.blockcall.R;
import com.example.blockcall.service.BlockCallService;
import com.example.blockcall.utils.AppUtil;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private SwitchPreference swBlockcall, swSynchornize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        swBlockcall = (SwitchPreference) getPreferenceScreen().findPreference("key_block");
        swSynchornize = (SwitchPreference) getPreferenceScreen().findPreference("key_syn");
        swBlockcall.setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean enableValue = AppUtil.isEnable(getActivity());
        if(swBlockcall != null){
            swBlockcall.setChecked(enableValue);
        }
        enableService(enableValue);
    }

    private void enableService(boolean enable){
        if (enable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getActivity().startForegroundService(new Intent(getActivity(), BlockCallService.class));
            } else {
                getActivity().startService(new Intent(getActivity(), BlockCallService.class));
            }
        } else {
            getActivity().stopService(new Intent(getActivity(), BlockCallService.class));
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if(preference == swBlockcall) {
            boolean enableValue =  AppUtil.isEnable(getActivity());
            AppUtil.setEnable(getActivity(),!enableValue);
            enableService(!enableValue);
        }
        return true;
    }
}
