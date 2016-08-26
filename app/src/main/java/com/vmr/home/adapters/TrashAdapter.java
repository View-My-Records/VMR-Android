package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.folder_structure.VmrTrashItem;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.TrashViewHolder>{

    private List<VmrTrashItem> itemsList;
    private final OnItemClickListener listener;

    public TrashAdapter(List<VmrTrashItem> itemsList, OnItemClickListener listener) {
        this.listener = listener;
        this.itemsList = itemsList;
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
        holder.bind(itemsList.get(position), listener);
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

    public class TrashViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;

        public TrashViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
        }

        public void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void bind(final VmrTrashItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
