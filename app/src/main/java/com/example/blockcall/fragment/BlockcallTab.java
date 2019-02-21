package com.example.blockcall.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.example.blockcall.R;
import com.example.blockcall.adapter.BlacklistAdapter;
import com.example.blockcall.adapter.BlockcallAdapter;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.Constant;

import java.util.ArrayList;
import java.util.List;


public class BlockcallTab extends Fragment {

    FloatingActionButton fab;
    RecyclerView rvBlockcall;
    List<ContactObj> listBlock = new ArrayList<>();
    BlockcallAdapter blockcallAdapter;
    IntentFilter mIntentFilter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.mBroadcastAction)) {
                listBlock.clear();
                listBlock.addAll( BlockcallData.Instance(context).getAllBlockCall());
                blockcallAdapter.notifyDataSetChanged();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blockcall, container, false);

        fab = rootView.findViewById(R.id.fab_blockcall);
        rvBlockcall = rootView.findViewById(R.id.rv_blockcall);


        listBlock.addAll( BlockcallData.Instance(getContext()).getAllBlockCall());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvBlockcall.setLayoutManager(mLayoutManager);
        blockcallAdapter = new BlockcallAdapter(listBlock, getContext());
        rvBlockcall.setAdapter(blockcallAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlockcallData.Instance(getContext()).deleteAll();
                listBlock.clear();
                blockcallAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.mBroadcastAction);
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }

}
