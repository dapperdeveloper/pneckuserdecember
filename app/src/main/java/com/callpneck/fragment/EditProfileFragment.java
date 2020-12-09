package com.callpneck.fragment;

import android.app.AlertDialog;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    private RadioGroup radioGroup;
    private RadioButton radioButton, radioMale, radioFemale;
    private AlertDialog progressDialog;
    String gender, empId, empToken, firstName, lastname, vehicle_number, address;
    Button btn_update_profile;
    EditText et_fstname, et_lstname, et_address;
    String f_name, l_name, update_address, myaddress, genderr;
    SessionManager sessionManager;

    public EditProfileFragment() {

    }

    public static EditProfileFragment newInstance() {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        return editProfileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.update_profile_fragment, container, false);
        sessionManager = new SessionManager(getActivity());
        empId = sessionManager.getUserid();
        empToken = sessionManager.getUserToken();
        init();
        f_name = getArguments().getString("fname");
        l_name = getArguments().getString("lname");


        //myaddress = getArguments().getString("address");
        if (getArguments().getString("address") != null) {
            myaddress = getArguments().getString("address");
            et_address.setText(myaddress);
        }
        if (getArguments().getString("gender") != null) {
            genderr = getArguments().getString("gender");
            if (genderr.equals("Male"))
            {
                radioMale.setChecked(true);
            } else if (genderr.equals("Female"))
            {
                radioFemale.setChecked(true);
            }
        }
        et_fstname.setText(f_name);
        et_lstname.setText(l_name);




        // find the radiobutton by returned id

        btn_update_profile.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update_profile:

                firstName = et_fstname.getText().toString();
                lastname = et_lstname.getText().toString();
                address = et_address.getText().toString();
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == radioMale.getId()) {
                    Log.d("male_id",""+selectedId);
                    gender = radioMale.getText().toString();
                    Log.d("male_gender",gender);
                } else if (selectedId == radioFemale.getId()) {
                    Log.d("male_id",""+selectedId);
                    gender = radioFemale.getText().toString();
                    Log.d("female_gender",gender);
                }

                if (radioGroup.getCheckedRadioButtonId() == -1) {

                    Toast.makeText(getActivity(), "please select gender", Toast.LENGTH_LONG).show();
                } else {
                    if (validate()) {
                        if (CommonUtility.isNetworkAvailable(getActivity())) {
                            pNeckUpdateProfile(empId, empToken, firstName, lastname, gender, address);
                        } else {
                            Toast.makeText(getActivity(), "Please check your internet", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please fill correct information", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
        }
    }

    public void pNeckUpdateProfile(String empId, String empToken, String first_name, String last_name, String gender, String address) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", empId);
        params.put("ep_token", empToken);
        params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("usr_gender", gender);
        params.put("usr_address", address);
        // Utility.showProgressDialog(this);
        progressDialog = new SpotsDialog(getActivity(), R.style.Custom);
        progressDialog.show();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.editUserProfile, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("uprofile", jsonObject.toString());
                // Utility.dismissProgressDialog();
                progressDialog.dismiss();


                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("profile", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("pmessage", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {


                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        update_address = jsonObject2.getString("usr_address");
                        genderr = jsonObject2.getString("usr_gender");

                        sessionManager.setEmployeeAddress(update_address);
                        sessionManager.setEmployeeGender(genderr);
                        // empLname = jsonObject2.getString("last_name");
                        // String empMobile = jsonObject2.getString("mobile");
                        //  String empEmail = jsonObject2.getString("email");
                        gotoHome();


                    } else if (!pass.equals(resp_status)) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Please enter right credential", Toast.LENGTH_LONG).show();
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

    //
    private boolean validate() {

        boolean result = true;
        if (!(et_fstname.getText().length() > 0)) {
            et_fstname.setError("First name can't be empty");
            result = false;
        }
        if (!(et_lstname.getText().length() > 0)) {
            et_lstname.setError("Last name can't be empty");
            result = false;
        }

        if (!(et_address.getText().length() > 0)) {
            et_address.setError("Please enter address");
            result = false;
        }
        return result;
    }
    //

    public void init() {
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        btn_update_profile = (Button) view.findViewById(R.id.btn_update_profile);
        et_fstname = (EditText) view.findViewById(R.id.et_fstname);
        et_lstname = (EditText) view.findViewById(R.id.et_lstname);
        et_address = (EditText) view.findViewById(R.id.et_address);
        //radioButton = (RadioButton) view.findViewById(selectedId);
        radioMale = (RadioButton) view.findViewById(R.id.radioMale);
        radioFemale = (RadioButton) view.findViewById(R.id.radioFemale);
    }

    public void gotoHome() {
        /*getFragmentManager().popBackStack();
        Fragment fragment = new ProfileFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();*/
    }
}
