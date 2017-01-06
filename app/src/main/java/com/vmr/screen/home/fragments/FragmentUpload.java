package com.vmr.screen.home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.upload_queue.UploadItem;
import com.vmr.debug.VmrDebug;
import com.vmr.network.controller.request.Constants;
import com.vmr.screen.home.adapters.UploadAdapter;
import com.vmr.service.UploadService;
import com.vmr.utils.ContentUtil;

import java.util.ArrayList;
import java.util.List;


public class FragmentUpload extends Fragment implements UploadAdapter.OnItemClickListener {

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;

    private DbManager dbManager;

    // Variables
    private List<UploadItem> uploadItems = new ArrayList<>();
    private UploadAdapter uploadAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        uploadAdapter = new UploadAdapter(uploadItems,  this);

        dbManager = Vmr.getDbManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.UPLOAD);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_upload, container, false);
        setupRecyclerView(fragmentView);
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateDataset();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvUpload);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateDataset();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(uploadAdapter);
    }

    private void updateDataset(){
        uploadItems = dbManager.getAllUploads();
        uploadAdapter.updateDataset(uploadItems);
        mSwipeRefreshLayout.setRefreshing(false);
        if(uploadItems.size() == 0){
            mTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(UploadItem uploadItem) {

    }

    @Override
    public void onDeleteClick(UploadItem uploadItem, int position) {
        VmrDebug.printLogE(this.getClass(), "Delete : " + uploadItem.getFileName());
    }

    @Override
    public void onRetryClick(final UploadItem uploadItem) {
        VmrDebug.printLogE(this.getClass(), "Retry : " + uploadItem.getFileName());
        try {
            ContentUtil.getFileName(Vmr.getContext(), Uri.parse(uploadItem.getFileUri()));
            dbManager.updateUploadStatus(uploadItem, UploadItem.STATUS_PENDING);
            initiateUpload();
        } catch (Exception e) {
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "File is no longer accessible", Snackbar.LENGTH_SHORT);
            snackbar.setAction("Delete", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dbManager.deleteUpload(uploadItem);
                    updateDataset();
                }
            });
            snackbar.show();
        }
    }

    private void initiateUpload(){
        Intent uploadIntent = new Intent(getActivity(), UploadService.class);
        getActivity().startService(uploadIntent);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
