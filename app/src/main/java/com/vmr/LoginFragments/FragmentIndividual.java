package com.vmr.LoginFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vmr.AsyncTasksForRequests.LoginCorporate;
import com.vmr.HomeActivity;
import com.vmr.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragmentIndividual extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_individual, container, false);
        final EditText etEmail = (EditText) rootView.findViewById(R.id.etIndividualEmail);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etIndividualPassword);
        CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbIndividualRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnIndividualSignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (etEmail.getText().toString().equals("abhijit@vmr.com")
//                        && etPassword.getText().toString().equals("password")) {
//                    Intent homeIntent = new Intent(getContext(), HomeActivity.class);
//                    startActivity(homeIntent);
//                } else {
//                    Toast.makeText(getActivity(), "Invalid Username or Password.", Toast.LENGTH_SHORT).show();
//                }

                Log.e("Login", "Sign in clicked");

                new LoginCorporate().execute("Login Corporate");
            }
        });
        return rootView;
    }

}
