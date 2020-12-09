package com.callpneck.map;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.commonutility.CommonUtility;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LocationUpdaterService extends Service {
    public static final int TWO_MINUTES = 30000; // 120 seconds
    public static Boolean isRunning = false;
    public static String url = "anbcbc";

    public LocationManager mLocationManager;
    public LocationUpdaterListener mLocationListener;
    public Location previousBestLocation = null;
    private String ride_id;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationUpdaterListener();
    }

    Handler mHandler = new Handler();
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) {
                startListening();
            }
            mHandler.postDelayed(mHandlerTask, TWO_MINUTES);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandlerTask.run();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListening();
        mHandler.removeCallbacks(mHandlerTask);
        super.onDestroy();
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (mLocationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        }
        isRunning = true;
    }


    private void stopListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
        isRunning = false;
    }

    public class LocationUpdaterListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            if (location.getAccuracy() < 100.0 && location.getAccuracy() > 0 /*location.getSpeed() < 6.95*/) {
                if (isBetterLocation(location, previousBestLocation)) {
                    previousBestLocation = location;
                    try {
                        // Script to post location data to server..
                        Log.d("dkp dharma lat", ":" + location.getLatitude());
                        Log.d("dkp dharma long", ":" + location.getLongitude());
                        String lati = String.valueOf(location.getLatitude());
                        String longi = String.valueOf(location.getLongitude());
                        Log.d("pps", lati);
                        Log.d("ppsc", longi);
                        if (CommonUtility.isNetworkAvailable(getApplication())) {
                            sendToStartTrip(url, lati, longi);
                        } else {
                            Toast.makeText(getApplication(), "Driver location : " + "lat : " + lati + "long : " + longi, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        stopListening();
                    }
                }
            } else {
                //Continue listening for a more accurate location
                if (!isRunning) {
                    startListening();
                }
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            stopListening();
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public void sendToStartTrip(String url, final String lat, final String longi) {
        Log.d("Start updat lat lat:", " url = " + url + " lat = " + lat + " longi" + longi);

        RequestQueue rq = Volley.newRequestQueue(getApplication());
        StringRequest postReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Start update latlong", "=start trip= " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("error ocurred", "TimeoutError");
                    // Toast.makeText(context, "TimeoutError", Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");
                    //  Toast.makeText(context, "AuthFailureError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");
                    // Toast.makeText(context, "ServerError", Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");
                    // Toast.makeText(context, "NetworkError", Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");
                    //Toast.makeText(context, "ParseError", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String[] tag = {"d_latt", "d_long"};
                String[] value = {lat, longi};

                for (int i = 0; i < tag.length; i++)
                    params.put(tag[i], value[i]);
//                return params;
                return checkParams(params);
            }
        };
        postReq.setRetryPolicy(new DefaultRetryPolicy(
                800000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rq.add(postReq);
    }

    private Map<String, String> checkParams(Map<String, String> map) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }
}
