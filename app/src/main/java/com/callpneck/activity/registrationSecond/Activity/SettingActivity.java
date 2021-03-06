package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.callpneck.Const;
import com.callpneck.Language.LanguageSettingActivity;
import com.callpneck.Language.ThemeUtils;
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
    private LinearLayout changeLanguageBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_setting);

        backBtn = findViewById(R.id.backBtn);
        fcmSwitch = findViewById(R.id.fcmSwitch);
        notificationStatusTv = findViewById(R.id.notificationStatusTv);
        changeLanguageBtn = findViewById(R.id.changeLanguageBtn);

        firebaseAuth = FirebaseAuth.getInstance();
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

        changeLanguageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLanguageActivity();
            }
        });

    }

    private void openLanguageActivity() {
        Intent intent = new Intent(SettingActivity.this, LanguageSettingActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
}