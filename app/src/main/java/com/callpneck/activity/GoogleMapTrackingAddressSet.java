package com.callpneck.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoogleMapTrackingAddressSet extends AppCompatActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
private boolean mLocationPermissionGranted = true;
private FusedLocationProviderClient mFusedLocationProviderClient;
private Location mLastKnownLocation = null;
//not initilized till now
private LatLng mDefaultLocation;
private float DEFAULT_ZOOM = 17.0f;

private static final String TAG = "GoogleMapActivity";
private GoogleMap mMap;
private LatLng mCenterLatLong;
private JSONArray ShopAddressList = new JSONArray();
private Marker locationMarker;


private LinearLayout AddressView;
private TextView DoneSaveAddress;
//private TextView WorkAddressTypeHeading;
//private TextView OtherAddressTypeHeading;
//private TextView LocalityArea;
private EditText CompleteAddress;



private String city = "";
private String state = "";
private String SubAdminAreaAfterState = "";
private String country = "";
private String postalCode = "";
private String knownName = "";
private String locality = "";
private String UserLatitude = "";
private String UserLongitude = "";


private ArrayList<String> ShopAreaList = new ArrayList();


private double mLatitude, mLongitude;
private String PrevAddress;

private SessionManager sessionManager;

private boolean IsFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_tracking_address_set);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        IsFromLogin = getIntent().getBooleanExtra("is_form_login", false);

        sessionManager = new SessionManager(this);
        findViews();
        clickListeners();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void SaveUserAddress() {

        Intent intent=new Intent();
        intent.putExtra("user_latitude",UserLatitude);
        intent.putExtra("user_longitude",UserLongitude);
        intent.putExtra("user_complete_address",CompleteAddress.getText().toString());
        setResult(2,intent);
        finish();
    }


    private void findViews() {
        AddressView = (LinearLayout) findViewById(R.id.Address_view);
        DoneSaveAddress = (TextView) findViewById(R.id.done_save_address);
        //LocalityArea = (TextView)findViewById( R.id.Locality_area );
        CompleteAddress = (EditText) findViewById(R.id.Complete_address);

    }


    private void clickListeners() {

        DoneSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CompleteAddress.getText().length() > 5) {
                    SaveUserAddress();
                } else {
                    CompleteAddress.setError("Enter valid address");
                }
            }
        });

        /*WorkAddressTypeHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDRESS_TYPE_SELECTED=2;
                showFinalAddressSubmition();
            }
        });

        OtherAddressTypeHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDRESS_TYPE_SELECTED=3;
                showFinalAddressSubmition();
            }
        });
        */
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //refer to link https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial

        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        /*if (PrevAddress != null && PrevAddress.length() > 3) {
            MarkerOptions markerOptions = new MarkerOptions();

            // Setting the position for the marker
            markerOptions.position(new LatLng(mLatitude, mLongitude));

            markerOptions.icon(PublicMethods.
                    convertToBitmapFromVector(GoogleMapTrackingAddressSet.this,
                            R.drawable.ic_placeholder))
                    .title("Previous location");
            markerOptions.snippet(PrevAddress);

            // Clears the previously touched position
            mMap.clear();

            // Animating to the touched position
            //mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(mLatitude,mLongitude)));

            // Placing a marker on the touched position

            locationMarker = mMap.addMarker(markerOptions);
            locationMarker.setDraggable(true);
            locationMarker.showInfoWindow();

        }*/


    }

    @Override
    public void onCameraIdle() {
        Log.e("skjlksfjsfs", "camera is in ideal state");
        if (mLastKnownLocation == null) {
            getDeviceLocation();
        } else {
            CameraPosition cameraPosition = mMap.getCameraPosition();
            mCenterLatLong = cameraPosition.target;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getCompleteAddressString(mCenterLatLong.latitude, mCenterLatLong.longitude);

                }
            }, 100);
        }
    }


    private void getCompleteAddressString(double latitude, double longitude) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 3);

            if (addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
                locality = addresses.get(0).getSubLocality();
                UserLatitude = "" + latitude;
                UserLongitude = "" + longitude;

                UserLatitude = "" + latitude;
                UserLongitude = "" + longitude;


                for (int i = 0; i < addresses.size(); i++) {

                    if (addresses.get(i).getSubLocality() != null && addresses.get(i).getSubLocality().length() > 1) {

                        if (locality == null || locality.length() == 0) {
                            locality = addresses.get(i).getSubLocality();
                        }

                        if (!ShopAreaList.contains(addresses.get(i).getSubLocality())) {
                            ShopAreaList.add(addresses.get(i).getSubLocality());
                        }
                    }
                    if (addresses.get(i).getLocality() != null && addresses.get(i).getLocality().length() > 1) {
                        if (city == null || city.length() == 0) {
                            city = addresses.get(i).getLocality();
                        }
                        if (!ShopAreaList.contains(addresses.get(i).getLocality())) {
                            ShopAreaList.add(addresses.get(i).getLocality());
                        }
                    }
                    if (addresses.get(i).getFeatureName() != null && addresses.get(i).getFeatureName().length() > 1) {
                        if (!ShopAreaList.contains(addresses.get(i).getFeatureName())) {
                            ShopAreaList.add(addresses.get(i).getFeatureName());
                        }
                    }
                    if (addresses.get(i).getCountryName() != null && addresses.get(i).getCountryName().length() > 1) {
                        if (!ShopAreaList.contains(addresses.get(i).getCountryName())) {
                            ShopAreaList.add(addresses.get(i).getCountryName());
                        }
                    }
                    if (addresses.get(i).getAdminArea() != null && addresses.get(i).getAdminArea().length() > 1) {
                        if (!ShopAreaList.contains(addresses.get(i).getAdminArea())) {
                            ShopAreaList.add(addresses.get(i).getAdminArea());
                        }
                    }
                    if (addresses.get(i).getPremises() != null && addresses.get(i).getPremises().length() > 1) {

                        if (locality == null || locality.length() == 0) {
                            locality = addresses.get(i).getPremises();
                        }

                        if (!ShopAreaList.contains(addresses.get(i).getPremises())) {
                            ShopAreaList.add(addresses.get(i).getPremises());
                        }
                    }
                    if (addresses.get(i).getSubAdminArea() != null && addresses.get(i).getSubAdminArea().length() > 1) {
                        if (city == null || city.length() == 0) {
                            city = addresses.get(i).getLocality();
                        }
                        if (!ShopAreaList.contains(addresses.get(i).getSubAdminArea())) {
                            ShopAreaList.add(addresses.get(i).getSubAdminArea());
                        }
                    }
                }


                CompleteAddress.setText(address);
                CompleteAddress.setSelection(CompleteAddress.getText().length());
            }


        } catch (Exception e) {
            Log.e("kjfksfsfsf", "this is error exception " + e.getMessage());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocation();
    }

    private void requestLocation() {

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(GoogleMapTrackingAddressSet.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void getDeviceLocation() {
        Log.e("skjlksfjsfs","inside getDeviceLocation ");
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation!=null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                getCompleteAddressString(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        //mMap.clear();
    }

}