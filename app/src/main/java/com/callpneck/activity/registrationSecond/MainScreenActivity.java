package com.callpneck.activity.registrationSecond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.fragment.BookingFragment;
import com.callpneck.activity.registrationSecond.fragment.HomeFragment;
import com.callpneck.activity.registrationSecond.fragment.ProfileFragment;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainScreenActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
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

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    public void wallet()
    {
        View view = findViewById(R.id.action_wallet);
        view.performClick();
    }
    public void booking()
    {
        View view = findViewById(R.id.action_booking);
        view.performClick();
    }
}