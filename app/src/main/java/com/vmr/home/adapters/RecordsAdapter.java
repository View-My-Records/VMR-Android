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
                .inflate(R.layout.item_layout_records, parent, false);

        return new MyRecordsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyRecordsViewHolder holder, int position) {
        Record record = this.records.get(position);

        if (record.isFolder()) {
            holder.setItemImage(R.drawable.ic_folder);
            holder.itemSize.setVisibility(View.GONE);
        } else {
            holder.setItemImage(R.drawable.ic_file);

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);

            String value;
            float size = record.getFileSize();
            if (size >= (1000*1000))
                value = df.format(size / (1000*1000)) + " Mb";
            else if(size >= 1000)
                value = df.format(size / 1000) + " Kb";
            else
                value = df.format(size) + " Bytes";

            holder.setItemSize(value);
        }
        holder.setItemName(record.getRecordName());
//        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
//        holder.setItemTimeStamp(ft.format(record.getCreatedDate()));
        holder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(record.getCreatedDate().getTime()).toString());
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
