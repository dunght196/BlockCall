package com.example.blockcall.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

import com.example.blockcall.R;

public class SettingsFragment extends PreferenceFragment {

    private SwitchPreference blockcall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_main);
        blockcall = (SwitchPreference) getPreferenceScreen().findPreference("key_block");
    }

}
