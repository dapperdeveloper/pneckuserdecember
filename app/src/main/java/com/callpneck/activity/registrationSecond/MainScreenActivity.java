package com.callpneck.activity.registrationSecond;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.MainActivity;
import com.callpneck.activity.registrationSecond.Adapter.MyCategoryAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyCustomPagerAdapter;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.fragment.BookingFragment;
import com.callpneck.activity.registrationSecond.fragment.HomeFragment;
import com.callpneck.activity.registrationSecond.fragment.OrderFragment;
import com.callpneck.activity.registrationSecond.fragment.ProfileFragment;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;
import com.callpneck.model.dashboard.MainDashboard;
import com.callpneck.utils.AutoScrollViewPager;
import com.callpneck.utils.Constants;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS
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
                            fragment = new OrderFragment();
                        else if (item.getItemId() == R.id.action_wallet)
                            fragment = new WalletFragment();
                        else if (item.getItemId() == R.id.action_profile)
                            fragment = new ProfileFragment();


                        return loadFragment(fragment);
                    }
                });
                bottom_navigation.setSelectedItemId(R.id.action_home);



                if (AppController.isConnected(MainScreenActivity.this)){
                    getRezarPayId();
                    getWebsiteUrl();
                }
               // showDialog();

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }
        }).check();


    }

    private void getWebsiteUrl() {
        Call<JsonObject> call = APIClient.getInstance().getWebsiteUrl();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    if(jsonObject.has("data")){

                        JSONObject c = jsonObject.getJSONObject("data");
                        Constants.tremAndCondition = c.getString("term_condition");
                        Constants.privacyPolicy = c.getString("privacy");

                    }

                }catch (Exception e){
                    e.toString();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void getRezarPayId() {

        Call<JsonObject> call = APIClient.getInstance().paymentKey();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(new Gson().toJson(response.body()));
                    if(jsonObject.has("data")){

                        JSONObject c = jsonObject.getJSONObject("data");
                        Constants.razorKeyId = c.getString("key_id");
                        Constants.isRazor = Boolean.parseBoolean(c.getString("status"));

                    }

                }catch (Exception e){
                    e.toString();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    // this will show the sticky notification during uploading video
    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        //id for notification, random
        int notificationID = new Random().nextInt(3000);
        final String CHANNEL_ID = "default";
        final String CHANNEL_NAME = "Default";

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(defaultChannel);
        }

        androidx.core.app.NotificationCompat.Builder builder = (androidx.core.app.NotificationCompat.Builder) new androidx.core.app.NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setContentTitle("Uploading Video")
                .setContentText("Please wait! Video is uploading....")
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        android.R.drawable.stat_sys_upload))
                .setContentIntent(pendingIntent);

        //show notification
        notificationManager.notify(notificationID, builder.build());
    }
    public void showDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        View view = LayoutInflater.from(this).inflate(R.layout.wear_a_mask,null);
        dialog.setContentView(view);

        ImageView imageView = view.findViewById(R.id.jj);
        Glide.with(this).load(R.drawable.maskgif).into(imageView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

            }
        }, 1000);
    }
}