package com.vmr.home.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.shared_by_me.SharedRecord;

import java.util.List;

/*
* Created by abhijit on 8/31/16.
 */
public class SharedByMeAdapter extends RecyclerView.Adapter<SharedByMeAdapter.SharedByMeViewHolder>{

    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<SharedRecord> itemsList;

    public SharedByMeAdapter(
            List<SharedRecord> itemsList,
            OnItemClickListener itemClickListener,
            OnItemOptionsClickListener optionsClickListener ) {
        this.itemClickListener = itemClickListener;
        this.optionsClickListener = optionsClickListener;
        this.itemsList = itemsList;
    }

    @Override
    public SharedByMeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shared_by_me_item_layout, parent, false);
        return new SharedByMeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SharedByMeViewHolder holder, int position) {
        SharedRecord item = this.itemsList.get(position);
        holder.setItemName(item.getRecordName());
        holder.setItemImage(R.drawable.ic_file);
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public void updateDataset(List<SharedRecord> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(SharedRecord record);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(SharedRecord record, View view);
    }

    class SharedByMeViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private ImageView itemOptions;

        SharedByMeViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        void bind(final SharedRecord item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        void bind(final SharedRecord item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }

    }
}
