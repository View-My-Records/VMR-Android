package com.vmr.home.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmr.R;
import com.vmr.utils.Constants;


public class FragmentRecentlyAccessed extends Fragment {

    private OnFragmentInteractionListener fragmentInteractionListener;

    public FragmentRecentlyAccessed() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if ( fragmentInteractionListener== null) {
            fragmentInteractionListener= (OnFragmentInteractionListener) getActivity();
        }
        fragmentInteractionListener.onFragmentInteraction(Constants.Fragment.RECENTLY_ACCESSED);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_recently_accessed, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentInteractionListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
