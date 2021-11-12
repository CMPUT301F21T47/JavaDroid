package com.example.habitshare;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_LOCATION_PERMISSION = 999;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    private boolean isLocationPermissionGranted = true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private List<com.google.android.libraries.places.api.model.AutocompletePrediction> predictionList;
    private String addressLine;
    private Button confirm;


    GoogleMap GMap;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        confirm = findViewById(R.id.button_confirm_location);
        confirm.setVisibility(View.INVISIBLE);

        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{FINE_LOCATION, COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            showMap();
            Places.initialize(MapActivity.this, getString(R.string.API_KEY));
            placesClient = Places.createClient(this);
            getCurrentLocation();
            init();
        }

    }

    public void getCurrentLocation() {
        List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest.builder(placeField).build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        placesClient.findCurrentPlace(findCurrentPlaceRequest).addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
            @Override
            public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                Place place = findCurrentPlaceResponse.getPlaceLikelihoods().get(0).getPlace();
                LatLng latLng = place.getLatLng();
                addressLine = place.getAddress();
                moveCamera(latLng, 15f, addressLine);
                confirm.setVisibility(View.VISIBLE);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar failGetCurrentLocation = Snackbar.make(findViewById(R.id.activity_map), "Failed to get current location", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Try Again", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getCurrentLocation();
                                    }
                                });
                        failGetCurrentLocation.show();
                    }
                });
    }
    public void init() {
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setCountries("CA");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // TODO: Get info about the selected place.
                List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(place.getId(), placeField).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLng = place.getLatLng();
                        addressLine = place.getAddress();
                        Log.d(TAG, "Location is " + addressLine);
                        moveCamera(latLng, 15f, addressLine);
                        confirm.setVisibility(View.VISIBLE);
                    }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MapActivity.this, "Fail to get the location", Toast.LENGTH_SHORT).show();
                        }
                    });
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("address_line", addressLine);
                Log.d(TAG, "Address line is " + addressLine);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }



    public void showMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GMap = googleMap;
            }
        });
    }

    private void getLocationPermission(){
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){

            }
            else{
                ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        }
        Log.d(TAG, "Permission 1: " + (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_DENIED));
        Log.d(TAG, "Permission 2: " + (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_DENIED));
    }


    private void moveCamera(LatLng latLng, float zoom, String title){
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        GMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_LOCATION_PERMISSION:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_DENIED){
                            isLocationPermissionGranted = false;
                            return;
                        }
                    }
                }
                else{
                    Toast.makeText(MapActivity.this, "Fail to get location permission", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}