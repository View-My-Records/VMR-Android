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

public class FragmentLoginCorporate extends Fragment {

    private LoginFragmentInterface loginFragmentInterface ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_corporate, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.etCorpUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etCorpPassword);
        final EditText etCorpId = (EditText) rootView.findViewById(R.id.etCorpID);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbCorpRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnCorpSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionDetector.isOnline()) {
                    loginFragmentInterface.onCorporateLoginClick(
                        etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etCorpId.getText().toString(),
                        Constants.Domain.CORPORATE,
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
