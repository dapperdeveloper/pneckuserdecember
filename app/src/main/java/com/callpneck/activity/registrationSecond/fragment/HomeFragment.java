package com.callpneck.activity.registrationSecond.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.deliveryboy.DeliveryMainActivity;
import com.callpneck.activity.registrationSecond.Activity.CheckoutActivity;
import com.callpneck.activity.registrationSecond.Activity.ProviderActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchActivity;
import com.callpneck.activity.registrationSecond.Activity.SearchLocationActivity;
import com.callpneck.activity.registrationSecond.Activity.ServiceDetailActivity;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        initView();
        loc = view.findViewById(R.id.loc);
        addressTv = view.findViewById(R.id.addressTv);
        ll = view.findViewById(R.id.ll);
        locationBtn = view.findViewById(R.id.locationBtn);

        getLocationPermission();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sessionManager = new SessionManager(getContext());
        getDeviceLocation();

        if (InternetConnection.checkConnection(getContext()))
        getData();


        loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchLocationActivity();
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

    private void openSearchLocationActivity() {
        Intent intent = new Intent(getActivity(), SearchLocationActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
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
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        Call<MainDashboard> mainDashboardCall = RetrofitClient.getInstance()
                .getApi()
                .getDashData();
        mainDashboardCall.enqueue(new Callback<MainDashboard>() {
            @Override
            public void onResponse(Call<MainDashboard> call, Response<MainDashboard> response) {
                mainDashboard = response.body();
                if (mainDashboard.getResponse().getData().getSubcategoryList().size()>0 && mainDashboard != null){
                    loadCategoryView(mainDashboard.getResponse().getData().getSubcategoryList());
                }


                if (mainDashboard.getResponse().getData().getBannerSliderImages().size()>0 && mainDashboard != null){
                    loadBanner(mainDashboard.getResponse().getData().getBannerSliderImages());

                }

                progressBar.setVisibility(View.GONE);
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
                    Intent intent = new Intent(getContext(), ShopHomeActivity.class);
                    intent.putExtra("categoryName", item.getTitle());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);

                } else if(item.getCate_type().equalsIgnoreCase("cab")){
                    Intent intent = new Intent(getContext(), TaxiMainActivity.class);
                    intent.putExtra("categoryName", item.getTitle());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
                else if(item.getCate_type().equalsIgnoreCase("provider")){
                    Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
                    intent.putExtra("categoryName", item.getTitle());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
                else if(item.getCate_type().equalsIgnoreCase("shop")){
                    Intent intent = new Intent(getContext(), ProviderActivity.class);
                    intent.putExtra("categoryName", item.getTitle());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
                else if(item.getCate_type().equalsIgnoreCase("delivery")){
                    Intent intent = new Intent(getContext(), DeliveryMainActivity.class);
                    intent.putExtra("categoryName", item.getTitle());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                }
                else {
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
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

            Glide.with(mContext).load(bannerDatumList.get(position).getImage()).placeholder(R.drawable.pneck_icon).into(imageView);
            container.addView(itemView);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;

                }
            }
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getDeviceLocation() {
        Log.e("skjlksfjsfs", "inside getDeviceLocation ");
        Toast.makeText(getContext(), "Getting current location", Toast.LENGTH_SHORT).show();
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation != null) {
                                getCompleteAddressString(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            }

                        } else {
                            Log.d("Seraj", "Current location is null. Using defaults.");
                            Log.e("Seraj", "Exception: %s", task.getException());

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("location_get_error", e.getMessage());
        }
    }

    private void getCompleteAddressString(double latitude, double longitude) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
                locality = addresses.get(0).getSubLocality();

                UserLatitude = "" + latitude;
                UserLongitude = "" + longitude;

                sessionManager.setUserLocation(UserLatitude, UserLongitude);
                currentFullAddress = address;
                Log.d("Seraj", address);
                addressTv.setText(address);

                Log.e("CURRENTADDRESS", "Latitude AAAAAA is called...." + UserLatitude.toString());
                Log.e("CURRENTADDRESS", "Longitude AAAAAA  is called...." + UserLongitude.toString());
                Log.e("CURRENTADDRESS", "address AAAAAA is called...." + address.toString());
                //CompleteAddress.setSelection(CompleteAddress.getText().length());

            }

        } catch (Exception e) {
            Log.e("kjfksfsfsf", "this is error exception " + e.getMessage());
        }

    }

    //end user current location


}