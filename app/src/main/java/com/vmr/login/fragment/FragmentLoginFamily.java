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

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.login.LoginController;
import com.vmr.login.interfaces.LoginFragmentInterface;
import com.vmr.login.interfaces.LoginRequestInterface;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;

import org.json.JSONObject;


public class FragmentLoginFamily extends Fragment {

    private LoginFragmentInterface loginFragmentInterface ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_family, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.etFamilyUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etFamilyPassword);
        final EditText etFamilyId = (EditText) rootView.findViewById(R.id.etFamilyID);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbFamilyRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnFamilySignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ConnectionDetector.isOnline()) {
                    loginFragmentInterface.onFamilyLoginClick(
                        etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etFamilyId.getText().toString(),
                        Constants.Request.Domain.FAMILY,
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
