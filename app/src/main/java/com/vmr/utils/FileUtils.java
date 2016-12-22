package com.vmr.utils;

/*
 * Created by abhijit on 9/14/16.
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {

    final static Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");

    public static String getNewFileName(String fileName, File parent) {
        File newFile = new File(parent, fileName);

        if (newFile.exists()) {
            Matcher m = PATTERN.matcher(fileName);
            if (m.matches()) {
                String prefix = m.group(1);
                String last = m.group(2);
                String suffix = m.group(3);
                if (suffix == null) suffix = "";
                int count = last != null ? Integer.parseInt(last) : 0;
                do {
                    count++;
                    fileName = prefix + "(" + count + ")" + suffix;
                    newFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
                } while (newFile.exists());
            }
        }
        return fileName;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static String getMimeTypeFromExtension(String extension){
        String mimeType;
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType;
    }

    public static String getMimeType(String filePath) {
        String type;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath).toLowerCase();
        if (!extension.equals("")) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } else {
            type = "*/*";
        }
        return type;
    }

    public static String getMimeType(File file) throws IOException {
        String type = file.toURL().openConnection().getContentType();
        if(type != null)
            return type;
        return null;
    }

    public static FileInputStream getInputStream(Context context, Uri uri) throws FileNotFoundException {
//        if(uri.getScheme().equals("file")) {
//            return new FileInputStream(new File(uri.getPath()).getAbsolutePath());
//        } else if(uri.getScheme().equals("content")){
        return (FileInputStream) context.getContentResolver().openInputStream(uri);
//        }
//        return null;
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
