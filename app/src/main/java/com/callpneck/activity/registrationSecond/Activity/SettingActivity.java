package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.SecurityActivity;
import com.callpneck.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private SwitchCompat fcmSwitch;
    private TextView notificationStatusTv;

    private static final String enabledMessage = "Notification are enabled";
    private static final String disabledMessage = "Notification are disabled";


    private boolean isChecked = false;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private ProgressDialog progressDialog;
    Activity activity= this;

    SessionManager sessionManager;
    Button notificationsBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backBtn = findViewById(R.id.backBtn);
        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationsBtn = findViewById(R.id.notificationsBtn);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);

        firebaseAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        //init shared preference
        sp = getSharedPreferences("SETTINGS_SP",MODE_PRIVATE);

        isChecked = sp.getBoolean("FCM_ENABLED",false);
        fcmSwitch.setChecked(isChecked);

        if(isChecked){
            //enable
            notificationStatusTv.setText(enabledMessage);
        }else {
            //disable
            notificationStatusTv.setText(disabledMessage);
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fcmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    subscribeToTopic();
                }else {
                    unSubscribeToTopic();
                }
            }
        });

        notificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareNotificationMessage("12","You Have New Order");
            }
        });
    }

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic(Const.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //subscribe successfully
                        //save setting ins shared preference
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",true);
                        spEditor.apply();
                        Toast.makeText(SettingActivity.this, ""+enabledMessage, Toast.LENGTH_SHORT).show();

                        notificationStatusTv.setText(enabledMessage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                //failed
            }
        });
    }
    private void unSubscribeToTopic(){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(Const.FCM_TOPIC)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //unSubscribed
                        //save setting ins shared preference
                        spEditor = sp.edit();
                        spEditor.putBoolean("FCM_ENABLED",false);
                        spEditor.apply();

                        Toast.makeText(SettingActivity.this, ""+disabledMessage, Toast.LENGTH_SHORT).show();
                        notificationStatusTv.setText(disabledMessage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed
                    }
                });
    }



    //SendNotification
    private void prepareNotificationMessage(String orderId,String message){
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order" + orderId;
        String NOTIFICATION_MESSAGE = "You Have"+ message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("buyerUid",sessionManager.getUserid());
            notificationBodyJo.put("sellerUid","609");  //current user is seller so uid is seller id
            notificationBodyJo.put("orderId","12");
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("FCM_RESPONSE","onResponse"+response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);
                return headers;
            }
        };

        //enqueue the Volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }
}