package com.callpneck.activity;

import android.app.Activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class OrderCompleteHappyScreen extends AppCompatActivity {

    private SessionManager sessionManager;
    private String sesBookingId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_order_complete_happy_screen);

        sessionManager=new SessionManager(OrderCompleteHappyScreen.this);
        sesBookingId=sessionManager.getSesBookingId();


        findViewById(R.id.home_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setSesBookingId(null);
                sessionManager.setOtpVerified(false);
                sessionManager.setOrderStatus(null);
                sessionManager.setDestination(null,null);
                sessionManager.setDeliveryOtp(null);
                sessionManager.setUserValues("...","...","...","...","Generating");
                sessionManager.clearOrderSession();

                LaunchActivityClass.LaunchMainActivity(OrderCompleteHappyScreen.this);
            }
        });

        RateUsDialog(OrderCompleteHappyScreen.this);

    }


    public void RateUsDialog(Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.rate_us_alert_dialog, null);

        TextView CancelDialog;
        TextView SubmitRating;

        final LinearLayout commentLayout = mView.findViewById(R.id.comment_layout);
        final RatingBar ratingBar = mView.findViewById(R.id.user_rating);
        final EditText userFeedBack = mView.findViewById(R.id.user_feed_back_comment);

        CancelDialog = (TextView) mView.findViewById(R.id.Cancel_dialog);
        SubmitRating = (TextView) mView.findViewById(R.id.submit_rating);


        builder.setView(mView);
        final AlertDialog NewCategory_dialog = builder.create();

        NewCategory_dialog.setCancelable(false);


        SubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() > 0) {
                    NewCategory_dialog.dismiss();
                    submitRatingToServer(ratingBar.getRating(), userFeedBack.getText().toString());

                } else {
                    Toast.makeText(OrderCompleteHappyScreen.this, "Please provide your feedback", Toast.LENGTH_SHORT).show();
                }

            }
        });

        CancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCategory_dialog.dismiss();
            }
        });

        NewCategory_dialog.show();

    }


    private void submitRatingToServer(float rating, String user_feedback) {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/bookingRatingAdd";
        HashMap<String, String> dataParams = new HashMap<String, String>();
/*

        ses_booking_id,  user_type,
                rating , message
*/

        //dataParams.put("user_id", sessionManager.getUserID());
        dataParams.put("ses_booking_id", sesBookingId);
        dataParams.put("user_type", "user");
        dataParams.put("rating", "" + rating);
        if (user_feedback.length() == 0) {
            user_feedback = "empty";
        }
        dataParams.put("message", user_feedback);

        Log.e("user_feedback", " that's we are sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                UserFeedbackSuccessListener(),
                UserFeedbackErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(OrderCompleteHappyScreen.this).add(dataParamsJsonReq);

    }

    private String tag = "rating_feedback";

    private Response.Listener<JSONObject> UserFeedbackSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.v(tag, "this is complete response " + response);

                    if (response.getBoolean("success")) {
                        Toast.makeText(OrderCompleteHappyScreen.this, "Your rating submitted successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("sdjkffsfs", "this is error exception " + e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener UserFeedbackErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                Log.v(tag, "inside error block  " + error.getMessage());
            }
        };
    }

}
