package com.vmr.HomeFragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.vmr.FileFolderListAdapter;
import com.vmr.R;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentMyRecords extends Fragment {

    private List<File> mFileList;
    private OnFragmentInteractionListener fragmentInteractionListener;
    private FloatingActionButton fab;
    private BottomSheetBehavior behavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction("My Records");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_records, container, false);
        // Get the views from the fragment layout
        ListView listView = (ListView) view.findViewById(R.id.lvMyRecords);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.bottom_sheet_layout);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final FrameLayout bottomSheet = (FrameLayout) coordinatorLayout.findViewById(R.id.bottom_sheet_frame);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(150);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Get file list
        mFileList = getFileList();

        // Set adapter for the ListView
        FileFolderListAdapter mAdapter = new FileFolderListAdapter(getActivity(), mFileList);


        // Set Callback for BottomSheet interaction
        // Handle FAB visibility
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.i("-> BottomSheet Callback", "newState->"+newState);
                if ( newState == BottomSheetBehavior.STATE_EXPANDED ) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                bottomSheet.setBackgroundColor(Color.BLACK);
                bottomSheet.getBackground().setAlpha(50);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(new ListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                String text = mFileList.get(i).getName();
                Toast.makeText(getActivity(), text + " clicked.", Toast.LENGTH_SHORT).show();
            }

        });

        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            fragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private List<File> getFileList(){
        File f = new File(Environment.getExternalStorageDirectory().getPath());
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }


}
