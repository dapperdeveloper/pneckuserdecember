package com.callpneck.activity.registrationSecond.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.Database.MainData;
import com.callpneck.activity.Database.RoomDB;
import com.callpneck.activity.registrationSecond.Model.GetWallet;
import com.callpneck.activity.registrationSecond.Model.RawData;
import com.callpneck.activity.registrationSecond.Model.foodDashboard.ResponseOrderSubmit.ResponseOrderSubmit;
import com.callpneck.activity.registrationSecond.Model.getAddress.ResponseAddress;
import com.callpneck.activity.registrationSecond.Model.walletOrder.WalletOrder;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;
import com.callpneck.activity.registrationSecond.helper.Constant;
import com.callpneck.utils.ApiConfig;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {


    private static final String TAG = CheckoutActivity.class.getSimpleName();
    CheckBox chWallet, chHome, chWork,chLocation;
    LinearLayout lytPayOption, lytTax, lytWallet, lytRazorPay;
    String paymentMethod = "";
    public String razorPayId;
    RadioButton rbCod, rbRazorPay;
    TextView listTv, tvConfirmOrder, tvTaxPercent, tvCity, tvName, tvWltBalance, tvWallet, tvSubTotal, tvTotal, tvDeliveryCharge, tvTaxAmt;
    private SessionManager sessionManager;
    List<MainData> dataList = new ArrayList<>();
    RelativeLayout walletLyt;
    RoomDB database;
    int position;
    String name, image, price, cost, quantity;
    String deliveryCharge = "0";
    double usedBalance = 0;
    String user_id="", res_id="", userName="" ,userMobile="", usr_address="", userMail="" , longi="", lati="",  item_count="", total_amount="";
    ProgressDialog progressDialog;
    String label = "";
    Gson gson;
    Activity activity;
    RawData rawData;
    String json;
    private String[] locationPermission;
    private static final int LOCATION_REQUEST_CODE = 100;
    private double latitude, longitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    double total, subtotal;
    double taxAmt = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_checkout);
        listTv = findViewById(R.id.listTv);
        tvConfirmOrder  = findViewById(R.id.tvConfirmOrder);
        rbCod = findViewById(R.id.rbcod);
        rbRazorPay = findViewById(R.id.rbRazorPay);
        lytPayOption = findViewById(R.id.lytPayOption);
        lytRazorPay = findViewById(R.id.lytRazorPay);
        chWallet = findViewById(R.id.chWallet);
        chHome = findViewById(R.id.chHome);
        chWork = findViewById(R.id.chWork);
        tvName = findViewById(R.id.tvName);
        tvCity = findViewById(R.id.tvCity);
        tvWltBalance = findViewById(R.id.tvWltBalance);
        walletLyt = findViewById(R.id.walletLyt);
        lytWallet = findViewById(R.id.lytWallet);
        chLocation = findViewById(R.id.chLocation);
        tvWallet = findViewById(R.id.tvWallet);
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvTotal = findViewById(R.id.tvTotal);
        lytTax = findViewById(R.id.lytTax);
        tvTaxAmt = findViewById(R.id.tvTaxAmt);
        tvTaxPercent = findViewById(R.id.tvTaxPercent);
        tvDeliveryCharge = findViewById(R.id.tvDeliveryCharge);
        if (getIntent() != null){
            res_id = getIntent().getStringExtra("res_id");
            total_amount = getIntent().getStringExtra("total_amount");

        }
        //Initialize database
        database = RoomDB.getInstance(this);
        //store database value in data list
        dataList = database.mainDao().getAll();

        gson = new Gson();
        activity = this;
        sessionManager = new SessionManager(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait....");
        progressDialog.setCanceledOnTouchOutside(false);

        //init permission
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getUserDetails();

        ApiConfig.getWalletBalance(CheckoutActivity.this, sessionManager);

        chWallet.setTag("false");
        SetDataTotal();
        getWalletBalance();
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        if (Constant.WALLET_BALANCE == 0) {
            chWallet.setEnabled(false);
            walletLyt.setEnabled(false);
        }

        chWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chWallet.getTag().equals("false")) {
                    chWallet.setChecked(true);
                    lytWallet.setVisibility(View.VISIBLE);

                    if (Constant.WALLET_BALANCE >= subtotal) {
                        usedBalance = subtotal;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + (Constant.WALLET_BALANCE - usedBalance));
                        paymentMethod = "wallet";
                        lytPayOption.setVisibility(View.GONE);
                    } else {
                        usedBalance = Constant.WALLET_BALANCE;
                        tvWltBalance.setText(getString(R.string.remaining_wallet_balance) + Constant.SETTING_CURRENCY_SYMBOL + "0.0");
                        lytPayOption.setVisibility(View.GONE);
                        paymentMethod = "wallet";
                    }
                    subtotal = (subtotal - usedBalance);
                    tvWallet.setText("-" + Constant.SETTING_CURRENCY_SYMBOL + usedBalance);
                    tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.decimalformatData.format(subtotal));
                    chWallet.setTag("true");

                } else {
                    walletUncheck();
                }

            }
        });
        tvConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()){
                    if (paymentMethod.equals(getResources().getString(R.string.codpaytype))){
                        if (AppController.isConnected(CheckoutActivity.this))
                            PlaceOrderProcess();
                    }
                    else if (paymentMethod.equals(getString(R.string.razor_pay))){
                        if (AppController.isConnected(CheckoutActivity.this))
                            startPayment();
                    }
                    else if ( paymentMethod.equals("wallet")){
                        if (AppController.isConnected(CheckoutActivity.this))
                        OrderByWallet();
                    }
                }

            }
        });
        setPaymentMethod();


    }

    private void OrderByWallet() {
        progressDialog.setMessage("Ordering....");
        progressDialog.show();
        Call<WalletOrder> call = APIClient.getInstance().orderSubmitWallet(res_id, user_id, lati, longi, item_count, total_amount,
                json, userName, userMobile, usr_address, userMail, Constant.WALLET_BALANCE+"");

        call.enqueue(new Callback<WalletOrder>() {
            @Override
            public void onResponse(Call<WalletOrder> call, Response<WalletOrder> response) {

                try {
                    WalletOrder orderSubmit  = response.body();
                    if (orderSubmit != null && orderSubmit.getSuccess()){
                        startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                        progressDialog.dismiss();
                    }
                    else if(orderSubmit != null && !orderSubmit.getSuccess()) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, orderSubmit.getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<WalletOrder> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private void getUserDetails() {
        userName = sessionManager.getUserName();
        user_id = sessionManager.getUserid();
        userMobile = sessionManager.getUserMobile();
        userMail = sessionManager.getUserMail();
        tvName.setText(userName);
    }

    private void SetDataTotal() {
        total = Double.parseDouble(total_amount);
        tvTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.decimalformatData.format(total));
        subtotal = total;
        if (total <= Constant.SETTING_MINIMUM_AMOUNT_FOR_FREE_DELIVERY) {
            tvDeliveryCharge.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.SETTING_DELIVERY_CHARGE);
            subtotal = subtotal + Constant.SETTING_DELIVERY_CHARGE;
            deliveryCharge = Constant.SETTING_DELIVERY_CHARGE + "";
        } else {
            tvDeliveryCharge.setText(getResources().getString(R.string.free));
            deliveryCharge = "0";
        }
        taxAmt = ((Constant.SETTING_TAX * total) / 100);
        subtotal = (subtotal + taxAmt);
        tvTaxPercent.setText("Tax(" + Constant.SETTING_TAX + "%)");
        tvTaxAmt.setText("+ " + Constant.SETTING_CURRENCY_SYMBOL + Constant.decimalformatData.format(taxAmt));
        tvSubTotal.setText(Constant.SETTING_CURRENCY_SYMBOL + Constant.decimalformatData.format(subtotal));
    }

    public void walletUncheck() {
        lytPayOption.setVisibility(View.VISIBLE);
        tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
        lytWallet.setVisibility(View.GONE);
        paymentMethod ="";
        chWallet.setChecked(false);
        chWallet.setTag("false");
        SetDataTotal();
    }
    private void getWalletBalance() {
        Call<GetWallet> call = APIClient.getInstance().getWallet(user_id);
        call.enqueue(new Callback<GetWallet>() {
            @Override
            public void onResponse(Call<GetWallet> call, Response<GetWallet> response) {
                GetWallet getWallet = response.body();
                if (getWallet != null && getWallet.getStatus()){
                    Constant.WALLET_BALANCE = Double.parseDouble(getWallet.getAmount()+"");
                    tvWltBalance.setText(getString(R.string.total_balance) + Constant.SETTING_CURRENCY_SYMBOL + Constant.WALLET_BALANCE);
                }
                else {

                }
            }

            @Override
            public void onFailure(Call<GetWallet> call, Throwable t) {
                Log.d("WalletBalance", t.getMessage());
            }
        });
    }


    private boolean validation(){
        boolean valid = true;
        usr_address = tvCity.getText().toString();
        item_count = dataList.size()+"";
        rawData = new RawData(dataList);
        json = gson.toJson(rawData);
        if(TextUtils.isEmpty(user_id)){
            Toast.makeText(this, "user_id is required", Toast.LENGTH_SHORT).show();

        }
        else if(TextUtils.isEmpty(longi)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(lati)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(userName)){
            Toast.makeText(this, "userName is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(userMobile)){
            Toast.makeText(this, "userMobile is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(usr_address)){
            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if(TextUtils.isEmpty(userMail)){
            Toast.makeText(this, "userMail is required", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(paymentMethod)) {
            Toast.makeText(CheckoutActivity.this, "Please select payment method!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else if (TextUtils.isEmpty(json)) {
            Toast.makeText(CheckoutActivity.this, "Please some product payment method!", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }
    private void setPaymentMethod() {
        chHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longi="";
                lati="";
                usr_address ="";
                tvCity.setText("");
                chHome.setChecked(true);
                chWork.setChecked(false);
                chLocation.setChecked(false);
                label = chHome.getTag().toString();

                getHomeAddress(user_id);
            }
        });
        chWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longi="";
                lati="";
                usr_address ="";
                tvCity.setText("");
                chWork.setChecked(true);
                chHome.setChecked(false);
                chLocation.setChecked(false);
                label = chWork.getTag().toString();
                getWorkAddress(user_id);
            }
        });
        chLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longi="";
                lati="";
                usr_address ="";
                tvCity.setText("");
                chLocation.setChecked(true);
                chHome.setChecked(false);
                chWork.setChecked(false);
                label = chWork.getTag().toString();
                //detect current location
                if(checkLocationPermission()){
                    detectLocation();
                }else {
                    requestLocationPermission();
                }
            }
        });

        rbCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCod.setChecked(true);
                rbRazorPay.setChecked(false);
                paymentMethod = rbCod.getTag().toString();

            }
        });
        rbRazorPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRazorPay.setChecked(true);
                rbCod.setChecked(false);
                paymentMethod = rbRazorPay.getTag().toString();
                Checkout.preload(getApplicationContext());
            }
        });



    }

    private void getWorkAddress(String user_id) {
        progressDialog.show();
        Call<ResponseAddress> call = APIClient.getInstance().getWorkAddress(user_id);
        call.enqueue(new Callback<ResponseAddress>() {
            @Override
            public void onResponse(Call<ResponseAddress> call, Response<ResponseAddress> response) {
                ResponseAddress model = response.body();
                if (model != null && model.getStatus()){
                    lati = model.getData().getLati();
                    longi = model.getData().getLongi();
                    tvCity.setText(model.getData().getAddress());
                    progressDialog.dismiss();
                }
                else {
                    Toast.makeText(CheckoutActivity.this, "No Address added yet", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getHomeAddress(String user_id) {
        progressDialog.show();
        Call<ResponseAddress> call = APIClient.getInstance().getHomeAddress(user_id);
        call.enqueue(new Callback<ResponseAddress>() {
            @Override
            public void onResponse(Call<ResponseAddress> call, Response<ResponseAddress> response) {
                ResponseAddress model = response.body();
                if (model != null && model.getStatus()){
                    lati = model.getData().getLati();
                    longi = model.getData().getLongi();
                    tvCity.setText(model.getData().getAddress());
                    progressDialog.dismiss();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(CheckoutActivity.this, "No Address added yet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseAddress> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startPayment() {

        String[] amount = String.valueOf(Double.valueOf(total_amount) * 100).split("\\.");

        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_xbt5pRoHOuHjZl");
        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.pneck_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put(Constant.NAME, userName);
            options.put(Constant.CURRENCY, "INR");
            options.put(Constant.AMOUNT, ""+amount[0]);
            JSONObject preFill = new JSONObject();
            preFill.put(Constant.EMAIL,userMail);
            preFill.put(Constant.CONTACT, userMobile);
            options.put("prefill", preFill);
            //pass amount in currency subunits
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        /**
         * Add your logic here for a successful payment response
         */
        try {
            razorPayId = razorpayPaymentID;
            PlaceOrder(res_id, user_id, lati, longi, item_count, total_amount,
                    json, userName, userMobile, usr_address, userMail, paymentMethod, razorPayId,"Success");
            startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();

        } catch (Exception e) {
            Log.d(TAG, "onPaymentSuccess  ", e);
        }

    }

    private void PlaceOrder(String res_id, String user_id, String lati, String longi, String item_count, String total_amount, String json, String userName, String userMobile, String usr_address, String userMail, String paymentMethod, String razorPayId, String success) {
        Call<ResponseOrderSubmit> call = APIClient.getInstance().orderSubmit(res_id, user_id, lati, longi, item_count, total_amount,
                json, userName, userMobile, usr_address, userMail, paymentMethod, razorPayId, success);

        call.enqueue(new Callback<ResponseOrderSubmit>() {
            @Override
            public void onResponse(Call<ResponseOrderSubmit> call, Response<ResponseOrderSubmit> response) {

                ResponseOrderSubmit orderSubmit  = response.body();

                if (orderSubmit != null && orderSubmit.getSuccess()){
                    Toast.makeText(CheckoutActivity.this, ""+orderSubmit.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    if (orderSubmit == null && !orderSubmit.getSuccess() ){
                        Toast.makeText(CheckoutActivity.this, ""+orderSubmit.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseOrderSubmit> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onPaymentError(int code, String response) {
        /**
         * Add your logic here for a failed payment response
         */
        try {
            Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.d(TAG, "onPaymentError  ", e);
        }
    }


    private void PlaceOrderProcess() {
        progressDialog.setMessage("Ordering....");
        progressDialog.show();
        Call<ResponseOrderSubmit> call = APIClient.getInstance().orderSubmitCod(res_id, user_id, lati, longi, item_count, total_amount,
                json, userName, userMobile, usr_address, userMail);

        call.enqueue(new Callback<ResponseOrderSubmit>() {
            @Override
            public void onResponse(Call<ResponseOrderSubmit> call, Response<ResponseOrderSubmit> response) {
                try{
                    ResponseOrderSubmit orderSubmit  = response.body();
                    if (orderSubmit != null && orderSubmit.getSuccess()){
                        startActivity(new Intent(CheckoutActivity.this, OrderPlacedActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();

                    }
                    else if (orderSubmit != null && !orderSubmit.getSuccess()) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, ""+orderSubmit.getMessage(), Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<ResponseOrderSubmit> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });


    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Toast.makeText(this, "Please wait..", Toast.LENGTH_SHORT).show();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {

                Location location = task.getResult();
                if(location != null){

                    Geocoder geocoder = new Geocoder(CheckoutActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String address = addresses.get(0).getAddressLine(0);

                        tvCity.setText(address);
                        longi = longitude+"";
                        lati= latitude+"";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

    }
    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this,locationPermission,LOCATION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length >0){
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){

                        detectLocation();
                    }else {
                        Toast.makeText(this, "Location permission is necessary.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);
    }
}