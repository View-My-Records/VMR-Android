package com.vmr.home.fragments;

import android.os.Bundle;
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
import com.vmr.db.DbManager;
import com.vmr.db.recently_accessed.Recent;
import com.vmr.home.HomeActivity;
import com.vmr.home.adapters.RecentAdapter;
import com.vmr.home.context_menu.TrashOptionsMenu;
import com.vmr.home.controller.HomeController;
import com.vmr.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class FragmentRecentlyAccessed extends Fragment
        implements
        RecentAdapter.OnItemClickListener,
        RecentAdapter.OnItemOptionsClickListener
{

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
    private List<Recent> recentRecords = new ArrayList<>();
    private RecentAdapter recentAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        recentAdapter = new RecentAdapter(recentRecords, this, this);

        dbManager = ((HomeActivity) getActivity()).getDbManager();

//        optionsMenuSheet = new TrashOptionsMenu();
//        optionsMenuSheet.setOptionClickListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (fragmentInteractionListener == null) {
            fragmentInteractionListener = (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.RECENTLY_ACCESSED);
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.home_fragment_recently_accessed, container, false);

        setupRecyclerView(fragmentView);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        recentRecords = dbManager.getAllRecentlyAccsseed();
        recentAdapter.updateDataset(recentRecords);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    @Override
    public void onItemClick(Recent item) {

    }

    @Override
    public void onItemOptionsClick(Recent item, View view) {

    }

    private void setupRecyclerView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mTextView = (TextView) view.findViewById(R.id.tvEmptyFolder);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvRecent);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recentRecords = dbManager.getAllRecentlyAccsseed();
                recentAdapter.updateDataset(recentRecords);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recentAdapter);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
