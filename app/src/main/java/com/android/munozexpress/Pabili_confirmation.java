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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Pabili_confirmation extends AppCompatActivity {
    EditText itemPurchase,estPrice,noteRider;
    Button confirmPabiliBtn,btnOk , book;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://munosxpress-default-rtdb.firebaseio.com/");
    FirebaseAuth mAuth;
    Dialog dialog , successDialog;
    double orlat,orlong,destlat,destlong;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    String estPriceText;
    double addfee;
    double km ;
    double dur ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_pabili_confirmation);
        itemPurchase = findViewById(R.id.itemPurchase);
        estPrice = findViewById(R.id.estPrice);
        noteRider = findViewById(R.id.noteRider);
        confirmPabiliBtn = findViewById(R.id.confirmPabili);
        Intent in = getIntent();
        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(this);

        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        confirmPabiliBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemPurchaseText = itemPurchase.getText().toString();
                estPriceText = estPrice.getText().toString();
                String noteRiderText = noteRider.getText().toString();
                orlat = in.getDoubleExtra("orlat" ,0);
                orlong = in.getDoubleExtra("orlong" ,0);
                destlat = in.getDoubleExtra("destlat" ,0);
                destlong = in.getDoubleExtra("destlong" ,0);
                String userLocation = in.getStringExtra("userLocation");
                String landmark = in.getStringExtra("landMark");
                String storeLocation = in.getStringExtra("storeLocation");
                String storeLandmark = in.getStringExtra("storeLandmark");
                String storeName = in.getStringExtra("storeName");
                String contactNumber = in.getStringExtra("contactNumber");
                String contactPerson = in.getStringExtra("contactPerson");
                String uid = mAuth.getCurrentUser().getUid();
                if(itemPurchaseText.isEmpty()){
                    itemPurchase.setError("Cant be empty");
                    itemPurchase.requestFocus();
                }else if(estPriceText.isEmpty()){
                    estPrice.setError("Price is needed");
                    estPrice.requestFocus();
                }else if(noteRiderText.isEmpty()){
                    noteRider.setError("Notes is required");
                    noteRider.requestFocus();
                }else{
                    if(Integer.parseInt(estPriceText)> 5000){
                        estPrice.setError("Price must not exceed 5000");
                        estPrice.requestFocus();
                    }else{

                        dialog.setContentView(R.layout.confirm_order);
                        TextView orig = dialog.findViewById(R.id.origin);
                        orig.setText(userLocation);
                        TextView desti = dialog.findViewById(R.id.destinaton);
                        desti.setText(storeLocation);
                        TextView itemprice = dialog.findViewById(R.id.itemprice);

                        itemprice.setText("PHP "+estPriceText+".00");
                        dialog.setCanceledOnTouchOutside(false);
                        book = dialog.findViewById(R.id.book);
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


                        book.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog();
                                String TransId = UUID.randomUUID().toString();
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("CustomerId").setValue(uid);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("CustomerName").setValue(contactPerson);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("CustomerNumber").setValue(contactNumber);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("CustomerLocation").setValue(userLocation);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Landmark").setValue(landmark);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("StoreLocation").setValue(storeLocation);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("StoreLandmark").setValue(storeLandmark);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("StoreName").setValue(storeName);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Items").setValue(itemPurchaseText);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("EstPrice").setValue(estPriceText);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("NoteRider").setValue(noteRiderText);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("ServiceType").setValue("Pabili");
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("TransactionId").setValue(TransId);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Cancelled").setValue(0);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Completed").setValue(0);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("AssignedTo").setValue("");
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Ongoing").setValue(0);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("OrLat").setValue(orlat);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("OrLong").setValue(orlong);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("DestLat").setValue(destlat);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("DestLong").setValue(destlong);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("ServFee").setValue(addfee);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Duration").setValue(dur);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("Distance").setValue(km);
                                databaseReference.child("Transactions").child("Pabili").child(TransId).child("TimePlaced").setValue(df.format(Calendar.getInstance().getTime()));
                            }
                        });

                        dialog.show();

                    }
                }
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

                distance.setText(String.format( " %.2f Km",km));
                if(dur>3600){
                    duration.setText(String.format( " %.0f Hour %.0f mins",hours,minutes));
                }else{
                    duration.setText(String.format( " %.0f mins",minutes));
                }

                TextView fee = dialog.findViewById(R.id.fee);
                TextView totalfee = dialog.findViewById(R.id.totalfee);
                addfee = 40.00;
                if(km>3){
                    addfee = addfee + ((km-3)*10);
                    fee.setText(String.format( "PHP %.2f",addfee));
                    totalfee.setText(String.format( "PHP %.2f",addfee+Integer.parseInt(estPriceText)));
                }else{
                    totalfee.setText(String.format( "PHP %.2f",addfee+Integer.parseInt(estPriceText)));
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

    private void alertDialog(){
        successDialog = new Dialog(this);
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



    public void onClick(View v) {
        finish();
    }

    public void onback(View v) {
       dialog.dismiss();
    }
}