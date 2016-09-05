package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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
import com.vmr.home.adapters.TrashAdapter;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.model.VmrTrashItem;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import java.util.ArrayList;
import java.util.List;


public class FragmentTrash extends Fragment
        implements
        VmrResponseListener.OnFetchTrashListener,
        TrashAdapter.OnItemClickListener,
        TrashAdapter.OnItemOptionsClickListener
{

    // FragmentInteractionListener
    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private ProgressDialog progressDialog;

    // Controllers
    private HomeController homeController;

    // Variables
    private List<VmrTrashItem> mFileList = new ArrayList<>();
    private TrashAdapter mAdapter;

    public FragmentTrash() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.TRASH);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        homeController = new HomeController(this);
        mAdapter = new TrashAdapter(mFileList, this, this);

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
        homeController.fetchTrash(VMR.getLoggedInUserInfo().getRootNodref());
        progressDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(VmrTrashItem item) {
        if(item.isFolder()){
            VmrDebug.printLine(item.getName() + " Folder clicked");
        } else {
            VmrDebug.printLine(item.getName() + " File clicked");
        }
    }

    @Override
    public void onFetchTrashSuccess( List<VmrTrashItem> vmrTrashItems ) {
        progressDialog.dismiss();
        VmrDebug.printLine("Un-indexed files retrieved.");
        mAdapter.updateDataset(vmrTrashItems);
    }

    @Override
    public void onFetchTrashFailure(VolleyError error) {
        progressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
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
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvTrash);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemOptionsClick(VmrTrashItem item, View view) {
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
}
