package com.vmr.screen.home.fragments.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.vmr.R;

/**
 * Created by abhijit on 1/5/17.
 */

public class LicencesDialog extends DialogFragment implements View.OnClickListener {

    public static LicencesDialog newInstance() {
        return new LicencesDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_fragment_licences, container);

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar_licences_dialog);
        toolbar.setTitle("Licences");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        setHasOptionsMenu(true);

        dialogView.findViewById(R.id.volley_link).setOnClickListener(this);
        dialogView.findViewById(R.id.firebase_link).setOnClickListener(this);
        dialogView.findViewById(R.id.chip_link).setOnClickListener(this);

        return dialogView;
    }

    @Override
    public void onClick(View view) {
        String url = null;
        switch (view.getId()){
            case R.id.volley_link:
                url = "https://developer.android.com/training/volley/index.html";
                break;
            case R.id.firebase_link:
                url = "https://firebase.google.com/";
                break;
            case R.id.chip_link:
                url = "https://github.com/klinker41/android-chips";
                break;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
