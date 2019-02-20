package com.example.blockcall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.model.ContactObj;

import java.util.List;

public class BlockcallAdapter extends RecyclerView.Adapter<BlockcallAdapter.MyViewHolder> {

    List<ContactObj> listBlock;
    Context context;

    public BlockcallAdapter(List<ContactObj> listBlock, Context context) {
        this.listBlock = listBlock;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_block_call, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.tvName.setText(listBlock.get(i).getUserName());
        myViewHolder.tvPhone.setText(listBlock.get(i).getPhoneNum());
        myViewHolder.tvDate.setText(listBlock.get(i).getDateBlock());
        myViewHolder.tvTime.setText(listBlock.get(i).getTimeBlock());
    }

    @Override
    public int getItemCount() {
        return listBlock.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView imageView;
        TextView tvName;
        TextView tvPhone;
        TextView tvDate;
        TextView tvTime;
        View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_block_call);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_block_call);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_block_call);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date_block);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time_block);
            view = (View)itemView.findViewById(R.id.view_line_block);
        }
    }
}
