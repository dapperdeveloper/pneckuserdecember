package com.callpneck.activity.deliveryboy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Activity.TransferMoneyActivity;
import com.callpneck.model.MapPointerModel;
import com.callpneck.utils.InternetConnection;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;
import com.shivtechs.maplocationpicker.MapUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.callpneck.activity.registrationSecond.helper.Constant.LAUNCH_ADDRESS_SET_SCREEN;
import static com.callpneck.activity.registrationSecond.helper.Constant.REQUEST_CHECK_SETTINGS;

public class DeliveryMainActivity extends AppCompatActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback, View.OnClickListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    //not initilized till now
    private LatLng mDefaultLocation;
    private float DEFAULT_ZOOM = 15.0f;

    private static final String TAG = "GoogleMapActivity";
    private GoogleMap mMap;
    private LatLng mCenterLatLong;
    private Marker locationMarker;

    private TextView CompleteAddress, destinationAddress;
    private CardView mCurrentLocationBtn, confirmBtn, skipBtn;
    RelativeLayout destinationLayout;
    LinearLayout layout_pick_up, layout_drop_off;

    private String city = "";
    private String state = "";
    private String SubAdminAreaAfterState = "";
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private String locality = "";
    private String currentFullAddress="";
    private String UserLatitude = "";
    private String UserLongitude = "";
    private String dropAddress="";

    private String dropLatitude = "";
    private String dropLongitude = "";
    Map<String, MapPointerModel> allMarkersMap = new HashMap<>();
    private ArrayList<MapPointerModel> mapsList=new ArrayList<>();
    private SessionManager sessionManager;

    public static final int PERMISSIONS_REQUEST_TOKEN = 2001;
    private static final int LOCATION_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_main);

        mCurrentLocationBtn = findViewById(R.id.get_device_location);
        CompleteAddress = findViewById(R.id.Complete_address);
        destinationAddress = findViewById(R.id.txt_to);
        confirmBtn = findViewById(R.id.confirmBtn);
        destinationLayout = findViewById(R.id.layout_to_address);
        layout_pick_up = findViewById(R.id.layout_pick_up);
        layout_drop_off  = findViewById(R.id.layout_drop_off);
        skipBtn = findViewById(R.id.card_skip);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //if permission given
        //initialized addres search...
        MapUtility.apiKey = getResources().getString(R.string.google_api_key);

        mCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        confirmBtn.setOnClickListener(this);
       skipBtn.setOnClickListener(this);
        layout_drop_off.setOnClickListener(this);
        destinationLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==confirmBtn){
            if (currentFullAddress!= null){
                destinationLayout.setVisibility(View.VISIBLE);
                layout_pick_up.setVisibility(View.GONE);
                layout_drop_off.setVisibility(View.VISIBLE);
            }else {
                destinationLayout.setVisibility(View.GONE);
            }
        }
        if (v == skipBtn){
            layout_pick_up.setVisibility(View.VISIBLE);
            layout_drop_off.setVisibility(View.GONE);
            destinationLayout.setVisibility(View.GONE);
        }
        if (v==destinationLayout){
            if (checkPermission()){
                settingsrequest();
            }else {
                askForPermission();
            }
        }
        if (v == layout_drop_off){
            if (TextUtils.isEmpty(currentFullAddress)){
                StyleableToast.makeText(DeliveryMainActivity.this, "Enter Current Address", Toast.LENGTH_LONG, R.style.mytoast).show();
                return;
            }
            if (TextUtils.isEmpty(dropAddress)){
                StyleableToast.makeText(DeliveryMainActivity.this, "Enter Drop Address", Toast.LENGTH_LONG, R.style.mytoast).show();
                return;
            }

            Bundle bundle=new Bundle();
            bundle.putString("pickupAddress",currentFullAddress);
            bundle.putString("dropAddress",dropAddress);
            bundle.putString("UserLatitude",UserLatitude);
            bundle.putString("UserLongitude",UserLongitude);
            LaunchActivityClass.LaunchDeliveryBoyListActivity(DeliveryMainActivity.this, bundle);

        }
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat
                    .checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                    .checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }
    public void askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat
                    .checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.FOREGROUND_SERVICE)+
                    ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                    .checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (this, Manifest.permission.FOREGROUND_SERVICE)||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.main_layout), getString(R.string.PERMISSION_SNACK_BAR_MESSAGE), Snackbar.LENGTH_LONG);
                    TextView snack_tv = (TextView)snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.primary_800));
                    snack_tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    snack_tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.setAction(R.string.ENABLE, new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.FOREGROUND_SERVICE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_TOKEN);
                        }
                    });
                    snackbar.show();

                }else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.FOREGROUND_SERVICE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_TOKEN);
                }

            }
        }else {
            if (!GPSstatusCheck()) {
                settingsrequest();
            }
        }
    }
    public boolean GPSstatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {

            case PERMISSIONS_REQUEST_TOKEN:
                if (grantResults.length> 0) {
                    boolean locationPemission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationPemission){
                        if (InternetConnection.checkConnection(this)){

                            settingsrequest();

                        }else {
                            Toast.makeText(this,getResources().getString(R.string.CHECK_YOUR_INTERNET_CONNECTION),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this,"Permission Denied can't post Global post.",Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
            break;
        }
    }

    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    public void settingsrequest() {

        mSettingsClient = LocationServices.getSettingsClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("TAG", "All location settings are satisfied.");


                        //noinspection MissingPermission
                        launchLocation();

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("TAG", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(DeliveryMainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("TAG", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("TAG", errorMessage);

                                Toast.makeText(DeliveryMainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }




    private void launchLocation(){

        LaunchActivityClass.LaunchAddressSetActivity(DeliveryMainActivity.this);

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHECK_SETTINGS){
            if (resultCode==RESULT_OK) {
                launchLocation();
            }else if (resultCode==RESULT_CANCELED){
                Log.e("TAG","location enabl cancled"+data.getExtras());
            }
        }
        else if (requestCode==LAUNCH_ADDRESS_SET_SCREEN){
            if (data!=null&&data.hasExtra("user_complete_address")){
                String address =data.getStringExtra("user_complete_address");

                dropLatitude=data.getStringExtra("user_latitude");
                dropLongitude=data.getStringExtra("user_longitude");


                dropAddress = address;
                destinationAddress.setText(dropAddress);
            }
        }
    }

    //end user current location

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
        LocationServices.getFusedLocationProviderClient(DeliveryMainActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
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
                            Toast.makeText(DeliveryMainActivity.this, "Getting Current Location...", Toast.LENGTH_SHORT).show();
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
            Log.e("location_get_error", e.getMessage());
        }
    }

    private void getCompleteAddressString(double latitude, double longitude) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

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



                currentFullAddress=address;
                CompleteAddress.setText(currentFullAddress);

                Log.e("CURRENTADDRESS","Latitude AAAAAA is called...."+UserLatitude.toString());
                Log.e("CURRENTADDRESS","Longitude AAAAAA  is called...."+UserLongitude.toString());
                Log.e("CURRENTADDRESS","address AAAAAA is called...."+address.toString());
                //CompleteAddress.setSelection(CompleteAddress.getText().length());

            }

        } catch (Exception e) {
            Log.e("kjfksfsfsf", "this is error exception " + e.getMessage());
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

    @Override
    public void onCameraIdle() {
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
            }, 100*2);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
}