package com.vmr.home.fragments.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.controller.RecordExpiryController;
import com.vmr.home.controller.ShareRecordController;
import com.vmr.utils.ErrorMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Created by abhijit on 9/2/16.
 */
public class ShareDialog extends DialogFragment
        implements
        Toolbar.OnMenuItemClickListener,
        DateTimePickerDialog.VmrDateTimePicker {

    private static final String NODE_REF = "NODE_REF";
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); //"31/10/2016 08:00:00"
    TextView tvInvisibleError;
    private String recordNodeRef;
    private Date shareExpiryDate;
    private Spinner spPermissions;
    private EditText etSubject;
    private EditText etShareWith;
    private TextView tvRecordExpiry;
    private TextView btnSetRecordExpiry;

    public static ShareDialog newInstance(Record record) {
        ShareDialog newDialog = new ShareDialog();

        Bundle arguments = new Bundle();
        arguments.putString( NODE_REF, record.getNodeRef());

        newDialog.setArguments(arguments);

        return newDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);

        recordNodeRef = getArguments().getString(NODE_REF);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_fragment_share, container);

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar_share_dialog);
        toolbar.setTitle("Share Records");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.inflateMenu(R.menu.share_menu);
        toolbar.setOnMenuItemClickListener(this);
        setHasOptionsMenu(true);
        setupFormFields(dialogView);

        return dialogView;
    }

    private void setupFormFields(View dialogView) {
        etSubject = (EditText) dialogView.findViewById(R.id.etSubject);
        etShareWith = (EditText) dialogView.findViewById(R.id.etShareWith);
        spPermissions = (Spinner) dialogView.findViewById(R.id.spinnerPermission);
        tvRecordExpiry = (TextView) dialogView.findViewById(R.id.tvRecordExpiry);
        btnSetRecordExpiry = (TextView) dialogView.findViewById(R.id.btnSetDateTime);

        btnSetRecordExpiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(ShareDialog.this);
                dateTimePicker.show(getActivity().getFragmentManager(), "datetimepicker");
            }
        });
    }

    private boolean validate() {

        if(etSubject.getText().toString().isEmpty()){
            etSubject.setError("The subject cannot be empty");
            return false;
        }

        if(etShareWith.getText().toString().isEmpty()){
            etShareWith.setError("Please provide email ids separated with comma(,) to share records");
            return false;
        }

        if (spPermissions.getSelectedItemPosition() == 0) {
            TextView errorText = (TextView)spPermissions.getSelectedView();
            errorText.setTextColor(Color.RED); //just to highlight that this is an error
            errorText.setText("Please select a permission");
            return false;
        }

        if (shareExpiryDate == null || shareExpiryDate.before(new Date(System.currentTimeMillis()))) {
//            new AlertDialog
//                    .Builder(getActivity())
//                    .setMessage("Please select share expiration date.")
//                    .show();
            tvRecordExpiry.setError("Please select share expiration date.");
            return false;
        }

        return true;
    }

    private void shareRecords() {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Processing...");

        String[] emailArray = etShareWith.getText().toString().split(",");
        final StringBuilder emails = new StringBuilder();
        for(String email : emailArray){
            emails.append(email).append(",");
        }

        final ShareRecordController shareRecordController =
                new ShareRecordController(new ShareRecordController.OnShareRecordListener() {
                    @Override
                    public void onShareRecordSuccess(JSONObject jsonObject) {
                        VmrDebug.printLogI(ShareDialog.this.getClass(), "Record shared");
                        VmrDebug.printLogI(ShareDialog.this.getClass(), jsonObject.toString());
                        Toast.makeText(getActivity(), "Records shared successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        getDialog().dismiss();
                    }

                    @Override
                    public void onShareRecordFailure(VolleyError error) {
                        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

        RecordExpiryController recordExpiryController =
                new RecordExpiryController(new RecordExpiryController.OnFetchRecordExpiryListener() {
            @Override
            public void onFetchRecordExpirySuccess(JSONObject jsonObject) {
                VmrDebug.printLogI(ShareDialog.this.getClass(), "Record details");
                progressDialog.dismiss();
                VmrDebug.printLogI(ShareDialog.this.getClass(), jsonObject.toString());
                Date recordExpiryDate = null;
                try {
                    JSONArray properties = jsonObject.getJSONArray("Properties");
                    for(int i = 0; i < properties.length(); i++){
                        JSONObject p = properties.getJSONObject(i);
                        if(p.getString("Name").equals("vmr_expirydate")){
                            DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                            recordExpiryDate = df.parse(p.getString("Value"));
                            break;
                        }
                    }
                    if(ShareDialog.this.shareExpiryDate.before(recordExpiryDate)){
                        DateFormat df = new SimpleDateFormat("dd-MM-yyyy kk:mm:ss ", Locale.ENGLISH);
                        String lifeSpan = df.format(ShareDialog.this.shareExpiryDate);
                        Record record = Vmr.getDbManager().getRecord(recordNodeRef);

                        JSONObject shareJson = new JSONObject();
                        shareJson.put("fileSelectedNodeRef", record.getNodeRef());
                        shareJson.put("toEmail", emails.toString());
                        shareJson.put("shareSubject", etSubject.getText().toString());
                        shareJson.put("permissions", spPermissions.getSelectedItem().toString());
                        shareJson.put("lifeSpan", lifeSpan);
                        shareJson.put("SharedFolderorFilesnames", record.getRecordName());
                        shareJson.put("owner", record.getRecordOwner());

                        shareRecordController.shareRecord(
                                shareJson,
                                1,
                                1,
                                record.getRecordName() );
                    } else {
                        tvRecordExpiry.setError("Expiry date should be before record expiry date");
                    }

                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFetchRecordExpiryFailure(VolleyError error) {
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        recordExpiryController.fetchRecordDetails(recordNodeRef, emails.toString(), recordNodeRef);
        progressDialog.show();
    }

    @Override
    public void onDateTimePicked(Date expiryDate) {
        this.shareExpiryDate = expiryDate;
        String dateString = df.format(expiryDate.getTime());
        tvRecordExpiry.setText(dateString);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        VmrDebug.printLogI(this.getClass(), item.getTitle().toString() + " clicked");

        switch (item.getItemId()){
            case R.id.action_share:
                if(validate()) shareRecords();
                break;
            case R.id.action_reset:

                break;
            case R.id.action_cancel:
                this.dismiss();
                break;
        }
        return false;
    }
}