package com.callpneck.activity.SideMenuScreens;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;

public class HelpScreen extends AppCompatActivity {

    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_help_screen);

        findViewById(R.id.call_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPneckNumber();
            }
        });
        findViewById(R.id.email_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMailToUs();
            }
        });
        findViewById(R.id.go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sessionManager=new SessionManager(HelpScreen.this);
        findViewById(R.id.drop_a_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMailToUs();
            }
        });
    }

    private void callPneckNumber(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", getResources().getString(R.string.PNECK_SUPPORT_NUMBER), null));
        startActivity(intent);
    }

    private void sendMailToUs(){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("mailto:" + getResources().getString(R.string.PNECK_SUPPORT_EMAIL)));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Pneck User Concern");
            intent.putExtra(Intent.EXTRA_TEXT, "Hi Pneck, \n\n\nThanks and Regards\n"+sessionManager.getUserName()+"\n"+
                    sessionManager.getMobileNo());
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(HelpScreen.this,"Please install email capability application.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}
