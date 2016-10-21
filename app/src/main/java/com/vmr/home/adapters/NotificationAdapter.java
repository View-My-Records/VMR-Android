package com.vmr.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.notification.Notification;

import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.TrashViewHolder>{

    private final OnNotificationClickListener itemClickListener;
    private List<Notification> itemsList;

    public NotificationAdapter(List<Notification> itemsList, OnNotificationClickListener itemClickListener ) {
        this.itemClickListener = itemClickListener;
        this.itemsList = itemsList;
    }

    @Override
    public TrashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_notification, parent, false);

        return new TrashViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrashViewHolder holder, int position) {
        Notification item = this.itemsList.get(position);
        if(item.isRead()){
            holder.setReadIndicator(View.INVISIBLE);
        } else {
            holder.setReadIndicator(View.VISIBLE);
        }
//        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//        String day = DateUtils.getRelativeTimeSpanString(item.getCreatedDate().getTime()).toString();
//        String time = localDateFormat.format(item.getCreatedDate().getTime());
//        holder.setReceiveTimeStamp( day + "\n" + time);
        holder.setReceiveTimeStamp( DateUtils.getRelativeTimeSpanString(item.getCreatedDate().getTime()).toString());
        holder.setSenderName(item.getSenderFirstName() + " " + item.getSenderLastName());
        holder.setMessageBody(item.getSubject());
        holder.bind(itemsList.get(position), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.itemsList.size();
    }

    public List<Notification> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<Notification> itemsList) {
        this.itemsList = itemsList;
    }

    public void updateDataset(List<Notification> newList){
        this.itemsList.clear();
        this.itemsList.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification item);
    }

    public class TrashViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llReadIndicator;
        private TextView tvReceiveTimeStamp ;
        private TextView tvSenderName ;
        private TextView tvMessageBody ;
        private ImageView ivOverflow;

        public TrashViewHolder(View itemView) {
            super(itemView);
            this.llReadIndicator = (LinearLayout) itemView.findViewById(R.id.llReadIndicator);
            this.tvReceiveTimeStamp = (TextView) itemView.findViewById(R.id.tvReceiveTimeStamp);
            this.tvSenderName = (TextView) itemView.findViewById(R.id.tvSenderName);
            this.tvMessageBody = (TextView) itemView.findViewById(R.id.tvMessageBody);
            this.ivOverflow = (ImageView) itemView.findViewById(R.id.ivOverflow);
        }

        public void setReadIndicator(int visibility) {
            this.llReadIndicator.setVisibility(visibility);
        }

        public void setReceiveTimeStamp(String itemTimeStamp) {
            this.tvReceiveTimeStamp.setText(itemTimeStamp);
        }

        public void setSenderName(String itemName) {
            this.tvSenderName.setText(itemName);
        }

        public void setMessageBody(String tvMessageBody) {
            this.tvMessageBody.setText(tvMessageBody);
        }

        public void setOptions(ImageView itemOptions) {
            this.ivOverflow = itemOptions;
        }

        public void bind(final Notification item, final OnNotificationClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onNotificationClick(item);
                }
            });
            ivOverflow.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onNotificationClick(item);
                }
            });
        }

    }

}
