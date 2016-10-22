package com.vmr.home.fragments.dialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.vmr.R;
import com.vmr.app.Vmr;
import com.vmr.db.record.Record;
import com.vmr.debug.VmrDebug;
import com.vmr.home.controller.FetchIndexController;
import com.vmr.home.controller.HomeController;
import com.vmr.response_listener.VmrResponseListener;
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

/*
 * Created by abhijit on 9/2/16.
 */
public class IndexDialog extends DialogFragment
        implements
        VmrResponseListener.OnFetchClassifications,
        FetchIndexController.OnFetchIndicesListener,
        AdapterView.OnItemSelectedListener,
        Toolbar.OnMenuItemClickListener,
        DateTimePickerDialog.VmrDateTimePicker,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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
    GoogleApiClient mGoogleApiClient;
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
    private HomeController homeController;
    private FetchIndexController fetchIndexController;
    private ProgressDialog mProgressDialog;
    private OnIndexDialogDismissListener onIndexDialogDismissListener;

    public static IndexDialog newInstance(Record record) {
        IndexDialog newDialog = new IndexDialog();

        Bundle arguments = new Bundle();
        arguments.putString(NODE_REF, record.getNodeRef());
        arguments.putString(PROGRAM, null);
        arguments.putString(FILE_NAME, record.getRecordName());
        if (record.getRecordDocType().equals("vmr:unindexed"))
            arguments.putBoolean(INDEXED_STATUS, false);
        else
            arguments.putBoolean(INDEXED_STATUS, true);

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

        mProgressDialog = new ProgressDialog(getActivity());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        ActionBar toolbar = dialog.getActionBar();
//        toolbar.setTitle("Indexing...");
//        toolbar.setDisplayHomeAsUpEnabled(true);
//        toolbar.setDisplayShowHomeEnabled(true);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            toolbar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
//        }
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_fragment_index, container);

        Toolbar toolbar = (Toolbar) dialogView.findViewById(R.id.toolbar_index_dialog);
        toolbar.setTitle(recordName);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.inflateMenu(R.menu.index_menu);
        toolbar.setOnMenuItemClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
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
        btnGeoTag = (ImageButton) dialogView.findViewById(R.id.btnGeoTag);
        etRemarks = (EditText) dialogView.findViewById(R.id.etRemarks);
        spCategory = (Spinner) dialogView.findViewById(R.id.spinnerCategory);
        tvNextAction = (TextView) dialogView.findViewById(R.id.tvNextAction);
        btnSetNextAction = (ImageButton) dialogView.findViewById(R.id.btnSetDateTime);
        etActionMessage = (EditText) dialogView.findViewById(R.id.etActionMessage);

        lifeSpanList.add("1");
        lifeSpanList.add("2");
        lifeSpanList.add("3");
        lifeSpanList.add("5");
        lifeSpanList.add("7");
        lifeSpanList.add("10");
        lifeSpanAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, lifeSpanList);
        lifeSpanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//        categoryMap.put("Select permission", "");
        categoryMap.put("Normal", "NORM");
        categoryMap.put("Confidential", "CONF");
        categoryMap.put("Highly Secure", "HCONF");
        categoryList.add(0, "Select permission");
        categoryList.addAll(categoryMap.keySet());
        categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setSelection(3);

        tvNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(IndexDialog.this);
                dateTimePicker.show(getActivity().getFragmentManager(), "DateTimePicker");
            }
        });

        btnSetNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickerDialog dateTimePicker = new DateTimePickerDialog();
                dateTimePicker.setDateTimePickerInterface(IndexDialog.this);
                dateTimePicker.show(getActivity().getFragmentManager(), "DateTimePicker");
            }
        });

        btnGeoTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Feature is not available yet", Toast.LENGTH_SHORT).show();
                getLocation();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchIndices();
        buildGoogleApiClient();
    }

    private void fetchIndices() {
        if (recordIndexStatus) {
            fetchIndexController.fetchIndices(recordNodeRef, "");
            mProgressDialog.setMessage("Fetching indices");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    private boolean validateIndices() {

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
                    .Builder(getActivity())
                    .setMessage("Please select document permission.")
                    .show();
            return false;
        }

        if (recordIndexStatus) {
            if (Integer.valueOf(spLifeSpan.getSelectedItem().toString()) < Integer.valueOf(propertiesMap.get("vmr_doclifespan"))) {
                new AlertDialog
                        .Builder(getActivity())
                        .setMessage("Record lifespan cannot be reduced.")
                        .show();
                return false;
            }
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

        final ProgressDialog saveIndexProgress = new ProgressDialog(getActivity());
        saveIndexProgress.setMessage("Saving indices...");
        saveIndexProgress.show();

        HomeController saveIndexController = new HomeController(new VmrResponseListener.OnSaveIndex() {
            @Override
            public void onSaveIndexSuccess(String response) {
                VmrDebug.printLogI(IndexDialog.this.getClass(), "Index saved");
                Toast.makeText(getActivity(), "Index saved successfully", Toast.LENGTH_SHORT).show();
                saveIndexProgress.dismiss();
                getDialog().dismiss();
            }

            @Override
            public void onSaveIndexFailure(VolleyError error) {
                Toast.makeText(Vmr.getVMRContext(), ErrorMessage.show(error), Toast.LENGTH_SHORT).show();
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

    private String getKeyFromValue(Map<String, String> hm, String value) {
        for (String o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mGoogleApiClient.disconnect();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        onIndexDialogDismissListener.onDismiss();
    }

    @Override
    public void onFetchIndexFailure(VolleyError error) {
        VmrDebug.printLogI(this.getClass(), "Failed to retrieve indices");
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_save) {
            if (validateIndices()) {
                saveIndex();
            }
            return true;
        } else if (id == R.id.action_refresh) {
            setupClassifications();
            fetchIndices();
            return true;
        } else if (id == R.id.action_reset) {

            return true;
        } else if (id == android.R.id.home) {

            dismiss();
            return true;
        }
        return false;
    }

    public void setOnIndexDialogDismissListener(OnIndexDialogDismissListener onIndexDialogDismissListener) {
        this.onIndexDialogDismissListener = onIndexDialogDismissListener;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        VmrDebug.printLogI(IndexDialog.this.getClass(), "Connected to Google Play services...");
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            PermissionHandler.requestPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, 1 );
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            VmrDebug.printLogI(IndexDialog.this.getClass(), "Received location coordinates..." + mLastLocation);
            if (mLastLocation != null) {
                String mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                String mLongitudeText = String.valueOf(mLastLocation.getLongitude());
                Toast.makeText(getActivity(), "Latitude: " + mLatitudeText + "\nLongitude: " + mLongitudeText, Toast.LENGTH_LONG).show();
                VmrDebug.printLogI(this.getClass(), "Latitude: " + mLatitudeText + "\nLongitude: " + mLongitudeText);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void getLocation(){
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                VmrDebug.printLogI(IndexDialog.this.getClass(), "Location : " + location);
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
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

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
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    Snackbar.make(getDialog().findViewById(R.id.clayout), "GPS Service is off", Snackbar.LENGTH_LONG)
                            .setAction("TURN ON", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    Toast.makeText(getActivity(), "Waiting for GPS to fix", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Snackbar snackbar =
                    Snackbar
                    .make(getDialog().findViewById(R.id.clayout), "Application needs permission to use GPS", Snackbar.LENGTH_LONG)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PermissionHandler.requestPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION, 1);
                        }
                    });
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            snackbar.show();
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        }
    }

    public interface OnIndexDialogDismissListener {
        void onDismiss();
    }
}
