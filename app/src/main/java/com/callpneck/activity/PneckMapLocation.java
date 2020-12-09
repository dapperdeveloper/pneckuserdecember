package com.callpneck.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.callpneck.LaunchActivityClass;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.commonutility.AllUrl;
import com.callpneck.model.MapPointerModel;
import com.callpneck.SessionManager;
import com.callpneck.Const;
import com.callpneck.utils.PublicMethod;
import com.shivtechs.maplocationpicker.MapUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class PneckMapLocation extends AppCompatActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private boolean mLocationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation = null;
    //not initilized till now
    private LatLng mDefaultLocation;
    private float DEFAULT_ZOOM = 15.0f;

    private static final String TAG = "GoogleMapActivity";
    private GoogleMap mMap;
    private LatLng mCenterLatLong;
    private Marker locationMarker;


    private LinearLayout AddressView;
    private LinearLayout TryAgainView;
    private TextView TryAgainBtn;
    private TextView BookNowBtn;
    //private TextView WorkAddressTypeHeading;
    //private TextView OtherAddressTypeHeading;
    //private TextView LocalityArea;
    private EditText CompleteAddress;
    private ImageButton mCurrentLocationBtn;

    //Bottom layout
   /* private LinearLayout Final_address_view;
    private RelativeLayout BottomLayout;
    private LinearLayout SaveAddressBtn;*/
    //private LinearLayout WorkAddressTitle;
    //private LinearLayout OtherAddressTitle;
    /*private EditText CompleteAddressEditText;
    private EditText LandMarkEditText;
    private View BackGroundOverLay;*/
    private boolean IsShowingSubmitAddress = false;


    private String city = "";
    private String state = "";
    private String SubAdminAreaAfterState = "";
    private String country = "";
    private String postalCode = "";
    private String knownName = "";
    private String locality = "";
    private String currentFullAddress="";
    private String UserLatitude = "";
    private String UserLongitude = "";
    private ProgressBar mProgressBar;

    private String your_booking_number, your_booking_status, your_booking_status_msg;
    private SessionManager sessionManager;
    private CountDownTimer mCountDownTimer;
    Dialog bookingDialog;
    ProgressBar pb;
    String p_ses_bk_id;
    Timer timer_pending_status;
    private long waiteTime=1000*60*2;
    Map<String, MapPointerModel> allMarkersMap = new HashMap<>();
    private ArrayList<MapPointerModel> mapsList=new ArrayList<>();

    private int progressStatus = 0;
    private long countDownInterval = 1000; //1 second (don't change this value)
    private String userId,epToken;

    private double mLatitude, mLongitude;

    private RelativeLayout navigationMenuLayout;
    private View backgroundOverlay;
    private RelativeLayout goBackMenu;
    private CardView menuIcon;
    private CircleImageView userImage;
    private View centerAlign;
    private RelativeLayout cameraImage;
    private TextView userName;
    private LinearLayout profileBtn;
    private LinearLayout homeBtn;
    private LinearLayout orders;
    private LinearLayout wallet;
    private LinearLayout setting;
    private LinearLayout notification;
    private LinearLayout aboutUs;
    private LinearLayout help;
    private LinearLayout share;
    private LinearLayout rateUs;
    private LinearLayout ReferAndEarn;
    private LinearLayout TermsAndCondition;
    private LinearLayout privacyPolicy;
    private LinearLayout logout;
    private LinearLayout socialMediaIntegration;
    private LinearLayout facebookLink;
    private LinearLayout linkedInLink;
    private LinearLayout twitterLink;
    private LinearLayout youtubeLink;
    private LinearLayout userFeedback;
    private MaterialRippleLayout VendorView;
    private FloatingActionButton Vendors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.address_setting_layout);

      //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(getResources().getColor(R.color.transparentcolor));
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sessionManager = new SessionManager(PneckMapLocation.this);
        findViews();
        clickListeners();

        //initialized addres search...
        MapUtility.apiKey = getResources().getString(R.string.google_api_key);


        timer_pending_status = new Timer();
        bookingDialog = new Dialog(PneckMapLocation.this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //if permission given

        mProgressBar=findViewById(R.id.progress_bar);

        userName.setText(sessionManager.getUserName());

    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    private boolean isExtraOpen=false;
    private void showExtraMenu(){
        if (!isExtraOpen){
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in);
            anim.reset();
            //InStockOptionMainLayout.setVisibility(View.VISIBLE);
            navigationMenuLayout.setVisibility(View.VISIBLE);
            navigationMenuLayout.clearAnimation();
            navigationMenuLayout.startAnimation(anim);
            isExtraOpen=true;
        }

        //extraOptionLayout.setVisibility(View.VISIBLE);
    }
    private void hideExtraMenu(){
        if (isExtraOpen){
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_out);
            anim.reset();

            navigationMenuLayout.clearAnimation();
            navigationMenuLayout.startAnimation(anim);
            final Handler handler = new Handler();

            handler.postDelayed(new Runnable(){
                public void run(){
                    //InStockOptionMainLayout.setVisibility(View.GONE);
                    isExtraOpen=false;
                    navigationMenuLayout.setVisibility(View.GONE);
                }
            }, 198);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ksdfhskdf","this is ses booking id "+sessionManager.getSesBookingId());
       int  progressBarMaximumValue = (int) (waiteTime / countDownInterval);
       if (progressStatus==progressBarMaximumValue){
           progressStatus  = 0;
       }

        if (sessionManager.getSesBookingId()!=null&&sessionManager.getSesBookingId().length()>=1){
            BookNowBtn.setText(getString(R.string.TRACK_ORDER));
        }else {
            progressStatus=0;
            BookNowBtn.setText(getString(R.string.BOOK_NOW));
        }

        if (sessionManager.getUserImage().length() >= 3) {
            Glide.with(PneckMapLocation.this)
                    .load(sessionManager.getUserImage())
                    .placeholder(R.drawable.ic_account_user)
                    .error(R.drawable.ic_account_user)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(userImage);
        }
    }

    @Override
    public void onBackPressed() {
        if (isExtraOpen) {
            hideExtraMenu();
        } else {
            super.onBackPressed();
        }
    }

    public void ShowSnackBar(String message, boolean isWarning) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);

        if (isWarning) {
            //textView.setTypeface(null, Typeface.BOLD);
            sbView.setBackgroundColor(getResources().getColor(R.color.warning_color));
        }

        //textView.setTextColor(color);
        snackbar.show();

    }

    private void SaveUserAddress() {

        if (UserLatitude == null || UserLongitude == null) {
            ShowSnackBar(getResources().getString(R.string.CAN_NOT_ABLE_TO_GET_ADDRESS), false);
            return;
        }


        if (locality == null || locality.trim().length() == 0) {
            locality = city;
        }
        if (city == null) {
            city = locality = "";
            //
        }
        if (postalCode == null) {
            postalCode = "";
        }
        if (knownName == null) {
            knownName = "";
        }
        if (country == null) {
            country = "";
        }
        if (state == null) {
            state = "";
        }
        if (SubAdminAreaAfterState == null) {
            SubAdminAreaAfterState = "";
        }


        try {

            JSONObject AddressObj = new JSONObject();
            //AddressObj.put("Shop_address", CompleteAddressEditText.getText().toString());
            AddressObj.put("Shop_locality", locality);
            AddressObj.put("Shop_address_extra_info", knownName);
            //AddressObj.put("Shop_address_landmark", LandMarkEditText.getText().toString());
            AddressObj.put("Shop_location_latitude", UserLatitude);
            AddressObj.put("Shop_location_longitude", UserLongitude);
            AddressObj.put("Shop_Country", country);
            AddressObj.put("Shop_District", city);
            AddressObj.put("Shop_SubAdminArea", SubAdminAreaAfterState);
            AddressObj.put("Shop_State", state);
            AddressObj.put("Shop_postal_code", postalCode);



        } catch (Exception e) {
            Log.e("sfskfjsfkd", "this is exception " + e.getMessage());
        }

    }


    private void findViews() {
        privacyPolicy=findViewById(R.id.privacy_policy);
        Vendors=findViewById(R.id.vendor);
        VendorView=findViewById(R.id.vendor_view);
        TryAgainView=findViewById(R.id.try_again_view);
        TryAgainBtn=findViewById(R.id.try_again_btn);
        userFeedback=findViewById(R.id.feedback);
        TermsAndCondition=findViewById(R.id.terms_condition);
        ReferAndEarn=findViewById(R.id.refer_and_earn);
        backgroundOverlay=findViewById(R.id.overlay);
        userImage = (CircleImageView)findViewById( R.id.user_image );
        centerAlign = (View)findViewById( R.id.center_align );
        cameraImage = (RelativeLayout)findViewById( R.id.camera_image );
        userName = (TextView)findViewById( R.id.user_name );
        profileBtn = (LinearLayout)findViewById( R.id.profile_btn );
        homeBtn=findViewById(R.id.home_btn);
        orders = (LinearLayout)findViewById( R.id.orders );
        wallet = (LinearLayout)findViewById( R.id.wallet );
        setting = (LinearLayout)findViewById( R.id.setting );
        notification = (LinearLayout)findViewById( R.id.notification );
        aboutUs = (LinearLayout)findViewById( R.id.about_us );
        help = (LinearLayout)findViewById( R.id.help );
        share = (LinearLayout)findViewById( R.id.share );
        rateUs = (LinearLayout)findViewById( R.id.rate_us );
        logout = (LinearLayout)findViewById( R.id.logout );
        socialMediaIntegration = (LinearLayout)findViewById( R.id.social_media_integration );
        facebookLink = (LinearLayout)findViewById( R.id.facebook_link );
        twitterLink = (LinearLayout)findViewById( R.id.twitter_link );
        youtubeLink = (LinearLayout)findViewById( R.id.youtube_link );
        linkedInLink=findViewById(R.id.linked_in);


        goBackMenu=findViewById(R.id.go_back_menu);
        menuIcon=findViewById(R.id.menu_icon);
        navigationMenuLayout=findViewById(R.id.menu_options);

        AddressView = (LinearLayout) findViewById(R.id.Address_view);
        BookNowBtn = (TextView) findViewById(R.id.done_save_address);
        //LocalityArea = (TextView)findViewById( R.id.Locality_area );
        CompleteAddress = (EditText) findViewById(R.id.Complete_address);
        mCurrentLocationBtn=findViewById(R.id.get_device_location);

    }

    public void sendLocation(String userId, String currentLati, String currentLongi,
                             String epToken, String current_address) {

        if (sessionManager.setUserLocation(""+currentLati,""+currentLongi)){
            Map<String, String> params = new HashMap<>();
            params.put("user_id", userId);
            params.put("user_lat", currentLati);
            params.put("user_long", currentLongi);
            params.put("ep_token", epToken);
            params.put("user_currentAddress", current_address);
            //sessionManager.setUserLocation(""+currentLati,""+currentLongi);
            Log.e("sdfjsfsdff","this is data we sending "+params);

            Log.e("sdfjsfsdff","this is data we latitude "+sessionManager.getUserLatitude()+
                    " longitude "+sessionManager.getUserLongitude());
            //progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
            //progressDialog.show();
            //Utility.showProgressDialog(this);
            RequestQueue requestQueue = Volley.newRequestQueue(PneckMapLocation.this);
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.sendLocation, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d("pprraa_book", jsonObject.toString());
                    try {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                        Log.d("jiii", jsonObject1.toString());
                        //Log.d("pass",pass);
                        String msg = jsonObject1.getString("message");
                        Log.d("massgr", msg);
                        boolean resp_status = true;
                        Log.e("sdjfkhskfd","this is success result "+jsonObject1.getBoolean("success"));
                        if (jsonObject1.getBoolean("success")) {


                            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                            String msge = jsonObject2.getString("msg");
                            String ses_booking_id = jsonObject2.getString("ses_booking_id");
                            your_booking_number = jsonObject2.getString("your_booking_number");
                            your_booking_status = jsonObject2.getString("your_booking_status");
                            your_booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                            sessionManager.setSesBookingId(ses_booking_id);
                            Toast.makeText(PneckMapLocation.this, msg, Toast.LENGTH_LONG).show();
                            AddressView.setVisibility(View.INVISIBLE);


                            bookingDialog.setContentView(R.layout.booking_id_dialog);
                            bookingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            bookingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            TextView user_book_id = (TextView) bookingDialog.findViewById(R.id.user_book_id);
                            //  Button btn_progress = (Button) bookingDialog.findViewById(R.id.btn_progress);
                            TextView userAddress=bookingDialog.findViewById(R.id.user_address);
                            TextView btn_book_cancle = (TextView) bookingDialog.findViewById(R.id.btn_book_cancle);
                            user_book_id.setText(your_booking_number);
                            pb = (ProgressBar) bookingDialog.findViewById(R.id.pb);
                            Log.e("ksdfskfsdsf","this is line 323 ");


                            userAddress.setText(CompleteAddress.getText().toString());

                            bookingDialog.show();
                            //bookingDialog.setCanceledOnTouchOutside(false);
                            bookingDialog.setCancelable(false);

                            int progressBarMaximumValue = (int) (waiteTime / countDownInterval);
                            pb.setMax(progressBarMaximumValue);
                            Log.e("kdsfhsfsfs","this is order progress bar max value "+progressBarMaximumValue);
                            mCountDownTimer = new CountDownTimer(waiteTime, countDownInterval) {
                                public void onTick(long millisUntilFinished) {
                                    //Another one second passed
                                    //Each second ProgressBar progress counter added one
                                    progressStatus += 1;
                                    pb.setProgress(progressStatus);
                                    Log.e("kdsfhsfsfs","this is order progress status "+progressStatus);
                                    if (progressStatus%10==0&&progressStatus!=0&&!isPendingStatusSuccess){
                                        userBookingPendingStatus(userId, epToken, p_ses_bk_id);
                                    }
                                }
                                public void onFinish() {
                                    progressStatus=0;
                                    userBookingCancel(userId, epToken, p_ses_bk_id,"System canceled order because no one accepted the order for 2 mins.");
                                    bookingDialog.dismiss();
                                }
                            }.start();

                            // end timer for 30 second

                            // show dialog for 30 second..................
                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                public void run() {
                                    bookingDialog.dismiss();
                                    // when the task active then close the dialog

                                    t.cancel();
                                    // also just top the timer thread, otherwise, you may receive a crash report

                                }
                            },waiteTime);
                            //AddressView.setVisibility(View.VISIBLE);
                            // end...........


                            //Set the schedule function
                            timer_pending_status.scheduleAtFixedRate(new TimerTask() {

                                                                         @Override
                                                                         public void run() {
                                                                             // Magic here
                                                                             p_ses_bk_id = sessionManager.getSesBookingId();
                                                                             //if (p_ses_bk_id != null && !p_ses_bk_id.isEmpty()) {

                                                                             //  }
                                                                             // cancle dialoog
                                                                             btn_book_cancle.setOnClickListener(new View.OnClickListener() {
                                                                                 @Override
                                                                                 public void onClick(View view) {
                                                                                     
                                                                                 }
                                                                             });
                                                                             // end cancle dialog
                                                                         }
                                                                     },
                                    0, 30000);   // 1000 Millisecond  = 1 second


                        } else {
                            Toast.makeText(PneckMapLocation.this, msg, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.e("ksdfskfsdsf","this is error  "+e.getMessage());
                        e.printStackTrace();
                    }


                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    Toast.makeText(PneckMapLocation.this,"System error "+error.getMessage(),Toast.LENGTH_LONG).show();
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
        }else {
            Toast.makeText(PneckMapLocation.this,"Location not set",Toast.LENGTH_SHORT).show();
        }


    }


    private boolean isCancelDialogOpen=false;
    public  void cancelOrderReasonDialog(final Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.cancel_order_dialog, null);
        TextInputEditText cancelReason;
        TextView stayOnOrder;
        TextView cancelOrder;

        cancelReason = (TextInputEditText)mView.findViewById( R.id.cancel_reason );
        stayOnOrder = (TextView)mView.findViewById( R.id.stay_on_order );
        cancelOrder = (TextView)mView.findViewById( R.id.cancel_order );

        builder.setView(mView);
        final AlertDialog NewCategory_dialog = builder.create();

        NewCategory_dialog.setCancelable(false);
        isCancelDialogOpen=true;

        stayOnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPendingStatusSuccess){
                    bookingDialog.cancel();
                    Bundle bundle=new Bundle();
                    bundle.putString("order_ses_id",p_ses_bk_id);
                    LaunchActivityClass.LaunchBookingCompleted(PneckMapLocation.this,bundle);
                }
                isCancelDialogOpen=false;
                NewCategory_dialog.dismiss();
            }
        });
        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cancelReason.getText().toString().length()<10){
                    Toast.makeText(activity,getString(R.string.PLEASE_ENTER_CANCEL_RESON),Toast.LENGTH_SHORT).show();
                    return;
                }

                userBookingCancel(userId, epToken, p_ses_bk_id,cancelReason.getText().toString());
                NewCategory_dialog.dismiss();
            }
        });

        NewCategory_dialog.show();
    }


    private boolean isPendingStatusSuccess=false;

    public void userBookingPendingStatus(String userId, String epToken, String p_ses_bk_id) {
        Log.e("kjdhkjdfsdfd","called userBookingPendingStatus");
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", p_ses_bk_id);
//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(PneckMapLocation.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingPendingStatus, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_pending_status", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_pending_resp", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {

                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        String currentBookingStatus = jsonObject2.getString("your_booking_status");
                        String booking_status_msg = jsonObject2.getString("your_booking_status_msg");
                        String your_booking_id = jsonObject2.getString("your_booking_id");
                        String your_booking_no = jsonObject2.getString("your_booking_number");

                        if (currentBookingStatus.equals("accepted")||
                                currentBookingStatus.equals("accepted_otp_confirmed")||
                                currentBookingStatus.equals("order_info_provided")||
                                currentBookingStatus.equals("delivery_otp_confirmed")||
                                currentBookingStatus.equals("order_request_payment")) {
                            isPendingStatusSuccess=true;

                            AddressView.setVisibility(View.VISIBLE);
                            mProgressBar.setVisibility(View.GONE);
                            mCountDownTimer.cancel();
                            bookingDialog.dismiss();
                            timer_pending_status.cancel();
                            progressStatus=0;


                            //change above this
                            LaunchActivityClass.LaunchRealTimeOrderTracking(PneckMapLocation.this);
                        }

                        /*Bundle bundle=new Bundle();
                        bundle.putString("order_ses_id",p_ses_bk_id);
                        LaunchActivityClass.LaunchRealTimeOrderTracking(PneckMapLocation.this);
*/
                        /*if (!isCancelDialogOpen){


                            //LaunchActivityClass.LaunchBookingCompleted(PneckMapLocation.this,bundle);
                        }*/
                        /*
                        Intent intent =new Intent(PneckMapLocation.this,BookingCompleted.class);
                        startActivity(intent);*/


                    } else {
                        Toast.makeText(PneckMapLocation.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error", "Error: " + error.getMessage());
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


    private void userBookingCancel(String userId, String epToken, String ses_booking, String cancelReason) {
        Map<String, String> params = new HashMap<>();
        ses_booking = sessionManager.getSesBookingId();
        params.put("user_id", userId);
        params.put("ep_token", epToken);
        params.put("ses_booking_id", ses_booking);
        params.put("cancel_reason",cancelReason);
        params.put("sys_ord_cancel","1");
        Log.e("booking_cancle","this is we are sending "+params);
//        progressDialog = new SpotsDialog(PneckMapLocation.this, R.style.Custom);
        //  progressDialog.show();
        //Utility.showProgressDialog(this);
        RequestQueue requestQueue = Volley.newRequestQueue(PneckMapLocation.this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, AllUrl.userBookingCancel, new JSONObject(params), new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d("booking_cancle", jsonObject.toString());
                // Utility.dismissProgressDialog();
                //     progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                    Log.d("booking_cancle_response", jsonObject1.toString());
                    Boolean pass = jsonObject1.getBoolean("success");
                    //Log.d("pass",pass);
                    String msg = jsonObject1.getString("message");
                    Log.d("massgr", msg);
                    boolean resp_status = true;
                    if (pass.equals(resp_status)) {
                        isCancelDialogOpen=false;
                        sessionManager.clearOrderSession();
                        bookingDialog.cancel();
                        TryAgainView.setVisibility(View.VISIBLE);
                        //AddressView.setVisibility(View.VISIBLE);
                        mProgressBar.setVisibility(View.GONE);
                        JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                        progressStatus=0;
                        BookNowBtn.setText(getResources().getString(R.string.BOOK_NOW));
                        Toast.makeText(getApplicationContext(),getString(R.string.NO_PNECK_BOY_AVALIABLE),Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(PneckMapLocation.this, msg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("booking_cancle", "Error: " + error.getMessage());
                Toast.makeText(PneckMapLocation.this,"Sestem error "+error.getMessage(),Toast.LENGTH_SHORT).show();
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


    private void startBookingOrder() {
        userId = sessionManager.getUserid();
        epToken = sessionManager.getUserToken();

        mProgressBar.setVisibility(View.VISIBLE);

        sendLocation(userId, UserLatitude, UserLongitude, epToken, CompleteAddress.getText().toString());

        /*CompleteAddressEditText.setText(CompleteAddress.getText().toString());
        CompleteAddressEditText.setSelection(CompleteAddressEditText.getText().toString().length() - 1);

        BottomLayout.setVisibility(View.VISIBLE);
        Final_address_view.setVisibility(View.VISIBLE);
        BackGroundOverLay.setVisibility(View.VISIBLE);

        Animation anim = AnimationUtils.loadAnimation(PneckMapLocation.this, R.anim.fade_in);
        anim.reset();
        BackGroundOverLay.clearAnimation();
        BackGroundOverLay.startAnimation(anim);

        Animation SlideUpnim = AnimationUtils.loadAnimation(PneckMapLocation.this, R.anim.slide_in_up);
        anim.reset();
        Final_address_view.clearAnimation();
        Final_address_view.startAnimation(SlideUpnim);*/


    }

    private void hideFinalAddressSubmition() {
        IsShowingSubmitAddress = false;/*
        Animation anim = AnimationUtils.loadAnimation(PneckMapLocation.this, R.anim.fade_out);
        anim.reset();
        BackGroundOverLay.clearAnimation();
        BackGroundOverLay.startAnimation(anim);

        Animation SlideDownanim = AnimationUtils.loadAnimation(PneckMapLocation.this, R.anim.slide_down_simple);
        anim.reset();
        Final_address_view.clearAnimation();
        Final_address_view.startAnimation(SlideDownanim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BottomLayout.setVisibility(View.GONE);
                Final_address_view.setVisibility(View.GONE);
                BackGroundOverLay.setVisibility(View.GONE);
            }
        }, 150);
*/
    }


    public static Intent getOpenFacebookIntent(Context context) {

        Log.e("kjhfsdfsfs","calliing facebook intent ");
        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            //return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/Pneck-100208454759224"));
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pneckteam/"));
        } catch (Exception e) {
            Log.e("kjhfsdfsfs","calliing facebook intent error "+e.getMessage());
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pneckteam/"));
        }
    }
    private Intent getTwitterIntent(Context context) {
        Intent intent = null;
        try{
            // Get Twitter app
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            //intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=Pneck3"));
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Pneck3?s=01"));
        } catch (Exception e) {
            // If no Twitter app found, open on browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/Pneck3?s=01"));
        }
        return intent;
    }
    private void clickListeners() {
        mCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        Vendors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( UserLatitude.length()>1&&UserLongitude.length()>1){
                    Bundle bundle =new Bundle();
                    bundle.putString("userLat",UserLatitude);
                    bundle.putString("userLong",UserLongitude);
                    LaunchActivityClass.LaunchVendorMainActivity(PneckMapLocation.this,bundle);
                }else {
                    Toast.makeText(PneckMapLocation.this,getString(R.string.ADJUST_USER_LOCATION),Toast.LENGTH_SHORT).show();
                }
            }
        });

        VendorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( UserLatitude.length()>1&&UserLongitude.length()>1){
                    Bundle bundle =new Bundle();
                    bundle.putString("userLat",UserLatitude);
                    bundle.putString("userLong",UserLongitude);
                    LaunchActivityClass.LaunchVendorMainActivity(PneckMapLocation.this,bundle);
                }else {
                 Toast.makeText(PneckMapLocation.this,getString(R.string.ADJUST_USER_LOCATION),Toast.LENGTH_SHORT).show();
                }
            }
        });

        TryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TryAgainView.setVisibility(View.GONE);
                AddressView.setVisibility(View.VISIBLE);
            }
        });
        userFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LaunchActivityClass.LaunchFeedbackScreen(PneckMapLocation.this);
            }
        });
        TermsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("url","http://pneck.in/termsandconditions");
                bundle.putBoolean("is_privacy",false);
                LaunchActivityClass.LaunchWebScreen(PneckMapLocation.this,bundle);
            }
        });
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle=new Bundle();
                bundle.putString("url","http://pneck.in/termsandconditions");
                bundle.putBoolean("is_privacy",true);
                LaunchActivityClass.LaunchWebScreen(PneckMapLocation.this,bundle);
            }
        });

        linkedInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://pneck-services-a04467196"));
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/pneck-in-62b88b1a3"));
                final PackageManager packageManager = PneckMapLocation.this.getPackageManager();
                final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (list.isEmpty()) {
                    Log.e("kjdfksfs","form link ");
                }
                startActivity(intent);
            }
        });

        facebookLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent= getOpenFacebookIntent(PneckMapLocation.this);
               startActivity(intent);
            }
        });

        youtubeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UCeIXgNHI5ttPtsaVkCSQ4DA"));
                startActivity(intent);
               // getOpenFacebookIntent(PneckMapLocation.this);
            }
        });
        twitterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=getTwitterIntent(PneckMapLocation.this);
                startActivity(intent);
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchProfile(PneckMapLocation.this);
            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchProfile(PneckMapLocation.this);
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchWallet(PneckMapLocation.this);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchSetting(PneckMapLocation.this);
            }
        });
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchNotification(PneckMapLocation.this);
            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchAboutUs(PneckMapLocation.this);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchHelp(PneckMapLocation.this);
            }
        });
        ReferAndEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchREferAndEarn(PneckMapLocation.this);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                shareApp();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                openPlayStore(PneckMapLocation.this);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                logoutToServer();
            }
        });


        backgroundOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
            }
        });

        navigationMenuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        goBackMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showExtraMenu();
            }
        });

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideExtraMenu();
                LaunchActivityClass.LaunchMyOrdersScreen(PneckMapLocation.this);
            }
        });


        /*SaveAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CompleteAddressEditText.getText().length() > 5) {
                    SaveUserAddress();
                } else {
                    CompleteAddressEditText.setError(getResources().getString(R.string.ENTER_VALID_ADDRESS));
                }
            }
        });

        Final_address_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        BackGroundOverLay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hideFinalAddressSubmition();

                return true;
            }
        });*/

        BookNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dsfsfdsfdsf","this is current booking id "+sessionManager.getSesBookingId());
                if (sessionManager.getSesBookingId()==null||sessionManager.getSesBookingId().length()<=0){
                    startBookingOrder();
                }else {
                    Toast.makeText(PneckMapLocation.this,getString(R.string.TRACK_RUNNING_ORDER),Toast.LENGTH_SHORT).show();
                    Bundle bundle=new Bundle();
                    bundle.putString("order_ses_id",p_ses_bk_id);
                    LaunchActivityClass.LaunchBookingCompleted(PneckMapLocation.this,bundle);
                }
            }
        });

        /*WorkAddressTypeHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDRESS_TYPE_SELECTED=2;
                startBookingOrder();
            }
        });

        OtherAddressTypeHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ADDRESS_TYPE_SELECTED=3;
                startBookingOrder();
            }
        });
        */
    }


    private void logoutToServer() {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userLogout";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id", sessionManager.getUserid());
        dataParams.put("ep_token", sessionManager.getUserToken());

        Log.e("logout_user", " that's we are sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                userLogoutSuccess(),
                userLogoutError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(PneckMapLocation.this).add(dataParamsJsonReq);

    }

    private Response.Listener<JSONObject> userLogoutSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.v("logout_user", "this is complete response " + response);
                    if(sessionManager.getLoginType().equals("2"))
                    {
                      //  LoginManager.getInstance().logOut();

                    }

                    sessionManager.clearSession();
                    LaunchActivityClass.LaunchLoginScreen(PneckMapLocation.this);

                } catch (Exception e) {
                    Log.e("logout_user", "this is error exception " + e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener userLogoutError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show();
                Log.v("logout_user", "inside error block  " + error.getMessage());
            }
        };
    }


    public static void openPlayStore(Context context) {
        // you can also use BuildConfig.APPLICATION_ID
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

    private void shareApp() {
        String applink;
        try {
            applink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
        } catch (android.content.ActivityNotFoundException anfe) {
            applink = "" + Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Join Pneck your local service provider");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Pneck provide most trusted and fastest service at your door step." +
                "\n\nClick on the below link and join Pneck network.\n"+applink);
        startActivity(Intent.createChooser(sharingIntent, "Choose app..."));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //refer to link https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial

        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);



    }

    @Override
    public void onCameraIdle() {
        Log.e("skjlksfjsfs", "camera is in ideal state");
        if (mLastKnownLocation == null) {
            getDeviceLocation();
        } else {
            /**
            CameraPosition cameraPosition = mMap.getCameraPosition();
            mCenterLatLong = cameraPosition.target;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getCompleteAddressString(mCenterLatLong.latitude, mCenterLatLong.longitude);

                }
            }, 100*2);**/
        }
    }


    private void getCompleteAddressString(double latitude, double longitude) {

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
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


                currentFullAddress=address;
                CompleteAddress.setText(address);

                Log.e("CURRENTADDRESS","Latitude AAAAAA is called...."+UserLatitude.toString());
                Log.e("CURRENTADDRESS","Longitude AAAAAA  is called...."+UserLongitude.toString());
                Log.e("CURRENTADDRESS","address AAAAAA is called...."+address.toString());
                //CompleteAddress.setSelection(CompleteAddress.getText().length());

                getEmployeeList();
            }

        } catch (Exception e) {
            Log.e("kjfksfsfsf", "this is error exception " + e.getMessage());
        }

    }


    private void getEmployeeList() {

       // progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userNearByEmployeesList";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());
        dataParams.put("curr_lat",""+UserLatitude);
        dataParams.put("curr_long",""+UserLongitude);
        dataParams.put("curr_address",currentFullAddress);

        Log.e("emoloyee_list", "this is url " +ServerURL);

        Log.e("emoloyee_list", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                SuccessListeners(),
                RegistrationError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(PneckMapLocation.this).add(dataParamsJsonReq);
    }


    private Response.Listener<JSONObject> SuccessListeners() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("emoloyee_list", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    mMap.clear();
                    mapsList.clear();
                    allMarkersMap.clear();

                    if (innerResponse.getBoolean("success")) {

                        JSONObject dataObj=innerResponse.getJSONObject("data");
                        JSONArray array=dataObj.getJSONArray("employees");

                        for (int i=0;i<array.length();i++){
                            JSONObject object=array.getJSONObject(i);

                            LatLng point = new LatLng(Double.parseDouble(object.getString("curr_latitude")), Double.parseDouble(object.getString("curr_longitude")));
                            mMap.addMarker(new MarkerOptions().position(point).icon(PublicMethod.convertToBitmapFromVector(PneckMapLocation.this,
                                    R.drawable.ic_scooter)).title(object.getString("first_name")
                                    +" "+object.getString("distance_km")+" away").snippet(object.getString("curr_loc_address")));

                            MapPointerModel mapModel=new MapPointerModel(object.getString("first_name"),
                                    object.getString("is_online"),object.getString("duty_status"),
                                    object.getString("distance_km"),object.getString("curr_latitude"),
                                    object.getString("curr_longitude"),object.getString("curr_loc_address"));
                            mapsList.add(mapModel);

                            allMarkersMap.put(object.getString("curr_latitude") + "," + object.getString("curr_longitude"), mapModel);
                        }

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {

                                try {
                                    if (marker != null) {
                                        Log.e("kjfdhgdkgd","this is marker clicked");

                                    }
                                } catch (Exception e) {

                                }
                                return true;
                            }
                        });
                    }

                } catch (Exception e) {
                    Log.v("emoloyee_list", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener RegistrationError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v("emoloyee_list", "inside error block  " + error.getMessage());
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocation();
    }

    private void requestLocation() {

        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //TODO: UI updates.
                    }
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(PneckMapLocation.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void getDeviceLocation() {
        Log.e("skjlksfjsfs","inside getDeviceLocation ");
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PneckMapLocation.this, "Getting Current Location...", Toast.LENGTH_SHORT).show();
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if (mLastKnownLocation!=null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                getCompleteAddressString(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
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
            Log.e("location_get_error", e.getMessage());
        }
    }


    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        //mMap.clear();

    }


}
