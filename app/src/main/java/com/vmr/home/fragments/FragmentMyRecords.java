package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeActivity;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.RecordsAdapter;
import com.vmr.home.interfaces.Interaction;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.model.folder_structure.VmrFile;
import com.vmr.model.folder_structure.VmrFolder;
import com.vmr.model.folder_structure.VmrItem;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentMyRecords extends Fragment
        implements
        VmrRequest.OnFetchRecordsListener,
        Interaction.HomeToMyRecordsInterface,
        RecordsAdapter.OnItemClickListener,
        RecordsAdapter.OnItemOptionsClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private FloatingActionButton fab;
    private BottomSheetBehavior behavior;
    private ProgressDialog progressDialog ;
    private View fragmentView;

    // Controllers
    private HomeController homeController;

    // Variables
    private VmrFolder head;
    private List<VmrItem> mFileList = new ArrayList<>();
    private RecordsAdapter mAdapter;

    public FragmentMyRecords() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((HomeActivity) getActivity()).setSendToMyRecords(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // User interface to change the Title in the Activity
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.MY_RECORDS);
        }

        fragmentView = inflater.inflate(R.layout.fragment_my_records, container, false);
        homeController = new HomeController(this);
        mAdapter = new RecordsAdapter(mFileList, this, this);

        setupRecyclerView(fragmentView);
        setupBottomSheet(fragmentView);
        setOnBackPress(fragmentView);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching The File...");
        progressDialog.setCancelable(true);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(VMR.getVmrRootFolder() == null) {
            progressDialog.show();
        } else {
            head = VMR.getVmrRootFolder();
            mFileList = head.getAll();
            mAdapter.updateDataset(mFileList);
            progressDialog.dismiss();
        }
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
            head = vmrFolder;
            head.setFolders(vmrFolder.getFolders());
            head.setIndexedFiles(vmrFolder.getIndexedFiles());
            head.setUnIndexedFiles(vmrFolder.getUnIndexedFiles());
            mFileList = head.getAll();
            mAdapter.updateDataset(mFileList);
        } else if(head.getNodeRef().equals(vmrFolder.getNodeRef())) {
            updateFolder(vmrFolder);
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
    public void onReceiveFromActivitySuccess(VmrFolder vmrFolder) {
        head = vmrFolder;
        mFileList = head.getAll();
        mAdapter.updateDataset(mFileList);
        progressDialog.dismiss();
    }

    @Override
    public void onReceiveFromActivityFailure(VolleyError error) {
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrItem item) {
        if(item instanceof VmrFolder){
            VmrDebug.printLine(item.getName() + "Folder clicked");
            head=(VmrFolder) item;
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        Toast.makeText(VMR.getVMRContext(), "Options menu opened", Toast.LENGTH_SHORT).show();
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.file_overflow_manu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
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
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                VmrDebug.printLine(String.valueOf(newState));
                if ( newState == BottomSheetBehavior.STATE_EXPANDED ) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ImageButton cameraImage = (ImageButton) bottomSheet.findViewById(R.id.ibCamera);
        ImageButton uploadFile = (ImageButton) bottomSheet.findViewById(R.id.ibUpload);
        ImageButton createFolder = (ImageButton) bottomSheet.findViewById(R.id.ibNewFolder);

        createFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bottomSheet.setBackgroundColor(Color.TRANSPARENT);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

                // get prompts.xml view
                View promptsView = View.inflate(getActivity(), R.layout.dialog_create_folder, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView.findViewById(R.id.etNewFolderName);

                // set dialog message
                alertDialogBuilder
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        Map<String, String> formData = VMR.getUserMap();
//                                        formData.remove("pageMode");
                                        formData.put(Constants.Request.FormFields.PAGE_MODE, Constants.PageMode.CREATE_FOLDER);
                                        JSONObject jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put(Constants.Request.FormFields.FOLDER_NAME,userInput.getText().toString());
                                            jsonObject.put(Constants.Request.FormFields.TYPE,1);
                                            jsonObject.put(Constants.Request.FormFields.PARENT_FOLDER, head.getNodeRef());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        formData.put(Constants.Request.FormFields.FOLDER_JSON_OBJECT, jsonObject.toString());
                                        HomeController createFolderController = new HomeController(new VmrRequest.OnCreateFolderListener() {
                                            @Override
                                            public void onCreateFolderSuccess(JSONObject jsonObject) {
                                                try {
                                                    if (jsonObject.has("result") && jsonObject.getString("result").equals("success")) {
                                                        Toast.makeText(VMR.getVMRContext(), "New folder created.", Toast.LENGTH_SHORT).show();
                                                        refreshFolder();
                                                    }
                                                } catch (JSONException e){
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onCreateFolderFailure(VolleyError error) {
                                                Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        createFolderController.createFolder(formData);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
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
                    if(head !=  VMR.getVmrRootFolder()) {
                        head = (VmrFolder) head.getParent();
                        mFileList=head.getAll();
                        mAdapter.updateDataset(mFileList);
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
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvMyRecords);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        registerForContextMenu(mRecyclerView);
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
