package com.callpneck.activity.Registration;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.commonutility.CommonUtility;
import com.callpneck.model.UserRegistrationModel;
import com.callpneck.SessionManager;
import com.callpneck.utils.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class SignupActivity extends BaseActivity implements View.OnClickListener {
    RelativeLayout rl_register1;
    EditText edit_fname, edit_lname, edit_email, edit_phone, edit_password, edit_cpassword;
    String name, lname, email, phone, password, c_password;
    String device_id;
    Button btn_login;
    // private ProgressDialog progressDialog;
    UserRegistrationModel userRegistrationModel;
    ArrayList<UserRegistrationModel> arrayList;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private AlertDialog progressDialog;
    public SessionManager sessionManager;
    private ImageView passWardEye,ConfirmpasswordEye;

    private boolean isEyeOpen=false,ReisEyeOpen=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
        sessionManager = new SessionManager(this);
        device_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        init();
        setListener();
        //rl_register.setOnClickListener(v -> startActivity(new Intent(SignupActivity.this, MainActivity.class)));
    }

    public void init() {
        passWardEye=findViewById(R.id.pass_eye);
        ConfirmpasswordEye=findViewById(R.id.confirm_pass_eye);
        btn_login = (Button) findViewById(R.id.btn_login);
        edit_fname = findViewById(R.id.edit_fname);
        edit_lname = findViewById(R.id.edit_lname);
        edit_email = findViewById(R.id.edit_email);
        edit_phone = findViewById(R.id.edit_phone);
        edit_password = findViewById(R.id.edit_password);
        edit_cpassword = findViewById(R.id.edit_cpassword);
    }

    public void setListener() {

        passWardEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEyeOpen){
                    passWardEye.setImageResource(R.drawable.ic_eye_closed);
                    edit_password.setTransformationMethod(new PasswordTransformationMethod());
                }else {
                    passWardEye.setImageResource(R.drawable.ic_eye);
                    edit_password.setTransformationMethod(null);
                }
                edit_password.setSelection(edit_password.getText().length());
                isEyeOpen=!isEyeOpen;
            }
        });

        ConfirmpasswordEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReisEyeOpen){
                    ConfirmpasswordEye.setImageResource(R.drawable.ic_eye_closed);
                    edit_cpassword.setTransformationMethod(new PasswordTransformationMethod());
                }else {
                    ConfirmpasswordEye.setImageResource(R.drawable.ic_eye);
                    edit_cpassword.setTransformationMethod(null);
                }
                edit_cpassword.setSelection(edit_cpassword.getText().length());
                ReisEyeOpen=!ReisEyeOpen;
            }
        });

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                name = edit_fname.getText().toString();
                lname = edit_lname.getText().toString();
                email = edit_email.getText().toString();
                phone = edit_phone.getText().toString();
                password = edit_password.getText().toString();
                c_password = edit_cpassword.getText().toString();
                //checkValidtion();

                if (validate()) {
                    if (!password.equals(c_password)) {
                        Toast.makeText(SignupActivity.this, "Password and Confirm Password must be same", Toast.LENGTH_LONG).show();
                    } else if (CommonUtility.isNetworkAvailable(this)) {
                        pneckUserRegistration(name, lname, email, phone, password, c_password, device_id);
                    } else {
                        Toast.makeText(SignupActivity.this, "Please check your internet", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(SignupActivity.this, "Please fill correct information", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public void checkValidtion() {

        if (edit_fname.getText().toString().isEmpty() && edit_fname.getText().toString().trim().equals("")) {
            edit_fname.setError("First name not empty");
        }
        if (edit_lname.getText().toString().isEmpty() && edit_lname.getText().toString().trim().equals("")) {
            edit_lname.setError("Last name not empty");
        }
        if (edit_email.getText().toString().isEmpty() && edit_email.getText().toString().trim().equals("")) {
            edit_email.setError("Email not empty");
        }
        if (!(edit_email.getText().toString().trim()).matches(emailPattern)) {
            edit_email.setError("Email not valid");
        }
        if (edit_phone.getText().toString().isEmpty() && edit_phone.getText().toString().trim().equals("")) {
            edit_phone.setError("Phone no not empty");
        }
        if (edit_phone.getText().toString().trim().length() < 10) {
            edit_phone.setError("Please enter valid Phone number");
        }
        if (edit_password.getText().toString().isEmpty() && edit_password.getText().toString().trim().equals("")) {
            edit_password.setError("Password not empty");
        }
        if (password.length() < 5) {
            edit_password.setError("Password length should not less then 5 digit");
        } else {

            if (CommonUtility.isNetworkAvailable(this)) {
                // pneckUserRegistration(name, lname, email, phone, password, device_id);
            } else {
                Toast.makeText(SignupActivity.this, "Please check your internet", Toast.LENGTH_LONG).show();
            }
        }
    }

    //
    private boolean validate() {

        Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);

        boolean result = true;
        if (!(edit_fname.getText().length() > 0)) {
            edit_fname.setError("First name can't be empty");
            result = false;
        }
        if (!(edit_lname.getText().length() > 0)) {
            edit_lname.setError("Last name can't be empty");
            result = false;
        }
        if (!matcherObj.matches()) {
            edit_email.setError("E-mail is not valid");
            result = false;
        }
        if (!(edit_password.getText().length() > 5)) {
            edit_password.setError("password lenght greater than 5 digit");
            result = false;
        }
        if (!(edit_phone.getText().length() == 10)) {
            edit_phone.setError("please enter correct phone no");
            result = false;
        }
        if (!(edit_cpassword.getText().length() > 5)) {
            edit_cpassword.setError("Password length greater then  5 digit");
            result = false;
        }

        return result;
    }
    //

    private void pneckUserRegistration(String name, String lname, String email, String phone, String password, String c_password, String device_id) {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/UserRegister";
        Map<String, String> params = new HashMap<>();
        params.put("first_name", name);
        params.put("last_name", lname);
        params.put("email", email);
        params.put("mobile", phone);
        params.put("password", password);
        params.put("c_password", c_password);
        params.put("device_id", device_id);

        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();

        sessionManager.saveUserMobileAndPass(phone,password);

        Log.e("user_registration", "this is url " +ServerURL);

        Log.e("user_registration", "this is we sending " + params.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                params,
                RegistrationSuccess(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(SignupActivity.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.e("user_registration","this is response "+response);
                    JSONObject jsonObject1 = response.getJSONObject("response");
                    Log.d("jiii", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                        progressDialog.dismiss();

                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String name = jsonObject2.getString("name");
                        String mobile = jsonObject2.getString("mobile");
                        otpverification(mobile);
                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG+error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("user_registration", "inside error block  " + error.getMessage());
            }
        };
    }

/*

    public void pneckUserRegistration(String name, String lname, String email, String phone, String password, String c_password, String device_id) {
        Map<String, String> params = new HashMap<>();
        params.put("first_name", name);
        params.put("last_name", lname);
        params.put("email", email);
        params.put("mobile", phone);
        params.put("password", password);
        params.put("c_password", c_password);
        params.put("device_id", device_id);

        progressDialog = new SpotsDialog(this, R.style.Custom);
        progressDialog.show();

        sessionManager.saveUserMobileAndPass(phone,password);
        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.pneckUserRegistration, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("pprraa", jsonObject.toString());

                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("jiii", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                        progressDialog.dismiss();

                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String name = jsonObject2.getString("name");
                        String mobile = jsonObject2.getString("mobile");
                        otpverification(mobile);
                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(SignupActivity.this, msg, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(SignupActivity.this, "Please enter right credential", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
*/

    private void otpverification(String mobile) {
        Intent intent = new Intent(SignupActivity.this, VerifyOtpActivity.class);
        intent.putExtra("mobile", mobile);
        startActivity(intent);
        SignupActivity.this.finish();
    }


}
