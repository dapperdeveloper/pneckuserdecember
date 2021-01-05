package com.callpneck.taxi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.taxi.activity.RealTimeActivity;
import com.google.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class GetBookingData{
    private Handler taskHandler = new android.os.Handler();
    Context context;
    SessionManager sessionManager;

    public GetBookingData(Context context, SessionManager sessionManager) {
        this.context = context;
        this.sessionManager = sessionManager;
        fetchBooking();
    }


    public boolean fetchBooking() {
        Log.d("Serajrt","api called "+sessionManager.getSesBookingId());
        String ServerURL = context.getResources().getString(R.string.pneck_app_url) + "/getUserdata";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",sessionManager.getSesBookingId());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                UserDataFetchSuccess(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(dataParamsJsonReq);

        return true;
    }
    private Response.Listener<JSONObject> UserDataFetchSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("serajrt","accept responce recieved");
                    Log.d("serajrt", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        Log.d("Serajdriverdata","responce success");
                        JSONObject object=innerResponse.getJSONObject("data");

                        String orderStatus=object.getString("order_status");
                        String desLatti=object.getString("destination_latti");
                        String desLongi=object.getString("destination_longi");
                        sessionManager.setDestination(desLatti,desLongi);


                        if (orderStatus.equalsIgnoreCase("accepted_otp_confirmed")){
                            sessionManager.setOtpVerified(true);
                            sessionManager.setOrderStatus("accepted");
                            Log.d("serajsessiondatasetted","otp");
                        }

                        String delivery=object.getString("delivery_otp");
                        if (!delivery.equalsIgnoreCase("null")){
                            {
                                sessionManager.setDeliveryOtp(delivery);
                                Log.d("serajsessiondatasetted","delivery");
                            }
                        }

                        int deliveryConfirmed=object.getInt("pay_amount");
                        if (!(deliveryConfirmed == 0)){
                            {
                                sessionManager.setOtpVerified(true);
                                Log.d("Serajdel",deliveryConfirmed+"s");
                                sessionManager.setPayAmount(object.getString("pay_amount"));
                                sessionManager.setOrderStatus("delivered");
                                Log.d("serajsessiondatasetted","pay");

                                //  Bundle bundle =new Bundle();
                                // bundle.putString("billing_amount",object.getString("pay_amount"));
                                // LaunchActivityClass.LaunchPaymentScreen(RealTimeActivity.this,bundle);
                            }
                        }




                    }else {
                        Log.d("Serajad","accept responce failed");

                    }

                } catch (Exception e) {
                    Log.d("Serajad", "error ad inside catch block  " + e.getMessage());
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
                Log.v("Seraj", "error occured" + error.getMessage());
                //  bookingDataLoaded=true;
            }
        };
    }
}
