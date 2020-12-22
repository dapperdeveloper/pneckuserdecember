package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.R;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.activity.registrationSecond.Model.VenderDetailModel.VendorDetail;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceDetailDescriptionActivity extends AppCompatActivity {


    String shopId, shopName, shopAvatar, shopRating, shopAddress, category;
    CircleImageView shopAvatarIv;
    TextView descriptionTv,professionTv, addressTv,working_daysTv, openTimeTv, closingTimeTv, websiteTv, shopNameTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_service_detail_description);
        shopAvatarIv = findViewById(R.id.shopAvatarIv);
        descriptionTv = findViewById(R.id.descriptionTv);
        professionTv = findViewById(R.id.professionTv);
        addressTv = findViewById(R.id.addressTv);
        working_daysTv = findViewById(R.id.working_daysTv);
        openTimeTv = findViewById(R.id.openTimeTv);
        closingTimeTv = findViewById(R.id.closingTimeTv);
        websiteTv = findViewById(R.id.websiteTv);
        shopNameTv = findViewById(R.id.shopNameTv);

        if (getIntent() != null){
            shopId =getIntent().getStringExtra("shopId");
            shopName = getIntent().getStringExtra("shopName");
            shopAvatar = getIntent().getStringExtra("shopAvatar");
            shopRating = getIntent().getStringExtra("shopRating");
            shopAddress = getIntent().getStringExtra("shopAddress");
            category = getIntent().getStringExtra("categoryName");

            getVendorList(shopId);
            Glide.with(this).load(shopAvatar).placeholder(R.drawable.ic_user_replace).into(shopAvatarIv);
            shopNameTv.setText(shopName);
            final ObjectAnimator animation = ObjectAnimator.ofFloat(shopAvatarIv, "rotationY", 0.0f, 360f);  // HERE 360 IS THE ANGLE OF ROTATE, YOU CAN USE 90, 180 IN PLACE OF IT,  ACCORDING TO YOURS REQUIREMENT
            animation.setDuration(1000); // HERE 500 IS THE DURATION OF THE ANIMATION, YOU CAN INCREASE OR DECREASE ACCORDING TO YOURS REQUIREMENT
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        }


        findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    private void getVendorList(String shopId) {
        Call<VendorDetail> call = APIClient.getInstance().vendorDetail(shopId);
        call.enqueue(new Callback<VendorDetail>() {
            @Override
            public void onResponse(Call<VendorDetail> call, Response<VendorDetail> response) {
                try {
                    VendorDetail model = response.body();
                    if (model != null && model.getSuccess()){
                        descriptionTv.setText(model.getData().getDescription()+"");
                        openTimeTv.setText(model.getData().getOpeningTime()+"");
                        closingTimeTv.setText(model.getData().getClosingTime()+"");
                        working_daysTv.setText(model.getData().getVendorData()+"");
                        addressTv.setText(model.getData().getAddress1()+"");
                        websiteTv.setText(model.getData().getWebsite()+"");
                        professionTv.setText(category);
                    }
                    else if (model != null && !model.getSuccess()){
                        Toast.makeText(ServiceDetailDescriptionActivity.this, ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<VendorDetail> call, Throwable t) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}