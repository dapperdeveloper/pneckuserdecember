package com.callpneck.Services;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.app.AlarmManager.ELAPSED_REALTIME;
import static android.os.SystemClock.elapsedRealtime;


public class LocationService extends Service {

    private static final String TAG = "emplocationservice";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "employee_location_023";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Employee Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("ONLINE")
                    .setContentText("You are online on Pneck").build();

            startForeground(1, notification);
        }
    }
    private boolean IsStopped=false;
    private boolean IsArreadyRunnig=false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("jsklsdsdf", "onStartCommand: called.");
        if (!IsArreadyRunnig){
            if (intent.getBooleanExtra("is_stop",true)){
                this.stopSelf();
                IsStopped=true;
                IsArreadyRunnig=false;
            }else {
                IsArreadyRunnig=true;
                IsStopped=false;
                startLookingForJobs();
            }
        }
        Log.d("jsklsdsdf", "onStartCommand: called. "+IsStopped);
        return START_NOT_STICKY;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    @Override
    public void onTaskRemoved(Intent rootIntent){
        Log.e("data_response","onTaskRemoved is called ");

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmService.set(ELAPSED_REALTIME, elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);

    }



    private void startLookingForJobs() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          if (!IsStopped&&!isOtpcalled){
                                              checkForUserLocation();
                                          }
                                      }
                                  },
                0, 30000);
    }


    private void checkForUserLocation(){


        SessionManager sessionManager=new SessionManager(LocationService.this);
        if (!sessionManager.isLoggedIn()||sessionManager.getUserid()==null||sessionManager.getSesBookingId()==null||
        sessionManager.getSesBookingId().length()<=1){
            stopSelf();
            return;
        }

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userCurrBookingTracking";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());
        dataParams.put("ses_booking_id",sessionManager.getSesBookingId());
        dataParams.put("curr_lat",sessionManager.getUserLatitude());
        dataParams.put("curr_long",sessionManager.getUserLongitude());
        dataParams.put("curr_address","");


        Log.e("employee_current_job", "this is url " +ServerURL);

        Log.e("employee_current_job", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                SuccessListener(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(LocationService.this).add(dataParamsJsonReq);
    }

    public static boolean isOtpcalled=false;

    private Response.Listener<JSONObject> SuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    SessionManager sessionManager=new SessionManager(LocationService.this);
                    Log.v("employee_current_job", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        sessionManager.setCurrentOrderResponse(innerResponse.toString());

                        JSONObject object=innerResponse.getJSONObject("data");

                        if (object.getString("curr_booking_status").
                                equalsIgnoreCase("order_request_payment")){
                            
                        }


                        /*
                        JSONObject object=innerResponse.getJSONObject("data");
                        if (object.getString("is_job_found").equalsIgnoreCase("yes")){
                            Bundle bundle =new Bundle();
                            bundle.putString("booking_order_number",object.getString("booking_order_number"));
                            bundle.putString("booking_order_id",object.getString("booking_order_id"));
                            bundle.putString("distance_km",object.getString("distance_km"));

                            if (!isOtpcalled){
                                isOtpcalled=true;
                                LaunchActivityClass.LaunchJOB_AcceptScreen(LocationService.this,bundle);
                            }
                        }*/
                    }

                } catch (Exception e) {
                    Log.v("employee_current_job", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener ErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("employee_current_job", "inside error block  " + error.getMessage());
            }
        };
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        IsStopped=true;

        Log.e("jfgdlkfgsdfg","service stopped");
    }


}