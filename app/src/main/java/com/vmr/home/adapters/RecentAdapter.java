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
import com.vmr.utils.FileUtils;

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
        String recordName = recent.getName();

        if(recordName.contains(".")) {
            String extension = (recordName.substring(recordName.lastIndexOf('.') + 1)).toLowerCase();
            String mimeType = FileUtils.getMimeTypeFromExtension(extension);

            if(mimeType!=null) {
                if (mimeType.contains("image")) {
                    holder.setItemIcon(R.drawable.ic_file_image);
                } else if (mimeType.contains("video")) {
                    holder.setItemIcon(R.drawable.ic_file_video);
                } else {
                    switch (extension) {
                        case "pdf":
                            holder.setItemIcon(R.drawable.ic_file_pdf);
                            break;
                        case "xml":
                            holder.setItemIcon(R.drawable.ic_file_xml);
                            break;
                        default:
                            holder.setItemIcon(R.drawable.ic_file);
                            break;
                    }
                }
            }

            holder.setItemName(recordName.substring(0, recordName.lastIndexOf('.')));
        } else {
            holder.setItemName(recordName);
        }

        holder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(recent.getLastAccess().getTime()).toString());

        if (recent.isIndexed()) {
            holder.setItemIndexed(View.VISIBLE);
        } else {
            holder.setItemIndexed(View.GONE);
        }

        holder.bind(recent, itemClickListener);
        holder.bind(recent, optionsClickListener);
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
        private ImageView itemIcon;
        private ImageView itemIndexed;
        private TextView itemName ;
        private TextView itemTimeStamp ;
        private ImageView itemOptions;

        public RecentViewHolder(View itemView) {
            super(itemView);
            this.itemIcon = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemIndexed = (ImageView) itemView.findViewById(R.id.ivIndexed);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setItemIcon(int itemImage) {
            this.itemIcon.setImageResource(itemImage);
        }

        void setItemIndexed(int visibility) {
            this.itemIndexed.setVisibility(visibility);
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
