package com.callpneck.activity.registrationSecond.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.PlaceAutoCompleteSearchActivityAdapter;
import com.callpneck.taxi.Adapter.PlaceAutoCompleteAdapter;
import com.callpneck.taxi.TaxiMainActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SearchLocationActivity extends AppCompatActivity {
    EditText etDestination;
    ImageButton searchBtn;
    RecyclerView locationsRv;

    private SessionManager sessionManager;

    private RectangularBounds bounds;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String destination_latti;
    private String destination_longi;
    private String destination_address;
    AutocompleteSessionToken token;
    Boolean isEditable = true;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private PlaceAutoCompleteSearchActivityAdapter mAutoCompleteAdapter;
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        Places.initialize(getApplicationContext(), "AIzaSyBTbcRRCLbhqeMVVGD5fnUevHfxj2MdgdI");
        placesClient = Places.createClient(this);
        sessionManager = new SessionManager(this);


        token = AutocompleteSessionToken.newInstance();
        searchBtn = findViewById(R.id.search);
        locationsRv=findViewById(R.id.address_recycler);

        bounds = RectangularBounds.newInstance(BOUNDS_INDIA);

        etDestination=findViewById(R.id.destination);

        mAutoCompleteAdapter = new PlaceAutoCompleteSearchActivityAdapter(this);

        locationsRv.setLayoutManager(new LinearLayoutManager(this));
        locationsRv.setAdapter(mAutoCompleteAdapter);

        clickListeners();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }
    String address;

    public void onItemClickAddressRecycler(int position){
        if (mAutoCompleteAdapter.getItemCount() == 0) return;
        String locationName = mAutoCompleteAdapter.getPredictions()
                .get(position).getFullText(null).toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

            Log.d("Tahseenduration",addresses.get(0).getAddressLine(0));
           destination_latti=""+addresses.get(0).getLatitude();
            destination_longi=""+addresses.get(0).getLongitude();
             address = addresses.get(0).getAddressLine(0);
            sessionManager.setUserLocation(destination_latti, destination_longi);
            Log.d("Tahseenduration",destination_latti+"/"+destination_longi);

            String s = destination_latti+ destination_longi;
            destination_address = address;
            showSnackBar(SearchLocationActivity.this,s);
            SaveUserAddress(destination_latti, destination_longi, destination_address);

        } catch (IOException e) {
            // e.printStackTrace();
            Log.println(Log.ERROR, "Tag", e.toString());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void SaveUserAddress(String destination_latti, String destination_longi, String destination_address) {
        Intent intent=new Intent();
        intent.putExtra("user_latitude",destination_latti);
        intent.putExtra("user_longitude",destination_longi);
        intent.putExtra("user_complete_address",destination_address);
        setResult(2,intent);
        finish();
    }

    private void clickListeners() {


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etDestination.getText().toString().trim();
                Log.d("Tahseen",text);
                Geocoder geocoder=new Geocoder(SearchLocationActivity.this, Locale.getDefault());
                try {
                    List<Address> addressList=geocoder.getFromLocationName(text,1);


                    if (addressList.size()>0){
                        Log.d("Tahseen","address fetched");
                        Address address=addressList.get(0);


                    }
                } catch (IOException e) {
                    //   e.printStackTrace();
                    Log.d("Tahseen","error while fetching address");
                }
            }
        });
        etDestination.addTextChangedListener(filterTextWatcher);

        etDestination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {

            }
        });

        etDestination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable && !s.toString().isEmpty() && s.length() > 2 && placesClient != null) {
                getAutocompletePredictions(s.toString());
            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    private void getAutocompletePredictions(String query) {
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((response) -> {
                    mAutoCompleteAdapter.setPredictions(response.getAutocompletePredictions());
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    locationsRv.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Ta", "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(getApplicationContext(), apiException.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101){
            Double longitude=data.getDoubleExtra("longitude",83);
            Double lattitude=data.getDoubleExtra("lattitude",83);
            Log.d("Tahseen",lattitude+""+longitude);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }

    public static void showSnackBar(Activity activity, String snackTitle) {
        View Parentview=activity.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(Parentview, snackTitle, Snackbar.LENGTH_SHORT);
        snackbar.show();
        View view = snackbar.getView();
        TextView txtv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
    }
}