package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.activity.AppController;
import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Adapter.ImageAdapter;
import com.callpneck.activity.registrationSecond.Model.GalleryResponse.ServiceGalleyResponse;
import com.callpneck.activity.registrationSecond.Model.ModelReview;
import com.callpneck.activity.registrationSecond.Model.VenderDetailModel.VendorDetail;
import com.callpneck.activity.registrationSecond.api.APIClient;
import com.callpneck.activity.registrationSecond.api.APIRequests;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderDetailActivity extends AppCompatActivity {

    TextView tabServiceTv, tabGalleryTv, tabReviewsTv;
    RelativeLayout serviceRl, galleryRl, reviewRl;
    RecyclerView   reviewRv;

    ArrayList<ModelReview> reviewList;
    AdapterReview adapterReview;
    String shopId, shopName, shopAvatar, shopRating, shopAddress, category;
    CircleImageView shopAvatarIv;
    TextView descriptionTv,professionTv, addressTv,working_daysTv, openTimeTv, closingTimeTv, websiteTv,
            shopNameTv;
    private void findViews() {
        tabServiceTv = findViewById(R.id.tabServiceTv);
        tabGalleryTv = findViewById(R.id.tabGalleryTv);
        tabReviewsTv = findViewById(R.id.tabReviewsTv);
        serviceRl = findViewById(R.id.serviceRl);
        galleryRl = findViewById(R.id.galleryRl);
        reviewRl = findViewById(R.id.reviewRl);
        reviewRv = findViewById(R.id.reviewRv);
        shopAvatarIv = findViewById(R.id.shopAvatarIv);

        descriptionTv = findViewById(R.id.descriptionTv);
        professionTv = findViewById(R.id.professionTv);
        addressTv = findViewById(R.id.addressTv);
        working_daysTv = findViewById(R.id.working_daysTv);
        openTimeTv = findViewById(R.id.openTimeTv);
        closingTimeTv = findViewById(R.id.closingTimeTv);
        websiteTv = findViewById(R.id.websiteTv);
        shopNameTv = findViewById(R.id.shopNameTv);

        grid_view = findViewById(R.id.grid_view);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_provider_detail);

        findViews();

        if (getIntent() != null) {
            shopId = getIntent().getStringExtra("shopId");
            shopName = getIntent().getStringExtra("shopName");
            shopAvatar = getIntent().getStringExtra("shopAvatar");
            shopRating = getIntent().getStringExtra("shopRating");
            shopAddress = getIntent().getStringExtra("shopAddress");
            category = getIntent().getStringExtra("categoryName");
            shopNameTv.setText(shopName);
            getVendorDetails(shopId);
            Glide.with(this).load(shopAvatar).placeholder(R.drawable.ic_user_replace).into(shopAvatarIv);
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
        reviewList = new ArrayList<>();
        showServicesUI();
        tabServiceTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showServicesUI();
            }
        });
        tabGalleryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showGalleryUI();
                if (AppController.isConnected(ProviderDetailActivity.this))
                    getGalleryImage();
            }
        });
        tabReviewsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showReviewUI();
            }
        });


        loadReviews();


    }
    List<String> galleryList;
    ImageAdapter imageAdapter;
    GridView grid_view;
    private void getGalleryImage() {
        galleryList = new ArrayList<>();
        Call<ServiceGalleyResponse> call = APIClient.getInstance().getGallery(shopId);
        call.enqueue(new Callback<ServiceGalleyResponse>() {
            @Override
            public void onResponse(Call<ServiceGalleyResponse> call, Response<ServiceGalleyResponse> response) {

                try {
                    ServiceGalleyResponse model = response.body();
                    if (model != null && model.getResponse().getSuccess()){
                        galleryList.clear();
                        galleryList = model.getResponse().getData().getImage();
                        imageAdapter = new ImageAdapter(ProviderDetailActivity.this,galleryList);
                        grid_view.setAdapter(imageAdapter);
                        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                Intent fullScreenIntent = new Intent(ProviderDetailActivity.this, FullScreenImageActivity.class);
                                fullScreenIntent.setData(Uri.parse(galleryList.get(position)));
                                startActivity(fullScreenIntent);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ServiceGalleyResponse> call, Throwable t) {
                Toast.makeText(ProviderDetailActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getVendorDetails(String shopId) {

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
                        Toast.makeText(ProviderDetailActivity.this, ""+model.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onFailure(Call<VendorDetail> call, Throwable t) {

            }
        });
    }



    private void loadReviews() {
        reviewList.add(new ModelReview("Jimmy"));
        reviewList.add(new ModelReview("Chu Chu"));
        reviewList.add(new ModelReview("Ramesh"));
        adapterReview = new AdapterReview(this, reviewList);
        reviewRv.setAdapter(adapterReview);
    }

    private void showServicesUI() {
        serviceRl.setVisibility(View.VISIBLE);
        galleryRl.setVisibility(View.GONE);
        reviewRl.setVisibility(View.GONE);
        tabServiceTv.setTextColor(getResources().getColor(R.color.white));
        tabServiceTv.setBackgroundResource(R.drawable.shape_rect04);
        tabGalleryTv.setTextColor(getResources().getColor(R.color.black));
        tabGalleryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabReviewsTv.setTextColor(getResources().getColor(R.color.black));
        tabReviewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    private void showGalleryUI() {

        serviceRl.setVisibility(View.GONE);
        galleryRl.setVisibility(View.VISIBLE);
        reviewRl.setVisibility(View.GONE);
        tabGalleryTv.setTextColor(getResources().getColor(R.color.white));
        tabGalleryTv.setBackgroundResource(R.drawable.shape_rect04);

        tabServiceTv.setTextColor(getResources().getColor(R.color.black));
        tabServiceTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabReviewsTv.setTextColor(getResources().getColor(R.color.black));
        tabReviewsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    private void showReviewUI() {

        serviceRl.setVisibility(View.GONE);
        galleryRl.setVisibility(View.GONE);
        reviewRl.setVisibility(View.VISIBLE);
        tabReviewsTv.setTextColor(getResources().getColor(R.color.white));
        tabReviewsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabGalleryTv.setTextColor(getResources().getColor(R.color.black));
        tabGalleryTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabServiceTv.setTextColor(getResources().getColor(R.color.black));
        tabServiceTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }
}
