package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callpneck.Language.ThemeUtils;
import com.callpneck.R;
import com.callpneck.activity.registrationSecond.Adapter.AdapterReview;
import com.callpneck.activity.registrationSecond.Model.ModelReview;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProviderDetailActivity extends AppCompatActivity {

    TextView tabServiceTv, tabGalleryTv, tabReviewsTv;
    RelativeLayout serviceRl, galleryRl, reviewRl;
    RecyclerView  galleryRv, reviewRv;

    ArrayList<ModelReview> reviewList;
    AdapterReview adapterReview;
    String shopId, shopName, shopAvatar, shopRating, shopAddress;
    CircleImageView shopAvatarIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_detail);
        ThemeUtils.setLanguage(this);

        tabServiceTv = findViewById(R.id.tabServiceTv);
        tabGalleryTv = findViewById(R.id.tabGalleryTv);
        tabReviewsTv = findViewById(R.id.tabReviewsTv);
        serviceRl = findViewById(R.id.serviceRl);
        galleryRl = findViewById(R.id.galleryRl);
        reviewRl = findViewById(R.id.reviewRl);
        galleryRv = findViewById(R.id.galleryRv);
        reviewRv = findViewById(R.id.reviewRv);
        shopAvatarIv = findViewById(R.id.shopAvatarIv);


        if (getIntent() != null) {
            shopId = getIntent().getStringExtra("shopId");
            shopName = getIntent().getStringExtra("shopName");
            shopAvatar = getIntent().getStringExtra("shopAvatar");
            shopRating = getIntent().getStringExtra("shopRating");
            shopAddress = getIntent().getStringExtra("shopAddress");
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

    private void openServiceDescriptionActivity() {
        Intent intent = new Intent(ProviderDetailActivity.this, ServiceDetailDescriptionActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in_activity, R.anim.scale_to_center);
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
