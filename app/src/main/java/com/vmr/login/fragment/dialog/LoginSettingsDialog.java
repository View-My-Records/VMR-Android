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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.debug.VmrDebug;
import com.vmr.login.LoginController;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import static org.apache.http.HttpStatus.SC_OK;

/*
 * Created by abhijit on 9/22/16.
 */

public class LoginSettingsDialog extends DialogFragment {

    SwitchCompat swOffline;
    SwitchCompat swCustom;
    Spinner spUrlType;
    TextView tvCustomUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        int style = DialogFragment.STYLE_NO_TITLE;
        int theme = android.R.style.Theme_Holo_Light;
        setStyle(style, theme);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_login_settings, null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Settings")
                .setView(dialogView)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();

        setupDialogView(dialogView);
        setupPreferences();

        return dialog;

    }

    private void setupPreferences() {
        if(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE) == null){
            PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.ONLINE);
        } else if(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE).equals(PrefConstants.ApplicationMode.OFFLINE)) {
            swOffline.setChecked(true);
        } else {
            swOffline.setChecked(false);
        }

        if(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.CUSTOM_URL) == null){
            PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.STANDARD);
            PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.URL_TYPE, PrefConstants.URLType.STANDARD);
        } else if(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            swCustom.setChecked(true);
        } else {
            swCustom.setChecked(false);
        }
    }

    private void setupDialogView(View dialogView) {
        swOffline = (SwitchCompat) dialogView.findViewById(R.id.swOfflineMode);
        swCustom = (SwitchCompat) dialogView.findViewById(R.id.swCustomUrl);
        spUrlType = (Spinner) dialogView.findViewById(R.id.spUrlType);
        tvCustomUrl = (TextView) dialogView.findViewById(R.id.tvCustomUrl);

        swOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.OFFLINE);
                } else {
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.ONLINE);
                }
                VmrDebug.printLogI(LoginSettingsDialog.this.getClass(), PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.APPLICATION_MODE));
            }
        });

        swCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.CUSTOM);
                    tvCustomUrl.setText(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                    spUrlType.setVisibility(View.VISIBLE);
                } else {
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.STANDARD);
                    spUrlType.setVisibility(View.GONE);
                    tvCustomUrl.setVisibility(View.GONE);
                    PrefUtils.setSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
                }
                VmrDebug.printLogI(LoginSettingsDialog.this.getClass(), PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
            }
        });

        if (swCustom.isChecked()) {
            spUrlType.setVisibility(View.VISIBLE);
            tvCustomUrl.setVisibility(View.VISIBLE);
        } else {
            spUrlType.setVisibility(View.GONE);
            tvCustomUrl.setVisibility(View.GONE);
        }

        spUrlType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if((getResources().getStringArray(R.array.list_url_types))[position].equals(PrefConstants.URLType.STANDARD)) {
                    tvCustomUrl.setVisibility(View.GONE);
                } else {
                    tvCustomUrl.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvCustomUrl.setVisibility(View.GONE);
            }
        });

        tvCustomUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View promptsView = View.inflate(getActivity(), R.layout.dialog_fragment_custom_url, null);


                final EditText userInput = (EditText) promptsView.findViewById(R.id.etCustomUrl);
                userInput.setText(PrefUtils.getSharedPreference(Vmr.getVMRContext(), PrefConstants.BASE_URL));
                userInput.setSelection(userInput.getText().length());

                final LoginController checkUrlController = new LoginController(new VmrResponseListener.OnCheckUrlResponse() {
                    @Override
                    public void onCheckUrlResponseSuccess(Integer responseCode) {
                        if(responseCode == SC_OK ) {
                            Toast.makeText(getActivity(), "Connection successful", Toast.LENGTH_SHORT).show();
                        } else {
                            userInput.setError("Invalid URL");
                        }
                    }

                    @Override
                    public void onCheckUrlResponseFailure(VolleyError error) {
                        userInput.setError("Invalid URL");
                    }
                });

                final AlertDialog customUrlEditDialog
                        = new AlertDialog.Builder(getActivity())
                        .setView(promptsView)
                        .setTitle("New URL")
                        .setCancelable(false)
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
                                checkUrlController.checkUrl(userInput.getText().toString());
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
                            customUrlEditDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(false);
                        } else {
                            customUrlEditDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            customUrlEditDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setEnabled(true);
                        }
                    }
                });

                customUrlEditDialog.show();
            }
        });

    }
}
