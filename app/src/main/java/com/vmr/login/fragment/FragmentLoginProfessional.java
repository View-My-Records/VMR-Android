package com.vmr.login.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vmr.R;
import com.vmr.login.interfaces.LoginFragmentInterface;


public class FragmentLoginProfessional extends Fragment {

    private LoginFragmentInterface loginFragmentInterface ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_professional, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.etProfessionalUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etProfessionalPassword);
        final EditText etProfessionalId = (EditText) rootView.findViewById(R.id.etProfessionalID);
        CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbFamilyRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnProfessionalSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                    response = asyncTaskLoginProfessional.execute("admin","Qwer!234","proid").get();
//                    response = asyncTaskLoginProfessional
//                            .execute(etUsername.getText().toString(),
//                                    etPassword.getText().toString(),
//                                    etProfessionalId.getText().toString())
//                            .get();

                loginFragmentInterface.onProfessionalLoginClick(
                        etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etProfessionalId.getText().toString(),
                        "PROF");

            }
        });

        return rootView;
    }

    public void setCallbackInterface(LoginFragmentInterface loginFragmentInterface){
        this.loginFragmentInterface = loginFragmentInterface;
    }

}
