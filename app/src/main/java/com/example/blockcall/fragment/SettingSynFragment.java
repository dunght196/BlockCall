package com.example.blockcall.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SettingSynFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private SwitchPreference swSynContact;
    private List<ContactObj> listBlack = new ArrayList<>();
    private List<ContactObj> listSyn = new ArrayList<>();
    private DatabaseReference mDatabase;
    private String account = "dunght";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_account);
        swSynContact = (SwitchPreference) getPreferenceScreen().findPreference("key_syn");
        swSynContact.setOnPreferenceChangeListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference(account);

//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                    ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
//                    Log.e("contact","=" + contactObj.getUserName());
//                    listSyn.add(contactObj);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
                    listSyn.add(contactObj);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == swSynContact) {
            listBlack.clear();
            listBlack.addAll(BlacklistData.Instance(getActivity()).getAllBlacklist());
            Boolean isCheck = (Boolean) newValue;
            if (isCheck) {
                Log.e("list","=" + listSyn.size());
                if (listSyn.isEmpty()) {
                    for (ContactObj c : listBlack) {
                        String contactID = mDatabase.push().getKey();
                        mDatabase.child(contactID).setValue(c);
                    }
                } else {
                    for(ContactObj c1 : listBlack) {
                        Boolean check = false;
                        for(ContactObj c2 : listSyn ) {
                            if(c2.getUserName().equals(c1.getUserName()) && c2.getPhoneNum().equals(c1.getPhoneNum())) {
                                check = false;
                                break;
                            }else {
                                check = true;
                            }
                        }
                        if (check) {
                            String contactID = mDatabase.push().getKey();
                            mDatabase.child(contactID).setValue(c1);
                        }
                    }
                }
                AppUtil.setAccount(getActivity(), account);
                AppUtil.setEnableSyn(getActivity(), isCheck);
            }else {
                AppUtil.setEnableSyn(getActivity(), isCheck);
            }
        }
        return true;
    }
}
