package com.vmr.home.fragments;

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
import com.vmr.db.DbManager;
import com.vmr.db.shared.SharedRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.SharedByMeAdapter;
import com.vmr.home.bottomsheet_behaviors.SharedOptionsMenuSheet;
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
        SharedOptionsMenuSheet.OnOptionClickListener
{

    // Fragment interaction listener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private SharedOptionsMenuSheet optionsMenuSheet;

    // Controllers
    private HomeController homeController;
    private DbManager dbManager;

    // Variables
    private List<SharedRecord> sharedRecords = new ArrayList<>();
    private SharedByMeAdapter sharedByMeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeController = new HomeController(this);
        sharedByMeAdapter = new SharedByMeAdapter(sharedRecords, this, this);

        optionsMenuSheet = new SharedOptionsMenuSheet();
        optionsMenuSheet.setOptionClickListener(this);

        dbManager = ((HomeActivity) getActivity()).getDbManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_BY_ME);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_shared_by_me, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        mSwipeRefreshLayout.setRefreshing(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeController.fetchSharedByMe();
        sharedRecords = dbManager.getAllSharedByMe();
        sharedByMeAdapter.updateDataset(sharedRecords);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems) {
        VmrDebug.printLogI(this.getClass(), "My Records retrieved.");

        dbManager.updateAllSharedByMe(SharedRecord.getSharedRecordsList(vmrSharedItems, "NA"));
        sharedRecords = dbManager.getAllSharedByMe();
        sharedByMeAdapter = new SharedByMeAdapter(sharedRecords, this, this);
        mRecyclerView.setAdapter(sharedByMeAdapter);

        mSwipeRefreshLayout.setRefreshing(false);

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
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(SharedRecord record) {
        VmrDebug.printLine(record.getRecordName() + " clicked");
    }

    @Override
    public void onItemOptionsClick(SharedRecord record, View view) {
        VmrDebug.printLine( record.getRecordName() + " options clicked.");
        optionsMenuSheet.setRecord(record);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    @Override
    public void onOpenClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " open clicked.");
    }

    @Override
    public void onDownloadClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " download clicked.");
    }

    @Override
    public void onRevokeAccessClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " revoke access clicked.");
    }

    @Override
    public void onPropertiesClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " properties clicked.");
    }

    @Override
    public void onMoveToTrashClicked(SharedRecord record) {
        VmrDebug.printLine( record.getRecordName() + " trash clicked.");
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLine( "Options dismissed");
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
                mSwipeRefreshLayout.setRefreshing(true);
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
