package com.callpneck.activity.SideMenuScreens;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class SettingScreen extends AppCompatActivity {

    private ImageView goBack;
    private LinearLayout changePassword;


    private void findViews() {
        goBack = (ImageView)findViewById( R.id.go_back );
        changePassword = (LinearLayout)findViewById( R.id.change_password );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_setting_screen);

        findViews();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
}
