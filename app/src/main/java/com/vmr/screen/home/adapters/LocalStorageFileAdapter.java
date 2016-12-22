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
import com.vmr.debug.VmrDebug;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by abhijit on 9/12/16.
 */

public class LocalStorageFileAdapter extends ArrayAdapter<File> implements Filterable {

    private Context context;
    private int viewResourceId;
    private List<File> originalFileList;
    private List<File> filteredFileList;
    private ItemFilter itemFilter;

    public LocalStorageFileAdapter(Context context, List<File> fileFoldersList) {
        super(context, R.layout.item_layout_device_file , fileFoldersList);
        this.context = context;
        this.viewResourceId = R.layout.item_layout_device_file;
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
        final File fileFolder = originalFileList.get(position);

        if (fileFolder != null) {
            TextView fileName = (TextView) view.findViewById(R.id.tvFileName);
            TextView fileSize = (TextView) view.findViewById(R.id.tvFileSize);
            ImageView fileImage = (ImageView) view.findViewById(R.id.ivFileIcon);

            if(fileName!=null) {
                fileName.setText(fileFolder.getName());
            }

            if(fileSize!=null) {
                if (fileFolder.isDirectory()) {
                    File[] files = fileFolder.listFiles();
                    int numOfFiles = 0;
                    if (files != null) {
                        numOfFiles = files.length;
                    }
                    String temp = numOfFiles + " Items";
                    fileSize.setText(temp);
                } else {
                    DecimalFormat df = new DecimalFormat();
                    df.setMaximumFractionDigits(2);

                    String value;
                    float size = fileFolder.length();
                    if (size >= (1000*1000))
                        value = df.format(size / (1000*1000)) + " Mb";
                    else if(size >= 1000)
                        value = df.format(size / 1000) + " Kb";
                    else
                        value = df.format(size) + " Bytes";

                    fileSize.setText(value);
                }
            }

            if(fileImage != null){
                if (! fileFolder.isDirectory()) {
                    fileImage.setImageResource(R.drawable.ic_file);
                } else {
                    fileImage.setImageResource(R.drawable.ic_folder);
                }
            }
            VmrDebug.printLine(fileFolder.getAbsolutePath());
        }

        return view;
    }

    public int getCount() {
        return filteredFileList.size();
    }

    public File getItem(int position) {
        return filteredFileList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    public Filter getFilter() {
        return itemFilter;
    }

    public void updateDateset(List<File> items){
        this.originalFileList.clear();
        this.originalFileList.addAll(items);
        this.notifyDataSetChanged();
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<File> list = originalFileList;

            int count = list.size();
            final ArrayList<File> nlist = new ArrayList<File>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
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
            filteredFileList = (ArrayList<File>) results.values;
            notifyDataSetChanged();
        }

    }
}
