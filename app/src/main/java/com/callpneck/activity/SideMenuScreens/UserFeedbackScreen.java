package com.callpneck.activity.SideMenuScreens;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class UserFeedbackScreen extends AppCompatActivity {

    private LinearLayout toolbar;
    private ImageView goBack;
    private TextInputEditText userName;
    private TextInputEditText subject;
    private TextInputEditText message;
    private ProgressBar progressBar;
    private MaterialRippleLayout submitFeedback;
    private SessionManager sessionManager;


    private void findViews() {
        toolbar = (LinearLayout)findViewById( R.id.toolbar );
        goBack = (ImageView)findViewById( R.id.go_back );
        userName = (TextInputEditText)findViewById( R.id.user_name );
        subject = (TextInputEditText)findViewById( R.id.subject );
        message = (TextInputEditText)findViewById( R.id.message );
        progressBar = (ProgressBar)findViewById( R.id.progress_bar );
        submitFeedback = (MaterialRippleLayout)findViewById( R.id.submit_feedback );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_user_feedback_screen);
        findViews();
        clickListeners();
        sessionManager=new SessionManager(UserFeedbackScreen.this);
    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

    private void clickListeners() {
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        submitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName.getText().toString().length()<=0){
                    userName.setError("User name is empty");
                    return;
                }
                if (subject.getText().toString().length()<=0){
                    subject.setError("Subject is empty");
                    return;
                }
                if (message.getText().toString().length()<=0){
                    message.setError("Message is empty");
                    return;
                }
                submitFeedBackMethod();
            }
        });
    }


    private void submitFeedBackMethod() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userAddFeedback";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id", sessionManager.getUserid());
        dataParams.put("ep_token", sessionManager.getUserToken());
        dataParams.put("name", userName.getText().toString());
        dataParams.put("subj",subject.getText().toString());
        dataParams.put("message",message.getText().toString());

        Log.e("user_feedback", "this is url " +ServerURL);

        Log.e("user_feedback", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                RegistrationSuccess(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(UserFeedbackScreen.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> RegistrationSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("user_feedback", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UserFeedbackScreen.this,getString(R.string.FEEDBACK_SUBMITED_SUCCESS),Toast.LENGTH_SHORT).show();
                        finish();
                    }

                } catch (Exception e) {
                    Log.v("user_feedback", "inside catch block  " + e.getMessage());
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                Log.v("user_feedback", "inside error block  " + error.getMessage());
            }
        };
    }

}
