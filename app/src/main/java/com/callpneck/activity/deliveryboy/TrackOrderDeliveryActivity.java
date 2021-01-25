package com.callpneck.activity.deliveryboy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.MainSplashScreen;
import com.google.android.material.snackbar.Snackbar;

public class TrackOrderDeliveryActivity extends AppCompatActivity implements View.OnClickListener {

    View view_order_placed,view_order_confirmed,view_order_processed,view_order_pickup,con_divider,ready_divider,placed_divider;
    ImageView img_orderconfirmed,orderprocessed,orderpickup;
    TextView textorderpickup,text_confirmed,textorderprocessed;

    LinearLayout toolbar_layout;

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
        Intent intent=getIntent();
        String orderStatus="1";

        toolbar_layout.setOnClickListener(this);
        getOrderStatus(orderStatus);

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


    boolean doubleBackToExitPressedOnce = false;
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