package com.example.blockcall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.blockcall.R;
import com.example.blockcall.model.ContactObj;

import java.util.ArrayList;
import java.util.List;

public class BlockcallAdapter extends RecyclerView.Adapter<BlockcallAdapter.MyViewHolder> {

    private List<ContactObj> listBlock;
    private Context context;
    private static BlacklistAdapter.OnItemClickListener listener;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();


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

        public MyViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_block_call);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_block_call);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_block_call);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date_block);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time_block);
            view = (View)itemView.findViewById(R.id.view_line_block);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }

    public void toggleSelection(View itemView, int pos) {
        if(selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            itemView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        }else {
            selectedItems.put(pos,true);
            itemView.setBackgroundColor(Color.parseColor("#DDDDDD"));
        }
    }

    public void setOnItemClickListener(BlacklistAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getPositionItem() {
        List<Integer> listItem = new ArrayList<>();
        for(int i=0; i<selectedItems.size(); i++) {
            listItem.add(selectedItems.keyAt(i));
        }
        return listItem;
    }
}
