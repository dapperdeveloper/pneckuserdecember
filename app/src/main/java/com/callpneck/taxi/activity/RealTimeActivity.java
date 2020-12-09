package com.callpneck.taxi.activity;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.Language.ThemeUtils;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    ImageView driverAvatar,carAvatar;
    Button callBtn;
    public static String timeReuired;

    String userLat;

    private int DEFAULT_ZOOM = 1;
    private ScheduledExecutorService executor,locationExecuter;
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



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(RealTimeActivity.this);

        getBookingData= new GetBookingData(this,sessionManager);

        fetchDriverData=new FetchDriverData(this,sessionManager);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        executor =
                Executors.newSingleThreadScheduledExecutor();

        Runnable periodicTask = new Runnable() {
            public void run() {
                Log.d("serajcallingdriver","calling");
                // Invoke method(s) to do the work
                fetchDriverData.start();
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, 4, TimeUnit.SECONDS);









    }

    private void checkStatus() {
        if (getBookingData.fetchBooking()){
            Log.d("serajgetbookingcalled","yes");
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
                mcarName.setTextColor(Color.GREEN);
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

        Log.d("serajsetuser","otp");

        userOtp.setText("OTP: "+ sessionManager.getPickUpOtp());
        userOtp.setTextColor(Color.GREEN);
        mDriverName.setText(sessionManager.getDname());
        mVehicleNumber.setText(sessionManager.getvehiclenumber());
        callBtn.setText("Call "+sessionManager.getDrivermobile());
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
                        R.drawable.ic_scooter)).title("Driver").snippet("curr_loc_address"));
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
                Log.d("serajduration",ftime+addressesf);

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


}