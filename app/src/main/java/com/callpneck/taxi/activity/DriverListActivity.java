package com.callpneck.taxi.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.taxi.Adapter.DriverAdapter;
import com.callpneck.taxi.map.WebSocketListener;
import com.callpneck.taxi.model.AgreeDriverData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DriverListActivity extends AppCompatActivity implements WebSocketListener {
    String bookingId;
    List<AgreeDriverData> agreeDriverDataList=new ArrayList<>();
    List<AgreeDriverData> tempagreeDriverDataList=new ArrayList<>();
    ScheduledExecutorService executor;
    Runnable periodicTask;
    DriverAdapter driverAdapter;
    RecyclerView driverRecycler;
    TextView cashTv;
    private String cash_offered,cash;
    private ScheduledExecutorService eFuture;
    TextView cancelBtn;
    int c;
    SessionManager sessionManager;
    ProgressBar progressBar;
    Handler mHandler;
    private boolean isAccepted=false;
    ImageView loadingIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_driver_list);
        sessionManager = new SessionManager(DriverListActivity.this);
        progressBar=findViewById(R.id.pb);

        Intent intent=getIntent();
        bookingId=intent.getStringExtra("bookin_id");
        cash=intent.getStringExtra("cash");



        cashTv=findViewById(R.id.tv_cash_offred);
        Button cashUpBtn=findViewById(R.id.cash_up_btn);
        cancelBtn=findViewById(R.id.cancel_btn);
        loadingIV = findViewById(R.id.loadingIV);

        loadingIV.setVisibility(View.VISIBLE);

        cashTv.setText(cash);

        driverRecycler=findViewById(R.id.driver_rv);
        driverRecycler.setLayoutManager(new LinearLayoutManager(this));
        driverRecycler.setHasFixedSize(true);

        //single thread executer


        eFuture=executor = Executors.newSingleThreadScheduledExecutor();

        periodicTask = new Runnable() {
            public void run() {
                // Invoke method(s) to do the work
               // doPeriodicWork();

            }
        };



        mHandler = new Handler();
        c=0;
        startRepeatingTask();




        driverAdapter=new DriverAdapter(DriverListActivity.this,agreeDriverDataList);
        driverAdapter.notifyDataSetChanged();
        driverRecycler.setAdapter(driverAdapter);
        executor.scheduleAtFixedRate(periodicTask, 0, 3, TimeUnit.SECONDS);


        Glide.with(this).load(R.drawable.loading_gif).into(loadingIV);

        cashUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Seraj","cash up button clicked");
                cashUp(driverRecycler,Integer.valueOf(bookingId));
            }
        });


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelBtn.setEnabled(false);
                userCancelBooking(sessionManager.getUserid(),sessionManager.getUserToken(),bookingId,"Cancel by user");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callFetchApi(String.valueOf(bookingId));
            }
        }, 20000);
    }

    private void userCancelBooking(String userid, String userToken, String sesBookingId, String cancel_by_user) {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userBookingCancel";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("user_id",userid);
        dataParams.put("ep_token",userToken);
        dataParams.put("ses_booking_id",sesBookingId);
        dataParams.put("cancel_reason",cancel_by_user);

        Log.d("Serajcandata","booking id  "+dataParams);

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                BookingCancekResponse(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DriverListActivity.this).add(dataParamsJsonReq);

    }

    private Response.Listener<JSONObject> BookingCancekResponse() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Serajcpa","accept responce recieved");
                    Log.d("Serajcpa", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        Toast.makeText(DriverListActivity.this, "Booking Cancelled", Toast.LENGTH_SHORT).show();
                        sessionManager.setSesBookingId(null);
                        sessionManager.clearOrderSession();
                        executor.shutdownNow();

                        finish();
                    }else {
                        Log.d("Serajad","accept responce failed");
                    }

                } catch (Exception e) {
                    Log.d("Serajad", "error ad inside catch block  " + e.getMessage());
                    e.printStackTrace();

                }
            }
        };
    }


    private void doPeriodicWork() {
        callFetchApi(String.valueOf(bookingId));
    }
    private void cashUp(RecyclerView driverRecycler, int bookind_id) {
        String new_cash=String.valueOf(Integer.valueOf(cash)+10);
        Log.d("Seraj","cash up value "+new_cash);
        cashTv.setText(new_cash);
        cash=new_cash;
        Log.d("Seraj","booking id  "+bookind_id);

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/updateOfferedCash";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",""+bookind_id);
        dataParams.put("cash_offer",new_cash);

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                CashFetchSuccess(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DriverListActivity.this).add(dataParamsJsonReq);



    }
    private Response.ErrorListener ErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("Seraj", "error occured" + error.getMessage());
            }
        };
    }
    private Response.Listener<JSONObject> CashFetchSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Serajc","accept responce recieved");
                    Log.d("Serajc", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        Log.d("Serajcr","cash responce success");
                    }else {
                        Log.d("Seraj","accept responce failed");
                    }

                } catch (Exception e) {
                    Log.d("Seraj", "error cash inside catch block  " + e.getMessage());
                    e.printStackTrace();

                }
            }
        };
    }
    private void callFetchApi(String b_id) {
        Log.d("Serajcpa","api called "+bookingId);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/fetchAvailableEmployees";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",bookingId);
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                DriverFetchSuccess(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DriverListActivity.this).add(dataParamsJsonReq);
    }
    private Response.Listener<JSONObject> DriverFetchSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Serajcpa","accept responce recieved");
                    Log.d("Serajcpa", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        Log.d("Serajad","responce success");
                        JSONArray jsonArray=innerResponse.getJSONArray("data");

//                        int size=agreeDriverDataList.size();
//                        agreeDriverDataList.clear();
                        loadingIV.setVisibility(View.GONE);

                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            Log.d("Serajemp",object.getString("employee_name"));
                            //agreeDriverDataList.add(new AgreeDriverData(""+object.getInt("id"),""+object.getInt("booking_id"),""+object.getInt("employee_id"),""+object.getString("ep_token"),""+object.getString("employee_lat"),""+object.getString("employee_lng"),""+object.getString("employee_name"),""+object.getString("employee_phone"),""+object.getString("employee_time_to_reach"),""+object.getString("description"),""+object.getString("status"),""+object.getString("employee_cash_offer"),""));
                            tempagreeDriverDataList.add(new AgreeDriverData(""+object.getInt("id"),""+object.getInt("booking_id"),""+object.getInt("employee_id"),""+object.getString("ep_token"),""+object.getString("employee_lat"),""+object.getString("employee_lng"),""+object.getString("employee_name"),""+object.getString("employee_phone"),""+object.getString("employee_time_to_reach"),""+object.getString("description"),""+object.getString("status"),""+object.getString("employee_cash_offer"),""));
                            updatelocation(i);
                        }
//                        if (agreeDriverDataList.size()!=size){
//                            Log.d("Serajsize","changed");
//                            driverAdapter.notifyDataSetChanged();
//                            loadingIV.setVisibility(View.GONE);
//                        }else{
//                            Log.d("Serajsize","same data");
//                        }
                    }else {
                        Log.d("Serajad","accept responce failed");
                    }

                } catch (Exception e) {
                    loadingIV.setVisibility(View.GONE);
                    Log.d("Serajad", "error ad inside catch block  " + e.getMessage());
                    e.printStackTrace();

                }
            }
        };
    }
    public void finishActivity(){
        isAccepted=true;
        eFuture.shutdown();
        finish();
    }





    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                progressBar.setProgress(c);
                c=c+1;//this function can change value of mInterval.
                if (c==100){
                    if (!isAccepted){
                        userCancelBooking(sessionManager.getUserid(),sessionManager.getUserToken(),bookingId,"Cancel by system");
                    }
                    stopRepeatingTask();
                }
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 1000);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }



    public void updatelocation(int value)
    {
        com.callpneck.taxi.map.GoogleMap.requesttimedistance(
                DriverListActivity.this,Double.parseDouble(tempagreeDriverDataList.get(value).getEmployeeLat()),Double.parseDouble(tempagreeDriverDataList.get(value).getEmployeeLong()),30.7411,76.7790,value
        );
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onMessage(@NotNull String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            jsonObject.getString("time");
            jsonObject.getInt("value");
            agreeDriverDataList.add(new AgreeDriverData(tempagreeDriverDataList.get(jsonObject.getInt("value")).getId(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getBookibgId(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmployeeId(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEpToken(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmployeeLat(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmployeeLong(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmployeeName(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmployeePhone(),jsonObject.getString("time"),tempagreeDriverDataList.get(jsonObject.getInt("value")).getDescription(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getStatus(),tempagreeDriverDataList.get(jsonObject.getInt("value")).getEmpCashOffered(),jsonObject.getString("distance")));
            driverAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onError(@NotNull String error) {

    }
}