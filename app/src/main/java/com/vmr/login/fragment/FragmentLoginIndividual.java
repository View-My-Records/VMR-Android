package com.vmr.login.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.user.DbUser;
import com.vmr.login.interfaces.OnLoginClickListener;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.Constants;
import com.vmr.utils.PermissionHandler;

import java.util.ArrayList;
import java.util.List;

public class FragmentLoginIndividual extends Fragment {

    private OnLoginClickListener onLoginClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.login_fragment_individual, container, false);

        final AutoCompleteTextView etUsername = (AutoCompleteTextView) rootView.findViewById(R.id.etIndividualUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etIndividualPassword);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbIndividualRememberPassword);
        Button buttonSignIn = (Button) rootView.findViewById(R.id.btnIndividualSignIn);

        List<DbUser> userList = Vmr.getDbManager().getAllIndividualUsers();

        ArrayList<String> users =  new ArrayList<>();

        for (DbUser user : userList) {
            users.add(user.getUserId());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, users);

        etUsername.setAdapter(adapter);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
                    if (ConnectionDetector.isOnline()) {
                        onLoginClickListener.onIndividualLoginClick(
                                etUsername.getText().toString(),
                                etPassword.getText().toString(),
                                Constants.Request.Login.Domain.INDIVIDUAL,
                                cbRememberMe.isChecked());
                    } else {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), getResources().getString(R.string.toast_internet_unavailable), Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(view.findViewById(android.R.id.content), "Internet access is required to connect to ViewMyRecords server.", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PermissionHandler.requestPermission(getActivity(),Manifest.permission.INTERNET, 1);
                                }
                            })
                            .show();
                }
            }
        });

        etUsername.setSelection(etUsername.getText().length());
        return rootView;
    }

    public void setCallbackInterface(OnLoginClickListener onLoginClickListener){
        this.onLoginClickListener = onLoginClickListener;
    }



}
