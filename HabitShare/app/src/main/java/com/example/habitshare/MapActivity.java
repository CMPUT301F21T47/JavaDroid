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

    PlacesClient placesClient;
    List<com.google.android.libraries.places.api.model.AutocompletePrediction> predictionList;
    String addressLine;
    Button confirm;
    GoogleMap GMap;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        confirm = findViewById(R.id.button_confirm_location);
        confirm.setVisibility(View.INVISIBLE); // hide the confirm button when there is no address line being captured

        if (ActivityCompat.checkSelfPermission(this, FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
               && ActivityCompat.checkSelfPermission(this, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // mandatory permission check
            init();
        }

    }

    /**
     * Uses Google Places api to find the current location and move the camera and pin point to that position
     */
    public void getCurrentLocation() {
        List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
        FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest.builder(placeField).build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { // mandatory permission check
            return;
        }
        placesClient.findCurrentPlace(findCurrentPlaceRequest).addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
            @Override
            public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                Place place = findCurrentPlaceResponse.getPlaceLikelihoods().get(0).getPlace(); // We will get a few possible results, simply take the first one
                LatLng latLng = place.getLatLng();
                addressLine = place.getAddress();
                moveCamera(latLng, 15f, addressLine);
                confirm.setVisibility(View.VISIBLE);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // sometimes this will be overtime so we give the user the option to try again
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

    /**
     * The initialization of the map feature
     */
    public void init() {
        // initialize the Places api feature with the api key, it is needed to fetch location and get the latlng information
        Places.initialize(MapActivity.this, getString(R.string.API_KEY));
        placesClient = Places.createClient(this);
        showMap();
        setAutoCompletion();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // the user clicks on this will return to the previous activity and also send this address line information to the previous activity
                Intent intent = new Intent();
                intent.putExtra("address_line", addressLine);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Setup the autocompletion fragment
     * It uses Google's autocompletion feature to search for places, and then uses Google Places api to fetch location of the selected search result
     */
    public void setAutoCompletion(){
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setCountries("CA"); // hardcoded to CA cause we are in Canada, not very flexible but at least it optimizes the experience for Canadian users
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                List<Place.Field> placeField = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);
                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(place.getId(), placeField).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng latLng = place.getLatLng();
                        addressLine = place.getAddress();
                        moveCamera(latLng, 15f, addressLine); // move the camera to the selected place and drop a pin point
                        confirm.setVisibility(View.VISIBLE); // display the confirm button
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MapActivity.this, "Fail to get the location", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(MapActivity.this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Let the map fragment to display the map.
     */
    public void showMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                GMap = googleMap;
                getCurrentLocation(); // as required in the user story, we set the default pin point to the user's current location
            }
        });
    }

    /**
     * Move the camera to a location and drop a pin point
     * @param latLng latitude and longitude of the location
     * @param zoom the amount of zooming
     * @param title the title for the pin point
     */
    private void moveCamera(LatLng latLng, float zoom, String title){
        GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);
        GMap.addMarker(markerOptions);
    }
}