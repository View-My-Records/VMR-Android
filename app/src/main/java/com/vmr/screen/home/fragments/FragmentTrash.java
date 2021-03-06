package com.vmr.screen.home.fragments;

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
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.trash.TrashRecord;
import com.vmr.debug.VmrDebug;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrTrashItem;
import com.vmr.network.VolleySingleton;
import com.vmr.network.controller.HomeController;
import com.vmr.network.controller.VmrResponseListener;
import com.vmr.network.controller.request.Constants;
import com.vmr.screen.home.adapters.TrashAdapter;
import com.vmr.screen.home.context_menu.TrashOptionsMenu;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;


public class FragmentTrash extends Fragment
        implements
        VmrResponseListener.OnFetchTrashListener,
        TrashAdapter.OnItemClickListener,
        TrashAdapter.OnItemOptionsClickListener,
        TrashOptionsMenu.OnOptionClickListener {

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private TrashOptionsMenu optionsMenuSheet;

    // Controllers
    private HomeController homeController;
    private DbManager dbManager;

    // Variables
    private List<TrashRecord> trashRecords = new ArrayList<>();
    private TrashAdapter trashAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        homeController = new HomeController(this);
        trashAdapter = new TrashAdapter(trashRecords, this, this);

        dbManager = Vmr.getDbManager();

        optionsMenuSheet = new TrashOptionsMenu();
        optionsMenuSheet.setOptionClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.TRASH);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_trash, container, false);

        setupRecyclerView(fragmentView);

        mSwipeRefreshLayout.setRefreshing(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeController.fetchTrash();
        trashRecords = dbManager.getAllTrash();
        trashAdapter.updateDataset(trashRecords);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(final TrashRecord record) {
        if(record.isFolder()){
            VmrDebug.printLine(record.getRecordName() + " Folder clicked");
            VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListTrashBin.TAG);
        } else {
            VmrDebug.printLine(record.getRecordName() + " File clicked");
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Can't view deleted files", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFetchTrashSuccess( List<VmrTrashItem> vmrTrashItems ) {
        VmrDebug.printLine("Trash folder retrieved.");

        List<TrashRecord> trashRecords = TrashRecord.getTrashRecordList(vmrTrashItems, PrefUtils.getSharedPreference(PrefConstants.VMR_LOGGED_USER_ROOT_NODE_REF));

        dbManager.updateAllTrash(trashRecords);
        trashRecords = dbManager.getAllTrash();
        trashAdapter.updateDataset(trashRecords);

        mSwipeRefreshLayout.setRefreshing(false);

        if(trashRecords.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchTrashFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvTrash);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VolleySingleton.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListUnIndexed.TAG);
                refreshFolder();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(trashAdapter);
    }

    @Override
    public void onItemOptionsClick(TrashRecord record, View view) {
        VmrDebug.printLine(record.getRecordName() + " options clicked");
        optionsMenuSheet.setRecord(record);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    private void refreshFolder(){
        homeController.fetchTrash();
    }

    @Override
    public void onRestoreClicked(TrashRecord record) {
        VmrDebug.printLine(record.getRecordName() + " restore clicked");
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onPropertiesClicked(TrashRecord record) {
        VmrDebug.printLine(record.getRecordName() + " properties clicked");
        Snackbar.make(getActivity().findViewById(android.R.id.content), "This feature is not available.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClicked(final TrashRecord record) {
        VmrDebug.printLine(record.getRecordName() + " delete clicked");
        VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        final HomeController deleteController = new HomeController(new VmrResponseListener.OnDeleteFromTrashListener() {
            @Override
            public void onDeleteFromTrashSuccess(List<DeleteMessage> deleteMessages) {
                VmrDebug.printLogI(FragmentTrash.this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success"))
                        Toast.makeText(Vmr.getContext(), dm.getObjectType() + " " + dm.getName() + " deleted" , Toast.LENGTH_SHORT).show();
                    dbManager.deleteRecordFromTrash(record);
                }
            }

            @Override
            public void onDeleteFromTrashFailure(VolleyError error) {
                Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                dbManager.deleteRecordFromTrash(record);
                refreshFolder();
            }

        });
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " deleted permanently",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar snackbar1 = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " restored!", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        deleteController.deleteFromTrash(record);
                    }
                });
        snackbar.show();
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLine( "Options dismissed");
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
