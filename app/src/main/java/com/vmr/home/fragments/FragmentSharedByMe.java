package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrItem;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentSharedByMe extends Fragment
        implements
        VmrRequest.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener {

    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private ProgressDialog progressDialog ;

    // Controllers
    private HomeController homeController;

    // Variables
    private VmrFolder head;
    private List<VmrItem> mFileList = new ArrayList<>();
    private RecordsAdapter mAdapter;

    public FragmentSharedByMe() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Map<String, String> formData = VMR.getUserMap();
//        formData.put(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE,VMR.getVmrRootFolder().getSharedFolder());
        formData.remove(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_SHARED_BY_ME);
        formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
        formData.put(Constants.Request.FormFields.LOGGEDIN_USER_ID, VMR.getUserInfo().getLoggedinUserId());
        homeController.fetchAllFilesAndFolders(formData);
        progressDialog.show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction("Shared By Me");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_records, container, false);
        homeController = new HomeController(this);
        mAdapter = new RecordsAdapter(mFileList, this, this);

        setupRecyclerView(view);
        setOnBackPress(view);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The File...");
        progressDialog.setCancelable(true);

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
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        progressDialog.dismiss();
        VmrDebug.printLine("My Records retrieved.");
        if (head==null) {
            VMR.setVmrSharedWithMeRootFolder(vmrFolder);
            head = VMR.getVmrSharedWithMeRootFolder();
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList = head.getAll();
            mAdapter.updateDataset(mFileList);
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
        progressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrItem item) {
        if(item instanceof VmrFolder){
            VmrDebug.printLine(item.getName() + "Folder clicked");
            head = (VmrFolder) item;
            Map<String, String> formData = VMR.getUserMap();
            formData.put(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE, item.getNodeRef());
            formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_ALL_FILE_FOLDER);
            formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
            homeController.fetchAllFilesAndFolders(formData);
        } else if(item instanceof VmrFile) {
            VmrDebug.printLine(item.getName() + "File clicked");
        }
    }

    @Override
    public void onItemOptionsClick(VmrItem item, View view) {
        Toast.makeText(VMR.getVMRContext(), item.getName() + " options clicked.", Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    if(head !=  VMR.getVmrSharedWithMeRootFolder()) {
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

    private void setupRecyclerView(View view) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvMyRecords);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }
}
