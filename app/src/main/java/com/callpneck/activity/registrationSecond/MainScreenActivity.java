package com.callpneck.activity.registrationSecond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.MainActivity;
import com.callpneck.activity.registrationSecond.fragment.BookingFragment;
import com.callpneck.activity.registrationSecond.fragment.HomeFragment;
import com.callpneck.activity.registrationSecond.fragment.ProfileFragment;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.ButterKnife;

public class MainScreenActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);

        Dexter.withActivity(this)
                .withPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                    setContentView(R.layout.activity_main_screen);
                    bottom_navigation = findViewById(R.id.bottom_navigation);
                    bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                        Fragment fragment = null;

                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            if (item.getItemId() == R.id.action_home)
                                fragment = new HomeFragment();
                            else if (item.getItemId() == R.id.action_booking)
                                fragment = new BookingFragment();
                            else if (item.getItemId() == R.id.action_wallet)
                                fragment = new WalletFragment();
                            else if (item.getItemId() == R.id.action_profile)
                                fragment = new ProfileFragment();


                            return loadFragment(fragment);
                        }
                    });
                    bottom_navigation.setSelectedItemId(R.id.action_home);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();


    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}