package com.vmr.home.adapters;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.DbConstants;

/*
 * Created by abhijit on 9/11/16.
 */

public class SearchSuggestionAdapter extends SimpleCursorAdapter {
    private LayoutInflater mInflater;

    public SearchSuggestionAdapter(Context context, Cursor c) {
        super(context,
                R.layout.item_layout_search,
                c,
                new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1,SearchManager.SUGGEST_COLUMN_ICON_1 },
                new int[]{R.id.tvItemName, R.id.ivFileIcon},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View   view    =    mInflater.inflate(R.layout.item_layout_search, parent, false);
        view.setBackgroundResource(android.R.drawable.menuitem_background);
        ViewHolder holder  =   new ViewHolder();
        holder.tvFileName    =   (TextView)  view.findViewById(R.id.tvItemName);
        holder.ivFileIcon   =   (ImageView)  view.findViewById(R.id.ivFileIcon);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder  =   (ViewHolder)    view.getTag();
        holder.tvFileName.setText(cursor.getString(cursor.getColumnIndex(DbConstants.RECORD_NAME)));
        if(cursor.getString(cursor.getColumnIndex(DbConstants.RECORD_IS_FOLDER)).equals("1"))
            holder.ivFileIcon.setImageResource(R.drawable.ic_folder);
        else holder.ivFileIcon.setImageResource(R.drawable.ic_file);
    }

    private static class ViewHolder {
        TextView tvFileName;
        ImageView ivFileIcon;
    }
}
