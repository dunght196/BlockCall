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
    private ActionModeCallback modeCallback;
    private ActionMode mActionMode;
    private int positionSeleceted = -1;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);

        fab = rootView.findViewById(R.id.fab_blacklist);
        rvBlacklist = rootView.findViewById(R.id.rv_blacklist);
        String account = AppUtil.getAccount(getContext(),"");
        mDatabase = FirebaseDatabase.getInstance().getReference(account);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvBlacklist.setLayoutManager(mLayoutManager);
        modeCallback = new ActionModeCallback();
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
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean enableValue = AppUtil.isEnableBlock(getActivity());
        AppUtil.enableService(getActivity(),enableValue);
        boolean checkSwSyn = AppUtil.isEnableSyn(getContext());
        if (checkSwSyn) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listBlack.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
                        listBlack.add(contactObj);
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

    private class ActionModeCallback implements ActionMode.Callback {
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
                    final Dialog dialogDelete = new Dialog(getActivity());
                    dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogDelete.setContentView(R.layout.dialog_delete);
                    TextView tvOKDelete = (TextView) dialogDelete.findViewById(R.id.tv_ok_delete);
                    TextView tvCancelDelete = (TextView) dialogDelete.findViewById(R.id.tv_cancel_delete);

                    tvOKDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for(Integer value : blacklistAdapter.getPositionItem()) {
                                if(AppUtil.isEnableSyn(getActivity())) {
                                    String idContact = String.valueOf(listBlack.get(value).getId());
                                    listBlack.remove(value);
                                    mDatabase.child(idContact).setValue(null);
                                }else {
                                    BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                                    listBlack.remove(value);
                                }
                            }
                            dialogDelete.cancel();
                            mode.finish();
                            blacklistAdapter.clearSelectedItems();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
                        }
                    });

                    tvCancelDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDelete.cancel();
                            mode.finish();
                        }
                    });

                    dialogDelete.show();
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
                            if (AppUtil.isEnableSyn(getActivity())) {
                                String idContact = String.valueOf(listBlack.get(positionSeleceted).getId());
                                mDatabase.child(idContact).child("phoneNum").setValue(edtPhone.getText().toString());
                                mDatabase.child(idContact).child("userName").setValue(edtName.getText().toString());
                            }else {
                                ContactObj contactObj = listBlack.get(positionSeleceted);
                                contactObj.setUserName(edtName.getText().toString());
                                contactObj.setPhoneNum(edtPhone.getText().toString());
                                BlacklistData.Instance(getContext()).update(contactObj);
                            }
                            dialog.cancel();
                        }
                    });
                    tvCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mode.finish();
                            dialog.cancel();
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
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
            mActionMode = null;
        }
    };

    public void enableActionMode(View itemView, int position) {
        if(mActionMode == null) {
            mActionMode = getActivity().startActionMode(modeCallback);
            itemView.setSelected(true);
        }
        toggleSelection(itemView,position);

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


}
