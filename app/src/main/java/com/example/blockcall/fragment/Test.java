package com.example.blockcall.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;

public class Test extends Fragment {

    protected FloatingActionButton fab;
    protected RecyclerView rvTest;
    protected ActionModeCallback modeCallback;
    protected ActionMode mActionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void bindView(View view) {
        fab = view.findViewById(R.id.fab_test);
        rvTest = view.findViewById(R.id.rv_test);
    }


    protected class ActionModeCallback implements ActionMode.Callback {
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
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    return true;
                case R.id.action_edit:
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

    public void enableActionMode(View itemView, int position) {
        if(mActionMode == null) {
            mActionMode = getActivity().startActionMode(modeCallback);
            itemView.setSelected(true);
        }
//        toggleSelection(itemView,position);
    }

//    public void toggleSelection(View itemView, int position) {
//        baseAdapter.toggleSelection(itemView,position);
//        int count = baseAdapter.getSelectedItemCount();
//        if(count == 0) {
//            itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
//            mActionMode.finish();
//            mActionMode = null;
//        }else {
//            if(count > 1) {
//                MenuItem menuItem = mActionMode.getMenu().findItem(R.id.action_edit);
//                menuItem.setVisible(false);
//            }
//            mActionMode.setTitle(String.valueOf(count));
//            mActionMode.invalidate();
//        }
//    }
}
