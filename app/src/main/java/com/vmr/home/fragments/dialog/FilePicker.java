package com.vmr.home.fragments.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.debug.VmrDebug;
import com.vmr.home.adapters.LocalStorageFileAdapter;
import com.vmr.network.VmrRequestQueue;
import com.vmr.utils.Constants;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/*
 * Created by abhijit on 9/12/16.
 */

public class FilePicker extends DialogFragment {

    private final String ROOT_PATH = Environment.getExternalStorageDirectory().getPath();

    private LocalStorageFileAdapter mAdapter;
    private List<File> mFileList =  new ArrayList<>();
    private Stack<String> stack =  new Stack<>();

    private OnFilePickedListener onFilePickedListener;

    private ListView mListView;
    private Button cancelButton;
    private SearchView searchView ;
    private ImageButton homeButton ;
    private TextView textView ;

    public FilePicker() {

    }

    public void setOnFilePickedListener(OnFilePickedListener onFilePickedListener) {
        this.onFilePickedListener = onFilePickedListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stack.push(ROOT_PATH);
        mFileList = getFileList(stack.peek());
        mAdapter = new LocalStorageFileAdapter(getActivity(), mFileList);

        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = android.R.style.Theme_Holo_Light;
        setStyle(style, theme);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View dialogView = View.inflate(getActivity(), R.layout.dialog_fragment_filepicker, null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle("File Picker")
                    .setView(dialogView)
                    .setCancelable(true)
                    .create();

        mListView = (ListView) dialogView.findViewById(R.id.lvFilePicker);
        cancelButton = (Button) dialogView.findViewById(R.id.btnCancelButton);
        searchView = (SearchView) dialogView.findViewById(R.id.svFilePicker);
        homeButton = (ImageButton) dialogView.findViewById(R.id.btnHome);
        textView = (TextView) dialogView.findViewById(R.id.tvEmptyFolder);

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File f = mFileList.get(i);
                if(f.isDirectory()){
                    VmrDebug.printLogI(FilePicker.this.getClass(), f.getName() + " clicked.");
                    stack.push(f.getPath());
                    updateAdapter();
                } else {
                    VmrDebug.printLogI(FilePicker.this.getClass(), f.getName() + " chosen.");
                    onFilePickedListener.onFilePicked(f);
                    getDialog().dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stack.removeAllElements();
                stack.push(ROOT_PATH);
                updateAdapter();
            }
        });

        setOnBackPress(dialogView);
        return dialog;
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
                    if (!stack.peek().equals(ROOT_PATH)) {
                        stack.pop();
                        updateAdapter();
                        return true;
                    } else {
                        getDialog().dismiss();
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    private void updateAdapter(){
        mFileList = getFileList(stack.peek());
        if(mFileList.isEmpty()){
            textView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
        mAdapter.updateDateset(mFileList);
    }

    private List<File> getFileList(String path){
        File f = new File(path);
        ArrayList<File> inFiles = new ArrayList<>();

        File[] files = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        });

        File[] dirs = f.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });

        Collections.addAll(inFiles, dirs);
        Collections.sort(inFiles);
        Collections.addAll(inFiles, files);

        return inFiles;
    }

    public interface OnFilePickedListener {
        void onFilePicked(File file);
    }
}
