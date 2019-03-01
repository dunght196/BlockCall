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

public class BaseAdapter  extends RecyclerView.Adapter<BaseAdapter.MyViewHolder> {

    private List<ContactObj> listBase;
    private Context context;
    private static OnItemClickListener listener;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int page;


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public BaseAdapter(List<ContactObj> listBase, Context context, int page) {
        this.listBase = listBase;
        this.context = context;
        this.page = page;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = null;
        if (page == 0) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blacklist, viewGroup, false);
        }else if (page == 1){
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_block_call, viewGroup, false);
        }
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        if (page == 0) {
            myViewHolder.tvName.setText(listBase.get(i).getUserName());
            myViewHolder.tvPhone.setText(listBase.get(i).getPhoneNum());
        }else if(page == 1) {
            myViewHolder.tvName.setText(listBase.get(i).getUserName());
            myViewHolder.tvPhone.setText(listBase.get(i).getPhoneNum());
            myViewHolder.tvDate.setText(listBase.get(i).getDateBlock());
            myViewHolder.tvTime.setText(listBase.get(i).getTimeBlock());
        }
    }

    @Override
    public int getItemCount() {
        return listBase.size();
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
            if (page == 0) {
                imageView = (ImageView) itemView.findViewById(R.id.iv_blacklist);
                tvName = (TextView) itemView.findViewById(R.id.tv_name_blacklist);
                tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_blacklist);
                view = (View)itemView.findViewById(R.id.view_line_blackllist);
            }else if (page == 1) {
                imageView = (ImageView) itemView.findViewById(R.id.iv_block_call);
                tvName = (TextView) itemView.findViewById(R.id.tv_name_block_call);
                tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_block_call);
                tvDate = (TextView) itemView.findViewById(R.id.tv_date_block);
                tvTime = (TextView) itemView.findViewById(R.id.tv_time_block);
                view = (View)itemView.findViewById(R.id.view_line_block);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
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

    public void clearSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }
}
