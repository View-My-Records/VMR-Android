package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.record.Record;

import java.text.DecimalFormat;
import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int FOLDER = 1;
    private static final int FILE = 2;
    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;
    private List<Record> records;

    public RecordsAdapter(List<Record> records,
                          OnItemClickListener itemClickListener,
                          OnItemOptionsClickListener optionsClickListener ) {
        this.records = records;
        this.itemClickListener = itemClickListener;
        this.optionsClickListener = optionsClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case FOLDER:
                View v1 = inflater.inflate(R.layout.item_layout_records_folder, parent, false);
                viewHolder = new FolderViewHolder(v1);
                break;
            case FILE:
                View v2 = inflater.inflate(R.layout.item_layout_records_file, parent, false);
                viewHolder = new FileViewHolder(v2);
                break;
            default:
                View v3 = inflater.inflate(R.layout.item_layout_records_folder, parent, false);
                viewHolder = new FolderViewHolder(v3);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case FOLDER:
                FolderViewHolder folderViewHolder = (FolderViewHolder) holder;
                configureFolderViewHolder(folderViewHolder, position);
                break;
            case FILE:
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                configureFileViewHolder(fileViewHolder, position);
                break;
            default:
                FolderViewHolder defaultViewHolder = (FolderViewHolder) holder;
                configureDefaultViewHolder(defaultViewHolder, position);
                break;
        }

    }

    private void configureDefaultViewHolder(FolderViewHolder folderViewHolder, int position) {
        folderViewHolder.setItemName(records.get(position).getRecordName());
        folderViewHolder.bind(records.get(position), itemClickListener);
        folderViewHolder.bind(records.get(position), optionsClickListener);
    }

    private void configureFolderViewHolder(FolderViewHolder folderViewHolder, int position) {
        folderViewHolder.setItemName(records.get(position).getRecordName());
        folderViewHolder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(records.get(position).getCreatedDate().getTime()).toString());
        folderViewHolder.bind(records.get(position), itemClickListener);
        folderViewHolder.bind(records.get(position), optionsClickListener);
    }

    private void configureFileViewHolder(FileViewHolder fileViewHolder, int position) {
        String recordName = records.get(position).getRecordName();

        if(recordName.contains(".")) {
            fileViewHolder.setItemName(recordName.substring(0, recordName.lastIndexOf('.')));
        }

        if (!records.get(position).getRecordDocType().equals("vmr:unindexed")) {
            fileViewHolder.setItemIndexed(View.VISIBLE);
        } else { // indexed
            fileViewHolder.setItemIndexed(View.GONE);
        }

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        String value;
        float size = records.get(position).getFileSize();
        if (size >= (1024*1024))
            value = df.format(size / (1024*1024)) + " Mb";
        else if(size >= 1024)
            value = df.format(size / 1024) + " Kb";
        else
            value = df.format(size) + " Bytes";

        fileViewHolder.setItemSize(value);

        fileViewHolder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(records.get(position).getCreatedDate().getTime()).toString());
        fileViewHolder.bind(records.get(position), itemClickListener);
        fileViewHolder.bind(records.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.records.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (records.get(position).isFolder() ) {
            return FOLDER;
        } else {
            return FILE;
        }
    }

    public void updateDataset(List<Record> newList) {
        this.records.clear();
        this.records.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Record record);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(Record record, View view);
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIcon;
        private ImageView itemIndexed;
        private TextView itemName ;
        private TextView itemSize ;
        private TextView itemTimeStamp;
        private ImageView itemOptions;

        public FileViewHolder(View itemView) {
            super(itemView);
            this.itemIcon = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemIndexed = (ImageView) itemView.findViewById(R.id.ivIndexed);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setItemIcon(int itemIcon) {
            this.itemIcon.setImageResource(itemIcon);
        }

        public void setItemIndexed(int visibility) {
            this.itemIndexed.setVisibility(visibility);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemSize(Long itemSize) {
            this.itemSize.setText(String.valueOf(itemSize));
        }

        public void setItemSize(String itemSize) {
            this.itemSize.setText(itemSize);
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemImage(ImageView itemImage) {
            this.itemIcon = itemImage;
        }

        public void bind(final Record record, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(record);
                }
            });
        }

        public void bind(final Record record, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(record, v);
                }
            });
        }
    }

    public class FolderViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemIcon;
        private ImageView itemIndexed;
        private TextView itemName ;
        private TextView itemSize ;
        private TextView itemTimeStamp;
        private ImageView itemOptions;

        public FolderViewHolder(View itemView) {
            super(itemView);
            this.itemIcon = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setItemIcon(int itemIcon) {
            this.itemIcon.setImageResource(itemIcon);
        }

        public void setItemIndexed(int visibility) {
            this.itemIndexed.setVisibility(visibility);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemSize(Long itemSize) {
            this.itemSize.setText(String.valueOf(itemSize));
        }

        public void setItemSize(String itemSize) {
            this.itemSize.setText(itemSize);
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setVisibility(View.GONE);
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemImage(ImageView itemImage) {
            this.itemIcon = itemImage;
        }

        public void bind(final Record record, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(record);
                }
            });
        }

        public void bind(final Record record, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(record, v);
                }
            });
        }
    }
}
