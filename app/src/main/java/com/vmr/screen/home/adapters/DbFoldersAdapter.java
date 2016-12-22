package com.vmr.screen.home.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/12/16.
 */

public class DbFoldersAdapter extends ArrayAdapter<Record> implements Filterable {

    private Context context;
    private int viewResourceId;
    private List<Record> originalFileList;
    private List<Record> filteredFileList;
    private ItemFilter itemFilter;

    public DbFoldersAdapter(Context context, List<Record> fileFoldersList) {
        super(context, R.layout.item_layout_db_folder , fileFoldersList);
        this.context = context;
        this.viewResourceId = R.layout.item_layout_db_folder;
        this.originalFileList = fileFoldersList;
        this.filteredFileList = fileFoldersList;
        this.itemFilter = new ItemFilter();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view ;

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(viewResourceId, parent, false);
        } else {
            view = convertView;
        }

        /* create a new view of my layout and inflate it in the row */
        final Record fileFolder = originalFileList.get(position);

        if (fileFolder != null) {
            TextView fileName = (TextView) view.findViewById(R.id.tvFileName);
            ImageView fileImage = (ImageView) view.findViewById(R.id.ivFileIcon);

            if(fileName!=null) {
                fileName.setText(fileFolder.getRecordName());
            }

            if(fileImage != null){
                fileImage.setImageResource(R.drawable.ic_folder);
            }
            VmrDebug.printLine(fileFolder.getRecordName());
        }

        return view;
    }

    public int getCount() {
        return filteredFileList.size();
    }

    public Record getItem(int position) {
        return filteredFileList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    public Filter getFilter() {
        return itemFilter;
    }

    public void updateDateset(List<Record> items){
        this.originalFileList.clear();
        this.originalFileList.addAll(items);
        this.notifyDataSetChanged();
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Record> list = originalFileList;

            int count = list.size();
            final ArrayList<Record> nlist = new ArrayList<>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getRecordName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredFileList = (ArrayList<Record>) results.values;
            notifyDataSetChanged();
        }

    }
}
