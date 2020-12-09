package com.callpneck.activity.registrationSecond.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Adapter.MyProviderAdapter;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.ModelProvider;
import com.callpneck.activity.registrationSecond.Model.response.responseCategoryList.Vendor;
import com.callpneck.activity.registrationSecond.api.ApiClient;
import com.callpneck.activity.registrationSecond.api.ApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderActivity extends AppCompatActivity {

    private ImageView providerIV,mapIV, filterIV;
    private RelativeLayout providrListRl, mapRl;
    private RecyclerView providerListRv;
    List<Vendor> providerList;
    MyProviderAdapter adapter;
    TextView featuredTv;

    String user_id, ep_token, curr_lat, curr_long, category;
    SessionManager sessionManager;


    private double latitude, longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_provider);

        providerIV = findViewById(R.id.providerIV);
        mapIV = findViewById(R.id.mapIV);
        filterIV = findViewById(R.id.filterIV);
        providrListRl = findViewById(R.id.providrListRl);
        mapRl = findViewById(R.id.mapRl);
        providerListRv = findViewById(R.id.providerListRv);
        featuredTv = findViewById(R.id.featuredTv);

        if (getIntent() != null){
            category = getIntent().getStringExtra("categoryName");
        }

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(this);
        //init permission
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        if(checkLocationPermission()){
            detectLocation();
        }else {
            requestLocationPermission();
        }

        providerList = new ArrayList<>();

        showProviderUI();
        providerIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProviderUI();
            }
        });
        mapIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMapUI();
            }
        });
        filterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermission,LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){

                        detectLocation();
                    }else {
                        Toast.makeText(this, "Location permission is necessary.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if(location != null){

                    Geocoder geocoder = new Geocoder(ProviderActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String address = addresses.get(0).getAddressLine(0);


                        user_id = sessionManager.getUserid();
                        ep_token = sessionManager.getUserToken();
                        curr_lat = String.valueOf(latitude);
                        curr_long =  String.valueOf(longitude);

                        getProviderDetail(user_id, ep_token, curr_lat,curr_long);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }


    private void getProviderDetail(String user_id, String ep_token, String curr_lat, String curr_long) {
        ApiInterface apiInterface = ApiClient.getInstance(this).getApi();
        RequestBody requestUserId  = RequestBody.create(MediaType.parse("multipart/form-data"), user_id);
        RequestBody requestEp_tokenId  = RequestBody.create(MediaType.parse("multipart/form-data"), ep_token);
        RequestBody requestCurr_lat  = RequestBody.create(MediaType.parse("multipart/form-data"), curr_lat);
        RequestBody request_curr_long  = RequestBody.create(MediaType.parse("multipart/form-data"), curr_long);
        RequestBody requestCategory  = RequestBody.create(MediaType.parse("multipart/form-data"), category);

        Log.d("TahseenVendorList","user_id-"+ user_id +"ep_token-"+ ep_token +"longitude-"+ curr_long +"latitute-"+ curr_lat +"category-"+category);
        Call<ModelProvider> call = apiInterface.vendorList(requestUserId,requestEp_tokenId, requestCurr_lat, request_curr_long, requestCategory);
        call.enqueue(new Callback<ModelProvider>() {
            @Override
            public void onResponse(Call<ModelProvider> call, Response<ModelProvider> response) {
                ModelProvider modelProvider = response.body();
                if (modelProvider.getResponse() != null){
                    providerList.clear();
                    providerList = modelProvider.getResponse().getData().getVendors();
                    if ( modelProvider.getResponse().getData().getVendors().size()>0)
                    adapter = new MyProviderAdapter(ProviderActivity.this,providerList);
                    providerListRv.setAdapter(adapter);
                    Toast.makeText(ProviderActivity.this, ""+modelProvider.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else{
                    if (modelProvider == null && !modelProvider.getResponse().getSuccess()){
                        Toast.makeText(ProviderActivity.this, ""+modelProvider.getResponse().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ProviderActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ModelProvider> call, Throwable t) {

                Toast.makeText(ProviderActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void showMapUI() {

        providrListRl.setVisibility(View.GONE);
        mapRl.setVisibility(View.VISIBLE);
        mapIV.setImageResource(R.drawable.ic_map_white);
        mapIV.setBackgroundResource(R.drawable.shape_rect04);
        providerIV.setImageResource(R.drawable.ic_provider_list);
        providerIV.setBackgroundColor(getResources().getColor(android.R.color.transparent));


    }

    private void showProviderUI() {

        providrListRl.setVisibility(View.VISIBLE);
        mapRl.setVisibility(View.GONE);

        providerIV.setImageResource(R.drawable.ic_list_white);
        providerIV.setBackgroundResource(R.drawable.shape_rect04);

        mapIV.setImageResource(R.drawable.ic_map_black);
        mapIV.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}