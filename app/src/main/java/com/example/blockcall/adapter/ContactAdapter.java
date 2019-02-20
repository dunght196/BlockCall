package com.example.blockcall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blockcall.R;
import com.example.blockcall.controller.ItemClickListener;
import com.example.blockcall.db.table.BlacklistData;
import com.example.blockcall.model.ContactObj;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder> {

    List<ContactObj> listContact;
    Context context;
    ItemClickListener itemClickListener;

    public ContactAdapter(List<ContactObj> listContact, Context context, ItemClickListener itemClickListener) {
        this.listContact = listContact;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.tvName.setText(listContact.get(i).getUserName());
        myViewHolder.tvPhone.setText(listContact.get(i).getPhoneNum());
        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    itemClickListener.onCheckClick(i);
                }else {
                    itemClickListener.onUncheckClick(i);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return listContact.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        CheckBox checkBox;
        ImageView imageView;
        TextView tvName;
        TextView tvPhone;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.cb_contact);
            imageView = (ImageView) itemView.findViewById(R.id.iv_contact);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone);
            view = (View)itemView.findViewById(R.id.view_line_contact);
        }
    }
}
