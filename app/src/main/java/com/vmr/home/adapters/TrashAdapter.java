package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.VmrTrashItem;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.TrashViewHolder>{

    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<VmrTrashItem> itemsList;

    public TrashAdapter(List<VmrTrashItem> itemsList, OnItemClickListener itemClickListener, OnItemOptionsClickListener optionsClickListener ) {
        this.itemClickListener = itemClickListener;
        this.itemsList = itemsList;
        this.optionsClickListener = optionsClickListener;
    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trash_item_layout, parent, false);

        return new TrashViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrashViewHolder holder, int position) {
        VmrTrashItem item = this.itemsList.get(position);
        holder.setItemName(item.getName());
        if (item.isFolder()) {
            holder.setItemImage(R.drawable.ic_folder);
        } else {
            holder.setItemImage(R.drawable.ic_file);
        }
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public List<VmrTrashItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<VmrTrashItem> itemsList) {
        this.itemsList = itemsList;
    }

    public void updateDataset(List<VmrTrashItem> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(VmrTrashItem item);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(VmrTrashItem item, View view);
    }

    public class TrashViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private ImageView itemOptions;

        public TrashViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemOptions(ImageView itemOptions) {
            this.itemOptions = itemOptions;
        }

        public void bind(final VmrTrashItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void bind(final VmrTrashItem item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }

    }

}
