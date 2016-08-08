package com.vmr.LoginFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vmr.AsyncTasksForRequests.LoginCorporate;
import com.vmr.R;
import com.vmr.Utilities.JsonParserForLogin;

import java.util.concurrent.ExecutionException;


public class FragmentCorporate extends Fragment {

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
                LoginCorporate loginCorporate = new LoginCorporate();
                String response;
                try {
//                    response = loginCorporate.execute("admin","Qwer!234","crpid").get();
                    response = loginCorporate
                            .execute(etUsername.getText().toString(),
                                    etPassword.getText().toString(),
                                    etCorpId.getText().toString())
                            .get();
                    JsonParserForLogin jsonParser = new JsonParserForLogin(response);
                    if(jsonParser.isValid()){
                        if(jsonParser.getKey("result").equals("success")) {
                            Toast.makeText(getContext(),
                                    "Login success.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Invalid credentials.",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Invalid JSON.",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Something is wrong.",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        return rootView;
    }




}
