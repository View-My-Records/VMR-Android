package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.SharedByMeAdapter;
import com.vmr.home.bottomsheet_behaviors.OptionsMenuSheet;
import com.vmr.model.VmrItem;
import com.vmr.model.VmrSharedItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import java.util.ArrayList;
import java.util.List;

public class FragmentSharedByMe extends Fragment
        implements
        VmrResponseListener.OnFetchSharedByMeListener,
        SharedByMeAdapter.OnItemClickListener,
        SharedByMeAdapter.OnItemOptionsClickListener,
        OptionsMenuSheet.OnOptionClickListener
{

    // Fragment interaction listener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Controllers
    private HomeController homeController;

    // Views
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTextView;
    private ProgressDialog mProgressDialog;
    private OptionsMenuSheet optionsMenuSheet;

    // Variables
    private List<VmrSharedItem> vmrSharedItems = new ArrayList<>();
    private SharedByMeAdapter sharedByMeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeController = new HomeController(this);


        optionsMenuSheet = new OptionsMenuSheet();
        optionsMenuSheet.setOptionClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if ( fragmentInteractionListener== null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_BY_ME);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_shared_by_me, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Fetching The File...");
        mProgressDialog.setCancelable(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        homeController.removeExpiredRecords();
        homeController.fetchSharedByMe();
        mProgressDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems) {
        mProgressDialog.dismiss();
        VmrDebug.printLogI(this.getClass(), "My Records retrieved.");
//        sharedByMeAdapter.updateDataset(vmrSharedItems);
        sharedByMeAdapter = new SharedByMeAdapter(vmrSharedItems, this, this);
        mRecyclerView.setAdapter(sharedByMeAdapter);
        if(vmrSharedItems.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchSharedByMeFailure(VolleyError error) {
        mProgressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrSharedItem item) {
        VmrDebug.printLine(item.getName() + " clicked");
    }

    @Override
    public void onItemOptionsClick(VmrSharedItem item, View view) {
        VmrDebug.printLine( item.getName() + " options clicked.");
        optionsMenuSheet.setVmrSharedItem(item);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    @Override
    public void onOpenClicked(VmrItem vmrItem) {

    }

    @Override
    public void onIndexClicked(VmrItem vmrItem) {

    }

    @Override
    public void onShareClicked(VmrItem vmrItem) {

    }

    @Override
    public void onRenameClicked(VmrItem vmrItem) {

    }

    @Override
    public void onDownloadClicked(VmrItem vmrItem) {

    }

    @Override
    public void onMoveClicked(VmrItem vmrItem) {

    }

    @Override
    public void onCopyClicked(VmrItem vmrItem) {

    }

    @Override
    public void onDuplicateClicked(VmrItem vmrItem) {

    }

    @Override
    public void onPropertiesClicked(VmrItem vmrItem) {

    }

    @Override
    public void onMoveToTrashClicked(VmrItem vmrItem) {

    }

    @Override
    public void onOptionsMenuDismiss() {

    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    return false;
                }
                return false;
            }
        });
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSharedByMe);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedByMe.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressDialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void refreshFolder(){
        homeController.removeExpiredRecords();
        homeController.fetchSharedByMe();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
