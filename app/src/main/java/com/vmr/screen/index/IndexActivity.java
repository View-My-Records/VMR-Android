package com.vmr.screen.index;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.model.Classification;
import com.vmr.network.controller.FetchIndexController;
import com.vmr.network.controller.HomeController;
import com.vmr.network.controller.VmrResponseListener;
import com.vmr.screen.home.fragments.dialog.DateTimePickerDialog;
import com.vmr.utils.ErrorMessage;
import com.vmr.utils.PermissionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IndexActivity extends AppCompatActivity
        implements
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
    Map<String, String> propertiesMap;
    MenuItem saveIndex;
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
    private ImageButton btnGeoTag;
    private EditText etRemarks;
    private Spinner spCategory;
    private TextView tvNextAction;
    private ImageButton btnSetNextAction;
    private EditText etActionMessage;
    private FetchIndexController fetchIndexController;
    private ProgressDialog mProgressDialog;

    public static Intent newInstance(Context context, Record record) {

        Intent intent = new Intent(context, IndexActivity.class);
        intent.putExtra(NODE_REF, record.getNodeRef());
        intent.putExtra(NODE_REF, record.getNodeRef());
        intent.putExtra(PROGRAM, "");
        intent.putExtra(FILE_NAME, record.getRecordName());
        if (record.getRecordDocType().equals("vmr:unindexed"))
            intent.putExtra(INDEXED_STATUS, false);
        else
            intent.putExtra(INDEXED_STATUS, true);

        return intent;
    }

    private static String getKeyFromValue(Map<String, String> map, String value) {
        for (String o : map.keySet()) {
            if (map.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        recordNodeRef = intent.getStringExtra(NODE_REF);
        recordProgramName = intent.getStringExtra(PROGRAM);
        recordName = intent.getStringExtra(FILE_NAME);
        recordIndexStatus = intent.getBooleanExtra(INDEXED_STATUS, false);

        VmrDebug.printLogI(this.getClass(), "Intent Data");
        VmrDebug.printLogI(this.getClass(), "NodeRef ->" + recordNodeRef);
        VmrDebug.printLogI(this.getClass(), "ProgramName ->" + recordProgramName);
        VmrDebug.printLogI(this.getClass(), "RecordName ->" + recordName);
        VmrDebug.printLogI(this.getClass(), "IndexStatus ->" + recordIndexStatus);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_index);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        getSupportActionBar().setTitle(recordName);

        setupFormFields();

        fetchIndexController = new FetchIndexController(this);
        classificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classificationsList);
        classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchIndices();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.index_menu, menu);
        saveIndex = menu.findItem(R.id.action_save);
        return true;
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
            fetchIndices();
            return true;
        } else if (id == R.id.action_reset) {

            return true;
        } else if (id == android.R.id.home) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            finishActivityForResult(RESULT_CANCELED);
            return true;
        }
        return false;
    }

    private void fetchIndices() {
        if (recordIndexStatus) {
            fetchIndexController.fetchIndices(recordNodeRef, "");
            mProgressDialog.setMessage("Fetching indices");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if (position == 0) {
            indexFormLayout.setVisibility(View.GONE);
//            saveIndex.setEnabled(false);
        } else {
//            saveIndex.setEnabled(true);
            recordDocType = classificationsMap.get(classificationsList.get(position));
            HomeController requestController = new HomeController(new VmrResponseListener.OnFetchProperties() {
                @Override
                public void onFetchPropertiesSuccess(Map<String, JSONObject> properties) {
                    progressDialog.dismiss();
                    VmrDebug.printLogI(IndexActivity.this.getClass(), "Properties retrieved");
                    VmrDebug.printLogI(IndexActivity.this.getClass(), properties.keySet().toString());
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

                    if(!recordIndexStatus) {
                        InputMethodManager imm = (InputMethodManager) IndexActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }

                @Override
                public void onFetchPropertiesFailure(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                }
            });
            requestController.fetchProperties(recordDocType, recordNodeRef, recordProgramName + "");
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching index form...");
            progressDialog.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onFetchIndexSuccess(JSONObject jsonObject) {
        mProgressDialog.dismiss();
        VmrDebug.printLogI(this.getClass(), "Indices fetched");
        try {
            if (!jsonObject.getString("DoctypeID").equals("vmr_unindexed")) {
                VmrDebug.printLogI(this.getClass(), recordName + " already indexed");
                String value = jsonObject.getString("DoctypeID");
                String key = getKeyFromValue(classificationsMap, value);

                spClassification.setSelection(classificationsList.indexOf(key));

                propertiesMap = getPropertiesMap(jsonObject.getJSONArray("Properties"));

                indexFormLayout.setVisibility(View.VISIBLE);
                etQuickReference.setText(propertiesMap.get("vmr_quickref"));
                spLifeSpan.setSelection(lifeSpanList.indexOf(propertiesMap.get("vmr_doclifespan")));

                etGeoTag.setText(propertiesMap.get("vmr_geotag"));
                etRemarks.setText(propertiesMap.get("vmr_remarks"));

                String key2 = getKeyFromValue(categoryMap, propertiesMap.get("vmr_category"));
                VmrDebug.printLogI(this.getClass(), "Catagory key2 -> " + propertiesMap.get("vmr_category"));
                spCategory.setSelection(categoryList.indexOf(key2));

                DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.getDefault());
                nextActionDate = df.parse(propertiesMap.get("vmr_reminderdate"));
                tvNextAction.setText(this.df.format(nextActionDate));
                etActionMessage.setText(propertiesMap.get("vmr_remindermessage"));
            } else {
                VmrDebug.printLogI(this.getClass(), recordName + " not indexed");
            }
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFetchIndexFailure(VolleyError error) {
        VmrDebug.printLogI(this.getClass(), "Failed to retrieve indices");
    }

    @Override
    public void onDateTimePicked(Date nextActionDate) {
        this.nextActionDate = nextActionDate;
        String dateString = df.format(nextActionDate.getTime());
        tvNextAction.setText(dateString);
    }

    private void setupFormFields() {
        indexFormLayout = (LinearLayout) findViewById(R.id.indexFormLayout);
        spClassification = (Spinner) findViewById(R.id.spinnerClassification);
        etQuickReference = (EditText) findViewById(R.id.etQuickReference);
        spLifeSpan = (Spinner) findViewById(R.id.spinnerLifeSpan);
        etGeoTag = (EditText) findViewById(R.id.etGeoTag);
        btnGeoTag = (ImageButton) findViewById(R.id.btnGeoTag);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        spCategory = (Spinner) findViewById(R.id.spinnerCategory);
        tvNextAction = (TextView) findViewById(R.id.tvNextAction);
        btnSetNextAction = (ImageButton) findViewById(R.id.btnSetDateTime);
        etActionMessage = (EditText) findViewById(R.id.etActionMessage);

        spClassification.setOnItemSelectedListener(this);
        classificationsMap = Classification.getDocumentTypes();
        classificationsList.add(0, "Classification");
        classificationsList.addAll(classificationsMap.keySet());
        classificationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classificationsList);
        classificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClassification.setAdapter(classificationAdapter);

        lifeSpanList.add("1");
        lifeSpanList.add("2");
        lifeSpanList.add("3");
        lifeSpanList.add("5");
        lifeSpanList.add("7");
        lifeSpanList.add("10");
        lifeSpanAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lifeSpanList);
        lifeSpanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLifeSpan.setAdapter(lifeSpanAdapter);

//        categoryMap.put("Select permission", "");
        categoryMap.put("Normal", "NORM");
        categoryMap.put("Confidential", "CONF");
        categoryMap.put("Highly Secure", "HCONF");
        categoryList.add(0, "Select permission");
        categoryList.addAll(categoryMap.keySet());
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
        spCategory.setSelection(0);

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        };

        spClassification.setClickable(true);
        spClassification.setOnTouchListener(onTouchListener);
        spCategory.setClickable(true);
        spCategory.setOnTouchListener(onTouchListener);
        spLifeSpan.setClickable(true);
        spLifeSpan.setOnTouchListener(onTouchListener);

        tvNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(new View(IndexActivity.this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(IndexActivity.this);
                dateTimePicker.show(getFragmentManager(), "DateTimePicker");
            }
        });

        btnSetNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(new View(IndexActivity.this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(IndexActivity.this);
                dateTimePicker.show(getFragmentManager(), "DateTimePicker");
            }
        });

        btnGeoTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(new View(IndexActivity.this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                Toast.makeText(getActivity(), "Feature is not available yet", Toast.LENGTH_SHORT).show();
                getLocation();
            }
        });

        indexFormLayout.setVisibility(View.GONE);
    }

    private boolean validateIndices() {

        if (spClassification.getSelectedItemPosition() == 0) {
            new AlertDialog
                    .Builder(this)
                    .setMessage("Please select document type.")
                    .show();
            return false;
        }

        if (etQuickReference.getText().toString().equals("")) {
            etQuickReference.setError("This field can't be left empty");
            return false;
        } else if (etQuickReference.getText().toString().length() < 2) {
            etQuickReference.setError("Quick Reference text is too short");
            return false;
        }

        if (etGeoTag.getText().toString().equals("")) {
            etGeoTag.setError("This field can't be left empty");
            return false;
        } else if (etGeoTag.getText().toString().length() < 2) {
            etGeoTag.setError("Geo-Tag text is too short");
            return false;
        }

        if (etRemarks.getText().toString().equals("")) {
            etRemarks.setError("This field can't be left empty");
            return false;
        } else if (etRemarks.getText().toString().length() < 2) {
            etRemarks.setError("Remarks text is too short");
            return false;
        }

        if (spCategory.getSelectedItemPosition() == 0) {
            new AlertDialog
                    .Builder(this)
                    .setMessage("Please select document permission.")
                    .show();
            return false;
        }

        if (recordIndexStatus) {
            if (Integer.valueOf(spLifeSpan.getSelectedItem().toString()) < Integer.valueOf(propertiesMap.get("vmr_doclifespan"))) {
                new AlertDialog
                        .Builder(this)
                        .setMessage("Record lifespan cannot be reduced.")
                        .show();
                return false;
            }
        }

        if (nextActionDate == null || nextActionDate.before(new Date(System.currentTimeMillis()))) {
            new AlertDialog
                    .Builder(this)
                    .setMessage("Please select next action date.")
                    .show();
            return false;
        }

        return true;
    }

    private void saveIndex() {

        final ProgressDialog saveIndexProgress = new ProgressDialog(this);
        saveIndexProgress.setMessage("Saving indices...");
        saveIndexProgress.show();

        HomeController saveIndexController = new HomeController(new VmrResponseListener.OnSaveIndex() {
            @Override
            public void onSaveIndexSuccess(String response) {
                VmrDebug.printLogI(IndexActivity.this.getClass(), "Index saved");
                Toast.makeText(IndexActivity.this, "Index saved successfully", Toast.LENGTH_SHORT).show();
                saveIndexProgress.dismiss();
                finishActivityForResult(RESULT_OK);
            }

            @Override
            public void onSaveIndexFailure(VolleyError error) {
                Toast.makeText(Vmr.getContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
                saveIndexProgress.dismiss();
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

    private void getLocation(){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                VmrDebug.printLogI(IndexActivity.this.getClass(), "Location : " + location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        if (PermissionHandler.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            if(lastKnownLocation !=null) {
                List<Address> address = new ArrayList<>();
                try {
                    address = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //String addr = address.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = address.get(0).getLocality();
                String state = address.get(0).getAdminArea();
                String country = address.get(0).getCountryName();
                //String postalCode = address.get(0).getPostalCode();
                //String knownName = address.get(0).getFeatureName();
                etGeoTag.setText(city + ", " + state+", "+country);

            } else {
                if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(new View(this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    Snackbar.make(findViewById(R.id.content_index), "GPS Service is off", Snackbar.LENGTH_LONG)
                            .setAction("TURN ON", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .show();
                } else {
                    Toast.makeText(this, "Waiting for GPS to fix", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(new View(this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            Snackbar snackbar =
                    Snackbar
                            .make(findViewById(R.id.content_index), "Application needs permission to use GPS", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PermissionHandler.requestPermission(IndexActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, 1);
                                }
                            });
            snackbar.show();

        }
    }

    private Map<String, String> getPropertiesMap(JSONArray properties) throws JSONException {
        Map<String, String> propertiesMap = new HashMap<>();
        JSONObject jsonObject;
        for (int i = 0; i < properties.length() - 1; i++) {
            jsonObject = properties.getJSONObject(i);
            propertiesMap.put(jsonObject.getString("propertyName"), jsonObject.getString("propertyValue"));
        }

        return propertiesMap;
    }

    private void finishActivityForResult(int result){
        setResult(result);
        finish();
    }
}
