package com.callpneck.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.model.ClusterMarker;
import com.callpneck.model.PolylineData;
import com.callpneck.model.User;
import com.callpneck.model.UserLocation;
import com.callpneck.SessionManager;
import com.callpneck.Const;
import com.callpneck.utils.MyClusterManagerRenderer;
import com.callpneck.utils.PublicMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.callpneck.Const.MAPVIEW_BUNDLE_KEY;


public class TrackingUserFragment extends Fragment implements
        OnMapReadyCallback,
        View.OnClickListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener
{

    private static final String TAG = "TrackingUserFragment";
    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;


    //widgets
    //private RecyclerView mUserListRecyclerView;
    private MapView mMapView;
    private RelativeLayout mMapContainer;


    //vars
    private ArrayList<User> mUserList = new ArrayList<>();
    private ArrayList<UserLocation> mUserLocations = new ArrayList<>();
    private GoogleMap mGoogleMap;
    private UserLocation mUserPosition;
    private LatLngBounds mMapBoundary;
    private ClusterManager<ClusterMarker> mClusterManager;
    private MyClusterManagerRenderer mClusterManagerRenderer;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private int mMapLayoutState = 0;
    private GeoApiContext mGeoApiContext;
    private ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<>();
    private SessionManager sessionManager;
    private int DEFAULT_ZOOM = 1;
    private ProgressBar progressBar;

    private String customerAddress="";

    private JSONObject initialResponse;
    private String EmployeeName;
    private String bookingOrderNumber,bookingCharge,currentBookingStatus;
    private String employeePhoneNum;

    private CircleImageView mUserImage;
    private boolean isImageSet=false;
    private TextView employeeName;
    private TextView orderNumber;
    private TextView bookingCharges;
    private TextView userPhoneNo;
    private TextView bookingStatus;
    private LinearLayout phoneLayout;
    private TextView userOtp;
    private TextView employeeRating;
    private TextView VehicleNo;
    private LinearLayout orderInfoLayout;
    private TextView orderInfo;
    private LinearLayout cancelOrder;

    public TrackingUserFragment(){

    }
    @SuppressLint("ValidFragment")
    public TrackingUserFragment(JSONObject innerResponse) {
        initialResponse=innerResponse;
    }

    public static TrackingUserFragment newInstance() {
        return new TrackingUserFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager=new SessionManager(getActivity());
        if (mUserLocations.size()==0){

            try{

                Log.e("dskfskdfsdds","this is response "+initialResponse);

                JSONObject object=initialResponse.getJSONObject("data");
                JSONObject empObj=object.getJSONObject("employee_loc");
                EmployeeName=object.getString("employee_name");
                bookingOrderNumber=object.getString("booking_order_number");
                bookingCharge=object.getString("booking_charge");
                currentBookingStatus=object.getString("curr_booking_status");
                employeePhoneNum=object.getString("employee_mobile");


                User user=new User("employee","me_employee_4",EmployeeName,"");


                Log.e("kjdhsfksfsf","this is latitude "+empObj.getString("curr_lat"));
                GeoPoint geoPoint=new GeoPoint(Double.parseDouble(empObj.getString("curr_lat")),
                        Double.parseDouble(empObj.getString("curr_long")));

                mUserLocations.add(new UserLocation(user,geoPoint,""+System.currentTimeMillis()));



                user=new User("user",sessionManager.getUserid(),
                        sessionManager.getSesBookingId(),"");

                geoPoint=new GeoPoint(Double.parseDouble(sessionManager.getUserLatitude()),
                        Double.parseDouble(sessionManager.getUserLongitude()));

                mUserLocations.add(new UserLocation(user,geoPoint,""+System.currentTimeMillis()));


                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                addresses = geocoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);

                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    customerAddress=address;
                }
            }catch (Exception e){
                Log.e("kjdhfkdssfs","this is error "+e.getMessage());
            }

        }
        /*if (mUserLocations.size() == 0) { // make sure the list doesn't duplicate by navigating back
            if (getArguments() != null) {
                final ArrayList<User> users = getArguments().getParcelableArrayList(getString(R.string.intent_user_list));
                mUserList.addAll(users);

                final ArrayList<UserLocation> locations = getArguments().getParcelableArrayList(getString(R.string.intent_user_locations));
                mUserLocations.addAll(locations);
            }
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        //mUserListRecyclerView = view.findViewById(R.id.user_list_recycler_view);

        mMapView = view.findViewById(R.id.user_list_map);
        view.findViewById(R.id.btn_full_screen_map).setOnClickListener(this);
        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        mMapContainer = view.findViewById(R.id.map_container);

        orderInfoLayout=view.findViewById(R.id.order_info_layout);
        orderInfo=view.findViewById(R.id.order_info);

        progressBar=view.findViewById(R.id.progressBar);

        employeeName = (TextView)view.findViewById( R.id.employee_name );
        orderNumber = (TextView)view.findViewById( R.id.order_number );
        bookingCharges = (TextView)view.findViewById( R.id.booking_charges );
        userPhoneNo = (TextView)view.findViewById( R.id.user_phone_no );
        bookingStatus = (TextView)view.findViewById( R.id.booking_status );
        phoneLayout=view.findViewById(R.id.phone_layout);
        userOtp = (TextView)view.findViewById( R.id.user_otp );
        employeeRating = (TextView)view.findViewById( R.id.employee_rating );
        VehicleNo=view.findViewById(R.id.vehicle_no);
        cancelOrder = (LinearLayout)view.findViewById( R.id.cancel_order );
        mUserImage=view.findViewById(R.id.user_image);

        sessionManager=new SessionManager(getActivity());

        employeeName.setText(EmployeeName);
        orderNumber.setText("ORDER : "+bookingOrderNumber);
        if (bookingCharge==null||bookingCharge.length()<=1){
            bookingCharges.setVisibility(View.GONE);
        }else {
            bookingCharges.setText("Charges : "+bookingCharge);
        }

        //userPhoneNo.setText(employeePhoneNum);

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
        bookingStatus.setText(currentBookingStatus);


        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelOrderReasonDialog(getActivity());
            }
        });

        initUserListRecyclerView();
        initGoogleMap(savedInstanceState);

        setUserPosition();

        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", employeePhoneNum, null));
                startActivity(intent);
            }
        });


        return view;
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
                    Toast.makeText(getContext(),getString(R.string.PLEASE_ENTER_CANCEL_RESON),Toast.LENGTH_SHORT).show();
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
        progressBar.setVisibility(View.VISIBLE);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
                    progressBar.setVisibility(View.GONE);
                    if (jsonObject1.getBoolean("success")) {
                        Log.e("booking_cancle_2","inside if success ");
                        sessionManager.clearOrderSession();
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_status = jsonObject2.getString("your_booking_status");

                        bookingStatus.setText(your_booking_status);
                        //statusMsg.setText(booking_status_msg);
                        Log.e("booking_cancle_2","before tost inside if success ");
                        Toast.makeText(getActivity(),booking_status_msg,Toast.LENGTH_SHORT).show();
                        Log.e("booking_cancle_2","calling main if success ");
                        LaunchActivityClass.LaunchMainActivity(getActivity());
                    } else {
                        Log.e("booking_cancle_2","inside else success ");
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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



    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;

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

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }
/*

    private void retrieveUserLocations(){
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users in the chatroom.");

        try{
            for(final ClusterMarker clusterMarker: mClusterMarkers){

                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection(getString(R.string.collection_user_locations))
                        .document(clusterMarker.getUser().getUser_id());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){

                            final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);

                            User user=new User("employee","me_employee_4",sessionManager.getUserFirstName(),"");

                            GeoPoint geoPoint=new GeoPoint(Double.parseDouble(sessionManager.getEmployeeCurrentLatitude()),
                                    Double.parseDouble(sessionManager.getEmployeeCurrentLongitude()));

                            mUserLocations.add(new UserLocation(user,geoPoint,""+System.currentTimeMillis()));


                            // update the location
                            for (int i = 0; i < mClusterMarkers.size(); i++) {
                                try {
                                    if (mClusterMarkers.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {

                                        LatLng updatedLatLng = new LatLng(
                                                updatedUserLocation.getGeo_point().getLatitude(),
                                                updatedUserLocation.getGeo_point().getLongitude()
                                        );

                                        mClusterMarkers.get(i).setPosition(updatedLatLng);
                                        mClusterManagerRenderer.setUpdateMarker(mClusterMarkers.get(i));
                                    }


                                } catch (NullPointerException e) {
                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                                }
                            }
                        }
                    }
                });
            }
        }catch (IllegalStateException e){
            Log.e(TAG, "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
        }
    }

*/
    private void currentTrackingBooking() {

    String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userCurrBookingTracking";
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
    Volley.newRequestQueue(getActivity()).add(dataParamsJsonReq);
}


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


                        if (userImage!=null&&userImage.length()>3&&!isImageSet){
                            Log.e("kjhsdfkdsf","setting image this is employee image "+userImage);
                            isImageSet=true;
                            Glide.with(getActivity())
                                    .load(userImage)
                                    .into(mUserImage);
                        }else {
                            Log.e("kjhsdfkdsf","setting not is employee image "+userImage);
                        }

                        String jobDetail=object.getString("job_detail");

                        if (jobDetail.length()<=0||jobDetail.equals("null")){
                            orderInfoLayout.setVisibility(View.GONE);
                        }else {
                            orderInfoLayout.setVisibility(View.VISIBLE);
                            orderInfo.setText(jobDetail);
                        }

                        JSONObject empObj=object.getJSONObject("employee_loc");
                        if (object.getString("curr_booking_status").
                                equalsIgnoreCase("order_request_payment")){
                            //launch payment request screen
                            String bookingCharge=object.getString("payable_amount");
                            /* "booking_charge": "50",
                            "order_subtotal": "445.00",
                                    "payable_amount": "495.00",*/
                            Bundle bundle =new Bundle();
                            bundle.putString("billing_amount",bookingCharge);
                            LaunchActivityClass.LaunchPaymentScreen(getActivity(),bundle);
                            getActivity().finish();
                        }
                        employeeRating.setText(empObj.getString("emp_rating"));

                        if (!empObj.getString("delivery_otp").equals("null")&&empObj.getString("delivery_otp").length()>0){
                            userOtp.setText("OTP : "+empObj.getString("delivery_otp"));
                        }else {
                            if (!empObj.getString("accept_otp").equals("null")&&empObj.getString("accept_otp").length()>0){
                                userOtp.setText("OTP : "+empObj.getString("accept_otp"));
                            }else {
                                userOtp.setText("OTP");
                            }
                        }


                        bookingCharge=object.getString("booking_charge");
                        if (bookingCharge==null||bookingCharge.length()<=1){
                            bookingCharges.setVisibility(View.GONE);
                        }else {
                            bookingCharges.setText("Charges : "+bookingCharge);
                        }

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
                        bookingStatus.setText(currentBookingStatus);


                        VehicleNo.setText("VEHICLE NO. : "+empObj.getString("vehicle_number"));
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


    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolyLinesData.size() > 0){
                mPolyLinesData.clear();
                mPolyLinesData = new ArrayList<>();
            }
        }
    }

    private void addMapMarkers(){

        try {
            if(mGoogleMap != null){

                resetMap();

                if(mClusterManager == null){
                    mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
                }
                if(mClusterManagerRenderer == null){
                    mClusterManagerRenderer = new MyClusterManagerRenderer(
                            getActivity(),
                            mGoogleMap,
                            mClusterManager);


                    mClusterManager.setRenderer(mClusterManagerRenderer);
                }
                mGoogleMap.setOnInfoWindowClickListener(this);

                for(UserLocation userLocation: mUserLocations){

                    Log.d(TAG, "addMapMarkers: location: " + userLocation.getGeo_point().toString());
                    try{
                        String snippet = "";
                        int avatar = R.drawable.bike;
                        if(userLocation.getUser().getUser_id().equals("me_employee_4")){
                            snippet = "This current Pneck Boy location";
                            avatar = R.drawable.bike;
                        }
                        else{
                            avatar = R.drawable.ic_house;
                            snippet = "Customer Address : " + customerAddress;
                        }

                        // set the default avatar
                        try{
                            avatar = Integer.parseInt(userLocation.getUser().getAvatar());
                        }catch (NumberFormatException e){
                            Log.d(TAG, "addMapMarkers: no avatar for " + userLocation.getUser().getUsername() + ", setting default.");
                        }
                        ClusterMarker newClusterMarker = new ClusterMarker(
                                new LatLng(userLocation.getGeo_point().getLatitude(), userLocation.getGeo_point().getLongitude()),
                                userLocation.getUser().getUsername(),
                                snippet,
                                avatar,
                                userLocation.getUser()
                        );
                        mClusterManager.addItem(newClusterMarker);
                        mClusterMarkers.add(newClusterMarker);



                    }catch (NullPointerException e){
                        Log.e(TAG, "addMapMarkers: NullPointerException: " + e.getMessage() );
                    }

                }
                mClusterManager.cluster();



                if (mClusterMarkers.size()<1){
                    return;
                }
                ClusterMarker cmarker=mClusterMarkers.get(1);
                mClusterManager.removeItem(cmarker);
                cmarker=mClusterMarkers.get(0);
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(cmarker.getPosition())
                        .title(cmarker.getTitle())
                        .snippet(cmarker.getSnippet()));

                resetSelectedMarker();
                mSelectedMarker = marker;
                calculateDirections(marker);

                setCameraView();
            }
        }catch (Exception e){
            Log.e("kjdhfsdfss","this is exception error "+e.getMessage());
        }

    }

    /**
     * Determines the view boundary then sets the camera
     * Sets the view
     */
    private void setCameraView() {

        // Set a boundary to start
        double bottomBoundary = mUserPosition.getGeo_point().getLatitude() - .0058;
        double leftBoundary = mUserPosition.getGeo_point().getLongitude() - .0058;
        double topBoundary = mUserPosition.getGeo_point().getLatitude() + .0058;
        double rightBoundary = mUserPosition.getGeo_point().getLongitude() +.0058;

        mMapBoundary = new LatLngBounds(
                new LatLng(bottomBoundary, leftBoundary),
                new LatLng(topBoundary, rightBoundary)
        );

        mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, DEFAULT_ZOOM));
            }
        });

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition arg0) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, DEFAULT_ZOOM));
            }
        });
        //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 30));
    }

    private void setUserPosition() {
        for (UserLocation userLocation : mUserLocations) {
            if (!userLocation.getUser().getUser_id().equals("me_employee_4")) {
                mUserPosition = userLocation;
            }
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_api_key))
                    .build();
        }
    }

    private void initUserListRecyclerView() {
       /* mUserRecyclerAdapter = new UserRecyclerAdapter(mUserList, this);
        mUserListRecyclerView.setAdapter(mUserRecyclerAdapter);
        mUserListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable(); // update user locations every 'LOCATION_UPDATE_INTERVAL'
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        map.setMyLocationEnabled(true);
//        mGoogleMap = map;
//        setCameraView();

        Log.e("kjdfhskf","map is ready");

        mGoogleMap = map;
        addMapMarkers();
        mGoogleMap.setOnPolylineClickListener(this);

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        stopLocationUpdates(); // stop updating user locations
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void expandMapAnimation(){
        /*ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                50,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                50,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();*/
    }

    private void contractMapAnimation(){
        /*ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                50);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(mUserListRecyclerView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                50);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_full_screen_map:{

                if(mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }
                else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

            case R.id.btn_reset_map:{
                addMapMarkers();
                break;
            }
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        Log.e("dsfsdfsdsdf","this is onInfoWindowClick ");
        /*if(marker.getTitle().contains("Trip #")){
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            if(marker.getSnippet().equals("This is you")){
                marker.hideInfoWindow();
            }
            else{

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setMessage("Show poly lines?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                resetSelectedMarker();
                                mSelectedMarker = marker;
                                calculateDirections(marker);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }*/

    }

    private void calculateDirections(Marker marker){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mUserPosition.getGeo_point().getLatitude(),
                        mUserPosition.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
//                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
//                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
//                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
//                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                Log.d(TAG, "onResult: successfully retrieved directions.");
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void resetSelectedMarker(){
        if(mSelectedMarker != null){
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void removeTripMarkers(){
        for(Marker marker: mTripMarkers){
            marker.remove();
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);
                if(mPolyLinesData.size() > 0){
                    for(PolylineData polylineData: mPolyLinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    // highlight the fastest route and adjust camera
                    double tempDuration = route.legs[0].duration.inSeconds;
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    mSelectedMarker.setVisible(false);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 50;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolyLinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue1));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                String Title;
                Title=sessionManager.getUserName();

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(
                                Double.parseDouble(sessionManager.getUserLatitude()),
                                Double.parseDouble(sessionManager.getUserLongitude())))
                        .title(Title)
                        .icon(PublicMethod.convertToBitmapFromVector(getContext(),
                                R.drawable.ic_placeholder))
                        .snippet("Duration: " + polylineData.getLeg().duration));

                mTripMarkers.add(marker);

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

   /* @Override
    public void onUserClicked(int position) {
        Log.d(TAG, "onUserClicked: selected a user: " + mUserList.get(position).getUser_id());

        String selectedUserId = mUserList.get(position).getUser_id();

        for(ClusterMarker clusterMarker: mClusterMarkers){
            if(selectedUserId.equals(clusterMarker.getUser().getUser_id())){
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(clusterMarker.getPosition().latitude, clusterMarker.getPosition().longitude)),
                        600,
                        null
                );
                break;
            }
        }
    }*/
}
