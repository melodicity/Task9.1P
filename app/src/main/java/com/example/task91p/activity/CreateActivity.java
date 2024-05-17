package com.example.task91p.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.task91p.BuildConfig;
import com.example.task91p.R;
import com.example.task91p.data.DatabaseHelper;
import com.example.task91p.model.Item;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Locale;

public class CreateActivity extends AppCompatActivity {

    // Declare views
    RadioGroup rgPostType;
    RadioButton rbLost, rbFound, rbSelected;
    EditText etName, etPhone, etDescription, etDate;
    Button btnGetLocation, btnSave;

    // Database helper used for inserting new items
    DatabaseHelper db;

    // Location manager used for app's location service (get current location button)
    LocationManager locationManager;
    LocationListener locationListener;

    // AutocompleteSupportFragment for location autocomplete
    AutocompleteSupportFragment fragAutocomplete;

    // Declare a field to hold the selected place
    Place selectedPlace;
    LatLng selectedLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialise Places
        // Access the MAPS_API_KEY from BuildConfig
        String apiKey = BuildConfig.MAPS_API_KEY;
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        // Initialise views
        rgPostType = findViewById(R.id.rgPostType);
        rbLost = findViewById(R.id.rbLost);
        rbFound = findViewById(R.id.rbFound);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnSave = findViewById(R.id.btnSave);

        // Initialise the AutocompleteSupportFragment
        fragAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragAutocomplete);

        // Specify the types of place data to return
        assert fragAutocomplete != null;
        fragAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));
        fragAutocomplete.setHint("Enter Location");

        // Listen for place selection within the autocomplete fragment
        fragAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                selectedPlace = null;
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
                selectedPlace = place;
            }
        });

        // On "get location" click, update the etLocation field with the current location (lat/lng)
        btnGetLocation.setOnClickListener(v -> {
            // Check if location permissions are granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request location permissions if not granted
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                // Location permissions are granted, proceed with getting location
                // Initialise location manager
                locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

                // Initialise location listener
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        selectedLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        // Update the location fragment with the current location
                        fragAutocomplete.setText(String.format(Locale.getDefault(), "%.6f, %.6f",
                                location.getLatitude(), location.getLongitude()));

                        // Nullify selected place, as no place is selected now, just a lat/lng
                        selectedPlace = null;

                        // Stop listening for location updates to conserve resources
                        locationManager.removeUpdates(locationListener);
                    }
                };

                // Request location updates
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
            }
        });

        // On "save" click, add the item to the DB and go back
        btnSave.setOnClickListener(v -> {
            // Declare input variables
            String name, phone, description, date;
            LatLng location;

            // Validate post type radio buttons
            if (rgPostType.getCheckedRadioButtonId()==-1) {
                Toast.makeText(CreateActivity.this, "Please select a post type", Toast.LENGTH_SHORT).show();
                return;
            } else {
                // Get selected radio button from the radio group
                int selectedRadioButtonId = rgPostType.getCheckedRadioButtonId();
                // Find the radio button by returned id
                rbSelected = (RadioButton) findViewById(selectedRadioButtonId);
            }

            // Validate required fields
            if (etName.getText().toString().isEmpty() || etPhone.getText().toString().isEmpty()
                    || etDescription.getText().toString().isEmpty() || etDate.getText().toString().isEmpty()) {
                Toast.makeText(CreateActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get inputs from (now validated) fields
            name = rbSelected.getText().toString() + " " + etName.getText().toString();
            phone = etPhone.getText().toString();
            description = etDescription.getText().toString();
            date = etDate.getText().toString();

            // Validate and get location separately (more complex)
            if (selectedPlace != null) {
                location = selectedPlace.getLatLng();
            } else if (selectedLatLng != null) {
                location = selectedLatLng;
            } else {
                Toast.makeText(CreateActivity.this, "Please provide a location", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new item from fields
            Item item = new Item(name, phone, description, date, location);

            // Insert the item into the database
            db = new DatabaseHelper(CreateActivity.this);
            long newRowId = db.insertItem(item);
            if (newRowId <= 0) {
                Toast.makeText(CreateActivity.this, "Error saving to database", Toast.LENGTH_SHORT).show();
                return;
            }

            finish(); // closes this activity
        });
    }
}