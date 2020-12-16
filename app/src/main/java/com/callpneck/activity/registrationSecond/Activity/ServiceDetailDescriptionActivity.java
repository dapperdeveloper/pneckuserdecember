package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceDetailDescriptionActivity extends AppCompatActivity {

    TextView descriptionTv;

    String shopDescription;
    CircleImageView shopAvatarIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_service_detail_description);
        descriptionTv = findViewById(R.id.descriptionTv);
        shopAvatarIv = findViewById(R.id.shopAvatarIv);

        if (getIntent() != null){
            shopDescription =getIntent().getStringExtra("shopDescription");
            descriptionTv.setText(shopDescription);
            final ObjectAnimator animation = ObjectAnimator.ofFloat(shopAvatarIv, "rotationY", 0.0f, 360f);  // HERE 360 IS THE ANGLE OF ROTATE, YOU CAN USE 90, 180 IN PLACE OF IT,  ACCORDING TO YOURS REQUIREMENT
            animation.setDuration(1000); // HERE 500 IS THE DURATION OF THE ANIMATION, YOU CAN INCREASE OR DECREASE ACCORDING TO YOURS REQUIREMENT
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
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
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}