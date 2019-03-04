package com.example.blockcall.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.blockcall.R;

public class ListFragment extends Fragment {

    protected FloatingActionButton fab;
    protected RecyclerView rvList;
    protected ActionModeCallback modeCallback;
    protected ActionMode mActionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        modeCallback = new ActionModeCallback();
        return rootView;
    }

    protected void bindView(View view) {
        fab = view.findViewById(R.id.fab_list);
        rvList = view.findViewById(R.id.rv_list);
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
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    handleActionDelete(mode);
                    return true;
                case R.id.action_edit:
                    handleActionEdit(mode);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            handleActionDestroy();
        }
    };

    public void enableActionMode(View itemView, int position) {
        if(mActionMode == null) {
            mActionMode = getActivity().startActionMode(modeCallback);
            itemView.setSelected(true);
        }
    }

    public void handleActionDelete(ActionMode mode) {
    }

    public void handleActionEdit(ActionMode mode) {
    }

    public void handleActionDestroy() {
        mActionMode = null;
    }
}
