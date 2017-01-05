package com.vmr.screen.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.SearchResult;
import com.vmr.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 8/25/16.
 */
public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.SearchResultViewHolder>{

    private final OnItemClickListener itemClickListener;
//    private final OnItemOptionsClickListener optionsClickListener;
    private List<SearchResult> searchResults = new ArrayList<>();

    public ResultAdapter(List<SearchResult> searchResults, OnItemClickListener itemClickListener ) {
        this.searchResults = searchResults;
        this.itemClickListener = itemClickListener;
//        this.optionsClickListener = optionsClickListener;
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout_result, parent, false);

        return new SearchResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SearchResultViewHolder holder, int position) {
        SearchResult result = this.searchResults.get(position);

        holder.setItemName(result.getRecordName());
        holder.setItemTimeStamp(DateUtils.getRelativeTimeSpanString(result.getCreated().getTime()).toString());
        holder.setItemDocType(result.getDoctype());

        String recordName = result.getRecordName();

        if(result.getRecordName().contains(".")) {
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

        holder.bind(result, itemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.searchResults.size();
    }

    public void updateDataset(List<SearchResult> newList){
        this.searchResults.clear();
        this.searchResults.addAll(newList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(SearchResult result);
    }

    public interface OnItemOptionsClickListener {
        void onItemOptionsClick(SearchResult result, View view);
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private TextView itemTimeStamp ;
        private TextView itemDocType ;

        SearchResultViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemDocType = (TextView) itemView.findViewById(R.id.tvDocType);
        }

        void setItemIcon(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        void setItemDocType(String itemDocType) {
            this.itemDocType.setText(itemDocType);
        }

//        public void setItemOptions(ImageView itemOptions) {
//            this.itemOptions = itemOptions;
//        }

        void bind(final SearchResult item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

//        public void bind(final Recent item, final OnItemOptionsClickListener listener) {
//            itemOptions.setOnClickListener(new View.OnClickListener() {
//                @Override public void onClick(View v) {
//                    listener.onItemOptionsClick(item, v);
//                }
//            });
//        }

    }

}
