package com.vmr.screen.home.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.upload_queue.UploadItem;
import com.vmr.utils.FileUtils;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder>{

    private final OnItemClickListener itemClickListener;
    private List<UploadItem> uploadItems;

    public UploadAdapter(List<UploadItem> uploadItems, OnItemClickListener itemClickListener ) {
        this.itemClickListener = itemClickListener;
        this.uploadItems = uploadItems;
    }

    @Override
    public UploadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_upload, parent, false);
        return new UploadViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UploadViewHolder holder, int position) {
        UploadItem uploadItem = this.uploadItems.get(position);
        holder.setItemName(uploadItem.getFileName().replaceAll("[^A-Za-z0-9( _)\\[\\]]", ""));
        String recordName = uploadItem.getFileName();

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

            holder.setItemName(recordName);
        }

        holder.setItemUploadDate(DateUtils.getRelativeTimeSpanString(uploadItem.getCreationDate().getTime()).toString());

        holder.setupStatus(uploadItem);
        holder.setupActions(uploadItem);
        holder.bind(uploadItem, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.uploadItems.size();
    }

    public void updateDataset(List<UploadItem> newList){
        this.uploadItems.clear();
        this.uploadItems.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(UploadItem uploadItem);
        void onDeleteClick(UploadItem uploadItem);
        void onRetryClick(UploadItem uploadItem);
    }

    class UploadViewHolder extends RecyclerView.ViewHolder {
        private TextView itemUploadDate;
        private ImageView itemImage ;
        private TextView itemName ;
        private ImageView ivUploadStatus;
        private TextView tvUploadStatus;
        private ImageButton itemActionDelete;
        private ImageButton itemActionRetry;

        UploadViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemUploadDate = (TextView) itemView.findViewById(R.id.tvFileUploadDate);
            this.itemActionDelete = (ImageButton) itemView.findViewById(R.id.ivActionDelete);
            this.itemActionRetry = (ImageButton) itemView.findViewById(R.id.ivActionRetry);
            this.ivUploadStatus = (ImageView) itemView.findViewById(R.id.ivUploadStatus);
            this.tvUploadStatus = (TextView) itemView.findViewById(R.id.tvUploadStatus);
        }

        void setItemIcon(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        void setItemUploadDate(String itemName) {
            this.itemUploadDate.setText(itemName);
        }

        void setupStatus(UploadItem uploadItem){
            switch (uploadItem.getStatus()){
                case UploadItem.STATUS_SUCCESS:
                    ivUploadStatus.setImageResource(R.drawable.ic_status_success);
                    ivUploadStatus.setColorFilter(Color.parseColor("#FF669900")); // Dark Green
                    tvUploadStatus.setText("Success");
                    break;
                case UploadItem.STATUS_FAILED:
                    ivUploadStatus.setImageResource(R.drawable.ic_status_canceled);
                    ivUploadStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ivUploadStatus.setColorFilter(Color.parseColor("#e74c3c")); // Dark red
                    tvUploadStatus.setText("Failed");
                    break;
                case UploadItem.STATUS_PENDING:
                    ivUploadStatus.setImageResource(R.drawable.ic_status_pending);
                    ivUploadStatus.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    ivUploadStatus.setColorFilter(Color.GRAY);
                    tvUploadStatus.setText("Queued");
                    break;
                case UploadItem.STATUS_UPLOADING:
                    ivUploadStatus.setImageResource(R.drawable.ic_status_pending);
                    ivUploadStatus.setColorFilter(Color.GRAY);
                    tvUploadStatus.setText("Uploading");
                    break;
                default:
                    ivUploadStatus.setImageResource(R.drawable.ic_status_canceled);
                    ivUploadStatus.setColorFilter(Color.RED);
                    tvUploadStatus.setText("Error Occurred");
                    break;
            }
        }

        public void setupActions(UploadItem uploadItem) {
            if(uploadItem.getStatus() == UploadItem.STATUS_SUCCESS ){
                itemActionDelete.setVisibility(View.VISIBLE);
                itemActionRetry.setVisibility(View.GONE);
            } else {
                itemActionDelete.setVisibility(View.VISIBLE);
                itemActionRetry.setVisibility(View.VISIBLE);
            }
        }

        void bind(final UploadItem item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
            itemActionDelete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onDeleteClick(item);
                }
            });
            itemActionRetry.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onRetryClick(item);
                }
            });
        }
    }

}
