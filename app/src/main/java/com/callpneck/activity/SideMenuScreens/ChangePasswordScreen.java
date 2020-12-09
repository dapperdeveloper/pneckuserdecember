package com.callpneck.activity.SideMenuScreens;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.fragment.ChangePasswordFragment;

public class ChangePasswordScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_change_password_screen);
        changePassword();
    }
    public void changePassword() {
        Fragment fragment = new ChangePasswordFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        fm.beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }
}
