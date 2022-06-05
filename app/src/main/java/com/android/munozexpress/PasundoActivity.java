package com.android.munozexpress;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

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
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
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
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class PasundoActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener{
    private static final int REQUEST_CODE = 5678;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    LocationRequest locationRequest;
    CardView findLoc , getCurLoc, destLoc;
    TextView userLocation,destLocation;
    EditText conNum,conPer,landMark,landmark1;
    Button btnOk, conInfBtn, book;
    double orlat,orlong,destlat,destlong;
    Dialog dialog, successDialog;
    Boolean clicked = false;
    Boolean clicked2 = false;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    double addfee;
    double km ;
    double dur;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA");
        setContentView(R.layout.activity_pasundo);
        userLocation = findViewById(R.id.userLocation);
        destLocation = findViewById(R.id.destLocation);

        findLoc = findViewById(R.id.findLoc);
        getCurLoc = findViewById(R.id.getCurLoc);
        destLoc = findViewById(R.id.destLoc);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        conInfBtn = findViewById(R.id.confirmInfo);
        conNum = findViewById(R.id.conNum);
        conPer = findViewById(R.id.cPerson);
        landMark = findViewById(R.id.landmark);
        landmark1 = findViewById(R.id.landmark1);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        dialog = new Dialog(this);
        successDialog = new Dialog(this);
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                conPer.setText(snapshot.child(user.getUid()).child("FirstName").getValue(String.class)+" "+snapshot.child(user.getUid()).child("LastName").getValue(String.class));
                conNum.setText(snapshot.child(user.getUid()).child("Phone").getValue(String.class).substring(3));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        conInfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String conNumText = conNum.getText().toString();
                String conPersonTxt = conPer.getText().toString();
                String landMarkTxt = landMark.getText().toString();
                String userCurLoc = userLocation.getText().toString();
                String landmark1Txt = landmark1.getText().toString();
                String dropOffLoc = destLocation.getText().toString();
                String uid = mAuth.getCurrentUser().getUid();
                if (landMarkTxt.isEmpty()) {
                    landMark.setError("Landmark is required");
                    landMark.requestFocus();
                }else if(landmark1Txt.isEmpty()) {
                    landmark1.setError("Landmark is required");
                    landmark1.requestFocus();
                }else if (!clicked) {
                    findLoc.requestFocusFromTouch();
                    warn();
                }
                else if (!clicked2) {
                    destLoc.requestFocusFromTouch();
                    warn();
                }
                else if (conPersonTxt.isEmpty()){
                    conPer.setError("Contact person can't be empty");
                    conPer.requestFocus();
                } else if(conNumText.isEmpty()){
                        conNum.setError("Enter contact number");
                        conNum.requestFocus();
                } else{
                    if(conNumText.length() < 9){
                    conNum.setError("Please input valid number");
                    conNum.requestFocus();
                }else{
                        dialog.setContentView(R.layout.confirm_order);
                        TextView orig = dialog.findViewById(R.id.origin);
                        orig.setText(userCurLoc);
                        TextView desti = dialog.findViewById(R.id.destinaton);
                        desti.setText(dropOffLoc);
                        LinearLayout item = dialog.findViewById(R.id.pabili);
                        LinearLayout item1 = dialog.findViewById(R.id.pabilitotal);
                        item1.setVisibility(View.GONE);
                        item.setVisibility(View.GONE);
                        dialog.setCanceledOnTouchOutside(false);
                        mapView = dialog.findViewById(R.id.mapView);
                        mapView.onCreate(savedInstanceState);
                        mapView.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {
                                        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
                                        mapboxMap.getUiSettings().setAttributionEnabled(false);
                                        mapboxMap.getUiSettings().setLogoEnabled(false);
                                        // Set the origin location
                                        origin = Point.fromLngLat(orlong, orlat);

                                        // Set the destination location
                                        destination = Point.fromLngLat(destlong, destlat);
                                        initSource(style);

                                        initLayers(style);

                                        // Get the directions route from the Mapbox Directions API
                                        getRoute(mapboxMap, origin, destination);
                                        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                                .include(new LatLng(orlat,orlong))
                                                .include(new LatLng(destlat,destlong))
                                                .build();

                                        mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 70));
                                    }
                                });
                            }
                        });

                        Button book = dialog.findViewById(R.id.book);

                        book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openSuccessDialog();
                                String TransId = UUID.randomUUID().toString();
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("CustomerId").setValue(uid);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("CustomerName").setValue(conPersonTxt);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("CustomerAddress").setValue(userCurLoc);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("CustomerNumber").setValue(conNumText);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Landmark").setValue(landMarkTxt);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("DropOffLocation").setValue(dropOffLoc);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("DropOffLandmark").setValue(landmark1Txt);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("ServiceType").setValue("Pasundo");
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("TransactionId").setValue(TransId);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Cancelled").setValue(0);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Completed").setValue(0);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("AssignedTo").setValue("");
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Ongoing").setValue(0);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("OrLat").setValue(orlat);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("OrLong").setValue(orlong);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("DestLat").setValue(destlat);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("DestLong").setValue(destlong);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("ServFee").setValue(addfee);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Duration").setValue(dur);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("Distance").setValue(km);
                                databaseReference.child("Transactions").child("Pasundo").child(TransId).child("TimePlaced").setValue(df.format(Calendar.getInstance().getTime()));
                            }
                        });

                        dialog.show();

                }
                }
            }
        });

        getCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    clicked = true;
                    getCurLoc();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        findLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked=true;
                goToPickerActivity();
            }
        });

        destLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked2 = true;
                goToPickerActivity1();
            }
        });
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[] {
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

        // Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(4f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

        // Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_baseline_location_on_24)));

        // Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[] {0f, -4f})));
    }

    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                TextView distance = dialog.findViewById(R.id.distance);
                TextView duration = dialog.findViewById(R.id.duration);

                km = currentRoute.distance()/1000;
                dur = currentRoute.duration();
                book = dialog.findViewById(R.id.book);
                if(km<120){
                    book.setEnabled(true);
                    book.setText("Confirm");
                }
                double hours = dur/ 3600;
                double minutes = (dur % 3600) / 60;

                distance.setText(String.format( " %.2f Km",currentRoute.distance()/1000));
                if(dur>3600){
                    duration.setText(String.format( " %.0f Hour %.0f mins",hours,minutes));
                }else{
                    duration.setText(String.format( " %.0f mins",minutes));
                }
                distance.setText(String.format( "%.2f Km",currentRoute.distance()/1000));

                TextView fee = dialog.findViewById(R.id.fee);
                addfee = 40.00;
                if(km>3){
                    addfee = addfee + ((km-3)*10);
                    fee.setText(String.format( "PHP %.2f",addfee));
                }else{
                    fee.setText(String.format( "PHP %.2f",addfee));
                }

                if (mapboxMap != null) {
                    mapboxMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            // Retrieve and update the source designated for showing the directions route
                            GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

                            // Create a LineString with the directions route's geometry and
                            // reset the GeoJSON source for the route LineLayer source
                            if (source != null) {
                                source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(getApplication(), "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAlertDialog(){
        successDialog.setContentView(R.layout.custom_layout_dialog);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCanceledOnTouchOutside(false);
        btnOk =  successDialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
            }
        });
        successDialog.show();
    }

    public void onback(View v) {
        dialog.dismiss();
    }

    private void goToPickerActivity1() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA")
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .country("PH")
                        .statusbarColor(Color.parseColor("#ff6c6d"))
                        .build(PlaceOptions.MODE_CARDS))
                .build(PasundoActivity.this);
        startActivityForResult(intent, 2);
    }

    private void openSuccessDialog(){
        successDialog.setContentView(R.layout.custom_success);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCanceledOnTouchOutside(false);
        btnOk = successDialog.findViewById(R.id.btnOki);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
                Intent i = new Intent(getApplicationContext(),HomePageActivity.class);        // Specify any activity here e.g. home or splash or login etc
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("EXIT", true);
                startActivity(i);
                finish();
            }
        });
        successDialog.show();
    }

    private void warn(){
       successDialog.setContentView(R.layout.custom_error);
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successDialog.setCanceledOnTouchOutside(false);
        btnOk = successDialog.findViewById(R.id.btnOks);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                successDialog.dismiss();
            }
        });
        successDialog.show();
    }


    private void goToPickerActivity() {

        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : "pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA")
                .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .limit(10)
                        .country("PH")
                        .statusbarColor(Color.parseColor("#ff6c6d"))
                        .build(PlaceOptions.MODE_CARDS))
                .build(PasundoActivity.this);
        startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);

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
                        Toast.makeText(PasundoActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
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
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);
            orlong = selectedCarmenFeature.center().longitude();
            orlat = selectedCarmenFeature.center().latitude();
            userLocation.setText(selectedCarmenFeature.placeName());
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
            destlong = selectedCarmenFeature.center().longitude();
            destlat = selectedCarmenFeature.center().latitude();
            destLocation.setText(selectedCarmenFeature.placeName());
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                            .zoom(14)
                            .build()), 4000);
        }
    }

    private void getCurLoc() throws IOException {
            openAlertDialog();
        Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
        orlat = lastKnownLocation.getLatitude();
       orlong = lastKnownLocation.getLongitude();
        Geocoder geocoder = new Geocoder(getApplicationContext() , Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(orlat,orlong,1);
        userLocation.setText(addresses.get(0).getAddressLine(0));
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng( orlat,
                                orlong))
                        .zoom(14)
                        .build()), 4000);
    }


    public void onClick(View v) {
        finish();
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
                if(isGPSEnabled()){
                    enableLocationComponent(style);
                }else{
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

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);


            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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
                    Toast.makeText(PasundoActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(PasundoActivity.this,2);
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


    private boolean isGPSEnabled(){
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if(locationManager==null){
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }

        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isEnabled;

    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
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


}