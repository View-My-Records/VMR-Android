package com.vmr.screen.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.trash.TrashRecord;
import com.vmr.utils.FileUtils;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.TrashViewHolder>{

    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<TrashRecord> itemsList;

    public TrashAdapter(List<TrashRecord> itemsList, OnItemClickListener itemClickListener, OnItemOptionsClickListener optionsClickListener ) {
        this.itemClickListener = itemClickListener;
        this.itemsList = itemsList;
        this.optionsClickListener = optionsClickListener;
    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_trash, parent, false);

        return new TrashViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrashViewHolder holder, int position) {
        TrashRecord record = this.itemsList.get(position);
        holder.setItemName(record.getRecordName().replaceAll("[^A-Za-z0-9( _)\\[\\]]", ""));
        if (record.isFolder()) {
            holder.setItemIcon(R.drawable.ic_folder);
        } else if(!record.isFolder()) {
            String recordName = record.getRecordName();

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
            }
        }
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public void updateDataset(List<TrashRecord> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(TrashRecord item);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(TrashRecord item, View view);
    }

    class TrashViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private ImageView itemOptions;

        TrashViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        void setItemIcon(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        void bind(final TrashRecord item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        void bind(final TrashRecord item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }

    }

}
