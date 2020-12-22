package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Model.addressResponse.AddAddressResponse;
import com.callpneck.activity.registrationSecond.Model.getAddress.ResponseAddress;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.utils.InternetConnection;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.callpneck.activity.registrationSecond.helper.Constant.LAUNCH_ADDRESS_SET_SCREEN;
import static com.callpneck.activity.registrationSecond.helper.Constant.REQUEST_CHECK_SETTINGS;

public class HomeMapActivity extends AppCompatActivity {

    private TextView completeAddress;
    public static final int PERMISSIONS_REQUEST_TOKEN = 2001;
    public static final String CustomGalleryIntentKey = "ImageArray";//Set Intent Key Value
    private static final int CustomGallerySelectId = 1;//Set Intent Id

    private String userLatitude;
    private String userLongitude;

    private Button update_btn;
    String user_id;
    private SessionManager sessionManager;

    String homeAddress, address;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_home_map);

        completeAddress = findViewById(R.id.complete_address);
        update_btn = findViewById(R.id.update_btn);

        if (getIntent() !=null){
            homeAddress = getIntent().getStringExtra("home");
        }
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setCanceledOnTouchOutside(false);

        user_id = sessionManager.getUserid();

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        completeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    settingsrequest();
                }else {
                    askForPermission();
                }
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation())
                {
                    if (InternetConnection.checkConnection(HomeMapActivity.this))
                    updateData();
                }

            }
        });

        getHomeAddress();


    }

    private void getHomeAddress() {
        progressDialog.show();
        Call<ResponseAddress> call = APIClient.getInstance().getHomeAddress(user_id);
        call.enqueue(new Callback<ResponseAddress>() {
            @Override
            public void onResponse(Call<ResponseAddress> call, Response<ResponseAddress> response) {
                ResponseAddress model = response.body();
                if (model != null && model.getStatus()){
                    userLatitude = model.getData().getLati();
                    userLongitude = model.getData().getLongi();
                    completeAddress.setText(model.getData().getAddress());
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(HomeMapActivity.this, "No Address added yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(HomeMapActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData() {
        progressDialog.show();
        Call<AddAddressResponse> call = APIClient.getInstance().addUserHomeAddress(address, userLatitude, userLongitude,homeAddress, user_id);
        call.enqueue(new Callback<AddAddressResponse>() {
            @Override
            public void onResponse(Call<AddAddressResponse> call, Response<AddAddressResponse> response) {
                AddAddressResponse model = response.body();
                if (model!= null && model.getStatus()){
                    Toast.makeText(HomeMapActivity.this, ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(HomeMapActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<AddAddressResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }


    private boolean validation(){
        boolean valid = true;
        address = completeAddress.getText().toString();

        if(TextUtils.isEmpty(user_id)){
            Toast.makeText(this, "user_id is required", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(userLatitude)){
            Toast.makeText(this, "Latitude is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(userLongitude)){
            Toast.makeText(this, "Longitute is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(address)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(homeAddress)){
            Toast.makeText(this, "Something is missing", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat
                    .checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                    .checkSelfPermission(HomeMapActivity.this,
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
                    .checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.FOREGROUND_SERVICE)+
                    ContextCompat.checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                    .checkSelfPermission(HomeMapActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (HomeMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (HomeMapActivity.this, Manifest.permission.FOREGROUND_SERVICE)||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (HomeMapActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (HomeMapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.main_layout), getString(R.string.PERMISSION_SNACK_BAR_MESSAGE), Snackbar.LENGTH_LONG);
                    TextView snack_tv = (TextView)snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(HomeMapActivity.this, R.color.primary_800));
                    snack_tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    snack_tv.setTextColor(ContextCompat.getColor(HomeMapActivity.this, R.color.white));
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
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TOKEN:
                if (grantResults.length> 0) {


                    boolean locationPemission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationPemission){
                        if (InternetConnection.checkConnection(HomeMapActivity.this)){

                            settingsrequest();

                        }else {
                            Toast.makeText(HomeMapActivity.this,getResources().getString(R.string.CHECK_YOUR_INTERNET_CONNECTION),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(HomeMapActivity.this,"Permission Denied can't post Global post.",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }



    private String TAG="deliverymainfksfdsf";

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
                        Log.i(TAG, "All location settings are satisfied.");


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
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(HomeMapActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(HomeMapActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHECK_SETTINGS){
            if (resultCode==RESULT_OK) {
                launchLocation();
            }else if (resultCode==RESULT_CANCELED){
                Log.e(TAG,"location enabl cancled"+data.getExtras());
            }
        }else if (requestCode==LAUNCH_ADDRESS_SET_SCREEN){
            if (data!=null&&data.hasExtra("user_complete_address")){
                completeAddress.setText(data.getStringExtra("user_complete_address"));
                userLatitude=data.getStringExtra("user_latitude");
                userLongitude=data.getStringExtra("user_longitude");
            }
        }
    }

    private void launchLocation(){

        LaunchActivityClass.LaunchAddressSetActivity(HomeMapActivity.this);

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}