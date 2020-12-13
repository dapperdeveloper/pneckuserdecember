package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class ServiceDetailDescriptionActivity extends AppCompatActivity {

    TextView descriptionTv;

    String shopDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_service_detail_description);
        descriptionTv = findViewById(R.id.descriptionTv);

        if (getIntent() != null){
            shopDescription =getIntent().getStringExtra("shopDescription");
            descriptionTv.setText(shopDescription);
        }


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }
}