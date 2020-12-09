package com.callpneck.activity.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.callpneck.utils.BaseActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

import static android.widget.Toast.LENGTH_LONG;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    EditText et_username, et_pass;
    RelativeLayout relative_login;
    String username, password;
    // private ProgressDialog progressDialog;
    String getPhone, getEmail, getPass;
    private AlertDialog progressDialog;
    Button btn_login;
    TextView tv_forgot;
    String mobileNo;
    public SessionManager sessionManager;
    private ImageView passWardEye;
    private boolean isEyeOpen=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        sessionManager = new SessionManager(this);

        TextView tv_forgot = findViewById(R.id.tv_forgot);
        TextView tv_signup = findViewById(R.id.tv_signup);
        /*Bundle bundle = getIntent().getExtras();
        getPhone = bundle.getString("phone");
        getEmail = bundle.getString("email");
        getPass = bundle.getString("pass");*/

        et_username = findViewById(R.id.et_username);
        et_pass = findViewById(R.id.et_pass);
      //  tv_forgot = findViewById(R.id.tv_forgot);
        btn_login = (Button) findViewById(R.id.btn_login);
         tv_forgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordScreen.class)));
        tv_signup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        btn_login.setOnClickListener(this);
       // tv_forgot.setOnClickListener(this);

        passWardEye=findViewById(R.id.confirm_pass_eye);

        passWardEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEyeOpen){
                    passWardEye.setImageResource(R.drawable.ic_eye_closed);
                    et_pass.setTransformationMethod(new PasswordTransformationMethod());
                }else {
                    passWardEye.setImageResource(R.drawable.ic_eye);
                    et_pass.setTransformationMethod(null);
                }
                et_pass.setSelection(et_pass.getText().length());
                isEyeOpen=!isEyeOpen;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                username = et_username.getText().toString();
                password = et_pass.getText().toString();
                //checkValidtion();
                if (validate()) {
                    if (CommonUtility.isNetworkAvailable(this)) {
                        pNeckUserLogin(username, password);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please check your internet", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Please fill correct information", Toast.LENGTH_LONG).show();

                }
                break;

        }
    }

    public void checkValidtion() {

        if (username.isEmpty() && username == null) {
            et_username.setError("Username not empty");
        }
        if (password.isEmpty() && password == null) {
            et_pass.setError("Password not empty");
        } else {
            if (CommonUtility.isNetworkAvailable(this)) {
                //pNeckUserLogin(username, password);
            } else {
                Toast.makeText(LoginActivity.this, "Please check your internet", LENGTH_LONG).show();
            }

        }
    }

    private boolean validate() {

        //Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(et_username.getText().length() > 0)) {
            et_username.setError("username can't be empty");
            result = false;
        }
        if (!(et_pass.getText().length() > 0)) {
            et_pass.setError("password can't be empty");
            result = false;
        }

        return result;
    }
/*

    private void pNeckUserLogin(String uName, String uPass) {


        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("skdfhjskfd","this is exception "+e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userLogin";
                    HashMap<String, String> dataParams = new HashMap<String, String>();

                    progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
                    progressDialog.show();

                    Map<String, String> params = new HashMap<>();
                    params.put("mobile", uName);
                    params.put("password", uPass);
                    params.put("device_token", task.getResult().getToken());
                    params.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));


                    Log.e("user_login", "this is url " +ServerURL);

                    Log.e("user_login", "this is we sending " + dataParams.toString());

                    CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                            ServerURL,
                            dataParams,
                            RegistrationSuccess(),
                            RegistrationError());
                    dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                            (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    Volley.newRequestQueue(LoginActivity.this).add(dataParamsJsonReq);
                }
            }
        });

    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressDialog.dismiss();
                    JSONObject jsonObject1 = response.getJSONObject("response");
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
                                userLastName,userTokenNo,userEmail,jsonObject2.getString("image"));

                        // intent.putExtra("mobile",mobile);
                        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.v("user_login", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener RegistrationError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                Log.v("user_registration", "inside error block  " + error.getMessage());
            }
        };
    }
*/


    public void pNeckUserLogin(String uName, String uPass) {
        Log.d("user_login", "calling user login ");

        progressDialog = new SpotsDialog(LoginActivity.this, R.style.Custom);
        progressDialog.show();

        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("user_login","this is exception "+e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful()){
                    Map<String, String> params = new HashMap<>();
                    params.put("mobile", uName);
                    params.put("password", uPass);
                    params.put("device_token", task.getResult().getToken());
                    params.put("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                    Log.e("user_login","login this is we are sending "+params);
                    //

                    //Utility.showProgressDialog(this);
                    RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                    JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.pneckUserLogin, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d("user_login", jsonObject.toString());
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
                                    String userFirstName = jsonObject2.getString("first_name");
                                    String userId = jsonObject2.getString("UID");
                                    String userTokenNo = jsonObject2.getString("ep_token");
                                    String userLastName = jsonObject2.getString("last_name");
                                    String userEmail = jsonObject2.getString("email");
                                    String userMobile = jsonObject2.getString("mobile");


                                    sessionManager.createSession(userFirstName,userMobile,userId,
                                            userLastName,userTokenNo,userEmail,jsonObject2.getString("image"));

                                    // intent.putExtra("mobile",mobile);
                                    Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                    startActivity(intent);
                                    LoginActivity.this.finish();
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();

                                } else if (!pass.equals(resp_status)) {
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("user_login","login error "+error.getMessage());
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
        });
    }

}
