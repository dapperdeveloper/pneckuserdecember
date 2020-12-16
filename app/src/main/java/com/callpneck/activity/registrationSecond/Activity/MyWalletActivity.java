package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;

public class MyWalletActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_my_wallet);


        Fragment fragment = new WalletFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}