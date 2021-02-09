package com.callpneck.activity.deliveryboy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.AppController;
import com.callpneck.activity.deliveryboy.model.TrackMyOrder;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.MainSplashScreen;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.taxi.TaxiMainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.callpneck.SessionManager.isopen;

public class TrackOrderDeliveryActivity extends AppCompatActivity implements View.OnClickListener {

    View view_order_placed,view_order_confirmed,view_order_processed,view_order_pickup,con_divider,ready_divider,placed_divider;
    ImageView img_orderconfirmed,orderprocessed,orderpickup;
    TextView textorderpickup,text_confirmed,textorderprocessed, orderNumberTv, orderTimeTv, nameTv;

    LinearLayout toolbar_layout;

    SessionManager sessionManager;
    CardView callBtnLayout;
    CircleImageView deliveryBoyAvatar;
    CircleButton call_Btn;
    private ScheduledExecutorService executor;
    ConstraintLayout statusLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order_delivery);

        toolbar_layout = findViewById(R.id.toolbar_layout);

        view_order_placed=findViewById(R.id.view_order_placed);
        view_order_confirmed=findViewById(R.id.view_order_confirmed);
        view_order_processed=findViewById(R.id.view_order_processed);
        view_order_pickup=findViewById(R.id.view_order_pickup);
        placed_divider=findViewById(R.id.placed_divider);
        con_divider=findViewById(R.id.con_divider);
        ready_divider=findViewById(R.id.ready_divider);

        textorderpickup=findViewById(R.id.textorderpickup);
        text_confirmed=findViewById(R.id.text_confirmed);
        textorderprocessed=findViewById(R.id.textorderprocessed);

        img_orderconfirmed=findViewById(R.id.img_orderconfirmed);
        orderprocessed=findViewById(R.id.orderprocessed);
        orderpickup=findViewById(R.id.orderpickup);

        orderNumberTv = findViewById(R.id.textView4);
        orderTimeTv = findViewById(R.id.textView3);
        callBtnLayout = findViewById(R.id.callBtn);
        nameTv = findViewById(R.id.nameTv);
        deliveryBoyAvatar = findViewById(R.id.deliveryBoyAvatar);
        call_Btn = findViewById(R.id.call_Btn);

        statusLayout = findViewById(R.id.statusLayout);
        Intent intent=getIntent();


        sessionManager = new SessionManager(this);
        executor =
                Executors.newSingleThreadScheduledExecutor();
        toolbar_layout.setOnClickListener(this);


        if (AppController.isConnected (TrackOrderDeliveryActivity.this)){
            Runnable periodicTask = new Runnable() {
                public void run() {
                    Log.d("TahseenKhan","calling");
                    // Invoke method(s) to do the work
                    getTrackOrderData(sessionManager.getCurrentDeliveryOrderId());
                }
            };
            executor.scheduleAtFixedRate(periodicTask, 0, 4, TimeUnit.SECONDS);


        }


        call_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumber!= null)
                    callToNumber(phoneNumber);
            }
        });


    }
    private void callToNumber(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        startActivity(intent);
    }
    String orderStatus="";
    String phoneNumber="";
    BottomSheetDialog bottomSheet;
    private void getTrackOrderData(String oid) {
        Call<TrackMyOrder> call = APIClient.getInstance().userOrderStatusShow(oid);
        call.enqueue(new Callback<TrackMyOrder>() {
            @Override
            public void onResponse(Call<TrackMyOrder> call, Response<TrackMyOrder> response) {
                if(response.isSuccessful()){
                    try {
                        TrackMyOrder trackMyOrder= response.body();
                        if(trackMyOrder!= null && trackMyOrder.getSuccess()){

                            orderNumberTv.setText(trackMyOrder.getData().getOrderNumber()+"");
                            orderTimeTv.setText(trackMyOrder.getData().getTime()+"");

                            phoneNumber = trackMyOrder.getData().getEmpMobile();
                            nameTv.setText(trackMyOrder.getData().getEmpName()+"");
                            try {
                                Glide.with(TrackOrderDeliveryActivity.this).load(trackMyOrder.getData().getEmpProfile()).into(deliveryBoyAvatar);

                            }catch (Exception e){
                                e.toString();
                            }

                            String totalAmount = trackMyOrder.getData().getTotalAmount()+"";
                            double deliverFee = Double.parseDouble(trackMyOrder.getData().getDeliveryCharge());

                            if (!totalAmount.equalsIgnoreCase("0")){
                                if (!sessionManager.getBooleanData(SessionManager.isopen)){
                                    sessionManager.setBooleanData(isopen, true);
                                    final View view = getLayoutInflater().inflate(R.layout.layout_delivery_fee_amount_dialog, null);
                                    bottomSheet = new BottomSheetDialog(TrackOrderDeliveryActivity.this);
                                    bottomSheet.setContentView(view);
                                    bottomSheet.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    bottomSheet.setCancelable(false);

                                    TextView orderAmount = (TextView)view.findViewById( R.id.order_amount );
                                    AppCompatButton paymentDoneBtn = (AppCompatButton)view.findViewById( R.id.payment_done_btn );

                                    double amount = Double.parseDouble(totalAmount);

                                    double total = deliverFee+amount;
                                    orderAmount.setText("Pay : ₹"+total);
                                    paymentDoneBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            bottomSheet.dismiss();
                                        }
                                    });
                                    bottomSheet.show();
                                }

                            }

                            String status = trackMyOrder.getData().getStatus()+"";
                            if (status.equalsIgnoreCase("Pending")){
                                statusLayout.setVisibility(View.GONE);
                                callBtnLayout.setVisibility(View.GONE);
                            }
                            if (status.equalsIgnoreCase("Received")){
                                orderStatus = "0";
                                getOrderStatus(orderStatus);
                                statusLayout.setVisibility(View.VISIBLE);
                                callBtnLayout.setVisibility(View.GONE);
                            }
                            else if (status.equalsIgnoreCase("Preparing")){
                                orderStatus="1";
                                getOrderStatus(orderStatus);
                                statusLayout.setVisibility(View.VISIBLE);
                                callBtnLayout.setVisibility(View.VISIBLE);
                            }
                            else if (status.equalsIgnoreCase("On The Way")){
                                orderStatus = "2";
                                getOrderStatus(orderStatus);
                                statusLayout.setVisibility(View.VISIBLE);
                                callBtnLayout.setVisibility(View.VISIBLE);
                            }
                            else if (status.equalsIgnoreCase("Delivered")){
                                orderStatus= "3";
                                getOrderStatus(orderStatus);
                                statusLayout.setVisibility(View.VISIBLE);
                                callBtnLayout.setVisibility(View.VISIBLE);
                                StyleableToast.makeText(TrackOrderDeliveryActivity.this, "Thank You For Ordering!", Toast.LENGTH_LONG, R.style.mytoast).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sessionManager.clearDeliveryOrderSession();
                                        sessionManager.setBooleanData(isopen, false);
                                        Intent intent = new Intent(TrackOrderDeliveryActivity.this, MainScreenActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                                    }
                                }, 2000);
                            }



                        }
                    }catch (Exception e){


                    }
                }
            }

            @Override
            public void onFailure(Call<TrackMyOrder> call, Throwable t) {

            }
        });
    }

    private void getOrderStatus(String orderStatus) {
        if (orderStatus.equals("0")){
            float alfa= (float) 0.5;
            setStatus(alfa);

        }else if (orderStatus.equals("1")){
            float alfa= (float) 1;
            setStatus1(alfa);
        }else if (orderStatus.equals("2")){
            float alfa= (float) 1;
            setStatus2(alfa);
        }else if (orderStatus.equals("3")){
            float alfa= (float) 1;
            setStatus3(alfa);
        }
    }

    private void setStatus(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setAlpha(alfa);
        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(alfa);
        textorderpickup.setAlpha(myf);

    }
    private void setStatus1(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(myf);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(myf);
        view_order_pickup.setAlpha(myf);
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        orderpickup.setAlpha(myf);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
    }

    private void setStatus2(float alfa) {
        float myf= (float) 0.5;
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        img_orderconfirmed.setAlpha(alfa);

        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_current));
        textorderpickup.setAlpha(myf);
        orderpickup.setAlpha(myf);

    }

    private void setStatus3(float alfa) {
        view_order_placed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        view_order_confirmed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        orderprocessed.setAlpha(alfa);
        view_order_processed.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        con_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));

        img_orderconfirmed.setAlpha(alfa);
        placed_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        text_confirmed.setAlpha(alfa);
        textorderprocessed.setAlpha(alfa);
        view_order_pickup.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        ready_divider.setBackground(getResources().getDrawable(R.drawable.shape_status_completed));
        textorderpickup.setAlpha(alfa);
        orderpickup.setAlpha(alfa);
    }

    AlertDialog.Builder builder;
    @Override
    public void onBackPressed() {
        builder = new AlertDialog.Builder(TrackOrderDeliveryActivity.this);
        builder.setTitle("");
        builder.setMessage("Are you sure that you want to go to Home");
        builder.setNegativeButton("Yes",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        Intent intent = new Intent(TrackOrderDeliveryActivity.this, MainScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
                    }
                });
        builder.setPositiveButton("No",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        if (v == toolbar_layout){
            onBackPressed();
        }
    }
}