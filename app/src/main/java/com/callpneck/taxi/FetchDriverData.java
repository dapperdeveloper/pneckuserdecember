package com.callpneck.taxi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.taxi.activity.RealTimeActivity;

import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FetchDriverData {
    Context context;
    SessionManager sessionManager;
    boolean isContinue;
    boolean againDdata=true;
    boolean loaded=true;
    private boolean RequestComplete=true;

    public FetchDriverData(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
        this.isContinue=true;
        getDriverData();
    }

    public void start(){

        Log.d("serajfetchDriver","running inside cont");
        if (againDdata){
            againDdata=false;
            Log.d("serajfetchDriver","running inside called");
            getDriverData();
        }

    }

    public void stop(){

    }

    private void getDriverData() {
        Log.d("Serajcpa","api called "+sessionManager.getSesBookingId());
        String ServerURL = context.getResources().getString(R.string.pneck_app_url) + "/getDriverData";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",sessionManager.getSesBookingId());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                DriverFetchSuccess(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> DriverFetchSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                againDdata=true;
                Log.d("serajfetchDriver","recieved res");
                try {
                    Log.d("Serajdriverdata","accept responce recieved");
                    Log.d("Serajdriverdata", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        Log.d("Serajdriverdata","responce success");
                        JSONObject object=innerResponse.getJSONObject("data");


                        if(loaded){
                            Log.d("serajfetchDriver","running loaded");
                            String otp=object.getString("otp");
                            String name=object.getString("first_name") +" "+object.getString("last_name");
                            String mobile=object.getString("mobile");
                            String vehicleNumber=object.getString("vehicle_number");
                            String vehicleImage=object.getString("vehicle_image");

                            sessionManager.setUserValues(name,mobile,vehicleNumber,vehicleImage,otp);

                            loaded=false;
                        }

                        String lat=object.getString("curr_latitude");
                        String longi=object.getString("curr_longitude");
                        Double empLattitude=Double.valueOf(lat);
                        Double empLongitude=Double.valueOf(longi);
                        Log.d("serajempc","running"+empLattitude+" "+empLongitude);

                        sessionManager.setEmpLattitudeLongitude(String.valueOf(empLattitude),String.valueOf(empLongitude));

                    }else {
                        Log.d("Serajad","accept responce failed");
                    }

                } catch (Exception e) {
                    Log.d("Serajad", "error ad inside catch block  " + e.getMessage());
                    //e.printStackTrace();
                    againDdata=true;

                }
            }
        };
    }

    private Response.ErrorListener ErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(context, "Please check your network connectivity either slow or not connected... ", Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                    Log.d("seravvolley","auth");
                } else if (error instanceof ServerError) {
                    //TODO
                    Log.d("seravvolley","server error");
                } else if (error instanceof NetworkError) {
                    //TODO
                    Log.d("seravvolley","network error");
                } else if (error instanceof ParseError) {
                    //TODO
                    Log.d("seravvolley","parse error");
                }
                againDdata=true;
            }
        };
    }
}
