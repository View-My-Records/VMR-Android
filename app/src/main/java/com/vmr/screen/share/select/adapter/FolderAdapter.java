package com.vmr.screen.share.select.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.record.Record;
import com.vmr.utils.FileUtils;

import java.text.DecimalFormat;
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
            holder.setEnabled(true);
            holder.setItemIcon(R.drawable.ic_folder);
            holder.setItemName(record.getRecordName().replaceAll("[^A-Za-z0-9( _)\\[\\]]", ""));
            holder.bind(itemsList.get(position), itemClickListener);
        } else {
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

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            holder.setEnabled(false);
            holder.setItemIcon(R.drawable.ic_file);
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
        private View itemLayout;
        private ImageView itemImage ;
        private TextView itemName ;

        FolderViewHolder(View itemView) {
            super(itemView);
            this.itemLayout = itemView;
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
        }

        void setEnabled(boolean enabled){
            if(enabled) {
                this.itemLayout.setEnabled(true);
                this.itemLayout.setAlpha(1);
            } else {
                this.itemLayout.setEnabled(false);
                this.itemLayout.setAlpha((float) 0.5);
            }
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
