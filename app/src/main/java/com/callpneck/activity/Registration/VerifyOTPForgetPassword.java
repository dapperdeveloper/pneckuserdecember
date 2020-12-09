package com.callpneck.activity.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.commonutility.CommonUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;

public class VerifyOTPForgetPassword extends AppCompatActivity implements View.OnClickListener{

    Button btn_veryfyotp;
    EditText edit_otp;
    private AlertDialog progressDialog;
    String otp, empMobile;
    private TextView resendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        Intent intent = getIntent();
        empMobile = intent.getStringExtra("mobile");
        init();
        clickListeners();
        btn_veryfyotp.setOnClickListener(this);
        runTimer();
    }

    private void clickListeners() {
        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendOTPCounter++;
                resendOTPprocess();
            }
        });
    }

    private int SendOTPCounter=0;
    private void runTimer(){

        if (SendOTPCounter==0||SendOTPCounter==1){
            StartCountdownTimer(30000);
        }else if(SendOTPCounter==2||SendOTPCounter==3){
            StartCountdownTimer(60000);
        }else {
            StartCountdownTimer(60000);
        }

        resendOTP.setEnabled(false);
    }

    private void StartCountdownTimer(int timer_millis){
        new CountDownTimer(timer_millis, 1000) {

            public void onTick(long millisUntilFinished) {
                if((millisUntilFinished / 1000)<10){
                    resendOTP.setText("00." + "0"+millisUntilFinished / 1000);

                }else {
                    resendOTP.setText("00." + millisUntilFinished / 1000);

                }

            }

            public void onFinish() {
                resendOTP.setEnabled(true);
                resendOTP.setText("Resend OTP");
            }

        }.start();

    }


    private void resendOTPprocess() {

        runTimer();

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userResendOtp";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("mobile_number",empMobile);
        Log.e("otp_verfication", "this is url " +ServerURL);

        Log.e("otp_verfication", "this is we sending " + dataParams.toString());
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                OTPSuccess(),
                OtpError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(VerifyOTPForgetPassword.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> OTPSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("otp_verfication", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {


                    }

                } catch (Exception e) {
                    Log.v("otp_verfication", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener OtpError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("otp_verfication", "inside error block  " + error.getMessage());
            }
        };
    }


    public void init() {
        btn_veryfyotp = (Button) findViewById(R.id.btn_veryfyotp);
        edit_otp = (EditText) findViewById(R.id.edit_otp);
        resendOTP=findViewById(R.id.text_resendotp);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_veryfyotp:

                otp = edit_otp.getText().toString();

                if (checkValidate()) {
                    if (CommonUtility.isNetworkAvailable(this)) {
                        sendOtp(empMobile, otp);
                        //Toast.makeText(ForgotPasswordScreen.this, "OTP has been send to your register number", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Please check your internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Please Enter Right Information", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void sendOtp(String empMobile, String otp) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile_number", empMobile);
        params.put("otp", otp);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(VerifyOTPForgetPassword.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.forgetpassword, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //   Log.d("otpdata", jsonObject.toString());
                // Utility.dismissProgressDialog();
                progressDialog.dismiss();
                try {
                    Log.e("jkhdfsfsffs","this is the complete response "+jsonObject);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {


                       /* JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String mobile = jsonObject2.getString("name");*/


                        Intent intent = new Intent(VerifyOTPForgetPassword.this, LoginActivity.class);

                        startActivity(intent);
                        VerifyOTPForgetPassword.this.finish();
                        Toast.makeText(VerifyOTPForgetPassword.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(VerifyOTPForgetPassword.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VerifyOTPForgetPassword.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                    }
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
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    private boolean checkValidate() {

        //Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(edit_otp.getText().length() > 0)) {
            edit_otp.setError("Please enter otp");
            result = false;
        }

        return result;
    }
}
