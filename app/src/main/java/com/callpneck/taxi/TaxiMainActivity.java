package com.callpneck.taxi;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.model.MapPointerModel;
import com.callpneck.taxi.Adapter.DriverAdapter;
import com.callpneck.taxi.Adapter.PlaceAutoCompleteAdapter;
import com.callpneck.taxi.Adapter.ServiceAdapter;
import com.callpneck.taxi.activity.DriverListActivity;
import com.callpneck.taxi.activity.RealTimeActivity;
import com.callpneck.taxi.map.WebSocketListener;
import com.callpneck.taxi.model.CarType;
import com.callpneck.taxi.model.driver.DriverModel;
import com.callpneck.utils.Constants;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;


public class TaxiMainActivity extends AppCompatActivity implements OnMapReadyCallback, WebSocketListener {

    private static final String TAG ="Seraj" ;
    LinearLayout mainLayout,cashOfferLinearLayout;
    TextView whereto;
    TextView cashTv;
    EditText cashOffered,etDestination,discription;
    ImageButton searchBtn;
    GoogleMap mMap;
    Map<String, MapPointerModel> allMarkersMap = new HashMap<>();
    private ArrayList<MapPointerModel> mapsList = new ArrayList<>();
    private SessionManager sessionManager;
    Button demoBtn,getRideBtn;
    String bookingId;
    RecyclerView locationsRv;
    private String city = "";
    private String state = "";
    SlidingUpPanelLayout slidingUpPanelLayout;
    private EditText CompleteAddress;
    private ImageButton mCurrentLocationBtn;
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private String locality = "";
    private String currentFullAddress="";
    private String UserLatitude = "";
    private String UserLongitude = "";
    ProgressBar pb;
    private LinearLayout TryAgainView;
    private TextView TryAgainBtn;
    private RectangularBounds bounds;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    //not initilized till now
    private LatLng mDefaultLocation;
    private float DEFAULT_ZOOM = 1.0f;
    private String userId,epToken;
    private String your_booking_number, your_booking_status, your_booking_status_msg;
    Dialog bookingDialog;
    String p_ses_bk_id;
    Timer timer_pending_status;
    private long waiteTime=1000*60*2;
    private CountDownTimer mCountDownTimer;
    TextView currentAddress;
    DriverModel driverModel;
    DriverAdapter driverAdapter;
    RecyclerView driverRecycler;
    private PlacesClient placesClient;
    View view;

    private int progressStatus = 0;
    private long countDownInterval = 1000; //1 second (don't change this value)
    private String destination_latti;
    private String destination_longi;
    private String desinationAddress;
    AutocompleteSessionToken token;
    private String cash_offered;
    ServiceAdapter adapter;
    int bk_id;

    String cash;
    Boolean isEditable = true;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));
    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;
    RecyclerView serviceRv;

    //mk
    List<LatLng> nearbyCabLocations = new ArrayList<>();
    List<LatLng> pickupPath = new ArrayList<>();
    List<LatLng> shownearbyCabLocations = new ArrayList<>();
    Polyline greyPolyLine;
    Polyline blackPolyline;
    Marker originMarker;
    Marker destinationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_main);
        Places.initialize(getApplicationContext(), "AIzaSyBTbcRRCLbhqeMVVGD5fnUevHfxj2MdgdI");
        placesClient = Places.createClient(this);
        serviceRv=findViewById(R.id.service_rv);
        getCarType("11");
        token = AutocompleteSessionToken.newInstance();
        cashOfferLinearLayout = findViewById(R.id.cash_linear_layout);
        mainLayout = findViewById(R.id.dragView);
        searchBtn = findViewById(R.id.search);
        locationsRv=findViewById(R.id.address_recycler);
        bounds = RectangularBounds.newInstance(BOUNDS_INDIA);

        getRideBtn = findViewById(R.id.ride_now);
        cashOffered=findViewById(R.id.cash_offer);
        discription=findViewById(R.id.discription);
        slidingUpPanelLayout=findViewById(R.id.sliding_layout);

        TryAgainView=findViewById(R.id.try_again_view);
        TryAgainBtn=findViewById(R.id.try_again_btn);
        sessionManager = new SessionManager(TaxiMainActivity.this);
        view = this.getCurrentFocus();




        if (sessionManager.getSesBookingId()!=null){
            startActivity(new Intent(TaxiMainActivity.this, RealTimeActivity.class));
        }



        bookingDialog = new Dialog(TaxiMainActivity.this);
        pb=new ProgressBar(this);
        etDestination=findViewById(R.id.destination);
        currentAddress=findViewById(R.id.source);
        //map initialized
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this);
        locationsRv.setLayoutManager(new LinearLayoutManager(this));
        locationsRv.setAdapter(mAutoCompleteAdapter);
        clickListeners();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();




    }

    public void onItemClickAddressRecycler(int position){
        if (mAutoCompleteAdapter.getItemCount() == 0) return;
        String locationName = mAutoCompleteAdapter.getPredictions()
                .get(position).getFullText(null).toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

            sessionManager.setDestinationAddress(addresses.get(0).getSubLocality());
            Log.d("Serajduration",addresses.get(0).getAddressLine(0));
            mMap.addMarker(new MarkerOptions().position(new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude())));
            String address = addresses.get(0).getAddressLine(0);
            desinationAddress = address;
            destination_latti=""+addresses.get(0).getLatitude();
            destination_longi=""+addresses.get(0).getLongitude();
            // hideKeyboard(TaxiMainActivity.this);
            cashLayout(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
        } catch (IOException e) {
            // e.printStackTrace();
            Log.println(Log.ERROR, "Tag", e.toString());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void clickListeners() {

        Log.d("Seraj",""+slidingUpPanelLayout.getPanelState());

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etDestination.getText().toString().trim();
                Log.d("Seraj",text);
                Geocoder geocoder=new Geocoder(TaxiMainActivity.this,Locale.getDefault());
                try {
                    List<Address>addressList=geocoder.getFromLocationName(text,1);


                    if (addressList.size()>0){
                        Log.d("Seraj","address fetched");
                        Address address=addressList.get(0);


                    }
                } catch (IOException e) {
                    //   e.printStackTrace();
                    Log.d("Seraj","error while fetching address");
                }
            }
        });


        etDestination.addTextChangedListener(filterTextWatcher);

        etDestination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {

            }
        });

        etDestination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            }
            return false;
        });
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable && !s.toString().isEmpty() && s.length() > 2 && placesClient != null) {
                getAutocompletePredictions(s.toString());

            }

        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    private void getAutocompletePredictions(String query) {
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(query)
                .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((response) -> {
                    mAutoCompleteAdapter.setPredictions(response.getAutocompletePredictions());
                    mAutoCompleteAdapter.notifyDataSetChanged();
                    locationsRv.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("Ta", "Place not found: " + apiException.getStatusCode());
                        Toast.makeText(getApplicationContext(), apiException.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.pneck_retro_style));
        getDeviceLocation();

    }


    private void getCompleteAddressString(double latitude, double longitude) {

        Log.d("Seraj","LotLang"+latitude+"  ,  "+ longitude);

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Log.d("Seraj","Address found");

                /***  String address = addresses.get(0).getAddressLine(0);
                 city = addresses.get(0).getLocality();
                 state = addresses.get(0).getAdminArea();
                 country = addresses.get(0).getCountryName();
                 postalCode = addresses.get(0).getPostalCode();
                 knownName = addresses.get(0).getFeatureName();
                 locality = addresses.get(0).getSubLocality();**/

                String address=addresses.get(0).getAddressLine(0);

                UserLatitude = "" + latitude;
                UserLongitude = "" + longitude;


                currentFullAddress=address;
                // CompleteAddress.setText(address);
                Log.d("Seraj","setting :"+address);


                currentAddress.setText(address);


                Log.d("Seraj","Latitude AAAAAA is called...."+UserLatitude.toString());
                Log.d("Seraj","Longitude AAAAAA  is called...."+UserLongitude.toString());
                //  Log.d("Seraj","address AAAAAA is called...."+address.toString());



            }

        } catch (Exception e) {
            Log.e("kjfksfsfsf", "this is error exception " + e.getMessage());
        }

    }
    private void getDeviceLocation() {
        Log.d("Seraj","inside getDeviceLocation ");
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //ts   Toast.makeText(TaxiMainActivity.this, "Getting Current Location...", Toast.LENGTH_SHORT).show();
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation!=null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), 15.0f));



                                Log.d("Seraj","getting location...");

                                getNearByDriverList();
                                getCompleteAddressString(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                                LatLng mypos=new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                Log.d("serajmk",sessionManager.getUserImage());

                                mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).
                                        icon(BitmapDescriptorFactory.fromBitmap(
                                                CustomMarker(TaxiMainActivity.this,R.drawable.icon_user,sessionManager.getUserName())))).setTitle(sessionManager.getUserAddress());

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.d("Seraj", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101){
            Double longitude=data.getDoubleExtra("longitude",83);
            Double lattitude=data.getDoubleExtra("lattitude",83);
            Log.d("Seraj",lattitude+""+longitude);
            mMap.addMarker(new MarkerOptions().position(new LatLng(lattitude,longitude)));


            cashLayout(longitude,lattitude);
        }
    }

    private void cashLayout(Double lattitude, Double longitude) {
        Log.d("Seraj","Setting Layouts");
        //mainLayout.setVisibility(View.GONE);
        showpathapi(lattitude,longitude);
        cashOfferLinearLayout.setVisibility(View.VISIBLE);
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        getRideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(driverArray.length()==0)
                {
                    Toast.makeText(TaxiMainActivity.this,"No driver available right now...!",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    cash=cashOffered.getText().toString();
                    String discriptiontxt=discription.getText().toString();
                    int id = 0;
                    adapter.getCarTypeList();

                    for(int i=0; i<adapter.getCarTypeList().size();i++)
                    {
                        if(adapter.getCarTypeList().get(i).getSelected()==1)
                        {
                            id = adapter.getCarTypeList().get(i).getId();
                        }
                    }
                    if(id==0)
                    {
                        Toast.makeText(TaxiMainActivity.this,"Please Select Vehicle type",Toast.LENGTH_SHORT).show();
                    }
                    else if (!TextUtils.isEmpty(cash)){
                        if (Integer.parseInt(cash)>=40){

                            getDriverList(cash.toString(),id+"",discriptiontxt.toString(),UserLatitude,UserLongitude,destination_latti,destination_longi);
                            carType = id+"";
                        }
                        else {
                            Toast.makeText(TaxiMainActivity.this, "Amount is too low..!", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        cashOffered.setError("Please offer your fare...");
                    }
                }

            }
        });





        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

    }


    String carType;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void getCarType(String b_id) {
        Log.d("Serajcpa","api called "+bookingId);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/fetchVehicleTyle";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("booking_id",bookingId);
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.GET,
                ServerURL,
                dataParams,
                DriverFetchSuccess(),
                ErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(TaxiMainActivity.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> DriverFetchSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Serajcpa","accept responce recieved");
                    Log.d("Serajcpa", "this is complete response " + response);
                    // JSONObject innerResponse=response.getJSONObject("response");
                    if (response.getBoolean("success")) {
                        Log.d("Serajarray","responce success");
                        JSONArray jsonArray=response.getJSONArray("data");
                        Log.d("serajcararray","vehicletype list is......"+jsonArray.toString());
                        List<CarType> list = new ArrayList<>();
                        list.clear();

                        for (int i=0;i<jsonArray.length();i++){
                            JSONObject object=jsonArray.getJSONObject(i);
                            list.add(new CarType(object.getInt("id"),object.getString("vehicle_type"),object.getString("vehicle_image"),0));
                            Log.d("serajcararray",list.get(i).getCarName());
                        }
                        adapter = new ServiceAdapter(TaxiMainActivity.this,list);
                        serviceRv.setLayoutManager(new LinearLayoutManager(TaxiMainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        serviceRv.setItemAnimator(new DefaultItemAnimator());
                        serviceRv.setAdapter(adapter);

                    }else {
                        Log.d("Serajad","accept responce failed");
                    }
                } catch (Exception e) {
                    Log.d("Serajad", "error ad inside catch block  " + e.getMessage());
                    //   e.printStackTrace();

                }
            }
        };
    }


    private Response.ErrorListener ErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  error.printStackTrace();
                Log.v("Seraj", "error occured" + error.getMessage());
            }
        };
    }


    @Override
    protected void onResume() {
        super.onResume();
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }


    private Bitmap CustomMarker(Context context, @DrawableRes int resource, String _name) {

        Log.d("Serajmk","inside custom");

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = marker.findViewById(R.id.user_dp);

        SessionManager sessionManager1 = new SessionManager(context);

        String image_url="http://pneck.com/storage/user_img/5f94dc4223d59compress_img.jpg";

        if (sessionManager1.getUserImage()!=null){
            Glide.with(context).load(image_url).placeholder(R.drawable.userr).into(markerImage);
        }else{
            Glide.with(context).load(R.drawable.userr).into(markerImage);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }



    public void showNearByCabs()
    {
        shownearbyCabLocations.clear();
        for (int i=0; i<nearbyCabLocations.size(); i++) {

            Marker  nearbyCabMarker = addCarMarkerAndGet(nearbyCabLocations.get(i));
        }
    }

    private Marker addCarMarkerAndGet( LatLng latLng) {
        BitmapDescriptor bitmapDescriptor= BitmapDescriptorFactory.fromBitmap(getCarBitmap());
        return mMap.addMarker(new MarkerOptions().position(latLng).flat(true).title("Distance ").icon(bitmapDescriptor).snippet(String.valueOf(distance).substring(0,3)+" Km away"));
    }

    public Bitmap getCarBitmap()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_car);
        return Bitmap.createScaledBitmap(bitmap, 50, 100, false);

    }




    public void showPath( List<LatLng> latLngList) {
        String path = sessionManager.getPath();
        Gson gson = new Gson();
        Type type =new  TypeToken<List<LatLng>>() {}.getType();
        List<LatLng> finallist  =   gson.fromJson(path, type);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i=0;i<latLngList.size();i++) {
            builder.include(latLngList.get(i));
        }
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5f);
        polylineOptions.addAll(latLngList);
        greyPolyLine = mMap.addPolyline(polylineOptions);
        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(5f);
        blackPolylineOptions.color(Color.BLACK);
        blackPolyline = mMap.addPolyline(blackPolylineOptions);
        originMarker = addOriginDestinationMarkerAndGet( latLngList.get(0));
        originMarker.setAnchor(0.5f, 0.5f);
        destinationMarker = addOriginDestinationMarkerAndGet(latLngList.get(latLngList.size()-1));
        destinationMarker.setAnchor(0.5f, 0.5f);
        ValueAnimator polylineAnimator = polyLineAnimator();
        polylineAnimator.addUpdateListener(valueAnimator -> {
                    int percentValue = (int) valueAnimator.getAnimatedValue();
                    int index = (int) (greyPolyLine.getPoints().size() * (percentValue / 100.0f));
                    blackPolyline.setPoints(greyPolyLine.getPoints().subList(0, index));
                }
        );
        polylineAnimator.start();
    }


    private Marker addOriginDestinationMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getDestinationBitmap());
        return mMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }

    public Bitmap getDestinationBitmap() {
        int height = 20;
        int width = 20;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(0F, 0F, width, height, paint);
        return bitmap;
    }

    public ValueAnimator polyLineAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(2000);
        return valueAnimator;
    }

    private void getDriverList(String cash,String id,String description,String UserLatitude,String UserLongitude,String destination_latti,String destination_longi) {

        String userId = sessionManager.getUserid();
        String epToken = sessionManager.getUserToken();
        // progressBar.setVisibility(View.VISIBLE);
        //    String ServerURL = getResources().getString(R.string.pneck_app_url) + "/fetchDriversOnDemand";
        String ServerURL =  AllUrl.sendLocation;
        HashMap<String, String> dataParams = new HashMap<String, String>();

        //  dataParams.put("user_lat", "29.5985194");

        dataParams.put("user_id", userId);
        dataParams.put("ep_token", epToken);
        dataParams.put("user_lat", UserLatitude);
        dataParams.put("user_long", UserLongitude);
        dataParams.put("destination_latti", destination_latti);
        dataParams.put("destination_longi", destination_longi);
        dataParams.put("cash_offered", cash);
        dataParams.put("vehicle_type", id);
        dataParams.put("description", description);
        dataParams.put("user_drop_address", desinationAddress);


        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                DriverListSuccessListeners(),
                DriverListError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(TaxiMainActivity.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> DriverListSuccessListeners() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject innerResponse = response.getJSONObject("response");
                    //Log.d("pass",pass);
                    String msg = innerResponse.getString("message");
                    boolean resp_status = true;
                    if (innerResponse.getBoolean("success")) {

                        JSONObject jsonObject = innerResponse.getJSONObject("data");
                        String msge = jsonObject.getString("msg");
                        String ses_booking_id = jsonObject.getString("ses_booking_id");
                        your_booking_number = jsonObject.getString("your_booking_number");
                        your_booking_status = jsonObject.getString("your_booking_status");
                        your_booking_status_msg = jsonObject.getString("your_booking_status_msg");
                        bookingId=ses_booking_id;
                        Log.d("TahseenKhanBId",bookingId);
                        Toast.makeText(TaxiMainActivity.this, msg, Toast.LENGTH_LONG).show();
                        cashOfferLinearLayout.setVisibility(View.INVISIBLE);
                        Log.d("TahseenKhan"," calling getAvailableAtOfferDriverList()");

                        String message = "Congratulation...! You have new Booking!";
                        prepareNotificationMessage(ses_booking_id, your_booking_number, distance, UserLatitude, UserLongitude,desinationAddress, cash, discription.getText().toString(), sessionManager.getUserImage(), sessionManager.getUserName(), message);

                    }else {
                        Toast.makeText(TaxiMainActivity.this, msg, Toast.LENGTH_LONG).show();
                        Log.d("TahseenKhan"," error");
                    }

                } catch (Exception e) {
                    e.toString();
                    Log.e("TahseenKhan","this is error  "+e.getMessage()); }
            }
        };
    }

    private Response.ErrorListener DriverListError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error.printStackTrace();
                Log.d("TahseenKhan", "Error: " + error.getMessage());

                VolleyLog.d("Error", "Error: " + error.getMessage());
                Toast.makeText(TaxiMainActivity.this,"System error "+error.getMessage(),Toast.LENGTH_LONG).show();
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("TahseenKhan", "TimeoutError");

                } else if (error instanceof AuthFailureError) {
                    Log.d("TahseenKhan", "AuthFailureError");

                } else if (error instanceof ServerError) {
                    Log.d("TahseenKhan", "ServerError "+error.getMessage());

                } else if (error instanceof NetworkError) {
                    Log.d("TahseenKhan", "NetworkError");

                } else if (error instanceof ParseError) {
                    Log.d("TahseenKhan", "ParseError");

                }
            }
        };
    }


    private void prepareNotificationMessage(String ses_booking_id, String your_booking_number, double distance, String userLatitude, String userLongitude, String desinationAddress, String cash, String s, String userImage, String userName, String message){
        String NOTIFICATION_TOPIC = "/topics/" + Constants.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Booking...." ;
        String NOTIFICATION_MESSAGE = ""+ message;
        String NOTIFICATION_TYPE = "NewBooking";
        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();
        try {
            //what to send
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("carType",carType);
            notificationBodyJo.put("notificationTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("notificationMessage",NOTIFICATION_MESSAGE);

            notificationBodyJo.put("booking_order_number",your_booking_number);
            notificationBodyJo.put("booking_order_id",ses_booking_id);
            notificationBodyJo.put("distance_km",distance);
            notificationBodyJo.put("customer_lat",userLatitude);
            notificationBodyJo.put("customer_long",userLongitude);
            notificationBodyJo.put("user_drop_address",desinationAddress);
            notificationBodyJo.put("cash_offer",cash);
            notificationBodyJo.put("description",s);
            notificationBodyJo.put("profile_image",userImage);
            notificationBodyJo.put("name",userName);

            //where to send
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){

            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendFcmNotification(notificationJo);
    }

    private void sendFcmNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Intent intent=new Intent(TaxiMainActivity.this, DriverListActivity.class);
                        intent.putExtra("cash", cash+"");
                        intent.putExtra("bookin_id",bookingId);
                        intent.putExtra("desLat",destination_latti+"");
                        intent.putExtra("desLong",destination_longi+"");
                        startActivity(intent);
                        Log.e("FCM_RESPONCe", response.toString());
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Intent intent=new Intent(TaxiMainActivity.this, DriverListActivity.class);
                        intent.putExtra("cash", cash+"");
                        intent.putExtra("bookin_id",bookingId);
                        intent.putExtra("desLat",destination_latti+"");
                        intent.putExtra("desLong",destination_longi+"");
                        startActivity(intent);
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","key="+Constants.FCM_KEY);
                return headers;
            }
        };

        //enqueue the Volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }



    private void getNearByDriverList() {
        // progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/getNearByDrivers";
        HashMap<String, String> dataParams = new HashMap<String, String>();
        dataParams.put("latitude", mLastKnownLocation.getLatitude()+"");
        dataParams.put("longitude", mLastKnownLocation.getLongitude()+"");
        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                NearBySuccessListeners(),
                NearByDriverError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(TaxiMainActivity.this).add(dataParamsJsonReq);
    }
    double distance;
    JSONArray driverArray;
    private Response.Listener<JSONObject> NearBySuccessListeners() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject innerResponse = response.getJSONObject("response");
                    //Log.d("pass",pass);
                    String msg = innerResponse.getString("message");
                    boolean resp_status = true;
                    if (innerResponse.getBoolean("success")) {
                        JSONArray array = innerResponse.getJSONArray("data");
                        driverArray = array;
                        if(array.length()==0)
                        {
                            Log.e("DriverList","No Driver found");
                            return;
                        }
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            double latitude = Double.parseDouble(object.getString("curr_latitude"));
                            double longitude =   Double.parseDouble(object.getString("curr_longitude"));
                            LatLng latLng = new LatLng(latitude, longitude);
                            distance = object.getDouble("distance");
                            nearbyCabLocations.add(latLng);

                        }
                        showNearByCabs();
                    }

                } catch (Exception e) {
                }
            }
        };
    }

    private Response.ErrorListener NearByDriverError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        };
    }


    public void showpathapi(Double destilattitude, Double destlongitude)
    {
        com.callpneck.taxi.map.GoogleMap.requestCab(
                this,mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),destilattitude,destlongitude
        );

    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onMessage(@NotNull String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray=jsonObject.getJSONArray("path");
            if(pickupPath.size()>0)
            {
                pickupPath.clear();
                mMap.clear();
                getDeviceLocation();
            }

            for (int i=0;i<jsonArray.length();i++){
                JSONObject object=jsonArray.getJSONObject(i);
                LatLng ltlng= new LatLng(object.getDouble("lat"), object.getDouble("lng"));
                pickupPath.add(ltlng);
            }
            Gson gson = new Gson();
            String list = gson.toJson(pickupPath);
            sessionManager.setPath(list);
            showPath(pickupPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDisconnect() {

    }

    @Override
    public void onError(@NotNull String error) {
        Toast.makeText(TaxiMainActivity.this,"No Route Available",Toast.LENGTH_LONG).show();
    }

}