package com.callpneck.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.fragment.BookingCompleteFrag;
import com.callpneck.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookingCompleted extends AppCompatActivity {

    private String userId,epToken;
    private String p_ses_bk_id;
    private SessionManager sessionManager;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_booking_completed);

        mProgressBar=findViewById(R.id.progress_bar);
        sessionManager = new SessionManager(BookingCompleted.this);
        userId = sessionManager.getUserid();
        epToken = sessionManager.getUserToken();

        p_ses_bk_id=getIntent().getStringExtra("order_ses_id");
        if (p_ses_bk_id==null||p_ses_bk_id.length()<=0){
            p_ses_bk_id = sessionManager.getSesBookingId();
        }

        userBookingPendingStatus(userId, epToken, p_ses_bk_id);

        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    public void userBookingPendingStatus(String userId, String epToken, String p_ses_bk_id) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", p_ses_bk_id);
//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(BookingCompleted.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingPendingStatus, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_pending_status", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_pending_resp", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                        mProgressBar.setVisibility(View.GONE);
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String currentBookingStatus = jsonObject2.getString("your_booking_status");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_id = jsonObject2.getString("your_booking_id");
                        String your_booking_no = jsonObject2.getString("your_booking_number");

                        Log.e("booking_pending_status","this is current status "+currentBookingStatus);


                        // gotoBookingCompleteFragment(jsonObject1);
                        if (currentBookingStatus.equals("accepted")||
                                currentBookingStatus.equals("accepted_otp_confirmed")||
                                currentBookingStatus.equals("order_info_provided")||
                                currentBookingStatus.equals("delivery_otp_confirmed")||
                                currentBookingStatus.equals("order_request_payment")) {
                            Log.e("booking_pending_status","launch real time order tracking "+currentBookingStatus);

                            LaunchActivityClass.LaunchRealTimeOrderTracking(BookingCompleted.this);
                        }else if (currentBookingStatus.equals("order_completed")){
                            LaunchActivityClass.LaunchOrderCompleteHappyScreen(BookingCompleted.this);
                        }
                        else {
                            gotoBookingCompleteFragment(jsonObject1);
                        }

                    } else {
                        Toast.makeText(BookingCompleted.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("booking_pending_status","this is error status "+e.getMessage());

                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("error ocurred", "TimeoutError");

                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");

                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");

                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");

                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void gotoBookingCompleteFragment(JSONObject jsonObject1) {
        Fragment fragment = new BookingCompleteFrag(jsonObject1,p_ses_bk_id);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
