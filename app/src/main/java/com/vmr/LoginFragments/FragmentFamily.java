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

import com.vmr.AsyncTasksForRequests.LoginFamily;
import com.vmr.R;
import com.vmr.Utilities.JsonParserForLogin;

import java.util.concurrent.ExecutionException;


public class FragmentFamily extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_family, container, false);

        final EditText etUsername = (EditText) rootView.findViewById(R.id.etFamilyUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etFamilyPassword);
        final EditText etFamilyId = (EditText) rootView.findViewById(R.id.etFamilyID);
        CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbFamilyRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnFamilySignIn);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginFamily loginFamily = new LoginFamily();
                String response = null;
                try {
//                    response = loginFamily.execute("admin","Qwer!234","familyid").get();
                    response = loginFamily
                                .execute(etUsername.getText().toString(),
                                        etPassword.getText().toString(),
                                        etFamilyId.getText().toString())
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
