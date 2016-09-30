package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.recently_accessed.Recent;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class RecentAdapter extends RecyclerView.Adapter<RecentAdapter.RecentViewHolder>{

    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<Recent> itemsList;

    public RecentAdapter(List<Recent> itemsList, OnItemClickListener itemClickListener, OnItemOptionsClickListener optionsClickListener ) {
        this.itemClickListener = itemClickListener;
        this.itemsList = itemsList;
        this.optionsClickListener = optionsClickListener;
    }

    @Override
    public RecentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_recent, parent, false);

        return new RecentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecentViewHolder holder, int position) {
        Recent recent = this.itemsList.get(position);
        holder.setItemName(recent.getName().replaceAll("[^A-Za-z0-9( _.)\\[\\]]", ""));

        holder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(recent.getLastAccess().getTime()).toString());

        holder.setItemImage(R.drawable.ic_file);
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public List<Recent> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Recent> itemsList) {
        this.itemsList = itemsList;
    }

    public void updateDataset(List<Recent> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Recent item);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(Recent item, View view);
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private TextView itemTimeStamp ;
        private ImageView itemOptions;

        public RecentViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemName = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemOptions(ImageView itemOptions) {
            this.itemOptions = itemOptions;
        }

        public void bind(final Recent item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void bind(final Recent item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }

    }

}
