package com.example.task91p.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.task91p.R;
import com.example.task91p.data.DatabaseHelper;
import com.example.task91p.databinding.ActivityMapsBinding;
import com.example.task91p.model.Item;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    ActivityMapsBinding binding;

    // Database helper used for reading item data
    DatabaseHelper db;

    // List of all items to show on the map
    List<Item> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the selected item name from the intent
        Intent intent = getIntent();

        // Get the matching item from the database
        db = new DatabaseHelper(this);
        items = db.fetchItems();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    // Callback triggered once map is ready to be used
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Initialise map appearance
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker for each item in the list
        for (Item item : items) {
            LatLng location = item.getLocation();
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(item.getName())
                    .snippet(item.getDescription())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        // Move camera to the first item's location (if available)
        if (!items.isEmpty()) {
            LatLng firstItemLocation = items.get(0).getLocation();
            if (firstItemLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstItemLocation, 10));
            }
        }
    }
}