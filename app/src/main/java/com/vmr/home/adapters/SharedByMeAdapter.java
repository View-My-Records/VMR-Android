package com.vmr.home.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.VmrSharedItem;

import java.util.List;

/*
* Created by abhijit on 8/31/16.
 */
public class SharedByMeAdapter extends RecyclerView.Adapter<SharedByMeAdapter.SharedByMeViewHolder>{

    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<VmrSharedItem> itemsList;

    public SharedByMeAdapter(
            List<VmrSharedItem> itemsList,
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
        VmrSharedItem item = itemsList.get(position);
        holder.setItemName(item.getName());
        holder.setItemImage(R.drawable.ic_file);
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void updateDataset(List<VmrSharedItem> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(VmrSharedItem item);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(VmrSharedItem item, View view);
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

        void bind(final VmrSharedItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        void bind(final VmrSharedItem item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }

    }
}
