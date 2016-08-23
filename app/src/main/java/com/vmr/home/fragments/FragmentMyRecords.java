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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.adapters.FileFolderListAdapter;
import com.vmr.app.VMR;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.interfaces.MyRecordsRequestInterface;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrNode;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentMyRecords extends Fragment
        implements MyRecordsRequestInterface {

    private OnFragmentInteractionListener fragmentInteractionListener;
    private FloatingActionButton fab;
    private BottomSheetBehavior behavior;
    private HomeController homeController;
    private ListView mListView;

//    private VmrFolder root;
    private VmrFolder head;
    private List<VmrNode> mFileList;
    private FileFolderListAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction("My Records");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_records, container, false);
        HomeActivity homeActivity = (HomeActivity) this.getActivity();
        Map<String, String> formData = VMR.getUserMap();
        formData.put("alfNoderef",homeActivity.getUserInfo().getRootNodref());
        formData.put("pageMode",Constants.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put("alf_ticket",PrefUtils.getSharedPreference(homeActivity.getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        mListView = (ListView) view.findViewById(R.id.lvMyRecords);

        // Get the views from the fragment layout
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.bottom_sheet_layout);
        final FrameLayout bottomSheet = (FrameLayout) coordinatorLayout.findViewById(R.id.bottom_sheet_frame);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(150);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

//        if(VMR.getRootVmrFolder() != null) {
            head = VMR.getRootVmrFolder();
//            mFileList = new ArrayList<>();
//        }

        homeController = new HomeController(this);
        homeController.fetchAllFilesAndFolders(formData);

//        // Set adapter for the ListView
//        mAdapter = new FileFolderListAdapter(getActivity(), mFileList);
//        mListView.setAdapter(mAdapter);


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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mFileList.get(i) instanceof VmrFolder){
                    Toast.makeText(getActivity(), "Folder clicked", Toast.LENGTH_SHORT).show();
                    head=(VmrFolder) mFileList.get(i);
                    Map<String, String> formData = VMR.getUserMap();
                    formData.put("alfNoderef", mFileList.get(i).getNodeRef());
                    formData.put("pageMode",Constants.PageMode.LIST_ALL_FILE_FOLDER);
                    formData.put("alf_ticket",PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));

                    homeController.fetchAllFilesAndFolders(formData);
                } else if(mFileList.get(i) instanceof VmrFile){
                    Toast.makeText(getActivity(), "File clicked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK&& keyEvent.getAction() ==KeyEvent.ACTION_UP){
                    if(head !=  VMR.getRootVmrFolder()) {
                        Toast.makeText(getActivity(), "Back pressed", Toast.LENGTH_SHORT).show();
                        head = (VmrFolder) head.getParent();
                        mFileList=head.getAll();
                        mAdapter.updateDataset(mFileList);

                        return true;
                    } else {

                        return false;
                    }
                } else {
                    return false;
                }
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

    @Override
    public void fetchFilesAndFoldersSuccess(VmrFolder vmrFolder) {
        Toast.makeText(VMR.getVMRContext(), "My Records retrieved.", Toast.LENGTH_SHORT).show();
        System.out.println(vmrFolder.toString());
//        if (VMR.getRootVmrFolder() == null) {
//            // Set adapter for the ListView
//            VMR.setRootVmrFolder(vmrFolder);
//            head = VMR.getRootVmrFolder();
//            mFileList.clear();
//            mFileList.addAll(head.getAll());
////            mAdapter = new FileFolderListAdapter(getActivity(), mFileList);
////            mListView.setAdapter(mAdapter);
//            mAdapter.updateDataset(mFileList);
//        } else {
//            head.addFolder(vmrFolder);
//            head = vmrFolder;
//            mFileList.clear();
//            mFileList.addAll(head.getAll());
//            mAdapter.updateDataset(mFileList);
//        }
        if (head==null) {
            // Set adapter for the ListView
            if (VMR.getRootVmrFolder() == null) {
                VMR.setRootVmrFolder(vmrFolder);
            }

            head = vmrFolder;
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList = head.getAll();
            mAdapter = new FileFolderListAdapter(getActivity(), mFileList);
            mListView.setAdapter(mAdapter);

        } else{
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList=head.getAll();
            mAdapter.updateDataset(mFileList);
        }

    }

    @Override
    public void fetchFilesAndFoldersFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

}
