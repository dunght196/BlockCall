package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.adapter.BlacklistAdapter;
import com.example.blockcall.adapter.BlockcallAdapter;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.Constant;
import com.example.blockcall.utils.DialogUtil;

import java.util.ArrayList;
import java.util.List;


public class BlockcallTab extends ListFragment {

    private List<ContactObj> listBlock = new ArrayList<>();
    private BlockcallAdapter blockcallAdapter;
    private IntentFilter mIntentFilter;
    private int positionSeleceted = -1;

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
        bindView(rootView);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constant.mBroadcastAction);

        modeCallback = new ActionModeCallback();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(mLayoutManager);
        blockcallAdapter = new BlockcallAdapter(listBlock, getContext());
        rvList.setAdapter(blockcallAdapter);
        blockcallAdapter.setOnItemClickListener(new BlacklistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                positionSeleceted = position;
                enableActionMode(itemView,position);
            }
        });

        fab.setImageResource(R.drawable.ic_delete);
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
        getActivity().registerReceiver(mReceiver, mIntentFilter);
        listBlock.clear();
        listBlock.addAll(BlockcallData.Instance(getContext()).getAllBlockCall());
        blockcallAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void bindView(View view) {
        super.bindView(view);
    }

    @Override
    public void handleActionDelete(final ActionMode mode) {
        final DialogUtil dialogUtil = new DialogUtil(getContext(), R.layout.dialog_delete, listBlock.get(positionSeleceted));
        dialogUtil.action(new DialogUtil.DialogListener() {
            @Override
            public void onClickDone() {
                for(Integer value : blockcallAdapter.getPositionItem()) {
                    BlockcallData.Instance(getActivity()).delete(listBlock.get(value));
                    listBlock.remove(value);
                }
                dialogUtil.cancel();
                mode.finish();
            }

            @Override
            public void onClickCancel() {
                dialogUtil.cancel();
                mode.finish();
            }
        });
        dialogUtil.show();
    }

    @Override
    public void handleActionEdit(final ActionMode mode) {
        final DialogUtil dialogUtil = new DialogUtil(getContext(), R.layout.dialog_edit, listBlock.get(positionSeleceted));
        dialogUtil.action(new DialogUtil.DialogListener() {
            @Override
            public void onClickDone() {
                ContactObj contactObj = listBlock.get(positionSeleceted);
                contactObj.setUserName(dialogUtil.edtName.getText().toString());
                contactObj.setPhoneNum(dialogUtil.edtPhone.getText().toString());
                BlockcallData.Instance(getContext()).update(contactObj);
                dialogUtil.cancel();
                mode.finish();
            }

            @Override
            public void onClickCancel() {
                dialogUtil.cancel();
                mode.finish();
            }
        });
        dialogUtil.show();
    }

    @Override
    public void handleActionDestroy() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(BlockcallTab.this).attach(BlockcallTab.this).commit();
        super.handleActionDestroy();
    }

    @Override
    public void enableActionMode(View itemView, int position) {
        super.enableActionMode(itemView, position);
        toggleSelection(itemView,position);
    }

    public void toggleSelection(View itemView, int position) {
        blockcallAdapter.toggleSelection(itemView,position);
        int count = blockcallAdapter.getSelectedItemCount();
        if(count == 0) {
            itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
            mActionMode.finish();
            mActionMode = null;
        }else {
            if(count > 1) {
                MenuItem menuItem = mActionMode.getMenu().findItem(R.id.action_edit);
                menuItem.setVisible(false);
            }
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

}
