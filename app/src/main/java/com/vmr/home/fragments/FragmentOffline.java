package com.vmr.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vmr.R;
import com.vmr.utils.Constants;

public class FragmentOffline extends Fragment {

    private OnFragmentInteractionListener mListener;

    public FragmentOffline() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mListener == null) {
            mListener= (OnFragmentInteractionListener) getActivity();
        }
        mListener.onFragmentInteraction(Constants.Fragment.OFFLINE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offline, container, false);
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
