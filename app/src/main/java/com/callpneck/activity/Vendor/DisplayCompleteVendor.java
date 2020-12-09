package com.callpneck.activity.Vendor;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;

import com.callpneck.Language.ThemeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.callpneck.Const;
import com.callpneck.R;
import com.callpneck.Requests.CustomRequest;
import com.callpneck.Requests.JsonUTF8Request;
import com.callpneck.SessionManager;
import com.callpneck.activity.Vendor.Fragments.ImageHolder;
import com.callpneck.model.VendorModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DisplayCompleteVendor extends AppCompatActivity {

    private ViewPager imageHolder;
    private CircleImageView userImage;
    private TextView vendorName;
    private TextView vendorRating;
    //private TextView primeService;
    private TextView category;
    //private TextView catalog;
    private TextView distanceAway;
    private String vendorLatitude,vendorLongitude;
    private TextView workingTime;
    private TextView workingDays;
    private MaterialRippleLayout phoneLayout;
    private TextView userPhoneNo;
    private LinearLayout cancelOrder;
    private TextView vendorType;
    private ArrayList<String> imageArrayList=new ArrayList<>();
    private TextView vendorAddress;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    private String phoneNumber;
    private boolean isCallClicked=false;

    private LinearLayout rateMe;


    private void findViews() {
        NavigateDirection=findViewById(R.id.navigate_direction);
        showDirection=findViewById(R.id.direction);
        imageHolder = (ViewPager)findViewById( R.id.image_holder );
        userImage = (CircleImageView) findViewById( R.id.user_image );
        vendorName = (TextView)findViewById( R.id.vendor_name );
        vendorRating = (TextView)findViewById( R.id.vendor_rating );
        //primeService = (TextView)findViewById( R.id.prime_service );
        category = (TextView)findViewById( R.id.category );
        //catalog = (TextView)findViewById( R.id.catalog );
        distanceAway = (TextView)findViewById( R.id.distance_away );
        workingTime = (TextView)findViewById( R.id.working_time );
        workingDays = (TextView)findViewById( R.id.working_days );
        phoneLayout = (MaterialRippleLayout)findViewById( R.id.phone_layout );
        userPhoneNo = (TextView)findViewById( R.id.user_phone_no );
        cancelOrder = (LinearLayout)findViewById( R.id.cancel_order );
        vendorType = (TextView)findViewById( R.id.vendor_type );
        progressBar=findViewById(R.id.progress_bar);
        vendorAddress=findViewById(R.id.address);
        rateMe = (LinearLayout)findViewById( R.id.rate_me );
    }


    private String userLatitude,userLongitude;
    private ProgressBar progressBar;
    private String currentVendorId;
    private SessionManager sessionManager;
    private FloatingActionButton NavigateDirection;
    private MaterialRippleLayout showDirection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_display_complete_vendor);

        findViews();
        clickListeners();
        userLatitude=getIntent().getStringExtra("user_lat");
        userLongitude=getIntent().getStringExtra("user_long");

        currentVendorId=getIntent().getStringExtra("vendor_id");
        sessionManager=new SessionManager(DisplayCompleteVendor.this);

        getVendorData();

    }

    @Override
    protected void onResume() {
        if (isCallClicked){
            RateUsDialog(DisplayCompleteVendor.this);
        }
        super.onResume();
    }

    public void RateUsDialog(Activity activity) {
        isCallClicked=false;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.rate_us_alert_dialog, null);

        TextView CancelDialog;
        TextView SubmitRating;

        final LinearLayout commentLayout = mView.findViewById(R.id.comment_layout);
        final RatingBar ratingBar = mView.findViewById(R.id.user_rating);
        final EditText userFeedBack = mView.findViewById(R.id.user_feed_back_comment);

        CancelDialog = (TextView) mView.findViewById(R.id.Cancel_dialog);
        SubmitRating = (TextView) mView.findViewById(R.id.submit_rating);


        builder.setView(mView);
        final AlertDialog NewCategory_dialog = builder.create();

        NewCategory_dialog.setCancelable(false);


        SubmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() > 0) {
                    NewCategory_dialog.dismiss();
                    submitRatingToServer(ratingBar.getRating(), userFeedBack.getText().toString());

                } else {
                    Toast.makeText(DisplayCompleteVendor.this, "Please provide your feedback", Toast.LENGTH_SHORT).show();
                }

            }
        });

        CancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewCategory_dialog.dismiss();
            }
        });

        NewCategory_dialog.show();

    }


    private void submitRatingToServer(float rating, String user_feedback) {

        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userAddVendorRating";
        HashMap<String, String> dataParams = new HashMap<String, String>();
/*

        ses_booking_id,  user_type,
                rating , message
*/
        dataParams.put("user_id", sessionManager.getUserid());
        dataParams.put("ep_token", sessionManager.getUserToken());
        dataParams.put("vendor_id", currentVendorId);
        dataParams.put("rating", "" + rating);
        if (user_feedback.length() == 0) {
            user_feedback = "empty";
        }
        dataParams.put("message", user_feedback);

        Log.e("user_feedback", " that's url  " + ServerURL);

        Log.e("user_feedback", " that's we are sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                UserFeedbackSuccessListener(),
                UserFeedbackErrorListener());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DisplayCompleteVendor.this).add(dataParamsJsonReq);

    }

    private String tag = "rating_feedback";

    private Response.Listener<JSONObject> UserFeedbackSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Log.v("sfskfjskdfjs", "this is complete response " + response);

                    if (response.getBoolean("success")) {
                        Toast.makeText(DisplayCompleteVendor.this, "Your feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("sfskfjskdfjs", "this is error exception " + e.getMessage());
                }
            }
        };
    }

    private Response.ErrorListener UserFeedbackErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.SOMETHING_WENT_WRONG)+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v("sfskfjskdfjs", "inside error block  " + error.getCause());
            }
        };
    }

    private void openDirectionMap() {
        Uri navigationIntentUri = Uri.parse("google.navigation:q=" +  vendorLatitude +"," + vendorLongitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {

            startActivity(mapIntent);
            //startActivity(intent);
        }
        catch(ActivityNotFoundException ex) {
            try
            {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationIntentUri+""));
                startActivity(unrestrictedIntent);
            }
            catch(ActivityNotFoundException innerEx) {
                Toast.makeText(DisplayCompleteVendor.this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }



    private void clickListeners() {

        showDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDirectionMap();
            }
        });
        NavigateDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDirectionMap();
            }
        });

        rateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RateUsDialog(DisplayCompleteVendor.this);
            }
        });


        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCallClicked=true;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
                startActivity(intent);
            }
        });
    }

    private void getVendorData() {

        progressBar.setVisibility(View.VISIBLE);
        String ServerURL = getResources().getString(R.string.pneck_app_url) + "/userGetVendor";
        HashMap<String, String> dataParams = new HashMap<String, String>();

        dataParams.put("user_id",sessionManager.getUserid());
        dataParams.put("ep_token",sessionManager.getUserToken());
        dataParams.put("vendor_id",currentVendorId);
        dataParams.put("curr_lat",userLatitude);
        dataParams.put("curr_long",userLongitude);

        Log.e("get_vendors", "this is url " +ServerURL);

        Log.e("get_vendors", "this is we sending " + dataParams.toString());

        CustomRequest dataParamsJsonReq = new CustomRequest(JsonUTF8Request.Method.POST,
                ServerURL,
                dataParams,
                ShowSuccess(),
                ShowError());
        dataParamsJsonReq.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(Const.VOLLEY_RETRY_TIMEOUT),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(DisplayCompleteVendor.this).add(dataParamsJsonReq);
    }

    private Response.Listener<JSONObject> ShowSuccess() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("get_vendors", "this is complete response " + response);
                    JSONObject innerResponse=response.getJSONObject("response");
                    if (innerResponse.getBoolean("success")) {
                        progressBar.setVisibility(View.GONE);
                        JSONObject jsondata=innerResponse.getJSONObject("data");
                        JSONArray vendorsList=jsondata.getJSONArray("vendors");
                        for (int i=0;i<vendorsList.length();i++){
                            JSONObject object=vendorsList.getJSONObject(i);
                           VendorModel data=new VendorModel(object.getString("vendor_id"),
                                   object.getString("rating"),
                                   !object.getString("shop_title").equalsIgnoreCase("null")?object.getString("shop_title"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("first_name").equalsIgnoreCase("null")?object.getString("first_name"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("type").equalsIgnoreCase("null")?object.getString("type"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("opening_time").equalsIgnoreCase("null")?object.getString("opening_time"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("closing_time").equalsIgnoreCase("null")?object.getString("closing_time"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   object.getString("phone"),
                                   object.getString("profile_image"),
                                   !object.getString("professional_service").equalsIgnoreCase("null")?object.getString("professional_service"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("category").equalsIgnoreCase("null")?object.getString("category"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("catalogues").equalsIgnoreCase("null")?object.getString("catalogues"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   !object.getString("distance_km").equalsIgnoreCase("null")?object.getString("distance_km"):getResources().getString(R.string.DATA_NOT_FOUND),
                                   object.getString("curr_latitude"),
                                   object.getString("curr_longitude"),
                                   object.getString("curr_loc_address"));


                           phoneNumber=data.getUserPhone();
                            /*String daysList=object.getString("days");
                            JSONArray darray=new JSONArray(daysList);
                            String workDays="Working Days : ";
                            for (int j=0;j<darray.length();j++){
                                JSONObject object1=darray.getJSONObject(j);
                                if (j!=0){
                                    workDays+=" , ";
                                }
                                workDays+=""+object1.getString("day");
                            }*/

                            workingDays.setText("Available : "+object.getString("days"));

                            JSONArray imgArray=object.getJSONArray("images");
                            for (int k=0;k<imgArray.length();k++){
                                imageArrayList.add(""+imgArray.get(k));
                            }
                            updateImages();


                            Glide.with(DisplayCompleteVendor.this)
                                    .load(data.getUserImage())
                                    .placeholder(R.drawable.ic_account_user)
                                    .error(R.drawable.ic_account_user)
                                    .dontAnimate().into(userImage);
                            vendorRating.setText(data.getVendorRating());
                            vendorName.setText(data.getShopName());
                            workingTime.setText("Open At : "+data.getOpenTime()+" - Close At : "+data.getCloseTime());
                            //primeService.setText("Service : "+);
                            category.setText(data.getPrimeService()+", "+data.getCategory()+", "+data.getCatalogue());
                            //catalog.setText("Catalog : "+data.getCatalogue());
                            distanceAway.setText("Distance : "+data.getDistanc_km()+" away");
                            if (data.getUserType().equalsIgnoreCase("static")){
                                vendorType.setText(getResources().getString(R.string.STATIC));
                            }else {
                                vendorType.setText(getResources().getString(R.string.DYNAMIC));
                            }
                            vendorLatitude=data.getCurrent_latitude();
                            vendorLongitude=data.getCurrent_longitude();

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(DisplayCompleteVendor.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(Double.parseDouble(object.getString("curr_latitude")), Double.parseDouble(object.getString("curr_longitude")), 1);

                            if (addresses.size()>0){
                                vendorAddress.setText("Address : "+addresses.get(i).getAddressLine(0));
                            }else {
                                vendorAddress.setVisibility(View.GONE);
                                vendorAddress.setText("Not found");
                            }


                        }

                    }
                } catch (Exception e) {
                    Log.v("get_vendors", "inside catch block  " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
    }



    private Response.ErrorListener ShowError() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.SOMETHING_WENT_WRONG+error.getMessage(), Toast.LENGTH_LONG).show();
                Log.v("get_vendors", "inside error block  " + error.getMessage());
            }
        };
    }

    private void updateImages() {
        setupViewPager(imageHolder);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = buildAdapter();
        for (int i=0;i<imageArrayList.size();i++){
            adapter.addFrag(new ImageHolder(DisplayCompleteVendor.this,imageArrayList.get(i)),"");
        }

        viewPager.setAdapter(adapter);
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == imageArrayList.size()-1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    private ViewPagerAdapter buildAdapter() {
        return(new ViewPagerAdapter(DisplayCompleteVendor.this, getSupportFragmentManager()));
    }
    
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(Context context, FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
