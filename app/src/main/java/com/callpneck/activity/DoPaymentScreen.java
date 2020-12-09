package com.callpneck.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DoPaymentScreen extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private TextView orderAmount;
    private AppCompatButton paymentDoneBtn;
    private ProgressBar progressBar;
    String BilingAmount;

    private void findViews() {
        orderAmount = (TextView)findViewById( R.id.order_amount );
        paymentDoneBtn = (AppCompatButton)findViewById( R.id.payment_done_btn );
        progressBar = (ProgressBar)findViewById( R.id.progress_bar );

        sessionManager=new SessionManager(DoPaymentScreen.this);
        paymentDoneBtn.setOnClickListener( this );

        BilingAmount=getIntent().getStringExtra("billing_amount");
        orderAmount.setText("Pay : ₹"+BilingAmount);
    }


    @Override
    public void onClick(View v) {
        if ( v == paymentDoneBtn ) {
            submitBillingAmount();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_do_payment_screen);

        findViews();

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(DoPaymentScreen.this,getString(R.string.YOU_ARE_IN_MIDDLE_OF_PROCESS),Toast.LENGTH_SHORT).show();
    }

    private void submitBillingAmount() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userPaymentProcess";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ses_booking_id",sessionManager.getSesBookingId());
        dataParams.put("payment_mode","COD");

        Log.e("user_registration", "this is url " +ServerURL);

        Log.e("user_registration", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                RegistrationSuccess(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DoPaymentScreen.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("user_registration", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        progressBar.setVisibility(View.GONE);
                        LaunchActivityClass.LaunchHappyFaceScreen(DoPaymentScreen.this);

                    }

                } catch (Exception e) {
                    Log.v("user_registration", "inside catch block  " + e.getMessage());
                    //e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener RegistrationError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              //  error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG+error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("user_registration", "inside error block  " + error.getMessage());
            }
        };
    }
}
