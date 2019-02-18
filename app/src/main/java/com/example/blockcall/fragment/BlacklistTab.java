package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.blockcall.controller.RecyclerClick_Listener;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;

import java.util.List;

public class BlacklistTab extends Fragment implements RecyclerClick_Listener {

    FloatingActionButton fab;
    RecyclerView rvBlacklist;
    List<ContactObj> listBlack;
    BlacklistAdapter blacklistAdapter;

    private ActionMode actionMode;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);

        fab = rootView.findViewById(R.id.fab_blacklist);
        rvBlacklist = rootView.findViewById(R.id.rv_blacklist);
        listBlack = BlacklistData.Instance(getContext()).getAllBlacklist();
        if(listBlack.size() > 0) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvBlacklist.setLayoutManager(mLayoutManager);
            blacklistAdapter = new BlacklistAdapter(listBlack, getContext(), this);
            rvBlacklist.setAdapter(blacklistAdapter);
        }




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
                        final EditText edtName = (EditText)dialog1.findViewById(R.id.edt_name);
                        final EditText edtPhone = (EditText)dialog1.findViewById(R.id.edt_phone);
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

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int position) {
        if (actionMode == null) {
            actionMode = getActivity().startActionMode(modeCallBack);
        }
    }
}
