package com.vmr.screen.home.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.SearchResult;

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
        holder.setItemImage(R.drawable.ic_file);
        holder.bind(result, itemClickListener);
//        holder.bind(searchResults.get(position), optionsClickListener);
    }

    @Override
    public int getItemCount() {
        return this.searchResults.size();
    }

    public List<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchResult> searchResults) {
        this.searchResults = searchResults;
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

    public class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage ;
        private TextView itemName ;
        private TextView itemTimeStamp ;
        private TextView itemDocType ;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            this.itemImage = (ImageView) itemView.findViewById(R.id.ivFileIcon);
            this.itemName = (TextView) itemView.findViewById(R.id.tvFileName);
            this.itemTimeStamp = (TextView) itemView.findViewById(R.id.tvTimeStamp);
            this.itemDocType = (TextView) itemView.findViewById(R.id.tvDocType);
        }

        public void setItemImage(int itemImage) {
            this.itemImage.setImageResource(itemImage);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemTimeStamp(String itemTimeStamp) {
            this.itemTimeStamp.setText(itemTimeStamp);
        }

        public void setItemDocType(String  itemDocType) {
            this.itemDocType.setText(itemDocType);
        }

//        public void setItemOptions(ImageView itemOptions) {
//            this.itemOptions = itemOptions;
//        }

        public void bind(final SearchResult item, final OnItemClickListener listener) {
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
