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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchLocationActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.activity.registrationSecond.Adapter.MyCategoryAdapter;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.api.retrofit.RetrofitClient;
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
import com.google.android.gms.maps.model.LatLng;
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
import static com.callpneck.activity.registrationSecond.helper.Constant.LAUNCH_ADDRESS_SET_SCREEN;
import static com.callpneck.activity.registrationSecond.helper.Constant.REQUEST_CHECK_SETTINGS;


public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }
    public static final int PERMISSIONS_REQUEST_TOKEN = 2001;
    private String city = "";
    private String state = "";
    private String SubAdminAreaAfterState = "";
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private String locality = "";
    private String currentFullAddress = "";
    private String UserLatitude = "";
    private String UserLongitude = "";
    private double latitude, longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;

    private LatLng mDefaultLocation;
    private float DEFAULT_ZOOM = 17.0f;

    private RecyclerView recyclerView;
    List<Category> categoryList;
    private MyCategoryAdapter adapter;
    MainDashboard mainDashboard;

    private ImageView searchIcon;
    private AVLoadingIndicatorView progressBar;


    private boolean mLocationPermissionGranted = true;
    ImageView loc, ll;
    TextView addressTv;

    private LinearLayout locationBtn;
    private SessionManager sessionManager;
    AutoScrollViewPager viewPager;
    TabLayout tabview;
    private Context mContext;
    MyCustomPagerAdapter myCustomPagerAdapter;
    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermission;
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
        searchIcon = view.findViewById(R.id.searchIcon);
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
        loc = view.findViewById(R.id.loc);
        addressTv = view.findViewById(R.id.addressTv);
        ll = view.findViewById(R.id.ll);
        locationBtn = view.findViewById(R.id.locationBtn);


        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sessionManager = new SessionManager(getContext());

        if (AppController.isConnected(getActivity()))
        getData();

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

        if(checkLocationPermission()){
            detectLocation();
        }else {
            requestLocationPermission();
        }

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
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });

        return view;

    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(getActivity(),locationPermission,LOCATION_REQUEST_CODE);
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

    private void openSearchActivity() {
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Call<MainDashboard> mainDashboardCall = RetrofitClient.getInstance()
                .getApi()
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


        //  Call<DashBoard> dashBoardCall = ApiClient.getInstance()
        //      .getApi()
        //     .dashboard(email,pass);
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
                        Intent intent = new Intent(getContext(), DeliveryMainActivity.class);
                        intent.putExtra("categoryName", item.getTitle());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
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


    public class MyCustomPagerAdapter extends PagerAdapter {
        Context context;
        List<BannerSliderImage> bannerDatumList;
        LayoutInflater layoutInflater;

        public MyCustomPagerAdapter(Context context, List<BannerSliderImage> bannerDatumList) {
            this.context = context;
            this.bannerDatumList = bannerDatumList;
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        @Override
        public int getCount() {
            return bannerDatumList.size();
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==  object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = layoutInflater.inflate(R.layout.layout_banner_look, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.image_look_book);
            try {
                Glide.with(mContext).load(bannerDatumList.get(position).getImage()).placeholder(R.drawable.pneck_icon).into(imageView);
            }catch (Exception e){

            }

            container.addView(itemView);
            if (bannerDatumList.get(position).getImage()!=null)
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, ""+bannerDatumList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                }
            });
            return itemView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
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
    }

    //end user current location


}