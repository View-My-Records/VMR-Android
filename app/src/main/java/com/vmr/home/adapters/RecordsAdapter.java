package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrItem;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyRecordsViewHolder>{

    private List<VmrItem> itemsList;
    private final OnItemClickListener listener;

    public RecordsAdapter(List<VmrItem> itemsList, OnItemClickListener listener ) {
        this.itemsList = itemsList;
        this.listener = listener;
    }

    @Override
    public MyRecordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.records_item_layout, parent, false);

        return new MyRecordsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyRecordsViewHolder holder, int position) {
        VmrItem item = this.itemsList.get(position);
        holder.setItemName(item.getName());
        if (item instanceof VmrFile) {
            holder.setItemImage(R.drawable.ic_file);
            holder.setItemSize(((VmrFile)item).getFileSize());
        } else {
            holder.setItemImage(R.drawable.ic_folder);
            holder.setItemSize(null);
        }
        holder.setItemTimeStamp(item.getCreated().toString());
        holder.bind(itemsList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public List<VmrItem> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<VmrItem> itemsList) {
        this.itemsList = itemsList;
    }

    public void updateDataset(List<VmrItem> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(VmrItem item);
    }

    public class MyRecordsViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private TextView itemSize ;
        private TextView itemTimeStamp ;

        public MyRecordsViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
        }

        public void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemSize(Long itemSize) {
            this.itemSize.setText(String.valueOf(itemSize));
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void bind(final VmrItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
