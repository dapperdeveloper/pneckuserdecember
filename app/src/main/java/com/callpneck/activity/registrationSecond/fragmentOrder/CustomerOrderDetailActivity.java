package com.callpneck.activity.registrationSecond.fragmentOrder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.callpneck.R;
import com.callpneck.SessionManager;
import com.callpneck.activity.registrationSecond.Activity.FullScreenImageActivity;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.CustomerOrderData;
import com.callpneck.activity.registrationSecond.fragmentOrder.ModelDelivery.CustumerOrderDetail;
import com.callpneck.utils.CustPrograssbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOrderDetailActivity extends AppCompatActivity {

    private void init() {
        txtOrderid = findViewById(R.id.txt_orderid);
        txtTotal = findViewById(R.id.txt_total);
        txtTime = findViewById(R.id.txt_time);
        txtAddress = findViewById(R.id.txt_address);
        txtEmail = findViewById(R.id.txt_email);
        txtPaymode = findViewById(R.id.txt_paymode);
        txtName = findViewById(R.id.txt_name);
        progressBar = findViewById(R.id.progress_bar);
        orderListTv = findViewById(R.id.orderListTv);
        imageViewLayout = findViewById(R.id.imageViewLayout);

    }
    TextView txtOrderid,  txtTotal, txtTime, txtAddress, txtEmail, txtPaymode, txtName;
    ProgressBar progressBar;
    TextView orderListTv;
    RelativeLayout imageViewLayout;
    CustPrograssbar custPrograssbar;
    SessionManager sessionManager;
    String oid, type;

    ImageView orderImageBtn;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_detail);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Order Delivered Details");
            getSupportActionBar().setElevation(0f);
        }
        init();
        if (getIntent()!=null){
            oid = getIntent().getStringExtra("oid");
            type = getIntent().getStringExtra("type");
            Log.e("OrderId", oid);
        }
        sessionManager = new SessionManager(this);
        custPrograssbar = new CustPrograssbar();

        orderImageBtn = findViewById(R.id.orderImageBtn);
        getCurrentOrder(oid , type);

        orderImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //full Image code here
                if (orderImage!=null){
                    Intent fullScreenIntent = new Intent(CustomerOrderDetailActivity.this, FullScreenImageActivity.class);
                    fullScreenIntent.setData(Uri.parse(orderImage));
                    startActivity(fullScreenIntent);
                }

            }
        });
    }



    String orderImage;

    private void getCurrentOrder(String oid, String type) {
        custPrograssbar.PrograssCreate(CustomerOrderDetailActivity.this);
        Call<CustumerOrderDetail> call = APIClient.getInstance().deliveryBoyOrderList(oid, type);
        call.enqueue(new Callback<CustumerOrderDetail>() {
            @Override
            public void onResponse(Call<CustumerOrderDetail> call, Response<CustumerOrderDetail> response) {
                if (response.isSuccessful()){
                    custPrograssbar.ClosePrograssBar();
                    try {
                        CustumerOrderDetail model = response.body();
                        if (model != null && model.getStatus()){

                            CustomerOrderData data = model.getData();
                            txtOrderid.setText(new StringBuilder("#").append(model.getData().getOrderNumber()));
                            txtName.setText(data.getUserName()+"");
                            txtEmail.setText(data.getUserMobile()+"");
                            txtPaymode.setText(data.getPaymentMethod()+"");
                            txtAddress.setText(data.getUserAddress()+"");
                            txtTotal.setText(new StringBuilder("Rs.").append(data.getTotalAmount()));
                            txtTime.setText(data.getDates()+"");
                            orderListTv.setText(data.getOrdeList());


                            orderImage = data.getOrderImage();
                            if (orderImage!=null&&!orderImage.equalsIgnoreCase("")){
                                imageViewLayout.setVisibility(View.VISIBLE);
                                try {
                                    RequestOptions requestOptions= new RequestOptions();
                                    Glide.with(CustomerOrderDetailActivity.this)
                                            .load(orderImage)
                                            .apply(requestOptions)
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    progressBar.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .into(orderImageBtn);
                                    //Glide.with(context).load(bannerDatumList.get(position).getImage()).placeholder(R.drawable.pneck_banner).into(imageView);
                                }catch (Exception e){

                                }
                            }
                            else {
                                imageViewLayout.setVisibility(View.GONE);
                            }

                        }
                    }
                    catch (Exception e){
                        e.toString();
                        custPrograssbar.ClosePrograssBar();
                    }
                }
            }

            @Override
            public void onFailure(Call<CustumerOrderDetail> call, Throwable t) {
                custPrograssbar.ClosePrograssBar();
            }
        });
    }
}