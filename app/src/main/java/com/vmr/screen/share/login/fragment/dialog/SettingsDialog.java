package com.vmr.screen.share.login.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.debug.VmrDebug;
import com.vmr.network.controller.LoginController;
import com.vmr.network.controller.VmrResponseListener;
import com.vmr.network.controller.request.Constants;
import com.vmr.utils.PrefConstants;
import com.vmr.utils.PrefUtils;

import static org.apache.http.HttpStatus.SC_OK;

/*
 * Created by abhijit on 9/22/16.
 */

public class SettingsDialog extends DialogFragment {

    AlertDialog dialog;

    SwitchCompat swOffline;
    SwitchCompat swCustom;
    Spinner spUrlType;
    EditText etCustomUrl;

    Button buttonPositive;
    Button buttonNegative;
    Button buttonCheck;

    boolean urlValidFlag = false;

    OnDismissListener onDismissListener;

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

        dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Settings")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setNeutralButton("Check", null)
                .create();

        setupDialogView(dialogView);
        setupDialogButtons();

        return dialog;

    }

    private void setupDialogButtons() {
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                buttonPositive = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                buttonNegative = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonCheck    = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEUTRAL);

                final LoginController checkUrlController = new LoginController(new VmrResponseListener.OnCheckUrlResponse() {
                    @Override
                    public void onCheckUrlResponseSuccess(Integer responseCode) {
                        if(responseCode == SC_OK ) {
                            Toast.makeText(getActivity(), "Connection successful", Toast.LENGTH_SHORT).show();
                            urlValidFlag = true;
                            buttonPositive.setEnabled(true);
                        } else {
                            etCustomUrl.setError("Invalid URL");
                            urlValidFlag = false;
                            buttonPositive.setEnabled(false);
                        }
                    }

                    @Override
                    public void onCheckUrlResponseFailure(VolleyError error) {
                        etCustomUrl.setError("Invalid URL");
                        urlValidFlag = false;
                        buttonPositive.setEnabled(false);
                    }
                });

                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(swOffline.isChecked()) {
                            PrefUtils.setSharedPreference( PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.OFFLINE);
                        } else {
                            PrefUtils.setSharedPreference(PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.ONLINE);
                        }

                        if(swCustom.isChecked()) {
                            if (urlValidFlag) {
                                PrefUtils.setSharedPreference(PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.CUSTOM);
                                PrefUtils.setSharedPreference(PrefConstants.URL_TYPE, spUrlType.getSelectedItem().toString());
                                PrefUtils.setSharedPreference(PrefConstants.BASE_URL, etCustomUrl.getText().toString());
                                dialog.dismiss();
                            } else {
                                checkUrlController.checkUrl(etCustomUrl.getText().toString());
                            }
                        } else {
                            PrefUtils.setSharedPreference(PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.STANDARD);
                            PrefUtils.setSharedPreference(PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
                            dialog.dismiss();
                        }
                    }
                });

                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkUrlController.checkUrl(etCustomUrl.getText().toString());
                    }
                });

                setupPreferences();
            }
        });
    }

    private void setupPreferences() {
        if(PrefUtils.getSharedPreference(PrefConstants.APPLICATION_MODE) == null){
            PrefUtils.setSharedPreference(PrefConstants.APPLICATION_MODE, PrefConstants.ApplicationMode.ONLINE);
        } else if(PrefUtils.getSharedPreference(PrefConstants.APPLICATION_MODE).equals(PrefConstants.ApplicationMode.OFFLINE)) {
            swOffline.setChecked(true);
        } else {
            swOffline.setChecked(false);
        }

        if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL) == null){
            PrefUtils.setSharedPreference(PrefConstants.CUSTOM_URL, PrefConstants.CustomUrl.STANDARD);
            PrefUtils.setSharedPreference(PrefConstants.URL_TYPE, PrefConstants.URLType.STANDARD);
            PrefUtils.setSharedPreference(PrefConstants.BASE_URL, Constants.Url.DEFAULT_BASE_URL);
        } else if(PrefUtils.getSharedPreference(PrefConstants.CUSTOM_URL).equals(PrefConstants.CustomUrl.CUSTOM)) {
            swCustom.setChecked(true);
            buttonCheck.setEnabled(true);
        } else {
            swCustom.setChecked(false);
            buttonCheck.setEnabled(false);
        }

        buttonPositive.setEnabled(false);
    }

    private void setupDialogView(View dialogView) {

        swOffline = (SwitchCompat) dialogView.findViewById(R.id.swOfflineMode);
        swCustom = (SwitchCompat) dialogView.findViewById(R.id.swCustomUrl);
        spUrlType = (Spinner) dialogView.findViewById(R.id.spUrlType);
        etCustomUrl = (EditText) dialogView.findViewById(R.id.tvCustomUrl);

        etCustomUrl.setText(PrefUtils.getSharedPreference(PrefConstants.BASE_URL));
        etCustomUrl.setSelection(etCustomUrl.getText().length());

        swOffline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) {
//                    if (PrefUtils.getSharedPreference(Vmr.getContext(), PrefConstants.APPLICATION_MODE)
//                            .equals(PrefConstants.ApplicationMode.OFFLINE)) {
//                        buttonPositive.setEnabled(false);
//                    } else {
//                        buttonPositive.setEnabled(true);
//                    }
//                } else {
//                    if (PrefUtils.getSharedPreference(Vmr.getContext(), PrefConstants.APPLICATION_MODE)
//                            .equals(PrefConstants.ApplicationMode.ONLINE)) {
//                        buttonPositive.setEnabled(false);
//                    } else {
//                        buttonPositive.setEnabled(true);
//                    }
//                }
                buttonPositive.setEnabled(true);
                VmrDebug.printLogI(SettingsDialog.this.getClass(), PrefUtils.getSharedPreference(PrefConstants.APPLICATION_MODE));
            }
        });

        swCustom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
//                    spUrlType.setVisibility(View.VISIBLE);
//                    etCustomUrl.setVisibility(View.VISIBLE);
                    spUrlType.setEnabled(true);
                    spUrlType.setSelection(1);
                    etCustomUrl.setEnabled(true);
                    buttonCheck.setEnabled(true);
                    buttonPositive.setEnabled(false);
                } else {
//                    spUrlType.setVisibility(View.GONE);
//                    etCustomUrl.setVisibility(View.GONE);
                    spUrlType.setEnabled(false);
                    spUrlType.setSelection(0);
                    etCustomUrl.setEnabled(false);
                    buttonCheck.setEnabled(false);
                    buttonPositive.setEnabled(true);
                }

                VmrDebug.printLogI(SettingsDialog.this.getClass(), PrefUtils.getSharedPreference(PrefConstants.BASE_URL));
            }
        });

        if (swCustom.isChecked()) {
            spUrlType.setEnabled(true);
            etCustomUrl.setEnabled(true);
        } else {
            spUrlType.setEnabled(false);
            etCustomUrl.setEnabled(false);
        }

//        spUrlType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (PrefUtils.getSharedPreference(Vmr.getContext(), PrefConstants.URL_TYPE)
//                                        .equals((getResources().getStringArray(R.array.list_url_types))[position])) {
//                    buttonPositive.setEnabled(false);
//                } else {
//                    buttonPositive.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
////                tvCustomUrl.setVisibility(View.GONE);
//            }
//        });

        etCustomUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Patterns.WEB_URL.matcher(editable).matches()){
                    buttonCheck.setEnabled(false);
                } else {
                    buttonCheck.setEnabled(true);
                }
                urlValidFlag = false;
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onDismissListener.onDismiss();
    }

    public void setOnDismissListener(OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

}
