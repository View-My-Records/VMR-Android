package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.vmr.home.interfaces.Interaction;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrNode;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentMyRecords extends Fragment
        implements
        VmrRequest.onFetchRecordsListener,
        Interaction.HomeToMyRecordsInterface
//        HomeActivityInterface
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;
    // Views
    private FloatingActionButton fab;
    private ListView mListView;
    private BottomSheetBehavior behavior;
    private ProgressDialog progressDialog ;

    // Controllers
    private HomeController homeController;

    // Variables
    private VmrFolder head;
    private List<VmrNode> mFileList = new ArrayList<>();
    private FileFolderListAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction("My Records");
        }

        View view = inflater.inflate(R.layout.fragment_my_records, container, false);
        homeController = new HomeController(this);

        setupListView(view);
        setupBottomSheet(view);
        setOnBackPress(view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The File...");
        progressDialog.setCancelable(true);

        return view;
    }

    private void setupListView(View view) {
        mListView = (ListView) view.findViewById(R.id.lvMyRecords);
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
    }

    @Override
    public void onStart() {
        super.onStart();
        progressDialog.show();
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

        ((HomeActivity) getActivity()).setFragmentCommunicator(this);
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        progressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), "My Records retrieved.", Toast.LENGTH_SHORT).show();
        if (head==null) {
            head = vmrFolder;
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList = head.getAll();
            mAdapter = new FileFolderListAdapter(getActivity(), mFileList);
            mListView.setAdapter(mAdapter);

        } else {
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList=head.getAll();
            mAdapter.updateDataset(mFileList);
        }
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveFromActivitySuccess(VmrFolder vmrFolder) {
        head = vmrFolder;
        mFileList = head.getAll();
        mAdapter = new FileFolderListAdapter(getActivity(), mFileList);
        mListView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }

    @Override
    public void onReceiveFromActivityFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void setupBottomSheet(View view){
        // Get views and set them
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.bottom_sheet_layout);
        final FrameLayout bottomSheet = (FrameLayout) coordinatorLayout.findViewById(R.id.bottom_sheet_frame);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setPeekHeight(150);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

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

        // Set Callback for BottomSheet interaction
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                Log.i("-> BottomSheet Callback", "newState->"+newState);
                if ( newState == BottomSheetBehavior.STATE_EXPANDED ) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    if(head !=  VMR.getRootVmrFolder()) {
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
    }
}
