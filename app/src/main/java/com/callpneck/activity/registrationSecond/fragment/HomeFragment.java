package com.callpneck.activity.registrationSecond.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.deliveryboy.DeliveryBoyListActivity;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.registrationSecond.Activity.MyWalletActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchLocationActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.activity.registrationSecond.Adapter.MyCategoryAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyCustomPagerAdapter;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.model.dashboard.BannerSliderImage;
import com.callpneck.model.dashboard.MainDashboard;
import com.callpneck.model.dashboard.SubcategoryList;
import com.callpneck.taxi.TaxiMainActivity;
import com.callpneck.utils.AutoScrollViewPager;
import com.callpneck.utils.InternetConnection;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.callpneck.SessionManager.isopen;
import static com.callpneck.activity.registrationSecond.helper.Constant.LAUNCH_ADDRESS_SET_SCREEN;
import static com.callpneck.activity.registrationSecond.helper.Constant.REQUEST_CHECK_SETTINGS;


public class
HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    public static final int PERMISSIONS_REQUEST_TOKEN = 2001;
    private String currentFullAddress = "";
    private String UserLatitude = "";
    private String UserLongitude = "";
    private String spokenText ="";
    private double latitude, longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private RecyclerView recyclerView;
    List<Category> categoryList;
    private MyCategoryAdapter adapter;
    MainDashboard mainDashboard;

    private ImageView searchIcon, voiceSearch;
    private AVLoadingIndicatorView progressBar;
    ImageView loc, ll;
    TextView addressTv;

    private LinearLayout locationBtn;
    private SessionManager sessionManager;
    AutoScrollViewPager viewPager;
    TabLayout tabview;
    private Context mContext;
    MyCustomPagerAdapter myCustomPagerAdapter;
    private static final int SPEECH_REQUEST_CODE = 0;

    private static final int LOCATION_REQUEST_CODE = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void init(View view) {
        loc = view.findViewById(R.id.loc);
        addressTv = view.findViewById(R.id.addressTv);
        ll = view.findViewById(R.id.ll);
        locationBtn = view.findViewById(R.id.locationBtn);
        searchIcon = view.findViewById(R.id.searchIcon);
        voiceSearch = view.findViewById(R.id.voiceSearch);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        viewPager = view.findViewById(R.id.viewPager);
        tabview = view.findViewById(R.id.tabview);
        categoryList = new ArrayList<>();
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        initView();
        //init permission
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sessionManager = new SessionManager(getContext());

        if (AppController.isConnected(getActivity()))
        getData();

//        sessionManager.clearDeliveryOrderSession();
//        sessionManager.setBooleanData(isopen, false);

/*
        sessionManager.setSesBookingId(null);
        sessionManager.setOtpVerified(false);
        sessionManager.setOrderStatus(null);
        sessionManager.setDestination(null,null);
        sessionManager.setDeliveryOtp(null);
        sessionManager.setUserValues("...","...","...","...","Generating");
        sessionManager.clearOrderSession();


 */
        if(checkLocationPermission()){
            detectLocation();
        }

        clickListener();

        return view;

    }

    private void clickListener() {
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    settingsrequest();
                }else {
                    askForPermission();
                }
            }
        });
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
                startActivityForResult(intent, SPEECH_REQUEST_CODE);
            }
        });
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity(spokenText);
            }
        });
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission()){
                    settingsrequest();
                }else {
                    askForPermission();
                }
            }
        });
    }


    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat
                    .checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                    .checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                return false;
            }else {
                return true;
            }
        }else {
            return true;
        }
    }
    public void askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat
                    .checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)+
                    ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.FOREGROUND_SERVICE)+
                    ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) +  ContextCompat
                    .checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.FOREGROUND_SERVICE)||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    Snackbar snackbar = Snackbar
                            .make(view.findViewById(R.id.main_layout), getString(R.string.PERMISSION_SNACK_BAR_MESSAGE), Snackbar.LENGTH_LONG);
                    TextView snack_tv = (TextView)snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.primary_800));
                    snack_tv.setGravity(Gravity.CENTER_HORIZONTAL);
                    snack_tv.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                    snackbar.setActionTextColor(getResources().getColor(R.color.white));
                    snackbar.setAction(R.string.ENABLE, new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.FOREGROUND_SERVICE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_TOKEN);
                        }
                    });
                    snackbar.show();

                }else {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.FOREGROUND_SERVICE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_TOKEN);
                }

            }
        }else {
            if (!GPSstatusCheck()) {
                settingsrequest();
            }
        }
    }
    public boolean GPSstatusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_TOKEN:
                if (grantResults.length> 0) {
                    boolean locationPemission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationPemission){
                        if (InternetConnection.checkConnection(getContext())){

                            settingsrequest();

                        }else {
                            Toast.makeText(getContext(),getResources().getString(R.string.CHECK_YOUR_INTERNET_CONNECTION),Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getContext(),"Permission Denied can't post Global post.",Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){

                        detectLocation();
                    }else {
                        Toast.makeText(getContext(), "Location permission is necessary.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }


    @SuppressLint("MissingPermission")
    private void detectLocation() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if(location != null){

                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String address = addresses.get(0).getAddressLine(0);
                        sessionManager.setUserScreenAddress(address);
                        sessionManager.setUserLocation( latitude+"", ""+longitude);
                        addressTv.setText(sessionManager.getUserScreenAddress());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


    public void settingsrequest() {

        mSettingsClient = LocationServices.getSettingsClient(getActivity());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("TAG", "All location settings are satisfied.");


                        //noinspection MissingPermission
                        openSearchLocationActivity();

                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("TAG", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("TAG", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("TAG", errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }





    private void openSearchLocationActivity() {
        Intent intent = new Intent(getActivity(), SearchLocationActivity.class);
        startActivityForResult(intent,LAUNCH_ADDRESS_SET_SCREEN);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }


    private void alertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle("Current Location");
        alertDialogBuilder.setMessage(currentFullAddress);
        alertDialogBuilder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        if (!TextUtils.isEmpty(currentFullAddress)) {
            alertDialogBuilder.create().show();
        }


    }


    private void openSearchActivity(String spokenText) {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        intent.putExtra("spokenText", spokenText);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Call<MainDashboard> mainDashboardCall = APIClient.getInstance()
                .getDashData();
        mainDashboardCall.enqueue(new Callback<MainDashboard>() {
            @Override
            public void onResponse(Call<MainDashboard> call, Response<MainDashboard> response) {
                try {
                    mainDashboard = response.body();
                    if (mainDashboard.getResponse().getData().getSubcategoryList().size()>0 && mainDashboard != null){
                        loadCategoryView(mainDashboard.getResponse().getData().getSubcategoryList());
                    }

                    if (mainDashboard.getResponse().getData().getBannerSliderImages().size()>0 && mainDashboard != null){
                        loadBanner(mainDashboard.getResponse().getData().getBannerSliderImages());

                    }

                    progressBar.setVisibility(View.GONE);
                }catch (Exception e){
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<MainDashboard> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadCategoryView(List<SubcategoryList> subcategoryList) {
        adapter = new MyCategoryAdapter(getContext(), subcategoryList, new MyCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SubcategoryList item) {
                if (item.getCate_type().equalsIgnoreCase("restaurant")) {
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null){
                        Intent intent = new Intent(getContext(), ShopHomeActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }


                } else if(item.getCate_type().equalsIgnoreCase("cab")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), TaxiMainActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("provider")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), ProviderDetailActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("shop")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), ProviderActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("delivery")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {

                        if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                sessionManager.getCurrentDeliveryOrderId().length()>0){
                            LaunchActivityClass.LaunchTrackingDeliveryScreen(getActivity());
                        }
                        else {
                            Intent intent = new Intent(getContext(), DeliveryMainActivity.class);
                            intent.putExtra("categoryName", item.getTitle());
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                        }

                    }
                }
                else if(item.getCate_type().equalsIgnoreCase("wallet")){
                    Intent intent = new Intent(getContext(), MyWalletActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                }
                else {
                    Toast.makeText(getContext(), "Location not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void loadBanner(List<BannerSliderImage> bannerSliderImages) {
        myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, bannerSliderImages);
        viewPager.setAdapter(myCustomPagerAdapter);
        viewPager.startAutoScroll();
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        tabview.setupWithViewPager(viewPager, true);

    }


    private void initView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
//        recyclerView.addItemDecoration(new SpaceItemDecoration(0));
    }



    //get current user location




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CHECK_SETTINGS){
            if (resultCode==RESULT_OK) {
                openSearchLocationActivity();
            }else if (resultCode==RESULT_CANCELED){
                Log.e("TAG","location enabl cancled"+data.getExtras());
            }
        }
        else if (requestCode==LAUNCH_ADDRESS_SET_SCREEN){
            if (data!=null&&data.hasExtra("user_complete_address")){
                String address =data.getStringExtra("user_complete_address");
                UserLatitude=data.getStringExtra("user_latitude");
                UserLongitude=data.getStringExtra("user_longitude");
                sessionManager.setUserScreenAddress(address);
                sessionManager.setUserLocation(UserLatitude, UserLongitude);
                addressTv.setText(sessionManager.getUserScreenAddress());
            }
        }
        else if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK){
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            spokenText = results.get(0);
            // Do something with spokenText
            openSearchActivity(spokenText);
        }
    }

    //end user current location


}