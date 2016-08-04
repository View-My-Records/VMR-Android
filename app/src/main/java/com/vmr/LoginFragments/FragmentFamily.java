package com.vmr.LoginFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vmr.R;


public class FragmentFamily extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_family, container, false);

        final EditText etEmail = (EditText) rootView.findViewById(R.id.etFamilyEmail);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etFamilyPassword);
        final EditText etFamilyId = (EditText) rootView.findViewById(R.id.etFamilyID);
        CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbFamilyRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnFamilySignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return rootView;
    }


}
