package com.android.munozexpress;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;

import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PabiliActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener {

    private static final int REQUEST_CODE = 5678;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    LocationRequest locationRequest;
    CardView getCurLoc, storeLoc  , dropLoc;
    TextView userLocation, storeLocation;
    EditText conNum, conPer, landMark, storeLandmark, storeName;
    Button btnOk, conInfBtn;
    Dialog dialog;
    Boolean clicked = false;
    Boolean clicked2 = false;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    double orlat,orlong,destlat,destlong;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA");
        setContentView(R.layout.activity_pabili);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users");
        dialog = new Dialog(this);

        userLocation = findViewById(R.id.getCurLoc);
        storeLocation = findViewById(R.id.getStoreLoc);
        dropLoc = findViewById(R.id.dropLoc);

        conInfBtn = findViewById(R.id.confirmInfo);
        conNum = findViewById(R.id.conNum);
        conPer = findViewById(R.id.cPerson);
        landMark = findViewById(R.id.landmark);
        storeLandmark = findViewById(R.id.storeLandmark);
        storeName = findViewById(R.id.storeName);


        getCurLoc = findViewById(R.id.findLoc);
        storeLoc = findViewById(R.id.findStore);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conPer.setText(snapshot.child(user.getUid()).child("FirstName").getValue(String.class) + " " + snapshot.child(user.getUid()).child("LastName").getValue(String.class));
                conNum.setText(snapshot.child(user.getUid()).child("Phone").getValue(String.class).substring(3));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getCurLoc();
                    clicked = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        dropLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA")
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .country("PH")
                                .statusbarColor(Color.parseColor("#ff6c6d"))
                                .build(PlaceOptions.MODE_CARDS))
                        .build(PabiliActivity.this);
                startActivityForResult(intent,2);
            }
        });

        storeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA")
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .country("PH")
                                .statusbarColor(Color.parseColor("#ff6c6d"))
                                .build(PlaceOptions.MODE_CARDS))
                        .build(PabiliActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

            }
        });



        conInfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String conNumText = conNum.getText().toString();
                String conPersonTxt = conPer.getText().toString();
                String landMarkTxt = landMark.getText().toString();
                String storeLandmarkTxt = storeLandmark.getText().toString();
                String storeNameTxt = storeName.getText().toString();
                String userLoc = userLocation.getText().toString();
                String storeLocs = storeLocation.getText().toString();
                if (landMarkTxt.isEmpty()) {
                    landMark.setError("Landmark is required");
                    landMark.requestFocus();
                } else if (storeLandmarkTxt.isEmpty()) {
                    storeLandmark.setError("Store Landmark can't be empty");
                    storeLandmark.requestFocus();
                } else if (!clicked) {
                    getCurLoc.requestFocusFromTouch();
                    warn();
                } else if (!clicked2) {
                    storeLoc.requestFocusFromTouch();
                    warn();
                } else if (storeNameTxt.isEmpty()) {
                    storeName.setError("Store name is required");
                    storeName.requestFocus();
                } else if (conPersonTxt.isEmpty()) {
                    conPer.setError("Contact person can't be empty");
                    conPer.requestFocus();
                } else if (conNumText.isEmpty()) {
                    conNum.setError("Enter contact number");
                    conNum.requestFocus();
                } else {
                    if (conNumText.length() < 9) {
                        conNum.setError("Please input valid number");
                        conNum.requestFocus();
                    } else {
                        Intent in = new Intent(getApplicationContext(), Pabili_confirmation.class);
                        in.putExtra("userLocation", userLoc);
                        in.putExtra("landMark", landMarkTxt);
                        in.putExtra("storeLocation", storeLocs);
                        in.putExtra("storeLandmark", storeLandmarkTxt);
                        in.putExtra("storeName", storeNameTxt);
                        in.putExtra("contactNumber", conNumText);
                        in.putExtra("contactPerson", conPersonTxt);
                        in.putExtra("orlat", orlat);
                        in.putExtra("orlong",orlong);
                        in.putExtra("destlat",destlat);
                        in.putExtra("destlong",destlong);
                        startActivity(in);
                    }

                }
            }
        });
    }


    private void warn() {
        dialog.setContentView(R.layout.custom_error);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        btnOk = dialog.findViewById(R.id.btnOks);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openAlertDialog() {
        dialog.setContentView(R.layout.custom_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.getUiSettings().setAllGesturesEnabled(false);
                mapboxMap.getUiSettings().setAttributionEnabled(false);
                mapboxMap.getUiSettings().setLogoEnabled(false);
                if (isGPSEnabled()) {

                    enableLocationComponent(style);
                } else {
                    turnOnGPS(style);
                }
            }
        });
    }



    @SuppressLint("MissingPermission")
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .build();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);


        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    private void getCurLoc() throws IOException {
            openAlertDialog();
        Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
        orlat = lastKnownLocation.getLatitude();
        orlong = lastKnownLocation.getLongitude();
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(orlat, orlong, 1);
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(orlat,
                                orlong))
                        .zoom(14)
                        .build()), 4000);
        userLocation.setText(addresses.get(0).getAddressLine(0));
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Toast.makeText(PabiliActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
                openAlertDialog();
            clicked2 = true;
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            storeLocation.setText(selectedCarmenFeature.placeName());
            destlat = ((Point) selectedCarmenFeature.geometry()).latitude();
            destlong = ((Point) selectedCarmenFeature.geometry()).longitude();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                            .zoom(14)
                            .build()), 4000);


        }
        if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            openAlertDialog();
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            userLocation.setText(selectedCarmenFeature.placeName());
            orlat = ((Point) selectedCarmenFeature.geometry()).latitude();
            orlong = ((Point) selectedCarmenFeature.geometry()).longitude();
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                            .zoom(14)
                            .build()), 4000);


        }
    }

    private void turnOnGPS(Style style) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                enableLocationComponent(style);

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(PabiliActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(PabiliActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }

        });

    }


    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    public void onClick(View v) {
        finish();
    }
}

