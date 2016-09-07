package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.app.FragmentManager;
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
import com.vmr.db.record.Record;
import com.vmr.db.record.RecordManager;
import com.vmr.db.user.UserManager;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.bottomsheet_behaviors.AddItemMenuSheet;
import com.vmr.home.bottomsheet_behaviors.OptionsMenuSheet;
import com.vmr.home.fragments.dialog.IndexDialog;
import com.vmr.home.interfaces.Interaction;
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

public class FragmentMyRecords extends Fragment
        implements
        VmrResponseListener.OnFetchRecordsListener,
        Interaction.HomeToMyRecordsInterface,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener,
        AddItemMenuSheet.OnItemClickListener,
        OptionsMenuSheet.OnOptionClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private FloatingActionButton mFabAddItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView ;
    private TextView mTextView;
    private ProgressDialog mProgressDialog;
    private AddItemMenuSheet addItemMenu;
    private OptionsMenuSheet optionsMenuSheet;

    // Controllers
    private HomeController homeController;
    private UserManager userManager;
    private RecordManager recordManager;

    // Variables
    private VmrFolder currentFolder;
    private List<VmrItem> vmrItems = new ArrayList<>();
    private RecordsAdapter recordsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) getActivity()).setSendToMyRecords(this);

        homeController = new HomeController(this);
        recordsAdapter = new RecordsAdapter(vmrItems, this, this);

        addItemMenu = new AddItemMenuSheet();
        addItemMenu.setItemClickListener(this);

        userManager = ((HomeActivity) getActivity()).getUserManager();
        recordManager = ((HomeActivity) getActivity()).getRecordManager();

        optionsMenuSheet = new OptionsMenuSheet();
        optionsMenuSheet.setOptionClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.MY_RECORDS);

        View fragmentView = inflater.inflate(R.layout.fragment_my_records, container, false);

        setupRecyclerView(fragmentView);
        setupFab(fragmentView);
        setOnBackPress(fragmentView);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Fetching The File...");
        mProgressDialog.setCancelable(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(VMR.getVmrRootFolder() == null) {
            homeController.fetchAllFilesAndFolders(VMR.getLoggedInUserInfo().getRootNodref());
            mProgressDialog.show();
        } else {
            currentFolder = VMR.getVmrRootFolder();
            vmrItems = currentFolder.getAll();
            recordsAdapter.updateDataset(vmrItems);
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onFetchRecordsSuccess(VmrFolder vmrFolder) {
        // TODO: 9/2/16 Combine if conditions. Its all same code in all cases
        mProgressDialog.dismiss();
//        if (currentFolder ==null) {
//            VmrDebug.printLine("My Records retrieved.");
//            currentFolder = vmrFolder;
//            currentFolder.setFolders(vmrFolder.getFolders());
//            currentFolder.setIndexedFiles(vmrFolder.getIndexedFiles());
//            currentFolder.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
//            vmrItems = currentFolder.getAll();
//            recordsAdapter.updateDataset(vmrItems);
//        } else if(currentFolder.getNodeRef().equals(vmrFolder.getNodeRef())) {
//            VmrDebug.printLogI(this.getClass(), currentFolder.getName() + " updated.");

            updateFolder(vmrFolder);
//        } else {
//            VmrDebug.printLogI(this.getClass(), currentFolder.getName() + " retrieved.");
//            currentFolder.setFolders(vmrFolder.getFolders());
//            currentFolder.setIndexedFiles(vmrFolder.getIndexedFiles());
//            currentFolder.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
//            vmrItems = currentFolder.getAll();
//            recordsAdapter.updateDataset(vmrItems);
//        }

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
    public void onReceiveFromActivitySuccess(VmrFolder vmrFolder) {
        currentFolder = vmrFolder;
        vmrItems = currentFolder.getAll();
        recordsAdapter.updateDataset(vmrItems);
        mProgressDialog.dismiss();
    }

    @Override
    public void onReceiveFromActivityFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrItem item) {
        if(item instanceof VmrFolder){
            VmrDebug.printLine(item.getName() + "Folder clicked");
            currentFolder =(VmrFolder) item;
            homeController.fetchAllFilesAndFolders(item.getNodeRef());
            mProgressDialog.show();
        } else if(item instanceof VmrFile) {
            VmrDebug.printLine(item.getName() + "File clicked");
        }
    }

    @Override
    public void onItemOptionsClick(VmrItem item, View view) {
        optionsMenuSheet.setVmrItem(item);
        mFabAddItem.hide();
        optionsMenuSheet.show(getActivity().getSupportFragmentManager(), optionsMenuSheet.getTag());
    }

    @Override
    public void onCameraClick() {
        VmrDebug.printLogI(this.getClass(), "Camera button clicked" );
    }

    @Override
    public void onFileClick() {
        VmrDebug.printLogI(this.getClass(), "Upload file button clicked");
    }

    @Override
    public void onFolderClick() {
        VmrDebug.printLogI(this.getClass(), "create folder button clicked" );
        View promptsView = View.inflate(getActivity(), R.layout.dialog_create_folder, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewFolderName);

        // set dialog message
        alertDialogBuilder
            .setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(userInput.length() == 0) {
                            userInput.setError("Only alphabets, numbers and spaces are allowed");
                        } else {

                            HomeController createFolderController = new HomeController(new VmrResponseListener.OnCreateFolderListener() {
                                @Override
                                public void onCreateFolderSuccess(JSONObject jsonObject) {
                                    try {
                                        if (jsonObject.has("result") && jsonObject.getString("result").equals("success")) {
                                            Toast.makeText(VMR.getVMRContext(), "New folder created.", Toast.LENGTH_SHORT).show();
                                            refreshFolder();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCreateFolderFailure(VolleyError error) {
                                    Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                                }
                            });
                            createFolderController.createFolder( userInput.getText().toString(), currentFolder.getNodeRef() );
                        }
                    }
                })
            .setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
            .setTitle("Create New Folder");
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

        // show it
        alertDialog.show();
            }

    @Override
    public void onAddItemsMenuDismiss() {
        VmrDebug.printLogI(this.getClass(), "Menu dismissed" );
        mFabAddItem.show();
    }

    @Override
    public void onOpenClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Open button clicked" );
    }

    @Override
    public void onIndexClicked(VmrItem vmrItem) {
        VmrDebug.printLogI(this.getClass(), "Index button clicked" );
        FragmentManager fm = getActivity().getFragmentManager();
        IndexDialog indexDialog = new IndexDialog();
        indexDialog.show(fm, "Index");
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
                                        Toast.makeText(VMR.getVMRContext(), "vmrItem renamed", Toast.LENGTH_SHORT).show();
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
        mFabAddItem.show();
    }

    private void setupFab(View view){
        mFabAddItem = (FloatingActionButton) view.findViewById(R.id.fab);
        mFabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFabAddItem.hide();
                addItemMenu.show(getActivity().getSupportFragmentManager(), addItemMenu.getTag());
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
                    VmrRequestQueue.getInstance().cancelPendingRequest(Constants.Request.FolderNavigation.ListAllFileFolder.TAG);
                    if(currentFolder !=  VMR.getVmrRootFolder()) {
                        currentFolder = (VmrFolder) currentFolder.getParent();
                        vmrItems = currentFolder.getAll();
                        recordsAdapter.updateDataset(vmrItems);
                        refreshFolder();
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvMyRecords);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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

    private void updateFolder(VmrFolder vmrFolder){
        currentFolder.setFolders(vmrFolder.getFolders());
        currentFolder.setIndexedFiles(vmrFolder.getIndexedFiles());
        currentFolder.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
        vmrItems = currentFolder.getAll();
        recordManager.updateAllRecords(Record.getRecordList(vmrItems));
        // TODO: 9/5/16 Add code here to update db and return result for current dir

        recordsAdapter.updateDataset(vmrItems);

    }

    private void refreshFolder(){
        homeController.fetchAllFilesAndFolders(currentFolder.getNodeRef());
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
