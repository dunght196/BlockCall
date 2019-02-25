package com.example.blockcall.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SettingSynFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference swSynContact;
    private List<ContactObj> listBlack = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String account = "dunght";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_account);
        swSynContact = (SwitchPreference) getPreferenceScreen().findPreference("key_syn");
        swSynContact.setOnPreferenceChangeListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference(account);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == swSynContact) {
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
                AppUtil.setAccount(getActivity(),account);
                AppUtil.setEnableSyn(getActivity(),isCheck);
            }else {
                AppUtil.setEnableSyn(getActivity(),isCheck);
            }

        }
        return true;
    }
}
