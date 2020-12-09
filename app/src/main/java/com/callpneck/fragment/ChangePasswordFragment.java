package com.callpneck.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.callpneck.activity.Registration.LoginActivity;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.commonutility.CommonUtility;
import com.callpneck.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    View view;
    EditText edit_oldpass, edit_newpass, edit_confirmpass;
    Button btn_changepass;
    private AlertDialog progressDialog;
    String oldPass, newPass, myMobile, confirmPass, message;
    SessionManager sessionManager;
    String userId, empToken;

    public ChangePasswordFragment() {

    }

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        return changePasswordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.changepassword_fragment, container, false);
        sessionManager = new SessionManager(getActivity());
        myMobile = sessionManager.getUserMobile();
        userId = sessionManager.getUserid();
        empToken = sessionManager.getUserToken();
        init();
        btn_changepass.setOnClickListener(this);
        return view;
    }

    public void init() {
        edit_oldpass = (EditText) view.findViewById(R.id.edit_oldpass);
        edit_newpass = (EditText) view.findViewById(R.id.edit_newpass);
        edit_confirmpass = (EditText) view.findViewById(R.id.edit_confirmpass);
        btn_changepass = (Button) view.findViewById(R.id.btn_changepass);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_changepass:

                oldPass = edit_oldpass.getText().toString();
                newPass = edit_newpass.getText().toString();

                if (validate()) {
                    if (CommonUtility.isNetworkAvailable(getActivity())) {
                        changePassword(userId, empToken, oldPass, newPass, confirmPass);
                    } else {
                        Toast.makeText(getActivity(), "Please check your internet", Toast.LENGTH_LONG).show();
                    }

                }

                break;
        }
    }

    private boolean validate() {

        //Matcher matcherObj = Pattern.compile(emailPattern).matcher(email);
        confirmPass = edit_confirmpass.getText().toString();

        boolean result = false;
        if (!(edit_oldpass.getText().length() > 0)) {
           edit_oldpass.setError("old password can't be empty");
            result = false;
        }
        else if (!(edit_newpass.getText().length() > 0)) {
            edit_newpass.setError("password can't be empty");
            result = false;
        }

        else if (!(edit_confirmpass.getText().length() > 0)) {
            edit_confirmpass.setError("password can't be empty");
            result = false;
        }

        else if (!newPass.equals(confirmPass)) {
            edit_confirmpass.setError("New and Confirm Password must be same");
            result = false;
        }
        else
        {
            result = true;

        }

        return result;
    }

    public void changePassword(String userId, String empToken, String oldPass, String newPass, String confirmPass) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("ep_token", empToken);
        params.put("old_pass", oldPass);
        params.put("password", newPass);
        params.put("c_password", confirmPass);


        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.changepassword, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
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


                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        message = jsonObject2.getString("msg");

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        sessionManager.setUserMobile(null);
                        sessionManager.clearSession();
                        startActivity(intent);
                        getActivity().finish();
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
                headers.put("accept", "application/json");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }


}
