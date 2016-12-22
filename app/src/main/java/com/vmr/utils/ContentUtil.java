package com.vmr.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ContentUtil {

    public static FileInputStream getInputStream(Context context, Uri uri) throws FileNotFoundException {
        if(uri.getScheme().equals("file")) {
            return new FileInputStream(new File(uri.getPath()).getAbsolutePath());
        } else if(uri.getScheme().equals("content")){
            return (FileInputStream) context.getContentResolver().openInputStream(uri);
        }
        return null;
    }

    public static String getFileName(Context context, Uri uri) throws FileNotFoundException {
        if(uri.getScheme().equals("file")) {
            return new File(uri.getPath()).getName();
        } else if(uri.getScheme().equals("content")){
            Cursor returnCursor
                    = context.getContentResolver()
                            .query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            return returnCursor.getString(nameIndex);
        }
        return null;
    }

    public static long getFileSize(Context context, Uri uri) throws FileNotFoundException {
        if(uri.getScheme().equals("file")) {
            return new File(uri.getPath()).length();
        } else if(uri.getScheme().equals("content")){
            Cursor returnCursor
                    = context.getContentResolver()
                    .query(uri, null, null, null, null);

            assert returnCursor != null;
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            return returnCursor.getLong(sizeIndex);
        }
        return 0;
    }
}
