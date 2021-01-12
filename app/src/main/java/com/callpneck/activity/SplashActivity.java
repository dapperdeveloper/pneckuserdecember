package com.callpneck.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.utils.Variables;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.activity.Registration.LoginActivity;
import com.callpneck.api.ApiClient;
import com.callpneck.api.AuthApiHelper;
import com.callpneck.api.CallbackManager;
import com.callpneck.api.ModRes;
import com.callpneck.api.ModelRequest;
import com.callpneck.api.RetroError;
import com.callpneck.SessionManager;
import com.callpneck.utils.BaseActivity;

import java.lang.reflect.Method;

public class SplashActivity extends BaseActivity {
    int width;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;
    public static final int PERMISSIONS_REQUEST_TOKEN = 2231;
    String currentVersion, latestVersion,usermobile;
    private static final int READ_CONTACTS_RESULT_CODE = 1000;
    private static final int READ_PHONE_STATE_RESULT_CODE = 1001;
    private static final int STORAGE_RESULT_CODE = 1002;
    private static final int CAMERA_RESULT_CODE = 1004;
    private static final int WIFI_RESULT_CODE = 1005;
    private static final int HOARDWARE_RESULT_CODE = 1006;
    private static final int RECORD_AUDIO_RESULT_CODE = 1003;
    public static final int RequestPermissionCode = 1;
    protected static final int REQUEST_CHECK_SETTINGS = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_FINE_LOCATION = 999;
    private static final String TAG = "PPS";
    private boolean isPermissionGranted = false;
    int timeout = 1000;
    SessionManager sessionManager;
    private GoogleApiClient mGoogleApiLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_splash);
        sessionManager = new SessionManager(this);
        usermobile = sessionManager.getUserMobile();


        DisplayMetrics displayMetrics = new DisplayMetrics();
        (this).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        getCurrentVersion();
      //mk  doAuth();


     //mk   initGoogleAPIClientLocation();

        sessionManager.setDeviceToken(android_id);
        Log.e("DEVICE_TOKEN", android_id);
        Toast.makeText(this, ""+android_id, Toast.LENGTH_SHORT).show();
    }

    public void getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;
        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        assert pInfo != null;
        currentVersion = pInfo.versionName;
    }

    private void doAuth() {
        try {
            AuthApiHelper authApiHelper = ApiClient.getClient(getApplicationContext()).create(AuthApiHelper.class);
            ModelRequest authReq = new ModelRequest();
            authReq.setForce("true");
            authReq.setHsn("17302PP83575926");
            authReq.setMerchantId("800000000166");

            retrofit2.Call<ModRes> call = authApiHelper.getRequest(authReq, "application/json", "ZCb8pPkXcZDVO0CIngLSFrBJgA\\/BYyUZIHT8zaj3MPg=");
            call.enqueue(new CallbackManager<ModRes>() {
                @Override
                public void onSuccess(Object o) {
                    Log.e("Success", "");
                }

                @Override
                public void onError(RetroError retroError) {
                    Log.e("Success", "");

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void checkPermissions() {
        isCheckCalled=true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_RESULT_CODE);
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_RESULT_CODE);
            } else*/ if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_LOCATION);
            } else {
                goToNextScreen();
            }
        } else {
            goToNextScreen();
        }


    }

    public void goToNextScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    if (checkPermission()) {
                        getDeviceLocation();
                    } else {
                        askForPermission();
                    }

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, timeout); // wait for 3 seconds
    }

    public void askForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat
                    .checkSelfPermission(SplashActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    Snackbar.make(SplashActivity.this.findViewById(android.R.id.content),
                            R.string.PERMISSION_SNACK_BAR_MESSAGE,
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void onClick(View v) {
                                    requestPermissions(
                                            new String[]{
                                                    Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_REQUEST_TOKEN);
                                }
                            }).show();

                } else {
                    requestPermissions(
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_TOKEN);
                }

            } else {
                getDeviceLocation();
            }
        } else {
            getDeviceLocation();
        }


    }


    private boolean checkPermission() {
        // Permission is not granted
        return ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        isCheckCalled=false;
        Log.e("dksjfskfdsf", "onRequestPermissionsResult is called request code is " + requestCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TOKEN: {
                Log.e("dksjfskfdsf", "location access switch");
                // If request is cancelled, the result arrays are empty.
                Log.e("dksjfskfdsf", "location access true grantResults " + grantResults);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.e("dksjfskfdsf", "location access true");
                    getDeviceLocation();
                }
            }
            break;
        }
        if (!isCheckCalled){
            checkPermissions();
        }

    }

    boolean isCheckCalled=false;
    @Override
    protected void onResume() {
        if (!isCheckCalled){
            checkPermissions();
        }
        super.onResume();
    }

    private void getDeviceLocation() {
        if (GPSstatusCheck()) {
            setLocation();
        } else {
            settingsrequest();
        }
    }

    private void setLocation() {
        Log.e("ksdfksdfsdfdsf","this is booking id "+sessionManager.getSesBookingId());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LaunchActivityClass.LaunchMainActivity(SplashActivity.this);
                /*Intent intent = new Intent(SplashActivity.this, PneckMapLocation.class);
                startActivity(intent);*/
            }
        },10);

    }

    private void initGoogleAPIClientLocation() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiLocation = new GoogleApiClient.Builder(SplashActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiLocation.connect();
    }

    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiLocation, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {


                    case LocationSettingsStatusCodes.SUCCESS:
                        setLocation();
                        Log.e("fslkdkfsf", "success state ");
                        // All location settings are satisfied. The client can initializeFFMPEGLib location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e("fslkdkfsf", "RESOLUTION_REQUIRED state ");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("fslkdkfsf", "SETTINGS_CHANGE_UNAVAILABLE state ");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public boolean GPSstatusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("result_recieved", Integer.toString(resultCode));
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        setLocation();
                        //goToNextScreen();
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
                        Toast.makeText(SplashActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        buildAlertMessageNoGps();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setMessage("Your GPS seems to be disabled, please enable it to access services?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        isCheckCalled=false;
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}


