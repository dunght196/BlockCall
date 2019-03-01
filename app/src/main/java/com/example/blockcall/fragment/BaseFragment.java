package com.example.blockcall.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.example.blockcall.adapter.BaseAdapter;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.db.table.BlockcallData;
import com.example.blockcall.model.ContactObj;
import com.example.blockcall.utils.AppUtil;
import com.example.blockcall.utils.Constant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment {

    private int page;
    private FloatingActionButton fab;
    private RecyclerView rvBase;
    private List<ContactObj> listBlack = new ArrayList<>();
    private List<ContactObj> listBlock = new ArrayList<>();
    private BaseAdapter baseAdapter;
    private ActionModeCallback modeCallback;
    private ActionMode mActionMode;
    private int positionSeleceted = -1;
    private DatabaseReference mDatabase;
    private IntentFilter mIntentFilter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.mBroadcastAction)) {
                listBlock.clear();
                listBlock.addAll( BlockcallData.Instance(context).getAllBlockCall());
                baseAdapter.notifyDataSetChanged();
            }
        }
    };


    public static BaseFragment newInstance(int page) {
        BaseFragment baseFragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putInt("page", page);
        baseFragment.setArguments(args);
        return baseFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("page", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base,container,false);
        fab = rootView.findViewById(R.id.fab_base);
        rvBase = rootView.findViewById(R.id.rv_base);
        modeCallback = new ActionModeCallback();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvBase.setLayoutManager(mLayoutManager);
        if(page == 0) {
            fab.setImageResource(R.drawable.ic_add);
            baseAdapter = new BaseAdapter(listBlack,getContext(),page);
            rvBase.setAdapter(baseAdapter);
            baseAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
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
                                    int idNote = 0;
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
                                    ft.detach(BaseFragment.this).attach(BaseFragment.this).commit();
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

        }else if(page == 1){
            fab.setImageResource(R.drawable.ic_delete);
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(Constant.mBroadcastAction);

            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Constant.mBroadcastAction)) {
                        listBlock.clear();
                        listBlock.addAll( BlockcallData.Instance(context).getAllBlockCall());
                        baseAdapter.notifyDataSetChanged();
                    }
                }
            };

            listBlock.addAll( BlockcallData.Instance(getContext()).getAllBlockCall());
            Log.e("list","=" + listBlock.size());
            baseAdapter = new BaseAdapter(listBlock,getContext(),page);
            rvBase.setAdapter(baseAdapter);
            baseAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View itemView, int position) {
                    positionSeleceted = position;
                    enableActionMode(itemView,position);
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialogDelete = new Dialog(getActivity());
                    dialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialogDelete.setContentView(R.layout.dialog_delete);
                    TextView tvOKDelete = (TextView) dialogDelete.findViewById(R.id.tv_ok_delete);
                    TextView tvCancelDelete = (TextView) dialogDelete.findViewById(R.id.tv_cancel_delete);
                    tvOKDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BlockcallData.Instance(getContext()).deleteAll();
                            listBlock.clear();
                            baseAdapter.notifyDataSetChanged();
                            dialogDelete.cancel();
                        }
                    });

                    tvCancelDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogDelete.cancel();
                        }
                    });

                    dialogDelete.show();
                }
            });
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (page == 0) {
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
                            baseAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            } else {
                listBlack.clear();
                listBlack.addAll(BlacklistData.Instance(getContext()).getAllBlacklist());
                baseAdapter.notifyDataSetChanged();
            }
        }else if (page == 1){
            getActivity().registerReceiver(mReceiver, mIntentFilter);
        }

    }

    @Override
    public void onDestroy() {
        if (page == 1) getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
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
                            if (page == 0) {
                                for (Integer value : baseAdapter.getPositionItem()) {
                                    if (AppUtil.isEnableSyn(getActivity())) {
                                        String idContact = String.valueOf(listBlack.get(value).getId());
                                        BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                                        mDatabase.child(idContact).removeValue();
                                        listBlack.remove(value);
                                    } else {
                                        BlacklistData.Instance(getContext()).delete(listBlack.get(value));
                                        listBlack.remove(value);
                                    }
                                }
                            } else if (page == 1) {
                                for (Integer value : baseAdapter.getPositionItem()) {
                                    BlockcallData.Instance(getContext()).delete(listBlock.get(value));
                                    listBlock.remove(value);
                                }
                                baseAdapter.clearSelectedItems();
                                baseAdapter.notifyDataSetChanged();
                            }
                            baseAdapter.clearSelectedItems();
                            baseAdapter.notifyDataSetChanged();
                            dialogDelete.cancel();
                            mode.finish();
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
                            if (page == 0) {
                                if (AppUtil.isEnableSyn(getActivity())) {
                                    String idContact = String.valueOf(listBlack.get(positionSeleceted).getId());
                                    ContactObj contactObj = listBlack.get(positionSeleceted);
                                    contactObj.setUserName(edtName.getText().toString());
                                    contactObj.setPhoneNum(edtPhone.getText().toString());
                                    mDatabase.child(idContact).child("phoneNum").setValue(contactObj.getPhoneNum());
                                    mDatabase.child(idContact).child("userName").setValue(contactObj.getUserName());
                                    BlacklistData.Instance(getContext()).update(contactObj);
                                }else {
                                    ContactObj contactObj = listBlack.get(positionSeleceted);
                                    contactObj.setUserName(edtName.getText().toString());
                                    contactObj.setPhoneNum(edtPhone.getText().toString());
                                    BlacklistData.Instance(getContext()).update(contactObj);
                                }
                            }else if (page == 1) {
                                ContactObj contactObj = listBlock.get(positionSeleceted);
                                contactObj.setUserName(edtName.getText().toString());
                                contactObj.setPhoneNum(edtPhone.getText().toString());
                                BlockcallData.Instance(getContext()).update(contactObj);
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
            baseAdapter.clearSelectedItems();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.detach(BaseFragment.this).attach(BaseFragment.this).commit();
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
        baseAdapter.toggleSelection(itemView,position);
        int count = baseAdapter.getSelectedItemCount();
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
