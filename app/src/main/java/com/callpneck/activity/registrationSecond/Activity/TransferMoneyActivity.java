package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class TransferMoneyActivity extends AppCompatActivity {

    View top_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_transfer_money);

        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        top_view=findViewById(R.id.top_view);
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);
        top_view.startAnimation(anim);
        top_view.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        top_view.setVisibility(View.GONE);
        finish();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

    }

}