package com.example.blockcall.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.activity.ContactActivity;
import com.example.blockcall.activity.MainActivity;
import com.example.blockcall.adapter.BlacklistAdapter;
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

public class BlacklistTab extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView rvBlacklist;
    private List<ContactObj> listBlack = new ArrayList<>();
    private BlacklistAdapter blacklistAdapter;
    private ActionMode mActionMode;
    private int positionSeleceted = -1;
    private DatabaseReference mDatabase;
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
                    for(Integer value : blacklistAdapter.getPositionItem()) {
                        BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                        listBlack.remove(value);
                    }
                    mode.finish();
                    blacklistAdapter.clearSelectedItems();
                    // Refresh fragment
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
                    return true;
                case R.id.action_edit:
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_edit_item_black);

                    final EditText edtName = (EditText) dialog.findViewById(R.id.edt_edit_name);
                    final EditText edtPhone = (EditText) dialog.findViewById(R.id.edt_edit_phone);
                    TextView tvOK = (TextView) dialog.findViewById(R.id.tv_edit_ok);
                    TextView tvCancel = (TextView) dialog.findViewById(R.id.tv_edit_cancel);
                    edtName.setText(listBlack.get(positionSeleceted).getUserName());
                    edtPhone.setText(listBlack.get(positionSeleceted).getPhoneNum());
                    tvOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ContactObj contactObj = listBlack.get(positionSeleceted);
                            contactObj.setUserName(edtName.getText().toString());
                            contactObj.setPhoneNum(edtPhone.getText().toString());
                            BlacklistData.Instance(getContext()).update(contactObj);
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
                            dialog.cancel();
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
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            blacklistAdapter.clearSelectedItems();
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
                positionSeleceted = position;
                enableActionMode(itemView,position);
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
                LinearLayout llFromAccount = (LinearLayout) dialog.findViewById(R.id.ll_from_account);
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
                        final Dialog dialogNumber = new Dialog(getActivity());
                        dialogNumber.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogNumber.setContentView(R.layout.dialog_input_number);
                        final EditText edtName = (EditText) dialogNumber.findViewById(R.id.edt_name);
                        final EditText edtPhone = (EditText) dialogNumber.findViewById(R.id.edt_phone);
                        TextView tvOK = (TextView) dialogNumber.findViewById(R.id.tv_ok);
                        TextView tvCancel = (TextView) dialogNumber.findViewById(R.id.tv_cancel);
                        dialogNumber.show();
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
                                dialogNumber.cancel();
                            }
                        });


                    }
                });

                llFromAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialogAccount = new Dialog(getActivity());
                        dialogAccount.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialogAccount.setContentView(R.layout.dialog_input_account);
                        final EditText edtAccount = (EditText) dialogAccount.findViewById(R.id.edt_account);
                        TextView tvOK = (TextView) dialogAccount.findViewById(R.id.tv_ok_account);
                        TextView tvCancel = (TextView) dialogAccount.findViewById(R.id.tv_cancel_account);
                        dialogAccount.show();
                        dialog.cancel();

                        tvOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String account = edtAccount.getText().toString();
                                AppUtil.setAccount(getContext(), account);
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
                                dialogAccount.cancel();
                            }
                        });

                        tvCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogAccount.cancel();
                            }
                        });
                    }
                });
            }
        });

        return rootView;
    }

    public void enableActionMode(View itemView, int position) {
        if(mActionMode == null) {
            mActionMode = getActivity().startActionMode(modeCallBack);
//            itemView.setSelected(true);
            toggleSelection(itemView,position);
        }
    }

    public void toggleSelection(View itemView, int position) {
        blacklistAdapter.toggleSelection(itemView,position);
        int count = blacklistAdapter.getSelectedItemCount();
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

    @Override
    public void onResume() {
        super.onResume();
        boolean enableValue = AppUtil.isEnableBlock(getActivity());
        AppUtil.enableService(getActivity(),enableValue);
        boolean checkSwSyn = AppUtil.isEnableSyn(getContext());
        if (checkSwSyn) {
            String account = AppUtil.getAccount(getContext(),"");
            if(!account.equals("")) {
                mDatabase = FirebaseDatabase.getInstance().getReference(account);
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
            }
        } else {
            listBlack.clear();
            listBlack.addAll(BlacklistData.Instance(getContext()).getAllBlacklist());
            blacklistAdapter.notifyDataSetChanged();
        }
    }
}
