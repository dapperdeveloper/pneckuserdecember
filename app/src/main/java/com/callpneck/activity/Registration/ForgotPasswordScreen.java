package com.callpneck.activity.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.callpneck.R;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.commonutility.CommonUtility;
import com.callpneck.SessionManager;
import com.callpneck.utils.BaseActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ForgotPasswordScreen extends BaseActivity implements View.OnClickListener {

    EditText et_mobile;
    Button btn_vry;
    private AlertDialog progressDialog;
    String mobileNo;
    SessionManager sessionManager;
    String myNumber, myMail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_activity);
        sessionManager = new SessionManager(this);
        initview();
        setListner();

    }

    public void initview() {
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        btn_vry = (Button) findViewById(R.id.btn_vry);
    }

    public void setListner() {
        btn_vry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_vry:
                myNumber = et_mobile.getText().toString();

                if (checkValidate()) {

                    if (CommonUtility.isNetworkAvailable(this)) {
                        sendOtp(myNumber);
                        //Toast.makeText(ForgotPasswordScreen.this, "OTP has been send to your register number", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordScreen.this, "Please check your internet", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ForgotPasswordScreen.this, "Please Enter mobile number", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void sendOtp(String Number) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", Number);
        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPasswordScreen.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.passwordVerify, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                //   Log.d("otpdata", jsonObject.toString());
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


                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String mobile = jsonObject2.getString("mobile");
                        String message = jsonObject2.getString("msg");



                        Intent intent = new Intent(ForgotPasswordScreen.this, VerifyOTPForgetPassword.class);
                        intent.putExtra("mobile",myNumber);
                        startActivity(intent);
                        ForgotPasswordScreen.this.finish();
                        Toast.makeText(ForgotPasswordScreen.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(ForgotPasswordScreen.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordScreen.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                    }
                }
            catch (Exception e) {
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

    private boolean checkValidate() {

        //Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(et_mobile.getText().length() > 0)) {
            et_mobile.setError("Please enter mobile number");
            result = false;
        }

        return result;
    }


}
