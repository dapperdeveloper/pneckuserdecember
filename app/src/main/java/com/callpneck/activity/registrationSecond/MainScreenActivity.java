package com.callpneck.activity.registrationSecond;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.Activity.SettingActivity;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.fragment.HomeFragment;
import com.callpneck.activity.registrationSecond.fragment.OrderFragment;
import com.callpneck.activity.registrationSecond.fragment.ProfileFragment;
import com.callpneck.activity.registrationSecond.fragment.WalletFragment;
import com.callpneck.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import org.json.JSONObject;

import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.callpneck.SessionManager.isFetchLocation;

public class MainScreenActivity extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;

    SessionManager sessionManager;
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
                spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);

                sessionManager = new SessionManager(MainScreenActivity.this);
                spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
                spaceNavigationView.addSpaceItem(new SpaceItem("Home", R.drawable.ic_home_menu));
                spaceNavigationView.addSpaceItem(new SpaceItem("Booking", R.drawable.ic_booking_menu));
                spaceNavigationView.addSpaceItem(new SpaceItem("Wallet", R.drawable.ic_wallet_menu));
                spaceNavigationView.addSpaceItem(new SpaceItem("Profile", R.drawable.ic_user_profile_menu));

                spaceNavigationView.setCentreButtonColor(ContextCompat.getColor(MainScreenActivity.this, R.color.colorPrimary));
                spaceNavigationView.setActiveSpaceItemColor(ContextCompat.getColor(MainScreenActivity.this, R.color.colorPrimary));
                spaceNavigationView.setInActiveSpaceItemColor(ContextCompat.getColor(MainScreenActivity.this, R.color.black));
                spaceNavigationView.setSpaceItemIconSize((int) getResources().getDimension(R.dimen.space_item_icon_only_size));
                spaceNavigationView.setSpaceItemTextSize((int) getResources().getDimension(R.dimen.txt_size));

                spaceNavigationView.setCentreButtonRippleColor(ContextCompat.getColor(MainScreenActivity.this, R.color.white));
                spaceNavigationView.showIconOnly();

                //spaceNavigationView.setFont(Typeface.createFromAsset(getAssets(), "your_cutom_font.ttf"));
                spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
                    @Override
                    public void onCentreButtonClick() {

                        startActivity(new Intent(MainScreenActivity.this, SettingActivity.class));

                    }

                    @Override
                    public void onItemClick(int itemIndex, String itemName) {

                        //Toast.makeText(HomeActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                        Fragment fragment = null;
                        switch (itemIndex) {

                            case 0:
                                fragment = new HomeFragment();
                                break;
                            case 1:
                                fragment =new OrderFragment();
                                break;
                            case 2:
                                fragment =  new WalletFragment();
                                break;
                            case 3:
                                fragment =new ProfileFragment();
                                break;
                        }
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                    }

                    @Override
                    public void onItemReselected(int itemIndex, String itemName) {

                        //Toast.makeText(HomeActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                    }
                });


                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();



                /*
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


                 */


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
                        Constants.aboutUs = c.getString("about");

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

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        sessionManager.setBooleanFetchData(isFetchLocation, false);
        super.onDestroy();
    }
}