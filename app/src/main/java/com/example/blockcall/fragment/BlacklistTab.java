package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.activity.ContactActivity;
import com.example.blockcall.adapter.BlacklistAdapter;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;

import java.util.List;

public class BlacklistTab extends Fragment {

    FloatingActionButton fab;
    RecyclerView rvBlacklist;
    List<ContactObj> listBlack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);

        fab = rootView.findViewById(R.id.fab_blacklist);
        rvBlacklist = rootView.findViewById(R.id.rv_blacklist);
        listBlack = BlacklistData.Instance(getContext()).getAllContact();
        if(listBlack.size() > 0) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rvBlacklist.setLayoutManager(mLayoutManager);
            BlacklistAdapter blacklistAdapter = new BlacklistAdapter(listBlack, getContext());
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
                        dialog1.show();
                        dialog.cancel();
                    }
                });
            }
        });

        return rootView;
    }
}
