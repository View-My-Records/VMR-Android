package com.vmr.share.select.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.record.Record;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class FolderAdapter
        extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder>{

    private final OnItemClickListener itemClickListener;
    private List<Record> itemsList;

    public FolderAdapter(List<Record> itemsList,
                         OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
        this.itemsList = itemsList;
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FolderViewHolder holder, int position) {
        Record record = this.itemsList.get(position);
        if (record.isFolder()) {
            holder.setItemIcon(R.drawable.ic_folder);
            holder.setItemName(record.getRecordName().replaceAll("[^A-Za-z0-9( _)\\[\\]]", ""));
            holder.bind(itemsList.get(position), itemClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public void updateDataset(List<Record> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Record record);
        void onItemLongClick(Record record);
    }

    class FolderViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;

        FolderViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
        }

        void setItemIcon(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        void bind(final Record item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(item);
                    return true;
                }
            });
        }
    }

}
