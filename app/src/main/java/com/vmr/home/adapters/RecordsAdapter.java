package com.vmr.home.adapters;

import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 8/25/16.
 */
public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyRecordsViewHolder>{

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
    public MyRecordsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.records_item_layout, parent, false);

        return new MyRecordsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyRecordsViewHolder holder, int position) {
        Record record = this.records.get(position);

        if (record.isFolder()) {
            holder.setItemImage(R.drawable.ic_folder);
            holder.itemSize.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT < 23) {
                holder.itemName.setTextAppearance(Vmr.getVMRContext(), android.R.style.TextAppearance_Medium);
            } else {
                holder.itemName.setTextAppearance(android.R.style.TextAppearance_Medium);
            }
        } else {
            holder.setItemImage(R.drawable.ic_file);
            holder.setItemSize(record.getFileSize() + " bytes");
        }
        holder.setItemName(record.getRecordName());
        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        holder.setItemTimeStamp(ft.format(record.getCreatedDate()));
        holder.bind(records.get(position), itemClickListener);
        holder.bind(records.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.records.size();
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

    public class MyRecordsViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private TextView itemSize ;
        private TextView itemTimeStamp;
        private ImageView itemOptions;

        public MyRecordsViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemSize = (TextView) itemView.findViewById(R.id.tvFileSize);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemOptions = (ImageView) itemView.findViewById(R.id.ivOverflow);
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

        public void setItemSize(String itemSize) {
            this.itemSize.setText(itemSize);
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemImage(ImageView itemImage) {
            this.itemImage = itemImage;
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
