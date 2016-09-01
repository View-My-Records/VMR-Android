package com.vmr.home.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.VMR;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeController;
import com.vmr.home.adapters.SharedByMeAdapter;
import com.vmr.home.interfaces.VmrRequest;
import com.vmr.model.folder_structure.VmrSharedItem;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FragmentSharedByMe extends Fragment
        implements
        VmrRequest.OnFetchSharedByMeListener,
        SharedByMeAdapter.OnItemClickListener,
        SharedByMeAdapter.OnItemOptionsClickListener {

    private OnFragmentInteractionListener fragmentInteractionListener;

    // Views
    private ProgressDialog progressDialog ;

    // Controllers
    private HomeController homeController;

    // Variables
    private List<VmrSharedItem> mFileList = new ArrayList<>();
    private SharedByMeAdapter mAdapter;

    public FragmentSharedByMe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener != null) {
            fragmentInteractionListener.onFragmentInteraction("Shared By Me");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shared_by_me, container, false);
        homeController = new HomeController(this);
        mAdapter = new SharedByMeAdapter(mFileList, this, this);

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
        Map<String, String> formData = VMR.getUserMap();
        formData.remove(Constants.Request.FormFields.ALFRESCO_NODE_REFERENCE);
        formData.put(Constants.Request.FormFields.PAGE_MODE,Constants.PageMode.LIST_SHARED_BY_ME);
        formData.put(Constants.Request.FormFields.LOGGEDIN_USER_ID, VMR.getUserInfo().getLoggedinUserId());
        formData.put(Constants.Request.FormFields.ALFRESCO_TICKET, PrefUtils.getSharedPreference(getActivity().getBaseContext(), PrefConstants.VMR_ALFRESCO_TICKET));
        homeController.removeExpiredRecords();
        homeController.fetchSharedByMe(formData);
        progressDialog.show();
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
    public void onFetchSharedByMeSuccess(List<VmrSharedItem> vmrSharedItems) {
        progressDialog.dismiss();
        VmrDebug.printLine("My Records retrieved.");
        mAdapter.updateDataset(vmrSharedItems);
    }

    @Override
    public void onFetchSharedByMeFailure(VolleyError error) {
        progressDialog.dismiss();
        Toast.makeText(VMR.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(VmrSharedItem item) {
        VmrDebug.printLine(item.getFileName() + " clicked");
    }

    @Override
    public void onItemOptionsClick(VmrSharedItem item, View view) {
        Toast.makeText(VMR.getVMRContext(), item.getFileName() + " options clicked.", Toast.LENGTH_SHORT).show();
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
                    return false;
                }
                return false;
            }
        });
    }

    private void setupRecyclerView(View view) {
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSharedByMe);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }
}
