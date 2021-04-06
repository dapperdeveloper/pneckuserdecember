package com.callpneck.activity.SideMenuScreens;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.utils.Constants;

public class AboutUsScreen extends AppCompatActivity {

    TextView about_us;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_about_us_screen);

        about_us = findViewById(R.id.about_us);
        about_us.setText(Constants.aboutUs);
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        findViewById(R.id.privacy_policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Launch privacy policy

            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}
