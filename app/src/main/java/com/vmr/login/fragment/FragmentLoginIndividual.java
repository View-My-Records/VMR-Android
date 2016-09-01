package com.vmr.login.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vmr.R;
import com.vmr.login.interfaces.LoginFragmentInterface;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;

public class FragmentLoginIndividual extends Fragment {

    private LoginFragmentInterface loginFragmentInterface ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_individual, container, false);
        final EditText etUsername = (EditText) rootView.findViewById(R.id.etIndividualUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etIndividualPassword);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbIndividualRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnIndividualSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionDetector.isOnline()) {
                    loginFragmentInterface.onIndividualLoginClick(
                            etUsername.getText().toString(),
                            etPassword.getText().toString(),
                            Constants.Request.Domain.INDIVIDUAL,
                            cbRememberMe.isChecked());
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Internet not available", Snackbar.LENGTH_SHORT ).show();
                }
            }
        });
        return rootView;
    }

    public void setCallbackInterface(LoginFragmentInterface loginFragmentInterface){
        this.loginFragmentInterface = loginFragmentInterface;
    }



}
