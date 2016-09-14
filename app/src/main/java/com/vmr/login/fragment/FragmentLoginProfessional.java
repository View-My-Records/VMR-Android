package com.vmr.login.fragment;

import android.Manifest;
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
import com.vmr.login.interfaces.OnLoginClickListener;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;
import com.vmr.utils.PermissionHandler;


public class FragmentLoginProfessional extends Fragment {

    private OnLoginClickListener onLoginClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.login_fragment_professional, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.etProfessionalUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etProfessionalPassword);
        final EditText etProfessionalId = (EditText) rootView.findViewById(R.id.etProfessionalID);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbProRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnProfessionalSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
                    if(ConnectionDetector.isOnline()) {
                        onLoginClickListener.onProfessionalLoginClick(
                                etUsername.getText().toString(),
                                etPassword.getText().toString(),
                                etProfessionalId.getText().toString(),
                                Constants.Request.Login.Domain.PROFESSIONAL,
                                cbRememberMe.isChecked());
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.volley_error_internet_not_available , Snackbar.LENGTH_SHORT ).show();
                    }
                } else {
                    Snackbar.make(view.findViewById(android.R.id.content), "Internet access is required to connect to ViewMyRecords server.", Snackbar.LENGTH_SHORT)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PermissionHandler.requestPermission(getActivity(),Manifest.permission.INTERNET);
                                }
                            })
                            .show();
                }

            }
        });

        return rootView;
    }

    public void setCallbackInterface(OnLoginClickListener onLoginClickListener){
        this.onLoginClickListener = onLoginClickListener;
    }

}
