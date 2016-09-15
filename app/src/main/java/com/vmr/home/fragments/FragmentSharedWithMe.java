package com.vmr.home.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.DbManager;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.bottomsheet_behaviors.RecordOptionsMenuSheet;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFolder;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FragmentSharedWithMe extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        RecordOptionsMenuSheet.OnOptionClickListener
{

    // Fragment interaction listener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private RecordOptionsMenuSheet recordOptionsMenuSheet;

    // Controllers
    private HomeController homeController;
    private DbManager dbManager;

    // Variables
    private List<Record> records = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    // Stack
    private Stack<String> recordStack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        homeController = new HomeController(this);
        recordsAdapter = new RecordsAdapter(records, this, this);

        recordOptionsMenuSheet = new RecordOptionsMenuSheet();
        recordOptionsMenuSheet.setOptionClickListener(this);

        dbManager = ((HomeActivity) getActivity()).getDbManager();
        recordStack = new Stack<>();
        recordStack.push(Vmr.getVmrRootFolder().getSharedFolder());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_WITH_ME);

        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_shared_with_me, container, false);

        setupRecyclerView(fragmentView);
        setOnBackPress(fragmentView);

        mSwipeRefreshLayout.setRefreshing(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeController.fetchAllFilesAndFolders(recordStack.peek());
        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        VmrDebug.printLogI(this.getClass(), "Records retrieved.");

        dbManager.updateAllRecords(Record.getRecordList(vmrFolder.getAll(), recordStack.peek()));
        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
        recordsAdapter.updateDataset(records);

        mSwipeRefreshLayout.setRefreshing(false);

        if(records.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(Record record) {
        if(record.isFolder()){
            VmrDebug.printLine(record.getRecordName() + " Folder clicked");
            recordStack.push(record.getNodeRef());
            homeController.fetchAllFilesAndFolders(recordStack.peek());
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            VmrDebug.printLine(record.getRecordName() + " File clicked");
        }
    }

    @Override
    public void onItemOptionsClick(Record record, View view) {
        VmrDebug.printLine(record.getRecordName() + " Options clicked");
        recordOptionsMenuSheet.setRecord(record);
        recordOptionsMenuSheet.show(getActivity().getSupportFragmentManager(), recordOptionsMenuSheet.getTag());
    }

    @Override
    public void onOpenClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Open button clicked" );
    }

    @Override
    public void onIndexClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Index button clicked" );
    }

    @Override
    public void onShareClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Share button clicked" );
    }

    @Override
    public void onRenameClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Rename button clicked" );
        View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_rename, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewItemName);
        userInput.setText(record.getRecordName());
        userInput.setSelection(userInput.getText().length());

        // set dialog message
        alertDialogBuilder
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeController renameController = new HomeController(new VmrResponseListener.OnRenameItemListener() {
                            @Override
                            public void onRenameItemSuccess(JSONObject jsonObject) {
                                VmrDebug.printLogI(this.getClass(), jsonObject.toString() );
                                try {
                                    if (jsonObject.has("Response") && jsonObject.getString("Response").equals("success")) {
                                        Toast.makeText(Vmr.getVMRContext(), "vmrItem renamed", Toast.LENGTH_SHORT).show();
                                        refreshFolder();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onRenameItemFailure(VolleyError error) {
                                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                            }

                        });
                        renameController.renameItem(record, userInput.getText().toString());
                    }
                })
            .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                .setTitle("Rename");
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(editable)){
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        alertDialog.show();
    }

    @Override
    public void onDownloadClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Download button clicked" );
    }

    @Override
    public void onMoveClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Move button clicked" );
    }

    @Override
    public void onCopyClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Copy button clicked" );
    }

    @Override
    public void onDuplicateClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Duplicate button clicked" );
    }

    @Override
    public void onPropertiesClicked(Record vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Properties button clicked" );
    }

    @Override
    public void onMoveToTrashClicked(final Record record) {
        VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        final HomeController trashController = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
            @Override
            public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
                VmrDebug.printLogI(this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success"))
                    Toast.makeText(getContext(), dm.getObjectType() + " " + dm.getName() + " deleted" , Toast.LENGTH_SHORT).show();
                    dbManager.moveRecordToTrash(record);
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
            }

        });
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), record.getRecordName() + " moved to Trash",Snackbar.LENGTH_SHORT)
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
                        trashController.moveToTrash(record);
                    }
                });
                snackbar.show();
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
                    if (!recordStack.peek().equals(Vmr.getVmrRootFolder().getSharedFolder())) {
                        recordStack.pop();
                        records = dbManager.getAllSharedWithMeRecords(recordStack.peek());
                        recordsAdapter.updateDataset(records);
                        refreshFolder();
                        mSwipeRefreshLayout.setRefreshing(true);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSharedWithMe);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedWithMe.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recordsAdapter);
    }

    private void refreshFolder(){
        homeController.fetchAllFilesAndFolders(recordStack.peek());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}