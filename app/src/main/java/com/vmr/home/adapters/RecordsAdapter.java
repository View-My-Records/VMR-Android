package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrItem;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/*
 * Created by abhijit on 8/25/16.
 */
public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.MyRecordsViewHolder>{

    private List<VmrItem> itemsList;
    private final OnItemClickListener itemClickListener;
    private final OnItemOptionsClickListener optionsClickListener;

    public RecordsAdapter(List<VmrItem> itemsList, OnItemClickListener itemClickListener, OnItemOptionsClickListener optionsClickListener ) {
        this.itemsList = itemsList;
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
        VmrItem item = this.itemsList.get(position);
        holder.setItemName(item.getName());
        if (item instanceof VmrFile) {
            holder.setItemImage(R.drawable.ic_file);
            holder.setItemSize(((VmrFile)item).getFileSize());
        } else {
            holder.setItemImage(R.drawable.ic_folder);
            holder.setItemSize(null);
        }
        SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        holder.setItemTimeStamp(ft.format(item.getCreated()));
        holder.bind(itemsList.get(position), itemClickListener);
        holder.bind(itemsList.get(position), optionsClickListener);
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

    public void updateDataset(List<VmrItem> newList) {
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(VmrItem item);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(VmrItem item, View view);
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

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemImage(ImageView itemImage) {
            this.itemImage = itemImage;
        }

        public void bind(final VmrItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void bind(final VmrItem item, final OnItemOptionsClickListener listener) {
            itemOptions.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemOptionsClick(item, v);
                }
            });
        }
    }
}
