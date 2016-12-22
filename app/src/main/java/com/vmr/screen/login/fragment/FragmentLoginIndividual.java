package com.vmr.screen.login.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.vmr.R;
import com.vmr.db.user.DbUser;
import com.vmr.network.controller.request.Constants;
import com.vmr.screen.login.LoginActivity;
import com.vmr.screen.login.LoginPagerAdapter;
import com.vmr.utils.ConnectionDetector;
import com.vmr.utils.PermissionHandler;

import java.util.ArrayList;
import java.util.List;

public class FragmentLoginIndividual extends Fragment {

    private LoginPagerAdapter.OnLoginClickListener onLoginClickListener;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

//        onLoginClickListener.onFragmentChanged(Constants.Request.Login.Domain.INDIVIDUAL);

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.login_fragment_individual, container, false);

        final AutoCompleteTextView etUsername = (AutoCompleteTextView) rootView.findViewById(R.id.etIndividualUsername);
        final EditText etPassword = (EditText) rootView.findViewById(R.id.etIndividualPassword);
        final CheckBox cbRememberMe = (CheckBox) rootView.findViewById(R.id.cbIndividualRememberPassword);
        final Button buttonSignIn = (Button) rootView.findViewById(R.id.btnIndividualSignIn);

        List<DbUser> userList = ((LoginActivity)getActivity()).getDbManager().getAllIndividualUsers();

        ArrayList<String> users =  new ArrayList<>();

        for (DbUser user : userList) {
            users.add(user.getUserId());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, users);

        etUsername.setAdapter(adapter);

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                    buttonSignIn.performClick();
                    return true;
                }
                return false;
            }
        });

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if(PermissionHandler.checkPermission(Manifest.permission.INTERNET)) {
                    if(ConnectionDetector.isOnline()) {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onLoginClickListener.onFragmentChanged(Constants.Request.Login.Domain.INDIVIDUAL);
    }

    public void setCallbackInterface(LoginPagerAdapter.OnLoginClickListener onLoginClickListener){
        this.onLoginClickListener = onLoginClickListener;
    }
}
