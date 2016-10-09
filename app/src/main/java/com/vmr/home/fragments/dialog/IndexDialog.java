package com.vmr.home.fragments.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.controller.FetchIndexController;
import com.vmr.home.controller.HomeController;
import com.vmr.response_listener.VmrResponseListener;
import com.vmr.utils.ErrorMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/*
 * Created by abhijit on 9/2/16.
 */
public class IndexDialog extends DialogFragment
        implements
        VmrResponseListener.OnFetchClassifications,
        FetchIndexController.OnFetchIndicesListener,
        AdapterView.OnItemSelectedListener,
        DateTimePickerDialog.VmrDateTimePicker {

    private static final String NODE_REF = "NODE_REF";
    private static final String PROGRAM = "PROGRAM";
    private static final String FILE_NAME = "FILE_NAME";
    private static final String INDEXED_STATUS = "INDEXED_STATUS";
    List<String> classificationsList = new ArrayList<>();
    List<String> lifeSpanList = new ArrayList<>();
    List<String> categoryList = new ArrayList<>();
    Map<String, String> classificationsMap = new HashMap<>();
    Map<String, String> categoryMap = new HashMap<>();
    ArrayAdapter<String> classificationAdapter;
    ArrayAdapter<String> lifeSpanAdapter;
    ArrayAdapter<String> categoryAdapter;
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()); //"31/10/2016 08:00:00"
    private String recordDocType;
    private String recordNodeRef;
    private String recordName;
    private String recordProgramName;
    private boolean recordIndexStatus;
    private Date nextActionDate;
    private ProgressDialog progressDialog;
    private LinearLayout indexFormLayout;
    private Spinner spClassification;
    private EditText etQuickReference;
    private Spinner spLifeSpan;
    private EditText etGeoTag;
    private EditText etRemarks;
    private Spinner spCategory;
    private TextView tvNextAction;
    private TextView btnSetNextAction;
    private EditText etActionMessage;
    private HomeController homeController;
    private FetchIndexController fetchIndexController;

    public static IndexDialog newInstance(Record record) {
        IndexDialog newDialog = new IndexDialog();

        Bundle arguments = new Bundle();
        arguments.putString( NODE_REF, record.getNodeRef());
        arguments.putString( PROGRAM, null);
        arguments.putString( FILE_NAME, record.getRecordName());
        if(record.getRecordDocType().equals("vmr_unindexed"))
            arguments.putBoolean( INDEXED_STATUS, false);
        else arguments.putBoolean( INDEXED_STATUS, true);

        newDialog.setArguments(arguments);

        return newDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light);

        recordNodeRef = getArguments().getString(NODE_REF);
        recordProgramName = getArguments().getString(PROGRAM);
        recordName = getArguments().getString(FILE_NAME);
        recordIndexStatus = getArguments().getBoolean(INDEXED_STATUS);

        homeController = new HomeController(this);
        fetchIndexController = new FetchIndexController(this);
        classificationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, classificationsList);
        classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
        View dialogView = inflater.inflate(R.layout.dialog_fragment_index, container);

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar_index_dialog);
        toolbar.setTitle("Indexing...");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
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
        classificationsList.add(0, "Classification");

        spLifeSpan.setAdapter(lifeSpanAdapter);
        spCategory.setAdapter(categoryAdapter);
        spClassification.setAdapter(classificationAdapter);
        spClassification.setOnItemSelectedListener(this);
    }

    private void setupFormFields(View dialogView) {
        indexFormLayout = (LinearLayout) dialogView.findViewById(R.id.indexFormLayout);
        spClassification = (Spinner) dialogView.findViewById(R.id.spinnerClassification);
        etQuickReference = (EditText) dialogView.findViewById(R.id.etQuickReference);
        spLifeSpan = (Spinner) dialogView.findViewById(R.id.spinnerLifeSpan);
        etGeoTag = (EditText) dialogView.findViewById(R.id.etGeoTag);
        etRemarks = (EditText) dialogView.findViewById(R.id.etRemarks);
        spCategory = (Spinner) dialogView.findViewById(R.id.spinnerCategory);
        tvNextAction = (TextView) dialogView.findViewById(R.id.tvNextAction);
        btnSetNextAction = (TextView) dialogView.findViewById(R.id.btnSetDateTime);
        etActionMessage = (EditText) dialogView.findViewById(R.id.etActionMessage);

        lifeSpanList.add("1");
        lifeSpanList.add("2");
        lifeSpanList.add("3");
        lifeSpanList.add("5");
        lifeSpanList.add("7");
        lifeSpanList.add("10");
        lifeSpanAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, lifeSpanList);
        lifeSpanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoryMap.put("Normal", "NORM");
        categoryMap.put("Confidential", "CONF");
        categoryMap.put("Highly Secure", "HCON");
        categoryList.addAll(categoryMap.keySet());
        categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        btnSetNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(IndexDialog.this);
                dateTimePicker.show(getActivity().getFragmentManager(), "datetimepicker");

            }
        });

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(recordIndexStatus)
            fetchIndexController.fetchIndices(recordNodeRef, "");
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
            if (validateIndices()) {
                saveIndex();
            }
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

    private boolean validateIndices() {
        if (spClassification.getSelectedItemPosition() == 0) {
            new AlertDialog
                    .Builder(getActivity())
                    .setMessage("Please select document type.")
                    .show();
            return false;
        }

        if (nextActionDate == null || nextActionDate.before(new Date(System.currentTimeMillis()))) {
            new AlertDialog
                    .Builder(getActivity())
                    .setMessage("Please select next action date.")
                    .show();
            return false;
        }

        return true;
    }

    private void saveIndex() {
        HomeController saveIndexController = new HomeController(new VmrResponseListener.OnSaveIndex() {
            @Override
            public void onSaveIndexSuccess(String response) {
                VmrDebug.printLogI(IndexDialog.this.getClass(), "Index saved");
                Toast.makeText(getActivity(), "Index saved successfully", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }

            @Override
            public void onSaveIndexFailure(VolleyError error) {
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
            }
        });

        try {

            JSONObject finalJson = new JSONObject();
            finalJson.put("Doctype", classificationsMap.get(spClassification.getSelectedItem().toString()));

            JSONArray propertiesArray = new JSONArray();

            JSONObject quickRef = new JSONObject();
            quickRef.put("Name", "vmr_quickref");
            quickRef.put("Value", etQuickReference.getText().toString() + "");
            propertiesArray.put(quickRef);

            JSONObject geoTag = new JSONObject();
            geoTag.put("Name", "vmr_geotag");
            geoTag.put("Value", etGeoTag.getText().toString() + "");
            propertiesArray.put(geoTag);

            JSONObject remarks = new JSONObject();
            remarks.put("Name", "vmr_remarks");
            remarks.put("Value", etRemarks.getText().toString() + "");
            propertiesArray.put(remarks);

            String dateString = df.format(nextActionDate.getTime());
            JSONObject reminderDate = new JSONObject();
            reminderDate.put("Name", "vmr_reminderdate");
            reminderDate.put("Value", dateString);
            propertiesArray.put(reminderDate);

            JSONObject reminderMessage = new JSONObject();
            reminderMessage.put("Name", "vmr_remindermessage");
            reminderMessage.put("Value", etActionMessage.getText().toString() + "");
            propertiesArray.put(reminderMessage);

            JSONObject docLifeSpan = new JSONObject();
            docLifeSpan.put("Name", "vmr_doclifespan");
            docLifeSpan.put("Value", spLifeSpan.getSelectedItem().toString());
            propertiesArray.put(docLifeSpan);

            JSONObject category = new JSONObject();
            category.put("Name", "vmr_category");
            category.put("Value", categoryMap.get(spCategory.getSelectedItem().toString()));
            propertiesArray.put(category);

            finalJson.put("Properties", propertiesArray);

            saveIndexController.saveIndex(finalJson.toString().replaceAll("\\\\", ""), // filePropertyJsonString
                    recordNodeRef,
                    recordName,
                    false,
                    categoryMap.get(spCategory.getSelectedItem().toString()),
                    recordDocType,
                    recordProgramName + "");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFetchClassificationsSuccess(Map<String, String> classifications) {
        VmrDebug.printLogI(this.getClass(), "Classifications retrieved");
        classificationsList.addAll(classifications.keySet());
        this.classificationsMap = classifications;
        classificationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFetchClassificationsFailure(VolleyError error) {
        Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            indexFormLayout.setVisibility(View.GONE);
        } else {
            recordDocType = classificationsMap.get(classificationsList.get(position));
            HomeController requestController = new HomeController(new VmrResponseListener.OnFetchProperties() {
                @Override
                public void onFetchPropertiesSuccess(Map<String, JSONObject> properties) {
                    progressDialog.dismiss();
                    VmrDebug.printLogI(IndexDialog.this.getClass(), "Properties retrieved");
                    VmrDebug.printLogI(IndexDialog.this.getClass(), properties.keySet().toString());
                    indexFormLayout.setVisibility(View.VISIBLE);

                    if (properties.containsKey("vmr_quickref")) {
                        etQuickReference.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_doclifespan")) {
                        spLifeSpan.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_geotag")) {
                        etGeoTag.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_remarks")) {
                        etRemarks.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_category")) {
                        spCategory.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_reminderdate")) {
                        tvNextAction.setEnabled(true);
                        btnSetNextAction.setEnabled(true);
                    }
                    if (properties.containsKey("vmr_remindermessage")) {
                        etActionMessage.setEnabled(true);
                    }

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }

                @Override
                public void onFetchPropertiesFailure(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                }
            });
            requestController.fetchProperties(recordDocType, recordNodeRef, recordProgramName + "");
            progressDialog = new ProgressDialog(this.getActivity());
            progressDialog.setMessage("Fetching index form...");
            progressDialog.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDateTimePicked(Date nextActionDate) {
        this.nextActionDate = nextActionDate;
        String dateString = df.format(nextActionDate.getTime());
        tvNextAction.setText(dateString);
    }

    @Override
    public void onFetchIndexSuccess(JSONObject jsonObject) {
        VmrDebug.printLogI(this.getClass(), "Indices fetched");
        try {
            if(!jsonObject.getString("DoctypeID").equals("vmr_unindexed")) {
                Toast.makeText(getActivity(), "File is already indexed", Toast.LENGTH_SHORT).show();
                VmrDebug.printLogI(this.getClass(), "Map->"+classificationsMap.toString());
                VmrDebug.printLogI(this.getClass(), "List->"+classificationsList.toString());
                spClassification.setSelection(classificationsList.indexOf(jsonObject.getString("DoctypeName")));
                Map<String , String > propertiesMap = getPropertiesMap(jsonObject.getJSONArray("Properties"));

                indexFormLayout.setVisibility(View.VISIBLE);
                etQuickReference.setText(propertiesMap.get("vmr_quickref"));
                spLifeSpan.setSelection(lifeSpanList.indexOf(propertiesMap.get("vmr_doclifespan")));
                etGeoTag.setText(propertiesMap.get("vmr_geotag"));
                etRemarks.setText(propertiesMap.get("vmr_remarks"));
                spCategory.setSelection(categoryList.indexOf(propertiesMap.get("vmr_category")));
                tvNextAction.setText(propertiesMap.get("vmr_reminderdate"));
                etActionMessage.setText(propertiesMap.get("vmr_remindermessage"));
            } else {
                Toast.makeText(getActivity(), "File not indexed", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFetchIndexFailure(VolleyError error) {
        VmrDebug.printLogI(this.getClass(), "Failed to retrieve indices");
    }

    private Map<String , String > getPropertiesMap(JSONArray properties) throws JSONException {
        Map<String , String > propertiesMap = new HashMap<>();
        JSONObject jsonObject;
        for(int i=0; i< properties.length() - 1; i++){
            jsonObject = properties.getJSONObject(i);
            propertiesMap.put(jsonObject.getString("propertyName"), jsonObject.getString("propertyValue"));
        }

        return propertiesMap;
    }
}
