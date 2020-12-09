package com.callpneck.activity.SideMenuScreens;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;

public class ReferAndEarn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_refer_and_earn);

        findViewById(R.id.share_and_earn).setOnClickListener(new View.OnClickListener() {
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
}
