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
        CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbCorpRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnCorpSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                response = asyncTaskLoginCorporate.execute("admin", "Qwer!234", "crpid").get();
//                response = asyncTaskLoginCorporate
//                        .execute(etUsername.getText().toString(),
//                                etPassword.getText().toString(),
//                                etCorpId.getText().toString())
//                        .get();

                loginFragmentInterface.onCorporateLoginClick(
                        etUsername.getText().toString(),
                        etPassword.getText().toString(),
                        etCorpId.getText().toString(),
                        "CORP");
            }
        });
        return rootView;
    }

    public void setCallbackInterface(LoginFragmentInterface loginFragmentInterface){
        this.loginFragmentInterface = loginFragmentInterface;
    }




}
