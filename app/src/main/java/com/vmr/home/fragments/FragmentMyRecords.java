package com.vmr.home.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.interfaces.MyRecordsRequestInterface;
import com.vmr.model.MyRecords;
import com.vmr.model.VMRRecord;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentMyRecords extends Fragment
        implements MyRecordsRequestInterface {

    private List<VMRRecord> mFileList;
    private OnFragmentInteractionListener fragmentInteractionListener;
    private FloatingActionButton fab;
    private BottomSheetBehavior behavior;
    private TextView tempTextView;
    private HomeController homeController;

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
        tempTextView = (TextView) view.findViewById(R.id.tvFileFolderList);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.bottom_sheet_layout);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final FrameLayout bottomSheet = (FrameLayout) coordinatorLayout.findViewById(R.id.bottom_sheet_frame);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(150);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Get file list
        mFileList = getFileList();

        homeController = new HomeController(this);
        HomeActivity homeActivity = (HomeActivity) this.getActivity();
        String test = PrefUtils.getSharedPreference(this.getContext(), PrefConstants.VMR_ALFRESCO_TICKET);
        Map<String, String> formData = VMR.getUserMap();
        formData.put("alfNoderef",homeActivity.getUserInfo().getRootNodref());
        formData.put("pageMode",Constants.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put("alf_ticket",PrefUtils.getSharedPreference(homeActivity.getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));

        homeController.fetchAllFilesAndFolders(formData);

        // Set adapter for the ListView
        //FileFolderListAdapter mAdapter = new FileFolderListAdapter(getActivity(), mFileList);


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

    private List<VMRRecord> getFileList(){
        ArrayList<VMRRecord> inFiles = new ArrayList<>();

        return inFiles;
    }

    @Override
    public void fetchFilesAndFoldersSuccess(MyRecords myRecords) {
        Toast.makeText(VMR.getVMRContext(), "My Records retrieved.", Toast.LENGTH_SHORT).show();
        System.out.println(myRecords.toString());
    }

    @Override
    public void fetchFilesAndFoldersFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }


}
