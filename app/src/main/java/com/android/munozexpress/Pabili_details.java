package com.android.munozexpress;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.MapboxNavigationActivity;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewModel;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class Pabili_details extends AppCompatActivity implements PermissionsListener {
    TextView transId , storeName, itemPurchase, estPrice , noteRider, storeLocation, storeLandmark ,status;
    TextView customerId, customerName , phoneNumber , customerLocation, customerLandmark , dur , dis ,placed;
    Button cancelOrder,acceptOrder ,track;
    String[] items =  {"Fake Order","I've waited too long" ,"I changed my mind" ,"Wrong Information" ,"Duplicate order" } ;
    AutoCompleteTextView autoCompleteTxt;
    ArrayAdapter<String> adapterItems;
    FirebaseAuth mAuth;
    String reason;
    Dialog dialog , trackDialog;
    String databaseUid;
    int loyal = 0;
    int f = 0 , e = 0;

    LocationRequest locationRequest;
    private PermissionsManager permissionsManager;
    Point origin , destination;
    double orlat,orlong , destlat , destlong;
    double  destinationLat, destionLong;

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String RED_ICON_ID = "RED_ICON_ID";
    private static final String YELLOW_ICON_ID = "YELLOW_ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String ICON_PROPERTY = "ICON_PROPERTY";
    private MapView mapView;
    private MapboxMap maps;
    private static final String SOURCE_ID1 = "SOURCE_ID1";
    private static final String DIRECTIONS_LAYER_ID = "DIRECTIONS_LAYER_ID";
    private static final String LAYER_BELOW_ID = "road-label-small";
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    String itemEstPrice;
    String servFee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_pabili_details);
        dialog = new Dialog(this);
        track = findViewById(R.id.track);
        trackDialog = new Dialog(this);
        status = findViewById(R.id.status);

        Intent in = getIntent();
        String transactId = in.getStringExtra("transId");

        cancelOrder = findViewById(R.id.cancelOrder);
        acceptOrder = findViewById(R.id.acceptOrder);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        ImageView dial = findViewById(R.id.dialPhone);
        ImageView goLoc = findViewById(R.id.goLoc);
        ImageView goLoc2 = findViewById(R.id.goLoc2);

        transId = findViewById(R.id.transactionId);
        storeName = findViewById(R.id.storeName);
        storeLocation = findViewById(R.id.storeLocation);
        storeLandmark = findViewById(R.id.storeLandmark);
        itemPurchase = findViewById(R.id.itemPurchase);
        estPrice = findViewById(R.id.estPrice);
        noteRider = findViewById(R.id.noteRider);
        dur = findViewById(R.id.duration);
        dis = findViewById(R.id.distance);
        placed = findViewById(R.id.placed);


        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Transactions");

        customerId = findViewById(R.id.customerId);
        customerName = findViewById(R.id.customerName);
        phoneNumber = findViewById(R.id.phoneNumber);
        customerLocation = findViewById(R.id.customerLocation);
        customerLandmark = findViewById(R.id.customerLandmark);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Transactions");


        rootRef.child("riders-id/"+ uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    if(uid.equals(snapshot.child("Uid").getValue().toString())){
                        acceptOrder.setVisibility(View.VISIBLE);
                        track.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        usersRef.child("Pabili/"+transactId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                finish();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usersRef.child("Pabili").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    if(datas.getKey().equals(transactId)){
                        if(Integer.parseInt( datas.child("Cancelled").getValue().toString()) == 0){
                            cancelOrder.setVisibility(View.VISIBLE);
                        }
                        if(Integer.parseInt( datas.child("Cancelled").getValue().toString()) == 1){
                            track.setVisibility(View.GONE);
                            acceptOrder.setVisibility(View.GONE);
                        }
                        if(Integer.parseInt( datas.child("Ongoing").getValue().toString()) == 1){
                            cancelOrder.setVisibility(View.GONE);
                        }
                        if(Integer.parseInt( datas.child("Completed").getValue().toString()) == 1){
                            cancelOrder.setVisibility(View.GONE);
                            acceptOrder.setVisibility(View.GONE);
                            track.setVisibility(View.GONE);
                        }

                        String cName =datas.child("CustomerName").getValue().toString();
                        databaseUid = datas.child("CustomerId").getValue().toString();
                        String cLoc = datas.child("CustomerLocation").getValue().toString();
                        String cNumber =  datas.child("CustomerNumber").getValue().toString();
                        String duration = datas.child("Duration").getValue().toString();
                        String distance =  datas.child("Distance").getValue().toString();
                        String time =  datas.child("TimePlaced").getValue().toString();
                        String cLandmark =  datas.child("Landmark").getValue().toString();
                        String sName =  datas.child("StoreName").getValue().toString();
                        String sLocation =  datas.child("StoreLocation").getValue().toString();
                        String sLandmark =  datas.child("StoreLandmark").getValue().toString();
                        String item =  datas.child("Items").getValue().toString();
                        itemEstPrice =  datas.child("EstPrice").getValue().toString();
                        servFee = datas.child("ServFee").getValue().toString();
                        String riderNote =  datas.child("NoteRider").getValue().toString();
                        orlat = Double.parseDouble(datas.child("OrLat").getValue().toString());
                        orlong = Double.parseDouble(datas.child("OrLong").getValue().toString());
                        destlat = Double.parseDouble(datas.child("DestLat").getValue().toString());
                        destlong = Double.parseDouble(datas.child("DestLong").getValue().toString());

                        dial.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:+63"+cNumber));
                                startActivity(intent);
                            }
                        });

                        track.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                trackDialog.setContentView(R.layout.track_location);
                                trackDialog.setCanceledOnTouchOutside(false);
                                mapView = trackDialog.findViewById(R.id.mapView);
                                mapView.onCreate(savedInstanceState);
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(@NonNull MapboxMap mapboxMap) {
                                        maps = mapboxMap;
                                        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                                            @Override
                                            public void onStyleLoaded(@NonNull Style style) {
                                                mapboxMap.getUiSettings().setAttributionEnabled(false);
                                                mapboxMap.getUiSettings().setLogoEnabled(false);

                                                if (isGPSEnabled()) {
                                                    enableLocationComponent(style , mapboxMap);
                                                } else {
                                                    turnOnGPS(style , mapboxMap);
                                                }

                                            }
                                        });
                                    }


                                });
                                trackDialog.show();
                            }
                        });
                        goLoc2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" +cLoc);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);

                            }
                        });
                        goLoc.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" +sLocation);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        });

                        if(Integer.parseInt( datas.child("Ongoing").getValue().toString()) == 1){
                            status.setText("Ongoing");

                        }
                        if(Integer.parseInt( datas.child("Completed").getValue().toString()) == 1){
                            status.setText("Completed");
                        }
                        if(Integer.parseInt( datas.child("Cancelled").getValue().toString()) == 1){
                            status.setText("Cancelled");
                        }

                        if(Integer.parseInt( datas.child("Ongoing").getValue().toString()) == 0 && Integer.parseInt( datas.child("Cancelled").getValue().toString()) == 0 &&Integer.parseInt( datas.child("Completed").getValue().toString()) == 0){
                            status.setText("Waiting for confirmation");

                        }
                       storeName.setText(sName);
                       itemPurchase.setText(item);
                       estPrice.setText("₱ "+itemEstPrice);
                       noteRider.setText(riderNote);
                       storeLocation.setText(sLocation);
                       storeLandmark.setText(sLandmark);
                        transId.setText(datas.getKey());
                       customerId.setText(databaseUid);
                       customerName.setText(cName);
                       phoneNumber.setText("+63"+cNumber);
                       customerLandmark.setText(cLandmark);
                       customerLocation.setText(cLoc);

                        try {
                            Date date = parseDate(time, "yyyy-MM-dd hh:mm:ss");
                            DateFormat df = new SimpleDateFormat("EEE, MMMM dd, yyyy - hh:mm a");
                           placed.setText(df.format(date).toString());
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }


                        dis.setText(String.format( "%.2f Km",Double.parseDouble(distance)));
                       if(Double.parseDouble(duration)>3600){
                           dur.setText(String.format( "%.0f Hour %.0f minutes",Double.parseDouble(duration)/3600,(Double.parseDouble(duration) % 3600) / 60));
                       }else{
                           dur.setText(String.format( "%.0f minutes",(Double.parseDouble(duration) % 3600) / 60));
                       }

                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.cancel_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);
                Button submit = dialog.findViewById(R.id.submit);
                Button close = dialog.findViewById(R.id.close);
                autoCompleteTxt = dialog.findViewById(R.id.auto_complete_txt);

                adapterItems = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_item,items);
                autoCompleteTxt.setAdapter(adapterItems);

                autoCompleteTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        reason = parent.getItemAtPosition(position).toString();
                        autoCompleteTxt.setError(null);
                    }
                });
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(reason == null){
                            autoCompleteTxt.requestFocus();
                            autoCompleteTxt.setError("Choose reason");
                        }else {
                            usersRef.child("Pabili").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                                        if(datas.getKey().equals(transactId)){
                                            reference.child("Pabili").child(datas.getKey()).child("Cancelled").setValue(1);
                                            reference.child("Pabili").child(datas.getKey()).child("CancelReason").setValue(reason);
                                            finish();
                                            dialog.dismiss();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }


                    }
                });

               dialog.show();
            }
        });

        acceptOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.total_cost);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);

                Button submit = dialog.findViewById(R.id.submit);
                Button close = dialog.findViewById(R.id.close);
                EditText sfee = dialog.findViewById(R.id.sfee);
                TextView fee = dialog.findViewById(R.id.fee);
                TextView expenses = dialog.findViewById(R.id.expenses);
                TextView  lpoint = dialog.findViewById(R.id.lpoint);
                TextView itemfee = dialog.findViewById(R.id.est);

                itemfee.setText(String.format( "PHP %.2f",Double.parseDouble(servFee)));


                rootRef.child("users/"+databaseUid+"/LoyalPoints").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       loyal  = Integer.parseInt(snapshot.getValue().toString());
                        lpoint.setText(String.valueOf(loyal));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                sfee.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                       f = Integer.parseInt(sfee.getText().toString().equals("") ?"0" :sfee.getText().toString());
                       if(loyal>=4){
                           f = f/2;
                           fee.setText("PHP "+String.valueOf((f)+e) +".00");
                           lpoint.setText("5 = 50% off service fee");
                       }else{
                           fee.setText("PHP "+String.valueOf(f+e) +".00");
                           lpoint.setText(String.valueOf(loyal) +"+1 Point");
                       }

                    }
                });

                expenses.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        e = Integer.parseInt(expenses.getText().toString().equals("") ?"0" :expenses.getText().toString());
                        if(loyal>=4){
                            fee.setText("PHP "+String.valueOf((f)+e) +".00");
                            lpoint.setText("5 = 50% off service fee");
                        }else{
                            fee.setText("PHP "+String.valueOf(f+e) +".00");
                            lpoint.setText(String.valueOf(loyal) +"+1 Point");
                        }
                    }

                });


                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(f == 0){
                            sfee.setError("Please Input Fee");
                            sfee.requestFocus();
                        }
                        else if (e == 0){
                            expenses.setError("Please Input Fee");
                            expenses.requestFocus();
                        }
                        else{
                            usersRef.child("Pabili").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot datas: dataSnapshot.getChildren()){
                                        if(datas.getKey().equals(transactId)){
                                            reference.child("Pabili").child(datas.getKey()).child("Completed").setValue(1);
                                            reference.child("Pabili").child(datas.getKey()).child("Ongoing").setValue(0);
                                            reference.child("Pabili").child(datas.getKey()).child("ServiceFee").setValue(f);
                                            reference.child("Pabili").child(datas.getKey()).child("OrderExpense").setValue(e);
                                            reference.child("Pabili").child(datas.getKey()).child("TotalCost").setValue(f+e);
                                            reference.child("Pabili").child(datas.getKey()).child("TimeCompleted").setValue(df.format(Calendar.getInstance().getTime()));
                                            rootRef.child("riders-id/"+uid+"/Idle").setValue(0);
                                            if(loyal>=4){
                                                rootRef.child("users/"+databaseUid+"/LoyalPoints").setValue(0);
                                            }else{
                                                rootRef.child("users/"+databaseUid+"/LoyalPoints").setValue(loyal+1);
                                            }
                                            finish();
                                            dialog.dismiss();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    private Date parseDate(String date, String format) throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }


    private void initDottedLineSourceAndLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(SOURCE_ID1));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        DIRECTIONS_LAYER_ID, SOURCE_ID1).withProperties(
                        lineCap(Property.LINE_CAP_ROUND),
                        lineJoin(Property.LINE_JOIN_ROUND),
                        lineWidth(5f),
                        lineColor(Color.parseColor("#009688"))
                ), LAYER_BELOW_ID);
    }


    private void drawNavigationPolylineRoute(final DirectionsRoute route ,MapboxMap maps) {
        if (maps != null) {
           maps.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> coordinates = lineString.coordinates();
                    for (int i = 0; i < coordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)));
                    }
                    dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs(SOURCE_ID1);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }


    @SuppressWarnings( {"MissingPermission"})
    private void getRoute(Point destination , MapboxMap map) {
        Location lastKnownLocation = map.getLocationComponent().getLastKnownLocation();
        MapboxDirections client = MapboxDirections.builder()
                .origin(Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude()))
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();
        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

                drawNavigationPolylineRoute(response.body().routes().get(0), map);
                TextView distances = trackDialog.findViewById(R.id.distance);
                distances.setText(String.format( "%.2f Km",response.body().routes().get(0).distance()/1000));
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
            }
        });
    }
    private List<Feature> initCoordinateData() {
        Feature singleFeatureOne = Feature.fromGeometry(
                Point.fromLngLat(orlong,
                        orlat));
        singleFeatureOne.addStringProperty(ICON_PROPERTY, RED_ICON_ID);

        Feature singleFeatureTwo = Feature.fromGeometry(
                Point.fromLngLat(destlong,
                        destlat));
        singleFeatureTwo.addStringProperty(ICON_PROPERTY, YELLOW_ICON_ID);

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(singleFeatureOne);
        symbolLayerIconFeatureList.add(singleFeatureTwo);
        return symbolLayerIconFeatureList;
    }


    private boolean handleClickIcon(PointF screenPoint, MapboxMap map , LatLng nav) {
        List<Feature> features = map.queryRenderedFeatures(screenPoint, LAYER_ID);
        if (!features.isEmpty()) {
            getRoute(Point.fromLngLat(nav.getLongitude(),nav.getLatitude()) , map);
            destinationLat = nav.getLatitude();
            destionLong = nav.getLongitude();
            Button navigate = trackDialog.findViewById(R.id.navigate);
            navigate.setEnabled(true);
            navigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acceptOrder.setEnabled(true);
                    navigationRoute(map);
                }
            });
            return true;
        } else {
            return false;
        }
    }


    private void navigationRoute(MapboxMap map){
        Location lastKnownLocation = map.getLocationComponent().getLastKnownLocation();
        origin = Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
        destination = Point.fromLngLat(destionLong,destinationLat);
        NavigationRoute.builder(this)
                .accessToken("pk.eyJ1IjoieXZudHJpeCIsImEiOiJja3d3ZmJzdzEwMzJhMm5sYXhhOGk2aHFmIn0.eEYfgb1hmDXj1XsutR-eGA")
                .origin(origin)
                .destination(destination)
                .voiceUnits(DirectionsCriteria.METRIC)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        Timber.d("Response code: " + response.code());
                        if (response.body() == null) {
                            Toast.makeText(getApplication(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(getApplication(), "No routes found.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DirectionsRoute route = response.body().routes().get(0);

                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(route)
                                .build();

                        NavigationLauncher.startNavigation(Pabili_details.this,options);

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }


    public void onClick(View v) {
        finish();
    }
    public void onback(View v) {
      trackDialog.dismiss();
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle, MapboxMap mapboxMap) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .build();

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);

            locationComponent.setRenderMode(RenderMode.COMPASS);
            loadedMapStyle.addImage(RED_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                    getResources().getDrawable(R.drawable.ic_twotone_location_on_24)));

            loadedMapStyle.addImage(YELLOW_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                    getResources().getDrawable(R.drawable.ic_twotone_location_on_24_blue)));

            loadedMapStyle.addSource(new GeoJsonSource(SOURCE_ID,
                    FeatureCollection.fromFeatures(initCoordinateData())));

            loadedMapStyle.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                    .withProperties(iconImage(match(
                            get(ICON_PROPERTY), literal(RED_ICON_ID),
                            stop(YELLOW_ICON_ID, YELLOW_ICON_ID),
                            stop(RED_ICON_ID, RED_ICON_ID))),
                            iconAllowOverlap(true),
                            iconAnchor(Property.ICON_ANCHOR_BOTTOM))
            );

            initDottedLineSourceAndLayer(loadedMapStyle);

            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(new LatLng(orlat, orlong))
                    .include(new LatLng(destlat, destlong))
                    .build();

            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 70));

            mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                @Override
                public boolean onMapClick(@NonNull LatLng point) {
                    return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point), mapboxMap, point);
                }
            });
        }else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void turnOnGPS(Style style , MapboxMap map) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                enableLocationComponent(style , map);
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(Pabili_details.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {

                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(Pabili_details.this, 2);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Toast.makeText(Pabili_details.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Toast.makeText(Pabili_details.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                       trackDialog.dismiss();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
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
           maps.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style , maps);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

}