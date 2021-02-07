package com.callpneck.taxi.activity;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.MainActivity;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.model.ClusterMarker;
import com.callpneck.model.PolylineData;
import com.callpneck.model.User;
import com.callpneck.model.UserLocation;
import com.callpneck.taxi.FetchDriverData;
import com.callpneck.taxi.GetBookingData;
import com.callpneck.taxi.TaxiMainActivity;
import com.callpneck.utils.MyClusterManagerRenderer;
import com.callpneck.utils.PublicMethod;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Duration;
import com.google.maps.model.EncodedPolyline;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class RealTimeActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG ="SerajTrack" ;
    GoogleMap mGoogleMap;
    SessionManager sessionManager;
    private LatLngBounds mGoogleMapBoundary;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private int mGoogleMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private UserLocation mUserPosition;
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    String otp,vehicleNumber,avatar,mobile,name,vehicleImage;
    Double empLattitude,empLongitude;
    TextView userOtp,mVehicleNumber,mDriverName,mcarName;
    CircleImageView driverAvatar,carAvatar;
    LinearLayout callBtn;
    public static String timeReuired;

    String userLat;

    private float DEFAULT_ZOOM = 15.0f;
    private ScheduledExecutorService executor,trackingExecuter;
    private Runnable periodicTask,updateRunner;
    private boolean loaded=true;
    boolean okload=true;
    private boolean bookingDataLoaded=true;
    GetBookingData getBookingData;
    private boolean delivered=true;
    private boolean againDdata=true;

    FetchDriverData fetchDriverData;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private float bearing=0.0f;


    //mk
    private boolean mLocationPermissionGranted = true;
    private Location mLastKnownLocation = null;
    private LatLng mDefaultLocation;
    List<LatLng> nearbyCabLocations = new ArrayList<>();
    List<LatLng> pickupPath = new ArrayList<>();
    List<LatLng> shownearbyCabLocations = new ArrayList<>();
    Polyline greyPolyLine;
    Polyline blackPolyline;
    Marker originMarker;
    Marker destinationMarker;
    String dname="";
    String dphoneno="";

    private LinearLayout cancelOrder;


    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;
    String currentBookingStatus;
    private RatingBar driverRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_real_time);

        userOtp=findViewById(R.id.user_otp);
        mVehicleNumber=findViewById(R.id.vehicle_number);
        mDriverName=findViewById(R.id.driver_name);
        driverAvatar=findViewById(R.id.driver_image);
        carAvatar=findViewById(R.id.car_image);
        mcarName=findViewById(R.id.car_name);
        callBtn=findViewById(R.id.call_driver_btn);
        driverRating = findViewById(R.id.rating_bar);
        cancelOrder = (LinearLayout)findViewById( R.id.cancel_order );
        Intent intent=getIntent();
        dname= intent.getStringExtra("name");
        dphoneno=intent.getStringExtra("phoneno");
        if (dname!=null)
        mDriverName.setText(dname.toString());
       // if (dphoneno!=null)
       // callBtn.setText("Call "+dphoneno.toString());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(RealTimeActivity.this);
        getBookingData= new GetBookingData(this,sessionManager);
        fetchDriverData=new FetchDriverData(this,sessionManager);
        currentBookingStatus = sessionManager.getOrderStatus();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        executor = Executors.newSingleThreadScheduledExecutor();

        trackingExecuter = executor;

        Runnable periodicTask = new Runnable() {
            public void run() {
                // Invoke method(s) to do the work
                fetchDriverData.start();
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, 8, TimeUnit.SECONDS);


        Runnable trackingTask = new Runnable() {
            @Override
            public void run() {
                currentTrackingBooking();
            }
        };

        trackingExecuter.scheduleAtFixedRate(trackingTask, 0, 4, TimeUnit.SECONDS);


        addLocation();



        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cancelOrderReasonDialog(RealTimeActivity.this);
            }
        });


    }

    private void startUserLocationsRunnable(){
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                currentTrackingBooking();
                //retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }


    private boolean isBalanceDialogOpen = false;
    private void currentTrackingBooking() {

        String ServerURL = "http://pneck.com/api/userCurrBookingTracking";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());
        dataParams.put("ses_booking_id",sessionManager.getSesBookingId());
        dataParams.put("curr_lat",sessionManager.getUserLatitude());
        dataParams.put("curr_long",sessionManager.getUserLongitude());
        dataParams.put("curr_address","Empty");


        Log.e("user_employee_loc", "this is url " +ServerURL);

        Log.e("user_employee_loc", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                getLocationUpdate(),
                ErrorListeners());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(this).add(dataParamsJsonReq);
    }

    private boolean isImageSet=false;
    private boolean isDriverImageSet = false;
    private boolean isCarNameSet = false;
    private boolean isRatingSet = false;
    private Response.Listener<JSONObject> getLocationUpdate() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("user_employee_loc", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {

                        JSONObject object=innerResponse.getJSONObject("data");

                        String userImage=object.getString("employee_image");

                        String vehicleImage = object.getString("vehicle_image");
                        String vehicleName = object.getString("vehicle_name");

                        if (vehicleName!=null&&!isCarNameSet){
                            isCarNameSet = true;
                            mcarName.setText(vehicleName);
                        }

                        if (userImage!=null&&userImage.length()>3&&!isImageSet){
                            Log.e("kjhsdfkdsf","setting image this is employee image "+userImage);
                            isImageSet=true;
                            Glide.with(RealTimeActivity.this)
                                    .load(userImage)
                                    .into(driverAvatar);
                        }else {
                            Log.e("kjhsdfkdsf","setting not is employee image "+userImage);
                        }

                        if (vehicleImage!=null&&vehicleImage.length()>3&&!isDriverImageSet){
                            Log.e("kjhsdfkdsf","setting image this is vehicleImage image "+vehicleImage);
                            isDriverImageSet=true;
                            Glide.with(RealTimeActivity.this)
                                    .load(vehicleImage)
                                    .into(carAvatar);
                        }else {
                            Log.e("kjhsdfkdsf","setting not is vehicleImage image "+vehicleImage);
                        }

//                        String jobDetail=object.getString("job_detail");
//
//                        if (jobDetail.length()<=0||jobDetail.equals("null")){
////                            orderInfoLayout.setVisibility(View.GONE);
//                        }else {
////                            orderInfoLayout.setVisibility(View.VISIBLE);
////                            orderInfo.setText(jobDetail);
//                        }

                        JSONObject empObj=object.getJSONObject("employee_loc");

                        String ratingDriver = empObj.getString("emp_rating");

                        if (ratingDriver!=null&&!isRatingSet){
                            isRatingSet = true;
                            driverRating.setRating(Float.parseFloat(ratingDriver));
                        }
                        if (object.getString("curr_booking_status").
                                equalsIgnoreCase("order_request_payment") &&!isBalanceDialogOpen){

                            isBalanceDialogOpen = true;
                            //launch payment request screen
                            String bookingCharge=object.getString("payable_amount");
                            /* "booking_charge": "50",
                            "order_subtotal": "445.00",
                                    "payable_amount": "495.00",*/
                            Bundle bundle =new Bundle();
                            bundle.putString("billing_amount",bookingCharge);
                            LaunchActivityClass.LaunchPaymentScreen(RealTimeActivity.this,bundle);
                            finish();
                        }
                        //employeeRating.setText(empObj.getString("emp_rating"));

                        if (!empObj.getString("delivery_otp").equals("null")&&empObj.getString("delivery_otp").length()>0){
                            userOtp.setText("OTP : "+empObj.getString("delivery_otp"));
                        }else {
                            if (!empObj.getString("accept_otp").equals("null")&&empObj.getString("accept_otp").length()>0){
                                userOtp.setText("OTP : "+empObj.getString("accept_otp"));
                            }else {
                                userOtp.setText("OTP");
                            }
                        }


                        //bookingCharge=object.getString("booking_charge");
//                        if (bookingCharge==null||bookingCharge.length()<=1){
//                            bookingCharges.setVisibility(View.GONE);
//                        }else {
//                            bookingCharges.setText("Charges : "+bookingCharge);
//                        }

                        //userPhoneNo.setText(employeePhoneNum);
                        currentBookingStatus=object.getString("curr_booking_status");

                        if (currentBookingStatus.equals("accepted")){
                            currentBookingStatus="Order Accepted";
                        }else if (currentBookingStatus.equals("accepted_otp_confirmed")){
                            currentBookingStatus="OTP Confirmed";
                        }else if (currentBookingStatus.equals("order_info_provided")){
                            currentBookingStatus="Order Information Added";
                        }else if (currentBookingStatus.equals("delivery_otp_confirmed")){
                            currentBookingStatus="Delivery OTP Confirmed";
                        }else if (currentBookingStatus.equals("order_request_payment")){
                            currentBookingStatus="Order Payment Request";
                        }
                        //bookingStatus.setText(currentBookingStatus);



                       // VehicleNo.setText("VEHICLE NO. : "+empObj.getString("vehicle_number"));
                        for (int i = 0; i < mClusterMarkers.size(); i++) {
                            try {
                                if (!mClusterMarkers.get(i).getUser().getUser_id().equals(sessionManager.getUserid())) {

                                    LatLng updatedLatLng = new LatLng(Double.parseDouble(empObj.getString("curr_lat")),
                                            Double.parseDouble(empObj.getString("curr_long")));

                                    mClusterMarkers.get(i).setPosition(updatedLatLng);
                                    mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                }
                            } catch (NullPointerException e) {
                                Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.v("user_employee_loc", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener ErrorListeners() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("user_employee_loc", "inside error block  " + error.getMessage());
            }
        };
    }

    public  void cancelOrderReasonDialog(final Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.cancel_order_dialog, null);
        //TextInputEditText cancelReason;
        TextView stayOnOrder;
        TextView cancelOrder;

        RadioGroup radioGroup=mView.findViewById(R.id.cancel_reason_radio);
        //cancelReason = (TextInputEditText)mView.findViewById( R.id.cancel_reason );
        stayOnOrder = (TextView)mView.findViewById( R.id.stay_on_order );
        cancelOrder = (TextView)mView.findViewById( R.id.cancel_order );

        builder.setView(mView);
        final AlertDialog NewCategory_dialog = builder.create();



        stayOnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NewCategory_dialog.dismiss();
            }
        });
        final String[] selected = {""};
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) mView.findViewById(selectedId);
                selected[0] =radioButton.getText().toString();
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected[0].length()>0){
                    cancelCurrentOrder(selected[0].toString());
                    NewCategory_dialog.dismiss();
                }else {
                    Toast.makeText(RealTimeActivity.this,getString(R.string.PLEASE_ENTER_CANCEL_RESON),Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


        NewCategory_dialog.show();
    }
    private void cancelCurrentOrder(String cancelReason) {
        userBookingCancel(sessionManager.getUserid(), sessionManager.getUserToken(),
                sessionManager.getSesBookingId(),cancelReason);
    }
    private void userBookingCancel(String userId, String epToken, String ses_booking, String cancelReason) {
        Map<String, String> params = new HashMap<>();
        ses_booking = sessionManager.getSesBookingId();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", ses_booking);
        params.put("cancel_reason",cancelReason);
        Log.e("dhfsdfsdfsf","this is data sending "+params);

//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingCancel, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_cancle_2", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_cancle_2", jsonObject1.toString());

                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    if (jsonObject1.getBoolean("success")) {
                        Log.e("booking_cancle_2","inside if success ");
                        sessionManager.clearOrderSession();
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_status = jsonObject2.getString("your_booking_status");

                        //statusMsg.setText(booking_status_msg);
                        Log.e("booking_cancle_2","before tost inside if success ");
                        Toast.makeText(RealTimeActivity.this,booking_status_msg,Toast.LENGTH_SHORT).show();
                        Log.e("booking_cancle_2","calling main if success ");
                        LaunchActivityClass.LaunchMainActivity(RealTimeActivity.this);
                    } else {
                        Log.e("booking_cancle_2","inside else success ");
                        Toast.makeText(RealTimeActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("booking_cancle_2","this is error "+e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("booking_cancle_2", "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.d("error ocurred", "TimeoutError");

                } else if (error instanceof AuthFailureError) {
                    Log.d("error ocurred", "AuthFailureError");

                } else if (error instanceof ServerError) {
                    Log.d("error ocurred", "ServerError");

                } else if (error instanceof NetworkError) {
                    Log.d("error ocurred", "NetworkError");

                } else if (error instanceof ParseError) {
                    Log.d("error ocurred", "ParseError");

                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void checkStatus() {
        if (getBookingData.fetchBooking()){
        }
        if (!sessionManager.getOtpVerified()){
            setUserOtp();
            //draw route between user and driver
            LatLng source=new LatLng(Double.valueOf(sessionManager.getUserLatitude()),Double.valueOf(sessionManager.getUserLongitude()));
            if (sessionManager.getEmpLattitude()!=null){
                LatLng destination=new LatLng(Double.valueOf(sessionManager.getEmpLattitude()),Double.valueOf(sessionManager.getEmplongitude()));
                Log.d("serajdraw","not otp"+source+" "+ destination);
                drawRoutes(source,destination);
            }

        }
        else{
            //draw route between driver and destination

            if (sessionManager.getDesLattitude()!=null){
                LatLng source=new LatLng(Double.valueOf(sessionManager.getEmpLattitude()),Double.valueOf(sessionManager.getEmplongitude()));
                LatLng destination=new LatLng(Double.valueOf(sessionManager.getDesLattitude()),Double.valueOf(sessionManager.getDesLongitude()));
                Log.d("serajdraw","otp");
                drawRoutes(source,destination);
                mcarName.setText("On Ride");
                mcarName.setTextColor(Color.BLACK);
                userOtp.setText("You are on ride.");
                userOtp.setTextColor(Color.GREEN);
                mDriverName.setText(sessionManager.getDname());
                mVehicleNumber.setText(sessionManager.getvehiclenumber());
            }

        }

        if (sessionManager.getOrderStatus().equalsIgnoreCase("delivered")){
            delivered=false;
            executor.shutdown();
            fetchDriverData.stop();
            Bundle bundle =new Bundle();
            bundle.putString("billing_amount",sessionManager.getPayAmount());
            LaunchActivityClass.LaunchPaymentScreen(RealTimeActivity.this,bundle);
            finish();
        }

        if (!sessionManager.getDeliveryOtp().isEmpty()){
            userOtp.setText("Drop OTP : "+sessionManager.getDeliveryOtp());
            userOtp.setTextColor(Color.GREEN);
        }


    }

    private void setUserOtp() {

        userOtp.setText("OTP: "+ sessionManager.getPickUpOtp());
        userOtp.setTextColor(Color.GREEN);
        mDriverName.setText(sessionManager.getDname());
        mVehicleNumber.setText(sessionManager.getvehiclenumber());
        //callBtn.setText("Call "+sessionManager.getDrivermobile());
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent(sessionManager.getDrivermobile());
            }
        });
    }


    private void callIntent(String s) {
        String phone = "+91"+s;
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }





    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMyLocationEnabled(true);

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //ts   mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.pneck_retro_style));
        getDeviceLocation();
        mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                bearing=location.getBearing();
            }
        });


        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    //do your code here
                    if (delivered){
                        Log.d("serajrunner","running");
                        checkStatus();
                    }
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 1000);
                }
            }
        };

//runnable must be execute once
        handler.post(runnable);
    }

    private void drawRoutes(LatLng source, LatLng destination) {

        Log.d("serajrtd","inside draw");

        LatLng zaragoza = new LatLng(41.648823,-0.889085);

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList();


        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyBTbcRRCLbhqeMVVGD5fnUevHfxj2MdgdI")
                .build();
        Log.d("serajrtdl",source+"  "+destination);
        DirectionsApiRequest req = DirectionsApi.getDirections(context, source.latitude+","+source.longitude, destination.latitude+","+destination.longitude);
        try {
            DirectionsResult res = req.await();
            Log.d("serajrtd","inside req");

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];




                Log.d("serajrtdr","route found "+route);
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        Duration duration = leg.duration;
                        timeReuired=""+duration.humanReadable;

                        Log.d("serajduration",""+duration.humanReadable);
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++){
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }else{
                Log.d("serajrtd","route not found");
            }
        } catch(Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
            Log.d("serajrtd","inside error "+ex.getLocalizedMessage());

        }

        //Draw the polyline
        if (path.size() > 0) {
            mGoogleMap.clear();
            bookingDataLoaded=true;



            if (sessionManager.getOtpVerified()){

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(source);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_photo));
                markerOptions.rotation(bearing);
                markerOptions.anchor((float) 0.5, (float) 0.5);
                mGoogleMap.addMarker(markerOptions);



                mGoogleMap.addMarker(new MarkerOptions().position(destination).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(RealTimeActivity.this,R.drawable.ic_placeholder,"destination")))).setTitle("I am Driver");

            }else{

                mGoogleMap.addMarker(new MarkerOptions().position(source).
                        icon(BitmapDescriptorFactory.fromBitmap(
                                createCustomMarker(RealTimeActivity.this,R.drawable.userr,"Seraj")))).setTitle("I am User");

                Log.d("serajrtd","marker driver");
                // mGoogleMap.addMarker(new MarkerOptions().position(destination).
                //       icon(BitmapDescriptorFactory.fromBitmap(
                //             createCustomMarker(RealTimeActivity.this,R.drawable.ic_scooter,"driver")))).setTitle("I am Driver");
                mGoogleMap.addMarker(new MarkerOptions().position(destination).icon(PublicMethod.convertToBitmapFromVector(RealTimeActivity.this,
                        R.drawable.ic_car)).title("Driver").snippet("curr_loc_address"));
            }


            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source, 16));
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
            mGoogleMap.addPolyline(opts);
        }


    }


    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        CircleImageView markerImage = (CircleImageView) marker.findViewById(R.id.user_dp);
        RelativeLayout mainUserdp=marker.findViewById(R.id.uirl);
        RelativeLayout destrl=marker.findViewById(R.id.ddrl);
        TextView destname=marker.findViewById(R.id.destname);
        TextView desttime=marker.findViewById(R.id.dest_time);



        SessionManager sessionManager1 = new SessionManager(context);

        if (_name.equalsIgnoreCase("destination")){
            mainUserdp.setVisibility(View.GONE);
            destrl.setVisibility(View.VISIBLE);
            if (!timeReuired.isEmpty()){
                String[] separated = timeReuired.split(" ");
                String ftime=separated[0];
                desttime.setText(ftime);
                String addressesf=sessionManager1.getDestinationLocality();

                if (!addressesf.isEmpty()){
                    destname.setText(addressesf);
                }


            }
        }
        if (sessionManager1.getUserImage()!=null){
            markerImage.setImageResource(resource);
        }else{
            Glide.with(context).load(sessionManager1.getUserImage()).placeholder(R.drawable.pneck_logo).into(markerImage);
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }


    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            //    Toast.makeText(RealTimeActivity.this, "Getting Current Location...", Toast.LENGTH_SHORT).show();
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation!=null){
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));



                                Log.d("Seraj","getting location...");

                                LatLng mypos=new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                Log.d("serajmk",sessionManager.getUserImage());

                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).
                                        icon(BitmapDescriptorFactory.fromBitmap(
                                                CustomMarker(RealTimeActivity.this,R.drawable.icon_user,sessionManager.getUserName())))).setTitle(sessionManager.getUserAddress());

                                showPath(pickupPath);

                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.d("Seraj", e.getMessage());
        }
    }
    public void addLocation()
    {
        String path = sessionManager.getPath();
        Gson gson = new Gson();
        Type type =new  TypeToken<List<LatLng>>() {}.getType();
        List<LatLng> finallist  =   gson.fromJson(path, type);
        pickupPath = finallist;
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


    public void showPath( List<LatLng> latLngList) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i=0;i<latLngList.size();i++) {
            builder.include(latLngList.get(i));
        }
        LatLngBounds bounds = builder.build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 2));
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.GRAY);
        polylineOptions.width(5f);
        polylineOptions.addAll(latLngList);
        greyPolyLine = mGoogleMap.addPolyline(polylineOptions);
        PolylineOptions blackPolylineOptions = new PolylineOptions();
        blackPolylineOptions.width(5f);
        blackPolylineOptions.color(Color.BLACK);
        blackPolyline = mGoogleMap.addPolyline(blackPolylineOptions);
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

    private Marker addOriginDestinationMarkerAndGet(LatLng latLng) {
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(getDestinationBitmap());
        return mGoogleMap.addMarker(new MarkerOptions().position(latLng).flat(true).icon(bitmapDescriptor));
    }
    public ValueAnimator polyLineAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(2000);
        return valueAnimator;
    }

}