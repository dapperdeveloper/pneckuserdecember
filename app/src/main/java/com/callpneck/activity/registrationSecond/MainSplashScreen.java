package com.callpneck.activity.registrationSecond;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;

public class MainSplashScreen extends AppCompatActivity {

    private SessionManager sessionManager;
    Boolean success=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_splash);
        RelativeLayout main_layout = (RelativeLayout)findViewById(R.id.main_layout);
        main_layout.setVisibility(View.VISIBLE);
        sessionManager = new SessionManager(MainSplashScreen.this);
        success = sessionManager.getSuccess();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean value=true;
                if (success) {
                   //Intent intent = new Intent(MainSplashScreen.this, SplashActivity.class);
                    Intent intent = new Intent(MainSplashScreen.this, MainScreenActivity.class);
                   startActivity(intent);
                   finish();
                } else {
                    Intent intent = new Intent(MainSplashScreen.this, LoginScreenMain.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }


}
