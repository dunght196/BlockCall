package com.example.blockcall.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import com.example.blockcall.R;
import com.example.blockcall.adapter.ContactAdapter;
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

public class ContactActivity extends AppCompatActivity {

    private RecyclerView rvContact;
    private List<ContactObj> listContact = new ArrayList<>();
    private List<ContactObj> listSyn = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private Toolbar toolbar;
    private SparseBooleanArray listIndex = new SparseBooleanArray();
    private ImageView ivBack, ivDone;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        rvContact = (RecyclerView) findViewById(R.id.rv_contact);
        ivBack = (ImageView)findViewById(R.id.iv_back);
        ivDone = (ImageView)findViewById(R.id.iv_done);
        mDatabase = FirebaseDatabase.getInstance().getReference(AppUtil.getAccount(this,""));
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listSyn.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
                    listSyn.add(contactObj);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvContact.setLayoutManager(mLayoutManager);
        contactAdapter = new ContactAdapter(listContact, ContactActivity.this);
        rvContact.setAdapter(contactAdapter);

        contactAdapter.setCheckboxClickListener(new ContactAdapter.OnCheckboxClickListener() {
            @Override
            public void onCheckClick(int position) {
                listIndex.put(position,true);
            }

            @Override
            public void onUncheckClick(int position) {
                listIndex.put(position,false);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ivDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get key form listIndex
                List<Integer> listItem = new ArrayList<>();
                for(int i=0; i<listIndex.size(); i++) {
                    listItem.add(listIndex.keyAt(i));
                }
                for(Integer i : listItem) {
                    if(listIndex.get(i)) {
                        if(AppUtil.isEnableSyn(ContactActivity.this)) {
                            String contactID = mDatabase.push().getKey();
                            mDatabase.child(contactID).setValue(listContact.get(i));
                        }else {
                            BlacklistData.Instance(ContactActivity.this).add(listContact.get(i));
                        }
                    }
                }
                startActivity(new Intent(ContactActivity.this, MainActivity.class));
                finishAffinity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        listContact.addAll(getListContacts());
        // Delete contact selected
        if(AppUtil.isEnableSyn(ContactActivity.this)) {
            for(ContactObj c1 : listSyn) {
                for(ContactObj c2 : listContact ) {
                    if(c2.getUserName().equals(c1.getUserName()) && c2.getPhoneNum().equals(c1.getPhoneNum())) {
                        listContact.remove(c2);
                        break;
                    }
                }
            }
        }else {
            for(ContactObj c1 : BlacklistData.Instance(this).getAllBlacklist()) {
                for(ContactObj c2 : listContact ) {
                    if(c2.getUserName().equals(c1.getUserName()) && c2.getPhoneNum().equals(c1.getPhoneNum())) {
                        listContact.remove(c2);
                        break;
                    }
                }
            }
        }

        contactAdapter.notifyDataSetChanged();
    }

    private List<ContactObj> getListContacts() {
        List<ContactObj> listData = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                ContactObj contactObj = new ContactObj();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contactObj.setUserName(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactObj.setPhoneNum(phoneNo);
                    }
                    pCur.close();
                }

                listData.add(contactObj);
            }
        }
        if(cur!=null){
            cur.close();
        }

        return listData;
    }

    public void initPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Register permission
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }
}
