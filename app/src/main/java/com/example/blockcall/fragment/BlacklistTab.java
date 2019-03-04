package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.activity.ContactActivity;
import com.example.blockcall.adapter.BlacklistAdapter;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.dialog.DialogDelete;
import com.example.blockcall.dialog.DialogEdit;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.example.blockcall.utils.DialogUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BlacklistTab extends ListFragment {

    private List<ContactObj> listBlack = new ArrayList<>();
    private BlacklistAdapter blacklistAdapter;
    private int positionSeleceted = -1;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blacklist, container, false);
        bindView(rootView);
        modeCallback = new ActionModeCallback();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(mLayoutManager);
        blacklistAdapter = new BlacklistAdapter(listBlack, getContext());
        rvList.setAdapter(blacklistAdapter);
        blacklistAdapter.setOnItemClickListener(new BlacklistAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                positionSeleceted = position;
                enableActionMode(itemView,position);
            }
        });
        fab.setImageResource(R.drawable.ic_add);
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
                                int idNote = 1;
                                if(!listBlack.isEmpty()) {
                                    idNote = listBlack.get(listBlack.size()-1).getId() + 1;
                                }
                                ContactObj contactObj = new ContactObj();
                                contactObj.setId(idNote);
                                contactObj.setUserName(edtName.getText().toString());
                                contactObj.setPhoneNum(edtPhone.getText().toString());
                                if(AppUtil.isEnableSyn(getActivity())) {
                                    mDatabase.child(String.valueOf(idNote)).setValue(contactObj);
                                    BlacklistData.Instance(getActivity()).add(contactObj);
                                }else {
                                    BlacklistData.Instance(getActivity()).add(contactObj);
                                }
                                dialogNumber.cancel();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
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
            mDatabase = FirebaseDatabase.getInstance().getReference("dunght");
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listBlack.clear();
                    BlacklistData.Instance(getActivity()).deleteAll();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ContactObj contactObj = postSnapshot.getValue(ContactObj.class);
                        listBlack.add(contactObj);
                        BlacklistData.Instance(getActivity()).add(contactObj);
                     }
                    if(!listBlack.isEmpty()) {
                        blacklistAdapter.notifyDataSetChanged();
                    }
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

    @Override
    public void handleActionDelete(final ActionMode mode) {
        final DialogUtil dialogUtil = new DialogUtil(getContext(), R.layout.dialog_delete, listBlack.get(positionSeleceted));
        dialogUtil.action(new DialogUtil.DialogListener() {
            @Override
            public void onClickDone() {
                for(Integer value : blacklistAdapter.getPositionItem()) {
                    if(AppUtil.isEnableSyn(getActivity())) {
                        String idContact = String.valueOf(listBlack.get(value).getId());
                        BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                        mDatabase.child(idContact).removeValue();
                        listBlack.remove(value);
                    }else {
                        BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                        listBlack.remove(value);
                    }
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
        final DialogUtil dialogUtil = new DialogUtil(getContext(), R.layout.dialog_edit, listBlack.get(positionSeleceted));
        dialogUtil.action(new DialogUtil.DialogListener() {
            @Override
            public void onClickDone() {
                if (AppUtil.isEnableSyn(getActivity())) {
                    String idContact = String.valueOf(listBlack.get(positionSeleceted).getId());
                    ContactObj contactObj = listBlack.get(positionSeleceted);
                    contactObj.setUserName(dialogUtil.edtName.getText().toString());
                    contactObj.setPhoneNum(dialogUtil.edtPhone.getText().toString());
                    mDatabase.child(idContact).child("phoneNum").setValue(contactObj.getPhoneNum());
                    mDatabase.child(idContact).child("userName").setValue(contactObj.getUserName());
                    BlacklistData.Instance(getContext()).update(contactObj);
                }else {
                    ContactObj contactObj = listBlack.get(positionSeleceted);
                    contactObj.setUserName(dialogUtil.edtName.getText().toString());
                    contactObj.setPhoneNum(dialogUtil.edtPhone.getText().toString());
                    BlacklistData.Instance(getContext()).update(contactObj);
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
    public void handleActionDestroy() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(BlacklistTab.this).attach(BlacklistTab.this).commit();
        super.handleActionDestroy();
    }

    public void bindView(View view) {
        super.bindView(view);
    }

    @Override
    public void enableActionMode(View itemView, int position) {
        super.enableActionMode(itemView, position);
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
