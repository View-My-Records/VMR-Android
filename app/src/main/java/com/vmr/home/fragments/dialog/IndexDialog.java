package com.vmr.home.fragments.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.HomeController;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.Constants;
import com.vmr.utils.ErrorMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by abhijit on 9/2/16.
 */
public class IndexDialog extends DialogFragment
        implements
        VmrResponseListener.OnFetchClassifications,
        AdapterView.OnItemSelectedListener
{

    List<String> classificationsList = new ArrayList<>();
    Map<String, String> classificationsMap =  new HashMap<>();
    ArrayAdapter<String> adapter ;

    private String recordDocType;
    private String recordNodeRef;
    private String recordProgramName;

    private ProgressDialog progressDialog;

    private LinearLayout indexFormLayout;
    private Spinner  spClassification;
    private EditText etQuickReference;
    private Spinner  spLifeSpan;
    private EditText etGeoTag;
    private EditText etRemarks;
    private Spinner  spCategory;
    private EditText etNextAction;
    private EditText etActionMessage;

    private HomeController homeController;

    public IndexDialog() {

    }

    public static IndexDialog newInstance(Record record) {
        IndexDialog newDialog = new IndexDialog();

        Bundle arguments = new Bundle();
        arguments.putString(Constants.Request.FolderNavigation.Properties.FILE_NODE_REF, record.getNodeRef());
        arguments.putString(Constants.Request.FolderNavigation.Properties.PROGRAM_NAME, null);
        newDialog.setArguments(arguments);

        return newDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);

        recordNodeRef = getArguments().getString(Constants.Request.FolderNavigation.Properties.FILE_NODE_REF);
        recordProgramName = getArguments().getString(Constants.Request.FolderNavigation.Properties.PROGRAM_NAME);

        homeController =  new HomeController(this);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, classificationsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_fragment_index, container);

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar_index_dialog);
        toolbar.setTitle("Indexing...");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        setHasOptionsMenu(true);
        setupFormFields(dialogView);
        setupClassifications();

        homeController.fetchClassifications();

        return dialogView;
    }

    private void setupClassifications() {
        classificationsList.add(0,"Classification");

        spClassification.setAdapter(adapter);
        spClassification.setOnItemSelectedListener(this);
    }

    private void setupFormFields(View dialogView) {
        indexFormLayout = (LinearLayout) dialogView.findViewById(R.id.indexFormLayout);
        spClassification = (Spinner) dialogView.findViewById(R.id.spinnerClassification);
        etQuickReference = (EditText) dialogView.findViewById(R.id.etQuickReference);
        spLifeSpan       = (Spinner) dialogView.findViewById(R.id.spinnerLifeSpan);
        etGeoTag         = (EditText) dialogView.findViewById(R.id.etGeoTag);
        etRemarks        = (EditText) dialogView.findViewById(R.id.etRemarks);
        spCategory       = (Spinner) dialogView.findViewById(R.id.spinnerCategory);
        etNextAction     = (EditText) dialogView.findViewById(R.id.etNextAction);
        etActionMessage  = (EditText) dialogView.findViewById(R.id.etActionMessage);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.index_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {

            return true;
        } else if (id == R.id.action_refresh) {

            return true;
        } else if (id == R.id.action_reset) {

            return true;
        } else if (id == android.R.id.home) {

            dismiss();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFetchClassificationsSuccess( Map<String, String> classifications ) {
        VmrDebug.printLogI(this.getClass(), "Classifications retrieved");
        classificationsList.addAll(classifications.keySet());
        this.classificationsMap = classifications;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchClassificationsFailure(VolleyError error) {
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            indexFormLayout.setVisibility(View.GONE);
            etQuickReference.setEnabled(false);
            spLifeSpan.setEnabled(false);
            etGeoTag.setEnabled(false);
            etRemarks.setEnabled(false);
            spCategory.setEnabled(false);
            etNextAction.setEnabled(false);
            etActionMessage.setEnabled(false);

        } else {
            recordDocType = classificationsMap.get(classificationsList.get(position));
            HomeController requestController = new HomeController(new VmrResponseListener.OnFetchProperties() {
                @Override
                public void onFetchPropertiesSuccess(Map<String, JSONObject> properties) {
                    progressDialog.dismiss();
                    VmrDebug.printLogI(IndexDialog.this.getClass(), "Properties retrieved");
                    VmrDebug.printLogI(IndexDialog.this.getClass(), properties.keySet().toString());
                    indexFormLayout.setVisibility(View.VISIBLE);

                    if(properties.containsKey("vmr_quickref")){
                        etQuickReference.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_doclifespan")){
                        spLifeSpan.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_geotag")){
                        etGeoTag.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_remarks")){
                        etRemarks.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_category")){
                        spCategory.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_reminderdate")){
                        etNextAction.setEnabled(true);
                    }
                    if(properties.containsKey("vmr_remindermessage")){
                        etActionMessage.setEnabled(true);
                    }
                }

                @Override
                public void onFetchPropertiesFailure(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                }
            });
            requestController.fetchProperties(recordDocType, recordNodeRef, recordProgramName+"");
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage("Fetching index form...");
            progressDialog.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
