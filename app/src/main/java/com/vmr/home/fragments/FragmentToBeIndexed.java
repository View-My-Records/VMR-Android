package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

public class FragmentToBeIndexed extends Fragment
        implements
        VmrRequest.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener onFragmentInteractionListener;

    // Controllers
    private HomeController homeController;

    // Views
    private RecyclerView mRecyclerView;
    private ProgressDialog progressDialog ;

    // Variables
    private VmrFolder head;
    private List<VmrItem> mFileList = new ArrayList<>();
    private RecordsAdapter mAdapter;

    public FragmentToBeIndexed() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            onFragmentInteractionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (onFragmentInteractionListener != null) {
            onFragmentInteractionListener.onFragmentInteraction("To Be Indexed");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_to_be_indexed, container, false);
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
    public void onStart() {
        super.onStart();
        if(VMR.getVmrRootFolder() !=  null){
            head = VMR.getVmrRootFolder();
            mFileList = head.getAllUnindexed();
            mAdapter.updateDataset(mFileList);
        } else {
            Map<String, String> formData = VMR.getUserMap();
            formData.put(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE,VMR.getUserInfo().getRootNodref());
            formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_UN_INDEXED_FILE);
            formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
            homeController.fetchAllFilesAndFolders(formData);
            progressDialog.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onFragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(VmrItem item) {
        if(item instanceof VmrFolder){
            VmrDebug.printLine(item.getName() + " Folder clicked");
            head=(VmrFolder) item;
            Map<String, String> formData = VMR.getUserMap();
            formData.put(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE, item.getNodeRef());
            formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_UN_INDEXED_FILE);
            formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
            homeController.fetchAllFilesAndFolders(formData);
            progressDialog.show();
        } else if(item instanceof VmrFile){
            VmrDebug.printLine(item.getName() + " File clicked");
        }
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        progressDialog.dismiss();
        VmrDebug.printLine("Un-indexed files retrieved.");
        if (head==null) {
            head = vmrFolder;
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList = head.getAllUnindexed();
            mAdapter.updateDataset(mFileList);

        } else {
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList=head.getAllUnindexed();
            mAdapter.updateDataset(mFileList);
        }
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        if(head != null || head != VMR.getVmrRootFolder()) {
            head = (VmrFolder) head.getParent();
        }
        progressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemOptionsClick(VmrItem item, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.inflate(R.menu.file_overflow_manu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(VMR.getVMRContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        popupMenu.show();
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
                    if(head !=  VMR.getVmrRootFolder()) {
                        head = (VmrFolder) head.getParent();
                        mFileList=head.getAllUnindexed();
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvToBeIndexed);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateFolder(VmrFolder vmrFolder){
        head.setFolders(vmrFolder.getFolders());
        head.setIndexedFiles(vmrFolder.getIndexedFiles());
        head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
        mFileList = head.getAll();
        mAdapter.updateDataset(mFileList);
    }

    private void refreshFolder(){
        Map<String, String> formData = VMR.getUserMap();
        formData.put(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE, head.getNodeRef());
        formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_ALL_FILE_FOLDER);
        formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
        homeController.fetchAllFilesAndFolders(formData);
    }
}
