package com.callpneck.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.callpneck.activity.BookingCompleted;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.callpneck.utils.Utils.getApplicationContext;

public class BookingCompleteFrag extends Fragment {


    private TextView bookingId;
    private TextView bookingNum;
    private TextView bookingStatus;
    private RelativeLayout trackOrderBtn;
    private TextView statusMsg;

    private JSONObject dataObj;

    private String userId,epToken;
    private String p_ses_bk_id;
    private SessionManager sessionManager;

    private boolean isBookingAccepted;
    private String myBookingId;
    private boolean isBookingCompleted=false;
    private int orderStatus=-1;
    private TextView cancelOrder;
    private ProgressBar progressBar;
    private TextView EmployeeAddress;

    public BookingCompleteFrag() {
    }

    @SuppressLint("ValidFragment")
    public BookingCompleteFrag(JSONObject jsonObject1,String  bookingId) {
        Log.e("kjdfsfsfd","this is dataobj "+dataObj);
        dataObj=jsonObject1;
        myBookingId=bookingId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private boolean isTimerStarted=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.booking_complete_frag, container, false);

        bookingId = (TextView)view.findViewById( R.id.booking_id );
        bookingNum = (TextView)view.findViewById( R.id.booking_num );
        bookingStatus = (TextView)view.findViewById( R.id.booking_status );
        trackOrderBtn = (RelativeLayout)view.findViewById( R.id.track_order_btn );
        cancelOrder=view.findViewById(R.id.cancel_order);
        statusMsg=view.findViewById(R.id.booking_status_msg);
        progressBar=view.findViewById(R.id.progress_bar);
        EmployeeAddress=view.findViewById(R.id.address);

        sessionManager = new SessionManager(getContext());
        userId = sessionManager.getUserid();
        epToken = sessionManager.getUserToken();
        //p_ses_bk_id = sessionManager.getSesBookingId();
        p_ses_bk_id = myBookingId;

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cancelOrderReasonDialog(getActivity());
                //cancelCurrentOrder();
            }
        });


        try {
            JSONObject jsonObject2 = dataObj.getJSONObject("data");
            String currentBookingStatus = jsonObject2.getString("your_booking_status");
            String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
            String your_booking_id = jsonObject2.getString("your_booking_id");
            String your_booking_no = jsonObject2.getString("your_booking_number");

            if (currentBookingStatus.equals("rejected")){
                orderStatus=0;
                currentBookingStatus="Order Canceled";
            }else if (currentBookingStatus.equals("awaited")){
                currentBookingStatus="Order Not Yet Accepted";
            }else if (currentBookingStatus.equals("accepted")){
                currentBookingStatus="Order Accepted";
            }else if (currentBookingStatus.equals("accepted_otp_confirmed")){
                currentBookingStatus="OTP Confirmed";
            }else if (currentBookingStatus.equals("order_info_provided")){
                currentBookingStatus="Order Information Added";
            }else if (currentBookingStatus.equals("delivery_otp_confirmed")){
                currentBookingStatus="Delivery OTP Confirmed";
            }else if (currentBookingStatus.equals("order_request_payment")){
                currentBookingStatus="Order Payment Request";
            }

            bookingStatus.setText(currentBookingStatus);
            bookingId.setText(your_booking_id);
            bookingNum.setText(your_booking_no);
            statusMsg.setText(booking_status_msg);

        }catch (JSONException e){
            Log.e("sdjflskdfs","this is error "+e.getMessage());
        }

        trackOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBookingCompleted){
                    Toast.makeText(getContext(),"Order is completed",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isBookingAccepted){
                    Toast.makeText(getContext(),"Booking is not accepted yet",Toast.LENGTH_SHORT).show();
                }else {
                    Activity mActivity=getActivity();
                    if (mActivity instanceof  BookingCompleted){
                        LaunchActivityClass.LaunchRealTimeOrderTracking(getActivity());
                        //((BookingCompleted)(mActivity)).gotoTrackFragment();
                    }
                }
                if (!isTimerStarted){
                    checkUpdate();
                }
            }
        });

        checkUpdate();

        return view;
    }



    public  void cancelOrderReasonDialog(final Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.cancel_order_dialog, null);
        //TextInputEditText cancelReason;
        TextView stayOnOrder;
        TextView cancelOrder;

        RadioGroup radioGroup=mView.findViewById(R.id.cancel_reason_radio);
        //cancelReason = (TextInputEditText)mView.findViewById( R.id.cancel_reason );
        stayOnOrder = (TextView)mView.findViewById( R.id.stay_on_order );
        cancelOrder = (TextView)mView.findViewById( R.id.cancel_order );

        builder.setView(mView);
        final AlertDialog NewCategory_dialog = builder.create();



        stayOnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewCategory_dialog.dismiss();
            }
        });
        final String[] selected = {""};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) mView.findViewById(selectedId);
                selected[0] =radioButton.getText().toString();
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selected[0].length()>0){
                    cancelCurrentOrder(selected[0].toString());
                    NewCategory_dialog.dismiss();
                }else {
                    Toast.makeText(getContext(),getString(R.string.PLEASE_ENTER_CANCEL_RESON),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        NewCategory_dialog.show();
    }

    private void cancelCurrentOrder(String cancelReason) {
        progressBar.setVisibility(View.VISIBLE);
        userBookingCancel(userId, epToken, p_ses_bk_id,cancelReason);
    }

    private void userBookingCancel(String userId, String epToken, String ses_booking, String cancelReason) {
        Map<String, String> params = new HashMap<>();
        ses_booking = sessionManager.getSesBookingId();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", ses_booking);
        params.put("cancel_reason",cancelReason);

        //  progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //  Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingCancel, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_cancle", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_cancle", jsonObject1.toString());
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    progressBar.setVisibility(View.GONE);
                    if (jsonObject1.getBoolean("success")) {
                        sessionManager.clearOrderSession();
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_status = jsonObject2.getString("your_booking_status");

                        bookingStatus.setText(your_booking_status);
                        statusMsg.setText(booking_status_msg);
                        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();

                        getActivity().finish();

                    } else {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("booking_cancle","this is error "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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


    private void  checkUpdate(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
                                      @Override
                                      public void run() {
                                          Activity activity=getActivity();
                                          if (activity!=null){
                                              isTimerStarted=true;
                                              userBookingPendingStatus(userId, epToken, p_ses_bk_id);
                                          }
                                      }
                                  },
                0, 8000);

    }
    public void userBookingPendingStatus(String userId, String epToken, String p_ses_bk_id) {
        Log.d("booking_pending_status", "calling inside booking fragment");

        Activity activity=getActivity();
        if (activity==null){
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", p_ses_bk_id);
        Log.e("ksdfhskdf","this is i am sending "+params);
//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingPendingStatus, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_pending_status", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                Log.d("booking_pending_status", "this is response "+jsonObject);
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_pending_status", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String currentBookingStatus = jsonObject2.getString("your_booking_status");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_id = jsonObject2.getString("your_booking_id");
                        String your_booking_no = jsonObject2.getString("your_booking_number");

                        if (currentBookingStatus.equals("order_completed")){
                            isBookingCompleted=true;
                        }


                        bookingId.setText(your_booking_id);
                        bookingNum.setText(your_booking_no);
                        statusMsg.setText(booking_status_msg);


                        Log.e("booking_pending_status","this is current status "+currentBookingStatus);

                        if (currentBookingStatus.equals("accepted")||
                                currentBookingStatus.equals("accepted_otp_confirmed")||
                                currentBookingStatus.equals("order_info_provided")||
                                currentBookingStatus.equals("delivery_otp_confirmed")||
                                currentBookingStatus.equals("order_request_payment")) {
                            isBookingAccepted=true;
                            LaunchActivityClass.LaunchRealTimeOrderTracking(getActivity());

                        }else {
                            isBookingAccepted=false;
                        }

                        if (currentBookingStatus.equals("rejected")){
                            orderStatus=0;
                            currentBookingStatus="Order Canceled";
                        }else if (currentBookingStatus.equals("awaited")){
                            currentBookingStatus="Order Not Yet Accepted";
                        }else if (currentBookingStatus.equals("accepted")){
                            currentBookingStatus="Order Accepted";
                        }else if (currentBookingStatus.equals("accepted_otp_confirmed")){
                            currentBookingStatus="OTP Confirmed";
                        }else if (currentBookingStatus.equals("order_info_provided")){
                            currentBookingStatus="Order Information Added";
                        }else if (currentBookingStatus.equals("delivery_otp_confirmed")){
                            currentBookingStatus="Delivery OTP Confirmed";
                        }else if (currentBookingStatus.equals("order_request_payment")){
                            currentBookingStatus="Order Payment Request";
                        }

                        bookingStatus.setText(currentBookingStatus);

                    } else {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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


}
