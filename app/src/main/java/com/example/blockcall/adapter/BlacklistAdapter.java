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

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.MyViewHolder> {

    private List<ContactObj> listBlack;
    private Context context;
    private static OnItemClickListener listener;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }


    public BlacklistAdapter(List<ContactObj> listContact, Context context) {
        this.listBlack = listContact;
        this.context = context;
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
    }

    @Override
    public int getItemCount() {
        return listBlack.size();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's
        ImageView imageView;
        TextView tvName;
        TextView tvPhone;
        View view;

        public MyViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_blacklist);
            tvName = (TextView) itemView.findViewById(R.id.tv_name_blacklist);
            tvPhone = (TextView) itemView.findViewById(R.id.tv_phone_blacklist);
            view = (View)itemView.findViewById(R.id.view_line_blackllist);

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
