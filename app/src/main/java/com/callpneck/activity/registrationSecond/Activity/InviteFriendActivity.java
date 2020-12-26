package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class InviteFriendActivity extends AppCompatActivity {


    TextView txtinvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_invite_friend);

        txtinvite = findViewById(R.id.txtinvite);
        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        txtinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

    }
    private void shareApp() {
        String applink;
        try {
            applink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
        } catch (android.content.ActivityNotFoundException anfe) {
            applink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pneck your local service provider");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Pneck provide most trusted and fastest service at your door step." +
                "\n\nClick on the below link and join Pneck network.\n"+applink);
        startActivity(Intent.createChooser(sharingIntent, "Choose app..."));
    }
    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(200);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}