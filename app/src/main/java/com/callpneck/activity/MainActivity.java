package com.callpneck.activity;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.callpneck.R;
import com.callpneck.activity.Registration.LoginActivity;
import com.callpneck.fragment.AboutUsFragment;
import com.callpneck.fragment.ChangePasswordFragment;
import com.callpneck.fragment.HelpFragment;
import com.callpneck.fragment.NotificationFragment;
import com.callpneck.fragment.SettingFragment;
import com.callpneck.SessionManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DrawerLayout drawer;
    private FragmentManager fragmentManager;
    public static Toolbar mToolbar;
    LinearLayout menu_home_setting, menu_home_ll, menu_home_faqs, menu_home_market, menu_change_pass, ll_logout, ll_np,
            menu_home_ll_helps, menu_home_notification, menu_home_myorder, ll_aboutus;
    ImageView iv_logo;
    SessionManager sessionManager;
    String myMobile, myName;
    TextView user_name, user_mobile;
    ImageView user_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        myMobile = sessionManager.getUserMobile();
        myName = sessionManager.getUserName();
        initview();
        setMain();
        menu_home_setting.setOnClickListener(this);
        menu_change_pass.setOnClickListener(this);
        //menu_home_setting.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        menu_home_notification.setOnClickListener(this);
        menu_home_ll_helps.setOnClickListener(this);
        menu_home_myorder.setOnClickListener(this);
        ll_np.setOnClickListener(this);
        ll_aboutus.setOnClickListener(this);
        user_name.setText(myName);
        user_mobile.setText(myMobile);

    }

    public void initview() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        menu_home_setting = (LinearLayout) findViewById(R.id.menu_home_setting);
        menu_home_ll = (LinearLayout) findViewById(R.id.menu_home_ll);
        menu_change_pass = (LinearLayout) findViewById(R.id.menu_change_pass);
        // menu_home_market = (LinearLayout) findViewById(R.id.menu_home_market);
        user_name = (TextView) findViewById(R.id.user_name);
        user_image = (ImageView) findViewById(R.id.user_image);
        user_mobile = (TextView) findViewById(R.id.user_mobile);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        menu_home_ll_helps = findViewById(R.id.menu_home_ll_helps);
        menu_home_notification = findViewById(R.id.menu_home_notification);
        menu_home_myorder = findViewById(R.id.menu_home_myorder);
        ll_np = findViewById(R.id.ll_np);
        ll_aboutus = findViewById(R.id.ll_aboutus);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, this.getTheme());
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_change_pass:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                changePassword();
                break;

            case R.id.menu_home_ll:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                // homeFragment();
                break;

            case R.id.menu_home_setting:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                settingFragment();
                break;

            case R.id.menu_home_notification:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                notificationFragment();

                break;

            case R.id.menu_home_ll_helps:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                helpsFragment();
                break;

            case R.id.ll_aboutus:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                aboutus();
                break;

            case R.id.ll_logout:
                new AlertDialog.Builder(this).setTitle("Logout").setMessage("Do you want to Logout.")
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //  logOutUser();

                                dialog.cancel();
                                sessionManager.setUserMobile(null);
                                sessionManager.clearSession();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                MainActivity.super.finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();


                break;
            case R.id.ll_np:
                fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                gotoprofile();
                break;
        }
    }


    private void setFragment(final Fragment fragment, String title) {
        mToolbar.setTitle(title);
        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment, null);
        //ft.addToBackStack(null);

        ft.commit();
    }

    public void setMain() {
        //setFragment(MyMapFragment.newInstance(), "DashBoard");
        // NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void settingFragment() {
        Fragment fragment = new SettingFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void changePassword() {
        Fragment fragment = new ChangePasswordFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void gotoprofile() {
       /* Fragment fragment = new ProfileFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();*/
    }

    public void notificationFragment() {
        Fragment fragment = new NotificationFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void helpsFragment() {
        Fragment fragment = new HelpFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }

    public void aboutus() {
        Fragment fragment = new AboutUsFragment();
        FragmentManager fm = getSupportFragmentManager();
        int id = 0;
        if (fm.getBackStackEntryCount() >= 1) {
            id = fm.getBackStackEntryAt(0).getId();
            fm.popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fm.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }


    @Override
    public void onBackPressed() {
        // super.onBackPressed();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {

                new AlertDialog.Builder(this).setTitle("Exit").setMessage("Do you want to Exit.")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();


            } else {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
                getSupportFragmentManager().popBackStack();
            }

        }
    }
}
