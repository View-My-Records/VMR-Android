package com.vmr.adapters;/*
 * Created by abhijit on 6/15/16.
 */

import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrNode;

import java.io.File;
import java.util.Date;
import java.util.List;

public class FileFolderListAdapter extends ArrayAdapter<VmrNode> {

    private Context context;
    private int viewResourceId;
    private List<VmrNode> items;
    public PopupMenu popup;

    public FileFolderListAdapter(Context context, List<VmrNode> fileFoldersList) {
        super(context, R.layout.file_folder_layout , fileFoldersList);
        this.context = context;
        this.viewResourceId = R.layout.file_folder_layout;
        this.items = fileFoldersList;
    }

    public VmrNode getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(viewResourceId, parent, false);

        /* create a new view of my layout and inflate it in the row */
        final VmrNode fileFolder = items.get(position);

        TextView fileName = (TextView) convertView.findViewById(R.id.tvFileName);
        TextView fileSize = (TextView) convertView.findViewById(R.id.tvFileSize);
        ImageView fileImage = (ImageView) convertView.findViewById(R.id.ivFileIcon);

        fileName.setText(fileFolder.getName());
//        fileSize.setText(fileFolder.getCreated().toString());

        if (fileFolder instanceof VmrFile) {
            fileImage.setImageResource(R.drawable.ic_file);
        } else if(fileFolder instanceof VmrFolder) {
            fileImage.setImageResource(R.drawable.ic_folder);
        }

        return convertView;
    }

    public void updateDataset(List<VmrNode> newList){
        this.items.clear();
        this.items.addAll(newList);
        notifyDataSetChanged();
    }

}
