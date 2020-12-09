package com.callpneck.activity.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.callpneck.R;
import com.callpneck.activity.SplashActivity;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.commonutility.CommonUtility;
import com.callpneck.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.widget.Toast.LENGTH_LONG;

public class VerifyOtpActivity extends AppCompatActivity {
    private AlertDialog progressDialog;
    Button btn_veryfy;
    EditText et_otp;
    String otp, mobileno;
    SessionManager sessionManager;
    TextView text_resendotp;
    int resendInt = 0;
    boolean resp_status = true;
    //Declare timer
    CountDownTimer cTimer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        sessionManager = new SessionManager(this);
        Intent intent = getIntent();
        mobileno = intent.getStringExtra("mobile");
        btn_veryfy = (Button) findViewById(R.id.btn_veryfy);
        et_otp = (EditText) findViewById(R.id.et_otp);
        text_resendotp = findViewById(R.id.text_resendotp);

        btn_veryfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = et_otp.getText().toString();
                if (checkOtp()) {
                    if (CommonUtility.isNetworkAvailable(VerifyOtpActivity.this)) {
                        verifyOtp(mobileno, otp);
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, "please check your internet", LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "Please Enter OTP", LENGTH_LONG).show();
                }
            }
        });
        text_resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtility.isNetworkAvailable(VerifyOtpActivity.this)) {
                    resendOtp(mobileno);

                    // ************* Hold for 20 seconds.*******************
                    new CountDownTimer(20000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            text_resendotp.setClickable(false);
                           // Toast.makeText(VerifyOtpActivity.this, "Please wait for "+ millisUntilFinished / 1000 +" seconds", LENGTH_LONG).show();
                            //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        public void onFinish() {
                            text_resendotp.setClickable(true);

                        }

                    }.start();
                    //********************************end ******************
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "please check your internet", LENGTH_LONG).show();
                }

            }
        });


    }


    public void verifyOtp(String mob, String otp) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_number", mob);
        params.put("otp", otp);
        Log.e("jkhdfksfsdsdf","verifyOtp this is we are sending "+params);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(VerifyOtpActivity.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.pneckUserMobileVerify, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("pprraa", jsonObject.toString());
                // Utility.dismissProgressDialog();
                try {
                    Log.e("jkhdfksfsdsdf","verifyOtp success "+jsonObject);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("jiii", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                      new Handler().postDelayed(new Runnable() {
                          @Override
                          public void run() {
                              processLogin();
                          }
                      },20);

                        // sessionManager.setGotoverify(null);
                        //sessionManager.clearSession();

                    } else if (!pass) {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyOtpActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyOtpActivity.this, "Mobile verification is already done!", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(VerifyOtpActivity.this,"System error "+error.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("jkhdfksfsdsdf","otp verification case this is login error "+error.getMessage());
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
                headers.put("accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void processLogin() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("pprraa","this is exception "+e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    Map<String, String> params = new HashMap<>();
                    String uName; String uPass;
                    uName=sessionManager.getMobileNo();
                    uPass=sessionManager.getUserPassword();
                    params.put("mobile", uName);
                    params.put("password", uPass);
                    params.put("device_token", task.getResult().getToken());
                    params.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    Log.e("sdkjfskfsdf","login this is we are sending "+params);
                    progressDialog = new SpotsDialog(VerifyOtpActivity.this, R.style.Custom);
                    progressDialog.show();
                    //Utility.showProgressDialog(this);
                    RequestQueue requestQueue = Volley.newRequestQueue(VerifyOtpActivity.this);
                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.pneckUserLogin, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d("pprraa", jsonObject.toString());
                            // Utility.dismissProgressDialog();
                            progressDialog.dismiss();
                            try {
                                Log.e("jkhdfksfsdsdf","login success "+jsonObject);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                                Log.d("jiii", jsonObject1.toString());
                                Boolean pass = jsonObject1.getBoolean("success");
                                //Log.d("pass",pass);
                                String msg = jsonObject1.getString("message");
                                Log.d("massgr", msg);
                                boolean resp_status = true;
                                if (pass.equals(resp_status)) {


                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                                    String userFirstName = jsonObject2.getString("first_name");
                                    String userId = jsonObject2.getString("UID");
                                    String userTokenNo = jsonObject2.getString("ep_token");
                                    String userLastName = jsonObject2.getString("last_name");
                                    String userEmail = jsonObject2.getString("email");
                                    String userMobile = jsonObject2.getString("mobile");


                                    sessionManager.createSession(userFirstName,userMobile,userId,
                                            userLastName,userTokenNo,userEmail,
                                            jsonObject2.getString("image"));

                                    Intent intent = new Intent(VerifyOtpActivity.this, SplashActivity.class);
                                    startActivity(intent);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            VerifyOtpActivity.this.finish();
                                        }
                                    },10);

                                    Toast.makeText(VerifyOtpActivity.this, msg, Toast.LENGTH_LONG).show();

                                } else if (!pass.equals(resp_status)) {
                                    Toast.makeText(VerifyOtpActivity.this, msg, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(VerifyOtpActivity.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(VerifyOtpActivity.this,"System error "+error.getMessage(),Toast.LENGTH_SHORT).show();
                            Log.e("jkhdfksfsdsdf","login case this is login error "+error.getMessage());
                            VolleyLog.d("jkhdfksfsdsdf", "Error: " + error.getMessage());
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
                            headers.put("accept", "application/json");
                            headers.put("Content-Type", "application/json");
                            return headers;
                        }
                    };
                    requestQueue.add(stringRequest);
                }
            }
        });
    }


    private boolean checkOtp() {

        //Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(et_otp.getText().length() > 0)) {
            et_otp.setError("Please enter OTP");
            result = false;
        }

        return result;
    }


    public void resendOtp(String mob) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_number", mob);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(VerifyOtpActivity.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userResendOtp, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("pprraa", jsonObject.toString());
                // Utility.dismissProgressDialog();
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("jiii", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {


                      /*  JSONObject jsonObject2= jsonObject1.getJSONObject("data");
                        String name = jsonObject2.getString("name");
                        String mobile = jsonObject2.getString("mobile");*/
                        //Intent intent = new Intent(VerifyOtpActivity.this, VerifyOtpActivity.class);
                        // intent.putExtra("mobile",mobile);

                        // startActivity(intent);
                        // VerifyOtpActivity.this.finish();
                        Toast.makeText(VerifyOtpActivity.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(VerifyOtpActivity.this, msg, Toast.LENGTH_LONG).show();
                    } /*else {
                        Toast.makeText(VerifyOtpActivity.this, "Mobile verification ia already done!", Toast.LENGTH_LONG).show();
                    }*/
                } catch (Exception e) {
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
                headers.put("accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }
}
