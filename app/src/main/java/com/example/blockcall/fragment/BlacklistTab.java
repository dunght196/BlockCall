package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.activity.ContactActivity;
import com.example.blockcall.activity.MainActivity;
import com.example.blockcall.adapter.BlacklistAdapter;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.example.blockcall.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlacklistTab extends Fragment {

    FloatingActionButton fab;
    RecyclerView rvBlacklist;
    List<ContactObj> listBlack = new ArrayList<>();
    List<ContactObj> listSyn;
    BlacklistAdapter blacklistAdapter;
    ActionMode mActionMode;
    int pos = -1;
    DatabaseReference mDatabase;
    IntentFilter mIntentFilter;

    ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    BlacklistData.Instance(getContext()).delete(listBlack.get(pos));
                    listBlack.remove(pos);
                    blacklistAdapter.notifyDataSetChanged();
                    mActionMode.finish();
                    return true;
                case R.id.action_edit:
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_edit_item_black);

                    final EditText edtName = (EditText) dialog.findViewById(R.id.edt_edit_name);
                    final EditText edtPhone = (EditText) dialog.findViewById(R.id.edt_edit_phone);
                    TextView tvOK = (TextView) dialog.findViewById(R.id.tv_edit_ok);
                    TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_edit_cancel);
                    edtName.setText(listBlack.get(pos).getUserName());
                    edtPhone.setText(listBlack.get(pos).getPhoneNum());
                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ContactObj contactObj = listBlack.get(pos);
                            contactObj.setUserName(edtName.getText().toString());
                            contactObj.setPhoneNum(edtPhone.getText().toString());
                            BlacklistData.Instance(getContext()).update(contactObj);
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    });
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                            mActionMode.finish();
                        }
                    });
                    dialog.show();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);

        fab = rootView.findViewById(R.id.fab_blacklist);
        rvBlacklist = rootView.findViewById(R.id.rv_blacklist);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvBlacklist.setLayoutManager(mLayoutManager);
        blacklistAdapter = new BlacklistAdapter(listBlack, getContext());
        rvBlacklist.setAdapter(blacklistAdapter);

        blacklistAdapter.setOnItemClickListener(new BlacklistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                pos = position;
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = getActivity().startActionMode(modeCallBack);
                itemView.setSelected(true);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_add_blacklist);
                LinearLayout llFromContact = (LinearLayout) dialog.findViewById(R.id.ll_from_contact);
                LinearLayout llFromNumber = (LinearLayout) dialog.findViewById(R.id.ll_from_number);
                dialog.show();

                llFromContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ContactActivity.class));
                        dialog.cancel();
                    }
                });

                llFromNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog1 = new Dialog(getActivity());
                        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog1.setContentView(R.layout.dialog_input_number);
                        final EditText edtName = (EditText) dialog1.findViewById(R.id.edt_name);
                        final EditText edtPhone = (EditText) dialog1.findViewById(R.id.edt_phone);
                        TextView tvOK = (TextView) dialog1.findViewById(R.id.tv_ok);
                        TextView tvCancel = (TextView) dialog1.findViewById(R.id.tv_cancel);
                        dialog1.show();
                        dialog.cancel();

                        tvOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ContactObj contactObj = new ContactObj();
                                contactObj.setUserName(edtName.getText().toString());
                                contactObj.setPhoneNum(edtPhone.getText().toString());
                                BlacklistData.Instance(getContext()).add(contactObj);
                                startActivity(new Intent(getActivity(), MainActivity.class));
                            }
                        });

                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog1.cancel();
                            }
                        });


                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.mBroadcastAction);

        boolean enableValue = AppUtil.isEnableBlock(getActivity());
        AppUtil.enableService(getActivity(),enableValue);
        mDatabase = FirebaseDatabase.getInstance().getReference("contacts");
        boolean checkSw = AppUtil.isEnableSyn(getContext());
        if (checkSw) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listBlack.clear();
                    BlacklistData.Instance(getContext()).deleteAll();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
                        listBlack.add(contactObj);
                        // save db
                        BlacklistData.Instance(getContext()).add(contactObj);
                    }

                    blacklistAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });

        } else {
            listBlack.clear();
            listBlack.addAll(BlacklistData.Instance(getContext()).getAllBlacklist());
            blacklistAdapter.notifyDataSetChanged();
        }
    }
}
