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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.deliveryboy.DeliveryBoyListActivity;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.deliveryboy.TrackOrderDeliveryActivity;
import com.callpneck.activity.registrationSecond.Activity.MoreActivity;
import com.callpneck.activity.registrationSecond.Activity.MyWalletActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderDetailActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchLocationActivity;
import com.callpneck.activity.registrationSecond.Activity.ShopHomeActivity;
import com.callpneck.activity.registrationSecond.Adapter.CatRestaurantNearItem;
import com.callpneck.activity.registrationSecond.Adapter.MyCategoryAdapter;
import com.callpneck.activity.registrationSecond.Adapter.MyCustomPagerAdapter;
import com.callpneck.activity.registrationSecond.DemoModel.CatResModel;
import com.callpneck.activity.registrationSecond.InterFace.Adapter_Click_Listener;
import com.callpneck.activity.registrationSecond.Model.Category;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.model.dashboard.BannerSliderImage;
import com.callpneck.model.dashboard.MainDashboard;
import com.callpneck.model.dashboard.SubcategoryList;
import com.callpneck.taxi.TaxiMainActivity;
import com.callpneck.utils.AutoScrollViewPager;
import com.callpneck.utils.InternetConnection;
import com.facebook.shimmer.ShimmerFrameLayout;
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
import com.rd.PageIndicatorView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    ImageView loc, ll;
    TextView addressTv;

    private RelativeLayout locationBtn;
    private SessionManager sessionManager;
    ViewPager viewPager;
    PageIndicatorView pageIndicatorView;
    private Context mContext;
    MyCustomPagerAdapter myCustomPagerAdapter;
    private static final int SPEECH_REQUEST_CODE = 0;

    private static final int LOCATION_REQUEST_CODE = 100;

    private ShimmerFrameLayout mShimmerCat, shimerPromo, getShimmerchantnear;

    private LinearLayout bannerSlider, shimlistnear,shimlistcatnear;
    private TextView walletBlncTv, user_name;
    String myName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void shimmershow() {

        mShimmerCat.startShimmerAnimation();

        shimerPromo.startShimmerAnimation();
        getShimmerchantnear.startShimmerAnimation();
    }
    private void shimmertutup() {
        recyclerView.setVisibility(View.VISIBLE);

        mShimmerCat.setVisibility(View.GONE);
        mShimmerCat.stopShimmerAnimation();

        shimerPromo.setVisibility(View.GONE);
        shimerPromo.stopShimmerAnimation();

        getShimmerchantnear.stopShimmerAnimation();
        getShimmerchantnear.setVisibility(View.GONE);
    }
    private void init(View view) {
        loc = view.findViewById(R.id.loc);
        addressTv = view.findViewById(R.id.addressTv);
        ll = view.findViewById(R.id.ll);
        locationBtn = view.findViewById(R.id.locationBtn);
        searchIcon = view.findViewById(R.id.searchIcon);
        voiceSearch = view.findViewById(R.id.voiceSearch);
        recyclerView = view.findViewById(R.id.recycler_view);
        viewPager = view.findViewById(R.id.viewPager);
        pageIndicatorView = view.findViewById(R.id.pageIndicatorView);

        mShimmerCat = view.findViewById(R.id.shimmercat);
        shimerPromo = view.findViewById(R.id.shimmepromo);
        bannerSlider = view.findViewById(R.id.rlslider);
        walletBlncTv = view.findViewById(R.id.balance);
        getShimmerchantnear = view.findViewById(R.id.shimmerchantnear);
        shimlistnear = view.findViewById(R.id.shimlistnear);
        shimlistcatnear = view.findViewById(R.id.shimlistcatnear);
        user_name = view.findViewById(R.id.namapengguna);
        categoryList = new ArrayList<>();
    }

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);

        TextView nighttext = view.findViewById(R.id.nighttext);
        ImageView timeStatusIv = view.findViewById(R.id.timeStatusIv);
        initView();
        //init permission
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sessionManager = new SessionManager(getContext());

        String[] parsedpagi = "04:00".split(":");
        String[] parsedsiang = "11:00".split(":");
        String[] parsedsore = "13:00".split(":");
        String[] parsedmalam = "18:00".split(":");

        int pagi = Integer.parseInt(parsedpagi[0]), menitPagi = Integer.parseInt(parsedpagi[1]);
        int siang = Integer.parseInt(parsedsiang[0]), menitSiang = Integer.parseInt(parsedsiang[1]);
        int sore = Integer.parseInt(parsedsore[0]), menitSore = Integer.parseInt(parsedsore[1]);
        int malam = Integer.parseInt(parsedmalam[0]), menitMalam = Integer.parseInt(parsedmalam[1]);
        int totalpagi = (pagi * 60) + menitPagi;
        int totalsiang = (siang * 60) + menitSiang;
        int totalsore = (sore * 60) + menitSore;
        int totalmalam = (malam * 60) + menitMalam;

        Calendar now = Calendar.getInstance();
        int totalMenitNow = (now.get(Calendar.HOUR_OF_DAY) * 60) + now.get(Calendar.MINUTE);

        if (totalMenitNow >= totalpagi && totalMenitNow <= totalsiang && totalMenitNow <= totalsore && totalMenitNow <= totalmalam ) {
            nighttext.setText("Good Morning");
            timeStatusIv.setImageResource(R.drawable.ic_sunrise);
        } else if (totalMenitNow >= totalpagi && totalMenitNow >= totalsiang && totalMenitNow <= totalsore && totalMenitNow <= totalmalam ) {
            nighttext.setText("Good Afternoon");
            timeStatusIv.setImageResource(R.drawable.ic_noon);
        } else if (totalMenitNow >= totalpagi && totalMenitNow >= totalsiang && totalMenitNow >= totalsore && totalMenitNow <= totalmalam ) {
            nighttext.setText("Good Afternoon");
            timeStatusIv.setImageResource(R.drawable.ic_evening);
        } else {
            nighttext.setText("Good Night");
            timeStatusIv.setImageResource(R.drawable.ic_night);
        }


        shimmershow();
        if (AppController.isConnected(getActivity())){
            getWalletBalance();
            getData();
        }

        myName = sessionManager.getUserName();
        if (myName!=null){
            user_name.setText(myName);
        }else {
            user_name.setText("Pneck User");
        }


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


        //Static Demo

       RecyclerView rvcatmerchantnear = view.findViewById(R.id.catmerchantnear);
        rvcatmerchantnear.setHasFixedSize(true);
        rvcatmerchantnear.setNestedScrollingEnabled(false);
        rvcatmerchantnear.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        List<CatResModel> dataList = new ArrayList<>();
        dataList.add(new CatResModel("All"));
        dataList.add(new CatResModel("Food"));
        dataList.add(new CatResModel("Veg"));
        dataList.add(new CatResModel("Non-veg"));
        dataList.add(new CatResModel("Snacks"));

        if (dataList.size()>0){
            shimlistcatnear.setVisibility(View.GONE);
            rvcatmerchantnear.setVisibility(View.VISIBLE);
            CatRestaurantNearItem restaurantNearItem =  new CatRestaurantNearItem(dataList, getContext(), R.layout.item_cat_restaurant, new CatRestaurantNearItem.OnItemClickListener() {
                @Override
                public void onItemClick(CatResModel item) {

                }
            });
            rvcatmerchantnear.setAdapter(restaurantNearItem);
        }

        //static Demo
        clickListener();

        return view;

    }

    private void getWalletBalance() {
        Call<GetWallet> call = APIClient.getInstance().getWallet(sessionManager.getUserid());
        call.enqueue(new Callback<GetWallet>() {
            @Override
            public void onResponse(Call<GetWallet> call, retrofit2.Response<GetWallet> response) {
                GetWallet getWallet = response.body();
                if (getWallet != null && getWallet.getStatus()){
                    walletBlncTv.setText("₹"+getWallet.getAmount()+"");
                }
            }

            @Override
            public void onFailure(Call<GetWallet> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
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
        Call<MainDashboard> mainDashboardCall = APIClient.getInstance()
                .getDashData();
        mainDashboardCall.enqueue(new Callback<MainDashboard>() {
            @Override
            public void onResponse(Call<MainDashboard> call, Response<MainDashboard> response) {
                if (response.isSuccessful()){
                    if (Objects.requireNonNull(response.body()).getResponse().getSuccess()){
                        shimmertutup();
                        mainDashboard = response.body();
                        if (mainDashboard.getResponse().getData().getSubcategoryList().size()>0 && mainDashboard != null){
                            loadCategoryView(mainDashboard.getResponse().getData().getSubcategoryList());
                        }

                        if (mainDashboard.getResponse().getData().getBannerSliderImages().size()>0 && mainDashboard != null){

                            bannerSlider.setVisibility(View.VISIBLE);
                            loadBanner(mainDashboard.getResponse().getData().getBannerSliderImages());

                        }
                    }

                }


            }

            @Override
            public void onFailure(Call<MainDashboard> call, Throwable t) {

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
                else if(item.getCate_type().equalsIgnoreCase("more")){
                    Intent intent = new Intent(getContext(), MoreActivity.class);
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

        pageIndicatorView.setCount(bannerSliderImages.size());
        pageIndicatorView.setSelection(0);
        myCustomPagerAdapter = new MyCustomPagerAdapter(mContext, bannerSliderImages, new Adapter_Click_Listener() {
            @Override
            public void onItemClick(View view, int pos, Object object) {

                String type = bannerSliderImages.get(pos).getCate_type();
                String title = bannerSliderImages.get(pos).getTitle();
                if (type.equalsIgnoreCase("restaurant")) {
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null){
                        Intent intent = new Intent(getContext(), ShopHomeActivity.class);
                        intent.putExtra("categoryName", title);
                        startActivity(intent);
                    }


                } else if(type.equalsIgnoreCase("cab")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), TaxiMainActivity.class);
                        intent.putExtra("categoryName", title);
                        startActivity(intent);
                    }
                }
                else if(type.equalsIgnoreCase("provider")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), ProviderDetailActivity.class);
                        intent.putExtra("categoryName", title);
                        startActivity(intent);
                    }
                }
                else if(type.equalsIgnoreCase("shop")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {
                        Intent intent = new Intent(getContext(), ProviderActivity.class);
                        intent.putExtra("categoryName", title);
                        startActivity(intent);
                    }
                }
                else if(type.equalsIgnoreCase("delivery")){
                    if (sessionManager.getUserLatitude()!= null && sessionManager.getUserLongitude()!=null) {

                        if (sessionManager.getCurrentDeliveryOrderId()!=null&&
                                sessionManager.getCurrentDeliveryOrderId().length()>0){
                            Intent intent=new Intent(getContext(), TrackOrderDeliveryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(getContext(), DeliveryMainActivity.class);
                            intent.putExtra("categoryName", title);
                            startActivity(intent);
                        }

                    }
                }
                else if(type.equalsIgnoreCase("wallet")){
                    Intent intent = new Intent(getContext(), MyWalletActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getContext(), "Location not set", Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewPager.setAdapter(myCustomPagerAdapter);
//        viewPager.startAutoScroll();
//        viewPager.setInterval(3000);
//        viewPager.setCycle(true);
//        viewPager.setStopScrollWhenTouch(true);
//        tabview.setupWithViewPager(viewPager, true);

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