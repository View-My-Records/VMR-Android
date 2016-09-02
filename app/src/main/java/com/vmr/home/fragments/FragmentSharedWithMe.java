package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.bottomsheet_behaviors.OptionsMenuSheet;
import com.vmr.model.DeleteMessage;
import com.vmr.model.VmrFile;
import com.vmr.model.VmrFolder;
import com.vmr.model.VmrItem;
import com.vmr.network.VmrRequestQueue;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentSharedWithMe extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        OptionsMenuSheet.OnOptionClickListener
{

    // FragmentInteractionListener
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
    private VmrFolder currentFolder;
    private List<VmrItem> vmrItems = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        homeController = new HomeController(this);
        recordsAdapter = new RecordsAdapter(vmrItems, this, this);

        optionsMenuSheet = new OptionsMenuSheet();
        optionsMenuSheet.setOptionClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.SHARED_WITH_ME);
        }
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_shared_with_me, container, false);

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
        if(VMR.getVmrRootFolder() != null) {
            homeController.fetchAllFilesAndFolders(VMR.getVmrRootFolder().getSharedFolder());
            mProgressDialog.show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        mProgressDialog.dismiss();
        VmrDebug.printLogI(this.getClass(), "My Records retrieved.");

        if(VMR.getVmrSharedWithMeRootFolder() == null) {
            VMR.setVmrSharedWithMeRootFolder(vmrFolder);
            currentFolder = VMR.getVmrSharedWithMeRootFolder();
            currentFolder.setNodeRef(VMR.getVmrRootFolder().getSharedFolder());
            updateFolder(vmrFolder);
        }

        updateFolder(vmrFolder);

        if(vmrItems.isEmpty()){
            mRecyclerView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onFetchRecordsFailure(VolleyError error) {
        mProgressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrItem item) {
        if(item instanceof VmrFolder){
            VmrDebug.printLogI(item.getName() + "Folder clicked");
            currentFolder = (VmrFolder) item;
            homeController.fetchAllFilesAndFolders(item.getNodeRef());
        } else if(item instanceof VmrFile) {
            VmrDebug.printLogI(item.getName() + "File clicked");
        }
    }

    @Override
    public void onItemOptionsClick(VmrItem item, View view) {
        optionsMenuSheet.setVmrItem(item);
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    private void setOnBackPress(View view){
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP){
                    VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedWithyMe.TAG);
                    if(currentFolder !=  VMR.getVmrSharedWithMeRootFolder()) {
                        currentFolder = (VmrFolder) currentFolder.getParent();
                        vmrItems = currentFolder.getAll();
                        recordsAdapter.updateDataset(vmrItems);
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
                VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListSharedWithyMe.TAG);
                refreshFolder();
                mSwipeRefreshLayout.setRefreshing(false);
                mProgressDialog.show();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recordsAdapter);
    }

    private void updateFolder(VmrFolder vmrFolder) {
        currentFolder.setFolders(vmrFolder.getFolders());
        currentFolder.setIndexedFiles(vmrFolder.getIndexedFiles());
        currentFolder.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
        vmrItems = currentFolder.getAll();
        recordsAdapter.updateDataset(vmrItems);
    }

    private void refreshFolder(){
        homeController.fetchAllFilesAndFolders(currentFolder.getNodeRef());
    }

    @Override
    public void onOpenClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Open button clicked" );
    }

    @Override
    public void onIndexClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Index button clicked" );
    }

    @Override
    public void onShareClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Share button clicked" );
    }

    @Override
    public void onRenameClicked(final VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Rename button clicked" );
        View promptsView = View.inflate(getActivity(), R.layout.dialog_rename_folder, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewItemName);
        userInput.setText(vmrItem.getName());
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
                                                Toast.makeText(VMR.getVMRContext(), "Item renamed", Toast.LENGTH_SHORT).show();
                                                refreshFolder();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onRenameItemFailure(VolleyError error) {
                                        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                                    }

                                });
                                renameController.renameItem(vmrItem, userInput.getText().toString());
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
    public void onDownloadClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Download button clicked" );
    }

    @Override
    public void onMoveClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Move button clicked" );
    }

    @Override
    public void onCopyClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Copy button clicked" );
    }

    @Override
    public void onDuplicateClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Duplicate button clicked" );
    }

    @Override
    public void onPropertiesClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Properties button clicked" );
    }

    @Override
    public void onMoveToTrashClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Delete button clicked" );
        HomeController trashController = new HomeController(new VmrResponseListener.OnMoveToTrashListener() {
            @Override
            public void onMoveToTrashSuccess(List<DeleteMessage> deleteMessages) {
                VmrDebug.printLogI(this.getClass(), deleteMessages.toString() );
                refreshFolder();

                for (DeleteMessage dm : deleteMessages) {
                    if(dm.getStatus().equals("success"))
                        Toast.makeText(getContext(), dm.getObjectType() + " " + dm.getName() + " deleted" , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onMoveToTrashFailure(VolleyError error) {
                Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
            }

        });

        trashController.moveToTrash(vmrItem);
    }

    @Override
    public void onOptionsMenuDismiss() {
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
