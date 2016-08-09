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

import com.vmr.AsyncTasksForRequests.AsyncTaskLoginFamily;
import com.vmr.AsyncTasksForRequests.AsyncTaskLoginParseJSON;
import com.vmr.R;
import com.vmr.JSONParsers.JSONParserLogin;

import java.util.concurrent.ExecutionException;


public class FragmentLoginFamily extends Fragment {

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
                AsyncTaskLoginFamily asyncTaskLoginFamily = new AsyncTaskLoginFamily();
                AsyncTaskLoginParseJSON asyncTaskLoginParseJSON = new AsyncTaskLoginParseJSON();
                String response, result;
                try {
//                    response = asyncTaskLoginFamily.execute("admin","Qwer!234","familyid").get();
                    response = asyncTaskLoginFamily
                                .execute(etUsername.getText().toString(),
                                        etPassword.getText().toString(),
                                        etFamilyId.getText().toString())
                                .get();
                    result = asyncTaskLoginParseJSON
                            .execute(response)
                            .get();
                    Toast.makeText(getContext(),
                            result,
                            Toast.LENGTH_SHORT)
                            .show();
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
