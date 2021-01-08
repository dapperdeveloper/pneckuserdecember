package com.callpneck.taxi.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.model.MapPointerModel;
import com.callpneck.taxi.Adapter.AddressAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class DestinationPickerActivity extends AppCompatActivity implements OnMapReadyCallback ,
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener{

    RecyclerView recyclerView;
    EditText etDestination;
    AddressAdapter addressAdapter;
    List<Address> addressList;
    private LatLng mCenterLatLong;
    TextView source;
    ImageButton searchBtn;

    private static final String TAG ="Seraj" ;
    LinearLayout mDestinationLinearLayout;
    TextView whereto;
    ImageButton imageButton;
    GoogleMap mMap;
    Map<String, MapPointerModel> allMarkersMap = new HashMap<>();
    private ArrayList<MapPointerModel> mapsList = new ArrayList<>();
    private SessionManager sessionManager;
    Button demoBtn;

    private String city = "";
    private String state = "";

    private EditText CompleteAddress;
    private ImageButton mCurrentLocationBtn;
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private String locality = "";
    private String currentFullAddress="";
    private String UserLatitude = "";
    private String UserLongitude = "";
    TextView currentAddress;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    //not initilized till now
    private LatLng mDefaultLocation;
    private float DEFAULT_ZOOM = 15.0f;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

    Double lat;
    Double longi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_destination_picker);
        searchBtn=findViewById(R.id.search);
        recyclerView = findViewById(R.id.address_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        etDestination=findViewById(R.id.destination);
        currentAddress=findViewById(R.id.source);


        //get intent data
        Intent intent=getIntent();
        lat=intent.getDoubleExtra("lat",81);
        longi=intent.getDoubleExtra("long",23);
        String currentAddr=intent.getStringExtra("current");
        currentAddress.setText(currentAddr);

        addressList=new ArrayList<>();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        sessionManager = new SessionManager(DestinationPickerActivity.this);

        //map initialized
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etDestination.getText().toString().trim();
                Geocoder geocoder=new Geocoder(DestinationPickerActivity.this,Locale.getDefault());
                try {
                    List<Address>addressList=geocoder.getFromLocationName(text,1);

                    if (addressList.size()>0){
                        Address address=addressList.get(0);
                        Log.d("Tahseen_Khan",addressList.get(0).getAddressLine(0));
                        gotoLocation(address.getLatitude(),address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));

                        Intent intent1=new Intent();
                        intent.putExtra("longitude",address.getLongitude());
                        intent.putExtra("lattitude",address.getLatitude());
                        setResult(RESULT_OK,intent);
                        finish();


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void gotoLocation(double latitude, double longitude) {

    }

    private void geoLocate(View view){
        hideSoftKeyboard(view);
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    @Override
    public void onBackPressed() {
        finish();
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        getLocationPermission();
        getDeviceLocation();
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        getLocationPermission();
        getDeviceLocation();

        //mMap.addMarker(new MarkerOptions().position(new LatLng(lat,longi)));
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
                CompleteAddress.setText(address);

                //etDestination.setText(address);

                Log.d("Seraj","Latitude AAAAAA is called...."+UserLatitude.toString());
                Log.d("Seraj","Longitude AAAAAA  is called...."+UserLongitude.toString());
                Log.d("Seraj","address AAAAAA is called...."+address.toString());
                //CompleteAddress.setSelection(CompleteAddress.getText().length());

                source.setText(address);

                //getEmployeeList();
            }

        } catch (Exception e) {
            Log.d("Seraj", "this is error exception " + e.getMessage());
        }

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
                            Toast.makeText(DestinationPickerActivity.this, "Getting Current Location...", Toast.LENGTH_SHORT).show();
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

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    @Override
    public void onCameraIdle() {
        Log.d("Seraj", "camera is in ideal state");
        if (mLastKnownLocation == null) {
            getDeviceLocation();
        } else {

            CameraPosition cameraPosition = mMap.getCameraPosition();
            mCenterLatLong = cameraPosition.target;
            getCompleteAddressString(mCenterLatLong.latitude, mCenterLatLong.longitude);
            Log.d("Seraj", "location req send..");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                }
            }, 100*2);
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
        Log.d("Seraj", "camera is started move");
    }
}