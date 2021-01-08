package com.callpneck.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.fragment.BookingFragment;
import com.callpneck.activity.registrationSecond.fragment.HomeFragment;
import com.callpneck.activity.registrationSecond.fragment.ProfileFragment;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executor;

public class SecurityActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        //init bio matric
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("AUTHENTICATION Error", "" + errString);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.e("AUTHENTICATION", "AUTHENTICATION SUCCESS" );
                Intent intent = new Intent(SecurityActivity.this, MainScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e("AUTHENTICATION", "FAILED");
            }
        });
        //setup title, description on auth dialog
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Pneck Authentication")
                .setSubtitle("Login using Fingerprint authentication")
                .setDeviceCredentialAllowed(true)
                .build();

        biometricPrompt.authenticate(promptInfo);

    }
}