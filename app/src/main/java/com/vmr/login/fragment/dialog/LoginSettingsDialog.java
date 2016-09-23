package com.vmr.login.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

/*
 * Created by abhijit on 9/22/16.
 */

public class LoginSettingsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_login_settings, null);

        final SwitchCompat swCustomUrl = (SwitchCompat) v.findViewById(R.id.swCustomUrl);
        final TextView tvCustomUrl = (TextView) v.findViewById(R.id.tvCustomUrl);

        if(!PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL).equals(Constants.Url.DEFAULT_BASE_URL)){
            tvCustomUrl.setText(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
            tvCustomUrl.setVisibility(View.VISIBLE);
            swCustomUrl.setChecked(true);
        } else {
            tvCustomUrl.setVisibility(View.GONE);
        }

        AlertDialog loginSettingsDialog =
                new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Settings")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(swCustomUrl.isChecked()) {
                            PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL, tvCustomUrl.getText().toString());
                        } else {
                            PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
                        }
                        VmrDebug.printLogI(LoginSettingsDialog.this.getClass(), PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        VmrDebug.printLogI(LoginSettingsDialog.this.getClass(), PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                    }
                })
                .setCancelable(false)
                .create();

        swCustomUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tvCustomUrl.setText(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                    tvCustomUrl.setVisibility(View.VISIBLE);
                } else {
                    tvCustomUrl.setVisibility(View.GONE);
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
                }
                VmrDebug.printLogI(LoginSettingsDialog.this.getClass(), PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
            }
        });

        tvCustomUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_custom_url, null);


                final EditText userInput = (EditText) promptsView.findViewById(R.id.etCustomUrl);
                userInput.setText(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                userInput.setSelection(userInput.getText().length());

                final AlertDialog customUrlEditDialog
                        = new AlertDialog.Builder(getActivity())
                        .setView(promptsView)
                        .setTitle("New URL")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                tvCustomUrl.setText(userInput.getText().toString());
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setNeutralButton("Check", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "This will test the connection to server", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();

                userInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(!Patterns.WEB_URL.matcher(editable).matches()){
                            customUrlEditDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        } else {
                            customUrlEditDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                        }
                    }
                });

                customUrlEditDialog.show();
            }
        });

        return loginSettingsDialog;

    }
}
