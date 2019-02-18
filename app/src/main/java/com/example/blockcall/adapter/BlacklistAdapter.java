package com.example.blockcall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.controller.ItemClickListener;
import com.example.blockcall.controller.RecyclerClick_Listener;
import com.example.blockcall.model.ContactObj;

import java.util.List;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.MyViewHolder> {

    List<ContactObj> listBlack;
    Context context;
    RecyclerClick_Listener recyclerClick_listener;

    public BlacklistAdapter(List<ContactObj> listContact, Context context, RecyclerClick_Listener recyclerClick_listener) {
        this.listBlack = listContact;
        this.context = context;
        this.recyclerClick_listener = recyclerClick_listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blacklist, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.tvName.setText(listBlack.get(i).getUserName());
        myViewHolder.tvPhone.setText(listBlack.get(i).getPhoneNum());
//        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
////            @Override
////            public boolean onLongClick(View v) {
////                recyclerClick_listener.onLongClick(i);
////                return true;
////            }
////        });

//        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recyclerClick_listener.onClick(i);
//            }
//        });

        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recyclerClick_listener.onLongClick(i);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBlack.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView imageView;
        TextView tvName;
        TextView tvPhone;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_blacklist);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_blacklist);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_blacklist);
        }
    }
}