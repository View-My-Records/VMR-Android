package com.vmr.screen.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmr.R;
import com.vmr.network.controller.request.Constants;

public class FragmentAbout extends Fragment {

    private OnFragmentInteractionListener mListener;

    public FragmentAbout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener == null) {
            mListener= (OnFragmentInteractionListener) getActivity();
        }
        mListener.onFragmentInteraction(Constants.Fragment.ABOUT);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.home_fragment_about, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String title);
    }
}
